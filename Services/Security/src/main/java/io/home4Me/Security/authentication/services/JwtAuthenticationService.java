package io.home4Me.Security.authentication.services;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.LoginResponse;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.UserRoles;
import io.home4Me.Security.utils.TokenWrappee;

@Service
public class JwtAuthenticationService {
	
	private final AuthenticationManager authenticationManager;
	private final JwtInnerService jwtInnerService;
	
	@Autowired
	public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtInnerService jwtInnerService) {
		this.jwtInnerService = jwtInnerService;
		this.authenticationManager = authenticationManager;
	}

	public LoginResponse authenticateAndProvideTokens(LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        LoginDetails user = (LoginDetails) authentication.getPrincipal();
        
        TokenWrappee generatedTokens = jwtInnerService.provideRefreshAndAccessToken(userDetails);
                
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
		
		TokenWrappee generatedAccessToken = jwtInnerService.provideAccessToken(refreshToken, userDetails);
		
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
	
}
