package configCreation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.sim.model.pools.ProtocolPool;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class PopulateSFTP {

	public void populate(Element element){
		SFTPproperties sftp = new SFTPproperties();
		sftp.setName(element.getAttribute("name"));
		System.out.println("SFTP Populate the name : "+sftp.getName());
				
		NodeList nodes = element.getChildNodes();
		for (int temp = 0; temp < nodes.getLength(); temp++) {
			Node nNode = nodes.item(temp);
			if(nNode.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) nNode;
				if(eElement.getTagName().equals("RopIntervalName")){
					String[] intervalName = eElement.getTextContent().split(",");
					for(String name : intervalName){
						int hash = name.hashCode();
						if(hash<0){
							hash = hash * -1;
						}
						sftp.addInterval(hash);
					}
				}else{
					sftp.addProperty(eElement.getTagName(), eElement.getTextContent());
				}
			}

		}
		
		
		System.out.println("Adding the protocol : "+sftp.getName()+" to the protocol pool");
		ProtocolPool.getInstance().addProtocol(sftp);

	}
	
}
