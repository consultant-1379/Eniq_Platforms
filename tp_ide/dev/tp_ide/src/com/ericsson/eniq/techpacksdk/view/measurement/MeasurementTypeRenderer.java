package com.ericsson.eniq.techpacksdk.view.measurement;

import java.awt.Component;

import javax.swing.JTree;

import tableTree.TTRenderer;

/**
 * Concrete class for tree cell renderers. This really doesn't do anything and is superfluous,
 * but included just to show that this type of classes can be implemented.
 * @author enaland ejeahei
 *
 */public class MeasurementTypeRenderer extends TTRenderer {

	public Component getTreeCellRendererComponent(final JTree tree, final Object value,
	    final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) 
	{
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

}
