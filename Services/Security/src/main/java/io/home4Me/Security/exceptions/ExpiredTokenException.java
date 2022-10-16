package io.home4Me.Security.exceptions;

@SuppressWarnings("serial")
public class ExpiredTokenException extends InvalidTokenException {
	public ExpiredTokenException(String msg) {
		super(msg);
	}
}
