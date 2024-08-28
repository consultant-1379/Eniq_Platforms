/**
 *
 */
package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.common.RmiUrlFactory;
import com.distocraft.dc5000.common.StaticProperties;
import com.ericsson.eniq.licensing.cache.stubs.DefaultLicensingCacheWrapper;
import com.ericsson.eniq.licensing.cache.stubs.WLSApi_Stub;

/**
 * @author ecarbjo
 */
//TODO Refactor the class to watch the the startThreads and not base it on timeouts....
public class LicensingCacheTest {
	static{
		System.out.println("Checking is sysout works");
	}

  private final AtomicBoolean BOUND = new AtomicBoolean(false);
  @Before
  public void before() {	  
	  System.out.println("Inside before method.");
    DefaultLicensingCacheWrapper.resetSentinalApiCallDelay();
    DefaultLicensingCacheWrapper.destroyInstance();
    BOUND.set(false);
    StaticProperties.giveProperties(new Properties());
    System.out.println("exiting before method.");
  }

  @After
  public void after(){
    DefaultLicensingCacheWrapper.destroyInstance();
  }

  @Test
  public void testSlowStartup() throws Exception {
	  System.out.println("inside testSlowStartup");
    final AtomicReference<Throwable> startError = new AtomicReference<Throwable>(null);
    final Thread starterThread = new Thread() {
      @Override
      public void run() {
        try {
          System.out.println("before calling DefaultLicensingCacheWrapper.getInstance");
          DefaultLicensingCacheWrapper.getInstance(5000, BOUND, 2, true);
          System.out.println("after calling DefaultLicensingCacheWrapper.getInstance");
        } catch (Error e){
          startError.set(e);
        } catch (Throwable e) {
          startError.set(e);
        }
      }
    };
    starterThread.setDaemon(true);
    starterThread.start();
    final boolean timeout = !waitForStarted(BOUND, 6000);
    if(startError.get() != null){
      throw new Exception(startError.get());
    }
    if(timeout){
      fail("Timeout waiting for License Server to start.");
    }
    if(!BOUND.get()){
      fail("License Manager didnt start!");
    }

    final LicensingCache cache = (LicensingCache) Naming.lookup(RmiUrlFactory.getInstance().getLicmgrRmiUrl());
    //This should block until the updater thead finishes....
    final Vector<LicenseInformation> li = cache.getLicenseInformation();
    assertEquals("License Count not correct ", WLSApi_Stub.getDummyLicenseCount(), li.size());
  }

  /**
   *  Sait
   * @param flag ab
   * @param timeoutMillis timeout
   * @return FALSE is timeout, TRUE otherwise
   */
  private boolean waitForStarted(final AtomicBoolean flag, final long timeoutMillis){
    final Thread toThread = new Thread(){
      @Override
      public void run() {
        while (!flag.get()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {/**/}
        }
      }
    };
    toThread.setDaemon(true);
    toThread.start();

    final long waitStartTime = System.currentTimeMillis();
    long waitEndTime;
    try {
      toThread.join(timeoutMillis);
    } catch (InterruptedException e) {/*--*/} finally {
      waitEndTime = System.currentTimeMillis();
    }

    return waitEndTime - waitStartTime < timeoutMillis;
  }

  private LicensingCache getClientSide(final LicensingCache helper) throws RemoteException, MalformedURLException, NotBoundException {
    final LicensingSettings conf = helper.getSettings();
    return (LicensingCache) Naming.lookup(RmiUrlFactory.getInstance().getLicmgrRmiUrl());
  }

