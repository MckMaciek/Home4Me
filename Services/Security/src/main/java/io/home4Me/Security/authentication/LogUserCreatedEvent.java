package io.home4Me.Security.authentication;

import org.springframework.context.ApplicationEvent;

import io.home4Me.Security.authentication.identity.user.dto.LoginDetailsDto;

@SuppressWarnings("serial")
public class LogUserCreatedEvent extends ApplicationEvent{

	private LoginDetailsDto loginDetails;
	
	public LogUserCreatedEvent(Object source, LoginDetailsDto loginDetails) {
		super(source);
		this.loginDetails = loginDetails;
	}
	
	public LoginDetailsDto getDetails() {
		return loginDetails;
	}
}
