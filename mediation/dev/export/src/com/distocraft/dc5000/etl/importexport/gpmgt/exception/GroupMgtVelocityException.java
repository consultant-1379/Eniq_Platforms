package com.distocraft.dc5000.etl.importexport.gpmgt.exception;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import org.apache.velocity.exception.VelocityException;

/**
 * A <code>GroupMgtVelocityException</code> exception is thrown if the Velocity merge failes for any reason
 */
public class GroupMgtVelocityException extends GroupMgtException {
	/**
	 * Exception Message
	 */
	private static final String SLOGAN = "Velocity Merge Failed";
	/**
	 * Additional info key for the template used.
	 */
	private static final String ADD_TEMPLATE = "template:";

	/**
	 * Constructor
	 * @param template the template being used during the velocity merge.
	 * @param cause The orginal cause, may be null.
	 */
	public GroupMgtVelocityException(final String template, final VelocityException cause){
		this(template, (Exception)cause);
	}

	/**
	 * Constructor
	 * @param template the template being used during the velocity merge.
	 * @param cause The orginal cause, may be null.
	 */
	public GroupMgtVelocityException(final String template, final Exception cause){
		super(SLOGAN, cause);
		addAdditionalInfo(ADD_TEMPLATE, template);
	}
}
