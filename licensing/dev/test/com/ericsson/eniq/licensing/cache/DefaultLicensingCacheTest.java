package com.ericsson.eniq.licensing.cache;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rainbow.lmclient.WLSApi;
import rainbow.lmclient.WLSFeatureInfo;

/**
 * Tests for DefaultLicensingCache.
 * @author eciacah
 */
@Ignore
public class DefaultLicensingCacheTest {
  
  private DefaultLicensingCache testInstance;
  
  protected Mockery context = new Mockery();
  {
      // we need to mock classes, not just interfaces.
      context.setImposteriser(ClassImposteriser.INSTANCE);
  }
    
  /** Mock objects */
  LicensingSettings mockLicenseSettings;
  FeatureMapper mockFeatureMapper;
  LicenseDescriptor mockLicenseDescriptor;
  WLSFeatureInfo mockFeatureInfo;
  DefaultMappingDescriptor mockDefaultMappingDescriptor;
  DefaultLicensingCache mockDefaultLicensingCache;
  CPUDetector mockCpuDetector;
  
  Hashtable<String, Vector<WLSFeatureInfo>> cache;

  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    mockLicenseSettings = context.mock(LicensingSettings.class);
    mockFeatureMapper = context.mock(FeatureMapper.class);
    mockFeatureInfo = context.mock(WLSFeatureInfo.class);
    mockLicenseDescriptor = context.mock(LicenseDescriptor.class);
    mockDefaultMappingDescriptor = context.mock(DefaultMappingDescriptor.class);
    mockDefaultLicensingCache = context.mock(DefaultLicensingCache.class);
    mockCpuDetector = context.mock(CPUDetector.class);
    
    cache = new Hashtable<String, Vector<WLSFeatureInfo>>();
    
    // Create a test element:
    Vector<WLSFeatureInfo> testElement = new Vector<WLSFeatureInfo>();
    testElement.add(mockFeatureInfo);
    cache.put("Test element", testElement);
        
