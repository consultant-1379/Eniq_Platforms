package com.ericsson.eniq.techpacksdk.common;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.io.IOUtils;

public class FTPSessionServices {
	
	private static final Logger log = Logger.getLogger(FTPSessionServices.class.getName());
	private static String server ;
	private static String userName ;
	private static String password ;
	
    /**
     * To get the FTPSession object -- Global use
     * @param srv
     * @param un
     * @param pass
     * @return
     */
	public static FTPClient getFTPSession(final String srv, final String un, final String pass){
		server = srv ;
		userName = un ;
		password = pass ;
		return getSession() ;
	}
	
	/**
	 * To get the FTPSession object -- Private use only
	 * @return
	 */
	private static FTPClient getSession(){
		log.finest("Going to create FTP session for server: " + server);
		log.finest("Going to create FTP session for server: " + server + " with username: " + userName + " and password: " + password);
		FTPClient ftp = null ;
		boolean err = false ;
		try{
			ftp = new FTPClient();
			if(ftp != null){
				ftp.connect(server);
				log.info("Connected to server: " + server);
				int reply;
				reply = ftp.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)){
					err = true ;
					log.severe("FTP server refused connection for server: " + server);
					if(ftp.isConnected()){
						ftp.disconnect();
					}
				}else{
					if (!ftp.login(userName, password)){
						ftp.logout();
						err = true ;
						log.severe(" Failed to login into the server: " + server);
					}else{
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
						ftp.enterLocalPassiveMode();
						log.finest("Successfully created FTP session for server: " + server);
					}
				}//if(ftp != null){
			}
		}catch(final Exception e){
			String msg = "Can not create FTP session to server: " + server ;
			log.log(Level.SEVERE, msg, e);
			err = true ;
			if (ftp != null && ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (final Exception e1) {
					//Nothing to do
				}//inner catch
			} 
		}// outer catch
		if(err){
			return null ;
		}else{
			return ftp ;
		}
	}
	
	/**
	 * To Read a remote file
	 * @param ftp
	 * @param remote
	 * @return
	 */
	public static InputStream readFile(final FTPClient ftp, final String remote){
		log.finest("Start reading remote file: " + remote);
		InputStream in = null ;
		boolean err = false ;
		try{
			in = ftp.retrieveFileStream(remote);
		}catch(final Exception e){
			log.severe("Exception comes while reading the remote file: " + remote + " Exception mesaage: " + e.getMessage());
			err = true ;
		}
		log.finest("Stop reading remote file: " + remote + "  |  Reading Status: " + !err);
		if(err){
			return null ;
		}else{
			return in ;
		}	
	}
	
	/**
	 * To copy the remote file to the local system
	 * @param ftp
	 * @param remote
	 * @param local
	 * @return
	 */
	public static boolean copyRemoteFile(final FTPClient ftp, final String remote, final String local){
		log.finest("Start copying remote file: " + remote + " to local file: " + local);
		boolean succ = true ;
		try{
			InputStream in = ftp.retrieveFileStream(remote);
			if(in != null){
				String fileText = IOUtils.toString(in, "UTF-8");
				File file = new File(local);
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				out.writeBytes(fileText);
				out.flush();
				out.close();
				in.close();
			}else{
				log.severe("Reading failed for remote file: " + remote);
				succ = false ;
			}
		}catch(final Exception e){
			log.severe("Exception comes while copying the remote file: " + remote + " to local file: " + local + " Exception mesaage: " + e.getMessage());
			succ = false ;
		}
		log.finest("Stop copying remote file: " + remote + " to local file: " + local + "  |  Copying Status: " + succ);
		return succ ;
	}
	
	
	
	/**
	 * For Testing purpose
	 * @param args
	 */
	public static void main(String [] args){
		FTPClient ftp = FTPSessionServices.getFTPSession("atrcxb1274.athtem.eei.ericsson.se", "dcuser", "dcuser");
		if(ftp == null){
			log.severe("Could not get FTP Session.");
			System.exit(1);
		}
		log.info("Status of copying remote file: " + FTPSessionServices.copyRemoteFile(ftp, "/etc/hosts", "/eniq/sw/ANKIT"));
	}//main ends here
}// class ends here
