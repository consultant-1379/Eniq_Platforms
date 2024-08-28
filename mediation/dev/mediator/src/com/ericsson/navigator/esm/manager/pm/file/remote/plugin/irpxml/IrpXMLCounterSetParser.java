package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.irpxml;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class IrpXMLCounterSetParser extends DefaultHandler implements CounterSetFileParser {
	
	private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
	private final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final SimpleDateFormat formatter3 = new SimpleDateFormat("yyyyMMddHHmmssZ", Locale.US);
	private final TimeZone timeZone = (new GregorianCalendar()).getTimeZone();
	private Map<String, String> observedCounters;
	
	private List<String> counterNames = new ArrayList<String>();
	private int counterPos = 0;
	private String tag;
	private String nednString = "";
	private String moidString = "";
	private String mtString = null;
	private String rString =null;
	private String mtsString=null;
	private String objectdnString = null;

	CounterSetCallback parseCallback;
	
	public IrpXMLCounterSetParser(){

		formatter1.setTimeZone(timeZone);
		formatter2.setTimeZone(timeZone);
		formatter3.setTimeZone(timeZone);
	}

	void resetParsedStrings() {
		tag = null;
		nednString = "";
		moidString = "";
		mtString = null;
		rString =null;
		mtsString=null;
		objectdnString=null;
	}

	@Override
	public void characters( final char[] ch,  final int start,  final int length) throws SAXException {
		 final String _tag = tag;
		tag = null;
		if ("r".equalsIgnoreCase(_tag)) {
			rString = new String(ch, start, length).trim();
			addObservedCounter(counterPos,rString);
			counterPos++;
		} else if ("mt".equalsIgnoreCase(_tag)) {
			mtString = new String(ch, start, length).trim();
			counterNames.add(mtString);
		} else if ("nedn".equalsIgnoreCase(_tag)) {
			nednString = new String(ch, start, length).trim();
		} else if ("mts".equalsIgnoreCase(_tag)) {				
			mtsString = new String(ch, start, length).trim();
		} else if ("gp".equalsIgnoreCase(_tag)) {
//			gpString = new String(ch, start, length);
		} else if ("moid".equalsIgnoreCase(_tag)) {
			moidString = new String(ch, start, length).trim();
		}
	}
	
	@Override
	public void startElement( final String nsURI,  final String strippedName,
			 final String tagName,  final Attributes managed_object_attribute) throws SAXException {
		tag=tagName.toLowerCase();
		if ("mi".equalsIgnoreCase(tag)){
			counterNames = new ArrayList<String>();
			observedCounters = new HashMap<String, String>();
			return;
		}
		if ("mv".equalsIgnoreCase(tag)){
			counterPos = 0;
			return;
		}
	}

	@Override
	public void endElement( final String uri,  final String localName,  final String qName) {
		if("mv".equalsIgnoreCase(qName)) {
			addCounterSet(parseDate(mtsString));
			moidString = "";
			return;
		}
		if("md".equalsIgnoreCase(qName)) {
			nednString = "";
			moidString = "";
			objectdnString = "";
			return;
		}
	}

	private void addObservedCounter( final int pos, String value) {
		if (value == "") {
			value = "null";
		}
		if (pos < counterNames.size()) {
			observedCounters.put(counterNames.get(pos), value);
		} else {
			parseCallback.debug(".addObservedCounter(); More values than counter names found!");
		}
	}

	@SuppressWarnings( "PMD.CyclomaticComplexity" )
	private long parseDate( final String dateString){
		long endTime = System.currentTimeMillis();
		// Case 1: (+ or -) TIMEZONE
		if ((dateString.indexOf("+") > -1) || (dateString.indexOf("-") > -1)) {
			try {
				// <mts>20060605041000.0+0200</mts>
				//TR-HL84058 - ESMTP: Transformation do not aggregate correctly if PM data comes from different timezones
				String simpleDate = dateString.replaceAll("T", "").trim();
				final int dot = dateString.indexOf(".");
				int sign = 0;
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
			} catch ( final Exception e) {
				parseCallback.error(".parseDate(); Failed to parse date current time will be used.", e);
			}
		}

		try {
			// yyyyMMddHHmmss
			endTime = formatter2.parse(dateString).getTime();
			return endTime;
		} catch ( final Exception e) {
			parseCallback.error(".parseDate; Time format used for mts formatter2: " + e);
		}

		try {
			// yyyyMMddHHmm
			endTime = formatter1.parse(dateString).getTime();
			return endTime;
		} catch ( final Exception e) {
			parseCallback.error(".parseDate; Time format used for mts formatter1", e);
		}
		try {
			endTime = 1000L * Long.parseLong(dateString);
			return endTime;
		} catch ( final Exception e) {
			parseCallback.debug(".parseDate; Time format used for mts utime/EPOCH", e);
		}
		parseCallback.debug(".parseDate; Time format used is not parseable:"+dateString);
		return 0;
	}
	
	private void addCounterSet(long endTime) {
		if (endTime > System.currentTimeMillis()) { // TR-HM51919
			endTime = System.currentTimeMillis();
		}
		parseCallback.pushCounterSet(createMoid(nednString, moidString), new Date(endTime), observedCounters);
	}

	private String createMoid(final String nedn, final String moid) {
		String nednComma = nedn;
		if (!MVMEnvironment.PM3GPPMOIDRETENTION){
			return createMoidWithNednReplaced (nedn,moid);
		}
		if (!nedn.isEmpty() && !moid.isEmpty()) {
			nednComma = nedn + ",";
		}
		if ((nedn + moid).isEmpty()) {
			parseCallback.warn("nedn and moid both are empty");
			nednComma = "";
		}
		if(nedn.isEmpty()){
			parseCallback.warn("nedn is empty");
		}
		return nednComma+moid;
	}

	/**
     * Updates the NEDN string retrieved from the node with FDN. 
     * This is required when the same node name is appearing in 
     * two different parents sub-trees. 
     * Addresses the Alarm correlation as well. 
     * @param nedn
     * @param moid
     * @return
     */
    private String createMoidWithNednReplaced(final String nedn, final String moid) {
	    // TODO Streamline when the 3GPP XML files from node are made available to
    	//north bound. Currently the 3GPP XML files are retrieved, processed(parsed) 
    	//and stored. another routine creates the 3GPP XML file from parsed counters.
    	String replacedNedncomma = nedn;
		if (!nedn.isEmpty() && !moid.isEmpty()) {
			replacedNedncomma = objectdnString + ",";
		}
		if ((nedn + moid).isEmpty()) {
			parseCallback.warn("nedn and moid both are empty");
			replacedNedncomma = "";
		}
		
		if(nedn.isEmpty()){
			parseCallback.warn("nedn is empty");
		}
		return replacedNedncomma+moid;
    }

	@Override
	public File parseFile(final String fdn, final String filePath, final CounterSetCallback callback, boolean doLookup) throws ParserException {		
		try {
			parseCallback = callback;
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setValidating(true);
			final SAXParser parser = spf.newSAXParser();
			int bufferSize = 32768;
			final int size = Integer.parseInt(Long.toString(new File(filePath).length()));
			if(size != 0){
				bufferSize = size;
			}
			callback.debug(".parse; Parsing file " + filePath);
			resetParsedStrings();
			objectdnString = fdn;
			parser.setProperty("http://apache.org/xml/properties/input-buffer-size", bufferSize);
			parser.parse(filePath, this);
		} catch (final Exception e){
			throw new ParserException("", e);
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Default IRP 3GPP XML Counter Set Parser";
	}

	@Override
	public String getContactInformation() {
		return "Navigator, ESM product development team";
	}

	@Override
	public String getDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
