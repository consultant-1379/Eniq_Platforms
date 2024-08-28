package com.ericsson.navigator.esm.util.log;


public class LogException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public LogException(final Exception e) {
		super(e);
	}

	public LogException(final String message) {
		super(message);
	}
	
	public LogException(final String message, final Exception e) {
		super(message, e);
	}

}
