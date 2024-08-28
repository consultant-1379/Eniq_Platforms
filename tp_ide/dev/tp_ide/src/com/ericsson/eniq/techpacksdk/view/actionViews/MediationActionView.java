package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MediationActionView implements ActionView {

  public static final int FTP_PORT = 21;

  public static final int SFTP_PORT = 22;

  public static final String[] PROTOCOLS = { "ftp", "stfp" };

  public static final String[] METHODS = { "get", "put" };

  final private JPanel parent;

  final private JTextField host;

  final private JTextField port;

  final private JTextField username;

  final private JTextField password;

  final private JComboBox protocol;

  final private JComboBox method;

  final private JTextField localdir;

  final private JTextField remotedir;

  final private JTextField mask0;

  final private JTextField mask1;

  final private JTextField timestamp_mask1;

  final private JTextField lookback_days_mask1;

  final private JCheckBox removeRemoteFiles;

  final private JCheckBox useRecursiveFileListing;

  final private JCheckBox keepFileStructure;

  final private JCheckBox overwriteDownloadedFiles;

  final private JTextField lookbackHoursMask0;

  final private JLabel l_keepFileStructure;

  final private Properties orig;

  public MediationActionView(JPanel parent, Meta_transfer_actions action) {

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

    this.parent = parent;

    host = new JTextField(20);
    host.setText(orig.getProperty("hostName", ""));

    port = new JTextField(new NumericDocument(), orig.getProperty("portNumber", ""), 4);
    port.setText(orig.getProperty("portNumber", ""));

    username = new JTextField(10);
    username.setText(orig.getProperty("userName", ""));

    password = new JTextField(10);
    password.setText(orig.getProperty("password", ""));

    l_keepFileStructure = new JLabel("Maintain file structure");

    removeRemoteFiles = new JCheckBox();

    if (orig.getProperty("removeRemoteFiles", "true").equalsIgnoreCase("true")) {
      removeRemoteFiles.setSelected(true);
    } else {
      removeRemoteFiles.setSelected(false);
    }

    useRecursiveFileListing = new JCheckBox();

    if (orig.getProperty("useRecursiveFileListing", "false").equalsIgnoreCase("true")) {
      useRecursiveFileListing.setSelected(true);
    } else {
      useRecursiveFileListing.setSelected(false);
    }

    keepFileStructure = new JCheckBox();

    if (orig.getProperty("keepFileStructure", "true").equalsIgnoreCase("true")) {
      keepFileStructure.setSelected(true);
    } else {
      keepFileStructure.setSelected(false);
    }

    overwriteDownloadedFiles = new JCheckBox();

    if (orig.getProperty("overwriteDownloadedFiles", "true").equalsIgnoreCase("true")) {
      overwriteDownloadedFiles.setSelected(true);
    } else {
      overwriteDownloadedFiles.setSelected(false);
    }

    lookbackHoursMask0 = new JTextField(3);
    lookbackHoursMask0.setText(orig.getProperty("lookBackHours", ""));

    protocol = new JComboBox(PROTOCOLS);
    String origProtocol = orig.getProperty("protocol");
    if (origProtocol == null) {
      port.setText(String.valueOf(FTP_PORT));
    } else {
      for (int i = 0; i < PROTOCOLS.length; i++) {
        if (PROTOCOLS[i].equalsIgnoreCase(origProtocol)) {
          protocol.setSelectedIndex(i);
        }
      }
    }

    protocol.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {

        if (protocol.getSelectedIndex() == 0) {
          port.setText(String.valueOf(FTP_PORT));
        } else if (protocol.getSelectedIndex() == 1) {
          port.setText(String.valueOf(SFTP_PORT));
        }

        createLayout();
      }
    });

    method = new JComboBox(METHODS);
    String origMethod = orig.getProperty("method");
    if (origMethod != null) {
      for (int i = 0; i < METHODS.length; i++) {
        if (METHODS[i].equalsIgnoreCase(origMethod)) {
          method.setSelectedIndex(i);
        }
      }
    }
    method.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        createLayout();
      }
    });

    localdir = new JTextField(30);
    localdir.setText(orig.getProperty("localDir", ""));

    remotedir = new JTextField(30);
    remotedir.setText(orig.getProperty("remoteDir", ""));

    mask0 = new JTextField(20);
    mask0.setText(orig.getProperty("mask0", ""));

    mask1 = new JTextField(20);
    mask1.setText(orig.getProperty("mask1", ""));

    timestamp_mask1 = new JTextField(20);
    timestamp_mask1.setText(orig.getProperty("timestamp", ""));

    lookback_days_mask1 = new JTextField(3);
    lookback_days_mask1.setText(orig.getProperty("lookBackDays", ""));

    createLayout();

  }

  private void createLayout() {

    final Insets indentLevel1 = new Insets(2, 12, 2, 2);
    //final Insets indentLevel2 = new Insets(2, 24, 2, 2);
    final Insets normalInsets = new Insets(2, 2, 2, 2);

    parent.removeAll();

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;

    c.insets = normalInsets;
    c.weightx = 0;
    c.weighty = 0;

    final JLabel l_host = new JLabel("Remote host");
    l_host.setToolTipText("Hostname or ipaddress of remote host.");
    parent.add(l_host, c);

    c.weightx = 1;
    c.gridx = 1;
    host.setToolTipText("Hostname or ipaddress of remote host.");
    parent.add(host, c);

    c.gridy = 1;
    c.gridx = 0;
    final JLabel l_port = new JLabel("Port number");
    l_port.setToolTipText("Port number of remote host.");
    parent.add(l_port, c);

    c.weightx = 1;
    c.gridx = 1;
    port.setToolTipText("Port number of remote host.");
    parent.add(port, c);

    c.gridy = 2;
    c.gridx = 0;
    final JLabel l_username = new JLabel("Username");
    l_username.setToolTipText("Username of remote host.");
    parent.add(l_username, c);

    c.weightx = 1;
    c.gridx = 1;
    username.setToolTipText("Username of remote host.");
    parent.add(username, c);

    c.gridy = 3;
    c.gridx = 0;
    final JLabel l_password = new JLabel("Password");
    l_password.setToolTipText("Password of remote host.");
    parent.add(l_password, c);

    c.weightx = 1;
    c.gridx = 1;
    password.setToolTipText("Password of remote host.");
    parent.add(password, c);

    c.gridy = 4;
    c.gridx = 0;
    final JLabel l_protocol = new JLabel("Protocol");
    parent.add(l_protocol, c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(protocol, c);

    c.gridy = 5;
    c.gridx = 0;
    final JLabel l_method = new JLabel("Method");
    parent.add(l_method, c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(method, c);

    if (protocol.getSelectedIndex() == 0) { // ftp

      if (method.getSelectedIndex() == 0) { // get

        c.gridy = 6;
        c.gridx = 0;
        parent.add(new JLabel("Local dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(localdir, c);

        c.gridy = 7;
        c.gridx = 0;
        parent.add(new JLabel("Remote dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(remotedir, c);

        c.gridy = 8;
        c.gridx = 0;
        final JLabel l_mask0 = new JLabel("Mask 0");
        l_mask0
            .setToolTipText("Primary mask to select files from remote directory. If mask0 overflows mask1 is used with timestamp.");
        parent.add(l_mask0, c);

        c.weightx = 1;
        c.gridx = 1;
        mask0
            .setToolTipText("Primary mask to select files from remote directory. If mask0 overflows mask1 is used with timestamp.");
        parent.add(mask0, c);

        c.gridy = 9;
        c.gridx = 0;
        c.insets = indentLevel1;
        final JLabel l_lookbackHoursMask0 = new JLabel("Lookback hours");

        l_lookbackHoursMask0.setToolTipText("Number of lookback hours used with mask0.");
        parent.add(l_lookbackHoursMask0, c);

        c.weightx = 1;
        c.gridx = 1;
        c.insets = normalInsets;
        lookbackHoursMask0.setToolTipText("Number of lookback hours used with mask0.");
        parent.add(lookbackHoursMask0, c);

        c.gridy = 10;
        c.gridx = 0;
        final JLabel l_mask1 = new JLabel("Mask 1");
        l_mask1
            .setToolTipText("Secondary mask. Used when mask0 overflows. Mask need to contain timestamp. $ marks timestamp.");
        parent.add(l_mask1, c);

        c.weightx = 1;
        c.gridx = 1;
        mask1
            .setToolTipText("Secondary mask. Used when mask0 overflows. Mask need to contain timestamp. $ marks timestamp.");
        parent.add(mask1, c);

        c.gridy = 11;
        c.gridx = 0;
        c.insets = indentLevel1;
        final JLabel l_lookback_days_mask1 = new JLabel("Lookback days");
        l_lookback_days_mask1.setToolTipText("Amount of days used in backward day iteraton of mask1.");
        parent.add(l_lookback_days_mask1, c);

        c.weightx = 1;
        c.gridx = 1;
        c.insets = normalInsets;
        lookback_days_mask1.setToolTipText("Amount of days used in backward day iteraton of mask1.");
        parent.add(lookback_days_mask1, c);

        c.gridy = 12;
        c.gridx = 0;
        c.insets = indentLevel1;
        final JLabel l_timestamp_mask1 = new JLabel("Timestamp");
        l_timestamp_mask1.setToolTipText("Timestamp format used in iteration of mask1.");
        parent.add(l_timestamp_mask1, c);

        c.weightx = 1;
        c.gridx = 1;
        c.insets = normalInsets;
        timestamp_mask1.setToolTipText("Timestamp format used in iteration of mask1.");
        parent.add(timestamp_mask1, c);

        c.gridy = 13;
        c.gridx = 0;
        final JLabel l_removeRemoteFiles = new JLabel("Delete files from remote server after transfer");
        l_removeRemoteFiles.setToolTipText("Delete files from remote server after transferring them with FTP.");
        parent.add(l_removeRemoteFiles, c);

        c.weightx = 1;
        c.gridx = 1;
        removeRemoteFiles.setToolTipText("Delete files from remote server after transferring them with FTP.");
        parent.add(removeRemoteFiles, c);

        c.gridy = 14;
        c.gridx = 0;
        final JLabel l_useRecursiveFileListing = new JLabel("Use recursive file listing");
        l_useRecursiveFileListing
            .setToolTipText("Recursively iterate and get files in remote directory and all it's subdirectories.");
        parent.add(l_useRecursiveFileListing, c);

        c.weightx = 1;
        c.gridx = 1;
        useRecursiveFileListing
            .setToolTipText("Recursively iterate and get files in remote directory and all it's subdirectories.");

        useRecursiveFileListing.addActionListener(new ActionListener() {

          public void actionPerformed(final ActionEvent ae) {

            if (useRecursiveFileListing.isSelected() == true) {
              l_keepFileStructure.setEnabled(true);
              keepFileStructure.setEnabled(true);
            } else {
              l_keepFileStructure.setEnabled(false);
              keepFileStructure.setEnabled(false);
            }

          }
        });

        parent.add(useRecursiveFileListing, c);

        c.gridy = 15;
        c.gridx = 0;
        c.insets = indentLevel1;

        l_keepFileStructure
            .setToolTipText("If selected maintains file structure when downloading files from remote server. If not selected all files are downloaded to the local dir.");
        parent.add(l_keepFileStructure, c);

        c.weightx = 1;
        c.gridx = 1;
        c.insets = normalInsets;
        keepFileStructure
            .setToolTipText("If selected maintains file structure when downloading files from remote server. If not selected all files are downloaded to the local dir.");
        parent.add(keepFileStructure, c);

        c.gridy = 16;
        c.gridx = 0;
        c.insets = normalInsets;
        final JLabel l_overwriteDownloadedFiles = new JLabel("Overwrite downloaded files");
        l_overwriteDownloadedFiles
            .setToolTipText("If selected downloads and overwrites the files existing in the local dir. If not selected does not download and overwrite the existing files in local dir.");
        parent.add(l_overwriteDownloadedFiles, c);

        c.weightx = 1;
        c.gridx = 1;
        c.insets = normalInsets;
        overwriteDownloadedFiles
            .setToolTipText("If selected downloads and overwrites the files existing in the local dir. If not selected does not download and overwrite the existing files in local dir.");
        parent.add(overwriteDownloadedFiles, c);

        if (useRecursiveFileListing.isSelected() == true) {
          l_keepFileStructure.setEnabled(true);
          keepFileStructure.setEnabled(true);
        } else {
          l_keepFileStructure.setEnabled(false);
          keepFileStructure.setEnabled(false);
        }

      } else if (method.getSelectedIndex() == 1) { // put

        c.gridy = 6;
        c.gridx = 0;
        parent.add(new JLabel("Local dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(localdir, c);

        c.gridy = 7;
        c.gridx = 0;
        final JLabel l_mask0 = new JLabel("Mask");
        l_mask0.setToolTipText("Mask used selecting files from local directory.");
        parent.add(l_mask0, c);

        c.weightx = 1;
        c.gridx = 1;
        mask0.setToolTipText("Mask used selecting files from local directory.");
        parent.add(mask0, c);

        c.gridy = 8;
        c.gridx = 0;
        parent.add(new JLabel("Remote dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(remotedir, c);

      }

    } else if (protocol.getSelectedIndex() == 1) { // sftp

      if (method.getSelectedIndex() == 0) { // get

        c.gridy = 6;
        c.gridx = 0;
        parent.add(new JLabel("Local dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(localdir, c);

        c.gridy = 7;
        c.gridx = 0;
        parent.add(new JLabel("Remote dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(remotedir, c);

        c.gridy = 8;
        c.gridx = 0;
        final JLabel l_mask0 = new JLabel("Mask");
        l_mask0.setToolTipText("Mask used selecting files from local directory.");
        parent.add(l_mask0, c);

        c.weightx = 1;
        c.gridx = 1;
        mask0.setToolTipText("Mask used selecting files from remote directory.");
        parent.add(mask0, c);

      } else if (method.getSelectedIndex() == 1) { // put

        c.gridy = 6;
        c.gridx = 0;
        parent.add(new JLabel("Local dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(localdir, c);

        c.gridy = 7;
        c.gridx = 0;
        final JLabel l_mask0 = new JLabel("Mask");
        l_mask0.setToolTipText("Mask used selecting files from local directory.");
        parent.add(l_mask0, c);

        c.weightx = 1;
        c.gridx = 1;
        mask0.setToolTipText("Mask used selecting files from local directory.");
        parent.add(mask0, c);

        c.gridy = 8;
        c.gridx = 0;
        parent.add(new JLabel("Remote dir"), c);

        c.weightx = 1;
        c.gridx = 1;
        parent.add(remotedir, c);

      }

    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "Mediation";
  }

  public String validate() {
    String error = "";

    if (host.getText().length() <= 0) {
      error += "Parameter Remote host must be defined.\n";
    }

    if (port.getText().length() <= 0) {
      error += "Parameter Port number must be defined.\n";
    }

    if (username.getText().length() <= 0) {
      error += "Parameter Username must be defined.\n";
    }

    if (password.getText().length() <= 0) {
      error += "Parameter Password must be defined.\n";
    }

    if (localdir.getText().length() <= 0) {
      error += "Parameter Local dir must be defined.\n";
    }

    if (remotedir.getText().length() <= 0) {
      error += "Parameter Remote dir must be defined.\n";
    }

    if (mask0.getText().length() <= 0) {
      error += "Parameter Mask(0) must be defined.\n";
    }

    if (mask0.getText().length() > 0) {
      if (lookbackHoursMask0.getText().length() > 0) {
        try {
          Integer.parseInt(lookbackHoursMask0.getText());
        } catch (Exception e) {
          error += "Parameter Lookback hours is not a valid integer.\n";
        }
      }
    }

    if (mask1.getText().length() > 0) {

      if (mask1.getText().indexOf("$") < 0) {
        error += "Value of parameter Mask 1 must contain $ for timestamp.\n";
      }

      if (lookback_days_mask1.getText().length() <= 0) {
        error += "Parameter Lookback days must be defined when Mask 1 is defined.\n";
      } else {
        try {
          final int i = Integer.parseInt(lookback_days_mask1.getText());
          if (i < 0 || i > 100) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException nfe) {
          error += "Parameter Lookback days is not a valid integer.\n";
        }
      }

      if (timestamp_mask1.getText().length() <= 0) {
        error += "Parameter Timestamp must be defined when Mask 1 is defined.\n";
      } else {
        try {
          new SimpleDateFormat(timestamp_mask1.getText());
        } catch (IllegalArgumentException iae) {
          error += "Parameter Lookback days is not a valid timestamp.\n";
        }
      }

    }

    return error;
  }

  public String getContent() throws Exception {
    final Properties p = new Properties();

    p.setProperty("hostName", host.getText());
    p.setProperty("portNumber", port.getText());
    p.setProperty("userName", username.getText());
    p.setProperty("password", password.getText());
    p.setProperty("protocol", PROTOCOLS[protocol.getSelectedIndex()]);

    p.setProperty("method", METHODS[method.getSelectedIndex()]);

    if (protocol.getSelectedIndex() == 0) { // ftp

      if (method.getSelectedIndex() == 0) { // get

        p.setProperty("localDir", localdir.getText());
        p.setProperty("remoteDir", remotedir.getText());
        p.setProperty("mask0", mask0.getText());
        p.setProperty("mask1", mask1.getText());
        p.setProperty("lookBackDays", lookback_days_mask1.getText());
        p.setProperty("timestamp", timestamp_mask1.getText());
        p.setProperty("lookBackHours", lookbackHoursMask0.getText());

        if (removeRemoteFiles.isSelected()) {
          p.setProperty("removeRemoteFiles", "true");
        } else {
          p.setProperty("removeRemoteFiles", "false");
        }

        if (useRecursiveFileListing.isSelected()) {
          p.setProperty("useRecursiveFileListing", "true");
        } else {
          p.setProperty("useRecursiveFileListing", "false");
        }

        if (keepFileStructure.isSelected()) {
          p.setProperty("keepFileStructure", "true");
        } else {
          p.setProperty("keepFileStructure", "false");
        }

        if (overwriteDownloadedFiles.isSelected()) {
          p.setProperty("overwriteDownloadedFiles", "true");
        } else {
          p.setProperty("overwriteDownloadedFiles", "false");
        }

      } else if (method.getSelectedIndex() == 1) { // put

        p.setProperty("localDir", localdir.getText());
        p.setProperty("remoteDir", remotedir.getText());
        p.setProperty("mask0", mask0.getText());

      }

    } else if (protocol.getSelectedIndex() == 1) { // sftp

      if (method.getSelectedIndex() == 0) { // get

        p.setProperty("localDir", localdir.getText());
        p.setProperty("remoteDir", remotedir.getText());
        p.setProperty("mask0", mask0.getText());

      } else if (method.getSelectedIndex() == 1) { // put

        p.setProperty("localDir", localdir.getText());
        p.setProperty("remoteDir", remotedir.getText());
        p.setProperty("mask0", mask0.getText());

      }

    }

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
