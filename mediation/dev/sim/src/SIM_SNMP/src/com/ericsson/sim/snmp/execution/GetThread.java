package com.ericsson.sim.snmp.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
import com.ericsson.sim.snmp.execution.SNMPParent;
import com.ericsson.sim.snmp.file.CSVFileWriter;

import com.adventnet.snmp.beans.SnmpTarget;
import com.ericsson.sim.snmp.packet.RequestPackets;

public class GetThread extends SNMPParent{
	
	private Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SNMPproperties properties;
	private CounterSet cs;
	private ArrayList<String> disabledCounters;
	protected SNMPData data = new SNMPData();
	
	public GetThread(){
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
			// SNMP Target from request packet
			SnmpTarget scalar = RequestPackets.getInstance().getPackets(node);
			log.info("Request sent");
			scalar = setCounters(properties, scalar);
			String[] cols = getOIDs(cs, scalar, disabledCounters);
						
			if(!isArrayEmpty(cols)){
				data = new SNMPData();
				
				scalar.setObjectIDList(cols);
				String result[] = scalar.snmpGetList();
				
				if (result == null){
		        	throw new SIMException("Request failed or timed out. \n"+ scalar.getErrorString());
		        } else {
		        	log.info("Response Recieved. ");
		        	ArrayList<String> errorCounters = new ArrayList<String>();
			        String header = "";
			        String line = "";
				    
			        int disabledcounters = 0;
			        List<String> oids = Arrays.asList(cols);
			        for(Counter counter : counters){
			        	String value = "";
			        	
			        	if(oids.contains(counter.getProperty("OID"))){
			        		int index = oids.indexOf(counter.getProperty("OID"));
			        		value = result[index - disabledcounters];
			        		if(value.equals("NULL")){
			        			value = "";
			        			errorCounters.add(counter.getProperty("CounterName"));
			        		}
			        	}
			        	else{
			        		disabledcounters+=1;
			        	}
			        
				        	
			        	if(header.equals("")){
			        		header = counter.getProperty("CounterName");
			        		line = "0,"+value;
			        	}else{
			        		header = header + "," + counter.getProperty("CounterName");
			        		line = line + "," + value;
			        	}
			        	
			        }
			        
			        data.setHeader(header);
			        if(errorCounters.size() != result.length){ //Dont add a line that contains no counter data
		            	data.addToData(line);
		            }
	
			        
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
			    }
			
			}else{
				log.warn("Collection skipped as there are no counters enabled and/or available in the MIBS");
			}
			
		}catch(SIMException e){
			log.error("Error while performing get. "+ e);
		}catch(Exception e){
			log.error("Error while performing get. ", e);
		}
		
		
	}

}
