package io.home4Me.Security.authentication.dto;

import java.time.LocalDateTime;
import java.util.Set;

import io.home4Me.Security.authentication.entity.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginDetailsDto {
	
	private String username;
	private String email;
	private boolean isUserEnabled;
	private boolean isUserNonLocked;
	private boolean isUserAccountNonExpired;
	private boolean isUserCredentialsNonExpired;
	private LocalDateTime creationDate;
	private Set<UserRoles> userRoles;
}
