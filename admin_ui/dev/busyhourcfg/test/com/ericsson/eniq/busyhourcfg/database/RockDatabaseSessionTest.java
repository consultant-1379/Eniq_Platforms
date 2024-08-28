/**
 * 
 */
package com.ericsson.eniq.busyhourcfg.database;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
//import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultMappedType;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder;
import com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack;
import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
//import com.ericsson.eniq.busyhourcfg.data.vo.Key;
//import com.ericsson.eniq.busyhourcfg.data.vo.MappedType;
//import com.ericsson.eniq.busyhourcfg.data.vo.Source;
import com.ericsson.eniq.busyhourcfg.dwhmanager.DWHStorageTimeAction;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;
import com.ericsson.eniq.busyhourcfg.exceptions.DatabaseException;

import ssc.rockfactory.RockFactory;
import utils.MemoryDatabaseUtility;

/**
 * @author eheijun
 * 
 */
@RunWith(JMock.class)
public class RockDatabaseSessionTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

//  private static final String CREATE_TEST_VIEW = "create view test";

  public static final String TEST_APPLICATION = RockDatabaseSessionTest.class.getName();

  private static final String TEST_VERSION_ID = "DC_E_MGW:((999))";

  private static final String TEST_VERSION_NAME = "DC_E_MGW";

  private static final String TEST_BH_LEVEL = "DC_E_MGW_SERVICEBH";

  private static final String TEST_BH_TYPE0 = "CP0";

