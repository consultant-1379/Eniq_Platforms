package com.ericsson.navigator.esm.model.alarm.snmp.generic.mst;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.condition.Condition;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.condition.ICondition;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression.ExpressionBuilder;
import com.ericsson.navigator.esm.model.alarm.snmp.generic.mst.composer.expression.IExpression;
import com.ericsson.navigator.esm.util.jmx.BeanServerHelper;

/**
 * This will load a TrapTranslator from a collection of XML files
 * defined in directories.
 * 
 * It loads the  TrapTranslations from a directory. The translations
 * can be in different files.
 * @author qbacfre
 *
 */
public class TrapTranslationLoader extends DefaultHandler implements TrapTranslationLoaderMBean { //NOPMD

	// Definition for DTD validation feature
	// See http://xerces.apache.org/xerces2-j/features.html for more information
	private static final String VALIDATIONFEATURE = "http://xml.org/sax/features/validation";
	
	// Definition of translations element and attribute	
	private static final String TRANSLATIONS = "translations";	
	private static final String SYSTEMTYPE = "systemtype";

	// Definition of map element / child elements and attributes
	private static final String MAP = "map";
	private static final String MAPID = "id";
	private static final String MAPENTRY = "entry";
	private static final String ENTRYKEY = "key";
	private static final String ENTRYVALUE = "value";
	private static final String MAPDEFAULT = "default";
	private static final String DEFAULTVALUE = "value";
	
	// Definition of suppression element (also including condition element)
	private static final String SUPPRESSION = "suppression";

	// Definition of translation element / child elements and attributes
	private static final String TRANSLATION = "translation";
	private static final String CONDITION = "condition";
	private static final String CONDITIONFIELD = "field";
	private static final String CONDITIONVALUE = "value";
	private static final String ALARM = "alarm";
	private static final String ATTR = "attr";
	private static final String ATTRFIELD = "field";
	private static final String ATTRVALUE = "value";
	private static final String ATTRMAP = "map";
	
	private static Logger logger = Logger.getLogger(TrapTranslationLoader.class.getName());
	
	private static TrapTranslationLoader instance = new TrapTranslationLoader();
	
	private static Hashtable<String, LinkedList<TrapTranslation>> translations;
	private static Hashtable<String, LinkedList<TrapMap>> maps;
	
	private static int instanceTranslationCount;
	private static int instanceSuppressionCount;
	private static int instanceMapCount;
	private static File currentFile;
	private static boolean isValidTranslation;
	private static boolean isValidMap;
	
	// variables for trap translations.
	private static TrapTranslation translation;
	private static TrapMap map;
	private static String typeName;
	private static IExpression expression; //NOPMD
	
	private static final List<String> m_TranslationFiles = new Vector<String>();
	
