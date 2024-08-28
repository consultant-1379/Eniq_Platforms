package com.distocraft.dc5000.etl.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.distocraft.dc5000.etl.gui.set.TechPackTreeNode;

import ssc.rockfactory.RockFactory;

/** 
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class HostNode extends DefaultMutableTreeNode implements TechPackTreeNode {

  /**
   * Creates this node and subNodes
   */
  public HostNode(String title) {
    super(title);
    
  }
  
  public JComponent getTable() {
    JPanel pan = new JPanel();

    return pan;
  }

}
