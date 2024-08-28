package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.VelocityContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.distocraft.dc5000.install.ant.ZipCrypter;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.User;

public class UniverseBOPackageDialogTest extends TestCase {

  // Test instance:
  private UniverseBOPackageDialog testInstance = null;

  // Mock context:
  protected Mockery context = new JUnit4Mockery();
  {
    // we need to mock classes, not just interfaces.
    context.setImposteriser(ClassImposteriser.INSTANCE);
  }

  private Versioning versioningMock;

  private User userMock;

  private javax.swing.JTextField textFieldMock;

  private ZipOutputStream zipOutputStreamMock;

  private java.io.File mockFile;

  private FileInputStream fisMock;

  private ZipCrypter zipCrypterMock;

  @Override
  @After
  protected void tearDown() {
    context.assertIsSatisfied();
  }

  @Override
  @Before
  protected void setUp() {
    // Create the mocks:
    createMocks();

    testInstance = new UniverseBOPackageDialog(versioningMock, userMock, textFieldMock) {

      private static final long serialVersionUID = 1L;

      protected ZipOutputStream createZipOutputStream(String outputFilename) throws FileNotFoundException {
        return zipOutputStreamMock;
      }

      protected File createFile(String directory) {
        return mockFile;
      }

      protected File createFile(File parent, String child) {
        return mockFile;
      }

      protected FileInputStream createFileInputStream(File file) throws FileNotFoundException {
        return fisMock;
      }

      protected boolean mergeVelocityTemplate(StringWriter strw, VelocityContext context) {
        return true;
      }

      protected ZipCrypter createZipCrypter() {
        return zipCrypterMock;
      }
    };
  }

  private void createMocks() {
    versioningMock = context.mock(Versioning.class);
    userMock = context.mock(User.class);
    textFieldMock = context.mock(javax.swing.JTextField.class);
    zipOutputStreamMock = context.mock(ZipOutputStream.class);
    mockFile = context.mock(java.io.File.class);
    fisMock = context.mock(java.io.FileInputStream.class);
    zipCrypterMock = context.mock(ZipCrypter.class);
  }

