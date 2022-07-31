package io.home4Me.Security.Events;

import org.springframework.context.ApplicationEvent;

import io.home4Me.Security.authentication.dto.LoginDetailsDto;
import io.home4Me.Security.authentication.entity.LoginDetails;

@SuppressWarnings("serial")
public class LogUserCreatedEvent extends ApplicationEvent{

	private LoginDetails loginDetails;
	
	public LogUserCreatedEvent(Object source, LoginDetails loginDetails) {
		super(source);
		this.loginDetails = loginDetails;
	}
	
	public LoginDetails getDetails() {
		return loginDetails;
	}
}
