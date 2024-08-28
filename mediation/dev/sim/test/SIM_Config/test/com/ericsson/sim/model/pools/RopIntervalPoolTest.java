package com.ericsson.sim.model.interval;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.RopIntervalPool;

public class RopIntervalPoolTest {

	private RopIntervalPool obj;
	private RopInterval interval = new RopInterval();
	private RopInterval anotherInterval = new RopInterval();
	private String FILE_PATH;
	
	@Before
	public void setUp() throws Exception {
		obj = RopIntervalPool.getInstance();
		interval.setName("name");
		anotherInterval.setName("anotherName");
		FILE_PATH = "test";
	}

	@After
	public void tearDown() throws Exception { 
		Files.deleteIfExists(Paths.get((FILE_PATH + "/fake/intervals.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/fake")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/intervals.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/protocols.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/topology.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/properties.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config")).toAbsolutePath());
		obj.getRopIntervals().clear();
	}

	@Test
	public void testgetInstance() {
		assertNotNull(RopIntervalPool.getInstance());
	}
	
	@Test
	public void testgetInterval(){
		assertNull(obj.getInterval(interval.getID()));
	}
	
	@Test
	public void testaddInterval(){
		obj.addInterval(interval);
		assertEquals(obj.getInterval(interval.getID()), interval);
	}
	
	@Test
	public void testgetRopIntervals(){
		assertEquals(obj.getRopIntervals().size(), 0);
		obj.addInterval(interval);
		assertEquals(obj.getRopIntervals().size(), 1);
		obj.addInterval(anotherInterval);
		assertEquals(obj.getRopIntervals().size(), 2);
		obj.addInterval(anotherInterval);
		assertEquals(obj.getRopIntervals().size(), 2);
	}
	
	@Test
	public void testgetNunberofFRopIntervals(){
		assertEquals(obj.getNumberofRopIntervals(), 0);
		obj.addInterval(interval);
		assertEquals(obj.getNumberofRopIntervals(), 1);
	}
	
	@Test
	public void testremoveInterval(){
		assertEquals(obj.getNumberofRopIntervals(), 0);
		obj.addInterval(interval);
		assertEquals(obj.getNumberofRopIntervals(), 1);
		obj.removeInterval(interval.getID());
		assertEquals(obj.getNumberofRopIntervals(), 0);
	}
	
	@Test
	public void testwritePersistedFile() throws Exception {
		File fakeFile = new File(FILE_PATH + "/fake");
		assertTrue(!fakeFile.exists());
		obj.writePersistedFile(FILE_PATH + "/fake");
		assertTrue(fakeFile.exists());
		
    	RopIntervalPool.getInstance().writePersistedFile(FILE_PATH + "/config");
    	assertTrue((new File(FILE_PATH + "/config/intervals.simc")).exists());
	}
	
	@Test
	public void testloadPersistedFile() throws Exception {
		obj.addInterval(interval);
		obj.addInterval(anotherInterval);
		obj.writePersistedFile(FILE_PATH + "/config");
		assertTrue((new File(FILE_PATH + "/config/intervals.simc")).exists());
		obj.loadPersistedFile(FILE_PATH + "/config/intervals.simc");
		HashMap<Integer, RopInterval> actualIntervals = obj.getRopIntervals();
    	assertEquals(actualIntervals.size(), 2);
	}

}
