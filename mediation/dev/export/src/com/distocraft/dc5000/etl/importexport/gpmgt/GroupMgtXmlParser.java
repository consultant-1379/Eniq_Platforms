package com.distocraft.dc5000.etl.importexport.gpmgt;
/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtFileNotFoundException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtParseException;
import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtImportException;
import com.distocraft.dc5000.etl.importexport.GroupMgtHelper;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;

import org.xml.sax.SAXException;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class GroupMgtXmlParser {
	/**
	 * XML Unmarshaller
	 */
	private transient Unmarshaller xmlUnmarshaller = null;
	/**
	 * Content Error handler. Stores the errors for later retrieval
	 */
	private transient VEHImpl vHandler = null;

	/**
	 * Get the XML file as Java Object
	 * @param xmlFile The XML to get
	 * @return Root Groupmgt object
	 */
	@SuppressWarnings({"PMD.DataflowAnomalyAnalysis"})
	public Groupmgt getGroupData(final String xmlFile)  {
		if(xmlFile == null){
			throw new GroupMgtFileNotFoundException("null");
		}
		final File _xmlFile = new File(xmlFile);
		if (!_xmlFile.exists()) {
			throw new GroupMgtFileNotFoundException(xmlFile);
		}
		setupSchema();
		try {
			final Groupmgt groupMgtRoot = (Groupmgt)xmlUnmarshaller.unmarshal(new File(xmlFile));
			if(vHandler.wereErrors()){
				final List<String> errors = new ArrayList<String>();
				errors.addAll(vHandler.getFatalErrors());
				errors.addAll(vHandler.getParseErrors());
				throw new GroupMgtParseException(xmlFile, errors);
			}
			return groupMgtRoot;
		} catch (JAXBException e) {
			throw new GroupMgtImportException("Failed to Unmarshaller data", e);
		}
	}

	/**
	 * Set the schema to validate against.
	 */
	private void setupSchema() {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(GroupMgtHelper.SCHEMA_PACKAGE);
			xmlUnmarshaller = jaxbContext.createUnmarshaller();
			vHandler = new VEHImpl();
			xmlUnmarshaller.setEventHandler(vHandler);
			final Schema afjSchema = getSchema();
			xmlUnmarshaller.setSchema(afjSchema);
		} catch (JAXBException e) {
			throw new GroupMgtImportException("Failed to create Unmarshaller", e);
		} catch (SAXException e) {
			throw new GroupMgtImportException("Failed to create Unmarshaller", e);
		} catch (URISyntaxException e) {
			throw new GroupMgtImportException("Failed to create Unmarshaller", e);
		}
	}

	/**
	 * Get the Schema object form the schema file (converts it)
	 * @return Schmea to validate the input XML file against.
	 * @throws SAXException Errors
	 * @throws URISyntaxException Errors.
	 */
	private Schema getSchema() throws SAXException, URISyntaxException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final File schemaFile = getSchemaFile();
		return schemaFactory.newSchema(schemaFile);
	}

	/**
	 * Get the schema file location from the classpath
	 * @return File representing the xsd file
	 * @throws URISyntaxException If hte path for the file is not valid
	 */
	private File getSchemaFile() throws URISyntaxException {
		final String sFile = GroupMgtHelper.SCHEMA_FILE;
		final URL url = ClassLoader.getSystemResource(sFile);
		if (url == null) {
			throw new GroupMgtFileNotFoundException(sFile);
		}
		return new File(url.toURI().getRawPath());
	}
}
