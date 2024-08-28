package com.ericsson.sim.sftp;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.executor.ExecutionPool;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

@RunWith(JMock.class)
public class SFTPExecutionTest {
	
	SFTPExecution sftpExecution;
	ExecutionPool pool;
	
	
	Mockery context = new Mockery(){
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	private final Node node = context.mock(Node.class);
	private final SFTPproperties protocol = context.mock(SFTPproperties.class);
	

	@Before
	public void setUp() throws Exception {
		pool = new ExecutionPool(1);
		sftpExecution = new SFTPExecution();
		
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteSystemStatusClear() {
		context.checking(new Expectations(){
			{
				allowing(node).getName();will(returnValue("name"));
				allowing(protocol).getName();will(returnValue("name"));
				allowing(protocol).getInterval();will(returnValue(1));
				
				
				
			}
		});
		
	
		System.setProperty("ENIQEngineStatus", "online");
		System.setProperty("ENIQMemoryStatus", "failed");
		sftpExecution.execute(node, protocol, pool); 
	}
	
	@Test
	public void testCheckSystemStatus(){
		System.setProperty("ENIQEngineStatus", "Failed");
		System.setProperty("ENIQMemoryStatus", "Failed");
		assertFalse(sftpExecution.checkSystemStatus());
		 
		System.setProperty("ENIQEngineStatus", "Failed");
		System.setProperty("ENIQMemoryStatus", "MEMORYFULL");
		assertFalse(sftpExecution.checkSystemStatus());
		
		System.setProperty("ENIQEngineStatus", "online");
		System.setProperty("ENIQMemoryStatus", "MEMORYFULL");
		assertFalse(sftpExecution.checkSystemStatus());
		
		System.setProperty("ENIQEngineStatus", "online");
		System.setProperty("ENIQMemoryStatus", "failed");
		assertTrue(sftpExecution.checkSystemStatus());
		
	}

}
