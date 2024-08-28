/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.jmock.Expectations;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.junit.HelpClass;
import org.hsqldb.jdbc.*;

/**
 * Test class for AFJMangerImpl.
 * @author esunbal
 *
 */

public class AFJManagerImplTest{
	
	private static Properties afjProperties;
	private AFJManagerImpl testObject;
	private static File AFJManagerProperties;
	private static File ETLCServerProperties;
	private static String stnBasePath;
	final private HelpClass hc = new HelpClass(); 
	private static Connection connection;	
	private static Statement statement;
	private static File StaticProperties;	
	
	  @BeforeClass
	  public static void init() throws Exception{	
		  
			/* Create the needed tables. */
			try {
				Class.forName("org.hsqldb.jdbcDriver").newInstance();
				connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "sa", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			statement = connection.createStatement();
			
			statement.execute("CREATE TABLE META_DATABASES (USERNAME VARCHAR(300), VERSION_NUMBER VARCHAR(31), "
					+ "TYPE_NAME VARCHAR(300), CONNECTION_ID BIGINT, CONNECTION_NAME VARCHAR(300), "
					+ "CONNECTION_STRING VARCHAR(300), PASSWORD VARCHAR(31), DESCRIPTION VARCHAR(300), "
					+ "DRIVER_NAME VARCHAR(300), DB_LINK_NAME VARCHAR(300))");
			
			statement.executeUpdate("INSERT INTO META_DATABASES VALUES"
					+ "('sa', 'v1.2', 'USER', 1, 'dwhrep', 'jdbc:hsqldb:mem:testdb', '', 'desc', "
					+ "'org.hsqldb.jdbcDriver', 'db')");
		  
			statement.execute("create table TPActivation (TECHPACK_NAME varchar(300) not null, STATUS varchar(300) not null,VERSIONID varchar(300) null,TYPE varchar(300) not null, MODIFIED INTEGER)");
			statement.execute("insert into TPActivation (TECHPACK_NAME,STATUS,VERSIONID,TYPE,MODIFIED) values ('DC_E_STN','ACTIVE','DC_E_STN:((2))','PM',1)");
			
			
			statement.execute("create table DefaultTags (TAGID varchar(300) not null, " +
			"DATAFORMATID varchar(300) not null, DESCRIPTION varchar(300) null)");

			statement.execute("alter table DefaultTags add primary key (TAGID, DATAFORMATID)");
			
			statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('E1Interface','DC_E_STN:((2)):DC_E_STN_E1INTERFACE:mdc','Default tags for DC_E_STN_E1INTERFACE in DC_E_STN:((2)) with format mdc.')");
			statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('XYZ','DC_E_BSS:((16)):DC_E_BSS_XYZ:mdc','Default tags for DC_E_BSS_XYZ in DC_E_BSS:((16)) with format mdc.')");
			statement.execute("insert into DefaultTags (TAGID,DATAFORMATID,DESCRIPTION) values ('ABC','DC_E_CMN_STS:((3)):DC_E_CMN_STS_ABC:mdc','Default tags for DC_E_CMN_STS_ABC in DC_E_CMN_STS:((3)) with format mdc.')");
			
			// Create the MeasurementCounter table and insert values.
			statement.execute("CREATE TABLE Measurementcounter ( TYPEID VARCHAR(301)  ,DATANAME VARCHAR(301) ,DESCRIPTION VARCHAR(301) ,TIMEAGGREGATION VARCHAR(301) ,GROUPAGGREGATION VARCHAR(301) ,COUNTAGGREGATION VARCHAR(301) ,COLNUMBER BIGINT  ,DATATYPE VARCHAR(301) ,DATASIZE INTEGER  ,DATASCALE INTEGER  ,INCLUDESQL INTEGER  ,UNIVOBJECT VARCHAR(301) ,UNIVCLASS VARCHAR(301) ,COUNTERTYPE VARCHAR(301) ,COUNTERPROCESS VARCHAR(301) ,DATAID VARCHAR(301) ,FOLLOWJOHN INTEGER )");
			statement.execute("insert into Measurementcounter values('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifInErrors','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','SUM','SUM','PEG',102,'numeric',20,0,1,'ifInErrors',null,'PEG','PEG','ifInErrors',null)");
			statement.execute("insert into Measurementcounter values('DC_E_STN:((2)):DC_E_STN_E1INTERFACE','ifOutErrors','Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol.','SUM','SUM','PEG',103,'numeric',20,0,1,'ifInErrors',null,'PEG','PEG','ifOutErrors',null)");
			
			
			final String homeDir = System.getProperty("user.dir");
		  /* Create property file for database connection details */
		  AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		  System.setProperty("CONF_DIR",System.getProperty("user.dir"));
		  AFJManagerProperties.deleteOnExit();
		  
		  String afjBasePath = "afjBasePath=" + homeDir; 
		  afjBasePath = afjBasePath.replace("\\", "/");
		  String schemaFileLocation = "afj.schema.file=" + homeDir + File.separator + "oss_rc_pm_mim_PA5.xsd";
		  schemaFileLocation = schemaFileLocation.replace("\\", "/") + "\n";
		  
		  final String dtdDirPath = System.getProperty("user.dir") + File.separator + "dtd";
		  final File dtdDir = new File(dtdDirPath);
		  dtdDir.mkdir();
		  dtdDir.deleteOnExit();
		  
		  System.out.println("afjBasePath:"+afjBasePath);
		  try {
			  final  PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
			  pw.write("stn.name=DC_E_STN\n");
//			  pw.write("afjBasePath=/eniq/data/pmdata/AFJ\n");			  
			  pw.write("afj.supported.tps=DC_E_STN,DC_E_BSS\n");	
			  pw.write("bss.name=DC_E_BSS\n");
			  pw.write("afj.schema.package=com.ericsson.eniq.afj.schema\n");
			  pw.write("afj.schema.file=oss_rc_pm_mim_PA5.xsd\n");	
			  pw.write(afjBasePath);
			  pw.close();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }

		  afjProperties = new Properties();
		  final FileInputStream fis = new FileInputStream(AFJManagerProperties);
		  afjProperties.load(fis);
		  fis.close();
		  

		 

		  /* Create property file for database connection details */
		  ETLCServerProperties = new File(System.getProperty("user.dir"), "ETLCServer.properties");		  
		  ETLCServerProperties.deleteOnExit();
		  try {
			  final PrintWriter pw = new PrintWriter(new FileWriter(ETLCServerProperties));
			  pw.write("ENGINE_DB_URL = jdbc:hsqldb:mem:testdb\n");
			  pw.write("ENGINE_DB_USERNAME = sa\n");
			  pw.write("ENGINE_DB_PASSWORD = \n");
			  pw.write("ENGINE_DB_DRIVERNAME = org.hsqldb.jdbcDriver\n");
			  pw.close();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  
			/* Create property file for database connection details */
			StaticProperties = new File(System.getProperty("user.dir"), "static.properties");
			System.setProperty("CONF_DIR",System.getProperty("user.dir"));
			StaticProperties.deleteOnExit();
			try {
				final PrintWriter pw = new PrintWriter(new FileWriter(StaticProperties));
				pw.write("sybaseiq.option.public.DML_Options5=0");
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
	
	  /**
	   * @throws java.lang.Exception
	   */
	  @AfterClass
	  public static void tearDownAfterClass() throws Exception {
	    try {
	      final Statement stmt = connection.createStatement();
	      try {
	        stmt.executeUpdate("SHUTDOWN");
	      } finally {
	        stmt.close();
	      }
	    } finally {
	      connection.close();
	    }
	  }

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		/*
		 * Creating test object of Main before each test case run.
		 */		
		testObject = new AFJManagerImpl();		
		PropertiesUtility.setPropertiesUtility(null);		
	}	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPacks()}.
	 */
	@Test
	public void testGetAFJTechPacksNoValidDirs()throws AFJException{
		try{
		testObject.getAFJTechPacks();
		fail("Should not reach here. No valid directories are there for NVU files.");
		}
		catch(AFJConfiguationException ace){
			final String expected = "No valid directories for NVU files found on the server. Please check if the NVU techpack is installed.";
			assertEquals(expected, ace.getMessage());	
		}
		
	}
	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#checkForMOMFile()}.
	 */
	@Test
	public void testCheckForMOMFileNoFile() throws AFJException, Exception{			
		final String stnBaseDir = afjProperties.getProperty("stn.name");
		final String afjBasePath = afjProperties.getProperty("afjBasePath");
		final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);		
		afjSTNBaseDir.mkdirs();
		afjSTNBaseDir.deleteOnExit();
		stnBasePath = afjSTNBaseDir.getAbsolutePath();		

		String result = null;
		/* Reflecting the private tested method */	      
		final Class pcClass = testObject.getClass();
		final Method checkForMOMFile = pcClass.getDeclaredMethod("checkForMOMFile", new Class[] {String.class});
		checkForMOMFile.setAccessible(true);
		result = (String)checkForMOMFile.invoke(testObject, new Object[] {"DC_E_STN"});
		assertEquals(null, result);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#checkForMOMFile()}.
	 */
	@Test
	public void testCheckForMOMFile() throws AFJException, Exception{	
		final String stnBaseDir = afjProperties.getProperty("stn.name");
		final String afjBasePath = afjProperties.getProperty("afjBasePath");
		final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);		
		afjSTNBaseDir.mkdirs();
		afjSTNBaseDir.deleteOnExit();
		stnBasePath = afjSTNBaseDir.getAbsolutePath();
		
		final String fileName = stnBasePath + File.separator + "afj_stn.xml";
		final File stnXMLFile = new File(fileName);
		stnXMLFile.createNewFile();
//		File stnXMLFile = hc.createFile(stnBasePath,"afj_stn.xml",null);
		stnXMLFile.deleteOnExit();
		String result = null;
		System.out.println("stnXMLFile Location:"+stnXMLFile.getAbsolutePath());
		/* Reflecting the private tested method */	      
		final Class pcClass = testObject.getClass();
		final Method checkForMOMFile = pcClass.getDeclaredMethod("checkForMOMFile", new Class[] {String.class});
		checkForMOMFile.setAccessible(true);
		result = (String)checkForMOMFile.invoke(testObject, new Object[] {"DC_E_STN"});		
		assertEquals("afj_stn.xml", result);
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPacks()}.
	 */	
	@Test
	public void testGetAFJTechPacks() throws AFJException, AFJConfiguationException{
		final String stnBaseDir = afjProperties.getProperty("stn.name");
		final String afjBasePath = afjProperties.getProperty("afjBasePath");
		final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);		
		afjSTNBaseDir.mkdirs();
		afjSTNBaseDir.deleteOnExit();
		stnBasePath = afjSTNBaseDir.getAbsolutePath();		
		List<AFJTechPack> result = null;		
		result = testObject.getAFJTechPacks();
		assertEquals(1, result.size());
	}	
	
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPack()}.
   * @throws AFJConfiguationException 
   * @throws AFJException 
   */ 
  @Test
  public void testGetAFJTechPack() throws AFJException, AFJConfiguationException {
	  final String stnBaseDir = afjProperties.getProperty("stn.name");
	  final String afjBasePath = afjProperties.getProperty("afjBasePath");
	  final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);   
    afjSTNBaseDir.mkdirs();
    afjSTNBaseDir.deleteOnExit();
    stnBasePath = afjSTNBaseDir.getAbsolutePath();    
    final AFJTechPack result = testObject.getAFJTechPack("DC_E_STN");
    assertTrue(result != null);
  }

	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPacks()}.
	 */	
	@Test
	public void testGetAFJTechPacksMoreThanOneFile() throws AFJException, AFJConfiguationException{
		final String stnBaseDir = afjProperties.getProperty("stn.name");
		final String afjBasePath = afjProperties.getProperty("afjBasePath");
		final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);		
		afjSTNBaseDir.mkdirs();
		afjSTNBaseDir.deleteOnExit();
		stnBasePath = afjSTNBaseDir.getAbsolutePath();		
		final File stnXMLFile1 = hc.createFile(stnBasePath,"afj_stn1.xml",null);
		stnXMLFile1.deleteOnExit();
		final File stnXMLFile2 = hc.createFile(stnBasePath,"afj_stn2.xml",null);
		stnXMLFile2.deleteOnExit();		

		List<AFJTechPack> result = null;		
		result = testObject.getAFJTechPacks();			
		final String dir = afjProperties.getProperty("afjBasePath") + File.separator +afjProperties.getProperty("stn.name");
		final String expected = "ERROR:More than one xml file found. There should be only one xml file in the dir:"+ dir;
		assertEquals(result.get(0).getMessage(), expected);	
	}	
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#checkForMOMFile()}.
	 */
	@Test
	public void testCheckForMOMFileMoreThanOneFileFound(){		
		try{
			final String stnBaseDir = afjProperties.getProperty("stn.name");
			final String afjBasePath = afjProperties.getProperty("afjBasePath");
			final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);			
			afjSTNBaseDir.mkdirs();
			afjSTNBaseDir.deleteOnExit();
			stnBasePath = afjSTNBaseDir.getAbsolutePath();			
			final File stnXMLFile1 = hc.createFile(stnBasePath,"afj_stn1.xml",null);
			stnXMLFile1.deleteOnExit();
			final File stnXMLFile2 = hc.createFile(stnBasePath,"afj_stn2.xml",null);
			stnXMLFile2.deleteOnExit();			
			/* Reflecting the private tested method */	      
			final Class pcClass = testObject.getClass();
			final Method checkForMOMFile = pcClass.getDeclaredMethod("checkForMOMFile", new Class[] {String.class});
			checkForMOMFile.setAccessible(true);
			checkForMOMFile.invoke(testObject, new Object[] {"DC_E_STN"});
			fail("Should not reach here. More than one file in the input directory.");
		}		
		catch(Exception e){
			if(e.getClass().equals(InvocationTargetException.class)){
				final InvocationTargetException ite = (InvocationTargetException)e;
				if(ite.getTargetException() instanceof AFJException){
					final String actualMessage = ite.getTargetException().getMessage();
					final String dir = afjProperties.getProperty("afjBasePath") + File.separator +afjProperties.getProperty("stn.name");
					final String expected = "ERROR:More than one xml file found. There should be only one xml file in the dir:"+ dir;
					assertEquals(expected, actualMessage);
				}			
				else{
					fail("Should not reach here. Not AFJException. Some other exception thrown.");
				}
			}
			else{
				fail("Should not reach here. Some other exception thrown.");
			}
		}
	}
	

	

	
