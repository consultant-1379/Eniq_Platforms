package com.ericsson.sim.executor;

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ExecutionPoolTest {

	private Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
	
	private ExecutionPool executionPool;
	private ExecutorThread executorThread = context.mock(ExecutorThread.class);
	
	@Before
	public void setUp() throws Exception {
		executionPool = new ExecutionPool(1);
	}
	
	@After 
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testgetQueuedCount() {
		assertEquals(0, executionPool.getQueuedCount());
	}
	
	@Test
	public void testAddToExecutor() {
		context.checking(new Expectations() {
			{
				allowing(executorThread).run();			
			}
		});
		executionPool.addToExecutor(executorThread);
		assertEquals(1, executionPool.getQueuedCount());
	}
	
	@Test
	public void testshutdown() {
		executionPool.shutdown();
	} 
	
}




