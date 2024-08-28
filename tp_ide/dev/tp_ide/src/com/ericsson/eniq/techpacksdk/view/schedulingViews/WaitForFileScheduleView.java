package com.ericsson.eniq.techpacksdk.view.schedulingViews;

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
public class WaitForFileScheduleView implements ScheduleView {
  
  private JTextField filename;

  public WaitForFileScheduleView(JPanel parent, Meta_schedulings sch) {
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    
    parent.add(new JLabel("Waiting for file"),c);
    
    c.gridy = 1;
    
    filename = new JTextField(20);
    if(sch != null)
      filename.setText(sch.getTrigger_command());
    parent.add(filename,c);
        
    c.gridx = 1;
    c.gridy = 1;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    parent.add(Box.createRigidArea(new Dimension(5,5)),c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String validate() {
    String error = "";

    if(filename.getText() == null || filename.getText().trim().length() <= 0)
      error += "Filename must be defined\n";
    
    return error;
  }
  
  public void fill(Meta_schedulings sch) {
    sch.setTrigger_command(filename.getText().trim());
  }
  
}
