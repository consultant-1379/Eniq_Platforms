/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.importexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distocraft.dc5000.etl.importexport.gpmgt.exception.GroupMgtException;

/**
 * Used to generate a CSV export file from an input log file(s)
 * 
 * @author epaujor
 * 
 */
public class CsvExporter {

  private final Properties csvExportProps;

  private static File[] filterFilesInLogsDir = new File[0];

  private final String csvHeaders;

  private final String outputColSeperator;

  private final String outputRowSeperator;

  private final String inputRowSeperator;

  private final String filterlevel;
  
  private static String logFileNamePattern;

  private final List<String> patternsForCols;

  /**
   * Logger instance
   */
  private static final Logger LOGGER = Logger.getLogger("csv.export");

  /**
   * Used with JUnits to set specific properties/
   */
  private static Properties testCsvProps = null;

  /**
   * CSV Export properties source used in the common utilities Properties helper
   */
  public static final String CSV_PROP_SRC = "csvexport";

  public CsvExporter() {
    csvExportProps = getCsvExportProperties();
    if (csvExportProps.isEmpty()) {
      log("No Properties Loaded, Using Default.", Level.WARNING);
    }

    csvHeaders = csvExportProps.getProperty("out.record.header",
        "QUERYTIME,SYSTEMUSERID,IPADDRESS,SERVICEINFO,QUERYTARGET,DATEFROM,TIMEFROM,DATETO,TIMETO,INTERVAL,");
    outputColSeperator = csvExportProps.getProperty("out.col.seperator", ",");
    outputRowSeperator = csvExportProps.getProperty("out.row.seperator", "\n");
    inputRowSeperator = csvExportProps.getProperty("in.row.seperator", "];");
    filterlevel = csvExportProps.getProperty("in.filterlevel", "INFO");
    logFileNamePattern = csvExportProps.getProperty("in.LOG.pattern", ".+(\\.log).*");
    
    patternsForCols = getPatternsForCols();
  }

  private List<String> getPatternsForCols() {
    final List<String> patternsForColumns = new ArrayList<String>();
    final StringTokenizer tokens = new StringTokenizer(csvHeaders, ",");

    while (tokens.hasMoreTokens()) {
      final String csvHeader = tokens.nextToken();
      final String csvPattern = csvExportProps.getProperty("out." + csvHeader + ".pattern");
      patternsForColumns.add(csvPattern);
    }
    return patternsForColumns;
  }

  public static void main(final String[] args) throws IOException {
    final CsvExporter cvsExporter = new CsvExporter();

    final String inputLogsName = GroupMgtHelper.getArgValue(args, "-f");

    final ExitCodes code = checkFileArgs(inputLogsName);

    if (code != ExitCodes.EXIT_OK) {
        GroupMgtHelper.exitSystem(code);
    }

    final String outputCsvFileName = GroupMgtHelper.getArgValue(args, "-o");
    if (outputCsvFileName == null) {
      GroupMgtHelper.exitSystem(ExitCodes.EXIT_NO_OUTPUT_FILE);
    }

    final File outputCsvFile = new File(outputCsvFileName);
    final PrintWriter print = new PrintWriter(new FileWriter(outputCsvFile));
    
    for (File file : filterFilesInLogsDir) {
      try {
        log("Retrieving CSV data from log file: " + file + ".....", Level.INFO);
        final String csvDataFromFile = cvsExporter.getCsvDataFromFile(file);
        print.write(csvDataFromFile);
        log("Writing retrieved CSV data from " + file + " to " + outputCsvFileName, Level.INFO);
      } catch (IOException e) {
        log("Could not read the log file " + file + ". Please check that this file exists and is readable.",
            Level.CONFIG);
      } finally {
        print.close();
      }
    }
    log("Finished retrieving CSV data. All CSV data is present in file" + outputCsvFileName, Level.INFO);
  }

  protected String getCsvDataFromFile(final File file) throws IOException {
    final BufferedReader br = new BufferedReader(new FileReader(file));
    String line;
    final StringBuilder builder = new StringBuilder();

    while ((line = br.readLine()) != null) {
      final Pattern p = Pattern.compile(patternsForCols.get(0) + ".+" + filterlevel);
      final Matcher match = p.matcher(line);
      
      if (match.find()) {
        if (match.group(1) != null) {
          // line in log file could be spilt over several line
          // Append these together and look for special end of line character(s)
          while (!line.endsWith(inputRowSeperator)) {
            line = line + br.readLine();
          }
          builder.append(getCsvDataFromRow(line));
          line=null;
        }
      }
    }
    return builder.toString();
  }

  protected String getCsvDataFromRow(final String line) {
    final StringBuilder builder = new StringBuilder();

    for (String csvPattern : patternsForCols) {
      final Pattern pattern = Pattern.compile(csvPattern);
      final Matcher matcher = pattern.matcher(line);

      if (matcher.find()) {
        builder.append(matcher.group(1));
      }
      builder.append(outputColSeperator);
    }
    builder.append(outputRowSeperator);

    return builder.toString();
  }

  /**
   * Utility method to get the csvexport properties
   * 
   * @return Properties object containing csvexport properties
   */
  private Properties getCsvExportProperties() {
    com.distocraft.dc5000.common.Properties toReturn;
    try {
      if (testCsvProps == null) {
        toReturn = new com.distocraft.dc5000.common.Properties(CSV_PROP_SRC);
      } else {
        toReturn = new com.distocraft.dc5000.common.Properties(CSV_PROP_SRC, new Hashtable(), testCsvProps);
      }
    } catch (Exception e) {
      throw new GroupMgtException("Failed to get Properties", e);
    }
    return toReturn;
  }

  /**
   * Test method to override the default group mgt properties
   * 
   * @param testProps
   *          The new set of etlc properties
   */
  static void setCsvProperties(final Properties testProps) {
    testCsvProps = testProps;
  }

  /**
   * Log a message
   * 
   * @param msg
   *          What to log
   * @param lvl
   *          Level to log it at
   */
  private static void log(final String msg, final Level lvl) {
    LOGGER.log(lvl, msg);
  }

  static ExitCodes checkFileArgs(final String inputLogsName) {
    ExitCodes exitCode = ExitCodes.EXIT_OK;
    if (inputLogsName == null) {
      exitCode = ExitCodes.EXIT_NO_FILE;
    } else {
      filterFilesInLogsDir = getFilterFilesInLogsDir(inputLogsName);

      if (filterFilesInLogsDir.length == 0) {
        exitCode = ExitCodes.EXIT_INVALID_EXT;
      }
    }

    return exitCode;
  }
  
  protected static boolean isFileNameCorrect(String fileName) {
    final Pattern p = Pattern.compile(logFileNamePattern);
    final Matcher match = p.matcher(fileName);
    if (match.find()) {
      return true;
    }
    return false;
  }
  
  private static File[] getFilterFilesInLogsDir(final String inputLogsName) {
    File[] filteredFiles = new File[0];
    final File inputLogs = new File(inputLogsName);
    if (inputLogs.exists()) {
      if (inputLogs.isDirectory()) {
        filteredFiles = getFilteredFiles(inputLogs);
      } else if (isFileNameCorrect(inputLogsName)) {
        filteredFiles = new File[] { inputLogs };
      }
    }
    return filteredFiles;
  }

  private static File[] getFilteredFiles(final File logsDir) {
    final File[] filterFiles = logsDir.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String fileName) {
        return isFileNameCorrect(fileName);
      }
    });
    return filterFiles;
  }
}
