/*
 * Created on 19.04.2012
 *
 */
package com.ericsson.eniq.etl.iptnmsPS;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

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
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * iptnms Pack switch Parser: based on the mlxml parser
 * 
 * @author eneacon
 * 
 * 
 * */

public class IptnmsPSParser extends DefaultHandler implements Parser {


	// New packet switch stuff

	private HashMap <String, String> attrTable = new HashMap<String, String>(); // Generic Table to store Key Value Attributes
	private HashMap <String, String> fdfrTagList = new HashMap<String, String>(); //Map to store common tags for EndPoints in FDFR tag
	private HashMap<String,String> fdfrcptpTagList = new HashMap<String,String>(); //Map to store attributes for each Service Point in FDFR tag 
	private int CIRendPoint = 0; //Contain sum of CIR in EndPoint for ingress classifier
	private int CIRcptp =0; //Contain sum of CIR in CPTP for ingress classifier
	private int nestLevel = 0;
	private int taggedCount = 0;
	private boolean MELock = false;
	private boolean ELLLock = false;
	private boolean firstCPTP = false;
	private boolean routeLock = false;
	private boolean FDFrLock = false;
	private boolean cptpLock = false;
	private boolean endPointLock= false;	
	private boolean componentDomainNodeLock=false;
	private boolean classiferValid = false;

	private HashMap<String, MeasurementFile> mFileHashMap = new HashMap<String, MeasurementFile>();

	// Virtual machine timezone unlikely changes during execution of JVM
	//private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private SourceFile sourceFile;
	private String charValue;
	private MeasurementFile measFile = null; //File for ME Tag
	private MeasurementFile routemeasFile = null; //File for Routes in FDFR tag
	private MeasurementFile servicemeasFile = null; //File for Storing data as near and far attributes in FDFR
	private MeasurementFile serviceTypeMeasFile = null; //File for storing template id data
	private MeasurementFile endpointmeasFile = null; //File for storing data in endpoint tag
	private MeasurementFile cptpMeasFile = null; //File for storing data in CPTP tag
	private MeasurementFile ellmeasFile = null; //File for storing ell data
	private MeasurementFile serviceEndPointMeasFile = null; //File for storing each endpoint data in FDFR tag
	private Logger log;
	private String techPack;
	private String setType;
	private String setName;
      private String ServiceName = null;
	private int status = 0;
	private Main mainParserObject = null;
	private String workerName = "";
	final private List errorList = new ArrayList();


	/**
	 * 
	 */
	public void init(final Main main, final String techPack,
			final String setType, final String setName, final String workerName) {

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

		log = Logger.getLogger("etl." + techPack + "." + setType + "."
				+ setName + ".parser.hxmliptnms" + logWorkerName);
	}

	public int status() {
		return status;
	}

	public List errors() {
		return errorList;
	}

