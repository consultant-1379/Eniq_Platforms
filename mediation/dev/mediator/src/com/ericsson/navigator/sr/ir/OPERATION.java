package com.ericsson.navigator.sr.ir;


import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class OPERATION implements IrNode, IrNodeModify {
	public static Logger logger = Logger.getLogger("System Registration");
	private EricssonNMS ericssonNMS;
	private String protocolVersion;
	private Type type;

	
	public void setPROTOCOLVERSION(final String protocolversion) {
		protocolVersion = protocolversion;
	}
	
	public void setType(final Type type) {
		this.type = type;
	}
	
	public boolean verify(final Ir ir) {
		return ericssonNMS.verify(ir);
	}
	
	/**
	 * @param ericssonNMS the ericssonNMS to set
	 */
	public void setEricssonNMS(final EricssonNMS ericssonNMS) {
		this.ericssonNMS = ericssonNMS;
	}
	
	public Status srHrWrite(final String systemName, final int position){
		return ericssonNMS.srHrWrite(systemName,0);
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
		Status result = Status.Fail;
		String indent ="";
		for (int i = 0;i < position ; i++){
			indent+=" ";
		}
		try {
			fileWriter.write(indent + "<OPERATION PROTOCOLVERSION=" + "\""+ protocolVersion + "\"" + " TYPE=");
			if (type == Type.modify){
				fileWriter.write("\"modify\">\n");
			}
			else if (type == Type.add){
				fileWriter.write("\"add\">\n");
			}
			else if (type == Type.delete){
				fileWriter.write("\"delete\">\n");
			}
			else{
				fileWriter.write("\"purge\">\n");
			}
			result = ericssonNMS.srWrite(fileWriter, position + 3, systemName, srDTDPath);
			fileWriter.write(indent + "</OPERATION>\n");
			
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}
	
	public List<SNOSNE> getSystems(){
		
		List<SNOSNE> systems = null;
			
		systems = ericssonNMS.getSystems();
		
		return systems; 
	}
	public Status addSystems(final List<SNOSNE> systems) {
		
		return ericssonNMS.addSystems(systems);
		
	}
	public void sortSystems() {
		ericssonNMS.sortSystems();
		
	}
	
	public boolean systemExists(final String systemName){
		return ericssonNMS.systemExists(systemName);
	}
	public Status deleteSystem(final String systemName) {
		return ericssonNMS.deleteSystem(systemName);
	}
	public Status getOperations(final String resource, final String ipAddress, final List<Properties> result) {
		return ericssonNMS.getOperations(resource,ipAddress,result);
	}
}
