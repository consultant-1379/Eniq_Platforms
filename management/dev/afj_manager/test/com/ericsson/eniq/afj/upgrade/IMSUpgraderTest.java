package com.ericsson.eniq.afj.upgrade;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.dwhm.CreateViewsAction;
import com.distocraft.dc5000.dwhm.PartitionAction;
import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.dwhm.VersionUpdateAction;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.dwhm.CreateViewsActionFactory;
import com.ericsson.eniq.dwhm.PartitionActionFactory;
import com.ericsson.eniq.dwhm.StorageTimeActionFactory;
import com.ericsson.eniq.dwhm.VersionUpdateActionFactory;
import com.ericsson.eniq.engine.EngineRestarterWrapper;
import com.ericsson.eniq.repository.ActivationCacheWrapper;
import com.ericsson.eniq.repository.DataFormatCacheWrapper;
import com.ericsson.eniq.repository.PhysicalTableCacheWrapper;
import com.ericsson.eniq.common.setWizards.StatsCreateLoaderSet;
import com.ericsson.eniq.common.setWizards.StatsCreateTPDirCheckerSet;
import com.ericsson.eniq.techpacksdk.CreateLoaderSetFactory;
import com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory;


public class IMSUpgraderTest {

	  /**
	   * 
	   */
	  private static final String BASE_TP_ID = "SOME_BASE_RELEASE_ID";
	  
	  private static final String BASE_TP_VERSION = "DC_E_IMS:((1))";

	  private final Mockery context = new JUnit4Mockery() {

	    {
	      setImposteriser(ClassImposteriser.INSTANCE);
	    }
	  };

	  private static final String SOME_TP = "DC_E_IMS";

	  private static final String SOME_VERSION = "((1))";

	  private static final Long SOME_ID = 123L;

	  private static final String RAW_TYPE = "RAW";

	  private static final Long SOME_COL_NBR = 111L;

	  private static final Long SOME_COUNTER_COL_NBR = 11L;

	  private AFJDelta mockAfjDelta;
	  
	  private AFJMeasurementType mockAFJMeasurementType1_1;

	  private AFJMeasurementTag mockAFJTag1_1;

	  private AFJMeasurementTag mockAFJTag1_2;

	  private AFJMeasurementTag mockAFJTag2_1;

	  private AFJMeasurementTag mockAFJTag2_2;

	  private AFJMeasurementCounter mockAFJMeasurementCounter1_1_1;

	  private AFJMeasurementCounter mockAFJMeasurementCounter1_1_2;

	  private AFJMeasurementCounter mockAFJMeasurementCounter1_2_1;

	  private AFJMeasurementCounter mockAFJMeasurementCounter1_2_2;

	  private AFJMeasurementCounter mockAFJMeasurementCounter2_1_1;

	  private AFJMeasurementCounter mockAFJMeasurementCounter2_1_2;

	  private AFJMeasurementCounter mockAFJMeasurementCounter2_2_1;
	  
	  private Meta_collection_sets mockMetaCollectionSets;
	  
	  private Meta_collections mockMeta_collections;
	  
	  private Dwhtype mockDwhtype;
	  
	  private Versioning mockVersioning;
	  
	  private Measurementcolumn mockMeasurementcolumn;

	  private PartitionAction mockPartitionAction;

	  private StorageTimeAction mockStorageTimeAction;

	  private VersionUpdateAction mockVersionUpdateAction;

	  private CreateViewsAction mockCreateViewsAction;
	  
	  private StatsCreateLoaderSet mockCreateLoaderSet;
	  
	  private StatsCreateTPDirCheckerSet mockCreateTPDirCheckerSet;
	  
	  private AFJDatabaseCommonUtils mockAFJDatabaseCommonUtils;
	  
	  private AFJDatabaseHandler mockDatabaseHandler;

	  private RockFactory mockDwhrep;
	  
	  private RockFactory mockEtlrep;
	  
	  private RockFactory mockDwh;
	  
	  private RockFactory mockDbaDwh;

	  private CommonSetGenerator mockCommonSetGenerator;

