package com.ericsson.sim.sftp.plugins;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginParent;
import com.ericsson.sim.sftp.serialization.SFTPserFile;
import com.ericsson.sim.sftp.serialization.appended.FileProperties;
import com.ericsson.sim.sftp.serialization.appended.FilePropertyManager;
import com.ericsson.sim.sftp.session.SessionManager;
import com.ericsson.sim.sftp.session.SftpSession;
import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.SshException;

public class LsAppendedGet implements PluginParent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SFTPproperties properties;
	private SFTPserFile serfile;
	private SftpSession sftp;
	private String processID;
	private FilePropertyManager fpm = FilePropertyManager.getInstance();
	
	@Override
	public void execute(Node node, Protocol protocol) {
		this.node = node;
		this.properties = (SFTPproperties) protocol;
		this.serfile = new SFTPserFile();
		this.processID = node.getID()+"_"+properties.getID();
		String serPath = SIMEnvironment.PERSPATH+"/" + processID +".ser";
		
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
			
			//if(firstRop != null && firstRop.contains(String.valueOf(properties.getID()))){
			
			if(serfile.containsSERFile(serPath)){//  && firstRop.contains(String.valueOf(properties.getID()))){
				//if files should be collected
				
				//Filter out files that have already been collected
				ArrayList<String> newFiles = new ArrayList<String>();
				newFiles.addAll(existingFiles);
				newFiles.removeAll(knownFiles);
				
				log.debug(newFiles);
				//Identify and collect files to be processed
				ArrayList<String> processedFiles = findAndCollectFiles(newFiles);
				
				//Add unrequired files to ser list
				existingFiles.removeAll(processedFiles);
				
			}else{
				log.info("SIM first rop. No files were collected ");
				node.updateFirstRop(properties.getID());
				//existingFiles.removeAll(removeCurrentDayFile(existingFiles));
			}
			
			existingFiles.removeAll(removeCurrentDayFile(existingFiles));
			
			//Update ser file with collected files
			serfile.writeserFile(serPath, existingFiles);
		
		}catch(Exception e){
			log.error("Error while performing discovery and collection. ", e);
		}
		
	}

	private ArrayList<String> getls() throws SIMException, SftpStatusException, SshException{
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

	private ArrayList<String> findAndCollectFiles(ArrayList<String> list) throws SshException{
		ArrayList<String> result = new ArrayList<String>();
		String remoteDir = properties.getProperty("remoteDirectory");
		int maxfileperrop = Integer.parseInt(properties.getProperty("maxFilesPerRop"));
		
		//Make destination directory
		File destination = new File(SIMEnvironment.PARSINGPATH, node.getID()+"_"+properties.getID());
		if(!destination.exists()){
			destination.mkdirs();
		}
		
		int numberofFilesCollected = 0;
		//Process each file to determine if it should be collected
		for(String filename : list){
			if(numberofFilesCollected < maxfileperrop){
			
				try{
					String uniqueFileID = processID + "-" + filename;
					FileProperties props = fpm.getProperties(uniqueFileID);
					Date lastmodifiedTime = sftp.getLastModifiedTime(remoteDir + "/" + filename);

					if(lastmodifiedTime.after(props.getLastCollectedTime())){ //if the file has been updated since the last time SIM saw it
						try{
							//Collect the file. If successful then update the lastCollectedTime and clear counter of previous failures.
							sftp.get(remoteDir + "/" + filename, destination.getAbsolutePath()+"/"+filename);
							props.setLastCollectedTime(lastmodifiedTime);
							props.clearUnmodified();
							result.add(filename);
							numberofFilesCollected++;
						}catch(SIMException e){
							log.error("Unable to collect file " + filename, e);
							//Increment the counter to limit the number of times we process the error file
							props.incrementRopsUnmodified();
						}
					}else{
						//Increment the counter to limit the number of times we process the error file
						props.incrementRopsUnmodified();
					}
				
				
				if(props.getRopsUnmodified() < 4){
					fpm.addProperties(uniqueFileID, props);
					if(!result.contains(filename))
						result.add(filename);
				}else{
					log.info(filename + " unchanged for 4 ROPs and will no longer be collected");
					fpm.removeProperties(uniqueFileID);
				}
				
				}catch(Exception e){
					log.error("Unable to collect " + filename + ". ", e);
				}
				
			}
			
		}
		log.info("Successfully collected: " + result);
		return result;
	}

	private ArrayList<String> removeCurrentDayFile(ArrayList<String> list){
		String fileNameDateFormat = (String) properties.getProperty("dateFormatInFileName");
		fileNameDateFormat = fileNameDateFormat.replace("HH", "");
		fileNameDateFormat = fileNameDateFormat.replace("mm", "");
		
		SimpleDateFormat format = new SimpleDateFormat(fileNameDateFormat);
		Calendar cal = Calendar.getInstance();
		String dateStamp = format.format(cal.getTime());
		
		ArrayList<String> files = new ArrayList<String>();
		for(String file: list){
			if(file.contains(dateStamp)){
				files.add(file);
			}
		}
		return files;
	}
	
	
}
