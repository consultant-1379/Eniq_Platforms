package com.ericsson.navigator.sr;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractSRFContentHandler extends DefaultHandler {

	public static Logger logger = Logger.getLogger("System Registration");
	protected Locator myLoc;

	/**
	 * This methods sets a locator. It can be useful during debugging.
	 * 
	 * @param aLocator
	 */
	@Override
	public void setDocumentLocator(final Locator aLocator) {
		myLoc = aLocator;
	}

	@Override
	public void startDocument() {
		setDocumentLocator(myLoc);
	}
	
	public static String resolveAttribute(final String name,
			final Attributes attrs, final String defaultValue) {
		String result = attrs.getValue(name);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}
}
