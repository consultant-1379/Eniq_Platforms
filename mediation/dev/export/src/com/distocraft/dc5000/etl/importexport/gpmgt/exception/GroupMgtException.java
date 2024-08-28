package com.distocraft.dc5000.etl.importexport.gpmgt.exception;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Base exception for all group management exceptions.
 * If a new exception type is to be added it should extend this class and provide its own SLOGAN.
 * Is can then use {@link #addAdditionalInfo(String, Object)} to add addition
 * information specific to the new exception.
 */
public class GroupMgtException extends RuntimeException {
	/**
	 * A Map of additional information, which maps
	 * some String identifier with a string providing
	 * additional information about that identifier.
	 * <p/>
	 * Can be used in anyway by the extending exception
	 * names with the exact cause of the exception being thrown
	 */
	private transient final Map<String, String> extraInfo;
	/**
	 * HardCoded String used to show the stack trace when an exception or Throwable
	 * is thrown.
	 * Can be used be other exceptions in future.
	 */
	private static final String ADD_INFO_STACK = "stackTrace:";
	/**
	 * String indicating no additional info is available.
	 */
	private static final String NO_INFO = "No Additional Info";
	/**
	 * String used as pre-amble for the additional info.
	 */
	private static final String ADD_INFO = "Additional Info:";
	/**
	 * New Line character.
	 */
	private static final String NEW_LINE = "\n  ";

	/**
	 * Default constructor.
	 * @param slogan The exception message.
	 * @param cause The root cause, can be null.
	 */
	public GroupMgtException(final String slogan, final Throwable cause) {
		super(slogan, cause);
		assert null != slogan : "No slogan supplied";
		extraInfo = new HashMap<String, String>();
		if (null != cause) {
			addAdditionalInfo(ADD_INFO_STACK, populateStackTrace(cause)); //NOPMD
		}
	}

	/**
	 * Add any additional information to the exception. The <code>toString()</code> will include
	 * all information added using this method.
	 * @param key A key to identify the information e.g. datafile:
	 * @param text The actual information e.g. a file name
	 */
	protected void addAdditionalInfo(final String key, final Object text) {
		if (null != extraInfo) {
			extraInfo.put(key, text.toString());
		}
	}

	/**
	 * Get an  additional information entry from the exception.
	 * @param infoKey the information key
	 * @return The information value
	 */
	public final String getAdditionalInfo(final String infoKey) {
		String lReturn;
		if (extraInfo == null || extraInfo.isEmpty() || !extraInfo.containsKey(infoKey)) {
			lReturn = NO_INFO;
		} else {
			lReturn = extraInfo.get(infoKey);
		}
		return lReturn;
	}

	/**
	 * Generate a stack trace.
	 * @param aThrowable The Exception
	 * @return Same as Throwable.printStackTrace() 
	 */
	private String populateStackTrace(final Throwable aThrowable) {
		//doing it this way lets an IDE process the output so you can jump to the file
		// by clicking on the stack output
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * Get all additional information in one String.
	 * @return All additional information.
	 */
	public final String getAdditionalInfoAsString() {
		String lReturn;
		if (extraInfo == null || extraInfo.isEmpty()) {
			lReturn = NO_INFO;
		} else {
			final StringBuilder buffer = new StringBuilder(ADD_INFO);
			for (String key : extraInfo.keySet()) {
				buffer.append(NEW_LINE);
				buffer.append(key);
				buffer.append(": ");
				buffer.append(extraInfo.get(key));
				buffer.append(";");
			}
			lReturn = buffer.toString();
		}
		return lReturn;
	}

	/**
	 * Get all additional info as a Key Value map.
	 * Map is immutable.
	 * @return Addition Info.
	 */
	public Map<String, String> getAdditionalInfo(){
		return Collections.unmodifiableMap(extraInfo);
	}


	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder(super.toString());
		buffer.append("\n");
		buffer.append(getAdditionalInfoAsString());
		return buffer.toString();
	}

}
