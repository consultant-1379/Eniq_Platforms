package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextArea;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.LicenseTableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.TPDTableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.UETableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackDataModel.VRTableModel;
import com.ericsson.eniq.techpacksdk.view.newTechPack.NewTechPackInfo;

/**
 * Test for ManageGeneralPropertiesView.
 * 
 * @author eciacah
 */
public class ManageGeneralPropertiesViewTest extends TestCase {

  // Test instance:
  private ManageGeneralPropertiesView testInstance = null;

  // Mocks:
  private static DataModelController mockedController;

  private JComboBox typeCBMock;

  private JTextField unvnameFMock;

  private LimitedSizeTextField rstateFMock;

  private JTextField productFMock;

  private JTextField versionFMock;

  private JTextField nameFMock;

  private JComboBox baseCBMock;

  private LimitedSizeTextArea descriptionAMock;

  private JTable mockUnvextT;

  private JTable tppedencyTMock;

  private JTable mockLicenseTable;

  private LimitedSizeTextArea installdescrAMock;

  private LicenseTableModel licenseTableModelMock;

  private UETableModel uETableModelMock;

  private JTable vendorReleaseTableMock;

  private VRTableModel vendorReleaseTableModelMock;

  private TPDTableModel tpdTableModelMock;

  public ManageGeneralPropertiesViewTest() {
    super("ManageGeneralPropertiesViewTest");
  }

  @Override
  @Before
  protected void setUp() throws Exception {
    mockedController = createNiceMock(DataModelController.class);

    nameFMock = createNiceMock(JTextField.class);
    versionFMock = createNiceMock(JTextField.class);
    productFMock = createNiceMock(JTextField.class);
    rstateFMock = createNiceMock(LimitedSizeTextField.class);
    unvnameFMock = createNiceMock(JTextField.class);
    typeCBMock = createNiceMock(JComboBox.class);
    descriptionAMock = createNiceMock(LimitedSizeTextArea.class);
    baseCBMock = createNiceMock(JComboBox.class);
    tppedencyTMock = createNiceMock(JTable.class);
    mockUnvextT = createNiceMock(JTable.class);
    mockLicenseTable = createNiceMock(JTable.class);
    installdescrAMock = createNiceMock(LimitedSizeTextArea.class);
    vendorReleaseTableMock = createNiceMock(JTable.class);

    licenseTableModelMock = createNiceMock(LicenseTableModel.class);
    uETableModelMock = createNiceMock(UETableModel.class);
    vendorReleaseTableModelMock = createNiceMock(VRTableModel.class);
    tpdTableModelMock = createNiceMock(TPDTableModel.class);

    // Create new test instance:
    testInstance = new ManageGeneralPropertiesView(mockedController, nameFMock, versionFMock, productFMock,
        rstateFMock, "R1A", unvnameFMock, typeCBMock, descriptionAMock, baseCBMock, tppedencyTMock, mockUnvextT,
        mockLicenseTable, installdescrAMock, vendorReleaseTableMock);

    // Disable logging:
    Logger.getLogger(ManageGeneralPropertiesView.class.getName()).setLevel(Level.OFF);
  }

  @Override
  @After
  protected void tearDown() throws Exception {
    testInstance = null;
  }

  /**
   * Test CreateNewTechPackInfo. The tech pack name should not be in upper case.
   */
  public void testCreateNewTechPackInfo() {

    final String TECH_PACK_NAME = "Test Name";

    mockedController.getUserName();
    expectLastCall().andReturn("test user");
    replay(mockedController);

    nameFMock.getText();
    expectLastCall().andReturn(TECH_PACK_NAME);
    replay(nameFMock);

    versionFMock.getText();
    expectLastCall().andReturn("version");
    replay(versionFMock);

    productFMock.getText();
    expectLastCall().andReturn("CXC 123 456");
    replay(productFMock);

    rstateFMock.getText();
    expectLastCall().andReturn("R1B");

    // License table:
    mockLicenseTable.getModel();
    expectLastCall().andReturn(licenseTableModelMock);
    replay(mockLicenseTable);

    licenseTableModelMock.getRowCount();
    expectLastCall().andReturn(1);

    licenseTableModelMock.getValueAt(0, 0);
    expectLastCall().andReturn("CXC 123 456");
    replay(licenseTableModelMock);

    rstateFMock.getText();
    expectLastCall().andReturn("R1B");
    replay(rstateFMock);

    unvnameFMock.getText();
    expectLastCall().andReturn("Universe Name");
    replay(unvnameFMock);

    typeCBMock.getSelectedItem();
    expectLastCall().andReturn("System");
    replay(typeCBMock);

    // Universe extension table:
    mockUnvextT.getModel();
    expectLastCall().andReturn(uETableModelMock);
    replay(mockUnvextT);

    uETableModelMock.getRowCount();
    expectLastCall().andReturn(1);

    uETableModelMock.getValueAt(0, 0);
    expectLastCall().andReturn("");

    uETableModelMock.getValueAt(0, 1);
    expectLastCall().andReturn("");
    replay(uETableModelMock);

    descriptionAMock.getText();
    expectLastCall().andReturn("Test description");

    descriptionAMock.getText();
    expectLastCall().andReturn("Test description");
    replay(descriptionAMock);

    // Vendor release table:
    vendorReleaseTableMock.getModel();
    expectLastCall().andReturn(vendorReleaseTableModelMock);
    replay(vendorReleaseTableMock);

    vendorReleaseTableModelMock.getRowCount();
    expectLastCall().andReturn(1);

    vendorReleaseTableModelMock.getValueAt(0, 0);
    expectLastCall().andReturn("");
    replay(vendorReleaseTableModelMock);

    baseCBMock.getSelectedItem();
    expectLastCall().andReturn("Base TP");
    replay(baseCBMock);

    tppedencyTMock.getModel();
    expectLastCall().andReturn(tpdTableModelMock);
    replay(tppedencyTMock);

    tpdTableModelMock.getRowCount();
    expectLastCall().andReturn(1);

    tpdTableModelMock.getValueAt(0, 0);
    expectLastCall().andReturn("");

    tpdTableModelMock.getValueAt(0, 1);
    expectLastCall().andReturn("");
    replay(tpdTableModelMock);

    installdescrAMock.getText();
    expectLastCall().andReturn("Install description");

    installdescrAMock.getText();
    expectLastCall().andReturn("Install description");

    replay(installdescrAMock);

    // Create a NewTechPackInfo object:
    final NewTechPackInfo techPackInfoObject = testInstance.createNewTechPackInfo();
    final String techPackNameInUpperCase = techPackInfoObject.getName().toUpperCase();
    final boolean checkName = techPackNameInUpperCase.equals(techPackInfoObject.getName());
    assertFalse("Tech pack name should not be converted to upper case", checkName);
  }

}
