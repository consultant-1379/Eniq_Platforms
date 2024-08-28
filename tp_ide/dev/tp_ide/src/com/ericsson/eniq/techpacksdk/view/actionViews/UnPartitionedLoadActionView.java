package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class UnPartitionedLoadActionView implements ActionView {

  private JTextField tablename;

  private JTextField dir;

  private JTextField pattern;
  
  private JTextField techpack;

  private JTextArea content;

  private Meta_transfer_actions action;

  public UnPartitionedLoadActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;

    parent.removeAll();

    tablename = new JTextField(20);
    tablename.setToolTipText("Name of the table where data is loaded");

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_tablename = new JLabel("Table name");
    l_tablename.setToolTipText("Name of the table where data is loaded");
    parent.add(l_tablename, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(tablename, c);

    dir = new JTextField(20);
    dir.setToolTipText("Input directory for loader.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_dir = new JLabel("Directory");
    l_dir.setToolTipText("Input directory for loader.");
    parent.add(l_dir, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(dir, c);
  
    techpack = new JTextField(20);
    techpack.setToolTipText("Defines the techpack.");
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 2;

    JLabel l_techpack = new JLabel("Techpack");
    l_techpack.setToolTipText("Defines the techpack.");
    parent.add(l_techpack, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(techpack, c);
    
    pattern = new JTextField(20);
    pattern.setToolTipText("Regular expression pattern for input files.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    c.weighty = 0;

    JLabel l_pattern = new JLabel("Pattern");
    l_pattern.setToolTipText("Regular expression pattern for input files.");
    parent.add(l_pattern, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(pattern, c);

    content = new JTextArea(20, 20);
    content.setToolTipText("Template that is used to create TableLoad SQL-clause.");
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 4;

    JLabel l_content = new JLabel("Load Template");
    l_content.setToolTipText("Template that is used to create TableLoad SQL-clause.");
    parent.add(l_content, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(scrollPane, c);

    if (action != null) {
      Properties props = stringToProperty(action.getWhere_clause());
      tablename.setText(props.getProperty("tablename", ""));
      dir.setText(props.getProperty("dir", ""));
      pattern.setText(props.getProperty("pattern", ""));
      techpack.setText(props.getProperty("techpack", ""));

      content.setText(action.getAction_contents());
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "UnPartitioned Load";
  }

  public String getContent() throws Exception {

    return content.getText().trim();
  }

  public String getWhere() throws Exception {
    Properties p = new Properties();
    p.put("tablename", tablename.getText().trim());
    p.put("techpack", techpack.getText().trim());
    p.put("dir", dir.getText().trim());
    if (pattern.getText().trim().length() > 0)
      p.put("pattern", pattern.getText().trim());
    return propertyToString(p);
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

  public String validate() {
    
    try {
      
      Pattern.compile(pattern.getText());
    } catch(PatternSyntaxException  pse) {
      return "Pattern: Not a valid regExp pattern\n";
    }
    return "";
  }

  
}
