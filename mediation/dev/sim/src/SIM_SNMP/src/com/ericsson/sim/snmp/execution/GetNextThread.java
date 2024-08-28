package com.ericsson.sim.snmp.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.adventnet.snmp.beans.SnmpTarget;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.executor.ExecutorThread;
import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.snmp.Counter;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;
import com.ericsson.sim.snmp.data.SNMPData;
import com.ericsson.sim.snmp.file.CSVFileWriter;
import com.ericsson.sim.snmp.packet.RequestPackets;

public class GetNextThread extends SNMPParent{
	
	private Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SNMPproperties properties;
	private CounterSet cs;
	private ArrayList<String> disabledCounters;
	protected SNMPData data;
	
	public GetNextThread(){
		super();
	}
	
	public void init(Node node, Protocol protocol, CounterSet cs, ArrayList<String> disabledCountersList) {
		this.node = node;
		this.properties = (SNMPproperties) protocol;
		this.cs = cs;
		this.disabledCounters = new ArrayList<String>();
		this.disabledCounters.addAll(disabledCountersList);
	}
	
	public void run() {
		try{
			log.info("Collecting from " + node.getName() + ": " + properties.getName() + ": " + cs.getName());
			SnmpTarget table = RequestPackets.getInstance().getPackets(node);
			table = setCounters(properties, table);
			String[] cols = getOIDs(cs, table, disabledCounters);
			
			
			if(!isArrayEmpty(cols)){
			
				data = new SNMPData();
				
				table.setObjectIDList(cols);
				
				boolean collect = true;
				ArrayList<String> errorCounters = new ArrayList<String>();
				List<String> oids = Arrays.asList(cols);
				int tableSize = -1;
				do{
					String result[] = table.snmpGetNextList(); // do a getnext request
			        
			        if (result == null){
			        	collect=false;
			        	throw new SIMException("Request failed or timed out. "+ table.getErrorString());
			        	
			        }else { // print the values 
			        	tableSize++;
			        	String firstresponsecounter = table.getObjectID(0);
			        	String firstrequestedcounter = findFirstRequestedCounter();
			        	if(firstresponsecounter.contains(firstrequestedcounter)){ //Test if we have rolled to a new table
			        		
			        		String line = "";
			        		String vci = "";
			        		int disabledcounters = 0;
			        		for(Counter counter : counters){
			        			String value = "";
			        			if(oids.contains(counter.getProperty("OID"))){
			        				int index = oids.indexOf(counter.getProperty("OID"));
					        		value = result[index - disabledcounters];
					        		if(value.equals("NULL")){
					        			value = "";
					        			errorCounters.add(counter.getProperty("CounterName"));
					        		}
					        		
					        		
					        		if(vci.equals("")){
					        			String responseoid = table.getObjectID(index);
					        			String vciCountername = findVCIcounter(responseoid);
					        			
					            		if(responseoid.contains(vciCountername)){
					            			vci = responseoid.split(vciCountername+".")[1];
					            		}else{
					            			String counteroid = counter.getProperty("OID");
					            			vci = responseoid.replace(counteroid+".", "");
					            		}
					        		}
					        	}else{
					        		disabledcounters+=1;
					        	}
			        			line = line + "," + value;
			        			
			        		}
			        		
			        		if(errorCounters.size() != result.length){ //Dont add a line that contains no counter data
			        	    	data.addToData(vci + line);
			        	    }
			        		
			        	}else{
			        		collect=false; //triggers the end of the table
			        	}
			        }
				}while(collect);
				log.info(tableSize + " Responses Recieved.");
				
				String header = "";
				for(Counter counter : counters){
					if(header.equals("")){
		        		header = counter.getProperty("CounterName");
		        	}else{
		        		header = header + "," + counter.getProperty("CounterName");
		        	}
				}
				data.setHeader(header);
				
				// Populate SNMP Data
				String timeString = node.getName() + ":" + properties.getName() + ":" + "ROPStartTime";
				data.setTimeOfRop(System.getProperty(timeString));
				data.setMeasurement(cs.getName());
				
				RopInterval interval = RopIntervalPool.getInstance().getInterval(properties.getInterval());
				data.setROP(interval.getProperty("ROP"));
			
				if(errorCounters.size()>0){
		        	log.error(errorCounters.size() + " counters did not return values: " + errorCounters);
		        }
				
				// pass data to CSV filewriter
		        CSVFileWriter file = new CSVFileWriter(node, properties, data);   
		        // call filewriter  
		        file.writeFile();
			
			}else{
				log.warn("Collection skipped as there are no counters enabled and/or available in the MIBS");
			}
			
		}catch(SIMException e){
			log.error("Error while performing get. "+ e);
		}catch(Exception e){
			log.error("Error while performing get. ", e);
		}
		
		
	
	}
	
	public String findFirstRequestedCounter(){
    	for(int i=0; i<counters.size(); i++){
    		String countername = counters.get(i).getProperty("CounterName");
    		if(!disabledCounters.contains(countername)){
    			return countername;
    		}
    		
    	}
    	return "";
	}
	
	public String findVCIcounter(String responseOID){
		for(int i=0; i<counters.size(); i++){
    		String countername = counters.get(i).getProperty("CounterName");
    		if(responseOID.contains(countername)){
    			return countername;
    		}
    		
    	}
    	return "";
		
	}

}
