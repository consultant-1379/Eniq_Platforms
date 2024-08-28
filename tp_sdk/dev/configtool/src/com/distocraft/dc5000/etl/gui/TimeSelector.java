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
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.toedter.calendar.JDateChooser;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class TimeSelector extends JPanel {

  private int originalHour;
  private int originalMin;
  
  private JSpinner hourspin;
  private JSpinner minspin;
  
  public TimeSelector(int hour, int min) {
    super(new GridBagLayout());
    
    originalHour = hour;
    originalMin = min;

    setBorder(BorderFactory.createRaisedBevelBorder());
        
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(1,2,1,2);
    
    SpinnerModel hsp = new SpinnerNumberModel(originalHour,0,23,1);
    hourspin = new JSpinner(hsp);
    this.add(hourspin,c);
    
    c.gridx = 1;
    this.add(new JLabel(":"),c);
 
    c.gridx = 2;
    SpinnerModel msp = new SpinnerNumberModel(originalMin,0,59,1);
    minspin = new JSpinner(msp);
    this.add(minspin,c);
        
  }
  
  public int getHour() {
    return ((Integer)hourspin.getValue()).intValue();
  }
  
  public int getMin() {
    return ((Integer)minspin.getValue()).intValue();
  }

}
