package com.distocraft.dc5000.etl.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.toedter.calendar.JDateChooser;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class DateTimeSelector extends JPanel {

  private Calendar originalCalendar = null;
  private JDateChooser jdc;
  
  private JSpinner hourspin;
  private JSpinner minspin;
  
  public DateTimeSelector(Calendar cal) {
    super(new GridBagLayout());

    setBorder(BorderFactory.createRaisedBevelBorder());
    
    if(cal == null)
      originalCalendar = new GregorianCalendar();
    else
      originalCalendar = cal;

    cal.set(Calendar.SECOND,0);
    cal.set(Calendar.MILLISECOND,0);    
    
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(1,2,1,2);
    
    jdc = new JDateChooser();
    jdc.setDate(cal.getTime());

    this.add(jdc,c);
    
    c.gridx = 1;
    this.add(Box.createRigidArea(new Dimension(2,2)),c);
    
    c.gridx = 2;
    SpinnerModel hsp = new SpinnerNumberModel(cal.get(Calendar.HOUR_OF_DAY),0,23,1);
    hourspin = new JSpinner(hsp);
    this.add(hourspin,c);
    
    c.gridx = 3;
    this.add(new JLabel(":"),c);
 
    c.gridx = 4;
    SpinnerModel msp = new SpinnerNumberModel(cal.get(Calendar.MINUTE),0,59,1);
    minspin = new JSpinner(msp);
    this.add(minspin,c);
        
  }
  
  public Calendar getCalendar() {
    originalCalendar.setTime(jdc.getDate());
    originalCalendar.set(Calendar.HOUR_OF_DAY,((Integer)hourspin.getValue()).intValue());
    originalCalendar.set(Calendar.MINUTE,((Integer)minspin.getValue()).intValue());
    return originalCalendar;
  }

}
