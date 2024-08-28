package com.ericsson.sim.engine.scheduler;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class RopIntervalSchedulerTest {
	private RopIntervalScheduler ropIntervalScheduler;


	Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private RopInterval ropInterval = context.mock(RopInterval.class);

	@Before
	public void setUp() throws Exception {
		ropIntervalScheduler = RopIntervalScheduler.getInstance();
	}
 
	@After
	public void tearDown() throws Exception {
		ropIntervalScheduler = null;
	}

	@Test
	public void testScheduleRopIntervalsNotRunning() {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");

		ropIntervalScheduler.scheduleRopIntervals();
	}
	
	@Test
	public void testScheduleRopIntervalsRunningNotFirstRop() {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");
		
		RopInterval rop = new RopInterval();
		rop.setName("name");
		rop.addProperty("ROP", "20");
		rop.addProperty("Offset", "2");
		RopIntervalPool.getInstance().addInterval(rop);

		ropIntervalScheduler.firstRop = false;
		ropIntervalScheduler.running = true;
		ropIntervalScheduler.scheduleRopIntervals();
	}
	
	@Test
	public void testScheduleRopIntervalsRunningFirstRop() {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");
		
		RopInterval rop = new RopInterval();
		rop.setName("name");
		rop.addProperty("ROP", "20");
		rop.addProperty("Offset", "2");
		RopIntervalPool.getInstance().addInterval(rop);

		ropIntervalScheduler.firstRop = true;
		ropIntervalScheduler.running = true;
		ropIntervalScheduler.scheduleRopIntervals();
	}

	@Test
	public void testCalculateSleepTime() {
		boolean resultWithTolerance = false;

		context.checking(new Expectations() {
			{
				oneOf(ropInterval).getProperty("ROP");
				will(returnValue("10"));
				oneOf(ropInterval).getProperty("Offset");
				will(returnValue("2"));
			}
		});

		int expectedAnswer = Calendar.getInstance().get(Calendar.MINUTE);
		
		expectedAnswer = (expectedAnswer / 10) * 10 + 10;
		
		expectedAnswer = expectedAnswer - Calendar.getInstance().get(Calendar.MINUTE);
	
		expectedAnswer = (expectedAnswer * 60);
		expectedAnswer = expectedAnswer - Calendar.getInstance().get(Calendar.SECOND);
		ropIntervalScheduler.firstRop = false;
		long tolerance = 120000;
		long result = ropIntervalScheduler.calculateSleepTime(ropInterval, false);
		
		if (((expectedAnswer * 1000 + tolerance) >= result ) || ((expectedAnswer * 1000 - tolerance) <= result)){
			resultWithTolerance = true;
		}

		assertTrue(resultWithTolerance);
	}
	
	@Test
	public void testCalculateSleepTimeFirstRop() {

		context.checking(new Expectations() {
			{
				oneOf(ropInterval).getProperty("ROP");
				will(returnValue("10"));
				oneOf(ropInterval).getProperty("Offset");
				will(returnValue("2"));
			}
		});

		int expectedAnswer = Calendar.getInstance().get(Calendar.MINUTE);
		expectedAnswer = (expectedAnswer / 10) * 10 + 10;
		expectedAnswer = expectedAnswer - Calendar.getInstance().get(Calendar.MINUTE);
		
		expectedAnswer = expectedAnswer+2;
		
		expectedAnswer = (expectedAnswer * 60);
		expectedAnswer = expectedAnswer - Calendar.getInstance().get(Calendar.SECOND);
		ropIntervalScheduler.firstRop = true;

		assertEquals(expectedAnswer * 1000,
				ropIntervalScheduler.calculateSleepTime(ropInterval, true));
	}

	@Test
	public void testGetName() {
		assertEquals("com.ericsson.sim.engine.scheduler.RopIntervalScheduler",
				ropIntervalScheduler.getName());
	}

	@Test
	public void testExit() {
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutorThreads",
				"3");
		RuntimeProperties.getInstance().addProperty("NoOfSFTPExecutors", "1");

		ropIntervalScheduler.scheduleRopIntervals();
		ropIntervalScheduler.exit();
		assertFalse(ropIntervalScheduler.running);
	}

	@Test
	public void testStartUp() {
		ropIntervalScheduler.startup();
		assertTrue(ropIntervalScheduler.running);
	}

}
