package com.ericsson.eniq.techpacksdk.view.createDocuments;

import org.jdesktop.application.SingleFrameApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;

import junit.framework.TestCase;

public class CreateTechPackDescriptionTaskTest extends TestCase {
	private RockFactory dwhrep = null;
	private final String tpName = "DC_E_BSS";
	private Versioning tpVersion = null;
	private CreateTechPackDescriptionTask testInstance = null;
	private String outputDir = System.getProperty("java.io.tmpdir");

	public CreateTechPackDescriptionTaskTest() {
		super("CreateTechPackDescriptionTaskTest");
	}

	private SingleFrameApplication testApp = new SingleFrameApplication() {
		
		@Override
		protected void startup() {
			// TODO Auto-generated method stub
			
		}
	};
	
//	private final SingleFrameApplication sfa = new SingleFrameApplication() {
//		protected void startup() {
//		}
//	};

	@Test
	public void testGetBusyHourData(){
		final Map<String, List<Map<String, String>>> bData = testInstance.getBusyHourData();
		assertNotNull(bData);
		assertEquals(3, bData.size());
		final String pLevel = "DC_E_BSS_BSCBH";
		final List<Map<String, String>> pData = bData.get(pLevel);
		assertNotNull(pData);
		assertEquals(10, pData.size());

		//List is ordered, PP[0-4] , CP[0-4]
		final Map<String, String> hData = pData.get(0);
		checkBhData(hData, pLevel, "PP0", "Timelimited", tpVersion.getVersionid());
		final Map<String, String> cData = pData.get(9);
		checkBhData(cData, pLevel, "CP4", "Timelimited", tpVersion.getVersionid());
	}
	private void checkBhData(final Map<String, String> bhData, final String expectedBhLevel, final String expectedType,
													 final String expectedAgg, final String expectedVersionId){
		checkContents(bhData, "bhLevel", expectedBhLevel);
		checkContents(bhData, "bhType", expectedType);
		checkContents(bhData, "targetVersionId", expectedVersionId);
		checkContents(bhData, "source", "*");
		checkContents(bhData, "criteria", "*");
		checkContents(bhData, "description", "*");
		checkContents(bhData, "whereCondition", "*");
		checkContents(bhData, "aggregationType", expectedAgg);
		checkContents(bhData, "bhMappings", "*");
		//checkContents(bhData, "grouping", "*");
		checkContents(bhData, "rankKeys", "*");
		checkContents(bhData, "versionId", expectedVersionId);
	}
	private void checkContents(final Map<String, String> map, final String key, final String eValue){
		assertTrue("Expected key '"+key+"' not found", map.containsKey(key));
		if(!"*".equals(eValue)){
			assertEquals("Expected value not correct", eValue, map.get(key));
		}
	}

	@Test
	public void testGetSqlInterfaceData(){
		testInstance.getFactTableData(); // populates measurementTypes
		testInstance.getDimensionTableData(); // poulates referenceTabels
		final Map<String, List<Map<String, String>>> sIData = testInstance.getSqlInterfaceData();
		assertNotNull(sIData);
		assertEquals(53, sIData.size());
		final String type = tpVersion.getTechpack_name() + "_TG_RAW";
		final List<Map<String, String>> data = sIData.get(type);
		assertNotNull(data);
		for(Map<String, String> m : data){
			assertEquals(2, m.size());
			assertTrue(m.containsKey("name"));
			assertTrue(m.containsKey("type"));
		}
	}
	@Test
	public void testGetInterfaceData(){
		final Map<String, Map<String, Object>> iData = testInstance.getInterfaceData();
		assertEquals(1, iData.size());
		final String iName = "INTF_"+tpVersion.getTechpack_name()+"_APG";
		final Map<String, Object> data = iData.get(iName);
		assertNotNull(data);
		assertTrue(data.containsKey("name"));
		assertEquals(iName+"_measurement" ,data.get("name"));
		assertTrue(data.containsKey("type"));
		assertEquals("measurement" ,data.get("type"));
	}
	@Test
	public void testGetDimensionTableData(){
		final Map<String, Map<String, Object>> dData = testInstance.getDimensionTableData();
		assertEquals(4, dData.size());
		final String type = "DIM_E_BSS_BSCBH_BHTYPE";
		final Map<String, Object> rData = dData.get(type);
		assertNotNull(rData);
		assertEquals(5, rData.size());
		assertTrue(rData.containsKey("dimensionTable"));
		assertTrue(rData.containsKey("typeID"));
		assertEquals(tpVersion.getVersionid()+":"+type, rData.get("typeID"));
		assertTrue(rData.containsKey("updateMethod"));
		assertTrue(rData.containsKey("type"));
		assertTrue(rData.containsKey(type+"_columns"));
	}

