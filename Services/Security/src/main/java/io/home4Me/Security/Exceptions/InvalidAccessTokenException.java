package io.home4Me.Security.Exceptions;

@SuppressWarnings("serial")
public class InvalidAccessTokenException extends InvalidTokenException {
	public InvalidAccessTokenException(String msg) {
		super(msg);
	}
}
