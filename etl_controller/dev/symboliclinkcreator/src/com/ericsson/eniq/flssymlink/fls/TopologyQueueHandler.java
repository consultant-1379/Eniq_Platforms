package com.ericsson.eniq.flssymlink.fls;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.ericsson.eniq.flssymlink.symlink.SymbolicLinkCreationHelper;

public class TopologyQueueHandler implements Runnable {
	public  SymbolicLinkCreationHelper symbolicLinkCreationHelper;
	
	private LinkedBlockingQueue<TopologyJson> topologyQueue;
	Logger log;
	
	public TopologyQueueHandler(LinkedBlockingQueue<TopologyJson> topologyQueue, Logger log) {
		super();
		this.topologyQueue= topologyQueue;
		this.log = log;
	}
	@Override
	public void run() {
		Date startTime = new Date(System.currentTimeMillis());
		TopologyJson topologyJson=null;
		try {
			while(topologyQueue!=null){
			topologyJson =  topologyQueue.take();
			symbolicLinkCreationHelper=SymbolicLinkCreationHelper.getInstance();
			if(topologyJson.getDataType().equals("TOPOLOGY_WCDMA")){
				symbolicLinkCreationHelper.createSymbolicLink(topologyJson.getDataType()+"_"+topologyJson.getNodeType(),topologyJson.getFileLocation(), log);
			}
			else{
			symbolicLinkCreationHelper.createSymbolicLink(topologyJson.getDataType(),topologyJson.getFileLocation(), log);
			}
			}
		} catch (InterruptedException e) {
			
			log.warning("Exception occured while creating symbolic link for Topology : " + topologyJson.getFileLocation() );
		}
		Date endTime = new Date(System.currentTimeMillis());
		Long diff=endTime.getTime()-startTime.getTime();
		if(topologyJson != null){
		log.fine("Time taken(in ms) to create symlink for file "+topologyJson.getFileLocation()+" is "+diff.toString());
		}
	}
	

	

}
