package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class UpdateMonitoredTypesActionView implements ActionView {

  private JTextField modifier;

  private JTextField lookback;

  public UpdateMonitoredTypesActionView(JPanel parent, Meta_transfer_actions action) {
    parent.removeAll();

    Properties orig = new Properties();

    if (action != null) {

      String where = action.getWhere_clause();

      if (where != null && where.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading action contents");
          e.printStackTrace();
        }
      }
    }

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    c.weightx = 0;
    c.weighty = 0;
    JLabel l_modifier = new JLabel("Start date modifier");
    l_modifier.setToolTipText("Defines how many days from today we start to look for new monitored types. Default: 0 (days)");
    parent.add(l_modifier, c);

    modifier = new JTextField(orig.getProperty("startDateModifier", ""), 20);
    modifier.setToolTipText("Defines how many days from today we start to look for new monitored types. Default: 0 (days)");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(modifier, c);

    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JLabel l_lookback = new JLabel("Lookback days");
    l_lookback.setToolTipText("how many days (from startdate) backwards we serach for new monitored types. Default: 7 (days)");
    parent.add(l_lookback, c);

    lookback = new JTextField(new NumericDocument(),orig.getProperty("lookbackDays", ""), 2);
    lookback.setToolTipText("how many days (from startdate) backwards we serach for new monitored types. Default: 7 (days)");
    c.weightx = 1;
    c.gridx = 1;
    parent.add(lookback, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "UpdateMonitoredTypes";
  }

  public String getContent() throws Exception {
    return "";
  }

  public String getWhere() throws Exception {
    Properties p = new Properties();

    p.setProperty("startDateModifier", modifier.getText().trim());
    p.setProperty("lookbackDays", lookback.getText().trim());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public boolean isChanged() {
    return true;
  }

  public String validate() {
    
    String error = "";
    
    String lb = lookback.getText();
    if (lb.length() > 0) {
      try {
        Integer.parseInt(lookback.getText());
      } catch (NumberFormatException nfe) {
        error +=  "Lookback days: Not a number\n";
      }
    }
    
    String md = modifier.getText();
    if (md.length() > 0) {
      try {
        Integer.parseInt(modifier.getText());
      } catch (NumberFormatException nfe) {
        error +=  "StartDateModifier: Not a number\n";
      }
    }

    return error;
  }
  
}
