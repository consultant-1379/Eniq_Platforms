package com.ericsson.sim.engine.execution;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class ExecutionManagerTest {

	private ExecutionManager execManager;
	private Node node;
	private SFTPproperties protocolSFTP;
	private SNMPproperties protocolSNMP;

	@Before
	public void setUp() throws Exception {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");
		node = new Node();
		protocolSFTP = new SFTPproperties();

		protocolSFTP.addProperty("ExecutionThreadName",
				"com.ericsson.sim.engine.execution.SFTPExecutionDummy");
		
		System.setProperty("sftpScheduleExecutionTest", "failed".toUpperCase());
		System.setProperty("sftpScheduleExecutionTest", "failed".toUpperCase());

		execManager = ExecutionManager.getInstance();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testScheduleExecutionSFTP() {
		protocolSFTP = new SFTPproperties();

		protocolSFTP.addProperty("ExecutionThreadName",
				"com.ericsson.sim.engine.execution.SFTPExecutionDummy");

		execManager.scheduleExecution(node, protocolSFTP);
		
		assertEquals("passed".toUpperCase(), System.getProperty("sftpScheduleExecutionTest", "failed"));
	}
	
	@Test
	public void testScheduleExecutionSNMP() {
		protocolSNMP = new SNMPproperties();

		protocolSNMP.addProperty("ExecutionThreadName",
				"com.ericsson.sim.engine.execution.SNMPExecutionDummy");

		execManager.scheduleExecution(node, protocolSNMP);
		
		assertEquals("passed".toUpperCase(), System.getProperty("snmpScheduleExecutionTest", "failed"));
	}
	
	

}
