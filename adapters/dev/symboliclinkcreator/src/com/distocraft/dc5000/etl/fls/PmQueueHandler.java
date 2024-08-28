package com.distocraft.dc5000.etl.fls;

import com.distocraft.dc5000.etl.symlink.SymbolicLinkCreationHelper;

/*
 * This class is used to add PmJson objects into PmQueue to handle thread pool executor
 */

public class PmQueueHandler implements Runnable{
	
	private PmJson pmJson;
	
	public PmQueueHandler(PmJson pmJson) {
		super();
		this.pmJson = pmJson;
	}

	@Override
	public void run() {
		//to create symbolic for each and every file in the pmQueue
		SymbolicLinkCreationHelper symbolicLinkCreationHelper=new SymbolicLinkCreationHelper();
		symbolicLinkCreationHelper.createSymbolicLink(pmJson.getNodeType(),pmJson.getFileLocation());
		
	}
}
