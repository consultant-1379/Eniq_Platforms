package com.distocraft.dc5000.etl.gui.schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.distocraft.dc5000.etl.gui.TimeSelector;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.toedter.calendar.JDateChooser;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class OnceScheduleView implements ScheduleView {

  private JDateChooser dchooser;
  private TimeSelector tselector;
  
  public OnceScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();
  
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 1;
    
    parent.add(new JLabel("Occures"),c);
 
    c.gridy = 1;
    
    Calendar cal = new GregorianCalendar();
    
    if(sch != null && sch.getScheduling_day() != null)
      cal.set(Calendar.DAY_OF_MONTH, sch.getScheduling_day().intValue());
    
    if(sch != null && sch.getScheduling_month() != null)
      cal.set(Calendar.MONTH, sch.getScheduling_month().intValue());
    
    if(sch != null && sch.getScheduling_year() != null)
      cal.set(Calendar.YEAR, sch.getScheduling_year().intValue());
 
    dchooser = new JDateChooser();
    dchooser.setDate(cal.getTime());
    
    parent.add(dchooser,c);
    
    c.gridy = 2;
    parent.add(new JLabel("At"),c);
    
    c.gridy = 3;
    if(sch != null && sch.getScheduling_hour() != null && sch.getScheduling_min() != null) {
      tselector = new TimeSelector(sch.getScheduling_hour().intValue(), sch.getScheduling_min().intValue());
    } else {
      tselector = new TimeSelector(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
    }
    parent.add(tselector,c);
    
    c.gridx = 2;
    c.gridy = 4;
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

    sch.setScheduling_hour(new Long(tselector.getHour()));
    sch.setScheduling_min(new Long(tselector.getMin()));
    
    Calendar cal = new GregorianCalendar();
    cal.setTime(dchooser.getDate());
    
    sch.setScheduling_day(new Long(cal.get(GregorianCalendar.DAY_OF_MONTH)));
    sch.setScheduling_month(new Long(cal.get(GregorianCalendar.MONTH)));
    sch.setScheduling_year(new Long(cal.get(GregorianCalendar.YEAR)));

  }
    
}