//private static final Object TEST_BH_TYPE1 = "CP1";

  private static final String TEST_TARGETVERSION_ID = "DC_E_MGW:((999))";

  private static final String TEST_BH_OBJECT = "SERVICE";

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
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getActivatedVersionings()}
   * .
   */
  @Test
  public void testGetActivatedVersionings() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Tpactivation> result = rockDatabaseSession.getActivatedVersionings();
      assertTrue(result.size() == 3);
    } catch (DatabaseException e) {
      fail("Getting activated versionings failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourSourceTables(java.lang.String)}
   * .
   */
  @Test
  public void testGetBusyhourSourceTables() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Measurementtable> result = rockDatabaseSession.getBusyhourSourceTables(TEST_VERSION_ID);
      assertTrue(result.size() == 2);
      boolean rawFound = false;
      boolean countFound = false;
      boolean otherFound = false;
      for (Measurementtable mt : result) {
        rawFound = rawFound || mt.getTablelevel().equals("RAW");
        countFound = countFound || mt.getTablelevel().equals("COUNT");
        otherFound = otherFound || !mt.getTablelevel().equals("RAW") && !mt.getTablelevel().equals("COUNT");
      }
      assertTrue(rawFound);
      assertTrue(countFound);
      assertFalse(otherFound);
    } catch (DatabaseException e) {
      fail("Getting BH source tables failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhours(java.lang.String)}
   * .
   */
  @Test
  public void testGetBusyhours() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Busyhour> result = rockDatabaseSession.getBusyhours(TEST_VERSION_ID);
      assertTrue(result.size() == 70);
    } catch (DatabaseException e) {
      fail("Getting BHs failed: " + e.getMessage());
    }
  }
  
  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhours(java.lang.String)}
   * .
   */
  @Test
  public void testGetBusyhoursOrder() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Busyhour> result = rockDatabaseSession.getBusyhours("CUSTOM_DC_E_CPP:((888))");
      assertEquals(5, result.size()); // Expect 5:CTP_PP0,CTP_PP1,CTP_PP2,CTP_PP10,CTP_PP11 (correcting test data in wrong order in dB)
      // Check the order is correct
      assertEquals("Order of returned list wrong from rockDatabaseSession.getBusyhours: ", "CTP_PP0", result.get(0).getBhtype());
      assertEquals("Order of returned list wrong from rockDatabaseSession.getBusyhours: ", "CTP_PP1", result.get(1).getBhtype());
      assertEquals("Order of returned list wrong from rockDatabaseSession.getBusyhours: ", "CTP_PP2", result.get(2).getBhtype());
      assertEquals("Order of returned list wrong from rockDatabaseSession.getBusyhours: ", "CTP_PP10", result.get(3).getBhtype());
      assertEquals("Order of returned list wrong from rockDatabaseSession.getBusyhours: ", "CTP_PP11", result.get(4).getBhtype());
    } catch (DatabaseException e) {
      fail("Getting BHs failed: " + e.getMessage());
    }
  } //testGetBusyhoursOrder

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourplaceholders(java.lang.String)}
   * .
   */
  @Test
  public void testGetBusyhourplaceholders() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Busyhourplaceholders> result = rockDatabaseSession.getBusyhourplaceholders(TEST_VERSION_ID);
      assertTrue(result.size() == 7);
    } catch (DatabaseException e) {
      fail("Getting BH placeholders failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getActivatedVersioning(java.lang.String)}
   * .
   */
  @Test
  public void testGetActivatedVersioning() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Tpactivation result = rockDatabaseSession.getActivatedVersioning(TEST_VERSION_ID);
      assertTrue(result != null);
    } catch (DatabaseException e) {
      fail("Getting Tpactivation failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourSources(com.distocraft.dc5000.repository.dwhrep.Busyhour)}
   * .
   */
  @Test
  public void testGetBusyhourSources() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Busyhour mockBusyhour = context.mock(Busyhour.class);
      context.checking(new Expectations() {

        {
          allowing(mockBusyhour).getVersionid();
          will(returnValue(TEST_VERSION_ID));
          allowing(mockBusyhour).getBhlevel();
          will(returnValue(TEST_BH_LEVEL));
          allowing(mockBusyhour).getBhtype();
          will(returnValue(TEST_BH_TYPE0));
          allowing(mockBusyhour).getTargetversionid();
          will(returnValue(TEST_TARGETVERSION_ID));
          allowing(mockBusyhour).getBhobject();
          will(returnValue(TEST_BH_OBJECT));
        }
      });
      final Vector<Busyhoursource> result = rockDatabaseSession.getBusyhourSources(mockBusyhour);
      assertTrue(result.size() == 1);
    } catch (DatabaseException e) {
      fail("Getting BH sources failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourmappings(com.distocraft.dc5000.repository.dwhrep.Busyhour)}
   * .
   */
  @Test
  public void testGetBusyhourmappings() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Busyhour mockBusyhour = context.mock(Busyhour.class);
      context.checking(new Expectations() {

        {
          allowing(mockBusyhour).getVersionid();
          will(returnValue(TEST_VERSION_ID));
          allowing(mockBusyhour).getBhlevel();
          will(returnValue(TEST_BH_LEVEL));
          allowing(mockBusyhour).getBhtype();
          will(returnValue(TEST_BH_TYPE0));
          allowing(mockBusyhour).getTargetversionid();
          will(returnValue(TEST_TARGETVERSION_ID));
          allowing(mockBusyhour).getBhobject();
          will(returnValue(TEST_BH_OBJECT));
        }
      });
      final Vector<Busyhourmapping> result = rockDatabaseSession.getBusyhourmappings(mockBusyhour);
      assertTrue(result.size() == 1);
    } catch (DatabaseException e) {
      fail("Getting BH mappings failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourrankkeys(com.distocraft.dc5000.repository.dwhrep.Busyhour)}
   * .
   */
  @Test
  public void testGetBusyhourrankkeys() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Busyhour mockBusyhour = context.mock(Busyhour.class);
      context.checking(new Expectations() {

        {
          allowing(mockBusyhour).getVersionid();
          will(returnValue(TEST_VERSION_ID));
          allowing(mockBusyhour).getBhlevel();
          will(returnValue(TEST_BH_LEVEL));
          allowing(mockBusyhour).getBhtype();
          will(returnValue(TEST_BH_TYPE0));
          allowing(mockBusyhour).getTargetversionid();
          will(returnValue(TEST_TARGETVERSION_ID));
          allowing(mockBusyhour).getBhobject();
          will(returnValue(TEST_BH_OBJECT));
        }
      });
      final Vector<Busyhourrankkeys> result = rockDatabaseSession.getBusyhourrankkeys(mockBusyhour);
      assertTrue(result.size() == 5);
    } catch (DatabaseException e) {
      fail("Getting BH rankkeys failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#performBusyhourActivation(com.ericsson.eniq.busyhourcfg.data.vo.DefaultTechPack)}
   * .
   */
  @Test
  public void testPerformBusyhourActivation() {
    try {
      final DefaultTechPack mockTechpack = context.mock(DefaultTechPack.class);
      final DWHStorageTimeAction mockStorageTimeAction = context.mock(DWHStorageTimeAction.class);
      final Busyhour mockBusyhour = context.mock(Busyhour.class); 
      context.checking(new Expectations() {

        {
          allowing(mockTechpack).getVersionId();
          will(returnValue(TEST_VERSION_ID));
          allowing(mockTechpack).getName();
          will(returnValue(TEST_VERSION_NAME));
          
          allowing(mockStorageTimeAction).performDropBhRankViews(mockBusyhour);
//          allowing(mockStorageTimeAction).getCustomBhViewCreates(mockBusyhour);
//          will(returnValue(CREATE_TEST_VIEW));
//          allowing(mockStorageTimeAction).getPlaceholderCreateStatement(mockBusyhour, TEST_VERSION_ID, TEST_VERSION_NAME); 
//          will(returnValue(CREATE_TEST_VIEW));
          allowing(mockStorageTimeAction).performCreateBhRankViews(mockBusyhour);
          allowing(mockStorageTimeAction).updateBHCounters(with(any(Dwhtechpacks.class)));
          allowing(mockStorageTimeAction).updateBHCountersForCustomTechpack(with(any(Dwhtechpacks.class)));
          
        }
      });
      try {
        final Properties p = new Properties();
        p.put("dwhm.debug", "true");
        StaticProperties.giveProperties(p);
      } catch (Exception e) {
        fail(e.getMessage());
      }
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      rockDatabaseSession.performBusyhourActivation(mockTechpack, mockStorageTimeAction);
    } catch (DatabaseException e) {
      fail("Busyhour activation failed: " + e.getMessage());
    } catch (DWHManagerException e) {
      fail("Busyhour activation failed: " + e.getMessage());
    }
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getVersioning(java.lang.String)}.
   */
  @Test
  public void testGetVersioning() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Versioning result = rockDatabaseSession.getVersioning(TEST_VERSION_ID);
      assertTrue(result != null);
    } catch (DatabaseException e) {
      fail("Getting versioning failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getMeasurementtypes(java.lang.String)}.
   */
  @Test
  public void testGetMeasurementtypes() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Measurementtype> result = rockDatabaseSession.getMeasurementtypes(TEST_VERSION_ID);
      assertTrue(result.size() == 4);
    } catch (DatabaseException e) {
      fail("Getting measurementtypes failed: " + e.getMessage());
    }
  }

  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getMeta_collection_sets(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testGetMeta_collection_sets() {
    try {
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Meta_collection_sets result = rockDatabaseSession.getMeta_collection_sets("DWH_BASE", "R2D_b999");
      assertTrue(result != null);
    } catch (DatabaseException e) {
      fail("Getting measurementtypes failed: " + e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#getBusyhourSupportKeys}
   * .
   */
  @Test
  public void testGetBusyhourSupportKeys() {
    try {
      final String typeid = "DC_E_MGW:((999)):DC_E_MGW_SERVICEBH";
      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
      final Vector<Measurementkey> result = rockDatabaseSession.getBusyhourSupportKeys(typeid);
      assertTrue(result.size() == 1);
    } catch (DatabaseException e) {
      fail("Getting BH placeholders failed: " + e.getMessage());
    }
  }

// TODO resolve how to fix memory database problem   
//  /**
//   * Test method for
//   * {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#setPlaceholder(com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder)}
//   * .
//   */
//  @Test
//  public void testSetPlaceholder() {
//    try {
//      final DefaultPlaceholder mockPlaceholder = context.mock(DefaultPlaceholder.class);
//      final Source mockSource = context.mock(Source.class);
//      final Key mockKey = context.mock(Key.class);
//      final MappedType mockMappedType = context.mock(MappedType.class);
//      final List<Source> sources = new ArrayList<Source>();
//      sources.add(mockSource);
//      final List<Key> keys = new ArrayList<Key>();
//      keys.add(mockKey);
//      final List<MappedType> mappedTypes = new ArrayList<MappedType>();
//      mappedTypes.add(mockMappedType);
//      context.checking(new Expectations() {
//
//        {
//          // set values for Source mock 
//          allowing(mockSource).getTypename();
//          will(returnValue("SOME_TYPENAME"));
//          allowing(mockSource).isNew();
//          will(returnValue(true));
//          // set values for Key mock 
//          allowing(mockKey).getKeyName();
//          will(returnValue("SOME_KEYNAME"));
//          allowing(mockKey).getKeyValue();
//          will(returnValue("SOME_KEYVALUE"));
//          allowing(mockKey).getSource();
//          will(returnValue("SOME_SOURCE"));
//          allowing(mockKey).isNew();
//          will(returnValue(true));
//          // set values for MappedType mock 
//          allowing(mockMappedType).getTypeid();
//          will(returnValue("SOME_TYPEID"));
//          allowing(mockMappedType).getBhTargetlevel();
//          will(returnValue("SOME_BHTARGETLEVEL"));
//          allowing(mockMappedType).getBhTargettype();
//          will(returnValue("SOME_BHTARGETTYPE"));
//          allowing(mockMappedType).getEnabled();
//          will(returnValue(true));
//          allowing(mockMappedType).isNew();
//          will(returnValue(true));
//          // set values for Placeholder mock
//          allowing(mockPlaceholder).getVersionid();
//          will(returnValue(TEST_VERSION_ID));
//          allowing(mockPlaceholder).getBhlevel();
//          will(returnValue(TEST_BH_LEVEL));
//          allowing(mockPlaceholder).getBhtype();
//          will(returnValue(TEST_BH_TYPE1));
//          allowing(mockPlaceholder).getTargetversionid();
//          will(returnValue(TEST_TARGETVERSION_ID));
//          allowing(mockPlaceholder).getBhobject();
//          will(returnValue(TEST_BH_OBJECT));
//          allowing(mockPlaceholder).getAggregationType();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getWindowsize();
//          will(returnValue(60));
//          allowing(mockPlaceholder).getOffset();
//          will(returnValue(0));
//          allowing(mockPlaceholder).getLookback();
//          will(returnValue(0));
//          allowing(mockPlaceholder).getNThreshold();
//          will(returnValue(0));
//          allowing(mockPlaceholder).getPThreshold();
//          will(returnValue(0));
//          allowing(mockPlaceholder).getPlaceholdertype();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getDescription();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getWhere();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getCriteria();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getEnabled();
//          will(returnValue(false));
//          allowing(mockPlaceholder).getGrouping();
//          will(returnValue(""));
//          allowing(mockPlaceholder).getReactivateviews();
//          will(returnValue(0));
//          allowing(mockPlaceholder).getSources();
//          will(returnValue(sources));
//          allowing(mockPlaceholder).getRemovedSources();
//          will(returnValue(new ArrayList<Source>()));
//          allowing(mockPlaceholder).getMappedTypes();
//          will(returnValue(mappedTypes));
//          allowing(mockPlaceholder).getKeys();
//          will(returnValue(keys));
//          allowing(mockPlaceholder).getRemovedKeys();
//          will(returnValue(new ArrayList<Key>()));
//        }
//      });
//      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
//      rockDatabaseSession.setPlaceholder(mockPlaceholder);
//    } catch (DatabaseException e) {
//      fail("Setting placeholder failed: " + e.getMessage());
//    }
//  }
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#createBusyhourMappedtypes(com.ericsson.eniq.busyhourcfg.data.vo.DefaultPlaceholder)}.
   */
  @Test
  public void testCreateBusyhourMappedtypes() {
	  try {
		  final String targetVersionId = "DC_E_CPP:((123))"; //This is the currently active product TP.
		  final String newVersionId = "CUSTOM_DC_E_CPP:((888))";
		  final String bhlevel = "DC_E_CPP_AAL2APBH";
		  final String bhtype = "CTP_CP0";
		  final String bhobject = "AAL2AP";
		  final DefaultPlaceholder placeholder = createDefaultPlaceholder(newVersionId, bhlevel, bhtype, targetVersionId, bhobject);
	      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
	     
	      //Execution
	      rockDatabaseSession.createBusyhourMappedtypes(placeholder);
	      //Asserts
	      final Busyhour whereBusyhour = new Busyhour(testDwhRep);
	      whereBusyhour.setVersionid(newVersionId);
	      whereBusyhour.setTargetversionid(targetVersionId);
	      final Vector<Busyhourmapping> result = rockDatabaseSession.getBusyhourmappings(whereBusyhour);
	      assertTrue("For "+newVersionId+" Expected a list of Busyhourmappings but got "+result.size(), result.size()>0);
	      for (Busyhourmapping bhm : result) {
	    	  assertEquals("For "+newVersionId+" Expected Busyhourmapping TargetVserionID="+targetVersionId, targetVersionId, bhm.getTargetversionid());
	    	  // Test typeid is generated correctly, should use TargetVersionId (not versionId)
	    	  final String typeId = placeholder.getTargetversionid() + ":" + bhm.getBhtargettype();
	    	  assertEquals("For "+newVersionId+" Expected Busyhourmapping typeid="+typeId, typeId, bhm.getTypeid());
	      }
	  } catch (DatabaseException e) {
	      fail("testCreateBusyhourMappedtypes Exception: " + e.getMessage());
	  }
  } //testCreateBusyhourMappedtypes
  
  /**
   * Test method for {@link com.ericsson.eniq.busyhourcfg.database.RockDatabaseSession#deleteBusyhourMappedTypes(com.distocraft.dc5000.repository.dwhrep.Busyhour)}.
   */
  @Test
  public void testDeleteBusyhourMappedTypes() {
	  try {
		  final String newVersionId = "CUSTOM_DC_E_CPP:((123))";
		  final String targetVersionId = "DC_E_CPP:((129))"; //This is the currently active product TP.
		  final String bhlevel = "DC_E_CPP_AAL2APBH";
		  final String bhtype = "CTP_CP0";
		  final String bhobject = "AAL2AP";
	      final RockDatabaseSession rockDatabaseSession = new RockDatabaseSession(testDwhRep, testDwh, testEtlRep);
	      final Busyhour whereBusyhour = new Busyhour(testDwhRep);
	      whereBusyhour.setVersionid(newVersionId);
	      whereBusyhour.setTargetversionid(targetVersionId);
	      whereBusyhour.setBhlevel(bhlevel);
	      whereBusyhour.setBhtype(bhtype);
	      whereBusyhour.setBhobject(bhobject);
	      
	      // Before delete ensure mappings are in dB beforehand.
	      final Vector<Busyhourmapping> mappings = rockDatabaseSession.getBusyhourmappings(whereBusyhour);
	      assertTrue("For "+newVersionId+" At start of test Expected a list of Busyhourmappings but got "+mappings.size(), mappings.size()>0);
	     
	      //Execution
	      // Test mappings have been removed from dB.
	      rockDatabaseSession.deleteBusyhourMappedTypes(whereBusyhour);
	      //Asserts
	      final Vector<Busyhourmapping> result = rockDatabaseSession.getBusyhourmappings(whereBusyhour);
	      assertTrue("For "+newVersionId+" Expected list of Busyhourmappings to be empty but got "+result.size(), result.size()==0);
	  } catch (DatabaseException e) {
	      fail("testDeleteBusyhourMappedTypes Exception: " + e.getMessage());
	  }
  } // testDeleteBusyhourMappedTypes
  
  private DefaultPlaceholder createDefaultPlaceholder(final String versionid, final String bhlevel, final String bhtype, final String targetversionid, final String bhobject) {
	  final String aggregationType = null;
      final Integer lookback = 0;
      final Integer nThreshold = 0;
      final Integer pThreshold = 0;
      final String placeholdertype = null;
      final String description = "";
      final String where = "";
      final String criteria = "";
      final Boolean enabled = false; 
      final String grouping = null;
      final Integer reactivateviews = 0;
	  DefaultPlaceholder placeholder = new DefaultPlaceholder(versionid, bhlevel, bhtype, targetversionid, bhobject, aggregationType, lookback, nThreshold, pThreshold, placeholdertype, description, where, criteria, enabled, grouping, reactivateviews);
	  List<MappedType> mappedTypes = new ArrayList<MappedType>();
	  
//	  VERSIONID=CUSTOM_DC_E_CPP:((123))
//	  BHLEVEL=DC_E_CPP_AAL2APBH	
//	  BHTYPE=CTP_CP0	
//	  TARGETVERSIONID=DC_E_CPP:((129))	
//	  BHOBJECT=AAL2AP	
//	  TYPEID=DC_E_CPP:((129)):DC_E_CPP_AAL2AP	
//	  BHTARGETTYPE=DC_E_CPP_AAL2AP	
//	  BHTARGETLEVEL=AAL2AP_CTP_CP0	
//	  ENABLE=1
	  final String typeId = "DC_E_CPP:((129)):DC_E_CPP_AAL2AP";
	  final String bhtargetlevel = "AAL2AP_CTP_CP0";
	  final String bhtargettype = "DC_E_CPP_AAL2AP";
	  final MappedType mappedType1 = new DefaultMappedType(typeId, bhtargetlevel, bhtargettype, true);
	  mappedTypes.add(mappedType1);
	  placeholder.setMappedTypes(mappedTypes);
	  return placeholder;
  } // createDefaultPlaceholder

}
