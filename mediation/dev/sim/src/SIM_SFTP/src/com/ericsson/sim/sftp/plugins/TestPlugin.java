package com.ericsson.sim.sftp.plugins;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginParent;

public class TestPlugin implements PluginParent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private SFTPproperties properties;
	
	@Override
	public void execute(Node node, Protocol protocol) {
		this.properties = (SFTPproperties) protocol;
		String serPath = SIMEnvironment.PERSPATH+"/" + node.getID()+"_"+properties.getID()+".ser";
		
		log.debug("ROP: " + node.getName() + " - " + properties.getName());
		log.debug(serPath);
		for (String key : properties.getProperties().keySet()) {
			log.debug(key + " : " + properties.getProperty(key));
		}
		
		
		
	}

}
