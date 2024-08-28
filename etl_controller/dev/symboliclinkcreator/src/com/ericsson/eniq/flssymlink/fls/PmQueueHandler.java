package com.ericsson.eniq.flssymlink.fls;

import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import com.ericsson.eniq.flssymlink.symlink.SymbolicLinkCreationHelper;

/*
 * This class is used to add PmJson objects into PmQueue to handle thread pool executor
 */

public class PmQueueHandler implements Runnable{
	
	private PmJson pmJson;
	Logger log;
	
	public PmQueueHandler(PmJson pmJson, Logger log) {
		super();
		this.pmJson = pmJson;
		this.log = log;
	}

	@Override
	public void run() {
		try {
			Date startTime = new Date(System.currentTimeMillis());
			//to create symbolic for each and every file in the pmQueue
			SymbolicLinkCreationHelper symbolicLinkCreationHelper=SymbolicLinkCreationHelper.getInstance();
			String nodeType=pmJson.getNodeType();
			String dataType=pmJson.getDataType();
			
			if(nodeType=="RadioNode"){
				if(Pattern.compile(".+_LTE.xml$").matcher(pmJson.getFileLocation()).matches()){
					symbolicLinkCreationHelper.createSymbolicLink(dataType+"_"+nodeType+"_LTE",pmJson.getFileLocation(), log);
				}else if(Pattern.compile(".+_WCDMA.xml$").matcher(pmJson.getFileLocation()).matches()){
					symbolicLinkCreationHelper.createSymbolicLink(dataType+"_"+nodeType+"_WCDMA",pmJson.getFileLocation(), log);
				}else{
					symbolicLinkCreationHelper.createSymbolicLink(dataType+"_"+nodeType,pmJson.getFileLocation(), log);
				}
			}
			else{
					symbolicLinkCreationHelper.createSymbolicLink(dataType+"_"+nodeType,pmJson.getFileLocation(), log);	
			}
			Date endTime = new Date(System.currentTimeMillis());
			Long diff=endTime.getTime()-startTime.getTime();
			log.info("NodeType: " + nodeType + " Time taken(in ms) to create symlink for file: "+ pmJson.getFileLocation() +" is : "+diff.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			log.warning("Exception occured while creating symbolic link for PM!! " +e.getMessage() +'\t'+ pmJson.getNodeType() +  
					"   " +  pmJson.getFileLocation());
		}
		
	}
}
