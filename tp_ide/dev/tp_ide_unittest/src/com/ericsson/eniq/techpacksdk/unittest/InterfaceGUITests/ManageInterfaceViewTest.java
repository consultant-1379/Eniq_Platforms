/*
 * Copyright (c) Ericsson Mobile Data Design AB 2009
 *
 * The copyright to the computer program(s) herein is the
 * property of Ericsson mobile Data Design AB, Sweden. The program(s)
 * may be used and/or copied with the written permission from
 * Ericsson mobile Data Design AB or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *
 */
/**
 * This test class contains unit test cases for the TechPackIDE: Manage Interface
 * View.
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.InterfaceGUITests;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Vector;

import javax.swing.JButton;

import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
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
import com.ericsson.eniq.techpacksdk.unittest.utils.MeasurementTypesUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.SetsActionsSchedulingsUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

public class ManageInterfaceViewTest {

  public static TechPackIdeStarter starter = null;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ManageInterfaceViewTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: ManageInterfaceViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    // Get the TechPackIDE starter and start the IDE.
    starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
   
    CommonUtils.refreshIde();

    // If table change logging is enabled, start the logger.
    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

  } // end setUpBeforeClass

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    // If table change logging is enabled, stop the logger.
    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    System.out.println("tearDownAfterClass(): Done.");
  } // end tearDownAfterClass

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	  System.out.println("setUp(): Executing Test Case");
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
 
    // Reset logger
    CommonUtils.resetTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

  } // setUp

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
	  
    // Print the changed database tables in the last test
    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  } // tearDown

  /**
   * test for testing that the interface tree is available.
   * 
   * @throws Exception
   */
  @Test
  public void testTreeData() throws Exception {
    System.out.println("testTreeData(): Start.");

    // Select the Manage Interface tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");
    assertThat(tpTree).isNotNull().as("InterfaceTree should not be null");

    System.out.println("testTreeData(): Done.");
  } // testTreeData

  /**
   * Tests creation of an new "empty" interface with mandatory values defined.
   * 
   * @throws Exception
   */
  @Test
  public void testCreateNewInterface() throws Exception {
    System.out.println("testCreateNewInterface(): Start.");
    // Reset and start the database modification logger
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    // Select the Manage Interface tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");

    // Sanity check: New Interface, should not exist yet.
    // However, it might already be there due to earlier failed test run.
    JTreeFixture interfaceTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");
    assertThat(interfaceTree).isNotNull().as("InterfaceTree should not be null");

    try {
      interfaceTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.INTERFACE_NAME + "/" + TestSetupConstants.INTERFACE_NAME + ":"
          + TestSetupConstants.INTERFACE_VERSION);
    } catch (LocationUnavailableException lue) {
      System.out.println("testCreateNewInterface(): Sanity check passed. "
          + "New interface does not already exist yet.");
    }
