package io.home4Me.Security.authentication.services;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.home4Me.Security.DefaultRoles;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.dao.LoginDetailsDao;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.exceptions.UserAlreadyExistsException;

@Service
public class LoginDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(LoginDetailsService.class);
	
	private final LoginDetailsDao loginDetailsDao;
	private final PasswordEncoder encoder;
	private final ModelMapper modelMapper = new ModelMapper();
		
	@Autowired
	public LoginDetailsService(LoginDetailsDao loginDetailsDao,
			@Lazy PasswordEncoder encoder){
		this.loginDetailsDao = loginDetailsDao;
		this.encoder = encoder;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LoginDetails createUser(RegisterRequest registerRequest) {
		
		if(isRequestDataUnique(registerRequest.getEmail(), registerRequest.getUsername())) {
			LoginDetails loginDetails = modelMapper.map(registerRequest, LoginDetails.class)
					.buildNewUser(DefaultRoles.DEFAULT_USER);
			
			saveEntity(loginDetails);
			return loginDetails;
		}
		
		else throw new UserAlreadyExistsException(String.format("%s or %s is not unique", 
				registerRequest.getEmail(), registerRequest.getUsername()));
	}
	
	@Override
	@Transactional(readOnly = true)
	public LoginDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return loginDetailsDao.findByUsername(username).orElseThrow(logAndThrowNotFoundExc(username));
	}

	private void saveEntity(LoginDetails loginDetails) {
				
		VerificationInfo.checkAndInCaseOfFailureRun(prePersistValidation(loginDetails), (loginDetailFailure -> {
			throw new IllegalArgumentException(String.format("Cannot persist object - %s", loginDetailFailure.getReason()));
		}));
		
		encodePassword(loginDetails);
		loginDetailsDao.save(loginDetails);		
	}
	
	private Supplier<? extends UsernameNotFoundException> logAndThrowNotFoundExc(String username){
		return () -> {
			logger.warn(String.format("User with username - %s not found" , username));
			throw new UsernameNotFoundException(username);
		};
	}
	
	private boolean isRequestDataUnique(String email, String username) {
		return !loginDetailsDao.existsByUsernameOrEmail(email, username);
	}
	
	private void encodePassword(LoginDetails loginDetails) {
		loginDetails.setPassword(encoder.encode(loginDetails.getPassword()));
	}
		
	private List<VerificationInfo> prePersistValidation(LoginDetails loginDetails) {
		return List.of(new VerificationInfo(loginDetails.getId() == null).onFailSetReason("Entity Id cannot be non null"), 
				new VerificationInfo(StringUtils.hasText(loginDetails.getEmail())).onFailSetReason("Entity email cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getPassword())).onFailSetReason("Entity password cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getUsername())).onFailSetReason("Entity username cannot be empty"));
	}
}