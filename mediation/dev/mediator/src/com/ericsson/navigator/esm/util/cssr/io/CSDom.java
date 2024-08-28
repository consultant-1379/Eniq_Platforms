package com.ericsson.navigator.esm.util.cssr.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ericsson.navigator.esm.util.cssr.CSSR;

public class CSDom {

	public static final Logger logger = Logger.getLogger("CSSR");
	
 	public boolean removeAllNodesFromCSSRDocument(final Document doc, final String nodeType) {
 	   NodeList sections = doc.getElementsByTagName("EriNMSSubNW");
 	   final Node root = sections.item(0);
        sections = root.getChildNodes();
        
         //TODO log the number of nodes removed, give their names?
        cleanChildren(root, nodeType);
        return cleanTree(root);
        //return false;
 	}
	
	/*
	 * WARNING: this will delete the root node as well if all its children are
	 * empty.
	 */
 	private boolean cleanTree(final Node root) {
 		
 		if(hasIp(root)){
 			return false;
 		}
 		final List<Node> toDo = new ArrayList<Node>();
 		Node child = null;
 		child = root.getFirstChild();
 		
 		while(child != null) {
 			toDo.add(child);
 			child = child.getNextSibling();			
 		}
 		
 		//  the order of recursing is important, easy to miss branches
 		for(int i=0;i<toDo.size();i++) {
 			cleanTree(toDo.get(i));
 		}
 		
 		//TODO remove all empty nodes that do not have an IP, leave the SR root
 		Node parent = root;
 	    Node grandParent = parent.getParentNode();
 	   while(grandParent != null && !parent.hasChildNodes() && !"EriNMSSubNW".equals(parent.getNodeName())) {
 	    	grandParent.removeChild(parent);
 	    	parent = grandParent;
 	    	grandParent = parent.getParentNode();
 	    }
 	    
 		return (root.getChildNodes().getLength() == 0);
 	}
	
 	private boolean hasIp(final Node node) {
 		final NamedNodeMap atts = node.getAttributes();
 		final Node ip = atts.getNamedItem("snoshostip");
 	    
 		if(atts == null || ip == null) {
 			return false;
 		}
 		return !"".equals(ip.getNodeValue());
 	}
 	
 	private int cleanChildren(final Node node, final String nodeType) {
 		int nodesDeleted = 0;
 		Node child = null;
 		NamedNodeMap atts = null;
 		final NodeList children = node.getChildNodes();
 		final List<Node> toBeRemoved = new ArrayList<Node>();
 		for(int i=0;i<children.getLength();i++) {
 			child = children.item(i);
 			
 			//skipping remote file fetch stuff...
 			if("RemoteFileFetch".equals(child.getNodeName())){
 				continue;
 			}
 	 			
 			atts = child.getAttributes();
 			if(!"".equals(atts.getNamedItem("snoshostip").getNodeValue()) && nodeType.equalsIgnoreCase(atts.getNamedItem("snostype").getNodeValue())) {
 				toBeRemoved.add(child);
 				nodesDeleted++;
 			} else {
 				nodesDeleted += cleanChildren(child, nodeType);
 			}
 		}		
 		for(int i=0;i<toBeRemoved.size();i++) {
 			node.removeChild(toBeRemoved.get(i));
 		}
 		return nodesDeleted;
 	}
	
	public int removeNodesFromCSSRDocument(final Document doc, final List<CSSystem> systems) throws UserInputException {
		int nodesRemoved = 0;
		final List<CSElement> nodesToDo = new ArrayList<CSElement>();
		final List<Node> toRemove = new ArrayList<Node>();
		for(int i=0;i<systems.size();i++) {
			nodesRemoved += removeNodesFromDocumentHelper(doc, systems.get(i),nodesToDo, toRemove);
		}
		
		testIfAllNodesRemoved(nodesToDo, toRemove);
		return nodesRemoved;	    
	}

