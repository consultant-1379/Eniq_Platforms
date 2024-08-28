package com.ericsson.sim.config.configNodes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.parser.XmlParser;

public class TopologyToCSV {

	private String outputfilename = "";
	private String confDir = SIMEnvironment.CONFIGPATH;
	private BufferedWriter bw;
	private String[] tagAtts = { "name", "IPAddress", "uniqueID" };
	private String[] props = { "Offset", "sftpPort", "sftpUserName", "snmpCommunity","snmpPort","snmpVersion", "Protocols"};
	private int numberOfNodes;

	public TopologyToCSV(String filepath) {
		this.outputfilename = filepath;
		export();
		System.out.println("Parsing nodes to new format complete");
		System.out.println("Imported: " + this.getNumberOfNodes() + " Nodes");
		numberOfNodes = 0;

	}

	public void export() {
		try {
			XmlParser xml = new XmlParser();
			xml.loadFile(confDir + "/topology.xml");

			createOutputFile();
			writeHeader();

			for (int i = 0; i < xml.getTagListLength("Node"); i++) {
				numberOfNodes++;
				String line = "";
				for (String column : tagAtts) {
					line = line.concat(xml.getAttribute("Node", i, column)+ ",");
				}
				for (String column : props) {
					if (!column.equalsIgnoreCase("Protocols")) {
						try {
							line = line.concat(xml.getNodeChildValue("Node", i, column)+ ",");
						} catch (Exception e) {
							line = line.concat(",");
						}
					} else {
						try {
							String[] p = xml.getNodeValue(column, i).split(",");
							for (String s : p) {
								line = line.concat(s) + "|";
							}
						} catch (Exception e) {
							line = line.concat("|");
						}
					}

				}
				line = line.substring(0, line.length() - 1);
				bw.write(line + "\n");
			}

			bw.flush();
			bw.close();

		} catch (Exception e) {
			System.err.println("Unable to export the topology information to csv. "+ e);
			e.printStackTrace();
		}
	}

	public void createOutputFile() throws Exception {
		File outputFile = new File(confDir + "/" + outputfilename);

		bw = new BufferedWriter(new FileWriter(outputfilename));

	}

	public void writeHeader() throws IOException {
		String header = "name,IPAddress,uniqueID,Offset,sftpPort,sftpUserName,snmpCommunity,snmpPort,snmpVersion,pluginNames\n";// SNMPCommunity,SNMPPort,SNMPVersion,
		bw.write(header);
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	public static void main(String[] args) {
		new TopologyToCSV(args[0]);

	}

}
