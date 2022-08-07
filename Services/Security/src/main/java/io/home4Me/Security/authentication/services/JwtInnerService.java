package io.home4Me.Security.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.utils.AccessToken;
import io.home4Me.Security.utils.RefreshToken;
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
