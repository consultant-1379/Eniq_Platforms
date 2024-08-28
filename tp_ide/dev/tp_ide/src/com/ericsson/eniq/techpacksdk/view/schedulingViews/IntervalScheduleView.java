package com.ericsson.eniq.techpacksdk.view.schedulingViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.TimeSelector;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class IntervalScheduleView implements ScheduleView {

  private JSpinner hours;
  private JSpinner minutes;

  private TimeSelector base;

  public IntervalScheduleView(JPanel parent, Meta_schedulings sch) {
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

    int origHour = 0;
    if (sch != null && sch.getInterval_hour() != null)
      origHour = sch.getInterval_hour().intValue();
    SpinnerModel hsp = new SpinnerNumberModel(origHour,0,24,1);
    hours = new JSpinner(hsp);    
    parent.add(hours, c);

    c.gridx = 1;
    parent.add(new JLabel("hours"), c);

    c.gridy = 2;
    c.gridx = 0;
    
    int origMin = 0;
    if (sch != null && sch.getInterval_min() != null)
      origMin = sch.getInterval_min().intValue();
    SpinnerModel msp = new SpinnerNumberModel(origMin,0,59,1);
    minutes = new JSpinner(msp);    
    parent.add(minutes, c);

    c.gridx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    parent.add(new JLabel("minutes"), c);

    c.gridy = 3;
    c.gridx = 0;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.NONE;
    parent.add(new JLabel("Scheduling base time"), c);
    
    Calendar cal = new GregorianCalendar();
    
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    if(sch != null && sch.getScheduling_hour() != null)
      hour = sch.getScheduling_hour().intValue();
      
    int min = cal.get(Calendar.MINUTE);
    if(sch != null && sch.getScheduling_min() != null)
      min = sch.getScheduling_min().intValue();
    
    base = new TimeSelector(hour,min);
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
    String error = "";

    Long hour = new Long(((Integer)hours.getValue()).longValue());
    Long min = new Long(((Integer)minutes.getValue()).longValue());

    if(hour.intValue() <= 0 && min.intValue() <=0)
      error += "Defined interval must be positive\n";
    
    return error;
  }

  public void fill(Meta_schedulings sch) {
    sch.setScheduling_hour(new Long(base.getHour()));
    sch.setScheduling_min(new Long(base.getMin()));
        
    sch.setInterval_hour(new Long(((Integer)hours.getValue()).longValue()));
    sch.setInterval_min(new Long(((Integer)minutes.getValue()).longValue()));
    
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
