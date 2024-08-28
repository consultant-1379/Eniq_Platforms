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
 * Validation class used for limited range integers.
 * 
 * @author etuolem
 * 
 */
@SuppressWarnings("serial")
public class LimitedRangeIntegerField extends JTextField {

    /**
     * Error background color
     */
    public static final Color ERROR_BG = Color.PINK;

    private final int minvalue;

    private final int maxvalue;

    private final Color neutralBg;

    /**
     * Constructor.
     * 
     * @param minvalue
     * @param maxvalue
     * @param required
     */
    public LimitedRangeIntegerField(final int minvalue, int maxvalue,
	    final boolean required) {
	this("", 0, minvalue, maxvalue, required);
    }

    /**
     * Constructor.
     * 
     * @param cols
     * @param minvalue
     * @param maxvalue
     * @param required
     */
    public LimitedRangeIntegerField(final int cols, final int minvalue,
	    int maxvalue, final boolean required) {
	this("", cols, minvalue, maxvalue, required);
    }

    /**
     * Constructor.
     * 
     * @param text
     * @param cols
     * @param minvalue
     * @param maxvalue
     * @param required
     */
    public LimitedRangeIntegerField(final String text, final int cols,
	    final int minvalue, final int maxvalue, final boolean required) {
	super(text, cols);

	this.minvalue = minvalue;
	this.maxvalue = maxvalue;

	this.neutralBg = this.getBackground();

	if (required) {
	    getDocument().addDocumentListener(new RequiredWatch());
	    if (text.length() <= 0) {
		setBackground(ERROR_BG);
	    }
	}

    }

    protected Document createDefaultModel() {
	return new LimitedRangeDocument();
    }

    /**
     * A helper document class for the limited range integer.
     * 
     * @author etuolem
     */
    public class LimitedRangeDocument extends PlainDocument {

	/**
	 * @param offs
	 * @param str
	 * @param a
	 * @throws BadLocationException
	 */
	public void insertString(int offs, String str, AttributeSet a)
		throws BadLocationException {

	    if (str == null) {
		return;
	    }

	    try {
		final StringBuffer sb = new StringBuffer(
			LimitedRangeIntegerField.this.getText());
		sb.insert(offs, str);

		// Exception if not valid integer
		int value = Integer.parseInt(sb.toString());

		if (value < minvalue || value > maxvalue) {
		    throw new Exception();
		}

		super.insertString(offs, str, a);

	    } catch (Exception e) {
		indicateError();
	    }

	}

	private void indicateError() {
	    LimitedRangeIntegerField.this.setBackground(ERROR_BG);
	    final Vilkutin v = new Vilkutin();
	    v.execute();
	}

    };

    /**
     * A helper document listener class for the limited range integer.
     * 
     * @author etuolem
     */
    public class RequiredWatch implements DocumentListener {

	/**
	 * Method for handling the document change update event.
	 * 
	 * @param e
	 *                the document event
	 */
	public void changedUpdate(DocumentEvent e) {
	    if (LimitedRangeIntegerField.this.getText().length() > 0) {
		LimitedRangeIntegerField.this.setBackground(neutralBg);
	    } else {
		LimitedRangeIntegerField.this.setBackground(ERROR_BG);
	    }
	}

	/**
	 * Method for handling the document insert update event.
	 * 
	 * @param e
	 *                the document event
	 */
	public void insertUpdate(DocumentEvent e) {
	    changedUpdate(e);
	}

	/**
	 * Method for handling the document remove update event.
	 * 
	 * @param e
	 *                the document event
	 */
	public void removeUpdate(DocumentEvent e) {
	    changedUpdate(e);
	}

    };

    /**
     * A helper class for the limited size integer. Will flash the background
     * color in case of a document error.
     * 
     * @author etuolem
     */
    public class Vilkutin extends SwingWorker<Void, Void> {

	/**
	 * Sleeping in the background
	 * 
	 * @return nothing
	 */
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
