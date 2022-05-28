package io.home4Me.Security.authentication.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.utils.TokenSupplier;
import io.home4Me.Security.utils.TokenWrappee;

@Service
public class JwtService {

	private final TokenSupplier jwtSupplier;
	private final AuthenticationManager authenticationManager;
	
	@Autowired
	public JwtService(TokenSupplier jwtSupplier,
			AuthenticationManager authenticationManager
	){
		this.jwtSupplier = jwtSupplier;
		this.authenticationManager = authenticationManager;
	}
	
	public TokenWrappee provideRefreshAndAccessToken(LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails loginDetails = (UserDetails) authentication.getDetails();
		
		TokenWrappee tokens = jwtSupplier.buildOperation()
										 .generateBothTokens()
										 .withUserDetails(loginDetails)
										 .build();
		
		Optional<VerificationInfo> validationFailures = VerificationInfo.findAnyFailure(tokens.getValidationInfo());
		
		if(validationFailures.isPresent()) {
			throw new NullPointerException(validationFailures.get().getReason());
		}
		
		return tokens;
	}
	
}
