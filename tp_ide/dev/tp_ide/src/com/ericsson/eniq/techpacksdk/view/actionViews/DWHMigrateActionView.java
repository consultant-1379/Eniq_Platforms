package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

public class DWHMigrateActionView implements ActionView {

  private JTextField url;

  private JTextField username;

  private JTextField password;

  private JTextField versionIDs;

  private GenericPropertiesView gpv;

  public DWHMigrateActionView(JPanel parent, Meta_transfer_actions action, JDialog pdialog) {

    Properties orig = new Properties();

    if (action != null) {

      String whr = action.getWhere_clause();

      if (whr != null && whr.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(whr.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    url = new JTextField(20);
    url.setText(orig.getProperty("sourceURL", ""));

    username = new JTextField(10);
    username.setText(orig.getProperty("sourceUsername", ""));

    password = new JTextField(10);
    password.setText(orig.getProperty("sourcePassword", ""));

    versionIDs = new JTextField(30);
    versionIDs.setText(orig.getProperty("allowedVersionIDs", ""));
    versionIDs.setToolTipText("Insert VERSIONIDs of migrated TPs. Separated with column if needed.");

    Set<String> s = new HashSet<String>();
    s.add("sourceUsername");
    s.add("sourcePassword");
    s.add("sourceURL");
    s.add("allowedVersionIDs");

    gpv = new GenericPropertiesView(orig, s, pdialog);

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_url = new JLabel("DBHostname:DBPortNumber");
    l_url.setToolTipText("Host information of migration source repository database.");
    parent.add(l_url, c);

    c.weightx = 1;
    c.gridx = 1;
    url.setToolTipText("Host information of migration source repository database.");
    parent.add(url, c);

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_username = new JLabel("DB Username");
    l_username.setToolTipText("Username of migration source repository database.");
    parent.add(l_username, c);

    c.weightx = 1;
    c.gridx = 1;
    username.setToolTipText("Username of migration source repository database.");
    parent.add(username, c);

    c.gridy = 3;
    c.gridx = 0;
    JLabel l_password = new JLabel("Password");
    l_password.setToolTipText("Password of migration source repository database.");
    parent.add(l_password, c);

    c.weightx = 1;
    c.gridx = 1;
    password.setToolTipText("Password of migration source repository database.");
    parent.add(password, c);

    c.gridy = 4;
    c.gridx = 0;
    JLabel l_versionIDs = new JLabel("Version IDs");
    l_versionIDs.setToolTipText("Version ID to be migrated. Multiple IDs delimited by comma.");
    parent.add(l_versionIDs, c);

    c.weightx = 1;
    c.gridx = 1;
    versionIDs.setToolTipText("Version ID to be migrated. Multiple IDs delimited by comma.");
    parent.add(versionIDs, c);

    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 2;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(gpv, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "DWHMigrate";
  }

  public String validate() {
    String error = "";

    if (url.getText().length() <= 0)
      error += "Parameter DBHostname:DBPortNumber must be defined.\n";

    if (username.getText().length() <= 0)
      error += "Parameter DB username must be defined.\n";

    if (password.getText().length() <= 0)
      error += "Parameter Password must be defined.\n";

    if (versionIDs.getText().length() <= 0)
      error += "Parameter Version IDs must be defined.\n";

    return error;
  }

  public String getContent() throws Exception {
    return "";
  }

  public String getWhere() throws Exception {
    Properties p = gpv.getProperties();

    p.setProperty("sourceURL", url.getText());
    p.setProperty("sourceUsername", username.getText());
    p.setProperty("sourcePassword", password.getText());
    p.setProperty("allowedVersionIDs", versionIDs.getText());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public boolean isChanged() {
    return true;
  }

}
