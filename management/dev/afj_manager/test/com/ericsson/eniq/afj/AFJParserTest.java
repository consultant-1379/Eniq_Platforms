/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.validation.Schema;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.schema.DataType;
import com.ericsson.eniq.afj.schema.MocType;
import com.ericsson.eniq.afj.schema.PM;
import com.ericsson.eniq.afj.xml.AFJParser;
import com.ericsson.eniq.afj.xml.AFJParserValidationEventHandler;
import com.ericsson.eniq.exception.AFJException;
import com.ericsson.junit.HelpClass;

/**
 * @author esunbal
 *
 */
public class AFJParserTest {

	private static Properties afjProperties;
	
	private static File AFJManagerProperties;
	
	private static AFJParser objectUnderTest;
	
	private static File ETLCServerProperties;
	
	private static AFJTechPack techpack;	
	
	private static HelpClass hc = new HelpClass();
	
	private static String stnBasePath;
	
	private static File StaticProperties;	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		techpack = new AFJTechPack();
		techpack.setTechPackName("DC_E_STN");
		techpack.setFileName("afj_stn.xml");
		techpack.setContextPath("com.ericsson.eniq.afj.schema");
		techpack.setSchemaName("oss_rc_pm_mim_PA5.xsd");
		techpack.setNamespaceAware(false);
		
		final String userDir = System.getProperty("user.dir");
		String afjBasePath = "afjBasePath=" + userDir; 
		afjBasePath = afjBasePath.replace("\\", "/");
		
