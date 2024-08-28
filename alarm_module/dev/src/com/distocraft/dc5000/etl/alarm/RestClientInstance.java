package com.distocraft.dc5000.etl.alarm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.ericsson.eniq.common.DatabaseConnections;
import com.ericsson.eniq.common.RemoteExecutor;
import com.ericsson.eniq.repository.DBUsersGet;


import ssc.rockfactory.RockFactory;
import sun.misc.BASE64Decoder;
import com.jcraft.jsch.JSchException;
/**
 * RestClientInstance creates a Client instance and open the ENM session for
 * sending alarms.
 * 
 * @author xsarave
 * 
 */
public class RestClientInstance {

	private  Client client;
	private static RestClientInstance restClientInstance;
	private  String HOST = null;
	private static Boolean sessionCheck = false;
	private String USERNAME = null;
	private  String PASSWORD = null;
	Logger log = null;
	private PoolingHttpClientConnectionManager clientConnectionManager;
	

	private RestClientInstance() {

	}

	/**
	 * This function returns the RestClientInstance class instance and starts
	 * the timer thread.
	 * 
	 * @return Returns the instance of AlarmThreadHandling class.
	 */
	synchronized static RestClientInstance getInstance()
	{
		
		
		if(restClientInstance==null)
		{
			restClientInstance=new RestClientInstance();
			restClientInstance.startTimer();
			
		}
		
		return restClientInstance;
	}
	/**
	 * This function creates the client instance and returns the Client
	 * instance.
	 * 
	 * @param cache
	 *            contains the details of ENMServerDetails class object.
	 * @param log
	 *            contains the Logger class instance.
	 * @return Returns the instance of Client registered with session cookies.
	 */
	synchronized Client getClient(final ENMServerDetails cache, final Logger log) throws IOException {
		
		if (client == null) {
			try{
			this.log = log;
			log.finest("enter inside creation of rest client instance..");
			
			HOST = "https://" + cache.getHost();
			USERNAME = cache.getUsername();
			final String Password_decrypt = cache.getPassword();

			byte[] dec = new BASE64Decoder().decodeBuffer(Password_decrypt);
			final String PASSWORD1 = new String(dec, "UTF8");
			PASSWORD = PASSWORD1.trim();
			log.fine("host username password:" + HOST + " " + USERNAME);
			// To validate server CA certificate
			// In order to import server CA certificate
			// keytool -import -file cacert.pem -alias ENM -keystore
			// truststore.ts -storepass secret
			// And give the location of the keystore
			String keyStorePassValue=getValue("keyStorePassValue").trim();
	
			final SslConfigurator sslConfig = SslConfigurator.newInstance()
					.trustStoreFile(StaticProperties.getProperty("TRUSTSTORE_DIR",
							"/eniq/sw/runtime/jdk/jre/lib/security/truststore.ts"))
					.trustStorePassword(keyStorePassValue);
			final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
					sslConfig.createSSLContext());

			final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslSocketFactory).build();
			// Pooling HTTP Client Connection manager.
			// connections will be re-used from the pool
			// also can be used to enable
			// concurrent connections to the server and also
			// to keep a check on the number of connections
			//

			clientConnectionManager = new PoolingHttpClientConnectionManager(
					registry);
			clientConnectionManager.setMaxTotal(50);
			clientConnectionManager.setDefaultMaxPerRoute(20);

			final ClientConfig clientConfig = new ClientConfig();

			clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, clientConnectionManager);
			clientConfig.connectorProvider(new ApacheConnectorProvider());