	/**
	 * Create a TrapTranslator from a couple of 
	 * @param alarmTranslationsLocation
	 * @param originalTranslations 
	 * @return a loaded TrapTranslator
	 */
	public static Hashtable<String, LinkedList<TrapTranslation>> fromXML(final File alarmTranslationsLocation,
			final Hashtable<String, LinkedList<TrapTranslation>> originalTranslations,
			final Hashtable<String, LinkedList<TrapMap>> originalMaps) {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".fromXML(); --> " + alarmTranslationsLocation);
		}
		
		if (alarmTranslationsLocation == null) {
			logger.error("TrapTranslationLoader unable to load translations, the location is null");
		}
		
		translations = originalTranslations;
		maps = originalMaps;
		
		final File[] alarmTranslations = getFiles(alarmTranslationsLocation);
		
		parseFiles(alarmTranslations);
				
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".fromXML(); <-- " + alarmTranslationsLocation);
		}
		return translations;
	}
	
	private TrapTranslationLoader(){
		BeanServerHelper.registerMBean(this);
	}

	/**
	 * 
	 * @param files
	 */
	private static void parseFiles(final File[] files) {
		for (final File file : files) {
			if (logger.isDebugEnabled()) {
				logger.debug(TrapTranslationLoader.class.getName()+".parseFiles(); Parsing XML file " + file.getName());
			}
			
			instanceTranslationCount = 0;
			instanceSuppressionCount = 0;
			instanceMapCount = 0;
			currentFile = file;
			isValidTranslation = true;
			isValidMap = true;
			
			if (parse(file)) {
				m_TranslationFiles.add(file.toString());
				logger.info(TrapTranslationLoader.class.getName()+".parseFiles(); Parsed " +
						instanceTranslationCount + " translation(s), " +
						instanceSuppressionCount + " suppression(s) and " +
						instanceMapCount + " map(s) from file " + file.getName());
			} else {
				logger.error(TrapTranslationLoader.class.getName()+".parseFiles(); Unable to parse the XML file " + file.getName() + ".");
			}
		}
	}

	/**
	 * Returns the files in the directory.
	 * @param location
	 */
	private static File[] getFiles(final File dir) {

		if (!dir.isDirectory()) {
			logger.error(TrapTranslationLoader.class.getName()+
					".getFiles();  Unable to load translations from XML files, the location " + 
					dir.getName() + " is not a directory");
		}

		final File[] files = dir.listFiles();

		if (files == null) {
			return new File[0];
		}
		
		final LinkedList<File> xmlFiles = new LinkedList<File>();

		for (final File file : files) {
			xmlFiles.add(file);
		}

		return xmlFiles.toArray(new File[xmlFiles.size()]);
	}

	/**
	 * Starts the parsing of the file. This object is set as the handler for the
	 * XML tags. The methods startElement and endElement are the ones that does
	 * all the creation
	 * 
	 * @param file
	 * @return
	 */
	private static boolean parse(final File file) {
		
		XMLReader reader;
		
		try {
			reader = XMLReaderFactory.createXMLReader();			
			reader.setContentHandler(instance);
			reader.setErrorHandler(instance);
			reader.setDTDHandler(instance);
			reader.setEntityResolver(instance);
			reader.setFeature(VALIDATIONFEATURE, true);
			reader.parse(file.getPath());
			
			return true;
		} catch (final SAXException e) {
			logger.error(TrapTranslationLoader.class.getName()+".parse(); Problem parsing xml file " + file.getName(), e);
			isValidTranslation = false;
			isValidMap = false;
			return false;
		} catch (final IOException e) {
			logger.error(TrapTranslationLoader.class.getName()+".parse(); Could not read/write the file or the file does not exist: " + file.getName(), e);
			isValidTranslation = false;
			isValidMap = false;
			return false;
		} catch (final Exception e) {
			logger.error(TrapTranslationLoader.class.getName()+".parse(); unable to load composer, an unhandle exception occured", e);
			isValidTranslation = false;
			isValidMap = false;
			return false;
		} 
	}
	
	/**
	 * Called whenever a new tag is found.
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName,final Attributes attributes) { //NOPMD
		if (qName.equalsIgnoreCase(TRANSLATIONS)) {
			typeName = attributes.getValue(SYSTEMTYPE);
			
			if (typeName == null || typeName.equalsIgnoreCase("")) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in file " + currentFile.getName() + " the system type is not correct.");
				isValidTranslation = false;
				return;
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(TrapTranslationLoader.class.getName()+".startElement(); Processing translations for system " + typeName + " from file " + currentFile.getName());
			}
			
		} else if (qName.equalsIgnoreCase(MAP)) {
			final String mapId = attributes.getValue(MAPID);
			
			if (mapId == null || mapId.equalsIgnoreCase("")) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in attribute id for map " + instanceMapCount + " in file " + currentFile.getName());
				isValidMap = false;
				return;
			}
				
			map = new TrapMap(mapId);
			isValidMap = true;
			instanceMapCount++;
		} else if (qName.equalsIgnoreCase(MAPENTRY)) {
			final String key = attributes.getValue(ENTRYKEY);
			final String value = attributes.getValue(ENTRYVALUE);
			
			if (key == null || key.equalsIgnoreCase("") || value == null /* value = "" is ok */) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in entry for map " + instanceMapCount + " in file " + currentFile.getName());
				isValidMap = false;
				return;
			}
			
			map.setEntry(key, value);
		} else if (qName.equalsIgnoreCase(MAPDEFAULT)) {
			final String defaultValue = attributes.getValue(DEFAULTVALUE);
			
			if (defaultValue == null /* default value equals "" is ok */) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in default for map " + instanceMapCount + " in file " + currentFile.getName());
				isValidMap = false;
				return;
			}
			
			// A default value equals "" is ok
			map.setDefaultValue(defaultValue);
			
			if (isValidMap) {
				addMap(map);
				map = null;
				
				if (logger.isDebugEnabled()) {
					logger.debug(TrapTranslationLoader.class.getName()+".startElement(); Successfully loaded translation map " + instanceMapCount + " from file " + currentFile.getName());
				}
			} else {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); Unable to load translation map " + instanceMapCount + " from file " + currentFile.getName());
			}
			
		} else if (qName.equalsIgnoreCase(SUPPRESSION)) {
			translation = new TrapTranslation();
			translation.setSuppress(true);
			isValidTranslation = true;
			instanceSuppressionCount++;
		} else if (qName.equalsIgnoreCase(TRANSLATION)) {
			translation = new TrapTranslation();
			isValidTranslation = true;
			instanceTranslationCount++;
		} else if (qName.equalsIgnoreCase(ALARM)) {
			// Nothing to process, we are more interested of the child element and attributes  
		} else if (qName.equalsIgnoreCase(ATTR)) {
			final String field = attributes.getValue(ATTRFIELD);
			final String value = attributes.getValue(ATTRVALUE);
			final String map = attributes.getValue(ATTRMAP);
			
			if (field == null || field.equalsIgnoreCase("") || value == null) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in attr (field/value) for alarm " + instanceTranslationCount + " in file " + currentFile.getName());
				isValidTranslation = false;
				return;
			}
			
			if (map != null && map.equalsIgnoreCase("")) { // If no map attribute stated at all, this field will automatically be set to "mib"
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in attr (map) for alarm " + instanceTranslationCount + " in file " + currentFile.getName());
				isValidTranslation = false;
				return;
			}
			
			expression = ExpressionBuilder.compileExpression(value);
			
			if (translation != null) {
				if (field.equalsIgnoreCase("id")) {
					translation.setAlarmID(expression, map);
				} else if (field.equalsIgnoreCase("specificproblem")) {
					translation.setSpecificProblem(expression, map);
				} else if (field.equalsIgnoreCase("dname")) {
					translation.setManagedObjectInstance(expression, map);
				} else if (field.equalsIgnoreCase("severity")) {
					translation.setPerceivedSeverity(expression, map);
				} else if (field.equalsIgnoreCase("type")) {
					if (value.equalsIgnoreCase("alarm")) {
						translation.setRecordType(com.ericsson.navigator.esm.model.alarm.Alarm.RecordType.NON_SYNC_ALARM);
					} else if (value.equalsIgnoreCase("event")) {
						translation.setRecordType(com.ericsson.navigator.esm.model.alarm.Alarm.RecordType.EVENT);
					} else {
						logger.error(TrapTranslationLoader.class.getName()+".startElement(); undefined recordType " + value + " in translation " + instanceTranslationCount + " in file " + currentFile.getName());
						isValidTranslation = false;
						return;
					}
				} else if (field.equalsIgnoreCase("additionaltext")) {
					translation.setAdditionalText(expression, map);
				} else if (field.equalsIgnoreCase("eventtype")) {
					translation.setEventType(expression, map);
				} else if (field.equalsIgnoreCase("probablecause")) {
					translation.setProbableCause(expression, map);
				}
			} else {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); no valid parent element found for attr in file " + currentFile.getName());
				isValidTranslation = false;
				return;
			}
			
		} else if (qName.equalsIgnoreCase(CONDITION)) {			
			final String conditionField = attributes.getValue(CONDITIONFIELD);
			final String conditionValue = attributes.getValue(CONDITIONVALUE);
			
			if (conditionField == null || conditionField.equalsIgnoreCase("") || conditionValue == null || conditionValue.equalsIgnoreCase("")) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); found fault in condition for translation " + instanceTranslationCount + " in file " + currentFile.getName());
				isValidTranslation = false;
				return;
			}
			
			final ICondition condition = new Condition(conditionField, conditionValue);
			
			if (!condition.compile()) {
				logger.error(TrapTranslationLoader.class.getName()+".startElement(); unable to compile condition for translation " + instanceTranslationCount + " in file " + currentFile.getName());
				isValidTranslation = false;
				return;
			} else {
				if (translation != null) {
					translation.addCondition(condition);
				} else {
					logger.error(TrapTranslationLoader.class.getName()+".startElement(); no valid parent element for condition in file " + currentFile.getName());
					isValidTranslation = false;
					return;
				}
			}
		}
	}

	/**
	 * Called when an end tag is found
	 */
	@Override
	public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
		if (qName.equalsIgnoreCase(TRANSLATIONS)) {
			typeName = null;
		} else if (qName.equalsIgnoreCase(TRANSLATION)) {
			if (isValidTranslation) {
				addTranslation(translation);
				translation = null;
				
				if (logger.isDebugEnabled()) {
					logger.debug(TrapTranslationLoader.class.getName()+".endElement(); Successfully loaded translation " + instanceTranslationCount + " from file " + currentFile.getName());
				}
			} else {
				logger.error(TrapTranslationLoader.class.getName()+".endElement(); Unable to load translation " + instanceTranslationCount + " from file " + currentFile.getName());
			}
		} else if (qName.equalsIgnoreCase(SUPPRESSION)) {
			if (isValidTranslation) {
				addTranslation(translation);
				translation = null;
				
				if (logger.isDebugEnabled()) {
					logger.debug(TrapTranslationLoader.class.getName()+".endElement(); Successfully loaded suppression " + instanceSuppressionCount + " from file " + currentFile.getName());
				}
			} else {
				logger.error(TrapTranslationLoader.class.getName()+".endElement(); Unable to load suppression " + instanceSuppressionCount + " from file " + currentFile.getName());
			}
		}
	}

	/**
	 * This will add a translation to a given type.
	 */
	private void addTranslation(final TrapTranslation translation) {
		LinkedList<TrapTranslation> typeTranslations;
		
		if (translations.containsKey(typeName)) {
			typeTranslations = translations.get(typeName);
		} else {
			typeTranslations = new LinkedList<TrapTranslation>();
			translations.put(typeName, typeTranslations);
		}
		
		typeTranslations.add(translation);
	}
	
	/**
	 * This will add a map to a given type.
	 */
	private void addMap(final TrapMap map) {
		LinkedList<TrapMap> typeMaps;
		
		if (maps.containsKey(typeName)) {
			typeMaps = maps.get(typeName);
		} else {
			typeMaps = new LinkedList<TrapMap>();
			maps.put(typeName, typeMaps);
		}
		
		typeMaps.add(map);
	}
				
	/**
	 * Allows the application to resolve external entities. 
	 */
	@Override
	public InputSource resolveEntity(final String publicId, final String systemId) throws IOException, SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".resolveEntity(); publicId=" + publicId + ", systemId=" + systemId);
		}
		return super.resolveEntity(publicId, systemId);
	}
	
	/**
	 * Receives notification of the beginning of the document.
	 */
	@Override
	public void startDocument() throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".startDocument(); ");
		}
		super.startDocument();
	}
	
	/**
	 * Receive notification of the end of a document.
	 */
	@Override
	public void endDocument() throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".endDocument(); ");
		}
		super.endDocument();
	}
		
	/**
	 * Prints a warning message upon SAX parsing problem.
	 */
	@Override
	public void warning(final SAXParseException exception) throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".warning(); " + exception.toString());
		}
		super.warning(exception);
	}
	
	/**
	 * Prints a error message upon SAX parsing problem.
	 */
	@Override
	public void error(final SAXParseException exception) throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".error(); " + exception.toString());
		}
		super.error(exception);
	}
	
	/**
	 * Prints a fatal error message upon SAX parsing problem.
	 */
	@Override
	public void fatalError(final SAXParseException exception) throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug(TrapTranslationLoader.class.getName()+".fatalError(); " + exception.toString());
		}
		super.fatalError(exception);
	}

	public List<String> getLoadedTranslationFiles() {
		return m_TranslationFiles;
	}

	public String getInstanceName() {
		return "";
	}

	public static void clearJMXLoadedFiles() {
		m_TranslationFiles.clear();
	}
}
