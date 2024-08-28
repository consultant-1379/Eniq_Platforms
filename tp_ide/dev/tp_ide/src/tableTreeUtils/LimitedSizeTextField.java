package tableTreeUtils;

import java.awt.Color;

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

    /**
     * 
     */
    public static final String WINDOWSFILECHARS_STRING;
    /**
     * 
     */
    public static final String DATABASECHARS_STRING;

    static {
	final StringBuffer databasechars = new StringBuffer(97);
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

    public static final Color ERROR_BG = Color.PINK;

    private int limit;

    private char[] allowedChars;

    private Color neutralBg;

    /**
     * Constructor.
     * 
     * @param limit
     * @param required
     */
    public LimitedSizeTextField(int limit, boolean required) {
	this("", 0, limit, required);
    }

    /**
     * Constructor.
     * 
     * @param cols
     * @param limit
     * @param required
     */
    public LimitedSizeTextField(int cols, int limit, boolean required) {
	this("", cols, limit, required);
    }

    /**
     * 
     * Constructor.
     * 
     * @param text
     * @param cols
     * @param limit
     * @param required
     */
    public LimitedSizeTextField(String text, int cols, int limit,
	    boolean required) {
	this(null, text, cols, limit, required);
    }

    /**
     * Constructor.
     * 
     * NOTE: There is a bug here. The text must be null and required false in
     * creation. In case real string is used, the text will not be visible in
     * the text field. If required = true is used, the the text field does not
     * work at all. The real text can be set after the creation with setText()
     * method.
     * 
     * @param allowedChars
     * @param text
     * @param cols
     * @param limit
     * @param required
     */
    public LimitedSizeTextField(String allowedChars, String text, int cols,
	    int limit, boolean required) {
	super(null, text, cols);

	this.limit = limit;
	if (allowedChars == null) {
	    this.allowedChars = DATABASECHARS_STRING.toCharArray();
	} else {
	    this.allowedChars = allowedChars.toCharArray();
	}

	this.neutralBg = this.getBackground();

	if (required) {
	    getDocument().addDocumentListener(new RequiredWatch());
	    if (text.length() <= 0) {
		setBackground(ERROR_BG);
	    }
	}

    }

    protected Document createDefaultModel() {
	return new LimitedSizeDocument();
    }

    /**
     * @author etuolem
     * 
     */
    public class LimitedSizeDocument extends PlainDocument {

	/**
	 * Overridden version of the method.
	 * 
	 * @see javax.swing.text.PlainDocument#insertString(int,
	 *      java.lang.String, javax.swing.text.AttributeSet)
	 */
	public void insertString(final int offs, final String str,
		final AttributeSet a) throws BadLocationException {

	    if (str == null) {
		return;
	    }

	    // HACK!!!
	    // When calling the super constructor the outer class
	    // constructor, then this method will be called before the
	    // outer class constructor execution continues. The
	    // result is that the allowedChars array is null and the limit is
	    // zero when we get here the first time. So, in this case they are
	    // initialized here (temporarily). The real values will be set in
	    // the outer constructor later.
	    //
	    // NOTE: This is needed in case the LimitedSizeTextField is created
	    // with a string. If null string is used in creation and the text is
	    // set later, then it works fine.
	    //
	    // if (allowedChars == null) {
	    // allowedChars = DATABASECHARS_STRING.toCharArray();
	    // }
	    // if (limit == 0) {
	    // limit = 256;
	    // }

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
			throw new Exception("Illegal character \""
				+ str.substring(i, i + 1) + "\" (defined)");
		    }
		}

		if (LimitedSizeTextField.this.getText().length() >= limit) {
		    throw new Exception("Field before insert is too long "
			    + LimitedSizeTextField.this.getText().length()
			    + " >= " + limit);
		} else if (LimitedSizeTextField.this.getText().length()
			+ str.length() > limit) {
		    super.insertString(offs, str.substring(0, limit
			    - LimitedSizeTextField.this.getText().length()), a);
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

    /**
     * @author etuolem
     * 
     */
    public class RequiredWatch implements DocumentListener {

	public void changedUpdate(final DocumentEvent e) {
	    if (LimitedSizeTextField.this.getText().length() > 0) {
		LimitedSizeTextField.this.setBackground(neutralBg);
	    } else {
		LimitedSizeTextField.this.setBackground(ERROR_BG);
	    }
	}

	public void insertUpdate(final DocumentEvent e) {
	    changedUpdate(e);
	}

	public void removeUpdate(final DocumentEvent e) {
	    changedUpdate(e);
	}

    };

    /**
     * @author etuolem
     * 
     */
    public class Vilkutin extends SwingWorker<Void, Void> {

	/**
	 * 
	 */
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

    /**
     * @return the neutral background color
     */
    public Color getNeutralBg() {
	return neutralBg;
    }

    /**
     * @param neutralBg
     */
    public void setNeutralBg(final Color neutralBg) {
	this.neutralBg = neutralBg;
    };

}
