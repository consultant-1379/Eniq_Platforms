package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


public class GenericEmptyActionView implements ActionView {

  private Meta_transfer_actions action = null;

  private String type;
    
  public GenericEmptyActionView(JPanel parent, Meta_transfer_actions _action, String _type) {
    this.action = _action;
    this.type = _type;
    
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    
    parent.add(new JLabel("No user editable parameters"), c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return this.type;
  }
  
  public String validate() {
    return "";
  }
    
  public String getContent() throws Exception {
    if(action != null)
      return action.getAction_contents();
    else
      return "";
  }
  
  public String getWhere() throws Exception {
    if(action != null)
      return action.getWhere_clause();
    else
      return "";
  }
  
  public boolean isChanged() {
    return true;
  }

}
