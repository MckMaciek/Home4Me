package io.home4Me.Security.exceptions;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException(String msg) {
		super(msg);
	}
}
