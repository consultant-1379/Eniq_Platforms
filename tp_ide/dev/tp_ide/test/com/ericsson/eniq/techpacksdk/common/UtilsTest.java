package com.ericsson.eniq.techpacksdk.common;

import com.ericsson.eniq.common.testutilities.DatabaseTestUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

public class UtilsTest extends TestCase {
	
	private final Properties conf = new Properties();
	private Properties confWithParsers;
	private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
	private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";
	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	private RockFactory rock = null;
	private RockFactory rock1 = null;
	private Statement stm;
	private static Map<String, String> env = System.getenv();
	
  @Test
  public void testStringToProperty() throws Exception {
	  final String str = getAction_contentsStr();
	  Properties p= new Properties();
	  p = Utils.stringToProperty(str);
	  assertFalse("Properties should not be empty for String:"+str,p.isEmpty());
  }

  @Test
  public void testGetParserFormatsSameAsConstants() throws Exception {
	Object[] list = Utils.getParserFormats();
	Object[] expected = getDefaultParserFormats(); //=Constants.PARSERFORMATS
	assertTrue("List did not match expected, list="+Arrays.toString(list)+", expected="+Arrays.toString(expected), Arrays.equals(list, expected));
  }
  
  @Test
  public void testGetParserFormatsAdditionalParser() throws Exception {
	Object[] list = Utils.getParserFormats(confWithParsers);
	Object[] expected = getParamaterParserFormats(); //=Constants.PARSERFORMATS
	assertTrue("List did not match expected, list="+Arrays.toString(list)+", expected="+Arrays.toString(expected), Arrays.equals(list, expected));
  }
  
  @Test
  public void testGetParserFormatPathKnown() {
	  String actual = Utils.getParserFormatPath("hxml", confWithParsers);
	  final String expected = "com.ericsson.eniq.etl.hxml.HXMLParser";
	  assertEquals(expected, actual);
  }
  
  @Test
  public void testGetParserFormatPathUnKnown() {
	  String actual = Utils.getParserFormatPath("unknown", conf);
	  final String expected = "unknown";
	  assertEquals(expected, actual);
  }
  
  @Test
  public void testExtractDigitsFromBHType(){
	  String extractDigitsFromMe = "CTP_PP993";
	  Integer actual = Utils.extractDigitsFromEndOfString(extractDigitsFromMe);
	  Integer expected = new Integer(993);
	  assertEquals(expected, actual);
  }

  @Test
  public void testExtractDigitsFromBHTypeWhereBHTypeisEmpty(){
	  String extractDigitsFromMe = "";
	  Integer actual = Utils.extractDigitsFromEndOfString(extractDigitsFromMe);
	  Integer expected = new Integer(0);
	  assertEquals(expected, actual);
  }

  @Test
  public void testExtractDigitsFromBHTypeWhereBHTypeisOld(){
	  String extractDigitsFromMe = "oldtype";
	  Integer actual = Utils.extractDigitsFromEndOfString(extractDigitsFromMe);
	  Integer expected = new Integer(0);
	  assertEquals(expected, actual);
  }

  private Object[] getDefaultParserFormats() {
	List<String> parserFormats = Arrays.asList(Constants.PARSERFORMATS);
	// Sort list
	Collections.sort(parserFormats);
	return parserFormats.toArray();
  }
  
  private Object[] getParamaterParserFormats() {
	List<String> parserFormats = new ArrayList<String>();
	parserFormats.addAll(Arrays.asList(Constants.PARSERFORMATS));
	parserFormats.add("hxml");
	parserFormats.add("sascii");
	// Sort list
	Collections.sort(parserFormats);
	return parserFormats.toArray();
  }
  
  public void testReplaceNull(){
	  // check for Integer
	  final Integer testIntObj = null ;
	  final Integer expInt = 0 ;
	  assertEquals(" replaceNull should return 0 for null Integer object. ",expInt, Utils.replaceNull(testIntObj));
	  
	  // check for String
	  final String testStrObj = null ;
	  final String expStr = "";
	  assertEquals(" replaceNull should return empty string for null String object. ",expStr, Utils.replaceNull(testStrObj));
	  
	  //check for Double
	  final Double testDobObj = null ;
	  final Double expDob = new Double(0) ;
	  assertEquals(" replaceNull should return 0 for null Integer object. ",expDob, Utils.replaceNull(testDobObj));
	  
	  //check for Long
	  final Long testLongObj = null ;
	  final Long expLong = new Long(0) ;
	  assertEquals(" replaceNull should return 0 for null Integer object. ",expLong, Utils.replaceNull(testLongObj));
  }
  
