/*
 * Created on 7.9.2011
 *
 */
package com.distocraft.dc5000.etl.mlxml ;


import java.io.File;
import java.io.FileInputStream;

import java.text.ParseException;
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
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;

import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.eniq.common.ENIQEntityResolver;

/**
 * MiniLink Parser: Adapter implementation that parses the Mini Link  measurement data..  <br><br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * <br>
 * Version supported: v 7.20 <br>
 * <br>
 * Copyright Ericsson 2011 <br>
 * <br>
 * $id$ <br>
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
 * <td>Name val</td>
 * <td>Key Val</td>
 * <td>Description</td>
 * <td>Default</td>
 * </tr>
 * 
 * </table> <br>
 * <br>
 * <br>
 * 
 * <br><br> 
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns ( for PIC )</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>emIdNum</td>
 * <td>Contains the EM identifier EMId (Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>emId</td>
 * <td>Contains the EM Id: EM-EMId (Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>emMnr</td>
 * <td>Contains the attribute of EMName - MNR:The Marconi Network Release (Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>emVersion</td>
 * <td>Contains the attribute of EMName - version:EM release (Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>emName</td>
 * <td>Contains the EMName (Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the time stamp in format YYYY-MM-DD HH.MM.SS (example.2011-05-26 01.15.00)(Tag ID = EM) </td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the filename of the input data file.(Tag ID = EM)</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the input data file.(Tag ID = EM)</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Contains the JVM time zone (example. +0200)(Tag ID = EM) </td>
 * </tr> 
 * <tr>
 * <td> - </td>
 * <td> - </td>
 * </tr>
 * <tr>
 * <td>emId</td>
 * <td>Contains the EM Id: EM-EMId (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>NEIdOnEM</td>
 * <td>Contains the NE identifier onto the EM database (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>longName</td>
 * <td>Contains the whole NE name (Tag ID = NE) </td>
 * </tr>
 * <tr>
 * <td>NEShortName</td>
 * <td>Contains the NE acronym (in the GUI)(Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>NESuffix</td>
 * <td>Contains the NE Id number (in the GUI) (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>EMPlugin</td>
 * <td>Contains the EMPlugin (Tag ID = NE) </td>
 * </tr>
 * <tr>
 * <td>NEBasicTypeId</td>
 * <td>Contains the EM plugin identifier (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>EMPlugin_Version</td>
 * <td>Contains the EM plugin release (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>NEType</td>
 * <td>Contains the NEType (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>NEModel</td>
 * <td>Contains the model of NE (ADM, DXC, etc) (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>NETypeId</td>
 * <td>Contains the type of NE identifier (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>IP_ADDRESS</td>
 * <td>Contains the IP address (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>PORT</td>
 * <td>Contains the Port number (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>LOC_ADDR</td>
 * <td>Contains the subentity Address of Location  (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>LOC_ROOM</td>
 * <td>Contains the subentity Room of Location  (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>LOC_ROW</td>
 * <td>Contains the subentity row of Location  (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>LOC_RACK</td>
 * <td>Contains the subentity rack of Location  (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>LOC_SUBRACK</td>
 * <td>Contains the subentity Subrack of Location  (Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the time stamp in format YYYY-MM-DD HH.MM.SS (example.2011-05-26 01.15.00)(Tag ID = NE) </td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the filename of the input data file.(Tag ID = NE)</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the input data file.(Tag ID = NE)</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Contains the JVM time zone (example. +0200)(Tag ID = NE) </td>
 * </tr> 
 * <tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * <br>
 * <br>
 * 
 * <br><br> 
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns ( for PPM )</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>NEIdOnEM</td>
 * <td>Contains the NE identifier onto the EM database </td>
 * </tr>
 * <tr>
 * <td>longName</td>
 * <td>Contains the whole NE name </td>
 * </tr>
 * <tr>
 * <td>NEShortName</td>
 * <td>Contains the NE acronym (in the GUI)</td>
 * </tr>
 * <tr>
 * <td>NESuffix</td>
 * <td>Contains the NE Id number (in the GUI) </td>
 * </tr>
 * <tr>
 * <td>EMPlugin</td>
 * <td>Contains the EMPlugin </td>
 * </tr>
 * <tr>
 * <td>NEBasicTypeId</td>
 * <td>Contains the EM plugin identifier </td>
 * </tr>
 * <tr>
 * <td>EMPlugin_Version</td>
 * <td>Contains the EM plugin release </td>
 * </tr>
 * <tr>
 * <td>NEType</td>
 * <td>Contains the NEType </td>
 * </tr>
 * <tr>
 * <td>NEModel</td>
 * <td>Contains the model of NE (ADM, DXC, etc) </td>
 * </tr>
 * <tr>
 * <td>NETypeId</td>
 * <td>Contains the type of NE identifier</td>
 * </tr>
 * <tr>
 * <td>IP_ADDRESS</td>
 * <td>Contains the IP address </td>
 * </tr>
 * <tr>
 * <td>PORT</td>
 * <td>Contains the Port number </td>
 * </tr>
 * <tr>
 * <td>Entity Id</td>
 * <td>Contains Entity identification</td>
 * </tr>
 * * <td>EM_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.EM_ID value will be 1</td>
 * <td>Element manager Identification(SO-EM id)</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>NE_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.NE_ID value will be 2</td>
 * <td>Network entity Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>SHELF_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.SHELF_ID value will be 3</td>
 * <td>Shelf Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>PHYSICAL_SLOT_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.PHYSICAL_SLOT_ID value will be 4</td>
 * <td>Physical Slot Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>CARD_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.CARD_ID value will be 5</td>
 * <td>Slot Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>EQUIP_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.EQUIP_ID value will be 4-5</td>
 * <td>Equipment Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>TSCHEMA</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.TSCHEMA value will be 6</td>
 * <td>Element Scheme</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>PORT_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.PORT_ID value will be 7</td>
 * <td>Port Number</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>SK1</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.SK1 value will be 8</td>
 * <td>Schema Key1</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>SK2</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.SK2 value will be 9</td>
 * <td>Schema Key2</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>SK3</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.SK3 value will be 10</td>
 * <td>Schema Key3</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>SK4</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.SK4 value will be 11</td>
 * <td>Schema Key4</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>COMPLIANCE_ID</td>
 * <td>Will be fetched from Entity Id.For Example if Entity Id is EN-1-2-3-4-5-6-7-8-9-10-11-12-13.COMPLIANCE_ID value will be 14</td>
 * <td>Compliance Identification</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>sourceId</td>
 * <td>Contains PM source identification</td>
 * </tr>
 * <tr>
 * <td>Entity</td>
 * <td>Contains the Entity of the table </td>
 * </tr>	
 * <tr>
 * <td>Counter Name (Eg.UAS, ES etc)</td>
 * <td>Contains the corresponding counter value </td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the time stamp in format YYYY-MM-DD HH.MM.SS (example.2011-05-26 01.15.00) </td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the filename of the input data file.</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the input data file.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Contains the JVM time zone (example. +0200) </td>
 * </tr>
 * <tr>
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * <br>
 * <br>
 * 
 * <br><br> 
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns ( for FTP Config Data )</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>neId</td>
 * <td>Contains the NEID (TagId = CONF_TN_GEN) </td>
 * </tr>
 * <tr>
 * <td>NE_ALIAS</td>
 * <td>Ne alias in So-EM manager (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>NE_NAME</td>
 * <td>Contains the NE Name (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>LOCATION</td>
 * <td>Physical location of node (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>CONTACT</td>
 * <td>Contains the contact details (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>NODENAME</td>
 * <td>Contains the So-EM Manager nodename Configuration data is exported from (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>DCN_HOST_ADDR</td>
 * <td>Contains the DCN host address (TagId = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the time stamp in format YYYY-MM-DD HH.MM.SS (example.2011-05-26 01.15.00)(Tag ID = CONF_TN_GEN) </td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the filename of the input data file.(Tag ID = CONF_TN_GEN)</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the input data file.(Tag ID = CONF_TN_GEN)</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Contains the JVM time zone (example. +0200)(Tag ID = CONF_TN_GEN) </td>
 * </tr> 
 * <tr>
 * <td> - </td>
 * <td> - </td>
 * </tr>
 * <td>NODENAME</td>
 * <td>Contains the So-EM Manager nodename Configuration data is exported from (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>neId</td>
 * <td>Contains the NE identifier onto the EM database (TagId = CONF_TN_RT)</td>
 * </tr>
 * <td>NE_ALIAS</td>
 * <td>Ne alias in So-EM manager (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>RADIO_TRML_NAME</td>
 * <td>Contains the Radio Terminal Name (TagId = CONF_TN_RT) </td>
 * </tr>
 * <tr>
 * <td>TERMINAL_ID</td>
 * <td>Contains the Terminal ID </td>
 * </tr>
 * <tr>
 * <td>EQUIP_TYPE</td>
 * <td>Contains the type of equipment (TagId = CONF_TN_RT)  </td>
 * </tr>
 * <tr>
 * <td>PROT_MODE_ADM_STATUS</td>
 * <td>Contains the The equipment protection mode (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>EQ_PROT_ACTIVE_UNIT</td>
 * <td>Contains the equipment protection  active unit (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>CAPACITY</td>
 * <td>Contains the Traffic Capacity for the terminal (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>FAR_END_TERM_NAME</td>
 * <td>Contains the Expected network identity for the remote terminal (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>FAR_END_ID</td>
 * <td>Contains the Far end terminal id (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>FAR_END_TYPE</td>
 * <td>Contains the Far end terminal type (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>ACTIVE_TX_RADIO</td>
 * <td>Contains the Active radio settings. Inherent protect switch (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>INSTANCE_RA1</td>
 * <td>Contains the The textual name of the RAU1 interface (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>INSTANCE_RA2</td>
 * <td>Contains the The textual name of the RAU2 interface (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>FREQ_BAND_RA1</td>
 * <td>Contains the RAU frequency band Ra1 (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>FREQ_BAND_RA2</td>
 * <td>Contains the RAU frequency band Ra2 (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>INSTANCE_RF1</td>
 * <td>Contains the the textual name of the interface RF1 (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>INSTANCE_RF2</td>
 * <td>Contains the the textual name of the interface RF2 (TagId = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>Contains the time stamp in format YYYY-MM-DD HH.MM.SS (example.2011-05-26 01.15.00)(Tag ID = CONF_TN_RT) </td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>Contains the filename of the input data file.(Tag ID = CONF_TN_RT)</td>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>EMPTY</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the input data file.(Tag ID = CONF_TN_RT)</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>Contains the JVM time zone (example. +0200)(Tag ID = CONF_TN_RT) </td>
 * </tr> 
 * <tr>
 * <td>&nbsp;</td>
 * <td>&nbsp;</td>
 * </tr>
 * </table>
 * @author eananvi
 * 
 * 
 * */


