package com.distocraft.dc5000.etl.alarm;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * This class is a thread class that contains functionality to handle alarm
 * request send from ENIQ to ENM in the form of JSON Object in REST request.
 * 
 * @author xsarave
 * 
 */
public class AlarmProcessingTask implements Runnable {

	Logger log;
	static Client client;
	static Boolean session = false;
	static String eniqHostName;
	ENMServerDetails cache;
	static Date lastdate;
	static String HOST;
	RestClientInstance restClientInstance;
	String objectOfReference;
	String SpecificProblem;
	String probableCause;
	String perceivedSeverity;
	String thresholdInformation;
	String monitoredAttributes;
	String monitoredAttributeValues;
	String additionalText;
	String timeZone;
	String eventTime;
	String ossname;
	String eventType;
	String alarmName;
	String managedObjectInstance;
	String reportTitle;
	String perceivedSeverityText;
	String additionalInformation;
	String managedObjectClass;
	

	
	/**
	 * This function is the constructor of this AlarmMarkupAction class.
	 * 
	 * @param message
	 * @param log
	 * @param client
	 * @param cache
	 * 
	 */
	public AlarmProcessingTask(final Map<String, String> message, final Logger log, ENMServerDetails cache) {

		this.objectOfReference = message.get("ObjectOfReference");
		this.SpecificProblem = message.get("SPText");
		this.probableCause = message.get("PCText");
		this.perceivedSeverity = message.get("PerceivedSeverity");
		this.thresholdInformation = message.get("ThresholdInformation");
		this.monitoredAttributes = message.get("MonitoredAttributes");
		this.monitoredAttributeValues = message.get("MonitoredAttributeValues");
		this.additionalText = message.get("AdditionalText");
		this.timeZone = message.get("TimeZone");
		this.eventTime = message.get("EventTime");
		this.ossname = message.get("OssName");
		this.eventType = message.get("ETText");
		this.alarmName=message.get("AlarmName");
		this.managedObjectInstance=message.get("ManagedObjectInstance");
		this.reportTitle=message.get("ReportTitle");
		this.additionalInformation=message.get("AdditionalInformation");
		this.perceivedSeverityText=message.get("PerceivedSeverityText");
		this.managedObjectClass=message.get("ManagedObjectClass");
		
		this.log = log;
		log.finest("oor in message" + objectOfReference);

		this.cache = cache;
		AlarmProcessingTask.eniqHostName = cache.getHostname();

	}

	/**
	 * Send alarm information to ENM in REST PUT request in the form of JSON
	 * object.
	 */
	@Override
	public void run() {
		try {
			log.finest("Thread " + Thread.currentThread().getName()+" starts executing...");
			HOST = "https://" + cache.getHost();
			restClientInstance = RestClientInstance.getInstance();
			client = restClientInstance.getClient(cache, log);
			
			if(objectOfReference.contains("SubNetwork=SubNetwork,"))
					{
				objectOfReference=objectOfReference.replace("SubNetwork=SubNetwork,", "");
					}
			String path_name = "errevents-service/v1/errorevent/" + objectOfReference + "," + SpecificProblem + ","
					+ probableCause + "," + eventType;
			
			String target = "";
			for (int i = 0; i < eventTime.length(); i++) {
				if ((eventTime.charAt(i) != '-') && (eventTime.charAt(i) != ' ') && (eventTime.charAt(i) != ':')) {
					target = target + eventTime.charAt(i);
				}
			}
			target = target + "00";
			
			TimeZone z = TimeZone.getTimeZone(timeZone);
			
			log.finest("Timezone in alarm data:" + z.getID());
			log.finest("Eventtime in alarm data:" + target);
			AlarmRequestDetails event = new AlarmRequestDetails();
			event.setEventTime(target);
			event.setPerceivedSeverity(perceivedSeverity);
			event.setThresholdInformation(thresholdInformation);
			event.setMonitoredAttributes(monitoredAttributes);
			event.setMonitoredAttributeValues(monitoredAttributeValues);
			event.setAdditionalText(additionalText);
			event.setTimeZone(z.getID());
			event.setManagedObjectInstance(managedObjectInstance);
			event.setPerceivedSeverityText(perceivedSeverityText);
			event.setManagedObjectClass(managedObjectClass);
			event.setAdditionalInformation(additionalInformation);
			event.setNotificationSource(eniqHostName);

			final Response response1 = client.target(HOST).path(path_name).request("application/json")
					.put(Entity.json(event));
			log.finest("Alarm sending URL ::::  " + client.target(HOST).path(path_name));
			lastdate = new Date();
			log.finest("last alarm data send at the time ::" + lastdate);

			

			final StringBuffer buffer = new StringBuffer();

			buffer.append("\nRequest: " + path_name);
			Boolean check = false;
			String errorMessage = null;

			if (response1.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				log.info("Alarm data send successfully" + response1.readEntity(String.class));
			} else {
				client = RestClientInstance.getInstance().getClient(cache, log);
				check = true;
				for (int i = 0; i < 2; i++) {
					log.fine("Again request send to server");
					final Response response2 = client.target(HOST).path(path_name).request("application/json")
							.put(Entity.json(event));
					log.finest("Alarm sending URL ::::  " + client.target(HOST).path(path_name));
					if (response2.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
						log.info("Data send successfully:" + response2.readEntity(String.class));
						check = false;
						break;
					}
					errorMessage = "Error Status:" + response2.getStatus() + " ,Response Headers:"
							+ response2.getHeaders();
				}
			}
			if (check == true) {
				log.info("Request sent to ENM server three times and No success response from ENM server");
				String error = "ALARM DATA SENDING FAILED:" + errorMessage;
				log.log(Level.SEVERE,error);
				restClientInstance.errorTableUpdate(HOST, error,alarmName, managedObjectInstance,objectOfReference, ossname,reportTitle,eventTime);
			}

			log.finest("Thread " + Thread.currentThread().getName()+" finished its execution");
		} catch (Exception e) {
			if (e.getCause() instanceof TimeoutException) {
				log.info("TIMEOUT Exception while sending alarm ::  " + e);
				restClientInstance.errorTableUpdate(HOST, "TIMEOUT EXCEPTION IN ALARM REQUEST:" + e.getMessage(),
						alarmName, managedObjectInstance,objectOfReference, ossname,reportTitle,eventTime);
			} else {
				log.info("Exception while sending alarm ::  " + e);
				restClientInstance.errorTableUpdate(HOST, "Exception in Alarm data sending:" + e.getMessage(),
						alarmName, managedObjectInstance,objectOfReference, ossname,reportTitle,eventTime);
			}
		}
	}

}
