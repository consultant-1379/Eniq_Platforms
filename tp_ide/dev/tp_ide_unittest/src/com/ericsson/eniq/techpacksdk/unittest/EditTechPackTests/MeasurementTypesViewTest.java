/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;

import junit.framework.JUnit4TestAdapter;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.fest.assertions.Fail;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.TableModificationLogger;
import tableTreeUtils.PairComponent;
import tableTreeUtils.TableContainer;

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.MeasurementTypesUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

/**
 * This test class contains unit test cases for the TechPackIDE: Measurement
 * Types view.
 *  
 */
public class MeasurementTypesViewTest {

  // Note: extends TestCase must be omitted from the class definition in order
  // the beforeClass and AfterClass to work.
  
  private static FrameFixture techPackEditWindow = null;

  private static Robot robot = null;
  
  //private static  int mtRow = 0;

  private static JTabbedPaneFixture techPackEditPanel = null; 

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(MeasurementTypesViewTest.class);
  }   

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: MeasurementTypesViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    // Get the TechPackIDE starter and start the IDE.
    TechPackIdeStarter starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");
    }
    
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    //CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    techPackSanityCheck(path);
    
    System.out.println("setUpBeforeClass(): Click edit."); 
    CommonUtils.refreshIde();    
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    techPackEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(techPackEditWindow).isNotNull().as("The edit window should be shown.");

    techPackEditPanel = techPackEditWindow.tabbedPane();
    assertThat(techPackEditPanel).isNotNull().as("The panel should be visible.");
    techPackEditWindow.maximize();

    techPackEditPanel.selectTab("Measurement Types");
//    CommonUtils.waitForBusyIndicator(90000);
    System.out.println("Pausing IDE to give it time to load the Measurement Types Tab" );
    Pause.pause(90000);
    assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("MeasurementViewCancel").target.isEnabled()).isFalse();
    assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled()).isTrue();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);    
    
    robot = TechPackIdeStarter.getMyRobot();


    System.out.println("setUpBeforeClass(): Done.");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");
   
   CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);  
   
   if (techPackEditWindow != null){
	   techPackEditWindow.button("MeasurementViewClose").click();
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

    // Reset logger
    CommonUtils.resetTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

  }

  @After
  public void tearDown() throws Exception {
    System.out.println("tearDown()");
   
    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    // Change the tree selection to the server node to reset the button
    // states.
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  }

