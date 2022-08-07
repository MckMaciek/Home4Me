package io.home4Me.Security.Exceptions;

@SuppressWarnings("serial")
public class InvalidRefreshTokenException extends RuntimeException {
	public InvalidRefreshTokenException(String msg) {
		super(msg);
	}
}