	  /**
	   * @throws java.lang.Exception
	   */
	  @BeforeClass
	  public static void setUpBeforeClass() throws Exception {
	    
	    System.setProperty("CONF_DIR", System.getProperty("user.dir"));
	    
	    PropertiesUtility.setPropertiesUtility(null);
	    PropertiesUtility.setAfjProperties(null);
	    
	    final File propertyFile = new File(System.getProperty("user.dir"), "AFJManager.properties");
	    String iqLoaderDir = "iqloader.dir=" + System.getProperty("user.dir") + "/eniq/log/sw_log/iqloader\n";
	    iqLoaderDir = iqLoaderDir.replace("\\", "/");
	    String rejectedDir = "rejected.dir=" + System.getProperty("user.dir") + "/eniq/data/rejected\n";
	    rejectedDir = rejectedDir.replace("\\", "/");
	    String etldataDir = "etldata.dir=" + System.getProperty("user.dir") + "/eniq/data/etldata\n";
	    etldataDir = etldataDir.replace("\\", "/");
	    try {
	      final PrintWriter pw = new PrintWriter(new FileWriter(propertyFile));
	      pw.write("bss.name=DC_E_BSS\n");
	      pw.write("sts.name=DC_E_CMN_STS\n");
	      pw.write("dc_e_ims.interface=INTF_DC_E_IMS\n");
	      pw.write("datasize.integer=18\n");
	      pw.write("datascale.integer=0\n");
	      pw.write("DC_E_IMS.datasize.PEG.numeric=20\n");
	      pw.write("DC_E_IMS.datascale.PEG.numeric=0\n");
	      pw.write("DC_E_IMS.datasize.GAUGE.numeric=24\n");
	      pw.write("DC_E_IMS.datascale.GAUGE.numeric=4\n");
	      pw.write("mom.decimal=numeric\n");
	      pw.write("mom.integer=integer\n");
	      
	      pw.write("ims.name=DC_E_IMS\n");
	      pw.write("mom.decimal=numeric\n");
	      pw.write("datasize.numeric=18\n");
	      pw.write("datascale.numeric=6\n");
	      pw.write("default.includesql=0\n");
	      pw.write("default.followjohn=1\n");
	      pw.write("default.uniquevalue=0\n");
	      pw.write("default.nullable=1\n");
	      pw.write("default.indexes=\n");
	      pw.write("default.releaseid=\n");
	      pw.write("default.uniquekey=0\n");
	      pw.write("default.coltype=COUNTER\n");
	      pw.write("default.universeobject=\n");
	      pw.write("default.universeclass=\n");
	      pw.write("DC_E_IMS.parser=mdc\n");	      
	      pw.write("default.dataformat.objecttype=Measurement\n");
	      pw.write("default.dataformat.objecttype=Measurement\n");
	      pw.write("default.dataformat.processinstruction=\n");
	      pw.write("default.measurementtype.joinable=\n");
	      pw.write("default.measurementtype.sizing=medium\n");
	      pw.write("default.measurementtype.totalagg=0\n");
	      pw.write("default.measurementtype.elementbhsupport=1\n");
	      pw.write("default.measurementtype.rankingtable=0\n");
	      pw.write("default.measurementtype.deltacalcsupport=0\n");
	      pw.write("default.measurementtype.plaintable=0\n");
	      pw.write("default.measurementtype.universeextension=ALL\n");
	      pw.write("default.measurementtype.vectorsupport=0\n");
	      pw.write("default.measurementtype.dataformatsupport=1\n");
	      pw.write("default.measurementtable.tablelevel=RAW\n");
	      pw.write("default.measurementtable.partitionplan=medium_raw\n");
	      pw.write("default.transformer.description=\n");
	      pw.write("default.transformer.type=SPECIFIC\n");
	      pw.write("default.interfacemeasurement.status=1\n");
	      pw.write("default.interfacemeasurement.techpackversion=N/A\n");
	      pw.write("default.partitiontype=RAW\n");
	      pw.write("default.storagetime=-1\n");
	      pw.write("default.ide.templates=5.2\n");
	      pw.write(iqLoaderDir);
	      pw.write(rejectedDir);
	      pw.write(etldataDir);
	      pw.write("joined.dir=joined\n");	            
	      pw.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    final File etlcProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
	    try {
	    	final PrintWriter pw = new PrintWriter(new FileWriter(etlcProperties));
	      pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
	      pw.write("ENGINE_DB_USERNAME = sa\n");
	      pw.write("ENGINE_DB_PASSWORD = \n");
	      pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
	      pw.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    
	    final File staticProperties = new File(System.getProperty("user.dir"), "static.properties");
	    try {
	    	final PrintWriter pw = new PrintWriter(new FileWriter(staticProperties));
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
		  final File propertyFile = new File(System.getProperty("user.dir"), "AFJManager.properties");
	    propertyFile.delete();
	    final File etlcProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
	    etlcProperties.delete();
	    final File staticProperties = new File(System.getProperty("user.dir"), "static.properties");
	    staticProperties.delete();
	    System.clearProperty("CONF_DIR");
	  }
	  
	  /**
	   * @throws java.lang.Exception
	   */
	  @Before
	  public void setUp() throws Exception {
	    
		  
	    mockAfjDelta = context.mock(AFJDelta.class);
	    
	    mockAFJMeasurementType1_1 = context.mock(AFJMeasurementType.class, "MT1.1");	    

	    mockAFJTag1_1 = context.mock(AFJMeasurementTag.class, "T1.1");
	    
	    mockAFJTag1_2 = context.mock(AFJMeasurementTag.class, "T1.2");

	    mockAFJTag2_1 = context.mock(AFJMeasurementTag.class, "T2.1");
	    
	    mockAFJTag2_2 = context.mock(AFJMeasurementTag.class, "T2.2");

	    mockAFJMeasurementCounter1_1_1 = context.mock(AFJMeasurementCounter.class, "C1.1.1");
	    
	    mockAFJMeasurementCounter1_1_2 = context.mock(AFJMeasurementCounter.class, "C1.1.2");
	    
	    mockAFJMeasurementCounter1_2_1 = context.mock(AFJMeasurementCounter.class, "C1.2.1");
	    
	    mockAFJMeasurementCounter1_2_2 = context.mock(AFJMeasurementCounter.class, "C1.2.2");
	    
	    mockAFJMeasurementCounter2_1_1 = context.mock(AFJMeasurementCounter.class, "C2.1.1");
	    
	    mockAFJMeasurementCounter2_1_2 = context.mock(AFJMeasurementCounter.class, "C2.1.2");
	    
	    mockAFJMeasurementCounter2_2_1 = context.mock(AFJMeasurementCounter.class, "C2.2.1");
	    
	    mockMeasurementcolumn = context.mock(Measurementcolumn.class, "BaseTPColumn");
	    
	    mockAFJDatabaseCommonUtils = context.mock(AFJDatabaseCommonUtils.class);
	    
	    mockMeta_collections = context.mock(Meta_collections.class);
	    
	    mockMetaCollectionSets = context.mock(Meta_collection_sets.class);
	    
	    mockDwhtype = context.mock(Dwhtype.class);
	    
	    mockVersioning = context.mock(Versioning.class);
	    
	    mockCommonSetGenerator = context.mock(CommonSetGenerator.class);
	    
	    mockDatabaseHandler = context.mock(AFJDatabaseHandler.class);
	    
	    mockDwhrep = context.mock(RockFactory.class, "Dwhrep");
	    mockEtlrep = context.mock(RockFactory.class, "Etlrep");
	    mockDwh = context.mock(RockFactory.class, "Dwh");
	    mockDbaDwh = context.mock(RockFactory.class, "DbaDwh"); 

	    
	    mockCreateViewsAction = context.mock(CreateViewsAction.class);
	    mockPartitionAction = context.mock(PartitionAction.class);
	    mockStorageTimeAction = context.mock(StorageTimeAction.class);
	    mockVersionUpdateAction = context.mock(VersionUpdateAction.class);
	    mockCreateLoaderSet = context.mock(StatsCreateLoaderSet.class);
	    mockCreateTPDirCheckerSet = context.mock(StatsCreateTPDirCheckerSet.class);

	    ActivationCacheWrapper.setInitializeAllowed(false);
	    DataFormatCacheWrapper.setInitializeAllowed(false);
	    EngineRestarterWrapper.setExecutionAllowed(false);
	    PhysicalTableCacheWrapper.setInitializeAllowed(false);
	    
	    AFJDatabaseCommonUtilsFactory.setInstance(mockAFJDatabaseCommonUtils);
	    CommonSetGeneratorFactory.setInstance(mockCommonSetGenerator);
	    AFJDatabaseHandler.setInstance(mockDatabaseHandler);
	    CreateViewsActionFactory.setInstance(mockCreateViewsAction);
	    PartitionActionFactory.setInstance(mockPartitionAction);
	    StorageTimeActionFactory.setInstance(mockStorageTimeAction);
	    VersionUpdateActionFactory.setInstance(mockVersionUpdateAction);
	    CreateLoaderSetFactory.setInstance(mockCreateLoaderSet);
	    CreateTPDirCheckerSetFactory.setInstance(mockCreateTPDirCheckerSet);
	  
	  }

	  /**
	   * @throws java.lang.Exception
	   */
	  @After
	  public void tearDown() throws Exception {
	    ActivationCacheWrapper.setInitializeAllowed(true);
	    DataFormatCacheWrapper.setInitializeAllowed(true);
	    EngineRestarterWrapper.setExecutionAllowed(true);
	    PhysicalTableCacheWrapper.setInitializeAllowed(true);
	    
	    AFJDatabaseCommonUtilsFactory.setInstance(null);
	    CommonSetGeneratorFactory.setInstance(null);
	    AFJDatabaseHandler.setInstance(null);
	    CreateViewsActionFactory.setInstance(null);
	    PartitionActionFactory.setInstance(null);
	    StorageTimeActionFactory.setInstance(null);
	    VersionUpdateActionFactory.setInstance(null);
	    CreateLoaderSetFactory.setInstance(null);
	    CreateTPDirCheckerSetFactory.setInstance(null);
	    
	  }
	  
	  
	  /**
	   * Test method for {@link com.ericsson.eniq.afj.upgrade.IMSUpgrader#upgrade(com.ericsson.eniq.afj.common.AFJDelta)}.
	   * @throws Exception 
	   */
	  @Test
	  public void testUpgrade() throws Exception {
	    
	    context.checking(new Expectations() {

	      {
	        allowing(mockDatabaseHandler).getDwhrep();
	        will(returnValue(mockDwhrep));
	        allowing(mockDatabaseHandler).getEtlrep();
	        will(returnValue(mockEtlrep));
	        allowing(mockDatabaseHandler).getDwh();
	        will(returnValue(mockDwh));
	        allowing(mockDatabaseHandler).getDbaDwh();
	        will(returnValue(mockDbaDwh));
	        
	        allowing(mockAfjDelta).getTechPackName();
	        will(returnValue(SOME_TP));
	        
	        allowing(mockAFJDatabaseCommonUtils).getActiveTechPackVersion(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(SOME_TP + ":" + SOME_VERSION));
	        allowing(mockAFJDatabaseCommonUtils).getTechpack(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(mockMetaCollectionSets));
	        
	        allowing(mockAFJDatabaseCommonUtils).getMeasurementColumnNumber(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(SOME_COL_NBR));
	        allowing(mockAFJDatabaseCommonUtils).getNextMeasurementCounterColumnNumber(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(SOME_COUNTER_COL_NBR));
	        allowing(mockAFJDatabaseCommonUtils).getDwhType(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(mockDwhtype));
	        allowing(mockAFJDatabaseCommonUtils).getInterfaceVersion(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(SOME_VERSION));
	        allowing(mockAFJDatabaseCommonUtils).getTypeActivations(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(new ArrayList<Typeactivation>()));
	        allowing(mockAFJDatabaseCommonUtils).getVersioning(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(mockVersioning));
	        allowing(mockAFJDatabaseCommonUtils).getMeasurementColumns(with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(new ArrayList<Measurementcolumn>()));
	        allowing(mockAFJDatabaseCommonUtils).getMeta_collections(with(any(Long.class)), with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
	        will(returnValue(new Vector<Meta_collections>(Arrays.asList(new Meta_collections[] { mockMeta_collections }))));

	        allowing(mockMetaCollectionSets).getCollection_set_name();
	        will(returnValue(SOME_TP));
	        allowing(mockMetaCollectionSets).getVersion_number();
	        will(returnValue(SOME_VERSION));
	        allowing(mockMetaCollectionSets).getCollection_set_id();
	        will(returnValue(SOME_ID));
	        
	        allowing(mockMeta_collections).getCollection_id();
	        will(returnValue(SOME_ID));
	        
	        allowing(mockAfjDelta).getMeasurementTypes();
	        will(returnValue(new ArrayList<AFJMeasurementType>(Arrays.asList(new AFJMeasurementType[] { mockAFJMeasurementType1_1 }))));
	        
	        allowing(mockAFJMeasurementType1_1).isTypeNew();
	        will(returnValue(false));
	        allowing(mockAFJMeasurementType1_1).getTpName();
	        will(returnValue(SOME_TP));
	        allowing(mockAFJMeasurementType1_1).getTags();
	        will(returnValue(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] { mockAFJTag1_1, mockAFJTag1_2 }))));
	        allowing(mockAFJMeasurementType1_1).getTypeName();
	        will(returnValue(RAW_TYPE));
	        allowing(mockAFJMeasurementType1_1).getTpVersion();
	        will(returnValue(BASE_TP_ID));

	        allowing(mockAFJTag1_1).getNewCounters();
	        will(returnValue(Arrays.asList(new AFJMeasurementCounter[] {mockAFJMeasurementCounter1_1_1, mockAFJMeasurementCounter1_1_2})));
	        allowing(mockAFJTag1_1).getTagName();
	        will(returnValue("SOME_TAG_1_1"));
	        
	        allowing(mockAFJTag1_2).getNewCounters();
	        will(returnValue(Arrays.asList(new AFJMeasurementCounter[] {mockAFJMeasurementCounter1_2_1, mockAFJMeasurementCounter1_2_2})));
	        allowing(mockAFJTag1_2).getTagName();
	        will(returnValue("SOME_TAG_1_2"));
	        
	        allowing(mockAFJTag2_1).getNewCounters();
	        will(returnValue(Arrays.asList(new AFJMeasurementCounter[] {mockAFJMeasurementCounter2_1_1, mockAFJMeasurementCounter2_1_2})));
	        allowing(mockAFJTag2_1).getTagName();
	        will(returnValue("SOME_TAG_2_1"));
	        
	        allowing(mockAFJTag2_2).getNewCounters();
	        will(returnValue(Arrays.asList(new AFJMeasurementCounter[] {mockAFJMeasurementCounter2_2_1})));
	        allowing(mockAFJTag2_2).getTagName();
	        will(returnValue("SOME_TAG_2_2"));
	        
	        allowing(mockAFJMeasurementCounter1_1_1).getCounterName();
	        will(returnValue("SOME_COUNTER_1_1_1"));
	        allowing(mockAFJMeasurementCounter1_1_1).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter1_1_1).getDescription();
	        will(returnValue("1_1_1"));
	        allowing(mockAFJMeasurementCounter1_1_1).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter1_1_1).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter1_1_2).getCounterName();
	        will(returnValue("SOME_COUNTER_1_1_2"));
	        allowing(mockAFJMeasurementCounter1_1_2).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter1_1_2).getDescription();
	        will(returnValue("1_1_2"));
	        allowing(mockAFJMeasurementCounter1_1_2).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter1_1_2).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter1_2_1).getCounterName();
	        will(returnValue("SOME_COUNTER_1_2_1"));
	        allowing(mockAFJMeasurementCounter1_2_1).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter1_2_1).getDescription();
	        will(returnValue("1_2_1"));
	        allowing(mockAFJMeasurementCounter1_2_1).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter1_2_1).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter1_2_2).getCounterName();
	        will(returnValue("SOME_COUNTER_1_2_2"));
	        allowing(mockAFJMeasurementCounter1_2_2).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter1_2_2).getDescription();
	        will(returnValue("1_2_2"));
	        allowing(mockAFJMeasurementCounter1_2_2).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter1_2_2).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter2_1_1).getCounterName();
	        will(returnValue("SOME_COUNTER_2_1_1"));
	        allowing(mockAFJMeasurementCounter2_1_1).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter2_1_1).getDescription();
	        will(returnValue("2_1_1"));
	        allowing(mockAFJMeasurementCounter2_1_1).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter2_1_1).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter2_1_2).getCounterName();
	        will(returnValue("SOME_COUNTER_2_1_2"));
	        allowing(mockAFJMeasurementCounter2_1_2).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter2_1_2).getDescription();
	        will(returnValue("2_1_2"));
	        allowing(mockAFJMeasurementCounter2_1_2).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter2_1_2).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockAFJMeasurementCounter2_2_1).getCounterName();
	        will(returnValue("SOME_COUNTER_2_2_1"));
	        allowing(mockAFJMeasurementCounter2_2_1).getResultType();
	        will(returnValue("decimal"));
	        allowing(mockAFJMeasurementCounter2_2_1).getDescription();
	        will(returnValue("2_2_1"));
	        allowing(mockAFJMeasurementCounter2_2_1).getType();
	        will(returnValue("GAUGE"));
	        allowing(mockAFJMeasurementCounter2_2_1).getSubType();
	        will(returnValue("SUM"));
	        
	        allowing(mockDwhrep).insertData(with(any(Object.class)));
	        will(returnValue(1));
	        
	        allowing(mockVersioning).getVersionid();
	        will(returnValue(BASE_TP_VERSION));
	        
	        allowing(mockMeasurementcolumn).getDataname();
	        will(returnValue("DATE_ID"));
	        allowing(mockMeasurementcolumn).getDatatype();
	        will(returnValue("date"));
	        allowing(mockMeasurementcolumn).getDatasize();
	        will(returnValue(0));
	        allowing(mockMeasurementcolumn).getDatascale(); 
	        will(returnValue(0));
	        allowing(mockMeasurementcolumn).getUniquevalue();
	        will(returnValue(255));
	        allowing(mockMeasurementcolumn).getNullable();
	        will(returnValue(1));
	        allowing(mockMeasurementcolumn).getIndexes();
	        will(returnValue("LF"));
	        allowing(mockMeasurementcolumn).getDescription();
	        will(returnValue(""));
	        allowing(mockMeasurementcolumn).getDataid();
	        will(returnValue("DATE_ID"));
	        allowing(mockMeasurementcolumn).getReleaseid(); 
	        will(returnValue(BASE_TP_ID));
	        allowing(mockMeasurementcolumn).getUniquekey();
	        will(returnValue(0));
	        allowing(mockMeasurementcolumn).getIncludesql();
	        will(returnValue(0));
	        allowing(mockMeasurementcolumn).getColtype();
	        will(returnValue(null));
	        
	        allowing(mockDatabaseHandler).commitTransaction(with(any(Boolean.class)));
	        
	        allowing(mockCommonSetGenerator).updateLoaderAction(with(any(String.class)));

	        allowing(mockVersionUpdateAction).execute(with(any(String.class)));
	        
	        allowing(mockCreateLoaderSet).create(with(any(String.class)));
	        
	        allowing(mockCreateTPDirCheckerSet).createForAFJ(with(any(String.class)), with(any(Long.class)));
	        
	        allowing(mockDwhrep).setSelectSQL(with(any(Boolean.class)), with(any(String.class)));
	        
	        allowing(mockDatabaseHandler).closeConnections();
	      }
	    });
	    
	    final IMSUpgrader upgrader = new IMSUpgrader();
	    final String result = upgrader.upgrade(mockAfjDelta);
	    assertFalse(result.equals(""));
	  }



}
