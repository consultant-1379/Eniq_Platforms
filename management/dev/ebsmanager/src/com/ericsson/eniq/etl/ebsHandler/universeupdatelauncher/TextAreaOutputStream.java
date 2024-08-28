package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import java.awt.TextArea;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * An output stream that writes the received data into a text area.
 * 
 * @author epiituo
 * 
 */
public class TextAreaOutputStream extends OutputStream {

  private JTextArea textArea;

  private StringBuffer buffer;

  /**
   * Default constructor
   * 
   * @param textArea
   */
  public TextAreaOutputStream(JTextArea textArea) {
    this.textArea = textArea;
    this.buffer = new StringBuffer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.OutputStream#write(int)
   */
  public void write(int b) throws IOException {
    char character = (char) b;
    buffer.append(character);
    if (character == '\n') {
      flushBuffer();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.OutputStream#close()
   */
  public void close() throws IOException {
    this.textArea = null;
  }

  /**
   * Flushes the contents of the buffer to the text area.
   */
  private void flushBuffer() {
    String bufferContents = this.buffer.toString();
    this.buffer.setLength(0);
    this.textArea.append(bufferContents);
  }
}
