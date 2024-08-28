package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


/**
 * Copyright Ericsson 2011<br>
 * <br>
 * $id$
 *
 * @author eeoidiv
 */
public class SQLExecuteActionView implements ActionView {

  private JTextArea clause;
  private Meta_transfer_actions action;
  private JTextArea where;
  
  public SQLExecuteActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;
    
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
    
    // SQL Clause
    clause = new JTextArea(20,20);
    clause.setLineWrap(true);
    clause.setWrapStyleWord(true);
    JScrollPane scrollPane = 
      new JScrollPane(clause,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    clause.setToolTipText("SQL Clause");
    parent.add(new JLabel("SQL Clause"),c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(scrollPane,c);
    
    //eeoidiv,20110110,HN49966:TechPack IDE empties Where clause in SQL Execute action for DWH_MONITOR TP
    //Added Where to SQL Execute Action.
    // Where Clause
    where = new JTextArea(5, 20);
    where.setLineWrap(true);
    where.setWrapStyleWord(true);
    JScrollPane scrollPaneWhere = 
      new JScrollPane(where,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    where.setToolTipText("Where Clause");
    JLabel l_where = new JLabel("Where Clause");
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    parent.add(l_where, c);
    c.weightx = 1;
    c.gridx = 1;
    parent.add(scrollPaneWhere, c);
    
    // Text
    if(action != null) {
        clause.setText(action.getAction_contents());
        where.setText(action.getWhere_clause());
    }
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return "SQL Execute";
  }
  
  public String validate() {
    if(clause.getText().trim().length() <= 0)
      return "Parameter SQL clause must be defined\n";
    else
      return "";
  }
    
  public String getContent() throws Exception {
    return clause.getText().trim();
  }
  
  public String getWhere() throws Exception {
	  //eeoidiv,20110110,HN49966:TechPack IDE empties Where clause in SQL Execute action for DWH_MONITOR TP
	  //Added Where to SQL Execute Action.
	  return where.getText().trim();
  }
  
  public boolean isChanged() {
    if(action == null)
      return true;
    else
      return clause.getText().trim().equals(action.getAction_contents());    
  }


}
