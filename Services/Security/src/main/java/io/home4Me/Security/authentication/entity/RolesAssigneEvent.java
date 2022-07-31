package io.home4Me.Security.authentication.entity;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

import io.home4Me.Security.RoleTypes;
import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class RolesAssigneEvent extends ApplicationEvent {

	private LoginDetails loginDetails;
	private Set<RoleTypes> roles;
	
	public RolesAssigneEvent(Object source, LoginDetails loginDetails, Set<RoleTypes> roles) {
		super(source);
		this.loginDetails = loginDetails;
		this.roles = roles;
	}
}
