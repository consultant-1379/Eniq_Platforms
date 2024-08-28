package com.ericsson.sim.configConversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;

public class TopologyCSVWriter {
	
	public void write() throws IOException{
		HashMap<Integer,Node> nodes = NodePool.getInstance().getNodes();
		ProtocolPool protocolPool = ProtocolPool.getInstance();
		
		File outputFile = new File("/eniq/sw/conf/sim/SIMconfig.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		bw.write("name,IPAddress,uniqueID,Offset,sftpPort,sftpUserName,snmpCommunity,snmpPort,snmpVersion,Protocols\n");
		
		for (Integer key : nodes.keySet()) {
			Node node = nodes.get(key);
			String dataline = null;
			if (node.getProperty("sftpPort") == null
					&& node.getProperty("sftpUserName") == null
					&& node.getProperty("uniqueID") == null
					&& node.getProperty("SNMPCommunity") == null
					&& node.getProperty("SNMPPort") == null
					&& node.getProperty("SNMPVersion") == null) {
				dataline = "#" + node.getName();
			} else {
				dataline = node.getName();
			}

			dataline = addLine(dataline, node.getProperty("IPAddress"));
			dataline = addLine(dataline, node.getProperty("uniqueID"));
			dataline = addLine(dataline, node.getProperty("Offset"));
			dataline = addLine(dataline, node.getProperty("sftpPort"));
			dataline = addLine(dataline, node.getProperty("sftpUserName"));
			dataline = addLine(dataline, node.getProperty("SNMPCommunity"));
			dataline = addLine(dataline, node.getProperty("SNMPPort"));
			dataline = addLine(dataline, node.getProperty("SNMPVersion"));
			
			dataline = dataline.concat(",");
			
			ArrayList<Integer> protocols = node.getProtocols();
			for(Integer ID : protocols){
				try{
					dataline = dataline.concat(protocolPool.getProtocol(ID).getName()+"|");
				}catch(NullPointerException e){
					System.out.println(e.getMessage());;
				}
				
			}
			
			dataline = dataline.substring(0, dataline.length() - 1);
			bw.write(dataline + "\n");
			
		}
		
		bw.flush();
		bw.close();
	}
	
	
	private String addLine(String dataline, String property){
		if(property != null){
			dataline = dataline.concat("," + property);
		}else{
			dataline = dataline.concat(",");
		}
		return dataline;
	}

}
