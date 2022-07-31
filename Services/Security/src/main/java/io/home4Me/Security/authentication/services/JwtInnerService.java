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
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.utils.TokenSupplier;
import io.home4Me.Security.utils.TokenWrappee;

@Service
public class JwtInnerService {

	private final TokenSupplier jwtSupplier;
	
	@Autowired
	public JwtInnerService(TokenSupplier jwtSupplier){
		this.jwtSupplier = jwtSupplier;
	}
	
	public TokenWrappee provideRefreshAndAccessToken(UserDetails userDetails) {	
		TokenWrappee tokens = jwtSupplier.buildOperation()
										 .generateBothTokens()
										 .withUserDetails(userDetails)
										 .build();
		validateTokens(tokens);
		return tokens;
	}
	
	public TokenWrappee provideAccessToken(String refreshToken, UserDetails userDetails) {
		TokenWrappee accessTokenWrappee = jwtSupplier.buildOperation()
													 .getNewAccessToken()
													 .withRefreshToken(refreshToken)
													 .withUserDetails(userDetails)
													 .build();
		validateTokens(accessTokenWrappee);
		return accessTokenWrappee;
	}
	
	private void validateTokens(TokenWrappee tokens) {
		Optional<VerificationInfo> validationFailures = VerificationInfo.findAnyFailure(tokens.getValidationInfo());
		
		if(validationFailures.isPresent()) {
			throw new NullPointerException(validationFailures.get().getReason());
		}
	}
	
}
