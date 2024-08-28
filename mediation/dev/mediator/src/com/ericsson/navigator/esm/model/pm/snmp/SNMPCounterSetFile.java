package com.ericsson.navigator.esm.model.pm.snmp;

import java.util.ArrayList;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;

public class SNMPCounterSetFile {
	private String fdn;
	private CounterSetDefinition counterSetFileName;
	private String ipAddress;
	private ArrayList<CounterSet> counterSets;
	private long timeOfRop;
	private boolean readyToWrite;
	
	public SNMPCounterSetFile(String fdn, String ipAddress, CounterSetDefinition counterSetFileName, long ropTime){
		counterSets = new ArrayList<CounterSet>();
		this.fdn = fdn;
		this.counterSetFileName = counterSetFileName;
		this.timeOfRop = ropTime;
		this.ipAddress = ipAddress;
	}
	
	public String getFdn() {
		return fdn;
	}
	public void setFdn(String fdn) {
		this.fdn = fdn;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public long getTimeOfRop() {
		return timeOfRop;
	}
	public void setTimeOfRop(long ropTime) {
		this.timeOfRop = ropTime;
	}
	public CounterSetDefinition getCounterSetDefintion() {
		return counterSetFileName;
	}
	public void setCounterSetFileName(CounterSetDefinition counterSetFileName) {
		this.counterSetFileName = counterSetFileName;
	}
	public void addtoList(CounterSet counterset, boolean ready){
		counterSets.add(counterset);
		readyToWrite = ready;
	}
	public ArrayList<CounterSet> getListOfCounterSets(){
		return counterSets;
	}
	
	public boolean isReadytoWrite(){
		return readyToWrite;
	}
	
	@Override
	public boolean equals(Object object){
		if(object instanceof CounterSet){
			CounterSet counterset = (CounterSet) object;
			
			if(counterset.getManagedObjectInstance().equals(fdn) && counterSetFileName.getFileName().equals(counterset.getCounterSetId()+".xml")){
				return true;
			}
		}
		
		if(object instanceof SNMPCounterSetFile){
			SNMPCounterSetFile counterset = (SNMPCounterSetFile) object;
			if(counterset.getFdn().equals(fdn) && counterset.getCounterSetDefintion().getFileName().equals(counterSetFileName.getFileName()) && counterset.getIpAddress().equals(ipAddress)){
				return true;
			}
		}
		return false;
	}
	
}
