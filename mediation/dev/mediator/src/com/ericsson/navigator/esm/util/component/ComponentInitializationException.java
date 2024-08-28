package com.ericsson.navigator.esm.util.component;

public class ComponentInitializationException extends Exception {

	private static final long serialVersionUID = 1L; 
	private final boolean fatal;
	
	public ComponentInitializationException(final String message, final Exception e, final boolean fatal){
		super(message, e);
		this.fatal = fatal;
	}
	
	public boolean isFatal(){
		return fatal;
	}
}
