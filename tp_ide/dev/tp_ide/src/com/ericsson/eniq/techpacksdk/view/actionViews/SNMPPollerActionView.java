package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.NumericDocument;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class SNMPPollerActionView implements ActionView {

  private JTextField host;

  private JTextField port;

  private JTextField version;

  private JTextField community;

  private JTextField template;

  private Meta_transfer_actions action;

  private Properties orig;

  public SNMPPollerActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;

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

    host = new JTextField(20);
    host.setText(orig.getProperty("host", ""));

    JLabel l_host = new JLabel("Hostname");
    l_host.setToolTipText("Hostname or ipaddress of remote snmp daemon.");
    parent.add(l_host, c);

    c.weightx = 1;
    c.gridx = 1;
    host.setToolTipText("Hostname or ipaddress of remote snmp daemon.");
    parent.add(host, c);

    port = new JTextField(new NumericDocument(), orig.getProperty("port", ""), 6);

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_port = new JLabel("Port number");
    l_port.setToolTipText("Port number of remote snmp daemon.");
    parent.add(l_port, c);

    c.weightx = 1;
    c.gridx = 1;
    port.setToolTipText("Port number of remote snmp daemon.");
    parent.add(port, c);

    version = new JTextField(3);
    version.setText(orig.getProperty("version", ""));

    c.gridy = 2;
    c.gridx = 0;
    JLabel l_version = new JLabel("SNMP Version");
    l_version.setToolTipText("SNMP protocol version used in communications.");
    parent.add(l_version, c);

    c.weightx = 1;
    c.gridx = 1;
    version.setToolTipText("SNMP protocol version used in communications.");
    parent.add(version, c);

    community = new JTextField(10);
    community.setText(orig.getProperty("community", ""));

    c.gridy = 3;
    c.gridx = 0;
    JLabel l_community = new JLabel("Community String");
    l_community.setToolTipText("Community string used in authentication to remote SNMP daemon.");
    parent.add(l_community, c);

    c.weightx = 1;
    c.gridx = 1;
    community.setToolTipText("Community string used in authentication to remote SNMP daemon.");
    parent.add(community, c);

    template = new JTextField(20);
    template.setText(orig.getProperty("template", ""));

    c.gridy = 4;
    c.gridx = 0;
    JLabel l_template = new JLabel("Template File");
    l_template.setToolTipText("Template file for creating output files.");
    parent.add(l_template, c);

    c.weightx = 1;
    c.gridx = 1;
    template.setToolTipText("Template file for creating output files.");
    parent.add(template, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "SNMP Poller";
  }

  public String validate() {
    String error = "";

    if (host.getText().length() <= 0)
      error += "Parameter Hostname must be defined\n";

    if (port.getText().length() <= 0) {
      error += "Parameter Port number must be defined\n";
    } else {
      try {
        int i = Integer.parseInt("port");
        if (i <= 0 || i > 65536)
          throw new NumberFormatException();
      } catch (NumberFormatException nfe) {
        error = "Parameter Port number: Not a valid port number\n";
      }
    }

    if (version.getText().length() <= 0)
      error += "Parameter SNMP version must be defined\n";

    if (community.getText().length() <= 0)
      error += "Parameter Community string must be defined\n";

    if (template.getText().length() <= 0)
      error += "Parameter Template file must be defined\n";

    return error;
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("host", host.getText());
    p.setProperty("port", port.getText());
    p.setProperty("version", version.getText());
    p.setProperty("community", community.getText());
    p.setProperty("template", template.getText());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();

  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    if (action == null)
      return true;
    else {
      if (!orig.getProperty("host", "").equals(host.getText()))
        return true;
      if (!orig.getProperty("port", "").equals(port.getText()))
        return true;
      if (!orig.getProperty("version", "").equals(version.getText()))
        return true;
      if (!orig.getProperty("community", "").equals(community.getText()))
        return true;
      if (!orig.getProperty("template", "").equals(template.getText()))
        return true;

      return false;
    }
  }

}
