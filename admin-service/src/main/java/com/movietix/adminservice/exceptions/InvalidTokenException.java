package com.movietix.adminservice.exceptions;

public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String msg) {
		super(msg);
	}
}
