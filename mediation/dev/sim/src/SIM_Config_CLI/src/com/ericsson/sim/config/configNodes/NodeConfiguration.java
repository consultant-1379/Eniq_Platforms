package com.ericsson.sim.config.configNodes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.parser.CsvParser;

public class NodeConfiguration {

	private String csvFile = "";
	protected CsvParser csvParser;

	private String[] csvColumnHeaders;
	private List<String> tagNames;
	private int indexOfPropertiesCol;
	private int indexOfNameColumn;
	private int indexofIPColumn;
	private int indexOfUniqueIDColumn;
	private int numberOfNodes;
	private int numberOfNodesSkipped;

	public int getNumberOfNodesSkipped() {
		return numberOfNodesSkipped;
	}

	public void setNumberOfNodesSkipped(int numberOfNodesSkipped) {
		this.numberOfNodesSkipped = numberOfNodesSkipped;
	}

	private String outputFile;
	private PrintWriter printWriter;

	public NodeConfiguration(String filepath) {
		outputFile = SIMEnvironment.CONFIGPATH + "/topology.xml";

		csvFile = filepath;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					outputFile)));
			writeToFile("<simconfig>");
		} catch (IOException e) {
			System.out
					.println("Error opening topology file in NodeConfiguration "
							+ e.getMessage());
			;
		}

		csvParser = new CsvParser();

		runConfigurationOfNodes();

		csvParser.closeFileParser();

		System.out.println("Parsing nodes to new format complete");
		System.out.println("Imported: " + this.getNumberOfNodes() + " Nodes");
		System.out.println("Ignored: " + this.getNumberOfNodesSkipped()
				+ " Nodes");

		numberOfNodes = 0;
		numberOfNodesSkipped = 0;

	}

	/**
	 * 
	 * Use a csv parser to load the file thus its buffered reader. We get the
	 * header information allowing us to instantiate our tag data structure.
	 * Locate the properties column as it heavily influences our mappings and we
	 * must take care of people wrongly changing this.
	 * 
	 * 
	 * 
	 */

	private void runConfigurationOfNodes() {
		csvParser.loadFile(csvFile);
		csvColumnHeaders = csvParser.getDocumentHeader();

		tagNames = new ArrayList<String>();
		getTagNames(csvColumnHeaders);

		locatePropertiesColumn();
		getAllRows();

		writeToFile("</simconfig>");
		printWriter.close();

	}

	/**
	 * 
	 * This method is used as the migration from customer .csv to xml config
	 * file is heavily dependent on where the properties column is located
	 * 
	 */

	private void locatePropertiesColumn() {
		for (int i = 0; i < csvColumnHeaders.length; i++) {

			if (csvColumnHeaders[i].trim().equalsIgnoreCase("pluginNames")
					|| csvColumnHeaders[i].trim().equalsIgnoreCase("Protocols")) {
				indexOfPropertiesCol = i;
				break;
			}
		}
	}

	/**
	 * 
	 * We use the csv parser to send us back every String from a line of text
	 * repeatedly from the file in a String array, until there are no more lines
	 * and we send this to a method which manipulates this data
	 * 
	 */

	private void getAllRows() {

		String[] row = null;

		while ((row = csvParser.getNextLine()) != null) {
			if (!row[0].startsWith("#")) {
				numberOfNodes++;
				extractRowInformation(row);
			} else {
				numberOfNodesSkipped++;
			}
		}

	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	/**
	 * We find the index of the name column as this is inputed differently to
	 * the xml file. We get the number of protocols as these can vary and we
	 * need this information so as we can align each header with their
	 * respective attributes in the rows below them
	 * 
	 * We use above information to map rows to header and write the name value
	 * for the name attribute to a file
	 * 
	 * We then create each xml tag excepting name and properties
	 * 
	 * We then create the properties tag
	 * 
	 * 
	 * @param row
	 */

	private void extractRowInformation(String[] row) {

		int numberOfProtocols = 0;
		String protocols = "";
		String name = null;
		String ipAddress = null;
		String uniqueID = null;

		
		for (int i = 0; i < csvColumnHeaders.length; i++) {

			if (csvColumnHeaders[i].equals("name")) {
				indexOfNameColumn = i;
				
			}

			// added as change of IP Address to node tag
			if (csvColumnHeaders[i].equals("IPAddress")) {
				indexofIPColumn = i;
				
			}

			if (csvColumnHeaders[i].equals("uniqueID")) {
				indexOfUniqueIDColumn = i;
				
			}
		}
		
		numberOfProtocols = row.length - (csvColumnHeaders.length - 1);
		

		
		
		// Finding out what word in the string array from each row
		// will map to the name header from file
		// we can then create the node "NAME" key value pair
		// and write it to file

		if (indexOfNameColumn < indexOfPropertiesCol) {
			name = row[indexOfNameColumn];
		} else {
			name = row[(indexOfNameColumn + numberOfProtocols) - 1];
		}

		if (indexofIPColumn < indexOfPropertiesCol) {
			ipAddress = row[indexofIPColumn];
		} else {
			ipAddress = row[(indexofIPColumn + numberOfProtocols) - 1];
		}
		
		try{
			if (indexOfUniqueIDColumn < indexOfPropertiesCol) {
				uniqueID = row[indexOfUniqueIDColumn];
			} else {
				uniqueID = row[(indexOfUniqueIDColumn + numberOfProtocols) - 1];
			}
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("No value exists for UniqueID");
		}

		

		writeToFile(createNodeTag(name, ipAddress, uniqueID));

		// Logic required as protocols and number of them will affect row and
		// header mappings
		// depending on where that column is
		// Generically we can create correct mappings to any header column
		for (int i = 0; i < indexOfPropertiesCol; i++) {
			try{
				createTag(csvColumnHeaders[i], row[i]);
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("No value exists for: "+ csvColumnHeaders[i]);
			}
			
		}

		for (int i = indexOfPropertiesCol + 1; i < csvColumnHeaders.length; i++) {
			
			try{
				createTag(csvColumnHeaders[i], row[indexOfPropertiesCol
				               					+ numberOfProtocols]);
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("No value exists for: "+ csvColumnHeaders[i]);
			}
			
		}

		for (int i = indexOfPropertiesCol; i < indexOfPropertiesCol
				+ numberOfProtocols; i++) {
			
			try{
				protocols = protocols.concat(row[i]);
				if (i <= indexOfPropertiesCol + numberOfProtocols - 2) {
					protocols = protocols.concat(",");
				}
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("No plugins exist for: "+ name);
			}
			
		}

		createTag("Protocols", protocols);
		writeToFile("</Node>");

	}

	/**
	 * Use print writer to append a line at a time to the file.
	 * 
	 * @param ouputXML
	 */

	private void writeToFile(String ouputXML) {
		printWriter.println(ouputXML);
	}

	/**
	 * Takes a generic tag name and a value a creates an xml line
	 * 
	 * @param tagName
	 * @param value
	 */

	private void createTag(String tagName, String value) {

		if (!tagName.equals("name") && !tagName.equals("IPAddress")
				&& !tagName.equals("uniqueID") && !value.isEmpty()
				&& !value.equals("null")) {
			writeToFile("\t<" + tagName + ">" + value + "</" + tagName + ">");
		}

	}

	/**
	 * We fill our tag names from the header in file
	 * 
	 * @param columnHeaders
	 */
	private void getTagNames(String[] columnHeaders) {

		if (columnHeaders != null && columnHeaders.length > 0) {
			for (String colName : columnHeaders) {
				tagNames.add(colName);
			}
		} else {
			System.out.println("No headers");
		}

	}

	/**
	 * Unique method for creating name tag as its is different from all others
	 * 
	 * @param name
	 * @return
	 */

	public String createNodeTag(String name, String ipAddress, String uniqueID) {
		return "<Node uniqueID=\"" + uniqueID + "\" name=\"" + name + "\" "
				+ "IPAddress=\"" + ipAddress + "\">";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new NodeConfiguration(args[0]);

	}

}
