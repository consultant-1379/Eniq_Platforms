package com.distocraft.dc5000.etl.fls;
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

import javax.annotation.*;
//import jersey-client.*;

import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.common.DatabaseConnections;

import sun.misc.BASE64Decoder;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
/**
 * RestClientInstance creates a Client instance and open the ENM session for
 * getting FLS Responses
 * 
 * @author xdhanac
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
	RockFactory dwhdb;

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
			this.log = log;
			log.finest("enter inside creation of rest client instance");
			
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

			final SslConfigurator sslConfig = SslConfigurator.newInstance()
					.trustStoreFile(StaticProperties.getProperty("TRUSTSTORE_DIR",
							"/eniq/sw/runtime/jdk1.7.0_95/jre/lib/security/truststore.ts"))
					.trustStorePassword(StaticProperties.getProperty("KEYSTORE_PASSWORD", "changeit"));

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

			final PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(
					registry);
			clientConnectionManager.setMaxTotal(50);
			clientConnectionManager.setDefaultMaxPerRoute(20);

			final ClientConfig clientConfig = new ClientConfig();

			clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, clientConnectionManager);
			clientConfig.connectorProvider(new ApacheConnectorProvider());

			client = ClientBuilder.newBuilder().withConfig(clientConfig).build();

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
			if (dwhdb == null) {
				dwhdb = DatabaseConnections.getDwhDBConnection();
			}
			log.fine("enter inside session creation");
			log.finest("host:" + HOST + " " + USERNAME + " " + PASSWORD);
			log.finest("ENM hostname:" + HOST);

			log.finest("username:" + USERNAME);
			log.finest("password:" + (PASSWORD != null ? "********" : "null"));
			final WebTarget target = client.target(HOST).path("login").queryParam("IDToken1", USERNAME)
					.queryParam("IDToken2", PASSWORD);

			final Response response = target.request(MediaType.WILDCARD_TYPE).post(Entity.json(""));

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
					final Response response1 = target1.request(MediaType.WILDCARD_TYPE).post(Entity.json(""));

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
				log.info("Session creation request sent to server three times and No success response from server");
				String error = "SESSION CREATION FAILED:" + errorMessage;
				//errorTableUpdate(HOST, error, "-","-","-","-","-","-");
			}
		} catch (Exception e) {
			if (e.getCause() instanceof TimeoutException) {
				String error = "TIMEOUT EXCEPTION:" + e.getMessage();
				//errorTableUpdate(HOST, error,"-","-","-","-","-","-");
			} else {
				String error = "Exception:" + e.getMessage();
				//errorTableUpdate(HOST, error, "-","-","-","-","-","-");
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

		if (Main.lastDate == null) {
			Main.lastDate = new Date();
		}
		log.finest("inside timer method");
		Date date = new Date();
		log.finest("last executed time" + Main.lastDate);
		log.finest("current time" + date);

		if ((date.getTime() - Main.lastDate.getTime()) >= 30000) {
			if (sessionCheck == true) {
				try {
					dwhdb.getConnection().close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.finest("Error in closing the DWH database connection:" + e);
				}
				log.info("reaches the time difference...going to close the session");
				log.fine("client instance" + client);
				final Response response3 = client.target(HOST).path("logout").request("application/json").get();

				if (response3.getStatus() == 200) {
					sessionCheck = false;
					Main.lastDate = new Date();
					log.info("successfully logged out");
				} else

				{
					sessionCheck = true;
					log.fine("Error in closing the session..session exists");
				}
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
}
