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
import com.ericsson.eniq.component.NumericDocument;

/**
 * @author melantie Copyright Distocraft 2005
 * 
 * $id$
 */

public class CreateDirActionView implements ActionView {

  private String type;

  private Meta_transfer_actions action;

  private JTextField where;

  private JTextField permission;

  private JTextField owner;

  private JTextField group;

  public CreateDirActionView(JPanel parent, Meta_transfer_actions action) {

    parent.removeAll();

    where = new JTextField(20);
    where.setToolTipText("Path of created directory.");

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_where = new JLabel("Directory");
    l_where.setToolTipText("Path of created directory.");
    parent.add(l_where, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(where, c);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;

    permission = new JTextField(new NumericDocument(),"",4);
    permission.setToolTipText("Acces rights of created directory. Format unix-type octal definition.");
    
    JLabel l_permission = new JLabel("Permission");
    l_permission.setToolTipText("Acces rights of created directory. Format unix-type octal definition.");
    parent.add(l_permission, c);

    // c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(permission, c);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 2;

    group = new JTextField(20);
    group.setToolTipText("Group of created directory.");
    
    JLabel l_group = new JLabel("Group");
    l_group.setToolTipText("Group of created directory.");
    parent.add(l_group, c);

    // c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(group, c);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 3;

    owner = new JTextField(20);
    owner.setToolTipText("Owner of created directory.");
    
    JLabel l_owner = new JLabel("Owner");
    l_owner.setToolTipText("Owner of created directory.");
    parent.add(l_owner, c);

    // c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(owner, c);

    if (action != null) {

      where.setText(action.getWhere_clause());
      permission.setText(stringToProperty(action.getAction_contents()).getProperty("permission", ""));
      group.setText(stringToProperty(action.getAction_contents()).getProperty("group", ""));
      owner.setText(stringToProperty(action.getAction_contents()).getProperty("owner", ""));

    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return type;
  }

  public String validate() {
    String error = "";
    
    if(where.getText().length() <= 0)
      error += "Parameter Directory must be defined\n";
    
    if(permission.getText().length() > 0) {
      try {
        Integer.parseInt(permission.getText());
      } catch(NumberFormatException nfe) {
        error += "Parameter Permission is not valid\n";
      }
    }
    
    return error;
  }

  public String getContent() throws Exception {

    Properties p = new Properties();
    p.put("permission", permission.getText().trim());
    p.put("group", group.getText().trim());
    p.put("owner", owner.getText().trim());
    return propertyToString(p);

  }

  public String getWhere() throws Exception {
    return where.getText().trim();
  }

  public boolean isChanged() {
    return true;
  }

  protected Properties stringToProperty(String str) {

    Properties prop = new Properties();

    try {

      if (str != null && str.length() > 0) {

        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
        prop.load(bais);
        bais.close();
      }

    } catch (Exception e) {

    }

    return prop;

  }

  protected String propertyToString(Properties prop) {

    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      prop.store(baos, "");

      return baos.toString();

    } catch (Exception e) {

    }

    return "";

  }

}
