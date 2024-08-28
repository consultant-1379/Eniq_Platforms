package com.distocraft.dc5000.etl.gui.set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class TypeNode extends DefaultMutableTreeNode implements TechPackTreeNode {

  private String type;
  
  /**
   * Creates this node and subNodes
   */
  public TypeNode(String type) {
    super(type);
    
    this.type = type;
    
  }
  
  public String getType() {
    return type;
  }
  
  public JComponent getTable() {
    JPanel pan = new JPanel();

    return pan;
  }

}
