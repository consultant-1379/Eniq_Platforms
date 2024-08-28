/**
 * @author eanguan
 */
package com.ericsson.eniq.techpacksdk;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.tools.ant.util.regexp.Regexp;

import sun.security.x509.IPAddressName;

public class CheckLocalSetup {
	
	static Properties connProps ;
	private final static String DBURL = "ENGINE_DB_URL";
	private final static String DBURLVALUE = "jdbc:sqlanywhere:host=repdb:2641" ;
	private final static String DBUSERNAME = "ENGINE_DB_USERNAME";
	private final static String DBUSERNAMEVALUE = "etlrep";
	private final static String DBPASSWORD = "ENGINE_DB_PASSWORD";
	private final static String DBPASSWORDVALUE = "6GKOICE3nLQ=";
	private final static String DBDRIVERNAME = "ENGINE_DB_DRIVERNAME";
	private final static String URL_DELIM = ":" ;
	private final static String COMMENT_TOKEN_HOSTFILE = "#";
	final private static Logger LOGGER = Logger.getLogger(CheckLocalSetup.class.getName());
	private static String message = "";
  
	/**
	 * Check local ETLCServer.properties file of user who is running IDE
	 */
  public static void checkETLCFile() {
	  try{
			final String propertiesFile = System.getProperty("CONF_DIR", "/eniq/sw/conf") + "/ETLCServer.properties";
	        
	        final File propFile = new File(propertiesFile);
	        if(!propFile.exists()){
	        	
	        	String mess = " Error : !!!! ETLCServer.properties not found on the local system. " ;
	        	mess += "\n " ;
	        	mess += " Please get the correct file at below mentioned link: \n" ;
	        	mess += "http://cdmweb.ericsson.se/TeamCenter/controller/ViewDocs?DocumentName=1%2F12704-AOM901076&Latest=true" ;
	        	LOGGER.warning(mess);
            	Object[] options = { "OK" };
            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
            	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            	        null, options, options[0]);
	        }else{
	        	try{
	        		connProps = new ETLCServerFileProperties(propertiesFile);
	        	}catch(Exception ex){
	        		LOGGER.warning(" Error !!!!! Can not read " + propFile.getAbsolutePath() + " file due to exception: \n " + ex.getMessage() );
	        		Object[] options = { "OK" };
	        		final String mess = " Error !!!!! Can not read " + propFile.getAbsolutePath() + " file. " ;
	            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
	            	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
	            	        null, options, options[0]);
	        	}
	        	
	            final boolean result = checkProperties(connProps);
	            if(!result){
	            	LOGGER.warning(" Your " + propFile.getAbsolutePath() + " file has below mentioned problem: " + message);
	            	final String mess = " Your " + propFile.getAbsolutePath() + " file has problems. \n Check IDE Log for more info. ";
	            	Object[] options = { "OK" };
	            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
	            	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
	            	        null, options, options[0]);
	            	TechPackIDE.logFrame.setVisible(true);
	            	
	            }else{
	            	final String mess = " Your " + propFile.getAbsolutePath() + " is OK. " ;
	            	LOGGER.info(mess);
	            	Object[] options = { "OK" };
	            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
	            	        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
	            	        null, options, options[0]);
	            }
	        }
		}catch(Exception exp){
			LOGGER.warning(" Exception comes while checking ETLCServer.properties file on local system ");
			LOGGER.warning(" Exception message : " + exp.getMessage());
		}
  }
  
  /**
   * Check local hosts file of user who is running IDE
   */
  public static void checkHOSTSFile() {
	  try{
		  final String url = LoginTask.getDbUrl() ;
		  // Check if user has logged in or not
		  if(url != null){
			  final String hostsFile = System.getProperty("HOSTS_FILE_DIR", "/Windows/System32/drivers/etc") + "/hosts";
		        
		        final File file = new File(hostsFile);
		        if(!file.exists()){
		        	String mess = " Error : !!!! hostsFile: " + hostsFile + " not found on the local system. " ;
		        	mess += "\n " ;
		        	LOGGER.warning(mess);
	          	Object[] options = { "OK" };
	          	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
	          	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
	          	        null, options, options[0]);
		        }else{
		        	// Read the hosts file
		        	String hostName = null ;
		        	LOGGER.info(" URL fetched: " + url);
		        	StringTokenizer st = new StringTokenizer(url, URL_DELIM);
		        	int count = 0 ;
		        	while(st.hasMoreTokens()){
		        		hostName = st.nextToken();
		        		if(++count == 4){
		        			break;
		        		}
		        	}
		        	if(hostName != null){
		        		LOGGER.info(" IDE is connected to Hostname: " + hostName);
			        	InetAddress inetAdd = InetAddress.getByName(hostName);
			        	final String ipAdd = inetAdd.getHostAddress();
			        	LOGGER.info(" IDE is connected to IPAddress: " + ipAdd);
			        	if(ipAdd != null){
			        		// Start Reading hosts file
			        		FileInputStream fstream = new FileInputStream(hostsFile);
			        		DataInputStream in = new DataInputStream(fstream);
			        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
			        		String strLine;
			        		boolean isOK = true ;
			        		boolean isRepdbEnteryExist = false ;
			        		while ((strLine = br.readLine()) != null)   {
			        			String regExp = "^\\s*$"; // Regular experssion to identify Blank lines
			        			if((!strLine.startsWith(COMMENT_TOKEN_HOSTFILE)) && (!strLine.matches(regExp))){
			        				if(strLine.contains("repdb")){
			        					System.out.println (strLine);
			        					isRepdbEnteryExist = true ;
			        					StringTokenizer tokens = new StringTokenizer(strLine);
			        					if(!tokens.nextToken().trim().equals(ipAdd.trim())){
			        						isOK = false ;
			        						String errMess = "Your host file: " + file.getAbsolutePath() + " needs to be updated to add the following line at the start of the file: " ;
			        						errMess += "\n" ;
			        						errMess += ipAdd + "\t" + "repdb" + "\t" + "dwhdb" ;
			        						errMess += "\n";
			        						errMess += "The above line is needed while upgrading or activating a TechPack. [ Recommended ]";
			        						LOGGER.warning(errMess);
			        		            	final String mess = " Your host file: " + file.getAbsolutePath() + " needs to be updated. \n Check IDE Log for more info. ";
			        		            	Object[] options = { "OK" };
			        		            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
			        		            	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			        		            	        null, options, options[0]);
			        		            	TechPackIDE.logFrame.setVisible(true);
			        					}
			        					break;
			        				}
			        			}else{
			        				// Nothing to do as it is a commented or Blank line in host file
			        			}
			        		}
			        		if(!isRepdbEnteryExist){
			        			isOK = false ;
			        			String errMess = " Your host file: " + file.getAbsolutePath() + " needs to be updated to add the following line at the start of the file: " ;
        						errMess += "\n" ;
        						errMess += ipAdd + "\t" + "repdb" + "\t" + "dwhdb" ;
        						errMess += "\n";
        						errMess += "The above line is needed while upgrading or activating a TechPack. [ Recommended ]";
        						LOGGER.warning(errMess);
        		            	final String mess = " Your host file: " + file.getAbsolutePath() + " needs to be updated. \n Check IDE Log for more info. ";
        		            	Object[] options = { "OK" };
        		            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
        		            	        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
        		            	        null, options, options[0]);
        		            	TechPackIDE.logFrame.setVisible(true);
			        		}
			        		br.close();
			        		in.close();
			        		fstream.close();
			        		if(isOK){
			        			final String mess = " Your " + file.getAbsolutePath() + " is OK. " ;
				            	LOGGER.info(mess);
				            	Object[] options = { "OK" };
				            	JOptionPane.showOptionDialog(null, mess, "Result of Local Setup Check",
				            	        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				            	        null, options, options[0]);
			        		}
			        	}
		        	}
		        }
		  }
		  
	  }catch(Exception exp){
		  LOGGER.warning(" Exception comes while checking ETLCServer.properties file on local system ");
		  LOGGER.warning(" Exception message : " + exp.getMessage());
	  }
  }
  
  /**
   * Function to check the validity of property passed as argument
   * @param str
   * @return
   */
  private static boolean checkProperties(final Properties prop){
	  message = "";
	  if(prop == null){
		  message += "\n---------------------------------------------\n";
		  message += " Property is null." ;
		  message += "\n---------------------------------------------\n";
		  return false ;
	  }
	  boolean result = true ;
	  
	  final String driverName =  prop.getProperty(DBDRIVERNAME) ;
	  if(driverName == null){
		  message += "\n---------------------------------------------\n";
		  message += " Property " + DBDRIVERNAME + " is not set correctly." ;
		  result = false ;
	  }
	  
	  final String dbuserName =  prop.getProperty(DBUSERNAME) ;
	  if(dbuserName == null || !(dbuserName.equals(DBUSERNAMEVALUE))){
		  message += "\n---------------------------------------------\n";
		  message += " Property " + DBUSERNAME + " is not set correctly." ;
		  message += "\n";
		  message += " Please set it to --> " + DBUSERNAMEVALUE ;
		  result = false ;
	  }
	  
	  final String dbpass =  prop.getProperty(DBPASSWORD) ;
	  if(dbpass == null || !(dbpass.equals(DBPASSWORDVALUE))){
		  message += "\n---------------------------------------------\n";
		  message += " Property " + DBPASSWORD + " is not set correctly." ;
		  message += "\n";
		  message += " Please set it to --> " + DBPASSWORDVALUE ;
		  result = false ;
	  }
	  
	  final String dburl =  prop.getProperty(DBURL) ;
	  if(dburl == null || !(dburl.contains(DBURLVALUE))){
		  message += "\n---------------------------------------------\n";
		  message += " Property " + DBURL + " is not set correctly." ;
		  message += "\n";
		  message += " Please set it to --> " + DBURLVALUE ;
		  result = false ;
	  }
	  message += "\n---------------------------------------------";
	  return result ;
  }
  
}//end of class

class ETLCServerFileProperties extends Properties {

	public ETLCServerFileProperties(final String filepath) throws IOException {
	    super();
	    init(filepath);
	  }

	  private void init(final String filepath) throws IOException {
	    final File file = new File(filepath);

	    final FileInputStream fis = new FileInputStream(file);
	    try {
	      load(fis);
	    } finally {
	      fis.close();
	    }
	  }

	  @Override
	  public void load(final InputStream inStream) throws IOException {
	    synchronized (this) {
	      super.load(inStream);
	    }
	  }

	  @Override
	  public void load(final Reader reader) throws IOException {
	    synchronized (this) {
	      super.load(reader);
	    }
	  }

	  @Override
	  public void loadFromXML(final InputStream istream) throws IOException,
	    InvalidPropertiesFormatException {
	    synchronized (this) {
	      super.loadFromXML(istream);
	    }
	  }
}
