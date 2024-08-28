package com.ericsson.eniq.techpacksdk;

import java.io.File;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.velocity.context.Context;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.install.ant.ZipCrypter;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.InterfacedependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class CreateInstallationPackageViewTest extends TestCase {

  private static final String ENIQ_EVENT = "ENIQ_EVENT";

  private static final String PM = "PM";

  private static final String EMPTY_STRING = "";

  // Test instance:
  private CreateInstallationPackageView testInstance = null;

  // Mock context:
  protected Mockery context = new JUnit4Mockery();
  {
    // we need to mock classes, not just interfaces.
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }

  // Mocks:
  private Versioning versioningMock;

  private JTextField textFieldMock;

  private LimitedSizeTextField limitedTextFieldMock;

  private JCheckBox checkBoxMock;

  private ZipOutputStream zipOutputStreamMock;

  private CreateTPInstallFile createTPInstallFileMock;

  private CreateSetInstallFile createSetInstallFileMock;

  private CreateINTFInstallFile createINTFInstallFileMock;

  private DataModelController dataModelControllerMock;

  private TechpackdependencyFactory techpackdependencyFactoryMock;

  private InterfacedependencyFactory interfacedependencyFactoryMock;

  private Iterator<Techpackdependency> iteratorMock;

  private Techpackdependency techpackdependencyMock;

  private Interfacedependency interfacedependencyMock;

  private Vector<Techpackdependency> vec;

  private User userMock;

  private ZipCrypter zipCrypterMock;

  private Datainterface dataInterfaceMock;

  @Override
  @After
  protected void tearDown() throws Exception {
    context.assertIsSatisfied();
  }

  @Override
  @Before
  protected void setUp() throws Exception {
    createMocks();
  }

  private class CreateInstallationPackageViewInstance extends CreateInstallationPackageView {

    private static final long serialVersionUID = 1L;

    public CreateInstallationPackageViewInstance(){
    	// Default constructor required by running test in CI.
    	super(null, null, textFieldMock, limitedTextFieldMock, checkBoxMock);
    }
    
    public CreateInstallationPackageViewInstance(DataModelController dmc, Object id, JTextField textFieldMock,
        LimitedSizeTextField limitedTextFieldMock, JCheckBox checkBoxMock) {
      super(dmc, id, textFieldMock, limitedTextFieldMock, checkBoxMock);
    }

    @Override
    protected ZipOutputStream createZipOutputStream(String outputFilename) {
      return zipOutputStreamMock;
    }

    @Override
    protected CreateTPInstallFile createCreateTPInstallFile(Versioning versioning) {
      return createTPInstallFileMock;
    }

    @Override
    protected CreateSetInstallFile createCreateSetInstallFile(String name, String version, String oldBuildNumber,
        String newBuildNumber, DataModelController dmc) {
      return createSetInstallFileMock;
    }

    @Override
    protected CreateINTFInstallFile createCreateINTFInstallFile() {
      return createINTFInstallFileMock;
    }

    @Override
    protected boolean mergeVelocityTemplate(String templateName, String encoding, Context context, Writer writer) {
      return true;
    }

    @Override
    protected TechpackdependencyFactory createTechpackdependencyFactory(Versioning versioning) throws SQLException,
        RockException {
      return techpackdependencyFactoryMock;
    }

    @Override
    protected InterfacedependencyFactory createInterfacedependencyFactory(Datainterface datainterface)
        throws SQLException, RockException {
      return interfacedependencyFactoryMock;
    }

    @Override
    protected ZipCrypter setupZipCrypter(String outputFile, String privateKeyMod, String privateKeyExp) {
      return zipCrypterMock;
    }
  } //class InstanceForTest

  private CreateInstallationPackageView createTestInstanceForTP() {
    testInstance = new CreateInstallationPackageViewInstance(dataModelControllerMock, versioningMock, textFieldMock, limitedTextFieldMock,
        checkBoxMock);
    return testInstance;
  }

  private CreateInstallationPackageView createTestInstanceForInterface() {
    testInstance = new CreateInstallationPackageViewInstance(dataModelControllerMock, dataInterfaceMock, textFieldMock, limitedTextFieldMock,
        checkBoxMock);
    return testInstance;
  }

  private void createMocks() {
    versioningMock = context.mock(Versioning.class);
    dataInterfaceMock = context.mock(Datainterface.class);
    textFieldMock = context.mock(JTextField.class);
    limitedTextFieldMock = context.mock(LimitedSizeTextField.class);
    checkBoxMock = context.mock(JCheckBox.class);
    createTPInstallFileMock = context.mock(CreateTPInstallFile.class);
    createSetInstallFileMock = context.mock(CreateSetInstallFile.class);
    createINTFInstallFileMock = context.mock(CreateINTFInstallFile.class);
    zipOutputStreamMock = context.mock(ZipOutputStream.class);
    dataModelControllerMock = context.mock(DataModelController.class);

    techpackdependencyFactoryMock = context.mock(TechpackdependencyFactory.class);
    interfacedependencyFactoryMock = context.mock(InterfacedependencyFactory.class);
    iteratorMock = context.mock(Iterator.class);
    techpackdependencyMock = context.mock(Techpackdependency.class);
    interfacedependencyMock = context.mock(Interfacedependency.class);
    vec = context.mock(Vector.class);
    userMock = context.mock(User.class);
    zipCrypterMock = context.mock(ZipCrypter.class);
  }

  @Test
  public void testCreateTPInstallFile() throws Exception {

    testInstance = createTestInstanceForTP();

    expectCreateTPInstallFile("DC_E_RBS", EMPTY_STRING);
    
    expectCretateVersionPropertiesForTP();
    expectEncryptTechpackInstallFile(PM);

    Assert.assertTrue("Creating the tech pack installation file should return true value",
        testInstance.createTPInstallFile());
  }
  

  @Test
  public void testCreateTPInstallFileForEvents() throws Exception {

    testInstance = createTestInstanceForTP();

    expectCreateTPInstallFile("EVENT_E_SGEH", "123");

    expectCretateVersionPropertiesForTP();
    expectEncryptTechpackInstallFile(ENIQ_EVENT);

    Assert.assertTrue("Creating the tech pack installation file should return true value",
        testInstance.createTPInstallFile());
  }

  @Test
  public void testCreateAlarmIntfTPInstallFile() throws Exception {

    testInstance = createTestInstanceForTP();

    expectCreateTPInstallFile(Constants.ALARM_INTERFACES_TECHPACK_NAME, EMPTY_STRING);

    expectCretateVersionPropertiesForTP();
    expectEncryptTechpackInstallFile(PM);

    Assert.assertTrue("Creating the tech pack installation file should return true value",
        testInstance.createTPInstallFile());
  }
  
  @Test
  public void testCreate_DC_Z_ALARM_TPInstallFile() throws Exception {

    testInstance = createTestInstanceForTP();

    expectCreateTPInstallFile(Constants.DC_Z_ALARM_TECHPACK_NAME, EMPTY_STRING);

    expectCretateVersionPropertiesForTP();
    expectEncryptTechpackInstallFile(PM);

    Assert.assertTrue("Creating the tech pack installation file should return true value",
        testInstance.createTPInstallFile());
  }

  protected void expectCreateTPInstallFile(final String techPackName, final String installDesc) throws Exception {
    context.checking(new Expectations() {

      {
        oneOf(versioningMock).getTechpack_version();
        will(returnValue("R4A"));

        oneOf(versioningMock).getInstalldescription();
        will(returnValue(installDesc));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        allowing(versioningMock).getTechpack_name();
        will(returnValue(techPackName));

        oneOf(checkBoxMock).isSelected();
        will(returnValue(true));

        oneOf(textFieldMock).getText();
        will(returnValue("workingDir"));

        // =============
        oneOf(createTPInstallFileMock).create("sql/", zipOutputStreamMock);
        oneOf(createSetInstallFileMock).create("set/", zipOutputStreamMock, false);

        // createInstalXML
        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));
        oneOf(zipOutputStreamMock).write(with(any(byte[].class)));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        // Close:
        oneOf(zipOutputStreamMock).close();
      }
    });
  }

  @Test
  public void testCreateInterfaceInstallFileForEvents() throws Exception {
    testInstance = createTestInstanceForInterface();

    context.checking(new Expectations() {

      {
        oneOf(dataInterfaceMock).getRstate();
        will(returnValue("R4A"));

        oneOf(dataInterfaceMock).getInstalldescription();
        will(returnValue(ENIQ_EVENT));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        oneOf(dataInterfaceMock).getInterfacename();
        will(returnValue("INTF_DC_E_SGEH"));

        oneOf(textFieldMock).getText();
        will(returnValue("workingDir"));

        // =============
        oneOf(createINTFInstallFileMock).create("interface/", zipOutputStreamMock);
        oneOf(createSetInstallFileMock).create("interface/", zipOutputStreamMock, true);

        oneOf(dataInterfaceMock).getInterfacename();
        will(returnValue("INTF_DC_E_SGEH"));

        oneOf(dataInterfaceMock).getInterfaceversion();
        will(returnValue("R4A"));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        // createInstalXML
        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));
        oneOf(zipOutputStreamMock).write(with(any(byte[].class)));

        // Close:
        oneOf(zipOutputStreamMock).close();
      }
    });

    expectCretateVersionPropertiesForInterface();

    Assert.assertTrue("Creating the tech pack interface installation file should return true value",
        testInstance.createInterfaceInstallFile());
    
    Assert.assertEquals("workingDir" + File.separatorChar + "INTF_DC_E_SGEH_R4A_b100.tpi", testInstance.intfOutputFile);
  }

  private void expectCretateVersionPropertiesForInterface() throws Exception {

    context.checking(new Expectations() {

      {
        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));

        oneOf(interfacedependencyFactoryMock).get();
        will(returnValue(vec));

        oneOf(vec).iterator();
        will(returnValue(iteratorMock));

        // iterate once:
        oneOf(iteratorMock).hasNext();
        will(returnValue(true));

        oneOf(iteratorMock).next();
        will(returnValue(interfacedependencyMock));

        oneOf(interfacedependencyMock).getTechpackname();
        will(returnValue("INTF_DC_E_BSS"));

        oneOf(interfacedependencyMock).getTechpackversion();
        will(returnValue("R4A"));

        oneOf(iteratorMock).hasNext();
        will(returnValue(false));

        oneOf(dataInterfaceMock).getRstate();
        will(returnValue("R4A"));

        oneOf(dataInterfaceMock).getInterfacename();
        will(returnValue("INTF_DC_E_BSS"));

        oneOf(dataInterfaceMock).getLockedby();
        will(returnValue("eciacah"));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        oneOf(zipOutputStreamMock).write(with(any(byte[].class)));
      }
    });
  }

  private void expectCretateVersionPropertiesForTP() throws Exception {

    context.checking(new Expectations() {

      {
        // putNextEntry
        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));

        oneOf(techpackdependencyFactoryMock).get();
        will(returnValue(vec));

        oneOf(vec).iterator();
        will(returnValue(iteratorMock));

        // iterate once:
        oneOf(iteratorMock).hasNext();
        will(returnValue(true));

        oneOf(iteratorMock).next();
        will(returnValue(techpackdependencyMock));

        oneOf(techpackdependencyMock).getTechpackname();
        will(returnValue("DC_E_BSS"));

        oneOf(techpackdependencyMock).getVersion();
        will(returnValue("R4A"));

        oneOf(iteratorMock).hasNext();
        will(returnValue(false));

        oneOf(versioningMock).getLicensename();
        will(returnValue("license name"));

        oneOf(versioningMock).getLockedby();
        will(returnValue("eciacah"));

        oneOf(versioningMock).getTechpack_version();
        will(returnValue("R4A"));

        oneOf(limitedTextFieldMock).getText();
        will(returnValue("100"));

        oneOf(zipOutputStreamMock).write(with(any(byte[].class)));
      }
    });
  }

  private void expectEncryptTechpackInstallFile(final String techpackType) {
    context.checking(new Expectations() {

      {
        oneOf(checkBoxMock).isSelected();
        will(returnValue(true));

        allowing(versioningMock).getTechpack_type();
        will(returnValue(techpackType));

        oneOf(dataModelControllerMock).getUser();
        will(returnValue(userMock));

        oneOf(userMock).getPrivateKeyMod();
        will(returnValue(new BigInteger("1")));

        oneOf(userMock).getPrivateKeyExp();
        will(returnValue(new BigInteger("1")));

        oneOf(zipCrypterMock).execute();

      }
    });
  }

}
