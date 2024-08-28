package unitTests;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import junit.framework.TestCase;

import measurementType.MeasurementTypeFactory;

import org.junit.Test;
import org.junit.Before;

import tableTree.TTEditor;
import tableTreeUtils.TreeMainNode;

public class MeasurementTypeFactoryTest extends TestCase {

    private MeasurementTypeFactory myFactory = null;
    private JTree myTree = null;

    @Before
    public void setUp() {
	myFactory = new MeasurementTypeFactory(true, null);
	assertNotNull(myFactory);
    }

    @Test
    public void testCreateAndGetModel() {
	TreeModel newModel = myFactory.createAndGetModel();
	myTree = new JTree(newModel);
	assertNotNull("Model exists", newModel);

	DefaultMutableTreeNode root = (DefaultMutableTreeNode) newModel
		.getRoot();
	assertEquals("All types added", root.getChildCount(),
		MeasurementTypeFactory.N_OF_TYPES);
    }

    @Test
    public void testCreateAndGetModelEmpty() {
	TreeModel newModel = myFactory.createAndGetModel();
	assertNotNull("Model exists", newModel);

	DefaultMutableTreeNode root = (DefaultMutableTreeNode) newModel
		.getRoot();
	assertEquals("All types added", root.getChildCount(),
		MeasurementTypeFactory.N_OF_TYPES);
    }

    @Test
    public void testGetTreeCellEditor() {
	TTEditor editor = myFactory.getTreeCellEditor(myTree);
	assertNotNull("TreeCellEditor exists", editor);
    }

    @Test
    public void testCreateEmptyNode() {
	TreeMainNode aNode = myFactory.createEmptyNode();
	assertNotNull("Empty node exists", aNode);
    }

    /*
     * @Test public void testCreateTableNode() { fail("Not yet implemented"); }
     * 
     * @Test public void testCreateParameterNode() { fail("Not yet
     * implemented"); }
     * 
     * @Test public void testCreateMainNode() { fail("Not yet implemented"); }
     */
}
