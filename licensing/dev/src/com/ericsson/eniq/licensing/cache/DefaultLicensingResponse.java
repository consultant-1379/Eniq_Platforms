/***
 * This is the default implementation of the LicensingResponse interface.
 */
package com.ericsson.eniq.licensing.cache;

/**
 * @author ecarbjo
 */
public class DefaultLicensingResponse implements LicensingResponse {

	private static final long serialVersionUID = -3212387462491466067L;

	final private int responseType;
	final private String message;

	/**
	 * Default constructor
	 * 
	 * @param type
	 *            the type of response that this is. See
	 *            com.distrocraft.dc5000.licensing.cache.LicensingResponse for
	 *            the int declarations
	 * @param msg
	 *            A user friendly message to print if needed.
	 */
	public DefaultLicensingResponse(final int type, final String msg) {
		this.responseType = type;
		this.message = msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.distrocraft.dc5000.licensing.cache.LicensingResponse#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.distrocraft.dc5000.licensing.cache.LicensingResponse#getResponseType
	 * ()
	 */
	public int getResponseType() {
		return responseType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.distrocraft.dc5000.licensing.cache.LicensingResponse#isValid()
	 */
	public boolean isValid() {
		return (responseType == LICENSE_VALID);
	}
	
}
