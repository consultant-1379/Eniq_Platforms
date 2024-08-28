package com.ericsson.sim.model.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;


public class util_prop_parser {
	
	protected Document document;
	
	public void loadFile(String xmlfile) throws Exception{
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
			
		document = db.parse(new FileInputStream(new File(xmlfile)));
	}
	
	
	public void loadFile(InputStream xmlFile) throws Exception{
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
	}
	
	public int getContent( String childTag){                
		NodeList childList = document.getElementsByTagName(childTag); 
		
		return childList.getLength();
	}
	
	public String getAttribute(String childTag,int childIndex, String attributeTag, String attribute){		
		NodeList childList = document.getElementsByTagName(childTag);
		Element element = (Element) childList.item(childIndex);
		element.getAttribute(attributeTag);
		if(element.getAttribute(attributeTag).contains(attribute)){
			NodeList childlist = document.getElementsByTagName(childTag);
			Element field = (Element) childlist.item(childIndex);
			Node child = field.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		}
		return "";
	}
	
	
	public String getValue(String attribute){
		NodeList childList = document.getElementsByTagName("entry");
		for (int x=0; x<childList.getLength(); x++){
			Element element = (Element) childList.item(x);
			if(element.getAttribute("key").contains(attribute)){
				Node child = element.getFirstChild();
				if (child instanceof CharacterData) {
					CharacterData cd = (CharacterData) child;
					return cd.getData();
				}
				
			}
		}

		return null;
	}
	
	
}