	private int removeNodesFromDocumentHelper(final Document doc,
			final CSSystem system,final List<CSElement> nodesToDo,final List<Node> toRemove) throws UserInputException {
		Node parent = retrieveNodeByHierarchy(doc, system, true);
		Node node = null;
		NodeList children = null;
		
		//final List<Node> toRemove = getNodesToRemove(parent, system);
		final List<CSElement> nodesToDoLocal = system.getElements();
	   	nodesToDo.addAll(nodesToDoLocal);
	   	
	    children = parent.getChildNodes();
	    final List<Node> toRemoveLocal = new ArrayList<Node>();	    
	    for(int i=0;i<=children.getLength();i++) {
	    	node = children.item(i);
            
	    	if(node != null && node.getNodeType() == Node.ELEMENT_NODE) {
	    		if(isNodeInList(node, nodesToDoLocal)) {		    			
	    			toRemoveLocal.add(node);
	    			toRemove.add(node);
	    		}
	    	}		    	
	    }
	    
	    //remove the required children
	    for(int i=0;i<toRemoveLocal.size();i++) {
	    	parent.removeChild(toRemoveLocal.get(i));
	    }
	    
	    //remove all empty nodes
	    Node grandParent = parent.getParentNode();
	    while(!parent.hasChildNodes()) {
	    	grandParent.removeChild(parent);
	    	parent = grandParent;
	    	grandParent = parent.getParentNode();
	    }	    
	    //testIfAllNodesRemoved(nodesToDo, toRemove);
	    return toRemoveLocal.size();
	}

	private void testIfAllNodesRemoved(final List<CSElement> nodesToDo,
			final List<Node> toRemove) {
		Node node;
		if(toRemove.size() != nodesToDo.size()) {
			String message  ="\nWarning: failed to remove all required nodes, check removal template file!\nnodes to be removed:\n ";
	    	for(int i=0;i<nodesToDo.size();i++) {
	    		message += nodesToDo.get(i).getName() + '\n';
	    	}
	    	message += "\nnodes actually removed:\n";
	    	for(int i=0;i<toRemove.size();i++) {
	    		node = toRemove.get(i);
	    		final NamedNodeMap atts = node.getAttributes();
	    		message += atts.getNamedItem("cimname").getNodeValue() + '\n';
	    		//System.out.println(atts.getNamedItem("cimname").getNodeValue());
	    	}
	    	
			logger.warn(message);
			System.out.println(message);
		}
	}
	
    private  boolean isNodeInList(final Node node, final List<CSElement> list) {
		final NamedNodeMap atts = node.getAttributes();
		final String name = atts.getNamedItem("cimname").getNodeValue();
		final String type = atts.getNamedItem("snostype").getNodeValue();
		final String ip = atts.getNamedItem("snoshostip").getNodeValue();
		
		for(int i=0;i<list.size();i++) {
			final CSElement el = list.get(i);
			if(el.getType().equalsIgnoreCase("CCN")){
				if( name.equalsIgnoreCase(el.getName()) && ip.equalsIgnoreCase(el.getIp())   ){
					return true;
				}	
			}else{
			if( name.equalsIgnoreCase(el.getName()) && ip.equalsIgnoreCase(el.getIp()) && type.equalsIgnoreCase(el.getType())  ){
				return true;
			}
			}
		}
		return false;
	}
    
    
    public void addNodesToCSSRDocument(final Document doc, final List<CSSystem> systems) throws UserInputException{
    	for(int i=0;i<systems.size();i++) {
    		addNodesToCSSRDocumentHelper(doc, systems.get(i));
    	}
    }
    
	private void addNodesToCSSRDocumentHelper(final Document doc, final CSSystem system) throws UserInputException{
		    
		final Node parent = retrieveNodeByHierarchy(doc, system, false);
		addSystemsHelper(system, parent, doc);
	}
	
