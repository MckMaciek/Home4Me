package io.home4Me.Security.authentication;

import java.util.Date;

import io.home4Me.Security.utils.TokenType;

public abstract class AuthenticationToken {
	
	protected final String raw;
	
	protected final Date expiredDate;
	protected final String username;
	
	public AuthenticationToken(String raw ,String username, Date expiredDate) {
		this.username = username;
		this.expiredDate = expiredDate;
		this.raw = raw;
	}
	
	public String getRaw() {
		return raw;
	}
	public Date getTokenExpiration() {
		return expiredDate;
	}
	public String getUsername() {
		return username;
	}
	public boolean isTokenExpired() {
		return new Date().after(expiredDate);
	}
	
	public abstract TokenType getTokenType();
}
