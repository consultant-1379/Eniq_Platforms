package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class ManualReAggregationActionView implements ActionView {

  private JTextField typename;
  private JTextField datetime;

  private Meta_transfer_actions action;

  private Properties orig;

  public ManualReAggregationActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;

    orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
        } catch (Exception e) {
          System.out.println("Error loading action contents");
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

    typename = new JTextField(orig.getProperty("host", ""),20);
    typename.setToolTipText("Name of type to be reaggregated");
    
    JLabel l_typename = new JLabel("Typename");
    l_typename.setToolTipText("Name of type to be reaggregated");
    parent.add(l_typename, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(typename, c);

    datetime = new JTextField(orig.getProperty("datetime", ""),20);
    datetime.setToolTipText("Time of reaggregation");
    
    c.gridy = 1;
    c.gridx = 0;
    JLabel l_datetime = new JLabel("Datetime");
    parent.add(l_datetime, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(datetime, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "ManualReAggregation";
  }

  public String validate() {
    String error = "";
    
    if(typename.getText().length() <= 0)
      error += "Parameter Typename must be defined.\n";
    
    if(datetime.getText().length() <= 0)
      error += "Parameter Datetime must be defined.\n";
    
    return error;
  }
  
  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("typename", typename.getText());
    p.setProperty("datetime", datetime.getText());

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
      if (!orig.getProperty("datetime", "").equals(datetime.getText()))
        return true;
      if (!orig.getProperty("typename", "").equals(typename.getText()))
        return true;

      return false;
    }
  }

}
