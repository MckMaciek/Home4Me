package io.home4Me.Security.utils;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface AccessTokenOperations {

	String generateAccessToken(UserDetails userDetails);
	String getUserNameFromAccessToken(String token);
	boolean isTokenExpired(String token);
	Date getAccessTokenExpirationDate(String token);
	boolean validateToken(String token);
}
