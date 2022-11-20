package io.home4Me.Security.authentication.identity.user;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

import io.home4Me.Security.RoleTypes;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
class PermissionGainEvent extends ApplicationEvent {

	private LoginDetails loginDetails;
	private Set<RoleTypes> roles;
	
	public PermissionGainEvent(Object source, LoginDetails loginDetails, Set<RoleTypes> roles) {
		super(source);
		this.loginDetails = loginDetails;
		this.roles = roles;
	}
}
