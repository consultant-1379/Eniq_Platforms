package com.ericsson.sim.plugin;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class PluginUtilMethods {
	
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	public StringBuffer renameFile(Node node, SFTPproperties properties, String currentFilename){
		StringBuffer newFilename = new StringBuffer("");
		String ext = "";
		int index = currentFilename.lastIndexOf('.');
		if(index != -1){
			ext = currentFilename.substring(index);				//Get current file extension
			currentFilename = currentFilename.substring(0,index);	//Get current file name
		}
		
		String fileRenamePattern = properties.getProperty("fileRenaming");
		if(fileRenamePattern == null){
			fileRenamePattern = "%F";
		}
		
		List<String> prefixParts = Arrays.asList(fileRenamePattern.split("%"));
		for(String part : prefixParts){
			if(!part.equals("")){
				char element = part.toUpperCase().charAt(0);
				if(element == 'F'){
					newFilename.append(currentFilename);
				}else if(element == 'H'){
					newFilename.append(node.getID());
				}else if(element == 'P'){
					newFilename.append(properties.getProperty("filePrefix"));
				}else if(element == 'N'){
					newFilename.append(node.getName());
				}else if(element == 'I'){
					newFilename.append(node.getProperty("IPAddress"));
				}else{
					log.warn("Unsupported variable given to rename file");
				}
				
				try{
					char seperator = part.charAt(1);
					newFilename.append(seperator);
				}catch(IndexOutOfBoundsException e){}
			}
			
		}
		return newFilename.append(ext);
	}
	

}
