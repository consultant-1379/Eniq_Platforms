package com.distocraft.dc5000.etl.gui.schedule;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_schedulings;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class WaitScheduleView implements ScheduleView {

  public WaitScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;
    
    parent.add(new JLabel("Waiting for trigger event"),c);
        
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String validate() {
    return "";
  }
  
  public void fill(Meta_schedulings sch) {
    
  }
  
}
