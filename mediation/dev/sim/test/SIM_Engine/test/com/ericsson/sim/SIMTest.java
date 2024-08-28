package com.ericsson.sim;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.properties.RuntimeProperties;


public class SIMTest {
	private SIM sim;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.LOGGERCONFIG = FILE_PATH + "loger.properties";
		SIMEnvironment.LOGPATH = FILE_PATH + "sim.log";
		SIMEnvironment.RUNPROPSCONFIG = FILE_PATH + "properties.simc";
		SIMEnvironment.PROTOCOLCONFIG = FILE_PATH + "protocols.simc";
		SIMEnvironment.TOPOLOGYEXTRACONFIG = FILE_PATH + "topology.simc";
		SIMEnvironment.ROPINTERVALCONFIG = FILE_PATH + "intervals.simc";

		sim = new SIM();
	}

	@After
	public void tearDown() throws Exception {

		SIMEnvironment.LOGGERCONFIG = System.getProperty("LoggerConfig",
				SIMEnvironment.CONFIGPATH + "log.properties");
		SIMEnvironment.LOGPATH = System.getProperty("LogPath",
				"/eniq/log/sw_log/sim");
		SIMEnvironment.RUNPROPSCONFIG = System.getProperty("RunPropsConfig",
				SIMEnvironment.CONFIGPATH + "properties.simc");
		SIMEnvironment.PROTOCOLCONFIG = System.getProperty("ProtocolConfig",
				SIMEnvironment.CONFIGPATH + "protocols.simc");
		SIMEnvironment.TOPOLOGYEXTRACONFIG = System.getProperty(
				"TopologyExtraConfig", SIMEnvironment.CONFIGPATH
						+ "/topology.simc");
		SIMEnvironment.ROPINTERVALCONFIG = System.getProperty(
				"RopIntervalConfig", SIMEnvironment.CONFIGPATH
						+ "intervals.simc");
	}
	
	// Test must be veerified in feature test as there
	// is no guarantee which eniq server will be online

//	@Test
//	public void testStartUp() {
//		RuntimeProperties.getInstance().addProperty("LicenseSentinel", null);
//
//		// Cant gaurantee any eniq server that will be there for all
//		// test scenarios. Code validated in automated feature test
//		// assertTrue(sim.validateLicense());
//
//		System.setProperty("ENIQEngineStatus", "online");
//		System.setProperty("ENIQMemoryStatus", "notMemoryFull");
//
//		sim.startup();
//
//	}

	@Test
	public void testValidateLicense() {
		RuntimeProperties.getInstance().addProperty("LicenseSentinel", null);
		// Cant gaurantee any eniq server that will be there for all
		// test scenarios. Code validated in automated feature test
		// assertTrue(sim.validateLicense());

	}

	@Test
	public void testValidateLicenseOnInvalidHost() {
		RuntimeProperties.getInstance().addProperty("LicenseSentinel",
				"does.not.exist.here");
		assertFalse(sim.validateLicense());
	}

	@Test
	public void testSystemStatus() {

		SIMEnvironment.CONFIGPATH = "/eniq/sw/conf/sim";

		System.setProperty("ENIQEngineStatus", "failed");
		System.setProperty("ENIQMemoryStatus", "failed");
		assertEquals("EngineStatusConditionNotMet", false,
				sim.checkSystemStatus());

		System.setProperty("ENIQEngineStatus", "online");
		System.setProperty("ENIQMemoryStatus", "MEMORYFULL");
		assertEquals("ENIQMemoryStatusCondtionNotMet", false,
				sim.checkSystemStatus());

		System.setProperty("ENIQEngineStatus", "failed");
		System.setProperty("ENIQMemoryStatus", "MEMORYFULL");
		assertEquals("BothConditionNotMet", false, sim.checkSystemStatus());

		System.setProperty("ENIQEngineStatus", "online");
		System.setProperty("ENIQMemoryStatus", "notMemoryFull");
		assertEquals("online and memory not full", true,
				sim.checkSystemStatus());

	}

	@Test
	public void testInitLogging() {

		sim.initLogging();
		assertEquals(org.apache.log4j.Level.OFF, sim.getLog().getLevel());

	}

}
