package io.home4Me.Security.authentication.config;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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

    @Value("${jwt.expirationTime}")
    private int jwtExpirationMs;
	
	public String generateJwtToken(Authentication authentication) {
		
	    return Jwts.builder()
            .setSubject(authentication.getName())
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
	
   public String getUserNameFromJwtToken(String token) {
	   return Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
    }
   
   public boolean isTokenExpired(String  token) {
	   
	   Date expDate = Jwts.parser()
       		.setSigningKey(jwtSecret)
       		.parseClaimsJws(token)
       		.getBody()
       		.getExpiration();
	     
	   return new Date().before(expDate);
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
