package com.ericsson.navigator.esm.manager.snmp;

public class DateAndTimeException extends Exception {

	private static final long serialVersionUID = 1L; 

	public DateAndTimeException(final String message, final Exception e) {
		super(message, e);
	}
}
