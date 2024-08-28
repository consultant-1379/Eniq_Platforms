package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class PartitionedLoadActionView implements ActionView {

  private JTextField tablename;

  private JTextField techpack;
  
  private JTextField taildir;

  private JTextField dateformat;

  private JTextArea content;

  public PartitionedLoadActionView(JPanel parent, Meta_transfer_actions action) {
    parent.removeAll();

    tablename = new JTextField(20);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_tablename = new JLabel("Destination type");
    l_tablename.setToolTipText("Target type of load.");
    parent.add(new JLabel("Destination type"), c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    tablename.setToolTipText("Target type of load.");
    parent.add(tablename, c);

    content = new JTextArea(20, 20);
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    taildir = new JTextField(20);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    JLabel l_taildir = new JLabel("Table level");
    l_taildir.setToolTipText("Table level of target type.");
    parent.add(l_taildir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    taildir.setToolTipText("Table level of target type.");
    parent.add(taildir, c);

    techpack = new JTextField(20);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 2;
    JLabel l_techpack = new JLabel("Techpack");
    l_techpack.setToolTipText("Techpack of target type.");
    parent.add(l_techpack, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    techpack.setToolTipText("Techpack of target type.");
    parent.add(techpack, c);

    dateformat = new JTextField(20);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 3;
    JLabel l_dateformat = new JLabel("Dateformat");
    l_dateformat.setToolTipText("Format of timestamp in the names of loaded files.");
    parent.add(l_dateformat, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    dateformat.setToolTipText("Format of timestamp in the names of loaded files.");
    parent.add(dateformat, c);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 4;
    JLabel l_content = new JLabel("Load Template");
    l_content.setToolTipText("SQL load clause template.");
    parent.add(l_content, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    content.setToolTipText("SQL load clause template.");
    parent.add(scrollPane, c);

    if (action != null) {
      Properties props = stringToProperty(action.getWhere_clause());
      tablename.setText(props.getProperty("tablename", ""));
      techpack.setText(props.getProperty("techpack", ""));
      taildir.setText(props.getProperty("taildir", ""));
      dateformat.setText(props.getProperty("dateformat", ""));

      content.setText(action.getAction_contents());
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "Partitioned Load";
  }
  
  public String validate() {
    String error = "";
    
    if (tablename.getText().trim().length() <= 0) {
      error += "Parameter Destination type must be defined.\n";
    }
    
    if (techpack.getText().trim().length() <= 0) {
      error += "Parameter Table level must be defined.\n";
    }
    
    if (taildir.getText().trim().length() <= 0) {
      error += "Parameter Techpack must be defined.\n";
    }
    
    if( dateformat.getText().trim().length() <= 0) {
      error += "Parameter Dateformat must be defined.\n";
    } else {
      try {
        new SimpleDateFormat(dateformat.getText().trim());
      } catch(IllegalArgumentException iae) {
        error += "Parameter Dateformat: Invalid timestamp.\n";
      }
    }
    
    if (content.getText().length() <= 0) {
      error += "Parameter Load template must be defined.\n";
    }
    
    return error;
  }

  public String getContent() throws Exception {
    return content.getText().trim();
  }

  public String getWhere() throws Exception {
    Properties p = getProperties();
    return propertyToString(p);
  }

  protected Properties getProperties() {
    Properties p = new Properties();
    p.put("tablename", tablename.getText().trim());
    p.put("techpack", techpack.getText().trim());
    p.put("taildir", taildir.getText().trim());
    p.put("dateformat", dateformat.getText().trim());
    return p;
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
