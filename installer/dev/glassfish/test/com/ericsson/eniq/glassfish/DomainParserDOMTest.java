package com.ericsson.eniq.glassfish;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class DomainParserDOMTest {

  private static File DOMAIN_SAMPLE_1 = null;
  private static final String DOMAIN_FILENAME = "domain.xml";
  private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

  @BeforeClass
  public static void beforeClass() throws URISyntaxException {
    final URL url = ClassLoader.getSystemResource("xml/domain-1.xml");
    if (url == null) {
      fail("Can't find domain-1.xml on CLASSPATH");
    }
    DOMAIN_SAMPLE_1 = new File(url.toURI());
    if (!TMP_DIR.exists() && !TMP_DIR.mkdirs()) {
      fail("Couldn't create temporary directory " + TMP_DIR.getPath());
    }
  }

  @AfterClass
  public static void afterClass() {
    delete(TMP_DIR);
  }

  @Test
  public void testParseArgs() throws Exception {
    final DomainParserDOM parser = new DomainParserDOM();
    try {
      parser.execute(null);
      fail("Error regarding no arguements should have been thrown!");
    } catch (DomainParserException e) {
      // Ok
    }
    try {
      parser.execute(new String[]{});
      fail("Error regarding no arguements should have been thrown!");
    } catch (DomainParserException e) {
      // Ok
    }


    try {
      parser.execute(new String[]{"--d"});
      fail("No error regarding no Glassfish basedir arguement!");
    } catch (DomainParserException e) {
      //OK
    }

    File updatedFile = setupDomain("domain1", DOMAIN_SAMPLE_1);
    if (!updatedFile.delete()) {
      fail("Setup failed!!!");
    }

    String[] args = new String[]{"--b", TMP_DIR.getPath(), "--d"};
    try {
      parser.execute(args);
      fail("Error regarding non-existant domain file should have been generated!");
    } catch (DomainParserException e) {
      //OK
    }

    File updatedFile1 = setupDomain("domain1", DOMAIN_SAMPLE_1);
    if (!updatedFile1.delete()) {
        fail("Setup failed!!!");
      }
    String[] args1 = new String[]{"--b", TMP_DIR.getPath(), "--d"};
    try {
      parser.execute(args1);
      fail("No error regarding --o flag and no subsiquent options?!?!");
    } catch (DomainParserException e) {
      //OK
    }
  }

  @Test
  public void testListOptions() throws Exception {
    final String domainName = "domain1";
    setupDomain(domainName, DOMAIN_SAMPLE_1);
    final DomainParserDOM parser = new DomainParserDOM();
    final String[] args = {
      "--b", TMP_DIR.getPath(),
      "--d", domainName, "--l"};
    parser.execute(args);
  }

  @Test
  public void testParseNoChanges() throws Exception {
    final String domainName = "domain1";
    final File updatedFile = setupDomain(domainName, DOMAIN_SAMPLE_1);
    final DomainParserDOM parser = new DomainParserDOM();
    final String[] args = {
      "--b", TMP_DIR.getPath(),
      "--d", domainName, "--o",
      "-d32", "-XX:MaxPermSize=128m"
    };
    parser.execute(args);


    final File[] files = updatedFile.getParentFile().listFiles();
    assertNotNull("Listed files should return something in " + updatedFile.getParentFile().getPath(), files);
    assertEquals("Wrong number of files found in " + updatedFile.getParentFile().getPath(), 1, files.length);
    assertEquals("Wrong file found in " + updatedFile.getParentFile().getPath(), DOMAIN_FILENAME, files[0].getName());
  }

  @Test
  public void testParse() throws Exception {
    final String domainName = "domain1";
    final File updatedFile = setupDomain(domainName, DOMAIN_SAMPLE_1);
    final DomainParserDOM parser = new DomainParserDOM();
    final String[] args = {
      "--b", TMP_DIR.getPath(),
      "--d", domainName, "--o",
      "-d64", "-server", "-Xms512m", "-Xmx1024M", "-XX:MaxPermSize=256m"
    };
    parser.execute(args);
    verifyParse(updatedFile);
  }

  @Test
  public void testParserFromMain() throws Exception {
    final String domainName = "domain1";
    final File updatedFile = setupDomain(domainName, DOMAIN_SAMPLE_1);
    final String[] args = {
      "--b", TMP_DIR.getPath(),
      "--d", domainName, "--o",
      "-d64", "-server", "-Xms512m", "-Xmx1024M", "-XX:MaxPermSize=256m"
    };
    DomainParserDOM.main(args);
    verifyParse(updatedFile);
  }

  private void verifyParse(final File domainFile) {
    final File[] files = domainFile.getParentFile().listFiles();
    assertNotNull("Listed files should return something in " + domainFile.getParentFile().getPath(), files);
    assertEquals("Wrong number of files found in " + domainFile.getParentFile().getPath(), 2, files.length);
    boolean dFile = false;
    boolean bFile = false;
    final String regex = "domain.xml_\\d{4}_\\d{2}_\\d{2}T\\d{2}_\\d{2}_\\d{2}";
    for (File f : files) {
      if (f.getName().equals(DOMAIN_FILENAME)) {
        dFile = true;
      } else if (f.getName().matches(regex)) {
        bFile = true;
      }
    }
    if (!bFile) {
      fail("No backup file generated to " + domainFile.getParentFile().getPath());
    }
    if (!dFile) {
      fail("No domain file generated to " + domainFile.getParentFile().getPath());
    }
  }


  private static boolean delete(final File file) {
    if (!file.exists()) {
      return true;
    }
    if (file.isFile()) {
      return file.delete();
    } else {
      final File[] list = file.listFiles();
      for (File f : list) {
        delete(f);
      }
      return file.delete();
    }
  }


  private File setupDomain(final String domainName, final File templateXml) throws IOException {
    final File domainDir = new File(TMP_DIR, "domains/" + domainName + "/config");
    delete(domainDir);
    if (!domainDir.exists() && !domainDir.mkdirs()) {
      fail("Failed to create domain directory " + domainDir.getPath());
    }
    final File domainFile = new File(domainDir, DOMAIN_FILENAME);
    FileChannel source = null;
    FileChannel destination = null;
    try {
      source = new FileInputStream(templateXml).getChannel();
      destination = new FileOutputStream(domainFile, false).getChannel();
      destination.transferFrom(source, 0, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null) {
        destination.close();
      }
    }
    return domainFile;
  }
}
