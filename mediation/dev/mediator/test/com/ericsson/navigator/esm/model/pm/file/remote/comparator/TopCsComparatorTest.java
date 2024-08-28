package com.ericsson.navigator.esm.model.pm.file.remote.comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TopCsComparatorTest {
	
	@Test
	public void testComparatorPass() {
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("AccessPoint_2012_08_02_17_43_46.xml");
		exampleFiles.add("CrossConnection_2012_08_02_17_43_46.xml");
		exampleFiles.add("AccessPoint_2012_09_02_17_43_46.xml");
		exampleFiles.add("CrossConnection_2012_09_02_17_43_46.xml");
		exampleFiles.add("AccessPoint_2012_03_02_17_43_46.xml");
		exampleFiles.add("CrossConnection_2012_03_02_17_43_46.xml");

		Collections.sort(exampleFiles, new TopCsComparator());
		
		assertTrue(exampleFiles.get(0).equals("AccessPoint_2012_09_02_17_43_46.xml"));
		assertTrue(exampleFiles.get(1).equals("CrossConnection_2012_09_02_17_43_46.xml"));
		assertTrue(exampleFiles.get(2).equals("AccessPoint_2012_08_02_17_43_46.xml"));
		assertTrue(exampleFiles.get(3).equals("CrossConnection_2012_08_02_17_43_46.xml"));
		assertTrue(exampleFiles.get(4).equals("AccessPoint_2012_03_02_17_43_46.xml"));
		assertTrue(exampleFiles.get(5).equals("CrossConnection_2012_03_02_17_43_46.xml"));

	}

	@Test
	public void testComparatorFail() {
		boolean exceptionCaught = false;
		List<String> exampleFiles = new ArrayList<String>();
		exampleFiles.add("PPM-R15m-NE3-.xml");
		exampleFiles.add("PPM-R15m-NE3-2012_06_01-00_24.xml");
		
		try{
			Collections.sort(exampleFiles, new TopCsComparator());
		}catch(Exception e){
			exceptionCaught = true;
		}
		assertTrue("Exception was caught", exceptionCaught);
	}
}