	private Node retrieveNodeByHierarchy(final Document doc, final CSSystem system, final boolean raiseExceptionIfLevelNotFound) throws UserInputException {
		 final NodeList sections = doc.getElementsByTagName("EriNMSSubNW");
		 final Node root = sections.item(0);
		    
		 Node parent = root;
		 Node node = null;
		 NodeList children = null;
		 NamedNodeMap atts = null;
		   
		//now step through the document to find the path defined by system's hierarchy
	    //String hier = system.getNextHierarchy();
		String hier = system.peekNextHierarchy();
	    String[] bits = null;
	    String name = null;
	    String type = null;
	    boolean foundLevel = false;
	    while(hier != null) {
	    	bits = hier.split("=");
	    	
	    	if(bits.length != 2) {
				final String message = "CSDom:retrieveNodeByHierarchy() invalid hierarchy element: " + hier;
				throw new UserInputException(message);
			}
	    	
	    	name = bits[1];
	    	type = bits[0];
	    	foundLevel = false;
	    	children = parent.getChildNodes();
	    	for(int i=0;i<children.getLength();i++) {
	    		node = children.item(i);
	    		atts = node.getAttributes();
	    		//check to see if its the correct level
	    		if(name.equals(atts.getNamedItem("cimname").getNodeValue()) && type.equals(atts.getNamedItem("snostype").getNodeValue())) {
	    			parent = node;
	    			foundLevel = true;
	    			break;
	    		}
	    	}
	    	
	    	//test to see if failed to match hierarchy level to the node path
	    	if(!foundLevel) {
	    		break;
	    	}
	    	system.getNextHierarchy();
	    	hier = system.peekNextHierarchy(); 
	    }
	    
	    if(raiseExceptionIfLevelNotFound && !foundLevel) {
	    	final String message = "CSDom:retrieveNodeByHierarchy() template hierarchy not matched in xml file @ \" + hier";
			throw new UserInputException(message);
	    }	    
	    return parent;
	}
	
		
	public Document createCSSRDocument(final List<CSSystem> systems) throws UserInputException, ParserConfigurationException {
		
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
		final DocumentBuilder db = dbf.newDocumentBuilder ();
		final Document doc = db.newDocument ();
	
		//build the 'header' of the sr document
		final Element root = createChargingNodeRoot(doc);		
		addSystems(systems, root, doc);		
		return doc;
	}
	
	
	private void addSystems(final List<CSSystem> systems, final Element root, final Document doc) throws UserInputException{
		for(int i=0;i<systems.size();i++) {
			addSystemsHelper(systems.get(i), root, doc);
		}
	}


	private void addSystemsHelper(final CSSystem sys, final Node root, final Document doc) throws UserInputException{
		final NodeList children = root.getChildNodes();
		final String level = sys.peekNextHierarchy();
		
		if(level == null || children.getLength() == 0) {
			attachCSSystem(root, sys, doc);
			return;
		}
		
		final String[] bits = level.split("=");
		
		if(bits.length != 2) {
			final String message = "CSDom:addSystemsHelper() invalid hierarchy element: " + level;
			throw new UserInputException(message);
		}
		
		final String cimname = bits[1];
		final String snostype = bits[0];
		boolean foundParent = false;
		Node name = null;
		Node type = null;
		Node child = null;
		NamedNodeMap attributes = null;;
		for(int i=0;i<children.getLength();i++) {
			child = children.item(i);
			attributes = child.getAttributes();
			name = attributes.getNamedItem("cimname"); 
			type = attributes.getNamedItem("snostype");
			
			if(name.getNodeValue().equalsIgnoreCase(cimname) && type.getNodeValue().equalsIgnoreCase(snostype)) {
				foundParent = true;
				sys.getNextHierarchy();// done with the current level so dump it
				addSystemsHelper(sys, child, doc);
			}
		}
		
		if(!foundParent) {
			attachCSSystem(root, sys, doc);
		}	
	}
	
	private Element createChargingNodeRoot(final Document doc){
		
		//Suppressing the stand alone attribute, don't know how to get rid of the encoding attriubte
		doc.setXmlStandalone(true);
				
		final Element root = doc.createElement ("EricssonNMSCIM");
				
		root.setAttribute ("CIMVERSION", "2.4");
		root.setAttribute ("DTDVERSION", "1.0");		
		doc.appendChild (root);
		
		final Element op = doc.createElement("OPERATION");
		op.setAttribute("PROTOCOLVERSION", "1.0");
		op.setAttribute("TYPE", "purge");
		root.appendChild(op);
		
		final Element nms = doc.createElement("EricssonNMS");
		op.appendChild(nms);
		
		// subnw is the root for our charging nodes
		final Element subnw = doc.createElement("EriNMSSubNW");
		subnw.setAttribute("cimname", "Service Network");
		nms.appendChild(subnw);
		
		return subnw;
	}
	
