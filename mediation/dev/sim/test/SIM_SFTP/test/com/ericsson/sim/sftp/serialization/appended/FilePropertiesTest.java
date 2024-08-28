package com.ericsson.sim.sftp.serialization.appended;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;

public class FilePropertiesTest {
	private FileProperties classUnderTest;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString()+"/test/";
	
	@Before
	public void setUp() throws Exception {
		
		SIMEnvironment.CONFIGPATH = FILE_PATH;
		
		classUnderTest = new FileProperties();
	}

	@After
	public void tearDown() throws Exception {
		
		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetAndSetLastCollectedTime() {
		assertEquals(new Date(0), classUnderTest.getLastCollectedTime());
		
		classUnderTest.setLastCollectedTime(new Date(2015,3,21,12,15));
		
		assertNotSame(new Date(0), classUnderTest.getLastCollectedTime());
		assertEquals(new Date(2015,3,21,12,15), classUnderTest.getLastCollectedTime());
		
	}

	
	@Test
	public void testGetAndSetLastParsedLine() {
		assertEquals("", classUnderTest.getLastParsedLine());
		
		classUnderTest.setLastParsedLine("Line");
		
		assertNotSame("", classUnderTest.getLastParsedLine());
		assertEquals("Line", classUnderTest.getLastParsedLine());
	}
	
	
	
	
	@Test
	public void testGetAndIncrementRopsUnmodified() {
		assertEquals(0, classUnderTest.getRopsUnmodified());
		
		classUnderTest.incrementRopsUnmodified();
		assertEquals(1, classUnderTest.getRopsUnmodified());
		
		classUnderTest.clearUnmodified();
		assertEquals(0, classUnderTest.getRopsUnmodified());
		
		
	}
	
	

}
