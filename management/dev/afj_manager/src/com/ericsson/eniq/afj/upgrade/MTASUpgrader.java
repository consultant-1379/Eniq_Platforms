package com.ericsson.eniq.afj.upgrade;

import static com.ericsson.eniq.afj.common.PropertyConstants.DATAFORMAT_DELIMITER;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJMeasurementCounter;
import com.ericsson.eniq.afj.common.AFJMeasurementTag;
import com.ericsson.eniq.afj.common.AFJMeasurementType;
import com.ericsson.eniq.afj.common.CommonSetGenerator;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.engine.EngineRestarterWrapper;
import com.ericsson.eniq.exception.AFJException;

public class MTASUpgrader implements TechPackUpgrader {
	  private final Logger log = Logger.getLogger(this.getClass().getName());
		
		private RockFactory dwhrep;	
		
		private RockFactory etlrep;
		
		private RockFactory dwh;
		
		private RockFactory dbadwh;
		
		private AFJDatabaseHandler connection;
		
		private CommonUpgradeUtil commonUpgradeUtil;
		
		final private AFJDatabaseCommonUtils commonUtils = AFJDatabaseCommonUtilsFactory.getInstance();
		
		private String tpVersion;
		
		/**
		 * Method to identify new counters in existing meas types. Calls the logic to upgrade new counters in existing meas types.
		 * @param delta
		 * @return
		 * @throws AFJException
		 */
		@Override
		public String upgrade(final AFJDelta delta)throws AFJException{
			
			String returnValue = null;
			
			connection = AFJDatabaseHandler.getInstance();
			
			dwhrep = connection.getDwhrep();
			
			etlrep = connection.getEtlrep();	
			
			dwh = connection.getDwh();
			
			dbadwh = connection.getDbaDwh();
			
			commonUpgradeUtil = new CommonUpgradeUtil(dwhrep, etlrep, dwh, dbadwh);
			
			tpVersion = commonUtils.getActiveTechPackVersion(delta.getTechPackName(), dwhrep);
			
			log.info("Active version of TP:"+tpVersion);
			
			/*
			 * Separate the meas types into new meas types and existing meas types with new counters.
			 */
			final List<AFJMeasurementType> newCountersList = new ArrayList<AFJMeasurementType>();
			final List<AFJMeasurementType> newMeasTypeList = new ArrayList<AFJMeasurementType>();
			
			for(AFJMeasurementType amt:delta.getMeasurementTypes()){
				if(amt.isTypeNew()){
					newMeasTypeList.add(amt);				
				}
				else{
					newCountersList.add(amt);
				}
			}				
			
			// Initialise Caches.
			commonUpgradeUtil.initialiseCaches();
			
			// Check if the new countersList is not empty.
			if(!newCountersList.isEmpty()){
				returnValue = upgradeNewCountersInExistingMeasType(newCountersList);			
			}
	        
			// REDO: Clumsy hack. There should be a way for the engine to know the new meas type. Restarting engine picks up though. 
			log.info("Restarting Engine");
			try{
			  EngineRestarterWrapper.execute();
			} catch (Exception e) {
				throw new AFJException("Exception in restarting engine:" + e.getMessage());
			}		
			log.info("Engine restart completed successfully"); 
			
			return returnValue;
		}	
		
		/**
		 * Method to upgrade the new counters in existing meas types. Does the below three operations:
		 * 1.) Calls logic to insert values into dwhrep for new counters.
		 * 2.) Calls logic to generate the new loader action for the existing meas types with new counters.
		 * 3.) Calls VersionUpdateAction logic to see the counters in the fact tables.
		 * 
		 * @param mtList
		 * @return
		 * @throws AFJException
		 */
		public String upgradeNewCountersInExistingMeasType(final List<AFJMeasurementType> mtList)throws AFJException{
			
			String returnValue = null;		
			CommonSetGenerator csg = null;
			
			try{	
				String measName = null;
				for(AFJMeasurementType mType:mtList){			
					csg = commonUpgradeUtil.initialiseCommonSetGenerator(mType.getTpName());
					measName = mType.getTpVersion() + DATAFORMAT_DELIMITER + mType.getTypeName();
					long measColumnStartColumnNumber = commonUtils.getMeasurementColumnNumber(measName,dwhrep);
					long measCounterStartColumnNumber = commonUtils.getNextMeasurementCounterColumnNumber(measName, dwhrep);
					log.info("measColumnstartColumnNumber:"+measColumnStartColumnNumber);
					log.info("measCounterStartColumnNumber:"+measCounterStartColumnNumber);
					long start = System.currentTimeMillis();

					for(AFJMeasurementTag mTag:mType.getTags()){
						for(AFJMeasurementCounter mCounter:mTag.getNewCounters()){
							log.info("Node Version Update for counter:"+mCounter.getCounterName()+" in moc:"+mTag.getTagName()+" in meastype:"+measName+" started." );
							log.info("Creating entries in dwhrep for counter:"+mCounter.getCounterName());
							commonUpgradeUtil.createDwhrepEntries(mType.getTpName(), measName, mCounter, measColumnStartColumnNumber, measCounterStartColumnNumber);
							measColumnStartColumnNumber += 1;
							measCounterStartColumnNumber += 1;
						}
					}	
					long end = System.currentTimeMillis();
					log.info("Created entries in dwhrep for counter:"+mType.getTypeName()+" in "+( end - start) + " ms");

					start = System.currentTimeMillis();
					log.info("Creating entries in etlrep for meastype:"+mType.getTypeName());
					csg.updateLoaderAction(measName);
					end = System.currentTimeMillis();
					log.info("New loader action for meastype:"+mType.getTypeName()+" created in "+( end - start) + " ms");
					
					log.info("Calling VersionUpdateAction");	
					// Call the version update action for impacted meastypes.
					start = System.currentTimeMillis();
					commonUpgradeUtil.versionUpdateAction(mType.getTpName(),mType.getTypeName(), false);
					end = System.currentTimeMillis();				
					log.info("VersionUpdateAction for:"+mType.getTypeName()+" completed in " + (end - start) + " ms");		
					
				}

				// Commit the transactions and close the connection
				AFJDatabaseHandler.getInstance().commitTransaction(true);
				
				log.info("Committed the transactions");

				returnValue = "Upgrade for new counters in existing measurement types successful. Committed the delta to the db.";
				log.info("Upgrade successfull. Successfully committed to dwhrep, etlrep and dwh.");
			}
			catch(Exception e){
				// Rollback the transactions and close the connection.
				try {
					AFJDatabaseHandler.getInstance().commitTransaction(false);
				} catch (AFJException ae) {				
					throw new AFJException("Node Version Update Failure. Roll back failure.Message:"+ae.getMessage()+" Exception:"+ae.toString());
				}			
				throw new AFJException("Node Version Update Failure. Upgrade for new counters in existing measurement types failed and transaction rolledback successfully."+" Message:"+e.getMessage()+" Exception:"+e.toString());
			}
			catch(Throwable t){
				// Rollback the transactions and close the connection.			
				try {
					AFJDatabaseHandler.getInstance().commitTransaction(false);
				} catch (AFJException ae) {				
					throw new AFJException("Node Version Update Failure. Roll back failure.Message:"+ae.getMessage()+" Exception:"+ae.toString());
				}			
				throw new AFJException("Node Version Update Failure. Upgrade for new counters in existing measurement types failed and transaction rolledback successfully."+" Message:"+t.getMessage()+" Throwable:"+t.toString());
			}		
			return returnValue;
		}		
}
