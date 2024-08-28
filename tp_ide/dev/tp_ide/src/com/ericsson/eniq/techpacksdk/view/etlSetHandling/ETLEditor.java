package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.Component;

import javax.swing.JTree;

import tableTree.TTEditor;

/**
 * Concrete class for tree cell editors. This really doesn't do anything and is superfluous,
 * but included just to show that this type of classes can be implemented.
 * @author enaland ejeahei
 *
 */
public class ETLEditor extends TTEditor{

	public ETLEditor (JTree tree)
	{
		super (tree);
	}
	
	public Component getTreeCellEditorComponent(JTree tree,
			Object value,
			boolean isSelected,
			boolean expanded,
			boolean leaf,
			int row)
	{
		return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
	}
}
