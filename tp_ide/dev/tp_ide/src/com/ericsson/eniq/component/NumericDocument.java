package com.ericsson.eniq.component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


@SuppressWarnings("serial")
public class NumericDocument extends PlainDocument {

  @Override
  public void insertString(final int offset, final String text, final AttributeSet a) throws BadLocationException {
    // digits only please (or bring your own restriction/validation)
    final StringBuffer buf = new StringBuffer(text.length());
    for (int i = 0; i < text.length(); i++) {
      if (Character.isDigit(text.charAt(i))) { 
        buf.append(text.charAt(i));
      }
    }
    super.insertString(offset, buf.toString(), a);
  }
  
};
