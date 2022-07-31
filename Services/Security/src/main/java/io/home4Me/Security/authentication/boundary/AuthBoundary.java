package io.home4Me.Security.authentication.boundary;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.dto.LoginResponse;
import io.home4Me.Security.authentication.dto.RegisterRequest;
import io.home4Me.Security.authentication.services.JwtAuthenticationService;
import io.home4Me.Security.authentication.services.LoginDetailsService;

import static io.home4Me.Security.Access.ROLE_USER;

@RestController
@RequestMapping("/api/auth")
public class AuthBoundary {
	
	private final LoginDetailsService loginService;
	private final JwtAuthenticationService jwtService;
	
	@Autowired
	public AuthBoundary(LoginDetailsService loginService, JwtAuthenticationService jwtService){
		this.loginService = loginService;
		this.jwtService = jwtService;
	}
		
	@PostMapping("/register")
	public ResponseEntity<LocalDateTime> registerNewUser(@Valid @RequestBody RegisterRequest registerRequest){
		return ResponseEntity.ok(loginService.createUser(registerRequest).getCreationDate());
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
		return ResponseEntity.ok(jwtService.authenticateAndProvideTokens(loginRequest));
	}
	
	@PreAuthorize(ROLE_USER)
	@GetMapping("/refreshAccessToken")
	public ResponseEntity<LoginResponse> renewAccessToken(@NotBlank @RequestBody String refreshToken){
		return ResponseEntity.ok(jwtService.refreshAuthenticatedAccessToken(refreshToken));
	}
	
}
