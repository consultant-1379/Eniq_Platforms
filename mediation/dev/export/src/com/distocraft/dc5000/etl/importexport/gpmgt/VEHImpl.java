package com.distocraft.dc5000.etl.importexport.gpmgt;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores any errors generate during the schema validation
 */
public class VEHImpl implements ValidationEventHandler {
	/**
	 * Holders errors
	 */
  private transient final List<String> _errors;
	/**
	 * Holds warnings
	 */
	private transient final List<String> _warnings;
	/**
	 * Hold fatal errors
	 */
	private transient final List<String> _fatals;

	/**
	 * Constructor
	 */
	VEHImpl(){
		_errors = new ArrayList<String>();
		_warnings = new ArrayList<String>();
		_fatals = new ArrayList<String>();
	}

	/**
	 * Formats an error to include the line and column number
	 * @param msg The validation error
	 * @param lNumber The line the error is on
	 * @param cNumber The column number
	 * @return String containing error, line & cols of error
	 */
	private String format(final String msg, final int lNumber, final int cNumber){
		final StringBuilder buffer = new StringBuilder();
		buffer.append(msg).append("@").append("Line").append(lNumber).append(":").append(cNumber);
		return buffer.toString();
	}

	/**
	 * Handle an validation event i.e. error, fatal or warning
	 * @param event The event to handle
	 * @return TRUE
	 */
	public boolean handleEvent(final ValidationEvent event) {
		final String msg = format(event.getMessage(), event.getLocator().getLineNumber(),
			 event.getLocator().getColumnNumber());
		switch (event.getSeverity()) {
			case ValidationEvent.WARNING:
				_warnings.add(msg);
			case ValidationEvent.ERROR:
				_errors.add(msg);
			case ValidationEvent.FATAL_ERROR:
			default:
				_fatals.add(msg);
				break;
		}
		return true;
	}

	/**
	 * Where there any errors (both error & fatal) during validation
	 * @return TRUE is there were, FALSE otherwise
	 */
	public boolean wereErrors(){
		return wereParseErrors() || wereFatalErrors();
	}

	/**
	 * Where there an errors during validation
	 * @return TRUE is there were, FALSE otherwise
	 */
	public boolean wereParseErrors(){
		return !_errors.isEmpty();
	}

	/**
	 * Where there any warnings during validation
	 * @return TRUE is there were, FALSE otherwise
	 */
	public boolean wereParseWarnings(){
		return !_warnings.isEmpty();
	}

	/**
	 * Where there any fatal errors during validation
	 * @return TRUS is there were, FALSE otherwise
	 */
	public boolean wereFatalErrors(){
		return !_fatals.isEmpty();
	}

	/**
	 * Get the Errors during validation, can be empty.
	 * @return List of errors generated by validation
	 */
	public List<String> getParseErrors() {
		return _errors;
	}

	/**
	 * Get the warnings during validation, can be empty.
	 * @return List of warnings generated by validation
	 */
	public List<String> getParseWarnings() {
		return _warnings;
	}

	/**
	 * Get the fatal errors during validation, can be empty.
	 * @return List of fatal errors generated by validation
	 */
	public List<String> getFatalErrors() {
		return _fatals;
	}
}
