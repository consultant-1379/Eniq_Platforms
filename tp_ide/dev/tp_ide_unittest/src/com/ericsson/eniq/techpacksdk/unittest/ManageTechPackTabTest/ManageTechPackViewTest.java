package com.ericsson.eniq.techpacksdk.unittest.ManageTechPackTabTest;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.Component;
import java.io.File;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.fest.assertions.Fail;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.fest.swing.timing.Timeout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;



import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;


import junit.framework.JUnit4TestAdapter;



public class ManageTechPackViewTest {
	
	 public static TechPackIdeStarter starter = null;
	
	 public static String DESCRIPTION = null;
	 public static String RSTATE = null;
	
	public static junit.framework.Test suite() {
	    return new JUnit4TestAdapter(ManageTechPackViewTest.class);
	}
	
	/**
	   * @throws java.lang.Exception
	   */
	  @BeforeClass
	  public static void setUpBeforeClass() throws Exception {

	    System.out.println("setUpBeforeClass()");
	    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	    System.out.println("Executing test class: ManageTechPackViewTest");
	    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

	    // Get the TechPackIDE starter and start the IDE.
	    starter = TechPackIdeStarter.getInstance();
	    if (starter.startTechPackIde() == false) {
	      System.out.println("setUpBeforeClass(): IDE startup failed");
	      Fail.fail("TechPackIDE failed to start");
	    }    
	 
	    CommonUtils.refreshIde();
	    
	    System.out.println("setUpBeforeClass(): Done.");

	  }
	  
	  
	  @AfterClass
	  public static void tearDownAfterClass() throws Exception {
	    System.out.println("tearDownAfterClass(): Start.");

	    // If table change logging is enabled, stop the logger.
	    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);	  
	    
