/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import utils.MemoryDatabaseUtility;

import com.ericsson.eniq.busyhourcfg.data.vo.TechPack;


/**
 * @author eheijun
 *
 */
public class RockTechPackReaderTest {

  public static final String TEST_APPLICATION = RockTechPackReaderTest.class.getName();

  private static final String TEST_VERSION_ID1 = "CUSTOM_DC_E_CPP:((888))";
  private static final String TEST_VERSION_ID2 = "DC_E_MGW:((999))";
  private static final String TEST_VERSION_ID3 = "DIM_E_UTRAN:((120))";

  private static RockFactory testEtlRep;

  private static RockFactory testDwhRep;

  private static RockFactory testDwh;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    testEtlRep = new RockFactory(MemoryDatabaseUtility.TEST_ETLREP_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL etlrepsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_ETLREP_BASIC);
    MemoryDatabaseUtility.loadSetup(testEtlRep, etlrepsqlurl);
    testDwhRep = new RockFactory(MemoryDatabaseUtility.TEST_DWHREP_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL dwhrepsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_DWHREP_BASIC);
    MemoryDatabaseUtility.loadSetup(testDwhRep, dwhrepsqlurl);
    testDwh = new RockFactory(MemoryDatabaseUtility.TEST_DWH_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
    final URL dwhsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_DWH_BASIC);
    MemoryDatabaseUtility.loadSetup(testDwh, dwhsqlurl);
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    MemoryDatabaseUtility.shutdown(testDwh);
    MemoryDatabaseUtility.shutdown(testDwhRep);
    MemoryDatabaseUtility.shutdown(testEtlRep);
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockTechPackReader#getAllActivatedVersionIds()}.
   */
  @Test
  public void testGetAllActivatedPMTypeVersionIds() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final RockTechPackReader rockTechPackReader = new RockTechPackReader(rockDatabaseSession);
      final List<String> result = rockTechPackReader.getAllActivatedPMTypeVersionIds();
      assertTrue(result != null);
      assertTrue(result.size() > 0);
      assertTrue(result.size() == 2);
      final String firstItem = result.get(0);
      final String secondItem = result.get(1);
      assertEquals(TEST_VERSION_ID1, firstItem);
      assertEquals(TEST_VERSION_ID2, secondItem);
    } catch (Exception e) {
      fail("Getting IDs of all activated versions failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockTechPackReader#getTechPackByVersionId(java.lang.String)}.
   */
  @Test
  public void testGetTechPackByVersionId() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final RockTechPackReader rockTechPackReader = new RockTechPackReader(rockDatabaseSession);
      final TechPack result = rockTechPackReader.getTechPackByVersionId(TEST_VERSION_ID2);
      assertTrue(result != null);
      assertTrue(TEST_VERSION_ID2.equals(result.getVersionId()));
    } catch (Exception e) {
      fail("Getting techpack by version id failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockTechPackReader#getAllBusyhourBasetables(java.lang.String)}.
   */
  @Test
  public void testGetAllBusyhourBasetables() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final RockTechPackReader rockTechPackReader = new RockTechPackReader(rockDatabaseSession);
      final List<String> result = rockTechPackReader.getAllBusyhourBasetables(TEST_VERSION_ID2);
      assertTrue(result != null);
      assertEquals(3, result.size());
    } catch (Exception e) {
      fail("Getting all BH basetables failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockTechPackReader#getAllBusyhourSourceTechpacks()}.
   */
  @Test
  public void testGetAllBusyhourSourceTechpacks() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final RockTechPackReader rockTechPackReader = new RockTechPackReader(rockDatabaseSession);
      final List<String> result = rockTechPackReader.getAllBusyhourSourceTechpacks();
      assertTrue(result != null);
      assertTrue(result.size() > 0);
      final String firstItem = result.get(0);
      final String secondItem = result.get(1);
      final String thirdItem = result.get(2);
      assertEquals(TEST_VERSION_ID1, firstItem);
      assertEquals(TEST_VERSION_ID2, secondItem);
      assertEquals(TEST_VERSION_ID3, thirdItem);
    } catch (Exception e) {
      fail("Getting all BH source techpacks failed: " + e.getMessage());
    }
  }

}
