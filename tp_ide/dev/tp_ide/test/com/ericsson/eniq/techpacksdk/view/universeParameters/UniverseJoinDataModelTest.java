package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;


/**
 * Tests for UniverseJoinDataModel.
 * @author ECIACAH
 */
public class UniverseJoinDataModelTest extends TestCase {
  //Test instance:
  private UniverseJoinDataModel testInstance = null;

  // Mock context:
  protected Mockery context = new JUnit4Mockery();
  {
    // we need to mock classes, not just interfaces.
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }

  // Mocks:
  private DataModelController dmcMock = null;
  private Versioning versioningMock = null;
  private UniverseTablesDataModel unvTableDataModelMock = null;

  @Override
  @Before
  protected void setUp() {
    dmcMock = context.mock(DataModelController.class);
    versioningMock = context.mock(Versioning.class);
    unvTableDataModelMock = context.mock(UniverseTablesDataModel.class);
    testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
  }

  @Override
  @After
  protected void tearDown() {
    // Reset the expectations:
    dmcMock = null;
    versioningMock = null;
    unvTableDataModelMock = null;
  }

  /**
   * Test validateExtension(). Checks text entered in universe extension field.
   * Universe extensions defined in the tech pack are all in upper case.
   * @throws IOException
   */
  @Test
  public void testValidateExtensionsUpperCase() throws IOException {

    // Override getExtensions() to return test array.
    // Extensions defined in the tech pack General tab are all in upper case:
    UniverseJoinDataModel testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
    expectGetExtensions(new String[] {"A", "B", "C", "D"});
    checkExtensions(testInstance);
  }

  /**
   * Test validateExtension(). Checks text entered in universe extension field.
   * Universe extensions defined in the tech pack are all in lower case.
   * @throws IOException
   */
  @Test
  public void testValidateExtensionsLowerCase() throws IOException {

    // Override getExtensions() to return test array.
    // Extensions defined in the tech pack General tab are all in lower case:
    UniverseJoinDataModel testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
    expectGetExtensions(new String[] {"a", "b", "c", "d"});
    checkExtensions(testInstance);
  }

  /**
   * Test validateExtension(). Checks text entered in universe extension field.
   * Universe extensions defined in the tech pack are in different case.
   * @throws IOException
   */
  @Test
  public void testValidateExtensionsMixedCase() throws IOException {

    // Override getExtensions() to return test array.
    // Extensions defined in the tech pack General tab are in mixed case:
    UniverseJoinDataModel testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
    expectGetExtensions(new String[] {"A", "B", "c", "d"});
    checkExtensions(testInstance);
  }

  /**
   * Check the extensions entered by the user against the extensions defined for the tech pack.
   * The "ALL", "A", "a" etc. values here are the values entered by the user.
   * @param testInstance
   */
  private void checkExtensions(UniverseJoinDataModel testInstance) {
    List<String> result = testInstance.validateExtensions("", new Vector<String>());
    assertTrue("validateExtension should return no error message for an empty string", result.size() == 0);

    result = testInstance.validateExtensions(null, new Vector<String>());
    assertTrue("validateExtension should return no error message for an empty string", result.size() == 0);

    result = testInstance.validateExtensions("ALL", new Vector<String>());
    assertTrue("validateExtension should return no error message for ALL", result.size() == 0);

    result = testInstance.validateExtensions("A", new Vector<String>());
    assertTrue("validateExtension should return no error message for single universe A", result.size() == 0);

    result = testInstance.validateExtensions("a", new Vector<String>());
    assertTrue("validateExtension should return no error message for single universe a (lower case)", result.size() == 0);

    result = testInstance.validateExtensions("a, b", new Vector<String>());
    assertTrue("validateExtension should return no error message for a list of universes a, b (lower case)", result.size() == 0);

    result = testInstance.validateExtensions("E", new Vector<String>());
    assertFalse("validateExtension should return an error message for an extension that is not defined in the tech pack", result.size() == 0);

    result = testInstance.validateExtensions("A, B, C, ALL", new Vector<String>());
    assertTrue("validateExtension should return no error message for an extension that is not defined in the tech pack", result.size() == 0);

    result = testInstance.validateExtensions("A, B, C, ALL", new Vector<String>());
    assertTrue("validateExtension should return no error message for an extension that is not defined in the tech pack", result.size() == 0);

    // Test garbage input:
    result = testInstance.validateExtensions("A, , ,..", new Vector<String>());
    assertFalse("validateExtension should return error message for incorrect input", result.size() == 0);

    // Test several extensions that don't exist:
    result = testInstance.validateExtensions("L,M,N,O,P", new Vector<String>());
    assertFalse("validateExtension should return an error message for incorrect input", result.size() == 0);

    result = testInstance.validateExtensions(null, null);
    assertTrue("validateExtension should no error message if both parameters are null", result.size() == 0);
  }

