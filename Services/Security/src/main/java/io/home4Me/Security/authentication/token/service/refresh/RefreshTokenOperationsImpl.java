package io.home4Me.Security.authentication.token.service.refresh;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
public class RefreshTokenOperationsImpl implements RefreshTokenOperations {
	
	private static final Logger logger = LogManager.getLogger(RefreshTokenOperationsImpl.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.refreshTokenExpirationTime}")
	private long refreshTokenExpirationTime;
	
	private static final JwtParser parser = Jwts.parser();
	
	@Override
	public String generateRefreshToken(UserDetails userDetails, String accessToken) {
		String refreshToken = Jwts.builder()
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationTime))
				.signWith(SignatureAlgorithm.HS512, jwtSecret + accessToken.hashCode())
				.compact();
		
		return refreshToken;
	}

	@Override
	public String getUserNameFromRefreshToken(String token, String accessToken) {
		return parser.setSigningKey(jwtSecret + accessToken.hashCode()).parseClaimsJws(token).getBody().getSubject();
	}
	
	@Override
	public Date getExpirationDate(String token, String accessToken) {
		return parser.setSigningKey(jwtSecret + accessToken.hashCode()).parseClaimsJws(token).getBody().getExpiration();
	}

	@Override
	public boolean isTokenExpired(String token, String accessToken) {
		Date expDate = getExpirationDate(token, accessToken);
		return new Date().after(expDate);
	}
	
	@Override
	public boolean validateToken(String token, String accessToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret + accessToken.hashCode()).parseClaimsJws(token);
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
