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

/**
 * This is a view to AlarmMarkupAction. Copyright (c) 1999 - 2007 AB LM Ericsson
 * Oy All rights reserved.
 * 
 * @author ejannbe
 * 
 */
public class AlarmMarkupActionView implements ActionView {

  private JTextField nLatestDays;

  private JTextField beginStatuses;

  // private Meta_transfer_actions action;

  private Properties orig;

  public AlarmMarkupActionView(JPanel parent, Meta_transfer_actions action) {
    // this.action = action;

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

    JLabel nLatestDaysLabel = new JLabel("nLatestDays");
    nLatestDaysLabel.setToolTipText("Informs how many days older data is excluded from alarm execution.");
    parent.add(nLatestDaysLabel, c);

    c.weightx = 1;
    c.gridx = 1;
    nLatestDays = new JTextField(20);
    nLatestDays.setToolTipText("Informs how many days older data is excluded from alarm execution.");
    nLatestDays.setText(orig.getProperty("nLatestDays", ""));
    parent.add(nLatestDays, c);

    c.gridy = 1;
    c.gridx = 0;
    JLabel beginStatusesLabel = new JLabel("Begin statuses");
    beginStatusesLabel.setToolTipText("Comma separated list of rowstatuses where the alarm execution is started.");
    parent.add(beginStatusesLabel, c);
    c.weightx = 1;
    c.gridx = 1;
    beginStatuses = new JTextField(20);
    beginStatuses.setToolTipText("Comma separated list of rowstatuses where the alarm execution is started.");
    beginStatuses.setText(orig.getProperty("beginStatuses", ""));
    parent.add(beginStatuses, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "AlarmMarkup";
  }

  public String validate() {
    String error = "";

    if (nLatestDays.getText().length() <= 0) {
      error += "Parameter nLatestDays must be defined\n";
    }

    try {
      int i = Integer.parseInt(nLatestDays.getText());
    } catch (NumberFormatException e) {
      error += "Parameter nLatestDays must be numeric\n";
    }

    if (beginStatuses.getText().length() <= 0) {
      error += "Parameter Begin Statuses must be defined\n";
    }

    return error;
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("nLatestDays", nLatestDays.getText());
    p.setProperty("beginStatuses", beginStatuses.getText());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
