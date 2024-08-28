package com.ericsson.eniq.etl.twampM;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.eniq.common.ENIQEntityResolver;

public class twampM extends DefaultHandler implements Parser {
	private Logger log;
	private MeasurementFile measFile = null;
	private Map<String, String> measData = null;
	private Map<String, String> sessionData = null;
	private SourceFile sourceFile;
	private String sdcName = null;
	private String tagID = null;
	private String collectionBeginTime = null;
	int GRANULARITY ;// 15 min =(9000000/1000)/60  
	int INTERVALMS ; // 15 min = 15 * 60 * 1000 in mili seconds
	
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	// ***************** Worker stuff ****************************
	private String techPack;
	private String setType;
	private String setName;
	private String workerName = "";

	private int status = 0;

	private Main mainParserObject = null;

	@Override
	public void init(final Main main, final String techPack, final String setType, final String setName,
			final String workerName) {
		this.mainParserObject = main;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.status = 1;
		this.workerName = workerName;

		String logWorkerName = "";
		if (workerName.length() > 0) {
			logWorkerName = "." + workerName;
		}

		log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.twampM" + logWorkerName);

	}

	@Override
	public int status() {
		return status;
	}

	@Override
	public void run() {

		try {
			this.status = 2;
			SourceFile sf = null;
			while ((sf = mainParserObject.nextSourceFile()) != null) {

				try {
					mainParserObject.preParse(sf);
					parse(sf, techPack, setType, setName);
					mainParserObject.postParse(sf);
				} catch (final Exception e) {
					mainParserObject.errorParse(e, sf);
				} finally {
					mainParserObject.finallyParse(sf);
				}
			}
		} catch (final Exception e) {
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		} finally {
			this.status = 3;
		}
	}

	@Override
      public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
                  throws Exception {
            measData = new HashMap<String, String>();
            sessionData = new HashMap<String, String>();
            this.sourceFile = sf;
            log.fine("Parsing Started");
            final String sdcMask = sf.getProperty("vendorIDMask", "(.*)(-)(.*)(-)(.*-.*)(-)(.*)(-)(.*)([+,-])(.*)");
            final String filename = sf.getName();
            sdcName = parseFileName(filename, sdcMask, 1);
            log.fine("sdc name " + sdcName);
            tagID = parseFileName(filename, sdcMask, 5);
            log.fine("tagID is " + tagID);
            final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(this);
            xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
            xmlReader.parse(new InputSource(sf.getFileInputStream()));
            log.fine("Parsing Completed");
      }

	@Override
	public void startElement(final String uri, final String name, final String qName, final Attributes atts)
			throws SAXException {
		String stattime= null;
		String interval =null;
		if (qName.equals("RR")) {
			for (int i = 0; i < atts.getLength(); i++) {
				sessionData.put(atts.getLocalName(i), atts.getValue(i));
			}
		} else if (qName.equals("RR1")) {
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getLocalName(i).equalsIgnoreCase("statTime")) {
					stattime = atts.getValue(i);
					interval = atts.getValue(i+1);
					collectionBeginTime = calculateCollectionBeginTime(stattime,interval);
					measData.put("DATETIME_ID", collectionBeginTime);					 
				}
				
				measData.put(atts.getLocalName(i), atts.getValue(i));
			}
		}
	}

	@Override
	public void endElement(final String uri, final String name, final String qName) {
		try {
			if (qName.equals("RR1")) {
				if (measFile == null) {
					measFile = Main.createMeasurementFile(sourceFile, tagID, techPack, setType, setName,
							this.workerName, this.log);
					log.fine("Created new File with worker" + this.workerName);
				}
				// Adding implementation only for ENIQ supported granularity values
				// EQEV-28696
				// Data not added to file if INTERVALMS value does not match
				log.fine("INTERVALMS value is "+INTERVALMS);
				if (Arrays.asList(60000, 300000, 600000, 900000, 1800000, 3600000, 21600000, 86400000).contains(INTERVALMS))
						{
				log.fine("Valid ROP granularity");
				measFile.addData("Filename", sourceFile.getName());
				measFile.addData("SDC_NAME", sdcName);
				measFile.addData("DIRNAME", sourceFile.getDir());
				measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				measFile.addData(measData);
				measFile.addData(sessionData);
				measFile.saveData();
				log.fine("data is saved in meas file");
						}
				else{
					log.fine("Granularity " + GRANULARITY + "min not supported by ENIQ" );
				}
				measData.clear();
			} else if (qName.equals("RR")) {
				sessionData.clear();

			} else if (qName.equals("Response")) {
				measFile.close();
				measFile = null;
			}
		} catch (final Exception e) {
			log.warning("Exception caught at end element::" + e.getMessage());
		}

	}

	public String parseFileName(final String filename, final String regExp, final int index) {

		final Pattern pattern = Pattern.compile(regExp);
		final Matcher matcher = pattern.matcher(filename);
		if (matcher.matches()) {
			final String result = matcher.group(index);
			log.fine(" regExp (" + regExp + ") found from " + filename + "  :" + result);
			return result;
		} else {
			log.warning("String " + filename + " doesn't match defined regExp " + regExp);
		}
		return "";
	}

	private String calculateCollectionBeginTime(final String value,final String interval) {
		// TODO Auto-generated method stub
		log.fine("statTime is " + value);
		long epochTime = Long.decode(value);
		// Subtracting 15 Min from StatTime
		// SPF-179
		//EQEV-23246
		INTERVALMS =Integer.parseInt(interval);
		log.fine("INTERVALMS  value is "+INTERVALMS);
		epochTime = epochTime - INTERVALMS;		
		final Date date = new Date(epochTime);
		// log.fine(" statime is " + date);
		final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		calendar.setTime(date);
		int min = calendar.get(Calendar.MINUTE);
		//EQEV-23246
		GRANULARITY = (INTERVALMS/1000)/60;
		log.fine("GRANULARITY value is "+GRANULARITY);
		if (min != 0) {
			if (GRANULARITY != 0){							//EQEV-28696 (Div by Zero error correction)
			min = min - (min % GRANULARITY);
		}
		}
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, 00);
		final Date adjustedDate = calendar.getTime();
		final DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		final String formatted = format.format(adjustedDate);
		log.fine("after adjusting date is " + adjustedDate);
		return formatted;
	}
}
