package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class AggregationActionView implements ActionView {

  private JTextField table;
  private JTextArea content;
  private Meta_transfer_actions action;
  
  public AggregationActionView(JPanel parent, Meta_transfer_actions _action) {
    this.action = _action;
    
    parent.removeAll();
    
    table = new JTextField(20);
    table.setToolTipText("Table name used to prevent multiple simultaneous db accesses to same table.");
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
    
    JLabel l_table = new JLabel("Table Name");
    l_table.setToolTipText("Table name used to prevent multiple simultaneous db accesses to same table.");
    parent.add(l_table,c);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(table,c);
    
    content = new JTextArea(20,20);
    content.setToolTipText("Aggregation clause template.");
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    JScrollPane scrollPane = 
      new JScrollPane(content,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = 1;
    
    JLabel l_content = new JLabel("Aggregation Clause");
    l_content.setToolTipText("Aggregation clause template.");
    parent.add(l_content,c);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(scrollPane,c);
    
    if(action != null) {
      
      Properties prop = new Properties();

      String str = action.getWhere_clause();
      
      try {

        if (str != null && str.length() > 0) {

          ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }

      } catch (Exception e) {

      }
      
      table.setText(prop.getProperty("tablename",""));
      
      content.setText(action.getAction_contents());
      
    }
      
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return "Aggregation";
  }
    
  public String validate() {
    if(content.getText().trim().length() <= 0)
      return "Parameter Aggregation Clause must be defined\n";
    return "";
  }
  
  public String getContent() throws Exception {
    return content.getText().trim();
  }
  
  public String getWhere() throws Exception {
    
    Properties p = new Properties();
    p.put("tablename", table.getText().trim());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }
  
  public boolean isChanged() {
    return true;
  }

}
