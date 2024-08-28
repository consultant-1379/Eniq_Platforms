package com.ericsson.sim.monitor.eniq;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;

public class EniqSystemStatusHandlerTest {
	private EniqSystemStatusHandler esth;
//	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
//			.normalize().toString()
//			+ "/test/";
	
	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_Engine/test/";

	@Before
	public void setUp() throws Exception {
		esth = new EniqSystemStatusHandler();
		
		SIMEnvironment.CONFIGPATH = FILE_PATH+"esth/";
		SIMEnvironment.TOPOLOGYCONFIG = FILE_PATH+"topology.xml";
		System.setProperty("ConfigPath", FILE_PATH);
		System.setProperty("TopologyConfig", FILE_PATH+"topology.xml");
		
		Files.createDirectory(Paths.get(SIMEnvironment.CONFIGPATH));
		
		if(new File(SIMEnvironment.CONFIGPATH +"enginestatus.txt").exists()){
			;
		}else{
			Files.createFile(Paths.get(SIMEnvironment.CONFIGPATH+"enginestatus.txt"));
		}
		
		
	}

	@After
	public void tearDown() throws Exception {
		
		
		Files.deleteIfExists(Paths.get(SIMEnvironment.CONFIGPATH+"enginestatus.txt"));
		Files.deleteIfExists(Paths.get(SIMEnvironment.CONFIGPATH));
		System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
		SIMEnvironment.CONFIGPATH =  System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
		
		SIMEnvironment.TOPOLOGYCONFIG = System.getProperty("TopologyConfig", SIMEnvironment.CONFIGPATH + "topology.xml");
	}

	@Test
	public void testStart() throws IOException{
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(SIMEnvironment.CONFIGPATH+"enginestatus.txt")));
		bw.write("EniqEngineService=online");
		bw.newLine();
		bw.write("MEMORYNOTFULL");
		bw.close();
		
		
		esth.start();
		assertEquals("online", System.getProperty("ENIQEngineStatus"));
	}

}
