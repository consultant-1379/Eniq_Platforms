package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParseActionViewTest extends TestCase {

	private ParseActionView parseActionView = null;
	private Object[] listOfParserShortNames;
	
	@Test
	public void testInputDefaultParserShortName() {
		String selPType = "mdc";
	    String shortName = "mdc";
	    int expected = 1; // mdc
	    int actual = parseActionView.selectInList(listOfParserShortNames, selPType, shortName);
	    assertEquals(expected, actual);
	}
	
	@Test
	public void testInputConfigParserShortName() {
		String selPType = "hxml";
	    String shortName = "";
	    int expected = 2; // hxml
	    int actual = parseActionView.selectInList(listOfParserShortNames, selPType, shortName);
	    assertEquals(expected, actual);
	}
	
	@Test
	public void testInputCustomParserLongPath() {
		String selPType = "com.ericssons.custom.GSDCParser";
	    String shortName = "";
	    int expected = 0; // CUSTOM
	    int actual = parseActionView.selectInList(listOfParserShortNames, selPType, shortName);
	    assertEquals(expected, actual);
	}
	
	@Test
	public void testInputDefaultParserLongPath() {
		String selPType = "com.distocraft.dc5000.etl.mdc.MDCParser";
	    String shortName = "mdc";
	    int expected = 1; // mdc
	    int actual = parseActionView.selectInList(listOfParserShortNames, selPType, shortName);
	    assertEquals(expected, actual);
	}
	
	@Test
	public void testInputConfigParserLongPath() {
		String selPType = "com.ericsson.eniq.etl.hxml.HXMLParser";
	    String shortName = "hxml";
	    int expected = 2; // hxml
	    int actual = parseActionView.selectInList(listOfParserShortNames, selPType, shortName);
	    assertEquals(expected, actual);
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(parseActionView);
	}

	@Before
    protected void setUp() throws Exception {
		listOfParserShortNames = getTestParserFormats();
		parseActionView = new ParseActionView() {
			
		};

	}

	@After
    public void tearDown() throws Exception {
		parseActionView = null;
	}
	
	private Object[] getTestParserFormats() {
		List<String> parserFormats = new ArrayList<String>();
		parserFormats.add("CUSTOM"); // Custom is expected to be in Constants.PARSERFORMATS
		parserFormats.add("mdc");
		parserFormats.add("hxml");
		return parserFormats.toArray();
	}

}
