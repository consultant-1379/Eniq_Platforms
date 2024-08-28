package com.ericsson.eniq.techpacksdk.view.newInterface;



import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
class NewInterfaceRenderer  extends DefaultTreeCellRenderer {


  private DataModelController dataModelController;
  
  public NewInterfaceRenderer(DataModelController dataModelController){
    this.dataModelController = dataModelController;
  }
   
  public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row, hasFocus);
    
    setForeground(Color.black);
    Object aUserObject = null;

    if (value != null) {
      aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
      if (aUserObject instanceof DataTreeNode) {             

        DataTreeNode t = (DataTreeNode)aUserObject;
        Datainterface di = (Datainterface) t.getRockDBObject();
        String toolTip = " ";
                      
        if (t.locked!=null && t.locked.length()>0){
          
          
          if (t.locked.equalsIgnoreCase(dataModelController.getUserName())){
            toolTip += " LOCKED BY "+t.locked;

          } else {
            setForeground(Color.LIGHT_GRAY);
            toolTip += " LOCKED BY "+t.locked;
          }
                                     
        } else {
          toolTip += " UNLOCKED ";
        }
            
        
        if (di.getEniq_level() == null || di.getEniq_level().equalsIgnoreCase("1.0")) {
          setForeground(Color.LIGHT_GRAY);
          toolTip += " Version too old";
        }

        
        setToolTipText(toolTip);
        
      }
    }
    
     return this;

   }
 
}
