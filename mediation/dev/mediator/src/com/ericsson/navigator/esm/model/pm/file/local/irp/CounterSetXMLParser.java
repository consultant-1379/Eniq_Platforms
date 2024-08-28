package com.ericsson.navigator.esm.model.pm.file.local.irp;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileLoader;
import com.ericsson.navigator.esm.manager.pm.file.local.irp.CounterSetFileParser;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class CounterSetXMLParser extends DefaultHandler implements CounterSetFileParser { //NOPMD 

	private static final String classname = CounterSetXMLParser.class.getName();
	public static Logger logger = Logger.getLogger(classname);
	
	private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
	private final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final SimpleDateFormat formatter3 = new SimpleDateFormat("yyyyMMddHHmmssZ", Locale.US);
	private final TimeZone timeZone = (new GregorianCalendar()).getTimeZone();

	private final CounterSetFileLoader counterSetFileLoader;
	private Map<String, String> observedCounters;
	private List<String> counterNames = new Vector<String>();
	private int counterPos = 0;

	private String tag;
	private String nednString = "";
	private String moidString = "";
	private String mtString = null;
	private String rString =null;
	private String mtsString=null;
	private String gpString=null;
	private List<String> validTags = null;
	
	public CounterSetXMLParser(final CounterSetFileLoader counterSetFileLoader) {
		this.counterSetFileLoader = counterSetFileLoader;
		formatter1.setTimeZone(timeZone);
		formatter2.setTimeZone(timeZone);
		formatter3.setTimeZone(timeZone);
	}

	private void resetParsedStrings() {
		tag = null;
		nednString = "";
		moidString = "";
		mtString = null;
		rString =null;
		mtsString=null;
		gpString=null;
		validTags = null;
		validTags = new ArrayList<String>();

	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException { //NOPMD
		final String _tag = tag;
		tag = null;
		if ("r".equalsIgnoreCase(_tag)) {
			rString = new String(ch, start, length).trim();
			addObservedCounter(counterPos,rString);
			counterPos++;
			return;
		}
		if ("mt".equalsIgnoreCase(_tag)) {
			mtString = new String(ch, start, length).trim();
			counterNames.add(mtString);
			return;
		}
		if ("nedn".equalsIgnoreCase(_tag)) {
			nednString = new String(ch, start, length).trim();
			if(!counterSetFileLoader.validListener(nednString)) {
				logger.warn("nedn \"" + nednString + "\" found during parsing not matching a registred system");					
			}
			return;
		}
		if ("mts".equalsIgnoreCase(_tag)) {				
			mtsString = new String(ch, start, length).trim();
			return;
		}
		if ("gp".equalsIgnoreCase(_tag)) {
			gpString = new String(ch, start, length).trim();
			return;
		}
		if ("moid".equalsIgnoreCase(_tag)) {
			moidString = new String(ch, start, length).trim();
			return;
		}
		if ("sf".equalsIgnoreCase(_tag)) {
			return;
		}
	}
	
	@Override
	public void startElement(final String nsURI, final String strippedName,
			final String tagName, final Attributes managed_object_attribute) throws SAXException {
		tag = tagName.toLowerCase();
		if("mdc".equalsIgnoreCase(tag)){
			validTags.add(tag);

		}
		else if ("mi".equalsIgnoreCase(tag)){
			validTags.add(tag);
			counterNames = new Vector<String>();
			observedCounters = new HashMap<String, String>();
			return;
		}
		else if ("mv".equalsIgnoreCase(tag)){
			validTags.add(tag);
			counterPos = 0;
			return;
		}
	
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) {
		
		if("mv".equalsIgnoreCase(qName)) {
			addCounterSet(parseDate(mtsString), Integer.parseInt(gpString));
			moidString = "";
			return;
		}
		else if("md".equalsIgnoreCase(qName)) {
			nednString = "";
			moidString = "";
			return;
		}
		
	}

	private void addCounterSet(long endTime, final int gpLong) {
		if (endTime > System.currentTimeMillis()) { // TR-HM51919
			endTime = System.currentTimeMillis();
		}
		counterSetFileLoader.addCounterSet(nednString, moidString, gpLong, new Date(endTime), observedCounters);
	}

	private void addObservedCounter(final int pos, String value) {
		if (value == "") {
			value = "null";
		}
		if (pos < counterNames.size()) {
			observedCounters.put(counterNames.get(pos), value);
		} else {
			logger.warn(classname + ".addObservedCounter;pos is greater than List:counterNames size");
		}
	}
	
	@SuppressWarnings( "PMD.CyclomaticComplexity" )
	private long parseDate(final String dateString){
		long endTime = System.currentTimeMillis();
		// Case 1: (+ or -) TIMEZONE
		if ((dateString.indexOf("+") > -1) || (dateString.indexOf("-") > -1)) {
			try {
				// <mts>20060605041000.0+0200</mts>
				//TR-HL84058 - ESMTP: Transformation do not aggregate correctly if PM data comes from different timezones
				String simpleDate = dateString.replaceAll("T", "").trim();
				final int dot = dateString.indexOf(".");
				int sign =0;
				if (dateString.contains("+")) {
					sign = dateString.indexOf("+");
				} else if (dateString.contains("-")) {
					sign = dateString.indexOf("-");
				}
				simpleDate = dateString.substring(0, dot) + dateString.substring(sign);
				endTime = formatter3.parse(simpleDate).getTime();
				endTime = endTime - 30L * 1000L;
				endTime = endTime + (60L * 1000L - (endTime % (60L * 1000L)));
				return endTime;

			} catch (final Exception e) {
				logger.error(classname+".parseDate;", e);
			}
		}

		try {
			// yyyyMMddHHmmss
			endTime = formatter2.parse(dateString).getTime();
			return endTime;
		} catch (final Exception e) {
			logger.error(classname+".parseDate; Time format used for mts formatter2: " + e);
		}

		try {
			// yyyyMMddHHmm
			endTime = formatter1.parse(dateString).getTime();
			return endTime;
		} catch (final Exception e) {
			logger.error(classname+".parseDate; Time format used for mts formatter1: " + e);
		}
		try {
			endTime = 1000L * Long.parseLong(dateString);
			return endTime;
		} catch (final Exception e) {
			logger.debug(classname+".parseDate; Time format used for mts utime/EPOCH: " + e);
		}
		logger.debug(classname+".parseDate; Time format used is not parseable:"+dateString);
		return 0;
	}
	
	@Override
	public FilenameFilter getFilenameFilter() {
		return new XmlFilenameFilter();
	}

	class XmlFilenameFilter implements FilenameFilter {
		 public boolean accept(final File dir, final String name) {
			 if(!name.endsWith(".dtd")) {
				 return true;
			 }
			 return false;
		 }
	}

	@Override
	public TYPE getType() {
		return TYPE.IRP;
	}
	
	@Override
	public void parse(final String fileName) throws SAXException, IOException, ParserConfigurationException {
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		final SAXParser parser = spf.newSAXParser();
		int fileLength = 32768;

		try {
			final File theFile = new File(fileName);
			fileLength = Integer.parseInt(Long.toString(theFile.length()));
		} catch (final Exception e) {
			logger.warn(classname + ".parse;", e);
		}
		logger.debug(classname+".parse; Parsing file " + fileName);
		resetParsedStrings();
		parser.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(fileLength));
		parser.parse(fileName, this);
		checkValidTags();
	}
	
	public void checkValidTags()
	{
		if (validTags.contains("mdc") && validTags.contains("mi") && validTags.contains("mv")) {
			counterSetFileLoader.setSuccessfullparsing(true);
		}
		else{
			counterSetFileLoader.setSuccessfullparsing(false);
		}
	}
}