		/* Create property file for database connection details */
		AFJManagerProperties = new File(System.getProperty("user.dir"), "AFJManager.properties");
		System.setProperty("CONF_DIR",userDir);
		AFJManagerProperties.deleteOnExit();
		try {
			final PrintWriter pw = new PrintWriter(new FileWriter(AFJManagerProperties));
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
		  
//		FileUtils fileUtils = FileUtils.newFileUtils();
//		String sourceXsdFile = System.getProperty("CONF_DIR") +File.separator + "xsd" + File.separator + "oss_rc_pm_mim_PA5.xsd";
//		String sourceXsdFile = userDir + File.separator + "test/xml" + File.separator + "oss_rc_pm_mim_PA5.xsd";
//		String destXsdFile = System.getProperty("CONF_DIR") +File.separator + "dtd" + File.separator + "oss_rc_pm_mim_PA5.xsd";
//		fileUtils.copyFile(sourceXsdFile, destXsdFile);
		
			final String dtdDirPath = System.getProperty("user.dir") + File.separator + "dtd";
			final File dtdDir = new File(dtdDirPath);
		  dtdDir.mkdir();
		  dtdDir.deleteOnExit();
		
		  final String xsdContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
			"<!-- edited with XMLSpy v2006 rel. 3 sp2 (http://www.altova.com) by Casey (Ericsson) -->"+
			"<xs:schema xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">"+
				"<xs:annotation>"+
					"<xs:documentation xml:lang=\"en\">"+
			               	 "Copyright notice: (c) Ericsson AB 2010"+
			               	 "Warning text All rights reserved. No parts of this program may be reproduced in any form without the written permission of the copyright holder."+
					"</xs:documentation>"+
				"</xs:annotation>"+
				"<xs:element name=\"PM\">"+
					"<xs:complexType>"+
						"<xs:sequence>"+
							"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
							"<xs:element name=\"Capabilities\" type=\"CapabilitiesType\"/>"+
							"<xs:element name=\"data\" type=\"dataType\"/>"+
							"<xs:element name=\"pmMimMetadata\" minOccurs=\"0\">"+
								"<xs:complexType>"+
									"<xs:sequence>"+
										"<xs:element name=\"nodeType\" type=\"xs:string\"/>"+
										"<xs:element name=\"node_major_version\" type=\"xs:string\" minOccurs=\"0\"/>"+
										"<xs:element name=\"node_minor_version\" type=\"xs:string\" minOccurs=\"0\"/>"+
										"<xs:element name=\"timestamp\" type=\"xs:dateTime\" minOccurs=\"0\"/>"+
										"<xs:element name=\"pmMimVersion\" type=\"xs:string\"/>"+
										"<xs:element name=\"common_baseline_doc_id\" type=\"xs:string\" minOccurs=\"0\"/>"+
										"<xs:element name=\"common_baseline_major_version\" type=\"xs:integer\" minOccurs=\"0\"/>"+
										"<xs:element name=\"common_baseline_minor_version\" type=\"xs:integer\" minOccurs=\"0\"/>"+
									"</xs:sequence>"+
								"</xs:complexType>"+
							"</xs:element>"+
						"</xs:sequence>"+
						"<xs:attribute name=\"id\" type=\"xs:string\" use=\"required\"/>"+
					"</xs:complexType>"+
				"</xs:element>"+
				"<xs:complexType name=\"CapabilitiesType\">"+
					"<xs:sequence>"+
						"<xs:element name=\"alignedReportingPeriod\" default=\"true\" minOccurs=\"0\">"+
							"<xs:simpleType>"+
								"<xs:restriction base=\"xs:string\">"+
									"<xs:enumeration value=\"true\"/>"+
									"<xs:enumeration value=\"false\"/>"+
								"</xs:restriction>"+
							"</xs:simpleType>"+
						"</xs:element>"+
						"<xs:element name=\"JobPersistence\" default=\"RESTART\" minOccurs=\"0\">"+
							"<xs:simpleType>"+
								"<xs:restriction base=\"xs:string\">"+
									"<xs:enumeration value=\"NONE\"/>"+
									"<xs:enumeration value=\"RESTART\"/>"+
									"<xs:enumeration value=\"UPGRADE\"/>"+
								"</xs:restriction>"+
							"</xs:simpleType>"+
						"</xs:element>"+
						"<xs:element name=\"FileRetention\" type=\"xs:unsignedShort\" default=\"24\" minOccurs=\"0\"/>"+
						"<xs:element name=\"FileCompression\" type=\"xs:boolean\" default=\"true\" minOccurs=\"0\"/>"+
						"<xs:element name=\"CounterCapabilities\">"+
							"<xs:complexType>"+
								"<xs:sequence>"+
									"<xs:element name=\"OutputFormat\" default=\"3GPP_XML\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"3GPP_XML\"/>"+
												"<xs:enumeration value=\"3GPP_ASN.1\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"OutputVersion\" default=\"32.104 V3.5.0\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"32.104 V3.5.0\"/>"+
												"<xs:enumeration value=\"32.435 V7.2.0 \"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"FileDeliveryProtocol\" default=\"SFTP\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"SFTP\"/>"+
												"<xs:enumeration value=\"FTP\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"FileDeliveryMethod\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"PUSH\"/>"+
												"<xs:enumeration value=\"PULL\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"DeliveryTarget\" type=\"xs:string\" default='\"\"'/>"+
									"<xs:element name=\"GranularityPeriod\" default=\"15\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:unsignedInt\">"+
												"<xs:enumeration value=\"5\"/>"+
												"<xs:enumeration value=\"15\"/>"+
												"<xs:enumeration value=\"30\"/>"+
												"<xs:enumeration value=\"60\"/>"+
												"<xs:enumeration value=\"720\"/>"+
												"<xs:enumeration value=\"1440\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"ReportingPeriod\" default=\"15\" minOccurs=\"0\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:unsignedInt\">"+
												"<xs:enumeration value=\"5\"/>"+
												"<xs:enumeration value=\"15\"/>"+
												"<xs:enumeration value=\"30\"/>"+
												"<xs:enumeration value=\"60\"/>"+
												"<xs:enumeration value=\"720\"/>"+
												"<xs:enumeration value=\"1440\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
								"</xs:sequence>"+
							"</xs:complexType>"+
						"</xs:element>"+
					"</xs:sequence>"+
					"<xs:anyAttribute/>"+
				"</xs:complexType>"+
				"<xs:complexType name=\"mocType\">"+
					"<xs:sequence>"+
						"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
						"<xs:element name=\"moc_level\" default=\"0\" minOccurs=\"0\">"+
							"<xs:simpleType>"+
								"<xs:restriction base=\"xs:integer\">"+
									"<xs:enumeration value=\"0\"/>"+
									"<xs:enumeration value=\"1\"/>"+
									"<xs:enumeration value=\"2\"/>"+
									"<xs:enumeration value=\"3\"/>"+
								"</xs:restriction>"+
							"</xs:simpleType>"+
						"</xs:element>"+
						"<xs:element name=\"switchingTechnology\" default=\"Packet Switched\" minOccurs=\"0\">"+
							"<xs:simpleType>"+
								"<xs:restriction base=\"xs:string\">"+
									"<xs:whiteSpace value=\"collapse\"/>"+
									"<xs:enumeration value=\"Packet Switched\"/>"+
								"</xs:restriction>"+
							"</xs:simpleType>"+
						"</xs:element>"+
						"<xs:element name=\"group\" type=\"groupType\" maxOccurs=\"unbounded\"/>"+
					"</xs:sequence>"+
					"<xs:attribute name=\"name\" type=\"xs:string\" use=\"required\"/>"+
				"</xs:complexType>"+
				"<xs:complexType name=\"groupType\">"+
					"<xs:sequence>"+
						"<xs:element name=\"description\" minOccurs=\"0\"/>"+
						"<xs:element name=\"counter\" type=\"counterType\" maxOccurs=\"unbounded\"/>"+
					"</xs:sequence>"+
					"<xs:attribute name=\"name\" type=\"xs:string\" use=\"optional\"/>"+
				"</xs:complexType>"+
				"<xs:complexType name=\"dataType\">"+
					"<xs:sequence>"+
						"<xs:element name=\"moc\" type=\"mocType\" maxOccurs=\"unbounded\"/>"+
					"</xs:sequence>"+
				"</xs:complexType>"+
				"<xs:complexType name=\"counterType\">"+
					"<xs:sequence>"+
						"<xs:element name=\"measurementName\" type=\"xs:string\"/>"+
						"<xs:element name=\"description\" type=\"xs:string\" default='\"\"'/>"+
						"<xs:element name=\"collectionMethod\">"+
							"<xs:complexType>"+
								"<xs:sequence>"+
									"<xs:element name=\"type\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"CC\"/>"+
												"<xs:enumeration value=\"SI\"/>"+
												"<xs:enumeration value=\"GAUGE\"/>"+
												"<xs:enumeration value=\"DER\"/>"+
												"<xs:enumeration value=\"IDENTITY\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"subtype\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"MIN\"/>"+
												"<xs:enumeration value=\"MAX\"/>"+
												"<xs:enumeration value=\"LAST_UPDATE\"/>"+
												"<xs:enumeration value=\"RAW\"/>"+
												"<xs:enumeration value=\"MEAN\"/>"+
												"<xs:enumeration value=\"SUM\"/>"+
												"<xs:enumeration value=\"\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
								"</xs:sequence>"+
							"</xs:complexType>"+
						"</xs:element>"+
						"<xs:element name=\"measurementResult\">"+
							"<xs:complexType>"+
								"<xs:sequence>"+
									"<xs:element name=\"outputUnit\" type=\"xs:string\"/>"+
									"<xs:element name=\"resultType\">"+
										"<xs:simpleType>"+
											"<xs:restriction base=\"xs:string\">"+
												"<xs:enumeration value=\"decimal\"/>"+
												"<xs:enumeration value=\"integer\"/>"+
												"<xs:enumeration value=\"float\"/>"+
												"<xs:enumeration value=\"byte\"/>"+
												"<xs:enumeration value=\"int\"/>"+
												"<xs:enumeration value=\"smallInt\"/>"+
												"<xs:enumeration value=\"unsignedInt\"/>"+
												"<xs:enumeration value=\"long\"/>"+
												"<xs:enumeration value=\"double\"/>"+
												"<xs:enumeration value=\"dateTime\"/>"+
												"<xs:enumeration value=\"date\"/>"+
												"<xs:enumeration value=\"time\"/>"+
												"<xs:enumeration value=\"boolean\"/>"+
											"</xs:restriction>"+
										"</xs:simpleType>"+
									"</xs:element>"+
									"<xs:element name=\"resultType_freeText\" type=\"xs:string\" minOccurs=\"0\"/>"+
								"</xs:sequence>"+
							"</xs:complexType>"+
						"</xs:element>"+
						"<xs:element name=\"status\" default=\"current\">"+
							"<xs:simpleType>"+
								"<xs:restriction base=\"xs:string\">"+
									"<xs:enumeration value=\"current\"/>"+
									"<xs:enumeration value=\"obsolete\"/>"+
									"<xs:enumeration value=\"deprecated\"/>"+
								"</xs:restriction>"+
							"</xs:simpleType>"+
						"</xs:element>"+
					"</xs:sequence>"+
					"<xs:attribute name=\"measurementType\" type=\"xs:string\" use=\"required\"/>"+
				"</xs:complexType>"+
			"</xs:schema>";

		  final String xsdLocation = System.getProperty("user.dir")+File.separator+"dtd";
			System.out.println("xsdLocation"+xsdLocation);
			final File xsdFile = hc.createFile(xsdLocation,"oss_rc_pm_mim_PA5.xsd",xsdContent);		
			xsdFile.deleteOnExit();		
		  
		
		/* Create property file for database connection details */
		ETLCServerProperties = new File(userDir, "ETLCServer.properties");		
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
		
//		String sourceXmlFile = userDir + File.separator + "test/xml" + File.separator + "DC_E_STN_TestFile.xml";
//		String destXmlFile= PropertiesUtility.getProperty(PropertyConstants.PROP_AFJ_BASE_DIR) + File.separator 
//		+ techpack.getTechPackName() + File.separator + techpack.getFileName();		
//		fileUtils.copyFile(sourceXmlFile, destXmlFile);
		
//		final String xmlContent = "<PM id=\"\" xmlns:xi=\"http://www.w3.org/2001/XInclude\" xmlns=\"com:ericsson:ims:mim\">" +
//		"<data><moc name=\"E1INTERFACE\"><description/><group><counter measurementType=\"TestDemo3\">" +
//		"<measurementName>TestDemo3</measurementName>" +
//		"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
//		"</description>	<collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod>" +
//		"<measurementResult><outputUnit/><resultType>decimal</resultType></measurementResult>" +
//		"<status>current</status></counter><counter measurementType=\"ifInErrors\">" +
//		"<measurementName>ifInErrors</measurementName>" +
//		"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
//		"</description><collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod><measurementResult>" +
//		"<outputUnit/><resultType>decimal</resultType></measurementResult><status>current</status></counter>" +
//		"</group></moc></data></PM>";

		String xmlContent = "<PM id=\"\" xmlns:xi=\"http://www.w3.org/2001/XInclude\">" +
		"<description>This document defines the performance counters for STN</description>" +
		"<Capabilities><CounterCapabilities><DeliveryTarget>STN</DeliveryTarget></CounterCapabilities></Capabilities>"+
		"<data><moc name=\"E1INTERFACE\"><description/><group><counter measurementType=\"TestDemo3\">" +
		"<measurementName>TestDemo3</measurementName>" +
		"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
		"</description>	<collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod>" +
		"<measurementResult><outputUnit/><resultType>decimal</resultType></measurementResult>" +
		"<status>current</status></counter><counter measurementType=\"ifInErrors\">" +
		"<measurementName>ifInErrors</measurementName>" +
		"<description>Number of inbound packets that contained error preventing them from being deliverable to higher-layer protocol." +
		"</description><collectionMethod><type>CC</type><subtype>SUM</subtype></collectionMethod><measurementResult>" +
		"<outputUnit/><resultType>decimal</resultType></measurementResult><status>current</status></counter>" +
		"</group></moc></data></PM>";
		
		final String stnBaseDir = afjProperties.getProperty("stn.name");
//		String afjBasePath = afjProperties.getProperty("afjBasePath");
		final File afjSTNBaseDir = new File(afjProperties.getProperty("afjBasePath") + File.separator + stnBaseDir);		
		afjSTNBaseDir.mkdirs();
		afjSTNBaseDir.deleteOnExit();
		stnBasePath = afjSTNBaseDir.getAbsolutePath();
		
		System.out.println("stnBasePath:"+stnBasePath);
		final File stnXMLFile = hc.createFile(stnBasePath,"afj_stn.xml",xmlContent);		
		stnXMLFile.deleteOnExit();

	}
	