    testInstance = new DefaultLicensingCache(mockLicenseSettings, mockFeatureMapper, cache, new AtomicBoolean(true));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }
  
  /**
   * Status should return a list of status strings. Should not be empty.
   * @throws Exception
   */
  @Test
  public void testStatus() throws Exception {    
    List<String> status = testInstance.status();
    Assert.assertTrue("Status should contain a message", status.size() > 0);    
  }
    
  @Test
  public void testCheckCapacityLicenseExpired() throws Exception {

    final int numOfFeatures = 5;
    final String featureName = "Test feature";
    final int hardLimit = 20;
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        // .getName();
        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        allowing(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Return the "death day" as a date in the past, license should be detected as expired:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue(10L));
        
        oneOf(mockFeatureInfo).getNumLicenses();
        will(returnValue(hardLimit));        
      }
    });

    final int numberOfCPUs = 5;
    final LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if it has expired", licenseResponse.isValid());
  }
  
  @Test
  public void testCheckCapacityLicenseLicenseGreaterThanCPUs() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfCPUs = 5;
    final int hardLimit = 20;
    final String featureName = "Test feature";
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        allowing(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Assume there is no expiration on the license:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
        
        allowing(mockLicenseDescriptor).getCapacity();
        will(returnValue(numberOfCPUs));
        
        // Expect a high value in the hard limit of the license:
        oneOf(mockFeatureInfo).getNumLicenses();
        will(returnValue(hardLimit));
        
      }
    });

    final LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertTrue("Capacity license should be valid if the license hard limit is greater than the number of CPUs", 
        licenseResponse.isValid());
  }
  
  @Test
  public void testCheckCapacityLicenseLicenseEqualToCPUs() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfCPUs = 5;
    final String featureName = "Test feature";
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        allowing(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Assume there is no expiration on the license:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
        
        allowing(mockLicenseDescriptor).getCapacity();
        will(returnValue(numberOfCPUs));
        
        // Number of CPUs = the hard limit of the license:
        oneOf(mockFeatureInfo).getNumLicenses();
        will(returnValue(numberOfCPUs));        
      }
    });

    final LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertTrue("Capacity license should be valid if the license hard limit is equal to the number of Cores", 
        licenseResponse.isValid());
  }
  
  @Test
  public void testCheckCapacityLicenseLicenseLessThanCPUs() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfCPUs = 5;
    final int hardLimit = 3;
    final String featureName = "Test feature";
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        allowing(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Assume there is no expiration on the license:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
        
        allowing(mockLicenseDescriptor).getCapacity();
        will(returnValue(numberOfCPUs));
        
        // Number of CPUs > the hard limit of the license:
        oneOf(mockFeatureInfo).getNumLicenses();
        will(returnValue(hardLimit));        
      }
    });

    final LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if the license hard limit is less than the number of Cores", 
        licenseResponse.isValid());
  }
  
  @Test
  public void testCheckLicenseGreaterThanCores() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfcores = 8;
    final int hardLimit = 20;
    final String featureName = "Test feature";
    final String[] featureNames = {"Test feature", "Test feature1"};
 //   final MappingDescriptor md = null;
    
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        allowing(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        allowing(mockLicenseDescriptor).getFeatureNames();
        will(returnValue(featureNames));
               
        allowing(mockFeatureMapper).map(with (aNonNull((MappingDescriptor.class))));
        will(returnValue(null));
        
        allowing(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Assume there is no expiration on the license:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
        
        allowing(mockLicenseDescriptor).getCapacity();
        will(returnValue(numberOfcores));
        
        // Expect a high value in the hard limit of the license:
        allowing(mockFeatureInfo).getNumLicenses();
        will(returnValue(hardLimit));
      
      }
    });
    
    final LicensingResponse licenseResponse = testInstance.checkLicense(mockLicenseDescriptor);
    Assert.assertTrue("Capacity license should be valid if the license hard limit is greater than number of COres", 
  		  licenseResponse.isValid());
  }
    @Test
    public void testCheckLicenseLessThanCores() throws Exception {

      final int numOfFeatures = 5;
      final int numberOfcores = 8;
      final int hardLimit = 6;
      final String featureName = "Test feature";
      final String[] featureNames = {"Test feature", "Test feature1"};
   //   final MappingDescriptor md = null;
      
      
      context.checking(new Expectations() {
        {
          allowing(mockLicenseDescriptor).getNumFeatures();
          will(returnValue(numOfFeatures));

          allowing(mockLicenseDescriptor).getName();
          will(returnValue(featureName));
          
          allowing(mockLicenseDescriptor).getFeatureNames();
          will(returnValue(featureNames));
                 
          allowing(mockFeatureMapper).map(with (aNonNull((MappingDescriptor.class))));
          will(returnValue(null));
          
          allowing(mockFeatureInfo).getFeatureName();
          will(returnValue(featureName));
          
          // Assume there is no expiration on the license:
          allowing(mockFeatureInfo).getDeathDay();
          will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
          
          allowing(mockLicenseDescriptor).getCapacity();
          will(returnValue(numberOfcores));
          
          // Expect a high value in the hard limit of the license:
          allowing(mockFeatureInfo).getNumLicenses();
          will(returnValue(hardLimit));
        
        }
     
      });
      final LicensingResponse licenseResponse = testInstance.checkLicense(mockLicenseDescriptor);
      Assert.assertFalse("Capacity license should be Invalid if the license hard limit is less than number of COres", 
    		  licenseResponse.isValid());
    }
      @Test
      public void testCheckLicenseEqualsToCores() throws Exception {

        final int numOfFeatures = 5;
        final int numberOfcores = 8;
        final int hardLimit = 8;
        final String featureName = "Test feature";
        final String[] featureNames = {"Test feature", "Test feature1"};
     //   final MappingDescriptor md = null;
        
        
        context.checking(new Expectations() {
          {
            allowing(mockLicenseDescriptor).getNumFeatures();
            will(returnValue(numOfFeatures));

            allowing(mockLicenseDescriptor).getName();
            will(returnValue(featureName));
            
            allowing(mockLicenseDescriptor).getFeatureNames();
            will(returnValue(featureNames));
                   
            allowing(mockFeatureMapper).map(with (aNonNull((MappingDescriptor.class))));
            will(returnValue(null));
            
            allowing(mockFeatureInfo).getFeatureName();
            will(returnValue(featureName));
            
            // Assume there is no expiration on the license:
            allowing(mockFeatureInfo).getDeathDay();
            will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));
            
            allowing(mockLicenseDescriptor).getCapacity();
            will(returnValue(numberOfcores));
            
            // Expect a high value in the hard limit of the license:
            allowing(mockFeatureInfo).getNumLicenses();
            will(returnValue(hardLimit));
          
          }
        });

    final LicensingResponse licenseResponse = testInstance.checkLicense(mockLicenseDescriptor);
    Assert.assertTrue("Capacity license should be valid if the license hard limit is Equal number of COres", 
        licenseResponse.isValid());
  }
  
  
  @Test
  public void testCheckCapacityLicenseLicenseHasNoFeatures() throws Exception {

    final int numOfFeatures = 0;
    final int numberOfCPUs = 5;
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));     
      }
    });

    LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if the license is null or has no features", 
        licenseResponse.isValid());
    
    licenseResponse = testInstance.checkCapacityLicense(null, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if the license is null or has no features", 
        licenseResponse.isValid());
  }
  
  @Test
  public void testCheckCapacityLicenseEmptyCache() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfCPUs = 5;
    final int hardLimit = 10;
    final String featureName = "Test feature";
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));
        
        oneOf(mockFeatureInfo).getFeatureName();
        will(returnValue(featureName));
        
        // Assume there is no expiration on the license:
        allowing(mockFeatureInfo).getDeathDay();
        will(returnValue((long) WLSApi.VLS_NO_EXPIRATION));  
        
        // Number of CPUs < the hard limit of the license:
        oneOf(mockFeatureInfo).getNumLicenses();
        will(returnValue(hardLimit));   
      }
    });
    
    // Remove the test element from the cache:
    cache.remove("Test element");

    LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if the cache is empty", 
        licenseResponse.isValid());   
  }  
  
  @Test
  public void testCheckCapacityLicenseCacheIsNull() throws Exception {

    final int numOfFeatures = 5;
    final int numberOfCPUs = 5;
    //final int hardLimit = 10;
    final String featureName = "Test feature";
    
    testInstance = new DefaultLicensingCache(mockLicenseSettings, mockFeatureMapper, null, new AtomicBoolean(true));
    
    context.checking(new Expectations() {
      {
        allowing(mockLicenseDescriptor).getNumFeatures();
        will(returnValue(numOfFeatures));

        oneOf(mockLicenseDescriptor).getName();
        will(returnValue(featureName));  
      }
    });
    

    LicensingResponse licenseResponse = testInstance.checkCapacityLicense(mockLicenseDescriptor, numberOfCPUs);
    Assert.assertFalse("Capacity license should be invalid if the cache is empty", 
        licenseResponse.isValid());   
  }
  
//	/**
//	 * Test method for
//	 * {@link com.ericsson.eniq.licensing.cache.DefaultLicensingCache#checkLicense(com.ericsson.eniq.licensing.cache.LicenseDescriptor)}
//	 * .
//	 * 
//	 * @throws
//	 */
//	//@Test
//	public void testCheckLicense() {
//		// we are unit testing in dummy mode. All should be true.
//		final LicenseDescriptor invalid = new DefaultLicenseDescriptor(
//				"CXCSOMETHING");
//		try {
//			assertTrue("The license was deemed false", testInstance.checkLicense(
//					invalid).isValid());
//		} catch (RemoteException e) {
//			fail("Caught a RemoteException");
//		}
//	}
//
//	//@Test
//	public void testStatus() {
//		try {
//			final LicensingSettings conf = testInstance.getSettings();
//			final LicensingCache stub = (LicensingCache) Naming.lookup("rmi://"
//					+ conf.getServerHostName() + ":" + conf.getServerPort()
//					+ "/" + conf.getServerRefName());
//
//			final List<String> status = stub.status();
//			assertTrue("Status returned nothing.", status != null
//					&& status.size() > 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Caught an Exception");
//		}
//	}
  
}

