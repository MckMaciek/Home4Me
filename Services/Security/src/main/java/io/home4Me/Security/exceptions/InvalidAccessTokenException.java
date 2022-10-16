package io.home4Me.Security.exceptions;

@SuppressWarnings("serial")
public class InvalidAccessTokenException extends InvalidTokenException {
	public InvalidAccessTokenException(String msg) {
		super(msg);
	}
}
