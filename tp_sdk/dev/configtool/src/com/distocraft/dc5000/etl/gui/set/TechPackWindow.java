package com.distocraft.dc5000.etl.gui.set;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class TechPackWindow extends JDialog {

  private Meta_collection_sets tp;

  private boolean committed = false;

  public TechPackWindow(JFrame frame, RockFactory rock, Meta_collection_sets _tp) {
    super(frame, true);
    
    this.tp = _tp;
    
    setTitle("TechPack " + tp.getCollection_set_name()+" "+tp.getVersion_number());
    
    Container con = getContentPane();
    con.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 1;
    c.gridx = 1;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    String hdr = "Modify parameters of TechPack";
    con.add(new JLabel(hdr), c);

    c.gridy = 2;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Package"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    con.add(new JLabel(tp.getCollection_set_name() + " " + tp.getVersion_number()), c);

    // c.gridy = 4 removed

    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("ID"), c);

    c.gridx = 2; 
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    JLabel sid = new JLabel(tp.getCollection_set_id().toString());
    con.add(sid, c);

    // c.gridy = 6; //removed
    // c.gridy = 7; //removed
    // c.gridy = 8; //removed
    // c.gridy = 9; //removed
    
    c.gridx = 1;
    c.gridy = 10;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Status"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    JComboBox enstatus = new JComboBox(ConfigTool.STATUSES);
    if ("n".equalsIgnoreCase(tp.getEnabled_flag()))
      enstatus.setSelectedIndex(1);
    else
      enstatus.setSelectedIndex(0);
    con.add(enstatus, c);

    c.gridy = 11;
    c.gridx = 4;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 12;
    c.gridx = 1;
    JButton discard = new JButton("Cancel", ConfigTool.delete);
    discard.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        setVisible(false);
      }
    });
    con.add(discard, c);

    c.gridx = 2;
    JButton save = new JButton("Modify", ConfigTool.newIcon);
    save.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        committed = true;
        setVisible(false);
      }
    });
    con.add(save, c);

    pack();
    setVisible(true);

    if (!committed)
      return;

    try {
 
      if (enstatus.getSelectedIndex() == 0) {
        tp.setEnabled_flag("Y");
      } else {
        tp.setEnabled_flag("N");
      }
      
      tp.updateDB();

    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(frame,"Error","Unable to modify techPack",e);
    }
    
  }
  
  public boolean committed() {
    return committed;
  }

}
