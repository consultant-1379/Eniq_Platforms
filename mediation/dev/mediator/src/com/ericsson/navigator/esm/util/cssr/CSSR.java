/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2010
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.navigator.esm.util.cssr;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ericsson.navigator.esm.util.cssr.io.CSComponents;
import com.ericsson.navigator.esm.util.cssr.io.CSDom;
import com.ericsson.navigator.esm.util.cssr.io.CSElement;
import com.ericsson.navigator.esm.util.cssr.io.CSFileInputHandler;
import com.ericsson.navigator.esm.util.cssr.io.CSSystem;
import com.ericsson.navigator.esm.util.cssr.io.CounterSetDefinitionHandler;
import com.ericsson.navigator.esm.util.cssr.io.UserInputException;


/**
 * Class will allow a user to create a topology file for the charging system
 * integration.
 * 
 * @author ejammor
 */

public class CSSR {

	public static final Logger logger = Logger.getLogger("CSSR");

	public String propsFile = null;
	private String fmComponentFileName = null;
	private String loggerPropsFile = null;
	private String outputFile = null;
	
	private String pmComponentFileName=null;
	private CSComponents pmComponents =null;
	public static String pm_enable =null;

	// message strings
//	private final static String USAGE_STRING = "Usage:"+"" +
//			"java -jar <csjar> -props <props_file> -out <output file> -temp <template file>  -type <node type>\n" +
//			"java -jar <csjar> -props <props_file> -out <output file> -addtemp <template file>  -type <node type>\n" +
//			"java -jar <csjar> -props <props_file> -out <output file> -removetemp <system name>  -type <node type>\n" +
//			"java -jar <csjar> -props <props_file> -out <output file> -removeall <system name>  -type <node type>\n";

	private final static String USAGE_STRING = "";

//	private final static String TOP_FILE_FROM_USER_INPUT = "\nCSSR:execute() creating/appending topology file from user input.";
	private final static String FILE_FROM_CSCREATE = "\nCSSR:execute() creating counterset definition file.";
	private final static String FILENAME_FROM_CSCREATE = "\nGenerated Counterset Definitions are written to : ";
//	private final static String ADD_NODES_FROM_USER_INPUT = "\nCSSR:execute() adding nodes to topology file.";
	private final static String REMOVE_NODE_FROM_USER_INPUT = "\nCSSR:execute() removing node(s) from topology file.";
	private final static String REMOVE_ALL_NODES = "\nCSSR:execute() removing all nodes from topology file.";
	private final static String TOP_FILE_FROM_FILE_INPUT = "\nCSSR:execute() creating/adding nodes to topology file from template file.";
	private final static String NODETYPE = "-nodetype";
	private final static String REMOVE_TEMPLATE = "-removetemp";
	private final static String REMOVE_ALL = "-removeAll";
	private final static String TEMPLATE = "-temp";
	private final static String ADD_TEMPLATE = "-addtemp";
	private final static String CSCREATE = "-cscreate";
	private final static String CHECK_FOR_NODES = "-checkForNodes";
	