//    assertThat(interfaceTree.target.getSelectionPath()).as("Sanity check failed: New interface should not exist yet!")
//        .isNull();

    // Select the server from the tree, so that no techpack is selected
    // at the moment. If there is a selected techpack, that one would be
    // automatically taken as the source techpack in the creation
    // window. In this case, no source is wanted.
    interfaceTree.selectPath(TestSetupConstants.TEST_SERVER);

    // Click New button
    System.out.print("testCreateNewInterface(): Opening new interface window...");
    TechPackIdeStarter.getMyWindow().button("InterfaceNew").click();

    // Wait for the new window to show
    FrameFixture newWindow = WindowFinder.findFrame("CreateNewInterface").withTimeout(10000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(newWindow).isNotNull();  
    
    // Check that cancel is enabled
    assertThat(newWindow.button("CancelCreateNewInterface").target.isEnabled()).isTrue();
//    // Click cancel
//    System.out.println("testCreateNewInterface(): Pressing the Cancel button for a new interface.");
//    newWindow.button("CancelCreateNewInterface").click();
//    // Wait for busy indicator
//    CommonUtils.waitForBusyIndicator(10000);
//
//    // Check that create is disabled
    assertThat(newWindow.button("CreateNewInterface").target.isEnabled()).isFalse();

    // Fill in the required fields and base definition
    newWindow.textBox("Name").enterText(TestSetupConstants.INTERFACE_NAME);
    newWindow.textBox("Rstate").enterText(TestSetupConstants.TEST_R_STATE);
    newWindow.textBox("Version").enterText(TestSetupConstants.INTERFACE_VERSION);

    // Check that create is enabled
    assertThat(newWindow.button("CreateNewInterface").target.isEnabled()).as("Create button should be enabled")
        .isTrue();

    // Click create
    System.out.println("testCreateNewInterface(): Creating new interface.");
    newWindow.button("CreateNewInterface").click();

    // Wait for busy indicator
    CommonUtils.waitForBusyIndicator(10000);

    // NewInterface window should be closed.
    assertThat(newWindow.requireNotVisible()).as("CreateNewInterface window should be closed.");
    System.out.println("testCreateNewInterface(): Waiting for max 10 seconds for main window activating.");
    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
        "Main window should be visible after closing " + "the new interface window.");

    // Stop logging
    logger.stopLogging();
    // DATABASE ASSERTION
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewInterface", TestSetupConstants.DB_DWHREP,
        "DATAINTERFACE", new String[] { "LOCKEDBY", "LOCKDATE" }));
    
    String[] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    System.out.println("testCreateNewInterface(): Done.");
  } // testCreateNewInterface
  
  
  /**
   * Tests unlocking and locking of the newly created techpack (by
   * testCreateNewTechPack). Changes to the DB are verified.
   * 
   * @throws Exception
   */
  @Test
  public void testLockAndUnlockInterface() throws Exception {
    System.out.println("testLockAndUnlockInterface(): Start.");
    
    // Reset and start the database modification logger
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");

    JTreeFixture interfaceTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");	    
    assertThat(interfaceTree).isNotNull().as("InterfaceTree should not be null");
    

    // Select the interface in the tree
    try {
    	interfaceTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.INTERFACE_NAME + "/" + TestSetupConstants.INTERFACE_NAME + ":"
                + TestSetupConstants.INTERFACE_VERSION);
    }catch (LocationUnavailableException lue) {
        System.out.println("testLockAndUnlockInterface(): New interface does not already exist yet.");
    }    
    assertThat (interfaceTree.target.getSelectionPath().getLastPathComponent().toString())
    	.as(" Interface should exist in Interface Tree")
    		.isEqualTo(TestSetupConstants.INTERFACE_NAME + ":" + TestSetupConstants.INTERFACE_VERSION);
     
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceView").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("View button should be enabled.").isTrue();
    assertThat(TechPackIdeStarter.getMyWindow().button("InterfaceLock").target.isEnabled()).as(
        "Lock button should be disabled.").isFalse();
    

    // Select the interface in the tree
    interfaceTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.INTERFACE_NAME + "/" + TestSetupConstants.INTERFACE_NAME + ":"
            + TestSetupConstants.INTERFACE_VERSION);
    
    // UnLock the techpack, if not already locked.
    if (TechPackIdeStarter.getMyWindow().button("InterfaceUnlock").target.isEnabled()) {
    	System.out.println("testLockAndUnlockInterface(): Interface was already locked. Unlocking it..");
      TechPackIdeStarter.getMyWindow().button("InterfaceUnlock").click();
      System.out.println("testLockAndUnlockInterface(): Interface Unlocked.");
    } else {
      System.out.println("testLockAndUnlockInterface(): Techpack was already Unlocked.");
    }


    // Check the buttons
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceLock").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("Lock button should be enabled.").isTrue();
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceView").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("View button should be enabled.").isTrue();
    assertThat(TechPackIdeStarter.getMyWindow().button("InterfaceUnlock").target.isEnabled()).as(
        "Unlock button should be disabled.").isFalse();
    assertThat(TechPackIdeStarter.getMyWindow().button("InterfaceEdit").target.isEnabled()).as(
        "Edit button should be disabled.").isFalse();

    // Click Lock
    System.out.println("testLockAndUnlockInterface(): Locking interface agian.");
    TechPackIdeStarter.getMyWindow().button("InterfaceLock").click();	      

    // Check the buttons
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceUnlock").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("Unlock button should be enabled.").isTrue();
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceEdit").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("Edit button should be enabled.").isTrue();
    assertThat(
        TechPackIdeStarter.getMyWindow().button("InterfaceView").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("View button should be enabled.").isTrue();
    assertThat(TechPackIdeStarter.getMyWindow().button("InterfaceLock").target.isEnabled()).as(
        "Lock button should be disabled.").isFalse();
   
    System.out.println("testLockAndUnlockInterface(): done.");
    
    logger.stopLogging();
    
    System.out.println("testLockAndUnlockInterface(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
    
    //DATABASE ASSERTIONS
    List<TestTableItem> tables = new Vector<TestTableItem>();
//    tables.add(new TestTableItem(this.getClass().getName(), "testLockAndUnlockInterface", TestSetupConstants.DB_DWHREP,
//        "VERSIONING", new String[] { "LOCKDATE" }));
    tables.add(new TestTableItem(this.getClass().getName(), "testLockAndUnlockInterface", TestSetupConstants.DB_DWHREP,
            "DATAINTERFACE", new String[] { "LOCKEDBY", "LOCKDATE" }));
    //DatabaseTester.testTableList(tables);
    String[] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;
   
    System.out.println("testLockAndUnlockInterface(): Done.");
  }
  
  /**
   * Edit general properties of an existing interface.
   * 
   * @throws Exception
   */
  @Test
  public void testEditGeneralInterface() throws Exception {
    System.out.println("testEditGeneralInterface(): Start.");
    // Reset and start the database modification logger
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    // Select the Manage Interface tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");
    
    JTreeFixture interfaceTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");
    assertThat(interfaceTree).isNotNull().as("InterfaceTree should not be null");

    
    try {
        interfaceTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.INTERFACE_NAME + "/" + TestSetupConstants.INTERFACE_NAME + ":"
            + TestSetupConstants.INTERFACE_VERSION);
      } catch (LocationUnavailableException lue) {
        System.out.println("testCreateNewInterface(): Sanity check passed. "
            + "New interface does not already exist yet.");
      }
      assertThat (interfaceTree.target.getSelectionPath().getLastPathComponent().toString()).as(" Interface should exist in Interface Tree")
  		.isEqualTo(TestSetupConstants.INTERFACE_NAME + ":" + TestSetupConstants.INTERFACE_VERSION);     
      assertThat(TechPackIdeStarter.getMyWindow().button("InterfaceEdit").target.isEnabled()).as(
      "Edit button should be enabled.").isTrue();
      
      System.out.println("testEditGeneralInterface(): open edit window.");
      TechPackIdeStarter.getMyWindow().button("InterfaceEdit").click();
      
      final FrameFixture editFrame = WindowFinder.findFrame("EditInterfaceWindow").withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
      final JTabbedPaneFixture interfaceTabs = editFrame.tabbedPane();
      assertThat(interfaceTabs).isNotNull().as("The panel should be visible.");
      interfaceTabs.selectTab("General");
      
      JButtonFixture saveButton = editFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {

          @Override
          protected boolean isMatching(JButton button) {
            return (button.getText().equalsIgnoreCase("save")&& button.getName()==null);
          }
        });
      JButtonFixture cancelButton = editFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {

          @Override
          protected boolean isMatching(JButton button) {
            return (button.getText().equalsIgnoreCase("cancel")&& button.getName()==null);
          }
        });
      JButtonFixture closeButton = editFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {

          @Override
          protected boolean isMatching(JButton button) {
            return (button.getText().equalsIgnoreCase("close")&& button.getName()==null);
          }
        });
      editFrame.maximize();
      assertThat(saveButton.target.isEnabled()).isFalse();
      assertThat(cancelButton.target.isEnabled()).isFalse();
      assertThat(closeButton.target.isEnabled()).isTrue();
      JTableFixture tpTable = editFrame.table("TechPack");
      JPopupMenuFixture popupMenu = tpTable.tableHeader().showPopupMenuAt(0);      
      assertThat(popupMenu).isNotNull();
      popupMenu.menuItemWithPath("Add Empty TP").click();
      
