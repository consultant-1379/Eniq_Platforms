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
public class LimitedRangeIntegerField extends JTextField {

  public static final Color ERROR_BG = Color.PINK;

  private final int minvalue;
  
  private final int maxvalue;

  private final Color neutralBg;
  
  public void setEnabled(boolean enabled){
	  super.setEnabled(enabled);
	    
      if(super.getText()!=null && super.getText().length() <= 0) {
          setBackground(ERROR_BG);
        }
	  
	    if(!enabled) {
	        setBackground(neutralBg);
	    }
  }
 

  public LimitedRangeIntegerField(final int minvalue, int maxvalue, final boolean required) {
    this("", 0, minvalue, maxvalue, required);
  }

  public LimitedRangeIntegerField(final int cols, final int minvalue, int maxvalue, final boolean required) {
    this("", cols, minvalue, maxvalue, required);
  }

  public LimitedRangeIntegerField(final String text, final int cols, final int minvalue, final int maxvalue, final boolean required) {
    super(text, cols);

    this.minvalue = minvalue;
    this.maxvalue = maxvalue;
    
    this.neutralBg = this.getBackground();
    
    if(required) {
      getDocument().addDocumentListener(new RequiredWatch());
      if(text.length() <= 0) {
        setBackground(ERROR_BG);
      }
    }
    
  }

  protected Document createDefaultModel() {
    return new LimitedRangeDocument();
  }

  public class LimitedRangeDocument extends PlainDocument {

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      
      if (str == null) {
        return;
      }
      
      try {
        final StringBuffer sb = new StringBuffer(LimitedRangeIntegerField.this.getText());
        sb.insert(offs, str);
        
        //Exception if not valid integer
        int value = Integer.parseInt(sb.toString());
         
        if(value < minvalue || value > maxvalue) {
          throw new Exception();
        }
        
        super.insertString(offs, str, a);
        
      } catch(Exception e) {
        indicateError();
      }

    }

    private void indicateError() {
      LimitedRangeIntegerField.this.setBackground(ERROR_BG);
      final Vilkutin v = new Vilkutin();
      v.execute();
    }

  };
  
  public class RequiredWatch implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      if (LimitedRangeIntegerField.this.getText().length() > 0) {
        LimitedRangeIntegerField.this.setBackground(neutralBg);
      } else {
        LimitedRangeIntegerField.this.setBackground(ERROR_BG);
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
      LimitedRangeIntegerField.this.setBackground(neutralBg);
    }
    
  };

}
