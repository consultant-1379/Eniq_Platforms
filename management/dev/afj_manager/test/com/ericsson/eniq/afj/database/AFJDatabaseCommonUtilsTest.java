/**
 * 
 */
package com.ericsson.eniq.afj.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_ACTIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_BSS_ADDVENDORIDTO;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_PARTITIONTYPE;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_DEF_TABLELEVEL;
import static com.ericsson.eniq.afj.common.PropertyConstants.PROP_FOLLOWJOHN;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import utils.MemoryDatabaseUtility;

import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 * @author eheijun
 * 
 */
@Ignore
public class AFJDatabaseCommonUtilsTest {

  public static final String TEST_APPLICATION = AFJDatabaseCommonUtilsTest.class.getName();

  private static RockFactory testEtlRep;

  private static RockFactory testDwhRep;

  private static RockFactory testDwh;

  private static AFJDatabaseCommonUtils objectUnderTest;

  private static File ETLCServerProperties;

  private static File AFJManagerProperties;

  private static File StaticProperties;

  private AFJDatabaseHandler connectionObject;
  
  private List<AFJMeasurementType> oldMeasurementTypes;

  private List<AFJMeasurementType> newMeasurementTypes;

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

    /* Create property file for afjmanager details */
    AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
    AFJManagerProperties.deleteOnExit();
    try {
      final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
      pw.write(PROP_BSS_ACTIONTYPE + "=parse\n");
      pw.write(PROP_BSS_ADDVENDORIDTO + "=addVendorIDTo\n");
      pw.write(PROP_FOLLOWJOHN + "=1\n");
      pw.write(PROP_DEF_PARTITIONTYPE + "=RAW\n");
      pw.write(PROP_DEF_TABLELEVEL + "=RAW\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Create property file for database connection details */
    ETLCServerProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
    ETLCServerProperties.deleteOnExit();
    try {
      final PrintWriter pw = new PrintWriter(new FileWriter(ETLCServerProperties));
      pw.write("ENGINE_DB_URL=" + MemoryDatabaseUtility.TEST_ETLREP_URL + "\n");
      pw.write("ENGINE_DB_USERNAME=" + MemoryDatabaseUtility.TESTDB_USERNAME + "\n");
      pw.write("ENGINE_DB_PASSWORD=" + MemoryDatabaseUtility.TESTDB_PASSWORD + "\n");
      pw.write("ENGINE_DB_DRIVERNAME=" + MemoryDatabaseUtility.TESTDB_DRIVER + "\n");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Create property file for static properties */
    StaticProperties = new File(System.getProperty("user.dir"), "static.properties");
    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
    StaticProperties.deleteOnExit();
    try {
      final PrintWriter pw = new PrintWriter(new FileWriter(StaticProperties));
      pw.write("sybaseiq.option.public.DML_Options5=0");
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

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
    connectionObject = AFJDatabaseHandler.getInstance();
    objectUnderTest = new AFJDatabaseCommonUtils();
    
    AFJMeasurementCounter counter1 = new AFJMeasurementCounter();
    counter1.setCounterName("ifOutErrors");
    counter1.setCounterNew(true);
    AFJMeasurementCounter counter2 = new AFJMeasurementCounter();
    counter2.setCounterName("Followjohn");
    counter2.setCounterNew(true);
    
    AFJMeasurementTag tag1 = new AFJMeasurementTag();
    tag1.setTagName("E1Interface");
    tag1.setDataformattype("mdc");
    tag1.setNewCounters(new ArrayList<AFJMeasurementCounter>(Arrays.asList(new AFJMeasurementCounter[] {counter1})));
    AFJMeasurementTag tag2 = new AFJMeasurementTag();
    tag2.setTagName("Followjohn");
    tag2.setDataformattype("mdc");
    tag2.setNewCounters(new ArrayList<AFJMeasurementCounter>(Arrays.asList(new AFJMeasurementCounter[] {counter2})));
    
    AFJMeasurementType type1 = new AFJMeasurementType();
    type1.setTpName("DC_E_STN");
    type1.setTpVersion("DC_E_STN:((999))");
    type1.setTypeName("DC_E_STN_E1INTERFACE");
    type1.setTypeNew(false);
    type1.setTags(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] {tag1})));
    
    AFJMeasurementType type2 = new AFJMeasurementType();
    type2.setTpName("DC_E_STN");
    type2.setTpVersion("DC_E_STN:((999))");
    type2.setTypeName("DC_E_STN_FOLLOWJOHN");
    type2.setTypeNew(true);
    type2.setTags(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] {tag2})));
    
    oldMeasurementTypes = new ArrayList<AFJMeasurementType>(Arrays.asList(new AFJMeasurementType[] {type1}));
    newMeasurementTypes = new ArrayList<AFJMeasurementType>(Arrays.asList(new AFJMeasurementType[] {type2}));
    
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    objectUnderTest = null;
    connectionObject = null;
    oldMeasurementTypes = null;
    newMeasurementTypes = null;
  }

  /**
   * Helper method to check count of rows in given table
   * @param tableName
   * @param sourceFactory
   * @return
   * @throws Exception
   */
  private int getRowCount(String tableName, RockFactory sourceFactory) throws Exception {
    int rows = 0;
    ResultSet res = MemoryDatabaseUtility.executeQuery("SELECT COUNT(*) FROM " + tableName, sourceFactory);
    try {
      while (res.next()) {
        rows = res.getInt(1);
        return rows;
      }
    } finally {
      res.close();
    }
    return -1;
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateMeasCountersMap(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  @Ignore
  public void testPopulateMeasCountersMap() throws AFJException {
    final String typeId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACE";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final List<String> result = objectUnderTest.populateMeasCountersMap(typeId, dwhrep);
    final int actual = result.size();
    final int expected = 2;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateMeasCountersMap(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  @Ignore
  public void testPopulateMeasCountersMapException() {
    final String typeId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACESundar";
    try {
      RockFactory dwhrep = connectionObject.getDwhrep();
      objectUnderTest.populateMeasCountersMap(typeId, dwhrep);
      fail("Should not reach this point");
    } catch (AFJException ae) {
      final String expected = "No matching record found in MeasurementCounter table for typeId:" + typeId;
      final String actual = ae.getMessage();
      assertEquals(expected, actual);
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getActiveTechPackVersion(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetActiveTechPackVersion() throws AFJException {
    final String techpackName = "DC_E_STN";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final String actual = objectUnderTest.getActiveTechPackVersion(techpackName, dwhrep);
    final String expected = "DC_E_STN:((999))";
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateAddVendorIdList(java.lang.String, java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testPopulateAddVendorIdListException() {
    try {
      final String bssInterfaceName = "INTF_DC_E_BSS_APG";
      final String techpackVersion = "DC_E_BSS:((16))";
      RockFactory etlrep = connectionObject.getEtlrep();
      objectUnderTest.populateAddVendorIdList(techpackVersion, bssInterfaceName, etlrep);
      fail("Should not reach this point. Was expecting AFJException.");
    } catch (AFJException ae) {
      final String expected = "Error in processing the actionContents:There is no bss interface defined on the server.";
      final String actual = ae.getMessage();
      assertEquals(expected, actual);
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateAddVendorIdList(java.lang.String, java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testPopulateAddVendorIdList() throws Exception {
    MemoryDatabaseUtility.execute("insert into META_COLLECTION_SETS (collection_set_id,collection_set_name,DESCRIPTION,version_number,ENABLED_FLAG,TYPE) values (10,'INTF_DC_E_BSS_APG','Interface INTF_DC_E_BSS_APG by ConfigTool b409','((110))','N','Interface')", testEtlRep);
    MemoryDatabaseUtility.execute("insert into META_TRANSFER_ACTIONS(COLLECTION_SET_ID,ACTION_TYPE,ACTION_CONTENTS_01,ACTION_CONTENTS_02,ACTION_CONTENTS_03) values(10,'parse','addVendorIDTo=BSC,BSCAMSG','','')", testEtlRep);
    final String bssInterfaceName = "INTF_DC_E_BSS_APG";
    final String techpackVersion = "DC_E_BSS:((16))";
    RockFactory etlrep = connectionObject.getEtlrep();
    final List<String> resultList = objectUnderTest
        .populateAddVendorIdList(techpackVersion, bssInterfaceName, etlrep);
    final int expected = 2;
    final int actual = resultList.size();
    assertEquals(expected, actual);
    MemoryDatabaseUtility.execute("delete from META_COLLECTION_SETS where collection_set_id = 10 and collection_set_name = 'INTF_DC_E_BSS_APG'", testEtlRep);
    MemoryDatabaseUtility.execute("delete from META_TRANSFER_ACTIONS where COLLECTION_SET_ID = 10 and ACTION_TYPE = 'parse'", testEtlRep);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateDefaultTagsCache(java.lang.String, java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testPopulateDefaultTagsCacheStringStringRockFactory() throws AFJException {
    RockFactory dwhrep = connectionObject.getDwhrep();
    final String bssTechpackName = "DC_E_BSS";
    final String commonTechpackName = "DC_E_CMN_STS";
    final Map<String, String> resultMap = objectUnderTest.populateDefaultTagsCache(bssTechpackName, commonTechpackName,
        dwhrep);
    final int actual = resultMap.keySet().size();
    final int expected = 2;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateDefaultTagsCache(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testPopulateDefaultTagsCacheStringRockFactory() throws AFJException {
    RockFactory dwhrep = connectionObject.getDwhrep();
    final String techpackName = "DC_E_STN";
    final Map<String, String> resultMap = objectUnderTest.populateDefaultTagsCache(techpackName, dwhrep);
    final String actual = resultMap.get("E1Interface".toUpperCase());
    final String expected = "DC_E_STN:((999)):DC_E_STN_E1Interface".toUpperCase();
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#populateMeasurementCountersMap(java.util.Map, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testPopulateMeasurementCountersMap() throws AFJException {
    final Map<String, String> tagToMeasTypeMap = new HashMap<String, String>();
    tagToMeasTypeMap.put("E1INTERFACE", "DC_E_STN:((999)):DC_E_STN_E1INTERFACE");
    RockFactory dwhrep = connectionObject.getDwhrep();
    final Map<String, List<String>> resultMap = objectUnderTest
        .populateMeasurementCountersMap(tagToMeasTypeMap, dwhrep);
    final List<String> resultList = resultMap.get("E1INTERFACE");
    final int actual = resultList.size();
    final int expected = 2;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getNumberOfCounters(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetNumberOfCounters() throws AFJException {
    final String typeId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACE";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final int actual = objectUnderTest.getNumberOfCounters(typeId, dwhrep);
    final int expected = 2;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getNextMeasurementCounterColumnNumber(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetNextMeasurementCounterColumnNumber() throws AFJException {
    final String typeId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACE";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final long actual = objectUnderTest.getNextMeasurementCounterColumnNumber(typeId, dwhrep);
    final long expected = 104l;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getNextMeasurementCounterColumnNumber(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetNextMeasurementCounterColumnNumberNewMeasType() throws AFJException {
    final String typeId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACESundar";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final long actual = objectUnderTest.getNextMeasurementCounterColumnNumber(typeId, dwhrep);
    final long expected = 101l;
    assertEquals(expected, actual);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getMeasurementColumnNumber(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetMeasurementColumnNumberException() {
    final String dataFormatId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACE:RAW";
    try {
      RockFactory dwhrep = connectionObject.getDwhrep();
      objectUnderTest.getMeasurementColumnNumber(dataFormatId, dwhrep);
      fail("Should not reach this point.");
    } catch (AFJException ae) {
      final String expected = "No counters defined for the dataformatid:" + dataFormatId;
      final String actual = ae.getMessage();
      assertEquals(expected, actual);
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getMeasurementColumnNumber(java.lang.String, ssc.rockfactory.RockFactory)}
   * .
   */
  @Test
  public void testGetMeasurementColumnNumber() throws AFJException {
    final String dataFormatId = "DC_E_STN:((999)):DC_E_STN_E1INTERFACE";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final long actual = objectUnderTest.getMeasurementColumnNumber(dataFormatId, dwhrep);
    final long expected = 28l;
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getInterfaceVersion(final String
   * interfaceName, final RockFactory dwhrep)}.
   */
  @Test
  public void testGetInterfaceVersion() throws AFJException {
    final String interfaceName = "INTF_DC_E_STN";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final String actual = objectUnderTest.getInterfaceVersion(interfaceName, dwhrep);
    final String expected = "((100))";
    assertEquals(expected, actual);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getTechpack(final String techpackName,
   * final String versionid, final RockFactory etlrep)}.
   */
  @Test
  public void testGetTechpack() throws Exception {
    MemoryDatabaseUtility.execute("insert into META_COLLECTION_SETS (collection_set_id,collection_set_name,DESCRIPTION,version_number,ENABLED_FLAG,TYPE) values (10,'DC_E_STN','Techpack dc_e_stn','((2))','N','TechPack')", testEtlRep);
    final String techpackName = "DC_E_STN";
    final String versionId = "DC_E_STN:((999))";
    final RockFactory etlrep = connectionObject.getEtlrep();
    final String actual = objectUnderTest.getTechpack(techpackName, versionId, etlrep).getVersion_number();
    final String expected = "((999))";
    assertEquals(expected, actual);
    MemoryDatabaseUtility.execute("delete from META_COLLECTION_SETS where collection_set_id = 10 and collection_set_name = 'DC_E_STN'", testEtlRep);
  }

  @Test
  public void testGetDwhType() throws AFJException {
    RockFactory dwhrep = connectionObject.getDwhrep();
    String measName = "DC_E_STN_FOLLOWJOHN";
    Dwhtype dwhType = objectUnderTest.getDwhType(measName, dwhrep);
    assertNotNull(dwhType);
  }

  @Test
  public void testGetTypeActivations() throws AFJException {
    RockFactory dwhrep = connectionObject.getDwhrep();
    String mTypeName = "DC_E_STN_FOLLOWJOHN";
    String mTpName = "DC_E_STN";
    List<Typeactivation> typeActivations = objectUnderTest.getTypeActivations(mTypeName, mTpName, dwhrep);
    final int expected = 1;
    assertEquals(expected, typeActivations.size());
  }

  @Test
  public void testGetVersioning() throws AFJException {
    RockFactory dwhrep = connectionObject.getDwhrep();
    String versionId = "DC_E_STN:((999))";
    Versioning versioning = objectUnderTest.getVersioning(versionId, dwhrep);
    assertNotNull(versioning);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getMeasurementColumns(final String
   * mTableId, final RockFactory dwhrep) throws AFJException}.
   */
  @Test
  public void testGetMeasurementColumns() throws Exception {
    MemoryDatabaseUtility.execute("insert into Measurementcolumn values('DC_E_BSS:((22)):DC_E_BSS_BSC:RAW','DATE_ID',26,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','DATE_ID','DC_E_BSS:((B222))',0,1,'PUBLICKEY',NULL)", testDwhRep);
    final String mTableId = "DC_E_BSS:((22)):DC_E_BSS_BSC:RAW";
    RockFactory dwhrep = connectionObject.getDwhrep();
    final List<Measurementcolumn> mColumnList = objectUnderTest.getMeasurementColumns(mTableId, dwhrep);
    final int expected = 1;
    assertEquals(expected, mColumnList.size());
    MemoryDatabaseUtility.execute("delete from Measurementcolumn where mtableid = 'DC_E_BSS:((22)):DC_E_BSS_BSC:RAW' and dataname = 'DATE_ID'", testDwhRep);
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#getAFJDelta(java.lang.String, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeInterfaceMeasurement(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDataItem(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDefaultTags(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDataFormat(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeTransformation(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeTransformer(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementColumn(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementTable(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementCounter(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementKey(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementType(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementTypeClass(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * @throws Exception 
   */
  @Test
  public void testRestoreAFJDelta() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    final AFJDelta afjdelta = objectUnderTest.getAFJDelta("DC_E_STN", dwhrep);
    assertNotNull(afjdelta);
    List<AFJMeasurementType> measurementTypes = afjdelta.getMeasurementTypes();
    assertEquals(2, measurementTypes.size());
    AFJMeasurementType mtype1 = measurementTypes.get(0);
    AFJMeasurementType mtype2 = measurementTypes.get(1);
    assertEquals(1, mtype1.getTags().size());
    assertEquals(1, mtype2.getTags().size());
    AFJMeasurementTag mtag1 = mtype1.getTags().get(0);
    AFJMeasurementTag mtag2 = mtype2.getTags().get(0);
    assertEquals(1, mtag1.getNewCounters().size());
    assertEquals(1, mtag2.getNewCounters().size());
    
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeInterfaceMeasurement(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveInterfaceMeasurement() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("InterfaceMeasurement", dwhrep));
    objectUnderTest.removeInterfaceMeasurement(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("InterfaceMeasurement", dwhrep));
    objectUnderTest.removeInterfaceMeasurement(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("InterfaceMeasurement", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDataItem(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveDataItem() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(5, getRowCount("DataItem", dwhrep));
    objectUnderTest.removeDataItem(oldMeasurementTypes, false, dwhrep);
    assertEquals(4, getRowCount("DataItem", dwhrep));
    objectUnderTest.removeDataItem(newMeasurementTypes, true, dwhrep);
    assertEquals(2, getRowCount("DataItem", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDefaultTags(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveDefaultTags() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(4, getRowCount("DefaultTags", dwhrep));
    objectUnderTest.removeDefaultTags(oldMeasurementTypes, false, dwhrep);
    assertEquals(4, getRowCount("DefaultTags", dwhrep));
    objectUnderTest.removeDefaultTags(newMeasurementTypes, true, dwhrep);
    assertEquals(3, getRowCount("DefaultTags", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeDataFormat(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveDataFormat() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("DataFormat", dwhrep));
    objectUnderTest.removeDataFormat(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("DataFormat", dwhrep));
    objectUnderTest.removeDataFormat(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("DataFormat", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeTransformation(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveTransformation() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("Transformation", dwhrep));
    objectUnderTest.removeTransformation(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("Transformation", dwhrep));
    objectUnderTest.removeTransformation(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("Transformation", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeTransformer(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveTransformer() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("Transformer", dwhrep));
    objectUnderTest.removeTransformer(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("Transformer", dwhrep));
    objectUnderTest.removeTransformer(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("Transformer", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementColumn(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementColumn() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(5, getRowCount("MeasurementColumn", dwhrep));
    objectUnderTest.removeMeasurementColumn(oldMeasurementTypes, false, dwhrep);
    assertEquals(4, getRowCount("MeasurementColumn", dwhrep));
    objectUnderTest.removeMeasurementColumn(newMeasurementTypes, true, dwhrep);
    assertEquals(2, getRowCount("MeasurementColumn", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementTable(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementTable() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("MeasurementTable", dwhrep));
    objectUnderTest.removeMeasurementTable(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("MeasurementTable", dwhrep));
    objectUnderTest.removeMeasurementTable(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("MeasurementTable", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementCounter(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementCounter() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(3, getRowCount("MeasurementCounter", dwhrep));
    objectUnderTest.removeMeasurementCounter(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("MeasurementCounter", dwhrep));
    objectUnderTest.removeMeasurementCounter(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("MeasurementCounter", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementKey(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementKey() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("MeasurementKey", dwhrep));
    objectUnderTest.removeMeasurementKey(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("MeasurementKey", dwhrep));
    objectUnderTest.removeMeasurementKey(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("MeasurementKey", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementType(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementType() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("MeasurementType", dwhrep));
    objectUnderTest.removeMeasurementType(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("MeasurementType", dwhrep));
    objectUnderTest.removeMeasurementType(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("MeasurementType", dwhrep));
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils#removeMeasurementTypeClass(java.util.List, java.lang.Boolean, ssc.rockfactory.RockFactory)}
   * .
   * @throws Exception 
   */
  @Test
  public void testRemoveMeasurementTypeClass() throws Exception {
    RockFactory dwhrep = connectionObject.getDwhrep();
    assertEquals(2, getRowCount("MeasurementTypeClass", dwhrep));
    objectUnderTest.removeMeasurementTypeClass(oldMeasurementTypes, false, dwhrep);
    assertEquals(2, getRowCount("MeasurementTypeClass", dwhrep));
    objectUnderTest.removeMeasurementTypeClass(newMeasurementTypes, true, dwhrep);
    assertEquals(1, getRowCount("MeasurementTypeClass", dwhrep));
  }

}