  @Test
  public void testUpdateInForeground() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final int expectedDelayMS = 5000;
    DefaultLicensingCacheWrapper.setSentinalApiCallDelay(expectedDelayMS+500);
    long a = System.currentTimeMillis();
    cache.update();
    long b = System.currentTimeMillis();
    final long actualDelay = b - a;
    assertTrue("Call should have blocked for longer than " + expectedDelayMS + " seconds (" + actualDelay + ") ", actualDelay >= expectedDelayMS);
  }

  @Test
  public void testCheckValidLicense() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final LicenseDescriptor valid = new DefaultLicenseDescriptor("CXCSOMETHING");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertTrue("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_VALID, lr.getResponseType());
  }

  @Test
  public void testCheckInvalidLicense() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final LicenseDescriptor valid = new DefaultLicenseDescriptor("CXCSOMETHING_rrrrrrrrrr");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertFalse("The license was not checked correctly", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_INVALID, lr.getResponseType());
  }

  @Test
  public void testLicenseCapacity() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance(2);
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("TFN-3LIC");
    valid.setCapacity(2);
    final LicensingResponse lr = cache.checkLicense(valid);
    assertTrue("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_VALID, lr.getResponseType());
  }

  @Test
  public void testLicenseCapacityInvalid() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance(12);
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("TFN-10LIC");
    valid.setCapacity(12);
    final LicensingResponse lr = cache.checkLicense(valid);
    assertFalse("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_INVALID, lr.getResponseType());
  }

  @Test
  public void test_status() throws Exception {
    BOUND.set(false);
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance(BOUND);
    final boolean timeout = !waitForStarted(BOUND, 5000);
    if(timeout){
      fail("Timeout waiting for License Server to start.");
    }
    if(!BOUND.get()){
      fail("License Manager didnt start!");
    }
    final LicensingCache stub = getClientSide(cache);
    List<String> status = stub.status();
    assertTrue("Status returned nothing.", status != null && status.size() > 0);

    status = stub.sentinal_status();
    assertTrue("sentinal_status() returned nothing.", status != null && status.size() > 0);
  }

  @Test
  public void testGetKeyForComponent() throws Exception {
    BOUND.set(false);
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance(BOUND);
    final boolean timeout = !waitForStarted(BOUND, 5000);
    if(timeout){
      fail("Timeout waiting for License Server to start.");
    }
    if(!BOUND.get()){
      fail("License Manager didnt start!");
    }
    final LicensingCache stub = getClientSide(cache);
    stub.getKeyForComponent("something///");
  }

  @Test
  public void testGetNumberOfCores() {
    final int cores = DefaultLicensingCache.getNumberOfPhysicalCores(true);
    if (File.pathSeparatorChar == ':') {
      //On Unix so the getNumberOfCores() works.....
      assertTrue("Wrong number of cores returned ", cores > 1);
    } else {
      assertEquals(-1, cores);
    }
  }

  @Test
  public void testMissingLicense() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();

    LicensingResponse lr = cache.checkLicense(null);
    assertEquals(LicensingResponse.LICENSE_MISSING, lr.getResponseType());

    DefaultLicenseDescriptor licdes = new DefaultLicenseDescriptor("");
    lr = cache.checkLicense(licdes);
    assertEquals(LicensingResponse.LICENSE_MISSING, lr.getResponseType());

    licdes = new DefaultLicenseDescriptor(null);
    lr = cache.checkLicense(licdes);
    assertEquals(LicensingResponse.LICENSE_MISSING, lr.getResponseType());
  }

  @Test
  public void testFeatureMapping() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("CXC123456");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertTrue("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_VALID, lr.getResponseType());
  }

  @Test
  public void testLicenseExpired() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("CXC-Expired");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertFalse("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_INVALID, lr.getResponseType());
  }

  @Test
  public void testLicenseNotExpired() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("CXC-Expires");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertTrue("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_VALID, lr.getResponseType());
  }

  @Test
  public void testLicenseNeverExpires() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance();
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("CXC-NoExpiry");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertTrue("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_VALID, lr.getResponseType());
  }

  @Test
  public void testEmptyCache() throws Exception {
    final LicensingCache cache = DefaultLicensingCacheWrapper.getInstance(0, null, 2, false);
    final DefaultLicenseDescriptor valid = new DefaultLicenseDescriptor("CXC-NoExpiry");
    final LicensingResponse lr = cache.checkLicense(valid);
    assertFalse("The license was not checked correctly ", lr.isValid());
    assertEquals("The license was not checked correctly", LicensingResponse.LICENSE_SERVER_NOT_FOUND, lr.getResponseType());
  }

  @Test
  public void testUnbind() throws Exception {
    BOUND.set(false);
    final DefaultLicensingCacheWrapper cache = (DefaultLicensingCacheWrapper)DefaultLicensingCacheWrapper.getInstance(BOUND);
    final boolean timeout = !waitForStarted(BOUND, 5000);
    if(timeout){
      fail("Timeout waiting for License Server to start.");
    }
    if(!BOUND.get()){
      fail("License Manager didnt start!");
    }
    cache.unbind();
    try {
      getClientSide(cache);
      fail("Shouldnt be able to create an RMI connection after unbind is called.");
    } catch (NotBoundException e){
      // ok, expected this
    }
  }
}