	public void writeSystemRegFile(final String fileName, final Document doc) throws IOException, TransformerException {
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "file:///nav/opt/esm/etc/model.dtd");
        t.setOutputProperty(OutputKeys.INDENT,"yes");
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        final StreamResult result = new StreamResult(fileName);
        final DOMSource source = new DOMSource(doc);
        t.transform(source, result);
	}
	
    private void attachCSSystem(final Node parent, final CSSystem system, final Document doc) throws UserInputException{
    	//write whats left of the hierarchy
    	String hierarchy = system.getNextHierarchy();
    	Node node = null;
    	Node root = parent;
    	
    	while(hierarchy != null) {
    		final String[] bits = hierarchy.split("=");    		
    		if(bits.length != 2) {
    			final String message = "CSDom:attachCSSystem() invalid hierarchy element: " + hierarchy;
    			throw new UserInputException(message);
    		}
    		final String cimname = bits[1];
    		final String snostype = bits[0];

        	//mark the highest level as charging system
    		node = makeCSHierarchyElement(cimname, snostype, doc, (system.getHierarchyLevel() == 1));
    		root.appendChild(node);
    		
    		root = node;
    		hierarchy = system.getNextHierarchy();
    	}
    	// do the cimotheridentifyinginfo and build the actual elements
    	final List<CSElement> elements = system.getElements();
    	CSTopologyUtilities.createDefaultCOIInfo(elements);
    	
    	for(int i=0;i<elements.size();i++) {
   			root.appendChild(createCSNode(elements.get(i), doc));	
    	}
    }
    
    public boolean isDocumentEmpty(final Document doc) {
    	final NodeList sections = doc.getElementsByTagName("EriNMSSubNW");
		if(sections == null || sections.getLength() == 0) {
			return true;
		}
		
		// next look for children of the subnetwork element
		final Node subNet = sections.item(0);
		final NodeList children = subNet.getChildNodes();
		Node child = null;
		for(int i=0;i<children.getLength();i++) {
			child = children.item(i);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				return false;
			}
		}
		
		//return !subNet.hasChildNodes();
		return true;
    }
       
    private Node createCSNode(final CSElement cs, final Document doc) {
		final Element el = doc.createElement("SNOSNE");
		el.setAttribute("cimname", cs.getName());
		el.setAttribute("snostype", cs.getType());
			
		el.setAttribute("snoshostip", cs.getIp());
		el.setAttribute("snosprotocoltype", cs.getProtocolType());
		el.setAttribute("cimotheridentifyinginfo", cs.getOtherIndentifyingInfo());
		el.setAttribute("snossnmpcommunity", cs.getCommunity());		
		el.setAttribute("snossnmpport", cs.getSnmsPort());
		el.setAttribute("snoshostname", cs.getHostname());
		el.setAttribute("cimidentifyingdescription", cs.getCimIdentifyingDescription());
		el.setAttribute("snosversion", "");
		el.setAttribute("cimcaption", "");		
		el.setAttribute("cimdescription", "");
		el.setAttribute("syscontact", "");
		el.setAttribute("syslocation", "");
		
		
		if (CSSR.pm_enable.equalsIgnoreCase("true") && (!cs.getNodePMDisable())) {
			final List<CSRemoteElement> arl = cs.getCsr();
			for(int i=0;i<arl.size();i++){
				final CSRemoteElement csr = arl.get(i);
				final String plugIn = "CS"+cs.getType()+"_" + csr.getpluginDir();
				csr.setpluginDir(plugIn);				
			final Node node = addCSRemoteTags(doc, csr);
			csr.createPropertiesFile();
			el.appendChild(node);
			}
		}
		//FM components added here
		attachCSComponentsNode(el, cs.getFmComponents(),cs.getType(),doc); 

		
		//TODO: PM components added here
		if (CSSR.pm_enable.equalsIgnoreCase("true") && cs.getPmComponents()!=null) {
			attachCSComponentsNode(el, cs.getPmComponents(),cs.getType(),doc);
		}
    	return el;
    }
    
    private void attachCSComponentsNode(final Element cs, final CSComponents comps, final String type, final Document doc) {
    	
    	Node root = null;   // CSElement needs this to append to itself
    	Node parent = null; // components added here
    	NameTypePair pair = null;
    	Node temp = null;
    	Element el = null;
    	
    	//to be safe reset the indexing to the Collections
		if (comps != null) {
			comps.resetComponent();
			comps.resetHierarchy();

			pair = comps.nextHierachy();
			while (pair != null) {
				temp = makeCSHierarchyElement(pair.getName(), pair.getType(),
						doc, false);

				if (root == null) {
					root = temp;
					parent = temp;
				} else {
					parent.appendChild(temp);
					parent = temp;
				}

				pair = comps.nextHierachy();
			}

			// if no hierarchy...
			if (parent == null) {
				parent = cs;
			} else {
				cs.appendChild(root);
			}

			// add the components to the leaf of the hierarchy tree node
			pair = comps.nextComponent();
			while (pair != null) {
				el = doc.createElement("SNOSNE");
				el.setAttribute("cimname", pair.getName());
				el.setAttribute("snostype", pair.getType());
				
				/*if ("CCN".equalsIgnoreCase(type)) {
					el.setAttribute("snoshostip", "");
				} else {
					el.setAttribute("snoshostip", ip);
				}*/
				// el.setAttribute("snoshostip", ip);
				// el.setAttribute("snosprotocoltype", "TXF");
				
				el.setAttribute("snoshostip", "");
				
				el.setAttribute("snosprotocoltype", CSTopologyUtilities
						.getProtocolType(type));
				el.setAttribute("snossnmpcommunity", "");
				el.setAttribute("cimotheridentifyinginfo", "1.2.3.4.5.6");
				el.setAttribute("snossnmpport", "");
				el.setAttribute("snoshostname", "");
				el.setAttribute("snosversion", "");
				el.setAttribute("cimcaption", "");
				el.setAttribute("cimdescription", "");
				el.setAttribute("syscontact", "");
				el.setAttribute("syslocation", "");
				el.setAttribute("cimidentifyingdescription", "");

				parent.appendChild(el);

				pair = comps.nextComponent();
			}
		}
    }
    
    private Node makeCSHierarchyElement(final String name, final String type, final Document doc, final boolean isTopLevel) {
    	final Element el = doc.createElement("SNOSNE");
    	el.setAttribute("cimname", name);
		el.setAttribute("snostype", type);
		el.setAttribute("snoshostip", "");		
		el.setAttribute("snosprotocoltype", "TXF");// sr requires a value to be set defaulting to TXF
		el.setAttribute("cimotheridentifyinginfo", "");
		el.setAttribute("snossnmpcommunity", "");		
		el.setAttribute("snossnmpport", "");
		el.setAttribute("snoshostname", "");
		el.setAttribute("snosversion", "");
		el.setAttribute("cimcaption", "");		
		el.setAttribute("cimdescription", "");
		el.setAttribute("syscontact", "");
		el.setAttribute("syslocation", "");
		
		
		if(isTopLevel) {
			el.setAttribute("cimidentifyingdescription", "ChargingSystemNetwork");
		} else {
			el.setAttribute("cimidentifyingdescription", "");
		}
		return el;
    }
    
    private Node addCSRemoteTags( final Document doc,final CSRemoteElement cs2) {
    	final Element el = doc.createElement("RemoteFileFetch");
    	
    	   	
    	el.setAttribute("pluginDir", cs2.getpluginDir());
		el.setAttribute("ROP", cs2.getROP());
		el.setAttribute("ProtocolType",cs2.getRemoteProtocolType());		
		el.setAttribute("Offset", cs2.getOffset());
	   	
    	for (List<String> l : cs2.getRemoteCountersetRegexpPair()) {
    		final Element el1 = doc.createElement("RemoteFileCounterSet");
        	el1.setAttribute("fileName", l.get(0));
		   	el1.setAttribute("regExp", l.get(1));
			el.appendChild(el1);
		}
    	
   		return el;
    }
}
