package com.distocraft.dc5000.etl.gui.set.actionview;

  import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


  /**
   * @author melantie
   * Copyright Distocraft 2005
   * 
   * $id$
   */
  
  public class TableCleanerActionView implements ActionView {

      private JTextField tablename;
      private JTextField datecolumn;
      private JTextField threshold;

      public TableCleanerActionView(JPanel parent, Meta_transfer_actions action) {
        
        parent.removeAll();
                
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(2,2,2,2);
        c.weightx = 0;
        c.weighty = 0;
        
        tablename = new JTextField(20);
        JLabel l_tablename = new JLabel("Tablename");
        l_tablename.setToolTipText("Name of cleaned table");
        parent.add(l_tablename,c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        tablename.setToolTipText("Name of cleaned table");
        parent.add(tablename,c);
        
        //
        
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 1;
        
        datecolumn = new JTextField(20);
        JLabel l_datecolumn = new JLabel("Date Column");
        l_datecolumn.setToolTipText("Name of column that is used as condition for deletion.");
        parent.add(l_datecolumn,c);
        
        c.weightx = 1;
        c.gridx = 1;
        datecolumn.setToolTipText("Name of column that is used as condition for deletion.");
        parent.add(datecolumn,c);

        //
        
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 2;
        
        threshold = new JTextField(new NumericDocument(),"",4);
        JLabel l_threshold = new JLabel("Threshold In Days");
        l_threshold.setToolTipText("Limit of date age. Rows older than specified amount of days are deleted.");
        parent.add(l_threshold,c);
        
        
        c.weightx = 1;
        c.gridx = 1;
        threshold.setToolTipText("Limit of date age. Rows older than specified amount of days are deleted.");
        parent.add(threshold,c);
 
        if(action != null) {
          
          tablename.setText(stringToProperty(action.getAction_contents()).getProperty("tablename",""));
          datecolumn.setText(stringToProperty(action.getAction_contents()).getProperty("datecolumn",""));
          threshold.setText(stringToProperty(action.getAction_contents()).getProperty("threshold",""));         
        }
             
        parent.invalidate();
        parent.revalidate();
        parent.repaint();
      }

      public String getType() {
        return "TableCleaner";
      }
      
      public String validate() {
        String error = "";
        
        if(tablename.getText().trim().length() <= 0)
          error += "Parameter Tablename must be defined.\n";
        
        if(datecolumn.getText().trim().length() <= 0)
          error += "Parameter Date column must be defined.\n";
        
        if(threshold.getText().trim().length() <= 0)
          error += "Parameter Threshold in days must be defined.\n";
        
        return error;
      }

      public String getContent() throws Exception {
        
      	Properties p = new Properties();      	
      	p.put("tablename",tablename.getText().trim());      	
      	p.put("datecolumn",datecolumn.getText().trim());      	
      	p.put("threshold",threshold.getText().trim());      	
      	return propertyToString(p);
      	       
      }

      public String getWhere() throws Exception {
        return "";
      }

      public boolean isChanged() {
          return true;
      }

      
      protected Properties stringToProperty(String str){
        
        Properties prop = new Properties();

        try {
            
          	if (str != null && str.length() > 0) {
               
          		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
                prop.load(bais);
                bais.close();
          	}
            
     		
      	} catch (Exception e){
      		
      	}
      	
      	return prop;
      	
      }
      
      protected String propertyToString(Properties prop){
        
        try{
          
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();     
            prop.store(baos, "");

            return baos.toString();
        	
        } catch (Exception e){
        	
        }
      	
        return "";
        
      }
      

      
      
    }

