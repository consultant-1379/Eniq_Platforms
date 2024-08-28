package com.ericsson.sim.sftp.plugins.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.serialization.appended.FileProperties;
import com.ericsson.sim.sftp.serialization.appended.FilePropertyManager;

public class SOEMParser extends ParserParent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private SFTPproperties properties;
	private Node node;
	private String header = null;
	private String processID;
	private HashMap<String, String> lookupTable = null;
	
	public void parseFile(Node node, SFTPproperties properties, File dataFile) throws SIMException {
		this.node = node;
		this.properties = properties;
		this.processID = node.getID()+"_"+properties.getID();
		try{
			if(lookupTable == null){
				loadLookupTable();
			}
			
			if(isValidFile(dataFile)){
				log.debug("Parsing " + dataFile.getName());
				
				//Get the netype from the file
				String netype = getNEType(dataFile);
				
				if(lookupTable.containsKey(netype)){
					moveFile(lookupTable.get(netype), dataFile);
				}else{
					//find the new data
					String uniqueFileID = processID + "-" + dataFile.getName();
					FilePropertyManager fpm = FilePropertyManager.getInstance();
					FileProperties props = fpm.getProperties(uniqueFileID);
					//Increment the counter to limit the number of times we process the error file
					props.incrementRopsUnmodified();
					
					if(props.getRopsUnmodified() < 24){
						fpm.addProperties(uniqueFileID, props);
					}else{
						log.info(dataFile.getName() + " unsorted for 24 ROPs and will be deleted");
						fpm.removeProperties(uniqueFileID);
						dataFile.delete();
					}
					
				}
				
			}else{
				throw new SIMException("The file " + dataFile.getName() + " contains no data");
			}
		
		}catch(Exception e){
			throw new SIMException(e);
		}
		
	}
	
	private void loadLookupTable() throws SIMException, IOException{
		String lookupFilePath = (String) properties.getProperty("LookupTable");
		Path lookupFile = Paths.get(lookupFilePath);
		
		if(!lookupFile.toFile().exists()){
			throw new SIMException("Unable to load SOEM Lookup Table");
		}
		
		List<String> lines = Files.readAllLines(lookupFile, Charset.defaultCharset());
		
		lookupTable = new HashMap<String, String>();
		for(String line : lines){
			String[] dataElements = line.split(",");
			lookupTable.put(dataElements[0], dataElements[1]);
		}
	}
	
	private String getNEType(File dataFile) throws SIMException{
		String netype = "";
		boolean foundMatch = false;
		try{
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			
			String strLine;
			while ((strLine = br.readLine()) != null && !foundMatch) {
				if(strLine.contains("NEType")){
					int start = strLine.indexOf(">")+1;
					int end = strLine.indexOf("</");
					netype = strLine.substring(start, end);
					foundMatch = true;
				}
				
			}
			
			br.close();
			
		}catch(Exception e){
			throw new SIMException(e);
		}
		return netype;
	}
	
	
}
