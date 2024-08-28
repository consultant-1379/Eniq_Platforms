package com.ericsson.eniq.alarmcfg.clientexample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ericsson.eniq.alarmcfg.clientapi.IAlarmcfgSession;
import com.ericsson.eniq.alarmcfg.clientapi.IAlarmcfgReport;
import com.ericsson.eniq.alarmcfg.clientapi.IAlarmcfgReportRequest;
import com.ericsson.eniq.alarmcfg.clientapi.AlarmcfgSessionFactory;
import com.ericsson.eniq.alarmcfg.clientapi.exceptions.ACAuthenticateException;
import com.ericsson.eniq.alarmcfg.clientapi.exceptions.ACSessionException;
import com.ericsson.eniq.alarmcfg.clientapi.exceptions.ACReportNotFoundException;

/** * Example implementation of client that connects to ENIQ Alarmcfg and retrieves * one or more reports. *  * @author Antti Laurila (original) * @author Heikki Juntunen * @copyright Ericsson (c) 2009 */
public class ExampleClient {

  private static Logger logger = Logger.getLogger(ExampleClient.class.getName());

  private final String protocol;

  private final String host;

  private final String userID;

  private final String password;

  private final String authmethod;

  private final String cms;

  /**
   * Constructor
   * 
   * @param nProtocol
   * @param nHost
   * @param nUserID
   * @param nPassword
   */
  public ExampleClient(final String nProtocol, final String nHost, final String nCms, final String nUserID,
      final String nPassword, final String nAuthMethod) {
    protocol = nProtocol;
    host = nHost;
    cms = nCms;
    userID = nUserID;
    password = nPassword;
    authmethod = nAuthMethod;
  }

  /**
   * Do the trick.
   * 
   * Creates connection and makes report request object and ask connection to
   * retrieve the response for that report(s) and print out what we got.
   * 
   * @throws ACAuthenticateException
   * @throws ACSessionException
   * @throws ACReportNotFoundException
   */
  public List<IAlarmcfgReport> doTheTrick(final String[] reportNames) throws ACAuthenticateException,
      ACSessionException, ACReportNotFoundException {

    final List<IAlarmcfgReport> alarmcfgreports = new ArrayList<IAlarmcfgReport>(reportNames.length);

    final IAlarmcfgSession session = AlarmcfgSessionFactory.createSession(protocol, host, cms, userID, password,
        authmethod);
    try {
      for (String reportName : reportNames) {
        final IAlarmcfgReportRequest req = AlarmcfgSessionFactory.createRequest(reportName);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          logger.severe(e.getMessage());
        } catch (Exception e) {
          logger.severe(e.getMessage());
        }
        alarmcfgreports.add(session.getReport(req));
      }
    } finally {
      session.close();
    }
    return alarmcfgreports;
  }

  /**
   * By default connects do localhost using supervisor/supervisor Can be
   * overridden with arguments [host:port] [username] [password]
   * 
   * @param args
   */
  public static void main(final String[] args) {

    final String protocol = "http";
    final String hostname = "localhost:8080";
    final String cms = "atrcx886vm3:6400";
    final String username = "eniq_alarm";
    final String password = "eniq_alarm";
    final String auth = "secEnterprise";

    final String[] reports = { "reportName=AM_MGW_SYNCHRONIZATION_RAW_pmMaxDelayVariation&&promptValue_Number of Days backwards:=1&useUserValues=1" };

    // create this object...
    logger.info("Creating client to " + hostname + " with " + username + "/" + password);
    final ExampleClient client = new ExampleClient(protocol, hostname, cms, username, password, auth);
    logger.info("Client created");

    try {
      logger.info("Start of retrieving document");

      final List<IAlarmcfgReport> reportList = client.doTheTrick(reports);

      for (IAlarmcfgReport report : reportList) {
        logger.info("Retrieved report: >>>>>>>>>>>\n" + report.getXML() + "\n<<<<<<<<<<<<<");

        final String outputFileName = "c:\\temp\\result.xml";
        try {
          final FileWriter outputFileWriter = new FileWriter(outputFileName);
          final PrintWriter outputStream = new PrintWriter(outputFileWriter);
          outputStream.print(report.getXML());
          outputStream.close();
          logger.info("Report saved as: >>>\n" + outputFileName + "\n<<<");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      logger.info("Document(s) succesfully retrieved");
    } catch (ACAuthenticateException e) {
      logger.info("Report not retrieved, reason: " + e.getMessage());
      logger.info("Authentication failed");
    } catch (ACSessionException e) {
      logger.info("Report not retrieved, reason: " + e.getMessage());
      logger.info("Connection failed");
    } catch (ACReportNotFoundException e) {
      logger.info("Report not retrieved, reason: " + e.getMessage());
      logger.info("Document unsuccesfully retrieved");
    }
  }

}
