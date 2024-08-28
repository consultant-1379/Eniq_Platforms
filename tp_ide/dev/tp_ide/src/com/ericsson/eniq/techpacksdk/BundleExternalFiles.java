/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class to handle bundling up external files into a tech pack .tpi file.
 * 
 * The method areExternalFilesPresentToBundle() will firstly look for the file
 * TECH_PACK_IDE_POINT_TO_VOBS_FILE_NAME in the location
 * LOCATION_OF_FILE_POINTING_TO_VOBS If this file exists, it read the property
 * PATH_TO_TECH_PACK_FILES_PROPERTY_NAME from the file
 * 
 * Using this property, it will check the location:
 * PATH_TO_TECH_PACK_FILES_PROPERTY/<techPackName>/PATH_FOR_ZIPPED_ITEMS_IN_ZIP for files or
 * folders. Any files or folders it finds there will be bundled into the .tpi
 * file created at installation time in the path PATH_FOR_ZIPPED_ITEMS_IN_ZIP/
 * 
 * At install time, any files or folders found in the PATH_FOR_ZIPPED_ITEMS_IN_ZIP/ path in the
 * .tpi file will be unzipped to ENIQ server (see tasks_tp_installer.xml) for
 * more details
 * 
 * @author eemecoy
 * 
 */
class BundleExternalFiles {

  private final Logger logger;
  private final String techPackName;

  private static final int BUFFER_SIZE = 2048;
  private static final String TECH_PACK_IDE_POINT_TO_VOBS_FILE_NAME = "techPackIDE_pointToVOB.txt";
  private static final String LOCATION_OF_FILE_POINTING_TO_VOBS = System.getProperty("user.home") + File.separator + TECH_PACK_IDE_POINT_TO_VOBS_FILE_NAME;
  static final String PATH_TO_TECH_PACK_FILES_PROPERTY_NAME = "PATH_TO_TECH_PACK_FILES";
  static final String DEFAULT_OUTPUT_PATH = "DEFAULT_OUTPUT_PATH";
  public static final String PATH_FOR_ZIPPED_ITEMS_IN_ZIP = "externalFiles";
  private String externalFilesBaseDir = null;

  /**
   * 
   * @param inTechPackName
   *          name of tech pack
   * @param inLogger
   */
  BundleExternalFiles(final String inTechPackName, final Logger inLogger) {
    logger = inLogger;
    techPackName = inTechPackName;
  }

  void setExternalFilesBaseDir(final String aDir){
    this.externalFilesBaseDir = aDir;
  }