	@Test
	public void testGetFactTableData() throws RockException, SQLException {
		final Map<String, Map<String, Object>> results = testInstance.getFactTableData();
		assertNotNull(results);
		// Doesnt include the BH types...
		Assert.assertEquals(21, results.size());
		final Map<String, Measurementtype> dbTypes = getTypes();
		for(String mt : results.keySet()){
			assertTrue("Generated Doc type "+mt+" is not correct", dbTypes.containsKey(mt));
		}
		// Only going to check ont Measurement type to see if its correct, if it is, presume the rest are?


		final String mType = "DC_E_BSS_TG";
		final Map<String, Object> toCheck = results.get(mType);
		assertNotNull(toCheck);
		assertEquals(mType, toCheck.get("factTable"));
		assertEquals(tpVersion.getVersionid()+":" + mType, toCheck.get("typeID"));
		assertEquals("No", toCheck.get("OneMinuteAggregation"));
		assertEquals("No", toCheck.get("FifteenMinuteAggregation"));
		assertEquals("No", toCheck.get("totalAggregation"));	
		assertEquals("medium", toCheck.get("size"));
		assertEquals("No", toCheck.get("elementBusyHourSupport"));
		assertEquals("No", toCheck.get("deltaCalculation"));
		assertEquals("None", toCheck.get("objectBusyHourSupport"));
		@SuppressWarnings({"unchecked"}) final List<Map<String, String>> keys =
			 (List<Map<String, String>>)toCheck.get(mType+"_keys");
		assertNotNull(keys);
		for(Map<String, String> key : keys){
			assertTrue(key.containsKey("name"));
			assertTrue(key.containsKey("dataType"));
			assertTrue(key.containsKey("duplicateConstraint"));
		}
		@SuppressWarnings({"unchecked"}) final List<Map<String, String>> counters =
			 (List<Map<String, String>>)toCheck.get(mType+"_counters");
		assertNotNull(counters);
		for(Map<String, String> c : counters){
			assertTrue(c.containsKey("name"));
			assertTrue(c.containsKey("dataType"));
			assertTrue(c.containsKey("timeAggregation"));
			assertTrue(c.containsKey("groupAggregation"));
			assertTrue(c.containsKey("type"));
		}
	}
	private Map<String, Measurementtype> getTypes() throws RockException, SQLException {
		final Measurementtype where = new Measurementtype(dwhrep);
		where.setVersionid(tpVersion.getVersionid());
		final MeasurementtypeFactory f = new MeasurementtypeFactory(dwhrep, where);
		final List<Measurementtype> types = f.get();
		final Map<String, Measurementtype> map = new HashMap<String, Measurementtype>(types.size());
		for(Measurementtype mt : types){
			map.put(mt.getTypename(), mt);
		}
		return map;
	}

	@Test
	public void testGetIntroductionStrings() throws Exception {
		final Map<String, Object> results = testInstance.getIntroductionStrings();
		Assert.assertTrue("productNumberAndRelease not found", results.containsKey("productNumberAndRelease"));
		Assert.assertTrue("productNumber not found", results.containsKey("productNumber"));
		Assert.assertTrue("revision not found", results.containsKey("revision"));
		Assert.assertTrue("releases not found", results.containsKey("releases"));
		Assert.assertTrue("description not found", results.containsKey("description"));
		Assert.assertTrue("month not found", results.containsKey("month"));
		Assert.assertTrue("year not found", results.containsKey("year"));
		Assert.assertTrue("versioning not found", results.containsKey("versioning"));
		Assert.assertTrue("day not found", results.containsKey("day"));
		Assert.assertTrue("version not found", results.containsKey("version"));
		Assert.assertEquals("37", results.get("version"));
		Assert.assertEquals(tpVersion, results.get("versioning"));
	}

	private static RockFactory getDwhrep() throws RockException, SQLException {
		final String junitDbUrl = "jdbc:hsqldb:mem:tpide_ctpd";
		final String hsqlDriver = "org.hsqldb.jdbcDriver";
		return new RockFactory(junitDbUrl, "SA", "", hsqlDriver, "someComm-etl", false);
	}
	private static Versioning getVersioning(final String tpName, final RockFactory dwhrep) throws Exception {
		final Versioning where = new Versioning(dwhrep);
		where.setTechpack_name(tpName);
		final VersioningFactory vf = new VersioningFactory(dwhrep, where, "ORDER BY VERSIONID");
		final List<Versioning> vrsns = vf.get();
		return vrsns.get(0);
	}

