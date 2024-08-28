package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;

import tableTree.TableTreeComponent;

public class TreeEditor extends DefaultTreeCellEditor implements TreeCellEditor {

    public TreeEditor(JTree inTree){
	super(inTree, new DefaultTreeCellRenderer());
    }
    
    
    public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {

	DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
	Object obj = node.getUserObject();
	TableTreeComponent retTree;

	if(obj instanceof TableTreeComponent){
	    retTree = (TableTreeComponent)obj;
	    return retTree;
	}
	else {
	    return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
	}
    }

}
