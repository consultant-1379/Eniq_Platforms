package com.ericsson.eniq.techpacksdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Useraccount;
import com.distocraft.dc5000.repository.dwhrep.UseraccountFactory;
import com.ericsson.eniq.repository.AsciiCrypter;
import com.ericsson.eniq.techpacksdk.common.HostFileParser;
import com.ericsson.eniq.techpacksdk.common.RockFactoryTPIDE;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class LoginTask extends Task<Void, Void> {

  final private static Logger log = Logger.getLogger(LoginTask.class.getName());

  final private TechPackIDE tpide;

  final private ResourceMap resourceMap;

  static private LoginPanel lp;

  private DataModelController dmc;

  private User currentUser = null;

  private static String dbUrl;
  
  private static String serverEngine ;
  
  private static String serverScheduler ;
  
  private static String serverRepDb ;
  
  private static String serverDwhDb ;

  private final String dbDriver;
  
  private static RockFactory etlrepRock ;
  
  private static RockFactory dwhrepRock ;
  
  private static RockFactory dwhRock ;

  private static RockFactory dbaDwhRock ;
  
  //Path of ETLCServer.properties file in unix & local environment
  private static String ETLCFILE_PATH = "/eniq/sw/conf/ETLCServer.properties" ;
  
  

  @SuppressWarnings("static-access")
public LoginTask(final TechPackIDE tpide, final ResourceMap resourceMap, final LoginPanel lp, String dbUrl,
      String dbDriver) {
    super(tpide);

    this.tpide = tpide;
    this.lp = lp;
    this.resourceMap = resourceMap;
    this.dbUrl = dbUrl;
    this.dbDriver = dbDriver;
  }

  @Override
  protected Void doInBackground() throws Exception {

    final java.util.Properties props = new java.util.Properties();

    final InputStream staticPropertiesInputStream     = getClass().getClassLoader().getResourceAsStream("static.properties");
    

    if (staticPropertiesInputStream == null) {
      throw new IOException("Unable to read configFile from JAR");
    }


    props.load(staticPropertiesInputStream);

    StaticProperties.giveProperties(props);

    final String dbserver = lp.getServer();
    if (dbserver == null || dbserver.length() <= 0) {
      throw new Exception("Check parameter " + resourceMap.getString("ManageServerDialog.hostname"));
    }
    
    //Check whether the property set for this server is old or new
    if(lp.getETLDBPort() != null){
    	//Entry is old needs to migrate
    	JOptionPane.showMessageDialog(tpide.getMainFrame(), "Migrating IDE connection properties for this server: " + dbserver,
    			"IDE Task",  JOptionPane.INFORMATION_MESSAGE);
      	log.info(" Migrating right now.");
      	final String etlUser = lp.getETLDBUser() ;
      	final String etlPass = lp.getETLDBPass() ;
      	if(etlUser == null || etlUser.length() <= 0 || etlPass == null || etlPass.length() <= 0){
      		log.severe("ETL details are blank in your current IDE connection properties file for server: " + dbserver);
      		TechPackIDE.logFrame.setVisible(true);
      		throw new Exception("ETL details are blank in your current IDE connection properties file for server: " + dbserver);
      	}
      	//Migrate the entry for server
      	try{
      		migrateIDEConnPrp(dbserver, lp.getETLDBPort().trim(), etlUser, etlPass);
      	}catch(final Exception e){
      		log.log(Level.SEVERE, "Failed to migrate the old entries of IDE connection properties file.",e );
      		TechPackIDE.logFrame.setVisible(true);
      		throw new Exception("Failed to migrate the old entries of IDE connection properties file.");
      	}
    }
    
    //Already migrated entry
    //Get the Server userName and passWord
    final String srvUserName = lp.getSrvUser();
    if (srvUserName == null || srvUserName.length() <= 0) {
      throw new Exception("Check parameter " + resourceMap.getString("ManageServerDialog.user"));
    }
    
    String srvPassword = lp.getSrvPass();
    //Decrypt the password first
  	try{
  		srvPassword = AsciiCrypter.getInstance().decrypt(srvPassword);
  	}catch(final Exception e){
  		//decryption fails, may be the password was not encrypted
  		log.log(Level.WARNING, "Failed to decrypt the password.", e);
  		throw new Exception("Failed to decrypt the server password.");
  	}
    if (srvPassword == null || srvPassword.length() <= 0) {
      throw new Exception("Check parameter " + resourceMap.getString("ManageServerDialog.password"));
    }
    
    // Commented because of 17B changes
    /*//Get the ETLREP username and password
    if(!HostFileParser.parseETLCSrvPrpFile(dbserver, srvUserName, srvPassword)){
    	//Show dialog box
    	String errMsg = " Failed to get the ETLC Username and password. Can not continue.";
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception(errMsg);
    }*/
    
	final String etlDbUn = lp.getETLDBUser();//HostFileParser.getETLDBUserName() ;
	final String etlDbPass = lp.getETLDBPass();//HostFileParser.getETLDBPassword() ;
	final String etlDbUrl = this.dbUrl;//HostFileParser.getETLDBUrl();
	if(etlDbUn == null || etlDbUn.length() <= 0 || etlDbPass == null ||  etlDbPass.length() <= 0 || etlDbUrl == null || etlDbUrl.length() <= 0 ) {
		//Error message. This scenario should not happen
		String errMsg = " ETLC User details does not fetched correctly. Can not continue.";
		TechPackIDE.logFrame.setVisible(true);
		throw new Exception(errMsg);
	}

    final String dbuser = etlDbUn ;
    final String dbpass = etlDbPass ;
    
    //Fetch the port from ETL DB URL
    final String dbport = resourceMap.getString("etlDbPort");//fetchPortFrmUrl(etlDbUrl);
    if(dbport == null || dbport.length() <= 0){
    	String errMsg = "Port number for ETLrep DB does not fetched correctly.";
    	TechPackIDE.logFrame.setVisible(true);
		throw new Exception(errMsg);
    }
    
    // Commented because of 17B changes
    //Use this Servre name dbserver to get the hostfile from this
    /*if(!HostFileParser.parseRemoteServiceNamesFile(dbserver, srvUserName, srvPassword)){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to parse the remote service_names file for server: " + dbserver + " Can not continue.");
    }*/
    
    Map<String, String> srvIpMap = getServiceIP(dbserver, srvUserName, srvPassword);//HostFileParser.getServiceIpMap();
    
    //Server for engine
    if((serverEngine = srvIpMap.get("engine")) == null){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to get the server name for engine. Can not continue.");
    }
    
    //Server for scheduler
    if((serverScheduler = srvIpMap.get("scheduler")) == null){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to get the server name for scheduler. Can not continue.");
    }
    
    //Server for repDB
    if((serverRepDb = srvIpMap.get("repdb")) == null){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to get the server name for repdb. Can not continue.");
    }
    
    //Server for dwhdb
    if((serverDwhDb = srvIpMap.get("dwhdb")) == null){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to get the server name for dwhdb. Can not continue.");
    }
    // Commented because of 17B changes
    /*//Copy the ETLCServer.properties file from server
    if(!HostFileParser.copyRemoteFile(dbserver, srvUserName, srvPassword, ETLCFILE_PATH, ETLCFILE_PATH)){
    	TechPackIDE.logFrame.setVisible(true);
    	throw new Exception("Failed to copy file: " + ETLCFILE_PATH + " Can not continue.");
    }*/
    
    etlrepRock = null;

    try {

      // Add the real host and port values to the database URL, if needed.
      int ix;
      if ((ix = dbUrl.indexOf("@HOST@")) >= 0) {
        dbUrl = dbUrl.substring(0, ix) + serverRepDb + dbUrl.substring(ix + 6);
      }
      if ((ix = dbUrl.indexOf("@PORT@")) >= 0) {
        dbUrl = dbUrl.substring(0, ix) + dbport + dbUrl.substring(ix + 6);
      }

      //For SQL Anywhere we need to append the authentication string. This is got from 
      //ETLCServer.properties file.
      final String authentication = getEtlRepAuthenticationDetails();
      if(authentication == null){
    	  throw new Exception("Parameter etlAuth not found in TechPackIDE.properties: " + resourceMap.getResourcesDir());
      }
      dbUrl = dbUrl + authentication;

      log.info("Connecting ETLREP @ " + dbUrl);

      etlrepRock = new RockFactoryTPIDE(dbUrl, dbuser, dbpass, dbDriver, "TechPackIde", true);

      log.info("Connected to ETLREP");

    } catch (Exception e) {
    		log.log(Level.WARNING, "Unable to connect into ETLREP", e);
    	      throw new Exception("Connection failure: Check server settings");
    }

    final Meta_databases mdb = new Meta_databases(etlrepRock);
    mdb.setConnection_name("dwhrep");
    mdb.setType_name("USER");
    final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlrepRock, mdb);
    final Meta_databases dwhrepmdb = mdbf.get().get(0);

    dwhrepRock = null;

    try {

      String dwhRepDbURL = dbUrl;//dwhrepmdb.getConnection_string();

      // HACK Meta_databases currently uses servicename repdb
      int ix;
      if ((ix = dwhRepDbURL.indexOf("repdb")) >= 0) {
        dwhRepDbURL = dwhRepDbURL.substring(0, ix) + serverRepDb + dwhRepDbURL.substring(ix + 5);
      }

      log.fine("Connecting DWHREP @ " + dwhRepDbURL);

      dwhrepRock = new RockFactoryTPIDE(dwhRepDbURL, dwhrepmdb.getUsername(), dwhrepmdb.getPassword(), dbDriver, 
    		  "TechPackIde", true);

      log.info("Connected to DWHREP");

    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to connect into DWHREP", e);
      throw new Exception("Connection failure: Unable to connect dwhrep database");
    }

    final String ideUser = lp.getUser();
    final char[] idePass = lp.getPass();

    Useraccount useraccount = null;
    Vector<Useraccount> users = null;

    try {

      final Useraccount ua = new Useraccount(dwhrepRock);
      ua.setName(ideUser);
      final UseraccountFactory uaf = new UseraccountFactory(dwhrepRock, ua);
      users = uaf.get();

    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to get user account", e);
      throw new Exception("Connection failure: Server version < ENIQ 1.3");
    }

    if (users == null || users.size() <= 0) {
      throw new Exception("Connection failure: No such user \"" + ideUser + "\"");
    }

    // For checking user name case because IQ on case insensitive
    for (int ix = 0; ix < users.size(); ix++) {

      useraccount = users.get(ix);

      if (!useraccount.getName().equals(ideUser)) {
        useraccount = null;
        continue;
      }

      if (!isPasswordCorrect(idePass, useraccount.getPassword().toCharArray())) {
        throw new Exception("Connection failure: Incorrect password");
      }
    }

    if (useraccount == null) {
      throw new Exception("Connection failure: No such user \"" + ideUser + "\", check case");
    }

    currentUser = new User(useraccount);

    useraccount.setLastlogin(new Timestamp(System.currentTimeMillis()));
    useraccount.updateDB();

    final Meta_databases mdbdwh = new Meta_databases(etlrepRock);
    mdbdwh.setConnection_name("dwh");
    mdbdwh.setType_name("USER");
    final Meta_databasesFactory mdbdwhf = new Meta_databasesFactory(etlrepRock, mdbdwh);
    final Meta_databases dwhdb = mdbdwhf.get().get(0);

    final Meta_databases mdbadwhdb = new Meta_databases(etlrepRock);
    mdbadwhdb.setConnection_name("dwh");
    mdbadwhdb.setType_name("DBA");
    final Meta_databasesFactory mdbadwhdbf = new Meta_databasesFactory(etlrepRock, mdbadwhdb);
    final Meta_databases dbadwhdb = mdbadwhdbf.get().get(0);

    dwhRock = null;

    dbaDwhRock = null;

    try {

      String dwhdbUrl = resourceMap.getString("etlDbURL");//getDwhdbUrl(dbserver, dwhdb.getConnection_string());
      final String dwhPort = resourceMap.getString("dwhDbPort");
      final String dwhParam =  resourceMap.getString("dwhURLParam");
      int ix;
      if ((ix = dwhdbUrl.indexOf("@HOST@")) >= 0) {
    	  dwhdbUrl = dwhdbUrl.substring(0, ix) + serverDwhDb + dwhdbUrl.substring(ix + 6);
      }
      if ((ix = dwhdbUrl.indexOf("@PORT@")) >= 0) {
    	  dwhdbUrl = dwhdbUrl.substring(0, ix) + dwhPort + dwhdbUrl.substring(ix + 6);
      }
      dwhdbUrl = dwhdbUrl + dwhParam;
      
      log.fine("Connecting DWH @ " + dwhdbUrl);

      dwhRock = new RockFactoryTPIDE(dwhdbUrl, dwhdb.getUsername(), dwhdb.getPassword(), dbDriver,
          "TechPackIde",
          true);

      dbaDwhRock = new RockFactoryTPIDE(dwhdbUrl, dbadwhdb.getUsername(), dbadwhdb.getPassword(), dbDriver,
          "TechPackIde", true);

      log.info("Connected to DWH");

    } catch (Exception e) {
      log.log(Level.WARNING, "Unable to connect into DWH", e);
      throw new Exception("Connection failure: Unable to connect dwh database");
    }

    final String engRef = resourceMap.getString("Engine.rmiref");
    final String schRef = resourceMap.getString("Scheduler.rmiref");

    int rmiPort = 1200;

    try {
      rmiPort = Integer.parseInt(resourceMap.getString("Engine.rmiport"));
    } catch (NumberFormatException e) {
      log.log(Level.WARNING, "RMI port is misconfigured.", e);
    }

    log.fine("Creating DataModelController");

    dmc = new DataModelController(tpide, dwhrepRock, etlrepRock, dwhRock, dbaDwhRock, currentUser, lp.getWdir(),
        serverEngine, serverScheduler, dbserver, rmiPort, engRef, schRef);

    log.fine("DataModelController initialized");

    return null;
  }

  /**
 * @param srvPassword 
 * @param srvUserName 
 * @param dbserver 
 * @return
 * @throws JSchException 
 * @throws IOException 
 */
