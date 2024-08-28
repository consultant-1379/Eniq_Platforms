package com.ericsson.sim.model.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;


import org.w3c.dom.*;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XmlParser {

	private Document document;
	
	/**
	 * Takes an xmlFile path,stores the document with DOM
	 * 
	 * @param xmlFile path of .xml file
	 */

	public void loadFile(String xmlFile) {

		//File file = new File(xmlFile);
		
		try{
			
			//if (file.exists() && !file.isDirectory()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			db.setEntityResolver(new EntityResolver() {
		        @Override
		        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		            if (systemId.contains(".dtd")) {
		                return new InputSource(new StringReader(""));
		            } else {
		                return null;
		            }
		        }
		    });
				
			document = db.parse(new FileInputStream(new File(xmlFile)));

			//}
			
		} catch (Exception e){
			System.out.println(e.getMessage());
		}	

	}
	
	
	public void loadFile(InputStream xmlFile) {

		//File file = new File(xmlFile);
		
		try{
			
			//if (file.exists() && !file.isDirectory()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			db.setEntityResolver(new EntityResolver() {
		        @Override
		        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		            if (systemId.contains(".dtd")) {
		                return new InputSource(new StringReader(""));
		            } else {
		                return null;
		            }
		        }
		    });
			
			document = db.parse(xmlFile);

			//}
			
		} catch (Exception e){
			System.out.println(e.getMessage());
		}	

	}
	
	
	
	/**
	 * 
	 * Takes a tag name and the index of the required tag
	 * It then gets the list of all tags with that name and returns
	 * the node value of the first child at the given index
	 * 
	 * @param tagName
	 * @param index
	 * @return The node value
	 */

	public String getNodeValue(String tagName, int index) {

		NodeList tagList = document.getElementsByTagName(tagName);
		Element element = (Element) tagList.item(index);
		Node child = element.getFirstChild();

		if (child instanceof CharacterData) {
			return ((CharacterData) child).getData();
		} else {
			return "";
		}

	}
	
	public String getNodeChildValue(String tagName, int index, String childname){
		NodeList tagList = document.getElementsByTagName(tagName);
		Element element = (Element) tagList.item(index);
		
		try{
			return element.getElementsByTagName(childname).item(0).getTextContent();
		}catch(Exception e){
			
		}
		return "";
	}
	
	
	/**
	 *  Takes a tag name, an index and a specified attribute
	 *  It then gets the list of all named tags, finds the tag 
	 *  at the specified index and returns the value of the 
	 *  specified attribute
	 * 
	 * 
	 * @param tagName
	 * @param index
	 * @param attributeKey
	 * @return value of the attribute key
	 */

	public String getAttribute(String tagName, int index,
			String attributeKey) {

		NodeList list = document.getElementsByTagName(tagName);
		return ((Element) list.item(index))
				.getAttribute(attributeKey);

	}
	
	/**
	 * Takes a tag name, and searches the document for the 
	 * specified tag, attempts to add to nodeList and checks
	 * whether the size is greater than 0, signaling the 
	 * existence of that tag
	 * 
	 * @param tagName
	 * @return boolean 
	 */

	public boolean hasElement(String tagName) {

		if (document.getElementsByTagName(tagName).getLength() != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the number of tags with the specified name
	 * that are in the document
	 * 
	 * @param tagName
	 * @return number of tags with parameter name
	 */

	public int getTagListLength(String tagName) {

		return document.getElementsByTagName(tagName).getLength();
	}
	
	/**
	 * Returns the list of all nodes with the named parameter
	 * 
	 * @param tagName
	 * @return list
	 */

	public NodeList nodeList(String tagName) {
		return document.getElementsByTagName(tagName);
	}
	
	/**
	 * 
	 * Will return true if the given node name exists
	 * as a child of the given parent tag
	 * 
	 * @param parentTag tagName of the element
	 * @param index index of the tag
	 * @param childNodeName name of the child node to test if exists
	 * @return
	 */
	
	public boolean hasChild(String parentTag, int index, String childNodeName){
			
		NodeList childListNode =  document.getElementsByTagName(parentTag).item(index).getChildNodes();
		
		for (int i=0; i< childListNode.getLength(); i++){
			
			if (childListNode.item(i).getNodeName().equals(childNodeName)){
				return true;
			}
		}
		
		return false;	
		
	}

	public Document getDocument() {
		return this.document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
}
