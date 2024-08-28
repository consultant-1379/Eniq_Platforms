/**
 * Tests for editing Sets/Actions/Schedulings-view in techpack ide
 */
package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Vector;

import junit.framework.JUnit4TestAdapter;


import org.fest.assertions.Fail;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ssc.rockfactory.TableModificationLogger;

import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.MeasurementTypesUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.SetsActionsSchedulingsUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;


public class SetsActionsSchedulingsViewTest { 
	
  private static FrameFixture mainWindow = null;  
  
  private static JTreeFixture tpTree = null;  
  
  private static FrameFixture myEditWindow = null; 
 
  private static FrameFixture techPackEditWindow = null;
  
  private static JTabbedPaneFixture techPackEditPanel = null; 
  
  private static Robot robot = null;

  //private static JTabbedPaneFixture mainPanel = null;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(SetsActionsSchedulingsViewTest.class);
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: SetsActionsSchedulingsViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    TechPackIdeStarter starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
    mainWindow = TechPackIdeStarter.getMyWindow();   
    assertThat(mainWindow).isNotNull();
    Pause.pause(4000);

    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    tpTree = mainWindow.tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);
    
    String techPackCopy = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME +"/" + TestSetupConstants.TEST_TP_NAME + ":" +TestSetupConstants.TP_NEW_VERSION;
    copyAndSelectTechPack(techPackCopy);
    System.out.println("setUpBeforeClass(): Click edit."); 
    CommonUtils.refreshIde();    
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    
    techPackEditWindow = CommonUtils.openTechPackEditWindow(techPackCopy, 40000);    
    assertThat(techPackEditWindow).isNotNull().as("The edit window should be shown.");

    techPackEditPanel = techPackEditWindow.tabbedPane();
    assertThat(techPackEditPanel).isNotNull().as("The panel should be visible.");

    techPackEditPanel.selectTab("Sets/Actions/Schedulings");

    System.out.println("setUpBeforeClass(): Pausing IDE to give it time to load the Sets/Actions/Scheduling Tab" );
    Pause.pause(30000);
    
    assertThat(techPackEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();
    assertThat(techPackEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    
    robot = TechPackIdeStarter.getMyRobot();
   

    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");
    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);
    if (techPackEditWindow != null){
    	techPackEditWindow.button("ETLSetHandlingViewClose").click();
  	   techPackEditWindow = null;
     }
     if (techPackEditPanel != null){
  	   techPackEditPanel = null;
     }
     System.out.println("tearDownAfterClass(): Pausing " + "for 3 seconds after closing.");
     Pause.pause(3000);
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
   
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  }

  /**
   * Wizard button test with skip:
   * 
   * - Click "Wizard"-button (on the tab) - Select skip-value instead of
   * overwrite - Click "Create"-button
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testWizardWithSkipValue() throws Exception {
    System.out.println("testWizardWithSkipValue(): Start.");   
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(robot);
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");
    myEditWindow.maximize();
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue(); 
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    myEditWindow.button("ETLSetHandlingViewWizard").click();

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    FrameFixture fFix = new FrameFixture(robot, "ETLSetHandlingViewTechpackSetWizardFrame");
    fFix.comboBox("TechpackSetWizardViewExistinSetsComboBox").selectItem("Skip");
    fFix.button("TechpackSetWizardViewCreate").click();

    logger.stopLogging();    
    CommonUtils.waitForBusyIndicator(90000);
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
    optionPane.okButton().click();
    
    System.out.println("testWizardWithSkipValue(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");


    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
//      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithSkipValue", TestSetupConstants.DB_ETLREP, 
//    		"META_COLLECTION_SETS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithSkipValue", TestSetupConstants.DB_ETLREP,
          "META_COLLECTIONS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithSkipValue", TestSetupConstants.DB_ETLREP,
          "META_SCHEDULINGS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithSkipValue", TestSetupConstants.DB_ETLREP,
          "META_TRANSFER_ACTIONS", new String[] { "ACTION_CONTENTS_01", "WHERE_CLAUSE_01" }));

      DatabaseTester.testTableList(tables);      
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {   
    	 myEditWindow.normalize();
    	assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();
        assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    }

    System.out.println("testWizardWithSkipValue(): Stop.");
  }
  
  /**
   * Wizard button test:
   * 
   * - Click "Wizard"-button (on the tab) - Use existing values - Click
   * - Uncheck Disk Manager Sets, DWH Sets and Directory Checker Sets
   *   "Create"-button
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testCreateSelectedSets() throws Exception {
    System.out.println("testCreateSelectedSets(): Start.");
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(robot);
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");
    myEditWindow.maximize();
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    myEditWindow.button("ETLSetHandlingViewWizard").click();

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    FrameFixture fFix = new FrameFixture(robot, "ETLSetHandlingViewTechpackSetWizardFrame");
    fFix.click();
    //Uncheck DWHSets    
    fFix.checkBox("DWH Sets").uncheck();   
    
    //uncheck Direnctory Sets
    fFix.checkBox("Directory Checker Sets").uncheck();
    
    //Uncheck Disk Manager Sets
    fFix.checkBox("Disk Manager Sets").uncheck();
    
    fFix.button("TechpackSetWizardViewCreate").click();

    logger.stopLogging();
    CommonUtils.waitForBusyIndicator(90000);
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
    optionPane.okButton().click();
    
    System.out.println("testCreateSelectedSets(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSelectedSets",
          TestSetupConstants.DB_ETLREP, "META_COLLECTIONS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSelectedSets",
//          TestSetupConstants.DB_ETLREP, "META_COLLECTION_SETS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSelectedSets",
          TestSetupConstants.DB_ETLREP, "META_SCHEDULINGS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateSelectedSets",
         TestSetupConstants.DB_ETLREP, "META_TRANSFER_ACTIONS",
         new String[] { "ACTION_CONTENTS_01", "WHERE_CLAUSE_01" }));

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

    System.out.println("testWizardWithDefaultParameters(): Stop.");
  }

  /**
   * Wizard button test:
   * 
   * - Click "Wizard"-button (on the tab) - Use existing values - Click
   * "Create"-button
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testWizardWithDefaultParameters() throws Exception {
    System.out.println("testWizardWithDefaultParameters(): Start.");
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(robot);
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");
    myEditWindow.maximize();
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    myEditWindow.button("ETLSetHandlingViewWizard").click();

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    FrameFixture fFix = new FrameFixture(robot, "ETLSetHandlingViewTechpackSetWizardFrame");
    fFix.button("TechpackSetWizardViewCreate").click();

    logger.stopLogging();
    CommonUtils.waitForBusyIndicator(90000);
    
    JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
    optionPane.okButton().click();
    
    System.out.println("testWizardWithDefaultParameters(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
//      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithDefaultParameters",
//          TestSetupConstants.DB_ETLREP, "META_COLLECTIONS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithDefaultParameters",
          TestSetupConstants.DB_ETLREP, "META_COLLECTION_SETS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithDefaultParameters",
          TestSetupConstants.DB_ETLREP, "META_SCHEDULINGS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testWizardWithDefaultParameters",
          TestSetupConstants.DB_ETLREP, "META_TRANSFER_ACTIONS",
          new String[] { "ACTION_CONTENTS_01", "WHERE_CLAUSE_01" }));

      DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
    	myEditWindow.normalize();
    	assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();
        assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    }

    System.out.println("testWizardWithDefaultParameters(): Stop.");
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
  public void testDisableTheFirstSetAndSave() throws Exception {
    System.out.println("testDisableTheFirstSetAndSave(): Start.");
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(robot);
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");
    myEditWindow.maximize();
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    
    JTreeFixture etlSetTree = myEditWindow.tree();
    
    //Create sets first if non exist.
    if (etlSetTree == null) {
    	myEditWindow.button("ETLSetHandlingViewWizard").click();
        FrameFixture fFix = new FrameFixture(robot, "ETLSetHandlingViewTechpackSetWizardFrame");
        fFix.button("TechpackSetWizardViewCreate").click();
        CommonUtils.waitForBusyIndicator(90000);    
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
        optionPane.okButton().click();
    }  
    
    //Now find fist set and disable it
    MeasurementTypesUtils.selectParameters(etlSetTree, 0);
    JCheckBoxFixture cBoxFix = MeasurementTypesUtils.findJCheckBoxWithPairComponentName(robot, "Disable");
    cBoxFix.check();

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    myEditWindow.button("ETLSetHandlingViewSave").click();    
    CommonUtils.waitForBusyIndicator(30000);
    logger.stopLogging();
    
    System.out.println("testDisableTheFirstSetAndSave(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableTheFirstSetAndSave",
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

    System.out.println("testDisableTheFirstSetAndSave(): Stop.");
  }

  /**
   * PseudoEdit priority parameter test
   * 
   * - Open the first existing set - Select it's parameter row - Enter again the
   * same value that is in priority field - Click save-button.
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testPseudoEditPriorityParameterAndSave() throws Exception {
    System.out.println("testPseudoEditPriorityParameterAndSave(): Start.");
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(robot);
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");   

    myEditWindow.tabbedPane().selectTab("Sets/Actions/Schedulings");
    myEditWindow.maximize();
    
    assertThat(myEditWindow.button("ETLSetHandlingViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ETLSetHandlingViewClose").target.isEnabled()).isTrue();   
    assertThat(myEditWindow.button("ETLSetHandlingViewWizard").target.isEnabled()).isTrue();
    
    
    JTreeFixture etlSetTree = myEditWindow.tree();
    
    //Create sets first if non exist.
    if (etlSetTree == null) {
    	myEditWindow.button("ETLSetHandlingViewWizard").click();
        FrameFixture fFix = new FrameFixture(robot, "ETLSetHandlingViewTechpackSetWizardFrame");
        fFix.button("TechpackSetWizardViewCreate").click();
        CommonUtils.waitForBusyIndicator(90000);    
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(robot);
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
  
  /**
   * A Helper method.
   * Checks if the given techPack exists in the tree.
   * IF NOT it creates a new techPack and selects it.
   * @param techPackPath
   */
  private static void copyAndSelectTechPack(String tp) {
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
	    assertThat(selectionPath.equals("["+TestSetupConstants.TEST_SERVER+","+ TestSetupConstants.TEST_TP_NAME + "," + TestSetupConstants.TEST_TP_NAME +":" +TestSetupConstants.TP_NEW_VERSION +"]"));	   
	  
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
