/*
 * Created on 3.1.2008
 *
 */
package com.distocraft.dc5000.etl.xml3GPP32435;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * 3GPP TS 32.435 Parser <br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * <br>
 * Version supported: v 7.20 <br>
 * <br>
 * Copyright Ericsson 2008 <br>
 * <br>
 * $id$ <br>
 * 
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 * <tr>
 * <td>Vendor ID mask</td>
 * <td>3GPP32435Parser.vendorIDMask</td>
 * <td>Defines how to parse the vendorID</td>
 * <td>.+,(.+)=.+</td>
 * </tr>
 * <tr>
 * <td>Vendor ID from</td>
 * <td>3GPP32435Parser.readVendorIDFrom</td>
 * <td>Defines where to parse vendor ID (file/data supported)</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID</td>
 * <td>3GPP32435Parser.FillEmptyMOID</td>
 * <td>Defines whether empty moid is filled or not (true/ false)</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID style</td>
 * <td>3GPP32435Parser.FillEmptyMOIDStyle</td>
 * <td>Defines the style how moid is filled (static/inc supported)</td>
 * <td>inc</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID value</td>
 * <td>3GPP32435Parser.FillEmptyMOIDValue</td>
 * <td>Defines the value for the moid that is filled</td>
 * <td>0</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>collectionBeginTime</td>
 * <td>contains the begin time of the whole collection</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the vendor id parsed from MOID</td>
 * </tr>
 * <tr>
 * <td>MOID</td>
 * <td>contains the measured object id</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>contains the parsed duration of this measurement</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the counted starttime of this measurement</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>contains the suspected flag value</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200)</td>
 * </tr>
 * <tr>
 * <td>vendorName</td>
 * <td>contains the vendor name</td>
 * </tr>
 * <tr>
 * <td>fileFormatVersion</td>
 * <td>contains the version of file format</td>
 * </tr>
 * <tr>
 * <td>dnPrefix</td>
 * <td>contains the dn prefix</td>
 * </tr>
 * <tr>
 * <td>localDn</td>
 * <td>contains the local dn</td>
 * </tr>
 * <tr>
 * <td>managedElementLocalDn</td>
 * <td>contains the local dn of managedElement element</td>
 * </tr>
 * <tr>
 * <td>elementType</td>
 * <td>contains the element type</td>
 * </tr>
 * <tr>
 * <td>userLabel</td>
 * <td>contains the user label</td>
 * </tr>
 * <tr>
 * <td>swVersion</td>
 * <td>contains the software version</td>
 * </tr>
 * <tr>
 * <td>endTime</td>
 * <td>contains the granularity period end time</td>
 * </tr>
 * <tr>
 * <td>measInfoId</td>
 * <td>contains the measInfoId</td>
 * </tr>
 * <tr>
 * <td>jobId</td>
 * <td>contains the jobId</td>
 * </tr>
 * <tr>
 * <td>&lt;measType&gt; (amount varies based on measurement executed)</td>
 * <td>&lt;measValue&gt; (amount varies based on measurement executed)</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * 
 * @author pylkk?nen <br>
 * <br>
 * 
 */
public class Xml3GPP32435Parser extends DefaultHandler implements Parser {

	// Virtual machine timezone unlikely changes during execution of JVM
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * 3GPP 7.20
	 */
	private String fileFormatVersion;
	private String vendorName;
	private String dnPrefix;
	private String fsLocalDN;
	private String elementType;
	private String meLocalDN;
	private String userLabel;
	private String swVersion;
	private String collectionBeginTime;
	// private String collectionEndTime; //received so late, that migth not be used
	private String granularityPeriodDuration;
	private String granularityPeriodEndTime;
	private String repPeriodDuration;
	private String jobId;
	private String measInfoId;
	private HashMap measNameMap;
	private HashMap clusterMap;
	
	// Since special handling of #-mark causes parsing to fail in some cases,
	// let's keep the "original" counterrnames in the map also without special #-handling
	private HashMap<String, String> origMeasNameMap;

	private String suspectFlag = "";
	private String measIndex;
	private String measValueIndex;
	private String measObjLdn;

	private String charValue;

	private SourceFile sourceFile;

	private String objectClass;

	private String objectMask;

	private String matchObjectMaskAgainst;

	private String readVendorIDFrom;

	private boolean fillEmptyMoid = true;

	private String fillEmptyMoidStyle = "";

	private String fillEmptyMoidValue = "";

	// private Map measurement;

	private String oldObjClass;

	private MeasurementFile measFile = null;
	
	private MeasurementFile vectorMeasFile = null;

	private Logger log;

	private String techPack;

	private String setType;

	private String setName;

	private int status = 0;

	private Main mainParserObject = null;

	private String workerName = "";

	final private List errorList = new ArrayList();

	private boolean hashData = false;

	private boolean dataFormatNotFound = false;

	private String interfaceName="";

	private HashMap<String, HashMap<String, HashMap<String, String>>> loaderClass;

	private HashMap<String, HashMap<String, String>> moidMap;

	private HashMap<String, String> counterMap;

	private HashMap<String, String> objectMap;

	private HashMap<String, String> counterValueMap;

	private HashMap<String, HashMap<String, String>> dataMap;
	
	private Map<String, Map<String, String>> vectorBinValueMap = new HashMap<String, Map<String,String>>();
	
	private HashMap<String,  Map<String, Map<String, String>>> vectorData =new HashMap<String,  Map<String, Map<String, String>>>();
	
    private String rangeCounterTag="VECTOR";

    private String cmVectorTag ="CMVECTOR";


	private String rangeColunName = ""; // name of the range column

	private String rangePostfix = ""; // string that is added to the
	
	private String compressedVectorTag="COMPRESSEDVECTOR";
	
	private Set compressedVectorCounters;
	  
	private boolean createOwnVectorFile = true;
	
	private String keyColumnTag="KEY";
	
	private String vectorPostfix = null;
	
	private Map measurement;
	
	final static private String delimiter = ",";
	
	private String measInfoIdPattern = ".*,.*=(.*)";
	
	 DataFormatCache dfc = DataFormatCache.getCache();
	 
	 //For L17A Differentiated Observability Flex Counter handling
	 private String flexPostfix = null;						//Post-fix to MOID for Flex tables
	 private boolean hasFlexCounters = false;				//If flex counters not required
	 private boolean createOwnFlexFile = true;				//To keep it consistent with vector implementation/future impact
	 private MeasurementFile flexMeasFile = null;
	 private String flexCounterTag;							//Tag to differentiate flex counters in techpack
	 private HashMap flexValueMap;							//Map to store flex counter values
	 private HashMap flexFilterMap;							//Map to arrange data per filter
	 private HashMap flexMoidMap;							//Map to arrange data per MOID
	 private Map flexCounterBin;							//Map to store list of Flex counters in an MOID tag
	  
	/**
   * 
   */
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

