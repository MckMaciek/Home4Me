package io.home4Me.Security.authentication.dto;

import java.time.LocalDateTime;
import java.util.Set;

import io.home4Me.Security.RoleTypes;
import io.home4Me.Security.utils.TokenWrappee;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

	private String username;
	private String email;
	private Set<RoleTypes> roles;
	private TokenWrappee tokens;
	private LocalDateTime processedDate;
}
