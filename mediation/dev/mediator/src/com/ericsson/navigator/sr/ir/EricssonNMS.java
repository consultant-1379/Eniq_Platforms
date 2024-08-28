package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.util.Status;

public class EricssonNMS implements IrNode, IrNodeModify {
	public static Logger logger = Logger.getLogger("System Registration");
	private EriNMSSubNW eriNMSSubNW;
	
	public void setEriNMSSubNW(final EriNMSSubNW eriNMSSubNW){
		this.eriNMSSubNW = eriNMSSubNW;
	}

	public boolean verify(final Ir ir) {
		boolean result = true;
		result = eriNMSSubNW.verify(ir);
		return result;
	}
	
	public Status srHrWrite(final String systemName, final int position){
		return eriNMSSubNW.srHrWrite(systemName,0);
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
			fileWriter.write(indent + "<EricssonNMS>\n");
			result = eriNMSSubNW.srWrite(fileWriter,position + 3,systemName,srDTDPath);
			fileWriter.write(indent + "</EricssonNMS>\n");
			
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "+e.getMessage());
			logger.debug("Caused by: ",e);
			result = Status.Fail;
		}
		return result;
	}
	
	public List<SNOSNE> getSystems(){
		List<SNOSNE> systems = null;
		systems = eriNMSSubNW.getSystems();
		return systems; 
	}

	public Status addSystems(final List<SNOSNE> systems) {
		Status result = Status.Success;
		result = eriNMSSubNW.addSystems(systems);
		return result;	
	}

	public void sortSystems() {
		eriNMSSubNW.sortSystems();
	}
	
	public boolean systemExists(final String systemName){
		return eriNMSSubNW.systemExists(systemName);
	}

	public Status deleteSystem(final String systemName) {
		Status result = Status.Fail;
		result = eriNMSSubNW.deleteSystem(systemName);
		return result;
	}

	public Status getOperations(final String resource, final String ipAddress, final List<Properties> result) {
		Status status = Status.Fail;
		status = eriNMSSubNW.getOperations(resource, ipAddress, result);
		return status;
	}
}