private Map<String, String> getServiceIP(String dbserver, String srvUserName, String srvPassword) throws JSchException, IOException {
	Map<String, String> serviceIP = new HashMap<String, String>();
	Properties config = new Properties(); 
	config.put("StrictHostKeyChecking", "no");
	final String servNamePath = resourceMap.getString("servNamePath");
	final String command = "cat " + servNamePath;
	Session session = null;
	Channel channel = null;
	try{
	JSch jsch = new JSch();
	session = jsch.getSession(srvUserName, dbserver);
	session.setPassword(srvPassword);
	session.setConfig(config);
	session.connect();
	channel = session.openChannel("exec");
	((ChannelExec) channel).setCommand(command);
	channel.setInputStream(null);
	((ChannelExec) channel).setErrStream(System.err);
	channel.connect();
	InputStream in = channel.getInputStream();
	InputStreamReader isr = new InputStreamReader(in);
	BufferedReader br = new BufferedReader(isr);
	try{
		String line = br.readLine();
		while (line != null){
			if(line.contains("::")){
			switch(line.substring(line.lastIndexOf(":") + 1)){
			case "repdb": serviceIP.put("repdb", line.substring(0, line.indexOf("::")));
			break;
			case "dwhdb": serviceIP.put("dwhdb", line.substring(0, line.indexOf("::")));
			break;
			case "engine": serviceIP.put("engine", line.substring(0, line.indexOf("::")));
			break;
			case "scheduler": serviceIP.put("scheduler", line.substring(0, line.indexOf("::")));
			break;
			default: break;
			}
			}
			line= br.readLine();
		}
	}
	catch (Exception se){
		log.severe("Cannot read service names file - " + se);
	}
	finally{
		try{
			br.close();
			isr.close();
			in.close();
		}
		catch (Exception se){
			log.warning("Problem with reader disconnect" + se);
		}
	}
	}
	catch (Exception e){
		log.severe("Cannot connect to server - " + e);
	}
	finally{
		try{
	channel.disconnect();
	session.disconnect();
		}
		catch(Exception e){
			log.warning("Problem with JSCH disconnect" + e);
		}
	}
	return serviceIP;
}

