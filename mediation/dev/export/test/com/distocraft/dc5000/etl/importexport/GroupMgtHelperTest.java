package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.importexport.gpmgt.ImportType;
import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;


public class GroupMgtHelperTest {
	private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
  private static final String DB_URL = "jdbc:hsqldb:mem:gpmgt";
	private static RockFactory testFactory = null;
	private static final String ENGINE_DB_URL = "ENGINE_DB_URL";
	private static final String ENGINE_DB_DRIVERNAME = "ENGINE_DB_DRIVERNAME";
	private static final String ENGINE_DB_USERNAME = "ENGINE_DB_USERNAME";
	private static final String ENGINE_DB_PASSWORD = "ENGINE_DB_PASSWORD";
	private static final String SA = "SA";
	private static final String GPMGT = "gpmgt";


	@After
	public void tearDown() throws Exception {
		try {
			testFactory.getConnection().createStatement().executeUpdate("SHUTDOWN");
		} catch (SQLException e) {/**/}
	}

	@Before
	public void setUp() throws Exception {
    StaticProperties.giveProperties(new Properties());
		final Properties p = new Properties();
		p.setProperty(ENGINE_DB_URL, DB_URL);
		p.setProperty(ENGINE_DB_DRIVERNAME, TESTDB_DRIVER);
		p.setProperty(ENGINE_DB_USERNAME, SA);
		p.setProperty(ENGINE_DB_PASSWORD, "");
		GroupMgtHelper.setEtlcProperties(p);
		final Properties gpmgt = new Properties();
		gpmgt.setProperty(GroupMgtHelper.GPMGT_PROP_SRC + ".events.tpname", "EVENT_E_SGEH");
		GroupMgtHelper.setGpmgtProperties(gpmgt);
		testFactory = new RockFactory(DB_URL, SA, "", TESTDB_DRIVER, "", false);
		DatabaseTestUtils.loadSetup(testFactory, GPMGT);
	}
	@Test
	public void testGetProperties() throws IOException {
		final Properties p = new Properties();
		final String url = "url";
		final String driver = "driver";
		final String username = "username";
		final String passwd = "password";
		p.setProperty(ENGINE_DB_URL, url);
		p.setProperty(ENGINE_DB_DRIVERNAME, driver);
		p.setProperty(ENGINE_DB_USERNAME, username);
		p.setProperty(ENGINE_DB_PASSWORD, passwd);
		final File f = new File("p.properties");
		f.deleteOnExit();

		final FileWriter fw = new FileWriter(f);
		p.store(fw, "");
		fw.close();

		GroupMgtHelper.setPropertiesFile(f.getAbsolutePath());
		GroupMgtHelper.setEtlcProperties(null);
		final GroupMgtHelper h = GroupMgtHelper.getInstance();
		final Properties rProps = h.getEtlcProperties();
		assertEquals(url, rProps.getProperty(ENGINE_DB_URL, ""));
		assertEquals(driver, rProps.getProperty(ENGINE_DB_DRIVERNAME, ""));
		assertEquals(username, rProps.getProperty(ENGINE_DB_USERNAME, ""));
		assertEquals(passwd, rProps.getProperty(ENGINE_DB_PASSWORD, ""));
		assertNotNull(p);
	}
	@Test
	public void testGetInstance(){
		final GroupMgtHelper helper1 = GroupMgtHelper.getInstance();
		final GroupMgtHelper helper2 = GroupMgtHelper.getInstance();
		assertEquals(helper1, helper2);
	}


	@Test
	public void testGetArg(){
		assertNull(GroupMgtHelper.getArgValue(new String[0], "foo"));
		final String[] args = {"-f", "foption", "-t", "tvalue"};
		assertNull(GroupMgtHelper.getArgValue(args, "not_here"));
		assertEquals("foption", GroupMgtHelper.getArgValue(args, "-f"));
		assertEquals("tvalue", GroupMgtHelper.getArgValue(args, "-t"));
		assertNull( GroupMgtHelper.getArgValue(new String[]{"-f"}, "-f"));
	}
	@Test
	public void testUsage() throws Exception {
		assertEquals(ExitCodes.EXIT_NO_FILE, GroupMgtHelper.checkFileArgs(null));
		assertEquals(ExitCodes.EXIT_INVALID_FILE, GroupMgtHelper.checkFileArgs("fFile"));
		final File tmp = new File(System.getProperty("java.io.tmpdir"), "t.txt");
		tmp.deleteOnExit();
		//noinspection ResultOfMethodCallIgnored
		tmp.createNewFile();
		assertEquals(ExitCodes.EXIT_OK, GroupMgtHelper.checkFileArgs(tmp.getAbsolutePath()));
		assertEquals(ExitCodes.EXIT_INVALID_VER, GroupMgtHelper.checkVersionArgs("dc_e_noversion"));
		assertEquals(ExitCodes.EXIT_OK, GroupMgtHelper.checkVersionArgs("dc_e_abc:((1))"));

	}

	@Test
	public void testGetCurrentTime(){
		assertEquals("", GroupMgtHelper.getCurrentTime());
	}
	@Test
	public void testGetMaxTime(){
		assertEquals("", GroupMgtHelper.getMaxTime());
	}
	@Test
	public void testGetImportType(){
		assertEquals(ImportType.Add, GroupMgtHelper.getImportType(new String[]{"ff", "-add", "-t"}));
		assertEquals(ImportType.Delete, GroupMgtHelper.getImportType(new String[]{"-delete", "-f", "ffff"}));
		assertNull(GroupMgtHelper.getImportType(new String[]{"-g", "-f", "ffff"}));
	}
	@Test
	public void testGetExitMessage(){
		for(ExitCodes eCodes : ExitCodes.values()){
			assertNotNull(eCodes.getExistMessage());
		}
	}
}
