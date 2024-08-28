package com.ericsson.eniq.techpacksdk.datamodel;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;

/**
 * A proxy class to hide actual engine instance. Does automatic retry if RMI
 * connection is down. Only limited set of useful engine methods are exposed.
 * 
 * @author etuolem
 * 
 */
public class Scheduler {

  private static final Logger log = Logger.getLogger(Scheduler.class.getName());

  private final String dbserver;

  private final int rmiport;

  private final String rmiref;

  private ISchedulerRMI sch;

  public Scheduler(final String dbserver, final int rmiport, final String rmiref) {
    this.dbserver = dbserver;
    this.rmiport = rmiport;
    this.rmiref = rmiref;

    try {
      reconnect();
    } catch (Exception e) {
      log.log(Level.WARNING, "Cannot connect to Scheduler", e);
    }

  }

  private void reconnect() throws RemoteException {
    try {

      final String rmiURL = "//" + dbserver + ":" + rmiport + "/" + rmiref;

      log.fine("Connecting scheduler @ " + rmiURL);

      sch = (ISchedulerRMI) Naming.lookup(rmiURL);

      log.info("Connected to scheduler");

    } catch (Exception e) {
      throw new RemoteException("Unable to connect engine", e);
    }
  }

  private boolean shouldRetry(final Exception e) throws RemoteException {
    if (e instanceof RemoteException) {
      final RemoteException re = (RemoteException) e;
      final Throwable cause = e.getCause();
      if (cause instanceof java.rmi.ConnectException || cause instanceof java.net.ConnectException) {
    	log.severe("shouldRetry(): Connection to scheduler lost.");
        reconnect();
        return true;
      } else {
        throw re;
      }
    } else {
      throw new RemoteException("Unexpected exception in reconnect", e);
    }
  }

  public List<String> status() throws RemoteException {
    try {
      return sch.status();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return sch.status();
      } else {
        return null;
      }
    }
  }

  public void reload() throws RemoteException {
    try {
      sch.reload();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        sch.reload();
      }
    }
  }

  /**
   * After an engine restart, the scheduler is also restarted. This method can
   * be used to refresh the scheduler RMI connection from TechPackIDE to the new
   * scheduler process.
   * 
   * @param timeoutSecs
   *          The maximum waiting time for the scheduler connection refresh in
   *          seconds.
   * @throws RemoteException
   *           Exception is thrown in case the scheduler connection is not
   *           refreshed within the specified time.
   */
  public void refreshSchedulerConnectionAfterEngineRestart(int timeoutSecs) throws RemoteException {

    long startTime = System.currentTimeMillis();

    // Wait for the engine to come up, or until the timeout period expires.
    boolean schedulerReady = false;

    while (!schedulerReady) {
      // Try to get scheduler status. If not successful, then wait for 15
      // seconds.
      try {
        // Refresh the RMI connection to the scheduler.
        // NOTE: Since the scheduler does not do a naming unbind before
        // shutdown, the reconnect is always successful and returns the old RMI
        // object until the new scheduler makes naming rebind.
        log.finest("Reconnecting to scheduler.");
        reconnect();

        // Get scheduler status. If the scheduler is up and running, a list of
        // status messages is returned. If there is a problem in the connection,
        // the exception is caught and 10 seconds is waited before a retry.
        log.finest("Checking if scheduler is up.");
        List<String> status = sch.status();
        log.severe("Scheduler is up and running.");
        schedulerReady = true;

      } catch (Exception e1) {
        // Scheduler connection failed. Wait for 10 seconds.
        log.finest("Scheduler is not up yet, waiting for 10 seconds");
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          log.severe("Waiting interrupted.");
        }

        // Check if the max waiting time has elapsed. If yes, then stop waiting
        // and throw an exception.
        if (System.currentTimeMillis() - startTime > timeoutSecs * 1000) {
          log.severe("Scheduler connection refresh was not successful withing the timeout period of " + timeoutSecs
              + " seconds. Please perform a manual scheduler restart!");
          throw new RemoteException("Scheduler restart was not successful within " + timeoutSecs + " seconds.");
        }
      }
    }
    log.fine("Scheduler connection refresh was succesful.");
  }

}
