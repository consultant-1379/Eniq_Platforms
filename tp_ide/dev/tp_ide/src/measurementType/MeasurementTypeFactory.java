package measurementType;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;


import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTEditor;
import tableTree.TTParameterModel;
import tableTree.TTTableModel;
import tableTree.TreeDataFactory;
import tableTreeUtils.TreeMainNode;

/**
 * Concrete factory class for MeasurementTypes.
 * 
 * @author enaland ejeahei eheitur
 * 
 */
public class MeasurementTypeFactory extends TreeDataFactory {

    /**
     * Number of measurement types
     */
    public static int N_OF_TYPES = 3;
    /**
     * Number of counters in the counter table
     */
    public static int N_OF_COUNTERS = 25;

    /**
     * Number of keys in the key table
     */
    public static int N_OF_KEYS = 15;

    /**
     * The rock factory is the interface towards the DB.
     */
    private RockFactory rockFactory = null;
    
    private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";

    /**
     * The constructor initiates the renderer and DB interface for this specific
     * type of dialogue.
     * 
     * @param editable
     *            true in case the tree is editable
     * @param forcedNamePrefix
     *            the forced prefix for the tree main node names.
     */
    public MeasurementTypeFactory(boolean editable, String forcedNamePrefix) {
	this.editable = editable;
	this.setForcedNamePrefix(forcedNamePrefix);

	// Example for setting the maximum tree node name length.
	// In practice, this will depend on the length of techpack name and
	// revision.
	this.setMaximumTreeNodeNameLength(30);

	treeCellRenderer = new MeasurementTypeRenderer();

	createTableInfoData(); // Collect info about the different table types
	// used

	try {
		rockFactory = new RockFactory(DWHREP_URL, USERNAME, PASSWORD, TESTDB_DRIVER, "test", true);
	} catch (RockException e) {
	    e.printStackTrace();
	}catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Create a tree cell editor for the specific type of dialogue
     * (MeasurementType).
     * 
     * @param theTree
     * @return treeCellEditor.
     */
    public TTEditor getTreeCellEditor(JTree theTree) {
	if (treeCellEditor == null) {
	    treeCellEditor = new MeasurementTypeEditor(theTree);
	}
	return treeCellEditor;
    }

    /**
     * Create the tree model for the tree and initiate it with data from the
     * database.
     * 
     * @return treeModel
     */
    public TreeModel createAndGetModel() {
	Vector<Measurementtype> types = null;

	// Create the root node
	DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
	DefaultTreeModel theModel = new DefaultTreeModel(root);

	// This pretends to query the database for the types
	types = getAllTypes();

	// Loop through the measurement types, get their data and add them to
	// the model
	for (int i = 0; i < types.size(); i++) {

	    TreeMainNode mainNode = createMeasurementype(types.elementAt(i));
	    root.add(mainNode);
	}
	return theModel;
    }

    /**
     * Method for collecting info of the different table types
     */
    protected void createTableInfoData() {
	tableInformations.addElement(MeasurementTypeKeyTableModel
		.createTableTypeInfo());
	tableInformations.addElement(MeasurementTypeCounterTableModel
		.createTableTypeInfo());
    }

    /**
     * Helper method for creating the measurement type node and all its children
     * 
     * @param measurementtype
     * @return
     */
    private TreeMainNode createMeasurementype(Measurementtype measurementtype) {
	Measurementtable measurementTable = null;
	Vector<Object> myKeys = null;
	Vector<Object> myCounters = null;
	TTParameterModel parameterModel = null;
	TTTableModel keyTableModel = null;
	TTTableModel counterTableModel = null;

	if (measurementtype != null) {
	    measurementTable = getTableFor(measurementtype);

	    // Create the child node data for all three nodes: parameters, keys
	    // and
	    // counters
	    parameterModel = new MeasurementTypeParameterModel(measurementtype,
		    measurementTable, rockFactory, editable);
	    myKeys = getAllKeys(measurementtype);
	    myCounters = getAllCounters(measurementtype);
	} else {
	    // Create new type and table objects
	    measurementtype = new Measurementtype(rockFactory);
	    measurementTable = new Measurementtable(rockFactory);

	    // Set the ID to something that looks empty
	    measurementtype.setTypeid("New Measurementtype");
	    measurementTable.setTypeid(measurementtype.getTypeid());

	    // Create the empty data sets for the child nodes
	    parameterModel = new MeasurementTypeParameterModel(measurementtype,
		    measurementTable, rockFactory, editable);
	    myKeys = new Vector<Object>();
	    myCounters = new Vector<Object>();
	}
	keyTableModel = new MeasurementTypeKeyTableModel(rockFactory,
		tableInformations, editable);
	counterTableModel = new MeasurementTypeCounterTableModel(rockFactory,
		tableInformations, editable);

	// Set the observer/observable relationships
	parameterModel.addObserver(keyTableModel);
	parameterModel.addObserver(counterTableModel);

	// Create the main node
	TreeMainNode mainNode = new TreeMainNode(parameterModel);

	// Create the child nodes
	DefaultMutableTreeNode parameterNode = createParameterNode(parameterModel);
	DefaultMutableTreeNode keyNode = createTableNode(myKeys, keyTableModel);
	DefaultMutableTreeNode counterNode = createTableNode(myCounters,
		counterTableModel);

	// Connect the nodes
	mainNode.add(parameterNode);
	mainNode.add(keyNode);
	mainNode.add(counterNode);

	// Set the deletion order of the nodes
	Vector<DefaultMutableTreeNode> nodeOrder = new Vector<DefaultMutableTreeNode>();
	nodeOrder.addElement(keyNode);
	nodeOrder.addElement(counterNode);
	nodeOrder.addElement(parameterNode);

	setDeletionOrder(mainNode, nodeOrder);

	return mainNode;
    }

    /**
     * Set deletion order, e.g. tables have to be deleted before the parameter
     * panel due to references in the data. Nodes are deleted in the order that
     * they are in the vector sent to mainNode.setDeletionOrder(vector). Nodes
     * that aren't included in the vector will be deleted last
     */
    private void setDeletionOrder(TreeMainNode parentNode,
	    Vector<DefaultMutableTreeNode> nodes) {
	parentNode.setDeletionOrder(nodes);
    }

    /**
     * Overridden version of the corresponding TreeDataFactory method. Used for
     * creating new empty nodes when the user inserts nodes in a tree.
     * 
     * @return the new tree main node
     */
    public TreeMainNode createEmptyNode() {
	return createMeasurementype(null);
    }

    /**
     * Overridden version of the corresponding TreeDataFactory method. Used for
     * copying data from an exiting node to a new empty node when the user
     * duplicates a node in a tree.
     * 
     * @param existingNode
     * @param newNode
     */
    @Override
    public void duplicateExistingNode(TreeMainNode existingNode,
	    TreeMainNode newNode) {

	// TODO: Implement a sample duplicate
	System.out.println("duplicateExistingNode(): Not implemented yet. "
		+ "Data is not copied to the new empty node.");
	JOptionPane
		.showMessageDialog(null,
			"Not implemented yet! Data is not copied to the new empty node.");

    }

    /**
     * Dummy implementation, returns a fake measurement table for the given
     * measurement type
     * 
     * @param parentType
     *            the parent of the keys
     * @return a fake measurement table
     */
    private Measurementtable getTableFor(Measurementtype parenttype) {

	return new Measurementtable(rockFactory);
    }

    /**
     * Dummy implementation, returns a list of fake measurement keys
     * 
     * @param parentType
     *            the parent of the keys
     * @return theKeys a list of measurement keys
     */
    public Vector<Object> getAllKeys(Measurementtype parentType) {
	int nOfKeys = N_OF_KEYS;

	Vector<Object> theKeys = new Vector<Object>();
	for (int i = 0; i < nOfKeys; i++) {
	    theKeys.add(new Measurementkey(rockFactory));
	}

	return theKeys;
    }

    /**
     * Dummy implementation, returns a list of fake measurement counters
     * 
     * @param measurementtype
     *            the parent of the counters
     * @return theCounters a list of measurement counters
     */
    private Vector<Object> getAllCounters(Measurementtype measurementtype) {
	int nOfCounters = N_OF_COUNTERS;

	Vector<Object> theCounters = new Vector<Object>();
	for (int i = 0; i < nOfCounters; i++) {
	    theCounters.add(new Measurementcounter(rockFactory));
	}

	return theCounters;
    }

    /**
     * Dummy implementation, returns a list of fake measurement types
     * 
     * @return theTypes a list of measurement types
     */
    public Vector<Measurementtype> getAllTypes() {
	int nOfTypes = N_OF_TYPES;

	Vector<Measurementtype> theTypes = new Vector<Measurementtype>();
	for (int i = 0; i < nOfTypes; i++) {
	    theTypes.add(new Measurementtype(rockFactory));
	}

	return theTypes;
    }
}
