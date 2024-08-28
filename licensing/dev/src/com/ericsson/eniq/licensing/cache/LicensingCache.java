/**
 * This is the default Licensing server interface. It extends the java.rmi.Remote interface to be
 * eligible for registration with the RMI registry. 
 */
package com.ericsson.eniq.licensing.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * @author ecarbjo
 */
public interface LicensingCache extends Remote {
  
  /**
   * Checks a capacity license. Checks the cache for a single license only.
   * @param license           The LicenseDescriptor object
   * @param capacity          The capacity to check.
   * @return                  Should return a LicensingResponse object.
   * @throws RemoteException
   */
  LicensingResponse checkCapacityLicense(LicenseDescriptor license, int capacity) throws RemoteException;

  /**
   * Check the license to see if it is valid or not.
   * 
   * @param license
   *          the license to be checked
   * @return a LicensingResponse object corresponding to the given license.
   * @throws RemoteException
   */
  LicensingResponse checkLicense(LicenseDescriptor license) throws RemoteException;

  /**
   * Shut down the cache.
   * 
   * @throws RemoteException
   */
  void shutdown() throws RemoteException;

  /**
   * @return the status of this object.
   * @throws RemoteException
   */
  List<String> status() throws RemoteException;


  /**
   * @return the status of sentinal servers.
   * @throws RemoteException
   */
  List<String> sentinal_status() throws RemoteException;

  /**
   * @return the settings object for this cache.
   */
  LicensingSettings getSettings() throws RemoteException;

  /**
   * Updates the cache to sync it with the actual licensing server.
   * 
   * @throws RemoteException
   */
  void update() throws RemoteException;

  /**
   * Re-reads the logging properties.
   * 
   * @throws RemoteException
   */
  void reloadLogging() throws RemoteException;

  /**
   * Maps a given mapping descriptor onto the specified type (specified in the
   * mapping descriptor).
   * 
   * @param mapping
   *          the mapping descriptor that contains the feature name and the
   *          mapping type
   * @return a string vector containing the results of the mapping operation, or
   *         null if no mapping was found.
   * @throws RemoteException
   */
  Vector<String> map(MappingDescriptor mapping) throws RemoteException;

  /**
   * generates and returns a list of all licenses that are currently available
   * in the licensing cache.
   * 
   * @return a vector of all the licenses in the cache, or null if the cache is
   *         empty.
   * @throws RemoteException
   */
  Vector<LicenseInformation> getLicenseInformation() throws RemoteException;

  /**
   * Get a key for a component
   * @param component The component
   * @return THe key
   * @throws RemoteException Any errors
   */
  String getKeyForComponent(final String component) throws RemoteException;
}