//  /**
//   * This test opens Test TechPack DC_E_TEST for edit
//   * Navigates to Measurement types tab 
//   * adds a new dummy measurement type with basic  parameters 
//   * keys and counters.
//   * 
//   * @throws Exception
//   */
// 
  @org.junit.Test
  public void testAddMeasurementType() throws Exception {
    System.out.println("testAddMeasurementType(): Start.");
  
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();


    JTreeFixture measurementTypesTree = techPackEditWindow.tree();
    assertThat(measurementTypesTree).isNotNull();
    measurementTypesTree.rightClick();

    JPopupMenu targetPopupMenu = robot.findActivePopupMenu();
    assertThat(targetPopupMenu).isNotNull();
    JPopupMenuFixture popupMenu = new JPopupMenuFixture(robot, targetPopupMenu);

    JMenuItemFixture addElementMenuItem = popupMenu.menuItemWithPath("Add Element");
    addElementMenuItem.click();

    ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        if (!(c instanceof JDialog))
          return false;
        return ((JDialog) c).isVisible();
      }
    };

    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JDialog targetDialog = (JDialog) finder.find(matcher);
    assertThat(targetDialog).isNotNull();

    DialogFixture nameDialog = new DialogFixture(robot, targetDialog);
    nameDialog.textBox().deleteText();
    nameDialog.textBox().enterText(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
    nameDialog.button("LimitedSizeTextDialogOkButton").click();
    
    String selectionPath = measurementTypesTree.target.getSelectionPath().toString();
    assertThat(selectionPath.equals("[root, " + TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "]"));   
    
    int measurementRow = measurementTypesTree.target.getRowCount()-1;
    
    MeasurementTypesUtils.selectKeys(measurementTypesTree, measurementRow );
    MeasurementTypesUtils.addMeasurementKey(robot, measurementTypesTree, measurementRow, "KEY1", "varchar", "100", true);    
    
    MeasurementTypesUtils.selectCounters(measurementTypesTree, measurementRow);
    
    MeasurementTypesUtils.addMeasurementCounter(robot, measurementTypesTree, measurementRow, "COUNTER1", "varchar", "50");

    JButtonFixture saveButton = new JButtonFixture(robot, "MeasurementViewSave");
    saveButton.click();
    CommonUtils.waitForBusyIndicator(360000);

    logger.stopLogging();
    
    //DATABASE ASSERTIONS
    System.out.println("testAddMeasurementType(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {  
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "DATAITEM", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "UNIVERSECLASS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
//          "MEASUREMENTTYPECLASS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTKEY", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTTABLE", null));      
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
//          "EXTERNALSTATEMENT", new String[] { "STATEMENT" }));      
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTCOUNTER", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "MEASUREMENTDELTACALCSUPPORT", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "REFERENCETABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
          "TRANSFORMER", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddMeasurementType", TestSetupConstants.DB_DWHREP,
              "TPACTIVATION", null));      

      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    }

    System.out.println("testAddMeasurementType(): Done.");
  }

  /**
   * Test when duplicating MeasurementType Keys and Counters.
   * When a DataName is changed, the related DataId should be equal to the new DataName.
   * Rename a duplicated one and check it's DataName is the same as the new name.   
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testDuplicateKeyCounter() throws Exception {
	  System.out.println("testDuplicateKeyCounter(): Start.");
	 
	  TableModificationLogger logger = TableModificationLogger.instance();
	  logger.reset();
	  logger.startLogging();
	
//	  techPackEditWindow.maximize();
	  JTreeFixture measurementTypesTree = techPackEditWindow.tree();
	  assertThat(measurementTypesTree).isNotNull();
	  
	  CommonUtils.collapseTree(measurementTypesTree, 0);
	  
	  int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	 
	  final String newKeyName = "keynew";	  
	  MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow );
	  MeasurementTypesUtils.addMeasurementKey(robot, measurementTypesTree, mtRow, "DuplicateKey", "numeric", "100", false);
	  JTable keysTableTarget = MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow);
	  duplicateRows(measurementTypesTree, keysTableTarget);
	  JTable keysTableTarget2 = MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow);
	  renameLastRow(newKeyName, keysTableTarget2);
	
	  final String newCounterName = "newcounter";
	  JTable countersTableTarget = MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);
	  duplicateRows(measurementTypesTree, countersTableTarget);
	  JTable countersTableTarget2 = MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);
	  renameLastRow(newCounterName, countersTableTarget2);
	  
	  techPackEditWindow.button("MeasurementViewSave").click();
	  CommonUtils.waitForBusyIndicator(120000);
	  logger.stopLogging();	  

	  
	  try {
	      // Perform the database assertions
		  RockFactory rock = TechPackIdeStarter.getDwhRepDb();
		  // DATAID should be the same as the DATANAME after it has changed.
		  // Key
		  Measurementkey measurementkey = new Measurementkey(rock);
		  measurementkey.setTypeid("DC_E_TEST:((1)):DC_E_TEST_AAL1TPVCCTP");
		  measurementkey.setDataname(newKeyName);
		  MeasurementkeyFactory measurementkeyF = new MeasurementkeyFactory(rock, measurementkey);
		  Iterator measurementkeyFI = measurementkeyF.get().iterator();
		  while (measurementkeyFI.hasNext()) {
			  Measurementkey key = (Measurementkey) measurementkeyFI.next();
			  assertThat(key.getDataid()).isEqualTo(key.getDataname()).as("Should be the same value for both Dataname: "+key.getDataname()+" and DataId: "+key.getDataid());
		  } // end while
		  // Counter
		  Measurementcounter measurementcounter = new Measurementcounter(rock);
		  measurementcounter.setTypeid("DC_E_TEST:((1)):DC_E_TEST_AAL1TPVCCTP");
		  measurementcounter.setDataname(newCounterName);
		  MeasurementcounterFactory measurementcounterF = new MeasurementcounterFactory(rock, measurementcounter);
		  Iterator measurementcounterFI = measurementcounterF.get().iterator();
		  while (measurementcounterFI.hasNext()) {
			  Measurementcounter counter = (Measurementcounter) measurementcounterFI.next();
			  assertThat(counter.getDataid()).isEqualTo(counter.getDataname()).as("Should be the same value for both Dataname: "+counter.getDataname()+" and DataId: "+counter.getDataid());
		  } // end while
	  } finally {		  
		  assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
		  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
	  }
	  System.out.println("testDuplicateKeyCounter(): End.");
  } // testDuplicateKeyCounter
  
  
  /**
   * Remove Measurement Counter
   * @throws Exception
   */
  @org.junit.Test
  public void testRemoveMeasurementCounter() throws Exception {
	  System.out.println("testRemoveMeasurementCounter(): Start.");
	 
	  TableModificationLogger logger = TableModificationLogger.instance();
	  logger.reset();
	  logger.startLogging();
	
//	  techPackEditWindow.maximize();
	  JTreeFixture measurementTypesTree = techPackEditWindow.tree();
	  assertThat(measurementTypesTree).isNotNull();
	  
	  CommonUtils.collapseTree(measurementTypesTree, 0);
	  
	  int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	  JTable countersTableTarget = MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);
	  JTableFixture jTableFixture = new JTableFixture(robot, countersTableTarget);
	  final int editRowNumber = jTableFixture.rowCount() > 0 ? jTableFixture.rowCount() - 1 : 0;
	  TableCell nameCell = TableCell.row(editRowNumber).column(0);		
	  JTableCellFixture nameCellFixture = jTableFixture.cell(nameCell);
	
	  nameCellFixture.click(); // Select/Highlight row in table.
	  JPopupMenuFixture popupMenu = jTableFixture.showPopupMenuAt(nameCell);
	  assertThat(popupMenu).isNotNull().as("Could not pop up menu for cell");
	  JMenuItemFixture deleteRowItem = popupMenu.menuItemWithPath("Delete Row");
	  assertThat(deleteRowItem).isNotNull().as("Menu item Delete Row could not be found");
	  deleteRowItem.click();
	  
	  DialogFixture clearRowConfirmationDialog = CommonUtils.findDialogWithTitleName(robot, "Confirm Remove");

	  JOptionPaneFixture clearRowsJOptionPane = clearRowConfirmationDialog.optionPane();  
	  clearRowsJOptionPane.yesButton().click();
	  Pause.pause(2000);
	  
	  techPackEditWindow.button("MeasurementViewSave").click();
	  CommonUtils.waitForBusyIndicator(120000);
	  logger.stopLogging();
	  
	  //DATABASE ASSERTIONS
	  System.out.println("testRemoveMeasurementCounter(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	  
	  try {
	      // Perform the database assertions
	      List<TestTableItem> tables = new Vector<TestTableItem>();
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Aggregation", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Aggregationrule", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Busyhourplaceholders", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Busyhourrankkeys", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Dataitem", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
//	          "Dataformat", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Universeclass", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Measurementdeltacalcsupport", null));     
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Tpactivation", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
//	          "Transformer", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
//	          "Measurementtypeclass", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Measurementtype", new String[] { "DESCRIPTION" }));
	      //tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          //"Measurementkey", new String[] { "DESCRIPTION" }));
	      //tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          //"Measurementvector", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Measurementtable", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Measurementcolumn", new String[] { "DESCRIPTION" }));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Measurementcounter", new String[] { "DESCRIPTION" }));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Referencecolumn", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementCounter", TestSetupConstants.DB_DWHREP,
	          "Referencetable", null));
	      
	      //DatabaseTester.testTableList(tables);
	      String[] modifiedTables = DatabaseTester.testTableList(tables);
	      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
	      tables = null;
	    } finally {     
	  	  assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
		  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
	    }

	    System.out.println("testRemoveMeasurementCounter(): Done.");
  }
  
  /** Remove Key
   * @throws Exception
   */
  @org.junit.Test
  public void testRemoveMeasurementKey() throws Exception {
	  System.out.println("testRemoveMeasurementKey(): Start.");
	 
	  TableModificationLogger logger = TableModificationLogger.instance();
	  logger.reset();
	  logger.startLogging();
	
//	  techPackEditWindow.maximize();
	  JTreeFixture measurementTypesTree = techPackEditWindow.tree();
	  assertThat(measurementTypesTree).isNotNull();
	  
	  CommonUtils.collapseTree(measurementTypesTree, 0);
	  
	  int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	  JTable countersTableTarget = MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow);
	  JTableFixture jTableFixture = new JTableFixture(robot, countersTableTarget);
	  final int editRowNumber = jTableFixture.rowCount() > 0 ? jTableFixture.rowCount() - 1 : 0;
	  TableCell nameCell = TableCell.row(editRowNumber).column(0);		
	  JTableCellFixture nameCellFixture = jTableFixture.cell(nameCell);
	
	  nameCellFixture.click(); // Select/Highlight row in table.
	  JPopupMenuFixture popupMenu = jTableFixture.showPopupMenuAt(nameCell);
	  assertThat(popupMenu).isNotNull().as("Could not pop up menu for cell");
	  JMenuItemFixture deleteRowItem = popupMenu.menuItemWithPath("Delete Row");
	  assertThat(deleteRowItem).isNotNull().as("Menu item Delete Row could not be found");
	  deleteRowItem.click();
	  
	  DialogFixture clearRowConfirmationDialog = CommonUtils.findDialogWithTitleName(robot, "Confirm Remove");

	  JOptionPaneFixture clearRowsJOptionPane = clearRowConfirmationDialog.optionPane();  
	  clearRowsJOptionPane.yesButton().click();
	  Pause.pause(2000);
	  
	  techPackEditWindow.button("MeasurementViewSave").click();
	  CommonUtils.waitForBusyIndicator(120000);
	  logger.stopLogging();
	  
	  //DATABASE ASSERTIONS
	  System.out.println("testRemoveMeasurementKey(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");
	  
	  try {
	      // Perform the database assertions
	      List<TestTableItem> tables = new Vector<TestTableItem>();
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Aggregation", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Aggregationrule", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Busyhourplaceholders", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Busyhourrankkeys", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Dataitem", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
//	          "Dataformat", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Universeclass", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Measurementdeltacalcsupport", null));     
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Tpactivation", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
//	          "Transformer", null));
//	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
//	          "Measurementtypeclass", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Measurementtype", new String[] { "DESCRIPTION" }));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Measurementkey", new String[] { "DESCRIPTION" }));
	      //tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          //"Measurementvector", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Measurementtable", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Measurementcolumn", new String[] { "DESCRIPTION" }));
	      //tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          //"Measurementcounter", new String[] { "DESCRIPTION" }));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Referencecolumn", null));
	      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementKey", TestSetupConstants.DB_DWHREP,
	          "Referencetable", null));
	      
	      //DatabaseTester.testTableList(tables);
	      String[] modifiedTables = DatabaseTester.testTableList(tables);
	      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
	      tables = null;
	    } finally {     
	  	  assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
		  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
	    }

	    System.out.println("testRemoveMeasurementKey(): Done.");
  }
  /**
   * Add a vector counter by selecting it in parameters panel.
   * @throws Exception
   **/
  @org.junit.Test
  public void testAddVectorCounter() throws Exception {
    System.out.println("testAddVectorCounter(): Start.");
  
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging(); 

    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);    

    editFrame.tabbedPane().selectTab("Measurement Types");   
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();

    //Collapse tree if any  node was expanded in previous test.
    CommonUtils.collapseTree(measurementTypesTree, 0);
    
    int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);   

    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, mtRow);
    JRadioButtonFixture rb = parameterFixture.radioButton(new GenericTypeMatcher<JRadioButton>(JRadioButton.class) {

      @Override
      protected boolean isMatching(JRadioButton button) {
        return button.getText().equals("Vector Table");
      }
    });
    rb.click();
    
    MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow);  
    JTable countersTableTarget = MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);
    JTableFixture countersTable = new JTableFixture(robot, countersTableTarget);

    final int rowNumber = 0;
    final int columnNumber = 7;
    // HACK: The cell click must be done with coordinates, since the cell
    // fixture does not recognize the TableComponent as a cell editor. The
    // frame is also maximizes, because otherwise the click will go out of the
    // edit frame.
    editFrame.maximize();
    Rectangle rect = countersTableTarget.getCellRect(rowNumber, columnNumber, true);
    countersTable.robot.click(countersTableTarget, new Point(rect.x + rect.width - 5, rect.y + rect.height - 5));

    ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        if (!(c instanceof JDialog))
          return false;
        return ((JDialog) c).isVisible() && ((JDialog) c).getTitle().equals("VectorCounter");
      }
    };
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JDialog targetDialog = (JDialog) finder.find(matcher);
    assertThat(targetDialog).isNotNull();
    DialogFixture dialog = new DialogFixture(robot, targetDialog);

    for (int i = 0; i < 3; ++i) {
      JPopupMenuFixture popupMenu = dialog.table().tableHeader().showPopupMenuAt(0);
      
      JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

        @Override
        protected boolean isMatching(JMenuItem menuItem) {
          return menuItem.getText().equals("Add Row");
        }
      });
      menuItem.click();
    }
    // Add values for the rows.
    JTableFixture table = dialog.table();
    // Row 0
    table.cell(TableCell.row(0).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(0).column(2)).enterValue("1");
    table.cell(TableCell.row(0).column(3)).enterValue("10");
    table.cell(TableCell.row(0).column(4)).enterValue("%");
    // Row 1
    table.cell(TableCell.row(1).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(1).column(2)).enterValue("11");
    table.cell(TableCell.row(1).column(3)).enterValue("50");
    table.cell(TableCell.row(1).column(4)).enterValue("%");
    // Row 2
    table.cell(TableCell.row(2).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(2).column(2)).enterValue("51");
    table.cell(TableCell.row(2).column(3)).enterValue("100");
    table.cell(TableCell.row(2).column(4)).enterValue("%");
    
    JButtonFixture okButton = dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton button) {
        return button.getText().equalsIgnoreCase("ok");
      }
    });
    okButton.click();
    
    //editFrame.normalize();

    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();
    
    System.out.println("testAddVectorCounter(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {
      // Perform the database assertions
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Aggregation", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Aggregationrule", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Busyhourplaceholders", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Busyhourrankkeys", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Dataitem", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
//          "Dataformat", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Universeclass", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementdeltacalcsupport", null));     
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Tpactivation", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
//          "Transformer", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
//          "Measurementtypeclass", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementtype", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementkey", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementvector", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementtable", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Measurementcolumn", new String[] { "DESCRIPTION" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
//          "Measurementcounter", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Referencecolumn", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounter", TestSetupConstants.DB_DWHREP,
          "Referencetable", null));
      
      DatabaseTester.testTableList(tables);
      //String[] modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
  	  assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testAddVectorCounter(): Done.");
  } // testAddVectorCounter
  
  /**
   * Creates a new measurement type and selects vector table.
   * then in the counters PmRexVector is selected from drop down list.
   * @throws Exception
   */
  @org.junit.Test
  public void testAddVectorCounterPmRes() throws Exception {
    System.out.println("testAddVectorCounterPmResPmRes(): Start.");

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();   

    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");  
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();
    
    //Collapse tree if any  node was expanded in previous test.
    CommonUtils.collapseTree(measurementTypesTree, 0);
    
    int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);

    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, mtRow);
    JRadioButtonFixture rb = parameterFixture.radioButton(new GenericTypeMatcher<JRadioButton>(JRadioButton.class) {

      @Override
      protected boolean isMatching(JRadioButton button) {
        return button.getText().equals("Vector Table");
      }
    });
    rb.click();

    MeasurementTypesUtils.selectKeys(measurementTypesTree, mtRow); 

    JTable countersTableTarget = MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);
    JTableFixture countersTable = new JTableFixture(robot, countersTableTarget);
    MeasurementTypesUtils.addMeasurementCounter(robot, measurementTypesTree, mtRow, "PmResCounter", "varchar",
        "50");
    
    JTableCellFixture counterTypeCell = countersTable.cell(TableCell.row(countersTable.rowCount()-1).column(6));
    // Set the CounterType to PMRESVECTOR, so Quantity col should show.
    counterTypeCell.enterValue("PMRESVECTOR");
    // Get the vector counter table cell and click the '...' button to open
    // the vector dialog.
    final int columnNumber = 7;
    editFrame.maximize();
    Rectangle rect = countersTableTarget.getCellRect(countersTable.rowCount()-1, columnNumber, true);
    countersTable.robot.click(countersTableTarget, new Point(rect.x + rect.width - 5, rect.y + rect.height - 5));

    ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        if (!(c instanceof JDialog))
          return false;
        return ((JDialog) c).isVisible() && ((JDialog) c).getTitle().equals("VectorCounter");
      }
    };
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JDialog targetDialog = (JDialog) finder.find(matcher);
    assertThat(targetDialog).isNotNull();
    DialogFixture dialog = new DialogFixture(robot, targetDialog);

    // Add three new rows
    for (int i = 0; i < 3; ++i) {
      JPopupMenuFixture popupMenu = dialog.table().tableHeader().showPopupMenuAt(0);    
      JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

        @Override
        protected boolean isMatching(JMenuItem menuItem) {
          return menuItem.getText().equals("Add Row");
        }
      });
      menuItem.click();
    }
    // Add values for the rows.
    JTableFixture table = dialog.table();
    // Row 0
    table.cell(TableCell.row(0).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(0).column(2)).enterValue("1");
    table.cell(TableCell.row(0).column(3)).enterValue("10");
    table.cell(TableCell.row(0).column(4)).enterValue("%");
    table.cell(TableCell.row(0).column(5)).enterValue("0");
    // Row 1
    table.cell(TableCell.row(1).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(1).column(2)).enterValue("11");
    table.cell(TableCell.row(1).column(3)).enterValue("50");
    table.cell(TableCell.row(1).column(4)).enterValue("%");
    table.cell(TableCell.row(1).column(5)).enterValue("3");
    // Row 2
    table.cell(TableCell.row(2).column(0)).enterValue("R5.1");
    table.cell(TableCell.row(2).column(2)).enterValue("51");
    table.cell(TableCell.row(2).column(3)).enterValue("100");
    table.cell(TableCell.row(2).column(4)).enterValue("%");
    table.cell(TableCell.row(2).column(5)).enterValue("5");

    // Click the ok button
    JButtonFixture okButton = dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton button) {
        return button.getText().equalsIgnoreCase("ok");
      }
    });
    okButton.click();
    //editFrame.normalize();

    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();
    
    System.out.println("testAddVectorCounterPmRes(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {
        // Perform the database assertions
        List<TestTableItem> tables = new Vector<TestTableItem>();
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "AGGREGATION", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "AGGREGATIONRULE", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "BUSYHOURPLACEHOLDERS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "BUSYHOURRANKKEYS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "DATAITEM", null));
//        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
//            "Dataformat", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "UNIVERSECLASS", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTDELTACALCSUPPORT", null));        
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "TPACTIVATION", null));
//        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
//            "Transformer", null));
//        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
//            "Measurementtypeclass", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
//        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
//            "Measurementkey", new String[] { "DESCRIPTION" }));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTVECTOR", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTTABLE", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "MEASUREMENTCOUNTER", new String[] { "DESCRIPTION" }));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "REFERENCECOLUMN", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddVectorCounterPmRes", TestSetupConstants.DB_DWHREP,
            "REFERENCETABLE", null));
        
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
        tables = null;
    } finally {      
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
  	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testAddVectorCounterPmRes(): Done.");
  } // testAddVectorCounterPmRes
  
  /**
   * This test enables DataFormat for existing Test TP
   * @throws Exception
   */
  @org.junit.Test
  public void testEnableDataFormatSupport() throws Exception {
    System.out.println("testEnableDataFormatSupport(): Start.");   
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
  
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();
    
    //Collapse tree if any  node was expanded in previous test.
    CommonUtils.collapseTree(measurementTypesTree, 0);
    
    int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
    
    MeasurementTypesUtils.selectParameters(measurementTypesTree, mtRow);
    PairComponent pairComponent = CommonUtils.findPairComponentWithText("Data Format Support");
    JCheckBox dataFormatSupportCheckBoxTarget = (JCheckBox) pairComponent.getComponent();
    JCheckBoxFixture dataFormatSupportCheckBox = new JCheckBoxFixture(robot, dataFormatSupportCheckBoxTarget);
    if (dataFormatSupportCheckBox.check() == null){
    	dataFormatSupportCheckBox.click();
    } else {
    	dataFormatSupportCheckBox.click();
    	dataFormatSupportCheckBox.click();
    }
    

    // Save the changes
    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();
    
    System.out.println("testEnableDataFormatSupport(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {
      // Perform the database assertions
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "DATAITEM", null));    
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
              TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
