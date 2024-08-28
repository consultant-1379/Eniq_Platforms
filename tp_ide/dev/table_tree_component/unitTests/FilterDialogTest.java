package unitTests;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import junit.framework.TestCase;
import measurementType.MeasurementTypeFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tableTree.TableTreeComponent;
import tableTree.TreeDataFactory;
import tableTreeUtils.FilterDialog;
import tableTreeUtils.TableInformation;

public class FilterDialogTest extends TestCase {

  public StubbedFilterDialog filterDialogUnderTest = null;

  public TreeDataFactory treeDataFactory;

  public TableTreeComponent myTTC;

  public static boolean isTreeEditable;

  private Vector<TableInformation> tableInfos = null;

  @Before
  public void setUp() throws Exception {
    isTreeEditable = true;
    treeDataFactory = new MeasurementTypeFactory(isTreeEditable, null);
    myTTC = new TableTreeComponent(treeDataFactory);
    tableInfos = myTTC.getFactory().tableInformations;
    filterDialogUnderTest = new StubbedFilterDialog(myTTC, tableInfos);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testShowAndDisposeFilterDialog() {
    JFrame theDialog = filterDialogUnderTest.showDialog();

    assertTrue("The FilterDialog should be displayable", theDialog.isDisplayable());
    assertTrue("The FilterDialog should be visible", theDialog.isVisible());

    ActionEvent ae = new ActionEvent("hello", 1, FilterDialog.CLOSE);
    filterDialogUnderTest.actionPerformed(ae);

    assertFalse("The FilterDialog should not be displayable anymore", theDialog.isDisplayable());
    assertFalse("The FilterDialog should not be visible anymore", theDialog.isVisible());
  }

  @Test
  public void testAddFilterToColumnType() {
    int tableTypeIndex = 0;
    int tableColumnIndex = 0;

    TableInformation thisTableInfo = (TableInformation) tableInfos.elementAt(tableTypeIndex);
    String filterExpression = "Test Expression";

    filterDialogUnderTest.setFilterString(filterExpression);
    filterDialogUnderTest.setSelectedTable(tableTypeIndex);
    filterDialogUnderTest.setSelectedColumn(tableColumnIndex);

    ActionEvent ae = new ActionEvent("hello", 1, FilterDialog.SET_FILTER);
    filterDialogUnderTest.actionPerformed(ae);

    assertEquals(filterExpression, thisTableInfo.getTableTypeColumnFilter(tableColumnIndex));

  }

  @Test
  public void testModifyFilterForColumn() {
    int tableTypeIndex = 0;
    int tableColumnIndex = 0;

    TableInformation thisTableInfo = (TableInformation) tableInfos.elementAt(tableTypeIndex);
    String filterExpression = "Test Expression";

    thisTableInfo.setTableTypeColumnFilter(tableColumnIndex, filterExpression);

    filterDialogUnderTest.setSelectedTable(0);
    filterDialogUnderTest.setSelectedColumn(0);

    assertEquals(filterExpression, thisTableInfo.getTableTypeColumnFilter(tableColumnIndex));

    filterDialogUnderTest.setFilterString("Another Test Expression");

    ActionEvent ae = new ActionEvent("hello", 1, FilterDialog.EDIT_FILTER_TEXT);
    filterDialogUnderTest.actionPerformed(ae);

    assertNotSame(filterExpression, thisTableInfo.getTableTypeColumnFilter(tableColumnIndex));
  }

  @Test
  public void testRemoveFilterFromColumnType() {
    int tableTypeIndex = 0;
    int tableColumnIndex = 0;

    TableInformation thisTableInfo = (TableInformation) tableInfos.elementAt(tableTypeIndex);
    String filterExpression = "Test Expression";

    thisTableInfo.setTableTypeColumnFilter(tableColumnIndex, filterExpression);

    filterDialogUnderTest.setSelectedTable(0);
    filterDialogUnderTest.setSelectedColumn(0);

    assertEquals(filterExpression, thisTableInfo.getTableTypeColumnFilter(tableColumnIndex));

    ActionEvent ae = new ActionEvent("hello", 1, FilterDialog.CLEAR_FILTER);
    filterDialogUnderTest.actionPerformed(ae);

    assertEquals("", thisTableInfo.getTableTypeColumnFilter(tableColumnIndex));
  }

  @Test
  public void testRemoveFilterFromAllColumnTypes() {
    String filterExpression = "Dummy Expression";

    Iterator<TableInformation> infos = tableInfos.iterator();
    TableInformation thisInfo = null;

    while (infos.hasNext()) {
      thisInfo = infos.next();
      for (int i = 0; i < thisInfo.getColumnNamesInOriginalOrder().length; i++) {
        thisInfo.setTableTypeColumnFilter(i, filterExpression);
      }
    }

    ActionEvent ae = new ActionEvent("hello", 1, FilterDialog.CLEAR_ALL_FILTERS);
    filterDialogUnderTest.actionPerformed(ae);

    infos = tableInfos.iterator();
    while (infos.hasNext()) {
      thisInfo = infos.next();
      for (int i = 0; i < thisInfo.getColumnNamesInOriginalOrder().length; i++) {
        assertEquals("", thisInfo.getTableTypeColumnFilter(i));
      }
    }

  }

}
