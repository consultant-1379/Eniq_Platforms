/*
 * Created on 19.04.2012
 *
 */
package com.ericsson.eniq.etl.iptnmsCS;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
 * IPTNMS Circuit Switch Parser: Parses IPTNMS circuit switch data. It is based
 * on the MLXML parser. <br>
 * <br>
 * 
 * @author eneacon
 * 
 * 
 * */

public class IptnmsCSParser extends DefaultHandler implements Parser {

	// Circuit switch parser related items:

	private HashMap<String, String> attrTable = new HashMap<String, String>();
	private HashMap<String, String> measFileHashMap = new HashMap<String, String>();
	private HashMap<String, MeasurementFile> measFileAccessHashMap = new HashMap<String, MeasurementFile>();

	private boolean routing_PathIDLock = false;
	private boolean PathNameLock = false;
	private boolean fromTPLock = false;
	private boolean toTPLock = false;
	private boolean circuitDTLock = false;
	private boolean HOLock = false;
	private boolean phLayerLock = false;
	private boolean phLayerCardLock = false;
	private boolean phLayerPortLock = false;
	private boolean phLayerPortTypeLock = false;
	private boolean phLayerChannelLock = false;
	private boolean subNetworkLongNameLock = false;
	private boolean networkElementLock = false;
	private boolean accessSubNetworkIDLock = false;
	private boolean subNetworksubIDLock = false;
	private boolean subNetworkShortNameLock = false;
	private boolean accessPointShelfLock = false;
	private boolean networkElementNELock = false;
	private boolean routePathTypeLock = false;
	private int routeNodeLinkCount = 0;
	private boolean dtStateLock = false;
	private boolean phLayerShelfLock = false;
	private boolean phLayerSubIDLock = false;
	private boolean accessPhySlotLock = false;
	private boolean accessSlotLock = false;
	private boolean accessPortLock = false;
	private boolean accessChannelNameLock = false;
	private int networkElementCount;
	private int routeNodeTheTPCount = 0;

	private HashMap<String, MeasurementFile> mFileHashMap = new HashMap<String, MeasurementFile>();

