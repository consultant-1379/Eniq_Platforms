package com.ericsson.eniq.techpacksdk;


import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ericsson.eniq.component.DataTreeNode;

@SuppressWarnings("serial")
public class DWHTreeRenderer extends DefaultTreeCellRenderer {

  ImageIcon tutorialIcon;
  
  public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {

   super.getTreeCellRendererComponent(tree, value, sel,expanded, leaf, row, hasFocus);
  
   Object aUserObject = null;

   if (value != null) {
     aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
     if (aUserObject instanceof DataTreeNode) {             

       DataTreeNode t = (DataTreeNode)aUserObject;
       
       String toolTip = " ";
                     
       if (t.active){
                 
         toolTip += "ACTIVE ";
         setIcon(tutorialIcon); 
         setForeground(Color.GREEN);
          
       } else {
         toolTip += "INACTIVE ";
         setForeground(Color.RED);
         setIcon(tutorialIcon);
       }
           
       setToolTipText(toolTip);
       
     }
   }
   
    return this;

  }
}
