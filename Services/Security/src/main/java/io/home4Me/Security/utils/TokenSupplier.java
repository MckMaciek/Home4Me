package io.home4Me.Security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenSupplier {

	private final JwtUtils jwtUtils;
	
	@Autowired
	public TokenSupplier(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	public TokenSupplierSteps buildOperation() {
		return new TokenSupplierSteps();
	}

	public class TokenSupplierSteps
			implements GetNewAccessToken, WithRefreshToken, WithUserDetails, GenerateNewTokens, Built {

		private String accessToken;
		private String refreshToken;
		private UserDetails userDetails;

		private boolean provideNewRefreshToken = false;
		private boolean provideNewAccessToken = false;

		@Override
		public TokenWrappee build() {

			if (provideNewRefreshToken && provideNewAccessToken) {
				accessToken = jwtUtils.generateAccessToken(userDetails);
				refreshToken = jwtUtils.generateRefreshToken(userDetails, accessToken);
				
			} 
			else if (provideNewAccessToken) {
				// OPERATION DELETE PREVIOUS ACCESS TOKEN HERE WITH REFRESH TOKEN
				accessToken = jwtUtils.generateAccessToken(userDetails);
			}

			resetProvidingFlags();
			return new TokenWrappee(refreshToken, accessToken);
		}

		@Override
		public WithRefreshToken getNewAccessToken() {
			this.provideNewAccessToken = true;
			return this;
		}

		@Override
		public WithUserDetails withRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
			return this;
		}

		@Override
		public Built withUserDetails(UserDetails userDetails) {
			this.userDetails = userDetails;
			return this;
		}

		@Override
		public WithUserDetails generateBothTokens() {
			this.provideNewRefreshToken = true;
			this.provideNewAccessToken = true;
			return this;
		}
		
		private void resetProvidingFlags() {
			provideNewRefreshToken = false;
			provideNewAccessToken = false;
		}
	}

	public static interface GenerateNewTokens {
		WithUserDetails generateBothTokens();
	}

	public static interface GetNewAccessToken {
		WithRefreshToken getNewAccessToken();
	}

	public static interface WithRefreshToken {
		WithUserDetails withRefreshToken(String refreshToken);
	}

	public static interface WithUserDetails {
		Built withUserDetails(UserDetails userDetails);
	}

	public static interface Built {
		TokenWrappee build();
	}
}
