package com.ericsson.eniq.techpacksdk.view.reference;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Vector;

import org.jdesktop.application.Application;
import org.jmock.Expectations;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;

import com.ericsson.eniq.techpacksdk.common.Constants;

public class ReferenceTypeColumnTableModelFactoryTest extends BaseUnitTestX {

  ReferenceTypeColumnTableModel referenceTypeColumnTableModel;

  private Application application = null;

  private RockFactory rockFactory = null;

  private Vector<TableInformation> tableInfos = new Vector<TableInformation>();

  private boolean isTreeEditable = false;

  private Referencetable referencetable = null;

  private boolean isBaseTechPack = false;

  private Vector<Referencecolumn> publiccolumns = null;

  private Referencecolumn mockReferencecolumn;

  @Test
  public void checkThatColumnsNamesAreCorrectForPMTechpack() throws Exception {
    String techpackType = "PM";
    String[] expectedColumnNames = { "Name", "DataType", "DataSize", "DataScale", "DuplicateConstraint", "Mandatory",
        "Universe Class", "Universe Object", "Universe Condition", "SQL Interface Support", "Topology Update Use",
        "Description" };

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos,
        isTreeEditable, referencetable, isBaseTechPack, publiccolumns, techpackType);

    Field columnNames = ReferenceTypeColumnTableModel.class.getDeclaredField("myColumnNames");
    columnNames.setAccessible(true);

    String[] actualColumnNames = (String[]) columnNames.get(referenceTypeColumnTableModel);

    assertEquals(expectedColumnNames.length, actualColumnNames.length);
    for (int i = 0; i < expectedColumnNames.length; i++) {
      assertThat(expectedColumnNames[i], is(actualColumnNames[i]));
    }
  }

  @Test
  public void checkThatColumnsWidthsAreCorrectForPMTechpack() throws Exception {
    String techpackType = "PM";
    int[] expectedColumnWidths = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200 };

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos,
        isTreeEditable, referencetable, isBaseTechPack, publiccolumns, techpackType);

    Field columnWidths = ReferenceTypeColumnTableModel.class.getDeclaredField("myColumnWidths");
    columnWidths.setAccessible(true);

    int[] actualColumnWidths = (int[]) columnWidths.get(referenceTypeColumnTableModel);

    assertEquals(expectedColumnWidths.length, actualColumnWidths.length);
    for (int i = 0; i < expectedColumnWidths.length; i++) {
      assertThat(expectedColumnWidths[i], is(actualColumnWidths[i]));
    }
  }

  @Test
  public void checkThatColumnsNamesAreCorrectForENIQ_EVENTTechpack() throws Exception {
    String techpackType = Constants.ENIQ_EVENT;
    String[] expectedColumnNames = { "Name", "DataType", "DataSize", "DataScale", "DuplicateConstraint", "Mandatory",
        "Universe Class", "Universe Object", "Universe Condition", "SQL Interface Support", "Topology Update Use",
        "Description", "IQIndex" };

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos,
        isTreeEditable, referencetable, isBaseTechPack, publiccolumns, techpackType);

    Field columnNames = ReferenceTypeColumnTableModel.class.getDeclaredField("myColumnNames");
    columnNames.setAccessible(true);

    String[] actualColumnNames = (String[]) columnNames.get(referenceTypeColumnTableModel);

    assertEquals(expectedColumnNames.length, actualColumnNames.length);
    for (int i = 0; i < expectedColumnNames.length; i++) {
      assertEquals(expectedColumnNames[i] + " is not equals to " + actualColumnNames[i], expectedColumnNames[i],
          actualColumnNames[i]);
    }
  }

  @Test
  public void checkThatColumnsWidthsAreCorrectForENIQ_EVENTTechpack() throws Exception {
    String techpackType = Constants.ENIQ_EVENT;
    int[] expectedColumnWidths = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200, 120 };

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos,
        isTreeEditable, referencetable, isBaseTechPack, publiccolumns, techpackType);

    Field columnWidths = ReferenceTypeColumnTableModel.class.getDeclaredField("myColumnWidths");
    columnWidths.setAccessible(true);

    int[] actualColumnWidths = (int[]) columnWidths.get(referenceTypeColumnTableModel);

    assertEquals(expectedColumnWidths.length, actualColumnWidths.length);
    for (int i = 0; i < expectedColumnWidths.length; i++) {
      assertEquals(expectedColumnWidths[i] + " is not equals to " + actualColumnWidths[i], expectedColumnWidths[i],
          actualColumnWidths[i]);
    }
  }

  @Test
  public void checkThatTableNameIsCorrect() throws Exception {
    String techpackType = "PM";
    String expectedTableName = "Columns";

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos,
        isTreeEditable, referencetable, isBaseTechPack, publiccolumns, techpackType);

    String actualTableName = referenceTypeColumnTableModel.getTableName();

    assertEquals(expectedTableName + " is not equals to " + actualTableName, expectedTableName, actualTableName);
  }

  @Test
  public void checkThatColumnNumberIsCorrectForDataname() {
    String techpackType = "PM";
    int dataNameColumnNumber = 0;
    final String dataNameValue = "testDataNameValue";
    mockReferencecolumn = context.mock(Referencecolumn.class);
    context.checking(new Expectations() {

      {
        one(mockReferencecolumn).setDataname(dataNameValue);
        one(mockReferencecolumn).getDataname();
        will(returnValue(dataNameValue));
      }
    });

    tableInfos.addElement(ReferenceTypeColumnTableModelFactory.createTableTypeInfo(techpackType));

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos, isTreeEditable, referencetable, isBaseTechPack, publiccolumns,
        techpackType);

    setDataForTest();
    referenceTypeColumnTableModel.setValueAt(dataNameValue, 0, dataNameColumnNumber);

    assertEquals(dataNameValue, referenceTypeColumnTableModel.getValueAt(0, dataNameColumnNumber));
  }


  @Test
  public void checkThatColumnNumberIsCorrectForIQIndex() {
    String techpackType = Constants.ENIQ_EVENT;
    int iqIndexColumnNumber = 12;
    final String ipIndexValue = "testIQIndexValue";
    mockReferencecolumn = context.mock(Referencecolumn.class, "IQIndexReferencecolumn");
    context.checking(new Expectations() {

      {
        one(mockReferencecolumn).setIndexes(ipIndexValue);
        one(mockReferencecolumn).getIndexes();
        will(returnValue(ipIndexValue));
      }
    });

    tableInfos.addElement(ReferenceTypeColumnTableModelFactory.createTableTypeInfo(techpackType));

    referenceTypeColumnTableModel = ReferenceTypeColumnTableModelFactory.createReferenceTypeColumnTableModel(
        application, rockFactory, tableInfos, isTreeEditable, referencetable, isBaseTechPack, publiccolumns,
        techpackType);

    setDataForTest();

    // referenceTypeColumnTableModel.setDataForTest();
    referenceTypeColumnTableModel.setValueAt(ipIndexValue, 0, iqIndexColumnNumber);

    assertEquals(ipIndexValue, referenceTypeColumnTableModel.getValueAt(0, iqIndexColumnNumber));
  }

  private void setDataForTest() {
    Vector<Object> referenceColumns = new Vector<Object>();
    referenceColumns.add(mockReferencecolumn);
    referenceTypeColumnTableModel.setData(referenceColumns);
  }
}
