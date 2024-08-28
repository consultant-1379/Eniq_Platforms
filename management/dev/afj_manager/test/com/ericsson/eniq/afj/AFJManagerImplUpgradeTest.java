/**
 * 
 */
package com.ericsson.eniq.afj;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.afj.common.DatabaseAdmin;
import com.ericsson.eniq.afj.common.DatabaseLockAction;
import com.ericsson.eniq.afj.common.DatabaseState;
import com.ericsson.eniq.afj.common.EngineAdminFactory;
import com.ericsson.eniq.afj.common.FileArchiver;
import com.ericsson.eniq.afj.common.FileArchiverFactory;
import com.ericsson.eniq.afj.common.PropertiesUtility;
import com.ericsson.eniq.afj.common.PropertyConstants;
import com.ericsson.eniq.afj.common.RockDatabaseAdminFactory;
import com.ericsson.eniq.afj.common.RockDatabaseLockActionFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtils;
import com.ericsson.eniq.afj.database.AFJDatabaseCommonUtilsFactory;
import com.ericsson.eniq.afj.database.AFJDatabaseHandler;
import com.ericsson.eniq.afj.upgrade.TechPackRestorer;
import com.ericsson.eniq.afj.upgrade.TechPackRestorerFactory;
import com.ericsson.eniq.afj.upgrade.TechPackUpgrader;
import com.ericsson.eniq.afj.upgrade.TechPackUpgraderFactory;
import com.ericsson.eniq.afj.xml.AFJComparatorFactory;
import com.ericsson.eniq.afj.xml.AFJParser;
import com.ericsson.eniq.afj.xml.AFJParserFactory;
import com.ericsson.eniq.afj.xml.CompareInterface;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;

/**
 * @author eheijun
 * 
 */
public class AFJManagerImplUpgradeTest {

  private final Mockery context = new JUnit4Mockery() {

    {
      setImposteriser(ClassImposteriser.INSTANCE);
    }
  };

  private static final String PROFILE_NORMAL = "NORMAL";

  private static final String PROFILE_NOLOADS = "NoLoads";

  private static final String TEST_TP = "DC_E_TEST";  

  private static final String TEST_ANOTHER_TP = "DC_E_ANOTHER_TEST";

  private static final String INVALID_TP = "DC_E_INVALID";

  private static final String TEST_DIR = "test";

  private static final String SOME_MIM_XML = "someMIM.xml";

  private static final String SUCCESS_MESSAGE = "success message";

  private EngineAdmin mockEngineAdmin;

  private AFJDatabaseHandler mockDatabaseHandler;

  private DatabaseLockAction mockDatabaseLockAction;

  private FileArchiver mockFileArchiver;

  private RockFactory mockDbaDwh;
  
  private RockFactory mockDwhrep;

  private RockFactory mockEtlrep;

  private RockFactory mockDwh;
  
  private Tpactivation mockTpActivation;

  private AFJTechPack mockTechPack;

  private AFJDelta mockDelta;

  private DatabaseAdmin mockDatabaseAdmin;

  private TechPackUpgrader mockTechPackUpgrader;

  private PropertiesUtility mockPropertiesUtility;

  private Properties mockAfjProperties;

  private File mockBackUpFile;

  private TechPackRestorer mockTechPackRestorer;  
  
  private AFJDatabaseCommonUtils mockAFJDatabaseCommonUtils;
  
  private AFJParser mockAFJParser;
  
  private CompareInterface mockComparator; 
  
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    
	  final File testDir = new File(TEST_DIR);
    if (!testDir.exists()) {
      testDir.mkdir();
    }
    
    final File testTpDir = new File(testDir, TEST_TP);
    if (!testTpDir.exists()) {
      testTpDir.mkdir();
    }
    
    final File testAnotherTpDir = new File(testDir, TEST_ANOTHER_TP);
    if (!testAnotherTpDir.exists()) {
      testAnotherTpDir.mkdir();
    }
    
//    final FileWriter fwriter = new FileWriter(TEST_MIM_XML);
//    try {
//      final PrintWriter out = new PrintWriter(fwriter);
//      try {
//        out.println("<html></html>");
//      } finally {
//        out.close();
//      }
//    } finally {
//      fwriter.close();
//    }
    
    
    mockEngineAdmin = context.mock(EngineAdmin.class);

    mockDatabaseHandler = context.mock(AFJDatabaseHandler.class);

    mockDatabaseLockAction = context.mock(DatabaseLockAction.class);

    mockFileArchiver = context.mock(FileArchiver.class);

    mockDbaDwh = context.mock(RockFactory.class, "rockDbaDwh");

