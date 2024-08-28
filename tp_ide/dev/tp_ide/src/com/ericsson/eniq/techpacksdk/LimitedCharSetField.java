package com.ericsson.eniq.techpacksdk;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingworker.SwingWorker;

@SuppressWarnings("serial")
public class LimitedCharSetField extends JTextField {

  public static final Color ERROR_BG = Color.PINK;

  private final char[] chars;
  
  private final int length;
  
  private final Color neutralBg;
  
  public LimitedCharSetField(final String chars, final int length, final boolean required) {
    this("", chars, length, required);
  }

  public LimitedCharSetField(final String text, final String chars, final int length, final boolean required) {
    super(text, length);

    this.chars = chars.toCharArray();
    this.length = length;
    
    this.neutralBg = this.getBackground();
    
    if(required) {
      getDocument().addDocumentListener(new RequiredWatch());
      if(text.length() <= 0) {
        setBackground(ERROR_BG);
      }
    }
    
  }

  protected Document createDefaultModel() {
    return new LimitedCharSetDocument();
  }

  public class LimitedCharSetDocument extends PlainDocument {

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      
      if (str == null) {
        return;
      }
      
      try {
        final StringBuffer sb = new StringBuffer(LimitedCharSetField.this.getText());
        sb.insert(offs, str);

        if(sb.length() >= length) {
          throw new Exception();
        }
        
        for (int i = 0 ; i < sb.length() ; i++) {
          boolean found = false;
          for (int j = 0 ; j < chars.length ; j++) {
            if(sb.charAt(i) == chars[j]) {
              found = true;
            }
          }
          if(!found) {
            throw new Exception();
          }
        }
                
        super.insertString(offs, str, a);
        
      } catch(Exception e) {
        indicateError();
      }

    }

    private void indicateError() {
      LimitedCharSetField.this.setBackground(ERROR_BG);
      final Vilkutin v = new Vilkutin();
      v.execute();
    }

  };
  
  public class RequiredWatch implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      if (LimitedCharSetField.this.getText().length() > 0) {
        LimitedCharSetField.this.setBackground(neutralBg);
      } else {
        LimitedCharSetField.this.setBackground(ERROR_BG);
      }
    }

    public void insertUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
      changedUpdate(e);
    }

  };

  public class Vilkutin extends SwingWorker<Void, Void> {

    public Void doInBackground() {
      try {
        Thread.sleep(50);
      } catch (InterruptedException ie) {
      }
      
      return null;
    }

    protected void done() {
      LimitedCharSetField.this.setBackground(neutralBg);
    }
    
  };

}
