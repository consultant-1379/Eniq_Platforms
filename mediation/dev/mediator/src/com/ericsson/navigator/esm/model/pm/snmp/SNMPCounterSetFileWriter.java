package com.ericsson.navigator.esm.model.pm.snmp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVM;
import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.model.pm.Counter;
import com.ericsson.navigator.esm.model.pm.Counter.CounterType;
import com.ericsson.navigator.esm.model.pm.CounterSet;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinition;
import com.ericsson.navigator.esm.model.pm.CounterDefinition;

/**
 * This class generates the CSV file contents and writes the files. 
 * @author ebrifol
 *
 */

public class SNMPCounterSetFileWriter extends Thread{

	private SNMPCounterSetFile file;
	private static final String classname = SNMPCounterSetFileWriter.class.getName();
	private static Logger logger = Logger.getLogger(classname);
	private String VCI = "";
	private String SEPERATOR = "_";
	private ArrayList<String> lines;
	private String type;
	private String fdn;
	private String northBound;
	private char completed = 'f';
	
	/**
	 * Run method on the thread. The SNMPCounterSetFile will be transformed to CSV stucture and written to a file. 
	 */
	public void run() {
		try{

		VCI = "";
		CounterSetDefinition definition = file.getCounterSetDefintion(); //The definition is the CounterSetDefinition that contains the list of all requested counters
		String timeStamp = makeTimeStamp(file.getTimeOfRop());
		lines = new ArrayList<String>(); // Start of file
		lines.add("ROP Time,Variable Counter ID"); // Add the header
		northBound = definition.getNorthBoundLocation();
		
		LinkedHashMap<String, String> template = new LinkedHashMap<String, String>();
		Map<String, CounterDefinition> expectedCounters = definition.getDefinitions();
		Iterator<String> iterator = expectedCounters.keySet().iterator();
		while (iterator.hasNext()) {
			CounterDefinition counter = expectedCounters.get(iterator.next());
			if(!counter.getType().equals(CounterType.INDEX)){
				String name = counter.getName();
				template.put(name, null);
			}
		}

		ArrayList<CounterSet> counterset = file.getListOfCounterSets(); // Get list of countersets for this file
		if (counterset != null && counterset.size() > 0) {
			for (CounterSet cs : counterset) { // For each counterset
				type = cs.getType();
				fdn = cs.getFdn();
				String moid = cs.getMoid();
				parseVCI(moid);
				
				/*********************************************************************
				 The counter names from the template are used as the key for the map of received data. 
				 So the file will only consists of the counters from the CounterSetDefinition file
				 ***************************************************************/
				Map<String, Counter> counters = cs.getCounters(); 
				iterator = template.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();								
					Counter counter = counters.get(key);
					template.put(key, counter.getValueStr());

						
				}
				
				iterator = template.keySet().iterator();
				while (iterator.hasNext()) {
					String counterName = iterator.next();
					addToHeader(counterName);
					String value = template.get(counterName);
					addtoBody(value, timeStamp);
				}							
			}
			writeFile(lines);	
		}

		}catch(Exception e){
			logger.error(classname + ".run() Error when creating data for csv file");
			
		}
		completed = 't';
	}
	

	/**
	 * Method adds a counter name to the header of the CSV file
	 * @param counterName String, the name of the counter to be added.
	 */
	private void addToHeader(String counterName){
		String header = lines.get(0);
		
		String[] parts = header.split(",");
		boolean foundMatch = false;
		for(int index = 0; foundMatch == false && index < parts.length; index++){
				if(parts[index].equals(counterName)){
					foundMatch = true;
				}
			}
		
		if(!foundMatch){
			lines.set(0, header.concat("," + counterName));
		}
		
		
		
		
		
		//if (!header.contains(counterName+",")) {
		//	lines.set(0, header.concat("," + counterName));
		//}
	}
	

	/**
	 * Method adds a value to a line in the body of the CSV file or if
	 * it doesnt exist, it will create it
	 * @param value String, the value of the counter.
	 * @param timeStamp String, timestamp that will be added to the file. 
	 */
	private void addtoBody(String value, String timeStamp){
		try {
			int index = 1;
			if (!VCI.equals("")) {
				index = Integer.parseInt(VCI);
			}
			String previousLine = lines.get(index);
			lines.set(index, previousLine.concat("," + value));
		} catch (IndexOutOfBoundsException e) {
			lines.add(timeStamp + "," + VCI + ","
					+ value);
		}
	}
	
	
	/**
	 * Method to parse the VCI from the MOID
	 * @param moid
	 */
	private void parseVCI(String moid) {
		if (moid.contains(fdn + ".")) {
			VCI = moid.substring(moid.lastIndexOf(".")+1, moid.length());
		}
	}
	
	/**
	 * Convert the milliseconds time into string format.
	 * @param milliseconds
	 * @return String in the format yyyy-MM-dd-HHmm
	 */
	private String makeTimeStamp(long milliseconds){
		Date date = new Date(milliseconds);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmm");
		return dateFormat.format(date);
	}
	
	
	/**
	 * The output file is created in memory before it is wrtten. File locks are used here
	 * so that the file will not be attempted to be fetched by SIM as it is being written.
	 * @param lines ArrayList<String> of the file contents
	 */
	private void writeFile(ArrayList<String> lines) {
		try {
			if(northBound == null || northBound.equals("")){
				northBound = "/nav/var/esm/pm/output/raw";
			}
			
			File dir = new File(northBound);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			File outputFile = new File(dir, getFilename());
			outputFile.createNewFile();
			
			logger.info(classname + ".run() Writing file: "
					+ outputFile.getAbsolutePath());

			final FileChannel fchannel = new FileInputStream(outputFile)
					.getChannel();
			FileLock lock = fchannel.lock(0L, Long.MAX_VALUE, true);

			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

			for (String s : lines) {
				out.write(s);
				out.newLine();
			}

			out.close();
			lock.release();
			fchannel.close();

		} catch (IOException e) {
			logger.error(classname + ".run() Error occured when writing the file", e);
		} catch (Exception e) {
			logger.error(classname + ".run() Error occured when writing the file", e);
		}

	}

	/**
	 * Generate the filename
	 * @return String, filename
	 */
	private String getFilename() {
		// <TimeStamp>_<Granularity>_<Nodename>_<Measurement>.csv
		Date date = new Date();
		StringBuffer filename = new StringBuffer();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmm");
		
		filename.append(file.getIpAddress());
		String hashcode = file.getFdn().hashCode()+"";
		if(!hashcode.startsWith("-")){
			hashcode = "-"+hashcode;
		}
		filename.append(hashcode + SEPERATOR);
		filename.append(dateFormat.format(date) + SEPERATOR);
		int rop = file.getListOfCounterSets().get(0).getGranularityPeriod()/60;
		filename.append(rop + SEPERATOR);
		filename.append(getNodeName() + SEPERATOR);
		String counterDefFilename = file.getCounterSetDefintion().getFileName();
		int end  = counterDefFilename.indexOf(".");
		counterDefFilename = counterDefFilename.substring(0, end);
		filename.append(counterDefFilename + ".csv");
		return filename.toString();
	}

	/**
	 * Get the node name from the fdn
	 * @return String, name of the node.
	 */
	private String getNodeName() {
		int start = fdn.indexOf(type + "=") + type.length() + 1;
		int stop;
		if ((stop = fdn.indexOf(",", start)) == -1)
			stop = fdn.length();

		return fdn.substring(start, stop);
	}
	
	

	public void addFileToWrite(SNMPCounterSetFile file){
		this.file = file;
	}
	
	
	public char isCompleted(){
		return completed; 
	}
}