  /**
   * Test validateExtension() when there are no universe extensions defined in the tech pack
   * @throws IOException
   */
  @Test
  public void testDoCreatePackageNoExtDefined() throws Exception {

    // Override getExtensions() to return test array:
    testInstance = new UniverseJoinDataModel(dmcMock, versioningMock) {

      protected String[] getExtensions() {
        return new String[] {};
      }
    };

    List<String> result = testInstance.validateExtensions("A,B,C,D", new Vector<String>());
    assertTrue("validateExtension should return no error message if extensions are not defined", result.size() == 0);
  }

  /**
   * Tests getting the extensions that are defined in the tech pack.
   * The extensions should be converted to upper case.
   * @throws Exception
   */
  @Test
  public void testGetExtensions() throws Exception {

    // Override getExtensions() to return test array:
    testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
    // Test extensions for the tech pack:
    final String[] testExtensions = new String[] {"a", "b", "c", "d"};
    expectGetExtensions(testExtensions);

    assertTrue(Arrays.asList(testInstance.getExtensions()).contains("A"));
  }

  /**
   * Test the situation where no extensions are defined in the tech pack.
   * @throws Exception
   */
  @Test
  public void testGetExtensionsNoneDefined() throws Exception {

    // Override getExtensions() to return test array:
    testInstance = new UniverseJoinDataModel(dmcMock, versioningMock);
    // Test extensions for the tech pack:
    String[] testExtensions = new String[] {};
    expectGetExtensions(testExtensions);

    assertNotNull("List of extensions should not be null if no extensions are defined in the tech pack",
        testInstance.getExtensions());
  }

  /**
   * Return value should just be an empty array if extensions in the tech pack are null.
   * Null value should be handled correctly.
   * @throws Exception
   */
  @Test
  public void testGetExtensionsNullReturned() throws Exception {
    // Test extensions for the tech pack:
    String[] testExtensions = null;
    expectGetExtensions(testExtensions);
    assertNotNull("List of extensions should be empty if extensions in the tech pack are null",
        testInstance.getExtensions());
  }

  /**
   * Return value should just be an empty array if getting extensions in the tech pack throws an exception.
   * @throws Exception
   */
  @Test
  public void testGetExtensionsExceptionThrown() throws Exception {

    context.checking(new Expectations() {
      {
        allowing(dmcMock).getUniverseTablesDataModel();
        will(returnValue(unvTableDataModelMock));
        allowing(unvTableDataModelMock).getUniverseExtensions(with(any(String.class)));
        will(throwException(new Exception("Error getting tech pack extensions!")));

        allowing(versioningMock).getVersionid();
        will(returnValue("dummy version id"));
      }
    });
    assertNotNull("List of extensions should be empty if extensions in the tech pack are null",
        testInstance.getExtensions());
  }

  /**
   * Sets up standard expectations for methods that will be called in getExtensions().
   * @param testExtensions
   */
  private void expectGetExtensions(final String[] testExtensions) {
    context.checking(new Expectations() {
      {
        allowing(dmcMock).getUniverseTablesDataModel();
        will(returnValue(unvTableDataModelMock));
        allowing(unvTableDataModelMock).getUniverseExtensions(with(any(String.class)));
        will(returnValue(testExtensions));

        allowing(versioningMock).getVersionid();
        will(returnValue("dummy version id"));
      }
    });
  }

}
