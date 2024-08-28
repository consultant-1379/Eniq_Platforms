package com.ericsson.sim.monitor;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;

import com.ericsson.sim.model.properties.RuntimeProperties;

public class MonitorThreadTest {
	
	private MonitoringThread mt;
	private RopIntervalScheduler scheduler;
//	 private final String FILE_PATH = Paths.get(".").toAbsolutePath()
//	 .normalize().toString()
//	 + "/test/";

	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_Engine/test/";

	@Before
	public void setUp() throws Exception {

	}

	//@After
	//public void tearDown() throws Exception {
//
//		mt.keepRunning = false;
//		mt.exit();
//		Files.deleteIfExists(Paths.get(SIMEnvironment.CONFIGPATH
//				+ "enginestatus.txt"));
//
//		System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
//		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
//				"/eniq/sw/conf/sim");
//
//		SIMEnvironment.TOPOLOGYCONFIG = System.getProperty("TopologyConfig",
//				SIMEnvironment.CONFIGPATH + "topology.xml");
//
//	}

	@Test
	public void test(){
		
	}

//	@Test
//	public void testRunWithEngineStatusTextChange() throws IOException {
//		SIMEnvironment.CONFIGPATH = FILE_PATH;
//		SIMEnvironment.TOPOLOGYCONFIG = FILE_PATH + "topology.xml";
//		System.setProperty("ConfigPath", FILE_PATH);
//		System.setProperty("TopologyConfig", FILE_PATH + "topology.xml");
//
//		scheduler = RopIntervalScheduler.getInstance();
//
//		mt = new MonitoringThread();
//		if (new File(SIMEnvironment.CONFIGPATH + "enginestatus.txt").exists()) {
//			new File(SIMEnvironment.CONFIGPATH + "enginestatus.txt").delete();
//		}
//
//		Files.createFile(Paths.get(SIMEnvironment.CONFIGPATH
//				+ "enginestatus.txt"));
//
//		if (new File(SIMEnvironment.CONFIGPATH + "topology.xml").exists()) {
//			; 
//		} else {
//			Files.createFile(Paths.get(SIMEnvironment.CONFIGPATH
//					+ "topology.xml"));
//		}
//		mt.start();
//
//		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
//				"3");
//		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
//
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
//				SIMEnvironment.CONFIGPATH + "enginestatus.txt")));
//		bw.write("EniqEngineService=online");
//		bw.newLine();
//		bw.write("MEMORYNOTFULL");
//		bw.close();
//
////		BufferedWriter bw1 = new BufferedWriter(new FileWriter(
////				SIMEnvironment.CONFIGPATH + "topology.xml", true));
////		bw1.newLine();
////		bw1.close();
//
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//		}
//
//		assertEquals("online", System.getProperty("ENIQEngineStatus"));
//
//	}

}