	/**
	 * Creates a new instance of <code>CSSR</code>
	 * 
	 * @param String
	 *            properties file name
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public CSSR(final String props) throws FileNotFoundException, IOException {
		this.propsFile = props;

		initialize();
	}

	/**
	 * initilizes properties for the Charging System application, property file
	 * is etc/cs.properties
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void initialize() throws FileNotFoundException, IOException {
	
		final Properties props = new Properties();
		props.load(new FileInputStream(propsFile));

		fmComponentFileName = props.getProperty("cssr.fm.components", "");
		pmComponentFileName = props.getProperty("cssr.pm.components", "");
		loggerPropsFile = props.getProperty("cssr.log4j.propsfile",
				"etc/cssr.log4j.properties");
		PropertyConfigurator.configure(loggerPropsFile);
		//System.setProperty(props.getProperty("remoteFileDetails", "/nav/opt/si/ccn/etc/NodeRemoteFileFetchDetails.txt"), "/nav/opt/si/ccn/etc/NodeRemoteFileFetchDetails.txt");
		
		System.setProperty("CCN_remoteFileDetails",props.getProperty("CCN_remoteFileDetails", "/nav/opt/si/ccn/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("SDP_remoteFileDetails",props.getProperty("SDP_remoteFileDetails", "/nav/opt/si/sdp/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("AIR_remoteFileDetails",props.getProperty("AIR_remoteFileDetails", "/nav/opt/si/air/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("VS_remoteFileDetails",props.getProperty("VS_remoteFileDetails", "/nav/opt/si/vs/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("IVR_remoteFileDetails",props.getProperty("IVR_remoteFileDetails", "/nav/opt/si/ivr/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("OCMP_remoteFileDetails",props.getProperty("OCMP_remoteFileDetails", "/nav/opt/si/ivr/etc/OCMP_NodeRemoteFileFetchDetails.txt"));
		System.setProperty("MINSAT_remoteFileDetails",props.getProperty("MINSAT_remoteFileDetails", "/nav/opt/si/minsat/etc/NodeRemoteFileFetchDetails.txt"));
		System.setProperty("CRS_remoteFileDetails",props.getProperty("CRS_remoteFileDetails", "/nav/opt/si/crs/etc/NodeRemoteFileFetchDetails.txt"));
		
		
	
		pm_enable =props.getProperty("PM_ENABLE","false");
		//System.setProperty("CCN_PM_ENABLE",ccn_pm_enable);
		
		
	/*	System.setProperty(props.getProperty("SDP_PM_ENABLE",
		"false"),
		"false");
		System.setProperty(props.getProperty("AIR_PM_ENABLE",
		"false"),
		"false");
		System.setProperty(props.getProperty("VS_PM_ENABLE",
		"false"),
		"false");*/
	}

	/**
	 * parses a file into a List of Strings
	 * 
	 * @param file
	 * @return List<String>
	 * @throws IOException
	 */
	private List<String> parseFile(final File file) throws IOException {
		final List<String> list = new ArrayList<String>();
		final BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		while (line != null && !"".equals(line)) {
			list.add(line.trim());
			line = in.readLine();
		}
		return list;
	}

	/**
	 * Checks whether a topology file is empty or not.
	 * @param filepath
	 * @return 
	 * @return String if nodes in the topology file.
	 * @throws IOException
	 */			
	private String hasTopologyFileNodes(final String filepath) throws IOException {
		final File file = new File(filepath);
		final List<String> lines = parseFile(file);
		int count=0;
		String line;
		for(int i=0;i<lines.size();i++) {
			line = lines.get(i);
			if(line.startsWith("<SNOSNE ")) {
				if(++count > 1) {
					return "true";
				}
			}
		}		
		return "false";		
	}
	/**
	 * The entry point for the Charging System topology generation.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {

		try {

			validateArgs(args);

			final String propsFile = args[1];
			final CSSR cs = new CSSR(propsFile);

			if (CSCREATE.equalsIgnoreCase(args[4])) {
				cs.createCounterSetDefinition(args);
			} else {
				cs.execute(args);
			}
		} catch (final Exception e) {
			final String message = e.getMessage();
			System.out.println(message);
		}
	}

	/**
	 * @param args
	 * @throws UserInputException 
	 * @throws IOException 
	 */
	private void createCounterSetDefinition(final String[] args)throws UserInputException {
		// TODO Auto-generated method stub
		String message = null;
		final String nodeType = args[args.length - 1];
		final CounterSetDefinitionHandler user = new CounterSetDefinitionHandler();
		final String outputFileNamePattern = args[3];
		final String command = args[4];
	
		final File file = new File(outputFileNamePattern);

		if (!file.isDirectory()) {
			message = "Specified Output dirctory does not exist :"
					+ outputFileNamePattern;
			logger.error("CSSR:validateArgs()" +  message);
			throw new UserInputException(message);
		}

		if (CSCREATE.equalsIgnoreCase(command)) {
			final String inputFileName = args[5];
			message = FILE_FROM_CSCREATE + ": " + " node type: " + nodeType + "   Inputfile " + ":" + inputFileName;
			System.out.println(message);
			logger.info(message);
			try {
				user.createCounterSetDefinition(inputFileName, command, outputFileNamePattern, nodeType);
			} catch (final IOException e) {
				message = "CSSR:createCounterSetDefinition()  Exception: " + e.getMessage();
				logger.error(message);
				System.out.println(message);
			} catch (final UserInputException e) {
				message = "CSSR:createCounterSetDefinition()  Exception: " + e.getMessage();
				logger.error(message);
				System.out.println(message);
			}
		}
	}

	/**
	 * runs the topology building process.
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private void execute(final String[] args) throws UserInputException, IOException, ParserConfigurationException, SAXException, TransformerException {

		// to restrict tech packs to generating their own types of nodes
		// originality was to generate every type from template, preserving
		// this under type 'all'
		final String nodeTypeToGenerate = args[args.length - 1];

		// deal with the guaranteed args
		// properties file, output file names and command
		final String command = args[4];
		outputFile = args[3];

		List<CSSystem> systems = null;
		try {
			systems = executeHelper(args,command,nodeTypeToGenerate);
		}catch(UserInputException e) {
			if(ADD_TEMPLATE.equalsIgnoreCase(command)) {
				final File file = new File(outputFile);
				if(file.exists()) {
					file.delete();
				}
				final String message = "CSSR:execute() error in user input for adding to export file: deleting export file: " + outputFile;
				System.out.println(message);
				logger.info(message);
				throw e;		
			}
			final String message = "CSSR:main()  " + e.getMessage();
			logger.error(message);
			System.out.println(message);
		}
		
		//ejammor: this bit needs to be cleaned up in the remove script now no need for the -checkfornodes bit if -removeAll is used
		if(!haveElementsToWrite(systems) && !REMOVE_ALL.equalsIgnoreCase(command)){
			final String message = "\nCSSR:execute() no charging system topology file being created as no systems defined. "
				+ outputFile;
			System.out.println(message);
			logger.info(message);
			return;
		}		
		
		//before writing make sure all the objects declared in the template file belong to the same charging system
		//CSTopologyUtilities.checkAllBelongToSameChargingSystem(systems);
		
		executeWriteHelper(command, systems, nodeTypeToGenerate);
	}

	private boolean haveElementsToWrite(final List<CSSystem> systems) {
		
		if(systems == null || systems.size() == 0) {
			return false;
		}
		
		//check for elements in the systems
		CSSystem sys = null;
		List<CSElement> elements = null;
		for(int i=0;i<systems.size();i++) {
			sys = systems.get(i);
			elements = sys.getElements();
			
			if(elements == null || elements.size() == 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private List<CSSystem> createSystemsFromTemplate(final String nodeTypeToGenerate, final String template, final String  command) throws IOException, UserInputException{

		final String templateFile = template;
		final CSFileInputHandler reader = new CSFileInputHandler();

		final String message = TOP_FILE_FROM_FILE_INPUT + ": " + templateFile
				+ " node type: " + nodeTypeToGenerate + " command used : " + command;
		System.out.println(message);
		logger.info(message);
		
		//final List<CSComponents> list = reader.parseComponentsFile(fmComponentFileName);
		final CSComponents fmComponents = reader.parseComponentsFile(fmComponentFileName);
		if (pm_enable.equals("true")) {
			if (!pmComponentFileName.equals("")) {
				pmComponents = reader
						.parseComponentsFile(pmComponentFileName);
			}
		}
		return reader.parseInputFile(templateFile, nodeTypeToGenerate, fmComponents,pmComponents);
	}
	
	private List<CSSystem> executeHelper(final String[] args, final String  command,
			final String nodeTypeToGenerate) throws IOException, UserInputException {
		List<CSSystem> systems = null;
		String message;
		if (TEMPLATE.equalsIgnoreCase(command) || ADD_TEMPLATE.equalsIgnoreCase(command)) {

//			final String templateFile = args[5];
//			final CSFileInputHandler reader = new CSFileInputHandler();
//
//			message = TOP_FILE_FROM_FILE_INPUT + ": " + templateFile
//					+ " node type: " + nodeTypeToGenerate + " command used : " + command;
//			System.out.println(message);
//			logger.info(message);
//			
//			//final List<CSComponents> list = reader.parseComponentsFile(fmComponentFileName);
//			final CSComponents fmComponents = reader.parseComponentsFile(fmComponentFileName);
//			if (pm_enable.equals("true")) {
//				if (!pmComponentFileName.equals("")) {
//					pmComponents = reader
//							.parseComponentsFile(pmComponentFileName);
//				}
//			}
//			systems = reader.parseInputFile(templateFile, nodeTypeToGenerate, fmComponents,pmComponents);
			
			systems = createSystemsFromTemplate(nodeTypeToGenerate, args[5], command);

		} else if (REMOVE_TEMPLATE.equals(command)) {
			
			message = REMOVE_NODE_FROM_USER_INPUT;
			final String templateFile = args[5];
			final CSFileInputHandler reader = new CSFileInputHandler();		
			//final List<CSComponents> list = reader.parseComponentsFile(fmComponentFileName);
			final CSComponents fmComponents = reader.parseComponentsFile(fmComponentFileName);
			if (pm_enable.equals("true")) {
				if (!pmComponentFileName.equals("")) {
					pmComponents = reader
							.parseComponentsFile(pmComponentFileName);
				}
			}
			systems = reader.parseInputFile(templateFile, nodeTypeToGenerate, fmComponents,pmComponents);

			if(systems == null) {
				message += ", no node input, not removing.";
			}
			System.out.println(message);
			logger.info(message);
			
		}else if (REMOVE_ALL.equals(command)) {
			
			final String systemName = args[5];			
			message = REMOVE_ALL_NODES + " node type: " + nodeTypeToGenerate + " system: " + systemName;
			System.out.println(message);
			logger.info(message);
			systems = new ArrayList<CSSystem>();
					
			final CSSystem sys = new CSSystem();
			systems.add(sys);
			final CSElement el = new CSElement();
			el.setName("all");
			el.setType(nodeTypeToGenerate);
			sys.addElement(el);
		} else if(CHECK_FOR_NODES.equals(command)){
			//want unix envoirnment to pick up the result
			message = "\nchecking for charging nodes in file : " + outputFile;
			logger.info(message);
			System.out.println(hasTopologyFileNodes(outputFile));			
			//no futher work to be done just exit
		}		
		return systems;
	}

	private void executeWriteHelper(final String command, final List<CSSystem> systems, 
			final String nodeTypeToGenerate) throws UserInputException, ParserConfigurationException, SAXException, IOException, TransformerException {

		final CSDom dom = new CSDom();
		Document doc = null;
		String message = "";
		if (TEMPLATE.equals(command)) {

				message = "\nCSSR:execute() creating charging system topology file : "
				+ outputFile + ", node type: " + nodeTypeToGenerate;
				
				doc = dom.createCSSRDocument(systems);
		} else {
				//final CSSystem system = systems.get(0);
				message = "\nCSSR:execute() adding nodes to output file file : " + outputFile;
				
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    factory.setIgnoringComments(true);
			    factory.setIgnoringElementContentWhitespace(true);
			    factory.setCoalescing(true); // Convert CDATA to Text nodes
				factory.setNamespaceAware(false); // No namespaces: this is default
				factory.setValidating(false); // Don't validate DTD: also default

				final DocumentBuilder parser = factory.newDocumentBuilder();
			    doc = parser.parse(new File(outputFile));
				
			    //do the things specfic to each command
			    if (ADD_TEMPLATE.equals(command)) {
			    	checkForAlreadyExistingNodes(outputFile, systems);
			    	dom.addNodesToCSSRDocument(doc, systems);
				} else if (REMOVE_TEMPLATE.equals(command) ) {
					 //final int removedNodeCount = dom.removeNodesFromCSSRDocument(doc, system);
					final int removedNodeCount = dom.removeNodesFromCSSRDocument(doc, systems);
					 if(dom.isDocumentEmpty(doc)) {
						 System.out.println("\nThis will remove all the charging system nodes in the CS, manual removal of " +
					 		"systems using ESM sr script with delete option is required! Output file will not be created!!");
			 
						 final File file = new File(outputFile);
						 if(file.exists()) {
							 file.delete();
						 }
					 }else { 
						 //final int removedNodeCount = writer.removeNodesFromTopologyFile(outputFile, systems.get(0));
						 message = "\nCSSR:execute() removing node(s) from output file : " + outputFile + ". Removed " + removedNodeCount + " node(s).";
					 }
					 	
				} else if(REMOVE_ALL.equals(command)) {	
					final CSSystem system = systems.get(0);
					final String nodeType = system.getElements().get(0).getType();
					//final boolean emptyFile = dom.removeAllNodesFromCSSRDocument(doc, nodeType);
					dom.removeAllNodesFromCSSRDocument(doc, nodeType);
					//TODO if these lines are used then the package remove script will not need the -checkForNodes switch...
//					if(emptyFile) {
//						System.out.println("true");
//					}else {
//						System.out.println("false");
//					}
				}			    
		}
		
		//ejammor this hack is here for remove_all, the remove scripts should be updated instead to loose the -checkfornodes stage
		if(!dom.isDocumentEmpty(doc) || REMOVE_ALL.equals(command)) {
			dom.writeSystemRegFile(outputFile, doc);
			//System.out.println(message);
			logger.info(message);
		}		
	}
	
	private void checkForAlreadyExistingNodes(final String outputFile, final List<CSSystem> systems)
		throws IOException, UserInputException {
		
		final List<String> lines = parseFile(new File(outputFile));
		int counter = 0;
		boolean haveSystem = false;
		String line = null;
		CSSystem system = null;
		
		for(int j=0;j<systems.size();j++) {
			system = systems.get(j);
			final List<CSElement> elements = system.getElements();
			for(int i=0;i<lines.size();i++) {
				
			    line = lines.get(i);
				if(line.startsWith("<SNOSNE ")) {
					counter++;
				}else if(line.startsWith("</SNOSNE>")) {
					counter--;
				}
				
				if(counter == 1) {
					if(line.indexOf("cimname=\""+system.getName()+'\"') > -1) {
						haveSystem = true;
					}
				}else if(counter==2 && haveSystem) {
					checkForAlreadyExistingNodesHelper(elements, line,system.getName());
				}
				
			}		
		}
	}
	
	private void checkForAlreadyExistingNodesHelper(final List<CSElement> elements, final String line, final String systemName) throws UserInputException {
		CSElement el = null;
		for(int y=0;y<elements.size();y++) {
			el = elements.get(y);
			if((line.indexOf("cimname=\""+el.getName()+'\"') > -1) && (line.indexOf("snostype=\""+el.getType()+'\"') > -1)) {
				final String message = "node: "+el.getName()+", type: " + el.getType() + " already exists in system : " + systemName;
				throw new UserInputException(message);
			}
		}
	}
	/**
	 * helper method to validate the user's input, tests if the properties and
	 * template file if supplied exists, throws exception if files do not exist.
	 * 
	 * @param params
	 * @throws UserInputException
	 */
	private static void validateArgs(final String[] args)
			throws UserInputException {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	
		File file = null;
		String filename = null;
		String message = null;


		if ((args.length != 8) || !NODETYPE.equals(args[args.length - 2])) {
			message = "CSSR:validateArgs() incorrect args given, "
					+ getUsageString();
			logger.error(message);
			throw new UserInputException(message);
		}

		// first check for the properties file
		filename = args[1];
		file = new File(filename);

		if (!file.exists()) {
			message = "CSSR:validateArgs() properties file does not exist :"
					+ filename;
			logger.error(message);
			throw new UserInputException(message);
		}

		// check the command
		final String command = args[4];

		validateArgsHelper(command);
		
		// check for the template file
//		if (TEMPLATE.equals(command) || ADD_TEMPLATE.equals(command) || REMOVE_TEMPLATE.equals(command)) {
//			filename = args[5];
//			file = new File(filename);
//
//			if (!file.exists()) {
//				message = "CSSR:validateArgs() template file does not exist :"
//						+ filename;
//				logger.error(message);
//				throw new UserInputException(message);
//			}			
//		}
//		
		// output file has to exist for addtemp or removetemp
//		if (ADD_TEMPLATE.equals(command) || REMOVE_TEMPLATE.equals(command)) {
//			filename = args[3];
//			file = new File(filename);
//
//			if (!file.exists()) {
//				message = "CSSR:validateArgs() output file does not exist :  "
//						+ filename;
//				logger.error(message);
//				throw new UserInputException(message);
//			}			
//		}

		checkForFilesHelper(command, args);
	}

	private static void checkForFilesHelper(final String command, final String[] args) throws UserInputException {
		String filename = null;
		File file = null;
		String message = null;
		if (TEMPLATE.equals(command) || ADD_TEMPLATE.equals(command) || REMOVE_TEMPLATE.equals(command)) {
			checkValidFile(command, args); 
		}
		
		// output file has to exist for addtemp or removetemp
		if (ADD_TEMPLATE.equals(command) || REMOVE_TEMPLATE.equals(command)) {
			filename = args[3];
			file = new File(filename);
			
			if(!file.canWrite()){
				message = "CSSR:validateArgs() output directory does not have permisstion to write file:  "
					+ filename;
				throw new UserInputException(message);
			}
			else if (!file.exists()) {
				message = "CSSR:validateArgs() output file does not exist :  "
						+ filename;
				logger.error(message);
				throw new UserInputException(message);
			}			
		}
	}

	private static void checkValidFile(final String command, final String[] args)
			throws UserInputException {
		String filename;
		File file;
		String message;
		filename = args[5];
		file = new File(filename);
		if (!file.exists()) {
			message = "CSSR:validateArgs() template file does not exist :"
					+ filename;
			logger.error(message);
			throw new UserInputException(message);
		}
		final String outFile = args[3];
		file = new File(outFile);
		if (TEMPLATE.equals(command) && file.getParentFile() != null
				&& !file.getParentFile().canWrite()) {
			message = "CSSR:validateArgs() output directory does not have permisstion to write file:  "
					+ outFile;
			throw new UserInputException(message);
		}
	}

	private static void validateArgsHelper(final String command) throws UserInputException {
		if (!TEMPLATE.equals(command) && !REMOVE_TEMPLATE.equals(command)
				&& !ADD_TEMPLATE.equals(command) && !REMOVE_ALL.equals(command) && !CHECK_FOR_NODES.equals(command) && !CSCREATE.equalsIgnoreCase(command)) {
			final String message = "CSSR:validateArgs() invalid command given :" + command
					+ "\n" + getUsageString();
			logger.error(message);
			throw new UserInputException(message);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// the strings and their getter's are mostly to ease testing
	//

	/**
	 * returns CSSR usage string
	 * 
	 * @return String
	 */
	public static String getUsageString() {
		return USAGE_STRING;
	}

	/**
	 * returns CSSR create topology file from user input string
	 * 
	 * @return String
	 */
//	public static String getCreateFromUserInputMessage() {
//		return TOP_FILE_FROM_USER_INPUT;
//	}

	/**
	 * returns message indicating removing all nodes of given type from topology file
	 * 
	 * @return String
	 */
	public static String getRemoveAllNodesFromTopologyFileMessage() {
		return REMOVE_ALL_NODES;
	}

	/**
	 * returns create topology file from file input string
	 * 
	 * @return String
	 */
	public static String getCreateFromFileInputMessage() {
		return TOP_FILE_FROM_FILE_INPUT;
	}
	
	/**
	 * returns create topology file from file input string
	 * 
	 * @return String
	 */
	public static String getCreateFromCSCreateMessage() {
		return FILE_FROM_CSCREATE;
	}
	
	/**
	 * returns create topology file from file input string
	 * 
	 * @return String
	 */
	public static String getFromCSCreateMessage() {
		return FILENAME_FROM_CSCREATE;
	}
}
