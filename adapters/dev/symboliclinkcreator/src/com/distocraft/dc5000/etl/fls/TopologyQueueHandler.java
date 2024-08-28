package com.distocraft.dc5000.etl.fls;

import java.util.concurrent.LinkedBlockingQueue;

import com.distocraft.dc5000.etl.symlink.SymbolicLinkCreationHelper;

public class TopologyQueueHandler implements Runnable {
	public  SymbolicLinkCreationHelper symbolicLinkCreationHelper = new SymbolicLinkCreationHelper();
	
	private LinkedBlockingQueue<TopologyJson> topologyQueue;
	public TopologyQueueHandler(LinkedBlockingQueue<TopologyJson> topologyQueue) {
		super();
		this.topologyQueue= topologyQueue;
	}
	@Override
	public void run() {
		TopologyJson topologyJson=null;
		try {
			while(topologyQueue!=null){
			topologyJson = topologyQueue.take();
			symbolicLinkCreationHelper.createSymbolicLink("Topology",topologyJson.getFileLocation());
			}
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		symbolicLinkCreationHelper.createSymbolicLink("Topology",topologyJson.getFileLocation());

	}

	

}
