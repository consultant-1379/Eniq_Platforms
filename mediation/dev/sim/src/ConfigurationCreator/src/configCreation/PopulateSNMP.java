package configCreation;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.protocol.snmp.SNMPproperties;

public class PopulateSNMP {
	
	public void populate(Element element) throws IOException{
		SNMPproperties snmp = new SNMPproperties();
		snmp.setName(element.getAttribute("name"));
				
		//Get SNMP properties
		NodeList nodes = element.getChildNodes();
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if(nNode.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) nNode;
				
				if(eElement.getTagName().equals("RopIntervalName")){
					String[] intervalName = eElement.getTextContent().split(",");
					for(String name : intervalName){
						int hash = name.hashCode();
						if(hash < 0){
							hash = hash * -1;
						}
						snmp.addInterval(hash);
					}
					
				}else if(!eElement.getTagName().equals("CounterSets")){
					snmp.addProperty(eElement.getTagName(), eElement.getTextContent());
				}
			}
		}
		
		//Get CounterSets
		
		PopulateCounterSet pcs = new PopulateCounterSet();
		nodes = element.getElementsByTagName("CounterSet");
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			Element eElement = (Element) nNode;
			
			snmp.addCounterSet(pcs.populate(eElement));
		}
		
		ProtocolPool.getInstance().addProtocol(snmp);

	}
	
}
