package com.ericsson.eniq.techpacksdk.view.group;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import org.jdesktop.application.Application;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Grouptypes;

public class GroupTypeColumnTableModelTest extends BaseUnitTestX {

  private static final String VERSION_ID = "VERSION_ID";

  private static final String GROUP_TYPE_NAME = "GROUP_TYPE_NAME";

  GroupTypeColumnTableModel groupTypeColumnTableModel;

  private final static Application application = null;

  private final static RockFactory rockFactory = null;

  private final Vector<TableInformation> tableInfos = new Vector<TableInformation>();

  private final static boolean isTreeEditable = false;

  private GroupTable groupTable = null;

  private Grouptypes mockgroupTypes;

  @Before
  public void setUp() {
    groupTable = new GroupTable();
    groupTable.setTypeName(GROUP_TYPE_NAME);
    groupTable.setVersionid(VERSION_ID);
  }

  @Test
  public void checkThatColumnsNamesAreCorrect() throws Exception {
    final String[] expectedColumnNames = { "Name", "DataType", "DataSize", "DataScale", "Nullable" };

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    final Field columnNames = GroupTypeColumnTableModel.class.getDeclaredField("myColumnNames");
    columnNames.setAccessible(true);

    final String[] actualColumnNames = (String[]) columnNames.get(groupTypeColumnTableModel);

    assertEquals(expectedColumnNames.length, actualColumnNames.length);
    for (int i = 0; i < expectedColumnNames.length; i++) {
      assertThat(expectedColumnNames[i], is(actualColumnNames[i]));
    }
  }

  @Test
  public void checkThatColumnsWidthsAreCorrect() throws Exception {
    final int[] expectedColumnWidths = { 128, 120, 60, 60, 120 };

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    final Field columnWidths = GroupTypeColumnTableModel.class.getDeclaredField("myColumnWidths");
    columnWidths.setAccessible(true);

    final int[] actualColumnWidths = (int[]) columnWidths.get(groupTypeColumnTableModel);

    assertEquals(expectedColumnWidths.length, actualColumnWidths.length);
    for (int i = 0; i < expectedColumnWidths.length; i++) {
      assertThat(expectedColumnWidths[i], is(actualColumnWidths[i]));
    }
  }


  @Test
  public void checkThatTableNameIsCorrect() throws Exception {
    final String expectedTableName = "GroupColumns";

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    final String actualTableName = groupTypeColumnTableModel.getTableName();

    assertEquals(expectedTableName + " is not equals to " + actualTableName, expectedTableName, actualTableName);
  }

