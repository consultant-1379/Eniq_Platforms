package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class Ir {
	public static Logger logger = Logger.getLogger("System Registration");
	private EricssonNMSCIM ericssonNMSCIM;
	
	public boolean verify(final Ir ir) {
		return ericssonNMSCIM.verify(ir);
	}
	
	public void setEricssonNMSCIM(final EricssonNMSCIM ericssonNMSCIM) {
		this.ericssonNMSCIM = ericssonNMSCIM;
	}
	
	public Status srHrWrite(final String systemName){
		return ericssonNMSCIM.srHrWrite(systemName,0);
	}
	
	/**
	 * This method writes the XML element handled by this class to a file writer.
	 * @param fileWriter A file writer for a System Registration/System Topology File.
	 * @param systemName Not used in this class. Passed on to the next class.
	 * @param srDTDPath Path to the System Registration DTD.
	 * @return Success if the file was saved successfully.
	 */
	public Status srWrite(final Writer fileWriter, final String systemName, final String srDTDPath) {
		Status result = Status.Fail;
		try {
			fileWriter.write("<?xml version=\"1.0\"?>\n<!DOCTYPE EricssonNMSCIM SYSTEM \""+srDTDPath+"\">\n");
			result = ericssonNMSCIM.srWrite(fileWriter, 0, systemName, srDTDPath);
			
		}catch (final IOException e){
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}

	public List<SNOSNE> getSystems(){
		return ericssonNMSCIM.getSystems(); 	
	}
	
	public Status addSystems(final List<SNOSNE> systems){
		return ericssonNMSCIM.addSystems( systems);
	}
	
	public void sortSystems(){
		ericssonNMSCIM.sortSystems();
	}
	
	public boolean systemExists(final String systemName){
		return ericssonNMSCIM.systemExists(systemName);
	}

	public Status deleteSystem(final String systemName) {
		return ericssonNMSCIM.deleteSystem(systemName);
	}

	public Status getOperations(final String resource, final String ipAddress, final List<Properties> result) {
		return ericssonNMSCIM.getOperations(resource,ipAddress,result);
	}
}