    mockDwhrep = context.mock(RockFactory.class, "rockDwhrep");

    mockEtlrep = context.mock(RockFactory.class, "rockEtlrep");

    mockDwh = context.mock(RockFactory.class, "rockDwh");

    mockTechPack = context.mock(AFJTechPack.class);
    
    mockDelta = context.mock(AFJDelta.class);

    mockDatabaseAdmin = context.mock(DatabaseAdmin.class);

    mockTechPackUpgrader = context.mock(TechPackUpgrader.class);

    mockPropertiesUtility = context.mock(PropertiesUtility.class);

    mockAfjProperties = context.mock(Properties.class);

    mockBackUpFile = context.mock(File.class);
    
    mockTechPackRestorer = context.mock(TechPackRestorer.class);
    
    mockAFJDatabaseCommonUtils = context.mock(AFJDatabaseCommonUtils.class);
    
    mockAFJParser = context.mock(AFJParser.class);
    
    mockComparator = context.mock(CompareInterface.class);
    
    mockTpActivation = context.mock(Tpactivation.class);

    EngineAdminFactory.setInstance(mockEngineAdmin);

    AFJDatabaseHandler.setInstance(mockDatabaseHandler);

    FileArchiverFactory.setInstance(mockFileArchiver);

    RockDatabaseLockActionFactory.setDWHInstance(mockDatabaseLockAction);
    
    RockDatabaseAdminFactory.setDWHInstance(mockDatabaseAdmin);

    TechPackUpgraderFactory.setUpgrader(mockTechPackUpgrader);
    
    TechPackRestorerFactory.setInstance(mockTechPackRestorer); 
    
    AFJDatabaseCommonUtilsFactory.setInstance(mockAFJDatabaseCommonUtils);  
    
    AFJParserFactory.setParser(mockAFJParser);  
    
    AFJComparatorFactory.setComparator(mockComparator);

    PropertiesUtility.setPropertiesUtility(mockPropertiesUtility);

    PropertiesUtility.setAfjProperties(mockAfjProperties);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    PropertiesUtility.setAfjProperties(null);
    PropertiesUtility.setPropertiesUtility(null);
    AFJDatabaseCommonUtilsFactory.setInstance(null);
    TechPackUpgraderFactory.setUpgrader(null);
    RockDatabaseAdminFactory.setDWHInstance(null);
    RockDatabaseLockActionFactory.setDWHInstance(null);
    FileArchiverFactory.setInstance(null);
    AFJDatabaseHandler.setInstance(null);
    EngineAdminFactory.setInstance(null);
    
    final File testDir = new File(TEST_DIR);
    final File testTpDir = new File(testDir, TEST_TP);
    final File testAnotherTpDir = new File(testDir, TEST_ANOTHER_TP);
    
    if (testAnotherTpDir.exists()) {
      testAnotherTpDir.delete();
    }
    
    if (testTpDir.exists()) {
      testTpDir.delete();
    }
    
    if (testDir.exists()) {
      testDir.delete();
    }
    
    
  }

