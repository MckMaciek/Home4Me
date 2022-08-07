package io.home4Me.Security.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class AccessTokenOperationsImpl implements AccessTokenOperations {
	
	private static final Logger logger = LogManager.getLogger(AccessTokenOperationsImpl.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.accessTokenExpirationTime}")
	private long accessTokenExpirationTime;
	
	private static final JwtParser parser = Jwts.parser();
	
	@Override
	public String generateAccessToken(UserDetails userDetails) {
		String accessToken = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.claim("roles", getUserRoles(userDetails))
				.addClaims(generateUserDataToClaims(userDetails))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + accessTokenExpirationTime))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	
		return accessToken;
	}

	@Override
	public String getUserNameFromAccessToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}
	
	@Override
	public Date getAccessTokenExpirationDate(String token) {
		return parser.setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
	}

	@Override
	public boolean isTokenExpired(String token) {
		Date expDate = getAccessTokenExpirationDate(token);
		return new Date().after(expDate);
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

	@Override
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
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
