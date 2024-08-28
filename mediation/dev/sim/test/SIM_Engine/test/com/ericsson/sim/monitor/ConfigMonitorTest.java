package com.ericsson.sim.monitor;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;

public class ConfigMonitorTest {
	private ConfigMonitor configMonitor;
	private RopIntervalScheduler scheduler;
//	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
//			.normalize().toString()
//			+ "/test/";

	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_Engine/test/";
	
	@Before
	public void setUp() throws Exception {
		scheduler = RopIntervalScheduler.getInstance();
		configMonitor = new ConfigMonitor();
	}

	@After
	public void tearDown() throws Exception {
		configMonitor.exit();
		SIMEnvironment.CONFIGPATH =  System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
	}

	@Test
	public void testExit() {
		SIMEnvironment.CONFIGPATH = FILE_PATH;
			
		configMonitor.startup();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
	
		}
		//configMonitor.exit();
	}
	
	@Test
	public void testStartUp() {
		SIMEnvironment.CONFIGPATH = FILE_PATH;
		assertNull(configMonitor.mt);
		configMonitor.startup();
		assertNotNull(configMonitor.mt);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
	
		}
		//configMonitor.exit();
	}
	
	@Test
	public void testGetName() {
		SIMEnvironment.CONFIGPATH = FILE_PATH;
		
		configMonitor.startup();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
	
		}
		assertEquals("com.ericsson.sim.monitor.ConfigMonitor", configMonitor.getName());
	}

}
