package com.ericsson.eniq.techpacksdk.datamodel;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.engine.main.ITransferEngineRMI;

/**
 * A stubbed version of the proxy class to hide actual engine instance. The real
 * connection to the engine process will not made, since there is no engine /
 * scheduler process running in the test environment VM.
 * 
 * @author eheitur
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

      // final String rmiURL = "//" + dbserver + ":" + rmiport + "/" + rmiref;
      //
      // log.fine("Connecting engine @ " + rmiURL);
      //
      // eng = (ITransferEngineRMI) Naming.lookup(rmiURL);
      //
      // log.info("Connected to engine");

      System.out.println("Engine.reconnect(): Connection established successfully.");

    } catch (Exception e) {
      throw new RemoteException("Unable to connect engine", e);
    }
  }
  
  public void clearCountingManagementCache(String storageId) throws RemoteException {
	    
  }

  private boolean shouldRetry(final Exception e) throws RemoteException {
    return false;
  }

  public List<String> status() throws RemoteException {
    return null;
  }

  public boolean setActiveExecutionProfile(final String profileName) throws RemoteException {
    return true;
  }

  public boolean setActiveExecutionProfile(final String profileName, String messageText) throws RemoteException {
    return true;
  }

  public boolean setAndWaitActiveExecutionProfile(final String profileName) throws RemoteException {
    return true;
  }

  public boolean removeSetFromPriorityQueue(final Long ID) throws RemoteException {
    return true;
  }

  public boolean changeSetPriorityInPriorityQueue(final Long ID, final long priority) throws RemoteException {
    return true;
  }

  public void holdPriorityQueue() throws RemoteException {
  }

  public void restartPriorityQueue() throws RemoteException {
  }

  public void reloadProperties() throws RemoteException {
  }

  public void activateSetInPriorityQueue(final Long ID) throws RemoteException {
  }

  public void holdSetInPriorityQueue(final Long ID) throws RemoteException {
  }

  public List getFailedSets() throws java.rmi.RemoteException {
    return null;
  }

  public List getQueuedSets() throws java.rmi.RemoteException {
    return null;
  }

  public List getExecutedSets() throws java.rmi.RemoteException {
    return null;
  }

  public List getRunningSets() throws java.rmi.RemoteException {
    return null;
  }

  public void activateScheduler() throws RemoteException {
  }

  public void reloadDBLookups(final String tableName) throws RemoteException {
  }

  public void reloadTransformations() throws RemoteException {
  }

  public void updateTransformation(final String tpName) throws RemoteException {

  }

  public boolean isInitialized() throws RemoteException {
    return true;
  }

  public void reloadLogging() throws RemoteException {
  }

  public void giveEngineCommand(final String com) throws RemoteException {
  }

  public void fastGracefulShutdown() throws RemoteException {
  }

  public void fastGracefulEngineRestart(int timeoutSecs) throws RemoteException {
  }

  public void rundirectoryCheckerSet(String name) throws RemoteException {
  }

}
