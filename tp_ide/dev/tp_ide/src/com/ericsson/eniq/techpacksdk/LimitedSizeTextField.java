package com.ericsson.eniq.techpacksdk;

import java.awt.Color;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingworker.SwingWorker;

/**
 * A validating textField. Checks length and characters. If no allowed chars is
 * defined only A..Z, a..z, 0..9, ' ', ':' ,'/', '(', ')','-' and '_' are
 * allowed.
 * 
 * @author etuolem
 * 
 */
@SuppressWarnings("serial")
public class LimitedSizeTextField extends JTextField {

  public static final String WINDOWSFILECHARS_STRING;

  public static final String DATABASECHARS_STRING;

  public static String SERVERNAMECHARS_STRING;

  private boolean required = false;

  private boolean regExpField = false;

  static {
    final StringBuffer databasechars = new StringBuffer(98);
    
    databasechars.append((char) 10); // Enter allowed
    
    for (int ind = 32; ind <= 128; ind++) {
      databasechars.append((char) ind);
    }
    DATABASECHARS_STRING = databasechars.toString();
  }

  static {
    final StringBuffer windowsfilechars = new StringBuffer(88);
    for (int ind = 32; ind <= 128; ind++) {
      switch (ind) {
      case 34:
      case 42:
      case 47:
      case 58:
      case 60:
      case 62:
      case 63:
      case 92:
      case 124:
        break;
      default:
        windowsfilechars.append((char) ind);
        break;
      }
    }
    WINDOWSFILECHARS_STRING = windowsfilechars.toString();
  }

  static {
    
    final StringBuffer servernamechars = new StringBuffer(65);
    for (int ind = 45; ind <= 122; ind++) {
      switch (ind) {
      case 47:
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 91:
      case 92:
      case 93:
      case 94:
      case 96:
        break;
      default:
        servernamechars.append((char) ind);
        break;
      }
    }
    SERVERNAMECHARS_STRING = servernamechars.toString();
  }

  public static final Color ERROR_BG = Color.PINK;

  private final int limit;

  private char[] allowedChars;

  private Color neutralBg;

  public LimitedSizeTextField(final int limit, final boolean required) {
    this("", 0, limit, required);
  }

  public LimitedSizeTextField(final int cols, final int limit, final boolean required) {
    this("", cols, limit, required);
  }

  public LimitedSizeTextField(final String text, final int cols, final int limit, final boolean required) {
    this(null, text, cols, limit, required);
  }

  public LimitedSizeTextField(final String allowedChars, final String text, final int cols, final int limit,
      final boolean required) {
    super(text, cols);

    this.limit = limit;
    if (allowedChars == null) {
      this.allowedChars = DATABASECHARS_STRING.toCharArray();
    } else {
      this.allowedChars = allowedChars.toCharArray();
    }
    this.neutralBg = this.getBackground();

    getDocument().addDocumentListener(new RequiredWatch());
    this.required = required;

    if (required) {
      if (text.length() <= 0) {
        setBackground(ERROR_BG);
      }
    }
  }

  public void setAllowedChars(final char[] chars) {
    allowedChars = chars;
  }
  
  public char[] getAllowedChars() {
	  return this.allowedChars;
  }

  protected Document createDefaultModel() {
    return new LimitedSizeDocument();
  }

  /**
   * @return the regExpField
   */
  public boolean isRegExpField() {
    return regExpField;
  }

  /**
   * @param regExpField
   *          the regExpField to set
   */
  public void setRegExpField(boolean regExpField) {
    this.regExpField = regExpField;
  }

  public class LimitedSizeDocument extends PlainDocument {

    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {

      if (str == null) {
        return;
      }

      try {

        for (int i = 0; i < str.length(); i++) {
          boolean found = false;
          final char ch = str.charAt(i);

          for (int j = 0; j < allowedChars.length; j++) {
            if (ch == allowedChars[j]) {
              found = true;
              break;
            }
          }
          if (!found) {
            throw new Exception("Illegal character \"" + str.substring(i, i + 1) + "\" (defined)");
          }
        }

        if (LimitedSizeTextField.this.getText().length() >= limit) {
          throw new Exception("Field before insert is too long " + LimitedSizeTextField.this.getText().length()
              + " >= " + limit);
        } else if (LimitedSizeTextField.this.getText().length() + str.length() > limit) {
          super.insertString(offs, str.substring(0, limit - LimitedSizeTextField.this.getText().length()), a);
          throw new Exception("Too many chars. Stripped some");
        }

        super.insertString(offs, str, a);

      } catch (Exception e) {
        System.out.println(e);
        indicateError();
      }

    }

    private void indicateError() {
      final Vilkutin v = new Vilkutin();
      v.execute();
    }

  };

  public class RequiredWatch implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {

      boolean ok = true;
      String text = LimitedSizeTextField.this.getText();
      if (LimitedSizeTextField.this.required && text.length() == 0)
        ok = false;

      if (LimitedSizeTextField.this.isRegExpField() && !isValidRegExp())
        ok = false;

      if (ok)
        LimitedSizeTextField.this.setBackground(neutralBg);
      else
        LimitedSizeTextField.this.setBackground(ERROR_BG);

    }

    public void insertUpdate(final DocumentEvent e) {
      changedUpdate(e);
    }

    public void removeUpdate(final DocumentEvent e) {
      changedUpdate(e);
    }
        
    private boolean isValidRegExp() {
      boolean isOk = true;
      try {
        Pattern.compile(getText());
      } catch (Exception ex) {
        isOk = false;
      }
      return isOk;
    }

  };

  public class Vilkutin extends SwingWorker<Void, Void> {

    public Vilkutin() {
      if (LimitedSizeTextField.this.getBackground() == ERROR_BG) {
        LimitedSizeTextField.this.setBackground(neutralBg);
      } else {
        LimitedSizeTextField.this.setBackground(ERROR_BG);
      }
    }

    public Void doInBackground() {
      try {
        Thread.sleep(50);
      } catch (InterruptedException ie) {
      }

      return null;
    }

    protected void done() {
      LimitedSizeTextField.this.setBackground(neutralBg);
    }

  }

  public Color getNeutralBg() {
    return neutralBg;
  }

  public void setNeutralBg(final Color neutralBg) {
    this.neutralBg = neutralBg;
  }

}
