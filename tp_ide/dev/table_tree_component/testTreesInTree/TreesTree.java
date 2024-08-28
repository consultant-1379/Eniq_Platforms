package testTreesInTree;

import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tableTreeUtils.TreesInTreeListener;

/**
 * An example of the main tree of the TreesInTree demo.
 * 
 * @author enaland
 * 
 */
public class TreesTree extends JTree implements DocumentListener {

    private static final long serialVersionUID = 3759764509914294337L;
    private TreesInTreeListener listener = null;
    
    /**
     * Constructor.
     */
    public TreesTree() {
	super();
	// Always needs to be true
	this.setEditable(true);

	// Remove some of the auto scrolling in the tree
	this.setAutoscrolls(false);
	this.setScrollsOnExpand(false);

	// Double-click should edit, not expand
	this.setToggleClickCount(0);

	listener = new TreesInTreeListener(this);

	this.setModel(new MainTreeModel(this, listener, this));
	this.addTreeExpansionListener(listener);
	this.setEditable(true);
	this.setCellRenderer(new TreeRenderer());
	this.setCellEditor(new TreeEditor(this));

	setRowHeight(0);
    }

    /**
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent arg0) {
	// This changed event can be used to e.g. enabling the save button when
	// something has changed in the tableTreeComponent.
	System.out.println("TreesTree: changedUpdate event from the listener.");
    }

    /**
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent arg0) {
	// No action
    }

    /**
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent arg0) {
	// No action
    }

}
