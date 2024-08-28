package com.ericsson.eniq.etl.ebsHandler.action;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import junit.framework.TestCase;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;

public class PmmomTest extends TestCase {
    private Pmmom testInstance = null;
    private final String tpName = "PM_E_EBSW";
    public PmmomTest(){
        super("PmmomTest");
    }

    @Override @Before
    public void setUp() throws Exception {
        final Map<String, Measurementtype> mtMap = new HashMap<String, Measurementtype>(2);
        final Measurementtype mt1 = new Measurementtype(null);
        mt1.setTypename(tpName+"_CELL");
        mtMap.put("UtranCell", mt1);
        final Measurementtype mt2 = new Measurementtype(null);
        mt2.setTypename(tpName+"_CELL");
        mtMap.put("MOM_cell", mt2);
        final Measurementtype mt3 = new Measurementtype(null);
        mt3.setTypename(tpName+"_TRX");
        mtMap.put("MOM_trx", mt3);
        testInstance = new Pmmom();
        testInstance.setMeastypesMap(mtMap);
        testInstance.setTechpackName(tpName);
    }

    @Override @After
    public void tearDown() throws Exception {
        testInstance = null;
    }

	@Test
	public void testDeleteUndefinedMocs() throws Exception {
		final File momFile = getFile("test2.xml");
		testInstance.read(momFile);
		final List<Meastype> todo = testInstance.getMeastypes();
//		assertEquals("List should defined 2 mocs even though only one is defined in mom file", 2, todo.size());
		final List<String> expected = Arrays.asList("trx");
		for(Meastype mt : todo){
			final boolean found = expected.contains(mt.getName());
			assertTrue("Unknown MeasType '"+mt.getName()+"' found", found);
		}
		for(String eType : expected){
			boolean found = false;
			for(Meastype mt : todo){
				if(mt.getName().equals(eType)){
					found = true;
					break;
				}
			}
			assertTrue("Expected MeasType '"+eType+"' not found", found);
		}
	}
    @Test
    public void testParseWrongFileType() throws Exception{
        // now try and parse an ggsn file, error should be thrown..
        final File momFile = getFile("ebsg_test_mom.xml");
        try{
            testInstance.read(momFile);
            fail("Parse error should have been thrown, measObjClass is not correct for this tech pack.");
        } catch (SAXParseException spe){
            Assert.assertTrue("Exception message not correct ", spe.getMessage().contains("is not supported in "+tpName));
            Assert.assertTrue("Exception message not correct ", spe.getMessage().contains(momFile.getAbsolutePath()));
        }
    }
    @Test
    public void testTrimWhiteSpace() throws Exception {
        final String xml = getFileAsString("whiteSpaceTest.xml");
        testInstance.read(xml);
        final Vector<Meastype> mTypes = testInstance.getMeastypes();
        for(Meastype mt : mTypes){
            checkWhiteSpace("Meastype", mt.getName());
            final Vector<Group> gps = mt.getGroups();
            for(Group gp : gps){
                checkWhiteSpace("Group", gp.getName());
                final Vector<Formula> forms = gp.getFormulas();
                for(Formula f : forms){
                    checkWhiteSpace("Function", f.getFunction());
                    checkWhiteSpace("Function", f.getMeasName());
                    checkWhiteSpace("Function", f.getName());
                    for(String arg : f.getArguments()){
                        checkWhiteSpace("Arguement", arg);
                    }
                }
                final Vector<Counter> counters = gp.getCounters();
                for(Counter c : counters){
                    checkWhiteSpace("Counter", c.getAggregation());
                    checkWhiteSpace("Counter", c.getName());
                    checkWhiteSpace("Counter", c.getSize());
                    checkWhiteSpace("Counter", c.getType());
                }
            }
        }
    }
    @Test
    public void testFileToString() throws Exception {
        final String result = getFileAsString("fileToStringTest_expected.xml");
        try {
            final String xml = getFileAsString("test.xml");
            testInstance.read(xml);
            assertEquals(result, testInstance.toString());
        } catch (Exception e) {
            fail("Exception " + e);
        }
    }
    @Test
    public void testStringToString() throws Exception {
        final String input = getFileAsString("stringToStringTest.xml");
        final String expected = getFileAsString("stringToStringTest_expected.xml");
        try {
            testInstance.read(input);
            assertEquals(expected, testInstance.toString());
        } catch (Exception e) {
            fail("Exception " + e);
        }
    }
    @Test
    public void testFileToXML() throws Exception {
        final String expected = getFileAsString("fileToXMLTest_expected.xml");
        final String defaults = getFileAsString("defaults.xml");
        try {
            final String xml = getFileAsString("test.xml");
            testInstance.read(xml);
            final String tXml = testInstance.toXML(defaults);
            assertEquals(expected, tXml);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception " + e);
        }
    }
    @Test
    public void testStringToXML() throws Exception {
        final String input = getFileAsString("stringToXMLTest.xml");
        final String result = getFileAsString("stringToXMLTest_expected.xml");
        final String defaults = getFileAsString("defaults.xml");
        try {
            testInstance.read(input);
            assertEquals(result, testInstance.toXML(defaults));
        } catch (Exception e) {
            fail("Exception " + e);
        }
    }

    private void checkWhiteSpace(final String type, final String toCheck){
        final boolean chk = toCheck.startsWith(" ") || toCheck.endsWith(" ");
        assertFalse("Whitespace not trimmed from "+type+" '"+toCheck+"'", chk);
    }
    private File getFile(final String fileName) throws Exception {
        final URL url = ClassLoader.getSystemResource(fileName);
        return new File(url.toURI().getRawPath());
    }
    private String getFileAsString(final String fileName) throws Exception {
        final File f = getFile(fileName);
        final BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        final StringBuilder sb = new StringBuilder();
        try{
            while ( (line=br.readLine()) != null){
                sb.append(line).append("\n");
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
}
