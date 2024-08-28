package com.ericsson.navigator.esm.manager.snmp;

public class TranslationException extends Exception {

	private static final long serialVersionUID = 1L;
 
	public TranslationException(final String message){
		super(message);
	}
	
	public TranslationException(final String message, final Exception e){
		super(message, e);
	}
}
