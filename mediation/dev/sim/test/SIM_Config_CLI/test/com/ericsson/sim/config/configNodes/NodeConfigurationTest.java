package com.ericsson.sim.config.configNodes;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;

public class NodeConfigurationTest {
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString()+"/test";
	private File file;
	private NodeConfiguration classUnderTest;

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.CONFIGPATH = FILE_PATH;
		file = new File((FILE_PATH+"/inputFile"));
		PrintWriter pw = new PrintWriter(new FileWriter(new File(FILE_PATH+"/inputFile"), true));
		pw.print("name,IPAddress,uniqueID,sftPort,sftpUserName,SNMPCommunity,SNMPPort,SNMPVersion,pluginNames");
		pw.println();
		pw.print("CCN1,10.42.39.24,uniqu1,22,dcuser,null,null,v2,CCN_Plugin");
		pw.println();
		pw.print("MRFP02,10.42.39.24,uuuuu3,,public,,161,v2,HP-MRFP");
		pw.println();
		pw.print("MRFP02,10.42.39.24,,,public,,161,v2,HP-MRFP|AIR|SERT");
		pw.close();
		
		
		
	}

	@After
	public void tearDown() throws Exception {
		if(new File(FILE_PATH+"/inputFile").exists()){
			
			new File(FILE_PATH+"/inputFile").delete();
		}
		
		new File(FILE_PATH+"/topology.xml").delete();
		
		SIMEnvironment.CONFIGPATH =  System.getProperty("ConfigPath", "/eniq/sw/conf/sim");
	}

	@Test
	public void testRunConfigurationOfNodes() {
		
		classUnderTest = new NodeConfiguration(file.getAbsolutePath().toString());
		
		try {
			BufferedReader bw = new BufferedReader(new FileReader(new File(FILE_PATH+"/topology.xml")));
			bw.readLine();	
			assertEquals("<Node uniqueID=\"uniqu1\" name=\"CCN1\" IPAddress=\"10.42.39.24\">", bw.readLine());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
