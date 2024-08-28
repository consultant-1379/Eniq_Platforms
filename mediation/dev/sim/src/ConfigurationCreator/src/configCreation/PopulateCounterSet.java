package configCreation;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.sim.model.protocol.snmp.Counter;
import com.ericsson.sim.model.protocol.snmp.CounterSet;

public class PopulateCounterSet {
	
	public CounterSet populate(Element element) throws IOException{
		CounterSet cs = new CounterSet();
		cs.setName(element.getAttribute("name"));
		
		//GET CounterSet properties
		NodeList nodes = element.getChildNodes();
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if(nNode.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) nNode;
				if(!eElement.getTagName().equals("Counters")){
					cs.addProperty(eElement.getTagName(), eElement.getTextContent());
				}
			}
		}
		
		//Add Counters
		nodes = element.getElementsByTagName("Counters");
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			Element eElement = (Element) nNode;
			
			NodeList counters = eElement.getChildNodes();
			for (int x = 0; x < counters.getLength(); x++) {
				Node c = counters.item(x);
				if(c.getNodeType() == Node.ELEMENT_NODE){
					Element e = (Element) c;
					
					Counter counter = new Counter();
					counter.addProperty("OID", e.getTextContent());
					counter.addProperty("CounterName" , e.getTagName());
					cs.addCounter(counter);
					
				}
			}

		}
		
		return cs;
	}

}
