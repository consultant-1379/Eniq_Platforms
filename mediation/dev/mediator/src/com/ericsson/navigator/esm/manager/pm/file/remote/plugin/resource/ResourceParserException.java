/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.resource;

/**
 * @author esamart
 *
 */
public class ResourceParserException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourceParserException(final String message, final Exception e) {
		super(message, e);
	}

	public ResourceParserException(final String message) {
		super(message);
	}

}