protected static String getDwhdbUrl(final String dbServer, final String connectionString) {
    String dwhdbUrl = "";
    int ix;
    if ((ix = connectionString.indexOf("dwh")) >= 0) {
      String dwhServer = getDwhServerFromProps(connectionString, ix);
      String endOfUrl = connectionString.substring(connectionString.lastIndexOf(":"));
      if (dwhServer == null) {
        dwhdbUrl = connectionString.substring(0, ix) + dbServer + endOfUrl;
      } else {
        dwhdbUrl = connectionString.substring(0, ix) + dwhServer + endOfUrl;
      }
    }
    return dwhdbUrl;
  }

  private static String getDwhServerFromProps(final String connectionString, int ix) {
    String dwhServer = null;
    String dwhConnectionDetails = System.getProperty("dwh.connection.details");

    if (dwhConnectionDetails != null) {
      String dwhPartOfUrl = connectionString.substring(ix, connectionString.lastIndexOf(":"));
      StringTokenizer tokens = new StringTokenizer(dwhConnectionDetails, ",");

      while (tokens.hasMoreTokens()) {
        String token = tokens.nextToken();

        if (token.endsWith(dwhPartOfUrl)) {
          dwhServer = token.substring(0, token.lastIndexOf("="));
          break;
        }
      }
    }

    return dwhServer;
  }

  private String fetchPortFrmUrl(final String etlDbUrl) {
	String port = null ;
	try{
		StringTokenizer tokenize = new StringTokenizer(etlDbUrl, "?");
		String urlWithoutAuth = tokenize.nextToken();
		port = urlWithoutAuth.substring(urlWithoutAuth.lastIndexOf(':')+1,urlWithoutAuth.length());
	}catch(final Exception e){
		final String errMsg = "Failed to get the Port number from ETL DB URL: " + etlDbUrl ;
		log.severe(errMsg);
		port = null ;
	}
	return port;
  }

  /**
   * To migrate the old IDE connection propert entry to new format
   * @param srv
   * @param port
   * @param etlUser
   * @param etlPass
   * @throws Exception
   */
  private void migrateIDEConnPrp(final String srv, final String port, final String etlUser, final String etlPass) throws Exception{
	  
	  //Get the etlrep username and password from propertis
	  //Create ETLRep connection with etlrep username and etlrep as password
	  etlrepRock = null;
	  try {
		  // Add the real host and port values to the database URL, if needed.
	      int ix;
	      if ((ix = dbUrl.indexOf("@HOST@")) >= 0) {
	        dbUrl = dbUrl.substring(0, ix) + srv + dbUrl.substring(ix + 6);
	      }
	      if ((ix = dbUrl.indexOf("@PORT@")) >= 0) {
	        dbUrl = dbUrl.substring(0, ix) + port + dbUrl.substring(ix + 6);
	      }
	      
	      log.fine("Connecting ETLREP @ " + dbUrl);
	      etlrepRock = new RockFactoryTPIDE(dbUrl, etlUser, etlPass, dbDriver, "TechPackIde", true);
	      log.info("Connected to ETLREP");
	  }catch (Exception e) {
		  log.log(Level.WARNING, "Unable to connect into ETLREP", e);
	      throw new Exception("Connection failure: Check server settings");
	  }
	    
	  //Get the server login details
	  String srvUserName = null;
	  String srvPassword = null;
	  try{
		  final Meta_databases md = new Meta_databases(etlrepRock);
		  md.setConnection_name("repdb");
		  final Meta_databasesFactory mdbsrv = new Meta_databasesFactory(etlrepRock, md);
		  final Meta_databases srvMetaDB = mdbsrv.get().get(0);
		  srvUserName = srvMetaDB.getUsername();
		  srvPassword = srvMetaDB.getPassword();
	  }catch(final Exception e){
		  final String errMsg = "Failed to get the server details from ETL database. Can not migrate." ;
		  log.log(Level.SEVERE, errMsg, e);
		  throw new Exception(errMsg);
	  }
	  
	  if(srvUserName == null || srvUserName.length() <= 0 || srvPassword == null || srvPassword.length() <= 0){
		  final String errMsg = "Server details fetched from ETL database are null/empty. Can not migrate." ;
		  log.severe(errMsg);
		  throw new Exception(errMsg);
	  }
	  
	  String encSrvPass = null ;
	  //Encrypting the password for server
	  try{
		  encSrvPass = AsciiCrypter.getInstance().encrypt(srvPassword.trim());
	  }catch(final Exception e){
		  //encryption fails, saving the password in text only
		  final String errMsg = "Failed to encrypt the password. Can not migrate." ;
		  log.log(Level.SEVERE, errMsg, e);
		  throw new Exception(errMsg);
	  }
	  if(encSrvPass == null || encSrvPass.length() <= 0){
		  final String errMsg = "Failed to encrypt the password. Can not migrate" ;
		  log.severe(errMsg);
		  throw new Exception(errMsg);
	  }
	  
	  //Migrate the properties now with the fetched details
	  try{
		  Properties ideConnPrp = lp.getProp();
		  ideConnPrp.remove(srv + ".port");
		  ideConnPrp.setProperty(srv + ".user", srvUserName);
		  ideConnPrp.setProperty(srv + ".pass", encSrvPass);
		  storeConnectionProperties(ideConnPrp);
	  }catch(final Exception e){
		  final String errMsg = "Failed to set properties in IDE connection property file. Can not migrate. " + e.getMessage() ;
		  //Adding the port back in propert if it already deleted
		  Properties ideConnPrp = lp.getProp();
		  ideConnPrp.setProperty(srv + ".port",port);
		  log.log(Level.SEVERE, errMsg, e);
		  throw new Exception(errMsg);
	  }
	  
	  log.info("Migration for IDE connection properties was successful for server; " + srv);
  }
  
  /**
   * To store the connection properties in IDE connection property file
   * @param prp
   * @throws Exception
   */
  private void storeConnectionProperties(final Properties prp) throws Exception{
	    FileOutputStream fos = null;
	    File target = new File(TechPackIDE.CONPROPS_FILE);
	    try {
	      log.fine("Storing values in " + target.getAbsolutePath());
	      fos = new FileOutputStream(target);
	      prp.store(fos, "Stored by TPIDE " + (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(new Date()));
	    } catch (Exception e) {
	      throw new Exception("Failed to write connection properties file to " + target.getAbsolutePath());
	    } finally {
	      try {
	        fos.close();
	      } catch (Exception e) {
	      }
	    }
  }

  public DataModelController getController() {
    return dmc;
  }

  @Override
  protected void succeeded(final Void result) {
    tpide.loggedin(dmc, currentUser, lp.getServer());
  }

  @Override
  protected void failed(final Throwable cause) {
    log.log(Level.INFO, "Login failed", cause);

    JOptionPane.showMessageDialog(tpide.getMainFrame(), cause.getMessage(), resourceMap
        .getString("Login.loginFailed.title"), JOptionPane.ERROR_MESSAGE);

  }

  private boolean isPasswordCorrect(char[] input, char[] correctPassword) {
    boolean isCorrect = false;

    if (input.length != correctPassword.length) {
      isCorrect = false;
    } else {
      isCorrect = Arrays.equals(input, correctPassword);
    }

    return isCorrect;
  }
  
  /**
   * Get the ETLREP db authentication details.
   * Doing it this way so as not to change the constructor which would effect Engine.
   * @return Map containing the connection authentication to connect a RockFactory for etlrep
   * @throws Exception If there are any errors..
   */
  public String getEtlRepAuthenticationDetails() throws Exception {
      return tpide.getEtlAuth();
  }
  
  public static void  closeAllDBConn(){
	  try{
		  if((etlrepRock != null) && (etlrepRock.getConnection() != null)){
			  etlrepRock.getConnection().close();
		  }
		  if((dwhrepRock != null) && (dwhrepRock.getConnection() != null)){
			  dwhrepRock.getConnection().close();
		  }
		  if((dwhRock != null) && (dwhRock.getConnection() != null)){
			  dwhRock.getConnection().close();
		  }
		  if((dbaDwhRock != null) && (dbaDwhRock.getConnection() != null)){
			  dbaDwhRock.getConnection().close();
		  }
	  }catch(Exception e){
		  System.out.println(" Could not close connection successfully. Exception Message : " + e.getMessage());
		  log.log(Level.WARNING, " Could not close connection successfully. Exception Message : " + e.getMessage());
	  }
  }
  
  /**
   * To get the URL of the server on which IDE is getting connected
   */
  public static String getDbUrl(){
	  return dbUrl ;
  }
  
  /**
   * To get the UserName who is using IDE
   * @return
   */
  public static String getUserName(){
	  return lp.getUser() ;
  }
  
  /**
   * To get the ServerName on which IDE is connected
   * @return
   */
  public static String getServerName(){
	  return lp.getServer() ;
  }
}