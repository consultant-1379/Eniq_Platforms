package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;



/**
 * @author melantie
 * Copyright Distocraft 2005
 * 
 * $id$
 */
public class TriggerScheduledSetActionView implements ActionView {

    private JTextArea source;
    private Meta_transfer_actions action;
    
    public TriggerScheduledSetActionView(JPanel parent, Meta_transfer_actions action) {
      
      parent.removeAll();
      
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.NONE;
      c.insets = new Insets(2,2,2,2);
      c.weightx = 0;
      c.weighty = 0;
      
      source = new JTextArea(10,20);
      source.setLineWrap(true);
      source.setWrapStyleWord(true);
      
      if(action != null)
        source.setText(action.getAction_contents());

      JLabel l_source = new JLabel("Set names");
      l_source.setToolTipText("Names of triggered sets. Multiple names are delimited by comma.");
      parent.add(l_source,c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      c.gridx = 2;
      source.setToolTipText("Names of triggered sets. Multiple names are delimited by comma.");
      parent.add(source,c);
      
      parent.invalidate();
      parent.revalidate();
      parent.repaint();
    }

    public String getType() {
      return "TriggerScheduledSet";
    }
      
    public String getContent() throws Exception {
      return source.getText().trim();
    }
    
    public String validate() {
      // Parameter Set names can be empty if parser triggers loaders.
      /*
      if(source.getText().trim().length() <= 0)
        return "Parameter Set names must be defined\n";
      else
      */
        return "";
    }
    
    public String getWhere() throws Exception {
      return "";
    }
    
    public boolean isChanged() {
      if(action == null)
        return true;
      else 
        return (source.getText().trim().equals(action.getWhere_clause()) || source.getText().trim().equals(action.getAction_contents()));    
    }

  }