public class mlXmlParser extends DefaultHandler implements MemoryRestrictedParser {
	
	
  private String neIdOnEm     ;
  private String neLongName  ;
  private String neShortName   ;
  private String neSuffix;
  private String neEmPlugin;
  private String neEmPluginNeBasicTypeId ;
  private String neEmPluginVersion;
  private String neType;
  private String neTypeId;
  private String neModel ;
  private String neIpAddress;
 // private String nePort;
  private String neRoom;
  private String neAddr ;
  private String neRow;
  private String neRack;
  private String neSubRack;
  private String neEmId ;



  private String entityId     ;
  private String emId         ;
  private String neId         ;


  private String sourceId     ;

  
  
  private String counterName ;
  private String tagID ;
  private String localTimeFormatId;
  private String vendorId ;
  
  
  // Topology specific
  private String emMnr ;
  private String emVersion ;
  private String emName ;
  private String emIdNum;
  
  // For FTP Config Data
  private String ftpNeId             = null ;
  private String nodeName            = null ;
  private String neAlias             = null ;
  private String radioTerminalName   = null ;
  private String terminalId          = null ;
  private String equipmentType       = null ;
  private String protModeAdmStatus   = null ;
  private String eqProtActUnit       = null ;
  private String capacity            = null ;
  private String farEndTermName      = null ;
  private String farEndId            = null ;
  private String farEndType          = null ;
  private String activeTxRadio       = null ;
  private String instanceRa1         = null ;
  private String instanceRa2         = null ;
  private String freqBandRa1         = null ;
  private String freqBandRa2         = null ;
  private String instanceRf1         = null ;
  private String instanceRf2         = null ;
  private String location            = null ;
  private String contact             = null ;
  private String dcnHostAddress      = null ;
  private String neName              = null ;
  private String modulation = null;
  private String packetLinkCap = null;
  private String currInpPower1 = null;
  private String currInpPower2 = null;
  private String equpProtecMode  = null;
  private String empluginName = null;
  
   
  //added for CONF_MLE (topology)
  
