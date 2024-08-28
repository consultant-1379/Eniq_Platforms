package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class SNOSNE implements IrNode, SNOSNEParent, Comparable<SNOSNE> {
	private static final Logger logger = Logger.getLogger("System Registration");
	private final List<SNOSNE> snosne = new LinkedList<SNOSNE>();
	private final List<SNOSOP> snosop = new LinkedList<SNOSOP>();
	private final List<SnmpCounterSet> snmpCounterSets = new LinkedList<SnmpCounterSet>();
	private final List<IrpXmlCounterSet> irpXmlCounterSets = new LinkedList<IrpXmlCounterSet>();
	private final List<RemoteFileFetch> remoteFileFetchList = new LinkedList<RemoteFileFetch>();
	private String cimname = null;
	private String cimdescription = "";
	private String cimcaption = "";
	private String cimotheridentifyinginfo = null;
	private String cimidentifyingdescription= "";
	private String syscontact = "";
	private String syslocation = "";
	private String snoshostip = null;
	private String snosbackupip = null;
	private String snosversion= "";
	private String snoshostname= "";
	private String snossnmpcommunity = null;
	private String snossnmpport = null;
	private String snosprotocoltype = null;
	private static final String ALARMMOI_SNF = "___SNFSNFSNFSNFSNFSNFSNFSNFSNF___";
	private String snosalarmmoi = ALARMMOI_SNF;
	private String snostype = null;
	public static final String CIMNAME = "cimname";
	public static final String CIMDESCRIPTION = "cimdescription";
	public static final String CIMCAPTION = "cimcaption";
	public static final String CIMOTHERIDENTIFYINGINFO = "cimotheridentifyinginfo";
	public static final String CIMIDENTIFYINGDESCRIPTION = "cimidentifyingdescription";
	public static final String SYSCONTACT = "syscontact";
	public static final String SYSLOCATION = "syslocation";
	public static final String SNOSHOSTIP = "snoshostip";
	public static final String SNOSBACKUPIP = "snosbackupip";
	public static final String SNOSVERSION = "snosversion";
	public static final String SNOSHOSTNAME = "snoshostname";
	public static final String SNOSSNMPCOMMUNITY = "snossnmpcommunity";
	public static final String SNOSSNMPPORT = "snossnmpport";
	public static final String SNOSALARMMOI = "snosalarmmoi";
	public static final String SNOSPROTOCOLTYPE = "snosprotocoltype";
	public static final String SNOSTYPE = "snostype";
	public static final String PROTOCOLTYPE_ALARMIRP = "IRP";
	public static final String PROTOCOLTYPE_SNF = "SNF";
	public static final String PROTOCOLTYPE_MST = "MST";
	public static final String PROTOCOLTYPE_SNMP = "SNMP";
	public static final Object PROTOCOLTYPE_TXF = "TXF";
	public static final String PROTOCOLTYPE_MVMALARM = "MVMALARM";

	/**
	 * @return the cimname
	 */
	public String getCimname() {
		return cimname;
	}
	
	/* (non-Javadoc)
	 * @see com.ericsson.snms.sr.ir.SNOSNEParent#getSnosne()
	 */
	public List<SNOSNE> getSnosne() {
		return snosne;
	}
	/**
	 * @return the snosop
	 */
	public List<SNOSOP> getSnosop() {
		return snosop;
	}
	
	public List<SnmpCounterSet> getSnmpCounterSets() {
		return snmpCounterSets;
	}
	
	public List<IrpXmlCounterSet> getIrpXmlCounterSets() {
		return irpXmlCounterSets;
	}
	

	public List<RemoteFileFetch> getRemoteFileFetch() {
		return remoteFileFetchList;
	}

	/**
	 * @param snossnmpport the snossnmpport to set
	 */
	public void setSnossnmpport(final String snossnmpport) {
		this.snossnmpport = snossnmpport;
	}
	/**
	 * @param cimcaption the cimcaption to set
	 */
	public void setCimcaption(final String cimcaption) {
		this.cimcaption = cimcaption;
	}
	/**
	 * @param cimdescription the cimdescription to set
	 */
	public void setCimdescription(final String cimdescription) {
		this.cimdescription = cimdescription;
	}
	/**
	 * @param cimidentifyingdescription the cimidentifyingdescription to set
	 */
	public void setCimidentifyingdescription(final String cimidentifyingdescription) {
		this.cimidentifyingdescription = cimidentifyingdescription;
	}

	public void setCimname(final String cimname) {
		this.cimname = cimname;
	}
	
	/**
	 * @param cimotheridentifyinginfo the cimotheridentifyinginfo to set
	 */
	public void setCimotheridentifyinginfo(final String cimotheridentifyinginfo) {
		this.cimotheridentifyinginfo = cimotheridentifyinginfo;
	}
	/**
	 * @param snosalarmmoi the snosalarmmoi to set
	 */
	public void setSnosalarmmoi(final String snosalarmmoi) {
		this.snosalarmmoi = snosalarmmoi;
	}
	/**
	 * @param snoshostip the snoshostip to set
	 */
	public void setSnoshostip(final String snoshostip) {
		this.snoshostip = snoshostip;
	}
	
	public void setSnosbackupip(final String snosbackupip) {
		this.snosbackupip = snosbackupip;
	}
	
	public void setSnosProtocolType(final String type) {
		snosprotocoltype = type;
	}

	public void setSnosType(final String value) {
		snostype  = value;
	}
	
	public String getSnosType(){
		return snostype;
	}

	public static String getProtocolType(final String alarmMOI) {
		if(alarmMOI != null){
			return PROTOCOLTYPE_ALARMIRP;
		}else{
			return PROTOCOLTYPE_SNF;
		}	
	}
	
	/**
	 * @param snoshostname the snoshostname to set
	 */
	public void setSnoshostname(final String snoshostname) {
		this.snoshostname = snoshostname;
	}
	/**
	 * @param snossnmpcommunity the snossnmpcommunity to set
	 */
	public void setSnossnmpcommunity(final String snossnmpcommunity) {
		this.snossnmpcommunity = snossnmpcommunity;
	}
	/**
	 * @param snosversion the snosversion to set
	 */
	public void setSnosversion(final String snosversion) {
		this.snosversion = snosversion;
	}
	
	/**
	 * @param syscontact the syscontact to set
	 */
	public void setSyscontact(final String syscontact) {
		this.syscontact = syscontact;
	}
	
	/**
	 * @param syslocation the syslocation to set
	 */
	public void setSyslocation(final String syslocation) {
		this.syslocation = syslocation;
	}
	
	public boolean verify(final Ir ir) {
		boolean result = true;
		
		if (snosprotocoltype == null){
			result = true;
			/* It is possible to omit this attribute for backward compatibility reasons, introduced first in navigator 1.0. */
		}
		else if(!snosprotocoltype.equals(PROTOCOLTYPE_ALARMIRP) && !snosprotocoltype.equals(PROTOCOLTYPE_SNMP) && 
				!snosprotocoltype.equals(PROTOCOLTYPE_MST) && !snosprotocoltype.equals(PROTOCOLTYPE_SNF) && 
				!snosprotocoltype.equals(PROTOCOLTYPE_TXF)&&
				!snosprotocoltype.equals(PROTOCOLTYPE_MVMALARM)){
			logger.error("Invalid value assigned to attribute " +
					"snosprotocoltype for cimname " + cimname);
			result =  false;
		} else if(snosprotocoltype.equals(PROTOCOLTYPE_MST) && (snostype == null || snostype.length() == 0)){
			logger.error("Attribute snostype is missing value for MST system " + cimname);
			result =  false;
		} else if(snosprotocoltype.equals(PROTOCOLTYPE_TXF) && (snostype == null || snostype.length() == 0)){
			logger.error("Attribute snostype is missing value for TXF system " + cimname);
			result =  false;
		}
		
		//Check if the attribute cimotheridentifyinginfo exists
		if (this.cimotheridentifyinginfo == null){
			logger.warn("Attribute cimotheridentifyinginfo does not exist. The corresponding element SNOSNE has cimname=\"" + getCimname() + "\"");
			result = true;
			/* It is possible to omit attribute cimotheridentifyinginfo when 
			 * only using CM operations for a system/component and no alarm
			 * handling is used. */
		}
		
		// Check that there is a value assigned to the attribute cimotheridentifyinginfo.
		else if (this.cimotheridentifyinginfo.length() == 0 && snoshostip.length() > 0){
			logger.error("No value assigned to attribute " +
					"cimotheridentifyinginfo for cimname " + cimname);
			result =  false;
		}
		
		// Check that the value of attribute cimotheridentifyinginfo starts with 1, 2 or 3.
		else if (!(this.cimotheridentifyinginfo.startsWith("1.") || 
				this.cimotheridentifyinginfo.startsWith("2.") ||
				this.cimotheridentifyinginfo.startsWith("3.")) && snoshostip.length() > 0){
			logger.warn("Wrong value of cimotheridentifyinginfo: " + this.cimotheridentifyinginfo +
					", for system: "+cimname+", If this is a SNF system it must start with 1, 2 or 3.");
			result = true;
		}
		
		// Check that the value of cimotheridentifyinginfo has a min. length of 2, eg 1.2 
		else if (this.cimotheridentifyinginfo.indexOf(".") == -1 && snoshostip.length() > 0){
			logger.error("Wrong length of the value of cimotheridentifyinginfo: " + this.cimotheridentifyinginfo+
					", The length of the value must be at least 2, with a \".\" as separator.");
			result = false;
		}
		
		// Check that there is a value after the first "."
		else if (this.cimotheridentifyinginfo.length() < 3 && snoshostip.length() > 0){
				logger.error("Wrong length of the value of cimotheridentifyinginfo: " + this.cimotheridentifyinginfo+
				", The length of the value must be at least 2, with a \".\" as separator.");
				result = false;
		}
		
		// Check that the value of cimotheridentifyinginfo does not end with "."
		else if (this.cimotheridentifyinginfo.endsWith(".")){
			logger.error("Wrong format of cimotheridentifyinginfo: " + this.cimotheridentifyinginfo);
			result = false;
		}
		
		else {
			// Check that the format of cimotheridentifyinginfo is of type x.x.x.x
			boolean error = false;
			int pos = this.cimotheridentifyinginfo.indexOf(".");
			while (!error && pos != -1){
				if (this.cimotheridentifyinginfo.substring(pos+1, pos+2).equals(".")){
					logger.error("Wrong format of cimotheridentifyinginfo: " + this.cimotheridentifyinginfo);
					error = true;
					result = false;
				}
				pos = this.cimotheridentifyinginfo.indexOf(".", pos+1);
			}
		}
		
		
		testIpAddress(this.snoshostip, "snoshostip");
		
		if(this.snosbackupip != null){
			testIpAddress(this.snosbackupip, "snosbackupip");
		}
		
		final Iterator<SNOSNE> i = snosne.iterator();
		while (result && i.hasNext()){			
			result = i.next().verify(ir);
		}
		final Iterator<SNOSOP> j = snosop.iterator();
		while (result && j.hasNext()){			
			result = j.next().verify(ir);
		}
		final Iterator<SnmpCounterSet> snmpCSIterator = snmpCounterSets.iterator();
		while (result && snmpCSIterator.hasNext()){			
			result = snmpCSIterator.next().verify(ir);
		}
		final Iterator<IrpXmlCounterSet> irpXmlCSIterator = irpXmlCounterSets.iterator();
		while (result && irpXmlCSIterator.hasNext()){			
			result = irpXmlCSIterator.next().verify(ir);
		}
		final Iterator<RemoteFileFetch> remoteFileFetchIterator = remoteFileFetchList.iterator();
		while (result && remoteFileFetchIterator.hasNext()){			
			result = remoteFileFetchIterator.next().verify(ir);
		}
				
		return result;
	}
	
	private boolean testIpAddress(String ipAddress, String attribute) {
		boolean result = true;
		// Check that snoshostip is of type x.x.x.x, where x is an integer
		if (ipAddress.length() == 0) {
			logger.warn("No value assigned to attribute "+ attribute+". The corresponding element SNOSNE has cimname=\""
					+ getCimname() + "\"");
			result = true;
		}

		else if (ipAddress.startsWith(".") || ipAddress.endsWith(".")) {
			logger.error("Wrong format of attribute "+ attribute+": "+ ipAddress);
			result = false;
		}

		else if (ipAddress.equals("127.0.0.1")) {
			logger.error("Value of attribute "+ attribute+" not allowed: "+ ipAddress);
			result = false;
		}

		else {
			boolean error = false;
			int noOfDots = 0;
			int pos = ipAddress.indexOf(".");
			if (pos == -1) {
				logger.error("Wrong format of attribute "+ attribute+": "+ ipAddress);
				return false;
			}
			final String firstElement = ipAddress.substring(0, pos);
			try {
				new Integer(firstElement);
			} catch (final NumberFormatException e) {
				logger.error("Wrong format of attribute "+ attribute+": "+ ipAddress);
				logger.debug("Caused by: ", e);
				return false;
			}
			int oldPos = 0;
			while (!error && pos != -1) {
				noOfDots++;
				if ((ipAddress.indexOf(".", pos + 1) == -1) && (noOfDots != 3)) {
					logger.error("Wrong format of attribute "+ attribute+": "+ ipAddress);
					return false;
				}
				oldPos = pos;
				pos = ipAddress.indexOf(".", pos + 1);
				if (pos == -1 && noOfDots == 3) {
					final String nextElement = ipAddress.substring(
							oldPos + 1, ipAddress.length());
					try {
						Integer.parseInt(nextElement);
					} catch (final NumberFormatException e) {
						logger.error("Wrong format of "+ attribute+": "+ ipAddress);
						logger.debug("Caused by: ", e);
						error = true;
						result = false;
					}
				} else {
					final String nextElement = ipAddress.substring(
							oldPos + 1, pos);
					try {
						Integer.parseInt(nextElement);
					} catch (final NumberFormatException e) {
						logger.error("Wrong format of "+ attribute+": "+ ipAddress);
						logger.debug("Caused by: ", e);
						error = true;
						result = false;
					}
				}
			}
		}
		return result;
	}
	
	public Status srHrWrite(final String systemName, final int position){
		Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		
		System.out.println(indent + "cimname = " + cimname);
		
		if (cimcaption != null && !cimcaption.equals("")){
			System.out.println(indent + "cimcaption = " + cimcaption);
		}
		
		if (cimdescription != null && !cimdescription.equals("")){
			System.out.println(indent + "cimdescription = " + cimdescription);
		}
		
		System.out.println(indent + "snoshostip = " + snoshostip);
		
		if (snoshostname != null && !snoshostname.equals("")){
			System.out.println(indent + "snoshostname = " + snoshostname);
		}
		
		if (snosversion != null && !snosversion.equals("")){
			System.out.println(indent + "snosversion = " + snosversion);
		}
		
		if (cimotheridentifyinginfo != null && !cimotheridentifyinginfo.equals("")){
			System.out.println(indent + "cimotheridentifyinginfo = " + cimotheridentifyinginfo);
		}
		
		if (cimidentifyingdescription != null && !cimidentifyingdescription.equals("")){
			System.out.println(indent + "cimidentifyingdescription = " + cimidentifyingdescription);
		}
		
		if (snossnmpport != null && !snossnmpport.equals("")){
			System.out.println(indent + "snossnmpport = " + snossnmpport);
		}
		
		if (snossnmpcommunity != null && !snossnmpcommunity.equals("")){
			System.out.println(indent + "snossnmpcommunity = " + snossnmpcommunity);
		}
		
		if (snosalarmmoi != null && !snosalarmmoi.equals("") &&
				!snosalarmmoi.equals("___SNFSNFSNFSNFSNFSNFSNFSNFSNF___")){
			System.out.println(indent + "snosalarmmoi = " + snosalarmmoi);
		}
		
		if (syscontact != null && !syscontact.equals("")){
			System.out.println(indent + "syscontact = " + syscontact);
		}
		
		if (syslocation != null && !syslocation.equals("")){
			System.out.println(indent + "syslocation = " + syslocation);
		}
		
		if (snosprotocoltype != null && !snosprotocoltype.equals("")){
			System.out.println(indent + "snosprotocoltype = " + snosprotocoltype);
		}
		
		if (snostype != null && !snostype.equals("")){
			System.out.println(indent + "snostype = " + snostype);
		}
		
		System.out.println();
		
		final Iterator<SNOSNE> ine = snosne.iterator();
		while (ine.hasNext() && (Status.Success == result)){
			result = ine.next().srHrWrite(systemName,position + 3);
		}
		final Iterator<SNOSOP> iop = snosop.iterator();
		while (iop.hasNext() && (Status.Success == result)){
			result = iop.next().srHrWrite(systemName,position + 3);
		}
		final Iterator<SnmpCounterSet> snmpCSIterator = snmpCounterSets.iterator();
		while (snmpCSIterator.hasNext() && (Status.Success == result)){		
			result = snmpCSIterator.next().srHrWrite(systemName,position + 3);
		}
		final Iterator<IrpXmlCounterSet> irpXmlCSIterator = irpXmlCounterSets.iterator();
		while (irpXmlCSIterator.hasNext() && (Status.Success == result)){		
			result = irpXmlCSIterator.next().srHrWrite(systemName,position + 3);
		}
		final Iterator<RemoteFileFetch> remoteFileFetchIterator = remoteFileFetchList.iterator();
		while (remoteFileFetchIterator.hasNext() && (Status.Success == result)){		
			result = remoteFileFetchIterator.next().srHrWrite(systemName,position + 3);
		}
		
		return result;
	}
	
	/**
	 * This method writes the XML element handled by this class to a file writer.
	 * It starts to write in the column given by input parameter position.
	 * @param fileWriter A file writer for a System Registration/System Topology File.
	 * @param position The position where to start to write.
	 * @param systemName Not used in this class. Passed on to the next class.
	 * @param srDTDPath Path to the System Registration DTD.
	 * @return Success if the file was saved successfully.
	 */
	public Status srWrite(final Writer fileWriter, final int position, final String systemName, final String srDTDPath) {
		Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		try {
			if (snosalarmmoi.equals("___SNFSNFSNFSNFSNFSNFSNFSNFSNF___")){
				fileWriter.write(indent +
						"<SNOSNE cimname=\"" + cimname + "\"" + 
						" snoshostip=\"" + snoshostip + "\"");
				
				if (cimotheridentifyinginfo != null){
					fileWriter.write(" cimotheridentifyinginfo=\"" + cimotheridentifyinginfo + "\"");
				}
				if (snosprotocoltype != null){
					fileWriter.write(" snosprotocoltype=\"" + snosprotocoltype + "\"");
				}
				if (snostype != null){
					fileWriter.write(" snostype=\"" + snostype + "\"");
				}
				fileWriter.write(" snossnmpcommunity=\"" + snossnmpcommunity + "\"" +
						" snossnmpport=\"" + snossnmpport + "\"" +  
						" snoshostname=\"" + snoshostname + "\"" + 
						" snosversion=\"" + snosversion + "\"" + 
						" cimcaption=\"" + cimcaption + "\"" + 
						" cimdescription=\"" + cimdescription + "\"" + 
						" syscontact=\"" + syscontact + "\"" +
						" syslocation=\"" + syslocation + "\"" +
						" cimidentifyingdescription=\"" + cimidentifyingdescription + "\""  + 
						">\n");
				
			} else {
				fileWriter.write(indent +
						"<SNOSNE cimname=\"" + cimname + "\"" + 
						" snoshostip=\"" + snoshostip + "\"");
				
				if (cimotheridentifyinginfo != null){
					fileWriter.write(" cimotheridentifyinginfo=\"" + cimotheridentifyinginfo + "\"");
				}
				if (snosprotocoltype != null){
					fileWriter.write(" snosprotocoltype=\"" + snosprotocoltype + "\"");
				}
				if (snostype != null){
					fileWriter.write(" snostype=\"" + snostype + "\"");
				}
				fileWriter.write(" snossnmpcommunity=\"" + snossnmpcommunity + "\"" +
						" snossnmpport=\"" + snossnmpport + "\"" +  
						" snosalarmmoi=\"" + snosalarmmoi + "\"" +
						" snoshostname=\"" + snoshostname + "\"" + 
						" snosversion=\"" + snosversion + "\"" + 
						" cimcaption=\"" + cimcaption + "\"" + 
						" cimdescription=\"" + cimdescription + "\"" + 
						" syscontact=\"" + syscontact + "\"" +
						" syslocation=\"" + syslocation + "\"" +
						" cimidentifyingdescription=\"" + cimidentifyingdescription + "\""  + 
						">\n");		

			}
			final Iterator<SNOSNE> ine = snosne.iterator();
			while (ine.hasNext() && (Status.Success == result)){
				result = ine.next().srWrite(fileWriter,position + 3,systemName,srDTDPath);
			}
			final Iterator<SNOSOP> iop = snosop.iterator();
			while (iop.hasNext() && (Status.Success == result)){
				result = iop.next().srWrite(fileWriter,position + 3,systemName,srDTDPath);
			}
			final Iterator<SnmpCounterSet> snmpCSIterator = snmpCounterSets.iterator();
			while (snmpCSIterator.hasNext() && (Status.Success == result)){
				result = snmpCSIterator.next().srWrite(fileWriter,position + 3,systemName,srDTDPath);
			}
			final Iterator<IrpXmlCounterSet> irpXmlCSIterator = irpXmlCounterSets.iterator();
			while (irpXmlCSIterator.hasNext() && (Status.Success == result)){
				result = irpXmlCSIterator.next().srWrite(fileWriter,position + 3,systemName,srDTDPath);
			}
			final Iterator<RemoteFileFetch> remoteFileFetchIterator = remoteFileFetchList.iterator();
			while (remoteFileFetchIterator.hasNext() && (Status.Success == result)){
				result = remoteFileFetchIterator.next().srWrite(fileWriter,position + 3,systemName,srDTDPath);
			}
			fileWriter.write(indent + "</SNOSNE>\n");			
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}

	public int compareTo(final SNOSNE system) {
		return cimname.compareTo(system.getCimname());

	}

	public Status deleteSystem(final String systemName) {
		// This method is never used but must be there because of the IrNode interface.
		return Status.Success;
	}
	public Status getOperations(final String resource, final String ipAddress, final List<Properties> result) {
		Status status = Status.Fail;
		if (snoshostip.equals(ipAddress) && cimotheridentifyinginfo.equals(resource)){
			final Iterator<SNOSOP> i = snosop.iterator();
			while (i.hasNext()){
				i.next().getOperations(resource, ipAddress, result);
			}
			status = Status.Success;
		} else {
			final Iterator<SNOSNE> i = snosne.iterator();
			while ((status == Status.Fail) && i.hasNext()){
				status = i.next().getOperations(resource, ipAddress, result);
			}
		}
		return status;
	}

}
