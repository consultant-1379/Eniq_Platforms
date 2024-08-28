package com.ericsson.sim.snmp.execution;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import com.adventnet.snmp.beans.SnmpTarget;
import com.adventnet.snmp.mibs.MibOperations;
import com.ericsson.sim.model.protocol.snmp.Counter;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class SNMPParent {

	public Calendar cal;
	private Logger log = LogManager.getLogger(this.getClass().getName());
	protected ArrayList<Counter> counters;
	
	public SNMPParent(){
		Calendar.getInstance();
	}

	
	/**
	 * Common methods that apply for both Get and Get Next. */
	
	public SnmpTarget setCounters(SNMPproperties protocol, SnmpTarget scalar){
		if(protocol.containsProperty("SNMPRETRIES")){
			int intvalue = Integer.parseInt((String)protocol.getProperty("SNMPRETRIES"));
			scalar.setRetries(intvalue);
		}
		
		if(protocol.containsProperty("SNMPTIMEOUT")){
			int intvalue = Integer.parseInt((String)protocol.getProperty("SNMPTIMEOUT"));
			scalar.setTimeout(intvalue);
		}
		
		return scalar;
	}
	
	public String[] getOIDs(CounterSet cs, SnmpTarget scalar, ArrayList<String> disabledCounters){
		MibOperations mibOps = scalar.getMibOperations();
		counters = cs.getCounters();
		String[] cols = new String[counters.size()];
		for(int i=0; i<counters.size(); i++){
			String oid = counters.get(i).getProperty("OID");
			String countername = counters.get(i).getProperty("CounterName");
			
			if(mibOps.getMibNode(oid) == null){
				log.error("OID " + oid + " is not available in supplied mibs and will be omitted from collection");
			}else{
				
				if(!disabledCounters.contains(countername)){
					cols[i] = oid;
				}
			}
		}
		return cols;
	}
	
	
	public boolean isArrayEmpty(String[] counters){
		for (String counter : counters){
			if (counter != null){
				return false;
			}
		}
		return true;
	}
	
	
}
