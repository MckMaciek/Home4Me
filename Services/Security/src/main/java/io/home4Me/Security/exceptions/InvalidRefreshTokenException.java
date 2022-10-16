package io.home4Me.Security.exceptions;

@SuppressWarnings("serial")
public class InvalidRefreshTokenException extends RuntimeException {
	public InvalidRefreshTokenException(String msg) {
		super(msg);
	}
}
