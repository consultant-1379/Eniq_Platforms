/*
 * ---------------------------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * ---------------------------------------------------------------------------------------
 */

package com.ericsson.eniq.licensing.cache.stubs;

import com.ericsson.eniq.licensing.cache.DefaultLicensingCache;
import com.ericsson.eniq.licensing.cache.LicenseDescriptor;
import com.ericsson.eniq.licensing.cache.LicenseInformation;
import com.ericsson.eniq.licensing.cache.LicensingCache;
import com.ericsson.eniq.licensing.cache.LicensingResponse;
import com.ericsson.eniq.licensing.cache.LicensingSettings;
import com.ericsson.eniq.licensing.cache.MappingDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import rainbow.lmclient.WLSApi;

public class DefaultLicensingCacheWrapper implements LicensingCache {
  private static final WLSApi_Stub wLSApi_Stub = new WLSApi_Stub();
  private static DefaultLicensingCache wrappedObject;


  private DefaultLicensingCacheWrapper(final int startupDelay, final AtomicBoolean rmiBound, final int coreCount, final boolean cacheEnabled) throws RemoteException {
    final String propFile = writeProperties();
    wrappedObject = new DefaultLicensingCache(propFile){
      @Override
      protected WLSApi getSentinalApi() {
        try {
          Thread.sleep(startupDelay);// Slow the start down a bit...
        } catch (InterruptedException e) {/**/}
        return wLSApi_Stub;
      }

      @Override
      public void rebind() throws RemoteException {
        if(rmiBound!= null){
          super.rebind();
          rmiBound.set(true);
        }
      }

      @Override
      protected void updateCache() {
        if(cacheEnabled){
          super.updateCache();
        }
      }

      @Override
      public void unbind() throws RemoteException, NotBoundException {
        //gets exposed to the DefaultLicensingCacheWrapper.unbind() reflection call....
        super.unbind();
      }
    };
  }

  public static void unbind() throws RemoteException {
    try {
      final Method unbind = wrappedObject.getClass().getMethod("unbind");
      unbind.invoke(wrappedObject);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      if(!(e.getCause() instanceof NotBoundException)){
        throw new RuntimeException(e.getCause());
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void update() throws RemoteException {
    wrappedObject.update();
  }

  @Override
  public void reloadLogging() throws RemoteException {
    wrappedObject.reloadLogging();
  }

  @Override
  public Vector<String> map(final MappingDescriptor mapping) throws RemoteException {
    return wrappedObject.map(mapping);
  }

  @Override
  public Vector<LicenseInformation> getLicenseInformation() throws RemoteException {
    return wrappedObject.getLicenseInformation();
  }

  @Override
  public String getKeyForComponent(final String component) throws RemoteException {
    return wrappedObject.getKeyForComponent(component);
  }

  public LicensingResponse checkLicense(final LicenseDescriptor license) throws RemoteException {
    return wrappedObject.checkLicense(license);
  }

  @Override
  public void shutdown() throws RemoteException {
    wrappedObject.shutdown();
  }

  @Override
  public List<String> status() throws RemoteException {
    return wrappedObject.status();
  }

  @Override
  public List<String> sentinal_status() throws RemoteException {
    return wrappedObject.sentinal_status();
  }

  public LicensingSettings getSettings() throws RemoteException {
    return wrappedObject.getSettings();
  }


  /**
   * Get an instance of the License Cache that got a set number of cores available.
   * No Start delay, not bound to RMI
   * @param cores Number of cores on machine
   * @return DefaultLicensingCache instance
   * @throws RemoteException errors
   */
  public static LicensingCache getInstance(final int cores) throws RemoteException {
    return getInstance(0, null, cores, true);
  }
  /**
   * Get an instance of the License Cache with default settings and now delay on startup and not bound to RMI
   * No Start delay
   * @return DefaultLicensingCache instance
   * @throws RemoteException errors
   */
  public static LicensingCache getInstance() throws RemoteException {
    return getInstance(null);
  }
  /**
   * Get an instance of the License Cache thats bound to the RMI ref rmi://localhost:1200/LicensingCache
   * No Start delay, cores set to -1
   * @param rmiBound flag to indicate the RMI bind call has been made.
   * @return DefaultLicensingCache instance
   * @throws RemoteException errors
   */
  public static LicensingCache getInstance(final AtomicBoolean rmiBound) throws RemoteException {
    return getInstance(0, rmiBound, 1, true);
  }
  /**
   * Get an instance of the License Cache thats bound to the RMI ref rmi://localhost:1200/LicensingCache and delays its
   * startup, also set the number of cores to check licenses against.
   * @param startDelay The delay to use when starting the cache
   * @param rmiBound flag to indicate the RMI bind call has been made.
   * @param cores Number of cores on machine
   * @param cacheEnabled cache generation enabled
   * @return DefaultLicensingCache instance
   * @throws RemoteException errors
   */
  public static LicensingCache getInstance(final int startDelay, final AtomicBoolean rmiBound, final int cores, final boolean cacheEnabled) throws RemoteException {
    return new DefaultLicensingCacheWrapper(startDelay, rmiBound, cores, cacheEnabled);
  }

  public static void destroyInstance(){
    try {
      unbind();
    } catch (Throwable e) {/**/}
    wrappedObject = null;
  }

  /**
   * Set delay, in milliseconds, for each sentinal call i.e. simulate slow system
   * @param delay delay, in milliseconds, for each sentinal call
   */
  public static void setSentinalApiCallDelay(final long delay){
    wLSApi_Stub.setGetCallDelay(delay);
  }

  /**
   * Reset sentinal call delay back to zero
   */
  public static void resetSentinalApiCallDelay(){
    wLSApi_Stub.setGetCallDelay(0);
  }

  private String writeProperties(){
    final String tmpDir = System.getProperty("java.io.tmpdir") + File.separator;
    
    final File featureMappingFile = new File(tmpDir + "feature.mappings");
    featureMappingFile.deleteOnExit();
    try{
      final BufferedWriter bw = new BufferedWriter(new FileWriter(featureMappingFile));
      bw.write("CXC123456::This Is Some Feature::FAJ 123 9876");
      bw.close();
    } catch (IOException e){
      e.printStackTrace();
    }

    if(File.pathSeparatorChar == ';'){
      // Map file delimiter is : by default, if running tests on Win the set it to something else
      System.setProperty("mapping.files.delimiter", ";");
    }

    final Properties p = new Properties();
    p.put("ENGINE_HOSTNAME", "localhost");
    p.put("ENGINE_PORT", "1200");
    p.put("ENGINE_REFNAME", "LicensingCache");
    p.put("MAPPING_FILES", featureMappingFile.getAbsolutePath());
    final File propFile = new File(tmpDir + "tmp.props");
    propFile.deleteOnExit();

    try {
      final BufferedWriter bw = new BufferedWriter(new FileWriter(propFile));
      p.store(bw, "For Tests");
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return propFile.getAbsolutePath();
  }
  
  /*
   * (non-Javadoc)
   * @see com.ericsson.eniq.licensing.cache.LicensingCache#checkCapacityLicense(com.ericsson.eniq.licensing.cache.LicenseDescriptor, int)
   */
  @Override
  public LicensingResponse checkCapacityLicense(LicenseDescriptor license, int capacity) throws RemoteException {	
    return wrappedObject.checkCapacityLicense(license, capacity);
  }
  
}
