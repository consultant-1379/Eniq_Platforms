package com.ericsson.navigator.esm.model.pm.snmp;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.MVMEnvironment;
/**
 * This class controls the file writer threads
 * @author ebrifol
 *
 */
public class SNMPCounterSetFileWriterController {

	private final String classname = getClass().getName();
	private static SNMPCounterSetFileWriter[] writers = new SNMPCounterSetFileWriter[MVMEnvironment.NUMBEROFWRITERS];
	private volatile ArrayList<SNMPCounterSetFile> ready = new ArrayList<SNMPCounterSetFile>();
	private boolean shutdown = false;
	private Semaphore sem;

	/**
	 * When instansiated this class will great X amount of File Writer threads. The number of threads is defined 
	 * by the attribute NUMBEROFWRITERS which is loaded from mvm.properties on startup. 
	 */
	public SNMPCounterSetFileWriterController(){
		sem = new Semaphore(1);	
		for(int index = 0; index < MVMEnvironment.NUMBEROFWRITERS; index ++){
			SNMPCounterSetFileWriter writer = new SNMPCounterSetFileWriter();
			writers[index] = writer;
		}
		
		final Thread task = new Thread() { // NOPMD
			@Override
			public void run() {
				while(!shutdown){
					
					if(getSizeOfReadyList()>0){		//if there is data to be written 
						boolean foundMatch = false;
						int index = 0;
						for(index = 0; foundMatch == false && index < writers.length; index++){
							if(writers[index].getState().equals(Thread.State.NEW)){  //if the thread has not been used
								writers[index].addFileToWrite(getIndex(0));
								writers[index].start();
								MVM.logger.debug("Added to thread writer: "+ getIndex(0).getFdn() + " : " + getIndex(0).getCounterSetDefintion().getFileName());
								removeIndex(0);
								foundMatch = true;
							}else if(writers[index].getState().equals(Thread.State.TERMINATED)){ //if the thread is completed its run method, create a new instance
								SNMPCounterSetFileWriter writer = new SNMPCounterSetFileWriter();
								writers[index] = writer;
								writers[index].addFileToWrite(getIndex(0));
								writers[index].start();
								MVM.logger.debug("Added to thread writer: "+ getIndex(0).getFdn() + " : " + getIndex(0).getCounterSetDefintion().getFileName());
								removeIndex(0);
								foundMatch = true;
							}else if(writers[index].isCompleted() == 't'){
								SNMPCounterSetFileWriter writer = new SNMPCounterSetFileWriter();
								writers[index] = writer;
								writers[index].addFileToWrite(getIndex(0));
								writers[index].start();
								MVM.logger.debug("Added to thread writer: "+ getIndex(0).getFdn() + " : " + getIndex(0).getCounterSetDefintion().getFileName());
								removeIndex(0);
								foundMatch = true;
							}
						}

					}
				}
			}
		};
		task.start();

	}
	
	/**
	 * Set the status of the Shutdown flag. The value true will stop any files from getting generated. 
	 * @param shutdown
	 */
	public void setShutdown(boolean shutdown){
		this.shutdown = shutdown;
	}
	
	/**
	 * Will add a single CounterSet to the list of files ready to be generated and written. 
	 * @param scalarCounterSet SNMPCounterSetFile
	 */
	public void addToReadyList(SNMPCounterSetFile scalarCounterSet){
		try {
			acquireSemaphore();
			ready.add(scalarCounterSet);
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".getIndex() Error adding scalar countersets to ready list");
		}
		
	}
	/**
	 * Will add multiple CounterSets to the list of files ready to be generated
	 * @param tabularCounterSets ArrayList<SNMPCounterSetFile>
	 */
	public void addMultipleToReadyList(ArrayList<SNMPCounterSetFile> tabularCounterSets){
		try {
			acquireSemaphore();
			ready.addAll(tabularCounterSets);
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".getIndex() Error adding tabular countersets to ready list");
		}
		
	}
	
	/**
	 * Return the size of the ready list. The number of SNMPCounterSetFile instances ready to be written
	 * 
	 */
	public int getSizeOfReadyList(){
		int size = 0;
		try {
			acquireSemaphore();
			size = ready.size();
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".getIndex() Error getting number of files ready to be written");
		}
		return size;
	}
	
	
	/**
	 * Method will return the SNMPCounterSetFile at the given index
	 * @param integer index
	 */
	public SNMPCounterSetFile getIndex(int index){
		SNMPCounterSetFile file = null;
		try {
			acquireSemaphore();
			file = ready.get(index);
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".getIndex() Error returning file instance");
		}
		return file;
	}
	
	
	/**
	 * Method will remove the SNMPCounterSetFile at the given index
	 * @param integer index
	 */
	public void removeIndex(int index){
		SNMPCounterSetFile file = null;
		try {
			acquireSemaphore();
			ready.remove(index);
			releaseSemaphore();
		} catch (InterruptedException e) {
			MVM.logger.error(classname + ".getIndex() Error removing file instance");
		}
	}
	
	private void acquireSemaphore() throws InterruptedException{
		sem.acquire();	
	}
	
	private void releaseSemaphore(){
		sem.release();
	}
	

}
