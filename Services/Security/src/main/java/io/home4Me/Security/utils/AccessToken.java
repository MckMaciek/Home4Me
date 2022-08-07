package io.home4Me.Security.utils;

import java.util.Date;

import io.home4Me.Security.authentication.AuthenticationToken;

public class AccessToken extends AuthenticationToken {
	
	private static final TokenType TOKEN_TYPE = TokenType.ACCESS;

	public AccessToken(String raw, String username, Date expiredDate) {
		super(raw, username, expiredDate);
	}

	@Override
	public TokenType getTokenType() {
		return TOKEN_TYPE;
	}

}
