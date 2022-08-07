package io.home4Me.Security.utils;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenOperations {

	String generateRefreshToken(UserDetails userDetails, String accessToken);
	String getUserNameFromRefreshToken(String token, String accessToken);
	Date getExpirationDate(String token, String accessToken);
	boolean isTokenExpired(String token, String accessToken);
	public boolean validateToken(String token, String accessToken);
}
