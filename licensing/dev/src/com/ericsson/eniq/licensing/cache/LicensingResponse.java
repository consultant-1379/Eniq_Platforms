/***
 * This is the interface used for the response from the LicensingCache checkLicense() method. 
 */
package com.ericsson.eniq.licensing.cache;

import java.io.Serializable;

/**
 * @author ecarbjo
 */
public interface LicensingResponse extends Serializable {
	// constants to be used for the responseType.
	int LICENSE_INVALID = 0;
	int LICENSE_VALID = 1;
	int LICENSE_MISSING = 2;
	int LICENSE_SERVER_NOT_FOUND = 3;

	/**
	 * @return a user readable message for the given response (explanation why
	 *         the query failed for example)
	 */
	String getMessage();

	/**
	 * @return the type of response this is. See the constants of this interface
	 *         for the possible responses.
	 */
	int getResponseType();

	/**
	 * A method for quickly checking if the license is valid or not.
	 * 
	 * @return true if the license is valid, false otherwise.
	 */
	boolean isValid();
}