  /**
   * 
   * This method will firstly look for the file
   * TECH_PACK_IDE_POINT_TO_VOBS_FILE_NAME in the location
   * LOCATION_OF_FILE_POINTING_TO_VOBS If this file doesn't exist, it returns
   * false
   * 
   * If this file exists, it read the property
   * PATH_TO_TECH_PACK_FILES_PROPERTY_NAME from the file
   * 
   * Using this property, it will check the location:
   * PATH_TO_TECH_PACK_FILES_PROPERTY/<techPackName>/PATH_FOR_ZIPPED_ITEMS_IN_ZIP for files or
   * folders. If this folder exists, and if it finds any files or folders then
   * it returns true, otherwise false
   * 
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  boolean areExternalFilesPresentToBundle() throws FileNotFoundException,
      IOException {
    if (doesFilePointingToVOBsExist()) {
      String techPackSpecificFilesFolder = getPathToTechPackFiles();
      logger.info("Looking in " + techPackSpecificFilesFolder
          + " for files to bundle up in the .tpi file");
      final File techPackSpecificExternalFolder = createFileObject(techPackSpecificFilesFolder);
      if (techPackSpecificExternalFolder.exists()
          && techPackSpecificExternalFolder.isDirectory()) {
        final String[] filesInFolder = techPackSpecificExternalFolder.list();
        if (filesInFolder.length > 0) {
          logger
              .info("Have found external files or folders to include in tech pack, this will be zipped into the .tpi file");
          return true;
        }
      }
      logger.info("No external files or folders found in "
          + techPackSpecificFilesFolder
          + " to include in tech pack file, creation will proceed as normal");
      return false;
    }
    logger
        .info("The file "
            + LOCATION_OF_FILE_POINTING_TO_VOBS
            + " doesn't exist to point the tech pack ide to the "
            + "tech pack files stored in the VOBs, no external files will be bundled with the .tpi file.  "
            + "This has no effect on the successful creation of a tech pack");
    return false;

  }
  public static void doesBuildProprtyExist(){

  }

  /**
   * find the path to the tech pack files for the specific tech pack This is
   * made of the user specified location(specified in the file
   * LOCATION_OF_FILE_POINTING_TO_VOBS) plust the tech pack name plus
   * PATH_FOR_ZIPPED_ITEMS_IN_ZIP/
   * 
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  private String getPathToTechPackFiles() throws FileNotFoundException,
      IOException {
    Properties propertiesSpecifiedInFile = createPropertiesObjectAndLoadFile();
    String pathToAllTechPacks = (String) propertiesSpecifiedInFile
        .get(PATH_TO_TECH_PACK_FILES_PROPERTY_NAME);
    return pathToAllTechPacks + File.separator + techPackName + File.separator
        + "externalFiles";
  }

  /**
   * extracted out for unit test
   * 
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  Properties createPropertiesObjectAndLoadFile() {
    final Properties propertiesSpecifiedInFile = new Properties();
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(LOCATION_OF_FILE_POINTING_TO_VOBS);
      propertiesSpecifiedInFile.load(fis);
    } catch (IOException e) {
      // ignore...
    } finally {
      if(fis != null){
        try { fis.close(); } catch (IOException e) {/**/}
      }
    }
    if(externalFilesBaseDir != null){
      propertiesSpecifiedInFile.setProperty(PATH_TO_TECH_PACK_FILES_PROPERTY_NAME, externalFilesBaseDir);
    }
    return propertiesSpecifiedInFile;
  }

  /**
   * 
   * @return
   */
  boolean doesFilePointingToVOBsExist() {
    File fileObject = createFileObject(LOCATION_OF_FILE_POINTING_TO_VOBS);
    return fileObject.exists();
  }

  /**
   * NB - this won't check that the required parent folders exist -
   * areExternalFilesPresentToBundle() must be called first to validate this
   * 
   * This method zips all files in the location <?insert this> into the provided
   * zipOutputStream in the path pathforZippedItemsInZip
   * 
   * @param pathForZippedItemsInZip
   * @param zipOutputStream
   * @throws IOException
   */
  void zipExternalFilesAndFoldersForTechPack(
      final String pathForZippedItemsInZip,
      final ZipOutputStream zipOutputStream) throws IOException {

    String techPackSpecificFilesFolder = getPathToTechPackFiles();
    final File techPackSpecificExternalFolder = createFileObject(techPackSpecificFilesFolder);

    zipFileOrFolder(pathForZippedItemsInZip, zipOutputStream,
        techPackSpecificExternalFolder);

  }

  /**
   * recursive method - will loop thru the rootSourceFolder If it finds folders,
   * it will call this method recursively If it finds files, it will bundle them
   * into the zip file with the following path: baseForZippedItemsInZip + / +
   * <relative path from rootSourceFolder to file name>
   * 
   * @param baseForZippedItemsInZip
   * @param zipOutputStream
   * @param rootSourceFolder
   * 
   * @throws IOException
   * @throws FileNotFoundException
   */
  private void zipFileOrFolder(final String baseForZippedItemsInZip,
      final ZipOutputStream zipOutputStream, final File rootSourceFolder)
      throws IOException, FileNotFoundException {
    final String[] filesInFolder = rootSourceFolder.list();
    final byte data[] = new byte[BUFFER_SIZE];

    for (final String fileName : filesInFolder) {
      final File fileObject = createFileObject(rootSourceFolder, fileName);
      if (fileObject.isDirectory()) {
        final String folderName = fileName;
        final String pathInZip = baseForZippedItemsInZip + File.separator
            + folderName;
        zipFileOrFolder(pathInZip, zipOutputStream, fileObject);
        continue;
      }
      zipAFile(rootSourceFolder, data, fileName, baseForZippedItemsInZip,
          zipOutputStream);
    }
  }

  /**
   * @param rootSourceFolder
   * @param data
   * @param fileName
   * @param baseForZippedItemsInZip
   * @param zipOutputStream
   * @throws IOException
   * 
   */
  private void zipAFile(File rootSourceFolder, byte[] data, String fileName,
      String baseForZippedItemsInZip, ZipOutputStream zipOutputStream)
      throws IOException {

    BufferedInputStream bufferedInputStream = null;
    try {
      bufferedInputStream = createBufferedInputStream(rootSourceFolder
          .getAbsolutePath(), fileName, BUFFER_SIZE);
      final ZipEntry entry = new ZipEntry(baseForZippedItemsInZip
          + File.separator + fileName);
      zipOutputStream.putNextEntry(entry);
      int count;
      while ((count = bufferedInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
        zipOutputStream.write(data, 0, count);
      }

    } finally {
      if (bufferedInputStream != null) {
        bufferedInputStream.close();
      }
    }

  }

  /**
   * extracted out to get under test
   * 
   * @param parentFolder
   * @param fileName
   *          just file name, ie not including file path
   * @return
   */
  File createFileObject(final File parentFolder, final String fileName) {
    return new File(parentFolder, fileName);
  }

  /**
   * extracted out to get under test
   * 
   * @param fileName
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  BufferedInputStream createBufferedInputStream(final String parentFolder,
      final String fileName, final int bufferSize) throws FileNotFoundException {
    final FileInputStream fileInputStream = new FileInputStream(parentFolder
        + File.separator + fileName);
    final BufferedInputStream bufferedInputStream = new BufferedInputStream(
        fileInputStream, bufferSize);
    return bufferedInputStream;
  }

  /**
   * extracted out to get under test
   * 
   * @param fullFileName
   *          complete file name, ie including path
   * @return
   */
  File createFileObject(final String fullFileName) {
    return new File(fullFileName);
  }

}
