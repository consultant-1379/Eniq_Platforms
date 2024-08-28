package com.ericsson.sim.configConversion;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.sftp.serialization.SFTPserFile;

public class SERConversionProcess {

	public HashMap<Integer, Node> nodes = NodePool.getInstance().getNodes();
	public ProtocolPool protocolPool = ProtocolPool.getInstance();
	
	public void convert(ZipFileReader zfr) throws Exception{
		ArrayList<String> serlist = zfr.getSERNames();
		
		for(String filename : serlist){
			try{
				InputStream stream = zfr.getFileStream(filename);
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(stream));
				Map<String, Set<String>> oldser = (Map<String, Set<String>>) in.readObject();
				
				for(String key: oldser.keySet()){
					String name = key.split("=")[1];
					
					String serFilename = getNewSERFileName(name);
					if(serFilename != null){
						String newFilename = "/eniq/sw/conf/sim/pers/" + serFilename;
						SFTPserFile newser = new SFTPserFile();
						
						if(!newser.containsSERFile(newFilename)){
							ArrayList<String> newlist = new ArrayList<String>(oldser.get(key));
							newser.writeserFile(newFilename, newlist);
						}
					}
					
				}
			
			}catch(Exception e){
				System.out.println("Unable to read ser file: " + filename);
			}
			
			
		}
		
		
	}
	
	
	public String getNewSERFileName(String serName){
		String serFilename = null;
		
		ConversionMapping cm = new ConversionMapping();
		HashMap<String, String> mapping = cm.LoadMapping();
		
		for(Node node : nodes.values()){
			if(serName.startsWith(node.getName())){
				String plugin = serName.replace(node.getName(), "");
				
				if(mapping.containsKey(plugin)){
					plugin = mapping.get(plugin);
				}
				
				if(protocolPool.containsProtocol(plugin)){
					Protocol protocol = protocolPool.getProtocol(generateID(plugin));
					serFilename = node.getID()+"_"+protocol.getID()+".ser";
				}
			}
		}
		return serFilename;
	}
	
	
	public int generateID(String name){
		int hash = name.hashCode();
		if(hash < 0){
			hash = hash * -1;
		}
		return hash;
	}
}