	private void loadSetup(final RockFactory testDB, final String baseDir) throws ClassNotFoundException,
		 IOException, SQLException, URISyntaxException {
		final String toGet = "setupSQL/"+baseDir;
		final URL url = ClassLoader.getSystemResource(toGet);
		if(url == null){
			throw new FileNotFoundException(toGet);
		}
		final File loadFrom = new File(url.toURI());
		final String _createStatementMetaFile = "TableCreateStatements.sql";
		final File[] toLoad = loadFrom.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".sql") && !name.equals(_createStatementMetaFile);
			}
		});
		final File createFile = new File(loadFrom, _createStatementMetaFile);
		loadSqlFile(createFile, testDB);
		for (File loadFile : toLoad) {
			loadSqlFile(loadFile, testDB);
		}
	}

	private void loadSqlFile(final File sqlFile, final RockFactory testDB) throws IOException,
		 SQLException, ClassNotFoundException {
		if (!sqlFile.exists()) {
			System.out.println(sqlFile + " doesnt exist, skipping..");
			return;
		}
		BufferedReader br = new BufferedReader(new FileReader(sqlFile));
		String line;
		int lineCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				lineCount++;
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				while (!line.endsWith(";")) {
					final String tmp = br.readLine();
					if (tmp != null) {
						line += "\r\n";
						line += tmp;
					} else {
						break;
					}
				}
				update(line, testDB);
			}
			testDB.commit();
		} catch (SQLException e) {
			throw new SQLException("Error executing on line [" + lineCount + "] of " + sqlFile, e);
		} finally {
			br.close();
		}
	}

	private void update(final String insertSQL, final RockFactory testDB) throws SQLException,
		 ClassNotFoundException, IOException {
		final Statement s = testDB.getConnection().createStatement();
		try {
			s.executeUpdate(insertSQL);
		} catch (SQLException e) {
			if (e.getSQLState().equals("S0004")) {
				System.out.println("Views not supported yet: " + e.getMessage());
			} else if (e.getSQLState().equals("S0001") || e.getSQLState().equals("42504")) {
				//ignore, table already exists.......
			} else {
				throw e;
			}
		}
	}

	@Override @Before
	protected void setUp() throws Exception {
		dwhrep = getDwhrep();
		loadSetup(dwhrep, "tpDocTables");
		tpVersion = getVersioning(tpName, dwhrep);
		final SingleFrameApplication app1 = new TestApplication() ;
        String [] arrArgs = {} ;
        TestApplication.launch(TestApplication.class, arrArgs);
        final SingleFrameApplication inst = new TestApplication();
        Thread.sleep(5000); //Giving the time to initialise TestApplication. Don't remove this.
		testInstance = new CreateTechPackDescriptionTask(outputDir, tpVersion, dwhrep, (SingleFrameApplication)TestApplication.getInstance(), Logger.getAnonymousLogger()){
			@Override
			protected Map<String, Object> getIntroductionStrings() {
				return super.getIntroductionStrings();
			}

			@Override
			protected Map<String, Map<String, Object>> getFactTableData() {
				return super.getFactTableData();
			}

			@Override
			protected Map<String, Map<String, Object>> getDimensionTableData() {
				return super.getDimensionTableData();
			}

			@Override
			protected Map<String, Map<String, Object>> getInterfaceData() {
				return super.getInterfaceData();
			}

			@Override
			protected Map<String, List<Map<String, String>>> getSqlInterfaceData() {
				return super.getSqlInterfaceData();
			}

			@Override
			protected Map<String, List<Map<String, String>>> getBusyHourData() {
				return super.getBusyHourData();
			}
		};
	}

	@Override @After
	protected void tearDown() throws Exception {
		if (dwhrep != null) {
			try {
				final Statement stmt = dwhrep.getConnection().createStatement();
				stmt.executeUpdate("SHUTDOWN");
				stmt.close();
			} catch (Throwable t) {
				// 
			} finally {
				dwhrep = null;
			}
		}
	}
}

class TestApplication extends SingleFrameApplication {
    

    @Override
    protected void startup() {
//        final Properties props = new Properties();
//        props.put("dwhm.debug", "true");
//        //Setting dir for Velocity templates             
//    	final String currentLocation = System.getProperty("user.home");        
//    	if(!currentLocation.endsWith("ant_common")){        	
//    		props.put("dwhm.templatePath", ".\\jar\\5.2\\"); // Gets tests running on laptop        
//    	}
//    	try {
//			StaticProperties.giveProperties(props);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Velocity.setProperty("resource.loader", "class");
//        Velocity.setProperty("class.resource.loader.class",
//            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        Velocity
//            .setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//        Velocity.setProperty("file.resource.loader.path", StaticProperties.getProperty("dwhm.templatePath",
//            "/dc/dc5000/conf/dwhm_templates"));
//        Velocity.setProperty("file.resource.loader.cache", "true");
//        Velocity.setProperty("file.resource.loader.modificationCheckInterval", "60");
//        try {
//			Velocity.init();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println(" Velocity Initialized");
    }
};