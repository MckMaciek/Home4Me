package io.home4Me.Security.authentication.dto;

import java.time.LocalDateTime;
import java.util.Set;

import io.home4Me.Security.authentication.token.dto.TokenWrappee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private String username;
	private String email;
	private Set<String> roles;
	private TokenWrappee tokens;
	private LocalDateTime processedDate;
}
