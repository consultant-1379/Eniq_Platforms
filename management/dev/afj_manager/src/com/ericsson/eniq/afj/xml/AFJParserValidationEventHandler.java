/**
 * 
 */
package com.ericsson.eniq.afj.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.w3c.dom.Node;

/**
 * @author esunbal
 * Validation class to capture the various errors during xml parsing.
 *
 */
public class AFJParserValidationEventHandler implements ValidationEventHandler {
	
	private final List<String> messages = new ArrayList<String>();

	public List<String> getMessages() {      
		return messages;
	}    

	public boolean handleEvent(final ValidationEvent event) {
		if (event == null){        
			throw new IllegalArgumentException("event is null");
		}
		// calculate the severity prefix and return value      
		String severity = null;
		boolean continueParsing = false;
		switch (event.getSeverity()) {        
		case ValidationEvent.WARNING:          
			severity = "Warning";
			continueParsing = true;
			// continue after warnings          
			break;

		case ValidationEvent.ERROR: 
			severity = "Error";
			// continue after errors -> Should parsing continue ?
			continueParsing = true;          
			break;
		case ValidationEvent.FATAL_ERROR:
			severity = "Fatal error";
			// continue after fatal errors -> Should parsing continue ?
			continueParsing = true;          
			break;
		default:          
			assert false : "Unknown severity.";
		}      
		final String location = getLocationDescription(event);
		final String message = severity + " parsing " + location + " due to " + event.getMessage();
		messages.add(message);
		return continueParsing;
	}

	/**
	 * Gives the location in the xml for the specific validation event.
	 * @param event
	 * @return
	 */
	private String getLocationDescription(final ValidationEvent event) {
		final ValidationEventLocator locator = event.getLocator();
		if (locator == null) {
			return "XML with location unavailable";
		}      
		final StringBuffer msg = new StringBuffer();
		final URL url = locator.getURL();
		final Object obj = locator.getObject();
		final Node node = locator.getNode();
		final int line = locator.getLineNumber();
		if (url != null || line != -1) {
			msg.append("line " + line);
			if (url != null){
				msg.append(" of " + url);
			}
		} else if (obj != null) { 
			msg.append(" obj: " + obj.toString());
		} else if (node != null) { 
			msg.append(" node: " + node.toString());
		}  
		return msg.toString();
	}  
}	

