package com.ericsson.eniq.techpacksdk.view.newTechPack;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
class NewTechPackRenderer  extends DefaultTreeCellRenderer {

 
  private DataModelController dataModelController;
  private boolean noBaseTypesFiltering;
  private Vector<String> nobasetypeslist = new Vector<String>();
  
  public NewTechPackRenderer(final boolean noBaseTypesFiltering, final DataModelController dataModelController){
    this.dataModelController = dataModelController;
    this.noBaseTypesFiltering = noBaseTypesFiltering;
    for (int i = 0 ; i < Constants.TYPES_NOBASE.length ; i++){
    	nobasetypeslist.add(Constants.TYPES_NOBASE[i]);
    }
  }
   
  public Component getTreeCellRendererComponent(final JTree tree,final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row, hasFocus);
    
    setForeground(Color.black);
    Object aUserObject = null;

    if (value != null) {
      aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
      if (aUserObject instanceof DataTreeNode) {             

    	  final DataTreeNode t = (DataTreeNode)aUserObject;
    	  final Versioning v = (Versioning)t.getRockDBObject();
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
              
        
       if (v.getEniq_level()== null || v.getEniq_level().equalsIgnoreCase("1.0")){
         setForeground(Color.LIGHT_GRAY);
         toolTip += " Version too old";
       }
          
       if (!noBaseTypesFiltering && !nobasetypeslist.contains(v.getTechpack_type())){
           setForeground(Color.LIGHT_GRAY);
           toolTip += " No BaseTP detected, cannot select this TP.";
       }

        
        setToolTipText(toolTip);
        
      }
    }
    
     return this;

   }

  
}
