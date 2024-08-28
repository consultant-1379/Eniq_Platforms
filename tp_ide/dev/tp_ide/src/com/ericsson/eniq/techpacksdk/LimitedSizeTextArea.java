package com.ericsson.eniq.techpacksdk;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.jdesktop.swingworker.SwingWorker;

@SuppressWarnings("serial")
public class LimitedSizeTextArea extends JTextArea {

  public static final Color ERROR_BG = Color.PINK;

  private int limit = -1;

  private final Color neutralBg;

  public LimitedSizeTextArea(final int limit, final boolean required) {
    this(limit, required, 0, 0);
  }

  public LimitedSizeTextArea(final int limit, final boolean required, final int rows, final int cols) {
    super(rows, cols);

    this.limit = limit;
    this.neutralBg = this.getBackground();

    if (required) {
      getDocument().addDocumentListener(new RequiredWatch());
      if (getText().length() <= 0)
        setBackground(ERROR_BG);

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

      if (LimitedSizeTextArea.this.getText().length() >= limit) {
        indicateError();
        return;
      } else if (LimitedSizeTextArea.this.getText().length() + str.length() > limit) {
        indicateError();
        super.insertString(offs, str.substring(0, limit - LimitedSizeTextArea.this.getText().length()), a);
      } else {
        super.insertString(offs, str, a);
      }

    }

    private void indicateError() {
      LimitedSizeTextArea.this.setBackground(ERROR_BG);
      final Vilkutin v = new Vilkutin();
      v.execute();
    }

  };

  public class RequiredWatch implements DocumentListener {

    public void changedUpdate(DocumentEvent e) {
      if (LimitedSizeTextArea.this.getText().length() > 0) {
        LimitedSizeTextArea.this.setBackground(neutralBg);
      } else {
        LimitedSizeTextArea.this.setBackground(ERROR_BG);
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
      LimitedSizeTextArea.this.setBackground(neutralBg);
    }

  };

}
