package io.home4Me.Security.authentication.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.VerificationInfo;
import io.home4Me.Security.authentication.AuthenticationToken;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.LoginResponse;
import io.home4Me.Security.authentication.entity.LoginDetails;
import io.home4Me.Security.authentication.entity.UserRoles;
import io.home4Me.Security.exceptions.ExpiredTokenException;
import io.home4Me.Security.exceptions.InvalidTokenException;
import io.home4Me.Security.utils.AccessToken;
import io.home4Me.Security.utils.JwtUtils;
import io.home4Me.Security.utils.RefreshToken;
import io.home4Me.Security.utils.TokenWrappee;

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
        LoginDetails user = (LoginDetails) authentication.getPrincipal();
        
        TokenWrappee generatedTokens = jwtInnerService.provideRefreshAndAccessToken(userDetails);
                
        return LoginResponse.builder()
        		.tokens(generatedTokens)
        		.username(user.getUsername())
        		.email(user.getEmail())
        		.roles(extractRoles(user.getUserRoles()))
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
		LoginDetails user = (LoginDetails) auth.getPrincipal();
		
		TokenWrappee generatedAccessToken = jwtInnerService.provideAccessToken(refreshToken, outDatedToken, userDetails);
		
		return LoginResponse.builder()
        		.tokens(generatedAccessToken)
        		.username(user.getUsername())
        		.email(user.getEmail())
        		.roles(extractRoles(user.getUserRoles()))
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
	
	private Set<RoleTypes> extractRoles(Set<UserRoles> roles) {
		return roles.stream()
						  .map(UserRoles::getRole)
					      .collect(Collectors.toSet());
	}
	
}