	public void run() {

		try {

			this.status = 2;
			SourceFile sf = null;

			while ((sf = mainParserObject.nextSourceFile()) != null) {

				try {

					mainParserObject.preParse(sf);
					parse(sf, techPack, setType, setName);
					mainParserObject.postParse(sf);
				} catch (Exception e) {
					mainParserObject.errorParse(e, sf);
				} finally {
					mainParserObject.finallyParse(sf);
				}
			}
		} catch (Exception e) {
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
	public void parse(final SourceFile sf, final String techPack,
			final String setType, final String setName) throws Exception {
		this.measFile = null;
		final long start = System.currentTimeMillis();
		this.sourceFile = sf;

		SAXParserFactory spf = SAXParserFactory.newInstance();

		SAXParser parser = spf.newSAXParser();
		final XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);

		xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
		final long middle = System.currentTimeMillis();
		xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));
		final long end = System.currentTimeMillis();
		log.log(Level.FINER, "Data parsed. Parser initialization took "
				+ (middle - start) + " ms, parsing " + (end - middle)
				+ " ms. Total: " + (end - start) + " ms.");

	}

	/**
	 * 
	 */
	public void parse(final FileInputStream fis) throws Exception {

		final long start = System.currentTimeMillis();

		SAXParserFactory spf = SAXParserFactory.newInstance();
		final SAXParser parser = spf.newSAXParser();
		final XMLReader xmlReader = parser.getXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);

		final long middle = System.currentTimeMillis();

		xmlReader.parse(new InputSource(fis));

		final long end = System.currentTimeMillis();
		log.log(Level.FINEST, "Data parsed. Parser initialization took "
				+ (middle - start) + " ms, parsing " + (end - middle)
				+ " ms. Total: " + (end - start) + " ms.");
	}

	/**
	 * Event handlers
	 */
	//This method is introduced to reset all the global variable to avoid any issues in exceptional scenario for next file in same thread
	public void reset(){
		attrTable.clear();
		fdfrcptpTagList.clear(); 
		fdfrTagList.clear();
            CIRendPoint=CIRcptp=nestLevel=taggedCount = 0;
		MELock =ELLLock =firstCPTP =routeLock = false;
		FDFrLock =cptpLock =endPointLock=componentDomainNodeLock=classiferValid = false;
		measFile = null; 
		routemeasFile = null; 
		servicemeasFile = null; 
		serviceTypeMeasFile = null; 
		endpointmeasFile = null;
		cptpMeasFile = null; 
		ellmeasFile = null; 
		serviceEndPointMeasFile=null;
            ServiceName = null;
	}

	public void startDocument() {
		log.log(Level.INFO, "Parsing the file ");
		reset();
	}

	public MeasurementFile createFile(String tag) throws SAXException{
		try
		{
			MeasurementFile outputFile = null;
			log.fine("Creating "+tag+" Meas File");
			outputFile = Main.createMeasurementFile(sourceFile, tag, techPack, setType, setName,workerName, log);
			return outputFile;
		}
		catch(Exception e){
			log.log(Level.FINEST,"Error getting measurement file details ", e);
			e.printStackTrace();
			throw new SAXException("Error getting measurement file details "+ e.getMessage(), e);
		}

	}
	public void endDocument() throws SAXException {
		closeFile(measFile);
		closeFile(routemeasFile); 
		closeFile(servicemeasFile);
		closeFile(serviceTypeMeasFile); 
		closeFile(endpointmeasFile);
		closeFile(cptpMeasFile); 
		closeFile(ellmeasFile); 
		closeFile(serviceEndPointMeasFile);
		reset();
	}

	public void updateMeasFile (HashMap<String,String> MeasFileUpd,MeasurementFile updateMeasFile) throws SAXException {

		try{
			updateMeasFile.addData(MeasFileUpd);
			updateMeasFile.setData(MeasFileUpd);
			updateMeasFile.saveData();		
		}
		catch(Exception e ){
			log.log(Level.WARNING,"Error while saving data");
		}
	}

	public void startElement(final String uri, final String name,
			final String qName, final Attributes atts) throws SAXException {

		charValue = "";
		// Reset charValue each time


		nestLevel++;

		// Keeps track of where in the nest level it currently is
		// If nestlevel is equals to 2 it indicates that it is a main tag and
		// not nested within.

		if (nestLevel == 2) {

			if (qName.equals("ME") || qName.equals("ELL")|| qName.equals("FDFr")) {

				taggedCount = 0;
				// reset tagged count
				attrTable.clear();
				// clean the hashmap before starting to parse the relevent data
				if(qName.equals("ME")){

					MELock = true;
					// Lock so that ME specific data is parsed properly
					
					if(measFile == null){
						measFile = createFile(qName);
					}
				}
				// Create a measfile for the relevent data

				else if (qName.equals("ELL")) {
					ELLLock = true;
					if(ellmeasFile == null){
						ellmeasFile = createFile("ELL");
					}
					// Lock so that ELL specific data is parsed properly
				}
				else if (qName.equals("FDFr")) {
					if(servicemeasFile == null){
						servicemeasFile = createFile("FDfr");
					}
					FDFrLock = true;
					// Lock so that FDFr specific data is parsed properly
				}
				firstCPTP = true;
				// Keeps track of whether it is the first CPTP Tag encountered
				// within the ELL/FDFr tags
			}
			else if(qName.equals("CPTP")){
				CIRcptp = 0 ;
				attrTable.clear();
				if(cptpMeasFile == null){
					cptpMeasFile = createFile("cptp");
				}
				cptpLock = true;
			}
			else if(qName.equals("EndPoint")){
				CIRendPoint = 0;
				attrTable.clear();
				if(endpointmeasFile == null){
					endpointmeasFile = createFile("endpoints");
				}
				endPointLock = true;
			}
			else if(qName.equals("EthernetLineTemplate") || qName.equals("EthernetLanTemplate")){
				String id = atts.getValue("Id");
				String serviceType = null;
				if(qName.equals("EthernetLineTemplate")){
					serviceType = "E-LINE";
				}
				else {
					serviceType = "E-LAN";
				}
				if(serviceTypeMeasFile == null){
					serviceTypeMeasFile = createFile("serviceid");
				}

				attrTable.put("SERVICE_ID",id);
				attrTable.put("SERVICE_TYPE",serviceType);
				if(serviceTypeMeasFile != null){
					updateMeasFile(attrTable,serviceTypeMeasFile);
				}
				attrTable.clear();
			}
		}
		if (qName.equals("Route")) {
			routeLock = true;
			// Route lock required to overcome identical tags reused in the FDFr
			// elements
		}

		else if (qName.equals("ComponentDomainNode")) {
			componentDomainNodeLock = true;
			// Lock so that ME type  data is parsed properly
		}
		else if (FDFrLock && qName.equals("CPTPName")) {
			taggedCount++;
		}

		else if (qName.equals("tagged") ) {
			if(FDFrLock){
				if (taggedCount == 1) {
					attrTable.put("NEAR_END_TP_ID", atts.getValue("TPID").trim());
					fdfrcptpTagList.put("TP_ID", atts.getValue("TPID").trim());

					attrTable.put("NEAR_END_VLANID", atts.getValue("VlanId").trim());
					fdfrcptpTagList.put("VLAN_ID", atts.getValue("VlanId").trim());
                                                            
				}
				else if (taggedCount == 2) {
					attrTable.put("FAR_END_TP_ID", atts.getValue("TPID").trim());
					fdfrcptpTagList.put("TP_ID", atts.getValue("TPID").trim());

					attrTable.put("FAR_END_VLANID", atts.getValue("VlanId").trim());
					fdfrcptpTagList.put("VLAN_ID", atts.getValue("VlanId").trim());

				}
				else if (taggedCount > 2) {

					if(!routeLock){
						fdfrcptpTagList.put("VLAN_ID", atts.getValue("VlanId").trim());
						fdfrcptpTagList.put("TP_ID", atts.getValue("TPID").trim());

					}
					else{
						log.finest("Inside >2  tag");
						attrTable.put("ROUTE_TPID", atts.getValue("TPID").trim());
						attrTable.put("ROUTE_VLANID", atts.getValue("VlanId").trim());
					}

				}
			}
			else if(endPointLock){
				String vlanID = atts.getValue("VlanId").trim();
				attrTable.put("VLAN_ID",vlanID);
			}
		}
		else if (qName.equals("TCProfile")){
			String classifier = null;
			classifier = atts.getValue("Classifier").trim();
			if((classifier != null) && (classifier.equals("ingress"))){
				classiferValid = true;
			}
		}
	}


	public void endElement(final String uri, final String name,
			final String qName) throws SAXException {

		nestLevel--;
		// Go back down 1 nest level as a closing tag is encountered

		// ME table specific parsings

		if (qName.equals("Name") && MELock) {

			attrTable.put("meNE_Name", this.charValue.trim());
		}

		else if (qName.equals("Type") && MELock) {

			if(!componentDomainNodeLock) {   //we want <type> which is not under <componentDomainNode> tag

				attrTable.put("meNE_Type", this.charValue.trim());
			}
		}
		else if(qName.equals("EndPoint") && (endPointLock)){ //Populating Table to have ME+Slot+Port+Shelf+VlanId and sum(CIR) value for EndPoint Tag
			log.finest("CIR endpoint is"+CIRendPoint);
			attrTable.put("CIR",Integer.toString(CIRendPoint).trim());
			if(endpointmeasFile == null){
				log.log(Level.WARNING,"EndPoint Meas File null");
			}
			if(endpointmeasFile != null){
				updateMeasFile(attrTable,endpointmeasFile);
			}
			attrTable.clear();
			endPointLock = false ; 
		}
		else if(qName.equals("CPTP") && cptpLock ){
			log.fine("CIRcptp is::"+Integer.toString(CIRcptp).trim());
			attrTable.put("CIR",Integer.toString(CIRcptp).trim());
			if(cptpMeasFile == null){
				log.log(Level.WARNING,"CPTP Meas File null");
			}
			if(cptpMeasFile !=null ){
				updateMeasFile(attrTable,cptpMeasFile);
			}
			cptpLock = false;
			attrTable.clear();
		}
		//Adding CIR value only for "ingress" calssifier
		else if(qName.equals("CIR") && (classiferValid)){
			if(endPointLock){
				CIRendPoint+=Integer.parseInt(this.charValue.trim());
			}
			else if(cptpLock){
				CIRcptp+=Integer.parseInt(this.charValue.trim());
			}
			classiferValid = false;
		}
		else if (qName.equals("HostEM") && MELock) {

			attrTable.put("meEM_Name", this.charValue.trim());
		}

		// ELL table specific parsings

		else if (qName.equals("Name") && ELLLock) {
			
			attrTable.put("ellLINK_NAME",this.charValue.trim());
		}

		//else if (qName.equals("Type") && ELLLock) {
		
		//	attrTable.put("ellLINK_TYPE",this.charValue.trim());
		//}
		//EQEV-30252
		else if(qName.equals("LinkType") && ELLLock) {

			attrTable.put("ellLINK_TYPE", this.charValue.trim());
		}

		else if (qName.equals("State")) {
			attrTable.put("STATE", this.charValue.trim());
			fdfrTagList.put("STATE",this.charValue.trim());
		}
		else if((qName.equals("Rate")) && (cptpLock) ){
			attrTable.put("LINK_SPEED",this.charValue.trim());
		}
		else if((qName.equals("EncapsulationFormat")) && (cptpLock) ){
			attrTable.put("ENCAPSULATION",this.charValue.trim());
		}
		else if((qName.equals("SignalType")) && (cptpLock )){
			attrTable.put("SIGNAL_TYPE",this.charValue.trim());
		}

		else if (qName.equals("ME") && nestLevel > 2) {
			if((cptpLock) || (endPointLock)){
				attrTable.put("meNE_Name",this.charValue.trim());
			}

			else if (firstCPTP) {
				if (ELLLock) {
					attrTable.put("ellNEAR_END_NE", this.charValue.trim());
				} else {
				
					attrTable.put("NEAR_END_NE", this.charValue.trim());
					fdfrcptpTagList.put("NE_NAME",this.charValue.trim());
				}

			}
			else if (!routeLock) {
				if (ELLLock) {
					attrTable.put("ellFAR_END_NE", this.charValue.trim());
				} else {
					attrTable.put("FAR_END_NE", this.charValue.trim());
					fdfrcptpTagList.put("NE_NAME",this.charValue.trim());
				}

			} else {
				attrTable.put("NE_NAME", this.charValue.trim());
			}
		}
		
		else if (qName.equals("NEId")){
			if((cptpLock) || (endPointLock)){
				attrTable.put("meNE_ID", this.charValue.trim());
				log.log(Level.INFO, "NEID"+this.charValue.trim());
			}else{
				attrTable.put("NE_ID", this.charValue.trim());
			}
		}
		
		else if (qName.equals("Shelf")) {
			if((cptpLock) || (endPointLock)){
				attrTable.put("SHELF_ID",this.charValue.trim());
			}

			else if (firstCPTP) {
				if (ELLLock) {
					attrTable.put("ellNEAR_END_SHELF_ID", this.charValue.trim());
				} else {
					attrTable.put("NEAR_END_SHELF_ID", this.charValue.trim());
					fdfrcptpTagList.put("SHELF_ID",this.charValue.trim());
				}

			}
			else if (!routeLock) {
				if (ELLLock) {
					attrTable.put("ellFAR_END_SHELF_ID", this.charValue.trim());
				} else {
					attrTable.put("FAR_END_SHELF_ID", this.charValue.trim());
					fdfrcptpTagList.put("SHELF_ID",this.charValue.trim());
				}

			} else {
				attrTable.put("SHELF_ID", this.charValue.trim());
			}
		}

		else if (qName.equals("Slot")) {
			if((cptpLock)|| (endPointLock)){
				attrTable.put("SLOT_ID",this.charValue.trim());
			}
			
			else if (firstCPTP) {
				if (ELLLock) {
					attrTable.put("ellNEAR_END_SLOT_ID", this.charValue.trim());
				} else {
					attrTable.put("NEAR_END_SLOT_ID", this.charValue.trim());
					fdfrcptpTagList.put("SLOT_ID",this.charValue.trim());
				}

			}
			else if (!routeLock) {

				if (ELLLock) {
					attrTable.put("ellFAR_END_SLOT_ID", this.charValue.trim());
				} else {
					fdfrcptpTagList.put("SLOT_ID",this.charValue.trim());
					attrTable.put("FAR_END_SLOT_ID", this.charValue.trim());
				}

			} else {
				attrTable.put("SLOT_ID", this.charValue.trim());
			}
		}
		else if (qName.equals("InterfaceType")) {
			if (firstCPTP) {

				if (ELLLock) {
					attrTable.put("ellNEAR_END_INTERFACE_TYPE",this.charValue.trim());
				} else {
					attrTable.put("NEAR_END_INTERFACE_TYPE",this.charValue.trim());
					fdfrcptpTagList.put("INTERFACE_TYPE",this.charValue.trim());

				}

			}

			else if (!routeLock) {

				if (ELLLock) {
					attrTable.put("ellFAR_END_INTERFACE_TYPE",
							this.charValue.trim());
				} else {
					attrTable.put("FAR_END_INTERFACE_TYPE",
							this.charValue.trim());
					fdfrcptpTagList.put("INTERFACE_TYPE",this.charValue.trim());
				}

			} else {
				attrTable.put("INTERFACE_TYPE", this.charValue.trim());
			}
		}

		else if (qName.equals("Port")) {
			if ((cptpLock) || (endPointLock)){
				attrTable.put("PORT_ID",this.charValue.trim());
			}

			if (firstCPTP) {

				if (ELLLock) {
					attrTable.put("ellNEAR_END_PORT_ID", this.charValue.trim());
				} else {
					attrTable.put("NEAR_END_PORT_ID", this.charValue.trim());
					fdfrcptpTagList.put("PORT_ID",this.charValue.trim());	
				}

				firstCPTP = false;
			}

			else if (!routeLock) {

				if (ELLLock) {
					attrTable.put("ellFAR_END_PORT_ID", this.charValue.trim());
				} else {
					attrTable.put("FAR_END_PORT_ID", this.charValue.trim());
					fdfrcptpTagList.put("PORT_ID",this.charValue.trim());
				}

			} else {
				attrTable.put("PORT_ID", this.charValue.trim());
			}
		}
		else if (qName.equals("PhysicalMedialess")) {
			if ((cptpLock) || (endPointLock)){
				log.info("PORT_TYPE  PhysicalMedialess ");
				attrTable.put("PORT_TYPE","PhysicalMedialess");
			}
		}
		else if (qName.equals("AggregatePortName")){
			log.info("PORT_QUALIFIER  LAG MEMBER ");
			attrTable.put("PORT_QUALIFIER", "LAG MEMBER");
		}
		else if (qName.equals("EthernetAggregate")){
			log.info("PORT_QUALIFIER  LAG PORT ");
			attrTable.put("PORT_QUALIFIER", "LAG PORT");
		}
		else if (qName.equals("Name") && FDFrLock) {
				fdfrTagList.put("SERVICE_NAME",this.charValue.trim());
                  attrTable.put("SERVICE_NAME", this.charValue.trim());
		}
		else if (qName.equals("ServiceTemplateId") && FDFrLock){
			fdfrTagList.put("SERVICE_ID",this.charValue.trim());
			attrTable.put("SERVICE_ID", this.charValue.trim());
		}

		else if(qName.equals("ComponentDomainNode")) {

			componentDomainNodeLock=false;

		}

		else if (qName.equals("ME")) {
				if (measFile == null) {
					log.log(Level.WARNING,"ME Measurement file null");

				} else {
					// Saving ME topology table
					updateMeasFile(attrTable,measFile);
					attrTable.clear();
				}

			MELock = false;

		}

		else if (qName.equals("ELL")) {
			// Saving ELL topology table
			if (ellmeasFile == null) {
				log.log(Level.WARNING,"ELL Measurement file null");
			} 
			else{ 
				updateMeasFile(attrTable, ellmeasFile);
			}
			attrTable.clear();
			ELLLock = false;

		}

		else if(qName.equals("EndPoint") && (FDFrLock)){
			if(routeLock){


				try {

					if (routemeasFile == null) {
                                          routemeasFile = createFile("FDfr1");
                                       
                              } 
                              if(routemeasFile != null){
                                    String serviceName = null;
                                    serviceName = fdfrTagList.get("SERVICE_NAME");
                                    attrTable.put("SERVICE_NAME",serviceName);
						// Saving FDFr Route data
						routemeasFile.addData(attrTable);
						routemeasFile.setData(attrTable);
						routemeasFile.saveData();						
						attrTable.clear();
					}

				} catch (Exception e) {

					log.log(Level.FINEST, "Error saving measurement data", e);
					e.printStackTrace();
					throw new SAXException("Error saving measurement data: "
							+ e.getMessage(), e);
				}
			}
			else{	
					fdfrcptpTagList.putAll(fdfrTagList);
					if(serviceEndPointMeasFile ==  null){
						serviceEndPointMeasFile = createFile("Fdfr2");
					}
					if(serviceEndPointMeasFile != null){
						updateMeasFile(fdfrcptpTagList,serviceEndPointMeasFile);
					}
					fdfrcptpTagList.clear();
				
				
			}
		}
		// Save the FDFr service data now, before it is corrupted by the
		// upcoming route data and will be written to file at end of document after adding Service_ID Attribute
		else if (qName.equals("TerminationPoints") && FDFrLock) {
			if(servicemeasFile == null){
				log.log(Level.WARNING,"Service Measurement Fille null");
			}
			else{
				updateMeasFile(attrTable, servicemeasFile);
			}
			attrTable.clear();
		}

		else if (qName.equals("FDFr")) {
			fdfrTagList.clear();
			FDFrLock = false;
		}

		else if (qName.equals("Route")) {

			routeLock = false;
		}

		else {
			attrTable.put(qName, this.charValue.trim());
			// We have to save everything, so this is how it saves irrelevent
			// stuff
		}}

	private void closeFile(MeasurementFile measFile){
				if(measFile != null){
					try{
						measFile.close();
						measFile = null;
					}
					catch(Exception e){
						log.log(Level.WARNING,"Error closing measurement File"+e.getMessage());
					}
				}
		}

	public static void main(String[] args) {
		int argnum = 0;
		IptnmsPSParser np = null;
		FileInputStream fis = null;
		while (argnum < args.length) {
			if (args[argnum].equals("-sf")) {
				final String s = args[argnum + 1];
				final File f = new File(s);
				try {
					fis = new FileInputStream(f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			argnum++;
		}
		if (fis == null) {
			final File f = new File(
			"C:\\__AnandData\\__WorkDocs\\MiniLinkParser\\MiniLinkSampleFile1.xml");
			try {
				fis = new FileInputStream(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			np = new IptnmsPSParser();
			np.log = Logger.getLogger("etl.tp.st.sn.parser.NewParser.wn");

			np.parse(fis);
		} catch (Exception e) {
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

		if (matcher.matches()) {
			final String result = matcher.group(1);
			log.finest(" regExp (" + regExp + ") found from " + str + "  :"
					+ result);
			return result;
		} else {
			log.warning("String " + str + " doesn't match defined regExp "
					+ regExp);
		}

		return "";

	}

	public void characters(char[] ch, int start, int length)
	throws SAXException {
		for (int i = start; i < start + length; i++) {
			// If no control char
			if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r'
				&& ch[i] != '\t') {
				charValue += ch[i];
			}
		}
	}

}
