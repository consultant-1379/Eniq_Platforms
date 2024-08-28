package com.ericsson.sim.sftp.plugins;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.plugin.PluginParent;
import com.ericsson.sim.sftp.plugins.parser.ParserParent;

public class AppendedFileProcessing implements PluginParent{

	Logger log = LogManager.getLogger(this.getClass().getName());
	private Node node;
	private SFTPproperties properties;
	
	@Override
	public void execute(Node nodeobj, Protocol protocol) {
		this.node = nodeobj;
		this.properties = (SFTPproperties) protocol;
		
		try{
			ClassLoader classLoader = AppendedFileProcessing.class.getClassLoader();
			String parserName = (String) properties.getProperty("ParserName");
			Class<?> aClass = classLoader.loadClass(parserName);
			ParserParent parser = (ParserParent) aClass.newInstance();
			
			
			File sourceDir = new File(SIMEnvironment.PARSINGPATH, node.getID()+"_"+properties.getID());
			if(!sourceDir.exists()){
				log.error("Directory " + sourceDir.getAbsolutePath() + " could not be found");
			}else{
				File[] dataFiles = sourceDir.listFiles();
				for(File datafile : dataFiles){
					try{
						parser.parseFile(node, properties, datafile);
						datafile.delete();
					}catch(Exception e){
						log.error("Exception in parsing. " , e);
					}
				}
	
			}

		
		
		}catch(Exception e){
			log.error("Exception in operation. Unable to process the data", e);
		}
		
	}

}
