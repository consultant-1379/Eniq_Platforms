package com.ericsson.sim.configConversion;

import java.io.InputStream;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.parser.XmlParser;
import com.ericsson.sim.model.parser.util_prop_parser;
import com.ericsson.sim.model.pools.NodePool;
import com.ericsson.sim.model.pools.ProtocolPool;

public class ConversionProcess {

	public void convert(XmlParser parser, ZipFileReader zfr) throws Exception {
		ConversionMapping cm = new ConversionMapping();
		System.out.println("Loading mapping for the conversion");
		HashMap<String, String> mapping = cm.LoadMapping();
		
		ProtocolPool protocolPool = ProtocolPool.getInstance();

		NodeList list = parser.nodeList("SNOSNE");

		for (int x = 0; x < list.getLength(); x++) {

			Element nodeitem = (Element) list.item(x);
			System.out.println(nodeitem.getAttribute("cimname"));

			Node newNode = new Node();
			newNode.setName(nodeitem.getAttribute("cimname"));
			newNode.addProperty("IPAddress",nodeitem.getAttribute("snoshostip"));
			newNode.addProperty("uniqueID", "");
			newNode.addProperty("Offset", "");
			
			NodeList SFTP = nodeitem.getElementsByTagName("RemoteFileFetch");

			if (SFTP.getLength() > 0) {
				
				for (int y = 0; y < SFTP.getLength(); y++) {
					Element SFTPitem = (Element) SFTP.item(y);
					String pluginName = SFTPitem.getAttribute("pluginDir");
					String newpluginName = pluginName;
					
					if(mapping.containsKey(pluginName)){
						newpluginName = mapping.get(pluginName);
						System.out.println("Plugin found in mapping. New plugin name is " + newpluginName);
					}
					
					if (protocolPool.containsProtocol(generateID(newpluginName))) {
						util_prop_parser propParser = new util_prop_parser();
						InputStream stream = zfr.getFileStream("backup/plugins/" + pluginName+ "/config.xml");
						propParser.loadFile(stream);
						newNode.addProperty("sftpUserName",propParser.getValue("user"));
						newNode.addProperty("sftpPort",propParser.getValue("port"));
						newNode.addProtocol(generateID(newpluginName));
					} else {
						System.out.println("Plugin "+ pluginName+ " is not supported in the upgraded configuration.");
					}
					
				}

			}else{
				
				newNode.addProperty("SNMPCommunity",nodeitem.getAttribute("snossnmpcommunity"));
				newNode.addProperty("SNMPPort",nodeitem.getAttribute("snossnmpport"));
				newNode.addProperty("SNMPVersion","v2");
				newNode.addProperty("Offset", "0");
				if (protocolPool.containsProtocol(nodeitem.getAttribute("snostype")+"_Plugin")) {
					newNode.addProtocol(generateID(nodeitem.getAttribute("snostype")+"_Plugin"));
				}
			}

			newNode.generateID();
			if (!NodePool.getInstance().hasNode(newNode.getID())) {
				NodePool.getInstance().addNode(newNode);
			} else {
				System.err.println("Node " + newNode.getName()
						+ " with IP Address "
						+ newNode.getProperty("IPAddress")
						+ " already exists in SIM configuration");
				System.err.println(newNode.getName()
						+ " has not been converted.");
			}

		}

	}

	public int generateID(String name) {
		int hash = name.hashCode();
		if (hash < 0) {
			hash = hash * -1;
		}
		return hash;
	}

}
