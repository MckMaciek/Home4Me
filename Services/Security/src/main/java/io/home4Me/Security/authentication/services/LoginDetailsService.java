package io.home4Me.Security.authentication.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.dao.LoginDetailsDao;
import io.home4Me.Security.authentication.dto.LoginDetailsDto;
import io.home4Me.Security.authentication.entity.LoginDetails;

@Service
public class LoginDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(LoginDetailsService.class);
	
	private final LoginDetailsDao loginDetailsDao;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
		
	@Autowired
	public LoginDetailsService(LoginDetailsDao loginDetailsDao,
			@Lazy PasswordEncoder passwordEncoder,
			RoleService roleService
	){
		this.loginDetailsDao = loginDetailsDao;
		this.passwordEncoder = passwordEncoder;
		this.roleService = roleService;
	}
	
	@Transactional
	public LoginDetails createUser(LoginDetailsDto loginDetailsDTO, Set<RoleTypes> roleTypes) {
		
		ModelMapper modelMapper = new ModelMapper();
		LoginDetails loginDetails = modelMapper.map(loginDetailsDTO, LoginDetails.class);
		
		loginDetails.setId(null);
		loginDetails.setCreationDate(LocalDateTime.now());
			
		saveEntity(loginDetails);
		roleService.overrideUserRoles(loginDetails, roleTypes);
		
		return loginDetails;
	}
	
	@Override
	public LoginDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<LoginDetails> loginDetailsOpt =  Optional.ofNullable(loginDetailsDao.getLoginDetailsByUsername(username));
		return loginDetailsOpt.orElseThrow(logAndThrowNotFoundExc(username));
	}
	
	public void saveEntity(LoginDetails loginDetails) {
		
		Optional<VerificationInfo> foundFailure = VerificationInfo.findAnyFailure(prePersistValidation(loginDetails));
		
		if (foundFailure.isEmpty()) {
			encodePassword(loginDetails);
			loginDetailsDao.save(loginDetails);
			
			logger.debug(String.format("Entity - %s has been saved", loginDetails));
		}
		else throw new IllegalArgumentException(String.format("Cannot persist object - %s", foundFailure.get().getReason()));
	}
	
	private Supplier<? extends UsernameNotFoundException> logAndThrowNotFoundExc(String username){
		return () -> {
			logger.warn(String.format("User with username - %s not found" , username));
			throw new UsernameNotFoundException(username);
		};
	}
	
	private void encodePassword(LoginDetails loginDetails) {
		loginDetails.setPassword(passwordEncoder.encode(loginDetails.getPassword()));
	}
	
	private List<VerificationInfo> prePersistValidation(LoginDetails loginDetails) {
		return List.of(new VerificationInfo(loginDetails.getId() == null).onFailSetReason("Entity Id cannot be non null"), 
				new VerificationInfo(StringUtils.hasText(loginDetails.getEmail())).onFailSetReason("Entity email cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getPassword())).onFailSetReason("Entity password cannot be empty"),
				new VerificationInfo(StringUtils.hasText(loginDetails.getUsername())).onFailSetReason("Entity username cannot be empty"));
	}

}
