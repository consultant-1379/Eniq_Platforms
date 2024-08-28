package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.TimeSelector;

/**
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class WeeklyIntervalScheduleView implements ScheduleView {

  private JCheckBox mon;

  private JCheckBox tue;

  private JCheckBox wed;

  private JCheckBox thu;

  private JCheckBox fri;

  private JCheckBox sat;

  private JCheckBox sun;

  private TimeSelector intervalStartTime;

  private TimeSelector intervalEndTime;

  private JSpinner intervalHours;

  private JSpinner intervalMinutes;

  public WeeklyIntervalScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 2;

    parent.add(new JLabel("Occures on"), c);

    mon = new JCheckBox("Monday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getMon_flag())){
        mon.setSelected(true);
      }
    }
    c.gridy = 1;
    parent.add(mon, c);

    tue = new JCheckBox("Tuesday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getTue_flag())){
        tue.setSelected(true);
      }
    }
    c.gridy = 2;
    parent.add(tue, c);

    wed = new JCheckBox("Wednesday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getWed_flag())){
        wed.setSelected(true);
      }
    }
    c.gridy = 3;
    parent.add(wed, c);

    thu = new JCheckBox("Thursday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getThu_flag())){
        thu.setSelected(true);
      }
    }
    c.gridy = 4;
    parent.add(thu, c);

    fri = new JCheckBox("Friday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getFri_flag())){
        fri.setSelected(true);
      }
    }
    c.gridy = 5;
    parent.add(fri, c);

    sat = new JCheckBox("Saturday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getSat_flag())){
        sat.setSelected(true);
      }
    }
    c.gridy = 6;
    parent.add(sat, c);

    sun = new JCheckBox("Sunday");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getSun_flag())){
        sun.setSelected(true);
      }
    }
    c.gridy = 7;
    parent.add(sun, c);

    c.gridy = 8;
    parent.add(new JLabel("From"), c);

    // Get/parse the interval values from database to these variables from this.OSCommand.
    String serializedIntervalString = sch.getOs_command();

    Properties intervalProps = new Properties();

    if (serializedIntervalString != null && serializedIntervalString.length() > 0) {

      try {
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedIntervalString.getBytes());
        intervalProps.load(bais);
        bais.close();
        Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.WeeklyIntervalScheduleView").log(Level.FINEST,
            "Interval Properties-object read in view");
      } catch (Exception e) {
        Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.WeeklyIntervalScheduleView").log(Level.FINEST,
            "Interval Properties-object error in view");
      }
    }

    if (intervalProps.getProperty("intervalStartHour") == null
        || intervalProps.getProperty("intervalStartHour").equals("")) {
      intervalProps.setProperty("intervalStartHour", "0");
    }

    if (intervalProps.getProperty("intervalStartMinute") == null
        || intervalProps.getProperty("intervalStartMinute").equals("")) {
      intervalProps.setProperty("intervalStartMinute", "0");
    }

    if (intervalProps.getProperty("intervalEndHour") == null || intervalProps.getProperty("intervalEndHour").equals("")) {
      intervalProps.setProperty("intervalEndHour", "0");
    }

    if (intervalProps.getProperty("intervalEndMinute") == null
        || intervalProps.getProperty("intervalEndMinute").equals("")) {
      intervalProps.setProperty("intervalEndMinute", "0");
    }

    Integer intervalStartHour = new Integer(intervalProps.getProperty("intervalStartHour"));
    Integer intervalStartMinute = new Integer(intervalProps.getProperty("intervalStartMinute"));
    Integer intervalEndHour = new Integer(intervalProps.getProperty("intervalEndHour"));
    Integer intervalEndMinute = new Integer(intervalProps.getProperty("intervalEndMinute"));

    if (sch != null && intervalStartHour != null && intervalStartMinute != null) {
      intervalStartTime = new TimeSelector(intervalStartHour.intValue(), intervalStartMinute.intValue());
    } else {
      Calendar cal = new GregorianCalendar();
      intervalStartTime = new TimeSelector(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
    c.gridy = 9;
    parent.add(intervalStartTime, c);

    c.gridy = 10;
    parent.add(new JLabel("To"), c);

    if (sch != null && intervalEndHour != null && intervalEndMinute != null) {
      intervalEndTime = new TimeSelector(intervalEndHour.intValue(), intervalEndMinute.intValue());
    } else {
      Calendar cal = new GregorianCalendar();
      intervalEndTime = new TimeSelector(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
    c.gridy = 11;
    parent.add(intervalEndTime, c);

    c.gridy = 12;
    parent.add(new JLabel("Occures every"), c);

    int origHour = 0;
    if (sch != null && sch.getInterval_hour() != null){
      origHour = sch.getInterval_hour().intValue();
    }
    SpinnerModel hsp = new SpinnerNumberModel(origHour, 0, 24, 1);
    intervalHours = new JSpinner(hsp);

    c.gridy = 13;
    c.gridx = 0;
    c.gridwidth = 1;
    parent.add(intervalHours, c);

    c.gridx = 1;
    parent.add(new JLabel("hours"), c);

    int origMin = 0;
    if (sch != null && sch.getInterval_min() != null){
      origMin = sch.getInterval_min().intValue();
    }
    SpinnerModel msp = new SpinnerNumberModel(origMin, 0, 59, 1);
    intervalMinutes = new JSpinner(msp);
    c.gridy = 14;
    c.gridx = 0;
    parent.add(intervalMinutes, c);

    c.gridx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(new JLabel("minutes"), c);

    c.gridy = 15;
    c.gridx = 2;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridwidth = 2;
    parent.add(Box.createRigidArea(new Dimension(5, 5)), c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();

  }

  public String validate() {
    // TODO: More validation.
    if (!mon.isSelected() && !tue.isSelected() && !wed.isSelected() && !thu.isSelected() && !fri.isSelected()
        && !sat.isSelected() && !sun.isSelected()) {
      return "At least one day must be selected\n";
    } else if (((Integer) intervalHours.getValue()).intValue() > 23
        || ((Integer) intervalHours.getValue()).intValue() < 0) {
      return "Intervalhour must be between 0-23\n";
    } else if (((Integer) intervalMinutes.getValue()).intValue() > 59
        || ((Integer) intervalMinutes.getValue()).intValue() < 0) {
      return "Intervalminute must be between 0-59\n";
    } else{
      return "";
    }
  }

  public void fill(Meta_schedulings sch) {
    if (mon.isSelected()){
      sch.setMon_flag("Y");
    }
    else{
      sch.setMon_flag("N");
    }

    if (tue.isSelected()){
      sch.setTue_flag("Y");
    }
    else{
      sch.setTue_flag("N");
    }

    if (wed.isSelected()){
      sch.setWed_flag("Y");
    }
    else{
      sch.setWed_flag("N");
    }

    if (thu.isSelected()){
      sch.setThu_flag("Y");
    }
    else{
      sch.setThu_flag("N");
    }

    if (fri.isSelected()){
      sch.setFri_flag("Y");
    }
    else{
      sch.setFri_flag("N");
    }

    if (sat.isSelected()){
      sch.setSat_flag("Y");
    }
    else{
      sch.setSat_flag("N");
    }

    if (sun.isSelected()){
      sch.setSun_flag("Y");
    }
    else{
      sch.setSun_flag("N");
    }

    Integer intervalStartHour = new Integer(intervalStartTime.getHour());
    Integer intervalStartMinute = new Integer(intervalStartTime.getMin());
    Integer intervalEndHour = new Integer(intervalEndTime.getHour());
    Integer intervalEndMinute = new Integer(intervalEndTime.getMin());

    Properties intervalProps = new Properties();
    intervalProps.setProperty("intervalStartHour", intervalStartHour.toString());
    intervalProps.setProperty("intervalStartMinute", intervalStartMinute.toString());
    intervalProps.setProperty("intervalEndHour", intervalEndHour.toString());
    intervalProps.setProperty("intervalEndMinute", intervalEndMinute.toString());

    // Save the intervalStartTime and intervalEndTime to OS_COMMAND in database.
    // These values are stored within are serialized Properties-class.   
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      intervalProps.store(baos, "");
      sch.setOs_command(baos.toString());

    } catch (Exception e) {
      Logger.getLogger("com.distocraft.dc5000.etl.gui.schedule.WeeklyIntervalScheduleView").log(Level.FINEST,
          "Interval Properties-object error in view");
    }

    sch.setInterval_hour(new Long(((Integer)intervalHours.getValue()).longValue()));
    sch.setInterval_min(new Long(((Integer)intervalMinutes.getValue()).longValue()));

    /* current date */
    Date curDate = new Date();
    GregorianCalendar curCal = new GregorianCalendar();
    curCal.setTime(curDate);

    sch.setScheduling_day(new Long(curCal.get(GregorianCalendar.DAY_OF_MONTH)));
    sch.setScheduling_month(new Long(curCal.get(GregorianCalendar.MONTH)));
    sch.setScheduling_year(new Long(curCal.get(GregorianCalendar.YEAR)));

    sch.setStatus(null);
    sch.setLast_execution_time(null);

  }

}
