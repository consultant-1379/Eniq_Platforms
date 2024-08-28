package com.ericsson.sim.sftp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginLoader;
import com.ericsson.sim.plugin.PluginParent;


public class SFTPExecutionThreadTest {
	
	private SFTPExecutionThread classUnderTest;
	private Node node;
	private SFTPproperties properties;
	
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new SFTPExecutionThread();
		node = new Node();
		properties = new SFTPproperties();
		properties.addInterval(3373707);
		
		properties.setName("name");
		properties.generateID();
		
		
		node.setName("name");
		node.generateID();
		node.addProperty("firstRop", String.valueOf(properties.getID()));
		
		classUnderTest.init(node, properties);
		
		System.setProperty("SFTPExecutionThreadTestValue", "failed");

		
	}

	@After
	public void tearDown() throws Exception {
	
		
	}
	 
	@Test
	public void testRunWithOperationThread() throws SIMException, IOException{

		properties.addProperty("operationThread", "com.ericsson.sim.sftp.PluginParentMockTest");
	
		RopInterval conn = new RopInterval();
		conn.setName("name");
		conn.addProperty("ROP", "2000");
		conn.generateID();

		RopIntervalPool.getInstance().addInterval(conn);

		classUnderTest.run(); 
		
		assertEquals("passed", System.getProperty("SFTPExecutionThreadTestValue"));
	}
	
	@Test
	public void testRunWithPreProcessing() throws SIMException, IOException{

		properties.addProperty("operationThread", "com.ericsson.sim.sftp.PluginParentMockTest");
		properties.addProperty("PreProcessing", "com.ericsson.sim.sftp.PluginParentMockTest");
		RopInterval conn = new RopInterval();
		conn.setName("name");
		conn.addProperty("ROP", "2000");
		conn.generateID();

		RopIntervalPool.getInstance().addInterval(conn);

		classUnderTest.run(); 
		
		assertEquals("passed", System.getProperty("SFTPExecutionThreadTestValue"));
	}
	
	@Test
	public void testRunWithProcessing() throws SIMException, IOException{

		properties.addProperty("operationThread", "com.ericsson.sim.sftp.PluginParentMockTest");
		properties.addProperty("Processing", "com.ericsson.sim.sftp.PluginParentMockTest");
		RopInterval conn = new RopInterval();
		conn.setName("name");
		conn.addProperty("ROP", "2000");
		conn.generateID();

		RopIntervalPool.getInstance().addInterval(conn);

		classUnderTest.run(); 
		
		assertEquals("passed", System.getProperty("SFTPExecutionThreadTestValue"));
	}
	
	@Test
	public void testRunWithPostProcessing() throws SIMException, IOException{

		properties.addProperty("operationThread", "com.ericsson.sim.sftp.PluginParentMockTest");
		properties.addProperty("PostProcessing", "com.ericsson.sim.sftp.PluginParentMockTest");
		RopInterval conn = new RopInterval();
		conn.setName("name");
		conn.addProperty("ROP", "2000");
		conn.generateID();

		RopIntervalPool.getInstance().addInterval(conn);

		classUnderTest.run(); 
		
		assertEquals("passed", System.getProperty("SFTPExecutionThreadTestValue"));
	}



}
