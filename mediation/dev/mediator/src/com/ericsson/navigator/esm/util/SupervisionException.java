package com.ericsson.navigator.esm.util;

public class SupervisionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public SupervisionException(final String message) {
		super(message);
	}
	
	public SupervisionException(final String message, final Exception e) {
		super(message, e);
	}
}
