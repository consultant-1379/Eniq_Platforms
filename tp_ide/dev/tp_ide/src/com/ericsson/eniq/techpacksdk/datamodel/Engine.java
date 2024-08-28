package com.ericsson.eniq.techpacksdk.datamodel;

import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;

/**
 * A proxy class to hide actual engine instance. Does automatic retry if RMI
 * connection is down. Only limited set of useful engine methods are exposed.
 * 
 * @author etuolem
 * 
 */
public class Engine {

  private static final Logger log = Logger.getLogger(Engine.class.getName());

  private final String dbserver;

  private final int rmiport;

  private final String rmiref;

  private ITransferEngineRMI eng;

  public Engine(final String dbserver, final int rmiport, final String rmiref) {
    this.dbserver = dbserver;
    this.rmiport = rmiport;
    this.rmiref = rmiref;

    try {
      reconnect();
    } catch (Exception e) {
      log.log(Level.WARNING, "Cannot connect to Engine", e);
    }

  }

  private void reconnect() throws RemoteException {
    try {

      final String rmiURL = "//" + dbserver + ":" + rmiport + "/" + rmiref;

      log.fine("Connecting engine @ " + rmiURL);

      eng = (ITransferEngineRMI) Naming.lookup(rmiURL);

      log.info("Connected to engine");

    } catch (Exception e) {
      throw new RemoteException("Unable to connect engine", e);
    }
  }

  private boolean shouldRetry(final Exception e) throws RemoteException {
    if (e instanceof RemoteException) {
      final RemoteException re = (RemoteException) e;
      final Throwable cause = e.getCause();
      if (cause instanceof java.rmi.ConnectException || cause instanceof java.net.ConnectException) {
        // log.fine("Connection to engine lost.");
        log.severe("shouldRetry(): Connection to engine lost.");
        reconnect();
        return true;
      } else {
        throw re;
      }
    } else {
      throw new RemoteException("Unexpected exception in reconnect", e);
    }
  }

