package com.ericsson.eniq.techpacksdk.datamodel;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;
import com.distocraft.dc5000.etl.scheduler.ISchedulerRMI;

/**
 * A stubbed version of a proxy class to hide actual engine instance. The real
 * connection to the scheduler process will not made, since there is no engine /
 * scheduler process running in the test environment VM.
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

      // final String rmiURL = "//" + dbserver + ":" + rmiport + "/" + rmiref;
      //
      // log.fine("Connecting scheduler @ " + rmiURL);
      //
      // sch = (ISchedulerRMI) Naming.lookup(rmiURL);
      //
      // log.info("Connected to scheduler");

      System.out.println("Scheduler.reconnect(): Connection established successfully.");

    } catch (Exception e) {
      throw new RemoteException("Unable to connect engine", e);
    }
  }

  private boolean shouldRetry(final Exception e) throws RemoteException {
    return false;
  }

  public List<String> status() throws RemoteException {
    return null;
  }

  public void reload() throws RemoteException {
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

  }
}
