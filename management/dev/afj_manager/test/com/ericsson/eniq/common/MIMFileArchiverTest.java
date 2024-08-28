/**
 * 
 */
package com.ericsson.eniq.common;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.afj.common.MIMFileArchiver;

/**
 * @author eheijun
 * 
 */
public class MIMFileArchiverTest {

  private static boolean testDirCreated = false;

  private static File testDir;

  private File srcFile;

  private File nonExistingFile;

  private File writeProtectedFile;

  private File dstDir;

  private File dstFile;

  private File nonExistingDir;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    testDir = new File("test");
    if (!testDir.exists()) {
      testDir.mkdir();
      testDirCreated = true;
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    if (testDirCreated) {
      testDir.delete();
    }
  }
  

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    srcFile = new File(testDir, "testMIM.xml");
    srcFile.createNewFile();
    nonExistingFile = new File(testDir, "doesNotExistsMIM.xml");
    writeProtectedFile = new File(testDir, "writeProtectedMIM.xml");
    writeProtectedFile.createNewFile();
    writeProtectedFile.setReadOnly();
    dstDir = new File(testDir, "archive");
    dstDir.mkdir();
    nonExistingDir = new File(testDir, "doesNotExists");
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    if (dstFile != null && dstFile.exists()) {
      dstFile.delete();
    }
    if (srcFile != null && srcFile.exists()) {
      srcFile.delete();
    }
    if (writeProtectedFile != null && writeProtectedFile.exists()) {
      writeProtectedFile.delete();
    }
    if (dstDir != null && dstDir.exists()) {
      dstDir.delete();
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.MIMFileArchiver#backupFile(java.io.File, java.io.File)}.
   */
  @Test
  public void testBackupFile() {
	  final MIMFileArchiver fileArchiver = new MIMFileArchiver();
    try {
      dstFile = fileArchiver.backupFile(srcFile, dstDir);
      assertTrue(dstFile != null);
      assertTrue(dstFile.exists());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.MIMFileArchiver#backupFile(java.io.File, java.io.File)}.
   * 
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testUnwritableBackupFolder() throws IOException {
	  final MIMFileArchiver fileArchiver = new MIMFileArchiver();
    dstFile = fileArchiver.backupFile(srcFile, nonExistingDir);
    fail("File was backed up into unwritable folder.");
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.MIMFileArchiver#backupFile(java.io.File, java.io.File)}.
   * 
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testNonExistingBackupFile() throws IOException {
	  final MIMFileArchiver fileArchiver = new MIMFileArchiver();
    dstFile = fileArchiver.backupFile(nonExistingFile, dstDir);
    fail("Non existing file was backed up.");
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.common.MIMFileArchiver#backupFile(java.io.File, java.io.File)}.
   * 
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testWriteProtectedBackupFile() throws IOException {
	  final MIMFileArchiver fileArchiver = new MIMFileArchiver();
    dstFile = fileArchiver.backupFile(writeProtectedFile, dstDir);
    fail("Write protected file was backed up.");
  }

}