//      DialogFixture tpDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "TechPacks");
//      assertThat(tpDialog).isNotNull();
//      
//      tpDialog.comboBox().selectItem(TestSetupConstants.TEST_TP_NAME+":R1A (("+TestSetupConstants.TEST_TP_VERSION+"))");
//      tpDialog.optionPane().okButton().click();
      tpTable.cell(TableCell.row(0).column(0)).enterValue(TestSetupConstants.TEST_TP_NAME);
      tpTable.cell(TableCell.row(0).column(1)).enterValue(TestSetupConstants.TEST_R_STATE);
      
      JTableFixture tpDependencyTable = editFrame.table("tpdependency");
      JPopupMenuFixture popup = tpDependencyTable.tableHeader().showPopupMenuAt(0);      
      assertThat(popup).isNotNull();
      popup.menuItemWithPath("Add Empty TP").click();
      tpDependencyTable.cell(TableCell.row(0).column(0)).enterValue(TestSetupConstants.TEST_TP_DEPENDENCY);
      tpDependencyTable.cell(TableCell.row(0).column(1)).enterValue(TestSetupConstants.TEST_R_STATE);
      
      // this to enable save button
      tpDependencyTable.cell(TableCell.row(0).column(0)).click();
      
      assertThat(saveButton.target.isEnabled()).isTrue();
      assertThat(cancelButton.target.isEnabled()).isTrue();
      assertThat(closeButton.target.isEnabled()).isTrue();
      
      saveButton.click();
      CommonUtils.waitForBusyIndicator(60000);
      
      logger.stopLogging();
      
      System.out.println("testEditGeneralInterface(): Tables modified by this test are: " 
    		  + CommonUtils.listModifiedTables() + ".");
      
      //DATABASE ASSERTIONS
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testEditGeneralInterface", TestSetupConstants.DB_DWHREP,
            "DATAINTERFACE", new String[] { "LOCKEDBY", "LOCKDATE" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditGeneralInterface", TestSetupConstants.DB_DWHREP,
	         "INTERFACEDEPENDENCY", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditGeneralInterface", TestSetupConstants.DB_DWHREP,
	          "INTERFACETECHPACKS", null));
      //DatabaseTester.testTableList(tables);
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
     
      System.out.println("testEditGeneralInterface(): Done.");

  }
  
  /**
   * Edit sets/actions of an existing interface.
   * 
   * @throws Exception
   */
  @Test
  public void testCreateSetsOfInterface() throws Exception {
    System.out.println("testCreateSetsOfInterface(): Start.");
    // Reset and start the database modification logger
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    // Select the Manage Interface tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage Interface");
    
    JTreeFixture interfaceTree = TechPackIdeStarter.getMyWindow().tree("InterfaceTree");
    assertThat(interfaceTree).isNotNull().as("InterfaceTree should not be null");

    
    try {
        interfaceTree.selectPath(TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.INTERFACE_NAME + "/" + TestSetupConstants.INTERFACE_NAME + ":"
            + TestSetupConstants.INTERFACE_VERSION);
      } catch (LocationUnavailableException lue) {
        System.out.println("testCreateSetsOfInterface(): Sanity check passed. "
            + "New interface does not already exist yet.");
      }
      assertThat (interfaceTree.target.getSelectionPath().getLastPathComponent().toString()).as(" Interface should exist in Interface Tree")
  		.isEqualTo(TestSetupConstants.INTERFACE_NAME + ":" + TestSetupConstants.INTERFACE_VERSION);     
       
      final FrameFixture editFrame = WindowFinder.findFrame("EditInterfaceWindow").withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
      final JTabbedPaneFixture interfaceTabs = editFrame.tabbedPane();
      assertThat(interfaceTabs).isNotNull().as("The panel should be visible.");
      interfaceTabs.selectTab("Sets/Actions/Schedulings");      
     
      assertThat(editFrame.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
      assertThat(editFrame.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
      assertThat(editFrame.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue(); 
      assertThat(editFrame.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
      editFrame.button("ETLSetHandlingViewWizard").click();
      
      FrameFixture fFix = new FrameFixture(TechPackIdeStarter.getMyRobot(), "ETLSetHandlingViewInterfaceSetWizardFrame");
      fFix.button("InterfaceSetWizardViewCreate").click();
      CommonUtils.waitForBusyIndicator(90000);      
      logger.stopLogging();
      
      System.out.println("testCreateSetsOfInterface(): Tables modified by this test are: " 
    		  + CommonUtils.listModifiedTables() + ".");
      
      //DATABASE ASSERTIONS
      List<TestTableItem> tables = new Vector<TestTableItem>();      
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSetsOfInterface", TestSetupConstants.DB_ETLREP,
	        "META_COLLECTION_SETS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSetsOfInterface", TestSetupConstants.DB_ETLREP,
	        "META_COLLECTIONS", null));      
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSetsOfInterface",TestSetupConstants.DB_ETLREP, 
    		"META_TRANSFER_ACTIONS", new String[] { "ACTION_CONTENTS_01", "WHERE_CLAUSE_01" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSetsOfInterface", TestSetupConstants.DB_ETLREP,
  	        "META_SCHEDULINGS", null)); 
      
      //DatabaseTester.testTableList(tables);
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
     
      System.out.println("testCreateSetsOfInterface(): Done.");

  }
  
  /**
   * Disabling the first set test
   * 
   * - Open the first existing set - Select it's parameter row - Check the
   * "Disable" -checkbox - Click save-button.
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testDisableTheFirstInterfaceSetAndSave() throws Exception {
    System.out.println("testDisableTheFirstInterfaceSetAndSave(): Start.");
    
    final FrameFixture editFrame = WindowFinder.findFrame("EditInterfaceWindow").withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    assertThat(editFrame).isNotNull().as("Tech Pack Edit window should be shown");   
    final JTabbedPaneFixture interfaceTabs = editFrame.tabbedPane();
    assertThat(interfaceTabs).isNotNull().as("The panel should be visible.");
    interfaceTabs.selectTab("Sets/Actions/Schedulings");      
    
    assertThat(editFrame.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(editFrame.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(editFrame.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(editFrame.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
  
    JTreeFixture etlSetTree = editFrame.tree();
    if (etlSetTree == null) {
    	editFrame.button("ETLSetHandlingViewWizard").click();
        FrameFixture fFix = new FrameFixture(TechPackIdeStarter.getMyRobot(), "ETLSetHandlingViewInterfaceSetWizardFrame");
        fFix.button("InterfaceSetWizardViewCreate").click();
        CommonUtils.waitForBusyIndicator(90000);    
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
        optionPane.okButton().click();
    }  
    
    //Now find fist set and disable it
    MeasurementTypesUtils.selectParameters(etlSetTree, 0);
    JCheckBoxFixture cBoxFix = MeasurementTypesUtils.findJCheckBoxWithPairComponentName(TechPackIdeStarter.getMyRobot(), "Disable");
    cBoxFix.check();

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    editFrame.button("ETLSetHandlingViewSave").click();    
    CommonUtils.waitForBusyIndicator(30000);
    logger.stopLogging();
    
    System.out.println("testDisableTheFirstInterfaceSetAndSave(): Tables modified by this test are: " 
  		  + CommonUtils.listModifiedTables() + ".");
    
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testDisableTheFirstInterfaceSetAndSave", TestSetupConstants.DB_ETLREP,
	        "META_COLLECTIONS", null)); 
    
    String[] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;
   
    System.out.println("testDisableTheFirstInterfaceSetAndSave(): Done.");
    
  }
  
  /**
   * PseudoEdit priority parameter test
   * 
   * - Open the first existing set - Select it's parameter row - Enter again the
   * same value that is in priority field - Click save-button.
   * 
   * @throws Exception
   */
  @org.junit.Ignore
  public void testPseudoEditPriorityParameterAndSave() throws Exception {
    System.out.println("testPseudoEditPriorityParameterAndSave(): Start.");
    
    final  FrameFixture myEditWindow = WindowFinder.findFrame("EditInterfaceWindow").withTimeout(600000).using(TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Interface Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");  
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    
    JTreeFixture etlSetTree = myEditWindow.tree();
    
    //Create sets first if non exist.
    if (etlSetTree == null) {
    	myEditWindow.button("ETLSetHandlingViewWizard").click();
        FrameFixture fFix = new FrameFixture(TechPackIdeStarter.getMyRobot(), "ETLSetHandlingViewTechpackSetWizardFrame");
        fFix.button("TechpackSetWizardViewCreate").click();
        CommonUtils.waitForBusyIndicator(90000);    
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
        optionPane.okButton().click();
    }
    
    JPanelFixture parameterFix = MeasurementTypesUtils.selectParameters(etlSetTree, 0);

    // Enter "10" to priority -field
    JTextComponentFixture priorityTextFix = SetsActionsSchedulingsUtils.findJTextComponentWithTitleAndJPanelFixture(
        "Priority", parameterFix);
    String origPriorityText = priorityTextFix.text();
    priorityTextFix.selectAll();
    priorityTextFix.enterText("10");

    // Editing "queue time" is used just to un-focus priority field
    JTextComponentFixture queueTimeTextFix = SetsActionsSchedulingsUtils.findJTextComponentWithTitleAndJPanelFixture(
        "Queue Time", parameterFix);
    queueTimeTextFix.setText(queueTimeTextFix.text());

    // Enter the original value back to priority field
    priorityTextFix.selectAll();
    priorityTextFix.enterText(origPriorityText);

    // Start database mod logger
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    myEditWindow.button("ETLSetHandlingViewSave").click();

    logger.stopLogging();
    CommonUtils.waitForBusyIndicator(30000);
    
    System.out.println("testPseudoEditPriorityParameterAndSave(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditPriorityParameterAndSave",
          TestSetupConstants.DB_ETLREP, "META_COLLECTIONS", null));

      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {      
    	myEditWindow.normalize();
    	assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();
        assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();

    }

    System.out.println("testPseudoEditPriorityParameterAndSave(): Stop.");
  }
 
} // end ManageNewInterfaceViewTest
