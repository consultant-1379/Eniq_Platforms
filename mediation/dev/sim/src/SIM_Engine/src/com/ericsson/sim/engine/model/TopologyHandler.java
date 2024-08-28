package com.ericsson.sim.engine.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.engine.scheduler.RopIntervalScheduler;
import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.pools.RopIntervalPool;

public class TopologyHandler extends DefaultHandler {

	Logger log = LogManager.getLogger(this.getClass().getName());
	protected Node node = null;
	protected String tagName = "";
	private RopIntervalScheduler scheduler;
	private NodePool nodepool = NodePool.getInstance();
	protected StringBuffer accumulator;

	public TopologyHandler(RopIntervalScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.equalsIgnoreCase("Node")) {
			String name = attributes.getValue("name");
			String IpAddress = attributes.getValue("IPAddress");
			String uniqueID = attributes.getValue("uniqueID");

			String idstring = name + ":" + IpAddress + ":" + uniqueID;
			if (nodepool.hasNode(idstring.hashCode())) {
				node = nodepool.getNode(idstring.hashCode());
			} else {
				node = new Node();
				node.setName(name);
				node.addProperty("IPAddress", IpAddress);
				node.addProperty("uniqueID", uniqueID);
			}
			
			//Clear existing protocols from the node. The type may have changed.
			node.clearProtocols();
		} else {
			tagName = qName;
		}
		accumulator = new StringBuffer();
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("Node")) {

			ArrayList<Integer> protocolIDs = node.getProtocols();
			node.generateID();
			
			try{
				nodepool.addNode(node);
				for (Integer ID : protocolIDs) {
					int intervalID = ProtocolPool.getInstance().getProtocol(ID).getInterval();
					log.info("Configuring: " + node.getName() + ": " + ProtocolPool.getInstance().getProtocol(ID).getName());
					RopInterval interval = RopIntervalPool.getInstance().getInterval(intervalID);
					interval.addNode(node.getID());
					RopIntervalPool.getInstance().addInterval(interval);
				}
			}catch(Exception e){
				log.error("Unable to configure node " + node.getName()+". ", e);
			}
			node=null;
		}
		else if (tagName.equalsIgnoreCase("Protocols")) {
			addProtocol(accumulator.toString().split(","));
		}else{
			addProperty(tagName, accumulator.toString());
		}
				
		tagName = "";
	}

	@Override
	public void characters(char ch[], int start, int length) {
		if (node != null) {
			accumulator.append(new String(ch, start, length));
		}
	}
	
	public void addProperty(String tagName, String value){
		if(!tagName.trim().equalsIgnoreCase("") && !value.trim().equalsIgnoreCase("")){
			node.addProperty(tagName, value);
		}
	}
	
	public void addProtocol(String protocolNames[]){
		for (String name : protocolNames) {
			int hash = name.hashCode();
			if(hash<0){
				hash = hash * -1;
			}
			if (ProtocolPool.getInstance().containsProtocol(hash)) {
				node.addProtocol(hash);
			} else {
				log.error("Unable to map " + name + " for node "
						+ node.getName() + ". Plugin does not exist");
			}
		}
	}

	@Override
	public void endDocument() {
		log.debug("Topology file parsed, Model handed over to scheduler");
		
		// thread to make SFTP and SNMP sessions
		scheduler.scheduleRopIntervals();
	}

	public void startup() throws Exception {
		clearConfiguration();
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		File topologyFile = new File(SIMEnvironment.TOPOLOGYCONFIG);
		if(!topologyFile.exists()) {
			log.error("No topology.xml file found in conf directory");
		}else{
			saxParser.parse(topologyFile, this);
			log.info("Topology file found and parsed");
		}

	}
	
	public void clearConfiguration(){
		nodepool.removeNodes();
		for(RopInterval rop : RopIntervalPool.getInstance().getRopIntervals().values()){
			rop.removeNodes();
		}
	}
	

	public String getName() {
		return this.getClass().getName();
	}

}
