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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
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
 * @author ebrifol
 * 
 */

public class CsvFileParser implements CounterSetFileParser {

	private final SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
	private final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	private final TimeZone timeZone = (new GregorianCalendar()).getTimeZone();

	private String moidString = "";
	private String headerParsed = null;
	private String lastParsedLine = "";
	private static final String classname = CsvFileParser.class.getName();
	private Pattern fileNamePattern = null;
	CounterSetCallback parseCallback;

	public static final Logger logger = Logger.getLogger("CsvFileParser");
	/**
	 * Constructor
	 */
	public CsvFileParser() {
		formatter1.setTimeZone(timeZone);
		formatter2.setTimeZone(timeZone);
	}

	/**
	 * Parsing the date to generate the time stamp for the collection.
	 * 
	 * @param dateString
	 * @return
	 */
	private String parseDate(final String lineString) {
		
		int start = lineString.lastIndexOf("-")+1;
		int end = lineString.indexOf(",");
		String timeString = lineString.substring(start, end);
		
		return timeString;
			}



	@Override
	public File parseFile(final String fdn, final String filePath, final CounterSetCallback callback, boolean doLookup)
	        throws ParserException {

		try {
			parseCallback = callback;

			callback.debug(".parse; Parsing file " + filePath);
			//resetParsedStrings();
			final String datafilePath = filePath.substring(0, filePath.lastIndexOf("/"));
			final String datafileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			//moidString = createMoid(datafileName);			//commented out because the moidString is not needed and the createMoid() has hardcoded values. Code left intact in case it is needed for another type of appended file
			//nednString = fdn;
			
			readLastParsedLine(filePath);
			
			final List<String> parsedLines = readInputFile(filePath);
			if (parsedLines.isEmpty()) {
				throw new ParserException("The File contains no data: " + filePath);
			}
			
			File newData = null;
			if(parsedLines.size() > 1){
				headerParsed = parsedLines.get(0);
				String time = parseDate(parsedLines.get(1));
				
				String filename = datafileName.replace("0000", time);
				
				newData = new File(datafilePath, filename);
				newData.createNewFile();
				
				FileWriter fstream = new FileWriter(newData);
				BufferedWriter out = new BufferedWriter(fstream);
				
				out.write(headerParsed+"\n");
				
				for (int i = 1; i < parsedLines.size(); i++) {
					String line = parsedLines.get(i);
					out.write(line+"\n");
					lastParsedLine = line;
				}
				
				out.close();
			}
			writeLastParsedLine(lastParsedLine, filePath);
			
			return newData;
			
		} catch (final Exception e) {
			e.printStackTrace();
			throw new ParserException("", e);
		} finally {
			headerParsed = null;
			lastParsedLine = "";
		}
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
		//flag = getmatchingFiles(filePath, dir); 			//commented out because this method never actually returns anything other than true. Code left intact in case it is needed for another type of appended file

		if (updateLastParsedLine.equalsIgnoreCase("Not parsed data from this file.")) {
			flag = false;
		}
		if (flag) {
			topFile = new File(filePath + ".tmp");
			if(topFile.exists()){
				topFile.delete();
			}
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
			if(lastParsedLine == null){
				lastParsedLine = "";
			}
		} else {
			lastParsedLine = "";
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
				if(!line.equals(lastParsedLine)){
					linesToBeParsed.add(line);
				}
			} else if(lastParsedLine.equals("")){
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
		return "ProCus, SIM product development team";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Comma Seperated Parser";
	}
	
	/**
	 * Identifying the component from file name.
	 * 
	 * @param datafileName
	 * @return
	 */
	
	/**************************************************************************************************************************
		If this method is used again, the hardcoded values should be removed so that this parser will work for any appended file
		Currently this method is not needed
	****************************************************************************************************************************/
	
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
	 * Get the matching files to delete.
	 * 
	 * @param filename
	 *            , directory
	 * @return boolean value
	 * 
	 */
	
	/**************************************************************************************************************************
		Currently this method is not needed as the regular expression it creates does not 
		apture any files and will always return true
	****************************************************************************************************************************/
	
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
