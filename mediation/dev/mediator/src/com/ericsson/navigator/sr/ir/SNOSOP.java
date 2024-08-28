package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class SNOSOP implements IrNode {
	private static final Logger logger = Logger.getLogger("System Registration");
	private String cimname = null;
	private static final String cimdescription = "";
	private static final String cimcaption = "";
	private static final String cimotheridentifyinginfo = "";
	private static final String cimidentifyingdescription = "";
    private String snosopname = null;
    private String snosoptype = null;
    private String snosopargument = null;
    private String snosopuser = "";

	
	/**
	 * @param cimname the cimname to set
	 */
	public void setCimname(final String cimname) {
		this.cimname = cimname;
	}
	/**
	 * @param snosopargument the snosopargument to set
	 */
	public void setSnosopargument(final String snosopargument) {
		this.snosopargument = snosopargument;
	}
	/**
	 * @param snosopname the snosopname to set
	 */
	public void setSnosopname(final String snosopname) {
		this.snosopname = snosopname;
	}
	/**
	 * @param snosoptype the snosoptype to set
	 */
	public void setSnosoptype(final String snosoptype) {
		this.snosoptype = snosoptype;
	}
	/**
	 * @param snosopuser the snosopuser to set
	 */
	public void setSnosopuser(final String snosopuser) {
		this.snosopuser = snosopuser;
	}
	
	public boolean verify(final Ir ir) {
		return true;
	}
	
	public Status srHrWrite(final String systemName, final int position){
		final Status result = Status.Success;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		
		System.out.println(indent + "cimname = " + cimname);
		if (cimcaption != null && !cimcaption.isEmpty()){
			System.out.println(indent + "cimcaption = " + cimcaption);
		}
		if (cimdescription != null && cimdescription.isEmpty()){
			System.out.println(indent + "cimdescription = " + cimdescription);
		}
		System.out.println(indent + "snosopname = " + snosopname);
		System.out.println(indent + "snosoptype = " + snosoptype);
		System.out.println(indent + "snosopargument = " + snosopargument);
		if (snosopuser != "" && snosopuser != null){
			System.out.println(indent + "snosopuser = " + snosopuser);
		}
		System.out.println();
		
		return result;
	}
	
	
	/**
	 * This method writes the XML element handled by this class to a file writer.
	 * It starts to write in the column given by input parameter position.
	 * @param fileWriter A file writer for a System Registration/System Topology File.
	 * @param position The position where to start to write.
	 * @param systemName Not used in this class.
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
			// Replace special characters with XML predefined entities in attribute snosopargument.
			final String convSnosopargument = snosopargument.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;");	
			fileWriter.write(indent + "<SNOSOP cimname=\"" + cimname + "\"" + 
					" snosopname=\"" + snosopname + "\"" + 
					" snosoptype=\"" + snosoptype + "\"" + 
					" snosopargument=\"" + convSnosopargument + "\"" +
					" snosopuser=\"" + snosopuser + "\"" +
					" cimdescription=\"" + cimdescription + "\"" +
					" cimcaption=\"" + cimcaption + "\"" +
					"/>\n");
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}

	public Status deleteSystem(final String systemName) {
		// This method is never used but must be there because of the IrNode interface.
		return Status.Success;
	}
	public Status getOperations(final String resource, final String ipAddress, final List<Properties> result) {
		final Properties p = new Properties();
		p.setProperty("cimname", cimname);
		p.setProperty("cimdescription", cimdescription);
		p.setProperty("cimcaption", cimcaption);
		p.setProperty("cimotheridentifyinginfo", cimotheridentifyinginfo);
		p.setProperty("cimidentifyingdescription", cimidentifyingdescription);
		p.setProperty("snosopname", snosopname);
		p.setProperty("snosoptype", snosoptype);
		p.setProperty("snosopargument", snosopargument);
		p.setProperty("snosopuser", snosopuser);
		result.add(p);
		return Status.Success;//nobody cares...
	}
    
}
