package configCreation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.sim.model.interval.RopInterval;
import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.pools.RopIntervalPool;
import com.ericsson.sim.model.properties.RuntimeProperties;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.snmp.Counter;
import com.ericsson.sim.model.protocol.snmp.CounterSet;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class ConfigCreator {
	
	private String StandardvobLocation = "/vobs/eniq/design/plat/mediation/dev/simcfg";
	private String ProCusvobLocation = "/vobs/eniq/design/plat/mediation/dev/simcfg_procus";
	private String vobLocation = StandardvobLocation;
	private static String customerFile = null;
	
	public void create(){
		try{
			File fXmlFile = new File(StandardvobLocation, "/src/config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			populateInformation(dBuilder.parse(fXmlFile));
			
			if(customerFile != null){
				fXmlFile = new File(ProCusvobLocation, "/src/config.xml");
				dbFactory = DocumentBuilderFactory.newInstance();
				dBuilder = dbFactory.newDocumentBuilder();
				populateInformation(dBuilder.parse(fXmlFile));
				
				CustomerConfig cust = new CustomerConfig();
				cust.loadFile(customerFile);
				cust.removeProtocols();
				
				vobLocation = ProCusvobLocation;
				
				ProtocolPool.getInstance().writePersistedFile(vobLocation + "/conf/procusProtocols.simc");
			}else{
				ProtocolPool.getInstance().writePersistedFile(vobLocation + "/conf/protocols.simc");
			}
			
			
			RopIntervalPool.getInstance().writePersistedFile(vobLocation + "/conf");
			RuntimeProperties.getInstance().writePersistedFile(vobLocation + "/conf");			
			
			
			SNMPDisableFile disableTemplate = null;
			HashMap<Integer, Protocol> protocols = ProtocolPool.getInstance().getProtocols();
			for(Protocol proto : protocols.values()){
				if(proto instanceof SNMPproperties){
					if(disableTemplate == null){
						disableTemplate = new SNMPDisableFile(vobLocation);
						disableTemplate.writeLine("<DisabledCollections>");
					}
					
					disableTemplate.writeLine("\t<SNMPDisable name=\"\" pluginName=\"" + proto.getName() + "\">");
					disableTemplate.writeLine("\t<Nodes>");
					disableTemplate.writeLine("\t\t<Node name=\"\" IPAddress=\"\" uniqueID=\"\"/>");
					disableTemplate.writeLine("\t\t<Node name=\"\" IPAddress=\"\" uniqueID=\"\"/>");
					disableTemplate.writeLine("\t</Nodes>");
					
					disableTemplate.writeLine("\t<Measurements>");
					
					for( CounterSet counterset : ((SNMPproperties) proto).getCounterSets()){
						disableTemplate.writeLine("\t\t<Measurement name=\"" + counterset.getName() + "\">");
						
						for( Counter counter : counterset.getCounters()){
							disableTemplate.writeLine("\t\t\t<Counter name=\"" +counter.getProperty("CounterName") + "\"/>");
						}
						
						disableTemplate.writeLine("\t\t</Measurement>");
					}
					
					disableTemplate.writeLine("\t</Measurements>");
					disableTemplate.writeLine("\t</SNMPDisable>");
				}
				
			}
			if(disableTemplate != null){
				disableTemplate.writeLine("</DisabledCollections>");
				disableTemplate.close();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void populateInformation(Document doc) throws IOException{
		NodeList nList = doc.getElementsByTagName("RopInterval");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			createRopInterval(eElement);
		}
		
		nList = doc.getElementsByTagName("SFTP");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			PopulateSFTP sftp = new PopulateSFTP();
			sftp.populate(eElement);
		}
		
		nList = doc.getElementsByTagName("SNMP");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			PopulateSNMP snmp = new PopulateSNMP();
			snmp.populate(eElement);
		}
		
		nList = doc.getElementsByTagName("Runtime");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;

			NodeList nodes = eElement.getChildNodes();
			for (int x = 0; x < nodes.getLength(); x++) {
				Node n = nodes.item(x);
				if(n.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element) n;
					RuntimeProperties.getInstance().addProperty(e.getTagName(), e.getTextContent());
				}
			}
			
		}
	}
	
	private void createRopInterval(Element element){
		RopInterval rop = new RopInterval();
		rop.setName(element.getAttribute("name"));
		
		NodeList nodes = element.getChildNodes();
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if(nNode.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) nNode;
				rop.addProperty(eElement.getTagName(), eElement.getTextContent());
			}
		}
		
		RopIntervalPool.getInstance().addInterval(rop);
	}
	
	
	
	public static void main(String[] args){
		if(args.length > 0){
			customerFile = "/vobs/eniq/design/plat/mediation/dev/simcfg_procus/build/" + args[0];
		}

		ConfigCreator cc = new ConfigCreator();
		cc.create();
	}
}