		log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.xml3GPP32435Parser"
				+ logWorkerName);
	}

	@Override
	public int status() {
		return status;
	}

	public List errors() {
		return errorList;
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
			// Exception catched at top level. No good.
			log.log(Level.WARNING, "Worker parser failed to exception", e);
			errorList.add(e);
		} finally {
			this.status = 3;
		}
	}

	/**
   * 
   */
	@Override
	public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
			throws Exception {
		if (measFile != null) {
			try {
				log.log(Level.FINEST, "Closing Meas File");
				measFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing measurement file");
			}
		}
		if (vectorMeasFile != null) {
			try {
				log.log(Level.FINEST, "Closing vectorMeasFile File");
				vectorMeasFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing measurement file");
			}
			
		}
		if (flexMeasFile != null) {
			try {
				log.log(Level.FINEST, "Closing flexMeasFile File");
				flexMeasFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing flex measurement file");
			}
			
		}
		this.measFile = null;
		final long start = System.currentTimeMillis();
		this.sourceFile = sf;
		objectMask = sf.getProperty("x3GPPParser.vendorIDMask", ".+,(.+)=.+");
		matchObjectMaskAgainst = sf.getProperty("x3GPPParser.matchVendorIDMaskAgainst", "subset");
		readVendorIDFrom = sf.getProperty("x3GPPParser.readVendorIDFrom",sf.getProperty("3GPP32435Parser.readVendorIDFrom", null));
		if ((null == readVendorIDFrom) || (readVendorIDFrom == "")) {
			readVendorIDFrom = sf.getProperty("x3GPPParser.readVendorIDFrom", "data");
		}
		fillEmptyMoid = "true".equalsIgnoreCase(sf.getProperty("x3GPPParser.FillEmptyMOID", "true"));
		fillEmptyMoidStyle = sf.getProperty("x3GPPParser.FillEmptyMOIDStyle", "inc");
		fillEmptyMoidValue = sf.getProperty("x3GPPParser.FillEmptyMOIDValue", "0");
		hashData = "true".equalsIgnoreCase(sf.getProperty("x3GPPParser.HashData",sf.getProperty("3GPP32435Parser.HashData", "false")));		
		interfaceName = sf.getProperty("interfaceName", "");
		rangePostfix = sf.getProperty("x3GPPParser.RangePostfix",sf.getProperty("3GPP32435Parser.RangePostfix", "_DCVECTOR"));
	    rangeColunName = sf.getProperty("x3GPPParser.RangeColumnName",sf.getProperty("3GPP32435Parser.RangeColumnName", "DCVECTOR_INDEX"));
	    rangeCounterTag = sf.getProperty("x3GPPParser.RangeCounterTag",sf.getProperty("3GPP32435Parser.RangeCounterTag", "VECTOR"));
	    cmVectorTag = sf.getProperty("x3GPPParser.cmVectorTag",sf.getProperty("3GPP32435Parser.cmVectorTag", "CMVECTOR"));

		compressedVectorTag = sf.getProperty("x3GPPParser.compressedVectorTag",sf.getProperty("3GPP32435Parser.compressedVectorTag", "COMPRESSEDVECTOR"));
	    compressedVectorCounters = new HashSet();
   	    keyColumnTag = sf.getProperty("x3GPPParser.KeyColumnTag",sf.getProperty("3GPP32435Parser.KeyColumnTag", "KEY"));
	    vectorPostfix = sf.getProperty("x3GPPParser.VectorPostfix",sf.getProperty("3GPP32435Parser.VectorPostfix", "_V"));
	    
	    //Flex counter initializations before parse
	    flexPostfix = sf.getProperty("x3GPPParser.flexPostfix", "_FLEX");
	    hasFlexCounters = "true".equalsIgnoreCase(sf.getProperty("x3GPPParser.hasFlexCounters", "false"));
	    createOwnFlexFile = "true".equalsIgnoreCase(sf.getProperty("x3GPPParser.createOwnFlexFile", "false"));
	    flexCounterTag = sf.getProperty("x3GPPParser.flexCounterTag", "FlexCounter");
	    
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		// spf.setValidating(validate);

		final SAXParser parser = spf.newSAXParser();
		final XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);

		xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
		final long middle = System.currentTimeMillis();
		xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));
		final long end = System.currentTimeMillis();
		log.log(Level.FINER, "Data parsed. Parser initialization took " + (middle - start) + " ms, parsing "
				+ (end - middle) + " ms. Total: " + (end - start) + " ms.");
		oldObjClass = null;
	}

	/**
   * 
   */
	public void parse(final FileInputStream fis) throws Exception {

		final long start = System.currentTimeMillis();
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		// spf.setValidating(validate);
		final SAXParser parser = spf.newSAXParser();
		final XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		final long middle = System.currentTimeMillis();
		xmlReader.parse(new InputSource(fis));
		final long end = System.currentTimeMillis();
		log.log(Level.FINEST, "Data parsed. Parser initialization took " + (middle - start) + " ms, parsing "
				+ (end - middle) + " ms. Total: " + (end - start) + " ms.");
	}

	public HashMap strToMap(final String str) {

		final HashMap hm = new HashMap();
		int index = 0;
		if (str != null) {

			// list all triggers
			final StringTokenizer triggerTokens = new StringTokenizer(str, " ");
			while (triggerTokens.hasMoreTokens()) {
				index++;
				hm.put("" + index, triggerTokens.nextToken());
			}
		}

		return hm;
	}

	private String printAttributes(final Attributes atts) {
		String line = "";
		int i;
		if (atts == null) {
			return "";
		}
		for (i = 0; i < atts.getLength(); ++i) {
			line += "[" + atts.getType(i) + "," + atts.getValue(i) + "]";
		}
		return line;
	}

	/**
	 * Event handlers
	 */
	@Override
	public void startDocument() {
	}

	@Override
	public void endDocument() throws SAXException {
		
		if (measFile != null) {
			try {
				log.log(Level.FINEST, "Closing Meas File");
				measFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing measurement file");
			}
		}
		if (vectorMeasFile != null) {
			try {
				log.log(Level.FINEST, "Closing vectorMeasFile File");
				vectorMeasFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing measurement file");
			}
			
		}
		if (flexMeasFile != null) {
			try {
				log.log(Level.FINEST, "Closing flexMeasFile File");
				flexMeasFile.close();
			} catch (final Exception e) {
				log.log(Level.FINEST, "Worker parser failed to exception", e);
				throw new SAXException("Error closing flex measurement file");
			}
			
		}
		
	}

	@Override
	public void startElement(final String uri, final String name, final String qName, final Attributes atts)
			throws SAXException {

		charValue = "";

		if (qName.equals("fileHeader")) {
			this.fileFormatVersion = atts.getValue("fileFormatVersion");
			this.vendorName = atts.getValue("vendorName");
			this.dnPrefix = atts.getValue("dnPrefix");
		} else if (qName.equals("fileSender")) {
			this.fsLocalDN = atts.getValue("localDn");
			this.elementType = atts.getValue("elementType");
		} else if (qName.equals("measCollec")) {
			if (atts.getValue("beginTime") != null) {
				// header
				collectionBeginTime = atts.getValue("beginTime");
			} else if (atts.getValue("endTime") != null) {
				// footer
			}
		} else if (qName.equals("measData")) {
			measNameMap = new HashMap();
			clusterMap = new HashMap();

			origMeasNameMap = new HashMap<String, String>();
			
			if (hashData) {
				loaderClass = new HashMap<String, HashMap<String, HashMap<String, String>>>();
				objectMap = new HashMap<String, String>();
				dataMap = new HashMap<String, HashMap<String, String>>();
				flexMoidMap = new HashMap();
			}

		} else if (qName.equals("managedElement")) {
			this.meLocalDN = atts.getValue("localDn");
			this.userLabel = atts.getValue("userLabel");
			this.swVersion = atts.getValue("swVersion");
		} else if (qName.equals("measInfo")) {
			this.measInfoId = atts.getValue("measInfoId");
			//This is modified for JIRA EQEV-35781
			if(this.measInfoId != null){
				//This is modified for JIRA EQEV-35535
				if (measInfoId.contains("=")) {
					//measInfoId = measInfoId.substring(measInfoId.lastIndexOf("=") + 1, measInfoId.length());
					measInfoId = parseFileName(measInfoId, measInfoIdPattern);
				}
				}

			if(hashData && readVendorIDFrom.equals("measInfoId") ){

				getLoaderName(measInfoId);
							}
		} else if (qName.equals("job")) {
			this.jobId = atts.getValue("jobId");
		} else if (qName.equals("granPeriod")) {
			granularityPeriodDuration = getSeconds(atts.getValue("duration"));
			granularityPeriodEndTime = atts.getValue("endTime");
		} else if (qName.equals("repPeriod")) {
			repPeriodDuration = getSeconds(atts.getValue("duration"));
		} else if (qName.equals("measTypes")) {
		} else if (qName.equals("measType")) {
			measIndex = atts.getValue("p");
		} else if (qName.equals("measValue")) {
			this.suspectFlag = "";
			this.measObjLdn = atts.getValue("measObjLdn");

			handleTAGmoid(measObjLdn);
			if (hashData && readVendorIDFrom.equalsIgnoreCase("measInfoId")) {
				counterValueMap = new HashMap<String, String>();
				flexFilterMap = new HashMap();
			} else if (hashData
					&& (readVendorIDFrom.equalsIgnoreCase("measObjLdn") || readVendorIDFrom
							.equalsIgnoreCase("data"))) {
				counterValueMap = new HashMap<String, String>();
				flexFilterMap = new HashMap();
				getLoaderName(objectClass);
				

			} else {
				try {
					if (sourceFile != null) {
						if ((oldObjClass == null) || !oldObjClass.equals(objectClass)) {
							// close meas file
							if (measFile != null) {
								log.log(Level.FINEST, "R:measFile not null" );
								measFile.close();
							}if(vectorMeasFile != null){
								log.log(Level.FINEST, "R:vectormeasFile not null" );
								vectorMeasFile.close();
							}
							if(flexMeasFile != null)
							{
								log.log(Level.FINEST, "R:flexMeasFile not null" );
								flexMeasFile.close();
							}
							// create new measurementFile
							measFile = Main.createMeasurementFile(sourceFile, objectClass, techPack, setType, setName,
									workerName, log);
							
							  DFormat df = dfc.getFormatWithTagID(interfaceName, objectClass+vectorPostfix);
							  
							  if(df!=null){
							vectorMeasFile = Main.createMeasurementFile(sourceFile, objectClass+vectorPostfix, techPack, setType, setName,
									workerName, log);
							  }
							  if(hasFlexCounters)
							  {
								  df = dfc.getFormatWithTagID(interfaceName, objectClass + flexPostfix);
								  if(createOwnFlexFile && df != null)
								  {
									  flexMeasFile = Main.createMeasurementFile(sourceFile, objectClass + flexPostfix, techPack, setType, setName,workerName, log);
								  }
							  }
							oldObjClass = objectClass;
														
						}					}
				} catch (final Exception e) {
					log.log(Level.FINEST, "Error opening measurement data", e);
					e.printStackTrace();
					throw new SAXException("Error opening measurement data: " + e.getMessage(), e);
				}
			}
		} else if (qName.equals("measResults")) {
		} else if (qName.equals("r")) {
			this.measValueIndex = atts.getValue("p");
		} else if (qName.equals("suspect")) {
		} else if (qName.equals("fileFooter")) {
		}
	}

	private void getLoaderName(final String tagId) { String loaderName = null;
	  DataFormatCache dfc = DataFormatCache.getCache();
	  DFormat df = dfc.getFormatWithTagID(interfaceName, tagId);
	  
	  if(df==null){
		  df = dfc.getFormatWithTagID(interfaceName, tagId+vectorPostfix);
		  if(df==null)
		  {
			  log.finer("Dataformat not found");
			  dataFormatNotFound = true;
		  }
		  else{
			  loaderName = df.getFolderName();

				objectMap.put(tagId, loaderName);

				if (!loaderClass.containsKey(loaderName)) {

					moidMap = new HashMap<String, HashMap<String, String>>();
					loaderClass.put(loaderName, moidMap);

				} else {
					moidMap = loaderClass.get(loaderName);
				}
		  }
		  
		  objectMap.put(tagId, loaderName);
		  
		  if(!loaderClass.containsKey(loaderName)){
			   
				 moidMap = new HashMap<String,HashMap<String,String>>();
				 loaderClass.put(loaderName, moidMap);
				 
		  }
		  else{
			  moidMap = loaderClass.get(loaderName);
		  }
		  
		  
		} else {

			loaderName = df.getFolderName();

			objectMap.put(tagId, loaderName);

			if (!loaderClass.containsKey(loaderName)) {

				moidMap = new HashMap<String, HashMap<String, String>>();
				loaderClass.put(loaderName, moidMap);

			} else {
				moidMap = loaderClass.get(loaderName);
			}
	  }
}

	private void handleTAGmoid(String value) {

		this.objectClass = "";

		if ("file".equalsIgnoreCase(readVendorIDFrom)) {
			objectClass = parseFileName(sourceFile.getName(), objectMask);

		} else if ("data".equalsIgnoreCase(readVendorIDFrom) || "measObjLdn".equalsIgnoreCase(readVendorIDFrom)) {

			// if moid is empty and empty moids are filled.
			if (fillEmptyMoid && (value.length() <= 0)) {
				if (fillEmptyMoidStyle.equalsIgnoreCase("static")) {
					value = fillEmptyMoidValue;
				} else {
					value = measValueIndex + "";
				}
			}

			// read vendor id from data
			objectClass = parseFileName(value, objectMask);

		} else if ("measInfoId".equalsIgnoreCase(readVendorIDFrom)) {
			objectClass = this.measInfoId;
		}	 
		 if(hasFlexCounters)
		 {
			 final DFormat df = dfc.getFormatWithTagID(interfaceName, objectClass + flexPostfix);
			 if(df != null)
			 {
				 final Set flexCounters = getDataIDFromProcessInstructions(interfaceName, objectClass + flexPostfix, flexCounterTag);
				 final Set keyColumns = getDataIDFromProcessInstructions(interfaceName, objectClass + flexPostfix, keyColumnTag);
				 flexCounterBin = new HashMap();
				 flexCounterBin.put("filename", sourceFile.getName());
				 flexCounterBin.put("flexCounters", flexCounters);
				 flexCounterBin.put("keyColumns", keyColumns);
			 }
		 }
	      final DFormat df = dfc.getFormatWithTagID(interfaceName, objectClass+vectorPostfix);
	    if(df!=null){
		log.log(Level.FINEST,"T:ObjectClass:"+objectClass);
		    // VECTOR counters
	    final Set rangeCounters = getDataIDFromProcessInstructions(interfaceName, objectClass+vectorPostfix, rangeCounterTag);
	    log.log(Level.FINEST,"T:ObjectClass:"+objectClass+", vector Counters = " + rangeCounters);
	    
	    compressedVectorCounters.addAll(getDataIDFromProcessInstructions(interfaceName, objectClass+vectorPostfix, compressedVectorTag));
	  
	    rangeCounters.addAll(compressedVectorCounters);
	   	    // we search instructions also from objectClass+vectorPostfix
	  
	    final Set keyColumns = getDataIDFromProcessInstructions(interfaceName, objectClass+vectorPostfix, keyColumnTag);

	    // we search instructions also from objectClass+vectorPostfix
	    
	      keyColumns.addAll(getDataIDFromProcessInstructions(interfaceName, objectClass + vectorPostfix, keyColumnTag));


	   // CMVECTOR counters
		    final Set cmVectorCounters = getDataIDFromProcessInstructions(interfaceName, objectClass+vectorPostfix, cmVectorTag);
	     
		    cmVectorCounters
		      .addAll(getDataIDFromProcessInstructions(interfaceName, objectClass + vectorPostfix, cmVectorTag));
    
	    // new measurement started
	    measurement = new HashMap();
	    measurement.put("filename", sourceFile.getName());
	    measurement.put("rangeCounters", rangeCounters);
	    measurement.put("cmVectorCounters", cmVectorCounters);
	    measurement.put("keyColumns", keyColumns);

		   
 	}
	}
	
	private Set getDataIDFromProcessInstructions(final String interfacename, final String objectClass, final String key) {
	    final Set result = new HashSet();
	    try {

	      final DFormat df = dfc.getFormatWithTagID(interfacename, objectClass);

	      final List dItemList = df.getDitems();

	      final Iterator iter = dItemList.iterator();
	      while (iter.hasNext()) {

	        final DItem di = (DItem) iter.next();
	        if (di.getProcessInstruction() != null) {
	          final StringTokenizer token = new StringTokenizer(di.getProcessInstruction(), ",");
	          while (token.hasMoreElements()) {
	            final String t = (String) token.nextElement();

	            if (t.equalsIgnoreCase(key)) {
	              result.add(di.getDataID());
	            }
	          }
	        }
	      }

	      	if(result.size()==0){
	      		log.log(Level.FINEST, "ResultSet not updated");
	      	}else{
	      		log.log(Level.FINEST, "ResultSet  updated");
	      	}
	    } catch (Exception e) {
	      log.warning("Error while retrieving DataIDs from ProcessInstructions");

	    }

	    return result;

	  }
	
	 final private Map getKeyCounters(final Map datarow) {

		    final Set keyColumns = ((Set) datarow.get("keyColumns"));

		    final HashMap keyMap = new HashMap();

		    // create map that contains all keys to be added to every new datarow
		    final Iterator keyIter = keyColumns.iterator();
		    // loop all key columns
		    while (keyIter.hasNext()) {
		      final String key = (String) keyIter.next();
		      // add key columns from original datarow.
		      keyMap.put(key, datarow.get(key));
		    }

		    return keyMap;
		  }
	/**
	 * Rips PT and S values off from the value.
	 * 
	 * @param value
	 *            Contains the duration value
	 * @return the duration in seconds
	 */
	private String getSeconds(final String value) {
		String result = null;
		if (value != null) {
			result = value.substring(2, value.indexOf('S'));
		}
		return result;
	}

	private String nameField = "";
	private String clusterField = "";

	private String extractCounterName(final String counterName) {
		final int index1 = counterName.indexOf("#");
		final int index2 = counterName.indexOf(".", index1);
		if (index1 >= 0) {
			if (index2 > index1) { // Format NAME#cluster.NAME -> NAME.NAME and Cluster
				nameField = counterName.substring(0, index1) + counterName.substring(index2, counterName.length());
				clusterField = counterName.substring(index1 + 1, index2);
			} else { // Format NAME#Cluster -> NAME and Cluster
				nameField = counterName.substring(0, index1);
				clusterField = counterName.substring(index1 + 1, counterName.length());
			}
		} else { // Format NAME -> NAME
			nameField = counterName;
			clusterField = "";
		}
		return nameField;
	}

	@Override
	public void endElement(final String uri, final String name, final String qName) throws SAXException {
		// log.log(Level.FINEST, "endElement(" + uri + "," + name + "," + qName + "," + charobjectClass + ")");

		if (qName.equals("fileHeader")) {
		} else if (qName.equals("fileSender")) {
		} else if (qName.equals("measCollec")) {
		} else if (qName.equals("measData")) {
			if (hashData) {
				handleTAGMeasData();
			}
		} else if (qName.equals("managedElement")) {
		} else if (qName.equals("measInfo")) {
				dataFormatNotFound = false;
		} else if (qName.equals("job")) {
		} else if (qName.equals("granPeriod")) {
		} else if (qName.equals("repPeriod")) {
		} else if (qName.equals("measTypes")) {
			measNameMap = strToMap(charValue);
		} else if (qName.equals("measType")) {

						
			measNameMap.put(measIndex, extractCounterName(charValue));
			clusterMap.put(measIndex, clusterField);

			origMeasNameMap.put(measIndex, charValue);
			
		} else if (qName.equals("measValue") && !dataFormatNotFound) {
			if (hashData) {
 				handleMoidMap(measObjLdn);
			} else {
				try {

					if (measFile == null && vectorMeasFile == null && flexMeasFile == null) {
						System.err.println("Measurement file null");
						log.log(Level.FINEST, "PERIOD_DURATION: " + granularityPeriodDuration);
						log.log(Level.FINEST, "repPeriodDuration: " + repPeriodDuration);
						// DATETIME_ID calculated from end time
						final String begin = calculateBegintime();
						if (begin != null) {
							log.log(Level.FINEST, "DATETIME_ID: " + begin);
						}
						log.log(Level.FINEST, "collectionBeginTime: " + collectionBeginTime);
						log.log(Level.FINEST, "DC_SUSPECTFLAG: " + suspectFlag);
						log.log(Level.FINEST, "filename: " + (sourceFile == null ? "dummyfile" : sourceFile.getName()));
						log.log(Level.FINEST, "JVM_TIMEZONE: " + JVM_TIMEZONE);
						log.log(Level.FINEST, "DIRNAME: " + (sourceFile == null ? "dummydir" : sourceFile.getDir()));
						log.log(Level.FINEST, "measInfoId: " + measInfoId);
						log.log(Level.FINEST, "MOID: " + measObjLdn);
						log.log(Level.FINEST, "objectClass: " + objectClass);
						log.log(Level.FINEST, "vendorName: " + vendorName);
						log.log(Level.FINEST, "fileFormatVersion: " + fileFormatVersion);
						log.log(Level.FINEST, "dnPrefix: " + dnPrefix);
						log.log(Level.FINEST, "localDn: " + fsLocalDN);
						log.log(Level.FINEST, "managedElementLocalDn: " + meLocalDN);
						log.log(Level.FINEST, "elementType: " + elementType);
						log.log(Level.FINEST, "userLabel: " + userLabel);
						log.log(Level.FINEST, "swVersion: " + swVersion);
						// collectionEndTime received so late, that migth not be used
						log.log(Level.FINEST, "endTime: " + granularityPeriodEndTime);
						log.log(Level.FINEST, "jobId: " + jobId);
					} else {
						measFile.addData("DC_SUSPECTFLAG", suspectFlag);
						measFile.addData("MOID", measObjLdn);
						measFile.addData(addValues());
						measFile.saveData();
						
						if(createOwnFlexFile && hasFlexCounters && flexMeasFile != null)
						{
							flexMeasFile.addData("DC_SUSPECTFLAG", suspectFlag);
							flexMeasFile.addData("MOID", measObjLdn);
							flexMeasFile.addData(addValues());
							flexMeasFile.saveData();
						}
						
						if(vectorMeasFile != null){
						int maxBinSize=0;
						log.log(Level.FINEST, "E:StoreMap " + vectorBinValueMap);
						for(String counterName : vectorBinValueMap.keySet()) {
					      int binSize= vectorBinValueMap.get(counterName).size();
					      if(binSize > maxBinSize){
					    	  maxBinSize =binSize;
					      }
					       
						}
						final String begin1 = calculateBegintime();
						String checkZero = "NIL";
						for(int i=0;i<maxBinSize;i++){
							String binIndex= i+"";
							int flag=0;
							Map<String,String> value =new HashMap<String, String>();
							for(String counterName : vectorBinValueMap.keySet()){
								value = vectorBinValueMap.get(counterName);	
								
								if(value.containsKey(binIndex)&& binIndex!=null){
									if(flag==0){
										flag=1;
									}
									if(checkZero.equals(value.get(binIndex))){
										vectorMeasFile.addData(counterName,null);
									}else{
										vectorMeasFile.addData(counterName,value.get(binIndex));
									}
								log.log(Level.FINEST ,"Q:CounterName" + counterName +"Value" +value.get(binIndex));
								log.finest("DCVECTOR_INDEX"+binIndex);
								vectorMeasFile.addData("DCVECTOR_INDEX", binIndex);
								 vectorMeasFile.addData(counterName+rangePostfix, binIndex);
								 vectorMeasFile.addData("MOID",measObjLdn);
								 vectorMeasFile.addData("DC_SUSPECTFLAG", suspectFlag);
									vectorMeasFile.addData(addValues());
									
							 }
								
							}
							try {
								if(flag==1)
								vectorMeasFile.saveData();
							} catch (final Exception e) {
								log.log(Level.WARNING, "Error saving measurement data", e);
								e.printStackTrace();
								throw new SAXException("Error saving measurement data: " + e.getMessage(), e);

							}
				}
						
						
						Map<String,String> value =new HashMap<String, String>();
						for(String counterName : vectorBinValueMap.keySet()){
							value = vectorBinValueMap.get(counterName);	
							Iterator iterator = value.keySet().iterator();
						      while (iterator.hasNext()) {
						       String i1= (String) iterator.next();
						       if(Integer.parseInt(i1)> maxBinSize){
						    	   if(value.containsKey(i1)&& i1!=null && value.get(i1)!=null){
										if(checkZero.equals(value.get(i1))){
											vectorMeasFile.addData(counterName,null);
										}else{
											vectorMeasFile.addData(counterName,value.get(i1));
										}
											log.log(Level.FINEST ,"Q:CounterName" + counterName+"Value" +value.get(i1));
											vectorMeasFile.addData("DCVECTOR_INDEX", i1);
										    vectorMeasFile.addData(counterName+rangePostfix, i1);
										    vectorMeasFile.addData("MOID",measObjLdn);
									        vectorMeasFile.addData("DC_SUSPECTFLAG", suspectFlag);
											vectorMeasFile.addData(addValues());

											try {
												vectorMeasFile.saveData();
											} catch (final Exception e) {
												log.log(Level.WARNING, "Error saving measurement data", e);
												e.printStackTrace();
												throw new SAXException("Error saving measurement data: " + e.getMessage(), e);

											}
						    	   }
						        }
						      }
						      
						}   
												
						}    
						vectorBinValueMap.clear();
					}

				} catch (final Exception e) {
					log.log(Level.FINEST, "Error saving measurement data", e);
					e.printStackTrace();
					throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
				}
			}
		} else if (qName.equals("measResults")) {
			final Map measValues = strToMap(charValue);
			if (measValues.keySet().size() == measNameMap.keySet().size()) {
				final Iterator it = measValues.keySet().iterator();
				while (it.hasNext()) {
					final String s = (String) it.next();
					String origValue = (String) measValues.get(s);
					if ((origValue != null) && ( origValue.equalsIgnoreCase("NIL")  || origValue.trim().equalsIgnoreCase("")) ) {
						origValue = null;
						log.finest("Setting the value to null as in-valid data being received from the Node");
					}
					if (hashData) {
						boolean isFlexVal = false;
						if(hasFlexCounters && flexCounterBin != null)
						{
							isFlexVal = checkIfFlex((String) measNameMap.get(s));
						}
						if(!isFlexVal)
						{
							counterValueMap.put((String) measNameMap.get(s), origValue);
							log.log(Level.FINEST, (String) measNameMap.get(measValueIndex) + ": " + origValue);
						}
					} else {
						if (measFile == null) {
							System.out.println((String) measNameMap.get(s) + ": " + origValue);
						} else {
							measFile.addData((String) measNameMap.get(s), origValue);
							log.log(Level.FINEST, (String) measNameMap.get(s) + ": " + origValue);
						}
					}
				}
			} else {
				log.warning("Data contains one or more r-tags than mt-tags");
			}
		} else if (qName.equals("r") && !dataFormatNotFound) {
			if (hashData) {
				if (measNameMap.get(measValueIndex) != null) {
					String origValue = charValue;
					boolean isFlexCounter = false;
					if(hasFlexCounters && flexCounterBin != null)
					{
						isFlexCounter = checkIfFlex((String) measNameMap.get(measValueIndex));
					}
					if ((origValue!=null ))
					{
				        Pattern whitespace = Pattern.compile("\\s+");
				        Matcher matcher = whitespace.matcher(origValue);
				        origValue = matcher.replaceAll("");
					}
					if ((origValue != null) && ( origValue.equalsIgnoreCase("NIL")  || origValue.trim().equalsIgnoreCase("")) ) {
						origValue = null;
						log.finest("Hashdata: Setting the value to null as in-valid data being received from the Node");
					}
					
					if (origValue != null) {
					if(origValue.contains(",")){
						 final Map keyCounters = new HashMap();//getKeyCounters(measurement);
						 final String counterName = origMeasNameMap.get(measValueIndex);
						 Map<String,String> tmpMap = handleVectorCounters( measurement, keyCounters,counterName,origValue);
						 vectorBinValueMap.put(counterName, tmpMap);
		    		}
					else if(!isFlexCounter)
					{
					counterValueMap.put((String) measNameMap.get(measValueIndex), origValue);
					counterValueMap.put("clusterId", (String) clusterMap.get(measValueIndex));

					counterValueMap.put(origMeasNameMap.get(measValueIndex), origValue);

					log.log(Level.FINEST, (String) measNameMap.get(measValueIndex) + ": " + origValue);
					log.log(Level.FINEST, origMeasNameMap.get(measValueIndex) + ": " + origValue);
		    		}
				} }else {
					log.warning("Data contains one or more r-tags than mt-tags");
				}
			} else {
				if (measNameMap.get(measValueIndex) != null) {
					boolean isFlexCounter = false;
					if(hasFlexCounters && flexCounterBin != null)
					{
						isFlexCounter = checkIfFlex((String) measNameMap.get(measValueIndex));
					}
					
					String origValue = charValue;
					if ((origValue!=null ))
					{
					Pattern whitespace = Pattern.compile("\\s+");
			        Matcher matcher = whitespace.matcher(origValue);
			        origValue = matcher.replaceAll("");
					}
					if ((origValue != null) && ( origValue.equalsIgnoreCase("NIL") || origValue.trim().equalsIgnoreCase("")) ) {
						origValue = null;
						log.finest(" else block : Setting the value to null as in-valid data being received from the Node");
					}
									
					if (measFile == null) {
						System.out.println((String) measNameMap.get(measValueIndex) + ": " + origValue + " clusterId: "
								+ (String) clusterMap.get(measValueIndex));
						log.finest("origValue ===="+origValue);
						
					}  else if (origValue != null) {
							
							String vector= origValue;
							if(vector.contains(",")){
								 final Map keyCounters = getKeyCounters(measurement);
								 final String counterName = origMeasNameMap.get(measValueIndex);
								 								 
								 Map<String,String> tmpMap = handleVectorCounters( measurement, keyCounters,counterName,vector);
								 vectorBinValueMap.put(counterName, tmpMap);
						
				    		}
							else if(hasFlexCounters &&  isFlexCounter && flexFilterMap != null)
							{
								final Iterator flexIter	= flexFilterMap.keySet().iterator();
								while (flexIter.hasNext())
								{
									flexMeasFile.addData((Map) flexFilterMap.get(flexIter.next()));
								}
							}
							else{
				    			log.log(Level.FINEST, "value" + ":"+origValue);
								measFile.addData((String) measNameMap.get(measValueIndex), origValue);
								measFile.addData("clusterId", (String) clusterMap.get(measValueIndex));
								measFile.addData(origMeasNameMap.get(measValueIndex), origValue);
								
							}
				
					}
				} else {
					log.warning("Data contains one or more r-tags than mt-tags");
				}
			}
					
		} else if (qName.equals("suspect")) {
			this.suspectFlag = charValue;
		} else if (qName.equals("fileFooter")) {
		}
	}

	private void handleMoidMap(String moid) {

		log.log(Level.FINEST, "Loader Name: " + moidMap.size());
		moid = moid.toUpperCase();
	
		if (moidMap.containsKey(moid)) {
			counterMap = moidMap.get(moid);
			log.log(Level.FINEST, "Loader Name: " + moidMap.get(moid));
			counterMap.putAll(counterValueMap);
			moidMap.put(moid, counterMap);
		} else {
			moidMap.put(moid, counterValueMap);
		}
		
		if(hasFlexCounters && flexFilterMap != null)
		{
			 flexMoidMap.put(moid, flexFilterMap);			
		}
		final HashMap<String, String> rowMap = new HashMap<String, String>();
		
		Iterator iter = moidMap.entrySet().iterator();
		if(iter == null){
			log.log(Level.FINEST, "Loader Name:is null");
		}
	  
		
		rowMap.put("PERIOD_DURATION", granularityPeriodDuration);
		log.log(Level.FINEST, "PERIOD_DURATION: " + granularityPeriodDuration);
		rowMap.put("repPeriodDuration", repPeriodDuration);
		log.log(Level.FINEST, "repPeriodDuration: " + repPeriodDuration);
		// DATETIME_ID calculated from end time
		final String begin = calculateBegintime();
		if (begin != null) {
			rowMap.put("DATETIME_ID", begin);
			log.log(Level.FINEST, "DATETIME_ID: " + begin);
		}
		rowMap.put("collectionBeginTime", collectionBeginTime);
		log.log(Level.FINEST, "collectionBeginTime: " + collectionBeginTime);
		rowMap.put("DC_SUSPECTFLAG", suspectFlag);
		log.log(Level.FINEST, "DC_SUSPECTFLAG: " + suspectFlag);
		rowMap.put("filename", (sourceFile == null ? "dummyfile" : sourceFile.getName()));
		log.log(Level.FINEST, "filename: " + (sourceFile == null ? "dummyfile" : sourceFile.getName()));
		rowMap.put("JVM_TIMEZONE", JVM_TIMEZONE);
		log.log(Level.FINEST, "JVM_TIMEZONE: " + JVM_TIMEZONE);
		rowMap.put("DIRNAME", (sourceFile == null ? "dummydir" : sourceFile.getDir()));
		log.log(Level.FINEST, "DIRNAME: " + (sourceFile == null ? "dummydir" : sourceFile.getDir()));
		rowMap.put("measInfoId", measInfoId);
		log.log(Level.FINEST, "measInfoId: " + measInfoId);
		rowMap.put("MOID", measObjLdn);
		log.log(Level.FINEST, "MOID: " + measObjLdn);
		rowMap.put("objectClass", objectClass);
		log.log(Level.FINEST, "objectClass: " + objectClass);
		rowMap.put("vendorName", vendorName);
		log.log(Level.FINEST, "vendorName: " + vendorName);
		rowMap.put("fileFormatVersion", fileFormatVersion);
		log.log(Level.FINEST, "fileFormatVersion: " + fileFormatVersion);
		rowMap.put("dnPrefix", dnPrefix);
		log.log(Level.FINEST, "dnPrefix: " + dnPrefix);
		rowMap.put("localDn", fsLocalDN);
		log.log(Level.FINEST, "localDn: " + fsLocalDN);
		rowMap.put("managedElementLocalDn", meLocalDN);
		log.log(Level.FINEST, "managedElementLocalDn: " + meLocalDN);
		rowMap.put("elementType", elementType);
		log.log(Level.FINEST, "elementType: " + elementType);
		rowMap.put("userLabel", userLabel);
		log.log(Level.FINEST, "userLabel: " + userLabel);
		rowMap.put("swVersion", swVersion);
		log.log(Level.FINEST, "swVersion: " + swVersion);
		// collectionEndTime received so late, that migth not be used
		rowMap.put("endTime", granularityPeriodEndTime);
		log.log(Level.FINEST, "endTime: " + granularityPeriodEndTime);
		rowMap.put("jobId", jobId);
		log.log(Level.FINEST, "jobId: " + jobId);	
		final String loadname = objectMap.get(objectClass);

		dataMap.put(moid + loadname, rowMap);
		
		Map<String, Map<String, String>> vectorBinValueMap1 = new HashMap<String, Map<String,String>>();
		for(String counterName1 : vectorBinValueMap.keySet()) {
			vectorBinValueMap1.put(counterName1,vectorBinValueMap.get(counterName1));
		}
		vectorBinValueMap.clear();
		
		if(vectorData.containsKey(measObjLdn)){
			Map<String, Map<String, String>> vectorBinValueMap2 = new HashMap<String, Map<String,String>>();
			vectorBinValueMap2= vectorData.get(measObjLdn);
			vectorBinValueMap1.putAll(vectorBinValueMap2);
			vectorBinValueMap2.clear();
		}
		vectorData.put(measObjLdn, vectorBinValueMap1);
	}

	private void handleTAGMeasData() throws SAXException {

		if (!loaderClass.isEmpty() && !objectMap.isEmpty()) {

			final HashMap<String, String> objectMapClone = new HashMap<String, String>();
			final Iterator<String> objectIter = objectMap.keySet().iterator();

			while (objectIter.hasNext()) {
				final String tagID = objectIter.next();
				final String loader = objectMap.get(tagID);
				log.log(Level.FINEST, "Loader:", loader);
				if (!objectMapClone.containsValue(loader)) {
					objectMapClone.put(loader, tagID);
				}
			}


			final Iterator<String> loadIter = loaderClass.keySet().iterator();

			while (loadIter.hasNext()) {
				final String loaderName = loadIter.next();
				final HashMap<String, HashMap<String, String>> moid = loaderClass.get(loaderName);

				final String objectClassClone = objectMapClone.get(loaderName);
				  DFormat df = dfc.getFormatWithTagID(interfaceName, objectClassClone+vectorPostfix);
				  
				  if(df!=null){
					  try{

							log.log(Level.FINEST, "Creating MeasureMentFile for TagID " + objectClassClone+vectorPostfix + "and Interface Name"
									+ interfaceName);
							vectorMeasFile = Main.createMeasurementFile(sourceFile, objectClassClone+vectorPostfix, techPack, setType, setName,
									workerName, log);
							} catch (final Exception e) {
								log.log(Level.FINEST, "Error opening vector measurement data", e);
								e.printStackTrace();
								throw new SAXException("Error opening vector measurement data: " + e.getMessage(), e);
							}
							 for(String counterName1 : vectorData.keySet()) {
								 	String holdVal[] = counterName1.split(",");
								 	
								      if(holdVal[holdVal.length-1].contains(objectClassClone)){
										 Map<String, Map<String, String>> vectorBinValueMap = new HashMap<String, Map<String,String>>();
										 Map<String,String> value1 =new HashMap<String, String>();
										 int maxBinSize=0;
										 vectorBinValueMap = vectorData.get(counterName1);
											for(String counterName : vectorBinValueMap.keySet()) {
										      int binSize= vectorBinValueMap.get(counterName).size();
										      if(binSize > maxBinSize){
										    	  maxBinSize =binSize;
										      }
										       
											}
											int maxval=0;									
											for(String counterName : vectorBinValueMap.keySet()){
												value1 = vectorBinValueMap.get(counterName);
											for(String i2 :value1.keySet()){									
													if(i2!=null || i2!="0"){
													int binval=Integer.parseInt(i2);
													if(binval > maxval){
														maxval =binval;												
												      }
													}
													}
											}	
											final String begin1 = calculateBegintime();
											String checkZero = "NIL";
											
											for(int i=0;i<=maxval;i++){
												String binIndex= i+"";
												int flag=0;
												Map<String,String> value =new HashMap<String, String>();
												for(String counterName : vectorBinValueMap.keySet()){
													value = vectorBinValueMap.get(counterName);	
													
													if(value.containsKey(binIndex)&& binIndex!=null){
														if(flag==0){
															flag=1;
														}
														if(checkZero.equals(value.get(binIndex))){
															vectorMeasFile.addData(counterName,null);
														}else{
															vectorMeasFile.addData(counterName,value.get(binIndex));
														}
														log.log(Level.FINEST ,"Q:CounterName" + counterName+"Value" +value.get(binIndex));
														log.finest("DCVECTOR_INDEX"+binIndex);
													vectorMeasFile.addData("DCVECTOR_INDEX", binIndex);
													 vectorMeasFile.addData(counterName+rangePostfix, binIndex);	 
													 vectorMeasFile.addData("MOID",counterName1);
													 vectorMeasFile.addData("DC_SUSPECTFLAG", suspectFlag);

														vectorMeasFile.addData(addValues());
														
												 }
													
												}
												try {
													if(flag==1)
													vectorMeasFile.saveData();
												} catch (final Exception e) {
													log.log(Level.WARNING, "Error saving measurement data", e);
													e.printStackTrace();
													throw new SAXException("Error saving measurement data: " + e.getMessage(), e);

												}
									}	
										}
									 
								 }
								 
								 
								 
									if ((vectorMeasFile != null) && vectorMeasFile.isOpen()) {
										try {
											vectorMeasFile.close();
										} catch (final Exception e) {
											log.log(Level.WARNING, "Error closing measurement File", e);
											e.printStackTrace();
											throw new SAXException("Error closing Measurement File: " + e.getMessage(), e);
										}
									}
								 }
				  if(hasFlexCounters)
				  {
				  DFormat dff = dfc.getFormatWithTagID(interfaceName, objectClassClone + flexPostfix);
				  if(dff != null)
				  {
						if(createOwnFlexFile && flexMoidMap != null)
						{
						log.log(Level.FINEST, "Creating MeasureMentFile for TagID " + objectClassClone + flexPostfix + "and Interface Name"
								+ interfaceName);
						try {
							
							flexMeasFile = Main.createMeasurementFile(sourceFile, objectClassClone + flexPostfix, techPack, setType, setName,
									workerName, log);
						} catch (final Exception e) {
							log.log(Level.WARNING, "Error opening measurement data", e);
							e.printStackTrace();
							throw new SAXException("Error opening measurement data: " + e.getMessage(), e);
						}

						final Iterator<String> moidIter = moid.keySet().iterator();
						while (moidIter.hasNext()) {
							final String moidName = moidIter.next();
							final HashMap filterMap = (HashMap) flexMoidMap.get(moidName);
							final Iterator filterIter = filterMap.keySet().iterator();
							while (filterIter.hasNext())
							{
								final HashMap valMap = (HashMap) filterMap.get(filterIter.next());
								flexMeasFile.addData(valMap);
								try {
									flexMeasFile.saveData();
								} catch (final Exception e) {
									log.log(Level.FINEST, "Error saving measurement data", e);
									e.printStackTrace();
									throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
								}
							}
						}
						if ((flexMeasFile != null) && flexMeasFile.isOpen()) {
							try {
								flexMeasFile.close();
							} catch (final Exception e) {
								log.log(Level.FINEST, "Error closing measurement File", e);
								e.printStackTrace();
								throw new SAXException("Error closing Measurement File: " + e.getMessage(), e);
							}
						}
						}
				  }
				  
				  }
				  DFormat dfm = dfc.getFormatWithTagID(interfaceName, objectClassClone);
				  
				  if(dfm!=null){
				
				log.log(Level.FINEST, "Creating MeasureMentFile for TagID " + objectClassClone + "and Interface Name"
						+ interfaceName);
				try {
					
					measFile = Main.createMeasurementFile(sourceFile, objectClassClone, techPack, setType, setName,
							workerName, log);
				} catch (final Exception e) {
					log.log(Level.WARNING, "Error opening measurement data", e);
					e.printStackTrace();
					throw new SAXException("Error opening measurement data: " + e.getMessage(), e);
				}

				final Iterator<String> moidIter = moid.keySet().iterator();
				while (moidIter.hasNext()) {
					final String moidName = moidIter.next();
					final HashMap<String, String> counter = moid.get(moidName);

					measFile.addData(counter);
					measFile.addData(dataMap.get(moidName + loaderName));
					measFile.addData(counter);
					measFile.addData(dataMap.get(moidName + loaderName));
					try {
						measFile.saveData();
					} catch (final Exception e) {
						log.log(Level.FINEST, "Error saving measurement data", e);
						e.printStackTrace();
						throw new SAXException("Error saving measurement data: " + e.getMessage(), e);

					}

				}
				if ((measFile != null) && measFile.isOpen()) {
					try {
						measFile.close();
					} catch (final Exception e) {
						log.log(Level.FINEST, "Error closing measurement File", e);
						e.printStackTrace();
						throw new SAXException("Error closing Measurement File: " + e.getMessage(), e);
					}
				}
				  }
			}vectorData.clear();
			objectMapClone.clear();
			moidMap.clear();
			loaderClass.clear();
		}

	}

	private String calculateBegintime() {
		String result = null;
		try {
			String granPeriodETime = granularityPeriodEndTime;
			if (granPeriodETime.matches(".+\\+\\d\\d(:)\\d\\d") || granPeriodETime.matches(".+\\-\\d\\d(:)\\d\\d")) {
				granPeriodETime = granularityPeriodEndTime.substring(0, granularityPeriodEndTime.lastIndexOf(":"))
						+ granularityPeriodEndTime.substring(granularityPeriodEndTime.lastIndexOf(":") + 1);
			}
			granPeriodETime = granPeriodETime.replaceAll("[.]\\d{3}", "");
			if (granPeriodETime.endsWith("Z")) {
				granPeriodETime = granPeriodETime.replaceAll("Z", "+0000");
			}

			final Date end = simpleDateFormat.parse(granPeriodETime);
			final Calendar cal = Calendar.getInstance();
			cal.setTime(end);
			final int period = Integer.parseInt(granularityPeriodDuration);
			cal.add(Calendar.SECOND, -period);
			result = simpleDateFormat.format(cal.getTime());
		} catch (final ParseException e) {
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		} catch (final NumberFormatException e) {
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		} catch (final NullPointerException e) {
			log.log(Level.WARNING, "Worker parser failed to exception", e);
		}
		return result;
	}

	public static void main(final String[] args) {
		int argnum = 0;
		Xml3GPP32435Parser np = null;
		FileInputStream fis = null;
		while (argnum < args.length) {
			if (args[argnum].equals("-sf")) {
				final String s = args[argnum + 1];
				final File f = new File(s);
				try {
					fis = new FileInputStream(f);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			argnum++;
		}
		if (fis == null) {
			final File f = new File("C:\\tmp\\koetus.xml");
			try {
				fis = new FileInputStream(f);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		try {
			np = new Xml3GPP32435Parser();
			np.log = Logger.getLogger("etl.tp.st.sn.parser.NewParser.wn");

			/*
			 * Add logging handler np.log = Logger.getLogger("MyLog"); FileHandler fh = new
			 * FileHandler("c:\\tmp\\log.txt", true); np.log.addHandler(fh); SimpleFormatter formatter = new
			 * SimpleFormatter(); fh.setFormatter(formatter); np.log.setLevel(Level.ALL); np.log.log(Level.INFO,
			 * "Logger started");
			 */

			np.parse(fis);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * Extracts a substring from given string based on given regExp
	 * 
	 */
	public String parseFileName(final String str, final String regExp) {

		final Pattern pattern = Pattern.compile(regExp);
		final Matcher matcher = pattern.matcher(str);
		String result = "";

		if (matchObjectMaskAgainst.equalsIgnoreCase("whole")) {
			// Find a match between regExp and whole of str, and return group 1 as result
			if (matcher.matches()) {
				result = matcher.group(1);
				log.finest(" regExp (" + regExp + ") found from " + str + "  :" + result);
			} else {
				log.warning("String " + str + " doesn't match defined regExp " + regExp);
			}
		} else {
			// Find a match between regExp and return group 1 as result.
			if (matcher.find()) {
				result = matcher.group(1);
				log.finest(" regExp (" + regExp + ") found from subset of " + str + "  :" + result);
			} else {
				log.warning("No subset of String " + str + " matchs defined regExp " + regExp);
			}
		}

		return result;

	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		for (int i = start; i < (start + length); i++) {
			// If no control char
			if ((ch[i] != '\\') && (ch[i] != '\n') && (ch[i] != '\r') && (ch[i] != '\t')) {
				charValue += ch[i];
			}
		}
	}
	 /**
	   * This method handleVectorCounters is used to handle the vector counters. 
	   * 
	   * @input	Input to the handleVectorCounters is the counterName,counterValues,MeasurementFile
	   * @return Output is a Map containing the bins and the value for bins. 
	   */
	final private Map<String,String> handleVectorCounters(final Map datarow, final Map keyMap,String counter,String value){

		    final Map<String,String> tmpMap = new TreeMap<String,String>();
		    final Map<String,Map<String,String>> tmpMap1 = new HashMap<String,Map<String,String>>();
			final Map newtmpMap = new HashMap();
			List <Integer> rangeindex=new ArrayList<Integer>();
			final Map compressvectorkeymap=new HashMap();
			final Map compressvectorvaluemap=new HashMap();
			final Map cmvectorkeymap=new HashMap();
			final Map cmvectorvaluemap=new HashMap();
			try{
		    int max = 0;
		    String tmp = value;		   
		    
		    // get VECTOR counters
		    final Set rangeCounters = ((Set) datarow.get("rangeCounters"));
		    log.log(Level.FINEST, "T:Range Counters Set : " + rangeCounters + "<<");
		    
		    Iterator iter = rangeCounters.iterator();		    
		    while (iter.hasNext()) {
		    	
		    	final String key = (String) iter.next();
		    	log.log(Level.FINEST, "T:Key: " + key);
		    	
		    	if (counter.equals(key)) {
		    	  
			    	//BREAK-OUT THE RANGE (VECTOR) INTO AN ArrayList:
			    	final StringTokenizer tokens = new StringTokenizer(value, delimiter, true);
			    	List <String> bins = new ArrayList(tokens.countTokens()+1); //We do not know capacity needed, so make it bigger then needed (and reduce later).
			    	boolean prevTokenWasDelim = true;
			    	String currentToken; 
			   		while (tokens.hasMoreTokens()){
			   			currentToken = tokens.nextToken();
			   			if(!currentToken.equalsIgnoreCase(delimiter)){
			   				if(currentToken.equalsIgnoreCase("Nil") || currentToken.trim().equalsIgnoreCase("")){
	   						currentToken = null;
			   				log.finest("delimiter:: "+delimiter);
			   			}
			   				bins.add(currentToken); //It's not a delimiter so add it.
			   				prevTokenWasDelim = false;
			   			}else if(!prevTokenWasDelim){
			   				prevTokenWasDelim = true; //It's a delimiter so we don't add anything
			   			}else{
			   				bins.add(null); //It's a delimiter AND SO WAS THE LAST ONE. This represents empty input - so we add null.
			   			}
			   		}
			   		if(prevTokenWasDelim){ //This accounts for empty bin at end of vector.
			   			bins.add(null);
			   		}
			   		((ArrayList)bins).trimToSize();  //Set the capacity of the ArrayList to it's size.
			   		
			   		
			   		//DECOMPRESS THE VECTOR IF REQUIRED:
			   		if(compressedVectorCounters.contains(key)){
			    		rangeindex= getrangeindexfromcompressVector(bins);
			    		bins = getvaluesfromcompressVector(bins);
			    		compressvectorkeymap.put(key,rangeindex);
			    		if((null==bins)||(null==rangeindex)){
			    			log.finest("Vector "+key+" is not having valid data.");
			    			datarow.put(key, null);
			    			continue;
			    		}
			    	}
			   		log.finest("rangeindex"+rangeindex);
			   		log.finest("bins"+bins);
			   		newtmpMap.put(key, bins);
			   	//COLLECT VECTOR IN A HashMap
			   		//tmpMap.put(key, bins);
			   		
			   		//IS IT THE LONGEST VECTOR SO FAR?
			   		if(bins.size()>max){
			   			max = bins.size();
			   		}

			    	// IF IT'S REQUIRED, INSERT THE 1ST VALUE OF VECTOR (THE ZERO INDEX) INTO datarow
			        if (!tmpMap.isEmpty()) {
			          datarow.put(key, bins.get(0));
			        	}
			        iter = newtmpMap.keySet().iterator();
			    	List oldcompressvectorlist=new ArrayList();
			    	TreeSet<Integer> keylist=new TreeSet<Integer>();
			    	int compressvectorlen=-1;
			    	List<Integer> compressvectorkeylist=new ArrayList();
			          // loop all range counters (columns)
			          while (iter.hasNext()) {

			            final String key1 = (String) iter.next();
			    		
			    		if(compressedVectorCounters.contains(key1)){
			    			oldcompressvectorlist = (List) newtmpMap.get(key1);
			    			compressvectorkeylist =  (List)compressvectorkeymap.get(key1);
			    			keylist.addAll(compressvectorkeylist);
			    				//Instanciate ArrayList to be returned (with required capacity) and fill it with zeros
			    				List <String> result = new ArrayList();
			    				log.fine("max:"+max);
			    			/*	for(int i=0;i<max;i++){
			    					tmpMap.put(i+"", null);
			    					 result.add(null);
			    				}*/
			    				log.finest("oldcompressvectorlist:"+oldcompressvectorlist);
								log.finest("compressvectorkeylist:"+compressvectorkeylist);
			    				//Add the values from input into the correct position in the returned ArrayList (according to their corresponding index)
			    		/*	Iterator compressIter = compressvectorkeylist.iterator();
			    			while(compressIter.hasNext()){
			    				int i =(Integer) compressIter.next();
			    				log.info(i+"");
			    				tmpMap.put(compressvectorkeylist.get(i)+"", (String)oldcompressvectorlist.get(i));
			    			}*/
			    				for(int i=0;i<oldcompressvectorlist.size();i++){
			    					tmpMap.put(compressvectorkeylist.get(i)+"", (String)oldcompressvectorlist.get(i));
			    					/*if ( compressvectorkeylist.get(i)< max){
			    						tmpMap.put(compressvectorkeylist.get(i)+"", (String)oldcompressvectorlist.get(i));
			    						result.set(compressvectorkeylist.get(i), (String)oldcompressvectorlist.get(i));
			    					}
			    					else{
			    						tmpMap.put(compressvectorkeylist.get(i)+"", (String)oldcompressvectorlist.get(i));
			    						result.add((String)oldcompressvectorlist.get(i));
			    					}*/
			    				}
			    			if(result.size() > compressvectorlen){
			    				compressvectorlen=result.size();
			    			}
			    			//if(result.size() > max){
			    				//max=result.size();
			    			//}
			    			newtmpMap.put(key1,result);
			    			tmpMap1.put(key1, tmpMap);
			    			compressvectorvaluemap.put(key1,oldcompressvectorlist);
			    		}else{
			    			for(int i=0;i<max;i++){
		    					tmpMap.put(i+"", (String)bins.get(i));
			    			}
			    		}
			    	 }


			      


			          
			       /* for(int i=0;i<bins.size();i++){
			        	tmpMap.put(i+"", bins.get(i)+"");
			        }*/



		    }
		    

		    }
		    
			/// get CMVECTOR counters
		    final Set cmVectorCounters = ((Set) datarow.get("cmVectorCounters"));
		    log.log(Level.FINEST, "CM Counters Set : " + cmVectorCounters + "<<");	
		    
		    // loop all CMVECTOR counters in this datarow
		    Iterator cmIter = cmVectorCounters.iterator();
		    while (cmIter.hasNext()) {
		    final String key1 = (String) cmIter.next();		     
		      if (counter.equals(key1)) {			      				       
		        // musta add one delim to the end to make it work...
		        tmp += delimiter;
		        int i = 0;
		        boolean prewWasDelim = true;
		        final List list = new ArrayList();
		        final StringTokenizer token = new StringTokenizer(tmp, delimiter, true);
		        log.finest("Printing the tmp....."+tmp);
		        List <String> bins = new ArrayList(token.countTokens()+1);	
		        log.finest("Iterating through the whil for bins......");
		        while (token.hasMoreTokens()) {

	                final String tmptoken = token.nextToken();
					String tmpvalue = null;
					log.finest("Printing the token   "+tmptoken);
					if (prewWasDelim || (tmptoken.equalsIgnoreCase(delimiter) && prewWasDelim)) {
						 if (!tmptoken.equalsIgnoreCase(delimiter)) {
				            	if(tmptoken.equalsIgnoreCase("Nil") || tmptoken.trim().equalsIgnoreCase("")){
				            		log.finest("Nil or space has been occured.....");
				            		tmpvalue = null;
				            	}else{
				            		tmpvalue = tmptoken;
				            	}
				            }
	        
							log.finest("Value added to bins   "+tmptoken);
							bins.add(tmpvalue);
							i++;
	            }  
	            

					prewWasDelim = false;
					if (tmptoken.equalsIgnoreCase(delimiter)) {
						prewWasDelim = true;
					}
	        }
		          
		       
		          if(cmVectorCounters.contains(key1)){	
		        	  log.finest("In new tmp map  "+key1+"  "+bins);
		        	  newtmpMap.put(key1, bins);			    		
		    		if((null==bins)||(null==rangeindex)){
		    			log.finest("Vector "+key1+" is not having valid data.");
		    			datarow.put(key1, null);
		    			continue;
		    		}
		    		
		          }	
		     
			    		if(!bins.isEmpty()) {
			    			log.finest("To set null at 0 index");
			    			 // put the value from the first (zero) index into the original datarow because CMVECTORs are on hand			    			
			    			counterValueMap.put((String) measNameMap.get(measValueIndex), bins.get(0));					    			
					        bins.set(0, null);								      
					        }	        	  
			    				   		
			    		
		    iter = newtmpMap.keySet().iterator();
		   
	    	List oldcmvectorlist=new ArrayList();
	    	TreeSet<Integer> keylist=new TreeSet<Integer>();
	    	int cmvectorlen=-1;
	    	List<Integer> cmvectorkeylist=new ArrayList();
		 
	    		while(iter.hasNext()){
	            final String key2 = (String) iter.next();		            
	    		if(cmVectorCounters.contains(key2)){
	    			oldcmvectorlist = (List) newtmpMap.get(key2);
	    			
	    				//Instanciate ArrayList to be returned (with required capacity) and fill it with zeros
	    				List <String> result = new ArrayList();
	       				log.finest("max:"+max);		    			
	       				log.finest("oldcmvectorlist:"+oldcmvectorlist);
	    				
	    		
	    				for(int i2=1;i2<oldcmvectorlist.size();i2++){
	    					tmpMap.put(i2+"", (String)oldcmvectorlist.get(i2));
	    					log.info("Printing the final map  "+i2+"  "+(String)oldcmvectorlist.get(i2));			    					
	    				}
	    				
	    			if(result.size() > cmvectorlen){
	    				cmvectorlen=result.size();
	    			}
	    			
	    			newtmpMap.put(key1,result);
	    			tmpMap1.put(key1, tmpMap);
	    			cmvectorvaluemap.put(key1,oldcmvectorlist);
	    		}else{
	    			for(int i1=0;i1<max;i1++){
    					tmpMap.put(i1+"", (String)bins.get(i1));
    					
	    			}
	    		}
	    		}
		    
		      }		     
		    	  
		      }   
		    
		    
}catch(NullPointerException ne){
	log.log(Level.FINEST,"Counter values are not present in database");
}
finally{
			return tmpMap;
	}
}
	
	  public Map addValues(){
		    measurement = new HashMap();
		    measurement.put("PERIOD_DURATION", granularityPeriodDuration);
			log.log(Level.FINEST, "PERIOD_DURATION: " + granularityPeriodDuration);
			measurement.put("repPeriodDuration", repPeriodDuration);
			log.log(Level.FINEST, "repPeriodDuration: " + repPeriodDuration);
			final String begin = calculateBegintime();
			if (begin != null) {
				measurement.put("DATETIME_ID", begin);
				log.log(Level.FINEST, "DATETIME_ID: " + begin);
			}
			measurement.put("collectionBeginTime", collectionBeginTime);
			log.log(Level.FINEST, "collectionBeginTime: " + collectionBeginTime);
			//measurement.put("DC_SUSPECTFLAG", suspectFlag);
			//log.log(Level.FINEST, "T:DC_SUSPECTFLAG: " + suspectFlag +objectClass);
			measurement.put("filename", (sourceFile == null ? "dummyfile" : sourceFile.getName()));
			log.log(Level.FINEST, "filename: " + (sourceFile == null ? "dummyfile" : sourceFile.getName()));
			measurement.put("JVM_TIMEZONE", JVM_TIMEZONE);
			log.log(Level.FINEST, "JVM_TIMEZONE: " + JVM_TIMEZONE);
			measurement.put("DIRNAME", (sourceFile == null ? "dummydir" : sourceFile.getDir()));
			log.log(Level.FINEST, "DIRNAME: " + (sourceFile == null ? "dummydir" : sourceFile.getDir()));
			measurement.put("measInfoId", measInfoId);
			log.log(Level.FINEST, "measInfoId: " + measInfoId);
			measurement.put("objectClass", objectClass);
			log.log(Level.FINEST, "objectClass: " + objectClass);
			measurement.put("vendorName", vendorName);
			log.log(Level.FINEST, "vendorName: " + vendorName);
			measurement.put("fileFormatVersion", fileFormatVersion);
			log.log(Level.FINEST, "fileFormatVersion: " + fileFormatVersion);
			measurement.put("dnPrefix", dnPrefix);
			log.log(Level.FINEST, "dnPrefix: " + dnPrefix);
			measurement.put("localDn", fsLocalDN);
			log.log(Level.FINEST, "localDn: " + fsLocalDN);
			measurement.put("managedElementLocalDn", meLocalDN);
			log.log(Level.FINEST, "managedElementLocalDn: " + meLocalDN);
			measurement.put("elementType", elementType);
			log.log(Level.FINEST, "elementType: " + elementType);
			measurement.put("userLabel", userLabel);
			log.log(Level.FINEST, "userLabel: " + userLabel);
			measurement.put("swVersion", swVersion);
			log.log(Level.FINEST, "swVersion: " + swVersion);
			measurement.put("endTime", granularityPeriodEndTime);
			log.log(Level.FINEST, "endTime: " + granularityPeriodEndTime);
			measurement.put("jobId", jobId);
			log.log(Level.FINEST, "jobId: " + jobId);
		    measurement.put("filename", sourceFile.getName());
		    measurement.put("JVM_TIMEZONE", JVM_TIMEZONE);
		    measurement.put("DIRNAME", sourceFile.getDir());
		  
		    return measurement;
	  }
	  
	  public List getrangeindexfromcompressVector(List input){

	  		if(null==input || input.get(0)==null){  //HQ59381 fix for null pointer exception
				  return null;
			  }
			  
			  int inputSize = input.size();
			  

			  if (input.isEmpty() || (input.get(0).equals("")&&inputSize==1) ){
				  return input;  //Return input List if it is empty 
			  }
			  
			  final int expectedNumOfPairs;
			  try{
				  expectedNumOfPairs = Integer.parseInt((String)input.get(0)); //Find how many index value pairs there are in List.
			  }catch(Exception e){
				  return null;
			  }
			  
			  if(expectedNumOfPairs > 1024){ //Make sure it's not too big
				  return null;
			  }
			  if(expectedNumOfPairs==0 && inputSize==1){
				  return null;  //return input if it just has zero
			  }
			  if (inputSize % 2 == 0){ //Make sure it has odd size
				  return null;
			  }
			  if(inputSize!=(expectedNumOfPairs*2)+1){ //Check for correct num of name-value pairs. This also catches negative expectedNumOfPairs
				  return null; 
			  }

			  //This FOR loop does 3 things: sanity checks the indecies, finds the highest one and puts them in their own ArrayList, 
			  int index;
			  int highestIndex = -1;
			  List  <Integer> indecies = new ArrayList(expectedNumOfPairs);
			  for (int i=1;i<inputSize;i=i+2){ //Takes the indecies from input List and put the in their own list
				  try{
					  index = Integer.parseInt((String)input.get(i));

				  }catch(Exception e){
					  return null;
				  }
				  if(index<0){
					  return null;
				  }
				  indecies.add(index);
				  if(index > highestIndex){
					  highestIndex = index;
				  }
			  }
			  if(highestIndex>1024){ //Make sure highest index is not too big
				  return null;
			  }

			  return indecies;
	  	  }
		  
		  /**
		   * This method decompresses (decodes) a compressed vector. 
		   * 
		   * @input	A compressed vector as a List: first entry in list indicates number of indexes, and following entries are alternatly an index and a value. 
		   * @return	A decompressed (decoded) version of the input as a List, containing the values in their corect position.
		   */
		  public List getvaluesfromcompressVector(List input){
			  

			  if(null==input || input.get(0)==null){  //HQ59381 fix for null pointer exception
				  return null;
			  }
			  
			  int inputSize = input.size();
			  
			  if (input.isEmpty() || (input.get(0).equals("")&&inputSize==1) ){
				  return input;  //Return input List if it is empty 
			  }
			  
			  final int expectedNumOfPairs;
			  try{
				  expectedNumOfPairs = Integer.parseInt((String)input.get(0)); //Find how many index value pairs there are in List.
			  }catch(Exception e){
				  return null;
			  }
			  
			  if(expectedNumOfPairs > 1024){ //Make sure it's not too big
				  return null;
			  }
			  if(expectedNumOfPairs==0 && inputSize==1){
				  return input;  //return input if it just has zero
			  }
			  if (inputSize % 2 == 0){ //Make sure it has odd size
				  return null;
			  }
			  if(inputSize!=(expectedNumOfPairs*2)+1){ //Check for correct num of name-value pairs. This also catches negative expectedNumOfPairs
				  return null; 
			  }

			  //This FOR loop does 3 things: sanity checks the indecies, finds the highest one and puts them in their own ArrayList, 
			  int index;
			  int highestIndex = -1;
			  List  <Integer> indecies = new ArrayList(expectedNumOfPairs);
			  for (int i=1;i<inputSize;i=i+2){ //Takes the indecies from input List and put the in their own list
				  try{
					  index = Integer.parseInt((String)input.get(i));

				  }catch(Exception e){
					  return null;
				  }
				  if(index<0){
					  return null;
				  }
				  indecies.add(index);
				  if(index > highestIndex){
					  highestIndex = index;
				  }
			  }
			  if(highestIndex>1024){ //Make sure highest index is not too big
				  return null;
			  }
			  
			  //Instanciate ArrayList to be returned (with required capacity) and fill it with zeros
			  List <String> result = new ArrayList(highestIndex+1);
			/*  for(int i=0;i<=highestIndex;i++){
				  result.add("0");
			  }*/


			  
			  //Add the values from input into the correct position in the returned ArrayList (according to their corresponding index)
			  for(int i=0;i<indecies.size();i++){
				 // result.set(indecies.get(i), (String)input.get(i*2+2));
				  result.add((String)input.get(i*2+2));
			  }
			  
			  return result;
		  }

	// Flex specific methods
		  
	  /**
	   * Checks if a given counter is a Flex Counter
	   * 
	   */
	  private boolean checkIfFlex(String counterName) 
	  {
		  String counter = "";
		  if(counterName.contains("_"))
		  	{
			  String[] flexSplit = counterName.split("_");
			  counter = flexSplit[0];
			  if(((Set) flexCounterBin.get("flexCounters")).contains(counter))
			  {
				  handlesFlex(true, counterName);
				  return true;
			  }
			}
			else
			{
			  counter = counterName;
			  if(((Set) flexCounterBin.get("flexCounters")).contains(counter))
			  {
				  handlesFlex(false, counterName);
				  return true;
			  }
			}
		  	return false;
	  }
	  /**
	   * Handles Flex counters and populates flex counter map
	   * 
	   */
	  
	  private void handlesFlex(boolean hasFilter, String flexCounterFilter)
	  {try{
		  String flexCounter = "";
	      String flexFilter = "";
	      String flexHash = "";
	      String origValue=charValue;
	      if(hasFilter)
	  		{
	  			String[] flexSplit = flexCounterFilter.split("_");
	  			flexCounter = flexSplit[0];
	  			flexFilter = flexSplit[1];
	  		}
	  		else
	  		{
	  			flexCounter = flexCounterFilter;
	  			flexFilter = "";
	  		}
	      flexHash = Integer.toString(flexFilter.hashCode());
		
		if ((origValue != null) && ( origValue.equalsIgnoreCase("NIL")  || origValue.trim().equalsIgnoreCase("")) ) {
			origValue = null;
			log.finest("Setting the value to null as in-valid data being received from the Node");
		}
		
		
	      if(flexFilterMap != null && flexFilterMap.containsKey(flexHash))
	      {
	  		//log.info("flexFilterMap contains flexHash : " + flexHash);

	      	flexValueMap = (HashMap) flexFilterMap.get(flexHash);
	  		//log.info("Case 2: got flexValueMap with size "+ flexValueMap.size()+ " value for "+ flexCounter + "in flexValueMap is "
	  				//+ flexValueMap.get(flexCounter) + " going to put value : \"" + origValue +"\"");
	  		

	      	flexValueMap.put(flexCounter,origValue);
	      }
	      else
	      {
	    	  flexValueMap = new HashMap();
	    	  flexValueMap = (HashMap) addValues();
	    	  flexValueMap.put("DC_SUSPECTFLAG", suspectFlag);
	    	  flexValueMap.put("MOID", measObjLdn);
	    	  flexValueMap.put("FLEX_FILTERNAME", flexFilter);
	    	  flexValueMap.put("FLEX_FILTERHASHINDEX", flexHash);
	    	  flexValueMap.put(flexCounter,origValue);
	    	  //log.info("Case 1 : flexValueMap is newly created and inserted with size : " +flexValueMap.size() + 
	    			  //" going to put value : \"" + origValue +"\"" + " for counter : " + flexCounter);
	      }
	      //log.info("Putting value for filter: " + flexFilter + " : " + flexHash);
	      flexFilterMap.put(flexHash,flexValueMap);

	    	        
	  }catch(Exception e){
		  log.info("Exception occured trace below" +e.getMessage());
		  e.getStackTrace();
		  
	  }
	  }
		 
}