	  @AfterClass
	  public static void tearDownAfterClass() throws Exception {
		  /* Cleaning up after test */		
		  objectUnderTest = null;
	  }
	
	  @Before
		public void setUpBeforeTest() throws Exception {

			/* Create AFJParser instance before every test and set up a ant project object for it. */
			objectUnderTest = new AFJParser(techpack);			
		
		}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.AFJParser#parse(com.ericsson.eniq.afj.common.AFJTechPack)}.
	 */
	@Test
	public void testParse() throws AFJException{
		final PM actualObject = (PM)objectUnderTest.parse(techpack);
		final DataType dt = actualObject.getData();
		final List<MocType> mtList = dt.getMoc();
		final int actualMocSize = mtList.size();
		assertEquals(1, actualMocSize);		
	}

	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.AFJParser#getSchema(String)}.
	 */
	@Test
	public void testGetSchema() throws Exception{
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getSchema = pcClass.getDeclaredMethod("getSchema", new Class[] {String.class});
		getSchema.setAccessible(true);		
		final Schema schema = (Schema)getSchema.invoke(objectUnderTest, new Object[] {"oss_rc_pm_mim_PA5.xsd"});	
		assertNotNull("Schema is not null", schema);
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.afj.xml.AFJParser#processException(AFJParserValidationEventHandler handler, Exception e) throws AFJException}.
	 */
	@Test
	public void testProcessException(){
		final Class pcClass = objectUnderTest.getClass();
		final String errorString = "Error parsing line 10 due to dummy value";
		final String expectedString = "The XML is not valid.\nError parsing line 10 due to dummy value";
		try{
			final Method processException = pcClass.getDeclaredMethod("processException", new Class[] {AFJParserValidationEventHandler.class});
		processException.setAccessible(true);
		
		final AFJParserValidationEventHandler handlerObject = new AFJParserValidationEventHandler();
		final Class handlerClass = handlerObject.getClass();
		final Field messages = handlerClass.getDeclaredField("messages");
		messages.setAccessible(true);
		final List<String> mockedValues = new ArrayList<String>();		
		mockedValues.add(errorString);
		messages.set(handlerObject, mockedValues);		
		processException.invoke(objectUnderTest, new Object[] {handlerObject});
		}				
		catch(Exception e){
//			System.out.println(e.getCause().getMessage());
			assertEquals(expectedString, e.getCause().getMessage());
		}
	}
	
	
}