//          TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", new String[] { "ORDERNO", "CONFIG" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
      
     
      DatabaseTester.testTableList(tables);
      //String[] modifiedTables = DatabaseTester.testTableList(tables);
      //DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {      
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testEnableDataFormatSupport(): End.");
  }

  @org.junit.Test
  public void testDisableDataFormatSupport() throws Exception {
    System.out.println("testDisableDataFormatSupport(): Start.");
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
  
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();
    
    //Collapse tree if any  node was expanded in previous test.
    CommonUtils.collapseTree(measurementTypesTree, 0);
    
    int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
    
    MeasurementTypesUtils.selectParameters(measurementTypesTree, mtRow);
    PairComponent pairComponent = CommonUtils.findPairComponentWithText("Data Format Support");
    JCheckBox dataFormatSupportCheckBoxTarget = (JCheckBox) pairComponent.getComponent();
    JCheckBoxFixture dataFormatSupportCheckBox = new JCheckBoxFixture(robot, dataFormatSupportCheckBoxTarget);
    
    //check if DataFormat Support enabled, then disable it
    if (dataFormatSupportCheckBox.check() != null){
    	dataFormatSupportCheckBox.click();
    }

    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();
    
    System.out.println("testDisableDataFormatSupport(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {
      // Perform the database assertions
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "DATAITEM", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
//          TestSetupConstants.DB_DWHREP, "DEFAULTTAGS", null));      
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
//          TestSetupConstants.DB_DWHREP, "TRANSFORMATION", new String[] { "ORDERNO", "CONFIG" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testDisableDataFormatSupport",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      
      DatabaseTester.testTableList(tables);
     String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {      
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testDisableDataFormatSupport(): End.");
  }

  /**
   * Open existing test TechPack for edit 
   * Do some changes in measurement types and 
   * click discard to roll back the changes
   * @throws Exception
   */
  @org.junit.Test
  public void testDiscardMeasurementTypeChanges() throws Exception {
    System.out.println("testDiscardMeasurementTypeChanges(): Start.");
   
    TableModificationLogger logger = TableModificationLogger.instance();
	logger.reset();
	logger.startLogging();
	
	 FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

	 editFrame.tabbedPane().selectTab("Measurement Types");
	  
	 JTreeFixture measurementTypesTree = editFrame.tree();
	 assertThat(measurementTypesTree).isNotNull();
	    
	 //Collapse tree if any  node was expanded in previous test.
	 CommonUtils.collapseTree(measurementTypesTree, 0);
	    
	 int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	 
	 int rowsBeforeDiscard = measurementTypesTree.target.getRowCount();
	    
	 assertThat(editFrame.button("MeasurementViewCancel").target.isEnabled()).isFalse();
	 
//	 MeasurementTypesUtils.selectCounters(measurementTypesTree, mtRow);	    
	 MeasurementTypesUtils.addMeasurementType(robot, measurementTypesTree, TestSetupConstants.TEST_TP_NAME+"_MT2");
    
    assertThat(editFrame.button("MeasurementViewCancel").target.isEnabled()).isTrue();
    editFrame.button("MeasurementViewCancel").click();    
    CommonUtils.waitForBusyIndicator(600000);
    int rowsAfterDiscard = measurementTypesTree.target.getRowCount();
    
    logger.stopLogging();
    
    //Here assertion is made by number of measurement types in the tree
    //after discard number of measurement types should be same as before 
    assertThat(rowsBeforeDiscard == rowsAfterDiscard).as("New Measurement type should not be added after discard");
    assertEquals(rowsAfterDiscard,rowsBeforeDiscard);
    System.out.println("testDiscardMeasurementTypeChanges(): Done.");
  }

  
  /**
   * Open existing techpack, edit measurement type fieds with same values and
   * test that nothing has changed in database when save is pressed.
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testPseudoEditMeasurementType() throws Exception {
    System.out.println("testPseudoEditMeasurementType(): Start.");
    
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);
    editFrame.tabbedPane().selectTab("Measurement Types");

    JTreeFixture measTypesTree = editFrame.tree();
    assertThat(measTypesTree).isNotNull();
    
    TableModificationLogger logger = TableModificationLogger.instance();
	logger.reset();
	logger.startLogging();

    measTypesTree.toggleRow(0);
    measTypesTree.toggleRow(2);

    JPopupMenuFixture columnMenu = measTypesTree.showPopupMenuAt(3);
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    // Close the pop up menu by pressing "ESC" -key
    columnMenu.pressAndReleaseKeys(KeyEvent.VK_ESCAPE);

    Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
    Point measurementTypesTreeLocation = measTypesTree.target.getLocationOnScreen();
    int relativePointerLocationX = pointerLocation.x - measurementTypesTreeLocation.x;
    int relativePointerLocationY = pointerLocation.y - measurementTypesTreeLocation.y;
    Point relativePointerLocation = new Point(relativePointerLocationX, relativePointerLocationY);
    robot.click(measTypesTree.target, relativePointerLocation);
    robot.click(measTypesTree.target, relativePointerLocation);

    TableContainer[] tableContainers = CommonUtils.findTableContainers(measTypesTree.target.getModel());
    JTable keysTableTarget = tableContainers[0].getTable();
    JTableFixture keysTable = new JTableFixture(robot, keysTableTarget);
    JTableCellFixture nameCell = keysTable.cell(TableCell.row(0).column(0));

    // We enter the same value that there already exist
    // to fool that table has changed
    nameCell.enterValue(nameCell.value());

    editFrame.button("MeasurementViewSave").click();
    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();
    CommonUtils.collapseTree(measTypesTree, 0);
   System.out.println("testPseudoEditMeasurementType(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {
      // Perform the database assertions
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURMAPPING", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "DATAITEM", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "DEFAULTTAGS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "EXTERNALSTATEMENT", new String[] { "STATEMENT" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", new String[] { "DESCRIPTION" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTVECTOR", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "SUPPORTEDVENDORRELEASE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "TECHPACKDEPENDENCY", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "TRANSFORMATION", new String[] { "ORDERNO", "CONFIG" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSENAME", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSETABLE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSEJOIN", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSEOBJECT", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERIFICATIONCONDITION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERIFICATIONOBJECT", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testPseudoEditMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERSIONING", new String[] { "LOCKDATE" }));

      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testPseudoEditMeasurementType(): Done.");
  }
  
  /**
   * Calc table test
   * 
   * - Open existing TP for edit - Open measType-tab - Select "Parameters"
   * - Adjust parameters: - Table size = medium - Total aggregation = checked
   * (compulsory) - Element BH support = unchecked - Data Format support =
   * checked - Has 1 object BH support ("BHSUPPORT1") - Classification =
   * carrier1234 - Universe extention = ALL - Description = somedescription1234
   * - Has 1 Delta calc support selected
   * 
   * - Click Save - Click Close
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testChangeTableTypeToCalcTable() throws Exception {
    System.out.println("testChangeTableTypeToCalcTable(): Start.");

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();     
   
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();

	 int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
   
    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, mtRow);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "small", "Calc Table", true, false,
        true, "carrier1234", "ALL", "somedescription1234", new String[] { "BHSUPPORT1" }, null);
   
    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);
    
    logger.stopLogging();
    
    System.out.println("testChangeTableTypeToCalcTable(): Tables modified by this test are: " 
    		+ CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
          TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
         TestSetupConstants.DB_DWHREP, "DATAITEM", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToCalcTable",
         TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
      
      
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
      
    } finally {     
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testChangeTableTypeToCalcTable(): Stop.");
  }

  /**
   * Vector table test
   * 
   * - Open existing TP for edit - Open measType-tab - Select "Parameters"
   * - Adjust parameters: - Table size = extralarge - Total aggregation = checked
   * (compulsory) - Element BH support = unchecked - Data Format support =
   * checked - Has 1 object BH support ("BHSUPPORT1") - Classification =
   * carrier1234 - Universe extention = ALL - Description = somedescription1234
   * - Has 1 Delta calc support selected
   * 
   * - Click Save - Click Close
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testChangeTableTypeToVectorTable() throws Exception {
    System.out.println("testChangeTableTypeToVectorTable(): Start."); 

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();     
   
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();

	 int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" +TestSetupConstants.MEASUREMENT_TYPE_NAME);
    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, 1);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "extralarge", "Vector Table", true, false,
        true, "carrier1234", "ALL", "somedescription1234", new String[] { "BHSUPPORT1" }, null);
   
    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);
    
    logger.stopLogging();
    
    System.out.println("testChangeTableTypeToVectorTable(): Tables modified by this test are: " 
    		+ CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
//            TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
            TestSetupConstants.DB_DWHREP, "DATAITEM", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
//      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));   
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
              TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", null));
//    tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToVectorTable",
//          TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
     
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testChangeTableTypeToVectorTable(): Stop.");
  }
  
  /**
   * Plain table test
   * 
   * - Open existing TP for edit - Open measType-tab - Select "Parameters"
   * - Adjust parameters: - Table size = extralarge - Total aggregation = checked
   * (compulsory) - Element BH support = unchecked - Data Format support =
   * checked - Has 1 object BH support ("BHSUPPORT1") - Classification =
   * carrier1234 - Universe extention = ALL - Description = somedescription1234
   * - Has 1 Delta calc support selected
   * 
   * - Click Save - Click Close
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testChangeTableTypeToPlainTable() throws Exception {
    System.out.println("testChangeTableTypeToPlainTable(): Start.");
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();     
   
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();

	 int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	 
    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, 0);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "small", "Plain", true, false,
        true, "carrier1234", "ALL", "somedescription1234", new String[] { "BHSUPPORT1" }, null);
   
    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);
    
    logger.stopLogging();
    
    System.out.println("testChangeTableTypeToPlainTable(): Tables modified by this test are: " 
    		+ CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "DATAITEM", null));    
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", null));   
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
          
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", new String[] { "DESCRIPTION" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
                  TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
//              TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", new String[] { "ORDERNO", "CONFIG" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testChangeTableTypeToPlainTable",
              TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
      
          DatabaseTester.testTableList(tables);
//      String[] modifiedTables = DatabaseTester.testTableList(tables);
//      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testChangeTableTypeToPlainTable(): Stop.");
  }
  
  /**
   * In this test we create a new Measurement type with Ranking table selected.
   * Click -- save   IDE throws error dialog.
   * Click --- Ok  on dialog 
   * Create 2 more measurement types with Ranking Tables and ObjBHSupport enabled. All the mt:s
   * have shared ObjBHSupport-value. The last mt's table type is "None"
   * 
   * @throws Exception
   */
  @org.junit.Test
  public void testTwoRankingTablesWithBHSupport() throws Exception {
    System.out.println("testTwoRankingTablesWithBHSupport(): Start.");
    
    final String PRODUCTPLACEHOLDERS = "Product BH Placeholders";
    final String CUSTOMPLACEHOLDERS = "Custom BH Placeholders";
    
    FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(robot);

    editFrame.tabbedPane().selectTab("Measurement Types");
    JTreeFixture measurementTypesTree = editFrame.tree();
    assertThat(measurementTypesTree).isNotNull();
    CommonUtils.collapseTree(measurementTypesTree, 0);
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();   
    
    // Create first Measurement Type
    MeasurementTypesUtils.addMeasurementType(robot, measurementTypesTree, TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_1BH");
  
    TreePath pathOfMT1 = measurementTypesTree.target.getSelectionPath();
    assertThat(pathOfMT1.toString().equals("[root, " + TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_1BH]")); 
    
    final int MT1_ROW = measurementTypesTree.target.getRowForPath(pathOfMT1);
    
    JPanelFixture parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, MT1_ROW);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "medium", "Ranking Table", true, false,
        true, "Carrier2", "ALL", "someDescription1", new String[] { "objbhsup1" }, null);

    PairComponent productPlaceholders;
    PairComponent customPlaceholders;
    productPlaceholders = CommonUtils.findPairComponentWithText(PRODUCTPLACEHOLDERS);
    assertThat(productPlaceholders.isVisible()).as(PRODUCTPLACEHOLDERS + " should be visible.").isTrue();
    assertThat(productPlaceholders.isEnabled()).as(PRODUCTPLACEHOLDERS + " should be enabled.").isTrue();
    customPlaceholders = CommonUtils.findPairComponentWithText(CUSTOMPLACEHOLDERS);
    assertThat(customPlaceholders.isVisible()).as(CUSTOMPLACEHOLDERS + " should be visible.").isTrue();
    assertThat(customPlaceholders.isEnabled()).as(CUSTOMPLACEHOLDERS + " should be enabled.").isTrue();

    MeasurementTypesUtils.selectKeys(measurementTypesTree, MT1_ROW);
    MeasurementTypesUtils.addMeasurementKey(robot, measurementTypesTree, MT1_ROW, "KEY1", "varchar", "100", true);

    // Commented out: no counters allowed for ranking tables.
    
    //Measurement Type 2
    MeasurementTypesUtils.addMeasurementType(robot, measurementTypesTree, TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_2BH");
     
    TreePath pathOfMT2 = measurementTypesTree.target.getSelectionPath();
    assertThat(pathOfMT2.toString().equals("[root, " + TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_2BH]")); 
    
    final int MT2_ROW = measurementTypesTree.target.getRowForPath(pathOfMT2);
    
    parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, MT2_ROW);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "medium", "Ranking Table", true, false,
        true, "Carrier2", "ALL", "someDescription2", new String[] { "objbhsup2" }, null);

    MeasurementTypesUtils.selectKeys(measurementTypesTree, MT2_ROW);
    MeasurementTypesUtils.addMeasurementKey(robot, measurementTypesTree, MT2_ROW, "KEY2", "varchar", "100", true);
    // Commented out: no counters allowed for ranking tables.
    
    //Measurement Type 3
    MeasurementTypesUtils.addMeasurementType(robot, measurementTypesTree, TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_2");
    
    TreePath pathOfMT3 = measurementTypesTree.target.getSelectionPath();
    assertThat(pathOfMT3.toString().equals("[root, " + TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME + "_2BH]")); 
    
    final int MT3_ROW = measurementTypesTree.target.getRowForPath(pathOfMT3);
    
    parameterFixture = MeasurementTypesUtils.selectParameters(measurementTypesTree, MT3_ROW);
    MeasurementTypesUtils.setBasicMeasurementypeParams(robot, parameterFixture, "medium", "None", false, false, false,
        "Carrier2", "ALL", "someDescription3", new String[] { "objbhsup1", "objbhsup2" }, null);

    MeasurementTypesUtils.selectKeys(measurementTypesTree, MT3_ROW);
    MeasurementTypesUtils.addMeasurementKey(robot, measurementTypesTree, MT3_ROW, "KEY3", "varchar", "100", true);
    MeasurementTypesUtils.selectCounters(measurementTypesTree, MT3_ROW);
    MeasurementTypesUtils.addMeasurementCounter(robot, measurementTypesTree, MT3_ROW, "COUNTER3", "varchar", "50");

    // placeholder fields should not be visible for non-BH tables (hence will be
    // null as not found)
    productPlaceholders = CommonUtils.findPairComponentWithText(PRODUCTPLACEHOLDERS);
    assertThat(productPlaceholders).as(PRODUCTPLACEHOLDERS + " should not be visible.").isNull();
    customPlaceholders = CommonUtils.findPairComponentWithText(CUSTOMPLACEHOLDERS);
    assertThat(customPlaceholders).as(CUSTOMPLACEHOLDERS + " should not be visible.").isNull();

    editFrame.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);
    
    logger.stopLogging();
    
    System.out.println("testTwoRankingTablesWithBHSupport(): Tables modified by this test are: " 
    		+ CommonUtils.listModifiedTables() + ".");

    try {
      // DATABASE ASSERTION
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "DATAITEM", null));    
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", new String[] { "DESCRIPTION" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
                  TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
//          tables.add(new TestTableItem(this.getClass().getName(), "testEnableDataFormatSupport",
//              TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", new String[] { "ORDERNO", "CONFIG" }));
          tables.add(new TestTableItem(this.getClass().getName(), "testTwoRankingTablesWithBHSupport",
              TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));

      
      
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      
    } finally {      
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }

    System.out.println("testTwoRankingTablesWithBHSupport(): Stop.");
  }
  
  /**
   * Test copies Test TechPack DC_E_TEST. open new TP for edit.
   * Finds measurement type created in addMeasurementType test 
   * and deletes this measurement type.
   * @throws Exception
   */
  @org.junit.Test
  public void testRemoveMeasurementType() throws Exception {
    System.out.println("testRemoveMeasurementType(): Start.");
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
  
    FrameFixture editTPWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(120000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(editTPWindow).isNotNull();
    System.out.println("testRemoveMeasurementType(): Edit window opened.");

    JTabbedPaneFixture myEditPanel = editTPWindow.tabbedPane();
    assertThat(myEditPanel).isNotNull();
    myEditPanel.selectTab("Measurement Types");
    JTreeFixture measurementTypesTree = editTPWindow.tree();
	 int mtRow = MeasurementTypesUtils.measurementTypeSanityCheck(TestSetupConstants.TEST_TP_NAME + "_" + TestSetupConstants.MEASUREMENT_TYPE_NAME);
	 
    measurementTypesTree.selectRow(mtRow);
    String selectionPath = measurementTypesTree.target.getSelectionPath().toString();
    assertThat(selectionPath.equals("["+TestSetupConstants.TEST_SERVER+","+ TestSetupConstants.TEST_TP_NAME + "," + TestSetupConstants.MEASUREMENT_TYPE_NAME+"]"));
    JPopupMenuFixture popupMenu = measurementTypesTree
        .showPopupMenuAt(measurementTypesTree.target.getSelectionRows()[0]);
    popupMenu.focus().menuItemWithPath("Remove Element").click();

    DialogFixture dialog = WindowFinder.findDialog(JDialog.class).using(TechPackIdeStarter.getMyRobot());
    dialog.optionPane().yesButton().click();

    System.out.println("testRemoveMeasurementType(): Measurement type removed.");
    editTPWindow.button("MeasurementViewSave").click();

    CommonUtils.waitForBusyIndicator(120000);    
    
    logger.stopLogging();
    
    // Perform the database assertions
    System.out.println("testRemoveMeasurementType(): Tables modified by this test are: " + CommonUtils.listModifiedTables() + ".");

    try {      
      List<TestTableItem> tables = new Vector<TestTableItem>();
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "AGGREGATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "AGGREGATIONRULE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOUR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURMAPPING", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURPLACEHOLDERS", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "BUSYHOURRANKKEYS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "BUSYHOURSOURCE", null));      
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "DATAITEM", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "DEFAULTTAGS", null));  
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "EXTERNALSTATEMENT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", new String[] { "DESCRIPTION" }));      
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTDELTACALCSUPPORT", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTOBJBHSUPPORT", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTABLE", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPE", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
              TestSetupConstants.DB_DWHREP, "MEASUREMENTVECTOR", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "MEASUREMENTTYPECLASS", new String[] { "DESCRIPTION" }));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "SUPPORTEDVENDORRELEASE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "TECHPACKDEPENDENCY", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "TRANSFORMATION", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
          TestSetupConstants.DB_DWHREP, "UNIVERSECLASS", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSECONDITION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSEJOIN", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSENAME", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSEOBJECT", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "UNIVERSETABLE", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERIFICATIONCONDITION", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERIFICATIONOBJECT", null));
