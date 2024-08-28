package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import tableTree.TableTreeComponent;

public class TreeRenderer implements TreeCellRenderer {

    	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		Object obj = node.getUserObject();
		//JLabel retLab;
		TableTreeComponent retTree;
		
		if(obj instanceof TableTreeComponent){
			retTree = (TableTreeComponent)obj;
			return retTree;
		}
		else{
			//retLab = new JLabel((String)obj);
			//return retLab;
		    	return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
	
	}

}
