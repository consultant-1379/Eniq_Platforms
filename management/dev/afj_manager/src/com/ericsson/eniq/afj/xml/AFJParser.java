/**
 * 
 */
package com.ericsson.eniq.afj.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.common.PropertyConstants;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author esunbal
 *
 */
public class AFJParser {

  private final Logger log = Logger.getLogger(this.getClass().getName());
	
	private AFJTechPack afjTechPack;
	
	public AFJParser(final AFJTechPack tp){
		this.afjTechPack = tp;		
	}
	
	/**
	 * @return the afjTechPack
	 */
	public AFJTechPack getAfjTechPack() {
		return afjTechPack;
	}

	/**
	 * @param afjTechPack the afjTechPack to set
	 */
	public void setAfjTechPack(final AFJTechPack afjTechPack) {
		this.afjTechPack = afjTechPack;
	}
	
	
	/*
	 * Get the file name and tp name from the techpack object
	 * Create the absolute file name for parsing.
	 * Get the schema package from properties file
	 * Get the schema
	 * Validate whether the xml fits the schema.
	 * Parse the xml file
	 */
	public Object parse(final AFJTechPack techPack)throws AFJException{
		Object pmObject = null;		
		final String tpName = techPack.getTechPackName();
		final String fileName = techPack.getFileName();		
		final String absoluteFileName = PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_BASE_DIR) + File.separator 
		+ tpName + File.separator + fileName;
		
		log.info("TpName = "+tpName+"  AbsoluteFileName="+absoluteFileName);		
		
		JAXBContext jc = null;
		Unmarshaller um = null;
//		final String contextPath = PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE);
		final String contextPath = techPack.getContextPath();
		final File file = new File(absoluteFileName);
		AFJParserValidationEventHandler handler = null;
		InputStream inStream =  null;
		try {
			jc = JAXBContext.newInstance(contextPath);			
			
			um = jc.createUnmarshaller();			
			handler = new AFJParserValidationEventHandler();    
			um.setEventHandler(handler);
			final Schema schema = getSchema(techPack.getSchemaName());
			um.setSchema(schema);		  
			
			// Setup code to ignore the namespace in the xml.
			final SAXParserFactory parserFactory = SAXParserFactory.newInstance(); 
            parserFactory.setNamespaceAware(techPack.isNamespaceAware());            
            final  XMLReader reader = parserFactory.newSAXParser().getXMLReader(); 
            inStream = new FileInputStream(file);
            final  Source source = new SAXSource(reader, new InputSource(inStream)); 	
            // Check validity of xml before unmarshalling
            checkXMLValidity(file, schema);
			pmObject = um.unmarshal(source);
			
		}		
		catch (JAXBException je) {
			processException(handler);
		} catch (SAXException se) {
			throw new AFJException("SAX exception during parsing:"+se.getMessage());
		} catch (ParserConfigurationException pce) {
			throw new AFJException("ParserConfigurationException during parsing:"+pce.getMessage());
		} catch (FileNotFoundException fnfe) {
			throw new AFJException("File Not Found for Parsing:"+fnfe.getMessage());
		}	
		finally{
			try {
				inStream.close();
			} catch (IOException e) {
				throw new AFJException("Exception in closing the inputstream of the PM MIM xml:"+e.getMessage());
			}
		}
		return pmObject; 
	}

	/**
	 * Processes the exceptions
	 * @param handler
	 * @throws AFJException
	 */
	private void processException(final AFJParserValidationEventHandler handler)throws AFJException{
		final StringBuffer errorMessage = new StringBuffer();
	    if (!handler.getMessages().isEmpty()) {
	    	errorMessage.append("The XML is not valid.");
	    	for (String message : handler.getMessages()) {
	    		errorMessage.append("\n" + message);      
	    		}
	    	throw new AFJException(errorMessage.toString());
	    	}	    
	}
	
	
	/**
	 * Returns the schema.
	 * @return
	 * @throws AFJException
	 */
	private Schema getSchema(final String schemaName) throws AFJException{

		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		File schemaFile = null;
		try {
			
			final String schemaFileLocation = System.getProperty(PropertyConstants.CONF_DIR) + File.separator + 
			PropertyConstants.PROP_AFJ_SCHEMA_DIR + File.separator + schemaName;
			log.info("schemaFileLocation:"+schemaFileLocation);
			schemaFile = new File(schemaFileLocation);
			log.info("schema absolute path:"+schemaFile.getAbsolutePath());
			schema = schemaFactory.newSchema(schemaFile);
		} catch (SAXException e) {
			throw new AFJException("SAXException: Unable to get the schema for file:"+schemaFile.getAbsolutePath());
		}
		return schema;
	}
	
	/**
	 * @param file
	 * @param schema
	 * @throws AFJException
	 */
	private void checkXMLValidity(final File file, final Schema schema) throws AFJException{				
		final Validator validator = schema.newValidator();		
		final Source xmlFile = new StreamSource(file); 
		try {
			validator.validate(xmlFile);			
		} catch (SAXException e) {
			throw new AFJException("XML file "+file + " is not compliant with the schema. "+e.getMessage());
		} catch (IOException e) {
			throw new AFJException("Unable to process the xmlFile:"+ file +" for validation.");
		}		
	}
}