  @Test
  public void checkThatColumnNumbersAreCorrect() {
    final int row = 0;
    final int dataColNumberZero = 0;
    final int dataColNumberOne = 1;
    final int dataColNumberTwo = 2;
    final int dataColNumberThree = 3;
    final int dataColNumberFour = 4;

    final String dataNameValue = "testDataNameValue";
    final String dataTypeValue = "testDataTypeValue";
    final int dataSizeValue = 10;
    final int dataScaleValue = 0;
    final int nullableValue = 1;
    final boolean isNullable = true;
    mockgroupTypes = context.mock(Grouptypes.class);
    context.checking(new Expectations() {

      {
        one(mockgroupTypes).setDataname(dataNameValue);
        exactly(3).of(mockgroupTypes).getDataname();
        will(returnValue(dataNameValue));
        one(mockgroupTypes).setDatatype(dataTypeValue);
        exactly(3).of(mockgroupTypes).getDatatype();
        will(returnValue(dataTypeValue));
        one(mockgroupTypes).setDatasize(dataSizeValue);
        exactly(3).of(mockgroupTypes).getDatasize();
        will(returnValue(dataSizeValue));
        one(mockgroupTypes).setDatascale(dataScaleValue);
        exactly(3).of(mockgroupTypes).getDatascale();
        will(returnValue(dataScaleValue));
        one(mockgroupTypes).setNullable(nullableValue);
        exactly(3).of(mockgroupTypes).getNullable();
        will(returnValue(nullableValue));
      }
    });

    tableInfos.addElement(GroupTypeColumnTableModel.createTableTypeInfo());

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    setDataForTest(mockgroupTypes);

    groupTypeColumnTableModel.setValueAt(dataNameValue, row, dataColNumberZero);
    assertEquals(dataNameValue, groupTypeColumnTableModel.getValueAt(row, dataColNumberZero));
    assertEquals(dataNameValue, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberZero));
    assertEquals(dataNameValue, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberZero));
    assertNull(groupTypeColumnTableModel.getColumnFilterForTableType(dataColNumberZero));
    assertFalse(groupTypeColumnTableModel.isColumnFilteredForTableType(dataColNumberZero));

    groupTypeColumnTableModel.setValueAt(dataTypeValue, row, dataColNumberOne);
    assertEquals(dataTypeValue, groupTypeColumnTableModel.getValueAt(row, dataColNumberOne));
    assertEquals(dataTypeValue, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberOne));
    assertEquals(dataTypeValue, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberOne));
    assertNull(groupTypeColumnTableModel.getColumnFilterForTableType(dataColNumberOne));
    assertFalse(groupTypeColumnTableModel.isColumnFilteredForTableType(dataColNumberOne));

    groupTypeColumnTableModel.setValueAt(dataSizeValue, row, dataColNumberTwo);
    assertEquals(dataSizeValue, groupTypeColumnTableModel.getValueAt(row, dataColNumberTwo));
    assertEquals(dataSizeValue, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberTwo));
    assertEquals(dataSizeValue, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberTwo));
    assertNull(groupTypeColumnTableModel.getColumnFilterForTableType(dataColNumberTwo));
    assertFalse(groupTypeColumnTableModel.isColumnFilteredForTableType(dataColNumberTwo));

    groupTypeColumnTableModel.setValueAt(dataScaleValue, row, dataColNumberThree);
    assertEquals(dataScaleValue, groupTypeColumnTableModel.getValueAt(row, dataColNumberThree));
    assertEquals(dataScaleValue, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberThree));
    assertEquals(dataScaleValue, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberThree));
    assertNull(groupTypeColumnTableModel.getColumnFilterForTableType(dataColNumberThree));
    assertFalse(groupTypeColumnTableModel.isColumnFilteredForTableType(dataColNumberThree));

    groupTypeColumnTableModel.setValueAt(isNullable, row, dataColNumberFour);
    assertEquals(isNullable, groupTypeColumnTableModel.getValueAt(row, dataColNumberFour));
    assertEquals(isNullable, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberFour));
    assertEquals(isNullable, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberFour));
    assertNull(groupTypeColumnTableModel.getColumnFilterForTableType(dataColNumberFour));
    assertFalse(groupTypeColumnTableModel.isColumnFilteredForTableType(dataColNumberFour));
  }

  @Test
  public void checkThatColumnNumbersIsCorrectWhenNullableIsFalse() {
    final int row = 0;
    final int dataColNumberFour = 4;
    final int nullableValue = 0;
    final boolean isNullable = false;
    mockgroupTypes = context.mock(Grouptypes.class, "groupType");
    context.checking(new Expectations() {

      {
        one(mockgroupTypes).setNullable(nullableValue);
        exactly(3).of(mockgroupTypes).getNullable();
        will(returnValue(nullableValue));
      }
    });

    tableInfos.addElement(GroupTypeColumnTableModel.createTableTypeInfo());

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    setDataForTest(mockgroupTypes);

    groupTypeColumnTableModel.setValueAt(isNullable, row, dataColNumberFour);
    assertEquals(isNullable, groupTypeColumnTableModel.getValueAt(row, dataColNumberFour));
    assertEquals(isNullable, groupTypeColumnTableModel.getOriginalValueAt(row, dataColNumberFour));
    assertEquals(isNullable, groupTypeColumnTableModel.getColumnValueAt(mockgroupTypes, dataColNumberFour));
  }

  @Test
  public void checkIncorrectColNumbersLeavesDefaultValues() {
    final int row = 0;
    final int dataColNumberZero = 0;
    final int dataColNumberOne = 1;
    final int dataColNumberTwo = 2;
    final int dataColNumberThree = 3;
    final int dataColNumberFour = 4;
    final int dataColNumberFive = 5;

    mockgroupTypes = context.mock(Grouptypes.class, "mockGroupTypes");
    context.checking(new Expectations() {

      {
        anything();
      }
    });

    tableInfos.addElement(GroupTypeColumnTableModel.createTableTypeInfo());

    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);

    setDataForTest(new Grouptypes(null));

    groupTypeColumnTableModel.setValueAt(0, row, dataColNumberFive);
    assertTrue(groupTypeColumnTableModel.getValueAt(row, dataColNumberZero).equals(""));
    assertTrue(groupTypeColumnTableModel.getValueAt(row, dataColNumberOne).equals(""));
    assertTrue(groupTypeColumnTableModel.getValueAt(row, dataColNumberTwo).toString().equals("0"));
    assertTrue(groupTypeColumnTableModel.getValueAt(row, dataColNumberThree).toString().equals("0"));
    assertTrue(groupTypeColumnTableModel.getValueAt(row, dataColNumberFour).toString().equals("false"));
    assertNull(groupTypeColumnTableModel.getValueAt(row, dataColNumberFive));
  }

  @Test
  public void testCreateNewGivesCorrectDefaultValues() {
    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);
    final RockDBObject rockDBObject = groupTypeColumnTableModel.createNew();
    assertTrue(rockDBObject instanceof Grouptypes);
    assertTrue(((Grouptypes) rockDBObject).getGrouptype().equals(GROUP_TYPE_NAME));
    assertTrue(((Grouptypes) rockDBObject).getVersionid().equals(VERSION_ID));
    assertTrue(((Grouptypes) rockDBObject).getDataname().equals(""));
    assertTrue(((Grouptypes) rockDBObject).getDatasize().equals(0));
    assertTrue(((Grouptypes) rockDBObject).getDatascale().equals(0));
    assertTrue(((Grouptypes) rockDBObject).getNullable().equals(0));
  }

  @Test
  public void checkThatValidDataReturnEmptyWhenDataIsCorrect() {
    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);
    final Grouptypes grouptypes = (Grouptypes) groupTypeColumnTableModel.createNew();
    grouptypes.setDataname("testDataName");
    grouptypes.setDatasize(0);
    grouptypes.setDatatype("int");
    setDataForTest(grouptypes);
    assertTrue(groupTypeColumnTableModel.validateData().isEmpty());
  }

  @Test
  public void checkValidDataReturnsErrorWhenDataTypeAndNameIsEmpty() {
    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);
    final Grouptypes grouptypes = (Grouptypes) groupTypeColumnTableModel.createNew();
    setDataForTest(grouptypes);
    final List<String> errorStrings = groupTypeColumnTableModel.validateData();
    assertEquals(2, errorStrings.size());
    assertEquals("GROUP_TYPE_NAME: Name for column is required", errorStrings.get(0));
    assertEquals("GROUP_TYPE_NAME: DataType for column is required", errorStrings.get(1));
  }

  @Test
  public void checkValidDataReturnsErrorWhenDataTypeIsVarCharAndDatasizeIsZero() {
    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);
    final Grouptypes grouptypes = (Grouptypes) groupTypeColumnTableModel.createNew();
    grouptypes.setDataname("testDataName");
    grouptypes.setDatasize(0);
    grouptypes.setDatatype("varchar");
    setDataForTest(grouptypes);
    final List<String> errorStrings = groupTypeColumnTableModel.validateData();
    assertEquals(1, errorStrings.size());
    assertEquals("GROUP_TYPE_NAME testDataName Column: Datasize can not be 0.", errorStrings.get(0));
  }

  @Test
  public void checkValidDataReturnsErrorWhenDataNameIsNOTUnique() {
    groupTypeColumnTableModel = new GroupTypeColumnTableModel(application, rockFactory, tableInfos, isTreeEditable,
        groupTable);
    final Grouptypes grouptypes = (Grouptypes) groupTypeColumnTableModel.createNew();
    grouptypes.setDataname("testDataName");
    grouptypes.setDatasize(0);
    grouptypes.setDatatype("int");

    // Create a copy of the existing groupTypes
    final Grouptypes groupTypes2 = (Grouptypes) groupTypeColumnTableModel.copyOf(grouptypes);

    // Add two groupTypes with the same to the list
    final Vector<Object> groupColumns = new Vector<Object>();
    groupColumns.add(grouptypes);
    groupColumns.add(groupTypes2);
    groupTypeColumnTableModel.setData(groupColumns);

    final List<String> errorStrings = groupTypeColumnTableModel.validateData();
    assertEquals(2, errorStrings.size());
    assertEquals("GROUP_TYPE_NAME Key: Name (testDataName) is not unique", errorStrings.get(0));
  }

  private void setDataForTest(final Grouptypes groupTypes) {
    final Vector<Object> groupColumns = new Vector<Object>();
    groupColumns.add(groupTypes);
    groupTypeColumnTableModel.setData(groupColumns);
  }
}
