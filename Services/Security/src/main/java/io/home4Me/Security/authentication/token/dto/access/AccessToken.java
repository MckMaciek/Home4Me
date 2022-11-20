package io.home4Me.Security.authentication.token.dto.access;

import java.util.Date;

import io.home4Me.Security.authentication.token.TokenType;
import io.home4Me.Security.authentication.token.dto.AuthenticationToken;

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
