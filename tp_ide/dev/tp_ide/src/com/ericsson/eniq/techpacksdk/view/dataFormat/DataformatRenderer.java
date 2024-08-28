package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.awt.Component;

import javax.swing.JTree;

import tableTree.TTRenderer;

/**
 * Concrete class for tree cell renderers. This really doesn't do anything and is superfluous,
 * but included just to show that this type of classes can be implemented.
 * @author enaland ejeahei
 *
 */public class DataformatRenderer extends TTRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

}
