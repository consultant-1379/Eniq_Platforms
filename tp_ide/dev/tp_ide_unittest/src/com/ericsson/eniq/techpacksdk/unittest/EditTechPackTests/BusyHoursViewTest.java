/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
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
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import ssc.rockfactory.TableModificationLogger;
import tableTree.TableTreeComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.TableContainer;

import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.BusyhourplaceholdersFactory;
import com.ericsson.eniq.techpacksdk.unittest.fest.TTTableCellWriter;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.MeasurementTypesUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

/**
 * This test class contains test cases for testing the busy hour view. The tests
 * are implemented for testing the new BH Improvements, i.e. the new tree view
 * for managing the busy hour calculation. 
 * 
 */
public class BusyHoursViewTest {  

  private static FrameFixture myEditWindow = null; 

  private static FrameFixture editFrame = null;
  
  private static FrameFixture techPackEditWindow = null;
  
  private static JTabbedPaneFixture techPackEditPanel = null; 
  
  public static int COL_NUM_DESCRIPTION = 0;

  private static int COL_NUM_SOURCE = 1;

  public static int COL_NUM_WHERE = 2;

  private static int COL_NUM_CRITERIA = 3;

  private static int COL_NUM_KEYS = 4;

  private static int COL_NUM_TYPE = 5;

  private static int COL_NUM_MAPPED_TYPES = 6;

  private static int COL_NUM_GROUPING = 7;

  //private static int COL_NUM_Clause = 8;

  private static int COL_NUM_ENABLE = 8;
  
  public static int PPH = 5;

  private static TechPackIdeStarter starter;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(BusyHoursViewTest.class);
  }

  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass(): Start.");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: BusyHoursViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    //CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackCheck(path);
    
    System.out.println("setUpBeforeClass(): Click edit."); 
    CommonUtils.refreshIde();    
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    techPackEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(techPackEditWindow).isNotNull().as("The edit window should be shown.");

    techPackEditPanel = techPackEditWindow.tabbedPane();
    assertThat(techPackEditPanel).isNotNull().as("The panel should be visible.");

    techPackEditPanel.selectTab("Busy Hours");

    System.out.println("setUpBeforeClass(): Pausing IDE to give it time to load the Busy Hours Tab" );
    Pause.pause(30000);
    
    assertThat(techPackEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);    
    
