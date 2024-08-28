package com.ericsson.eniq.techpacksdk.common;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test Class for Class HostFileparser
 * @author eanguan
 *
 */
public class HostFileParserTest extends TestCase{
	private String server ;
	private String userName ;
	private String passWord ;
	private String remoteFile = "/etc/hosts" ;
	private String localUserHomePath = System.getProperty("user.home") + File.separatorChar ;
	private String localFileLocation = localUserHomePath + "testFile" ;
	
	
	//Path of host file in unix environment
	private String HOST_FILE_PATH = "/eniq/sw/conf/service_names" ;
		
	//Path of ETLCServer.properties file in unix & local environment
	private String HOST_ETLCFILE_PATH = "/eniq/sw/conf/ETLCServer.properties" ;	
	
	@Override
	protected void setUp() throws Exception {
		if(!System.getProperty("user.dir").contains("ant_common")){
			  System.setProperty("integration_host", "atrcx892zone3.athtem.eei.ericsson.se");
		}
		server = System.getProperty("integration_host","atrcx892zone3.athtem.eei.ericsson.se");
		userName = "dcuser" ; //  needs to be changed in future
		passWord = "dcuser" ; // needs to be changed in future
	}
	
	@Test
	public void testCopyRemoteFile(){
		try{
			File f= new File(localFileLocation);
			if(f.exists()){
				f.delete();
			}
			boolean copyStatus =  HostFileParser.copyRemoteFile(server, userName, passWord, remoteFile, localFileLocation);
			if(copyStatus && !f.exists()){
				fail("File: " + remoteFile + " not copied correctly to localFile: " + localFileLocation);
			}
		}catch(final Exception e){
			fail("Exception came: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCopyLocalFile(){
		try{
			File f= new File(localFileLocation);
			if(!f.exists()){
				f.createNewFile();
			}
			boolean copyStatus =  HostFileParser.copyRemoteFile("localhost" , userName, passWord, localFileLocation, localFileLocation);
			if(copyStatus && !f.exists()){
				fail("File: " + remoteFile + " not copied correctly to localFile: " + localFileLocation);
			}
		}catch(final Exception e){
			e.printStackTrace();
			fail("Exception came: " + e.getMessage());
			
		}
	}
	
	@Test
	public void testIsValidIp(){
		final List<String> correctIPs = Arrays.asList("10.2.3.4", "100.200.200.100", "1.1.1.1");
		final List<String> inCorrectIPs = Arrays.asList("10.2.3", "100.200.256.100", "-1.1.1.1");
		//Using reflection to run private method
		try{
			Class<HostFileParser> hostClass = HostFileParser.class ;
			Class[] paramTypes = {String.class };
			Method method = hostClass.getDeclaredMethod("isValidIp", paramTypes);
			method.setAccessible(true);
			for(String correctIP : correctIPs){
				Boolean res = (Boolean)method.invoke(hostClass, correctIP) ;
				if(!res.booleanValue()){
					fail("IP: " + correctIP + " is expected as valid, why testcase is giving this as invalid IP." );
				}
			}
			for(String inCorrectIP : inCorrectIPs){
				Boolean res = (Boolean)method.invoke(hostClass, inCorrectIP) ;
				if(res.booleanValue()){
					fail("IP: " + inCorrectIP + " is expected as invalid, why testcase is giving it as valid IP." );
				}
			}
		}catch(final Exception e){
			e.printStackTrace();
			fail("Exception came: " + e.getMessage());
		}
	}
	
	@Test
	public void testParseETLServerPropertyFile(){
		try{
			//Using reflection to set private instance variables
			Field privateServiceNameFileField = HostFileParser.class.getDeclaredField("HOST_FILE_PATH");
			Field privateETLServerPropFileField = HostFileParser.class.getDeclaredField("HOST_ETLCFILE_PATH");
			privateServiceNameFileField.setAccessible(true);
			privateETLServerPropFileField.setAccessible(true);
			privateServiceNameFileField.set(HostFileParser.class, HOST_FILE_PATH);
			privateETLServerPropFileField.set(HostFileParser.class, HOST_ETLCFILE_PATH);
			if(!HostFileParser.parseETLCSrvPrpFile(server, userName, passWord)){
				fail("Failed to parse remote file: /eniq/sw/conf/ETLCServer.properties on server" + server);
			}
			final String etlUN = HostFileParser.getETLDBUserName() ;
			final String etlPS = HostFileParser.getETLDBPassword() ;
			final String etlURL = HostFileParser.getETLDBUrl() ;
			if(etlUN == null || etlUN.length() <= 0 || etlPS == null || etlPS.length() <=0 ||
					etlURL == null || etlURL.length() <=0 ){
				fail("Fetched ETL username or password or url is null.");
			}
		}catch(final Exception e){
			e.printStackTrace();
			fail("Exception came: " + e.getMessage());
		}
	}
	
	@Test
	public void testParseServiceNamesFile(){
		try{
			//Using reflection to set private instance variables
			Field privateServiceNameFileField = HostFileParser.class.getDeclaredField("HOST_FILE_PATH");
			Field privateETLServerPropFileField = HostFileParser.class.getDeclaredField("HOST_ETLCFILE_PATH");
			privateServiceNameFileField.setAccessible(true);
			privateETLServerPropFileField.setAccessible(true);
			privateServiceNameFileField.set(HostFileParser.class, HOST_FILE_PATH);
			privateETLServerPropFileField.set(HostFileParser.class, HOST_ETLCFILE_PATH);
			if(!HostFileParser.parseRemoteServiceNamesFile(server, userName, passWord)){
				fail("Failed to parse remote file: /eniq/sw/conf/service_names on server" + server);
			}
			Map<String,String> map = HostFileParser.getServiceIpMap();
			if(map == null || map.size() < 4){
				fail("Failed to populate all services in Service IP map correctly. Current entries are: " + map.toString());
			}
		}catch(final Exception e){
			e.printStackTrace();
			fail("Exception came: " + e.getMessage());
		}
	}
	
	
}
