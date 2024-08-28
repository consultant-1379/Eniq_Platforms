package com.ericsson.sim.testing;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class ProtocolPrinter {

	private Protocol protocol;
	Logger log = LogManager.getLogger(this.getClass().getName());
	
	public ProtocolPrinter(Protocol protocol){
		this.protocol = protocol;
	}
	
	
	public void printProtocol(){
		log.debug("PROTO PRINTER -->");
		
		if(protocol instanceof SFTPproperties){
			SFTPproperties props = (SFTPproperties) protocol;
		
		
			log.debug(props.getName());
			for (String key : props.getProperties().keySet()) {
				log.info(key + " : " + props.getProperty(key));
			}
		}
		
		log.debug("<-- PROTO PRINTER");
		
	}
	
	
}
