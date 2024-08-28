package com.ericsson.sim.constants;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SIMEnvironmentTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstantsDefaultReturned() {
		
		
		assertEquals("/eniq/data/pmdata_sim", SIMEnvironment.RUNTIMEPATH);
		assertEquals("/eniq/sw/conf/sim", SIMEnvironment.CONFIGPATH);
			
		assertEquals(SIMEnvironment.CONFIGPATH + "/mibs", SIMEnvironment.MIBSPATH);
		 
		assertEquals(SIMEnvironment.RUNTIMEPATH + "/optimizer", SIMEnvironment.OPTPATH);
		assertEquals("/eniq/log/sw_log/sim", SIMEnvironment.LOGPATH);
		assertEquals(SIMEnvironment.CONFIGPATH + "/pers", SIMEnvironment.PERSPATH);
		assertEquals(SIMEnvironment.RUNTIMEPATH + "/parsing", SIMEnvironment.PARSINGPATH);
		
		assertEquals(SIMEnvironment.CONFIGPATH + "/topology.xml", SIMEnvironment.TOPOLOGYCONFIG);
		
		assertEquals(SIMEnvironment.CONFIGPATH + "/topology.simc", SIMEnvironment.TOPOLOGYEXTRACONFIG);
		assertEquals(SIMEnvironment.CONFIGPATH + "/intervals.simc", SIMEnvironment.ROPINTERVALCONFIG);
		assertEquals(SIMEnvironment.CONFIGPATH + "/protocols.simc", SIMEnvironment.PROTOCOLCONFIG);
		assertEquals(SIMEnvironment.CONFIGPATH + "/properties.simc", SIMEnvironment.RUNPROPSCONFIG);
		assertEquals(SIMEnvironment.CONFIGPATH + "/log.properties", SIMEnvironment.LOGGERCONFIG);
		assertEquals(SIMEnvironment.CONFIGPATH + "/maverickLicense.txt", SIMEnvironment.MAVERICKLICENSE);
		assertEquals(SIMEnvironment.CONFIGPATH + "/optimizerRules.txt", SIMEnvironment.OPTIMIZERRULES);
		
		
		assertEquals("/eniq/home/dcuser/.ssh/id_rsa", SIMEnvironment.SSHKEYSPATH);
		
	}

}
