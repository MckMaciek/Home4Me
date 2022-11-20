package io.home4Me.Security.authentication.identity.user;

import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.home4Me.Security.DefaultRoles;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.identity.user.dto.LoginDetailsDto;
import io.home4Me.Security.commons.identity.Password;
import io.home4Me.Security.exceptions.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(LoginDetailsService.class);
	
	private final LoginDetailsDao loginDetailsDao;
	private final PasswordEncoder encoder;
	private final ModelMapper modelMapper = new ModelMapper();
	
	@Override
	@Transactional(readOnly = true)
	public LoginDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return loginDetailsDao.findByUsername(username).orElseThrow(logAndThrowNotFoundExc(username));
	}

	@Transactional
	public LoginDetailsDto createUser(final RegisterRequest registerRequest) {
		if(isRequestDataUnique(registerRequest.getEmail(), registerRequest.getUsername())) {
			
			LoginDetails loginDetails = LoginDetails.mapRequest(registerRequest);
					
			VerificationInfo.checkAndInCaseOfFailureRun(prePersistValidation(loginDetails), (loginDetailFailure -> {
				throw new IllegalArgumentException(String.format("Cannot persist object - %s", loginDetailFailure.getReason()));
			}));
			
			loginDetails.saveWithRoles(DefaultRoles.DEFAULT_USER, encoder);
			loginDetailsDao.save(loginDetails);	
			
			return modelMapper.map(loginDetails, LoginDetailsDto.class);
		}
		
		else throw new UserAlreadyExistsException(String.format("%s or %s is not unique", 
				registerRequest.getEmail(), registerRequest.getUsername()));
	}
	
	private Supplier<? extends UsernameNotFoundException> logAndThrowNotFoundExc(final String username){
		return () -> {
			logger.warn(String.format("User with username - %s not found" , username));
			throw new UsernameNotFoundException(username);
		};
	}
	
	private boolean isRequestDataUnique(final String email, final String username) {
		return !loginDetailsDao.existsByUsernameOrEmail(email, username);
	}
	
	private List<VerificationInfo> prePersistValidation(final LoginDetails loginDetails) {
		return List.of(new VerificationInfo(loginDetails.getId() == null).onFailSetReason("Entity Id cannot be non null"), 
				new VerificationInfo(StringUtils.hasText(loginDetails.getEmail())).onFailSetReason("Entity email cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getPassword())).onFailSetReason("Entity password cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getUsername())).onFailSetReason("Entity username cannot be empty"));
	}
}