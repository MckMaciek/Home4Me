package io.home4Me.Security.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.home4Me.Security.RoleTypes;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {

	private static final Logger logger = LogManager.getLogger(JwtUtils.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.refreshTokenExpirationTime}")
	private long refreshTokenExpirationTime;
	
	@Value("${jwt.accessTokenExpirationTime}")
	private long accessTokenExpirationTime;

	public String generateRefreshToken(UserDetails userDetails, String accessToken) {
	
		String refreshToken = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationTime))
				.signWith(SignatureAlgorithm.HS512, jwtSecret + accessToken.hashCode())
				.compact();
	
		logger.debug(String.format("Refresh token for user %s - has been generated", userDetails.getUsername()));
		return refreshToken;
	}
	
	public String generateAccessToken(UserDetails userDetails) {
			
		String accessToken = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.claim("roles", getUserRoles(userDetails))
				.addClaims(generateUserDataToClaims(userDetails))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + accessTokenExpirationTime))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	
		logger.debug(String.format("Access token for user %s - has been generated", userDetails.getUsername()));
		return accessToken;
	}

	@SuppressWarnings("serial")
	private Map<String, Object> generateUserDataToClaims(UserDetails userDetails) {
		return new HashMap<>() {{
			put("roles", getUserRoles(userDetails));
			put("username", userDetails.getUsername());	
		}};
	}

	private List<String> getUserRoles(UserDetails userDetails) {
		return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}
	
	public String getUserNameFromAccessToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public String getUserNameFromRefreshToken(String token, String accessToken) {
		return Jwts.parser().setSigningKey(jwtSecret + accessToken.hashCode()).parseClaimsJws(token).getBody().getSubject();
	}
		
	public boolean isTokenExpired(String token) {
		Date expDate = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
		return new Date().after(expDate);
	}

	public boolean validateJwtToken(String authToken) {

		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
