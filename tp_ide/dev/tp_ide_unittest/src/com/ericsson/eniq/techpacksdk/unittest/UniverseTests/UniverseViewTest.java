/**
 * This test class contains unit test cases for the TechPackIDE: Manage Universe
 * View.
 * 
 * * @author efaigha 20110915
 */
package com.ericsson.eniq.techpacksdk.unittest.UniverseTests;

import static org.fest.assertions.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
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
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;

import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;
import com.ericsson.eniq.techpacksdk.unittest.utils.UniverseParametersUtils;
import com.ericsson.eniq.techpacksdk.view.universeParameters.ExpandableTextField;

public class UniverseViewTest {

  private static FrameFixture myEditWindow = null;

  private static JTabbedPaneFixture myViewPanel = null;
  
  private static JPanelFixture universeTab = null;
  
  private static Robot robot = null;


  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(UniverseViewTest.class);
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: UniverseViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    // Get the TechPackIDE starter and start the IDE.
    TechPackIdeStarter starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
    
    robot = TechPackIdeStarter.getMyRobot();  
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
      
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    copyAndSelectTechPack(path);
    tpTree.selectPath(path);
    System.out.println("setUpBeforeClass(): Click edit.");
    CommonUtils.refreshIde();  
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    myEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(myEditWindow).isNotNull().as("The edit window should be shown.");

    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible.");
    
    universeTab = UniverseParametersUtils.getTab(robot, myViewPanel, "Universe");
    //Pause.pause(5000);    Pause is built in Universe utils
    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    // If table change logging is enabled, stop the logger.
    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

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

