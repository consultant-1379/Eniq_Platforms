package unitTests;

import java.awt.event.ActionEvent;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TablePopupMenuListener;

public class TablePopupMenuListenerTest extends TestCase {

    public TablePopupMenuListener listenerUnderTest = null;
    public stubbedTableModel tableModel = null;
    public stubbedRockDBObject dbObject = null;
    public stubbedRockDBObject newObject = null;
    public stubbedTableContainer tableContainer = null;

    @Before
    public void setUp() throws Exception {

	tableModel = new stubbedTableModel(new RockFactory("anUrl", "user",
		"password", "driver", "con", false),null); // null ok here?
	dbObject = new stubbedRockDBObject();
	newObject = new stubbedRockDBObject();

	Vector<Object> data = new Vector<Object>();
	data.add(dbObject);

	tableModel.setData(data);
	tableModel.newObject = newObject;

	// stubbedTableContainer needed because to the TuneSize() method
	tableContainer = new stubbedTableContainer(tableModel);
	listenerUnderTest = new TablePopupMenuListener(tableContainer);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testActionPerformedAdd() {
	int size = tableModel.getRowCount();
	ActionEvent ae = new ActionEvent("hello", 1, TablePopupMenuListener.ADD);
	listenerUnderTest.actionPerformed(ae);

	assertTrue("The number of rows should have increased", tableModel
		.getRowCount() == size + 1);
    }

    @Test
    public void testActionPerformedDelete() {
	// Select rows
	tableContainer.getTable().addRowSelectionInterval(0, 0);

	int size = tableModel.getRowCount();
	ActionEvent ae = new ActionEvent("hello", 1,
		TablePopupMenuListener.DELETE_NO_CONFIRM);
	listenerUnderTest.actionPerformed(ae);

	assertTrue("The number of rows should have decreased", tableModel
		.getRowCount() == size - 1);
    }

    @Test
    public void testActionPerformedMoveUp() {
	// Add one more row
	tableModel.insertDataLast(newObject);
	newObject.value0 = 42;
	// Select rows
	tableContainer.getTable().addRowSelectionInterval(1, 1);

	int size = tableModel.getRowCount();
	ActionEvent ae = new ActionEvent("hello", 1,
		TablePopupMenuListener.MOVE_UP);
	listenerUnderTest.actionPerformed(ae);

	assertTrue("The number of rows should have stayed the same", tableModel
		.getRowCount() == size);
	assertTrue("The object should have moved up", (Integer) tableModel
		.getValueAt(0, 0) == newObject.value0);
    }

    @Test
    public void testActionPerformedMoveDown() {
	// Add one more row
	tableModel.insertDataAtRow(newObject, 0);
	newObject.value0 = 42;
	// Select rows
	tableContainer.getTable().addRowSelectionInterval(0, 0);

	int size = tableModel.getRowCount();
	ActionEvent ae = new ActionEvent("hello", 1,
		TablePopupMenuListener.MOVE_DOWN);
	listenerUnderTest.actionPerformed(ae);

	assertTrue("The number of rows should have stayed the same", tableModel
		.getRowCount() == size);
	assertTrue("The object should have moved down", (Integer) tableModel
		.getValueAt(1, 0) == newObject.value0);
    }

}
