package com.ericsson.eniq.techpacksdk.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import com.ericsson.eniq.repository.ETLCServerProperties;

/**
 * Singleton Class to prase the Host File of unix system
 * @author eanguan
 *
 */
public class HostFileParser {
	
	// Logger
	private static final Logger LOGGER = Logger.getLogger(HostFileParser.class.getName());
	
	//Path of host file in unix environment
	private static String HOST_FILE_PATH = "/eniq/sw/conf/service_names" ;
	
	//Path of ETLCServer.properties file in unix & local environment
	private static String HOST_ETLCFILE_PATH = "/eniq/sw/conf/ETLCServer.properties" ;
	
	//hack for CI
	private static String LOCAL_PATH_CI ;
	
	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	// Map to contain the service as Key and IP address of the server on which it is running as its value
	private static Map<String,String> serviceIpMap ;
	
	// userName for unix server
	private static String ETL_DB_USERNAME ;
		
	//Password for unix server
	private static String ETL_DB_PASS ;
	
	private static String ETL_DB_URL ;

	//IDE require only these services now. If needed can change in future to read from service_names file
	// If you are changing, then make sure that order of entries here will always be same for example: engine should always be at 0 and scheduler at 4
	private static final List<String> serviceNames = Arrays.asList("engine", "dwhdb", "repdb", "scheduler") ;
  private static final int _SN_SCHEDULER = 3;
  private static final int _SN_ENGINE = 0;

	/**
	 * Function to copy the remote file
	 * @param server
	 * @param un
	 * @param pass
	 * @param remoteFile
	 * @param localFile
	 * @return
	 */
	public static boolean copyRemoteFile(final String server, final String un, final String pass, final String remoteFile, String localFile){
		LOGGER.finest("Starts copying remote file: " + remoteFile + " on server: " + server + " to local file: " + localFile);
    boolean copySucc = true ;
    if("localhost".equalsIgnoreCase(server)){
      final File rFile = new File(remoteFile);
      if(rFile.exists()){
        final File lFile = new File(localFile);
        if(!rFile.equals(lFile)){
          try{
            final FileChannel lfc = new FileInputStream(lFile).getChannel();
            final FileChannel rfc = new FileInputStream(rFile).getChannel();
            lfc.transferFrom(rfc, 0, rfc.size());
          } catch (FileNotFoundException e){
            e.printStackTrace();
          } catch (IOException e){
            e.printStackTrace();
          }
        }
        LOGGER.finest("Using local version of " + remoteFile);
      } else {
        LOGGER.severe("Cant copy non existant local file " + remoteFile);
        copySucc = false;
      }
    } else {
    	
    	//Hack to run it in Jenkins
    	if(System.getProperty("user.dir").contains("ant_common")){
    		localFile = System.getProperty("user.home") + File.separatorChar + (new File(localFile)).getName();
    		LOCAL_PATH_CI = localFile ;
    		System.out.println("Running in CI. LocalPath calculated: " + localFile);
    	}

      if(server == null){
        copySucc = false ;
      }else{
        // Set up the FTp Session
        FTPClient ftp = FTPSessionServices.getFTPSession(server, un, pass) ;
        if(ftp == null){
          LOGGER.severe(" Failed to set up a FTP session with the server: " + server);
          copySucc = false ;
        }else{
          //Check whether directory structure /eniq/sw/conf exist on user machine or not
          final File path = new File("/eniq/sw/conf");
          try{
            if(!path.exists()){
              LOGGER.info("Creating path: " + path.getAbsolutePath());
              path.mkdirs();
            }
          }catch(final Exception e){
            LOGGER.log(Level.SEVERE," Exception comes while checking the Path " + path.getAbsolutePath() + " on this machine.",e);
            copySucc = false ;
          }

          //Copy the remote file
          if(!FTPSessionServices.copyRemoteFile(ftp, remoteFile, localFile)){
            LOGGER.severe("Failed to copy remote file: " + remoteFile + " on server: " + server + " to local file: " + localFile);
            copySucc = false ;
          }else{
            LOGGER.info("Successfully copied remote file: " + remoteFile + " on server: " + server + " to local file: " + localFile);
          }
        }
      }
      LOGGER.finest("Stops copying remote host file: " + remoteFile + " on server: " + server + " to local file: " + localFile + " Status: " + copySucc);
    }
		return copySucc ;
	}
	
