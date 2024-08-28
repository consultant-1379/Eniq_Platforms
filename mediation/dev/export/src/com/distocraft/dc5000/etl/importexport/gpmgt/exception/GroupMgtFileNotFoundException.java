package com.distocraft.dc5000.etl.importexport.gpmgt.exception;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

/**
 * Used in place of the {@link java.io.FileNotFoundException} exception which is a checked exception.
 */
public class GroupMgtFileNotFoundException extends GroupMgtException {
	/**
	 * Exception Message
	 */
	private static final String SLOGAN = "File could not be found";
	/**
	 * Additional info key file that couldn't be found.
	 */
	private static final String AINFO_DATA_FILE = "datafile:";

	/**
	 * Constructor
	 * @param file The file that cound not be found
	 * @param cause Original cause 
	 */
	public GroupMgtFileNotFoundException(final String file , final Throwable cause) {
		super(SLOGAN, cause);
		addAdditionalInfo(AINFO_DATA_FILE, file);
	}

	/**
	 * Constructor
	 * @param file The file that cound not be found
	 */
	public GroupMgtFileNotFoundException(final String file) {
		this(file, null);
	}
}
