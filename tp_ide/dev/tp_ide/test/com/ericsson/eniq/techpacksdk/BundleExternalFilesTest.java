/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk;

import static com.ericsson.eniq.techpacksdk.ZipEntryMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;



/**
 * @author eemecoy
 * 
 */
public class BundleExternalFilesTest extends BaseUnitTestX {

  private static final String EXTERNAL_FILES = "externalFiles";
  private static final int EXPECTED_BUFFER_SIZE = 2048;
  protected static final int END_OF_FILE = -1;
  public Map<String, File> mockedFileObjects = new HashMap<String, File>();
  public BufferedInputStream mockedBufferedInputStream;
  Logger mockedLogger;
  public Properties mockedProperties;
  private static final String VOB_POINTER_FILE_NAME = System
      .getProperty("user.home")
      + File.separator + "techPackIDE_pointToVOB.txt";
  protected static final String techPacksInVob = "someViewRoot"
      + File.separator + "eniq_events" + File.separator + "techPacks";

  @Before
  public void setUp() {
    // bit of a hack - as creating same mock objects for each test, and jmock
    // doesn't like this, so recreating jmock instead
    recreateMockeryContext();
    createAndIgnoreLogger();
    mockedBufferedInputStream = context.mock(BufferedInputStream.class);
    mockedProperties = context.mock(Properties.class);
  }

  @Test
  public void testWhenLocalFileIsntPresentToPointToVobs()
      throws FileNotFoundException, IOException {
    File mockedFileObject = createMockedFileObject(VOB_POINTER_FILE_NAME);
    expectThisFileToExist(mockedFileObject, false);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles("",
        mockedLogger);
    assertThat(objToTest.areExternalFilesPresentToBundle(), is(false));
  }

  @Test
  public void testZipExternalFilesLayeredDirectoryStructure()
      throws IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "myTechPack";
    String parentFolderName = techPacksInVob + File.separator + techPackName
        + File.separator + EXTERNAL_FILES;

    // expectatations on the parent folder
    File mockedFileObject = createMockedFileObject(parentFolderName);
    String directoryInExternalFiles = "bin";
    String[] fileListToReturn = new String[] { directoryInExternalFiles };
    expectAnLSOnThisFolder(mockedFileObject, fileListToReturn);

    // expectations on the subfolder within the directory structure
    String subFolderInExternalFiles = parentFolderName + File.separator
        + directoryInExternalFiles;
    File mockedSubFolder = createMockedFileObject(subFolderInExternalFiles);
    expectIsDirectoryOnThis(mockedSubFolder, true);
    String sqlFile = "something.sql";
    String[] filesInSubFolder = new String[] { sqlFile };
    expectAnLSOnThisFolder(mockedSubFolder, filesInSubFolder);

