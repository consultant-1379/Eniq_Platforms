package com.ericsson.navigator.esm.manager.pm.file.remote.plugin.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;

/**
 * To parse the comma separated files to process the counters for SDP, AIR and VS nodes.
 * 
 * @author qbhasha
 * 
 */

public class CsvCounterSetFileParser implements CounterSetFileParser {

	private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
	private final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final TimeZone timeZone = (new GregorianCalendar()).getTimeZone();
	private Map<String, String> observedCounters;

	private List<String> counterNames = new ArrayList<String>();
	private int counterPos = 0;
	private String nednString = "";
	private String moidString = "";
	private String mtsString = null;
	private String headerParsed = null;
	private String lastParsedLine = "";
	private static final String classname = CsvCounterSetFileParser.class.getName();
	private Pattern fileNamePattern = null;
	CounterSetCallback parseCallback;
	private boolean idFlag = false;
	private int idLoc = 0;
	private String dyMoidString = "";
	public static final Logger logger = Logger.getLogger("CsvCounterSetFileParser");
	/**
	 * Constructor
	 */
	public CsvCounterSetFileParser() {
		formatter1.setTimeZone(timeZone);
		formatter2.setTimeZone(timeZone);
	}

	/**
	 * Resetting the global variables for each run.
	 */
	void resetParsedStrings() {
		nednString = "";
		moidString = "";
		mtsString = null;
		idFlag = false;
		idLoc = 0;
		dyMoidString = "";
	}

	/**
	 * to mark the beginning of processing of one set of counters. One line in csv file.
	 * 
	 * @param nsURI
	 */
	private void startElement(final String cntNames) {
		counterNames = new ArrayList<String>();
		observedCounters = new HashMap<String, String>();
		processCounterNames(cntNames);
		return;
	}

	/**
	 * to mark the processing of one set of counters. One line in csv file.
	 */
	private void endElement() {
		addCounterSet(parseDate(mtsString));
		return;
	}

	/**
	 * adding the counter after reading from CSV file.
	 * 
	 * @param pos
	 * @param value
	 */
	private void addObservedCounter(final int pos, String value) {
		if (value == "") {
			value = "null";
		}
		if (pos < counterNames.size()) {
			observedCounters.put(counterNames.get(pos), value);
		} else {
			parseCallback.warn(".addObservedCounter(); More values than counter names found!");
		}
	}

	/**
	 * Parsing the date to generate the time stamp for the collection.
	 * 
	 * @param dateString
	 * @return
	 */
	private long parseDate(final String dateString) {
		long endTime = System.currentTimeMillis();
		// Case 1: (+ or -) TIMEZONE

		String date = dateString;
		if (dateString.indexOf("-") > -1 && dateString.indexOf("-") != dateString.lastIndexOf("-")) {
			try {
				// yyyy-MM-dd-HHmm
				date = dateString.replaceAll("-", "").trim();
				endTime = formatter1.parse(date).getTime();
				return endTime;
			} catch (final Exception e) {
				parseCallback.error(".parseDate(); Failed to parse date current time will be used.", e);
			}
		}

		try {
			endTime = 1000L * Long.parseLong(date);
			return endTime;
		} catch (final Exception e) {
			parseCallback.debug(".parseDate; Time format used for mts utime/EPOCH", e);
		}
		parseCallback.warn(".parseDate; Time format used is not parseable:" + date);
		return 0;
	}

	private void addCounterSet(long endTime) {
		if (endTime > System.currentTimeMillis()) { // TR-HM51919
			endTime = System.currentTimeMillis();
		}
		parseCallback.pushCounterSet(createMoid(nednString, moidString), new Date(endTime), observedCounters);
	}

	/**
	 * Generating the MOID to be used by ESM for data storage.
	 * 
	 * @param nedn
	 * @param moid
	 * @return
	 */
	private String createMoid(final String nedn, final String moid) {

		String nednComma = nedn;
		final String moidId = moid + dyMoidString;
		if (!nedn.isEmpty() && !moid.isEmpty()) {
			nednComma = nedn + ",";
		}
		if ((nedn + moid).isEmpty()) {
			nednComma = "";
		}
		return nednComma + moidId;
	}

