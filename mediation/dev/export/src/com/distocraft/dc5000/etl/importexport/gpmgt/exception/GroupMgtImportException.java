package com.distocraft.dc5000.etl.importexport.gpmgt.exception;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
/**
 * Generic exception used for any unknown errors during the import.
 */
public class GroupMgtImportException extends GroupMgtException {
	/**
	 * Error message
	 */
	private static final String SLOGAN = "Error importing Group Management Data.";
	/**
	 * Error message additional info key
	 */
	private static final String ADD_INFO_ERROR = "error:";

	/**
	 * Constructor
	 * @param msg Error text
	 */
	public GroupMgtImportException(final String msg) {
		this(msg, null);
	}

	/**
	 * Constructor with original cause
	 * @param msg Error test
	 * @param cause Original cause, may be null.
	 */
	public GroupMgtImportException(final String msg, final Throwable cause) {
		super(SLOGAN, cause);
		addAdditionalInfo(ADD_INFO_ERROR, msg);
	}
}
