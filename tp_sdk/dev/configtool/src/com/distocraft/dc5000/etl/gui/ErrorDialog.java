package com.distocraft.dc5000.etl.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class ErrorDialog extends JDialog {

  private JFrame frame;

  public ErrorDialog(JFrame frame, String title, String message) {
    this(frame, title, message, null);
  }

  public ErrorDialog(JFrame frame, String title, String message, Throwable e) {
    super(frame, true);

    this.frame = frame;

    this.setTitle(title);

    Container con = this.getContentPane();

    con.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.insets = new Insets(2, 2, 2, 2);

    JLabel msg = new JLabel(message, ConfigTool.noWay, SwingConstants.LEFT);
    con.add(msg, c);

    c.gridy = 1;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    if (e != null) { // show exception
      c.gridy = 2;
      JTextArea exc = new JTextArea(stackTraceToString(e));
      JScrollPane jsp = new JScrollPane(exc, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      con.add(jsp, c);
    }

    c.gridy = 3;
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.NONE;
    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        ErrorDialog.this.setVisible(false);
      }
    });
    con.add(ok, c);

    this.pack();
    this.setVisible(true);

  }

  public static String stackTraceToString(Throwable t) {

    StringBuffer sb = new StringBuffer();

    while (t != null) {
      String msg = t.getMessage();
      if (msg != null)
        sb.append(msg).append("\n");

      sb.append("Caused by: ");
      sb.append(t.getClass().getName());
      sb.append("\n");
      StackTraceElement[] stea = t.getStackTrace();
      for (int i = 0; i < stea.length; i++) {
        sb.append("   ");
        sb.append(stea[i].getClassName()).append(".");
        sb.append(stea[i].getMethodName()).append(":");
        sb.append(stea[i].getLineNumber()).append("\n");
      }

      t = t.getCause();

    }

    return sb.toString();
  }

}