  @Test
  public void testDoCreatePackageErrorEncrypting() throws IOException {

    context.checking(new Expectations() {

      {
        // get universe folder:
        exactly(2).of(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\universe"));
        // get report folder:
        exactly(2).of(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\report"));

        // Allow writing to the ZipOutputStream:
        allowing(zipOutputStreamMock)
            .write(with(any(byte[].class)), with(any(Integer.class)), with(any(Integer.class)));

        exactly(2).of(zipOutputStreamMock).close();
      }
    });

    expectGetBuildNumber();

    expectGetOutputFilename(2);

    expectAddDirectoryToZip(4);

    expectCreateVersionProperties();
    expectCreateVersionProperties();

    // expectEncryptZipFile();
    context.checking(new Expectations() {

      {
        oneOf(zipCrypterMock).setFile(with(any(String.class)));

        oneOf(zipCrypterMock).setCryptType(with(any(String.class)));

        oneOf(zipCrypterMock).setIsPublicKey(with(any(String.class)));

        oneOf(userMock).getPrivateKeyMod();
        will(returnValue(new BigInteger("1")));
        oneOf(zipCrypterMock).setKeyModulate(with(any(String.class)));

        oneOf(userMock).getPrivateKeyExp();
        will(returnValue(new BigInteger("1")));
        oneOf(zipCrypterMock).setKeyExponent(with(any(String.class)));

        oneOf(zipCrypterMock).execute();
        will(throwException(new BuildException("Error zipping file")));
      }
    });

    Assert.assertFalse("doCreatePackage should return false if there is an error creating the zip file", testInstance.doCreatePackage());
  }

  @Test
  public void testDoCreatePackage() throws IOException {

    context.checking(new Expectations() {

      {
        // get universe folder:
        exactly(2).of(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\universe"));
        // get report folder:
        exactly(2).of(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\report"));

        // Allow writing to the ZipOutputStream:
        allowing(zipOutputStreamMock)
            .write(with(any(byte[].class)), with(any(Integer.class)), with(any(Integer.class)));

        exactly(2).of(zipOutputStreamMock).close();
      }
    });

    expectGetBuildNumber();

    expectGetOutputFilename(2);

    expectAddDirectoryToZip(4);

    expectCreateVersionProperties();
    expectCreateVersionProperties();

    expectEncryptZipFile();

    Assert.assertTrue("doCreatePackage should return true", testInstance.doCreatePackage());
  }

  private void expectEncryptZipFile() {
    context.checking(new Expectations() {

      {
        oneOf(zipCrypterMock).setFile(with(any(String.class)));

        oneOf(zipCrypterMock).setCryptType(with(any(String.class)));

        oneOf(zipCrypterMock).setIsPublicKey(with(any(String.class)));

        oneOf(userMock).getPrivateKeyMod();
        will(returnValue(new BigInteger("1")));
        oneOf(zipCrypterMock).setKeyModulate(with(any(String.class)));

        oneOf(userMock).getPrivateKeyExp();
        will(returnValue(new BigInteger("1")));
        oneOf(zipCrypterMock).setKeyExponent(with(any(String.class)));

        oneOf(zipCrypterMock).execute();
      }
    });
  }

  private void expectCreateVersionProperties() throws IOException {
    context.checking(new Expectations() {

      {
        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));

        // Get build number:
        oneOf(textFieldMock).getText();
        will(returnValue("100"));

        // Get build tag:
        oneOf(textFieldMock).getText();
        will(returnValue("b100"));

        oneOf(versioningMock).getLicensename();
        will(returnValue("license name"));

        oneOf(userMock).getName();
        will(returnValue("tester"));

        oneOf(versioningMock).getTechpack_version();
        will(returnValue("R4A"));

        oneOf(zipOutputStreamMock).write(with(any(byte[].class)));
      }
    });
  }

  private void expectAddDirectoryToZip(int numberOfDirectories) throws IOException {
    // Test simple case where there is a directory with a file:
    for (int i = 0; i < numberOfDirectories; i++) {
      expectZipDirForDirectory();
      expectZipDirForFile("file1.txt");
    }
  }

  private void expectZipDirForFile(final String filename) throws IOException {
    context.checking(new Expectations() {

      {
        oneOf(mockFile).list();
        will(returnValue(new String[] { filename }));

        // This is a file not a directory:
        oneOf(mockFile).isDirectory();
        will(returnValue(false));

        oneOf(mockFile).getName();
        will(returnValue(filename));

        oneOf(zipOutputStreamMock).putNextEntry(with(any(ZipEntry.class)));

        // reading & writing:
        exactly(10).of(fisMock).read(with(any(byte[].class)));
        will(returnValue(10));

        oneOf(fisMock).read(with(any(byte[].class)));
        will(returnValue(-1));

        oneOf(fisMock).close();
      }
    });
  }

  private void expectZipDirForDirectory() {
    context.checking(new Expectations() {

      {
        // Directory will include a directory and a file:
        oneOf(mockFile).list();
        will(returnValue(new String[] { "dir1" }));

        oneOf(mockFile).isDirectory();
        will(returnValue(true));

        oneOf(mockFile).getName();
        will(returnValue("dir1"));

        oneOf(mockFile).getPath();
        will(returnValue("dir1"));
        // zipDir called recursively
      }
    });
  }

  private void expectGetBuildNumber() {
    context.checking(new Expectations() {

      {
        // get build number:
        oneOf(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\"));
      }
    });
  }

  private void expectGetOutputFilename(final int numberOfCalls) {
    context.checking(new Expectations() {

      {
        // get working directory:
        exactly(numberOfCalls).of(textFieldMock).getText();
        will(returnValue("c:\\test\\directory\\"));

        exactly(numberOfCalls).of(versioningMock).getTechpack_version();
        will(returnValue("R4A"));
      }
    });
  }
}
