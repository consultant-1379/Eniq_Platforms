/**
 * 
 */
package ttctests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import junit.framework.TestCase;
import measurementType.MeasurementTypeFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import tableTree.TableTreeComponent;
import tableTreeUtils.TreeMainNode;
import tableTreeUtils.TreePopupMenuListener;
import tableTreeUtils.TreesInTreeListener;

/**
 * @author eheitur
 * 
 */
public class TableTreeComponentTest extends TestCase{

    private MeasurementTypeFactory myFactory = null;
    private TableTreeComponent myTTC = null;
    private boolean isTreeEditable = true;

    @Before
    public void setUp() {
	myFactory = new MeasurementTypeFactory(true, null);
	assertNotNull(myFactory);

	isTreeEditable = true;
	myFactory = new MeasurementTypeFactory(isTreeEditable, null);

	// Create a TTC without any document listener
	myTTC = new TableTreeComponent(myFactory, null);
	assertNotNull(myTTC);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link tableTree.TableTreeComponent#setRootVisible(boolean)}.
     */
    @Test
    public final void testSetRootVisible() {
	myTTC.setRootVisible(false);
	assertFalse("The root should not be visible.", myTTC.isRootVisible());
	myTTC.setRootVisible(true);
	assertTrue("The root should be visible.", myTTC.isRootVisible());
    }

    /**
     * Test method for creating a TTC with a factory (only)
     * {@link tableTree.TableTreeComponent#TableTreeComponent(tableTree.TreeDataFactory)}.
     */
    @Test
    public final void testTableTreeComponentTreeDataFactory() {
	TableTreeComponent ttc = new TableTreeComponent(
		new MeasurementTypeFactory(true, null));
	assertNotNull("The factory should not be null", ttc);
    }

    /**
     * Test method for creating a TTC with a factory and a listener
     * {@link tableTree.TableTreeComponent#TableTreeComponent(tableTree.TreeDataFactory, testTreesInTree.CommonTreeModelListener)}.
     */
    @Test
    public final void testTableTreeComponentTreeDataFactoryCommonTreeModelListener() {
	JTree tree = new JTree();
	TreesInTreeListener listener = new TreesInTreeListener(tree);
	TableTreeComponent ttc = new TableTreeComponent(
		new MeasurementTypeFactory(true, null), listener);
	assertNotNull("The factory should not be null", ttc);
    }

    /**
     * Test method for {@link tableTree.TableTreeComponent#getFactory()}.
     */
    @Test
    public final void testGetFactory() {
	assertTrue("The Factory should be of type MeasurementTypeFactory", (myTTC.getFactory() instanceof MeasurementTypeFactory));
    }

//    /**
//     * Test method for {@link tableTree.TableTreeComponent#saveChanges()}.
//     */
//    @Test
//    public final void testSaveChanges() {
//	myTTC.saveChanges();
//    }

//    /**
//     * Test method for {@link tableTree.TableTreeComponent#discardChanges()}.
//     */
//    @Ignore
//    public final void testDiscardChanges() {
//	//fail("Not yet implemented");
//    }
//
    /**
     * Test method for {@link tableTree.TableTreeComponent#getAllData()}.
     */
    @Test
    public final void testGetAllData() {
	Object retObj = myTTC.getAllData();
	assertTrue("A vector should be returned", retObj instanceof Vector);
	if(retObj instanceof Vector){ // The former assert passed
	    assertTrue("The Vector size should equal the amount of MainTreeNodes in the Tree",((Vector)retObj).size() == ((DefaultMutableTreeNode)myTTC.getModel().getRoot()).getChildCount());
	    assertTrue("The Vector should contain Vectors as its data elements",((Vector)retObj).firstElement() instanceof Vector);
	}
    }

    /**
     * Test method for {@link tableTree.TableTreeComponent#getDeletedData()}.
     */
    @Test
    public final void testGetDeletedData() {
	TreeNode thisNode = ((DefaultMutableTreeNode)myTTC.getModel().getRoot()).getFirstChild();
	assertTrue("The roots first child should be of type TreeMainNode", thisNode instanceof TreeMainNode);
	if(thisNode instanceof TreeMainNode){ // The previous assert passed
	    JPopupMenu popupMenu = myTTC.getFactory().nodeRightClickMenu((TreeMainNode)thisNode);
	    ActionEvent ae = new ActionEvent("hello", 1, TreePopupMenuListener.REMOVE_NODE_NO_CONFIRM);

	    ActionListener[] listeners = ((JMenuItem)popupMenu.getComponent(0)).getActionListeners();

	    for(int i=0; i<listeners.length;i++){
		listeners[i].actionPerformed(ae);
	    }
	    
	    Object retObj = myTTC.getDeletedData();
	    assertTrue("A vector should be returned", retObj instanceof Vector);
	    if(retObj instanceof Vector){ // The previous assert passed
		assertTrue("The Vector size should equal 1 as we have removed one element",((Vector)retObj).size() == 1);
		assertTrue("The Vector should contain Vectors as its data elements",((Vector)retObj).firstElement() instanceof Vector);
	    }
	}

    }
    
   /**
     * Test method for {@link tableTree.TableTreeComponent#getDeletedData()}.
     */
    @Test
    public final void testRemoveMultipleNodes() {
    final Vector<DefaultMutableTreeNode> selectedNodes = new Vector<DefaultMutableTreeNode>();
    boolean nodesInstanceOfTreeMainNode = true;
    
    Enumeration children = ((DefaultMutableTreeNode)myTTC.getModel().getRoot()).children();
    while(children.hasMoreElements()){
    	TreeNode currentChild = (DefaultMutableTreeNode) children.nextElement();
    	if (!(currentChild instanceof TreeMainNode)){
       		nodesInstanceOfTreeMainNode = false;
    	}
    	assertTrue("The roots first child should be of type TreeMainNode", currentChild instanceof TreeMainNode);
    	selectedNodes.add((DefaultMutableTreeNode) currentChild);
    }
    	
	if(nodesInstanceOfTreeMainNode){ // The previous assert passed
	    JPopupMenu popupMenu = myTTC.getFactory().nodesRightClickMenu(selectedNodes);
	    ActionEvent ae = new ActionEvent("hello", 1, TreePopupMenuListener.REMOVE_NODES_NO_CONFIRM);

	    ActionListener[] listeners = ((JMenuItem)popupMenu.getComponent(0)).getActionListeners();

	    for(int i=0; i<listeners.length;i++){
		listeners[i].actionPerformed(ae);
	    }
	    
	    Object retObj = myTTC.getDeletedData();
	    assertTrue("A vector should be returned", retObj instanceof Vector);
	    if(retObj instanceof Vector){ // The previous assert passed
		assertTrue("The Vector size should equal 3 as we have removed three elements",((Vector)retObj).size() == 3);
		assertTrue("The Vector should contain Vectors as its data elements",((Vector)retObj).firstElement() instanceof Vector);
	    }
	}
  }

//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#updateTree(java.awt.Point)}.
//     */
//    @Ignore
//    public final void testUpdateTree() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#markForDeletion(tableTreeUtils.TreeMainNode)}.
//     */
//    @Ignore
//    public final void testMarkForDeletion() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#markAsNewAndUnsaved(tableTreeUtils.TreeMainNode)}.
//     */
//    @Ignore
//    public final void testMarkAsNewAndUnsaved() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for {@link tableTree.TableTreeComponent#setModelForTree()}.
//     */
//    @Ignore
//    public final void testSetModelForTree() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#collectTableContainers()}.
//     */
//    @Ignore
//    public final void testCollectTableContainers() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#scrollPathToVisible(javax.swing.tree.TreePath)}.
//     */
//    @Ignore
//    public final void testScrollPathToVisibleTreePath() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#expandAll(javax.swing.JTree, boolean)}.
//     */
//    @Ignore
//    public final void testExpandAllJTreeBoolean() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#expandAll(javax.swing.JTree, javax.swing.tree.TreePath, boolean)}.
//     */
//    @Ignore
//    public final void testExpandAllJTreeTreePathBoolean() {
//	//fail("Not yet implemented");
//    }
//
//    /**
//     * Test method for
//     * {@link tableTree.TableTreeComponent#update(java.util.Observable, java.lang.Object)}.
//     */
//    @Ignore
//    public final void testUpdateObservableObject() {
//	//fail("Not yet implemented");
//    }
//
    /**
     * Test method for GetDocumentListsner, addDocumentListener and removeDocumentListener methods
     * {@link tableTree.TableTreeComponent#addDocumentListener(javax.swing.event.DocumentListener)}.
     * {@link tableTree.TableTreeComponent#removeDocumentListener(javax.swing.event.DocumentListener)}.
     * {@link tableTree.TableTreeComponent#getDocumentListeners()}.
     */
    @Test
    public final void testGetAddRemoveDocumentListener() {

	// Check that there are no MyDocumetnListeners for myTTC
	Vector<DocumentListener> listeners = myTTC.getDocumentListeners();
	assertEquals("There should be no DocumentListeners for the TTC", listeners.size(), 0);	
	
	// Add a listener
	MyDocumentListener docList = new MyDocumentListener();
	myTTC.addDocumentListener(docList);

	MyDocumentListener docList2 = new MyDocumentListener();
	myTTC.addDocumentListener(docList2);

	// Check that there are two listeners
	listeners = myTTC.getDocumentListeners();
	assertEquals("There should be two DocumentListener for the TTC", listeners.size(), 2);
	
	// Remove the first listener
	myTTC.removeDocumentListener(docList);

	// Check that there is only one listeners
	listeners = myTTC.getDocumentListeners();
	assertEquals("There should be one DocumentListener for the TTC", listeners.size(), 1);

	// Remove the second listener
	myTTC.removeDocumentListener(docList2);

	// Check that there is no listeners
	listeners = myTTC.getDocumentListeners();
	assertEquals("All DocumentListeners for the TTC should have been removed", listeners.size(), 0);
    }

    /**
     * Test method for
     * {@link tableTree.TableTreeComponent#fireTableTreeComponentDataChanged()}.
     */
    @Test
    public final void testFireTableTreeComponentDataChanged() {
	//fail("Not yet implemented");
    }

    /**
     * Temporary class for implementing a document listener for monitoring the
     * changes in the table tree component.
     * 
     * @author eheitur
     * 
     */
    class MyDocumentListener implements DocumentListener {
	String newline = "\n";

	public void insertUpdate(DocumentEvent e) {
	    updateLog(e, "inserted into");
	}

	public void removeUpdate(DocumentEvent e) {
	    updateLog(e, "removed from");
	}

	public void changedUpdate(DocumentEvent e) {
	    // Plain text components do not fire these events
	}

	public void updateLog(DocumentEvent e, String action) {
	    Document doc = (Document) e.getDocument();
	    int changeLength = e.getLength();
	    System.out.println((changeLength + " character"
		    + ((changeLength == 1) ? " " : "s ") + action
		    + doc.getProperty("name") + "." + newline
		    + "  Text length = " + doc.getLength() + newline));
	}
    }

}
