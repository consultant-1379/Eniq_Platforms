package com.ericsson.sim.model.pools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.sim.model.protocol.snmp.disable.DisabledCollection;

public class DisabledPool extends DefaultHandler {

	private static DisabledPool obj;
	private static int instance;
	private HashMap<String, DisabledCollection> disabledCollections;
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	private DisabledCollection collection;
	private String measName;
	private ArrayList<String> counternames;
	
	private DisabledPool(){
		obj = this;
		disabledCollections = new HashMap<String, DisabledCollection>();
	}
	
	public synchronized DisabledCollection getDisabledCollection(Integer nodeID){
		for(DisabledCollection dc :  disabledCollections.values()){
			if (dc.containsNode(nodeID)){
				return dc;
			}
		}
		
		return new DisabledCollection();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equalsIgnoreCase("SNMPDisable")) {
			collection = new DisabledCollection();
			collection.setcollectionName(attributes.getValue("name"));
			collection.setPluginName(attributes.getValue("pluginName"));
		}
		else if (qName.equalsIgnoreCase("Node")) {
			String name = attributes.getValue("name");
			String IpAddress = attributes.getValue("IPAddress");
			String uniqueID = attributes.getValue("uniqueID");
			String idstring = name + ":" + IpAddress + ":" + uniqueID;
			int hash = idstring.hashCode();
			if(hash<0){
				hash = hash * -1;
			}
			collection.addNodeToList(hash);
		}
		else if (qName.equalsIgnoreCase("Measurement")){
			measName = attributes.getValue("name");
			counternames = new ArrayList<String>();
		}
		else if (qName.equalsIgnoreCase("Counter")){
			counternames.add(attributes.getValue("name"));
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("SNMPDisable")) {
			disabledCollections.put(collection.getcollectionName(), collection);
		}
		else if (qName.equalsIgnoreCase("Measurement")){
			collection.addCounterSet(measName, counternames);
		}
	}
	
	public void parseInput(String filePath){
		try{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			File disabledFile = new File(filePath);
			if(disabledFile.exists()) {
				saxParser.parse(disabledFile, this);
				log.info("SNMP disabled counters file found and parsed");
			}
		}catch(Exception e){
			log.error("Error occured parsing SNMP disabled counters file", e);
		}
	}
	
	public static DisabledPool getInstance(){
		if(instance ==0){
			instance = 1;
			return new DisabledPool();
		}
		if(instance == 1){
			return obj;
		}
		
		return null;
	}
	
}
