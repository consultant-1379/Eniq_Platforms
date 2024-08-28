package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class SystemCallActionView implements ActionView {

  private JTextArea source;
  private Meta_transfer_actions action;
  
  public SystemCallActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;
    
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
    
    source = new JTextArea(5,20);
    source.setLineWrap(true);
    source.setWrapStyleWord(true);
    if(action != null)
      source.setText(action.getAction_contents());

    parent.add(new JLabel("Command"),c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(source,c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return "System Call";
  }
  
  public String validate() {
    if(source.getText().trim().length() <= 0)
      return "Parameter Command must be defined\n";
    else
      return "";
  }
    
  public String getContent() throws Exception {
    return source.getText().trim();
  }
  
  public String getWhere() throws Exception {
    return "";
  }
  
  public boolean isChanged() {
    if(action == null)
      return true;
    else
      return source.getText().trim().equals(action.getAction_contents());    
  }

}