	@Override
	public File parseFile(final String fdn, final String filePath, final CounterSetCallback callback, boolean doLookup)
	        throws ParserException {

		try {
			parseCallback = callback;

			callback.debug(".parse; Parsing file " + filePath);
			resetParsedStrings();

			final String datafileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			moidString = createMoid(datafileName);
			nednString = fdn;

			readLastParsedLine(filePath);
			
			final List<String> parsedLines = readInputFile(filePath);
			if (parsedLines.isEmpty()) {
				throw new ParserException("The File contains no data: " + filePath);
			}

			headerParsed = parsedLines.get(0);

			for (int i = 1; i < parsedLines.size(); i++) {
				final String line = parsedLines.get(i);
				if (!(line.isEmpty() || line.equals(""))) {
					startElement(headerParsed);
					processCounterValues(line);
					endElement();
					counterPos = 0;
					idFlag = false;
					idLoc = 0;
					lastParsedLine = parsedLines.get(i);
				}
			}
			writeLastParsedLine(lastParsedLine, filePath);
		} catch (final Exception e) {
			throw new ParserException("", e);
		} finally {
			headerParsed = null;
			lastParsedLine = "";
		}
		return null;
	}

	/**
	 * Storing the last parsed line.
	 * 
	 * @param lastParsedLine2
	 * @param filePath
	 * @throws IOException
	 */
	private void writeLastParsedLine(final String updateLastParsedLine, final String filePath) throws IOException {
		boolean flag = true;

		File topFile = new File(filePath);
		final String dir = filePath.substring(0, filePath.lastIndexOf(File.separator));
		flag = getmatchingFiles(filePath, dir);

		if (updateLastParsedLine.equalsIgnoreCase("Not parsed data from this file.")) {
			flag = false;
		}
		if (flag) {
			topFile = new File(filePath + ".tmp");
			topFile.createNewFile();
			// TR HL96408 - 0 byte .tmp files in /nav/var/esm/pm/remote/input directory
			BufferedWriter top = null;
			try {
				top = new BufferedWriter(new FileWriter(topFile, true));
				top.write(updateLastParsedLine);
				parseCallback.debug(classname + "writeLastParsedLine(); writing updateLastParsedLine");
			} finally {
				if (top != null) {
					top.close();
				}
			}
		}
	}

	/**
	 * Reading the last parsed line.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	private void readLastParsedLine(final String filePath) throws IOException {
		final File topFile = new File(filePath + ".tmp");
		if (topFile.exists()) {
			final BufferedReader top = new BufferedReader(new FileReader(topFile));
			lastParsedLine = top.readLine();
		} else {
			lastParsedLine = "Not parsed data from this file.";
		}
	}

	/**
	 * Identifying the component from file name.
	 * 
	 * @param datafileName
	 * @return
	 */
	private String createMoid(final String datafileName) {
		String moid = "";
		String tempmoid = "";
		if (datafileName.indexOf("_PSC") > -1) {
			tempmoid = datafileName.substring(datafileName.indexOf("PSC"));
			tempmoid = tempmoid.substring(0, tempmoid.indexOf("_"));
			if (datafileName.indexOf("System") > -1) {
				moid = tempmoid + "_System";
			} else if (datafileName.indexOf("ServiceClass") > -1) {
				moid = tempmoid + "_ServiceClass";
			}else {
				moid = tempmoid;
			}
		} else if ((datafileName.indexOf("AUV") > -1)) {
			tempmoid = datafileName.substring(datafileName.indexOf("AUV"));
			moid = tempmoid.substring(0, tempmoid.indexOf("_"));
		} else if (datafileName.indexOf("FSC") > -1) {
			tempmoid = datafileName.substring(datafileName.indexOf("FSC"));
			moid = tempmoid.substring(0, tempmoid.indexOf("_"));
		}
		return "Component=" + moid;
	}

	/**
	 * Processing the counter values
	 * 
	 * @param line
	 */
	private void processCounterValues(final String line) {
		if (line.startsWith("DATE") || line.startsWith("Date") || line.startsWith("date")) {
			return;
		}
		if (line.startsWith(",")) {
			parseCallback.warn("ERRORNUOUS DATA RECORD present in the file, Processing couldnot be done. ");
			return;
		}
		final String[] value = line.split(",");
		mtsString = value[0];
		if (idFlag) {
			dyMoidString = "_is_" + value[idLoc] + "_ie_" ;
		}
		for (int i = 1; i < value.length; i++) {
			addObservedCounter(counterPos, value[i]);
			counterPos++;
		}
	}

