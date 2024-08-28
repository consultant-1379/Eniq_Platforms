package com.ericsson.navigator.sr;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import com.ericsson.navigator.sr.ir.IrpXmlCounterSet;
import com.ericsson.navigator.sr.ir.RemoteFileCounterSet;
import com.ericsson.navigator.sr.ir.RemoteFileFetch;
import com.ericsson.navigator.sr.ir.SnmpCounterSet;
import com.ericsson.navigator.sr.ir.EriNMSSubNW;
import com.ericsson.navigator.sr.ir.EricssonNMS;
import com.ericsson.navigator.sr.ir.EricssonNMSCIM;
import com.ericsson.navigator.sr.ir.Ir;
import com.ericsson.navigator.sr.ir.OPERATION;
import com.ericsson.navigator.sr.ir.SNOSNE;
import com.ericsson.navigator.sr.ir.SNOSNEParent;
import com.ericsson.navigator.sr.ir.SNOSOP;
import com.ericsson.navigator.sr.ir.Type;

@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class SRFContentHandler extends AbstractSRFContentHandler { //NOPMD

	public static Logger logger = Logger.getLogger("System Registration");
	private Ir result = null;
	private final Stack<Object> relations;

	/**
	 * The stack relations is used when building up the Intermediate
	 * Representation (IR) from parsing the System Registration file or the
	 * System Topology File. The stack is pushed for every new element and
	 * popped when each element has been treated.
	 */
	public SRFContentHandler() {
		relations = new Stack<Object>();
		final Ir ir = new Ir();
		relations.push(ir);
	}
	
	/**
	 * This method handles the element tags from the XML file.
	 * 
	 * @param namespaceURI
	 *            is the namespace's full URI
	 * @param localName
	 *            is the element name minus the namespace prefix
	 * @param qname
	 *            is the fully qualified name including the namespace prefix
	 * @param atts
	 *            is the list of the elements Attribute objects
	 */
	@Override
	public void startElement(final String namespaceURI, final String localName, final String qname, final Attributes atts) { //NOPMD
		setDocumentLocator(myLoc);
		if (localName.equals("EricssonNMSCIM")) {
			startEricssonNMSCIM(atts);
		} else if (localName.equals("OPERATION")) {
			startOPERATION(atts);
		} else if (localName.equals("EricssonNMS")) {
			startEricssonNMS();
		} else if (localName.equals("EriNMSSubNW")) {
			startEriNMSSubNW(atts);
		} else if (localName.equals("SNOSNE")) {
			startSNOSNE(atts);
		} else if (localName.equals("SNOSOP")) {
			startSNOSOP(atts);
		} else if (localName.equals("SnmpCounterSet")) {
			startSnmpCounterSet(atts);
		} else if (localName.equals("IrpXmlCounterSet")) {
			startIrpXmlCounterSet(atts);
		} else if (localName.equals("RemoteFileFetch")) {
			startRemoteFileFetch(atts);
		} else if (localName.equals("RemoteFileCounterSet")) {
			startRemoteFileCounterSet(atts);
		}
	}


	private void startSnmpCounterSet(final Attributes attributes) {
		final SnmpCounterSet counterSet = new SnmpCounterSet();
		final SNOSNE snosne = (SNOSNE) relations.peek();

		final List<SnmpCounterSet> counterSets = snosne.getSnmpCounterSets();
		counterSets.add(counterSet);
		relations.push(counterSet);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("fileName")) {
				counterSet.setFileName(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("ROP")) {
				counterSet.setRop(attributes.getValue(i));
			} else {
				logger.debug("Not used attribute: " + attributes.getLocalName(i));
			}
		}
	}
	
	private void startIrpXmlCounterSet(final Attributes attributes) {
		final IrpXmlCounterSet counterSet = new IrpXmlCounterSet();
		final SNOSNE snosne = (SNOSNE) relations.peek();

		final List<IrpXmlCounterSet> counterSets = snosne.getIrpXmlCounterSets();
		counterSets.add(counterSet);
		relations.push(counterSet);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("fileName")) {
				counterSet.setFileName(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("regExp")) {
				counterSet.setRegExp(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("ROP")) {
				counterSet.setROP(attributes.getValue(i));
			} else {
				logger.debug("Not used attribute: " + attributes.getLocalName(i));
			}
		}
	}
	

	private void startRemoteFileFetch(final Attributes attributes) {
		final RemoteFileFetch remoteFileFetch = new RemoteFileFetch();
		final SNOSNE snosne = (SNOSNE) relations.peek();

		final List<RemoteFileFetch> remoteFetch = snosne.getRemoteFileFetch();
		remoteFetch.add(remoteFileFetch);
		relations.push(remoteFileFetch);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("ROP")) {
				remoteFileFetch.setRop(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("pluginDir")) {
				remoteFileFetch.setPluginDir(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("ProtocolType")) {
				remoteFileFetch.setProtocolType(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("Offset")) {
				remoteFileFetch.setOffset(attributes.getValue(i));
			}
			
			else {
				logger.debug("Not used attribute: " + attributes.getLocalName(i));
			}
		}
				
	}
	
	private void startRemoteFileCounterSet(final Attributes attributes) {
		final RemoteFileCounterSet remoteFileCounterSet = new RemoteFileCounterSet();
		final RemoteFileFetch remoteFileFetch = (RemoteFileFetch) relations.peek();

		final List<RemoteFileCounterSet> counterSets = remoteFileFetch.getRemoteFileCounterSets();
		counterSets.add(remoteFileCounterSet);
		relations.push(remoteFileCounterSet);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("fileName")) {
				remoteFileCounterSet.setFileName(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("regExp")) {
				remoteFileCounterSet.setRegExp(attributes.getValue(i));
			} 
			else {
				logger.debug("Not used attribute: " + attributes.getLocalName(i));
			}
		}
		
	}

	/**
	 * This method finish to handle the element tags from the XML file.
	 * 
	 * @param namespaceURI
	 *            is the namespace's full URI
	 * @param localName
	 *            is the element name minus the name space prefix
	 * @param qname
	 *            is the fully qualified name including the name space prefix
	 * @param atts
	 *            is the list of the elements Attribute objects
	 */
	@Override
	public void endElement(final String namespaceURI, final String localName, final String qname) {
		try {
			relations.pop();
		} catch (final EmptyStackException e) {
			logger.fatal("Stack is empty: " + e.getMessage());
			logger.debug("Caused by: ", e);
		}
	}

	@Override
	public void endDocument() {
		try {
			result = (Ir) relations.pop();
		} catch (final EmptyStackException e) {
			logger.error("Parsing produced no result: ");
		}
	}

	public Ir getResult() {
		return result;
	}

	public void startEriNMSSubNW(final Attributes attributes) {
		final EriNMSSubNW eriNMSSubNW = new EriNMSSubNW();
		final EricssonNMS ericssonNMS = (EricssonNMS) relations.peek();
		ericssonNMS.setEriNMSSubNW(eriNMSSubNW);
		relations.push(eriNMSSubNW);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("cimname")) {
				eriNMSSubNW.setCimname(attributes.getValue(i));
			} else {
				logger.debug("Not used attribute: "
						+ attributes.getLocalName(i));
				/*
				 * The other attributes can be set, but in the documentation
				 * they are not mentioned.
				 */
			}

		}
	}

	public void startEricssonNMS() {
		final EricssonNMS ericssonNMS = new EricssonNMS();
		final OPERATION operation = (OPERATION) relations.peek();
		operation.setEricssonNMS(ericssonNMS); // A reference from OPERATION to
												// EricssonNMS
		relations.push(ericssonNMS);
	}

	public void startEricssonNMSCIM(final Attributes attributes) {
		final EricssonNMSCIM ericssonNMSCIM = new EricssonNMSCIM();
		final Ir ir = (Ir) relations.peek();
		ir.setEricssonNMSCIM(ericssonNMSCIM); // A reference from Ir to
												// EricssonNMSCIM
		relations.push(ericssonNMSCIM);

		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.getLocalName(i).equals("CIMVERSION")) {
				ericssonNMSCIM.setCIMVERSION(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("DTDVERSION")) {
				ericssonNMSCIM.setDTDVERSION(attributes.getValue(i));
			} else {
				// This case should be taken care of by the validation using the
				// DTD.
				logger.error("Faulty arguments");
			}
		}
	}

	public void startOPERATION(final Attributes attributes) {
		final OPERATION operation = new OPERATION();
		final EricssonNMSCIM ericssonNMSCIM = (EricssonNMSCIM) relations.peek();
		ericssonNMSCIM.setOperation(operation);
		relations.push(operation);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("PROTOCOLVERSION")) {
				operation.setPROTOCOLVERSION(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("TYPE")) {

				if (attributes.getValue(i).equals("purge")) {
					operation.setType(Type.purge);
				} else if (attributes.getValue(i).equals("modify")) {
					operation.setType(Type.modify);
				} else if (attributes.getValue(i).equals("add")) {
					operation.setType(Type.add);
				} else if (attributes.getValue(i).equals("delete")) {
					operation.setType(Type.delete);
				} else {
					// This case should be taken care of by the validation using
					// the DTD.
					logger.error("Faulty value of attribute Type");
				}
			} else {
				// This case should be taken care of by the validation using the
				// DTD.
				logger.error("Faulty arguments");
			}
		}
	}

	public void startSNOSNE(final Attributes attributes) { //NOPMD
		final SNOSNE snosne = new SNOSNE();

		final SNOSNEParent SnosNEParent = (SNOSNEParent) relations.peek();

		final List<SNOSNE> snosnes = SnosNEParent.getSnosne();
		snosnes.add(snosne);
		relations.push(snosne);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals(SNOSNE.CIMNAME)) {
				snosne.setCimname(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.CIMDESCRIPTION)) {
				snosne.setCimdescription(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.CIMCAPTION)) {
				snosne.setCimcaption(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.CIMOTHERIDENTIFYINGINFO)) {
				snosne.setCimotheridentifyinginfo(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.CIMIDENTIFYINGDESCRIPTION)) {
				snosne.setCimidentifyingdescription(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSHOSTIP)) {
				snosne.setSnoshostip(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSVERSION)) {
				snosne.setSnosversion(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSHOSTNAME)) {
				snosne.setSnoshostname(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSSNMPCOMMUNITY)) {
				snosne.setSnossnmpcommunity(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSSNMPPORT)) {
				snosne.setSnossnmpport(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSPROTOCOLTYPE)) {
				snosne.setSnosProtocolType(attributes.getValue(i)); 
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSTYPE)) {
				snosne.setSnosType(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SNOSALARMMOI)) {
				snosne.setSnosalarmmoi(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SYSCONTACT)) {
				snosne.setSyscontact(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals(SNOSNE.SYSLOCATION)) {
				snosne.setSyslocation(attributes.getValue(i));
			}  else if (attributes.getLocalName(i).equals(SNOSNE.SNOSBACKUPIP)) {
				snosne.setSnosbackupip(attributes.getValue(i));
			} else {
				logger.debug("Not used attribute: "
						+ attributes.getLocalName(i));
				/*
				 * The other attributes can be set, but in the documentation
				 * they are not mentioned.
				 */
			}
		}
	}

	public void startSNOSOP(final Attributes attributes) {
		final SNOSOP snosop = new SNOSOP();
		final SNOSNE snosne = (SNOSNE) relations.peek();

		final List<SNOSOP> snosnes = snosne.getSnosop();
		snosnes.add(snosop);
		relations.push(snosop);

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equals("cimname")) {
				snosop.setCimname(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("snosopname")) {
				snosop.setSnosopname(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("snosoptype")) {
				snosop.setSnosoptype(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("snosopargument")) {
				snosop.setSnosopargument(attributes.getValue(i));
			} else if (attributes.getLocalName(i).equals("snosopuser")) {
				snosop.setSnosopuser(attributes.getValue(i));
			} else {
				logger.debug("Not used attribute: " + attributes.getLocalName(i));
			}
		}
	}
}
