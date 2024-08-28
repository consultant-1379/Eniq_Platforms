package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


public class PartitionedSQLExecuteActionView implements ActionView {

  private Properties orig = new Properties();
  
  private JTextArea clause;
  
  private JTextField typeName;
  
  private JComboBox useOnlyLoadedPartitions;
  
  public PartitionedSQLExecuteActionView(JPanel parent, Meta_transfer_actions action) {    
    if (action != null) {

      String where = action.getWhere_clause();

      if (where != null && where.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(where.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading where clause");
          e.printStackTrace();
        }
      }
    }
    
    parent.removeAll();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2,2,2,2);
    c.weightx = 0;
    c.weighty = 0;
    
    typeName = new JTextField(orig.getProperty("typeName",""),15);
    
    JLabel t_typeName = new JLabel("Basetablename");
    t_typeName.setToolTipText("Basetablename of the type.");
    parent.add(t_typeName,c);
    
    c.gridx = 1;
    c.weightx = 1;
    
    typeName.setToolTipText("Basetablename of the type.");
    parent.add(typeName,c);
    
    clause = new JTextArea(20,20);
    clause.setLineWrap(true);
    clause.setWrapStyleWord(true);
    JScrollPane scrollPane = 
      new JScrollPane(clause,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    if(action != null)
      clause.setText(action.getAction_contents());

    c.gridx = 0;
    c.weightx = 0;

    
    String[] booleans = {"false", "true"};
    useOnlyLoadedPartitions = new JComboBox(booleans);
    useOnlyLoadedPartitions.setSelectedIndex(Integer.parseInt(orig.getProperty("useOnlyLoadedPartitions","0")));
    
    c.gridx = 0;
    
    JLabel t_useOnlyLoadedPartitions = new JLabel("Only loaded");
    t_useOnlyLoadedPartitions.setToolTipText("Does the action use only loaded partitions or not.");
    parent.add(t_useOnlyLoadedPartitions,c);
    
    c.gridx = 1;
    c.weightx = 1;
    c.gridy = 1;
    useOnlyLoadedPartitions.setToolTipText("Does the action use only loaded partitions or not.");
    parent.add(useOnlyLoadedPartitions,c);
  
    c.gridx = 0;
    c.gridy = 2;
    
    JLabel l_clause = new JLabel("SQL Template");
    l_clause.setToolTipText("SQL template used to create executed clause.");
    parent.add(l_clause,c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    c.gridy = 2;
    clause.setToolTipText("SQL template used to create executed clause.");
    parent.add(scrollPane,c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }
  
  public String getType() {
    return "PartitionedSQLExecute";
  }
    
  public String validate() {
    String error = "";
    
    if(typeName.getText().trim().length() <= 0)
      error += "Parameter Basetablename must be defined.\n";
    
    if(clause.getText().trim().length() <= 0)
      error += "Parameter SQL template must be defined.\n";
    
    return error;
  }
  
  public String getContent() throws Exception {
    return clause.getText().trim();
  }
  
 
  public String getWhere() throws Exception {
    Properties p = new Properties();
    p.put("typeName", typeName.getText().trim());
    p.put("useOnlyLoadedPartitions", String.valueOf(useOnlyLoadedPartitions.getSelectedIndex()));
    return propertyToString(p);
  }
  
  protected String propertyToString(Properties prop) {

    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      prop.store(baos, "");

      return baos.toString();

    } catch (Exception e) {

    }

    return "";

  }
  
  public boolean isChanged() {
    return true;
  }

}

