package com.distocraft.dc5000.etl.gui.iface;

import javax.swing.tree.DefaultMutableTreeNode;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;



public class InterfaceTreeNode extends DefaultMutableTreeNode {
  
  private Datainterface di = null;
  
  public InterfaceTreeNode(Datainterface di) {
    super(di.getInterfacename());
    this.di = di;
  }
  
  public Datainterface getDataInterface() {
    return di;
  }

}