  private String mleNeId             = null ;
  private String ownID               = null ;
  private String trafficRate         = null ;
  private String protectionMode      = null ;
  private String IDSInOwnAMM         = null ;
  private String NCCAMNo             = null ;
  private String IDSInConnectedAMM   = null ;
  private String IDSInConnectedRTU   = null ;
  private String activeRadio         = null ;
  private String freqIndexRa1        = null ;
  private String freqIndexRa2        = null ;
  private String channelSpacing      = null ;  
   
  private HashMap<String, MeasurementFile> mFileHashMap = new HashMap<String, MeasurementFile>();
  private HashMap<String,String> primKeysdata = new HashMap<String,String>();
	private ArrayList<String> sourceIdValues = new ArrayList<String>();
	private HashMap<String,String> entitySoamData = new HashMap<String,String>();
	private HashMap<String, ArrayList<String>>sourceIdValues1 =  new HashMap<String,ArrayList<String>>();
  // Virtual machine timezone unlikely changes during execution of JVM
  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

  
  private SourceFile      sourceFile;
  private String          charValue;
  private MeasurementFile measFile = null;
  private MeasurementFile confTnRtMeasFile  = null;
  private MeasurementFile confMleMeasFile  = null;
  private MeasurementFile confTnGenMeasFile = null;
  private Logger          log;
  private String          techPack;
  private String          setType;
  private String          setName;
  private int             status = 0;
  private Main            mainParserObject = null;
  private String          workerName = "";
  final private List      errorList = new ArrayList();
  private String[] soamKeys = {"MEG_LEVEL","MEP_DIRECTION","DEST_MEP_ID","SOURCE_MEP_ID","SERVICE_VLAN_ID"};
  private String[] primaryKeys = {"EM_ID","NE_ID","SHELF_ID","PHYSICAL_SLOT_ID","CARD_ID","TSCHEMA","PORT","SK1","SK2","SK3","SK4","ENTITY_ID"};
  private String sourceid1;
  final int INTERVALS_15 = 900000; // 15 min = 15 * 60 * 1000 in mili seconds
  final int INTERVALS_24 = 86400000; // 24 h = 24 * 60 * 60 * 1000 in milli seconds
  private int memoryConsumptionMB = 0;
  	
  	/*
	 * Variables for ShelfId Handling
	 * */ 
  	private String shelfId;
	private String shelfIdOnNM;
	private String actualShelfId;
	private String entityShelfId;
	private Boolean iptnms_installed = null;
	private HashMap<String, String> shelfMap = new HashMap<String, String>();
	DataFormatCache dfc = DataFormatCache.getCache();
	private boolean checkNeg=false;
  /**
   * 
   */
  public void init(final Main main, final String techPack, final String setType, final String setName, final String workerName) {
	  
    this.mainParserObject = main;
    this.techPack         = techPack;
    this.setType          = setType;
    this.setName          = setName;
    this.status           = 1;
    this.workerName       = workerName;

    String logWorkerName = "";
    if (workerName.length() > 0) {
      logWorkerName = "." + workerName;
    }
    this.memoryConsumptionMB = mainParserObject.getNextSFMemConsumptionMB();
		log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.mlXmlParser" + logWorkerName);
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

      File is_iptnms = new File("/eniq/sw/bin/IPTNMS_Installed");
      if (is_iptnms.exists()){
			iptnms_installed = true;
      } else {
			iptnms_installed = false;
      }

      while ((sf = mainParserObject.nextSourceFile()) != null) {

        try {
        	this.memoryConsumptionMB = sf.getMemoryConsumptionMB();
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
  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
  throws Exception {
    this.measFile = null;
    final long start = System.currentTimeMillis();
    this.sourceFile = sf;

    
    SAXParserFactory spf = SAXParserFactory.newInstance();


    SAXParser parser = spf.newSAXParser();
    
    final XMLReader xmlReader = parser.getXMLReader();
    String id    = "http://apache.org/xml/properties/input-buffer-size";
    Object value = new Integer(16384);
    try {
    	xmlReader.setProperty(id, value);
    } 
    catch (SAXException e) {
        System.err.println("could not set parser property");
    }
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
    final long middle = System.currentTimeMillis();
    xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));
    final long end = System.currentTimeMillis();
    log.log(Level.FINER, "Data parsed. Parser initialization took "+ (middle-start)+ " ms, parsing "+(end-middle)+ " ms. Total: "+(end-start)+ " ms.");
  
  }

  /**
   * 
   */
  public void parse(final FileInputStream fis) throws Exception {

    final long start = System.currentTimeMillis();
    
    SAXParserFactory spf = SAXParserFactory.newInstance();
    final SAXParser parser = spf.newSAXParser();
    final XMLReader xmlReader = parser.getXMLReader();
    String id    = "http://apache.org/xml/properties/input-buffer-size";
    Object value = new Integer(16384);
    try {
    	xmlReader.setProperty(id, value);
    } 
    catch (SAXException e) {
        System.err.println("could not set parser property");
    }
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);
    final long middle = System.currentTimeMillis();
    
    xmlReader.parse(new InputSource(fis));
    
    final long end = System.currentTimeMillis();
    log.log(Level.FINEST, "Data parsed. Parser initialization took "+ (middle-start)+ " ms, parsing "+(end-middle)+ " ms. Total: "+(end-start)+ " ms.");
  }




  /**
   * Event handlers
   */
  public void startDocument() {
	  if(!(mFileHashMap.isEmpty())){
		  mFileHashMap.clear();
	  }
		sourceIdValues1.clear();
  }

