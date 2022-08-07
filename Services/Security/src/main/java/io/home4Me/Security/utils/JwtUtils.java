package io.home4Me.Security.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {

	private static final Logger logger = LogManager.getLogger(JwtUtils.class);

	@Autowired
	private RefreshTokenOperationsImpl refreshTokenOperations;
	
	@Autowired
	private AccessTokenOperationsImpl accessTokenOperations;
	
	@Autowired
	private InvalidTokensManager invalidTokensManager;

	public String generateRefreshToken(UserDetails userDetails, String accessToken) {

		String refreshToken = refreshTokenOperations.generateRefreshToken(userDetails, accessToken);
		logger.debug(String.format("Refresh token for user %s - has been generated", userDetails.getUsername()));
		return refreshToken;
	}
	
	public String generateAccessToken(UserDetails userDetails) {

		String accessToken = accessTokenOperations.generateAccessToken(userDetails);
		logger.debug(String.format("Access token for user %s - has been generated", userDetails.getUsername()));
		return accessToken;
	}
	
	public RefreshToken wrapToken(String refreshTokenRaw, String accessTokenRaw) {
		
		refreshTokenOperations.validateToken(refreshTokenRaw, accessTokenRaw);
		String username = refreshTokenOperations.getUserNameFromRefreshToken(refreshTokenRaw, accessTokenRaw);
		Date expiredDate = refreshTokenOperations.getExpirationDate(refreshTokenRaw, accessTokenRaw);
			
		return new RefreshToken(refreshTokenRaw, username, expiredDate);
	}
	
	public AccessToken wrapToken(String accessTokenRaw) {
		
		accessTokenOperations.validateToken(accessTokenRaw);
		String username = accessTokenOperations.getUserNameFromAccessToken(accessTokenRaw);
		Date expirationDate = accessTokenOperations.getAccessTokenExpirationDate(accessTokenRaw);
		
		return new AccessToken(accessTokenRaw, username, expirationDate);
	}

	public void disableAccessToken(String refreshToken, String accessToken) {
		invalidTokensManager.discardAccessToken(refreshToken, accessToken);
	}
}
