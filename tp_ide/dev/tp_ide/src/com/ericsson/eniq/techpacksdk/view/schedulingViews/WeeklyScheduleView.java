package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.TimeSelector;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class WeeklyScheduleView implements ScheduleView {

  private JCheckBox mon;
  private JCheckBox tue;
  private JCheckBox wed;
  private JCheckBox thu;
  private JCheckBox fri;
  private JCheckBox sat;
  private JCheckBox sun;

  private TimeSelector tselector;

  public WeeklyScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 1;

    parent.add(new JLabel("Occures on"), c);

    mon = new JCheckBox("Mondays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getMon_flag())){
        mon.setSelected(true);
      }
    }
    c.gridy = 1;
    parent.add(mon, c);

    tue = new JCheckBox("Tuesdays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getTue_flag())){
        tue.setSelected(true);
      }
    }
    c.gridy = 2;
    parent.add(tue, c);

    wed = new JCheckBox("Wednesdays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getWed_flag())){
        wed.setSelected(true);
      }
    }
    c.gridy = 3;
    parent.add(wed, c);

    thu = new JCheckBox("Thursdays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getThu_flag())){
        thu.setSelected(true);
      }
    }
    c.gridy = 4;
    parent.add(thu, c);

    fri = new JCheckBox("Fridays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getFri_flag())){
        fri.setSelected(true);
      }
    }
    c.gridy = 5;
    parent.add(fri, c);

    sat = new JCheckBox("Saturdays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getSat_flag())){
        sat.setSelected(true);
      }
    }
    c.gridy = 6;
    parent.add(sat, c);

    sun = new JCheckBox("Sundays");
    if (sch != null) {
      if ("y".equalsIgnoreCase(sch.getSun_flag())){
        sun.setSelected(true);
      }
    }
    c.gridy = 7;
    parent.add(sun, c);

    c.gridy = 8;
    parent.add(new JLabel("At"), c);

    c.gridy = 9;
    if (sch != null && sch.getScheduling_hour() != null && sch.getScheduling_min() != null) {
      tselector = new TimeSelector(sch.getScheduling_hour().intValue(), sch.getScheduling_min().intValue());
    } else {
      Calendar cal = new GregorianCalendar();
      tselector = new TimeSelector(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
    parent.add(tselector, c);

    c.gridx = 2;
    c.gridy = 10;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridwidth = 1;
    parent.add(Box.createRigidArea(new Dimension(5, 5)), c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();

  }

  public String validate() {
    if (!mon.isSelected() && !tue.isSelected() && !wed.isSelected() && !thu.isSelected() && !fri.isSelected()
        && !sat.isSelected() && !sun.isSelected()) {
      return "At least one day must be selected\n";
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

    sch.setScheduling_hour(new Long(tselector.getHour()));
    sch.setScheduling_min(new Long(tselector.getMin()));

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