//    robot = TechPackIdeStarter.getMyRobot();

    
    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");
   
    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);      
    if (techPackEditWindow != null){
    	techPackEditWindow.button("BusyHourViewClose").click();
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

//    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");
//    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
//    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  }

  /**
   * Test busy hour improvements. DC_E_TEST:1 techpack is copied to the
   * get it converted to new busy hour configuration. The techpack is opened up
   * for editing and existing busy hour grouping is changed for different values.
   *  Changes are saved and DB is verified.
   * @throws Exception
   */
  
 //****************************************************************************
 // Grouping is removed from BusyHour so Below Tet case is not valid anymore
 //******************************************************************************
 
//  public void testEditExistingProductBusyhourEntry_grouping() throws Exception {
//    System.out.println("testEditExistingProductBusyhourEntry_grouping(): Start.");
//   
//    final String mtTypeName = "AAL2AP";
//    final String mt2Name = TestSetupConstants.TEST_TP_NAME + "_" + mtTypeName + "BH";
//     
//    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(
//        TechPackIdeStarter.getMyRobot());
//   
//    myEditWindow.tabbedPane().selectTab("Busy Hours");
//
//    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
//    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
//    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
//
//    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
//    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
//
//    // Get the versionId of the target techpack (the current techpack). This
//    // value is used in selecting the tree path, and also as the name of the
//    // TableTreeComponent in the BusyHourTree.
//    String targetTechpackVersionId = TestSetupConstants.TEST_TP_NAME + ":((" + TestSetupConstants.TP_NEW_VERSION + "))";
//
//    bhTree.selectPath(targetTechpackVersionId);
//    bhTree.toggleRow(bhTree.target.getSelectionRows()[0]);
//    bhTree.toggleRow(bhTree.target.getSelectionRows()[0] + 1);
//
//    bhTree.component().setSelectionRow(bhTree.target.getSelectionRows()[0] + 1);
//    bhTree.component().startEditingAtPath(bhTree.component().getSelectionPath());
//
//    // Get the TTC
//    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) bhTree.target.getLastSelectedPathComponent();
//    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
//    assertThat(ttcFix).isNotNull().as("The TTC for techpack " + targetTechpackVersionId + " is not found.");
//
//    Pause.pause(1000);
//    System.out.println("Selecting the element busy hour support");
//    ttcFix.selectPath(mt2Name);
//    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
//
//    // Select the product placeholder node and expand it.
//    ttcFix.selectPath(mt2Name + "/" + Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + " Product Placeholders");
//
//    Pause.pause(1000);
//
//    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
//
//    Pause.pause(1000);
//    // by toggling node does not display table for some reason
//    // Therefore select it again and this displays table properly.
//    ttcFix.selectPath(mt2Name + "/" + Constants.DEFAULT_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + " Product Placeholders");
//
//    int elemBhPPTableRow = ttcFix.target.getSelectionRows()[0] + 1;
//
//    ttcFix.selectRow(elemBhPPTableRow); 
////    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
//   
//    Pause.pause(2000);
//    
//
//    // Find the table container and create the table and table header
//    // fixtures.
//    TableContainer table = CommonUtils.findTablePanel();
//    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");
//    JTableFixture prodBhTableFixture = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
//
//    // Set the custom table cell writer for the table. This is needed because
//    // otherwise editing of the description component cells does not work.
//    prodBhTableFixture.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
//    
//    JTableCellFixture cell = null;
//    int row = 0;
//
//    System.out.println("Entering values for row: " + (row + 1));
//
//    // Maximise the window to see the rest of the columns. Otherwise, the edit
//    // button for the rank keys cell is not visible on the screen.
//    myEditWindow.maximize();
//    try {
//      cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
//      
//      // Edit grouping -> node
//      cell.enterValue("Time");
//      Pause.pause(2000);
//
//      TableModificationLogger logger = TableModificationLogger.instance();
//      logger.reset();
//      logger.startLogging();
//
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_1(): Saving the changes.");
//      myEditWindow.button("BusyHourViewSave").click();
//      CommonUtils.waitForBusyIndicator(100000);
//
//      logger.stopLogging();
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_1(): Tables modified by this are: " + CommonUtils.listModifiedTables() + ".");
//
//
//      // DATABASE ASSERTION
//      List<TestTableItem> tables = new Vector<TestTableItem>();
//
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_1",
//          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_1",
//              TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_1",
//          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_1",
//          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_1",
//          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//
//      String[] modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
//
//      // Edit grouping -> node
//      cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
//      cell.enterValue("Node");
//
//      Pause.pause(2000);
//     
//      logger = TableModificationLogger.instance();
//      logger.reset();
//      logger.startLogging();
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_2(): Saving the changes.");
//      myEditWindow.button("BusyHourViewSave").click();
//
//      CommonUtils.waitForBusyIndicator(100000);
//
//      logger.stopLogging();
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_2(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
//
//      tables = new Vector<TestTableItem>();
//
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_2",
//              TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_2",
//              TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_2",
//              TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_2",
//              TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//
//      modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
//
//      // Edit grouping -> Time+Node
//      cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
//      cell.enterValue("Time + Node");
//
//      Pause.pause(2000);
//
//      logger = TableModificationLogger.instance();
//      logger.reset();
//      logger.startLogging();
//
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_3(): Saving the changes.");
//      myEditWindow.button("BusyHourViewSave").click();
//
//      CommonUtils.waitForBusyIndicator(100000);
//      logger.stopLogging();
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_3(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
//
//      // DATABASE ASSERTION
//      tables = new Vector<TestTableItem>();
//
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_3",
//              TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_3",
//              TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_3",
//              TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_3",
//              TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//
//      modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
//
//      // Edit grouping -> none
//      cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
//      cell.enterValue("None");
//
//      Pause.pause(2000);
//      logger = TableModificationLogger.instance();
//      logger.reset();
//      logger.startLogging();
//     
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_4(): Saving the changes.");
//      myEditWindow.button("BusyHourViewSave").click();
//   
//      CommonUtils.waitForBusyIndicator(100000);     
//      logger.stopLogging();
//      System.out.println("testEditExistingProductBusyhourEntry_grouping_4(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
//
//      // DATABASE ASSERTION
//      tables = new Vector<TestTableItem>();
//
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_4",
//              TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_4",
//              TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_4",
//              TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry_grouping_4",
//              TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//
//      modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
//
//    } finally {     
//     assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
//     assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
//     assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
//     CommonUtils.collapseTree(bhTree, 1);
//     myEditWindow.normalize();
//     Pause.pause(2000);
//    }
//
//    System.out.println("testEditExistingProductBusyhourEntry_grouping(): Stop.");
//  }

  /**
   * Test busy hour improvements.DC_E_TEST techpack is copied to the
   * get it converted to new busy hour configuration. The techpack is opened up
   * for editing and a new product busy hour details are defined for the ELEMBH
   * busy hour support. Changes are saved and DB is verified.
   * 
   * @throws Exception
   */
 
  @org.junit.Test
  public void testEditExistingProductBusyhourEntry2() throws Exception {
    System.out.println("testEditExistingProductBusyhourEntry2(): Start."); 
    
    String measurementType = TestSetupConstants.TEST_TP_NAME + "_" + "ELEMBH";
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(600000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");
    myEditWindow.tabbedPane().selectTab("Busy Hours");
    myEditWindow.maximize();  
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
   
   
    JTableCellFixture cell = null;
    DialogFixture editDialog = null; 
    int row = 0;    
    
    
    // Set the custom table cell writer for the table. This is needed because
    // otherwise editing of the description component cells does not work.    
    JTableFixture prodBhTableFixture = selectProdPlaceHoldersTable(bhTree, measurementType);
    System.out.println("Entering values for row: " + (row + 1));   
    try {      
      cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_TYPE));
      cell.startEditing();
      editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
      editDialog.comboBox("BusyhourAggTypeCellEditorAggregationType").selectItem(1);//Select Sliding Window.
      editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
      cell.stopEditing(); 
      Pause.pause(2000);

      TableModificationLogger logger = TableModificationLogger.instance();
      logger.reset();
      logger.startLogging();

      System.out.println("testEditExistingProductBusyhourEntry2_1(): Saving the changes.");
      myEditWindow.button("BusyHourViewSave").click();

      CommonUtils.waitForBusyIndicator(100000);

      logger.stopLogging();    
      //collapse tree before start editing again.
      // This revalidates tree
      collapseBHTreeAfterEdit(bhTree, measurementType);
      Pause.pause(1000);
      
      System.out.println("testEditExistingProductBusyhourEntry2_1(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");


      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();

      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_1",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_1",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_1",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_1",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_1",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      DatabaseTester.testTableList(tables);
      //String[] modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);  
      tables = null;
      Pause.pause(1000);
      JTableFixture prodBhTableFixture2 = selectProdPlaceHoldersTable(bhTree, measurementType);
      Pause.pause(1000);
 
      // Edit Busy Hour Type
      cell = prodBhTableFixture2.cell(TableCell.row(row).column(COL_NUM_TYPE));
      cell.startEditing();
      editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
      editDialog.comboBox("BusyhourAggTypeCellEditorAggregationType").selectItem(2);
      editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
      cell.stopEditing();

      Pause.pause(1000);
     
      logger.reset();
      logger.startLogging();

      System.out.println("testEditExistingProductBusyhourEntry2_2(): Saving the changes.");
      myEditWindow.button("BusyHourViewSave").click();

      CommonUtils.waitForBusyIndicator(100000);

      logger.stopLogging();
      //collapse tree before start editing again.
      // This revalidates tree
      collapseBHTreeAfterEdit(bhTree, measurementType);
      Pause.pause(1000);
      
      System.out.println("testEditExistingProductBusyhourEntry2_2(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

      // DATABASE ASSERTION
      tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_2",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
       tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_2",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_2",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_2",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_2",
//          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      
      DatabaseTester.testTableList(tables);
      //modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
      Pause.pause(1000);
      JTableFixture prodBhTableFixture3 = selectProdPlaceHoldersTable(bhTree, measurementType);
      Pause.pause(1000); 

      // Edit Busy Hour Type
      cell = prodBhTableFixture3.cell(TableCell.row(row).column(COL_NUM_TYPE));
      cell.startEditing();
      editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
      editDialog.textBox("lookback").deleteText();
      editDialog.textBox("lookback").enterText("15");
      editDialog.textBox("pthreshold").deleteText();
      editDialog.textBox("pthreshold").enterText("25");
      editDialog.textBox("nthreshold").deleteText();
      editDialog.textBox("nthreshold").enterText("35");
      editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
      cell.stopEditing();

      Pause.pause(2000);
     
      logger.reset();
      logger.startLogging();

      System.out.println("testEditExistingProductBusyhourEntry2_3(): Saving the changes.");
      myEditWindow.button("BusyHourViewSave").click();

      CommonUtils.waitForBusyIndicator(100000);
      logger.stopLogging();

      //collapse tree before start editing again.
      // This revalidates tree
      collapseBHTreeAfterEdit(bhTree, measurementType);
      Pause.pause(1000);

      System.out.println("testEditExistingProductBusyhourEntry2_3(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
      
      // DATABASE ASSERTION
      tables = new Vector<TestTableItem>();

      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_3",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_3",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_3",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_3",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_3",
//          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));

      DatabaseTester.testTableList(tables);
      //modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      
      tables = null;
      Pause.pause(1000);
      JTableFixture prodBhTableFixture4 = selectProdPlaceHoldersTable(bhTree, measurementType);
      Pause.pause(1000);
      
      // Edit Busy Hour Type
      cell = prodBhTableFixture4.cell(TableCell.row(row).column(COL_NUM_TYPE));
      cell.startEditing();
      editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
      editDialog.comboBox("BusyhourAggTypeCellEditorAggregationType").selectItem(3);
      editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
      cell.stopEditing();

      Pause.pause(2000);      
      // myEditWindow.normalize();
    
      logger.reset();
      logger.startLogging();

      System.out.println("testEditExistingProductBusyhourEntry2_4(): Saving the changes.");
      myEditWindow.button("BusyHourViewSave").click();

      CommonUtils.waitForBusyIndicator(100000);
      logger.stopLogging();
      collapseBHTreeAfterEdit(bhTree, measurementType);
      Pause.pause(1000);

      System.out.println("testEditExistingProductBusyhourEntry2_4(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
      
      // DATABASE ASSERTION
      tables = new Vector<TestTableItem>();

      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_4",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_4",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_4",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_4",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_4",
//          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      DatabaseTester.testTableList(tables);
      //modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      
      tables = null;
      Pause.pause(1000);
      JTableFixture prodBhTableFixture5 = selectProdPlaceHoldersTable(bhTree, measurementType);
      Pause.pause(1000);

      // Edit Busy Hour Type
      cell = prodBhTableFixture5.cell(TableCell.row(row).column(COL_NUM_TYPE));
      cell.startEditing();
      editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
      editDialog.textBox("lookback").deleteText();
      editDialog.textBox("lookback").enterText("25");
      editDialog.textBox("pthreshold").deleteText();
      editDialog.textBox("pthreshold").enterText("35");
      editDialog.textBox("nthreshold").deleteText();
      editDialog.textBox("nthreshold").enterText("45");
      editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
      cell.stopEditing();      
      
      //if Edit enable is TRUE.. 
      //then ide requires source and Formula for that PlaceHolder.     
    cell = prodBhTableFixture5.cell(TableCell.row(row).column(COL_NUM_CRITERIA));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test");
    Pause.pause(2000);
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();
    
    // Edit source column
    cell = prodBhTableFixture5.cell(TableCell.row(row).column(COL_NUM_SOURCE));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Source");
    JTableFixture sourceTable = editDialog.table();
    sourceTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Add Row").click();
    sourceTable.cell(TableCell.row(0).column(1)).click();

    JButtonFixture bFix = editDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton b) {
        if ("...".equals(b.getText()) && b.isVisible()) {
          return true;
        } else {
          return false;
        }
      }
    });
    bFix.click();

    DialogFixture baseTableDialogFix = new DialogFixture(TechPackIdeStarter.getMyRobot(),
        "BusyHourSourceSelectorCellEditorBaseTableDialog");    
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTargetTechPackComboBox").selectItem(0);
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTypeNameComboBox").selectItem(0);
    baseTableDialogFix.button("BusyHourSourceSelectorCellEditorBaseTableOkButton").click();
    Pause.pause(2000);
    editDialog.button("Busy Hour SourceOkButton").click();
    cell.stopEditing();
    
 // Edit Enabled
    cell = prodBhTableFixture5.cell(TableCell.row(row).column(COL_NUM_ENABLE));
    cell.enterValue("true");

    Pause.pause(2000);

 
    logger.reset();
    logger.startLogging();
    System.out.println("testEditExistingProductBusyhourEntry2_5(): Saving the changes.");
    myEditWindow.button("BusyHourViewSave").click();
    CommonUtils.waitForBusyIndicator(100000);
    logger.stopLogging();
    
    collapseBHTreeAfterEdit(bhTree, measurementType);
    Pause.pause(1000);
    System.out.println("testEditExistingProductBusyhourEntry2_5(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

      // DATABASE ASSERTION
    tables = new Vector<TestTableItem>();

    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry2_5",
    	TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));

    DatabaseTester.testTableList(tables);
    //modifiedTables = DatabaseTester.testTableList(tables);
    //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;
    } finally {
    	
       assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
       assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
       assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();     
    }
    System.out.println("testCreateOneProductBusyhourEntry(): Stop.");
  }

  /**
   * Test busy hour improvements. Old DC_E_MGW:((6)) techpack is copied to the
   * get it converted to new busy hour configuration. The techpack is opened up
   * for editing and a new product busy hour details are defined for the ELEMBH
   * busy hour support. Changes are saved and DB is verified.
   * 
   * @throws Exception
   */
 
  @org.junit.Test
  public void testEditExistingProductBusyhourEntry() throws Exception {
    System.out.println("testEditExistingProductBusyhourEntry(): Start.");

    final String mtTypeName = "AAL2AP";
    final String mt1Name = TestSetupConstants.TEST_TP_NAME + "_" + mtTypeName + "BH";

    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");

    myEditWindow.tabbedPane().selectTab("Busy Hours");
    Pause.pause(30000);

    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();    
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");

    System.out.println("Just set the winodw to maximum size!!");
    myEditWindow.maximize();  
    
       
    JTableFixture prodBhTableFixture = selectProdPlaceHoldersTable(bhTree, mt1Name);
      
    JTableCellFixture cell = null;
    FrameFixture editFrame = null;
    DialogFixture editDialog = null;
    int row = 0;
    
    // Set the row number of the product busy hour.
    row = 4;
    System.out.println("Entering values for row: " + (row + 1));

    // Edit description column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_DESCRIPTION));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test description");
    Pause.pause(2000);
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();

    // Edit source column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_SOURCE));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Source");
    JTableFixture sourceTable = editDialog.table();
    sourceTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Add Row").click();
    sourceTable.cell(TableCell.row(0).column(1)).click();

    JButtonFixture bFix = editDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton b) {
        if ("...".equals(b.getText()) && b.isVisible()) {
          return true;
        } else {
          return false;
        }
      }
    });
    bFix.click();

    DialogFixture baseTableDialogFix = new DialogFixture(TechPackIdeStarter.getMyRobot(),
        "BusyHourSourceSelectorCellEditorBaseTableDialog");    
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTargetTechPackComboBox").selectItem(0);
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTypeNameComboBox").selectItem(0);
    baseTableDialogFix.button("BusyHourSourceSelectorCellEditorBaseTableOkButton").click();
    Pause.pause(2000);
    editDialog.button("Busy Hour SourceOkButton").click();
    cell.stopEditing();

    // Edit where column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_WHERE));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test where");
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();

    // Edit Criteria column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_CRITERIA));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test criteria");
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();   

    // Edit Keys
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_KEYS));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour RANK Keys");
    JTableFixture keysTable = editDialog.table();
    int rowNumber = keysTable.rowCount();
    keysTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Add Row").click();
    keysTable.cell(TableCell.row(rowNumber).column(0)).enterValue("testkey");
    keysTable.cell(TableCell.row(rowNumber).column(2)).enterValue("testkey");
    Pause.pause(2000);
    editDialog.button("Busy Hour RANK KeysOkButton").click();
    cell.stopEditing();

    // Edit busy hour type
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_TYPE));

    // Edit mapped types
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_MAPPED_TYPES));

    // Edit grouping
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
    cell.enterValue("Time");

    // Edit enable
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_ENABLE));
    cell.enterValue("true");
    Pause.pause(2000);
   
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    System.out.println("testEditExistingProductBusyhourEntry(): Saving the changes.");
    myEditWindow.button("BusyHourViewSave").click();   
    CommonUtils.waitForBusyIndicator(100000);   

    logger.stopLogging();
        
    //Collapse tree now
    collapseBHTreeAfterEdit(bhTree, mt1Name);
    System.out.println("testEditExistingProductBusyhourEntry(): Tables modified by this are: " + CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      
      DatabaseTester.testTableList(tables);
//      String[] modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);

    } finally {   
    	myEditWindow.normalize();
        assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
        assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();      
      
    }

    System.out.println("testEditExistingProductBusyhourEntry(): Stop.");
  }
  
  /**
   * 
   * @throws Exception
   */

  @org.junit.Test
  public void testCreateNewProductBusyhourEntry() throws Exception {
    System.out.println("testCreateNewProductBusyhourEntry(): Start."); 
    final String mt1Name = TestSetupConstants.TEST_TP_NAME + "_" + "ELEMBH";
    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");
    myEditWindow.tabbedPane().selectTab("Busy Hours");
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
    
    //System.out.println("Just set the winodw to maximum size!!");
    myEditWindow.maximize();         
    JTableFixture prodBhTableFixture = selectProdPlaceHoldersTable(bhTree, mt1Name);

    JTableCellFixture cell = null;
    FrameFixture editFrame = null;
    DialogFixture editDialog = null;

    int row = 1;   
   
    // Edit description column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_DESCRIPTION));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test description");
    Pause.pause(2000);
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();

    // Edit source column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_SOURCE));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Source");
    JTableFixture sourceTable = editDialog.table();

    sourceTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Add Row").click();
    sourceTable.cell(TableCell.row(0).column(1)).click();

    JButtonFixture bFix = editDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton b) {
        if ("...".equals(b.getText()) && b.isVisible()) {
          return true;
        } else {
          return false;
        }
      }
    });
    bFix.click();

    DialogFixture baseTableDialogFix = new DialogFixture(TechPackIdeStarter.getMyRobot(),
        "BusyHourSourceSelectorCellEditorBaseTableDialog");
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTargetTechPackComboBox").selectItem(0);
    baseTableDialogFix.comboBox("BusyHourSourceSelectorCellEditorBaseTableTypeNameComboBox").selectItem(1);
    baseTableDialogFix.button("BusyHourSourceSelectorCellEditorBaseTableOkButton").click();
    Pause.pause(2000);
    editDialog.button("Busy Hour SourceOkButton").click();
    cell.stopEditing();

    // Edit where column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_WHERE));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test where");
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();

    // Edit Criteria column
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_CRITERIA));
    cell.startEditing();
    editFrame = WindowFinder.findFrame("DescriptionEditFrame").withTimeout(30000)
        .using(TechPackIdeStarter.getMyRobot());
    editFrame.textBox("DescriptionEditFrameEditPane").enterText("test criteria");
    editFrame.button("DescriptionEditFrameOkButton").click();
    cell.stopEditing();

  
    // Edit Keys    
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_KEYS));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour RANK Keys");
    JTableFixture keysTable = editDialog.table();
    int rowNumber = keysTable.rowCount();
    keysTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Add Row").click();
    keysTable.cell(TableCell.row(rowNumber).column(0)).enterValue("testRankKeys");
    keysTable.cell(TableCell.row(rowNumber).column(2)).enterValue("testRankKeys");
    Pause.pause(2000);
    editDialog.button("Busy Hour RANK KeysOkButton").click();
    cell.stopEditing();

    // Edit busy hour type
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_TYPE));
    cell.startEditing();
    editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Busy Hour Type");
    editDialog.comboBox("BusyhourAggTypeCellEditorAggregationType").selectItem(1);//Select Sliding Window.
    editDialog.button("BusyhourAggTypeCellEditorSaveButton").click();
    cell.stopEditing(); 
    Pause.pause(2000);

    // Edit mapped types
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_MAPPED_TYPES));

    // Edit grouping
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_GROUPING));
    cell.enterValue("Time");

    // Edit enable
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_ENABLE));
    cell.enterValue("true");

    Pause.pause(2000);
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    System.out.println("testCreateOneProductBusyhourEntry(): Saving the changes.");
    myEditWindow.button("BusyHourViewSave").click();

    CommonUtils.waitForBusyIndicator(100000);

    logger.stopLogging();
    
    //Collapse tree now
    collapseBHTreeAfterEdit(bhTree, mt1Name);    
    System.out.println("testCreateNewProductbusyhourEntry(): Tables modified by this are: " + CommonUtils.listModifiedTables() + ".");


    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testCreateNewProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      
      DatabaseTester.testTableList(tables);