//  @Test
//  public void testGetAFJDelta() throws AFJException {
//    
//    context.checking(new Expectations() {
//
//      {
//        allowing(mockDatabaseHandler).getDwhrep();
//        will(returnValue(mockDwhrep));
//        allowing(mockTechPack).getTechPackName();
//        will(returnValue(TEST_TP));
//        allowing(mockTechPack).getFileName();
//        will(returnValue(SOME_MIM_XML));
//        allowing(mockAfjProperties).getProperty(PROP_STN_NAME, null);
//        will(returnValue(TEST_TP));
//        allowing(mockAfjProperties).getProperty(PROP_AFJ_BASE_DIR, null);
//        will(returnValue(TEST_DIR));
//        allowing(mockAfjProperties).getProperty(PROP_AFJ_SCHEMA_PACKAGE, null);
//        will(returnValue(TEST_DIR));
//        allowing(mockDatabaseCommonUtils).getActiveTechPackVersion(TEST_TP, mockDwhrep);
//        will(returnValue(TEST_TP_VERSION));
//
//      }
//    });
//    
//    AFJManagerImpl afjmgr = new AFJManagerImpl();
//    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
//    AFJDelta result = afjmgr.getAFJDelta(mockTechPack);
//    assertNotNull(result);
//  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test
  public void testUpgradeAFJTechPack() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseAdmin).isState(with(any(DatabaseState.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(returnValue(true));

        allowing(mockTechPackUpgrader).upgrade(mockDelta);
        will(returnValue(SUCCESS_MESSAGE));

        allowing(mockDelta).getMomFileName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
        will(returnValue(TEST_DIR));

        allowing(mockFileArchiver).backupFile(with(any(File.class)), with(any(File.class)));
        will(returnValue(mockBackUpFile));

        allowing(mockBackUpFile).getName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    final String result = afjmgr.upgradeAFJTechPack(mockDelta);
    assertTrue(SUCCESS_MESSAGE.equals(result));
  }

	/**
	 * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJDelta(final AFJTechPack techPack)}.
	 */
	@Test	
	public void testGetAFJDelta() throws Exception{
	    context.checking(new Expectations() {

	        {	        	 
	            allowing(mockDatabaseHandler).getDwhrep();
	            will(returnValue(mockDwhrep));

	            allowing(mockDatabaseHandler).getEtlrep();
	            will(returnValue(mockEtlrep));
	            allowing(mockDatabaseHandler).getDwh();
	            will(returnValue(mockDwh));
	            allowing(mockDatabaseHandler).getDbaDwh();
	            will(returnValue(mockDbaDwh));
	            
	            allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_HSS_NAME, null);
		        will(returnValue("DC_E_HSS"));
		        
		        allowing(mockTechPack).getTechPackName();
		        will(returnValue("DC_E_TEST"));	        
		      
		        
		        allowing(mockAFJDatabaseCommonUtils).getActiveTechPackVersion(with(any(String.class)), with(any(RockFactory.class)));
	            will(returnValue("DC_E_BSS:((1))"));

	            allowing(mockAFJParser).setAfjTechPack(with(any(AFJTechPack.class)));	            
	            
	            allowing(mockAFJParser).parse(with(any(AFJTechPack.class)));
	            will(returnValue("Some PM Object"));         	            
	            
	            allowing(mockComparator).getMeasTypeDelta(with(any(Object.class)));
	            will(returnValue(new ArrayList<String>()));
	            
	            allowing(mockTechPack).getFileName();
		        will(returnValue("Some File"));

	            allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
	            will(returnValue(TEST_DIR));
	            
	            allowing(mockDatabaseHandler).closeConnections();
	            
	              
	        	
	        }
	    });
	    
	    final AFJManagerImpl afjmgr = new AFJManagerImpl();
	    afjmgr.getAFJDelta(mockTechPack);
	    
	}
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackEngineNoLoads() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NOLOADS));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockDatabaseHandler).closeConnections();

      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackEngineCheckFails() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(throwException(new Exception("CurrentProfile Failed.")));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackDatabaseMaintenace() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(false));

        allowing(mockDatabaseHandler).closeConnections();
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackProfileChangeFails() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(false));

        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackDatabaseLockChangeFails() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(false));

        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackDatabaseLockChangeFailsWithException() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(throwException(new Exception("Lock Failed.")));

        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test
  public void testUpgradeAFJTechPackDatabaseUnlockChangeFails() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(returnValue(false));
        
        allowing(mockTechPackUpgrader).upgrade(mockDelta);
        will(returnValue(SUCCESS_MESSAGE));

        allowing(mockDelta).getMomFileName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
        will(returnValue(TEST_DIR));

        allowing(mockFileArchiver).backupFile(with(any(File.class)), with(any(File.class)));
        will(returnValue(mockBackUpFile));

        allowing(mockBackUpFile).getName();
        will(returnValue(SOME_MIM_XML));
        
        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test
  public void testUpgradeAFJTechPackDatabaseUnlockChangeFailsWithException() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(throwException(new Exception("UnLock Failed.")));
        
        allowing(mockTechPackUpgrader).upgrade(mockDelta);
        will(returnValue(SUCCESS_MESSAGE));

        allowing(mockDelta).getMomFileName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
        will(returnValue(TEST_DIR));

        allowing(mockFileArchiver).backupFile(with(any(File.class)), with(any(File.class)));
        will(returnValue(mockBackUpFile));

        allowing(mockBackUpFile).getName();
        will(returnValue(SOME_MIM_XML));
        
        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test
  public void testUpgradeAFJTechPackMIMFileBackupFailsWithException() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(returnValue(true));
        
        allowing(mockTechPackUpgrader).upgrade(mockDelta);
        will(returnValue(SUCCESS_MESSAGE));

        allowing(mockDelta).getMomFileName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
        will(returnValue(TEST_DIR));

        allowing(mockFileArchiver).backupFile(with(any(File.class)), with(any(File.class)));
        will(throwException(new IOException("Backup Failed As It Should Do.")));

        allowing(mockBackUpFile).getName();
        will(returnValue(SOME_MIM_XML));
        
        allowing(mockDatabaseHandler).closeConnections();
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test
  public void testUpgradeAFJTechPackMIMFileBackupReturnsNullFile() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDelta).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockDatabaseAdmin).isState(with(DatabaseState.NORMAL));
        will(returnValue(true));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(returnValue(true));
        
        allowing(mockTechPackUpgrader).upgrade(mockDelta);
        will(returnValue(SUCCESS_MESSAGE));

        allowing(mockDelta).getMomFileName();
        will(returnValue(SOME_MIM_XML));

        allowing(mockAfjProperties).getProperty(with(any(String.class)), with(any(String.class)));
        will(returnValue(TEST_DIR));

        allowing(mockFileArchiver).backupFile(with(any(File.class)), with(any(File.class)));
        will(returnValue(null));

        allowing(mockBackUpFile).getName();
        will(returnValue(SOME_MIM_XML));
        
        allowing(mockDatabaseHandler).closeConnections();
        
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(mockDelta);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#upgradeAFJTechPack(com.ericsson.eniq.afj.common.AFJDelta)}.
   * 
   * @throws Exception
   */
  @Test(expected = AFJException.class)
  public void testUpgradeAFJTechPackWithNullDelta() throws Exception {

    context.checking(new Expectations() {

      {
        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));
        
        allowing(mockDatabaseHandler).closeConnections();

      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.setDatabaseAdmin(mockDatabaseAdmin);
    afjmgr.upgradeAFJTechPack(null);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPack()}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetAFJTechPacks() throws Exception {

    context.checking(new Expectations() {
      {

        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_BASE_DIR, null);
        will(returnValue(TEST_DIR));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJTECHPACKS, null);
        will(returnValue(TEST_TP + "," + TEST_ANOTHER_TP));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_MAXCOUNTERS, "100");
        will(returnValue("100"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_MAXMEASTYPES, "20");
        will(returnValue("20"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
        will(returnValue("DC_E_BSS"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
        will(returnValue("DC_E_STN"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE_IMS, null);
        will(returnValue("pm_mim_xsd.xsd"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE_IMS, null);
        will(returnValue("com.ericsson.ims.mim"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_IMS_NAMESPACEWARE, null);
        will(returnValue("false"));
        
      }
    });
    
    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    final List<AFJTechPack> result = afjmgr.getAFJTechPacks();
    assertNotNull(result);
    assertTrue(result.size() == 2);
    
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPack()}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetAFJTechPack() throws Exception {

    context.checking(new Expectations() {
      {

        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_BASE_DIR, null);
        will(returnValue(TEST_DIR));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJTECHPACKS, null);
        will(returnValue(TEST_TP));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_MAXCOUNTERS, "100");
        will(returnValue("100"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_MAXMEASTYPES, "20");
        will(returnValue("20"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
        will(returnValue("DC_E_BSS"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
        will(returnValue("DC_E_STN"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE_IMS, null);
        will(returnValue("pm_mim_xsd.xsd"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE_IMS, null);
        will(returnValue("com.ericsson.ims.mim"));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_IMS_NAMESPACEWARE, null);
        will(returnValue("false"));    
        
        
      }
    });
    
    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    final AFJTechPack result = afjmgr.getAFJTechPack(TEST_TP);
    assertNotNull(result);
    
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getAFJTechPack()}.
   * @throws AFJException 
   */ 
  @Test(expected=AFJConfiguationException.class)
  public void testGetAFJTechPackInvalid() throws Exception {

    context.checking(new Expectations() {
      {

        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_BASE_DIR, null);
        will(returnValue(TEST_DIR));
        
        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJTECHPACKS, null);
        will(returnValue(INVALID_TP));
        
      }
    });
    
    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    afjmgr.getAFJTechPack(TEST_TP);
    
  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#restoreAFJTechPack()}.
   * @throws AFJException 
   */ 
  @Test
  public void testRestoreAFJTechPack() throws Exception {

    context.checking(new Expectations() {

      {

        allowing(mockTechPack).getTechPackName();
        will(returnValue(TEST_TP));

        allowing(mockDatabaseHandler).getDbaDwh();
        will(returnValue(mockDbaDwh));

        allowing(mockDatabaseHandler).getDwhrep();
        will(returnValue(mockDwhrep));

        allowing(mockDatabaseHandler).getEtlrep();
        will(returnValue(mockEtlrep));

        allowing(mockDatabaseHandler).getDwh();
        will(returnValue(mockDwh));

        allowing(mockEngineAdmin).getCurrentProfile();
        will(returnValue(PROFILE_NORMAL));

        allowing(mockEngineAdmin).changeProfile(with(any(String.class)));
        will(returnValue(true));

        allowing(mockDatabaseAdmin).isState(with(any(DatabaseState.class)));
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performLock();
        will(returnValue(true));

        allowing(mockDatabaseLockAction).performUnlock();
        will(returnValue(true));

        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_FOLLOWJOHN, null);
        will(returnValue(TEST_TP));
        
        allowing(mockTechPackRestorer).restore();
        will(returnValue(true));
        
        allowing(mockDatabaseHandler).closeConnections();
      }
    });

    final AFJManagerImpl afjmgr = new AFJManagerImpl();
    final Boolean result = afjmgr.restoreAFJTechPack(mockTechPack);
    assertTrue(result);
  }

  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getContextPath(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetContextPathIMS() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE_IMS, null);
		        will(returnValue("com.ericsson.ims.mim"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getContextPath = pcClass.getDeclaredMethod("getContextPath", new Class[] {String.class});
		getContextPath.setAccessible(true);	
		String actualResult = (String) getContextPath.invoke(objectUnderTest, new Object[] {"DC_E_IMS"});
		String expectedResult = "com.ericsson.ims.mim";
	    assertTrue(actualResult.equalsIgnoreCase(expectedResult));

  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getContextPath(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetContextPath() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_PACKAGE, null);
		        will(returnValue("com.ericsson.eniq.afj.schema"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getContextPath = pcClass.getDeclaredMethod("getContextPath", new Class[] {String.class});
		getContextPath.setAccessible(true);	
		String actualResult = (String) getContextPath.invoke(objectUnderTest, new Object[] {"DC_E_BSS"});
		String expectedResult = "com.ericsson.eniq.afj.schema";
	    assertTrue(actualResult.equalsIgnoreCase(expectedResult));

  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getSchemaName(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetSchemaName() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE, null);
		        will(returnValue("oss_rc_pm_mim_PA5.xsd"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getSchemaName = pcClass.getDeclaredMethod("getSchemaName", new Class[] {String.class});
		getSchemaName.setAccessible(true);	
		String actualResult = (String) getSchemaName.invoke(objectUnderTest, new Object[] {"DC_E_BSS"});
		String expectedResult = "oss_rc_pm_mim_PA5.xsd";
	    assertTrue(actualResult.equalsIgnoreCase(expectedResult));

  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#getSchemaName(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testGetSchemaNameIMS() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_AFJ_SCHEMA_FILE_IMS, null);
		        will(returnValue("pm_mim_xsd.xsd"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method getSchemaName = pcClass.getDeclaredMethod("getSchemaName", new Class[] {String.class});
		getSchemaName.setAccessible(true);	
		String actualResult = (String) getSchemaName.invoke(objectUnderTest, new Object[] {"DC_E_IMS"});
		String expectedResult = "pm_mim_xsd.xsd";
	    assertTrue(actualResult.equalsIgnoreCase(expectedResult));

  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#isNamespaceAware(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testIsNamespaceAwareIMS() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_IMS_NAMESPACEWARE, null);
		        will(returnValue("true"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method isNamespaceAware = pcClass.getDeclaredMethod("isNamespaceAware", new Class[] {String.class});
		isNamespaceAware.setAccessible(true);	
		boolean actualResult = (Boolean) isNamespaceAware.invoke(objectUnderTest, new Object[] {"DC_E_IMS"});		
	    assertTrue(actualResult == true);

  }
  
  /**
   * Test method for {@link com.ericsson.eniq.afj.AFJManagerImpl#isNamespaceAware(String)}.
   * @throws AFJException 
   */ 
  @Test
  public void testIsNamespaceAware() throws Exception {
	  context.checking(new Expectations() {

		  {
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_BSS_NAME, null);
		        will(returnValue("DC_E_BSS"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_STN_NAME, null);
		        will(returnValue("DC_E_STN"));
		        
		        allowing(mockAfjProperties).getProperty(PropertyConstants.PROP_DEFAULT_NAMESPACEAWARE, null);
		        will(returnValue("false"));
		  }
	  });
	  
	  	final AFJManagerImpl objectUnderTest = new AFJManagerImpl();
		/* Reflecting the tested method */
		final Class pcClass = objectUnderTest.getClass();
		final Method isNamespaceAware = pcClass.getDeclaredMethod("isNamespaceAware", new Class[] {String.class});
		isNamespaceAware.setAccessible(true);	
		boolean actualResult = (Boolean) isNamespaceAware.invoke(objectUnderTest, new Object[] {"DC_E_BSS"});		
	    assertTrue(actualResult == false);
  }
  
}
