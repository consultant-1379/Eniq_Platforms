package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.net.SyslogAppender;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * Copyright Distocraft 2006<br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class SystemMonitorActionView implements ActionView {

  private JTextField alarmTemplate;

  private JTextField alarmFileOutputDirectory;

  private JTextField alarmFilenamePattern;

  private JTextField syslogTemplate;

  private JTextField syslogHost;

  private static String[] syslogFacilityOptions = { "USER", "KERN", "MAIL", "DAEMON", "AUTH", "SYSLOG", "LPR", "NEWS",
      "UUCP", "CRON", "AUTHPRIV", "FTP" };

  private JComboBox syslogFacility = new JComboBox(syslogFacilityOptions);

  private String[] syslogPriorityOptions = { "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL" };

  private JComboBox syslogPriority = new JComboBox(syslogPriorityOptions);

  private Meta_transfer_actions action;

  private Properties properties;

  public SystemMonitorActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;

    properties = new Properties();

    if (action != null) {
      String act_cont = action.getAction_contents();
      if (act_cont != null && act_cont.length() > 0) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          properties.load(bais);
          bais.close();
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

    alarmTemplate = new JTextField(20);
    alarmTemplate.setToolTipText("Name of the template used in system monitor alarms.");
    alarmTemplate.setText(properties.getProperty("alarmTemplate", ""));

    parent.add(new JLabel("Alarm template"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(alarmTemplate, c);

    alarmFileOutputDirectory = new JTextField(20);
    alarmFileOutputDirectory.setToolTipText("Directory path where the system monitor alarm files are saved.");
    alarmFileOutputDirectory.setText(properties.getProperty("alarmFileOutputDirectory", ""));

    c.gridy = 1;
    c.gridx = 0;
    parent.add(new JLabel("Output directory for alarms"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(alarmFileOutputDirectory, c);

    alarmFilenamePattern = new JTextField(20);
    alarmFilenamePattern
        .setToolTipText("Naming pattern of the system monitor alarm files. Special characters in filename: $ is replaced by timestamp, @ is replaced by hostname and # is replaced by monitorname.");
    alarmFilenamePattern.setText(properties.getProperty("alarmFilenamePattern", ""));

    c.gridy = 2;
    c.gridx = 0;
    parent.add(new JLabel("Filename pattern for alarm files"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(alarmFilenamePattern, c);

    syslogTemplate = new JTextField(20);
    syslogTemplate.setToolTipText("Template used to create syslog messages.");
    syslogTemplate.setText(properties.getProperty("syslogTemplate", ""));
    c.gridy = 3;
    c.gridx = 0;
    parent.add(new JLabel("Syslog template"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(syslogTemplate, c);

    syslogHost = new JTextField(20);
    syslogHost.setToolTipText("Name of the host where syslog messages are sent.");
    syslogHost.setText(properties.getProperty("syslogHost", ""));

    c.gridy = 4;
    c.gridx = 0;
    parent.add(new JLabel("Syslog host name"), c);

    c.weightx = 1;
    c.gridx = 1;
    parent.add(syslogHost, c);

    c.gridy = 5;
    c.gridx = 0;
    parent.add(new JLabel("Syslog facility"), c);

    syslogFacility.setToolTipText("Name of the facility where the log entry is sent.");
    String syslogFacilityProperty = properties.getProperty("syslogFacility");
    if (syslogFacilityProperty != null) {
      // Set the selected value.
      for (int i = 0; i < syslogFacilityOptions.length; i++) {
        if (syslogFacilityOptions[i].equalsIgnoreCase(syslogFacilityProperty))
          syslogFacility.setSelectedIndex(i);
      }
    }

    c.weightx = 1;
    c.gridx = 1;
    parent.add(syslogFacility, c);

    c.gridy = 6;
    c.gridx = 0;
    parent.add(new JLabel("Syslog priority"), c);

    syslogPriority.setToolTipText("Priority of the syslog entry.");
    String syslogPriorityProperty = properties.getProperty("syslogPriority");
    if (syslogPriorityProperty != null) {
      // Set the selected value.
      for (int i = 0; i < syslogPriorityOptions.length; i++) {
        if (syslogPriorityOptions[i].equalsIgnoreCase(syslogPriorityProperty))
          syslogPriority.setSelectedIndex(i);
      }
    }

    c.weightx = 1;
    c.gridx = 1;
    parent.add(syslogPriority, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "System Monitor";
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("alarmTemplate", alarmTemplate.getText());
    p.setProperty("alarmFileOutputDirectory", alarmFileOutputDirectory.getText());
    p.setProperty("alarmFilenamePattern", alarmFilenamePattern.getText());
    p.setProperty("syslogTemplate", syslogTemplate.getText());
    p.setProperty("syslogHost", syslogHost.getText());
    p.setProperty("syslogFacility", syslogFacilityOptions[syslogFacility.getSelectedIndex()]);
    p.setProperty("syslogPriority", this.syslogPriorityOptions[syslogPriority.getSelectedIndex()]);
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

  /**
   * This function validates this actionview's parameters.
   * @return Returns the error strings separated by linechange (\n). In case of no errors, empty string is returned.
   */
  public String validate() {
    // Check if the alarm parameters are all set correctly.
    if ((alarmTemplate.getText().trim().length() > 0) || (alarmFileOutputDirectory.getText().trim().length() > 0)
        || (alarmFilenamePattern.getText().trim().length() > 0)) {
      if (alarmTemplate.getText().trim().length() <= 0)
        return "Parameter Alarm template must be defined\n";
      else if (alarmFileOutputDirectory.getText().trim().length() <= 0)
        return "Parameter Output directory for alarms must be defined\n";
      else if (alarmFilenamePattern.getText().trim().length() <= 0)
        return "Parameter Filename pattern for alarm files must be defined\n";
    }

    // Syslog host name and syslog template name are requires parameters if syslogs are used.
    if ((syslogTemplate.getText().trim().length() > 0) || (syslogHost.getText().trim().length() > 0)) {
      if (syslogTemplate.getText().trim().length() <= 0)
        return "Parameter Syslog template must be defined\n";
      else if (syslogHost.getText().trim().length() <= 0)
        return "Parameter Syslog host name must be defined\n";
    }

    return "";
  }
}
