/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.TechPackActivationandUpgradeTests;

import static org.fest.assertions.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
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

import com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests.BusyHoursViewTest;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;

/**
 * This test class contains test cases for testing the TechPack activation and  upgrade
 * 
 */
public class TechPackActivationTest {  

  private static TechPackIdeStarter starter;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TechPackActivationTest.class);
  }

  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass(): Start.");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: TechPackActivation");
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
    //CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackCheck(path);    
    Pause.pause(10000);
    CommonUtils.refreshIde();
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");

    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    assertThat(dwhTree).isNotNull().as("TechPackTree should not be null");
    final JPopupMenuFixture rightMenu = dwhTree.showPopupMenu();
    assertThat(rightMenu).isNotNull().as("A popup should be presented.");
    
    rightMenu.menuItemWithPath("Refresh").click();
    CommonUtils.waitForBusyIndicator(10000);   
    
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHUpgrade").target.isEnabled()).isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isFalse();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHQuit").target.isEnabled()).isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHActivate").target.isEnabled()).isTrue();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHBHActivate").target.isEnabled()).isFalse();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);    
    
//    robot = TechPackIdeStarter.getMyRobot();


    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);   
    
//    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");
//    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
//    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");  
//    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

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
  public void testActivateTechPack() throws Exception{
    System.out.println("testActivateTechPack(): Start.");   
   
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");    
    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    assertThat(dwhTree.target.getSelectionPath()).as("Sanity check passed: TP is Created!").isNotNull();
    
   
    TechPackIdeStarter.getMyWindow().button("DWHBHActivate").requireDisabled();    
    TechPackIdeStarter.getMyWindow().button("DWHActivate").requireEnabled();    
    
    TechPackIdeStarter.getMyWindow().button("DWHActivate").click();    
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    optionPane.yesButton().click();
    Pause.pause(90000);

    try{ 
    	GenericTypeMatcher<JOptionPane> matcher = new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
    	protected boolean isMatching(JOptionPane optionPane) {
  	    return "Techpack activation failed.".equals(optionPane.getMessage());
  	   }
  	 };
  	 		 
  		JOptionPaneFixture failPane = JOptionPaneFinder.findOptionPane(matcher).withTimeout(10000).using(TechPackIdeStarter.getMyRobot());
  		assertThat(failPane.target.isVisible()).as("Activation Failed. Fail message should not appear").isFalse();
//  		if(failPane.target.isVisible()) {
//  			failPane.okButton().click();  		
//  	  		Assert.fail("TECHPACK activation failed.Check log for more information!!!!!!!!!!!");			
// 		}
  	 } catch(Exception e){
  		 System.out.println("TechPackActivationTest(): TechPack Activation is successfull");//  		 
  	 } 	
  	 
  	JOptionPaneFixture msgPane = null;
  	
  	try {
		msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
	}catch (Exception e) {
		
		if(msgPane != null){
			msgPane.okButton().click();
			System.out.println(e.getMessage());
		}		
	}  

    
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHActivate").target.isEnabled())
		.as("After activation Activate button sould be disabled if not then Activation is failed").isFalse();
    assertThat( TechPackIdeStarter.getMyWindow().button("DWHDeactivate").target.isEnabled()).isTrue();
  	 //DataBase Assertions. DWHDb is verified for tables check
	  	try {
	  		RockFactory dwhDb = TechPackIdeStarter.getDwhDb();
	  	  	Statement stm = dwhDb.getConnection().createStatement();
	  	  	String listTablesSQL = "select table_name from systable where table_name LIKE 'DC_E_TEST%';";
	  	  	
	  	  	ResultSet rs = stm.executeQuery(listTablesSQL);
	  	  if (rs.next()){
	  		  System.out.println("TechPackActivationTest(): Test TechPack record found in DWHDB.. Now printing tables from DWHDB");
	  		do {
	  			System.out.println(rs.getString(1));
	  		}while (rs.next());
	  	  } else {
	  		  Assert.fail("No record about DC_E_TEST found in DWHDB which means techpack activation failed");
	  	  }
	  	  	
	  	  	stm.close();
	  	}catch (SQLException ex) {
	  		System.out.println(ex.getMessage());
	  	}	  	
	 
    System.out.println("testActivateTechPack(): Done.");
  }
  
  /**
   * Select an active TP. Edit one of it's Busy Hour criteria's. 
   * Save the edit. Return to the Manage DWH tab and select the TP and choose
   * Activate Busy Hour Criteria. 
   * @throws Exception
   */
  
  //TODO: TechPack Activation fails because of the no partition plans for DC_E_TEST
  @org.junit.Ignore
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
    
   
    BusyHoursViewTest.collapseBHTreeAfterEdit(bhTree, measurementTypeName);
    myEditWindow.normalize();

    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");    
    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    assertThat(dwhTree.target.getSelectionPath()).as("Sanity check passed: TP is Created!").isNotNull();
    
    
    TechPackIdeStarter.getMyWindow().button("DWHBHActivate").requireDisabled();    
    TechPackIdeStarter.getMyWindow().button("DWHActivate").requireEnabled();    
    TechPackIdeStarter.getMyWindow().button("DWHActivate").click();    
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    optionPane.yesButton().click();
    CommonUtils.waitForBusyIndicator(180000);

  
  GenericTypeMatcher<JOptionPane> matcher = new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
  	   protected boolean isMatching(JOptionPane optionPane) {
  	     return "Techpack activation failed.".equals(optionPane.getMessage());
  	   }
  	 };
  	 try{  		 
  		JOptionPaneFixture failPane = JOptionPaneFinder.findOptionPane(matcher).withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
  		if(failPane.target.isVisible()) {  	  		
//  	  		  JOptionPaneFixture msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
//  	  		  msgPane.okButton().click();
   	  		Assert.fail("TECHPACK activation failed.Check log for more information!!!!!!!!!!!");

  		}
  	 } catch(Exception e){
  		 System.out.println("Check log for activation results");
  	 } 
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
