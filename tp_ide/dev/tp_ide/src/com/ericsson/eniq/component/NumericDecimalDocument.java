package com.ericsson.eniq.component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Allow the user to add decimal value for aggregation threshold.
 * Has a text field which allows the user to set the "Aggregation Threshold" property.
 * 
 * @author ejohabd
 *
 */


@SuppressWarnings("serial")
public class NumericDecimalDocument extends PlainDocument {

	@Override	
	  public void insertString(final int offset, final String text, final AttributeSet a) throws BadLocationException {
	    // digits only please (or bring your own restriction/validation)
	    final StringBuffer buf = new StringBuffer(text.length());
	    for (int i = 0; i < text.length(); i++) {
	      if (Character.isDigit(text.charAt(i)) || text.indexOf(".") != -1){
	    	  buf.append(text.charAt(i));
	      }
	    }
	    super.insertString(offset, buf.toString(), a);
	  }
	  
	};