	// Virtual machine timezone unlikely changes during execution of JVM
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z"))
			.format(new Date());
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	private SourceFile sourceFile;
	private String charValue;
	private String routingType;
	private MeasurementFile measFile = null;
	private MeasurementFile confTnRtMeasFile = null;
	private MeasurementFile confTnGenMeasFile = null;		
	//private MeasurementFile routeAccessMeasFile = null;
	// private MeasurementFile routeLinkOrderMeasFile = null;
	// private MeasurementFile tempMeasFile = null;
	private Logger log;
	private String techPack;
	private String setType;
	private String setName;
	private int status = 0;
	private Main mainParserObject = null;
	private String workerName = "";
	final private List<Exception> errorList = new ArrayList<Exception>();

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
				+ setName + ".parser.xml3GPP32435BCSParser" + logWorkerName);
	}

	public int status() {
		return status;
	}

	public List<Exception> errors() {
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
	public void startDocument() {
		log.log(Level.INFO, "Parsing the file ");
	}

	public void endDocument() throws SAXException {
		log.log(Level.INFO, "Parsing completed for the file ");
		// Close the measurement files for others
		if (confTnRtMeasFile != null) {

			try {

				confTnRtMeasFile.close();
			} catch (Exception e) {

				log.log(Level.FINEST, "Error closing measurement file", e);
				throw new SAXException("Error closing measurement file");

			}

		}

		// Close the measurement files for others
		if (confTnGenMeasFile != null) {

			try {

				confTnGenMeasFile.close();
			} catch (Exception e) {

				log.log(Level.FINEST, "Error closing measurement file", e);
				throw new SAXException("Error closing measurement file");

			}

		}
		// Close the measurement files for others		if (routeAccessMeasFile != null) {			try {				routeAccessMeasFile.close();			} catch (Exception e) {				log.log(Level.FINEST, "Error closing measurement file", e);				throw new SAXException("Error closing measurement file");			}		}
		// We have reached the end of the sample file
		// Close all the measurementFiles in the hashmap

		Iterator<String> it = mFileHashMap.keySet().iterator();

		while (it.hasNext()) {

			String strTagID = (String) it.next();

			this.measFile = mFileHashMap.get(strTagID);

			try {

				this.measFile.close();

			} catch (Exception e) {

				log.log(Level.FINEST, "Error closing measurement file", e);
				throw new SAXException("Error closing measurement file");

			}

		}

		// Clear the hashmap
		mFileHashMap.clear();

	}

	public void startElement(final String uri, final String name,
			final String qName, final Attributes atts) throws SAXException {

		charValue = "";

		// *********************DIM_E_IPTNMS_CIRCUIT_PATH***********************************

		// enable locks
		if (qName.equals("CircuitHO.toTP") || qName.equals("CircuitLO.toTP")
				|| qName.equals("CircuitNCV.toTP")
				|| qName.equals("PathOCH.toTP")) {

			toTPLock = true;
		}

		else if (qName.equals("CircuitHO.fromTP")
				|| qName.equals("CircuitLO.fromTP")
				|| qName.equals("CircuitNCV.fromTP")
				|| qName.equals("PathOCH.fromTP")) {

			fromTPLock = true;
		}

		else if (qName.equals("Path")) {

			attrTable.put("PATH", atts.getValue("Id").trim());
		}

		else if (qName.equals("Path.pathname")) {

			PathNameLock = true;
		}

		else if (qName.equals("CircuitHO") || qName.equals("CircuitLO")
				|| qName.equals("CircuitNCV") || qName.equals("PathOCH")) {

			attrTable.put("CIRCUIT_ID", atts.getValue("Id").trim());
		}

		// CircuitHO specific stuff
		else if (qName.equals("ListTPHO.theCTPHO")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("CTPHO")
						.trim());
			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("CTPHO")
						.trim());
			}

		}

		else if (qName.equals("ListTPHO.theTTPHO")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("TTPHO")
						.trim());

			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("TTPHO")
						.trim());
			}
		}

		// CircuitLO specific stuff
		else if (qName.equals("ListTPLO.theTTPLO")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("TTPLO")
						.trim());

			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("TTPLO")
						.trim());

			}
		}

		else if (qName.equals("ListTPLO.theCTPLO")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("CTPLO")
						.trim());

			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("CTPLO")
						.trim());

			}
		}

		// VCG specific stuff
		else if (qName.equals("ListTPNCV.theVCG")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("VCG")
						.trim());

			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("VCG").trim());

			}
		}

		// PathOCH specific stuff

		else if (qName.equals("ListTPOCH.theTTPOCH")) {

			if (toTPLock) {

				attrTable.put("CIRCUIT_START_POINT", atts.getValue("TTPOCH")
						.trim());

			}

			if (fromTPLock) {

				attrTable.put("CIRCUIT_END_POINT", atts.getValue("TTPOCH")
						.trim());

			}
		}

		// ****************DIM_E_IPTNMS_CIRCUIT_ACCESSPOINT******************

		else if (qName.equals("CTPHO") || qName.equals("TTPHO")
				|| qName.equals("VCG") || qName.equals("TTPLO")
				|| qName.equals("CTPOCH") || qName.equals("TTPOCH")
				|| qName.equals("CTPODU") || qName.equals("TTPODU") || qName.equals("CTPLO")) {

			circuitDTLock = true;

			attrTable.put("CT_TT_ID_VALUE", atts.getValue("Id").trim());

		}

		if (circuitDTLock) {

			if (qName.equals("AccessPoint")) {

				attrTable.put("ACCESS_POINT_ID", atts.getValue("Id").trim());
			}

			else if (qName.equals("AccessPoint.phLayerTTP")) {

				attrTable.put("ACC_PH_LAYER_TTP", atts.getValue("PhLayerTtp")
						.trim());
			}

			else if (qName.equals("AccessPoint.subNetworkId")) {

				accessSubNetworkIDLock = true;

			}

			else if (qName.equals("AccessPoint.physicalSlotId")) {

				// Need lock
				accessPhySlotLock = true;
			}

			else if (qName.equals("AccessPoint.channelName")) {

				// Need lock
				accessChannelNameLock = true;
			}

			else if (qName.equals("AccessPoint.cardId")) {
				// need lock
				accessSlotLock = true;
			}

			else if (qName.equals("AccessPoint.portId")) {
				// need lock
				accessPortLock = true;

			}

			else if (qName.equals("AccessPoint.subNetwork")) {

				attrTable.put("ACC_SUBNETWORK_NAME", atts
						.getValue("SubNetwork").trim());

			}

			else if (qName.equals("AccessPoint.shelfIdOnNM")) {

				accessPointShelfLock = true;
			}

		}

		// ***********************DIM_E_IPTNMS_LINKS_HOLINK***********************

		else if (qName.equals("HOLink") || qName.equals("LOLink")
				|| qName.equals("OCHLink") || qName.equals("PDHLink") || qName.equals("SDHLink")|| qName.equals("PhotonicLink")) {

			attrTable.put("LINK_ORDER", atts.getValue("Id").trim());
		}
		//store the value of ph_link from the logical files
		else if (qName.equals("HOLink.phLink") || qName.equals("LOLink.phLink")
				|| qName.equals("OCHLink.phLink") || qName.equals("PDHLink.phLink")
				|| qName.equals("SDHLink.phLink")|| qName.equals("PhotonicLink.phLink")) {
			attrTable.put("PH_LINK", atts.getValue("Link").trim());
		}
		//store the value of ph_link from the physical files
		else if (qName.equals("PhLink")) {

			attrTable.put("PH_LINK", atts.getValue("Id").trim());
		}

		else if (qName.equals("Link")) {

			attrTable.put("LINK_ID", atts.getValue("Id").trim());
		}

		else if (qName.equals("Link.toSN")) {

			attrTable.put("LINK_TO_SUB", atts.getValue("SubNetwork").trim());
		}

		else if (qName.equals("Link.fromSN")) {

			attrTable.put("LINK_FROM_SUB", atts.getValue("SubNetwork").trim());
		}

		else if (qName.equals("Link.linkName")) {

			HOLock = true;
		}

		else if (qName.equals("Link.toTP")) {

			attrTable.put("LINK_TO_PH_ID", atts.getValue("PhLayerTtp").trim());
		}

		else if (qName.equals("Link.fromTP")) {

			attrTable
					.put("LINK_FROM_PH_ID", atts.getValue("PhLayerTtp").trim());
		}

		else if (qName.equals("Link.state")) {

			dtStateLock = true;
		}

		// *******************************DIM_E_IPTNMS_LINKS_PHLAYERTTP*********************************

		else if (qName.equals("PhLayerTtp")) {

			phLayerLock = true;

			attrTable.put("PH_TTP_ID", atts.getValue("Id").trim());
		}

		else if (qName.equals("PhLayerTtp.subNetwork")) {

			attrTable
					.put("SUBNETWORK_NAME", atts.getValue("SubNetwork").trim());
		}

		else if (qName.equals("PhLayerTtp.cardId")) {

			phLayerCardLock = true;
		}

		else if (qName.equals("PhLayerTtp.portId")) {

			phLayerPortLock = true;
		}
		
		else if (qName.equals("PhLayerTtp.portType")) {

			phLayerPortTypeLock = true;
		}


		else if (qName.equals("PhLayerTtp.channelName")) {

			phLayerChannelLock = true;
		}

		else if (qName.equals("PhLayerTtp.shelfIdOnNM")) {

			phLayerShelfLock = true;
		}

		else if (qName.equals("PhLayerTtp.subNetworkId")) {

			phLayerSubIDLock = true;
		}

		// *********************DIM_E_IPTNMS_NBI_SUBNETWORK***********************************

		else if (qName.equals("SubNetwork")) {

			attrTable.put("SUBNETWORK_NAME", atts.getValue("Id").trim());
		}

		else if (qName.equals("RealSubNetwork.theNetworkElement")) {

			attrTable.put("NETWORK_ELEMENT", atts.getValue("NetworkElement")
					.trim());
		}

		else if (qName.equals("SubNetwork.longName")) {

			subNetworkLongNameLock = true;
		}

		else if (qName.equals("SubNetwork.subNetworkId")) {

			subNetworksubIDLock = true;
		}

		else if (qName.equals("SubNetwork.shortName")) {

			subNetworkShortNameLock = true;
		}

		// *********************DIM_E_IPTNMS_NBI_NETWORKELEMENT***********************************

		else if (qName.equals("NetworkElement")) {

			attrTable.put("NETWORK_ELEMENT", atts.getValue("Id").trim());

			networkElementLock = true;
		}

		else if (qName.equals("NetworkElement.EMId")) {

			networkElementCount = 1;
		}

		else if (qName.equals("NetworkElement.EMName")) {

			networkElementCount = 2;
		}

		else if (qName.equals("NetworkElement.NELongName")) {

			networkElementCount = 3;
		}

		else if (qName.equals("NetworkElement.NEShortName")) {

			networkElementCount = 4;
		}

		else if (qName.equals("NetworkElement.NEIdOnEM")) {

			networkElementCount = 5;
		}

		else if (qName.equals("NetworkElement.nEModel")) {

		      networkElementCount = 6;
		    }
		
		else if (qName.equals("NetworkElement.nEType")) {

			networkElementNELock = true;
		}

		// *********************DIM_E_IPTNMS_LINKS_ROUTE***********************************

		else if (qName.equals("PathRouting")) {

			attrTable.put("PATH_ROUTING_ID", atts.getValue("Id").trim());

		}

		else if (qName.equals("PathRouting.thePath")) {

			attrTable.put("PATH", atts.getValue("Path").trim());

		}

		else if (qName.equals("PathRouting.pathId")) {

			routing_PathIDLock = true;

		}

		else if (qName.equals("RouteNode.thePath")) {

			attrTable.put("ROUTE_NODE_PATH", atts.getValue("Path").trim());

		}

		else if (qName.equals("RouteNode.theTp")) {

			MeasurementFile routeAccessMeasFile;

			try {

				routeAccessMeasFile = Main.createMeasurementFile(sourceFile,
						routingType, techPack, setType, setName, workerName,
						log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}
			routeAccessMeasFile.addData("ACCESS_POINT",
					atts.getValue("AccessPoint"));

			routeNodeTheTPCount++;

			String routeAccessPointTag = "ACCESSPOINT_"
					+ Integer.toString(routeNodeTheTPCount);

			measFileAccessHashMap.put(routeAccessPointTag, routeAccessMeasFile);


		}

		// SIMPLE ROUTING Specific

		else if (qName.equals("SimpleRouting")) {

			routingType = "SimpleRouting";

			attrTable.put("ROUTING_TYPE", atts.getValue("Id").trim());

		}

		else if (qName.equals("Route.links")) {

			if (routingType.equals("ProtectionRouting")) {

				routeNodeLinkCount++;

				String routeLinkOrderTag = "ROUTE_LINK_ORDER_"
						+ Integer.toString(routeNodeLinkCount);

				measFileHashMap.put(routeLinkOrderTag, atts.getValue("Link")
						.trim());

			}

			else {

				attrTable.put("ROUTE_LINK_ORDER", atts.getValue("Link").trim());
			}

		}

		// PROTECTION ROUTING specific

		else if (qName.equals("ProtectionRouting")) {

			attrTable.put("ROUTING_TYPE", atts.getValue("Id").trim());

			routingType = "ProtectionRouting";

		}

		else if (qName.equals("PathRouting.pathType")) {

			routePathTypeLock = true;

		}

	}

	public void endElement(final String uri, final String name,
			final String qName) throws SAXException {

		// *********************DIM_E_IPTNMS_CIRCUIT_PATH***********************************

		// reset locks
		if (qName.equals("CircuitHO.toTP") || qName.equals("CircuitLO.toTP")
				|| qName.equals("CircuitNCV.toTP")
				|| qName.equals("PathOCH.toTP")) {

			toTPLock = false;
		}

		else if (qName.equals("CircuitHO.fromTP")
				|| qName.equals("CircuitLO.fromTP")
				|| qName.equals("CircuitNCV.fromTP")
				|| qName.equals("PathOCH.fromTP")) {

			fromTPLock = false;
		}

		else if (qName.equals("DTString") && PathNameLock) {

			attrTable.put("PATH_NAME", this.charValue.trim());

			PathNameLock = false;
		}

		// Path - Circuit HO specific

		else if (qName.equals("CircuitHO") || qName.equals("CircuitLO")
				|| qName.equals("CircuitNCV") || qName.equals("PathOCH")) {

			// create meas file to hold data

			try {

				measFile = Main.createMeasurementFile(sourceFile, qName,
						techPack, setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}

			try {

				if (measFile == null) {

					System.err.println("measurement file null");

				} else {

					measFile.addData(attrTable);
					measFile.setData(attrTable);
					measFile.saveData();
					attrTable.clear();
					measFile.close();
				}

			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

		}

		// ****************DIM_E_IPTNMS_CIRCUIT_ACCESSPOINT******************

		else if (qName.equals("DTInteger") && accessSubNetworkIDLock) {

			attrTable.put("ACC_SUBNETWORK_ID", this.charValue.trim());

			accessSubNetworkIDLock = false;

		}

		else if (qName.equals("DTInteger") && accessPointShelfLock) {

			attrTable.put("ACC_SHELF_ID", this.charValue.trim());

			accessPointShelfLock = false;

		}

		else if (qName.equals("DTString") && accessChannelNameLock) {

			attrTable.put("ACCPOINT_CHANNEL_NAME", this.charValue.trim());

			accessChannelNameLock = false;

		}

		else if (qName.equals("DTInteger") && accessPhySlotLock) {
			attrTable.put("ACC_PHY_SLOT_ID", this.charValue.trim());

			accessPhySlotLock = false;
		}

		else if (qName.equals("DTInteger") && accessSlotLock) {
			attrTable.put("ACCPOINT_SLOT_ID", this.charValue.trim());

			accessSlotLock = false;
		}

		else if (qName.equals("DTInteger") && accessPortLock) {
			attrTable.put("ACC_PORT_ID", this.charValue.trim());

			accessPortLock = false;
		}

		else if (qName.equals("CTPHO") || qName.equals("TTPHO")
				|| qName.equals("VCG") || qName.equals("TTPLO")
				|| qName.equals("CTPOCH") || qName.equals("TTPOCH")
				|| qName.equals("CTPODU") || qName.equals("TTPODU") || qName.equals("CTPLO")) {

			// create meas file to hold data

			try {

				measFile = Main.createMeasurementFile(sourceFile, qName,
						techPack, setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}

			try {

				if (measFile == null) {

					System.err.println("Measurement file null");

				} else {

					measFile.addData(attrTable);
					measFile.setData(attrTable);
					measFile.saveData();
					attrTable.clear();
					//TR Fix for HS81192
					measFile.close();

				}

			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

			circuitDTLock = false;

		}

		// ***********************DIM_E_IPTNMS_LINKS_HOLINK***********************

		else if (HOLock && qName.equals("DTString")) {

			attrTable.put("LNG_LINK_NAME", this.charValue.trim());

			HOLock = false;

		}

		else if (dtStateLock && qName.contains("DTState.")) {

			String[] DTstate = qName.split("\\.");

			attrTable.put("LINK_STATE", DTstate[1].trim());

			dtStateLock = false;

		}

		// HOLink specific

		else if (qName.equals("HOLink") || qName.equals("LOLink")
				|| qName.equals("OCHLink")|| qName.equals("PDHLink")
				|| qName.equals("SDHLink")|| qName.equals("PhotonicLink")) {

			// create meas file to hold data

			try {

				measFile = Main.createMeasurementFile(sourceFile, qName,
						techPack, setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}

			try {

				if (measFile == null) {

					System.err.println("Measurement file null");

				} else {

					measFile.addData(attrTable);
					measFile.setData(attrTable);
					measFile.saveData();
					attrTable.clear();
					measFile.close();
				}

			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

		}

		// *******************************DIM_E_IPTNMS_LINKS_PHLAYERTTP*********************************

		else if (phLayerCardLock && qName.equals("DTInteger")) {
			attrTable.put("CARD_ID", this.charValue.trim());

			phLayerCardLock = false;
		}

		else if (phLayerPortLock && qName.equals("DTInteger")) {

			attrTable.put("PORT_ID", this.charValue.trim());

			phLayerPortLock = false;
		}
		
		else if (phLayerPortTypeLock && qName.contains("DTPortType.")) {

			String[] DTportType = qName.split("\\.");
			log.info("PORT_TYPE:"+DTportType[1].trim());
			attrTable.put("PORT_TYPE", DTportType[1].trim());

			phLayerPortTypeLock = false;
			
		}

		else if (phLayerChannelLock && qName.equals("DTString")) {

			attrTable.put("PHLAYER_CHANNEL_NAME", this.charValue.trim());

			phLayerChannelLock = false;
		}

		else if (phLayerShelfLock && qName.equals("DTInteger")) {

			attrTable.put("PHLAYER_SHELF_ID", this.charValue.trim());

			phLayerShelfLock = false;

		}

		else if (phLayerSubIDLock && qName.equals("DTInteger")) {

			attrTable.put("PHLAYER_SUBNETWORK_ID", this.charValue.trim());

			phLayerSubIDLock = false;

		}

		else if (qName.equals("PhLayerTtp")) {

			// create meas file to hold data

			try {

				measFile = Main.createMeasurementFile(sourceFile, qName,
						techPack, setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}

			try {

				if (measFile == null) {

					System.err.println("Measurement file null");

				} else {

					measFile.addData(attrTable);
					measFile.setData(attrTable);
					measFile.saveData();
					attrTable.clear();
					measFile.close();

				}

			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

			phLayerLock = false;
		}

		// *********************DIM_E_IPTNMS_NBI_SUBNETWORK***********************************

		else if (subNetworkLongNameLock && qName.equals("DTString")) {

			attrTable.put("SUB_LONG_NAME", this.charValue.trim());

			subNetworkLongNameLock = false;
		}

		else if (subNetworkShortNameLock && qName.equals("DTString")) {

			attrTable.put("SUB_SHORT_NAME", this.charValue.trim());

			subNetworkShortNameLock = false;
		}

		else if (subNetworksubIDLock && qName.equals("DTInteger")) {

			attrTable.put("SUBNETWORK_ID", this.charValue.trim());

			subNetworksubIDLock = false;
		}

		else if (qName.equals("RealSubNetwork")) {

			// create meas file to hold data

			try {

				measFile = Main.createMeasurementFile(sourceFile, qName,
						techPack, setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST,
						"Error getting measurement file details ", e);
				e.printStackTrace();
				throw new SAXException(
						"Error getting measurement file details "
								+ e.getMessage(), e);

			}

			try {

				if (measFile == null) {

					System.err.println("Measurement file null");

				} else {

					measFile.addData(attrTable);
					measFile.setData(attrTable);
					measFile.saveData();
					attrTable.clear();
					measFile.close();
				}

			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}

		}

		// *********************DIM_E_IPTNMS_NBI_NETWORKELEMENT***********************************

		else if (networkElementLock) {

			if (qName.equals("DTInteger")) {

				if (networkElementCount == 1) {

					attrTable.put("EM_ID", this.charValue.trim());

					networkElementCount = 0;
				}

				else if (networkElementCount == 5) {

					attrTable.put("NE_ID", this.charValue.trim());

					networkElementCount = 0;
				}

			}

			else if (qName.equals("DTString")) {

				if (networkElementCount == 2) {

					attrTable.put("EM_NAME", this.charValue.trim());

					networkElementCount = 0;
				}

				else if (networkElementCount == 3) {

					attrTable.put("NE_LONG_NAME", this.charValue.trim());

					networkElementCount = 0;
				}

				else if (networkElementCount == 4) {

					attrTable.put("NE_SHORT_NAME", this.charValue.trim());

					networkElementCount = 0;
				}
				
				else if (networkElementCount == 6) {

			          attrTable.put("NE_MODEL", this.charValue.trim());

			          networkElementCount = 0;
			        }

			}

			else if (networkElementNELock && qName.contains("DTNEType.")) {

				String[] DTNEType = qName.split("\\.");

				attrTable.put("NE_TYPE", DTNEType[1].trim());

				networkElementNELock = false;
			}

			else if (qName.equals("NetworkElement")) {

				// create meas file to hold data

				try {

					measFile = Main.createMeasurementFile(sourceFile, qName,
							techPack, setType, setName, workerName, log);

				} catch (Exception e) {

					log.log(Level.FINEST,
							"Error getting measurement file details ", e);
					e.printStackTrace();
					throw new SAXException(
							"Error getting measurement file details "
									+ e.getMessage(), e);

				}

				try {

					if (measFile == null) {

						System.err.println("Measurement file null");

					} else {

						measFile.addData(attrTable);
						measFile.setData(attrTable);
						measFile.saveData();
						attrTable.clear();
						measFile.close();

					}

				} catch (Exception e) {

					log.log(Level.FINEST, "Error saving measurement data", e);
					e.printStackTrace();
					throw new SAXException("Error saving measurement data: "
							+ e.getMessage(), e);
				}

				networkElementLock = false;
				networkElementCount = 0;
			}

		}

		// *********************DIM_E_IPTNMS_LINKS_ROUTE***********************************

		else if (qName.equals("DTInteger") && routing_PathIDLock) {

			attrTable.put("PATH_ID", this.charValue.trim());

			routing_PathIDLock = false;

		}

		else if (qName.contains("DTPathType.") && routePathTypeLock) {

			String[] DTRoutePathType = qName.split("\\.");

			attrTable.put("PATH_TYPE", DTRoutePathType[1].trim());

			routePathTypeLock = false;

		}

		// Save routing data

		else if (qName.equals("ProtectionRouting")) {

			// The while loop below is to save the distinct access_point KVP to
			// a measfile along with the other criteria needed

			Iterator<String> it2 = measFileAccessHashMap.keySet().iterator();

			Iterator<String> it = measFileHashMap.keySet().iterator();

			while (it2.hasNext()) {

				String strTagID1 = (String) it2.next();

				String curVal;

				if (it.hasNext()) {

					String strTagID = (String) it.next();

					curVal = measFileHashMap.get(strTagID);

				}

				else {
					curVal = "";
				}

				attrTable.put("ROUTE_LINK_ORDER", curVal);

				MeasurementFile tempMeasFile = measFileAccessHashMap
						.get(strTagID1);

				try {

					tempMeasFile.addData(attrTable);
					tempMeasFile.saveData();
					tempMeasFile.close();

				} catch (Exception e) {

					log.log(Level.FINEST,
							"Error with access_point measurement file", e);
					throw new SAXException(
							"Error with access_point measurement file");

				}

			}

			routeNodeTheTPCount = 0;

			routeNodeLinkCount = 0;

			measFileHashMap.clear();

			measFileAccessHashMap.clear();

			attrTable.clear();

		}

		else if (qName.equals("SimpleRouting")) {

			// Simplerouting doesn't need a save method for link_order, as there
			// is only ever 1 link order kvp expected
			// The while loop below is to save the distinct access_point KVP to
			// a measfile along with the other criteria needed

			Iterator<String> it2 = measFileAccessHashMap.keySet().iterator();

			while (it2.hasNext()) {

				String strTagID = (String) it2.next();

				MeasurementFile tempMeasFile = measFileAccessHashMap
						.get(strTagID);

				try {

					tempMeasFile.addData(attrTable);
					tempMeasFile.saveData();
					tempMeasFile.close();

				} catch (Exception e) {

					log.log(Level.FINEST,
							"Error with access_point measurement file", e);
					throw new SAXException(
							"Error with access_point measurement file");

				}

			}

			routeNodeTheTPCount = 0;

			routeNodeLinkCount = 0;

			measFileHashMap.clear();

			measFileAccessHashMap.clear();

			attrTable.clear();

		}

		else {
			attrTable.put(qName, this.charValue.trim());
			// We have to save everything, so this is how it saves irrelevent
			// stuff
		}

	}

	/**
	 * Fetches the Measurementfile for the vendor Id.
	 * 
	 * @param index
	 * @return MeasurementFile object
	 */

	private MeasurementFile getMeasFile(final String index) throws SAXException {

		// For PPM - tag id (index) would be EntityIdentity value

		if (mFileHashMap.containsKey(index)) {

			return mFileHashMap.get(index);
		} else {

			MeasurementFile mf = null;

			try {

				mf = Main.createMeasurementFile(sourceFile, index, techPack,
						setType, setName, workerName, log);

			} catch (Exception e) {

				log.log(Level.FINEST, "Error opening measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error opening measurement data: "
						+ e.getMessage(), e);
			}

			mFileHashMap.put(index, mf);
			return mf;

		}

	}

	public static void main(String[] args) {
		int argnum = 0;
		IptnmsCSParser np = null;
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
			np = new IptnmsCSParser();
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