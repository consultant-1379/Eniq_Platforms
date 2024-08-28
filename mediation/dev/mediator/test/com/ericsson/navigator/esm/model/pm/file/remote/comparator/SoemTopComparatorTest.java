package com.ericsson.navigator.esm.model.pm.file.remote.comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SoemTopComparatorTest {

	@Test
	public void testComparatorPass() {
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("380g7x01.mlm2003.se_Configuration_Data_20120802_093030.xml");
		exampleFiles.add("380g7x01.mlm2003.se_Configuration_Data_20120102_093030.xml");
		exampleFiles.add("380g7x01.mlm2003.se_Configuration_Data_20120902_093030.xml");
		exampleFiles.add("380g7x01.mlm2003.se_Configuration_Data_20120302_093030.xml");
		
		Collections.sort(exampleFiles, new SoemTopComparator());
		
		
		assertTrue(exampleFiles.get(3).equals("380g7x01.mlm2003.se_Configuration_Data_20120102_093030.xml"));
		assertTrue(exampleFiles.get(2).equals("380g7x01.mlm2003.se_Configuration_Data_20120302_093030.xml"));
		assertTrue(exampleFiles.get(1).equals("380g7x01.mlm2003.se_Configuration_Data_20120802_093030.xml"));
		assertTrue(exampleFiles.get(0).equals("380g7x01.mlm2003.se_Configuration_Data_20120902_093030.xml"));
		
	}

	@Test
	public void testComparatorFail() {
		boolean exceptionCaught = false;
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("PPM-R15m-NE3-.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_06_01-00_24.xml");
		
		try{
			Collections.sort(exampleFiles, new SoemTopComparator());
		}catch(Exception e){
			exceptionCaught=true;
			
		}
		assertTrue("Exception was caught", exceptionCaught);
	}
	
}
