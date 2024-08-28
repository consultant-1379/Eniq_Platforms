package com.ericsson.eniq.techpacksdk.view.reference;

import static org.junit.Assert.*;

import java.util.Properties;

import org.jdesktop.application.Application;
import org.jmock.Expectations;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

import com.ericsson.eniq.techpacksdk.common.Constants;

public class ReferenceTypeFactoryTest extends BaseUnitTestX {

  private final Application application = null;

  private final RockFactory rockFactory = null;

  private final boolean editable = false;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    StaticProperties.giveProperties(new Properties());
  }
  @Test
  public void checkThatENIQ_EVENTColumnsAreGotFromTableInformationInReferenceTypeFactory() {
    String[] expectedColumnNames = { "Name", "DataType", "DataSize", "DataScale", "DuplicateConstraint", "Mandatory",
        "Universe Class", "Universe Object", "Universe Condition", "SQL Interface Support", "Topology Update Use",
        "Description", "IQIndex" };
    int[] expectedColumnWidths = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200, 120 };

    ReferenceTypeDataModel mockReferenceTypeDataModel = createMockReferenceTypeDataModel("CheckColumnsForENIQ_EVENT");
    Versioning versioning = createVersioning(Constants.ENIQ_EVENT);

    ReferenceTypeFactory referenceTypeFactory = new ReferenceTypeFactory(application, mockReferenceTypeDataModel,
        editable, versioning, null);

    TableInformation tableInfo = referenceTypeFactory.tableInformations.get(0);

    // Check column names
    String[] actualColumnNames = tableInfo.getColumnNamesInOriginalOrder();
    assertEquals(expectedColumnNames.length, actualColumnNames.length);
    for (int i = 0; i < expectedColumnNames.length; i++) {
      assertEquals(expectedColumnNames[i] + " is not equals to " + actualColumnNames[i], expectedColumnNames[i],
          actualColumnNames[i]);
    }

    // Check column widths
    int[] actualColumnWidths = tableInfo.getColumnWidthsInOrderFromProperties();
    assertEquals(expectedColumnWidths.length, actualColumnWidths.length);
    for (int i = 0; i < expectedColumnWidths.length; i++) {
      assertEquals(expectedColumnWidths[i] + " is not equals to " + actualColumnWidths[i], expectedColumnWidths[i],
          actualColumnWidths[i]);
    }
  }

  @Test
  public void checkThatPMColumnsAreGotFromTableInformationInReferenceTypeFactory() {
    String[] expectedColumnNames = { "Name", "DataType", "DataSize", "DataScale", "DuplicateConstraint", "Mandatory",
        "Universe Class", "Universe Object", "Universe Condition", "SQL Interface Support", "Topology Update Use",
        "Description" };
    int[] expectedColumnWidths = { 128, 120, 60, 60, 120, 120, 120, 90, 90, 90, 60, 200 };

    ReferenceTypeDataModel mockReferenceTypeDataModel = createMockReferenceTypeDataModel("CheckColumnsForPM");
    Versioning versioning = createVersioning("PM");

    ReferenceTypeFactory referenceTypeFactory = new ReferenceTypeFactory(application, mockReferenceTypeDataModel,
        editable, versioning, null);

    TableInformation tableInfo = referenceTypeFactory.tableInformations.get(0);

    // Check column names
    String[] actualColumnNames = tableInfo.getColumnNamesInOriginalOrder();
    assertEquals(expectedColumnNames.length, actualColumnNames.length);
    for (int i = 0; i < expectedColumnNames.length; i++) {
      assertEquals(expectedColumnNames[i] + " is not equals to " + actualColumnNames[i], expectedColumnNames[i],
          actualColumnNames[i]);
    }

    // Check column widths
    int[] actualColumnWidths = tableInfo.getColumnWidthsInOrderFromProperties();
    assertEquals(expectedColumnWidths.length, actualColumnWidths.length);
    for (int i = 0; i < expectedColumnWidths.length; i++) {
      assertEquals(expectedColumnWidths[i] + " is not equals to " + actualColumnWidths[i], expectedColumnWidths[i],
          actualColumnWidths[i]);
    }
  }

  private Versioning createVersioning(String techpackType) {
    Versioning versioning = new Versioning(rockFactory);
    versioning.setTechpack_type(techpackType);
    return versioning;
  }

  private ReferenceTypeDataModel createMockReferenceTypeDataModel(String mockName) {
    final ReferenceTypeDataModel mockReferenceTypeDataModel = context.mock(ReferenceTypeDataModel.class, mockName);

    context.checking(new Expectations() {

      {
        one(mockReferenceTypeDataModel).getRockFactory();
        will(returnValue(null));
      }
    });

    return mockReferenceTypeDataModel;
  }

}