//      tables.add(new TestTableItem(this.getClass().getName(), "testRemoveMeasurementType",
//          TestSetupConstants.DB_DWHREP, "VERSIONING", new String[] { "LOCKDATE" }));
//      
    
      String[] modifiedTables = DatabaseTester.testTableList(tables);
      DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
      tables = null;
    } finally {     
    	assertThat(techPackEditWindow.button("MeasurementViewSave").target.isEnabled()).isFalse();
    	  assertThat(techPackEditWindow.button("MeasurementViewClose").target.isEnabled());
    }
    System.out.println("testRemoveMeasurementType(): Done.");
  }
  
  //****************************
  //	Helper Methods
  //****************************

  
  /**
   * A helper method for creating a new techpack and opening the edit window.
   * 
   * @throws Exception
   */
  public static void openEditTPPanel(String tpPath) throws Exception {
    System.out.println("openEditTPPanel(): Start.");

    // Select the Manage TechPack tab
    techPackEditPanel.selectTab("Manage TechPack");

    JTreeFixture techPackTree = techPackEditWindow.tree("TechPackTree");
    assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");

    Pause.pause(5000);

//    String tpTreePath = TestSetupConstants.TEST_SERVER + "/" + TECHPACK_NAME + "/" + TECHPACK_NAME + ":"
//        + TECHPACK_VERSION;
    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");
   
    try {
    	System.out.println("Trying to select treepath=" + tpPath);
       techPackTree.selectPath(tpPath);
    } catch (LocationUnavailableException lue) {
    	System.out.println("openEditTPPanel():Test TP " + TestSetupConstants.TEST_TP_NAME +" does not exist.");    	
    }
    assertThat(techPackTree.target.getSelectionPath()).as("Sanity check passed: Test techpack should exist: "+techPackTree.target.getSelectionPath()).isNotNull();
        
    Pause.pause(3000);

    techPackEditWindow = CommonUtils.openTechPackEditWindow(tpPath, 60000);
    assertThat(techPackEditWindow).isNotNull().as("Opening edit window failed.");

    techPackEditPanel = techPackEditWindow.tabbedPane();

    System.out.println("openEditTPPanel(): Done.");
  }
  
  
  private void renameLastRow(final String newName, final JTable newTableTarget) {
	  // Get Last row & rename
	  JTableFixture newJTableFixture = new JTableFixture(robot, newTableTarget);
	  final int newRowNumber = newJTableFixture.rowCount() > 0 ? newJTableFixture.rowCount() - 1 : 0;
	  TableCell newNameCell = TableCell.row(newRowNumber).column(0);
	  JTableCellFixture newNameCellFixture = newJTableFixture.cell(newNameCell);
	  newNameCellFixture.enterValue(newName);
  } // renameLastRow
  

	private static void lockSelectedTechPack() {
		// Lock the techpack, if not already locked.
	    if (TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()) {
	      TechPackIdeStarter.getMyWindow().button("TechPackLock").click();
	      System.out.println("lockSelectedTechPack(): Techpack locked.");
	    } else {
	      System.out.println("lockSelectedTechPack(): Techpack was already locked.");
	    }
	} // lockSelectedTechPack
	
	/**
	   * testDuplicateKeyCounter Helper method 
	   * @param measurementTypesTree
	   * @param tableTarget
	   */
	  private void duplicateRows(final JTreeFixture measurementTypesTree, final JTable tableTarget) {
		  JTableFixture jTableFixture = new JTableFixture(robot, tableTarget);
		  final int editRowNumber = jTableFixture.rowCount() > 0 ? jTableFixture.rowCount() - 1 : 0;
		  TableCell nameCell = TableCell.row(editRowNumber).column(0);		
		  JTableCellFixture nameCellFixture = jTableFixture.cell(nameCell);
		  // Add measurement type counter
		  nameCellFixture.click(); // Select/Highlight row in table.
		  JPopupMenuFixture popupMenu = jTableFixture.showPopupMenuAt(nameCell);
		  assertThat(popupMenu).isNotNull().as("Could not pop up menu for cell");
		  JMenuItemFixture duplicateItem = popupMenu.menuItemWithPath("Advanced", "Duplicate Row");
		  assertThat(duplicateItem).isNotNull().as("Menu item \"Advanced\" Duplicate Row could not be found");
		  duplicateItem.click();
		  // Find the Duplicate Row dialog & Press OK
		  DialogFixture duplicateRowDialog = CommonUtils.findDialogWithTitleName(robot, "Duplicate Row");
		  assertThat(duplicateRowDialog).isNotNull().as("Could not find Duplicate Row dialog.");
		  duplicateRowDialog.textBox().setText("2"); // Create duplicates
		  JButtonFixture okButton = duplicateRowDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {
		      @Override
		      protected boolean isMatching(JButton button) {
		        return button.getText().equalsIgnoreCase("ok");
		      }
		  	});
		  okButton.click();
		  
	  } // duplicateRows
  
	  /**
	   * Checks if the given techPack exists in the tree.
	   * IF NOT it creates a new techPack and selects it.
	   * @param techPackPath
	   */
  private static void techPackSanityCheck(String techPackPath) {
	  
	  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	  JTreeFixture techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	  assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");
	   
	  TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

	  techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
	  assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");
	   
	  try {
		  techPackTree.target.clearSelection();
		  System.out.println("techPackSanityCheck(): Trying to select treepath=" + techPackPath);    
		  techPackTree.selectPath(techPackPath);
		  lockSelectedTechPack();
	  } catch (LocationUnavailableException lue) {
	      System.out.println("techPackSanityCheck(): Sanity check failed. " + "New techpack does not exist.");
		}
	    
	    if (techPackTree.target.getSelectionPath() == null) {	
	    	System.out.println("techPackSanityCheck(): Creating Test TechPack copy....");
	    	techPackTree.selectPath(TestSetupConstants.TEST_SERVER);
	    	CommonUtils.copyTechPack(TestSetupConstants.TEST_TP_PATH, 60000, TestSetupConstants.TP_NEW_VERSION,
	    			TestSetupConstants.BASE_TP_VERSIONID);
	    }
	    techPackTree.selectPath(techPackPath);
	    String selectionPath = techPackTree.target.getSelectionPath().toString();
	    assertThat(selectionPath.equals("["+TestSetupConstants.TEST_SERVER+","+ TestSetupConstants.TEST_TP_NAME + "," + TestSetupConstants.TEST_TP_NAME +":"+TestSetupConstants.TP_NEW_VERSION));
	  
  }
  
  
  
}
