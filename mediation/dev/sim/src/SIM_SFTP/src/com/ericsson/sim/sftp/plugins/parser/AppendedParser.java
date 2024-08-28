package com.ericsson.sim.sftp.plugins.parser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginUtilMethods;
import com.ericsson.sim.sftp.serialization.appended.FileProperties;
import com.ericsson.sim.sftp.serialization.appended.FilePropertyManager;

public class AppendedParser extends ParserParent{
	
	Logger log = LogManager.getLogger(this.getClass().getName());
	private SFTPproperties properties;
	private Node node;
	private String header = null;
	private String processID;


	public void parseFile(Node node, SFTPproperties properties, File dataFile) throws SIMException {
		this.node = node;
		this.properties = properties;
		this.processID = node.getID()+"_"+properties.getID();
		try{		
			if(isValidFile(dataFile)){
				log.debug("Parsing " + dataFile.getName());
				
				String uniqueFileID = processID + "-" + dataFile.getName();
				String delimitor = (String) properties.getProperty("delimitor");
				
				//find the new data
				FileProperties props = FilePropertyManager.getInstance().getProperties(uniqueFileID);
				processData(dataFile, properties, props);
				
				if(data.size()>0){
					//Validate the data before writing to the file
					int columnCount = getMaximumColumnCount(delimitor);
					File newFile = getNewFilename(dataFile);
					writeNewFile(newFile, delimitor, columnCount);
					
					log.debug("Created " + newFile.getName() + " with " + data.size() + " rows");
					moveFile(properties, newFile);
					log.debug("Moved " + newFile.getName() + " to destination directory");
					
					//Update the last parsed line
					String lastline = data.get(data.size()-1);
					props.setLastParsedLine(lastline);
					FilePropertyManager.getInstance().addProperties(uniqueFileID, props);
				}else{
					log.info("No new data found in file "+dataFile.getName());
				}
				
			}else{
				throw new SIMException("The file " + dataFile.getName() + " contains no data");
			}
		
		}catch(Exception e){
			throw new SIMException(e);
		}
		
	}

	
	public File getNewFilename(File dataFile) throws SIMException{
		try{
			Calendar calender = Calendar.getInstance();
			String linedateformat = (String) properties.getProperty("dateFormatInsideFile");
			String delimitor = (String) properties.getProperty("delimitor");
			String fileNameDateFormat = (String) properties.getProperty("dateFormatInFileName");
			
			SimpleDateFormat filenameformat = new SimpleDateFormat(fileNameDateFormat);
			SimpleDateFormat format = new SimpleDateFormat(linedateformat);
			
			//Get timestamp from the first line of data in the file
			calender.setTime(format.parse(firstDataLine.split(delimitor)[0]));
			
			//Create the datestamp expected to be in the filename
			String oldDateStamp = filenameformat.format(calender.getTime());
			
			if(!fileNameDateFormat.endsWith("mm")){
				fileNameDateFormat = fileNameDateFormat+"mm";
			}
			filenameformat = new SimpleDateFormat(fileNameDateFormat);
			
			//Get the date from the first line of new data
			String linedate = data.get(0).split(delimitor)[0];
			calender.setTime(format.parse(linedate));
			
			//Create the new datestamp for the file name
			String newDateStamp = filenameformat.format(calender.getTime());

			//Create the new file name
			String filename = dataFile.getName();
			
			filename = filename.replace(oldDateStamp, newDateStamp);
			PluginUtilMethods pum = new PluginUtilMethods();
			filename = pum.renameFile(node, properties, filename).toString();
			
			return new File(dataFile.getParent(), filename);
		
		}catch(Exception e){
			log.error("Error: ",e);
			throw new SIMException("Unable to create new file", e);
		}
		
	}
	

}