//	/**
//	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack()}.
//	 */
//	@Test
//	public void testUpgradeAFJTechPack() throws AFJException{
//		String result = null;		
//		result = testObject.upgradeAFJTechPack(null);		
//		assertEquals("dummySetGenerationMethod inside CommonGenericTPUG called successfully", result);
//	}
	
	


//	public void testGetAFJDelta() throws AFJException{
//		
//		final String stnBaseDir = afjProperties.getProperty("stn.name");
//		final String afjBasePath = afjProperties.getProperty("afjBasePath");
//		final File afjSTNBaseDir = new File(afjBasePath + File.separator + stnBaseDir);
//		System.out.println("afjSTNBaseDir:"+afjSTNBaseDir.getAbsolutePath());
//		afjSTNBaseDir.mkdirs();
//		afjSTNBaseDir.deleteOnExit();
//		stnBasePath = afjSTNBaseDir.getAbsolutePath();
//		System.out.println("stnBaseDir:"+stnBasePath);
//
//		final String xmlContent = "<PM id=\"\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns=\"com:ericsson:ims:mim\">" +
//				"<data><moc name=\"E1INTERFACE\"><description/><group><counter measurementType=\"TestDemo3\">" +
//				"<measurementName>TestDemo3</measurementName>" +
//				"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
//				"</description>	<collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod>" +
//				"<measurementResult><outputUnit/><resultType>decimal</resultType></measurementResult>" +
//				"<status>current</status></counter><counter measurementType=\"ifInErrors\">" +
//				"<measurementName>ifInErrors</measurementName>" +
//				"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
//				"</description><collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod><measurementResult>" +
//				"<outputUnit/><resultType>decimal</resultType></measurementResult><status>current</status></counter>" +
//				"</group></moc></data></PM>";
//		
//		final File stnXMLFile = hc.createFile(stnBasePath,"afj_stn.xml",xmlContent);		
//		stnXMLFile.deleteOnExit();
//		
//		final String xsdContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
//		"<!-- edited with XMLSpy v2006 rel. 3 sp2 (http://www.altova.com) by Casey (Ericsson) -->"+
//		"<xs:schema xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">"+
//			"<xs:annotation>"+
//				"<xs:documentation xml:lang=\"en\">"+
//		               	 "Copyright notice: (c) Ericsson AB 2010"+
//		               	 "Warning text All rights reserved. No parts of this program may be reproduced in any form without the written permission of the copyright holder."+
//				"</xs:documentation>"+
//			"</xs:annotation>"+
//			"<xs:element name=\"PM\">"+
//				"<xs:complexType>"+
//					"<xs:sequence>"+
//						"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
//						"<xs:element name=\"Capabilities\" type=\"CapabilitiesType\"/>"+
//						"<xs:element name=\"data\" type=\"dataType\"/>"+
//						"<xs:element name=\"pmMimMetadata\" minOccurs=\"0\">"+
//							"<xs:complexType>"+
//								"<xs:sequence>"+
//									"<xs:element name=\"nodeType\" type=\"xs:string\"/>"+
//									"<xs:element name=\"node_major_version\" type=\"xs:string\" minOccurs=\"0\"/>"+
//									"<xs:element name=\"node_minor_version\" type=\"xs:string\" minOccurs=\"0\"/>"+
//									"<xs:element name=\"timestamp\" type=\"xs:dateTime\" minOccurs=\"0\"/>"+
//									"<xs:element name=\"pmMimVersion\" type=\"xs:string\"/>"+
//									"<xs:element name=\"common_baseline_doc_id\" type=\"xs:string\" minOccurs=\"0\"/>"+
//									"<xs:element name=\"common_baseline_major_version\" type=\"xs:integer\" minOccurs=\"0\"/>"+
//									"<xs:element name=\"common_baseline_minor_version\" type=\"xs:integer\" minOccurs=\"0\"/>"+
//								"</xs:sequence>"+
//							"</xs:complexType>"+
//						"</xs:element>"+
//					"</xs:sequence>"+
//					"<xs:attribute name=\"id\" type=\"xs:string\" use=\"required\"/>"+
//				"</xs:complexType>"+
//			"</xs:element>"+
//			"<xs:complexType name=\"CapabilitiesType\">"+
//				"<xs:sequence>"+
//					"<xs:element name=\"alignedReportingPeriod\" default=\"true\" minOccurs=\"0\">"+
//						"<xs:simpleType>"+
//							"<xs:restriction base=\"xs:string\">"+
//								"<xs:enumeration value=\"true\"/>"+
//								"<xs:enumeration value=\"false\"/>"+
//							"</xs:restriction>"+
//						"</xs:simpleType>"+
//					"</xs:element>"+
//					"<xs:element name=\"JobPersistence\" default=\"RESTART\" minOccurs=\"0\">"+
//						"<xs:simpleType>"+
//							"<xs:restriction base=\"xs:string\">"+
//								"<xs:enumeration value=\"NONE\"/>"+
//								"<xs:enumeration value=\"RESTART\"/>"+
//								"<xs:enumeration value=\"UPGRADE\"/>"+
//							"</xs:restriction>"+
//						"</xs:simpleType>"+
//					"</xs:element>"+
//					"<xs:element name=\"FileRetention\" type=\"xs:unsignedShort\" default=\"24\" minOccurs=\"0\"/>"+
//					"<xs:element name=\"FileCompression\" type=\"xs:boolean\" default=\"true\" minOccurs=\"0\"/>"+
//					"<xs:element name=\"CounterCapabilities\">"+
//						"<xs:complexType>"+
//							"<xs:sequence>"+
//								"<xs:element name=\"OutputFormat\" default=\"3GPP_XML\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"3GPP_XML\"/>"+
//											"<xs:enumeration value=\"3GPP_ASN.1\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"OutputVersion\" default=\"32.104 V3.5.0\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"32.104 V3.5.0\"/>"+
//											"<xs:enumeration value=\"32.435 V7.2.0 \"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"FileDeliveryProtocol\" default=\"SFTP\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"SFTP\"/>"+
//											"<xs:enumeration value=\"FTP\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"FileDeliveryMethod\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"PUSH\"/>"+
//											"<xs:enumeration value=\"PULL\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"DeliveryTarget\" type=\"xs:string\" default='\"\"'/>"+
//								"<xs:element name=\"GranularityPeriod\" default=\"15\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:unsignedInt\">"+
//											"<xs:enumeration value=\"5\"/>"+
//											"<xs:enumeration value=\"15\"/>"+
//											"<xs:enumeration value=\"30\"/>"+
//											"<xs:enumeration value=\"60\"/>"+
//											"<xs:enumeration value=\"720\"/>"+
//											"<xs:enumeration value=\"1440\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"ReportingPeriod\" default=\"15\" minOccurs=\"0\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:unsignedInt\">"+
//											"<xs:enumeration value=\"5\"/>"+
//											"<xs:enumeration value=\"15\"/>"+
//											"<xs:enumeration value=\"30\"/>"+
//											"<xs:enumeration value=\"60\"/>"+
//											"<xs:enumeration value=\"720\"/>"+
//											"<xs:enumeration value=\"1440\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//							"</xs:sequence>"+
//						"</xs:complexType>"+
//					"</xs:element>"+
//				"</xs:sequence>"+
//				"<xs:anyAttribute/>"+
//			"</xs:complexType>"+
//			"<xs:complexType name=\"mocType\">"+
//				"<xs:sequence>"+
//					"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
//					"<xs:element name=\"moc_level\" default=\"0\" minOccurs=\"0\">"+
//						"<xs:simpleType>"+
//							"<xs:restriction base=\"xs:integer\">"+
//								"<xs:enumeration value=\"0\"/>"+
//								"<xs:enumeration value=\"1\"/>"+
//								"<xs:enumeration value=\"2\"/>"+
//								"<xs:enumeration value=\"3\"/>"+
//							"</xs:restriction>"+
//						"</xs:simpleType>"+
//					"</xs:element>"+
//					"<xs:element name=\"switchingTechnology\" default=\"Packet Switched\" minOccurs=\"0\">"+
//						"<xs:simpleType>"+
//							"<xs:restriction base=\"xs:string\">"+
//								"<xs:whiteSpace value=\"collapse\"/>"+
//								"<xs:enumeration value=\"Packet Switched\"/>"+
//							"</xs:restriction>"+
//						"</xs:simpleType>"+
//					"</xs:element>"+
//					"<xs:element name=\"group\" type=\"groupType\" maxOccurs=\"unbounded\"/>"+
//				"</xs:sequence>"+
//				"<xs:attribute name=\"name\" type=\"xs:string\" use=\"required\"/>"+
//			"</xs:complexType>"+
//			"<xs:complexType name=\"groupType\">"+
//				"<xs:sequence>"+
//					"<xs:element name=\"description\" minOccurs=\"0\"/>"+
//					"<xs:element name=\"counter\" type=\"counterType\" maxOccurs=\"unbounded\"/>"+
//				"</xs:sequence>"+
//				"<xs:attribute name=\"name\" type=\"xs:string\" use=\"optional\"/>"+
//			"</xs:complexType>"+
//			"<xs:complexType name=\"dataType\">"+
//				"<xs:sequence>"+
//					"<xs:element name=\"moc\" type=\"mocType\" maxOccurs=\"unbounded\"/>"+
//				"</xs:sequence>"+
//			"</xs:complexType>"+
//			"<xs:complexType name=\"counterType\">"+
//				"<xs:sequence>"+
//					"<xs:element name=\"measurementName\" type=\"xs:string\"/>"+
//					"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
//					"<xs:element name=\"collectionMethod\">"+
//						"<xs:complexType>"+
//							"<xs:sequence>"+
//								"<xs:element name=\"type\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"CC\"/>"+
//											"<xs:enumeration value=\"SI\"/>"+
//											"<xs:enumeration value=\"GAUGE\"/>"+
//											"<xs:enumeration value=\"DER\"/>"+
//											"<xs:enumeration value=\"IDENTITY\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"subtype\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"MIN\"/>"+
//											"<xs:enumeration value=\"MAX\"/>"+
//											"<xs:enumeration value=\"LAST_UPDATE\"/>"+
//											"<xs:enumeration value=\"RAW\"/>"+
//											"<xs:enumeration value=\"MEAN\"/>"+
//											"<xs:enumeration value=\"SUM\"/>"+
//											"<xs:enumeration value=\"\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//							"</xs:sequence>"+
//						"</xs:complexType>"+
//					"</xs:element>"+
//					"<xs:element name=\"measurementResult\">"+
//						"<xs:complexType>"+
//							"<xs:sequence>"+
//								"<xs:element name=\"outputUnit\" type=\"xs:string\"/>"+
//								"<xs:element name=\"resultType\">"+
//									"<xs:simpleType>"+
//										"<xs:restriction base=\"xs:string\">"+
//											"<xs:enumeration value=\"decimal\"/>"+
//											"<xs:enumeration value=\"integer\"/>"+
//											"<xs:enumeration value=\"float\"/>"+
//											"<xs:enumeration value=\"byte\"/>"+
//											"<xs:enumeration value=\"int\"/>"+
//											"<xs:enumeration value=\"smallInt\"/>"+
//											"<xs:enumeration value=\"unsignedInt\"/>"+
//											"<xs:enumeration value=\"long\"/>"+
//											"<xs:enumeration value=\"double\"/>"+
//											"<xs:enumeration value=\"dateTime\"/>"+
//											"<xs:enumeration value=\"date\"/>"+
//											"<xs:enumeration value=\"time\"/>"+
//											"<xs:enumeration value=\"boolean\"/>"+
//										"</xs:restriction>"+
//									"</xs:simpleType>"+
//								"</xs:element>"+
//								"<xs:element name=\"resultType_freeText\" type=\"xs:string\" minOccurs=\"0\"/>"+
//							"</xs:sequence>"+
//						"</xs:complexType>"+
//					"</xs:element>"+
//					"<xs:element name=\"status\" default=\"current\">"+
//						"<xs:simpleType>"+
//							"<xs:restriction base=\"xs:string\">"+
//								"<xs:enumeration value=\"current\"/>"+
//								"<xs:enumeration value=\"obsolete\"/>"+
//								"<xs:enumeration value=\"deprecated\"/>"+
//							"</xs:restriction>"+
//						"</xs:simpleType>"+
//					"</xs:element>"+
//				"</xs:sequence>"+
//				"<xs:attribute name=\"measurementType\" type=\"xs:string\" use=\"required\"/>"+
//			"</xs:complexType>"+
//		"</xs:schema>";
//
//		final String xsdLocation = System.getProperty("user.dir")+File.separator+"dtd";
//		System.out.println("xsdLocation"+xsdLocation);
//		final File xsdFile = hc.createFile(xsdLocation,"oss_rc_pm_mim_PA5.xsd",xsdContent);		
//		xsdFile.deleteOnExit();
//		
//		final AFJTechPack afjTechPack = new AFJTechPack();
//		final String techPackName = "DC_E_STN";
//		afjTechPack.setFileName("afj_stn.xml");
//		afjTechPack.setMessage("");
//		afjTechPack.setMomFilePresent(true);
//		afjTechPack.setTechPackName(techPackName);
//		
//		final AFJDelta result = testObject.getAFJDelta(afjTechPack);		
//		final int expected = 1;
//		final int actual = result.getMeasurementTypes().size();
//		assertEquals(expected, actual);
//	}	
	
	
//	 @AfterClass
//	  public void clean() {
////		  HelpClass hc = new HelpClass();
//		  String homeDir = System.getProperty("user.dir");	 
//		  
//		  String stnBaseDir = afjProperties.getProperty("afjSTNBaseDir");
//		  File afjSTNBaseDir = new File(stnBaseDir);
//		  afjSTNBaseDir.delete();
////		  File statusFile = hc.createFile(stnBaseDir, "afj_status", "STATUS=Upgraded on May 17, 2010 at 15:39.");
////		  statusFile.delete();		  
////		  File stnXMLFile = hc.createFile(stnBaseDir,"afj_stn.xml",null);
////		  stnXMLFile.delete();
//	  }
	 



}