	/**
	 * Function to parse the ETLCServer.properties file of remote host
	 * @param srv
	 * @return
	 */
	public static boolean parseETLCSrvPrpFile(final String server, final String un, final String pass){
		LOGGER.finest("Starts parsing remote host file: " + HOST_ETLCFILE_PATH + " on server: " + server);
		boolean parseSucc = true ;
		if(!copyRemoteFile(server, un, pass, HOST_ETLCFILE_PATH, HOST_ETLCFILE_PATH)){
			LOGGER.severe(" Failed to copy the remote file: " + HOST_ETLCFILE_PATH);
			parseSucc = false ;
		}else{
			if(!populateETLCUserNameAndPass()){
				LOGGER.severe(" Failed to populate the ETLC username and password from the local file: " + HOST_ETLCFILE_PATH);
				parseSucc = false ;
			}else{
				LOGGER.info(" Successfully populated the ETLC username and password from the local file: " + HOST_ETLCFILE_PATH);
			}
		}
		LOGGER.finest("Stops parsing remote host file: " + HOST_ETLCFILE_PATH + " on server: " + server + " parsed status: " + parseSucc);
		return parseSucc ;
	}
	
	/**
	 * Function to populate the ETLC Username and password in the class variables
	 * @return
	 */
	private static boolean populateETLCUserNameAndPass() {
		LOGGER.finest("Starts populateETLCUserNameAndPass");
		boolean popSucc = true ;
		try{
			//Hack to run it in Jenkins CI
	    	if(System.getProperty("user.dir").contains("ant_common")){
	    		HOST_ETLCFILE_PATH = LOCAL_PATH_CI ;
	    		System.out.println("CI Run: Reading ETLServerProperty file from : " + LOCAL_PATH_CI);
	    	}
			ETLCServerProperties etl = new ETLCServerProperties(HOST_ETLCFILE_PATH);
			Map<String,String> etlConnDetails = etl.getDatabaseConnectionDetails() ;
			if(etlConnDetails == null || etlConnDetails.size() < 1){
				LOGGER.severe("Failed to fetch ETLC username and password. ");
				popSucc = false ;
			}else{
				ETL_DB_USERNAME = etlConnDetails.get("etlrepDatabaseUsername");
				ETL_DB_PASS = etlConnDetails.get("etlrepDatabasePassword");
				ETL_DB_URL = etlConnDetails.get("etlrepDatabaseUrl");
				if((ETL_DB_USERNAME == null) || (ETL_DB_PASS == null) || (ETL_DB_URL == null)){
					LOGGER.severe("Failed to fetch ETL DB details correctly. ");
					popSucc = false ;
				}
			}
		}catch(final Exception e){
			LOGGER.log(Level.SEVERE, " Failed to read the ETLC username and password from local file : " + HOST_ETLCFILE_PATH, e);
			popSucc = false ;
		}
		LOGGER.finest("Stops populateETLCUserNameAndPass " + " Return Status: " + popSucc );
		return popSucc ;
	}
	
	public static String getETLDBUserName(){
		return ETL_DB_USERNAME ;
	}
	
	public static String getETLDBPassword(){
		return ETL_DB_PASS ;
	}
	
	public static String getETLDBUrl(){
		return ETL_DB_URL ;
	}

	/**
	 * Method to parse the host file and populates the map of the given server
	 */
	public static boolean parseRemoteServiceNamesFile(final String server, final String un, final String pass){
		LOGGER.finest("Starts parsing remote host file: " + HOST_FILE_PATH + " on server: " + server);

    if(!copyRemoteFile(server, un, pass, HOST_FILE_PATH, HOST_FILE_PATH)){
      LOGGER.severe(" Failed to read the remote file: " + HOST_FILE_PATH);
      return false;
    }
    FileInputStream reader = null;
    final String fileText;
    try{
    	//Hack to run it in Jenkins CI
    	if(System.getProperty("user.dir").contains("ant_common")){
    		HOST_FILE_PATH = LOCAL_PATH_CI ;
    		System.out.println("CI Run: Reading service_names file from : " + LOCAL_PATH_CI);
    	}
      reader = new FileInputStream(HOST_FILE_PATH);
      fileText = IOUtils.toString(reader, "UTF-8");
    } catch (FileNotFoundException e){
      LOGGER.log(Level.SEVERE, " Failed to parse the file: " + HOST_FILE_PATH, e);
      return false;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, " Failed to parse the file: " + HOST_FILE_PATH, e);
      return false;
    } finally {
      if(reader != null){
        try {
          reader.close();
        } catch (IOException e) {/*--*/}
      }
    }
    
