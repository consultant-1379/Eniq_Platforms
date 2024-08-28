package com.ericsson.eniq.techpacksdk.common;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import junit.framework.TestCase;

public class FTPSessionServicesTest extends TestCase {
	
	private String server ;
	private String userName ;
	private String passWord ;
	private String worngUserName = "wrong" ;
	private String worngServer = "wrong" ;
	private String remoteFile = "/etc/hosts" ;
	private String wrongRemoteFile = "wrongFile" ;
	private String localFileLocation = System.getProperty("user.home") + File.separatorChar + "testFile" ;
	
	
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
	public void testGetFTPSessionPositive(){
		
		FTPClient ftp = FTPSessionServices.getFTPSession(server, userName, passWord);
		assertNotNull("FTP Object should not be null. Check the server status: " + server,ftp);
		
	}
	
	@Test
	public void testGetFTPSessionNegativeWrongUser(){
		FTPClient ftp = FTPSessionServices.getFTPSession(server, worngUserName, passWord);
		assertNull("FTP Object should be null.", ftp);
	}
	
	@Test
	public void testGetFTPSessionNegativeWrongServer(){
		FTPClient ftp = FTPSessionServices.getFTPSession(worngServer, userName, passWord);
		assertNull("FTP Object should be null.", ftp);
	}
	
	@Test
	public void testReadFile(){
		FTPClient ftp = FTPSessionServices.getFTPSession(server, userName, passWord);
		InputStream inReader = FTPSessionServices.readFile(ftp, remoteFile);
		assertNotNull("InputStream Object should not be null.", inReader);
	}
	
	@Test
	public void testReadFileNegative(){
		FTPClient ftp = FTPSessionServices.getFTPSession(server, userName, passWord);
		InputStream inReader = FTPSessionServices.readFile(ftp, wrongRemoteFile);
		assertNull("InputStream Object should be null.", inReader);
	}
	
	@Test
	public void testCopyRemoteFile(){
		FTPClient ftp = FTPSessionServices.getFTPSession(server, userName, passWord);
		try{
			File f= new File(localFileLocation);
			if(f.exists()){
				f.delete();
			}
			boolean copyStatus = FTPSessionServices.copyRemoteFile(ftp, remoteFile, localFileLocation);
			if(copyStatus && !f.exists()){
				fail("File: " + remoteFile + " not copied correctly to localFile: " + localFileLocation);
			}
		}catch(final Exception e){
			fail("Exception came: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
