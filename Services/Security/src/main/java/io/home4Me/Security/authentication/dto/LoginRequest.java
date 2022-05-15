package io.home4Me.Security.authentication.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class LoginRequest {

	private String emailAddress;
	private String password;
	
}
