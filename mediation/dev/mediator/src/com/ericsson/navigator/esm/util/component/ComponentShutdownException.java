package com.ericsson.navigator.esm.util.component;

public class ComponentShutdownException extends Exception {

	private static final long serialVersionUID = 1L; 
	
	public ComponentShutdownException(final String message, final Exception e) {
		super(message, e);
	}

}