  public List<String> status() throws RemoteException , Exception{
    try {
      return eng.status();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.status();
      } else {
        return null;
      }
    }
  }

  public void clearCountingManagementCache(String storageId) throws RemoteException {
    try {
      eng.clearCountingManagementCache(storageId);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.clearCountingManagementCache(storageId);
      }
    }
  }

  public boolean setActiveExecutionProfile(final String profileName) throws RemoteException {
    try {
      return eng.setActiveExecutionProfile(profileName);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.setActiveExecutionProfile(profileName);
      } else {
        return false;
      }
    }
  }

  public boolean setActiveExecutionProfile(final String profileName, String messageText) throws RemoteException {
    try {
      return eng.setActiveExecutionProfile(profileName, messageText);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.setActiveExecutionProfile(profileName, messageText);
      } else {
        return false;
      }
    }
  }

  public boolean setAndWaitActiveExecutionProfile(final String profileName) throws RemoteException {
    try {
      return eng.setAndWaitActiveExecutionProfile(profileName);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.setAndWaitActiveExecutionProfile(profileName);
      } else {
        return false;
      }
    }
  }

  public boolean removeSetFromPriorityQueue(final Long ID) throws RemoteException {
    try {
      return eng.removeSetFromPriorityQueue(ID);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.removeSetFromPriorityQueue(ID);
      } else {
        return false;
      }
    }
  }

  public boolean changeSetPriorityInPriorityQueue(final Long ID, final long priority) throws RemoteException {
    try {
      return changeSetPriorityInPriorityQueue(ID, priority);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.changeSetPriorityInPriorityQueue(ID, priority);
      } else {
        return false;
      }
    }
  }

  public void holdPriorityQueue() throws RemoteException {
    try {
      eng.holdPriorityQueue();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.holdPriorityQueue();
      }
    }
  }

  public void restartPriorityQueue() throws RemoteException {
    try {
      eng.restartPriorityQueue();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.restartPriorityQueue();
      }
    }
  }

  public void reloadProperties() throws RemoteException {
    try {
      eng.reloadProperties();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.reloadProperties();
      }
    }
  }

  public void activateSetInPriorityQueue(final Long ID) throws RemoteException {
    try {
      eng.activateSetInPriorityQueue(ID);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.activateSetInPriorityQueue(ID);
      }
    }
  }

  public void holdSetInPriorityQueue(final Long ID) throws RemoteException {
    try {
      eng.holdSetInPriorityQueue(ID);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.holdSetInPriorityQueue(ID);
      }
    }
  }

  public List getFailedSets() throws java.rmi.RemoteException {
    try {
      return eng.getFailedSets();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.getFailedSets();
      } else {
        return null;
      }
    }
  }

  public List getQueuedSets() throws java.rmi.RemoteException {
    try {
      return eng.getQueuedSets();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.getQueuedSets();
      } else {
        return null;
      }
    }
  }

  public List getExecutedSets() throws java.rmi.RemoteException {
    try {
      return eng.getExecutedSets();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.getExecutedSets();
      } else {
        return null;
      }
    }
  }

  public List getRunningSets() throws java.rmi.RemoteException {
    try {
      return eng.getRunningSets();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.getRunningSets();
      } else {
        return null;
      }
    }
  }

  public void activateScheduler() throws RemoteException {
    try {
      eng.activateScheduler();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.activateScheduler();
      }
    }
  }

  public void reloadDBLookups(final String tableName) throws RemoteException {
    try {
      eng.reloadDBLookups(tableName);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.reloadDBLookups(tableName);
      }
    }
  }

  public void reloadTransformations() throws RemoteException {
    try {
      eng.reloadTransformations();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.reloadTransformations();
      }
    }
  }

  public void updateTransformation(final String tpName) throws RemoteException {
    try {
      eng.updateTransformation(tpName);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.updateTransformation(tpName);
      }
    }
  }

  public boolean isInitialized() throws RemoteException {
    try {
      return eng.isInitialized();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        return eng.isInitialized();
      } else {
        return false;
      }
    }
  }

  public void reloadLogging() throws RemoteException {
    try {
      eng.reloadLogging();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.reloadLogging();
      }
    }
  }

  public void giveEngineCommand(final String com) throws RemoteException {
    try {
      eng.giveEngineCommand(com);
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.giveEngineCommand(com);
      }
    }
  }

  public void fastGracefulShutdown() throws RemoteException {
    try {
      eng.fastGracefulShutdown();
    } catch (Exception e) {
      if (shouldRetry(e)) {
        eng.fastGracefulShutdown();
      }
    }
  }

  /**
   * Performs a graceful shutdown of the engine and then waits for the engine to
   * come up again. The maximum waiting time for the engine to come after
   * restart can be specified with the timeoutSecs parameter.
   * 
   * The engine is considered to be up and running when the first engine status
   * query is successful made. However, the engine might not be fully functional
   * at this point, since the the execution slot profile might not be up yet. If
   * this method is called again before the engine is fully up and running, then
   * the graceful shutdown call to the engine will fail and the engine restart
   * will be considered unsuccessful.
   * 
   * @param timeoutSecs
   *          The maximum waiting time for the engine start in seconds.
   * @throws RemoteException
   *           Exception is thrown in case the engine does not come up within
   *           the specified time.
   */
  public void fastGracefulEngineRestart(int timeoutSecs) throws RemoteException {

    log.finest("Graceful shutdown of the engine started.");

    // Make a fast graceful shutdown of the engine. If the connection to the
    // engine is down, the shutdown is retried once if the reconnect succeeds.
    //
    // NOTE: The successful RMI operation will result in an UnmarshallException
    // since the engine process will "die" before the operation is completed
    // "normally".
    try {
      eng.fastGracefulShutdown();
    } catch (UnmarshalException ume) {
      // Engine shutdown was completed, but since the engine dies in the middle
      // of this RMI call, then this exception is thrown.
      log.finest("Engine shutdown successful.");
    } catch (Exception e) {
      // Unexpected exception during engine restart.
      log.finest("Engine graceful shutdown failed.");
      // e.printStackTrace();
      if (shouldRetry(e)) {
        try {
          log.finest("Retrying engine graceful shutdown.");
          eng.fastGracefulShutdown();
        } catch (UnmarshalException ume) {
          // Engine shutdown was completed, but since the engine dies in the
          // middle of this RMI call, then this exception is thrown.
          log.finest("Engine shutdown successful.");
        } catch (Exception ex) {
          log.severe("Engine graceful shutdown retry failed. Shutdown not performed.");
          // ex.printStackTrace();
        }
      }
    }

    log.fine("Engine gracefully restarted. Waiting for engine to come up again.");

    // Get the starting time
    long startTime = System.currentTimeMillis();

    // Wait for the engine to come up, or until the timeout period expires.
    //
    // NOTE: The engine is up if the status query is successful. This does not
    // mean that the engine is fully functional, since the execution slot
    // profile might not be up yet.
    boolean engineReady = false;
    while (!engineReady) {
      // Try to get engine status. If not successful, then wait for 15 seconds.
      try {
        // Refresh the RMI connection to the engine.
        // NOTE: Since the engine does not do a naming unbind before shutdown,
        // the reconnect is always successful and returns the old RMI object
        // until the new engine makes naming rebind.
        log.finest("Reconnecting to engine.");
        reconnect();

        // Get engine status. If the engine is up and running, a list of status
        // messages is returned. If there is a problem in the connection, the
        // exception is caught and 10 seconds is waited before a retry.
        log.finest("Checking if engine is up.");
        List<String> status = eng.status();
        log.severe("Engine is up and running.");
        engineReady = true;

      } catch (Exception e1) {
        // Engine connection failed. Wait for 10 seconds.
        log.finest("Engine is not up yet, waiting for 10 seconds");
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          log.severe("Waiting interrupted.");
        }

        // Check if the max waiting time has elapsed. If yes, then stop waiting
        // and throw an exception.
        if (System.currentTimeMillis() - startTime > timeoutSecs * 1000) {
          log.severe("Engine graceful restart was not successful withing the timeout period of " + timeoutSecs
              + " seconds. Please perform a manual engine restart!");
          throw new RemoteException("Engine restart was not successful within " + timeoutSecs + " seconds.");
        }
      }
    }
    log.fine("Engine graceful restart was succesful.");
  }

  /**
   * Runs the directory checker set for a techpack or an interface. If the
   * running of the set fails, an exception is thrown.
   * 
   * @param name
   *          The name of the techpack or the interface. For example: 'DC_E_MGW'
   *          or 'INTF_DC_E_MGW'.
   * @throws RemoteException
   */
  public void rundirectoryCheckerSet(String name) throws RemoteException {

    // Runs the directory checker collection set from the engine.
    eng.executeAndWait(name, "Directory_Checker_" + name, "");
  }
  
  /**
   * Runs the directory checker sets for the Alarm Interfaces tech pack.
   * @param cSetName        The collection set name.
   * @param collectionName  The collection name.
   * @throws RemoteException
   */
  public void rundirectoryCheckerSetForAlarmInterfaces(String cSetName, String collectionName) throws RemoteException {

    // Runs the directory checker collection set from the engine.
    eng.executeAndWait(cSetName, collectionName, "");
  }
}
