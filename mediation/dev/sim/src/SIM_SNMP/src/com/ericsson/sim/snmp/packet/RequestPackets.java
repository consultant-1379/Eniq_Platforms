package com.ericsson.sim.snmp.packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.adventnet.snmp.beans.SnmpTarget;
import com.adventnet.snmp.mibs.MibException;
import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.node.Node;

public class RequestPackets {
	
	private Logger log = LogManager.getLogger(this.getClass().getName());
	private static RequestPackets obj;
	private static int instance = 0;
	private HashMap<Integer, SnmpTarget> packets;
	
	private RequestPackets(){
		obj = this;
		packets = new HashMap<Integer, SnmpTarget>();
	}

	public synchronized SnmpTarget getPackets(Node node){
		SnmpTarget scalar = null;
		try{
			if(packets.containsKey(node.getID())){
				scalar = packets.get(node.getID());
			}else{
				scalar = createNodePackets(node);
				packets.put(node.getID(), scalar);
			}
	
		}catch(Exception e){
			e.printStackTrace();
			log.error("Exception: ",e);
		}
		return scalar;
	}
	
	public void removePackets(Integer NodeID){
		packets.remove(NodeID);
	}
	
	
	private SnmpTarget createNodePackets(Node node) throws FileNotFoundException, MibException, IOException{
		SnmpTarget nodescalar = initDefaultPackets();
		
		nodescalar.setTargetHost((String) node.getProperty("IPAddress"));
		
		String stringvalue = (String)node.getProperty("SNMPCommunity");
		nodescalar.setCommunity(stringvalue);
		
		int intvalue = Integer.parseInt((String)node.getProperty("SNMPPORT"));
		nodescalar.setTargetPort(intvalue);
		
		if(node.getProperty("SNMPVersion").equals("v3")){
			nodescalar.setSnmpVersion(SnmpTarget.VERSION3);
			
			stringvalue = (String)node.getProperty("SNMPUSERNAME");
			nodescalar.setPrincipal(stringvalue);
			
			stringvalue = (String)node.getProperty("SNMPAUTHPASSWORD");
			if(stringvalue != null){
				nodescalar.setAuthPassword(stringvalue);
			}
			
			stringvalue = (String)node.getProperty("SNMPAUTHPROTOCOL");
			if(stringvalue.equals("SHA")){
				nodescalar.setAuthProtocol(SnmpTarget.SHA_AUTH);
			}else if(stringvalue.equals("MD5")){
	        	nodescalar.setAuthProtocol(SnmpTarget.MD5_AUTH);
			}else{
	        	nodescalar.setAuthProtocol(SnmpTarget.NO_AUTH);
			}
			
			stringvalue = (String)node.getProperty("SNMPPRIVPASSWORD");
			if(stringvalue != null){
				nodescalar.setPrivPassword(stringvalue);
			}
			
			stringvalue = (String)node.getProperty("SNMPPRIVPROTOCOL");
			if(stringvalue.equals("AES-128")){
				nodescalar.setPrivProtocol(SnmpTarget.CFB_AES_128);
			}else if(stringvalue.equals("AES-192")){
	        	nodescalar.setPrivProtocol(SnmpTarget.CFB_AES_192);
			}else if(stringvalue.equals("AES-256")){
	        	nodescalar.setPrivProtocol(SnmpTarget.CFB_AES_256);
			}else if(stringvalue.equals("3DES")){
	        	nodescalar.setPrivProtocol(SnmpTarget.CBC_3DES);
			}else if(stringvalue.equals("DES")){
	        	nodescalar.setPrivProtocol(SnmpTarget.CBC_DES);
			}
			
			stringvalue = (String)node.getProperty("SNMPCONTEXTNAME");
			if(stringvalue != null){
				nodescalar.setContextName(stringvalue);
			}
			stringvalue = (String)node.getProperty("SNMPCONTEXTID");
			if(stringvalue != null){
				nodescalar.setContextID(stringvalue);
			}
			
			nodescalar.create_v3_tables();
			
		}else{
			nodescalar.setSnmpVersion(SnmpTarget.VERSION2C);
		}

		
		return nodescalar;
	}
	
	private SnmpTarget initDefaultPackets() throws FileNotFoundException, MibException, IOException{
		SnmpTarget target = new SnmpTarget();

		File mibDir = new File(SIMEnvironment.MIBSPATH);
		log.debug(mibDir.getAbsolutePath());
		String mibs = null;
		for(File mib : mibDir.listFiles()){
			if(mibs == null){
				mibs = mib.getAbsolutePath();
			}else{
				mibs = mibs + " " + mib.getAbsolutePath();
			}
		}
		target.loadMibs(mibs);
		
		return target;
	}
	
	public synchronized static RequestPackets getInstance(){
		if(instance == 0){
			instance = 1;
			return new RequestPackets();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
}
