package com.ericsson.navigator.esm.manager.pm.file.remote.plugin;

public class ParserException extends Exception {

	private static final long serialVersionUID = 1L;
 
	public ParserException(final String message){
		super(message);
	}
	
	public ParserException(final String message, final Exception e){
		super(message, e);
	}
}
