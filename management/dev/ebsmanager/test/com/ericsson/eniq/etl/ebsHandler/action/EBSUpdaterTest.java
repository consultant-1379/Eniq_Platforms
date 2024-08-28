package com.ericsson.eniq.etl.ebsHandler.action;


import com.ericsson.eniq.etl.ebsHandler.setCreators.stubs.CreateAggregatorSetStub;
import com.ericsson.eniq.etl.ebsHandler.setCreators.stubs.CreateLoaderSetStub;
import com.ericsson.eniq.storedprocedures.LockUserDummy;
import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.repository.cache.ActivationCache;
import com.distocraft.dc5000.etl.scheduler.SchedulerAdmin;
import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.etl.engine.system.StatusEvent;
import com.distocraft.dc5000.etl.engine.system.SetListener;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import ssc.rockfactory.RockFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Logger;
import java.nio.channels.FileChannel;
import java.rmi.ConnectException;

/**
 * A JUnit test class for the class EBSUpdateAction.
 *
 * @author epiituo
 */
public class EBSUpdaterTest {
    private final static String _createStatementMetaFile = "TableCreateStatements.sql";
    private EBSUpdaterTestSubclass sut;
    private RockFactory jUnitTestDB = null;
    private final String junitDbUrl = "jdbc:hsqldb:mem:ebs_db";
    private static final String hsqlDriver = "org.hsqldb.jdbcDriver";
		private static final Properties defaultTestProperties = new Properties();
		private static final Map<String, String> defaultTestEbsTypes = new HashMap<String, String>();
		private SetListener proxySetListener = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
			defaultTestProperties.put("retry.delay", "1000");
			defaultTestEbsTypes.put("EBSG", "EBSG");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }
	@Before
	public void setUp(){
		proxySetListener = new SetListener();
	}

    /*
      * Creates the EBSUpdateAction's test specific subclass, and all the stub
      * classes required for simulating the EBSUpdateAction's dependencies to
      * the rest of the system.
      */
    public void setUpForExecute() throws Exception {
        getTestDb();
        loadUnitDb(jUnitTestDB, "EbsParse");
        Properties propeties = new Properties();
        Logger logger = Logger.getLogger("EBSUpdaterTest");

        final String baseXmlDir = System.getProperty("user.dir") + "/ebs/";
        final File tmp = new File(baseXmlDir);
        if(!tmp.exists()){
            if(!tmp.mkdirs()){
                throw new Exception("Failed to create setup dir " + baseXmlDir);
            }
        }
        tmp.deleteOnExit();
        propeties.put("ebsDirs", "PM_E_EBSW="+baseXmlDir+",PM_E_EBSG="+baseXmlDir+",PM_E_EBSS="+baseXmlDir);

        System.setProperty("CONF_DIR", System.getProperty("user.dir"));
        final File tmpProps = new File(System.getProperty("user.dir") + "/ETLCServer.properties");
        final Properties cp = new Properties();
        cp.put("ENGINE_DB_URL", junitDbUrl);
        cp.put("ENGINE_DB_USERNAME", "SA");
        cp.put("ENGINE_DB_PASSWORD", "");
        cp.put("ENGINE_DB_DRIVERNAME", hsqlDriver);
        final FileOutputStream fos = new FileOutputStream(tmpProps, false);
        cp.store(fos, "");
        fos.close();
        tmpProps.deleteOnExit();
        StaticProperties.giveProperties(new Properties());



        final URL url = ClassLoader.getSystemResource("test.xml");
        final File from = new File(url.toURI().getRawPath());
        final File to = new File(baseXmlDir + "/testEbs.xml");
        copyFile(from, to);
        ActivationCache.initialize(jUnitTestDB);
        sut = new EBSUpdaterTestSubclass(propeties, jUnitTestDB, logger);

			final String lock_user = "CREATE ALIAS lock_user FOR \"com.ericsson.eniq.storedprocedures.LockUserDummy.lock_user\"";
			final String unlock_user = "CREATE ALIAS unlock_user FOR \"com.ericsson.eniq.storedprocedures.LockUserDummy.unlock_user\"";
			final String drop_user_connections = "CREATE ALIAS drop_user_connections FOR \"com.ericsson.eniq.storedprocedures.LockUserDummy.drop_user_connections\"";

			final Connection c = jUnitTestDB.getConnection();
			final Statement stmt = c.createStatement();
			stmt.executeUpdate(lock_user);
			stmt.executeUpdate(unlock_user);
			stmt.executeUpdate(drop_user_connections);
			c.commit();
			// dont close the connection, its used as the etlrep connection in EBSUpdater...
    }
    public static void copyFile(final File in, final File out) throws Exception {
        final FileChannel inChannel = new FileInputStream(in).getChannel();
        final FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    private RockFactory getTestDb() throws Exception {
        if (jUnitTestDB == null || jUnitTestDB.getConnection().isClosed()) {
            jUnitTestDB = new RockFactory(junitDbUrl, "SA", "", hsqlDriver, "con", true, -1);
        }
        return jUnitTestDB;
    }

	@Test
	public void testSchedulerRestartWithErrorsSuccessAfterRetry() throws Exception {
		final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
		final SchedulerAdmin mockedAdmin = context.mock(SchedulerAdmin.class);
		context.checking(new Expectations() {{
			oneOf(mockedAdmin).activate_silent();
			//noinspection ThrowableInstanceNeverThrown
			will(throwException(new ConnectException("Test Error")));
			oneOf(mockedAdmin).activate_silent();
		}});
		final EBSUpdater tInstance = new EBSUpdater(defaultTestProperties, defaultTestEbsTypes){
			@Override
			protected SchedulerAdmin getSchedulerAdmin() throws Exception {
				setListener = proxySetListener;
				return mockedAdmin;
			}
		};
		tInstance.resetScheduler();
		context.assertIsSatisfied();
		final List<StatusEvent> events = proxySetListener.getAllStatusEvents();
		boolean messageFound = false;
		for(StatusEvent se : events){
			if(se.getMessage().equals("Scheduler automatically refreshed.")){
				messageFound = true;
				break;
			}
		}
		if(!messageFound){
			Assert.fail("Restarted message not reported.");
		}
	}

	@Test
	public void testSchedulerRestartWithErrors() throws Exception {
		final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
		final SchedulerAdmin mockedAdmin = context.mock(SchedulerAdmin.class);
		context.checking(new Expectations() {{
			oneOf(mockedAdmin).activate_silent();
			//noinspection ThrowableInstanceNeverThrown
			will(throwException(new ConnectException("Test Error")));
			oneOf(mockedAdmin).activate_silent();
			//noinspection ThrowableInstanceNeverThrown
			will(throwException(new ConnectException("Test Error")));
			oneOf(mockedAdmin).activate_silent();
			//noinspection ThrowableInstanceNeverThrown
			will(throwException(new ConnectException("Test Error")));
		}});
		final EBSUpdater tInstance = new EBSUpdater(defaultTestProperties, defaultTestEbsTypes){
			@Override
			protected SchedulerAdmin getSchedulerAdmin() throws Exception {
				setListener = proxySetListener;
				return mockedAdmin;
			}
		};
		tInstance.resetScheduler();
		context.assertIsSatisfied();
		final List<StatusEvent> events = proxySetListener.getAllStatusEvents();
		boolean messageFound = false;
		for(StatusEvent se : events){
			if(se.getMessage().equals("Warning : Could not refresh Scheduler automatically, please refresh manually.")){
				messageFound = true;
				break;
			}
		}
		if(!messageFound){
			Assert.fail("Restart Manually message not reported.");
		}
	}
	@Test
	public void testSchedulerRestart() throws Exception {
		final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
		final SchedulerAdmin mockedAdmin = context.mock(SchedulerAdmin.class);
		context.checking(new Expectations() {{
			oneOf(mockedAdmin).activate_silent();
		}});
		final EBSUpdater tInstance = new EBSUpdater(defaultTestProperties, defaultTestEbsTypes){
			@Override
			protected SchedulerAdmin getSchedulerAdmin() throws Exception {
				return mockedAdmin;
			}
		};
		tInstance.resetScheduler();
		context.assertIsSatisfied();
	}

	@Test
	public void testEngineRestart() throws Exception {
		final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
		final EngineAdmin mockedAdmin = context.mock(EngineAdmin.class);
		context.checking(new Expectations() {{
			oneOf(mockedAdmin).changeProfile("Normal");
		}});
		final EBSUpdater tInstance = new EBSUpdater(defaultTestProperties, defaultTestEbsTypes){
			@Override
			protected EngineAdmin getEngineAdmin() {
				return mockedAdmin;
			}
		};
		tInstance.resetEngine();
		context.assertIsSatisfied();
	}

    /*
      * Tests the EBSUpdateAction.execute() method.
      */
    @org.junit.Test
    public void testExecute() throws Exception {
			setUpForExecute();
//        AggregationStatusCache.init(junitDbUrl, "SA", "", hsqlDriver);
			try{
				sut.execute();
			}
			catch(Exception e){
				e.printStackTrace();
			}
        

        // One instance of both CreateAggregatorSet and CreateLoaderSet should
        // have been created by the EBSUpdateAction.
        String message;

        message = "Number of CreateAggregatorSetStubs created by the EBSUpdateAction != 1";
        Assert.assertEquals(message, 1, sut.getCreatedCreateAggregatorSetStubs().length);
        Assert.assertEquals(1, sut.getCreatedCreateLoaderSetStubs().length);

        CreateAggregatorSetStub createAggregatorSetStub =
                sut.getCreatedCreateAggregatorSetStubs()[0];

        CreateLoaderSetStub createLoaderSetStub =
                sut.getCreatedCreateLoaderSetStubs()[0];

        // Correct method calls should have been made to the set creators
        String expectedMethodCallNames[] = {"removeSets", "create"};
        methodCallsEqual(expectedMethodCallNames,
                createAggregatorSetStub.getMethodCalls());
        methodCallsEqual(expectedMethodCallNames,
                createLoaderSetStub.getMethodCalls());
			
			final List<String> expected = Arrays.asList("dcbo", "dcpublic");
			check(expected, LockUserDummy.usersUnlocked, "Lock");
			check(expected, LockUserDummy.usersLocked, "Unlock");
			check(expected, LockUserDummy.usersDropped, "Drop");
    }
	private void check(final List<String> expected, final List<String> actual, final String type){
		Assert.assertEquals(type+" user count not correct", actual.size(), expected.size());
		for(String u : actual){
				Assert.assertTrue("Expected user not "+type, expected.contains(u));
			}
	}

    private void methodCallsEqual(String[] expectedMethodCallNames,
                                  String[] methodCalls) {
        Assert.assertEquals(expectedMethodCallNames.length,
                methodCalls.length);

        for (int i = 0; i < methodCalls.length; ++i) {
            String methodCallName = getMethodNameSubstring(methodCalls[i]);
            if (methodCallName != null) {
                String message = "The method call number " + i + " performed by " +
                        "EBSUpdateAction is " + methodCallName + ", when " +
                        "the expected method call was " +
                        expectedMethodCallNames[i];
                Assert.assertEquals(message, expectedMethodCallNames[i],
                        methodCallName);
            } else {
                // A closing paranthesis could not be found from the logged
                // method call.
                String message = "The logged method call: " + methodCalls[i] +
                        "does not seem to be a valid method call.";
                Assert.fail(message);
            }
        }
    }

    private String getMethodNameSubstring(String methodCall) {
        String result;

        int indexOfFirstParenthesis = methodCall.indexOf('(');
        if (indexOfFirstParenthesis != -1) {
            result = methodCall.substring(0, indexOfFirstParenthesis);
        } else {
            // A closing parenthesis character was not found from the string
            result = null;
        }

        return result;
    }
    private void loadUnitDb(final RockFactory unitdb, final String dir) throws Exception {
        try{
            final URL url = ClassLoader.getSystemClassLoader().getResource(dir);
					if(url == null){
						throw new Exception("Failed to load "+dir+", make sure its on the classpath.");
					}
            final File f = new File(url.toURI());
            loadSetup(unitdb, f.getAbsolutePath());
        } catch (SQLException e){
            throw new Exception(e.getMessage() + "\n\t" + e.getCause());
        }
    }
    private void loadSetup(final RockFactory testDB, final String baseDir) throws ClassNotFoundException, IOException, SQLException {
        final File loadFrom = new File(baseDir);
        final File[] toLoad = loadFrom.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql") && !name.equals(_createStatementMetaFile);
            }
        });
        final File createFile = new File(baseDir + "/" + _createStatementMetaFile);
        loadSqlFile(createFile, testDB);
        for (File loadFile : toLoad) {
            loadSqlFile(loadFile, testDB);
        }
    }

    private void loadSqlFile(final File sqlFile, final RockFactory testDB) throws IOException, SQLException, ClassNotFoundException {
        if (!sqlFile.exists()) {
            System.out.println(sqlFile + " doesnt exist, skipping..");
            return;
        }
//        System.out.println("Loading " + sqlFile);
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

    private void update(final String insertSQL, final RockFactory testDB) throws SQLException, ClassNotFoundException, IOException {
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

}
