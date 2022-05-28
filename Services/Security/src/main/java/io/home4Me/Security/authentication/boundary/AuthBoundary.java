package io.home4Me.Security.authentication.boundary;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.home4Me.Security.authentication.dto.LoginRequest;
import io.home4Me.Security.authentication.services.JwtService;
import io.home4Me.Security.utils.TokenWrappee;

@RestController
@RequestMapping("/api/auth")
public class AuthBoundary {
	
	private JwtService jwtService;
	
	@Autowired
	public AuthBoundary(JwtService jwtService){
		this.jwtService = jwtService;
	}
	
	public ResponseEntity<TokenWrappee> provideRefreshWithAccessToken(@Valid @RequestBody LoginRequest loginRequest) {
		
		return ResponseEntity.ok(jwtService.provideRefreshAndAccessToken(loginRequest));
	}

	
	
}
