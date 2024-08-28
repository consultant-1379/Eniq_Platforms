package com.ericsson.navigator.esm.model.pm.snmp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
/**
 * This class is the data structure that all received SNMP countersets will be stored before writing the files. 
 * Scalar countersets received from SNMPGetCounterSetRequest.java are passed on directly to the file writer. 
 * Tabular countersets received from SNMPGetNextCounterSetRequest.java will be stored until the 
 * tabular rop finishes and all countersets are received. 
 * @author ebrifol
 *
 */
public class SNMPCounterSetStore {

	private final String classname = getClass().getName();
	private static SNMPCounterSetStore m_instance = null;
	
	private ArrayList<SNMPCounterSetFile> processing = new ArrayList<SNMPCounterSetFile>();
	private static SNMPCounterSetFileWriterController writerController;
	private long currentTime;
	private static Semaphore sem;
	
	/**
	 * Method to retrieve an instance of the data store.
	 * @return Return an instance of SNMPCounterSetStore
	 */
	public static SNMPCounterSetStore getInstance() {
		if(m_instance == null){
			m_instance = new SNMPCounterSetStore();
			sem = new Semaphore(1);	
			writerController = new SNMPCounterSetFileWriterController();
		}
		return m_instance;
	}
	
	/**
	 * method will destory existing instance
	 */
	public void destroyInstance(){
		writerController.setShutdown(true);
		m_instance = null;
	}

	/**
	 * This is called when the SNMP ROP starts so the store will be
	 * populated with the nodes that requests are being sent to 
	 * @param fdn The FDN of the node the CounterSet came from.
	 * @param ipAddress The Ip address of the node the CounterSet came from.
	 * @param counterSetDef CounterSetDefinition for that node. 
	 */
	public void addNewCounterSetFile(String fdn, String ipAddress, CounterSetDefinition counterSetDef){
		try {
			acquireSemaphore();
			updateTime();
			SNMPCounterSetFile counterSetFile = new SNMPCounterSetFile(fdn, ipAddress, counterSetDef, currentTime);
			
			if(processing.contains(counterSetFile)){
				processing.remove(processing.indexOf(counterSetFile));
			}
				
			processing.add(counterSetFile);
			MVM.logger.debug("Added to processing: "+ fdn + " : " + counterSetDef.getFileName());
		
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".sortNewTabularCounterset() Unable to aquire lock on data store");
		}
	}
	

	/**
	 * This method is called from SNMPGetCounterSetRequest and SNMPGetNextCounterSetRequest.
	 * They pass in the recieved counterset and it is sorted. All data from the Get class will 
	 * be written immediately. All countersets from the GetNext will be stored until all the data 
	 * has been received. 
	 * @param counterset The received CounterSet.
	 * @param ready Status for the CounterSet to be written. Tabular = false, Scalar = true
	 */
	public void sortNewCounterset(CounterSet counterset, boolean ready){
		try {
			acquireSemaphore();

			boolean foundMatch = false;
			int index = 0;
			for(index = 0; foundMatch == false && index < processing.size(); index++){
				if(processing.get(index).equals(counterset)){
					processing.get(index).addtoList(counterset, ready);
					foundMatch = true;
				}
			}
			if(ready && foundMatch){
				index = index-1;
				writerController.addToReadyList(processing.get(index));
				MVM.logger.debug("Added to ready List: "+ processing.get(index).getFdn() + " : " + processing.get(index).getCounterSetDefintion().getFileName());
				processing.remove(index);
			}
			
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".sortNewTabularCounterset() Unable to aquire lock on data store");
		}
	}
	

	/**
	 * This is called from SNMPGetNextCounterSetRequest when the tabular process has finished. 
	 * All tabular entries are moved to the file writer to be written. 
	 */
	public void tabularFinished(String fdn, String counterSetFilename){
		try{
			acquireSemaphore();
			boolean foundMatch = false;
			ArrayList<Integer> removeIndex = new ArrayList<Integer>();
			
			for(int i=0; foundMatch == false && i<processing.size(); i++){
				SNMPCounterSetFile file = processing.get(i);
				
				if(file.getListOfCounterSets().size()>0 && !file.isReadytoWrite() && file.getFdn().equals(fdn) && file.getCounterSetDefintion().getFileName().equals(counterSetFilename)){
					foundMatch = true;
					writerController.addToReadyList(file);
					removeIndex.add(i);
					MVM.logger.debug("Added to ready List: "+ file.getFdn() + " : " + file.getCounterSetDefintion().getFileName());
				}
			}
			
			Collections.sort(removeIndex);
			
			for(int i=removeIndex.size()-1; i>=0; i--){
				try{
					processing.remove(processing.get(removeIndex.get(i)));			
					
				}catch(Exception e){
					MVM.logger.error(classname + ".tabularFinished() Error removing file from list");
				}
					
			}
			
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".tabularFinished() Unable to aquire lock on data store");
		} catch(Exception e){
			MVM.logger.error(classname + ".tabularFinished() Error occured when adding files to writer");
		}
		
	}
	
	private void acquireSemaphore() throws InterruptedException{
		sem.acquire();	
	}
	
	private void releaseSemaphore(){
		sem.release();
	}
	
	public long getROPStartingTime(){
		return currentTime;
	}
	public void updateTime(){
		this.currentTime = System.currentTimeMillis();
	}
	
}
