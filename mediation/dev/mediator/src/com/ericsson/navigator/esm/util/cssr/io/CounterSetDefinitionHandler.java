package com.ericsson.navigator.esm.util.cssr.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * Class handles the user's input for the generation of Counter Set Definition files.
 * 
 * @author qbhasha
 * 
 */
public class CounterSetDefinitionHandler {
	public static Logger logger = Logger.getLogger("ChargingSystem");

	private static final String initiatorStart = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n ";
	private static String counterSetDTD = "<!DOCTYPE counterset SYSTEM \"file:///nav/opt/esm/etc/counterset.dtd\" > \n ";
	private static final String initiatorEnd = "<counterset> \n <moid> \n   <managedsystem/> \n";
	private static final String terminator = "</counterset>";
	private boolean hasParsedData = false;
	private static final String classname = "CounterSetDefinitionHandler";

	/**
	 * Reading the template file to generate the CounterSet Definition.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */

	private List<String> readInputFile(final String filename) throws IOException {
		final ArrayList<String> lines = new ArrayList<String>();
		final File topFile = new File(filename);
		final BufferedReader top = new BufferedReader(new FileReader(topFile));

		String line = top.readLine();
		while (line != null) {
			line = line.trim();
			//HM59300 updated to handle spaces, tabs or newlines in counter set template
			if(!line.isEmpty()){
				lines.add(line.trim());
			}
			line = top.readLine();
		}
		return lines;

	}

	/**
	 * counterSet File definition creation.
	 * 
	 * @param inputFileName
	 * @param command
	 * @param outputFile
	 * @param nodeType
	 * @throws IOException
	 * @throws UserInputException
	 */
	public void createCounterSetDefinition(final String inputFileName, final String command, final String outputFile,
			final String nodeType) throws IOException, UserInputException {

		final List<String> csdef = readInputFile(inputFileName);

		processCounterSetDef(csdef, nodeType, outputFile);

		if(!hasParsedData){
			final String message = classname+ ".createCounterSetDefinition(); ERROR: Invalid counterset file. No counter sets created check input file : " +inputFileName +"\n";
			System.out.println(message);
			logger.error(message);
		}

	}

	/**
	 * processing the counterSet category and creating the XML elements
	 * 
	 * @param csdef
	 * @param nodeType
	 * @param outputFileName
	 * @throws IOException
	 */
	private void processCounterSetDef(final List<String> csdef, final String nodeType, final String outputFileName)
	throws IOException {

		String line;
		final String myLine = "CounterSetCategory";
		String outputFN;
		String contents = "";
		String countersettag = "";

		for (int i = 0; i < csdef.size(); i++) {
			line = csdef.get(i);
			if (line != "") {
				if (line.trim().startsWith(myLine)) {
					outputFN = nodeType + "_" + line.substring(line.indexOf("=") + 1) + "_" + "counterSet.xml";
					contents = contents + terminator;
					if ((outputFileName.lastIndexOf("/")+1) < outputFileName.length()) {
						outputFN = "/" + outputFN;
					}
					//HM58444 Updated to verify contents if incorrect file is given
					if(verify(contents)){
						hasParsedData = true;
						final String message = classname+ ".processCounterSetDef(); Generated Counterset Definitions are written to : " +outputFileName+outputFN +"\n";
						System.out.println(message);
						logger.debug(message);
						writeFile(outputFileName + outputFN, contents);
						contents = "";
					}else{
						final String message = classname+ ".processCounterSetDef(); ERROR: Not Generating file : " +outputFileName+outputFN +" because of invalid contents\n";
						System.out.println(message + " See Charging System Log for more information");
						logger.error(message);
					}
				} else {
					countersettag = processLine(line);
					contents = contents + countersettag;

				}

			}
		}

	}

	/**
	 * processing the counters and creating the XML elements
	 * 
	 * @param line
	 * @param contents
	 * @return
	 */
	private String processLine(final String line) {

		String contents = "" ;
		String tempCounters = "";

		if (line.startsWith("CounterSetName")) {
			contents = initiatorStart + counterSetDTD + initiatorEnd + contents;
			final int idx = line.indexOf("=");
			contents = contents + "<string value=\"" + line.substring(idx + 1) + "\"/> \n" + "</moid>" + "\n ";
		}
		else {

			final String[] value = line.split(",");
			final String[] name = "name=,id=,type=".split(",");
			if(value.length == 3){

				for (int i = 0; i < value.length; i++) {
					
					if(value[i] != null && !("".equals(value[i].trim()))){
						tempCounters = tempCounters + name[i].toString() +"\""+ value[i].toString() +"\""+ " ";
					} else{					

						//HM59414 updated to stop empty data being written to counterset.xml files	
						final String message = classname+ ".processLine(); ERROR: Counter data missing for \""+ name[i].toString() + "\" not adding line to counter set file : "+ line + " \n";
						System.out.println(message);
						logger.error(message);
						return "";
					}
				}
			} else{
				//HM58444 updated to stop incorrect file being used when parsing.
				//If there are commas in the file but data is incorrect it will not process because of index out of bounds exception
				final String message = classname+ ".processLine(); ERROR: Incorrect counter data in \""+ line + "\" not adding to counter set file. \n" +
				"Correct format for counter data is: <COUNTERNAME>,<COUNTERID>,<COUNTERTYPE>";
				System.out.println(message);
				logger.error(message);
				return "";
			}			

			contents = contents + "\n<counter " + tempCounters + "/>\n";

		}
		return contents;

	}

	/**
	 * Writing the final counterSet Definition.
	 * 
	 * @param string
	 * @param contents
	 * @throws IOException
	 */
	private void writeFile(final String fileName, final String contents) throws IOException {
		final File outFile = new File(fileName);
		final FileWriter out = new FileWriter(outFile);
		out.write(contents);
		out.close();

	}

	public boolean verify(final String counterSetToVerify){
		boolean isValid = true;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setValidating(true); // Default is false
			builder.parse(new InputSource(new StringReader(counterSetToVerify)));	         

		} catch (ParserConfigurationException e) {
			isValid =false;
			final String message = classname+ ".verify() ERROR: Validating counterset file \n"+
			"Invalid counter set file: " + counterSetToVerify.toString() + "\n";
			logger.error(message, e);   
		} catch (SAXException e) {
			isValid =false;
			final String message = classname+ ".verify() ERROR: Validating counterset file \n"+
			"Invalid counter set file: " + counterSetToVerify.toString() + "\n";
			logger.error(message, e);   
		} catch (IOException e) {
			isValid =false;
			final String message = classname+ ".verify() ERROR: Validating counterset file \n"+
			"Invalid counter set file: " + counterSetToVerify.toString() + "\n";
			logger.error(message, e);        	
		} 

		return isValid;

	}

	public void setDTD(final String newDTD){
		counterSetDTD = "<!DOCTYPE counterset SYSTEM \""+newDTD + "\" > \n ";
	}

	public String getContactInformation() {
		return "Navigator, ESM product development team";
	}

	public String getDescription() {
		return "CounterSet XML Definition Generator.";
	}
}
