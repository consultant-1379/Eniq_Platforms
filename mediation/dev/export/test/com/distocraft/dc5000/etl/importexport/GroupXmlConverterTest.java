package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.common.HashIdCreator;

public class GroupXmlConverterTest {

	private GroupXmlConverter testInstance = null;
	@Before
	public void setUp(){
		testInstance = new GroupXmlConverter(",", "\n", "", null);
	}
	@After
	public void tearDown(){
		testInstance = null;
	}

	@Test
	public void testWriteToFile() throws IOException {
		final File testFile = new File(System.getProperty("java.io.tmpdir"));
		final String tName = "SOME_TABLE_123";
		final String cols = "d, e, F";
		final List<String> toWrite = Arrays.asList("!123,", "b, b, c ,d, "+System.currentTimeMillis()+", e,");
		final Map<File, String> fcMap = new HashMap<File, String>();
		for(String s : toWrite){
			testInstance.writeToFile(testFile.getAbsolutePath(), tName, s, cols, fcMap);
		}
		assertTrue(fcMap.size() > 0);
		final File expFile = new File(testFile, tName+".sql");
		final File keyFile = fcMap.keySet().iterator().next();
		assertEquals(expFile.getAbsolutePath(), keyFile.getAbsolutePath());
		assertEquals(cols, fcMap.get(keyFile));
		testInstance.destroy();
		final File[] testingFile = testFile.listFiles(new FileFilter(){
			@Override
			public boolean accept(final File pathname) {
				return pathname.getName().contains(tName);
			}
		});
		assertNotNull(testingFile);
		assertEquals(1, testingFile.length);

		final BufferedReader br = new BufferedReader(new FileReader(testingFile[0]));
		String line;
		try{
			while ( (line=br.readLine()) != null){
				assertTrue(toWrite.contains(line));
			}
		} finally{
			br.close();
		}
		if(!testingFile[0].delete()){
			System.out.println("Warning, couldn't delete " + testingFile[0].getAbsolutePath());
		}
	}

  @Test
  public void checkEVENTSRC_IDIsCorrect() throws NoSuchAlgorithmException, IOException {
    HashIdCreator hashIdCreator = new HashIdCreator();
    final Map<String, String> unorderedSourceData = new HashMap<String, String>();
    unorderedSourceData.put("EVENT_SOURCE_NAME", "MSC10");
    String actualHashId = testInstance.getHashId(unorderedSourceData, "EVNTSRC_ID");
    String expectedHashId = Long.toString(hashIdCreator.hashStringToLongId("MSC10"));
    assertEquals(expectedHashId, actualHashId);
  }

  @Test
  public void checkHIER3_IDIsCorrect() throws NoSuchAlgorithmException, IOException {
    final String eventSrc = "MSC10";
    final String rat = "1";
    final String hier3 = "BSC123";
    final String vendor = "Ericsson";

    HashIdCreator hashIdCreator = new HashIdCreator();
    final Map<String, String> unorderedSourceData = new HashMap<String, String>();
    unorderedSourceData.put("EVENT_SOURCE_NAME", eventSrc);
    unorderedSourceData.put("RAT", rat);
    unorderedSourceData.put("HIERARCHY_3", hier3);
    unorderedSourceData.put("VENDOR", vendor);
    String actualHashId = testInstance.getHashId(unorderedSourceData, "HIER3_ID");
    String expectedHashId = Long.toString(hashIdCreator.hashStringToLongId(rat + "|" + hier3 + "|" + vendor));

    assertEquals(expectedHashId, actualHashId);
  }

  @Test
  public void checkHIER32_IDIsCorrect() throws NoSuchAlgorithmException, IOException {
    final String eventSrc = "MSC10";
    final String rat = "1";
    final String hier3 = "BSC123";
    final String hier2 = null;
    final String vendor = "Ericsson";

    HashIdCreator hashIdCreator = new HashIdCreator();
    final Map<String, String> unorderedSourceData = new HashMap<String, String>();
    unorderedSourceData.put("EVENT_SOURCE_NAME", eventSrc);
    unorderedSourceData.put("RAT", rat);
    unorderedSourceData.put("HIERARCHY_3", hier3);
    unorderedSourceData.put("HIERARCHY_2", hier2);
    unorderedSourceData.put("VENDOR", vendor);
    String actualHashId = testInstance.getHashId(unorderedSourceData, "HIER32_ID");
    String expectedHashId = Long.toString(hashIdCreator.hashStringToLongId(rat + "|" + hier3 + "||" + vendor));

    assertEquals(expectedHashId, actualHashId);
  }

  @Test
  public void checkHIER321_IDIsCorrect() throws NoSuchAlgorithmException, IOException {
    final String eventSrc = "MSC10";
    final String rat = "1";
    final String hier3 = "BSC123";
    final String hier2 = null;
    final String hier1 = "CELL123";
    final String vendor = "Ericsson";

    HashIdCreator hashIdCreator = new HashIdCreator();
    final Map<String, String> unorderedSourceData = new HashMap<String, String>();
    unorderedSourceData.put("EVENT_SOURCE_NAME", eventSrc);
    unorderedSourceData.put("RAT", rat);
    unorderedSourceData.put("HIERARCHY_3", hier3);
    unorderedSourceData.put("HIERARCHY_2", hier2);
    unorderedSourceData.put("HIERARCHY_1", hier1);
    unorderedSourceData.put("VENDOR", vendor);
    String actualHashId = testInstance.getHashId(unorderedSourceData, "HIER321_ID");
    String expectedHashId = Long.toString(hashIdCreator.hashStringToLongId(rat + "|" + hier3 + "||" + hier1 + "|"
        + vendor));

    assertEquals(expectedHashId, actualHashId);
  }
}