    if(!populateServiceIpMap(fileText)){
      LOGGER.severe(" Failed to populate the serviceIP Map correctly for remote file: " + HOST_FILE_PATH);
      return false;
    }else{
      if(serviceIpMap == null){
        LOGGER.severe("No Service was present in remote host file. Services which should present are: " + serviceNames);
        return false;
      }else{
        //Check that every service present in map or not
        for (String serviceName : serviceNames) {
          if (!serviceIpMap.containsKey(serviceName)) {
            if (serviceName.equals(serviceNames.get(_SN_SCHEDULER))) {
              //By continuing we are making sure that engine service to be populated first
              continue;
            }
            LOGGER.warning("No entry found for service: " + serviceName + " in file: " + HOST_FILE_PATH);
            LOGGER.warning("Using the default server: " + server + " for the service: " + serviceName);
            serviceIpMap.put(serviceName, server);
          }
        }
        //Special Check whether scheduler is set or not as if it is not mentioned in host file then it should be on the same server with engine
        if(!serviceIpMap.containsKey(serviceNames.get(_SN_SCHEDULER))){
          LOGGER.warning("No entry found for service: " + serviceNames.get(_SN_SCHEDULER) + " in file: " + HOST_FILE_PATH );
          LOGGER.warning("Using the same server on which engine is running: " +
            serviceIpMap.get(serviceNames.get(_SN_ENGINE)) + " for the service: " + serviceNames.get(_SN_SCHEDULER) );
          serviceIpMap.put(serviceNames.get(_SN_SCHEDULER), serviceIpMap.get(serviceNames.get(_SN_ENGINE)));
        }
        LOGGER.info("Parsed host file successfully. Fetched Services list: " + serviceIpMap.toString());
      }
    }
		LOGGER.finest("Stops parsing remote host file: " + HOST_FILE_PATH + " on server: " + server);
		return true ;
	}
	
	/**
	 * Function to populate the Map with Service name as key and IPAddress as its value
	 * @param txt
	 * @return true if map populated correctly
	 */
	private static boolean populateServiceIpMap(final String txt){
		LOGGER.finest("Starts populateServiceIpMap");
		boolean popSucc = true ;
		StringTokenizer st = new StringTokenizer(txt, "\n");
		while(st.hasMoreTokens()){
			String line = (st.nextToken()) ;
			if(line == null){
				continue ;
			}
			line = line.trim();
			if(line.startsWith("#")){
				continue ;
			}
      for (String serviceName : serviceNames) {
        if (line.contains(serviceName)) {
          if (!putServiceIpInMap(line, serviceName)) {
            LOGGER.severe(" Failed to populate the serviceIP Map correctly for service: " + serviceName + " in line: " + line);
            popSucc = false;
            break;
          }
        }
      }
		}
		LOGGER.finest("Stops populateServiceIpMap " + " Return Status: " + popSucc );
		return popSucc ;
	}
	
	/**
	 * Function to parse a line and put service 
	 * @param line
	 * @param service
	 * @return
	 */
	private static boolean putServiceIpInMap(String line, String service) {
		LOGGER.finest("Starts putServiceIpInMap for line: " + line + " for service: " + service);
		boolean succ = true ;
		StringTokenizer t = new StringTokenizer(line,":");
		final String ip = t.nextToken() ;
		if(!isValidIp(ip)){
			LOGGER.severe(" In Line: " + line + " The first token: " + ip  + " is not a valid IP address");
			succ = false ;
		}
		if(serviceIpMap == null){
			serviceIpMap = new HashMap<String, String>();
		}
		serviceIpMap.put(service, ip);
		LOGGER.finest("Stops putServiceIpInMap for line: " + line + " for service: " + service);
		return succ ;
	}

	/**
	 * Function to get the IPAddress from the server name
	 * Can be useful in future
	 * @param srv
	 * @return
	 */
	private static String getIpAddress(final String srv){
		LOGGER.finest("Starts getting IPAddress from entered servername: " + srv);
		String ipAddress = null ;
		if(isValidIp(srv)){
			ipAddress = srv ;
		}else{
			// Not a valid Server Name
			//Neesa to change it into IP address format
			try{
				ipAddress = InetAddress.getByName(srv).getHostAddress();
			}catch(final Exception e){
				String errMsg = " Exception comes while converting the serverName: " + srv + " to IPAddress.";
				LOGGER.log(Level.WARNING, errMsg, e);
				ipAddress = null ;
			}
		}
		LOGGER.finest("Stops getting IPAddress from entered servername: " + srv + " Ip address fetched is: " + ipAddress);
		return ipAddress ;
	}

	/**
	 * Function to test whether IPAddress is valid
	 * @param server
	 * @return
	 */
	private static boolean isValidIp(final String server) {
		LOGGER.finest("Starts validating IP Address format for: " + server );
		boolean validIp = false ;
		try{
			Matcher matcher = Pattern.compile(IPADDRESS_PATTERN).matcher(server);
			if(matcher.matches()){
				validIp = true ;
			}else{
				validIp = false ;
			}
		}catch(final Exception e){
			LOGGER.info("The server name entered: " +  server + " is not in IP Address format.");
			validIp = false ;
		}
		LOGGER.finest("Stops validating IP Address format for: " + server + " Status: " + validIp);
		return validIp ;
	}
	
	/**
	 * Function to get the ServiceIP Map
	 * @return
	 */
	public static Map<String,String> getServiceIpMap(){
		return serviceIpMap ;
	}
	
	/**
	 * For Testing purpose only
	 * @param args
	 */
	public static void main(String [] args){
		final String server = "atrcxb1563.athtem.eei.ericsson.se";
		HostFileParser.parseRemoteServiceNamesFile(server,"dcuser", "dcuser");
	}
}
