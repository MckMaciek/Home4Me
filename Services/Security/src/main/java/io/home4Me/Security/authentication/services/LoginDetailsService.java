package io.home4Me.Security.authentication.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.Exceptions.UserAlreadyExistsException;
import io.home4Me.Security.authentication.dao.LoginDetailsDao;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.LoginResponse;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.UserRoles;
import io.home4Me.Security.utils.TokenWrappee;

@Service
public class LoginDetailsService implements UserDetailsService {

	private static final Logger logger = LogManager.getLogger(LoginDetailsService.class);
	
	private final LoginDetailsDao loginDetailsDao;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final RoleService roleService;
	private final JwtService jwtService;
	
	private final ModelMapper modelMapper = new ModelMapper();
		
	@Autowired
	public LoginDetailsService(LoginDetailsDao loginDetailsDao,
			@Lazy PasswordEncoder passwordEncoder,
			@Lazy AuthenticationManager authenticationManager,
			RoleService roleService,
			JwtService jwtService
	){
		this.loginDetailsDao = loginDetailsDao;
		this.passwordEncoder = passwordEncoder;
		this.roleService = roleService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}
	
	@Transactional
	public LoginDetails createUser(RegisterRequest registerRequest, Set<RoleTypes> roleTypes) {
		
		if(isRequestDataUnique(registerRequest.getEmail(), registerRequest.getUsername())) {
			LoginDetails loginDetails = modelMapper.map(registerRequest, LoginDetails.class)
					.buildNewUser();
			
			roleService.overrideUserRoles(loginDetails, roleTypes);
			saveEntity(loginDetails);
				
			return loginDetails;
		}
		
		else throw new UserAlreadyExistsException(String.format("%s or %s is not unique", 
				registerRequest.getEmail(), registerRequest.getUsername()));
	}
	
	public LoginResponse authenticateAndProvideTokens(LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        LoginDetails user = (LoginDetails) authentication.getPrincipal();
        
        TokenWrappee generatedTokens = jwtService.provideRefreshAndAccessToken(userDetails);
                
        return LoginResponse.builder()
        		.tokens(generatedTokens)
        		.username(user.getUsername())
        		.email(user.getEmail())
        		.roles(extractRoles(user.getUserRoles()))
        		.processedDate(LocalDateTime.now())
        		.build();
	}
	
	public LoginResponse refreshAuthenticatedAccessToken(String refreshToken) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		UserDetails userDetails = (UserDetails) auth.getDetails();
		LoginDetails user = (LoginDetails) auth.getPrincipal();
		
		TokenWrappee generatedAccessToken = jwtService.provideAccessToken(refreshToken, userDetails);
		
		return LoginResponse.builder()
        		.tokens(generatedAccessToken)
        		.username(user.getUsername())
        		.email(user.getEmail())
        		.roles(extractRoles(user.getUserRoles()))
        		.processedDate(LocalDateTime.now())
        		.build();
	}
	
	private Set<RoleTypes> extractRoles(Set<UserRoles> roles) {
		return roles.stream()
						  .map(UserRoles::getRole)
					      .collect(Collectors.toSet());
	}

	public void saveEntity(LoginDetails loginDetails) {
		
		Optional<VerificationInfo> foundFailure = VerificationInfo.findAnyFailure(prePersistValidation(loginDetails));
		
		if (foundFailure.isEmpty()) {
			encodePassword(loginDetails);
			loginDetailsDao.save(loginDetails);
		}
		else throw new IllegalArgumentException(String.format("Cannot persist object - %s", foundFailure.get().getReason()));
	}
	
	@Override
	public LoginDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return loginDetailsDao.findByUsername(username).orElseThrow(logAndThrowNotFoundExc(username));
	}

	private Supplier<? extends UsernameNotFoundException> logAndThrowNotFoundExc(String username){
		return () -> {
			logger.warn(String.format("User with username - %s not found" , username));
			throw new UsernameNotFoundException(username);
		};
	}
	
	@Transactional(readOnly = true)
	private boolean isRequestDataUnique(String email, String username) {
		return !loginDetailsDao.existsByUsernameOrEmail(email, username);
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