package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class UpdateMonitoringActionView implements ActionView {

  private JTextField threshold; 
  private JTextField completeThreshold;
  private JTextField lookback;
  private JTextField flaglookback;
  private JComboBox ignoreflag;
  private JLabel l_wo = new JLabel("Flag Ignore Lookback");
  
  
  private GenericPropertiesView gpv;

  public UpdateMonitoringActionView(JPanel parent, Meta_transfer_actions action, JDialog pdialog) {

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    Properties orig = new Properties();

    if (action != null) {

      String whr = action.getWhere_clause();

      if (whr != null && whr.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(whr.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading action where");
          e.printStackTrace();
        }
      }
    }

    
    //***********
    
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    
    threshold = new JTextField(new NumericDocument(),orig.getProperty("threshold", ""), 5);
    threshold.setToolTipText("Time, in hours, that is waited before aggregations may start if data in incomplete. Default: 1 (hours)");
    
    JLabel th_l = new JLabel("Aggregation threshold");
    th_l.setToolTipText("Time, in hours, that is waited before aggregations may start if data is incomplete. Default: 1 (hours)");
    parent.add(th_l, c);

    c.gridx = 1;
    parent.add(threshold, c);
    
    
    //***********

    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 1;  
    
    completeThreshold = new JTextField(new NumericDocument(),orig.getProperty("completeThreshold", ""), 5);
    completeThreshold.setToolTipText("Time, in hours, that is waited before aggregations may start if data in complete. Default: 1 (hours)");
   
    JLabel clb_l = new JLabel("Complete aggregation threshold");
    clb_l.setToolTipText("Time, in hours, that is waited before aggregations may start if data is complete. Default: 1 (hours)");
    parent.add(clb_l, c);
      
    c.gridx = 1;
    parent.add(completeThreshold, c);
    
    //*********** 

    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;

    lookback = new JTextField(new NumericDocument(),orig.getProperty("lookback",""), 5);
    lookback.setToolTipText("Time, in days, that the monitoring is performed into history from current day. Default: 7 (days)");
           
    JLabel lb_l = new JLabel("Monitoring lookback time");
    lb_l.setToolTipText("Time, in days, that the monitoring is performed into history from current day. Default: 7 (days)");
    parent.add(lb_l, c);
      
    c.gridx = 1;
    parent.add(lookback, c);
    
    //*********** 
    
    c.gridx = 0;
    c.gridy = 4;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    JLabel l_if = new JLabel("Ignore handled flag");
    l_if
        .setToolTipText("Are the handeld flags ignored.");
    parent.add(l_if, c);

    
    String[] method = { "false", "true" };
    ignoreflag = new JComboBox(method);
    ignoreflag
        .setToolTipText("Are the handeld flags ignored.");


    ignoreflag.setSelectedIndex(0);
    
    if ("false".equalsIgnoreCase(orig.getProperty("ignoreflag", "")))
      ignoreflag.setSelectedIndex(0);
    
    if ("true".equalsIgnoreCase(orig.getProperty("ignoreflag", "")))
      ignoreflag.setSelectedIndex(1);
    
    ignoreflag.addActionListener(new ActionListener() {

    public void actionPerformed(ActionEvent ae) {
      l_wo.setEnabled(ignoreflag.getSelectedIndex() == 1);
      flaglookback.setEnabled(ignoreflag.getSelectedIndex() == 1);
    }
  });
  
    c.gridx = 1;
    parent.add(ignoreflag, c);
    
    //****************************

    c.gridx = 0;
    c.gridy = 5;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    l_wo.setEnabled(ignoreflag.getSelectedIndex() == 1);
    l_wo.setToolTipText("How many days backwards we ignore the handled flags. Default: 1");
    parent.add(l_wo, c);

    flaglookback = new JTextField(new NumericDocument(), orig.getProperty("flaglookback", ""), 5);
    flaglookback.setToolTipText("How many days backwards we ignore the handled flags. Default: 1");
    flaglookback.setEnabled(ignoreflag.getSelectedIndex() == 1);
    c.gridx = 1;
    parent.add(flaglookback, c);

    //******************************
    
    

    c.fill = GridBagConstraints.NONE;

    
    
    
    Set s = new HashSet();
    s.add("threshold");
    s.add("completeThreshold");   
    s.add("lookback");
    s.add("flaglookback");
    s.add("ignoreflag");

    gpv = new GenericPropertiesView(orig, s, pdialog);

    c.gridx = 0;
    c.gridy = 12;
    c.gridwidth = 2;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    
    parent.add(gpv,c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "UpdateMonitoring";
  }

  public String getContent() throws Exception {
    return "";
  }

  public String getWhere() throws Exception {
    Properties p = gpv.getProperties();
    
    if (threshold.getText().trim().length() > 0)
      p.setProperty("threshold", threshold.getText().trim());
    
    if(lookback.getText().trim().length() > 0)
      p.setProperty("lookback", lookback.getText().trim());

    if(completeThreshold.getText().trim().length() > 0)
      p.setProperty("completeThreshold", completeThreshold.getText().trim());
    
    if(flaglookback.getText().trim().length() > 0)
      p.setProperty("flaglookback", flaglookback.getText().trim());
    
   
    p.setProperty("ignoreflag", (String)ignoreflag.getSelectedItem());
    
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
        error += "Aggregation threshold: Not a number\n";
      }
    }
    
    String th = threshold.getText();
    if (th.length() > 0) {
      try {
        Integer.parseInt(threshold.getText());
      } catch (NumberFormatException nfe) {
        error += "Monitoring lookback time: Not a number\n";
      }
    }

    return error;
  }

  
}
