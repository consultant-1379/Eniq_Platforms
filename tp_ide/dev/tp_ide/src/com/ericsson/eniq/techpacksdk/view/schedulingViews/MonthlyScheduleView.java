package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MonthlyScheduleView implements ScheduleView {

  private JSpinner day;

  private JCheckBox last;

  private TimeSelector base;

  public MonthlyScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 2;

    parent.add(new JLabel("Occures every"), c);

    c.gridy = 1;
    c.gridx = 0;
    c.gridwidth = 1;

    int origDay = 1;
    boolean enabled = true;
    if (sch != null && sch.getScheduling_day() != null) {
      origDay = sch.getScheduling_day().intValue();
      if (origDay <= 0) {
        origDay = 0;
        enabled = false;
      }
    }

    SpinnerModel dsp;

    if (origDay == 0){
      dsp = new SpinnerNumberModel(origDay, 0, 31, 1);
    } else {
      dsp = new SpinnerNumberModel(origDay, 1, 31, 1);
    }
    
    day = new JSpinner(dsp);
    day.setEnabled(origDay != 0);
    parent.add(day, c);

    c.gridx = 1;
    parent.add(new JLabel("day of the month"), c);

    c.gridy = 2;
    c.gridx = 0;
    c.gridwidth = 2;
    last = new JCheckBox("last day of month");
    last.setSelected(origDay == 0);
    last.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        day.setEnabled(!last.isSelected());
      }
    });
    parent.add(last, c);

    c.gridy = 3;
    c.gridx = 0;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.NONE;
    parent.add(new JLabel("At"), c);

    Calendar cal = new GregorianCalendar();

    int hour = cal.get(Calendar.HOUR_OF_DAY);
    if (sch != null && sch.getScheduling_hour() != null)
      hour = sch.getScheduling_hour().intValue();

    int min = cal.get(Calendar.MINUTE);
    if (sch != null && sch.getScheduling_min() != null)
      min = sch.getScheduling_min().intValue();

    base = new TimeSelector(hour, min);
    c.gridy = 4;
    parent.add(base, c);

    c.gridx = 2;
    c.gridy = 5;
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
    return "";
  }

  public void fill(Meta_schedulings sch) {
    sch.setScheduling_hour(new Long(base.getHour()));
    sch.setScheduling_min(new Long(base.getMin()));

    if (last.isSelected())
      sch.setScheduling_day(new Long(-1));
    else
      sch.setScheduling_day(new Long(((Integer) day.getValue()).longValue()));

    /* current date */
    Date curDate = new Date();
    GregorianCalendar curCal = new GregorianCalendar();
    curCal.setTime(curDate);

    sch.setScheduling_month(new Long(curCal.get(GregorianCalendar.MONTH)));
    sch.setScheduling_year(new Long(curCal.get(GregorianCalendar.YEAR)));

    sch.setStatus(null);
    sch.setLast_execution_time(null);

  }

}
