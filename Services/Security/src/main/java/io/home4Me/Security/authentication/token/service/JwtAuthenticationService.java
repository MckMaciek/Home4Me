package io.home4Me.Security.authentication.token.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.LoginResponse;
import io.home4Me.Security.authentication.token.dto.AuthenticationToken;
import io.home4Me.Security.authentication.token.dto.TokenWrappee;
import io.home4Me.Security.authentication.token.dto.access.AccessToken;
import io.home4Me.Security.authentication.token.dto.refresh.RefreshToken;
import io.home4Me.Security.exceptions.ExpiredTokenException;
import io.home4Me.Security.exceptions.InvalidTokenException;

@Service
public class JwtAuthenticationService {
	
	private static final Logger logger = LogManager.getLogger(JwtAuthenticationService.class);
	
	private final AuthenticationManager authenticationManager;
	private final JwtInnerService jwtInnerService;
	private final JwtUtils utils;
	
	@Autowired
	public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtInnerService jwtInnerService, JwtUtils utils) {
		this.jwtInnerService = jwtInnerService;
		this.authenticationManager = authenticationManager;
		this.utils = utils;
	}

	public LoginResponse authenticateAndProvideTokens(LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        
        TokenWrappee generatedTokens = jwtInnerService.provideRefreshAndAccessToken(userDetails);
                
        return LoginResponse.builder()
        		.tokens(generatedTokens)
        		.username(userDetails.getUsername())
        		.roles(extractRoles(userDetails.getAuthorities()))
        		.processedDate(LocalDateTime.now())
        		.build();
	}
	
	public LoginResponse refreshAuthenticatedAccessToken(TokenWrappee tokenWrapee) {
			
		VerificationInfo.checkAndInCaseOfFailureRun(tokenWrapee.getValidationInfo(), (invalidToken -> {
			logger.info(invalidToken.getReason());
			throw new InvalidTokenException(invalidToken.getReason());
		}));
		
		RefreshToken refreshToken = utils.wrapToken(tokenWrapee.getRefreshToken(), tokenWrapee.getAccessToken());
		AccessToken outDatedToken = utils.wrapToken(tokenWrapee.getAccessToken());
		
		checkTokensNotExpired(refreshToken, outDatedToken);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) auth.getDetails();
		
		TokenWrappee generatedAccessToken = jwtInnerService.provideAccessToken(refreshToken, outDatedToken, userDetails);
		
		return LoginResponse.builder()
        		.tokens(generatedAccessToken)
        		.username(userDetails.getUsername())
        		.roles(extractRoles(userDetails.getAuthorities()))
        		.processedDate(LocalDateTime.now())
        		.build();
	}
	
	private void checkTokensNotExpired(RefreshToken refreshToken, AccessToken accessToken) {
		List<AuthenticationToken> tokens = List.of(refreshToken, accessToken);
		tokens.forEach(token -> {
			if(token.isTokenExpired()) {
				logger.info("Token {} is expired", token.getTokenType());
				throw new ExpiredTokenException(String.format("token - %s is expired - %s", token.getTokenType(), token.getTokenExpiration()));
			}
		});
	}
	
	private Set<String> extractRoles(Collection<? extends GrantedAuthority> collection) {
		return collection.stream()
						  .map(GrantedAuthority::getAuthority)
					      .collect(Collectors.toSet());
	}
	
}
