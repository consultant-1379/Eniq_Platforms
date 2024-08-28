package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AlarmParseView implements ParseView {

  private final JPanel parent;

  private final JTextField dateFormat;

  private final JTextField tag_id;

  private final JTextField alarmTemplate;

  private final  JTextField periodDuration;

  private final JTextField timelevel;

  public AlarmParseView(Properties p, JPanel par) {

    this.parent = par;

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    // TagID

    tag_id = new JTextField(p.getProperty("tag_id", "alarm"), 30);
    tag_id.setToolTipText("TagID used for alarm measurement type.");

    c.gridx = 0;
    JLabel l_tag_id = new JLabel("Tag ID");
    l_tag_id.setToolTipText("TagID used for alarm measurement type.");
    parent.add(l_tag_id, c);

    c.gridx = 1;
    parent.add(tag_id, c);

    // Dateformat

    dateFormat = new JTextField(p.getProperty("dateformat", "yyyy-MM-dd HH:mm:ss"), 30);
    dateFormat.setToolTipText("Format of dates in alarm HTML report.");
    c.gridy = 1;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel l_dateFormat = new JLabel("Dateformat");
    l_dateFormat.setToolTipText("Format of dates in alarm HTML report.");
    parent.add(l_dateFormat, c);

    c.gridx = 1;
    parent.add(dateFormat, c);

    // Alarm Template

    alarmTemplate = new JTextField(p.getProperty("AlarmTemplate", ""), 30);
    alarmTemplate
        .setToolTipText("Template used to format alarm file for outbound interface. Name of the file in directory /eniq/sw/conf/alarm_templates.");
    c.gridy = 2;
    c.gridx = 0;

    JLabel l_alarmTemplate = new JLabel("Alarm output format");
    l_alarmTemplate
        .setToolTipText("Template used to format alarm file for outbound interface. Name of the file in directory /eniq/sw/conf/alarm_templates.");
    parent.add(l_alarmTemplate, c);

    c.gridx = 1;
    parent.add(alarmTemplate, c);

    // Period duration

    periodDuration = new JTextField(p.getProperty("periodDuration", "0"), 10);
    periodDuration.setToolTipText("");
    c.gridy = 3;
    c.gridx = 0;

    JLabel l_periodDuration = new JLabel("Period duration");
    l_periodDuration.setToolTipText("");
    parent.add(l_periodDuration, c);

    c.gridx = 1;
    parent.add(periodDuration, c);

    // Timelevel

    timelevel = new JTextField(p.getProperty("timelevel", "0"), 10);
    timelevel.setToolTipText("");
    c.gridy = 4;
    c.gridx = 0;

    JLabel l_timelevel = new JLabel("Timelevel");
    l_timelevel.setToolTipText("");
    parent.add(l_timelevel, c);

    c.gridx = 1;
    parent.add(timelevel, c);

    // Finishing touch for the ui.

    c.gridx = 2;
    c.gridy = 5;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1, 1)), c);

  }

  public String validate() {
    try {
      new SimpleDateFormat(dateFormat.getText());
    } catch (IllegalArgumentException e) {
      return "Parameter Dateformat: Invalid timestamp format\n";
    }
    
    if(periodDuration.getText().length() <= 0) {
    } else {
      try {
        Integer.parseInt(periodDuration.getText());
      } catch(NumberFormatException nfe) {
        return "Period duration is not a valid number.";
      }
    }

    return "";
  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("tag_id");
    s.add("dateformat");
    s.add("AlarmTemplate");
    s.add("periodDuration");
    s.add("timelevel");
    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {

    p.setProperty("tag_id", tag_id.getText());
    p.setProperty("dateformat", dateFormat.getText());
    p.setProperty("AlarmTemplate", alarmTemplate.getText());
    p.setProperty("periodDuration", periodDuration.getText());
    p.setProperty("timelevel", timelevel.getText());
  }

}