    // expectations on the file itself
    File mockedFileObjectForSQLFile = createMockedFileObject(subFolderInExternalFiles
        + File.separator + sqlFile);
    expectIsDirectoryOnThis(mockedFileObjectForSQLFile, false);

    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);
    String rootFolderInZip = "configurationFiles";
    String expectedPathInZip = rootFolderInZip + File.separator
        + directoryInExternalFiles;
    ZipOutputStream zipOutputStream = setUpExpectationsForAllStreams(sqlFile,
        expectedPathInZip);

    objToTest.zipExternalFilesAndFoldersForTechPack(rootFolderInZip,
        zipOutputStream);
  }

  @Test
  public void testZipExternalFilesFlatDirectoryStructure() throws IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "myTechPack";
    String parentFolderName = techPacksInVob + File.separator + techPackName
        + File.separator + EXTERNAL_FILES;
    File mockedFileObject = createMockedFileObject(parentFolderName);
    String fileName = "a file to compress";
    String[] fileListToReturn = new String[] { fileName };
    expectAnLSOnThisFolder(mockedFileObject, fileListToReturn);
    File actualFile = createMockedFileObject(parentFolderName + File.separator
        + fileName);
    expectIsDirectoryOnThis(actualFile, false);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);

    String baseFolderToExtractOutTo = "configurationFiles";
    ZipOutputStream zipOutputStream = setUpExpectationsForAllStreams(fileName,
        baseFolderToExtractOutTo);

    objToTest.zipExternalFilesAndFoldersForTechPack(baseFolderToExtractOutTo,
        zipOutputStream);
  }

  @Test
  public void testareExternalFilesPresentToBundleWhenUserHasCreatedTechPackSpecificFolderButTheresNoFilesInIt()
      throws FileNotFoundException, IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "a tech pack";
    File mockedTechPackSpecificFolder = createMockedFileObject(techPacksInVob
        + File.separator + techPackName + File.separator + EXTERNAL_FILES);
    expectThisFileToExist(mockedTechPackSpecificFolder, true);
    expectThisFileToBeADirectory(mockedTechPackSpecificFolder);
    String[] fileListToReturn = new String[] {};
    expectAnLSOnThisFolder(mockedTechPackSpecificFolder, fileListToReturn);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);
    assertThat(objToTest.areExternalFilesPresentToBundle(), is(false));
  }

  private void setUpEverythingForLocalFilePointingToVOBRoot() {
    File mockedFileObject = createMockedFileObject(VOB_POINTER_FILE_NAME);
    expectThisFileToExist(mockedFileObject, true);
    expectGetOnPropertiesForVobLocation();
  }

  private void expectGetOnPropertiesForVobLocation() {
    context.checking(new Expectations() {
      {
        allowing(mockedProperties).get("PATH_TO_TECH_PACK_FILES");
        will(returnValue(techPacksInVob));
      }
    });

  }

  @Test
  public void testareExternalFilesPresentToBundleWhenUserHasCreatedTechPackSpecificFolderButAsAFile()
      throws FileNotFoundException, IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "a tech pack";
    File mockedTechPackSpecificFolder = createMockedFileObject(techPacksInVob
        + File.separator + techPackName + File.separator + EXTERNAL_FILES);
    expectThisFileToExist(mockedTechPackSpecificFolder, true);
    expectIsDirectoryOnThis(mockedTechPackSpecificFolder, false);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);
    assertThat(objToTest.areExternalFilesPresentToBundle(), is(false));
  }

  @Test
  public void testareExternalFilesPresentToBundleWhenUserHasntCreatedTechPackSpecificFolder()
      throws FileNotFoundException, IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "a tech pack";
    File mockedTechPackSpecificFolder = createMockedFileObject(techPacksInVob
        + File.separator + techPackName + File.separator + EXTERNAL_FILES);
    expectThisFileToExist(mockedTechPackSpecificFolder, false);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);
    assertThat(objToTest.areExternalFilesPresentToBundle(), is(false));
  }

  @Test
  public void testareExternalFilesPresentToBundleWhenItIs()
      throws FileNotFoundException, IOException {
    setUpEverythingForLocalFilePointingToVOBRoot();
    String techPackName = "DIM_E_SGEH";
    File mockedTechPackSpecificFolder = createMockedFileObject(techPacksInVob
        + File.separator + techPackName + File.separator + EXTERNAL_FILES);
    expectThisFileToExist(mockedTechPackSpecificFolder, true);
    expectThisFileToBeADirectory(mockedTechPackSpecificFolder);
    String[] fileListToReturn = new String[] { "a file" };
    expectAnLSOnThisFolder(mockedTechPackSpecificFolder, fileListToReturn);
    BundleExternalFiles objToTest = new StubbedBundleExternalFiles(
        techPackName, mockedLogger);
    assertThat(objToTest.areExternalFilesPresentToBundle(), is(true));
  }

  // private methods for expectations on streams

  /**
   * @param fileName
   * @param pathInZipFile
   * @return
   * @throws IOException
   */
  private ZipOutputStream setUpExpectationsForAllStreams(String fileName,
      String pathInZipFile) throws IOException {
    ZipOutputStream zipOutputStream = context.mock(ZipOutputStream.class);
    expectAPutNextEntry(zipOutputStream, fileName, pathInZipFile);
    int countToReturn = 23;
    expectReadOnBufferedInputStream(countToReturn);
    expectCloseOnBufferedInputStream();
    expectWriteOnZipOutputStream(zipOutputStream, countToReturn);
    return zipOutputStream;
  }

  /**
   * @throws IOException
   * 
   */
  private void expectCloseOnBufferedInputStream() throws IOException {
    context.checking(new Expectations() {
      {
        one(mockedBufferedInputStream).close();
      }
    });

  }

  /**
   * @param zipOutputStream
   * @param countToReturn
   * @throws IOException
   */
  private void expectWriteOnZipOutputStream(
      final ZipOutputStream zipOutputStream, final int countToReturn)
      throws IOException {
    context.checking(new Expectations() {
      {
        one(zipOutputStream).write(with(any(byte[].class)), with(equal(0)),
            with(equal(countToReturn)));
      }
    });

  }

  /**
   * @throws IOException
   * 
   */
  private void expectReadOnBufferedInputStream(final int countToReturn)
      throws IOException {
    context.checking(new Expectations() {
      {
        one(mockedBufferedInputStream).read(with(any(byte[].class)),
            with(equal(0)), with(equal(EXPECTED_BUFFER_SIZE)));
        will(returnValue(countToReturn));
        one(mockedBufferedInputStream).read(with(any(byte[].class)),
            with(equal(0)), with(equal(EXPECTED_BUFFER_SIZE)));
        will(returnValue(END_OF_FILE));
      }
    });

  }

  /**
   * @param zipOutputStream
   * @param fileName
   * @param baseFolderToExtractOutTo
   * @throws IOException
   */
  private void expectAPutNextEntry(final ZipOutputStream zipOutputStream,
      String fileName, String baseFolderToExtractOutTo) throws IOException {
    final ZipEntry expectedZipEntry = new ZipEntry(baseFolderToExtractOutTo
        + File.separator + fileName);
    context.checking(new Expectations() {
      {
        one(zipOutputStream).putNextEntry(
            with(zipEntryEquals(expectedZipEntry)));
      }
    });

  }

  // expectations and mocking on files

  /**
   * @return
   */
  private File createMockedFileObject(String fileNameToMock) {
    File mockedFileObject = context.mock(File.class, fileNameToMock);
    setUpGetPathOnThisFile(mockedFileObject, fileNameToMock);
    mockedFileObjects.put(fileNameToMock, mockedFileObject);
    return mockedFileObject;
  }

  /**
   * @param fileNameToMock
   * @param mockedFileObject
   * 
   */
  private void setUpGetPathOnThisFile(final File mockedFileObject,
      final String fileNameToMock) {
    context.checking(new Expectations() {
      {
        allowing(mockedFileObject).getAbsolutePath();
        will(returnValue(fileNameToMock));
      }
    });
  }

  /**
   * @param mockedFileObject
   */
  private void expectThisFileToBeADirectory(final File mockedFileObject) {
    expectIsDirectoryOnThis(mockedFileObject, true);
  }

  /**
   * @param mockedFileObject
   * @param toExist
   */
  private void expectThisFileToExist(final File mockedFileObject,
      final boolean toExist) {
    context.checking(new Expectations() {
      {
        allowing(mockedFileObject).exists();
        will(returnValue(toExist));
      }
    });

  }

  /**
   * @param mockedFolder
   * @param fileListToReturn
   */
  private void expectAnLSOnThisFolder(final File mockedFolder,
      final String[] fileListToReturn) {
    context.checking(new Expectations() {
      {
        one(mockedFolder).list();
        will(returnValue(fileListToReturn));
      }
    });

  }

  /**
   * @param mockedFileObject
   * @param toBeADirectory
   */
  private void expectIsDirectoryOnThis(final File mockedFileObject,
      final boolean toBeADirectory) {
    context.checking(new Expectations() {
      {
        one(mockedFileObject).isDirectory();
        will(returnValue(toBeADirectory));
      }
    });

  }

  private void createAndIgnoreLogger() {
    mockedLogger = context.mock(Logger.class);
    context.checking(new Expectations() {
      {
        ignoring(mockedLogger);
      }
    });
  }

  class StubbedBundleExternalFiles extends BundleExternalFiles {

    /**
     * @param inTechPackName
     * @param logger
     */
    public StubbedBundleExternalFiles(String inTechPackName, Logger logger) {
      super(inTechPackName, logger);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.techpacksdk.BundleExternalFiles#createFileObject(java
     * .lang.String)
     */
    @Override
    File createFileObject(String fileName) {
      File mockedFile = mockedFileObjects.get(fileName);
      checkForNull(fileName, mockedFile);
      return mockedFile;
    }

    /**
     * @param fileName
     * @param mockedFile
     */
    private void checkForNull(String fileName, File mockedFile) {
      if (mockedFile == null) {
        fail("No mock has been created for " + fileName);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.techpacksdk.BundleExternalFiles#createFileObject(java
     * .io.File, java.lang.String)
     */
    @Override
    File createFileObject(File parentFolder, String fileName) {
      String fullFileName = parentFolder.getAbsolutePath() + File.separator
          + fileName;
      //System.out.println("full file name: " + fullFileName);
      for (String what : mockedFileObjects.keySet()) {
        //System.out.println(what);
      }

      File mockedFile = mockedFileObjects.get(fullFileName);
      checkForNull(fullFileName, mockedFile);
      return mockedFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.techpacksdk.BundleExternalFiles#createBufferedInputStream
     * (java.lang.String, int)
     */
    @Override
    BufferedInputStream createBufferedInputStream(String folderName,
        String fileName, int bufferSize) throws FileNotFoundException {
      assertThat(bufferSize, is(2048));
      return mockedBufferedInputStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ericsson.eniq.techpacksdk.BundleExternalFiles#createPropertiesObject
     * ()
     */
    @Override
    Properties createPropertiesObjectAndLoadFile() {
      return mockedProperties;
    }

  }

}
