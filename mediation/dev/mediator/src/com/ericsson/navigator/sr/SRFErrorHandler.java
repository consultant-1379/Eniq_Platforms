package com.ericsson.navigator.sr;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ericsson.navigator.sr.util.Status;

public class SRFErrorHandler implements ErrorHandler {
	
	private Status status = Status.Success;
	
	private static final String classname = SRFErrorHandler.class.getName();
	private static Logger logger = Logger.getLogger(classname);

	public void error(final SAXParseException e) throws SAXException {
		logger.error("Parsing failed: "+ e.getMessage());
		logger.debug("Caused by: ",e);
		status = Status.Fail;
	}

	public void fatalError(final SAXParseException e) throws SAXException {
		logger.fatal("Parsing failed: "+ e.getMessage());
		logger.debug("Caused by: ",e);
		status = Status.Fail;
	}

	public void warning(final SAXParseException e) throws SAXException {
		logger.warn("Parsing failed: "+ e.getMessage());
		logger.debug("Caused by: ",e);
		status = Status.Fail;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
}
