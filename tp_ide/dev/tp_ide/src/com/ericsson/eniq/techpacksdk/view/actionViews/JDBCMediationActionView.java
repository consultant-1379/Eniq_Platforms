package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class JDBCMediationActionView implements ActionView {

  private JTextField drv;

  private JTextField url;

  private JTextField username;

  private JTextField password;

  private JTextField tempDir;

  private JTextField outDir;

  private JTextField filePrefix;

  private JTextField column_delimiter;

  private JComboBox writeHeader;

  private JTextArea select;

  private Properties orig;

  public JDBCMediationActionView(JPanel parent, Meta_transfer_actions action) {

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

    drv = new JTextField(30);
    drv.setText(orig.getProperty("jdbc_drv"));

    JLabel l_drv = new JLabel("JDBC Driverclass");
    l_drv
        .setToolTipText("JDBC driver class name. The jar containing the class must be available in ETLC server classpath.");
    parent.add(l_drv, c);

    c.weightx = 1;
    c.gridx = 1;
    drv
        .setToolTipText("JDBC driver class name. The jar containing the class must be available in ETLC server classpath.");
    parent.add(drv, c);

    url = new JTextField(20);
    url.setText(orig.getProperty("jdbc_url", ""));

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_url = new JLabel("JDBC URL");
    l_url.setToolTipText("JDBC URL of remote database.");
    parent.add(l_url, c);

    c.weightx = 1;
    c.gridx = 1;
    url.setToolTipText("JDBC URL of remote database.");
    parent.add(url, c);

    username = new JTextField(10);
    username.setText(orig.getProperty("db_user", ""));

    c.gridy = 2;
    c.gridx = 0;
    JLabel l_username = new JLabel("DB Username");
    l_username.setToolTipText("Username used in remote database.");
    parent.add(l_username, c);

    c.weightx = 1;
    c.gridx = 1;
    username.setToolTipText("Username used in remote database.");
    parent.add(username, c);

    password = new JTextField(10);
    password.setText(orig.getProperty("db_pwd", ""));

    c.gridy = 3;
    c.gridx = 0;
    JLabel l_password = new JLabel("DB Password");
    l_password.setToolTipText("Password used in remote database");
    parent.add(l_password, c);

    c.weightx = 1;
    c.gridx = 1;
    password.setToolTipText("Password used in remote database");
    parent.add(password, c);

    tempDir = new JTextField(20);
    tempDir.setText(orig.getProperty("tempDir", ""));

    c.gridy = 4;
    c.gridx = 0;
    JLabel l_tempDir = new JLabel("Temp Directory");
    l_tempDir
        .setToolTipText("The directory where incomplete output files are temporarily stored. When complete files are moved into Ouput Directory.");
    parent.add(l_tempDir, c);

    c.weightx = 1;
    c.gridx = 1;
    tempDir
        .setToolTipText("The directory where incomplete output files are temporarily stored. When complete files are moved into Ouput Directory.");
    parent.add(tempDir, c);

    outDir = new JTextField(20);
    outDir.setText(orig.getProperty("outdir", ""));

    c.gridy = 5;
    c.gridx = 0;
    JLabel l_outDir = new JLabel("Output Directory");
    l_outDir.setToolTipText("The directory where output files are stored.");
    parent.add(l_outDir, c);

    c.weightx = 1;
    c.gridx = 1;
    outDir.setToolTipText("The directory where output files are stored.");
    parent.add(outDir, c);

    filePrefix = new JTextField(15);
    filePrefix.setText(orig.getProperty("fileprefix", ""));

    c.gridy = 6;
    c.gridx = 0;
    JLabel l_filePrefix = new JLabel("Filename Prefix");
    l_filePrefix.setToolTipText("Name prefix used for output files.");
    parent.add(l_filePrefix, c);

    c.weightx = 1;
    c.gridx = 1;
    filePrefix.setToolTipText("Name prefix used for output files.");
    parent.add(filePrefix, c);

    column_delimiter = new JTextField(3);
    column_delimiter.setToolTipText("String used to limit columns. Default is tab-char (\\t)");
    column_delimiter.setText(orig.getProperty("column_delimiter"));

    c.gridy = 7;
    c.gridx = 0;
    c.weightx = 0;
    JLabel l_column_delimiter = new JLabel("Column delimiter");
    l_column_delimiter.setToolTipText("String used to limit columns. Default is tab-char (\\t)");
    parent.add(l_column_delimiter, c);

    c.gridx = 1;
    parent.add(column_delimiter, c);

    String[] opts = { "false", "true" };
    writeHeader = new JComboBox(opts);
    if ("true".equals(orig.getProperty("write_hdr")))
      writeHeader.setSelectedIndex(1);
    else
      writeHeader.setSelectedIndex(0);

    c.gridy = 8;
    c.gridx = 0;
    JLabel l_writeHeader = new JLabel("Write Header Row");
    l_writeHeader.setToolTipText("Write header containing column names at the start of the file.");
    parent.add(l_writeHeader, c);

    c.weightx = 1;
    c.gridx = 1;
    writeHeader.setToolTipText("Write header containing column names at the start of the file.");
    parent.add(writeHeader, c);

    select = new JTextArea(10, 30);
    select.setLineWrap(true);
    select.setWrapStyleWord(true);
    if (action != null)
      select.setText(action.getWhere_clause());

    c.gridy = 9;
    c.gridx = 0;
    JLabel l_select = new JLabel("Select Clause");
    l_select.setToolTipText("SQL select clause used to extract data from remote database.");
    parent.add(l_select, c);

    c.weightx = 1;
    c.gridx = 1;
    select.setToolTipText("SQL select clause used to extract data from remote database.");
    parent.add(select, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "JDBC Mediation";
  }

  public String validate() {
    String error = "";

    if (drv.getText().length() <= 0)
      error += "Parameter JDBC driverclass must be defined\n";

    if (url.getText().length() <= 0)
      error += "Parameter JDBC URL must be defined\n";

    if (username.getText().length() <= 0)
      error += "Parameter DB username must be defined\n";

    if (password.getText().length() <= 0)
      error += "Parameter DB password must be defined\n";

    if (tempDir.getText().length() <= 0)
      error += "Parameter Temp directory must be defined\n";

    if (outDir.getText().length() <= 0)
      error += "Parameter Output directory must be defined\n";

    if (filePrefix.getText().length() <= 0)
      error += "Parameter Filename prefix must be defined\n";

    if (select.getText().length() <= 0)
      error += "Parameter Select clause must be defined\n";

    return error;
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("jdbc_drv", drv.getText());
    p.setProperty("jdbc_url", url.getText());
    p.setProperty("db_user", username.getText());
    p.setProperty("db_pwd", password.getText());
    p.setProperty("tempDir", tempDir.getText());
    p.setProperty("outdir", outDir.getText());
    p.setProperty("column_delimiter", column_delimiter.getText());
    if (writeHeader.getSelectedIndex() == 1)
      p.setProperty("write_hdr", "true");
    else
      p.setProperty("write_hdr", "false");
    p.setProperty("fileprefix", filePrefix.getText());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  }

  public String getWhere() throws Exception {
    return select.getText();
  }

  public boolean isChanged() {
    return true;
  }

}
