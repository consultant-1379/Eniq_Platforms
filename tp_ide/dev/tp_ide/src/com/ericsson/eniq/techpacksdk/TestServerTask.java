package com.ericsson.eniq.techpacksdk;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.repository.dwhrep.Useraccount;
import com.distocraft.dc5000.repository.dwhrep.UseraccountFactory;
import com.ericsson.eniq.techpacksdk.common.HostFileParser;
import com.ericsson.eniq.techpacksdk.common.RockFactoryTPIDE;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class TestServerTask extends Task<Void, Void> {

  final private Logger log = Logger.getLogger(TestServerTask.class.getName());

  private SingleFrameApplication app;

  private ResourceMap resourceMap;

  private final String host;

  private String dbport;

  private final String srvUser;

  private final String srvPass;
  // For 17B changes
  private final String etlUser;
  private final String etlPwd;

  private String dbUrl;

  private String dbDriver;

  public TestServerTask(final SingleFrameApplication app, final ResourceMap resourceMap, final String host,
      final String sUser, final String sPass, final String url, final String dbDriver, final Properties prop, final String etlUser, final String etlPwd) {
    super(app);

    this.app = app;
    this.resourceMap = resourceMap;
    this.host = host;
    this.srvUser = sUser;
    this.srvPass = sPass;
    this.dbDriver = dbDriver;
    this.dbUrl = url;
    this.etlUser = etlUser;
    this.etlPwd = etlPwd;
  }

  @Override
  protected Void doInBackground() throws Exception{

  //Commented because of 17B changes  
  /*//Get the ETLREP username and password
    if(!HostFileParser.parseETLCSrvPrpFile(host, srvUser, srvPass)){
    	//Show dialog box
    	String errMsg = " Failed to get the ETLC Username and password. Can not test connection.";
    	throw new Exception(errMsg);
    }*/
	  
	if(!checkSSHConn(host,srvUser,srvPass)){
		//Show dialog box
    	String errMsg = "Failed to connect to server " + host + ". Please check login credentials.";
    	throw new Exception(errMsg);
	}
    
	final String etlDbUn = etlUser;//HostFileParser.getETLDBUserName() ;
	final String etlDbPass = etlPwd;//HostFileParser.getETLDBPassword() ;
//	final String etlDbUrl = HostFileParser.getETLDBUrl();
	if(etlDbUn == null || etlDbUn.length() <= 0 || etlDbPass == null ||  etlDbPass.length() <= 0){// || etlDbUrl == null || etlDbUrl.length() <= 0){
		//Error message. This scenario should not happen
		String errMsg = " ETL database details does not fetched correctly. Can not test connection.";
		throw new Exception(errMsg);
	}
	
	//Fetch the port from ETL DB URL
    dbport = resourceMap.getString("etlDbPort");//fetchPortFrmUrl(etlDbUrl);
    if(dbport == null || dbport.length() <= 0){
    	String errMsg = "Port number for ETL DB does not fetched correctly.";
		throw new Exception(errMsg);
    }
	
	int ix;
    if ((ix = dbUrl.indexOf("@HOST@")) >= 0) {
      dbUrl = dbUrl.substring(0, ix) + host + dbUrl.substring(ix + 6);
    }
    if ((ix = dbUrl.indexOf("@PORT@")) >= 0) {
      dbUrl = dbUrl.substring(0, ix) + dbport + dbUrl.substring(ix + 6);
    }
    
    log.info("Testing ETLREP @ " + this.dbUrl);
	
	
    RockFactory etlrepRock = null;
    RockFactory dwhrepRock = null;

    try {

      etlrepRock = new RockFactoryTPIDE(this.dbUrl, etlDbUn, etlDbPass, this.dbDriver, "TechPackIde", true);
      if(etlrepRock.getConnection() == null){
    	  log.info("YOU HAVE FAILED!!");
      }
      final Meta_databases mdb = new Meta_databases(etlrepRock);
      mdb.setConnection_name("dwhrep");
      mdb.setType_name("USER");
      final Meta_databasesFactory mdbf = new Meta_databasesFactory(etlrepRock, mdb);
      final Meta_databases dwhrepmdb = (Meta_databases) mdbf.get().get(0);

      log.fine("ETLREP tested successfully");

      String dwhRepDbUrl = this.dbUrl;//dwhrepmdb.getConnection_string();

      // HACK Meta_databases currently uses servicename repdb
      //int ixx;
      //if ((ixx = dwhRepDbUrl.indexOf("repdb")) >= 0) {
      //  dwhRepDbUrl = dwhRepDbUrl.substring(0, ixx) + host + dwhRepDbUrl.substring(ixx + 5);
      //}

      log.fine("Testing DWHREP @ " + dwhRepDbUrl);

      dwhrepRock = new RockFactoryTPIDE(dwhRepDbUrl, dwhrepmdb.getUsername(), dwhrepmdb.getPassword(), this.dbDriver,//dwhrepmdb.getDriver_name(), 
    		  "TechPackIde", true);
      if(dwhrepRock.getConnection() == null){
    	  log.info("YOU HAVE FAILED in DWHREP!!");
      }
      final Useraccount ua = new Useraccount(dwhrepRock);
      final UseraccountFactory uaf = new UseraccountFactory(dwhrepRock, ua);

      log.finer("DWHREP tested successfully. " + uaf.get().size() + " user accounts available.");

    } finally {
      try {
        etlrepRock.getConnection().close();
      } catch (Exception e) {
      }
      try {
        dwhrepRock.getConnection().close();
      } catch (Exception e) {
      }
    }

    return null;
  }

  private boolean checkSSHConn(String host2, String srvUser2, String srvPass2) {
	  try{
		  	Properties config = new Properties(); 
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(srvUser2, host2);
			session.setPassword(srvPass2);
			session.setConfig(config);
			session.connect();
	  }
	  catch (Exception e){
		  log.warning("Cannot connect to server - " + e);
		  return false;
	  }
	  log.info("Connection to server successful.");
	return true;
}

protected void succeeded(Void result) {

    String msg = resourceMap.getString("ManageServerDialog.test.succeed.message");

    int ix;
    if ((ix = msg.indexOf("@HOST@")) >= 0) {
      msg = msg.substring(0, ix) + host + msg.substring(ix + 6);
    }

    JOptionPane.showMessageDialog(app.getMainFrame(), msg, resourceMap
        .getString("ManageServerDialog.test.succeed.title"), JOptionPane.INFORMATION_MESSAGE);

  }

  protected void failed(Throwable cause) {
    log.log(Level.INFO, "Connection test failed", cause);
    String msg = resourceMap.getString("ManageServerDialog.test.failed.message");
    int ix;
    if ((ix = msg.indexOf("@HOST@")) >= 0) {
      msg = msg.substring(0, ix) + host + msg.substring(ix + 6);
    }
    JOptionPane.showMessageDialog(app.getMainFrame(), msg, resourceMap
        .getString("ManageServerDialog.test.failed.title"), JOptionPane.ERROR_MESSAGE);

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

}
