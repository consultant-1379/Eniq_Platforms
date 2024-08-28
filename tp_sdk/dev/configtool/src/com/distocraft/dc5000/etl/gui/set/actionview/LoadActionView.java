package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class LoadActionView implements ActionView {

  private JTextArea clause;
  private Meta_transfer_actions action;
  
  public LoadActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;
    
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
    
    clause = new JTextArea(20,20);
    clause.setToolTipText("SQL template clause used to load files");
    clause.setLineWrap(true);
    clause.setWrapStyleWord(true);
    JScrollPane scrollPane = 
      new JScrollPane(clause,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    if(action != null)
      clause.setText(action.getAction_contents());

    JLabel l_clause = new JLabel("Load Clause");
    l_clause.setToolTipText("SQL template clause used to load files");
    parent.add(l_clause,c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 2;
    parent.add(scrollPane,c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return "Load";
  }
  
  public String validate() {
    if(clause.getText().length() <= 0)
      return "Parameter Load clause must be defined.\n";
    else
      return "";
  }
    
  public String getContent() throws Exception {
    return clause.getText().trim();
  }
  
  public String getWhere() throws Exception {
    return "";
  }
  
  public boolean isChanged() {
    if(action == null)
      return true;
    else
      return clause.getText().trim().equals(action.getAction_contents());    
  }

}
