package ttctests;

import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import junit.framework.TestCase;

import measurementType.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import measurementType.Measurementcounter;
import ssc.rockfactory.RockFactory;
import tableTree.DescriptionCellEditor;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TableInformation;

//import com.sun.net.httpserver.Authenticator.Success;

public class MeasurementTypeCounterTableModelTest extends TestCase {

	private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
    private Vector<Object> data = null;
    private stubbedMeasurementcounter counter1 = null;
    private stubbedMeasurementcounter counter2 = null;
    private stubbedMeasurementcounter counter3 = null;
    private stubbedMeasurementcounter counter4 = null;
    private stubbedMeasurementcounter counter5 = null;
    private stubbedMeasurementcounter newCounter = null;
    private RockFactory rockFactory = null;
    private MeasurementTypeCounterTableModel modelUnderTest = null;

    @Before
    public void setUp() throws Exception {
	rockFactory = new RockFactory(DWHREP_URL, USERNAME, PASSWORD, TESTDB_DRIVER, "test", true);
	
	//System.out.println("WOOOHOOOO");
	// Create the table informations vector for the model
	Vector<TableInformation> tableInformations = new Vector<TableInformation>();
	tableInformations.addElement(MeasurementTypeCounterTableModel.createTableTypeInfo());
	
	// Create the model under test
	modelUnderTest = new MeasurementTypeCounterTableModel(rockFactory,
		tableInformations, true); 

	
	
	data = new Vector<Object>();

	counter1 = new stubbedMeasurementcounter(rockFactory);
	counter1.setDataname("Counter1");
	counter1.setDescription("Description 1");
	counter1.setTypeid("Type for counter 1");

	counter2 = new stubbedMeasurementcounter(rockFactory);
	counter2.setDataname("Counter2");
	counter2.setDescription("Description 2");
	counter2.setTypeid("Type for counter 2");

	counter3 = new stubbedMeasurementcounter(rockFactory);
	counter3.setDataname("Counter3");
	counter3.setDescription("Description 3");
	counter3.setTypeid("Type for counter 3");

	counter4 = new stubbedMeasurementcounter(rockFactory);
	counter4.setDataname("Counter4");
	counter4.setDescription("Description 4");
	counter4.setTypeid("Type for counter 4");

	counter5 = new stubbedMeasurementcounter(rockFactory);
	counter5.setDataname("Counter5");
	counter5.setDescription("Description 5");
	counter5.setTypeid("Type for counter 5");

	newCounter = new stubbedMeasurementcounter(rockFactory);
	newCounter.setDataname("NewCounter");
	newCounter.setDescription("New Description");
	newCounter.setTypeid("Type for new counter");

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetData() {
	try {
	    modelUnderTest.setData(data);
	} catch (Exception e) {
	    fail("Setting empty data fails");
	}
    }

    @Test
    public void testGetValueAtEmpty() {
	data.clear();
	modelUnderTest.setData(data);
	try {
	    modelUnderTest.getValueAt(0, 0);
	    fail("Get value at for empty data should throw exception");
	} catch (Exception e) {

	}
    }

    @Test
    public void testGetValueAtOne() {
	data.clear();
	data.addElement(counter1);
	modelUnderTest.setData(data);
	assertTrue(counter1.getDataname().equals(
		modelUnderTest.getValueAt(0, 0)));
	assertTrue(counter1.getDescription().equals(
		modelUnderTest.getValueAt(0, 1)));
	assertTrue(counter1.getTimeaggregation().equals(
		modelUnderTest.getValueAt(0, 2)));
	assertTrue(counter1.getGroupaggregation().equals(
		modelUnderTest.getValueAt(0, 3)));
	assertTrue(counter1.getCountaggregation().equals(
		modelUnderTest.getValueAt(0, 4)));
    }

    @Test
    public void testGetValueAt() {
	addData();
	assertTrue(counter1.getDataname().equals(
		modelUnderTest.getValueAt(0, 0)));
	assertTrue(counter2.getDataname().equals(
		modelUnderTest.getValueAt(1, 0)));
	assertTrue(counter3.getDataname().equals(
		modelUnderTest.getValueAt(2, 0)));
	assertTrue(counter4.getDataname().equals(
		modelUnderTest.getValueAt(3, 0)));
	assertTrue(counter5.getDataname().equals(
		modelUnderTest.getValueAt(4, 0)));
    }

    @Test
    public void testSetValueAtEmpty() {
	data.clear();
	modelUnderTest.setData(data);
	try {
	    modelUnderTest.setValueAt("NewValue", 0, 0);
	    fail("Set value at for empty data should throw exception");
	} catch (Exception e) {

	}
    }

    @Test
    public void testSetValueAtOne() {
	data.clear();
	data.addElement(counter1);
	modelUnderTest.setData(data);

	String newValue0 = "123";
	String newValue1 = "456";
	String newValue2 = "789";
	String newValue3 = "876";
	String newValue4 = "543";

	modelUnderTest.setValueAt(newValue0, 0, 0);
	modelUnderTest.setValueAt(newValue1, 0, 1);
	modelUnderTest.setValueAt(newValue2, 0, 2);
	modelUnderTest.setValueAt(newValue3, 0, 3);
	modelUnderTest.setValueAt(newValue4, 0, 4);

	assertTrue(newValue0.equals(counter1.getDataname()));
	assertTrue(newValue1.equals(counter1.getDescription()));
	assertTrue(newValue2.equals(counter1.getTimeaggregation()));
	assertTrue(newValue3.equals(counter1.getGroupaggregation()));
	assertTrue(newValue4.equals(counter1.getCountaggregation()));
    }

    @Test
    public void testSetValueAt() {
	addData();

	String newValue1 = "abc";
	String newValue2 = "def";
	String newValue3 = "ghi";
	String newValue4 = "jkl";
	String newValue5 = "mno";

	modelUnderTest.setValueAt(newValue1, 0, 0);
	modelUnderTest.setValueAt(newValue2, 1, 0);
	modelUnderTest.setValueAt(newValue3, 2, 0);
	modelUnderTest.setValueAt(newValue4, 3, 0);
	modelUnderTest.setValueAt(newValue5, 4, 0);

	assertTrue(counter1.getDataname().equals(newValue1));
	assertTrue(counter2.getDataname().equals(newValue2));
	assertTrue(counter3.getDataname().equals(newValue3));
	assertTrue(counter4.getDataname().equals(newValue4));
	assertTrue(counter5.getDataname().equals(newValue5));
    }

    /**
     * 
     */
    private void addData() {
	data.clear();
	data.addElement(counter1);
	data.addElement(counter2);
	data.addElement(counter3);
	data.addElement(counter4);
	data.addElement(counter5);

	modelUnderTest.setData(data);
    }

    @Test
    public void testSetColumnEditors() {
	addData();
	JTable theTable = new JTable(modelUnderTest);
	modelUnderTest.setColumnEditors(theTable);
	TableColumn descriptionColumn = theTable.getColumnModel().getColumn(1);
	assertTrue(descriptionColumn.getCellEditor() instanceof DescriptionCellEditor);
    }

    // @Test
    // public void testSetColumnRenderers() {
    // }

    @Test
    public void testCreateNew() {
	assertTrue(modelUnderTest.createNew() instanceof Measurementcounter);
    }

    @Test
    public void testIsCellEditable() {
	assertTrue(modelUnderTest.isCellEditable(0, 0));
	assertTrue(modelUnderTest.isCellEditable(0, 1));
	assertTrue(modelUnderTest.isCellEditable(0, 2));
	assertTrue(modelUnderTest.isCellEditable(0, 3));
	assertTrue(modelUnderTest.isCellEditable(0, 4));
	assertTrue(modelUnderTest.isCellEditable(0, 5));
    }

    @Test
    public void testGetColumnCount() {
	assertTrue(modelUnderTest.getColumnCount() == 6);
    }

    @Test
    public void testGetRowCountNone() {
	data.clear();
	modelUnderTest.setData(data);
	assertTrue(modelUnderTest.getRowCount() == 0);
    }

    public void testGetRowCount() {
	data.clear();
	addData();
	modelUnderTest.setData(data);
	assertTrue(modelUnderTest.getRowCount() == 5);
    }

    @Test
    public void testGetTableName() {
	assertTrue("Counters".equals(modelUnderTest.getTableName()));
    }

    @Test
    public void testSetTableName() {
	String newName = "MyCounters";
	modelUnderTest.setTableName(newName);
	assertTrue(newName.equals(modelUnderTest.getTableName()));
    }

    @Test
    public void testInsertDataAtRow() {
	addData();

	int sizeBefore = modelUnderTest.getRowCount();
	int index = sizeBefore / 2;
	modelUnderTest.insertDataAtRow(newCounter, index);
	assertTrue("Row count should increase by 1", modelUnderTest
		.getRowCount() == sizeBefore + 1);
	assertTrue("The element should have been inserted at the given point",
		modelUnderTest.getValueAt(index, 0).equals(
			newCounter.getDataname()));
    }

    @Test
    public void testInsertDataLast() {
	addData();

	int sizeBefore = modelUnderTest.getRowCount();
	int index = sizeBefore;
	modelUnderTest.insertDataLast(newCounter);
	assertTrue("Row count should increase by 1", modelUnderTest
		.getRowCount() == sizeBefore + 1);
	assertTrue("The element should have been inserted last", modelUnderTest
		.getValueAt(index, 0).equals(newCounter.getDataname()));
    }

    /* 20110628 eanguan :: Closing these tests. 
     * Set up needs to be done for these to run correctly.
     * 
    @Test
    public void testRemoveSelectedData() {
	addData();
	int sizeBefore = modelUnderTest.getRowCount();
	int[] indeces = { 1, 3 };
	Object mc1 = data.elementAt(1);
	Object mc3 = data.elementAt(3);

	modelUnderTest.removeSelectedData(indeces);

	assertTrue("Row count should decrease by 2", modelUnderTest
		.getRowCount() == sizeBefore - 2);
	assertFalse("The data should not contain the removed counter", data
		.contains(mc1));
	assertFalse("The data should not contain the removed counter", data
		.contains(mc3));
    }

    @Test
    public void testMoveRowsUp() {
	addData();
	int sizeBefore = modelUnderTest.getRowCount();
	int[] indeces = { 1, 3 };
	Object mc1 = data.elementAt(1);
	Object mc3 = data.elementAt(3);

	// Move rows up. Note: the updated indeces are returned.
	indeces = modelUnderTest.moveRowsUp(indeces);

	assertTrue("Row count should stay the same", modelUnderTest
		.getRowCount() == sizeBefore);
	assertTrue("The row should have moved up one row", modelUnderTest
		.getValueAt(indeces[0], 0).equals(
			((Measurementcounter) mc1).getDataname()));
	assertTrue("The row should have moved up one row", modelUnderTest
		.getValueAt(indeces[1], 0).equals(
			((Measurementcounter) mc3).getDataname()));
    }

    @Test
    public void testMoveRowsDown() {
	addData();
	int sizeBefore = modelUnderTest.getRowCount();
	int[] indeces = { 1, 3 };
	Object mc1 = data.elementAt(1);
	Object mc3 = data.elementAt(3);

	// Move rows down. Note: the updated indeces are returned.
	indeces = modelUnderTest.moveRowsDown(indeces);

	assertTrue("Row count should stay the same", modelUnderTest
		.getRowCount() == sizeBefore);
	assertTrue("The row should have moved down one row", modelUnderTest
		.getValueAt(indeces[0], 0).equals(
			((Measurementcounter) mc1).getDataname()));
	assertTrue("The row should have moved down one row", modelUnderTest
		.getValueAt(indeces[1], 0).equals(
			((Measurementcounter) mc3).getDataname()));
    }

    @Test
    public void testSaveChanges() {
	addData();
	int index = 1;
	((stubbedMeasurementcounter) data.elementAt(index)).isSaved = false;

	modelUnderTest.saveChanges();

	assertTrue("Element should be saved", ((stubbedMeasurementcounter) data
		.elementAt(index)).isSaved);
    }

    @Test
    public void testRemoveFromDB() {
	addData();
	int index = 1;
	((stubbedMeasurementcounter) data.elementAt(index)).isDeleted = false;

	modelUnderTest.removeFromDB();

	assertTrue("Element should be removed",
		((stubbedMeasurementcounter) data.elementAt(index)).isDeleted);
    }
    */

    @Test
    public void testTableRightClickMenuOnHeader() {
	addData();
	assertTrue(
		"A JMenu should be returned",
		modelUnderTest.getPopupMenu(new JTableHeader(),
			new TableContainer(modelUnderTest)) instanceof JPopupMenu);
    }

    @Test
    public void testTableRightClickMenuOnTable() {
	addData();
	assertTrue("A JMenu should be returned",
		modelUnderTest.getPopupMenu(new JTable(), new TableContainer(
			modelUnderTest)) instanceof JPopupMenu);
    }

}
