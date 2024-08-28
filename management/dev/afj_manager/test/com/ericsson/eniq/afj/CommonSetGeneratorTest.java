package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;

public class CommonSetGeneratorTest {

	private static CommonSetGenerator objectUnderTest;
	
	private static File ETLCServerProperties;	
	
	private static Connection connection;
	
	private static Statement statement;

	private static File AFJManagerProperties;
	
	private static File StaticProperties;	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* Create the needed tables. */
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		statement = connection.createStatement();
		
		statement.execute("CREATE TABLE META_DATABASES (USERNAME VARCHAR(180), VERSION_NUMBER VARCHAR(180), "
				+ "TYPE_NAME VARCHAR(180), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(180), "
				+ "CONNECTION_STRING VARCHAR(180), PASSWORD VARCHAR(180), DESCRIPTION VARCHAR(180), "
				+ "DRIVER_NAME VARCHAR(180), DB_LINK_NAME VARCHAR(180))");
		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'etlrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
				+ "('sa', 'v1.2', 'USER', 1, 'dwh', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
				+ "'org.hsqldb.jdbcDriver', 'db')");
		
		// Create meta_collection_sets entries
		statement.execute("CREATE TABLE META_COLLECTION_SETS (COLLECTION_SET_ID VARCHAR(180),COLLECTION_SET_NAME VARCHAR(180), "
                + "DESCRIPTION VARCHAR(180), VERSION_NUMBER VARCHAR(180), ENABLED_FLAG VARCHAR(180), TYPE VARCHAR(180))");		
		statement.execute("insert into META_COLLECTION_SETS (collection_set_id,collection_set_name,version_number) values (3,'DC_E_STN','((2))')");
		
		statement.execute("CREATE TABLE Meta_schedulings ( VERSION_NUMBER VARCHAR(180)  ,ID BIGINT  ,EXECUTION_TYPE VARCHAR(180) ,OS_COMMAND VARCHAR(180) ,SCHEDULING_MONTH BIGINT  ,SCHEDULING_DAY BIGINT  ,SCHEDULING_HOUR BIGINT  ,SCHEDULING_MIN BIGINT  ,COLLECTION_SET_ID BIGINT  ,COLLECTION_ID BIGINT  ,MON_FLAG VARCHAR(180) ,TUE_FLAG VARCHAR(180) ,WED_FLAG VARCHAR(180) ,THU_FLAG VARCHAR(180) ,FRI_FLAG VARCHAR(180) ,SAT_FLAG VARCHAR(180) ,SUN_FLAG VARCHAR(180) ,STATUS VARCHAR(180) ,LAST_EXECUTION_TIME TIMESTAMP  ,INTERVAL_HOUR BIGINT  ,INTERVAL_MIN BIGINT  ,NAME VARCHAR(180) ,HOLD_FLAG VARCHAR(180) ,PRIORITY BIGINT  ,SCHEDULING_YEAR BIGINT  ,TRIGGER_COMMAND VARCHAR(180) ,LAST_EXEC_TIME_MS BIGINT )");
		
		statement.execute("CREATE TABLE Meta_collections ( COLLECTION_ID BIGINT  ,COLLECTION_NAME VARCHAR(180) ,COLLECTION VARCHAR(180) ,MAIL_ERROR_ADDR VARCHAR(180) ,MAIL_FAIL_ADDR VARCHAR(180) ,MAIL_BUG_ADDR VARCHAR(180) ,MAX_ERRORS BIGINT  ,MAX_FK_ERRORS BIGINT  ,MAX_COL_LIMIT_ERRORS BIGINT  ,CHECK_FK_ERROR_FLAG VARCHAR(180) ,CHECK_COL_LIMITS_FLAG VARCHAR(180) ,LAST_TRANSFER_DATE TIMESTAMP  ,VERSION_NUMBER VARCHAR(180) ,COLLECTION_SET_ID BIGINT  ,USE_BATCH_ID VARCHAR(180) ,PRIORITY BIGINT  ,QUEUE_TIME_LIMIT BIGINT  ,ENABLED_FLAG VARCHAR(180) ,SETTYPE VARCHAR(180) ,FOLDABLE_FLAG VARCHAR(180) ,MEASTYPE VARCHAR(180) ,HOLD_FLAG VARCHAR(180) ,SCHEDULING_INFO VARCHAR(180))");
		statement.execute("insert into Meta_collections(COLLECTION_ID, COLLECTION_NAME, VERSION_NUMBER, COLLECTION_SET_ID) values(87,'Loader_DC_E_STN_E1INTERFACE','((2))',3)");
		
		statement.execute("CREATE TABLE Meta_transfer_actions ( VERSION_NUMBER VARCHAR(180)  ,TRANSFER_ACTION_ID BIGINT  ,COLLECTION_ID BIGINT  ,COLLECTION_SET_ID BIGINT  ,ACTION_TYPE VARCHAR(180) ,TRANSFER_ACTION_NAME VARCHAR(180) ,ORDER_BY_NO BIGINT  ,DESCRIPTION VARCHAR(180) ,WHERE_CLAUSE_01 VARCHAR(180) ,ACTION_CONTENTS_01 VARCHAR(180) ,ENABLED_FLAG VARCHAR(180) ,CONNECTION_ID BIGINT  ,WHERE_CLAUSE_02 VARCHAR(180) ,WHERE_CLAUSE_03 VARCHAR(180) ,ACTION_CONTENTS_02 VARCHAR(180) ,ACTION_CONTENTS_03 VARCHAR(180))");
		statement.execute("insert into meta_transfer_actions(COLLECTION_SET_ID,ACTION_TYPE,ACTION_CONTENTS_01,ACTION_CONTENTS_02,ACTION_CONTENTS_03) values(10,'parse','addVendorIDTo=BSC,BSCAMSG','','')");
		statement.execute("insert into meta_transfer_actions(COLLECTION_SET_ID,TRANSFER_ACTION_NAME,COLLECTION_ID,TRANSFER_ACTION_ID,VERSION_NUMBER,ACTION_CONTENTS_01,ACTION_CONTENTS_02,ACTION_CONTENTS_03) values(3,'Loader_DC_E_STN_E1INTERFACE',87,469,'((2))','LOAD TABLE $TABLE  (\r\nOSS_ID   , SN   ) \r\nFROM $FILENAMES \r\n$LOADERPARAMETERS\r\n;','','')");
		
		statement.execute("insert into META_COLLECTION_SETS (collection_set_id,collection_set_name,DESCRIPTION,version_number,ENABLED_FLAG,TYPE) values (10,'INTF_DC_E_BSS_APG','Interface INTF_DC_E_BSS_APG by ConfigTool b409','((110))','N','Interface')");
		
		statement.execute("insert into meta_schedulings(version_number,id,collection_set_id,name) values ('((110))',369,10,'TriggerAdapter_INTF_DC_E_BSS_APG_eniqasn1')");
		
		statement.execute("CREATE TABLE Measurementcolumn ( MTABLEID VARCHAR(180)  ,DATANAME VARCHAR(180) ,COLNUMBER BIGINT  ,DATATYPE VARCHAR(180) ,DATASIZE INTEGER  ,DATASCALE INTEGER  ,UNIQUEVALUE BIGINT  ,NULLABLE INTEGER  ,INDEXES VARCHAR(180) ,DESCRIPTION VARCHAR(180) ,DATAID VARCHAR(180) ,RELEASEID VARCHAR(180) ,UNIQUEKEY INTEGER  ,INCLUDESQL INTEGER  ,COLTYPE VARCHAR(180) ,FOLLOWJOHN INTEGER )");
		statement.execute("insert into Measurementcolumn values('DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW','abc',26,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','ifInErrors','DC_E_STN:((B222))',0,1,'COUNTER',NULL)");
		statement.execute("insert into Measurementcolumn values('DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW','def',27,'numeric',20,0,255,1,'','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','ifInErrors','DC_E_STN:((B222))',0,1,'COUNTER',NULL)");
		
		statement.execute("CREATE TABLE Measurementtype ( TYPEID VARCHAR(180)  ,TYPECLASSID VARCHAR(180) ,TYPENAME VARCHAR(180) ,VENDORID VARCHAR(180) ,FOLDERNAME VARCHAR(180) ,DESCRIPTION VARCHAR(180) ,STATUS BIGINT  ,VERSIONID VARCHAR(180) ,OBJECTID VARCHAR(180) ,OBJECTNAME VARCHAR(180) ,OBJECTVERSION INTEGER  ,OBJECTTYPE VARCHAR(180) ,JOINABLE VARCHAR(180) ,SIZING VARCHAR(180) ,TOTALAGG INTEGER  ,ELEMENTBHSUPPORT INTEGER  ,RANKINGTABLE INTEGER  ,DELTACALCSUPPORT INTEGER  ,PLAINTABLE INTEGER  ,UNIVERSEEXTENSION VARCHAR(180) ,VECTORSUPPORT INTEGER  ,DATAFORMATSUPPORT INTEGER, FOLLOWJOHN INTEGER )");
		statement.execute("insert into Measurementtype(TYPEID,JOINABLE,TYPENAME) values ('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','','DC_E_STN_E1INTERFACE')");
		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			pw.write("afj.template.dir=template\n");			
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}    
		
		/* Create property file for database connection details */
		ETLCServerProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		ETLCServerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(ETLCServerProperties));
			pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
			pw.write("ENGINE_DB_USERNAME = sa\n");
			pw.write("ENGINE_DB_PASSWORD = \n");
			pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		/* Create property file for database connection details */
		StaticProperties = new File(System.getProperty("user.dir"), "static.properties");
		System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		StaticProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(StaticProperties));
			pw.write("sybaseiq.option.public.DML_Options5=0");
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
    try {
      final Statement stmt = connection.createStatement();
      try {
        stmt.executeUpdate("SHUTDOWN");
      } finally {
        stmt.close();
      }
    } finally {
      connection.close();
    }
	}

	@Before
	public void setUp() throws Exception {
		final String templateDir =PropertiesUtility.getProperty("afj.template.dir");
		final String name = "DC_E_STN";
		final String version = "((2))";
//		final String versionid = "((2))";
		final RockFactory dwhrepRock = AFJDatabaseHandler.getInstance().getDwhrep();
		final RockFactory rock = AFJDatabaseHandler.getInstance().getEtlrep(); 
		final int techPackID = 3;
		final String techPackName = "DC_E_STN";
		final boolean schedulings = true;
			
		objectUnderTest = new CommonSetGenerator(templateDir, name, version, dwhrepRock, rock, techPackID, techPackName, schedulings);
	}

	@After
	public void tearDown() throws Exception {
		objectUnderTest = null;
	}

	@Test
	@Ignore
	public void testUpdateLoaderAction() throws Exception{
		objectUnderTest.updateLoaderAction("DC_E_STN:((2)):DC_E_STN_E1INTERFACE");
		ResultSet rs = null;
		String actual = null;
		final String expected =  "LOAD TABLE $TABLE  (" +
		"\r\nabc   , def   ) "+		
		"\r\nFROM $FILENAMES "+ 
		"\r\n$LOADERPARAMETERS"+
		"\r\n;";
		try{
		rs = statement.executeQuery("select ACTION_CONTENTS_01 from meta_transfer_actions where transfer_action_id=469");
		
		if(rs.next()){
			actual = rs.getString(1);
		}		
		System.out.println(actual);
		}
		catch(Exception e){
			throw e;
		}
		finally{
			rs.close();	
		}		
		assertEquals(expected, actual);
	}


	@Test
	@Ignore
	public void testCreateSql() throws Exception{
		
		final String mTableId = "DC_E_STN:((2)):DC_E_STN_E1INTERFACE:RAW";
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method CreateSql = pcClass.getDeclaredMethod("CreateSql", new Class[] {String.class});
		CreateSql.setAccessible(true);	
		final String actual = (String)CreateSql.invoke(objectUnderTest, new Object[] {mTableId});	
		final String expected =  "LOAD TABLE $TABLE  (" +
		"\r\nabc   , def   ) "+		
		"\r\nFROM $FILENAMES "+ 
		"\r\n$LOADERPARAMETERS"+
		"\r\n;";
		assertEquals(expected, actual);
	}
	
	@Test
	@Ignore
	public void testMerge() throws Exception{
		final VelocityContext context = new VelocityContext();
		final Vector<String> vec = new Vector();
		vec.add("abc");
		vec.add("def");
		context.put("measurementColumn", vec);		
		
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method merge = pcClass.getDeclaredMethod("merge", new Class[] {VelocityContext.class});
		merge.setAccessible(true);	
		final String actual = (String)merge.invoke(objectUnderTest, new Object[] {context});		
		final String expected = "LOAD TABLE $TABLE  (" +
		"\r\nabc   , def   ) "+		
		"\r\nFROM $FILENAMES "+ 
		"\r\n$LOADERPARAMETERS"+
		"\r\n;";
		System.out.println(expected);		
		assertEquals(expected, actual);		
	}

}