  public void endDocument() throws SAXException {
	  
	  
	  // Close the measurement files for others
	  if ( confTnRtMeasFile != null  ){
		  
		  try{
			  
			  confTnRtMeasFile.close();
		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error closing measurement file", e);
			  throw new SAXException("Error closing measurement file");				  

		  }
		  
	  }
	  
	  // Close the measurement files for others
	  if ( confTnGenMeasFile != null  ){
		  
		  try{
			  
			  confTnGenMeasFile.close();
		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error closing measurement file", e);
			  throw new SAXException("Error closing measurement file");				  

		  }
		  
	  }
		if ( confMleMeasFile != null  ){
			try{

				confMleMeasFile.close();
			} catch (Exception e) {

				log.log(Level.FINEST, "Error closing measurement file", e);
				throw new SAXException("Error closing measurement file");				  

			}
		}
	  
	  // We have reached the end of the sample file
	  // Close all the measurementFiles in the hashmap

	  Iterator it = mFileHashMap.keySet().iterator() ;

	  this.measFile = null ;

	  while (it.hasNext()) {

		  String strTagID = (String)it.next();

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
	  this.measFile = null ;
	 primKeysdata.clear();
	  
  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
  throws SAXException {
	  
	  charValue = "";
	  
	  if (qName.equals("DocGenerator")) {
		  
		  this.localTimeFormatId = atts.getValue("date"); // In PPM files, this value will be overwritten at later stage during parsing.
	  }
	  else if (qName.equals("NE") ){
		  
		  this.neIdOnEm = atts.getValue("NEIdOnEM");
		  this.neId     = atts.getValue("Id");
		  this.neEmId   = atts.getValue("emId");
	  }
	  
	  else if (qName.equals("NEName") ){
		  
		  this.neLongName = atts.getValue("longName");
	  }
	  else if (qName.equals("EMPlugin") ){
		  
		  this.neEmPluginNeBasicTypeId = atts.getValue("NEBasicTypeId");
		  this.neEmPluginVersion = atts.getValue("version");
	  }
	  else if (qName.equals("NEType") ){
		  
		  this.neModel  = atts.getValue("NEModel");
		  this.neTypeId = atts.getValue("NETypeId");
	  }
	  else if (qName.equals("EM") ){
		  
		  this.emIdNum   = atts.getValue("EMId");
		  this.emId      = atts.getValue("Id");
		    
		  
	  }
	  else if (qName.equals("EMName") ){
		  
		  this.emMnr     = atts.getValue("MNR");
		  this.emVersion = atts.getValue("version");
	  }
	  else if (qName.equals("Source")){
			
		  this.sourceid1  = atts.getValue("Id");
	  }
		  
	  
	else if (qName.equals("SchemeSOAM") ){		  
			sourceIdValues.add(atts.getValue("megLevel"));
			sourceIdValues.add(atts.getValue("mepDirection"));
			sourceIdValues.add(atts.getValue("remoteMepId"));
			sourceIdValues.add(atts.getValue("sourceMepId"));
			sourceIdValues.add(atts.getValue("vlan_isidId")); 
			if(!(sourceIdValues.isEmpty())){
			sourceIdValues1.put( sourceid1,new ArrayList<String>(sourceIdValues));
			}
			sourceIdValues.clear();
		 
	}
	else if (qName.equals("Shelf")) {
		if (iptnms_installed){
			this.shelfId = atts.getValue("Id");
			this.shelfIdOnNM = atts.getValue("ShelfIdOnNM");
			shelfMap.put(shelfId, shelfIdOnNM);
		}
		
	}
  	  
	  else if ( qName.equals("Entity")){
			entitySoamData.clear();
		/*  this.entityString  = qName+" " ;
		  for ( int idx = 0 ; idx < atts.getLength(); idx++){
			  this.entityString += atts.getQName(idx)+"=\""+atts.getValue(idx)+"\" " ;
		  }*/
		  
		  this.entityId = atts.getValue("Id");
		  if (this.entityId.contains("--")){
			  log.log(Level.FINEST, "Entity_id contains -- in it "+this.entityId);
			  checkNeg=true;
		  
		  }  else{
		  populateKeys(entityId);
		  if (iptnms_installed){
				this.entityShelfId = atts.getValue("shelfId");
				this.actualShelfId = shelfMap.get(entityShelfId);
				//primKeysdata.put("SHELF_ID", actualShelfId);
		  }
		
		  this.sourceId = atts.getValue("sourceId");
			for (String key : sourceIdValues1.keySet()) {

				if (key !=null){
					if(key.equals(sourceId)){
						ArrayList<String> entry1 = new ArrayList<String>();
						entry1 = sourceIdValues1.get(key);
						int index = 0;
						for(String SchemeValue : entry1){
							if(index < soamKeys.length){
								entitySoamData.put(soamKeys[index],SchemeValue);	
								index++;
							}
						}

					}

				}		        		   	

			}
	  }
		  
	  }
	  else if ( qName.equals("EntityIdentity")){
		  
		  this.tagID    = atts.getValue("entityTypeId");
	  }
	  else if( qName.equals("Counter")){
		  
		  this.counterName = atts.getValue("name");
	  }
	  else if( qName.equals("TimeStamp")){
		  
		  this.localTimeFormatId = calculateCollectionBeginTime(atts.getValue("localTimeFormatId"));
	  }
	  
  }
 


  public void endElement(final String uri, final String name, final String qName) throws SAXException {

	  if (qName.equals("DocTitle")) {

	  }
	  else if (qName.equals("Doc")) {


	  }
	  else if (qName.equals("EMName") ){
		  
		  this.emName = this.charValue ;
	  }
	  else if (qName.equals("NEShortName")) {
		  
		  this.neShortName = this.charValue ;
	  }
	  else if (qName.equals("NESuffix")) {
		  
		  this.neSuffix = this.charValue ;
	  }
	  else if (qName.equals("EMPlugin")) {
		  
		  this.neEmPlugin = this.charValue ;
	  }
	  else if (qName.equals("NEType")) {
		  
		  this.neType = this.charValue ;
	  }
	  else if (qName.equals("Address")) {
		  
		  this.neIpAddress = this.charValue ;
	  }
	/*  else if (qName.equals("Port")) {
		  
		  this.nePort = this.charValue ;
	  }*/
	  else if (qName.equals("Addr")) {
		  
		  this.neAddr = this.charValue ;
	  }
	  else if (qName.equals("Room")) {
		  
		  this.neRoom = this.charValue ;
	  }
	  else if (qName.equals("Row")) {
		  
		  this.neRow = this.charValue ;
	  }
	  else if (qName.equals("Rack")) {
		  
		  this.neRack= this.charValue ;
	  }
	  else if (qName.equals("SubRack")) {
		  
		  this.neSubRack = this.charValue ;
	  }
	 
	  else if (qName.equals("Counter")){


		  try {

			  if (measFile == null) {

				  System.err.println("Measurement file null");

			  }
			  else{

				  measFile.addData(this.counterName ,this.charValue );
				  log.log(Level.FINEST, "Counter "+ this.counterName + ":" + this.charValue);
			  }
		  }
		  catch (Exception e) {

			  log.log(Level.FINEST, "Error saving measurement data", e);
			  e.printStackTrace();
			  throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
		  }
	  }



	  else if(qName.equals("EntityIdentity")){
		  if(!checkNeg){
		  	this.vendorId = this.charValue ;

		  try {
			  log.info("Index:"+primKeysdata.get("ENTITY_ID")+"Plugin Name:"+this.neEmPlugin+"  EntityName "+this.charValue.replaceAll("\\s+",""));
			  if(this.neEmPlugin.toLowerCase().equalsIgnoreCase("xsa")){
				  String vendorName = this.charValue.replaceAll("\\s+","");
				  this.measFile = getMeasFile(this.neEmPlugin.toLowerCase()+"_"+vendorName); 
			  }else{
			  this.measFile = getMeasFile(this.neEmPlugin.toLowerCase()+"_"+primKeysdata.get("ENTITY_ID"));
			  }

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error getting measurement file details ", e);
			  e.printStackTrace();
			  throw new SAXException("Error getting measurement file details " + e.getMessage(), e);
		  }
		  }else{
			  log.log(Level.FINEST,"entity_id is negative");
		  }


	  }
	  else if(qName.equals("Details")){
		  if(!checkNeg){
		  try {

			  if (measFile == null) {

					log.log(Level.WARNING,"Measurement file null");

			  }
			  else{
				  
				  // This will happen only for a PPM sample file

				  measFile.addData("filename", sourceFile.getName());
				  measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				  measFile.addData("DIRNAME", sourceFile.getDir());
				  measFile.addData("DC_SUSPECTFLAG", "" );
				  
				  measFile.addData("NEIdOnEM", this.neIdOnEm);
				  measFile.addData("emVersion", this.emVersion);
				  measFile.addData("emName",this.emName);
				  measFile.addData("longName", this.neLongName);
				  measFile.addData("NEShortName", this.neShortName);
				  measFile.addData("NESuffix", this.neSuffix);
				  measFile.addData("EMPlugin", this.neEmPlugin);
				  measFile.addData("NEBasicTypeId", this.neEmPluginNeBasicTypeId);
				  measFile.addData("EMPlugin_Version",this.neEmPluginVersion);
				  measFile.addData("NEType", this.neType);
				  measFile.addData("NEModel", this.neModel);
				  measFile.addData("NETypeId",this.neTypeId);
				  measFile.addData("IP_ADDRESS", this.neIpAddress);
				  measFile.addData("PORT",primKeysdata.get("PORT"));
				  measFile.addData("LOC_ADDR", this.neAddr);
				  measFile.addData("LOC_ROOM", this.neRoom);
				  measFile.addData("LOC_ROW",  this.neRow);
				  measFile.addData("LOC_RACK", this.neRack);
				  measFile.addData("LOC_SUBRACK", this.neSubRack);
				  
				  
				  measFile.addData("Entity Id", this.entityId);


				  measFile.addData("ENTITY_NAME", this.vendorId);
				  measFile.addData("ENTITY_TYPE_ID", this.tagID);


				  measFile.addData("sourceId", this.sourceId);
				  measFile.addData("DATETIME_ID",this.localTimeFormatId);

					if(!(entitySoamData.isEmpty())){
					measFile.addData(entitySoamData);
					}
					if(!(primKeysdata.isEmpty())){
						measFile.addData(primKeysdata);
					}

					measFile.saveData();
				}



			} catch (Exception e) {

				log.log(Level.FINEST, "Error saving measurement data", e);
				e.printStackTrace();
				throw new SAXException("Error saving measurement data: " + e.getMessage(), e);
			}
		  }else{
			  log.log(Level.FINEST, "Entity_id is negative");
		  }


		}
		
	  else if ( qName.equals("SwInfo") ) {
		  
		  // Only happens for PIC files
		  // The neID is extracted not from the same place in the PIC and PPM files
		  
		  
		  // EM Measurement file
		 try {

			  measFile = Main.createMeasurementFile(sourceFile, "EM", techPack, setType, setName, workerName, log);

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error getting measurement file details ", e);
			  e.printStackTrace();
			  throw new SAXException("Error getting measurement file details " + e.getMessage(), e);

		  }
		  
		  try {
			  
			  if (measFile == null) {
						log.log(Level.WARNING,"Error while adding data");

			  }
			  else{
		
				  // Write the measurement data
				  measFile.addData("filename", sourceFile.getName());
				  measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				  measFile.addData("DIRNAME", sourceFile.getDir());
				  measFile.addData("DC_SUSPECTFLAG", "" );
				  measFile.addData("DATETIME_ID",this.localTimeFormatId);
				  
				  //measFile.addData("neId", this.neId);
				  measFile.addData("emIdNum",this.emIdNum);
				  measFile.addData("emId", this.emId);
				  measFile.addData("emMnr", this.emMnr);
				  measFile.addData("emVersion", this.emVersion);
				  measFile.addData("emName",this.emName);
				  measFile.addData("ENTITY_NAME", this.vendorId);
				  measFile.addData("ENTITY_TYPE_ID", this.tagID);	
				  				  
				  measFile.saveData();
				  measFile.close();
			  
			  }
		  
			  
			  
		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error saving measurement data ", e);
			  e.printStackTrace();
			  throw new SAXException("Error saving measurement data " + e.getMessage(), e);

		  }
		  
		  
		  // NEID measurement file
		  try {

			  //this.measFile = getMeasFile(this.neId);
			  measFile = Main.createMeasurementFile(sourceFile, "NE", techPack, setType, setName, workerName, log);

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error getting measurement file details ", e);
			  e.printStackTrace();
			  throw new SAXException("Error getting measurement file details " + e.getMessage(), e);

		  }
		  
		  try {
			  
			  if (measFile == null) {

				  System.err.println("Measurement file null");

			  }
			  else{
		
				  // Write the measurement data
				  measFile.addData("filename", sourceFile.getName());
				  measFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				  measFile.addData("DIRNAME", sourceFile.getDir());
				  measFile.addData("DC_SUSPECTFLAG", "" );
				  measFile.addData("DATETIME_ID",this.localTimeFormatId);
				  
				  measFile.addData("neId", this.neId);
				  measFile.addData("emId", this.neEmId);
				  /*
				  measFile.addData("emMnr", this.emMnr);
				  measFile.addData("emVersion", this.emVersion);
				  measFile.addData("emName",this.emName);
				  
				  */
				  		
				  measFile.addData("NEIdOnEM", this.neIdOnEm);
				  measFile.addData("longName", this.neLongName);
				  measFile.addData("NEShortName", this.neShortName);
				  measFile.addData("NESuffix", this.neSuffix);
				  measFile.addData("EMPlugin", this.neEmPlugin);
				  measFile.addData("NEBasicTypeId", this.neEmPluginNeBasicTypeId);
				  measFile.addData("EMPlugin_Version",this.neEmPluginVersion);
				  measFile.addData("NEType", this.neType);
				  measFile.addData("NEModel", this.neModel);
				  measFile.addData("NETypeId",this.neTypeId);
				  measFile.addData("IP_ADDRESS", this.neIpAddress);
				  measFile.addData("PORT",primKeysdata.get("PORT"));
				  
				  measFile.addData("LOC_ADDR", this.neAddr);
				  measFile.addData("LOC_ROOM", this.neRoom);
				  measFile.addData("LOC_ROW",  this.neRow);
				  measFile.addData("LOC_RACK", this.neRack);
				  measFile.addData("LOC_SUBRACK", this.neSubRack);
				  measFile.addData("ENTITY_NAME", this.vendorId);
				  measFile.addData("ENTITY_TYPE_ID", this.tagID);
				  
				  measFile.saveData();
				  measFile.close();
			  
			  }
		  
			  
			  
		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error saving measurement data ", e);
			  e.printStackTrace();
			  throw new SAXException("Error saving measurement data " + e.getMessage(), e);

		  }
		  
		  
		  
		  
	  }
	  // FTP Specific
      if (qName.equals("NodeName")) {
		  
		   nodeName = charValue ;
	  }
	  else if (qName.equals("NEID")) {
		  
		   ftpNeId =  charValue ;
	  }
	  else if (qName.equals("NEAlias")) {
		  
		   neAlias = charValue ;
		  
	  }
	  else if (qName.equals("Radio_Terminal_Name")) {
		  
		   radioTerminalName = charValue ;
		  
	  }
	  else if (qName.equals("Terminal_ID")) {
		  
		   terminalId = charValue;
	  }
	  else if (qName.equals("Type")) {
		  
		   equipmentType = charValue ;
	  }
	  else if (qName.equals("Protection_Mode_Admin_Status")) {
		  
		   protModeAdmStatus = charValue ;
	  }
	  else if (qName.equals("Equipment_Protection_Active_Unit")) {
		  
		   eqProtActUnit = charValue ;
	  }
	  else if (qName.equals("Capacity")) {
		  
		   capacity = charValue ;
		  
	  }
	  else if (qName.equals("Modulation")){
		  modulation = charValue.trim();
	  }
	  else if(qName.equals("Packet_Link_Capacity")){
		  packetLinkCap = charValue.trim();
	  }
	  else if(qName.equals("Current_Input_Power_RF1")){
		  currInpPower1 = charValue.trim();
	  }
	  else if (qName.equals("Current_Input_Power_RF2")){
		  currInpPower2 = charValue.trim();
	  }
	  else if (qName.equals("Equipment_Protection_Mode")){
		  equpProtecMode = charValue.trim();
	  }
	  else if (qName.equals("Far_End_Terminal_Name")) {
		  
		   farEndTermName = charValue ;
		
	  }
	  else if (qName.equals("Far_End_ID")) {
		  
		   farEndId = charValue ;
	  }
	  else if (qName.equals("Far_End_Type")) {
		  
		   farEndType = charValue ;
	  }
	  else if (qName.equals("Active_TX_Radio")) {
		  
		   activeTxRadio = charValue ;
	  }
	  else if (qName.equals("Instance_Ra1")) {
		  
		   instanceRa1 = charValue ;
	  }
	  else if (qName.equals("Instance_Ra2")) {
		  
		   instanceRa2 = charValue ;
	  }
	  else if (qName.equals("Freq_Band_Ra1")) {
		  
		   freqBandRa1 = charValue ;
	  }
	  else if (qName.equals("Freq_Band_Ra2")) {
		  
		   freqBandRa2 = charValue ;
	  }
	  else if (qName.equals("Doc")) {
		  
		   farEndTermName = charValue ;
	  }
	  else if (qName.equals("Instance_RF1")) {
		  
		   instanceRf1 = charValue ;
	  }
	  else if (qName.equals("Instance_RF2")) {
		  
		   instanceRf2 = charValue ;
	  }
 	  else if ( qName.equals("Name")) {
		  
 		  this.neName = charValue ;
		  
	  }
 	  else if ( qName.equals("Location")) {
		  
 		  this.location = charValue;
		  
	  }
 	  else if ( qName.equals("Contact")) {
		  
		  this.contact = charValue;
	  }
 	  else if ( qName.equals("DCN_Host_Address")) {
		  
 		  this.dcnHostAddress = charValue;
		  
	  }
	  else if (qName.equals("CONF_TN_RT")) {
		  
		  // Write the details to CONF_TN meas file
		  try {

			  if( confTnRtMeasFile == null ){
				  
				  // Should happen only for the first time
				  confTnRtMeasFile = Main.createMeasurementFile(sourceFile, "CONF_TN_RT", techPack, setType, setName, workerName, log);
				  
			  }
			 
			  

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error getting measurement file details ", e);
			  e.printStackTrace();
			  throw new SAXException("Error getting measurement file details " + e.getMessage(), e);

		  }
		  
		  try {
			  
			  if (confTnRtMeasFile == null) {

				  System.err.println("CONF_TN_RT Measurement file null");

			  }
			  else{
				  
				  // Write to measurement file.
				  confTnRtMeasFile.addData("filename", sourceFile.getName());
				  confTnRtMeasFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				  confTnRtMeasFile.addData("DIRNAME", sourceFile.getDir());
				  confTnRtMeasFile.addData("DC_SUSPECTFLAG", "" );
				  //confTnRtMeasFile.addData("DATETIME_ID",localTimeFormatId);
				  
				  confTnRtMeasFile.addData("NODENAME", nodeName);
				  confTnRtMeasFile.addData("NE_ID", ftpNeId);
				  confTnRtMeasFile.addData("NE_ALIAS", neAlias);
				  confTnRtMeasFile.addData("RADIO_TRML_NAME", radioTerminalName);
				  confTnRtMeasFile.addData("TERM_ID", terminalId);
				  confTnRtMeasFile.addData("EQUIP_TYPE", equipmentType);
				  confTnRtMeasFile.addData("PROT_MODE_ADM_STATUS", protModeAdmStatus);
				  confTnRtMeasFile.addData("EQ_PROT_ACTIVE_UNIT", eqProtActUnit);
				  confTnRtMeasFile.addData("CAPACITY", capacity);
				  confTnRtMeasFile.addData("FAR_END_TERM_NAME", farEndTermName);
				  confTnRtMeasFile.addData("FAR_END_ID", farEndId);
				  confTnRtMeasFile.addData("FAR_END_TYPE", farEndType);
				  confTnRtMeasFile.addData("ACTIVE_TX_RADIO", activeTxRadio);
				  confTnRtMeasFile.addData("INSTANCE_RA1", instanceRa1);
				  confTnRtMeasFile.addData("INSTANCE_RA2", instanceRa2);
				  confTnRtMeasFile.addData("FREQ_BAND_RA1", freqBandRa1);
				  confTnRtMeasFile.addData("FREQ_BAND_RA2", freqBandRa2);
				  confTnRtMeasFile.addData("INSTANCE_RF1", instanceRf1);
				  confTnRtMeasFile.addData("INSTANCE_RF2", instanceRf2);
				  confTnRtMeasFile.addData("MODULATION",modulation);
				  confTnRtMeasFile.addData("PACKET_LINK_CAPACITY",packetLinkCap);
				  confTnRtMeasFile.addData("CURRENT_INPUT_POWER_RF1",currInpPower1);
				  confTnRtMeasFile.addData("CURRENT_INPUT_POWER_RF2",currInpPower2);
				  confTnRtMeasFile.addData("EQUIPMENT_PROTECTION_MODE",equpProtecMode);
				  confTnRtMeasFile.addData("ENTITY_NAME", vendorId);
				  confTnRtMeasFile.addData("ENTITY_TYPE_ID", tagID);
				  confTnRtMeasFile.saveData();
				  
			  }
		  
	  
		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error saving measurement data ", e);
			  e.printStackTrace();
			  throw new SAXException("Error saving measurement data " + e.getMessage(), e);

		  }


  
	  }
    //CONF_MLE specific
  	
	     else if (qName.equals("ID")) {
	       
	        mleNeId =  charValue ;
	     }
	     else if (qName.equals("Own_ID")) {
	       
	        ownID = charValue ;
	     }
	     else if (qName.equals("Equipment_Type")) {
	           
	            equipmentType = charValue ;
	     }
	     else if (qName.equals("Protection_Mode")) {
	       
	        protectionMode = charValue;
	     }
	     else if (qName.equals("Traffic_Rate")) {
	       
	        trafficRate = charValue ;
	     }
	     else if (qName.equals("IDS_In_Own_AMM")) {
	       
	        IDSInOwnAMM = charValue ;
	     }
	     else if (qName.equals("NCC_AM_No")) {
		       
	    	 NCCAMNo = charValue ;
		 }
	     else if (qName.equals("IDS_In_Connected_AMM")) {
		       
	    	 IDSInConnectedAMM = charValue ;
		 }
	     else if (qName.equals("IDS_In_Connected_RTU")) {
	       
	        IDSInConnectedRTU = charValue ;
	     }
	     else if (qName.equals("Active_Radio")) {
	       
	        activeRadio = charValue ;
	     }
	     else if (qName.equals("Freq_Index_Ra1")) {
	       
	        freqIndexRa1 = charValue ;
	     }
	     else if (qName.equals("Freq_Index_Ra2")) {
	       
	        freqIndexRa2 = charValue ;
	     }
	     else if (qName.equals("Channel_Spacing")) {
	       
	        channelSpacing = charValue ;
	     }
	      
	      
	      
	      
		  //write the details for CONF_MLE
	      
	      
	 else if (qName.equals("CONF_MLE")) {
	      
	      // Write the details to CONF_TN meas file
	      try {

	        if( confMleMeasFile == null ){
	          
	          // Should happen only for the first time
	          confMleMeasFile = Main.createMeasurementFile(sourceFile, "CONF_MLE", techPack, setType, setName, workerName, log);
	          
	        }
	       
	        

	      } catch (Exception e) {

	        log.log(Level.FINEST, "Error getting measurement file details ", e);
	        e.printStackTrace();
	        throw new SAXException("Error getting measurement file details " + e.getMessage(), e);

	      }
	      
	      try {
	        
	        if (confMleMeasFile == null) {

	          System.err.println("CONF_MLE Measurement file null");

	        }
	        else{
	          
	          // Write to measurement file.
	          confMleMeasFile.addData("filename", sourceFile.getName());
	          confMleMeasFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
	          confMleMeasFile.addData("DIRNAME", sourceFile.getDir());
	          confMleMeasFile.addData("DC_SUSPECTFLAG", "" );
	          confMleMeasFile.addData("NODENAME", nodeName);
	          confMleMeasFile.addData("NE_ID", mleNeId);
	          confMleMeasFile.addData("NE_ALIAS", neAlias);
	          confMleMeasFile.addData("TERM_ID", ownID);
	          confMleMeasFile.addData("EQUIP_TYPE",equipmentType );
	          confMleMeasFile.addData("TRAFFIC_RATE", trafficRate);
	          confMleMeasFile.addData("PROTECTION_MODE", protectionMode);
	          confMleMeasFile.addData("FAR_END_ID", farEndId);
	          confMleMeasFile.addData("FAR_END_TYPE", farEndType);
	          confMleMeasFile.addData("IDS_IN_OWN_AMM", IDSInOwnAMM);
	          confMleMeasFile.addData("NCC_AM_NO", NCCAMNo);
	          confMleMeasFile.addData("IDS_IN_CONNECTED_AMM", IDSInConnectedAMM);
	          confMleMeasFile.addData("IDS_IN_CONNECTED_RTU", IDSInConnectedRTU);
	          confMleMeasFile.addData("ACTIVE_RADIO", activeRadio);
	          confMleMeasFile.addData("FREQ_BAND_RA1", freqBandRa1);
	          confMleMeasFile.addData("FREQ_INDEX_RA1", freqIndexRa1);
	          confMleMeasFile.addData("FREQ_INDEX_RA2", freqBandRa2);
	          confMleMeasFile.addData("FREQ_INDEX_RA2", freqIndexRa2);
	          confMleMeasFile.addData("CHANNEL_SPACING", channelSpacing);
	          confMleMeasFile.addData("ENTITY_NAME", vendorId);
	          confMleMeasFile.addData("ENTITY_TYPE_ID", tagID);
	          confMleMeasFile.saveData();
	          
	        }
	      
	    
	      } catch (Exception e) {

	        log.log(Level.FINEST, "Error saving measurement data ", e);
	        e.printStackTrace();
	        throw new SAXException("Error saving measurement data " + e.getMessage(), e);

	       }
	     } 

	  else if ( qName.equals("CONF_TN_GEN")) {
		  
		  // Write the details to CONF_TN_GEN meas file
		  try {

			  if( confTnGenMeasFile == null ){
				  
				  // Should happen only for the first time
				  confTnGenMeasFile = Main.createMeasurementFile(sourceFile, "CONF_TN_GEN", techPack, setType, setName, workerName, log);
				  
			  }
			 
			  

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error getting measurement file details ", e);
			  e.printStackTrace();
			  throw new SAXException("Error getting measurement file details " + e.getMessage(), e);

		  }
		  
		  try {
			  
			  if( confTnGenMeasFile == null){
				  
				  System.err.println("CONF_TN_GEN Measurement file null");
				  
			  }
			  else {
				  
				  // Write to measurement file.
				  confTnGenMeasFile.addData("filename", sourceFile.getName());
				  confTnGenMeasFile.addData("JVM_TIMEZONE", JVM_TIMEZONE);
				  confTnGenMeasFile.addData("DIRNAME", sourceFile.getDir());
				  confTnGenMeasFile.addData("DC_SUSPECTFLAG", "" );
				  //confTnGenMeasFile.addData("DATETIME_ID",localTimeFormatId);
				  
				  confTnGenMeasFile.addData("NE_ID", ftpNeId);
				  confTnGenMeasFile.addData("NE_ALIAS", neAlias);
				  confTnGenMeasFile.addData("NE_NAME", neName);
				  confTnGenMeasFile.addData("LOCATION", location);
				  confTnGenMeasFile.addData("CONTACT",contact);
				  confTnGenMeasFile.addData("NODENAME", nodeName);
				  confTnGenMeasFile.addData("DCN_HOST_ADDR", dcnHostAddress);
				  confTnGenMeasFile.addData("ENTITY_NAME", vendorId);
				  confTnGenMeasFile.addData("ENTITY_TYPE_ID", tagID);
				  confTnGenMeasFile.saveData();
				  
				  
			  }
				  
			  
			  
		  }catch (Exception e) {

			  log.log(Level.FINEST, "Error saving measurement data ", e);
			  e.printStackTrace();
			  throw new SAXException("Error saving measurement data " + e.getMessage(), e);

		  }
		  
	  }
  }

  /**
   * Fetches the Measurementfile for the vendor Id.
   * 
   * @param  index 
   * @return MeasurementFile object
   */

  private MeasurementFile getMeasFile(final String index) throws SAXException {

	  // For PPM - tag id (index) would be EntityIdentity value
	//  DFormat df = dfc.getFormatWithTagID(interfaceName, objectClass+vectorPostfix);

	  if( mFileHashMap.containsKey(index)){

		  return mFileHashMap.get(index);
	  }
	  else{


		  MeasurementFile mf = null;

		  try {


			  mf = Main.createMeasurementFile(sourceFile, index, techPack, setType, setName, workerName, log );
			  

		  } catch (Exception e) {

			  log.log(Level.FINEST, "Error opening measurement data", e);
			  e.printStackTrace();
			  throw new SAXException("Error opening measurement data: " + e.getMessage(), e);
		  }

		  mFileHashMap.put(index, mf);
		  return mf;

	  }

  }


  private void populateKeys(String entityId){
	  if(!(primKeysdata.isEmpty())){
		  primKeysdata.clear();
	  }
	  String EQUIP_ID = null;
	  String COMPLIANCE_ID = null;
	  String st[] = entityId.split("-");
	  int len = primaryKeys.length;
	  for (int entityIdIndex = 1,primkeyIndex=0;primkeyIndex<len;entityIdIndex++,primkeyIndex++){
		  primKeysdata.put(primaryKeys[primkeyIndex],st[entityIdIndex]);
	  }
	  EQUIP_ID = st[4]+"-"+st[5];
	  COMPLIANCE_ID = st[(st.length)-1];
	  primKeysdata.put("EQUIP_ID",EQUIP_ID);
	  primKeysdata.put("COMPLIANCE_ID",COMPLIANCE_ID);
  }

  public static void main(String[] args) {
    int argnum = 0;
    mlXmlParser np = null;
    FileInputStream fis = null;
    while (argnum < args.length) {
      if (args[argnum].equals("-sf")) {
        final String s = args[argnum+1];
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
      final File f = new File("C:\\__AnandData\\__WorkDocs\\MiniLinkParser\\MiniLinkSampleFile1.xml");
      try {
        fis = new FileInputStream(f);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      np = new mlXmlParser();
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
      log.finest(" regExp (" + regExp + ") found from " + str + "  :" + result);
      return result;
    } else {
      log.warning("String " + str + " doesn't match defined regExp " + regExp);
    }

    return "";

  }

  public void characters(char[] ch, int start, int length) throws SAXException {
	  final StringBuffer charBuffer = new StringBuffer(length);
    for (int i = start; i < start + length; i++) {
      // If no control char
    	 if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
    	        charBuffer.append(ch[i]);
    	      }
    	    }
    	    charValue += charBuffer;
  }
  private String calculateCollectionBeginTime(final String value) {
		log.finest("statTime is " + value);
		long epochTime = 0;
		String format= "yyyy-MM-dd HH.mm.ss";
		Date dt;
		try {
			dt = new java.text.SimpleDateFormat(format).parse(value);
			epochTime = dt.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String fileName=sourceFile.getName();
		if((fileName.contains("-R24h-")))
		{
			epochTime = epochTime - INTERVALS_24;
		}else{
			epochTime = epochTime - INTERVALS_15;
		}
		String currenttime = new java.text.SimpleDateFormat(format).format(new java.util.Date (epochTime));
		log.finest("after adjusting date is " + currenttime);
		return currenttime;


	}

  public int memoryConsumptionMB() {
	  return memoryConsumptionMB;
  }

  public void setMemoryConsumptionMB(int memoryConsumptionMB) {
	  this.memoryConsumptionMB = memoryConsumptionMB;
  }


}
