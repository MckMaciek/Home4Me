package io.home4Me.Security.Events;

import org.springframework.context.ApplicationEvent;

import io.home4Me.Security.authentication.dto.LoginDetailsDto;
import io.home4Me.Security.authentication.entity.LoginDetails;

@SuppressWarnings("serial")
public class LogUserCreated extends ApplicationEvent{

	private LoginDetails loginDetails;
	
	public LogUserCreated(Object source, LoginDetails loginDetails) {
		super(source);
		this.loginDetails = loginDetails;
	}
	
	public LoginDetails getDetails() {
		return loginDetails;
	}
}
