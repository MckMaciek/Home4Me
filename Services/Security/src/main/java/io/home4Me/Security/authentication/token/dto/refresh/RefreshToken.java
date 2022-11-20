package io.home4Me.Security.authentication.token.dto.refresh;

import java.util.Date;

import io.home4Me.Security.authentication.token.TokenType;
import io.home4Me.Security.authentication.token.dto.AuthenticationToken;

public class RefreshToken extends AuthenticationToken{
	
	private static final TokenType TOKEN_TYPE = TokenType.REFRESH;
	
	public RefreshToken(String raw, String username, Date expirationDate) {
		super(raw, username, expirationDate);
	}

	@Override
	public TokenType getTokenType() {
		return TOKEN_TYPE;
	}
}
