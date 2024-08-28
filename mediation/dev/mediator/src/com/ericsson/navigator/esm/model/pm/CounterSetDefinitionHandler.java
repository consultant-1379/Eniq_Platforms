package com.ericsson.navigator.esm.model.pm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.navigator.esm.model.pm.Counter.CounterType;
//import com.ericsson.navigator.esm.pm.db.PmProperties;


class CounterSetDefinitionHandler extends DefaultHandler {
	

	@Override
	public void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {
		
		
		
		
		if(qName.equals("moid")) {
			insideMoidElement = true;
		}
		
		//if(qName.equals("transformation")) {
			//transformationNames.add(attributes.getValue("class"));
		//}
		
		if(insideMoidElement) {
			if(qName.equals("northBound")) {
				northBoundDirectory = attributes.getValue("path");
			}
			if(qName.equals("string")) {
				moidStrings.add(attributes.getValue("value"));
			}
			if(qName.equals("managedsystem")) {
				moidStrings.add(CounterSetDefinition.MANAGED_SYSTEM);
			}
		}
		
		if(qName.equals("counter")) {
			addCounterDefinition(attributes);
		}
	}

	private void addCounterDefinition(final Attributes attributes) {
		if((null == attributes.getValue("id") || attributes.getValue("id").trim().isEmpty()) 	
			|| (null == attributes.getValue("name") || attributes.getValue("name").isEmpty())
			|| (null == attributes.getValue("type") || attributes.getValue("type").isEmpty())){
			logger.error("CounterSetDefinitionHandler.addCounterDefinition(); Invalid Counter data; Counter missing id name or type in file with moidStrings: " +
					moidStrings.toString());
		}else{
			final String id = attributes.getValue("id");
			final String name = attributes.getValue("name");
			final String typeStr = attributes.getValue("type");
			final CounterType cType = getCounterType(typeStr);

			definitions.put(id, new CounterDefinition(id, name, cType));
			
		}
	}

	private CounterType getCounterType(final String typeStr) {
		CounterType cType;
		if(typeStr.equalsIgnoreCase("gauge")) {
			cType = CounterType.GAUGE;
		} else if (typeStr.equalsIgnoreCase("peg")) {
			cType = CounterType.PEG;
		} else if (typeStr.equalsIgnoreCase("min")) {
			cType = CounterType.MIN;
		} else if (typeStr.equalsIgnoreCase("max")) {
			cType = CounterType.MAX;
		} else if (typeStr.equalsIgnoreCase("kpi")) {
			cType = CounterType.KPI;
		} else if (typeStr.equalsIgnoreCase("index")) {
			cType = CounterType.INDEX;
		}
		else {
			logger.warn("unknown type: " + typeStr + ". defaulting to peg.");
			cType = CounterType.PEG;
		}
		return cType;
	}
	
	
	@Override
	public void endElement(final String uri, final String localName, final String qName)
			throws SAXException {
		
		if(qName.equals("moid")) {
			insideMoidElement = false;
		}
	}
	
	CounterSetDefinitionHandler() {
		parserFactory = SAXParserFactory.newInstance();
		parser = null;
		moidStrings = new ArrayList<String>();
		definitions = new HashMap<String, CounterDefinition>();
		try {
			parser = parserFactory.newSAXParser();
		} catch (ParserConfigurationException pcex) {
			logger.error("failed to setup SAXparser: " + pcex.toString());
		} catch (SAXException sex) {
			logger.error("failed to setup SAXparser: " + sex.toString());
		}
	}
	
	CounterSetDefinition getCounterSetDefinitionFromFile(final File definitionFile) 
		throws IOException, SAXException {
		
		//re-initialize
		moidStrings.clear();
		definitions = new HashMap<String, CounterDefinition>();
		//transformationNames=new ArrayList<String>();
		
		if(parser != null) {
			parser.parse(definitionFile, this);
		}
		if(!isCounterSetComplete()) {
			logger.warn("Extracted counterset from file " + definitionFile.getName() +
					"isn't complete.");
			return null;
		}
		//if (transformationNames.size()==0){
        	//transformationNames=PmProperties.DEFAULTTRANSFORMATIONS;
        //}
        return new CounterSetDefinition(definitionFile.getName(), northBoundDirectory, moidStrings, definitions);
	}
	
	/*PRIVATE*/
	private static final String classname = CounterSetDefinitionHandler.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	private final SAXParserFactory parserFactory;
	private SAXParser parser;//NOPMD
	private boolean insideMoidElement = false;
	private String northBoundDirectory = "";
	//these are re-initialized each time getDefinition() is called
	private final List<String> moidStrings;
	private Map<String, CounterDefinition> definitions;
	//private List<String> transformationNames;
	
	private boolean isCounterSetComplete() {
		if(definitions.size() < 1) {
			return false;
		}
		
		return true;
	}	
}
