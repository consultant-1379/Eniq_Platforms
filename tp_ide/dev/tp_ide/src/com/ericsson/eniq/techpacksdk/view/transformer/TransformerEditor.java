package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;

import javax.swing.JTree;

import tableTree.TTEditor;

/**
 * Concrete class for tree cell editors. This really doesn't do anything and is superfluous,
 * but included just to show that this type of classes can be implemented.
 * @author enaland ejeahei
 *
 */
public class TransformerEditor extends TTEditor{

	public TransformerEditor (JTree tree)
	{
		super (tree);
	}
	
	public Component getTreeCellEditorComponent(final JTree tree,
			final Object value,
			final boolean isSelected,
			final boolean expanded,
			final boolean leaf,
			final int row)
	{
		return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
	}
}
