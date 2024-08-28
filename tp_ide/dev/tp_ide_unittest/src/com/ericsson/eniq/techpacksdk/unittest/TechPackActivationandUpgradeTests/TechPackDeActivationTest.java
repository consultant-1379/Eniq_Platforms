/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests;

import static org.fest.assertions.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.fest.swing.timing.Timeout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;

/**
 * This test class contains test cases for testing the TechPack activation and  upgrade
 * 
 */
public class TechPackDeActivationTest {  

  private static TechPackIdeStarter starter;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TechPackDeActivationTest.class);
  }

  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass(): Start.");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: TechPackDeActivation");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    //CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackCheck(path);    
    
    CommonUtils.refreshIde();
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");

    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    assertThat(dwhTree).isNotNull().as("TechPackTree should not be null");
    final JPopupMenuFixture rightMenu = dwhTree.showPopupMenu();
    assertThat(rightMenu).isNotNull().as("A popup should be presented.");
    
    rightMenu.menuItemWithPath("Refresh").click();
    CommonUtils.waitForBusyIndicator(10000);   
    
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHUpgrade").target.isEnabled()).isFalse();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHQuit").target.isEnabled()).isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHActivate").target.isEnabled()).isFalse();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHBHActivate").target.isEnabled()).isFalse();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);    
    
//    robot = TechPackIdeStarter.getMyRobot();


    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);   
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

    System.out.println("tearDownAfterClass(): Done.");
  }


  @Before
  public void setUp() throws Exception {
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	  System.out.println("setUp(): Executing Test Case");
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    CommonUtils.resetTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

  }

  @After
  public void tearDown() throws Exception {
    System.out.println("tearDown()");

    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);   

  }
  /**
   * Duplicate Test TechPack and 
   * Return to the Manage DWH tab and select the TP and choose
   * 
   * @throws Exception
   */  
  @org.junit.Test
  public void testDeActivateTechPack() throws Exception{
    System.out.println("testDe-ActivateTechPack(): Start.");   
   
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");    
    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    assertThat(dwhTree.target.getSelectionPath()).as("Sanity check passed: TP is Created!").isNotNull();
    
   
    TechPackIdeStarter.getMyWindow().button("DWHBHActivate").requireDisabled();    
    assertThat(
	        TechPackIdeStarter.getMyWindow().button("DWHDeactivate").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("DeActivate button should be enabled after activation.").isTrue(); 
    
    TechPackIdeStarter.getMyWindow().button("DWHDeactivate").click();    
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    optionPane.yesButton().click();
    Pause.pause(90000);
    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHActivate").target.isEnabled())
	.as("Activate button should be enabled after successful DeActivation of TechPack").isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isFalse();
  //DataBase Assertions. DWHDb is verified for tables check
  	try {
  		RockFactory dwhDb = TechPackIdeStarter.getDwhDb();
  	  	Statement stm = dwhDb.getConnection().createStatement();
  	  	String listTablesSQL = "select table_name from systable where table_name LIKE 'DC_E_TEST%';";
  	  	
  	  	ResultSet rs = stm.executeQuery(listTablesSQL);
  	  	Assert.assertTrue("Test TechPack record not found in DWHDB.. De-Activation is successful", !rs.next());  	 
  		System.out.println("testDe-ActivateTechPack(): Test TechPack record not found in DWHDB.. De-Activation is successful"); 
  	
  	  	stm.close();
  	}catch (SQLException ex) {
  		System.out.println(ex.getMessage());
  	}

    
    System.out.println("testDe-ActivateTechPack(): Done.");
  }
  
  
  /**
   * A Helper method.
   * Checks if the given techPack exists in the tree.
   * IF NOT it creates a new techPack and selects it.
   * @param techPackPath
   */
  private static void techPackCheck(String tp) {
	  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	  JTreeFixture techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	  assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");
	   
	  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	  techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	  assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");
	   
	  try {
		  techPackTree.target.clearSelection();
		  System.out.println("Trying to select treepath=" + tp);    
		  techPackTree.selectPath(tp);
		  lockSelectedTechPack();
	  } catch (LocationUnavailableException lue) {
	      System.out.println("techPackSanityCheck(): Sanity check failed. " + "New techpack does not exist.");
		}
	    
	    if (techPackTree.target.getSelectionPath() == null) {	
	    	System.out.println("techPackSanityCheck(): Creating Test TechPack....");
	    	techPackTree.selectPath(TestSetupConstants.TEST_SERVER);
	    	CommonUtils.copyTechPack(TestSetupConstants.TEST_TP_PATH, 30000, TestSetupConstants.TP_NEW_VERSION,
	    			TestSetupConstants.BASE_TP_VERSIONID);
	    }
	    techPackTree.selectPath(tp);
	    String selectionPath = techPackTree.target.getSelectionPath().toString();
	    assertThat(selectionPath.equals("["+TestSetupConstants.TEST_SERVER+","+ TestSetupConstants.TEST_TP_NAME + "," + TestSetupConstants.TEST_TP_NAME +":"+TestSetupConstants.TP_NEW_VERSION+"]"));
	  
  }
  
  private static void lockSelectedTechPack() {
		// Lock the techpack, if not already locked.
	    if (TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()) {
	      TechPackIdeStarter.getMyWindow().button("TechPackLock").click();
	      System.out.println("lockSelectedTechPack(): Techpack locked.");
	    } else {
	      System.out.println("lockSelectedTechPack(): Techpack was already locked.");
	    }
	} // lockSelectedTechPack
  
  
}
