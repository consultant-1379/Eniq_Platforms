package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.licensing.cache.MappingDescriptor.MappingType;

public class FeatureMapperTest {

  private FeatureMapper fm;

  private static File mappingFile = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // generate a temporary file with a couple of mapping entries for testing.
    PrintWriter pw = null;

    try {
      mappingFile = File.createTempFile("mapping", "tmp");

      pw = new PrintWriter(new FileWriter(mappingFile));
                              //SPACE HERE V IS IMPORTANT, tests reloadMapping()
      pw.write("CXC4010585::INTF_DC_E_STN\n \nCXC4010585::INTF_DC_E_BSS_APG\nCXC4010585::INTF_DC_E_BSS_IOG\n");
      pw
          .write("CXC4010585::Ericsson GSM BSS PM Tech Pack::FAJ 121 1138\nCXC4010586::Ericsson WCDMA RAN PM Tech Pack::FAJ 121 1144\n");
      pw.write("CXC4010585::BO_E_BSS\nCXC4010585::BO_E_STN\nCXC4010585::BO_E_CMN_STS\n");
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } finally {
      if (pw != null) {
        pw.close();
        System.out.println("Wrote mapping data to " + mappingFile.getAbsolutePath());
      }
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    if (mappingFile != null) {
      mappingFile.deleteOnExit();
    }
  }

  @Before
  public void setUp() throws Exception {
    LicensingSettings settings = new LicensingSettings();
    settings.setMappingFiles(new String[] { mappingFile.getAbsolutePath() });
    this.fm = new FeatureMapper(settings);

    System.out.println("Initialized mapper with " + fm.getMappingSize() + " mappings");
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testMapFaj() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010586" }, MappingType.FAJ);
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010500" }, MappingType.FAJ);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);

    results = fm.map(pass);
    assertNotNull(results);
    assertTrue(results.size() == 1);
  }

  @Test
  public void testMapMultipleFaj() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010586", "CXC4010500" },
        MappingType.FAJ);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testMapEmptyFaj() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] {}, MappingType.FAJ);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testMapDescription() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010586" },
        MappingType.DESCRIPTION);
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010500" },
        MappingType.DESCRIPTION);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);

    results = fm.map(pass);
    assertNotNull(results);
    assertTrue(results.size() == 1);
  }

  @Test
  public void testMapMultipleDescription() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010586", "CXC4010500" },
        MappingType.DESCRIPTION);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testMapEmptyDescription() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] {}, MappingType.DESCRIPTION);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testMapInterface() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010585" },
        MappingType.INTERFACE);
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010500" },
        MappingType.INTERFACE);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);

    results = fm.map(pass);
    assertNotNull(results);
    // there are three dummy file interfaces. Check setUpBeforeClass();
    assertTrue(results.size() == 3);
  }

  @Test
  public void testMapMultipleInterface() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010586", "CXC4010585" },
        MappingType.INTERFACE);

    Vector<String> results;
    results = fm.map(pass);
    assertNotNull(results);
    assertTrue(results.size() == 3);
  }

  @Test
  public void testMapEmptyInterface() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] {}, MappingType.INTERFACE);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testMapReportPackage() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010585" },
        MappingType.REPORTPACKAGE);
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] { "CXC4010500" },
        MappingType.REPORTPACKAGE);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);

    results = fm.map(pass);
    assertNotNull(results);
    // there are three dummy file interfaces. Check setUpBeforeClass();
    assertTrue(results.size() == 3);
  }

  @Test
  public void testMapMultipleReportPackages() {
    final DefaultMappingDescriptor pass = new DefaultMappingDescriptor(new String[] { "CXC4010586", "CXC4010585" },
        MappingType.REPORTPACKAGE);

    Vector<String> results;
    results = fm.map(pass);
    assertNotNull(results);
    assertTrue(results.size() == 3);
  }

  @Test
  public void testMapEmptyReportPackage() {
    final DefaultMappingDescriptor fail = new DefaultMappingDescriptor(new String[] {}, MappingType.REPORTPACKAGE);

    Vector<String> results;
    results = fm.map(fail);
    assertNull(results);
  }

  @Test
  public void testReload() {
    fm.clear();
    fm.reloadMapping(new String[] { mappingFile.getAbsolutePath() });
    //the space in the setup used to generate an exception resulting in only one entry getting populated,
    // the fix should skip empty lines i.e. size will be greater than one
    assertTrue(fm.getMappingSize() > 1);
  }
}