  @Test
  public void testIsEmpty(){
	  
	  //test for empty string
	  final String testString1 = "";
	  assertEquals(" It should return true for empty string. ",true, Utils.isEmpty(testString1));
	  
	  //test for non-empty string
	  final String testString2 = "String";
	  assertEquals(" It should return false for non-empty string. ",false, Utils.isEmpty(testString2));
  }
  
  @Test
  public void testStringToNumeric(){
	  final String testString = "786" ;
	  
	  // Test for String to Integer
	  final Integer expInt = new Integer(786);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + testString, expInt, Utils.stringToInteger(testString));
	  
	// Test for String to Long
	  final Long expLong = new Long(786);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + testString, expLong, Utils.stringToLong(testString));
  }
  
  @Test
  public void testBooleanIntegerConversion(){
	  // Test for Boolean to Integer
	  final Boolean testBool = new Boolean(true);
	  final Integer expInt = new Integer(1);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + expInt.toString(),expInt, Utils.booleanToInteger(testBool));
	  
	  final Boolean testBool1 = new Boolean(false);
	  final Integer expInt1 = new Integer(0);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + expInt1.toString(),expInt1, Utils.booleanToInteger(testBool1));
	  
	  
	  // Test for Integer to Boolean
	  final Integer testInt = new Integer(1);
	  final Boolean expBool = new Boolean(true);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + expBool.toString(),expBool, Utils.integerToBoolean(testInt));
	  
	  // Test for Integer to Boolean
	  final Integer testInt1 = new Integer(0);
	  final Boolean expBool1 = new Boolean(false);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + expBool1.toString(),expBool1, Utils.integerToBoolean(testInt1));
	  
	// Test for Integer to Boolean
	  final Integer testInt2 = new Integer(19);
	  final Boolean expBool2 = new Boolean(false);
	  assertEquals(" Actual Resut does not match with expected result. Expected value = " + expBool2.toString(),expBool2, Utils.integerToBoolean(testInt2));
  }
  
  @Test
  public void testGetSmallerAndLarger(){
	  final int smallInt = 10 ;
	  final int bigInt = 20 ;
	  assertEquals(" Actual result does not match with expected. Execpted = " + smallInt, smallInt, Utils.getSmaller(smallInt, bigInt));
	  assertEquals(" Actual result does not match with expected. Execpted = " + bigInt, bigInt, Utils.getLarger(smallInt, bigInt));
  }
  
  @Test
  public void testGetScheduleMaxID(){
	  try{
      DatabaseTestUtils.loadSetup(rock, "etlSetHandling");
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select max(id) maxval from META_SCHEDULINGS");
		  value.next();
		  long exp = value.getLong("maxval");
		  long actual = Utils.getScheduleMaxID(rock);
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, actual);
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testGetActionMaxID(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select max(TRANSFER_ACTION_ID) maxval from META_TRANSFER_ACTIONS");
		  value.next();
		  long exp = value.getLong("maxval");
		  long actual = Utils.getActionMaxID(rock);
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, actual);
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testGetSetMaxID(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select max(COLLECTION_ID) maxval from META_COLLECTIONS");
		  value.next();
		  long exp = value.getLong("maxval");
		  long actual = Utils.getSetMaxID(rock);
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, actual);
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testGetTPMaxID(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select max(COLLECTION_SET_ID) maxval from META_COLLECTION_SETS");
		  value.next();
		  long exp = value.getLong("maxval");
		  long actual = Utils.getTPMaxID(rock);
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, actual);
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testisSet(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select COLLECTION_NAME, VERSION_NUMBER, COLLECTION_SET_ID from Meta_collections");
		  value.next();
		  final long collID = value.getLong("COLLECTION_SET_ID");
		  final String collName = value.getString("COLLECTION_NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  final boolean expected = true ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected, expected, Utils.isSet(collName, collVerNum, collID, rock));
		  
		  //Testing Negative scenario
		  final String wrongCollName = "Wrong_COLLN_ID";
		  final boolean expected1 = false ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected1, expected1, Utils.isSet(wrongCollName, collVerNum, collID, rock));
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }	  
  }
  
  @Test
  public void testRemoveSet(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select COLLECTION_NAME, VERSION_NUMBER, COLLECTION_SET_ID from Meta_collections");
		  value.next();
		  final long collID = value.getLong("COLLECTION_SET_ID");
		  final String collName = value.getString("COLLECTION_NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  Utils.removeSet(collName, collVerNum, collID, rock);
		  ResultSet value1 = stm.executeQuery("select * from Meta_collections where COLLECTION_NAME='" + collName + "' AND VERSION_NUMBER='" + collVerNum + "' AND COLLECTION_SET_ID='" + collID + "'");
		  if(value1.next() == false){
			  //System.out.println("Set deleted Successfully.");
		  }else{
			  fail(" Set not Deleted Successfully.");
		  }
		  
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testGetSetId(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select COLLECTION_NAME, VERSION_NUMBER, COLLECTION_SET_ID, COLLECTION_ID from Meta_collections");
		  value.next();
		  final long collSetID = value.getLong("COLLECTION_SET_ID");
		  final String collName = value.getString("COLLECTION_NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  final long collID = value.getLong("COLLECTION_ID");
		  assertEquals("Actual result does not match with expected. Execpted = " + collID, collID, Utils.getSetId(collName, collVerNum, collSetID, rock));
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testIsAction(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select VERSION_NUMBER, COLLECTION_ID, COLLECTION_SET_ID, TRANSFER_ACTION_NAME from Meta_transfer_actions");
		  value.next();
		  final long collSetID = value.getLong("COLLECTION_SET_ID");
		  final long collID = value.getLong("COLLECTION_ID");
		  final String actionName = value.getString("TRANSFER_ACTION_NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  final boolean expected = true ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected, expected, Utils.isAction(actionName, collVerNum, collSetID, collID, rock));
		  
		  //Testing Negative scenario
		  final String wrongActionName = "Wrong_ACT_NAME";
		  final boolean expected1 = false ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected1, expected1, Utils.isAction(wrongActionName, collVerNum, collSetID, collID, rock));
		  c.close();
		  //System.out.println(" Expected = " + Utils.isAction("Aggregator_DC_E_RBS_DLBASEBANDPOOL_DAYBH_DLBASEBANDPOOL", "((117))", (long)30, (long)2201, rock));
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testRemoveAction(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select VERSION_NUMBER, COLLECTION_ID, COLLECTION_SET_ID, TRANSFER_ACTION_NAME from Meta_transfer_actions");
		  value.next();
		  final long collSetID = value.getLong("COLLECTION_SET_ID");
		  final long collID = value.getLong("COLLECTION_ID");
		  final String actionName = value.getString("TRANSFER_ACTION_NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  Utils.removeAction(actionName, collVerNum, collSetID, collID, rock);
		  ResultSet value1 = stm.executeQuery("select * from Meta_transfer_actions where TRANSFER_ACTION_NAME ='" + actionName + "' AND VERSION_NUMBER='" + collVerNum + "' AND COLLECTION_ID='" + collID + "' AND COLLECTION_SET_ID='" + collSetID + "'");
		  if(value1.next() == false){
			  //System.out.println("Set deleted Successfully.");
		  }else{
			  fail(" Action not Deleted Successfully.");
		  }
		  
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testIsScheduling(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select VERSION_NUMBER, COLLECTION_ID, COLLECTION_SET_ID, NAME from Meta_schedulings");
		  value.next();
		  final long collSetID = value.getLong("COLLECTION_SET_ID");
		  final long collID = value.getLong("COLLECTION_ID");
		  final String schName = value.getString("NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  final boolean expected = true ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected, expected, Utils.isScheduling(schName, collVerNum, collSetID, collID, rock));
		  
		  //Testing Negative scenario
		  final String wrongSchName = "Wrong_SCH_NAME";
		  final boolean expected1 = false ;
		  assertEquals(" Actual result does not match with expected. Execpted = " + expected1, expected1, Utils.isScheduling(wrongSchName, collVerNum, collSetID, collID, rock));
		  c.close();
		  //System.out.println(" Expected = " + Utils.isScheduling("Aggregator_DC_E_RBS_DLBASEBANDPOOL_DAYBH_DLBASEBANDPOOL", "((117))", (long)30, (long)2201, rock));
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testRemoveScheduling(){
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  ResultSet value = stm.executeQuery("select VERSION_NUMBER, COLLECTION_ID, COLLECTION_SET_ID, NAME from Meta_schedulings");
		  value.next();
		  final long collSetID = value.getLong("COLLECTION_SET_ID");
		  final long collID = value.getLong("COLLECTION_ID");
		  final String schName = value.getString("NAME");
		  final String collVerNum = value.getString("VERSION_NUMBER");
		  Utils.removeScheduling(schName, collVerNum, collSetID, collID, rock);
		  ResultSet value1 = stm.executeQuery("select * from Meta_schedulings where NAME ='" + schName + "' AND VERSION_NUMBER='" + collVerNum + "' AND COLLECTION_ID='" + collID + "' AND COLLECTION_SET_ID='" + collSetID + "'");
		  if(value1.next() == false){
			  //System.out.println("Set deleted Successfully.");
		  }else{
			  fail(" Scheduling not Deleted Successfully.");
		  }
		  
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testDeleteDir() throws IOException{
	  File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "TestDir");
	  tempDir.mkdir();
	  File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "TestDir" + File.separator + "testFile");
	  tempFile.createNewFile();
	  Utils.deleteDir(tempDir);
	  assertEquals("Directory deletion is not successful. ",false, tempDir.exists());
  }
  
  @Test
  public void testGetTechpackEniqLevel() throws Exception{
	  try{
		  Class.forName(TESTDB_DRIVER);
		  Connection c;
		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
		  stm = c.createStatement();
		  stm.execute("CREATE TABLE VERSIONING (VERSIONID VARCHAR(128), DESCRIPTION VARCHAR(50), STATUS NUMERIC(9), TECHPACK_NAME VARCHAR(30), TECHPACK_VERSION VARCHAR(32), TECHPACK_TYPE VARCHAR(10), PRODUCT_NUMBER VARCHAR(255), LOCKEDBY VARCHAR(255), LOCKDATE DATETIME, BASEDEFINITION VARCHAR(128), BASEVERSION VARCHAR(16), INSTALLDESCRIPTION VARCHAR(32000), UNIVERSENAME VARCHAR(30), UNIVERSEEXTENSION VARCHAR(16), ENIQ_LEVEL VARCHAR(12), LICENSENAME VARCHAR(255))");
		  stm.execute("insert into VERSIONING (VERSIONID, DESCRIPTION, STATUS, TECHPACK_NAME, TECHPACK_VERSION, TECHPACK_TYPE, PRODUCT_NUMBER, LOCKEDBY, LOCKDATE, BASEDEFINITION, INSTALLDESCRIPTION, ENIQ_LEVEL, LICENSENAME) values ('DC_E_MGW:((802))', 'Ericsson MGW', '1', 'DC_E_MGW', 'R3A', 'PM', 'COA 252 133/1', 'nuutti', '2009-12-16 13:51:54.0', 'TP_BASE:BASE_TP_090421', '', '11', 'CXC4010588')");
		  stm.execute("insert into VERSIONING (VERSIONID, DESCRIPTION, STATUS, TECHPACK_NAME, TECHPACK_VERSION, TECHPACK_TYPE, PRODUCT_NUMBER, LOCKEDBY, LOCKDATE, BASEDEFINITION, INSTALLDESCRIPTION, ENIQ_LEVEL, LICENSENAME) values ('DC_E_MGW:((803))', 'Ericsson MGW', '1', 'DC_E_MGW', 'R3A', 'PM', 'COA 252 133/1', 'Kenneth', '2009-12-16 13:51:54.0', 'TP_BASE:BASE_TP_090421', '', '11', 'CXC4010588')");
		  String exp = "11";
		  //System.out.println(" Result = " + Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((802))"));
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((802))"));
		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((803))"));
		  stm.close();
		  c.close();
	  }catch(final Exception e){
		  fail("Exception comes: " + e.getMessage());
	  }
  }
  
  @Test
  public void testRemoveFromStart(){
	  final String subString = "I AM START";
	  final String wholeString = "I AM START I AM MIDDLE. I AM LAST";
	  final String expString = " I AM MIDDLE. I AM LAST";
	  assertEquals(" Actual result does not match with expected. Execpted = " + expString, expString, Utils.removeFromStart(wholeString, subString));
  }
  
  // I have to ask this
//  @Test
//  public void testGetNextTransformationOrderNro(){
//	  try{
//		  Class.forName(TESTDB_DRIVER);
//		  Connection c;
//		  c = DriverManager.getConnection(DWHREP_URL, USERNAME, PASSWORD);
//		  stm = c.createStatement();
//		  stm.execute("CREATE TABLE TRANSFORMATION (TRANSFORMERID VARCHAR(255), ORDERNO NUMERIC(9), TYPE VARCHAR(128), SOURCE VARCHAR(128), TARGET VARCHAR(128), CONFIG VARCHAR(32000), DESCRIPTION VARCHAR(32000))");
//		  stm.execute("insert into TRANSFORMATION (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('DIM_E_GRAN:((9)):ALL:ascii', '1', 'lookup', 'DIRNAME', 'lastDIR', 'pattern=.+/.+/.+/.+/(.+)')");
//		  stm.execute("insert into TRANSFORMATION (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('DIM_E_GRAN:((9)):DIM_E_GRAN_TS_AS:ascii', '2', 'lookup', 'targetFDN', 'targetFDN.SubNetwork2', 'pattern=.+?,.+?=(.+?),.*')");
//		  stm.execute("insert into TRANSFORMATION (TRANSFORMERID, ORDERNO, TYPE, SOURCE, TARGET, CONFIG) values ('DC_E_STN:((6)):ALL:mdc', '3', 'propertyTokenizer', 'MOID', 'MOID', 'delim=,')");
//		  System.out.println(" Result = " + Utils.getNextTransformationOrderNro(1, rock));
////		  String exp = "11";
////		  //System.out.println(" Result = " + Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((802))"));
////		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((802))"));
////		  assertEquals(" Actual result does not match with expected. Execpted = " + exp, exp, Utils.getTechpackEniqLevel(rock, "DC_E_MGW:((803))"));
//		  stm.close();
//		  c.close();
//	  }catch(final Exception e){
//		  fail("Exception comes: " + e.getMessage());
//	  }
//  }
  
  @Override
  @Before
  public void setUp() throws Exception {
	  confWithParsers = new Properties();
	  confWithParsers.setProperty("parserType.hxml", "com.ericsson.eniq.etl.hxml.HXMLParser");
	  confWithParsers.setProperty("parserType.sascii", "com.ericsson.eniq.etl.siemens.SASCIIParser");
	  rock = new RockFactory(DWHREP_URL, USERNAME, PASSWORD, TESTDB_DRIVER, "test", true); 
	  rock1 = new RockFactory(DWHREP_URL, USERNAME, PASSWORD, TESTDB_DRIVER, "test", true);
  }

  @Override
  @After
  public void tearDown() throws Exception {
  }
  
  private static String getAction_contentsStr() {
		String result =  "#"+"\n";
		result+="#Thu Jul 15 00:14:20 BST 2010"+"\n";
		result+="outDir=${ETLDATA_DIR}/adapter_tmp//"+"\n";
		result+="maxFilesPerRun=32765"+"\n";
		result+="dublicateCheck=true"+"\n";
		result+="thresholdMethod=more"+"\n";
		result+="inDir=${PMDATA_DIR}/${OSS}//"+"\n";
		result+="interfaceName=1"+"\n";
		result+="ProcessedFiles.fileNameFormat="+"\n";
		result+="minFileAge=1"+"\n";
		result+="baseDir=${ARCHIVE_DIR}/${OSS}//"+"\n";
		result+="archivePeriod=168"+"\n";
		result+="useZip=false"+"\n";
		result+="loaderDir=${ETLDATA_DIR}"+"\n";
		result+="parserType=3gpp32435"+"\n";
		result+="doubleCheckAction=delete"+"\n";
		result+="ProcessedFiles.processedDir=${ARCHIVE_DIR}/${OSS}//processed/"+"\n";
		result+="failedAction=move"+"\n";
		result+="dirThreshold=0"+"\n";
		result+="workers=3"+"\n";
		result+="afterParseAction=delete"+"\n";
		return result;
  }
  
}