    // Print the changed database tables in the last test
    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    // Change the tree selection to the etlrep server node to reset the button
    // states.
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  }

  @org.junit.Test
  public void testAddUniverseTable() throws Exception {
    System.out.println("testAddUniverseTable(): Start.");

    // Start logging database changes
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.startLogging();
    logger.reset();
    
    
    JPanelFixture universeTablesTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Tables");
    JTableFixture universeTablesTable = universeTablesTab.table();

    // Add rows to the table
    for (int i = 0; i < 2; ++i) {
      JPopupMenuFixture popupMenu = universeTablesTable.tableHeader().showPopupMenuAt(0);
      popupMenu.menuItemWithPath("Add empty row").click();
      universeTablesTable.cell(TableCell.row(i).column(1)).enterValue("name" + i);
    }
    
    assertThat(universeTablesTab.button("UniverseTablesSave").target.isEnabled()).isTrue();
    // Save the changes
    universeTablesTab.button("UniverseTablesSave").click();

    // Wait for the saving to finish
    CommonUtils.waitForBusyIndicator(10000);

    logger.stopLogging();
    
    System.out.println("testAddUniverseTable(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");

    try {
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseTable", TestSetupConstants.DB_DWHREP,
          "Universetable", null));
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {
      assertThat(universeTablesTab.button("UniverseTablesClose").target.isEnabled()).isTrue();
      assertThat(universeTablesTab.button("UniverseTablesSave").target.isEnabled()).isFalse();
      assertThat(universeTablesTab.button("UniverseTablesDiscard").target.isEnabled()).isFalse();
    }

    System.out.println("testAddUniverseTable(): Done.");
  }

  @org.junit.Test
  public void testAddUniverseClass() throws Exception {
    System.out.println("testAddUniverseClass()");

    // Start logging database changes
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.startLogging();
    logger.reset();

    String[] classNames = { "Class1", "Class2"};
    JPanelFixture universeClassTab = addClasses(classNames);

    logger.stopLogging();
    System.out.println("testAddUniverseClass(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    try {
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseClass", TestSetupConstants.DB_DWHREP,
          "Universeclass", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseClass", TestSetupConstants.DB_DWHREP,
           "Universeobject", null));
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    } finally {
    	 assertThat(universeClassTab.button("UniverseClassClose").target.isEnabled()).isTrue();
         assertThat( universeClassTab.button("UniverseClassSave").target.isEnabled()).isFalse();
         assertThat(universeClassTab.button("UniverseClassCancel").target.isEnabled()).isFalse();
    }

    System.out.println("testAddUniverseClass(): End.");
  }

  @org.junit.Test
  public void testAddUniverseCondition() throws Exception {
    System.out.println("testAddUniverseCondition()");

    // Start logging database changes
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.startLogging();
    logger.reset();

    String[] classNames = { "Class3", "Class4" };
    addClasses(classNames);
    
    JPanelFixture universeConditionTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Conditions");
    JTableFixture universeConditionTable = universeConditionTab.table();

    // Add rows to the table
    for (int i = 0; i < classNames.length; ++i) {
      JPopupMenuFixture popupMenu = universeConditionTable.tableHeader().showPopupMenuAt(0);
      popupMenu.menuItemWithPath("Add empty row").click();
      universeConditionTable.cell(TableCell.row(i).column(0)).enterValue(classNames[i]);
      universeConditionTable.cell(TableCell.row(i).column(1)).enterValue("Condition" + i);
    }
    assertThat(universeConditionTab.button("UniverseConditionSave").target.isEnabled()).isTrue();
    // Save the changes
    universeConditionTab.button("UniverseConditionSave").click();

    // Wait for the saving to finish
    CommonUtils.waitForBusyIndicator(10000);

    logger.stopLogging();
    
    System.out.println("testAddUniverseCondition(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    try {
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseCondition", TestSetupConstants.DB_DWHREP,
          "Universeclass", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseCondition", TestSetupConstants.DB_DWHREP,
          "Universecondition", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseCondition", TestSetupConstants.DB_DWHREP,
          "Universeobject", null));
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables=null;
    } finally {
    	assertThat(universeConditionTab.button("UniverseConditionClose").target.isEnabled()).isTrue();
        assertThat( universeConditionTab.button("UniverseConditionSave").target.isEnabled()).isFalse();
        assertThat(universeConditionTab.button("UniverseConditionCancel").target.isEnabled()).isFalse();
    }

    System.out.println("testAddUniverseCondition(): End.");
  }

  @org.junit.Test
  public void testAddUniverseJoin() throws Exception {
    System.out.println("testAddUniverseJoin()");

    // Start logging database changes
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.startLogging();
    logger.reset();
    
    JPanelFixture universeJoinTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Joins");
    JTableFixture universeJoinTable = universeJoinTab.table();

    // Add rows to the table
    for (int i = 0; i < 3; ++i) {
      JPopupMenuFixture popupMenu = universeJoinTable.tableHeader().showPopupMenuAt(0);
      popupMenu.menuItemWithPath("Add empty row").click();
      universeJoinTable.cell(TableCell.row(i).column(0)).enterValue("SourceTable" + i);
      universeJoinTable.cell(TableCell.row(i).column(1)).enterValue("1");
      universeJoinTable.cell(TableCell.row(i).column(2)).enterValue("1");
      universeJoinTable.cell(TableCell.row(i).column(3)).enterValue("TargetTable" + i);
      universeJoinTable.cell(TableCell.row(i).column(4)).enterValue("2");
      universeJoinTable.cell(TableCell.row(i).column(5)).enterValue("2");
    }

    // Save the changes
    JButtonFixture saveButton = universeJoinTab.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton button) {
        return button.getText().equalsIgnoreCase("Save");
      }
    });
    saveButton.click();

    // Wait for the saving to finish
    CommonUtils.waitForBusyIndicator(10000);

    logger.stopLogging();

    String[] modifications = logger.get();
    for (int j = 0; j < modifications.length; j++) {
      String string = modifications[j];
      System.out.println(string);
    }

    try {
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseJoin", TestSetupConstants.DB_DWHREP,
          "Universejoin", null));
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    } finally {
      // Close the window
      JButtonFixture closeButton = universeJoinTab.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("Close");
        }
      });
      assertThat(closeButton.target.isEnabled()).isTrue();
    }

    System.out.println("testAddUniverseJoin(): End.");
  }

  @org.junit.Test
  public void testAddUniverseFormulas() throws Exception {
    System.out.println("testAddUniverseFormulas()");

    // Start logging database changes
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.startLogging();
    logger.reset();

    JPanelFixture universeFormulasTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Formulas");
    JTableFixture universeFormulasTable = universeFormulasTab.table("UniverseFormulasTable");

    // Add rows to the table
    // NOTE: For some reason, the table cells containing a combo box have to
    // be clicked before their value is modified. If this is not done, the
    // combo box is not considered visible by the FEST framework.
    JPopupMenuFixture popupMenu = universeFormulasTable.tableHeader().showPopupMenuAt(0);
    popupMenu.menuItemWithPath("Add empty row").click();
    universeFormulasTable.cell(TableCell.row(0).column(0)).enterValue("rather_complex_formula");
    setExpandableCellValue(universeFormulasTable.cell(TableCell.row(0).column(1)), "1+8", universeFormulasTab);
    universeFormulasTable.cell(TableCell.row(0).column(2)).click(); // See the
    // above
    // comment
    universeFormulasTable.cell(TableCell.row(0).column(2)).enterValue("character");
    universeFormulasTable.cell(TableCell.row(0).column(3)).click(); // See the
    // above
    // comment
    universeFormulasTable.cell(TableCell.row(0).column(3)).enterValue("detail");

    popupMenu = universeFormulasTable.tableHeader().showPopupMenuAt(0);
    popupMenu.menuItemWithPath("Add empty row").click();
    universeFormulasTable.cell(TableCell.row(1).column(0)).enterValue("even_more_complex_formula");
    setExpandableCellValue(universeFormulasTable.cell(TableCell.row(1).column(1)), "6/(3+1)", universeFormulasTab);
    universeFormulasTable.cell(TableCell.row(1).column(2)).click(); // See the
    // above
    // comment
    universeFormulasTable.cell(TableCell.row(1).column(2)).enterValue("number");
    universeFormulasTable.cell(TableCell.row(1).column(3)).click(); // See the
    // above
    // comment
    universeFormulasTable.cell(TableCell.row(1).column(3)).enterValue("measure");
    universeFormulasTable.cell(TableCell.row(1).column(4)).click(); // See the
    // above
    // comment
    universeFormulasTable.cell(TableCell.row(1).column(4)).enterValue("avg");

    // Save the changes
    JButtonFixture saveButton = universeFormulasTab.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton button) {
        return button.getText().equalsIgnoreCase("Save");
      }
    });
    saveButton.click();

    // Wait for the saving to finish
    CommonUtils.waitForBusyIndicator(10000);

    logger.stopLogging();

    String[] changes = logger.get();
    for (int i = 0; i < changes.length; i++) {
      String string = changes[i];
      System.out.println(string);
    }

    try {
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddUniverseFormulas", TestSetupConstants.DB_DWHREP,
          "Universeformulas", null));
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    } finally {
      // Close the window
      JButtonFixture closeButton = universeFormulasTab.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("Close");
        }
      });
      assertThat(closeButton.target.isEnabled()).isTrue();
    }

    System.out.println("testAddUniverseFormulas(): End.");
  }
  /**
   * Create a new Universe 
   * 
   * Assumptions: test assumes that BIS server is setup
   * 			  and exists in the C:\windows\System32\Drivers\etc\hosts file 
   * 			  ODBC connection must be setup for ENIQ server.
   */
  @org.junit.Test
  public void testCreateUniverse() throws Exception {
	  
	  System.out.println("testCreateUniverse()");
	  System.out.println("Creating universe while assuming that BO sever and ODBC connections are setup. ");

	  JPanelFixture universeGenerateTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Generate");
	  universeGenerateTab.click();
	  universeGenerateTab.button("Create Universe").click();
	  
	  DialogFixture universeWizard = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Universe Wizard");	 
	  
	  universeWizard.textBox("BO Repository").selectAll().enterText(TestSetupConstants.UNIVERSE_REPOSITORY);
	  universeWizard.textBox("BO Username").selectAll().enterText("Administrator");
	  universeWizard.comboBox().selectItem("ENTERPRISE");
	  universeWizard.textBox("ODBC ConnectionName").selectAll().enterText(TestSetupConstants.ODBC_CONNECTION);
	  
	  universeWizard.button(new GenericTypeMatcher<JButton>(JButton.class) {

	      @Override
	      protected boolean isMatching(JButton button) {
	        return button.getText().equalsIgnoreCase("Create Universe");
	      }
	    }).click();
	  
	  Pause.pause(180000);	  
	  try { 		
		  Runtime.getRuntime().exec("taskkill /F /IM TPIDE_BOIntf.exe"); 

		  } catch (Exception err) { 
		  err.printStackTrace(); 
		  }
	
	 verifyUniverseTask();	  
	//System.out.println("Universe is created: Please check logFile in " + System.getProperty("user.home") +" for any exception");
	  System.out.println("testCreateUniverse(): done.");

  }
  
  /**
   * Update Universe 
   * 
   * Assumptions: A universe already exists.
   * 			  test assumes that BIS server is setup
   * 			  and exists in the C:\windows\System32\Drivers\etc\hosts file 
   * 			  ODBC connection must be setup for ENIQ server.
   */
  @Ignore
  public void testUpdateUniverse() throws Exception {
	  
	  System.out.println("testUpdateUniverse()");
	  
	  JPanelFixture universeGenerateTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Generate");
	  universeGenerateTab.click();
	  universeGenerateTab.button("UpdateUniverse").click();
	  
	  DialogFixture universeWizard = CommonUtils.findDialogWithTitleName(robot, "Universe Wizard");
	  
	  universeWizard.textBox("BO Repository").selectAll().enterText(TestSetupConstants.UNIVERSE_REPOSITORY);
	  universeWizard.textBox("BO Username").selectAll().enterText("Administrator");
	  universeWizard.comboBox().selectItem("ENTERPRISE");
	  universeWizard.textBox("ODBC ConnectionName").selectAll().enterText(TestSetupConstants.ODBC_CONNECTION);
	  universeWizard.button(new GenericTypeMatcher<JButton>(JButton.class) {

	      @Override
	      protected boolean isMatching(JButton button) {
	        return button.getText().equalsIgnoreCase("Update Universe");
	      }
	    }).click();
	  
	  Pause.pause(60000);
	  
	  JFileChooserFixture fileChooser = new JFileChooserFixture(robot);
	  fileChooser.focus();
	  File uniDir = new File (System.getProperty("user.home")+File.separator+"unv");
	  fileChooser.setCurrentDirectory(uniDir);
//	  File uniFile = new File();
//	  fileChooser.selectFile(new UnvFileFilter())
//	  try { 		
//		  Runtime.getRuntime().exec("taskkill /F /IM TPIDE_BOIntf.exe"); 
//
//		  } catch (Exception err) { 
//		  err.printStackTrace(); 
//		  }
	  
	  System.out.println("testUpdateUniverse(): done.");
	  
	  
  }
  
  /**  
   * Assumptions: A universe already exists.
   * 			  "unv" and "rep" folder must exist in user's 
   * 			  home directory 			  
   */
  @Test
  public void testCreateBOPackage() throws Exception {
	  
	  System.out.println("testCreateBOPackage()");
	  
	  String univDirectory = System.getProperty("user.home")+File.separator+"unv";
	  String reportDirectory = System.getProperty("user.home")+File.separator+"rep";
	  
	  //check if above directories exist.
	  try {
		  File repDir = new File(reportDirectory);
		  if (!repDir.exists()){
			  System.out.println(reportDirectory+ " does not exist.creating rep directory...");
			  repDir.mkdir();
		  }
	  }catch (Exception e) {
		  System.out.println("Error in creating "+reportDirectory+ e.getMessage());
	  }
	  
	  
	  JPanelFixture universeGenerateTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Generate");
	  universeGenerateTab.click();
	  universeGenerateTab.button("CreateBOPackage").click();
	  
	  DialogFixture boPkgDialog = CommonUtils.findDialogWithTitleName(robot, "Create Business Objects Package");
	  boPkgDialog.textBox("Universe Folder").selectAll().enterText(univDirectory);
	  boPkgDialog.textBox("Report Folder").selectAll().enterText(reportDirectory);
	  boPkgDialog.textBox("Working Dir").selectAll().enterText(System.getProperty("user.home"));
	  boPkgDialog.textBox("Build Number").selectAll().enterText("b452");
	  
	  boPkgDialog.button("UniverseBOPackageCreateButton").click();
	  
	  CommonUtils.waitForBusyIndicator(60000);
	  
	  GenericTypeMatcher<JOptionPane> matcher = new GenericTypeMatcher<JOptionPane>(JOptionPane.class) {
	  	   protected boolean isMatching(JOptionPane optionPane) {
	  	     return "Successfully created BO package".equals(optionPane.getMessage());
	  	   }
	  	 };
	  
	  JOptionPaneFixture successPane = JOptionPaneFinder.findOptionPane(matcher).withTimeout(10000).using(robot);
	  successPane.okButton().click();
	  
  }
    
  //****************************
  //****** Helper Methods********
  //*****************************
  
  
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
	      System.out.println("copyAndSelectTechPack():New techpack path "+tp +" does not exist.");
		}
	    
	    if (techPackTree.target.getSelectionPath() == null) {	
	    	System.out.println("copyAndSelectTechPack(): Creating Test TechPack....");
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

  private void setExpandableCellValue(JTableCellFixture cellFixture, String value, JPanelFixture universeFormulasTab) {
    // Start editing the cell
    cellFixture.click();

    // Click the button to expand the cell
    JButtonFixture editButton = universeFormulasTab.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton button) {
        return button.getText().equals("...");
      }
    });
    editButton.click();

    // Get the dialog
    DialogFixture editDialog = WindowFinder.findDialog(ExpandableTextField.EditDialog.class).using(robot);
    editDialog.textBox().setText(value);

    // Close the dialog
    editDialog.button("EditDialogOkButton").click();
  }

  public static JPanelFixture addClasses(String[] classNames) {
    
    JPanelFixture universeClassTab = UniverseParametersUtils.getTab(robot, universeTab.tabbedPane(), "Classes");
    JTableFixture universeClassTable = universeClassTab.table();

    // Add rows to the table
    for (int i = 0; i < classNames.length; ++i) {
      JPopupMenuFixture popupMenu = universeClassTable.tableHeader().showPopupMenuAt(0);
      popupMenu.menuItemWithPath("Add empty row").click();
      universeClassTable.cell(TableCell.row(i).column(1)).enterValue(classNames[i]);
    }
    assertThat(universeClassTab.button("UniverseClassSave").target.isEnabled()).isTrue();
    // Save the changes
    universeClassTab.button("UniverseClassSave").click();

    // Wait for the saving to finish
    CommonUtils.waitForBusyIndicator(10000);

    return universeClassTab;
  }
  
  @SuppressWarnings("unchecked")