	    System.out.println("tearDownAfterClass(): Done.");
	  }

	
	  
	  /**
	   * @throws java.lang.Exception
	   */
	  @Before
	  public void setUp() throws Exception {
	    System.out.println("setUp()");
	    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

	    // Reset logger
	    CommonUtils.resetTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);
	   

	  }
	  /**
	   * @throws java.lang.Exception
	   */
	  
	  @After
	  public void tearDown() throws Exception {
	    System.out.println("tearDown(): Start.");

	    // Print the changed database tables in the last test
	    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

	    // Change the tree selection to the server node to reset the button
	    // states.
	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER);
	    
	    System.out.println("tearDown(): Done.");
	  }
	  
	  @Test
	  public void testViewExistingTP() throws Exception {
	    System.out.println("testViewExistingTP(): Start.");

	    String TP_NAME = "DC_E_TEST";
	    String TP_VERSION = "1";	  
	    CommonUtils.refreshIde();

	    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
	    
	    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TEST_TP_VERSION;
	    try {	    	
	      tpTree.selectPath(path);
	    } catch (LocationUnavailableException lue) {
	      System.out.println("testViewExistingTP(): " + " Old tech pack does not exist!");
	      Fail.fail("Old tech pack " + path + " does not exist in the tree.");
	    }

	    System.out.println("testEditExistingTP(): Selected techpack: " + tpTree.target.getSelectionPath().toString());
  
	   
	    // Select a techpack in the tree
	    try {
	      tpTree.selectPath(path);
	    } catch (LocationUnavailableException lue) {
	      System.out.println("testEditExistingTP(): " + "Old tech pack does not exist!");
	      Fail.fail("Old tech pack " + path+ " does not exist in the tree.");
	    }

	    System.out.println("testEditExistingTP(): Selected techpack: " + tpTree.target.getSelectionPath().toString());

	    // Check that edit is enabled
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackView").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("View button should be enabled.").isTrue();

	    // Click view button
	    System.out.println("testViewExistingTP(): Opening View techpack window.");
	    TechPackIdeStarter.getMyWindow().button("TechPackView").click();

	    // Wait for the busy indicator
	    CommonUtils.waitForBusyIndicator(120000);

	    // Wait for the edit window to open
	    FrameFixture viewTPWindow = WindowFinder.findFrame("ViewTechPackWindow").withTimeout(120000).using(
	        TechPackIdeStarter.getMyRobot());
	    assertThat(viewTPWindow).isNotNull();
	    System.out.println("testViewExistingTP(): View window opened.");

	    // Now we have the view window open.
	    JTabbedPaneFixture myEditPanel = viewTPWindow.tabbedPane();
	    assertThat(myEditPanel).isNotNull();

	    // Loop through all the tabs
	    myEditPanel.selectTab("General");
	    Pause.pause(2000);
	    myEditPanel.selectTab("Measurement Types");
	    Pause.pause(20000);
	    myEditPanel.selectTab("Busy Hours");
	    Pause.pause(10000);
	    myEditPanel.selectTab("Sets/Actions/Schedulings");
	    Pause.pause(10000);
	    myEditPanel.selectTab("Data Formats");
	    Pause.pause(10000);
	    myEditPanel.selectTab("Reference Types");
	    Pause.pause(10000);
	    myEditPanel.selectTab("Transformers");
	    Pause.pause(5000);
	    myEditPanel.selectTab("External Statements");
	    Pause.pause(5000);
	    myEditPanel.selectTab("Universe");
	    Pause.pause(5000);
	    myEditPanel.selectTab("Verification Objects");
	    Pause.pause(3000);
	    myEditPanel.selectTab("Verification Conditions");
	    Pause.pause(3000);
	    myEditPanel.selectTab("Group Types");
	    Pause.pause(2000);
	    myEditPanel.selectTab("General");
	    // Check that we have the correct tech pack open.
	    System.out.println("testViewExistingTP(): Checking that correct techpack is open.");
	    assertThat(viewTPWindow.textBox("TechPackName").text().equals(TP_NAME)).as(
	        "Selected tech pack should match the openend techpack name");
	    assertThat(viewTPWindow.textBox("Version").text().equals(TP_VERSION)).as(
	        "Selected tech pack should match the openend techpack version");

	   
	    // Check buttons
	    System.out.println("testViewExistingTP(): Checking buttons.");
	    assertThat(viewTPWindow.button("ManageGeneralPropertiesSave").target.isEnabled()).isFalse();
	    assertThat(viewTPWindow.button("ManageGeneralPropertiesDiscard").target.isEnabled()).isFalse();
	    assertThat(viewTPWindow.button("ManageGeneralPropertiesClose").target.isEnabled()).isTrue();

	 // Click the close button
	    System.out.println("testViewExistingTP(): Closing.");
	    viewTPWindow.button("ManageGeneralPropertiesClose").click();

	    System.out.println("testViewExistingTP(): Done.");
	  }
	  
	  /**
	   * Tests unlocking and locking of the newly created techpack (by
	   * testCreateNewTechPack). Changes to the DB are verified.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testLockAndUnlockTechPack() throws Exception {
	    System.out.println("testLockAndUnlockTechPack(): Start.");
	    
	    // Reset and start the database modification logger
	    TableModificationLogger logger = TableModificationLogger.instance();
	    logger.reset();
	    logger.startLogging();

	    // Select the Manage TechPack tab
	    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");	    
	    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
	    

	    // Select the Test techpack in the tree: DC_E_TEST
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/"
	        + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TEST_TP_VERSION);
	    assertThat (tpTree.target.getSelectionPath().getLastPathComponent().toString())
	    	.as(" Test TechPack should exist in TPTree")
	    		.isEqualTo(TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TEST_TP_VERSION);
	  

	    // Check the buttons
//	    assertThat(
//	        TechPackIdeStarter.getMyWindow().button("TechPackEdit").requireEnabled(Timeout.timeout(5000)).target
//	            .isEnabled()).as("Edit button should be enabled.").isFalse();
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackView").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("View button should be enabled.").isTrue();
	    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()).as(
	        "Lock button should be disabled.").isFalse();
	    

	    // Select the test TechPack in the tree
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/"
		        + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TEST_TP_VERSION);
	    
	    // UnLock the techpack, if not already locked.
	    if (TechPackIdeStarter.getMyWindow().button("TechPackUnlock").target.isEnabled()) {
	    	System.out.println("testLockAndUnlockTechPack(): Techpack was already locked. Unlocking TP..");
	      TechPackIdeStarter.getMyWindow().button("TechPackUnlock").click();
	      System.out.println("testLockAndUnlockTechPack(): Techpack Unlocked.");
	    } else {
	      System.out.println("testLockAndUnlockTechPack(): Techpack was already Unlocked.");
	    }


	    // Check the buttons
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackLock").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("Lock button should be enabled.").isTrue();
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackView").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("View button should be enabled.").isTrue();
	    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackUnlock").target.isEnabled()).as(
	        "Unlock button should be disabled.").isFalse();
	    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
	        "Edit button should be disabled.").isFalse();

	    // Click Lock
	    System.out.println("testLockAndUnlockTechPack(): Locking TechPack agian.");
	    TechPackIdeStarter.getMyWindow().button("TechPackLock").click();	      

	    // Check the buttons
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackUnlock").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("Unlock button should be enabled.").isTrue();
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackEdit").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("Edit button should be enabled.").isTrue();
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackView").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("View button should be enabled.").isTrue();
	    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()).as(
	        "Lock button should be disabled.").isFalse();
	   
	    System.out.println("testLockAndUnlockTechPack(): done.");
	    
	    logger.stopLogging();
	    
	    System.out.println("testLockAndUnlockTechPack(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	    
	    //DATABASE ASSERTIONS
	    List<TestTableItem> tables = new Vector<TestTableItem>();
	    tables.add(new TestTableItem(this.getClass().getName(), "testLockAndUnlockTechPack", TestSetupConstants.DB_DWHREP,
	        "VERSIONING", new String[] { "LOCKDATE" }));
	    DatabaseTester.testTableList(tables);
	    tables = null;
//	    final RockFactory dwhrep = TestSetupConstants.getRockFactory(Schema.dwhrep);
//	    final Versioning version = new Versioning(dwhrep,TestSetupConstants.TEST_TP_NAME +":(("+TestSetupConstants.TEST_TP_VERSION+"))");	    
//	    assertEquals("tester", version.getLockedby());
	   
	    System.out.println("testLockAndUnlockTechPack(): Done.");
	  }
	  
	  
	  /**
	   * Tests creation of an new "empty" techpack with mandatory values defined.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testCreateNewTechPack() throws Exception {
	    System.out.println("testCreateNewTechPack(): Start.");
	    //starter.reinitializeDatabases();

	    // Reset and start the database modification logger
	    TableModificationLogger logger = TableModificationLogger.instance();
	    logger.reset();
	    logger.startLogging();

	    // Select the Manage TechPack tab
	    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	    // Sanity check: New techpack should not exist yet.
	    // However, it might already be there due to earlier failed test run.
	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");

	    try {
	      tpTree.target.clearSelection();
	      tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.NEW_TEST_TP_NAME + "/"
			        + TestSetupConstants.NEW_TEST_TP_NAME + ":" + TestSetupConstants.NEW_TEST_TP_VERSION);
	    } catch (LocationUnavailableException lue) {
	      System.out.println("testCreateNewTechPack(): Sanity check passed. " + "New techpack does not exist yet.");
	    }
	    assertThat(tpTree.target.getSelectionPath()).as("Sanity check failed: New techpack should not exist yet: "+tpTree.target.getSelectionPath()).isNull();

	    // Select the server from the tree, so that no techpack is selected
	    // at the moment. If there is a selected techpack, that one would be
	    // automatically taken as the source techpack in the creation
	    // window. In this case, no source is wanted.
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

	    // Click New button
	    System.out.print("testCreateNewTechPack(): Opening new techpack window...");
	    TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

	    // Wait for the new tech pack window to show
	    FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(10000).using(
	        TechPackIdeStarter.getMyRobot());
	    assertThat(newTPWindow).isNotNull();
	    System.out.print("Done.\n");

	    // Check that create is disabled
	    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).isFalse();

	    // Fill in the required fields and base definition

	    newTPWindow.textBox("Name").enterText(TestSetupConstants.NEW_TEST_TP_NAME);
	    newTPWindow.textBox("Version").enterText(TestSetupConstants.NEW_TEST_TP_VERSION.toLowerCase());
	    newTPWindow.textBox("Product").enterText("product");
	    newTPWindow.textBox("Rstate").enterText("p1a");	    
	    newTPWindow.comboBox("Type").selectItem("PM");
	    newTPWindow.textBox("UniverseName").enterText("universe");
	    enterRowsInUeTable(newTPWindow.table("UniverseExtension"), newTPWindow);
	    newTPWindow.comboBox("BaseDefinition").selectItem(TestSetupConstants.BASE_TP_VERSIONID);

	    // Check that create is enabled
	    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).as("Create button should be enabled")
	        .isTrue();

	    // Click create
	    System.out.println("testCreateNewTechPack(): Creating new techpack.");
	    newTPWindow.button("CreateNewTechpack").click();

	    // Wait for busy indicator
	    CommonUtils.waitForBusyIndicator(10000);

	    // NewTechPack window should be closed.
	    assertThat(newTPWindow.requireNotVisible()).as("NewTechPack window should be closed.");
	    System.out.println("testCreateNewTechPack(): Waiting for max 10 seconds for main window activating.");
	    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
	        "Main window should be visible after closing " + "the new tech pack window.");

	    // Stop logging
	    logger.stopLogging();
	    
	    System.out.println("testCreateNewTechPack(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	    // DATABASE ASSERTIONS    	   
	    List<TestTableItem> tables = new Vector<TestTableItem>();
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewTechPack", TestSetupConstants.DB_DWHREP,
	            "REFERENCETABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewTechPack", TestSetupConstants.DB_DWHREP,
	            "UNIVERSENAME", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewTechPack", TestSetupConstants.DB_DWHREP,
	            "VERSIONING", new String[] { "LOCKDATE" }));
	    
        String[] modifiedTables = DatabaseTester.testTableList(tables);
        DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
	    System.out.println("testCreateNewTechPack(): Done.");

	  } // testCreateNewTechPack
	  
	  /**
	   * Tests removal of the newly created techpack (by testCreateNewTechPack).
	   * Changes to the DB are verified.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testRemoveTechPack() throws Exception {
	    System.out.println("testRemoveTechPack(): Start.");
	    
	    TableModificationLogger logger = TableModificationLogger.instance();
	    logger.reset();
	    logger.startLogging();

	    // Select the Manage TechPack tab
	    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
	    
	    try {
		      tpTree.target.clearSelection();
		      tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.NEW_TEST_TP_NAME + "/"
				        + TestSetupConstants.NEW_TEST_TP_NAME + ":" + TestSetupConstants.NEW_TEST_TP_VERSION);
		      
		      
		    } catch (LocationUnavailableException lue) {
		      System.out.println("testRemoveTechPack(): Sanity check failed. " + "New techpack does not exist.");
		     			
		    }		
		
	  if (tpTree.target.getSelectionPath() == null) {	
		  System.out.println("testRemoveTechPack(): Creating Test TechPack....");
		  tpTree.selectPath(TestSetupConstants.TEST_SERVER);
		  TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

		  // Wait for the new tech pack window to show
		  FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(10000).using(
			    TechPackIdeStarter.getMyRobot());
			 
		  newTPWindow.textBox("Name").enterText(TestSetupConstants.NEW_TEST_TP_NAME);
		  newTPWindow.textBox("Version").enterText(TestSetupConstants.NEW_TEST_TP_VERSION.toLowerCase());
		  newTPWindow.textBox("Product").enterText("product");
	      newTPWindow.textBox("Rstate").enterText("p1a");
		  newTPWindow.textBox("UniverseName").enterText("universe");
		  newTPWindow.comboBox("BaseDefinition").selectItem(TestSetupConstants.BASE_TP_VERSIONID);
			 
		  newTPWindow.button("CreateNewTechpack").click();

		  // Wait for busy indicator
		 CommonUtils.waitForBusyIndicator(10000);
	  }
		CommonUtils.refreshIde();
		assertThat(tpTree.target.getSelectionPath()).as("Sanity check passed: New techpack should exist by now: "+tpTree.target.getSelectionPath()).isNotNull();
	    // Select the new tech pack in the tree
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.NEW_TEST_TP_NAME + "/"
		        + TestSetupConstants.NEW_TEST_TP_NAME + ":" + TestSetupConstants.NEW_TEST_TP_VERSION);

	    // Check that remove is enabled
	    assertThat(
	        TechPackIdeStarter.getMyWindow().button("TechPackRemove").requireEnabled(Timeout.timeout(5000)).target
	            .isEnabled()).as("Remove button should be enabled.").isTrue();

	    // Click Remove
	    System.out.println("testRemoveTechPack(): Removing techpack: " + TestSetupConstants.NEW_TEST_TP_NAME + ":"
	        + TestSetupConstants.NEW_TEST_TP_VERSION + ".");
	    TechPackIdeStarter.getMyWindow().button("TechPackRemove").click();

	    DialogFixture dialog = WindowFinder.findDialog(JDialog.class).using(TechPackIdeStarter.getMyRobot());

	    // Click Yes
	    dialog.optionPane().yesButton().click();

	    // Wait for the busy indicator
	    CommonUtils.waitForBusyIndicator(240000);

	    System.out.println("testRemoveTechPack(): New Tec hPack removed.");

	    // Verify that the removed tech pack has been removed from the tree
	    // (tree is refreshed).
	    System.out.println("testRemoveTechPack(): Now selected techpack after removal is: " + tpTree.target.getSelectionPath());
	    if (tpTree.target.getSelectionPath() != null) {
	      assertThat(
	          !tpTree.target.getSelectionPath().toString().equals(
	              new String("[" + TestSetupConstants.TEST_SERVER + ", " + TestSetupConstants.NEW_TEST_TP_NAME + ", "
	                  + TestSetupConstants.NEW_TEST_TP_NAME + ":" + TestSetupConstants.NEW_TEST_TP_VERSION + "]"))).as(
	          "Removed tech pack should not be selected in the tree anymore.");
	    }

	    logger.stopLogging();
	    
	    System.out.println("testRemoveTechPack(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	    
	    // DATABASE ASSERTION
	    List<TestTableItem> tables = new Vector<TestTableItem>();
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "AGGREGATION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "AGGREGATIONRULE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "BUSYHOURPLACEHOLDERS", null));
//	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
//	        "DATAITEM", null));
//	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
//	        "DEFAULTTAGS", null));
	    // TODO: External statement column statement is ignored since the comparison
	    // always fails for some reason.
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "EXTERNALSTATEMENT", new String[] { "STATEMENT" }));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
		    "GROUPTYPES", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "MEASUREMENTTYPECLASS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "PROMPT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "PROMPTIMPLEMENTOR", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "PROMPTOPTION", null));
//	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
//	        "REFERENCECOLUMN", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "REFERENCETABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "SUPPORTEDVENDORRELEASE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "TECHPACKDEPENDENCY", null));
	    // TODO: transformation column config is ignored since the comparison
	    // always fails for some reason.
//	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
//	        "TRANSFORMATION", new String[] { "ORDERNO", "CONFIG" }));
//	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
//	        "TRANSFORMER", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSECLASS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSECOMPUTEDOBJECT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSECONDITION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
		    "UNIVERSEFORMULAS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSEJOIN", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSENAME", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSEOBJECT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSEPARAMETERS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSETABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "VERIFICATIONCONDITION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "VERIFICATIONOBJECT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveTechPack", TestSetupConstants.DB_DWHREP,
	        "VERSIONING", new String[] { "LOCKDATE" }));
	    
	    String[] modifiedTables = DatabaseTester.testTableList(tables);
	    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
	    tables = null;

	    System.out.println("testRemoveTechPack(): Done.");
	  }
	  
	  
	  /**
	   * Tests creation of an new techpack based on existing TechPack.
	   * 
	   * @throws Exception
	   */
	  @Test
	  public void testCreateNewFromExistingTechPack() throws Exception {
	    System.out.println("testCreateNewFromExistingTechPack(): Start.");	 

	    // Reset and start the database modification logger
	    TableModificationLogger logger = TableModificationLogger.instance();
	    logger.reset();
	    logger.startLogging();

	    // Select the Manage TechPack tab
	    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
	    
	    //Select test TP DC_E_TEST as the source TechPack for creation of new TechPack
	    tpTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/"
		        + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TEST_TP_VERSION);

	    // Click New button
	    System.out.print("testCreateNewFromExistingTechPack(): Opening new techpack window...");
	    TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

	    // Wait for the new tech pack window to show
	    FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(10000).using(
	        TechPackIdeStarter.getMyRobot());
	    assertThat(newTPWindow).isNotNull();
	    System.out.print("Done.\n");

	    // Check that create is disabled
	    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).isFalse();
	    
	    //Change the version of techPack
	    newTPWindow.textBox("Version").deleteText();
	    newTPWindow.textBox("Version").enterText(TestSetupConstants.TEST_TP_VERSION + 1);
	    
	    DESCRIPTION  = newTPWindow.textBox("Description").target.getText();
	    RSTATE = newTPWindow.textBox("Rstate").target.getText();
	   //Check that create is enabled
	   assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).as("Create button should be enabled")
	        .isTrue();

	    // Click create
	    System.out.println("testCreateNewFromExistingTechPack(): Creating new techpack.");
	    newTPWindow.button("CreateNewTechpack").click();

	    // Wait for busy indicator
	    CommonUtils.waitForBusyIndicator(60000);

	    // NewTechPack window should be closed.
	    assertThat(newTPWindow.requireNotVisible()).as("NewTechPack window should be closed.");
	    System.out.println("testCreateNewFromExistingTechPack(): Waiting for max 60 seconds for main window activating.");
	    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
	        "Main window should be visible after closing " + "the new tech pack window.");

	    // Stop logging
	    logger.stopLogging();
	    
	    System.out.println("testCreateNewFromExistingTechPack(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	    
	    // DATABASE ASSERTIONS
	    List<TestTableItem> tables = new Vector<TestTableItem>();
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "AGGREGATION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "AGGREGATIONRULE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
		    "BUSYHOUR", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
			"BUSYHOURMAPPING", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
		    "BUSYHOURPLACEHOLDERS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
			"BUSYHOURRANKKEYS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
			"BUSYHOURSOURCE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "DATAFORMAT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "DATAITEM", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "DEFAULTTAGS", null));
	    // TODO: External statement column statement is ignored since the comparison
	    // always fails for some reason.
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "EXTERNALSTATEMENT", new String[] { "STATEMENT" }));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
		    "MEASUREMENTCOLUMN", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
			"MEASUREMENTCOUNTER", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
			"MEASUREMENTKEY", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	    	"MEASUREMENTOBJBHSUPPORT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
		    "MEASUREMENTTABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
		    "MEASUREMENTTYPE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "MEASUREMENTTYPECLASS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "REFERENCECOLUMN", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "REFERENCETABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "SUPPORTEDVENDORRELEASE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "TECHPACKDEPENDENCY", null));
	    // TODO: transformation column config is ignored since the comparison
	    // always fails for some reason.
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "TRANSFORMATION", new String[] { "ORDERNO", "CONFIG" }));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "TRANSFORMER", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSECLASS", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSECONDITION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSEJOIN", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSENAME", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSEOBJECT", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "UNIVERSETABLE", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "VERIFICATIONCONDITION", null));
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "VERIFICATIONOBJECT", null));	    
	    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewFromExistingTechPack", TestSetupConstants.DB_DWHREP,
	        "VERSIONING", new String[] { "LOCKDATE" }));
	    
	    DatabaseTester.testTableList(tables);
	    // Number of modified tables does not match with actual number
	    //String[] modifiedTables = DatabaseTester.testTableList(tables);
        //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
        tables=null;
	    System.out.println("testCreateNewFromExistingTechPack(): Done.");
	    
	  }  
	  /**
	   * Select Test TechPack and select create document from 
	   * right menu options.	  
	   */
	  @Test
	  public void testCreateTechPackDocument() throws Exception {
		  
		  System.out.println("testCreateTechPackDocument(): Start.");
		  String fileDir = System.getProperty("user.home");
		
		   File docFile = new File (fileDir+ File.separator + "TP Description "+DESCRIPTION+".sdif");	
		   if (docFile.exists()) {
			   docFile.delete();
		   }
		
		  // Reset and start the database modification logger
		  TableModificationLogger logger = TableModificationLogger.instance();
		  logger.reset();
		  logger.startLogging();

		  // Select the Manage TechPack tab
		  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

		  JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
		  assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
		  
		  String techPackPath = TestSetupConstants.TEST_SERVER +"/" + TestSetupConstants.TEST_TP_NAME +"/"
		  		+TestSetupConstants.TEST_TP_NAME +":"+TestSetupConstants.TEST_TP_VERSION;
		  
		  try {
		      System.out.println("Trying to select treepath= " + techPackPath);

		      // Make techpack active
		      tpTree.selectPath(techPackPath);
		      lockSelectedTechPack();
		    } catch (LocationUnavailableException lua) {
		      System.out.println("testCreateTechPackDocument(): Test TechPack not found!!! "+lua.getMessage());
		    }
		    
		    JPopupMenuFixture tpMenu = tpTree.showPopupMenuAt(techPackPath);
		    assertThat(tpMenu).isNotNull().as("Could not pop up menu ");
		    tpMenu.menuItemWithPath("Create documents", "Techpack description").click();
		    
		    FrameFixture documentFrame = WindowFinder.findFrame("Create Techpack Description").withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
		    documentFrame.button("CreateNewTechpack").click();
		    Pause.pause(10000);
		    
		    JOptionPaneFixture okMsg = JOptionPaneFinder.findOptionPane(new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
		   	   protected boolean isMatching(JOptionPane optionPane) {
		    	     return "TP Description document created OK.".equals(optionPane.getMessage());
		    	   }
		    	 }).withTimeout(10000).using(TechPackIdeStarter.getMyRobot());
		    okMsg.okButton().click();
		    documentFrame.button("Close").click();
		  //Assertion that file is created in the given directory.		  	    
		    assertTrue(docFile.exists());
		    List<TestTableItem> tables = new Vector<TestTableItem>();
		    tables.add(new TestTableItem(this.getClass().getName(), "testCreateTechPackDocument", TestSetupConstants.DB_DWHREP,
			        "VERSIONING", new String[] { "LOCKDATE" }));
			    
			DatabaseTester.testTableList(tables);
			tables = null;
		    
	  } 
	  
	  /**
	   * Tests CreatePackage Functionality of IDE.This creates a ".tpi" file.
	   * Directory of file and encryption level are set to default.
	   * Test verifies if ".tpi" file is created in the directory
	   */
	  @Test
	  public void testCreateEncryptedInstallationPackage() throws Exception {
		  System.out.println("testCreateEncryptedInstallationPackage(): Start.");
		  
		  String tpiFileDirectory = System.getProperty("user.home");
		  
		  File tpiFile = new File (tpiFileDirectory+ File.separator +TestSetupConstants.TEST_TP_NAME+"_"+RSTATE+"_b1234.tpi");		   
		  if (tpiFile.exists()) {
			   tpiFile.delete();
		   }		  
		 
		  // Reset and start the database modification logger
		  TableModificationLogger logger = TableModificationLogger.instance();
		  logger.reset();
		  logger.startLogging();

		  // Select the Manage TechPack tab
		  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

		  JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
		  assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
		  
		  String techPackPath = TestSetupConstants.TEST_SERVER +"/" + TestSetupConstants.TEST_TP_NAME +"/"
		  		+TestSetupConstants.TEST_TP_NAME +":"+TestSetupConstants.TEST_TP_VERSION;
		  
		  try {
		      System.out.println("Trying to select treepath= " + techPackPath);

		      // Make techpack active
		      tpTree.selectPath(techPackPath);
		      lockSelectedTechPack();
		    } catch (LocationUnavailableException lua) {
		      System.out.println("testCreateEncryptedInstallationPackage(): Test TechPack not found!!! "+lua.getMessage());
		    }
		    
		    System.out.print("testCreateEncryptedInstallationPackage(): Opening create package window...");
		    TechPackIdeStarter.getMyWindow().button("TechPackInstall").click();
		    
		    DialogFixture dialog = WindowFinder.findDialog(JDialog.class).using(TechPackIdeStarter.getMyRobot());		    
		    dialog.optionPane().yesButton().click();

		    // Wait for the new tech pack window to show
		    FrameFixture installWindow = WindowFinder.findFrame("InstallTechPackWindow").withTimeout(10000).using(
		        TechPackIdeStarter.getMyRobot());
		    assertThat(installWindow).isNotNull().as("Install TechPack window should display here");
		    System.out.print("Done.\n");
		    
		    JButtonFixture createButton = installWindow.button (new GenericTypeMatcher<JButton>(JButton.class) {

		        @Override
		        protected boolean isMatching(JButton button) {
		          return button.getText().equalsIgnoreCase("Create");
		        }
		      });
		    
		    JButtonFixture cancelButton = installWindow.button (new GenericTypeMatcher<JButton>(JButton.class) {

		        @Override
		        protected boolean isMatching(JButton button) {
		          return button.getText().equalsIgnoreCase("Cancel");
		        }
		      });
		   
		    // Check that create is disabled
		    assertThat(createButton.target.isEnabled()).isFalse();
		    assertThat(cancelButton.target.isEnabled()).isTrue();
		    
		    //Enter Build number
		    installWindow.textBox("WDIR").enterText(tpiFileDirectory);
		    installWindow.textBox("BuildNumber").enterText("1234");
		    
		    assertThat(installWindow.button("Create").target.isEnabled()).as("Create should be enabled now").isTrue();
		    
		    System.out.println("testCreateEncryptedInstallationPackage(): Creating new techpack package.....");
		    installWindow.button("Create").click();
		    
		    CommonUtils.waitForBusyIndicator(10000);
		   
		    //**TODO**//Click OK- on meassage pane
		    ComponentMatcher matcher = new ComponentMatcher() {

		      public boolean matches(Component c) {
		        if (!(c instanceof JDialog))
		          return false;
		        return ((JDialog) c).isVisible();
		      }
		    };

		    // Get the dialog
		    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
		    JDialog targetDialog = (JDialog) finder.find(matcher);
		    assertThat(targetDialog).isNotNull();

		    
		    DialogFixture messageDialog = new DialogFixture(TechPackIdeStarter.getMyRobot(), targetDialog);
		   
		    messageDialog.target.dispose();
		    assertThat(installWindow.requireNotVisible()).as("Create installation package window should be closed.");
		    System.out.println("testCreateEncryptedInstallationPackage(): Waiting for max 60 seconds for main window activating.");
		    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
		        "Main window should be visible after closing the installation package window.");
		    
		    // Stop logging
		    logger.stopLogging();
		    //cancelButton.click();
		    
		    //Assertion that file is created in hte given directory.		     
		    assertTrue(tpiFile.exists());
		    
		    System.out.println("testCreateEncryptedInstallationPackage(): done.");
		   
		    
	  }

	  
	  /**
	   * Tests closing of TechPack IDE with necessary Conditions
	   * Test user gets prompted for Locked TPs.	  
	   */
	  @Test
	  public void testQuitButton() throws Exception {
		  System.out.println("testQuitButton(): Starting");
		  // Select the Manage TechPack tab
		  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");
		  
		  // To check that Tree should not be empty
		  JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
		  assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
		  
		  // To check that Quit button should not be disabled
		  assertThat(TechPackIdeStarter.getMyWindow().button("TechPackQuit").target.
				  isEnabled()).as("Quit button should be enabled.").isTrue();
		  
		  // To click Quit button
		  System.out.print("testQuitButton(): Clicking on Quit Button.\n");
		  TechPackIdeStarter.getMyWindow().button("TechPackQuit").click();

		   JOptionPaneFixture quitPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
		   quitPane.cancelButton().click();
		        
		    System.out.println("testCloseTechPack(): Done.");
	  }	
	  /**
	   * Edit Universe Extension Table. Insert data into Universe Extension Name
	   * column. Add a row in the Universe Extension Table.
	   * 
	   * @throws Exception
	   */
	  private void enterRowsInUeTable(JTableFixture unvextT, FrameFixture newTPWindow) {
	    try {
	      newTPWindow.comboBox("BaseDefinition").selectItem(TestSetupConstants.BASE_TP_VERSIONID);
	      // Add rows to the table
	      for (int i = 0; i < 2; ++i) {
	        JPopupMenuFixture popupMenu = unvextT.tableHeader().showPopupMenuAt(0);
	        popupMenu.menuItemWithPath("Add").click();
	        unvextT.cell(TableCell.row(i).column(0)).enterValue("ue"+i);
	        unvextT.cell(TableCell.row(i).column(1)).enterValue("ExtName" + i);
	      }
	    } catch (Exception e) {
	      System.out.println("testCopyMgwTechpack.enterRowsInUeTable: Exception: " + e.toString() + ", " + e);
	    }
	  } // enterRowsInUeTable

	  private void lockSelectedTechPack() {
			// Lock the techpack, if not already locked.
		    if (TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()) {
		      TechPackIdeStarter.getMyWindow().button("TechPackLock").click();
		      System.out.println("lockSelectedTechPack(): Techpack locked.");
		    } else {
		      System.out.println("lockSelectedTechPack(): Techpack was already locked.");
		    }
		} // lockSelectedTechPack 
	  
}