package com.ericsson.sim.sftp.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginParent;
import com.ericsson.sim.plugin.PluginUtilMethods;
import com.ericsson.sim.sftp.serialization.SFTPserFile;
import com.ericsson.sim.sftp.session.SessionManager;
import com.ericsson.sim.sftp.session.SftpSession;
import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.SshException;

public class LsDirectGet implements PluginParent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SFTPproperties properties;
	private SFTPserFile serfile;
	private SftpSession sftp;
	private static int collected;
	
	@Override
	public void execute(Node node, Protocol protocol) {		
		this.node = node;
		this.properties = (SFTPproperties) protocol;
		this.serfile = new SFTPserFile();
		String serPath = SIMEnvironment.PERSPATH+"/" + node.getID()+"_"+properties.getID()+".ser";
		
		try{
			//node.printDetails();
			log.info("ROP: " + node.getName() + " - " + properties.getName());
			
			//Get SFTP connection
			SessionManager sm = SessionManager.getInstance();
			sftp = sm.createSession(node);
			
			//Load previously seen files from ser file
			ArrayList<String> knownFiles = serfile.loadPersistedFile(serPath);
			//Get list of existing files in the remote directory
			ArrayList<String> existingFiles = getls();
			log.debug("Total number of files available: " + existingFiles.size());
			//Match files against a regex
			existingFiles.removeAll(matchFiles(existingFiles));
			
			//String firstRop = (String) node.getProperty("firstRop");
			if(serfile.containsSERFile(serPath)){// && firstRop.contains(String.valueOf(properties.getID()))){
				//if files should be collected
				
				//Filter out files that have already been collected
				ArrayList<String> newFiles = new ArrayList<String>();
				newFiles.addAll(existingFiles);
				newFiles.removeAll(knownFiles);
				log.debug("Number of new files to collect: " + newFiles.size());
				
				//Collect files and return any that could not be collected
				newFiles = collectFiles(newFiles);
				//Remove files that could not be collected so they will be collected next ROP
				existingFiles.removeAll(newFiles);	
			}else{
				log.info("SIM first rop. No files were collected ");
				node.updateFirstRop(properties.getID());
			}
			
			//Update ser file with collected files
			serfile.writeserFile(serPath, existingFiles);
		
		}catch(Exception e){
			log.error("Error while performing discovery and collection. ", e);
		}
		
	}
	
	protected ArrayList<String> getls() throws SIMException, SftpStatusException, SshException{
		//Get all files in the remote directory
		ArrayList<String> list = new ArrayList<String>();
		String remoteDir = properties.getProperty("remoteDirectory");
		
		for(SftpFile file : sftp.ls(remoteDir)){
			if(file.isFile()){
				list.add(file.getFilename());
			}
		}

		
		return list;
	}
	
	private ArrayList<String> matchFiles(ArrayList<String> list){
		ArrayList<String> matched = new ArrayList<String>();
		ArrayList<String> notmatched = new ArrayList<String>();
		Pattern filenameRegex = Pattern.compile(properties.getProperty("lsRegex"));
		for(String filename : list){
			if(filenameRegex.matcher(filename).matches()){
				matched.add(filename);
			}else{
				notmatched.add(filename);
			}
		}
		log.debug("Matched files: "+ matched);
		log.debug("Unmatched files: "+ notmatched);
		return notmatched;
	}
	
	protected ArrayList<String> collectFiles(ArrayList<String> list){
		String remoteDir = properties.getProperty("remoteDirectory");
		int maxfileperrop = Integer.parseInt(properties.getProperty("maxFilesPerRop"));
		
		PluginUtilMethods pum = new PluginUtilMethods();
		
		//Make destination directory
		String destinationDir = properties.getProperty("DestinationDir");
		File destination = new File(destinationDir);
		if(!destination.exists()){
			destination.mkdirs();
		}
		
		int numberofFilesCollected = 0;
		ArrayList<String> successfullist = new ArrayList<String>();
		if(list.size()>0){
			for(String filename : list){
				
				if(numberofFilesCollected < maxfileperrop){
					try{
						String filePath = remoteDir + "/" + filename;
						String destFilename = pum.renameFile(node, properties, filename).toString();
						sftp.get(filePath, destination.getAbsolutePath()+"/"+destFilename);
						successfullist.add(filename);
						numberofFilesCollected++;
					}catch(SIMException e){
						log.error("Unable to collect file " + filename, e);
						successfullist.add(filename);
					}catch(SshException e){
						log.error("Unable to collect file " + filename, e);
						new File(destination.getAbsolutePath()+"/"+filename).delete();
						list.removeAll(successfullist);
						return list;
					}
					
				}
			}
		}else{
			log.info("No new files to collect");
		}
		collected = successfullist.size();
		list.removeAll(successfullist);
		log.info("Successfully collected: " + successfullist);
		
		return list;
	}
	
}

