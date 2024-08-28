package com.distocraft.dc5000.etl.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


public class NumericDocument extends PlainDocument {

  public void insertString(int offset, String text, AttributeSet a) throws BadLocationException {
    // digits only please (or bring your own restriction/validation)
    StringBuffer buf = new StringBuffer(text.length());
    for (int i = 0; i < text.length(); i++) {
      if (Character.isDigit(text.charAt(i)))
        buf.append(text.charAt(i));
    }
    super.insertString(offset, buf.toString(), a);
  }
  
};