	/**
	 * Processing the counter names
	 * 
	 * @param line
	 */
	private void processCounterNames(final String line) {
		final String[] value = line.split(",");
		dyMoidString = "";
		for (int i = 1; i < value.length; i++) {
			if (value[i].equalsIgnoreCase("ID")) {
				idFlag = true;
				idLoc = i;
			}
			counterNames.add(value[i]);
		}
	}

	/**
	 * Reading the template file to generate the CounterSet Definition.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws ParserException
	 * 
	 */

	private List<String> readInputFile(final String filename) throws IOException, ParserException {
		final ArrayList<String> lines = new ArrayList<String>();
		final ArrayList<String> linesToBeParsed = new ArrayList<String>();
		final File topFile = new File(filename);
		if (topFile.length() == 0) {
			logger.error("Retrieved 0 byte file, Processing could not be done");
			return lines;
			//throw new ParserException("Retrieved 0 byte file, Processing couldnot be done. "); 
		}
		final BufferedReader top = new BufferedReader(new FileReader(topFile));
		boolean isParsed = false;

		// moidString = filename.substring(0, filename.indexOf("-") - 1);
		String line = top.readLine();
		if (line != null) {
			linesToBeParsed.add(line.trim());
			lines.add(line.trim());
			line = top.readLine();
		}
		while (line != null) {
			if (line.equals(lastParsedLine) || isParsed) {
				isParsed = true;
				linesToBeParsed.add(line);
			} else {
				lines.add(line.trim());
			}
			line = top.readLine();
		}
		isParsed = false;
		if (linesToBeParsed.size() > 1) {
			return linesToBeParsed;
		}
		return lines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser#getContactInformation()
	 */
	@Override
	public String getContactInformation() {
		return "Navigator, ESM product development team";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Default Comma Seperated Counter Set Parser";
	}

	/**
	 * Get the matching files to delete.
	 * 
	 * @param filename
	 *            , directory
	 * @return boolean value
	 * 
	 */
	private boolean getmatchingFiles(final String filepath, final String dirFile) {
		boolean flag = true;
		final StringTokenizer st = new StringTokenizer(filepath, "_");
		final StringBuilder tempmoidString = new StringBuilder(moidString);

		tempmoidString.replace(0, tempmoidString.length(), tempmoidString.substring(tempmoidString.indexOf("=") + 1));
		if (tempmoidString.indexOf("_") > 0) {
			tempmoidString.delete(tempmoidString.indexOf("_"), tempmoidString.length());
		}
		final StringBuilder fileproper = new StringBuilder();
		if (st.hasMoreTokens()) {
			final StringBuilder host = new StringBuilder(st.nextToken());
			fileproper.insert(0, ".*");
			fileproper.append(host.substring(host.lastIndexOf(File.separator) + 1, host.lastIndexOf("-")));

		}
		final String component = st.nextToken();
		fileproper.append(".*" + component);

		if (!tempmoidString.toString().contains(component) && filepath.contains(tempmoidString)) {
			fileproper.append(".*" + tempmoidString);
		}

		fileproper.append("*.*.tmp");
		this.fileNamePattern = Pattern.compile(fileproper.toString());

		final String[] dirlist = getListOfmachingFilesTodelete(dirFile);

		for (final String fileToDelete : dirlist) {

			flag = delete(dirFile, fileToDelete);
		}

		return flag;
	}

	/**
	 * Get the matching files to delete.
	 * 
	 * @param directory
	 * @return list of files matching in the directory
	 * 
	 */
	String[] getListOfmachingFilesTodelete(final String aDirectory) {
		// our filename filter (filename pattern matcher)
		class PatternFilter implements FilenameFilter {
			public boolean accept(final File dir, final String file) {
				if (fileNamePattern.matcher(file).matches()) {
					return true;
				}
				return false;
			}
		}
		return new java.io.File(aDirectory).list(new PatternFilter());
	}

	/**
	 * Delete and return the boolean value .
	 * 
	 * @param file
	 *            ,directory
	 * @return
	 * 
	 */
	boolean delete(final String dirFile, final String fileTodelete) {
		return new File(dirFile, fileTodelete).delete();
	}

	@Override
	public String getDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
