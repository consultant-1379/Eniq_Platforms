package com.ericsson.navigator.esm.util.cssr.io;

/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2010
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

/**
 * Exception indicates the user made an error inputing data at the command
 * prompt.
 * 
 * @author ejammor
 */
public class UserInputException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserInputException(final String msg) {
		super(msg);
	}
}
