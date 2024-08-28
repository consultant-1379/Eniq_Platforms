package com.ericsson.eniq.techpacksdk;

import java.awt.Color;

import javax.swing.JPasswordField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingworker.SwingWorker;

@SuppressWarnings("serial")
public class LimitedSizePasswordField extends JPasswordField {

  public static final Color ERROR_BG = Color.PINK;

  private final int limit;

  private final Color neutralBg;
  
  public LimitedSizePasswordField(final int limit, final boolean required) {
    this("", 0, limit, required);
  }

  public LimitedSizePasswordField(final int cols, final int limit, final boolean required) {
    this("", cols, limit, required);
  }

  public LimitedSizePasswordField(final String text, final int cols, final int limit, final boolean required) {
    super(text, cols);

    this.limit = limit;
    this.neutralBg = this.getBackground();
    
    if(required) {
      getDocument().addDocumentListener(new RequiredWatch());
      if(text.length() <= 0) {
        setBackground(ERROR_BG);
      }
    }
    
  }

  protected Document createDefaultModel() {
    return new LimitedSizeDocument();
  }

  public class LimitedSizeDocument extends PlainDocument {

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      
      if (str == null) {
        return;
      }

      if (LimitedSizePasswordField.this.getText().length() >= limit) {
        indicateError();
        return;
      } else if (LimitedSizePasswordField.this.getText().length() + str.length() > limit) {
        indicateError();
        super.insertString(offs, str.substring(0, limit - LimitedSizePasswordField.this.getText().length()), a);
      } else {
        super.insertString(offs, str, a);
      }

    }

    private void indicateError() {
      LimitedSizePasswordField.this.setBackground(ERROR_BG);
      final Vilkutin v = new Vilkutin();
      v.execute();
    }

  };
  
  public class RequiredWatch implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      if (LimitedSizePasswordField.this.getText().length() > 0) {
        LimitedSizePasswordField.this.setBackground(neutralBg);
      } else {
        LimitedSizePasswordField.this.setBackground(ERROR_BG);
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
      LimitedSizePasswordField.this.setBackground(neutralBg);
    }
    
  };

}
