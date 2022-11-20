package io.home4Me.Security.authentication.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.token.dto.TokenWrappee;
import io.home4Me.Security.authentication.token.dto.access.AccessToken;
import io.home4Me.Security.authentication.token.dto.refresh.RefreshToken;

@Service
class JwtInnerService {

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
		return tokens;
	}
	
	public TokenWrappee provideAccessToken(RefreshToken refreshToken, AccessToken oldAccessToken, UserDetails userDetails) {
		TokenWrappee accessTokenWrappee = jwtSupplier.buildOperation()
													 .getNewAccessToken(oldAccessToken)
													 .withRefreshToken(refreshToken)
													 .withUserDetails(userDetails)
													 .build();
		return accessTokenWrappee;
	}
}
