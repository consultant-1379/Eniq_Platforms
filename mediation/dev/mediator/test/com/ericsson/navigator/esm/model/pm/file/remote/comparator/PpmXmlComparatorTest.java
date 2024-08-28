package com.ericsson.navigator.esm.model.pm.file.remote.comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


public class PpmXmlComparatorTest {

	@Test
	public void testComparatorPass() {
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("PPM-R15m-NE3-2012_06_03-00_24.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_06_01-00_24.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_07_07-00_24.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_01_01-00_24.xml");

		Collections.sort(exampleFiles, new PpmXmlComparator());

		assertTrue(exampleFiles.get(3).equals("PPM-R15m-NE3-2012_01_01-00_24.xml"));
		assertTrue(exampleFiles.get(2).equals("PPM-R15m-NE3-2012_06_01-00_24.xml"));
		assertTrue(exampleFiles.get(1).equals("PPM-R15m-NE3-2012_06_03-00_24.xml"));
		assertTrue(exampleFiles.get(0).equals("PPM-R15m-NE3-2012_07_07-00_24.xml"));
	}

	@Test
	public void testComparatorFail() {
		boolean exceptionCaught = false;
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("PPM-R15m-NE3-.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_06_01-00_24.xml");
		
		try{
			Collections.sort(exampleFiles, new PpmXmlComparator());
			System.out.println(exampleFiles);
		}catch(Exception e){
			exceptionCaught = true;	
		}
		
		assertTrue("Exception was caught", exceptionCaught);
	}
	
	
}