//      String[] modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {       
    	myEditWindow.normalize();
        assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
        assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
        
    }

    System.out.println("testCreateNewProductBusyhourEntry(): Stop.");
  }
  /**
   * This test defines the Object BH support for a measurement type 
   * and verifies the Keys in MTBH must be difined in original MT.
   * Otherwise PP and CP views would not be created.
   * @throws Exception
   */
  @org.junit.Ignore
  public void testObjectBHSupport() throws Exception {
    System.out.println("testObjectBHSupport(): Start.");
    
  }
  /**
   * This test adds more Product PlaceHoders to
   * DC_E_ATMPORTBH and  verifies it in DB and also in Busy Hour
   * test then enables 4 PPs in Busyhour tab and try to reduce 
   * PPs in Measurement tab below 4. This should give error dialog.
   * @throws Exception
   */
  @org.junit.Test
  public void testModifyNumberOfPlaceHolders() throws Exception {
    System.out.println("testModifyNumberOfPlaceHolders(): Start.");
    
    final String mtName = TestSetupConstants.TEST_TP_NAME + "_" + "ATMPORTBH";    
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");
    System.out.println("Just set the winodw to maximum size!!");
    myEditWindow.maximize();      
    myEditWindow.tabbedPane().selectTab("Measurement Types");
    Pause.pause(60000);
    
    assertThat(myEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("MeasurementViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("MeasurementViewClose").target.isEnabled()).isTrue();
    
    //Expand DC_E_ATMPORT
    JTreeFixture measurementTree = myEditWindow.tree();
    assertThat(measurementTree).isNotNull().as("MeasurementTree should not be null");
    
  //Collapse tree if any  node was expanded in previous test.
    CommonUtils.collapseTree(measurementTree, 0);
    
    //verify that measurement type exists.
    int measurementRow = MeasurementTypesUtils.measurementTypeSanityCheck(mtName);
    MeasurementTypesUtils.selectParameters(measurementTree, measurementRow);
    PairComponent pairComponent = CommonUtils.findPairComponentWithText("Product BH Placeholders");
    
    JTextField productPlaceholder =   (JTextField) pairComponent.getComponent();    
    JTextComponentFixture pphFixture = new JTextComponentFixture(TechPackIdeStarter.getMyRobot(), productPlaceholder);
    pphFixture.selectAll().enterText("7").pressAndReleaseKeys(KeyEvent.VK_ENTER);
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    assertThat(myEditWindow.button("MeasurementViewSave").target.isEnabled()).isTrue();
    myEditWindow.button("MeasurementViewSave").click();
    CommonUtils.waitForBusyIndicator(30000);
    
    logger.startLogging();
    System.out.println("testModifyNumberOfPlaceHolders(): Tables modified by are: " 
    		+ CommonUtils.listModifiedTables() + ".");
    
    //Database Asertions
    RockFactory rock = TechPackIdeStarter.getDwhRepDb();
    
    Busyhourplaceholders bhpp = new Busyhourplaceholders(rock);
    bhpp.setVersionid(TestSetupConstants.TEST_TP_VERSIONID);
    bhpp.setBhlevel(mtName);
    
    BusyhourplaceholdersFactory ppF = new BusyhourplaceholdersFactory(rock, bhpp);
	  Iterator ffFIter = ppF.get().iterator();
	  while (ffFIter.hasNext()) {
		  Busyhourplaceholders pp0 = (Busyhourplaceholders) ffFIter.next();
		  PPH = pp0.getProductplaceholders();
		  assertThat(pp0.getProductplaceholders() == 7).as("Product Placeholders must be 7 in DBRep");
	  } // end while
    myEditWindow.tabbedPane().selectTab("Busy Hours");
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
    
    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
    
    JTableFixture placeholder = selectProdPlaceHoldersTable(bhTree, mtName);
    //Veify there are 7 rows under product Placeholder table.
    assertThat(placeholder.target.getRowCount() == 7)
    .as("There should be 7 rows in Prodect Placeholders table");
    
    try {
        // DATABASE ASSERTION
        List<TestTableItem> tables = new Vector<TestTableItem>();
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "BUSYHOURMAPPING", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "DATAITEM", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testModifyNumberOfPlaceHolders",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
        
        //DatabaseTester.testTableList(tables);
        String[] modifiedTables = DatabaseTester.testTableList(tables);
        DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
        tables = null;

      } finally {
    	//Collapse tree now
    	collapseBHTreeAfterEdit(bhTree, mtName); 
      	myEditWindow.normalize();
        assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
        assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
          
      }
      System.out.println("testModifyNumberOfPlaceHolders(): Stop.");
    
  }
  

  /**
   * Clear a selected row in the Product Placeholders.
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testClearExistingProductBusyhourEntry() throws Exception {
    System.out.println("testClearExistingProductBusyhourEntry(): Start.");
    final String mt1Name = TestSetupConstants.TEST_TP_NAME + "_" + "ELEMBH";
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());

    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");

    myEditWindow.tabbedPane().selectTab("Busy Hours");
    
    myEditWindow.maximize();  
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
 
    JTableFixture placeholder = selectProdPlaceHoldersTable(bhTree, mt1Name);
    placeholder.selectCell(TableCell.row(0).column(COL_NUM_DESCRIPTION));
    JPopupMenuFixture placeholderPopupMenu = placeholder.showPopupMenu();
    // Get the "Clear Row" option on the popup and click it...
    placeholderPopupMenu.menuItemWithPath("Clear Row").click();
    Pause.pause(2000);

    // Get a handle on the confirmation dialog.
    DialogFixture clearRowConfirmationDialog = CommonUtils.findDialogWithTitleName(placeholder.robot,
        "Confirm Clearing of Row");

    JOptionPaneFixture clearRowsJOptionPane = clearRowConfirmationDialog.optionPane();  
    clearRowsJOptionPane.yesButton().click();
    Pause.pause(2000);
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    System.out.println("testClearExistingProductBusyhourEntry(): Saving the changes.");
  
    myEditWindow.button("BusyHourViewSave").target.setEnabled(true);
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isTrue();
    myEditWindow.button("BusyHourViewSave").click();   
  
    CommonUtils.waitForBusyIndicator(100000);
    logger.stopLogging();
    
    //Collapse tree now
    collapseBHTreeAfterEdit(bhTree, mt1Name);  
    System.out.println("testClearExistingProductbusyhourEntry(): Tables modified by are: " + CommonUtils.listModifiedTables() + ".");


    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testClearExistingProductBusyhourEntry",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      DatabaseTester.testTableList(tables);
//      String[] modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;

    } finally {     
    	myEditWindow.normalize();
        assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
        assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse(); 
        assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();
        
    }
    System.out.println("testClearExistingProductBusyhourEntry(): Stop.");
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
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("Tech Pack Edit window should be shown");

    myEditWindow.tabbedPane().selectTab("Busy Hours");    
    assertThat(myEditWindow.button("BusyHourViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("BusyHourViewClose").target.isEnabled()).isTrue();

    JTreeFixture bhTree = myEditWindow.tree("BusyHourTree");
    assertThat(bhTree).isNotNull().as("BusyHourTree should not be null");
    
    //System.out.println("Just set the winodw to maximum size!!");
    //myEditWindow.maximize();      
       
    JTableFixture prodBhTableFixture = selectProdPlaceHoldersTable(bhTree, measurementTypeName);
    JTableCellFixture cell = null;
    int row = 0;
    // Edit Description
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_DESCRIPTION));    
    cell.enterValue("Criteria One");
    // Edit Where
    cell = prodBhTableFixture.cell(TableCell.row(row).column(COL_NUM_WHERE));
    //cell.enterValue("Where Clause"); 

    myEditWindow.button("BusyHourViewSave").click();  
    CommonUtils.waitForBusyIndicator(100000);
    
    //Collapse BHTree now
    collapseBHTreeAfterEdit(bhTree, measurementTypeName);
    myEditWindow.normalize();

    //Select TP in the Manage DWH tab...
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage DWH");    
    JTreeFixture dwhTree = TechPackIdeStarter.getMyWindow().tree("dwhTree");
    dwhTree.selectPath("root/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION);
    assertThat(dwhTree.target.getSelectionPath()).as("Sanity check passed: TP is Created!").isNotNull();
    
    //Verify that the Activate Busy Hour Criteria button is inactive...
    TechPackIdeStarter.getMyWindow().button("DWHBHActivate").requireDisabled();

    //Verify that the Activate Busy Hour Criteria button is active...
    TechPackIdeStarter.getMyWindow().button("DWHActivate").requireEnabled();
    
    //Activate the Busy Hour Criteria.
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
  	  		System.out.println("WARNING: TECHPACK activation failed.Check log for more information!!!!!!!!!!!");
  	  		 System.out.println("Continuing Activation Test despite the activation failure !!!!!!!!!!!! ");
  	  		  JOptionPaneFixture msgPane = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
  	  		  msgPane.okButton().click();
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
  
  
  public static JTableFixture selectProdPlaceHoldersTable(JTreeFixture tree, String measurmentTypeName) {
	  	System.out.println("Selecting Product placeholders for given measurement type");
	  	RockFactory rock = TechPackIdeStarter.getDwhRepDb();
	    
	    Busyhourplaceholders bhpp = new Busyhourplaceholders(rock);
	    bhpp.setVersionid(TestSetupConstants.COPY_TP_VERSIONID);
	    bhpp.setBhlevel(measurmentTypeName);
	    
	    BusyhourplaceholdersFactory ppF = null;
		try {
			ppF = new BusyhourplaceholdersFactory(rock, bhpp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Iterator ffFIter = ppF.get().iterator();
		  while (ffFIter.hasNext()) {
			  Busyhourplaceholders pp0 = (Busyhourplaceholders) ffFIter.next();
			  PPH = pp0.getProductplaceholders();			  
		  } // end while
	  	
	    String targetTechpackVersionId = TestSetupConstants.TEST_TP_NAME + ":((" + TestSetupConstants.TP_NEW_VERSION + "))";
	  	
	  	tree.selectPath(targetTechpackVersionId);
	  	tree.toggleRow(tree.target.getSelectionRows()[0]);
	  	tree.toggleRow(tree.target.getSelectionRows()[0] + 1);

	    // Start editing at second row
	  	tree.component().setSelectionRow(tree.target.getSelectionRows()[0] + 1);
	  	tree.component().startEditingAtPath(tree.component().getSelectionPath());

	    // Get the TTC
	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) tree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for techpack " + measurmentTypeName + " is not found.");

	    Pause.pause(1000); 	    
	    ttcFix.selectPath(measurmentTypeName);   
	    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);  
	    ttcFix.selectPath(measurmentTypeName);
	    Pause.pause(2000);
	    // Select the product placeholder node and expand it.
	    ttcFix.selectPath(measurmentTypeName + "/" + PPH + " Product Placeholders");

	    Pause.pause(1000);

	    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
	    Pause.pause(1000);
	    
	    // by toggling node does not display table for some reason
	    // select node again and this displays table properly.
	    ttcFix.selectPath(measurmentTypeName + "/" + PPH + " Product Placeholders");

	    Pause.pause(1000);

	    int elemBhPPTableRow = ttcFix.target.getSelectionRows()[0] + 1;

	    ttcFix.selectRow(elemBhPPTableRow);
	    //ttcFix.toggleRow(elemBhPPTableRow);

	    Pause.pause(2000);
	    TableContainer table = CommonUtils.findTablePanel();
	    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");

	    JTableFixture tableFix = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());

	    Pause.pause(1000);	      
	      
	   tableFix.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
	    
	    return tableFix;
  }
  
  public static void collapseBHTreeAfterEdit(JTreeFixture tree, String measTypeName) {		 
	  System.out.println("Collapsing BusyHour tree before start editing again....");
	  String targetTechpackVersionId = TestSetupConstants.TEST_TP_NAME + ":((" + TestSetupConstants.TP_NEW_VERSION + "))";
	  RockFactory rock = TechPackIdeStarter.getDwhRepDb();
	    
	    Busyhourplaceholders bhpp = new Busyhourplaceholders(rock);
	    bhpp.setVersionid(TestSetupConstants.COPY_TP_VERSIONID);
	    bhpp.setBhlevel(measTypeName);
	    
	    BusyhourplaceholdersFactory ppF = null;
		try {
			ppF = new BusyhourplaceholdersFactory(rock, bhpp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Iterator ffFIter = ppF.get().iterator();
		  while (ffFIter.hasNext()) {
			  Busyhourplaceholders pp0 = (Busyhourplaceholders) ffFIter.next();
			  PPH = pp0.getProductplaceholders();			  
		  } // end while
	  	tree.selectPath(targetTechpackVersionId);
	  	tree.target.collapseRow(tree.target.getSelectionRows()[0]+1);	  
	  	tree.target.collapseRow(tree.target.getSelectionRows()[0]);
	  	
		tree.selectPath(targetTechpackVersionId);
		tree.toggleRow(tree.target.getSelectionRows()[0]);
	  	tree.toggleRow(tree.target.getSelectionRows()[0] + 1);

	    // Start editing at second row
	  	tree.component().setSelectionRow(tree.target.getSelectionRows()[0] + 1);
	  	tree.component().startEditingAtPath(tree.component().getSelectionPath());

	    // Get the TTC
	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) tree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for techpack " + measTypeName + " is not found.");

	    Pause.pause(1000);
	    ttcFix.click();
	    Pause.pause(1000);
	    ttcFix.selectPath(measTypeName); 
	    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);	    
	    ttcFix.click(); 
	    ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
	    Pause.pause(500);
	    ttcFix.click(); 
	    ttcFix.selectPath(measTypeName); 
	    Pause.pause(1000);
	    ttcFix.selectPath(measTypeName + "/" + PPH + " Product Placeholders");

	    Pause.pause(1000);

	    ttcFix.target.collapseRow(ttcFix.target.getSelectionRows()[0]);
	    Pause.pause(1000);
	    
	    // by toggling node does not display table for some reason
	    // select node again and this displays table properly.
	    ttcFix.selectPath(measTypeName); 
	    ttcFix.target.collapseRow(ttcFix.target.getSelectionRows()[0]);
	    tree.selectPath(targetTechpackVersionId);
	    tree.target.collapseRow(tree.target.getSelectionRows()[0]);	
		} 
}