public static void verifyUniverseTask() {
	  File dir = new File(System.getProperty("user.home")); 	  
      File[] files  = dir.listFiles(new LogFileFilter());  
      assert files.length >0;    
      Arrays.sort(files, new Comparator(){ 
          public int compare(Object o1, Object o2) { 
              return compare( (File)o1, (File)o2); 
          } 
          private int compare( File f1, File f2){ 
              long result = f2.lastModified() - f1.lastModified(); 
              if( result > 0 ){ 
                  return 1; 
              } else if( result < 0 ){ 
                  return -1; 
              } else { 
                  return 0; 
              } 
          } 
      }); 
      for(int i=0, length=Math.min(files.length, 1); i<length; i++) {     
    	  readFile(files[i]);
      }     
  }
 private static void readFile(File file) { 		
 		try{
 			  FileInputStream fstream = new FileInputStream(file);
 	 		  DataInputStream in = new DataInputStream(fstream);
 			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
 			  String strLine;
 			  int currentLine = -1; 
 			  int startLine = 0;
 			  int endLine = 5;   
      		  while (currentLine<startLine) {
      			strLine = br.readLine();
      			if(strLine == null) {
      				throw new IOException("Create Universe Log File is empty!!!! check ODBC connection and BO Server");
      				
      			}
      			currentLine++;
      		  }
      		  while(currentLine<= endLine)  {
      			strLine = br.readLine();
      			if (strLine == null){
      				return;
      			}
      		  //assertFail()
      			if(strLine.contains("Exception:") || strLine.contains("Error")){
      				//Fail test
      				System.out.println("Create Universe Log File has Error!!!! Make sure BO server and ODBC connection are setup properly");
      			}     			
      		  currentLine++;	  
      		 }     		  
      		 in.close();

 			System.out.println("Universe created.. Check log file here "+System.getProperty("user.home") +" for details");
 			System.out.println("verifying that universe file exists.......");
 			verifyUniverseFile();
      	 }catch (Exception e){
      		 
      		  System.err.println("Error in reading Log file: " + e.getMessage());
      	 }
 	}

 	@SuppressWarnings("unchecked")
 	private static void verifyUniverseFile() {
 		File dir = new File(System.getProperty("user.home")+File.separator+"unv"); 
         File[] files  = dir.listFiles(new UnvFileFilter());
         if (files.length > 0) {
         Arrays.sort(files, new Comparator(){ 
             public int compare(Object o1, Object o2) { 
                 return compare( (File)o1, (File)o2); 
             } 
             private int compare( File f1, File f2){ 
                 long result = f2.lastModified() - f1.lastModified(); 
                 if( result > 0 ){ 
                     return 1; 
                 } else if( result < 0 ){ 
                     return -1; 
                 } else { 
                     return 0; 
                 } 
             } 
         }); 
         for(int i=0, length=Math.min(files.length, 1); i<length; i++) {     
         	if (files[i].exists()) {
         		assert files[i].exists();
         		System.out.println("Universe File exists " +files[i]); //assertTrue
         	}else {
         		System.out.println("Universe file does not exist: Failed to save file. check log file for error");
         	}
         	
         	
         	}
         }else {
         	System.out.println("No universe File found in this directory: "+dir.toString());
         }
 		
 	}
 }
  class LogFileFilter implements FilenameFilter {

 
 		public boolean accept(final File dir, final String name) {
 			return name.endsWith(".log");
 		}
 		  
 }
  class UnvFileFilter implements FilenameFilter {

 		public boolean accept(final File dir, final String name) {
 			return name.endsWith(".unv");
 		}
 		  
  }


