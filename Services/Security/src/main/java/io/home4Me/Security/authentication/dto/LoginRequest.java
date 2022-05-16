package io.home4Me.Security.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

	private String emailAddress;
	private String password;
	
}
