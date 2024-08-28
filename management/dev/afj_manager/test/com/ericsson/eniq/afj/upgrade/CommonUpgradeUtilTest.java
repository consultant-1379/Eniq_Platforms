/**
 * 
 */
package com.ericsson.eniq.afj.upgrade;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
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
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.CommonSetGeneratorFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil;
import com.ericsson.eniq.dwhm.CreateViewsActionFactory;
import com.ericsson.eniq.dwhm.VersionUpdateActionFactory;
import com.ericsson.eniq.engine.EngineRestarterWrapper;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.eniq.repository.ActivationCacheWrapper;
import com.ericsson.eniq.repository.DataFormatCacheWrapper;
import com.ericsson.eniq.repository.PhysicalTableCacheWrapper;
import com.ericsson.eniq.techpacksdk.CreateLoaderSetFactory;
import com.ericsson.eniq.techpacksdk.CreateTPDirCheckerSetFactory;
import com.ericsson.eniq.common.setWizards.StatsCreateLoaderSet;
import com.ericsson.eniq.common.setWizards.StatsCreateTPDirCheckerSet;

/**
 * @author esunbal
 *
 */
public class CommonUpgradeUtilTest {
	
	  private AFJMeasurementType mockAFJMeasurementType1_1;
	  
	  private AFJMeasurementCounter mockAFJMeasurementCounter1_1_1;
	
	  private static final String BASE_TP_ID = "SOME_BASE_RELEASE_ID";
	  
	  private static final String BASE_TP_VERSION = "DC_E_BSS:((1))";

	  private static final String SOME_TP = "DC_E_BSS";
	  
	  private static final String SOME_MEASNAME = "ABC";
	  
	  private static final long SOME_COLNUMBER = 1L;

	  private static final String SOME_VERSION = "((1))";
	  
	  private static final Long SOME_ID = 123L;
	  
	  private static ArrayList<Measurementcolumn> measColumnList;
	  
	  private final Mockery context = new JUnit4Mockery() {

	    {
	      setImposteriser(ClassImposteriser.INSTANCE);
	    }
	  };
	  
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
	  
	  private static Connection connection;
		
	  private static Statement statement;
	  
	  private AFJMeasurementTag mockAFJTag1_1;

