package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;

/**
 * Copyright Distocraft 2006<br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class SMTPMediationActionView implements ActionView {

  private JTextField to;

  private JTextField from;

  private JTextField host;

  private JTextField filepath;

  private JTextField subject;

  private JComboBox sendFilesAsAttachments;

  private JTextField numberOfFilesPerEmail;

  // private Meta_transfer_actions action;

  private Properties orig;

  public SMTPMediationActionView(JPanel parent, Meta_transfer_actions action) {
    // this.action = action;

    orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    to = new JTextField(20);
    to.setToolTipText("Email address where the mediated items are sent to.");
    to.setText(orig.getProperty("to", ""));

    parent.add(new JLabel("Email address"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(to, c);

    from = new JTextField(20);
    from.setToolTipText("Email address where the mediated emails are sent from.");
    from.setText(orig.getProperty("from", ""));

    c.gridy = 1;
    c.gridx = 0;
    parent.add(new JLabel("Sender email address"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(from, c);

    host = new JTextField(20);
    host.setToolTipText("Outgoing STMP mail server which sends the emails forward.");
    host.setText(orig.getProperty("mail.smtp.host", ""));

    c.gridy = 2;
    c.gridx = 0;
    parent.add(new JLabel("Outgoing mail server (SMTP)"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(host, c);

    filepath = new JTextField(30);
    filepath.setToolTipText("Filepath to attachments sent by email.");
    filepath.setText(orig.getProperty("filepath", ""));

    c.gridy = 3;
    c.gridx = 0;
    parent.add(new JLabel("Filepath to attachments"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(filepath, c);

    subject = new JTextField(20);
    subject.setToolTipText("Subject of email messages.");
    subject.setText(orig.getProperty("subject", ""));

    c.gridy = 4;
    c.gridx = 0;
    parent.add(new JLabel("Email subject"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(subject, c);

    String[] opts = { "false", "true" };
    sendFilesAsAttachments = new JComboBox(opts);
    sendFilesAsAttachments
        .setToolTipText("Selection if the mediated items are sent as email attachments or in the email's content.");
    if ("true".equals(orig.getProperty("sendFilesAsAttachments")))
      sendFilesAsAttachments.setSelectedIndex(1);
    else
      sendFilesAsAttachments.setSelectedIndex(0);

    c.gridy = 7;
    c.gridx = 0;
    parent.add(new JLabel("Send files as email attachments"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(sendFilesAsAttachments, c);

    numberOfFilesPerEmail = new JTextField(new NumericDocument(), orig.getProperty("numberOfFilesPerEmail", ""), 5);
    numberOfFilesPerEmail.setToolTipText("Informs how many files are sent in a one email.");

    c.gridy = 8;
    c.gridx = 0;
    parent.add(new JLabel("Number of files sent in an email"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(numberOfFilesPerEmail, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "SMTP Mediation";
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("to", to.getText());
    p.setProperty("from", from.getText());
    p.setProperty("mail.smtp.host", host.getText());
    p.setProperty("filepath", filepath.getText());
    p.setProperty("subject", subject.getText());

    if (sendFilesAsAttachments.getSelectedIndex() == 1)
      p.setProperty("sendFilesAsAttachments", "true");
    else
      p.setProperty("sendFilesAsAttachments", "false");

    p.setProperty("numberOfFilesPerEmail", numberOfFilesPerEmail.getText());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

  /**
   * This function validates this actionview's parameters.
   * 
   * @return Returns the error strings separeted by linechange (\n). In case of
   *         no errors, empty string is returned.
   */
  public String validate() {
    if (to.getText().trim().length() <= 0)
      return "Parameter Email address must be defined\n";
    else if (from.getText().trim().length() <= 0)
      return "Parameter Sender email address must be defined\n";
    else if (host.getText().trim().length() <= 0)
      return "Parameter Outgoing mail server must be defined\n";
    else if (filepath.getText().trim().length() <= 0)
      return "Parameter Filepath to attachments must be defined\n";
    else if (subject.getText().trim().length() <= 0)
      return "Parameter Email subject must be defined\n";
    else if (numberOfFilesPerEmail.getText().trim().length() <= 0)
      return "Parameter Number of files sent in an email must be defined\n";
    else if (numberOfFilesPerEmail.getText().trim().length() > 0) {
      // Check for a valid positive integer.
      String numberOfFilesPerEmailValue = numberOfFilesPerEmail.getText().trim();
      try {
        Integer numberOfFilesPerEmailInteger = new Integer(Integer.parseInt(numberOfFilesPerEmailValue));
        if (numberOfFilesPerEmailInteger.intValue() <= 0) {
          return "Parameter Number of files sent in an email must be positive and larger than zero.\n";
        }
      } catch (NumberFormatException e) {
        return "Parameter Number of files sent in an email must be numeric\n";
      }
    }

    return "";
  }
}