			client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
		} catch (Exception e) {
			log.finest("Exception while creating client instance :::   " + e);
		}
		}
		if (sessionCheck == false) {
			getSession();
		}
		log.finest("client instance near return " + client);

		return client;
	}

	/**
	 * This function opens the ENM session for sending the REST request.
	 * 
	 * @param cache
	 *            contains the details of ENMServerDetails class object.
	 * @param log
	 *            contains the Logger class instance.
	 * @return Returns the instance of Client registered with session cookies.
	 */
	 void getSession() {

		try {
			String errorMessage = null;
			
			log.fine("Enter inside session creation");
			log.finest("Host:" + HOST + " " + USERNAME + " " + PASSWORD);
			log.finest("ENM hostname:" + HOST);

			log.finest("username:" + USERNAME);
			log.finest("password:" + (PASSWORD != null ? "********" : "null"));
			
			final WebTarget target = client.target(HOST).path("login").queryParam("IDToken1", USERNAME)
					.queryParam("IDToken2", PASSWORD);
			log.finest("login URL ::::  " + target.toString());
			final Response response = target.request(MediaType.WILDCARD_TYPE).post(Entity.json(""));
			AlarmProcessingTask.lastdate = new Date();
			log.finest("login response ::: " + response + " \n " + response.getStatus());
			Boolean check = false;

			// if the response is client error 401 or any exception that can be
			// successful by retrying,
			// send request for login again
			// and then send the request again for 2 more times
			if (response.getStatus() == 302) {
				sessionCheck = true;
				log.info("Session established...Response code:" + response.getStatus() + "session established:"
						+ response.getHeaders());
			} else {
				log.fine("response header" + response.getHeaders());
				check = true;
				for (int i = 0; i < 2; i++) {
					log.fine("Again session creation request send to server:");
					
					final WebTarget target1 = client.target(HOST).path("login").queryParam("IDToken1", USERNAME)
							.queryParam("IDToken2", PASSWORD);
					log.finest("loggin in again :: URL   :::  " + target1.toString());
					final Response response1 = target1.request(MediaType.WILDCARD_TYPE).post(Entity.json(""));
					log.finest("log in again response ::: " + response1 + " \n " + response1.getStatus());
					if (response1.getStatus() == 302) {
						sessionCheck = true;
						log.info("session established:" + response1.readEntity(String.class));
						check = false;
						break;
					} else {
						errorMessage = "Error Status:" + response1.getStatus() + " ,Response Headers:"
								+ response1.getHeaders();
					}
				}
			}
			if (check == true) {
				log.log(Level.SEVERE,"Session creation request sent to server three times and No success response from server");
				String error = "SESSION CREATION FAILED:" + errorMessage;
				errorTableUpdate(HOST, error, "-","-","-","-","-","-");
			}
		} catch (Exception e) {
			if (e.getCause() instanceof TimeoutException) {
				log.finest("TIMEOUT Exception while logging in ::  " + e);
				String error = "TIMEOUT EXCEPTION:" + e.getMessage();
				errorTableUpdate(HOST, error,"-","-","-","-","-","-");
			} else {
				log.finest("Exception while logging in ::  " + e);
				String error = "Exception:" + e.getMessage();
				errorTableUpdate(HOST, error, "-","-","-","-","-","-");
			}
		}
		
	}

	/**
	 * This function checks the request is not send for more than 30 seconds it
	 * will close the session.
	 * 
	 * @param client
	 *            instance of the Client object
	 * @param client
	 *            instance of Logger object
	 */
	 void sessionCloseCheck(Client client) {

			if (AlarmProcessingTask.lastdate == null) {
				AlarmProcessingTask.lastdate = new Date();
			}
			// log.finest("inside timer method");
			Date date = new Date();
			// log.finest("last executed time " + AlarmProcessingTask.lastdate);
			// log.finest("current time" + date);

			if ((date.getTime() - AlarmProcessingTask.lastdate.getTime()) >= 30000) {
				closeSession();
			}

		}
	 
	 public void closeSession() {
			// TODO Auto-generated method stub

			if (sessionCheck == true) {
				
				log.info("reaches the time difference...going to close the session  ");
				log.fine("client instance" + client);
				final Response response3 = client.target(HOST).path("logout").request("application/json").get();
				log.finest("logging out response ::: " + response3);

				if (response3.getStatus() == 200) {
					sessionCheck = false;
					clientConnectionManager.shutdown();
					client = null;
					AlarmProcessingTask.lastdate = new Date();
					log.info("successfully logged out");
				} else

				{
					sessionCheck = true;
					log.fine("Error in closing the session..session exists");
				}
			}

		}
	 /**
		 * This function insert a row in ALARM_ERROR table if any errors occur during sending of alarms.
		 * 
		 * @param hostName
		 *             Hostname of the ENM server
		 * @param detailsOfError
		 *            Details of the error occured during sending of alarms.
		 * @param nodeName
		 *            name of the node
		 * @param ossName
		 *            alias of the ossname
		 */
	 synchronized void errorTableUpdate(String ENMHostname, String errorDetail, String alarmName,
				String managedObjectInstance, String objectOfReference, String ossName, String reportTitle,
				String eventTime) {
		 	RockFactory dwhdb=null;
			String alarmError = "Insert into DC_Z_ALARM_ERROR (ENMHostname, ErrorDetail, AlarmName ,ManagedObjectInstance ,ObjectOfReference ,OssName ,ReportTitle ,EventTime) Values ('"
					+ ENMHostname + "','" + errorDetail + "','" + alarmName + "','" + managedObjectInstance + "','"
					+ objectOfReference + "','" + ossName + "','" + reportTitle + "','" + eventTime + "')";

			try {
				dwhdb = DatabaseConnections.getDwhDBConnection();
				dwhdb.getConnection().createStatement().executeUpdate(alarmError);
				log.finest("Errors occured during REST connection is stored successfully in DC_Z_ALARM_ERROR table");
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception: " + e);

			}
			finally{
				try {
					if (dwhdb.getConnection() != null) {
						dwhdb.getConnection().close();
					}
				} catch (SQLException e) {
						// TODO Auto-generated catch block
					log.log(Level.SEVERE,"Error in closing the DWH database connection:" + e);
				}
			}
		}
	 /**
		 * This function creates A Timer object and schedule a TimerTask to check
		 * the condition for session closing action.
		 * 
		 */
	public  void startTimer() {

		Timer timer = new Timer();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (client != null) {
					
					log.fine("executing run metghod of TimerTest");
					sessionCloseCheck(client);
				}
			}
		}, 3000, 3000);
	}
	/**
	 * This function read the value from niq.ini file and return the value
	 * @param command
	 * @return String
	 */
	public String getValue(String command){
		String output = "";
    	try {
    		
			String systemCommandString = "";
			final String user = "dcuser";
			final String service_name = "engine";
			List<Meta_databases> mdList = DBUsersGet
					.getMetaDatabases(user,
							service_name);
			if (mdList.isEmpty()) {
				mdList = DBUsersGet.getMetaDatabases(user,service_name);
				if (mdList.isEmpty()) {
				throw new Exception("Could not find an entry for "
						+ user+ ":"
						+ service_name + " in engine! (was is added?)");
				}
			}
			final String password = mdList.get(0).getPassword();
			systemCommandString = ". /eniq/home/dcuser; . ~/.profile; "+"cat /eniq/sw/conf/niq.ini |grep -i "+command;
			 output = RemoteExecutor.executeComand(user,password, service_name, systemCommandString );
			 String[] out2 = output.split("=");
			 output = out2[1];
			return output;
	}catch (final JSchException e) {
		e.printStackTrace();
		log.finest("Exception:"+e);
	} catch (final Exception e) {
		e.printStackTrace();
		log.finest("Exception:"+e);
	}
		return output;	
    }
	


}