	  private AFJMeasurementTag mockAFJTag1_2;  
	  

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		  try {
				Class.forName("org.hsqldb.jdbcDriver").newInstance();
				connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			statement = connection.createStatement();
			
			statement.execute("CREATE TABLE META_DATABASES (USERNAME VARCHAR(31), VERSION_NUMBER VARCHAR(31), "
					+ "TYPE_NAME VARCHAR(31), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(31), "
					+ "CONNECTION_STRING VARCHAR(31), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(31), "
					+ "DRIVER_NAME VARCHAR(31), DB_LINK_NAME VARCHAR(31))");
			
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");
			
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'etlrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");
			
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'dwh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");
		

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
			pw.write("sts.name=DC_E_CMN_STS\n");
			pw.write("DC_E_BSS.measname=DC_E_BSS_BSC\n");
			pw.write("datasize.integer=18\n");
			pw.write("datascale.integer=0\n");
			pw.write("stn.datasize.PEG.numeric=20\n");
			pw.write("stn.datascale.PEG.numeric=0\n");
			pw.write("stn.datasize.GAUGE.numeric=24\n");
			pw.write("stn.datascale.GAUGE.numeric=4\n");
			pw.write("mom.decimal=numeric\n");
			pw.write("mom.integer=integer\n");
			pw.write("DC_E_BSS.datasize.PEG.numeric=18\n");
			pw.write("DC_E_BSS.datascale.PEG.numeric=0\n");
			pw.write("DC_E_BSS.datasize.GAUGE.numeric=18\n");
			pw.write("DC_E_BSS.datascale.GAUGE.numeric=0\n");
			pw.write("bss.name=DC_E_BSS\n");
			pw.write("default.bss.measname=DC_E_BSS_BSC\n");    
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
			pw.write("DC_E_BSS.parser=eniqasn1\n");
			pw.write("DC_E_BSS.interface=INTF_DC_E_BSS_APG\n");
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
			pw.write("DC_E_BSS.keys=OSS_ID:OSS_ID:1:1:50:varchar:0:HG:255:1:Key:OSS Id:0:0,SN:SN:0:1:255:varchar:0::255:1:Key::0:0,MOID:MOID:0:1:255:varchar:0:HG:255:1:Key:MOID:0:0,BSC:BSC:1:1:64:varchar:0:HG:255:1:KEY:BSC:0:1");      
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

	    measColumnList = new ArrayList<Measurementcolumn>();	    
		final AFJDatabaseHandler connection = AFJDatabaseHandler.getInstance();
	    final Measurementcolumn mc1 = new Measurementcolumn(connection.getDwhrep());
	    mc1.setColnumber(1l);
	    mc1.setDataname("ABC");
	    mc1.setDataid("ABC");
	    mc1.setDatatype("integer");
	    mc1.setDatasize(18);
	    mc1.setDatascale(0);
	    measColumnList.add(mc1);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		mockAFJMeasurementType1_1 = context.mock(AFJMeasurementType.class, "MT1.1");
		mockAFJMeasurementCounter1_1_1 = context.mock(AFJMeasurementCounter.class, "C1.1.1");
		mockAFJTag1_1 = context.mock(AFJMeasurementTag.class, "T1.1");	    
	    mockAFJTag1_2 = context.mock(AFJMeasurementTag.class, "T1.2");
		
		mockDwhtype = context.mock(Dwhtype.class);
		mockCommonSetGenerator = context.mock(CommonSetGenerator.class);
		mockMetaCollectionSets = context.mock(Meta_collection_sets.class);
		mockAFJDatabaseCommonUtils = context.mock(AFJDatabaseCommonUtils.class);
		mockVersionUpdateAction = context.mock(VersionUpdateAction.class);
		mockCreateViewsAction = context.mock(CreateViewsAction.class);
		mockCreateLoaderSet = context.mock(StatsCreateLoaderSet.class);
		mockMeta_collections = context.mock(Meta_collections.class);
		mockCreateTPDirCheckerSet = context.mock(StatsCreateTPDirCheckerSet.class);		
		mockVersioning = context.mock(Versioning.class);
		
	    ActivationCacheWrapper.setInitializeAllowed(false);
	    DataFormatCacheWrapper.setInitializeAllowed(false);
	    EngineRestarterWrapper.setExecutionAllowed(false);
	    PhysicalTableCacheWrapper.setInitializeAllowed(false);
	    
	    mockDatabaseHandler = context.mock(AFJDatabaseHandler.class);
	    mockDwhrep = context.mock(RockFactory.class, "Dwhrep");
	    mockEtlrep = context.mock(RockFactory.class, "Etlrep");
	    mockDwh = context.mock(RockFactory.class, "Dwh");
	    mockDbaDwh = context.mock(RockFactory.class, "DbaDwh");
	    
	    AFJDatabaseCommonUtilsFactory.setInstance(mockAFJDatabaseCommonUtils);
	    AFJDatabaseHandler.setInstance(mockDatabaseHandler);
	    CommonSetGeneratorFactory.setInstance(mockCommonSetGenerator);
	    VersionUpdateActionFactory.setInstance(mockVersionUpdateAction);
	    CreateViewsActionFactory.setInstance(mockCreateViewsAction);
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
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#initialiseCaches()}.
	 */
	@Test
	public void testInitialiseCaches() throws Exception{		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.initialiseCaches();
		assertTrue(true);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#initialiseCommonSetGenerator(java.lang.String)}.
	 */
	@Test
	public void testInitialiseCommonSetGenerator() throws Exception{
		context.checking(new Expectations(){
			{
				allowing(mockAFJDatabaseCommonUtils).getActiveTechPackVersion(with(any(String.class)), with(any(RockFactory.class)));
				will(returnValue(SOME_TP + ":" + SOME_VERSION));
				
				allowing(mockAFJDatabaseCommonUtils).getTechpack(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
				will(returnValue(mockMetaCollectionSets));
				
		        allowing(mockMetaCollectionSets).getCollection_set_name();
		        will(returnValue(SOME_TP));
		        
		        allowing(mockMetaCollectionSets).getVersion_number();
		        will(returnValue(SOME_VERSION));
		        
		        allowing(mockMetaCollectionSets).getCollection_set_id();
		        will(returnValue(SOME_ID));     
		        
			}			
		});
		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.initialiseCommonSetGenerator(SOME_TP);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#createDwhrepEntries(java.lang.String, java.lang.String, com.ericsson.eniq.afj.common.AFJMeasurementCounter, long, long)}.
	 */
	@Test
	public void testCreateDwhrepEntries() throws Exception{
		context.checking(new Expectations(){
			{
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
				
		        allowing(mockDwhrep).insertData(with(any(Object.class)));
		        will(returnValue(1));

			}			
		});
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.createDwhrepEntries(SOME_TP, SOME_MEASNAME, mockAFJMeasurementCounter1_1_1, SOME_COLNUMBER, SOME_COLNUMBER);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#versionUpdateAction(java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testVersionUpdateAction() throws Exception{
		context.checking(new Expectations(){
			{
				allowing(mockVersionUpdateAction).execute(with(any(String.class)));
				
		        allowing(mockAFJDatabaseCommonUtils).getDwhType(with(any(String.class)), with(any(RockFactory.class)));
		        will(returnValue(mockDwhtype));
				
				
			}			
		});
		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.versionUpdateAction(SOME_TP, SOME_MEASNAME, false);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getDataItemObject(java.lang.String, java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, int, int)}.
	 */
	@Test
	public void testGetDataItemObject() throws Exception{		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Dataitem actualOutputObject = commonUpgradeUtil.getDataItemObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE:mdc","ifInErrors",new Long(26),"ifInErrors","PEG","numeric",new Integer(20),new Integer(0));		
		final String actualOutputValue = actualOutputObject.getDataid();
		final String expectedOutputValue = "ifInErrors";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementColumnObject(java.lang.String, java.lang.String, java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, int)}.
	 */
	@Test
	public void testGetMeasurementColumnObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Measurementcolumn actualOutputObject = commonUpgradeUtil.getMeasurementColumnObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW",
				"ifInErrors",new Long(26),"numeric",new Integer(20),new Integer(0),new Long(255),new Integer(1),"","Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.","ifInErrors","DC_E_STN:((B222))",new Integer(0),new Integer(1),"COUNTER",1);		
		final String actualOutputValue = actualOutputObject.getDataid();
		final String expectedOutputValue = "ifInErrors";	
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementCounterObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)}.
	 */
	@Test
	public void testGetMeasurementCounterObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);		
		final Measurementcounter actualOutputObject = commonUpgradeUtil.getMeasurementCounterObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE",
				"ifInErrors","Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.","SUM","SUM","PEG",new Long(102),"numeric",new Integer(20),new Integer(0),new Integer(1),"ifInErrors","","PEG","PEG","ifInErrors",1);
		
		final String actualOutputValue = actualOutputObject.getDataid();
		final String expectedOutputValue = "ifInErrors";
		
		assertEquals(expectedOutputValue,actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateDataFormat(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateDataFormat() throws Exception{	
		context.checking(new Expectations(){
			{
				allowing(mockAFJDatabaseCommonUtils).getActiveTechPackVersion(with(any(String.class)), with(any(RockFactory.class)));
				will(returnValue(SOME_TP + ":" + SOME_VERSION));
				
				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
				allowing(mockDwhrep).insertData(with(any(Object.class)));
		        will(returnValue(1));
				
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateDataFormat(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getDataFormatObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetDataFormatObject() {		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);	

		final Dataformat actualOutputObject = commonUpgradeUtil.getDataFormatObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE:mdc","DC_E_STN:((2)):DC_E_STN_E1INTERFACE","DC_E_STN:((2))","Measurement","DC_E_STN_E1INTERFACE","mdc");		
		final String actualOutputValue = actualOutputObject.getDataformatid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_E1INTERFACE:mdc";	

		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateDefaultTags(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateDefaultTags() throws Exception{	
		context.checking(new Expectations(){
			{
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
				allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));
				
				allowing(mockAFJMeasurementType1_1).getTags();
		        will(returnValue(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] { mockAFJTag1_1, mockAFJTag1_2 }))));
				
		        allowing(mockAFJTag1_1).getTagName();
		        will(returnValue("SOME_TAG_1_1"));
		        
		        allowing(mockAFJMeasurementType1_1).getDescription();
				will(returnValue("SOME_DESCRIPTION"));
				
				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
				allowing(mockDwhrep).insertData(with(any(Object.class)));
		        will(returnValue(1));
				
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateDefaultTags(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getDefaultTagsObject(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetDefaultTagsObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Defaulttags actualOutputObject = commonUpgradeUtil.getDefaultTagsObject("DC_E_STN:((2)):DC_E_STN_ABC:mdc","ABC","ABC Tag");		
		final String actualOutputValue = actualOutputObject.getDataformatid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_ABC:mdc";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateMeasurementTypeClass(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateMeasurementTypeClass() throws Exception{	
		context.checking(new Expectations(){
			{				
				allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));
				
				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJMeasurementType1_1).getTags();
		        will(returnValue(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] { mockAFJTag1_1, mockAFJTag1_2 }))));
				
		        allowing(mockAFJTag1_1).getTagName();
		        will(returnValue("SOME_TAG_1_1"));
				
				allowing(mockDwhrep).insertData(with(any(Object.class)));
		        will(returnValue(1));
				
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateMeasurementTypeClass(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementTypeClassObject(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetMeasurementTypeClassObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Measurementtypeclass actualOutputObject = commonUpgradeUtil.getMeasurementTypeClassObject("DC_E_STN:((2)):DC_E_STN_E1T1INTERFACE","DC_E_STN:((2))","E1T1INTERFACE");		
		final String actualOutputValue = actualOutputObject.getTypeclassid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_E1T1INTERFACE";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateMeasurmentType(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateMeasurmentType() throws Exception{	
		context.checking(new Expectations(){
			{				
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));

				allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));

				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));

				allowing(mockAFJMeasurementType1_1).getDescription();
				will(returnValue("SOME_DESCRIPTION"));

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));

			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateMeasurmentType(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementTypeObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	public void testGetMeasurementTypeObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Measurementtype actualOutputObject = commonUpgradeUtil.getMeasurementTypeObject(
				"DC_E_STN:((2)):DC_E_STN_E1INTERFACE","DC_E_STN:((2)):DC_E_STN_E1INTF","DC_E_STN_E1INTERFACE",
				"DC_E_STN","DC_E_STN_E1INTERFACE","",null,"DC_E_STN:((2))",
				"DC_E_STN:((2)):DC_E_STN_E1INTERFACE","DC_E_STN_E1INTERFACE",
				null,null,null,"medium",1,1,0,0,0,"ALL",0,1,1);		
		final String actualOutputValue = actualOutputObject.getTypeid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_E1INTERFACE";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateMeasurementTable(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateMeasurementTable() throws Exception{	
		context.checking(new Expectations(){
			{	
				allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));

				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));

			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateMeasurementTable(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementTableObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetMeasurementTableObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Measurementtable actualOutputObject = commonUpgradeUtil.getMeasurementTableObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE","RAW","DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW","DC_E_STN_E1INTERFACE_RAW",null,"medium_raw");		
		final String actualOutputValue = actualOutputObject.getMtableid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#popluateTransformer(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopluateTransformer() throws Exception{	
		context.checking(new Expectations(){
			{	
				allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));

				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));

			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.popluateTransformer(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getTransformerObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetTransformerObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Transformer actualOutputObject = commonUpgradeUtil.getTransformerObject("DC_E_STN:((2)):DC_E_STN_DHCPRELAYSERVERGROUP:mdc","DC_E_STN:((2))","","SPECIFIC");		
		final String actualOutputValue = actualOutputObject.getTransformerid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_DHCPRELAYSERVERGROUP:mdc";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateInterfaceMeasurement(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopulateInterfaceMeasurement() throws Exception{	
		context.checking(new Expectations(){
			{				
				allowing(mockAFJMeasurementType1_1).getTags();
		        will(returnValue(new ArrayList<AFJMeasurementTag>(Arrays.asList(new AFJMeasurementTag[] { mockAFJTag1_1, mockAFJTag1_2 }))));				
		        
		        allowing(mockAFJTag1_1).getTagName();
		        will(returnValue("SOME_TAG_1_1"));
		        
		        allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
				will(returnValue(SOME_TP + ":" + SOME_VERSION));
				
				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));
				
				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));
				
				allowing(mockAFJDatabaseCommonUtils).getInterfaceVersion(with(any(String.class)), with(any(RockFactory.class)));
		        will(returnValue("SOME_VERSION"));
				
				allowing(mockDwhrep).insertData(with(any(Object.class)));
		        will(returnValue(1));
				
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateInterfaceMeasurement(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getInterfaceMeasurementObject(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.sql.Timestamp, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetInterfaceMeasurementObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Interfacemeasurement actualOutputObject = commonUpgradeUtil.getInterfaceMeasurementObject("DHCPRELAYSERVERGROUP","DC_E_STN:((2)):DC_E_STN_DHCPRELAYSERVERGROUP:mdc","INTF_DC_E_STN","DC_E_STN:((2)):DC_E_STN_DHCPRELAYSERVERGROUP:mdc",1l,null,"Default tags for DC_E_STN_DHCPRELAYSERVERGROUP in DC_E_STN:((2)) with format mdc.","N/A","((100))");		
		final String actualOutputValue = actualOutputObject.getTagid();
		final String expectedOutputValue = "DHCPRELAYSERVERGROUP";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#insertKeysIntoDwhrep(java.lang.String, long, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInsertKeysIntoDwhrep() throws Exception{	
		context.checking(new Expectations(){
			{			

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.insertKeysIntoDwhrep(SOME_MEASNAME, 1L, "DC_E_BSS", "DC_E_BSS:((2))");
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getMeasurementKeyObject(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Long, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testGetMeasurementKeyObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Measurementkey actualOutputObject = commonUpgradeUtil.getMeasurementKeyObject("DC_E_STN:((2)):DC_E_STN_E1INTERFACE","SN","Sender Name",0,0,2l,"varchar",255,0,255l,1,"",1,"",0,"SN");		
		final String actualOutputValue = actualOutputObject.getTypeid();
		final String expectedOutputValue = "DC_E_STN:((2)):DC_E_STN_E1INTERFACE";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#insertValuesIntoTypeActivation(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testInsertValuesIntoTypeActivation() throws Exception{		
		context.checking(new Expectations(){
			{
				allowing(mockAFJDatabaseCommonUtils).getTypeActivations(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
				will(returnValue(new ArrayList<Typeactivation>()));

				allowing(mockAFJMeasurementType1_1).getTypeName();
				will(returnValue(SOME_MEASNAME));						

				allowing(mockAFJMeasurementType1_1).getTpName();
				will(returnValue(SOME_TP));

				allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));
			}			
		});


		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.insertValuesIntoTypeActivation(mockAFJMeasurementType1_1);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#populateMeasurementColumnBaseKeys(com.ericsson.eniq.afj.common.AFJMeasurementType, long)}.
	 */
	@Test
	public void testPopulateMeasurementColumnBaseKeys() throws Exception{
	    
	    context.checking(new Expectations() {
	    	{
	    	  allowing(mockAFJDatabaseCommonUtils).getActiveTechPackVersion(with(any(String.class)), with(any(RockFactory.class)));
	          will(returnValue(SOME_TP + ":" + SOME_VERSION));
	          allowing(mockDatabaseHandler).getDwhrep();
	          will(returnValue(mockDwhrep));
	          allowing(mockAFJDatabaseCommonUtils).getVersioning(with(any(String.class)), with(any(RockFactory.class)));
	          will(returnValue(mockVersioning));
	          allowing(mockVersioning).getVersionid();
	          will(returnValue(BASE_TP_VERSION));
	          allowing(mockAFJDatabaseCommonUtils).getMeasurementColumns((with(any(String.class))), with(any(RockFactory.class)));
	          will(returnValue(measColumnList));
	          allowing(mockDwhrep).insertData(with(any(Object.class)));
	          will(returnValue(1));
	        }
	    }
	    );


		final AFJMeasurementType amt = new AFJMeasurementType();
		amt.setTpName("DC_E_BSS");
		amt.setTpVersion("DC_E_BSS:((1))");
		amt.setTypeName("DC_E_BSS_ABC");
	    
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.populateMeasurementColumnBaseKeys(amt,10l);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#createLoaderSets(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testCreateLoaderSets() throws Exception{
		context.checking(new Expectations(){
			{
		        allowing(mockAFJMeasurementType1_1).getTpName();
		        will(returnValue(SOME_TP));
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
		        will(returnValue(BASE_TP_ID));
				allowing(mockAFJDatabaseCommonUtils).getTechpack(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
		        will(returnValue(mockMetaCollectionSets));
		        
		        allowing(mockMetaCollectionSets).getCollection_set_name();
		        will(returnValue(SOME_TP));
		        
		        allowing(mockMetaCollectionSets).getVersion_number();
		        will(returnValue(SOME_VERSION));
		        
		        allowing(mockMetaCollectionSets).getCollection_set_id();
		        will(returnValue(SOME_ID));	        
		        
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
		        will(returnValue(BASE_TP_ID));
		        
		        allowing(mockAFJMeasurementType1_1).getTypeName();
		        will(returnValue(SOME_MEASNAME));
		        
				allowing(mockCreateLoaderSet).create(with(any(String.class)));
			}			
		});

		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.createLoaderSets(mockAFJMeasurementType1_1);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#createDirectoryCheckerSets(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testCreateDirectoryCheckerSets() throws Exception{
		context.checking(new Expectations(){
			{
		        allowing(mockAFJMeasurementType1_1).getTpName();
		        will(returnValue(SOME_TP));
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
		        will(returnValue(BASE_TP_ID));
				allowing(mockAFJDatabaseCommonUtils).getTechpack(with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));
		        will(returnValue(mockMetaCollectionSets));
		        
		        allowing(mockMetaCollectionSets).getCollection_set_name();
		        will(returnValue(SOME_TP));
		        
		        allowing(mockMetaCollectionSets).getVersion_number();
		        will(returnValue(SOME_VERSION));
		        
		        allowing(mockMetaCollectionSets).getCollection_set_id();
		        will(returnValue(SOME_ID));	        
		        
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
		        will(returnValue(BASE_TP_ID));
		        
		        allowing(mockAFJMeasurementType1_1).getTpName();
		        will(returnValue(SOME_TP));
		        
		        allowing(mockAFJDatabaseCommonUtils).getMeta_collections(with(any(Long.class)), with(any(String.class)), with(any(String.class)), with(any(RockFactory.class)));		        
		        will(returnValue(new Vector<Meta_collections>(Arrays.asList(new Meta_collections[] { mockMeta_collections }))));
		        
		        allowing(mockMeta_collections).getCollection_id();
		        will(returnValue(1l));
		        
		        allowing(mockAFJMeasurementType1_1).getTypeName();
		        will(returnValue(SOME_MEASNAME));
		        
		        allowing(mockCreateTPDirCheckerSet).createForAFJ(with(any(String.class)), with(any(Long.class)));
			}			
		});

		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.createDirectoryCheckerSets(mockAFJMeasurementType1_1);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#createDirectoryCheckerDirectories(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateDirectoryCheckerDirectories() throws Exception{		
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);		
		commonUpgradeUtil.createDirectoryCheckerDirectories(SOME_TP, SOME_MEASNAME);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#popluateTransformation(com.ericsson.eniq.afj.common.AFJMeasurementType)}.
	 */
	@Test
	public void testPopluateTransformation() throws Exception{
		context.checking(new Expectations(){
			{
		        allowing(mockAFJMeasurementType1_1).getTpVersion();
		        will(returnValue(BASE_TP_ID));
		        
		        allowing(mockAFJMeasurementType1_1).getTpName();
		        will(returnValue(SOME_TP));
		        
				allowing(mockAFJDatabaseCommonUtils).getMaxOrderNumber((with(any(String.class))), with(any(RockFactory.class)));
		        will(returnValue(1L));
		        
		        allowing(mockAFJMeasurementType1_1).getTpName();
		        will(returnValue(SOME_TP));
		        
		        allowing(mockAFJMeasurementType1_1).getTypeName();
		        will(returnValue(SOME_MEASNAME));
		        
		        allowing(mockDwhrep).insertData(with(any(Object.class)));
				will(returnValue(1));		        

			}			
		});

		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		commonUpgradeUtil.popluateTransformation(mockAFJMeasurementType1_1);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.CommonUpgradeUtil#getTransformationObject(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetTransformationObject() {
		final CommonUpgradeUtil commonUpgradeUtil = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		final Transformation actualOutputObject = commonUpgradeUtil.getTransformationObject("DC_E_BSS:((28)):ALL:eniqasn1", 25, "copy", "DC_TIMEZONE", "TZONE", "");		
		final String actualOutputValue = actualOutputObject.getTransformerid();
		final String expectedOutputValue = "DC_E_BSS:((28)):ALL:eniqasn1";
		
		assertEquals(expectedOutputValue, actualOutputValue);
	}
	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.STNUpgrader#getProcessInstruction(String type)}.
	 */

	@Test		
	public void testGetProcessInstructionPEG() throws Exception{	
		final CommonUpgradeUtil objectUnderTest = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("getProcessInstruction", new Class[] {String.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {"CC"});
		final String expectedOuput = "PEG";		
		assertEquals(expectedOuput, actualOutput);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.STNUpgrader#getProcessInstruction(String type)}.
	 */
	@Test
	public void testGetProcessInstructionGuage() throws Exception{				
		final CommonUpgradeUtil objectUnderTest = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("getProcessInstruction", new Class[] {String.class});
		preProcessInput.setAccessible(true);	
		final String actualOutput = (String)preProcessInput.invoke(objectUnderTest, new Object[] {"GAUGE"});
		final String expectedOuput = "GAUGE";		
		assertEquals(expectedOuput, actualOutput);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.upgrade.STNUpgrader#getProcessInstruction(String type)}.
	 */
	@Test
	public void testGetProcessInstructionNull(){
		final CommonUpgradeUtil objectUnderTest = new CommonUpgradeUtil(mockDwhrep, mockEtlrep, mockDwh, mockDbaDwh);
		
		try{
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method preProcessInput = pcClass.getDeclaredMethod("getProcessInstruction", new Class[] {String.class});
		preProcessInput.setAccessible(true);	
		preProcessInput.invoke(objectUnderTest, new Object[] {"ABC"});
		fail("Should not reach this point.");
		}
		catch(Exception e){if(e.getClass().equals(InvocationTargetException.class)){
			final InvocationTargetException ite = (InvocationTargetException)e;
			if(ite.getTargetException() instanceof AFJException){
				final String actualMessage = ite.getTargetException().getMessage();				
				final String expected = "The counter type is invalid. Should be PEG or GUAGE.";
				assertEquals(expected, actualMessage);
			}			
			else{
				fail("Should not reach here. Not AFJException. Some other exception thrown.");
			}
		}
		else{
			fail("Should not reach here. Some other exception thrown.");
		}}
	}

}
