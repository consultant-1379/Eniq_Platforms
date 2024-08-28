/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests;

import static org.fest.assertions.Assertions.assertThat;

import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.BusyHoursViewTest;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;

/**
 * This test class contains test cases for testing the TechPack activation and  upgrade
 * 
 */
public class ActivateBHCriteriaTest {  

  private static TechPackIdeStarter starter;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ActivateBHCriteriaTest.class);
  }

  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass(): Start.");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: ActivateBHCriteriaTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    Pause.pause(10000);
    CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackCheck(path);    
    Pause.pause(10000);
    CommonUtils.refreshIde();
    
//    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");
//
//    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
//    assertThat(dwhTree).isNotNull().as("TechPackTree should not be null");
//    final JPopupMenuFixture rightMenu = dwhTree.showPopupMenu();
//    assertThat(rightMenu).isNotNull().as("A popup should be presented.");
//    
//    rightMenu.menuItemWithPath("Refresh").click();
//    CommonUtils.waitForBusyIndicator(20000);   
//    
//    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
//    
//    //assertThat( TechPackIdeStarter.getMyWindow().button("DWHUpgrade").target.isEnabled()).isTrue();
//    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isTrue();
//    assertThat( TechPackIdeStarter.getMyWindow().button("DWHQuit").target.isEnabled()).isTrue();
//    assertThat( TechPackIdeStarter.getMyWindow().button("DWHActivate").target.isEnabled()).isFalse();
//    assertThat( TechPackIdeStarter.getMyWindow().button("DWHBHActivate").target.isEnabled()).isFalse();

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
   * Select an active TP. Edit one of it's Busy Hour criteria's. 
   * Save the edit. Return to the Manage DWH tab and select the TP and choose
   * Activate Busy Hour Criteria. 
   * @throws Exception
   */
  
  @org.junit.Test
  public void testActivateBHEnabledWhenPlaceHolderModified() throws Exception{
    System.out.println("testActivateBHEnabledWhenPlaceHolderModified(): Start.");     
    final String measurementTypeName = TestSetupConstants.TEST_TP_NAME + "_" + "AAL2APBH";
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack"); 
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackCheck(path);
    CommonUtils.refreshIde();    
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    FrameFixture techPackEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(techPackEditWindow).isNotNull().as("The edit window should be shown.");

    JTabbedPaneFixture techPackEditPanel = techPackEditWindow.tabbedPane();
    assertThat(techPackEditPanel).isNotNull().as("The panel should be visible.");

    techPackEditPanel.selectTab("Busy Hours");
    FrameFixture myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");

    myEditWindow.tabbedPane().selectTab("Busy Hours");
    Pause.pause(5000);
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
    
    System.out.println("Just set the winodw to maximum size!!");
    myEditWindow.maximize();      
       
    JTableFixture prodBhTableFixture = BusyHoursViewTest.selectProdPlaceHoldersTable(bhTree, measurementTypeName);
    JTableCellFixture cell = null;
    int row = 0;
   
    cell = prodBhTableFixture.cell(TableCell.row(row).column(BusyHoursViewTest.COL_NUM_DESCRIPTION));    
    cell.enterValue("Criteria One");
  
    cell = prodBhTableFixture.cell(TableCell.row(row).column(BusyHoursViewTest.COL_NUM_WHERE));
    //cell.enterValue("Where Clause"); 

    myEditWindow.button("BusyHourViewSave").click();  
    CommonUtils.waitForBusyIndicator(100000);
    
    myEditWindow.normalize();
    myEditWindow.button("BusyHourViewClose").click();
    
    //DATABASE ASSERTIONS
    // Verify this this sets REACTIVATEVIEWS to 1
    RockFactory rock = TechPackIdeStarter.getDwhRepDb();
    
    Busyhour bh = new Busyhour(rock);
    bh.setVersionid(TestSetupConstants.COPY_TP_VERSIONID);
    bh.setBhlevel(measurementTypeName);
    bh.setReactivateviews(1);
    
    BusyhourFactory bhF = null;
	try {
		bhF = new BusyhourFactory(rock, bh);
	} catch (Exception e) {
		System.out.println("Exception in gettong Busygour factory");
		e.printStackTrace();
	}
	
	assertThat(bhF.size() != 0).as("After placehoder modification, in DWHREP there must be at least one row with REACTIVATEVIEWS set to 1");

    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");    
    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    assertThat(dwhTree.target.getSelectionPath()).as("Sanity check passed: TP is Created!").isNotNull();
    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isTrue();
    assertThat(TechPackIdeStarter.getMyWindow().button("DWHBHActivate").target.isEnabled()).isTrue();
    TechPackIdeStarter.getMyWindow().button("DWHActivate").requireDisabled();
    
    TechPackIdeStarter.getMyWindow().button("DWHBHActivate").click();    
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    optionPane.yesButton().click();
    CommonUtils.waitForBusyIndicator(180000);
    
    DialogFixture savedDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Saved");
    savedDialog.optionPane().okButton().click();
    
    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isTrue();
    assertThat(TechPackIdeStarter.getMyWindow().button("DWHBHActivate").target.isEnabled()).isFalse();
    TechPackIdeStarter.getMyWindow().button("DWHActivate").requireDisabled();  
    System.out.println("testActivateBHEnabledWhenPlaceHolderModified(): Done.");
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
