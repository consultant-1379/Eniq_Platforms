package com.distocraft.dc5000.etl.importexport;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvExporterTest {

  private static final String QUERY_TIME = "2011-01-19 10:56:30.374";

  private static final String SYSTEM_USER_ID = "EricSon";

  private static final String IP_ADDRESS = "1.2.3.4";

  private static final String SERVICE_INFO = "NETWORK/RANKING_ANALYSIS";

  private static final String DATE_FROM = "18012011";

  private static final String TIME_FROM = "0100";

  private static final String DATE_TO = "19012011";

  private static final String TIME_TO = "0145";

  private static final String INTERVAL = "30";

  private static final String QUERYTARGET = "imsi=460000057180826";

  private static final String COLUMN_SEPERATOR = ",";

  private static final String ROW_SEPERATOR = "\n";

  private CsvExporter csvExporter = null;

  private final Properties csvExport = new Properties();

  @Before
  public void setUp() throws Exception {

    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.record.header",
        "QUERYTIME,SYSTEMUSERID,IPADDRESS,SERVICEINFO,QUERYTARGET,DATEFROM,TIMEFROM,DATETO,TIMETO,INTERVAL,");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.QUERYTIME.pattern",
        "^([0-9]+[-][0-9]+[-][0-9]+[ ][0-9]+[:][0-9]+[:][0-9]+[.][0-9]+)[;].+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.SYSTEMUSERID.pattern", ".+userId=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.IPADDRESS.pattern", ".+srcIpAddress=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.SERVICEINFO.pattern",
        ".+http[^/]+[/]+[^/]+[/]+[^/]+[/]+([^\\?]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.QUERYTARGET.pattern",
        ".+(imsi=[^\\]&]*|groupname=[^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.DATEFROM.pattern", ".+dateFrom=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.TIMEFROM.pattern", ".+timeFrom=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.DATETO.pattern", ".+dateTo=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.TIMETO.pattern", ".+timeTo=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + ".out.INTERVAL.pattern", ".+time=([^\\]&]*).+");
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + "out.col.seperator", COLUMN_SEPERATOR);
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + "out.row.seperator", ROW_SEPERATOR);
    csvExport.setProperty(CsvExporter.CSV_PROP_SRC + "in.LOG.pattern", ".+(\\.log).*");

    CsvExporter.setCsvProperties(csvExport);

    csvExporter = new CsvExporter();
  }

  @Test
  public void testLogPattern() {
    assertTrue("This is not a correct log file", CsvExporter.isFileNameCorrect("servicesaudit-2011_01_26.log.12"));
    assertTrue("This is not a correct log file", CsvExporter.isFileNameCorrect("servicesaudit-2011_01_26.log"));
    assertTrue("This is not a correct log file", CsvExporter.isFileNameCorrect("servicesaudit-2011_01_26.log.ABC"));
    assertFalse("Log name is incorrect. This should be false.", CsvExporter.isFileNameCorrect("INCORRECT_LOG_NAME"));
  }
  
  @After
  public void tearDown() throws Exception {
    csvExporter = null;
    csvExport.clear();
  }

  @Test
  public void checkThatExceptionIsThrownWhenPropertyUnDefined() {
    try {
      csvExport.clear();
      csvExporter = new CsvExporter();
      fail("Should not have got here..");
    } catch (Exception e) {
      assertEquals("Property csvexport.out.QUERYTIME.pattern is undefined", e.getMessage());
    }
  }

  @Test
  public void checkThatCsvRowIsReturnedCorrectly() {
    String sampleLineFromLogFile = QUERY_TIME + ";14;INFO;"
        + "URI<http://atc3000bl3.athtem.eei.ericsson.se:18080/EniqEventsServices_eriwals/" + SERVICE_INFO + "?"
        + "time=" + INTERVAL + "&display=grid&type=IMSI&" + QUERYTARGET + "&dateFrom=" + DATE_FROM + "&dateTo="
        + DATE_TO + "&timeFrom=" + TIME_FROM + "&timeTo=" + TIME_TO + "&tzOffset=+0000&maxRows=50&userId="
        + SYSTEM_USER_ID + "&srcIpAddress=" + IP_ADDRESS + "]; ";

    String csvRow = csvExporter.getCsvDataFromRow(sampleLineFromLogFile);
    assertEquals(QUERY_TIME + COLUMN_SEPERATOR + SYSTEM_USER_ID + COLUMN_SEPERATOR + IP_ADDRESS + COLUMN_SEPERATOR
        + SERVICE_INFO + COLUMN_SEPERATOR + QUERYTARGET + COLUMN_SEPERATOR + DATE_FROM + COLUMN_SEPERATOR + TIME_FROM
        + COLUMN_SEPERATOR + DATE_TO + COLUMN_SEPERATOR + TIME_TO + COLUMN_SEPERATOR + INTERVAL
        + COLUMN_SEPERATOR + ROW_SEPERATOR, csvRow);
  }

  @Test
  public void checkThatCsvRowIsReturnedCorrectlyWhenDatesMissing() {
    String sampleLineFromLogFile = QUERY_TIME + ";14;INFO;"
        + "URI<http://atc3000bl3.athtem.eei.ericsson.se:18080/EniqEventsServices_eriwals/" + SERVICE_INFO + "?"
        + "time=" + INTERVAL + "&display=grid&type=IMSI&" + QUERYTARGET + "&tzOffset=+0000&maxRows=50&userId="
        + SYSTEM_USER_ID + "&srcIpAddress=" + IP_ADDRESS + "]; ";

    String csvRow = csvExporter.getCsvDataFromRow(sampleLineFromLogFile);
    assertEquals(QUERY_TIME + COLUMN_SEPERATOR + SYSTEM_USER_ID + COLUMN_SEPERATOR + IP_ADDRESS + COLUMN_SEPERATOR
        + SERVICE_INFO + COLUMN_SEPERATOR + QUERYTARGET + COLUMN_SEPERATOR + COLUMN_SEPERATOR + COLUMN_SEPERATOR
        + COLUMN_SEPERATOR + COLUMN_SEPERATOR + INTERVAL + COLUMN_SEPERATOR
        + ROW_SEPERATOR, csvRow);
  }
  
  @Test
  public void checkThatCsvGetDataFromFile() throws FileNotFoundException, IOException, URISyntaxException{
      File sampleLogFile = getDataFile("testLogFiles/servicesaudit-yyyy_mm_dd.log");
      
      if (sampleLogFile != null && sampleLogFile.exists() && sampleLogFile.isFile() && sampleLogFile.canWrite()) {
      String csvData = csvExporter.getCsvDataFromFile(sampleLogFile);
      String resultData = getContents(getDataFile("testLogFiles/ResultFileData.txt"));
      assertEquals(csvData, resultData);
      }
  }
  
  private File getDataFile(final String uri) throws URISyntaxException {
      final URL url = ClassLoader.getSystemResource(uri);
      assertNotNull("Couldnt load test data", url);
      return new File(url.toURI().getRawPath());
  }
  
  public String getContents(File aFile) throws IOException {
      StringBuilder contents = new StringBuilder();
      BufferedReader input =  new BufferedReader(new FileReader(aFile));
      String line = null; 
      while (( line = input.readLine()) != null){
          contents.append(line);
          contents.append("\n");
          }
      input.close();
      return contents.toString();
    }
}
