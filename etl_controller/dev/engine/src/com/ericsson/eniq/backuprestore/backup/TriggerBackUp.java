package com.ericsson.eniq.backuprestore.backup;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.structure.TransferActionBase;
import com.distocraft.dc5000.repository.cache.BackupConfigurationCache;

import ssc.rockfactory.RockFactory;

public class TriggerBackUp extends TransferActionBase{

	public TriggerBackUp(Logger log, RockFactory rockFact, String transferActionType) {
		
		boolean status = false;
	
		Thread t = null;
		
		try {
			status = BackupConfigurationCache.getCache().isBackupStatus();
			
			if (status == true) {
				
				if(transferActionType.contains("Topology")){
					t = new Thread(new BackupTopologyData(log)); 
				}else if(transferActionType.contains("Aggregation")){
					t = new Thread(new BackupAggregatedData(log, rockFact));
				}		
			log.log(Level.INFO, "Triggering " + transferActionType + " action." );
			t.start();
			}else {
				log.log(Level.INFO, " 2 weeks backup is not enabled. ");
			}

		} catch (Exception e) {
			log.log(Level.INFO, "Exception in triggering the action - " + transferActionType +  e.getMessage());
		}
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
