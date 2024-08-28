package com.distocraft.dc5000.etl.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 *
 * @author lemminkainen
 */
public class AboutDialog extends JDialog {

  public AboutDialog(JFrame frame) {
    super(frame, true);

    this.setTitle("About");
    this.setResizable(false);
    
    Container con = this.getContentPane();
    con.setBackground(Color.WHITE);
    
    con.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;
    con.add(Box.createRigidArea(new Dimension(10, 10)), c);

    c.gridx = 4;
    con.add(Box.createRigidArea(new Dimension(10, 10)), c);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0.5;
    c.gridx = 1;
    c.gridy = 1;
    con.add(Box.createRigidArea(new Dimension(1, 1)), c);
    
    c.gridx = 3;
    con.add(Box.createRigidArea(new Dimension(1, 1)), c);
    
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.gridx = 2;
    con.add(new JLabel(ConfigTool.eniqLogo),c);
    
    c.gridy = 2;
    con.add(Box.createRigidArea(new Dimension(10, 10)), c);
    
    c.gridy = 3;
    c.gridx = 1;
    c.gridwidth = 3;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    con.add(new JLabel("Ericsson Network IQ ETLC ConfigTool"),c);
    
    c.gridy = 4;
    con.add(new JLabel("Version b" + ConfigTool.BUILD_NUMBER),c);
    
    c.gridy = 5;
    con.add(new JLabel("(c) 2004-2006 Ericsson ltd"),c);
    
    c.gridy = 10;
    con.add(Box.createRigidArea(new Dimension(10, 10)), c);
    
    c.gridy = 11;
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.NONE;
    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        AboutDialog.this.setVisible(false);
      }
    });
    con.add(ok, c);
    
    c.gridy = 12;
    con.add(Box.createRigidArea(new Dimension(10, 10)), c);
    
    this.pack();
    this.setVisible(true);

  }


}
