/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.JOptionPaneFinder;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTableHeaderFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;
import tableTreeUtils.PairComponent;
import tableTreeUtils.TableContainer;

import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

/**
 * This test class contains unit test cases for the TechPackIDE: Manage TechPack
 * View.
 *  
 */
public class ReferenceTypesViewTest {

  private String currentElementName;  

  private static FrameFixture myEditWindow = null;

  private static JTabbedPaneFixture myViewPanel = null;

  private static int addedRow = 0;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ReferenceTypesViewTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: ReferenceTypesViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    TechPackIdeStarter starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");    }

    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    
    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);    
    
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;    
    techPackCheck(path);

    // Check and click the edit button
    System.out.println("setUpBeforeClass(): Click edit.");
    CommonUtils.refreshIde();
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
        "Edit button should be enabled.").isTrue();
    TechPackIdeStarter.getMyWindow().button("TechPackEdit").click();
   
    myEditWindow = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(myEditWindow).isNotNull().as("The edit window should be shown.");

    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be included.");

    myViewPanel.selectTab("Reference Types");
    
    //Pause to give IDE time to load tab
    System.out.println("setUpBeforeClass(): Pausing TechPack IDE to give it time to load the Tab" );
    Pause.pause(30000);
    
    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled()).isTrue();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

    System.out.println("setUpBeforeClass(): Done.");
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    System.out.println("tearDownAfterClass(): Start.");

    CommonUtils.stopTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);  

    if (myEditWindow != null) {
      myEditWindow.button("ReferenceViewClose").click();
      myEditWindow = null;
    }
    if (myViewPanel != null){
    	myViewPanel = null;
    }
    
    //TechPackIdeStarter.closeTechPackIde(); 

    System.out.println("tearDownAfterClass(): Done.");
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
	  System.out.println("setUp(): Executing Test Case");
	  System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");	   

    CommonUtils.resetTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    System.out.println("tearDown()");

    CommonUtils.printModifiedTables(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);
    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);

  }

  /**
   * Tests creation of a new reference type, and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testAddReferenceTypeCommit() throws Exception {
    System.out.println("testAddReferenceTypeCommit(): Start.");

    //int nameSalt = (int) Math.round(Math.random() * 999999);
    //this.currentElementName = "ReferenceType" + nameSalt;
    this.currentElementName = "testElement1";
    myEditWindow.maximize();

    addedRow = addElement(myViewPanel, this.currentElementName);

    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");   
   
    //verify the buttons status and click save.
    clickSave();
    
    System.out.println("testAddReferenceTypeCommit(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeCommit", TestSetupConstants.DB_DWHREP,
        "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeCommit", TestSetupConstants.DB_DWHREP,
        "REFERENCECOLUMN", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    myEditWindow.normalize();

    System.out.println("testAddReferenceTypeCommit(): Done.");
  }

  /**
   * Tests creation of a new reference type, but discarding the changes by
   * clicking cancel.
   * 
   * @throws Exception
   */
  @Test
  public void testAddReferenceTypeCancel() throws Exception {
    System.out.println("testAddReferenceTypeCancel: Start.");

    // int nameSalt = (int) Math.round(Math.random() * 999999);
    // this.currentElementName = "testElement" + nameSalt;
    this.currentElementName = "testElement2";
    myEditWindow.maximize();
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    int numberOfComponents = editTree.component().getComponentCount();

    // Clear the previous selection from the tree to get the correct pop-up
    // when adding a new element.
    editTree.target.clearSelection();
//    editTree.selectRow(0);
    addElement(myViewPanel, this.currentElementName);

    clickCancel();

    assertThat(editTree.component().isSelectionEmpty()).as("Selection isn't empty.");
    assertThat(editTree.component().getComponentCount() == numberOfComponents).as("Number of components doens't match");
    
    System.out.println("testAddReferenceTypeCancel(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");
    myEditWindow.normalize();
    System.out.println("testAddReferenceTypeCancel: Done.");
  }

  /**
   * @throws Exception
   */
  @Test
  public void testSaveReferenceTableChanges() throws Exception {
    System.out.println("testSaveReferenceTableChanges: Start.");
    myEditWindow.maximize();
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    editTree.toggleRow(addedRow);
    editTree.toggleRow(addedRow + 1);
    editTree.toggleRow(addedRow + 2);
    editTree.selectRow(addedRow + 2);

    JPanelFixture parameterFixture = new JPanelFixture(TechPackIdeStarter.getMyRobot(), CommonUtils
        .findParameterPanel());
    parameterFixture.textBox().selectAll();
    parameterFixture.textBox().enterText("Description changed!");
    PairComponent p = CommonUtils.findPairComponentWithText("Update Method");
    JComboBox cBox = (JComboBox) p.getComponent();
    JComboBoxFixture cBoxFix = new JComboBoxFixture(TechPackIdeStarter.getMyRobot(), cBox);
    cBoxFix.selectItem(2);

    Pause.pause(1000);

    // HACK: Select row will complete the description change, so that save is
    // enabled.
    editTree.selectRow(addedRow);

    // Save the changes
    clickSave();
    
    System.out.println("testSaveReferenceTableChanges(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testSaveReferenceTableChanges",
        TestSetupConstants.DB_DWHREP, "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testSaveReferenceTableChanges",
        TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testSaveReferenceTableChanges",
            TestSetupConstants.DB_DWHREP, "TPACTIVATION", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    myEditWindow.normalize();
    System.out.println("testSaveReferenceTableChanges: Done.");
  }

  /**
   * Tests addition of a new reference type, but then discarding the changes by
   * clicking cancel.
   * 
   * @throws Exception
   */
  @Test
  public void testDiscardReferenceTypeChanges() throws Exception {
    System.out.println("testDiscardReferenceTypeChanges(): Start.");
    myEditWindow.maximize();
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");
    
    int numberOfComponents = editTree.component().getComponentCount();

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    editTree.toggleRow(addedRow);
    editTree.toggleRow(addedRow + 1);
    editTree.toggleRow(addedRow + 2);
    editTree.selectRow(addedRow + 2);

    JPanelFixture parameterFixture = new JPanelFixture(TechPackIdeStarter.getMyRobot(), CommonUtils
        .findParameterPanel());
    parameterFixture.textBox().selectAll();
    parameterFixture.textBox().enterText("Description changed again!");
   

    // HACK: Select row will complete the description change, so that cancel is
    // enabled.
    editTree.selectRow(addedRow);

    clickCancel();

    // TODO: Add here a verification that the old value is back.
    
    assertThat(editTree.component().isSelectionEmpty()).as("Selection isn't empty.");
    assertThat(editTree.component().getComponentCount() == numberOfComponents).as("Number of components doens't match");
    
    System.out.println("testAddReferenceTypeCancel(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");
    myEditWindow.normalize();
    System.out.println("testDiscardReferenceTypeChanges(): Done.");
  }

  @Test
  public void testAddReferenceColumn() throws Exception {
    System.out.println("testAddReferenceColumn(): Start.");
    myEditWindow.maximize();
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    editTree.toggleRow(addedRow);
    editTree.toggleRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);
    
    TableContainer table = CommonUtils.findTablePanel();
    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");

    JTableFixture columnFixture = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());

    int numberOfRows = columnFixture.component().getRowCount();

    JTableHeaderFixture columnHeaderFixture = new JTableHeaderFixture(TechPackIdeStarter.getMyRobot(), table.getTable()
        .getTableHeader());
    
    JPopupMenuFixture columnMenu = columnHeaderFixture.showPopupMenuAt(1);
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
    addRowItem.click();

    editTree.selectRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);

    columnFixture.enterValue(TableCell.row(numberOfRows).column(0), "name_Attribute2");
    columnFixture.enterValue(TableCell.row(numberOfRows).column(1), "unsigned int");
    columnFixture.enterValue(TableCell.row(numberOfRows).column(5), "false");
    columnFixture.enterValue(TableCell.row(numberOfRows).column(4), "true");

    // save this row for use by the edit/delete cases.
    clickSave();
    
    System.out.println("testAddReferenceColumn(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCECOLUMN", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    myEditWindow.normalize();
    System.out.println("testAddReferenceColumn(): Done.");
  }

  @Test
  public void testEditReferenceColumn() throws Exception {
    System.out.println("testEditReferenceColumn(): Start.");
    myEditWindow.maximize();
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    editTree.toggleRow(addedRow);
    editTree.toggleRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);
    
    TableContainer table = CommonUtils.findTablePanel();
    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");

    JTableFixture columnFixture = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());

    int row = 0;
    
    editTree.selectRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);    
    columnFixture.enterValue(TableCell.row(row).column(1), "integer");

    // save this row for use by the edit/delete cases.
    clickSave();
    
    System.out.println("testEditReferenceColumn(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testEditReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCECOLUMN", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    myEditWindow.normalize();
    System.out.println("testEditReferenceColumn(): Done.");
  }

  @Test
  public void testRemoveReferenceColumn() throws Exception {
    System.out.println("testRemoveReferenceColumn(): Start.");
    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    editTree.toggleRow(addedRow);
    editTree.toggleRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);
    
    TableContainer table = CommonUtils.findTablePanel();
    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");

    JTableFixture columnFixture = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());

    editTree.selectRow(addedRow + 2);
    editTree.toggleRow(addedRow + 3);
    editTree.toggleRow(addedRow + 3);
    editTree.selectRow(addedRow + 3);  
    
    int lastRow = columnFixture.component().getRowCount()-1;
    columnFixture.selectCell(TableCell.row(lastRow).column(0));
    JPopupMenuFixture columnMenu = columnFixture.showPopupMenuAt(TableCell.row(lastRow).column(0));
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture rowItem = columnMenu.menuItemWithPath("Delete Row");
    assertThat(rowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
    rowItem.click();
    
    //confirm remove
    JOptionPaneFixture confirmRemove = JOptionPaneFinder.findOptionPane().withTimeout(40000).using(TechPackIdeStarter.getMyRobot());
    confirmRemove.yesButton().click();
	Pause.pause(3000);

    // save this row for use by the edit/delete cases.
    clickSave();
    
    System.out.println("testRemoveReferenceColumn(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");

    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveReferenceColumn", TestSetupConstants.DB_DWHREP,
        "REFERENCECOLUMN", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    System.out.println("testRemoveReferenceColumn(): Done.");
  }

  @Test
  public void testRemoveReferenceTable() throws Exception {
    System.out.println("testRemoveReferenceTable(): Start.");

    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    CommonUtils.collapseTree(editTree, addedRow);

    editTree.selectRow(addedRow);
    JPopupMenuFixture removeMenu = editTree.showPopupMenuAt(addedRow);
    assertThat(removeMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture removeItem = removeMenu.menuItemWithPath("Remove Element");
    assertThat(removeItem).isNotNull().as("Menu item \"Remove Element\" could not be found");
    removeItem.click();

    final JOptionPaneFixture removeElementWindow = JOptionPaneFinder.findOptionPane().withTimeout(2000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(removeElementWindow).isNotNull().as("There was confirmation dialog for removing the element.");

   
    assertThat(removeElementWindow.yesButton()).isNotNull().as("Yes button wasn't found.");
    removeElementWindow.yesButton().click();

    clickSave();
    
    System.out.println("testRemoveReferenceTable(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");

    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveReferenceTable", TestSetupConstants.DB_DWHREP,
        "REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveReferenceTable", TestSetupConstants.DB_DWHREP,
        "REFERENCECOLUMN", null));
    DatabaseTester.testTableList(tables);
    tables = null;

    System.out.println("testRemoveReferenceTable(): Done.");
  }

  // ///////////////////////////
  // HELPER METHODS
  // ///////////////////////////

  /**
   * Clicks the save button in the reference view window.
   */
  private void clickSave() {
    System.out.println("clickSave()");

    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled());

    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled());

    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled());
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    myEditWindow.button("ReferenceViewSave").click();
    
    logger.stopLogging(); 
    CommonUtils.waitForBusyIndicator(120000);
    
    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled()).isFalse();    
    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled());
  }

  /**
   * Clicks the cancel button in the reference view window.
   */
  private void clickCancel() {
    System.out.println("clickCancel()");
    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled());
    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled());
    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled());

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    myEditWindow.button("ReferenceViewCancel").click();
    CommonUtils.waitForBusyIndicator(120000);

    logger.stopLogging();

    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled());
  }

  /**
   * @param myViewPanel
   *          the tabbed pane to add the element to.
   * @param elementName
   *          the name of the element to add.
   */
  private int addElement(JTabbedPaneFixture myViewPanel, String elementName) {
    System.out.println("addElement(): Start.");
    JPopupMenuFixture rightMenu = myViewPanel.showPopupMenu();
    assertThat(rightMenu).isNotNull().as("A popup should be presented.");

    // Find and click the "Add Element" menu item
    JMenuItemFixture item = rightMenu.menuItemWithPath("Add Element");
    assertThat(item).isNotNull().as("There needs to be an add element item on the popup menu.");
    item.click();

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
    
    DialogFixture nameDialog = new DialogFixture(TechPackIdeStarter.getMyRobot(), targetDialog);
    nameDialog.textBox().deleteText();
    nameDialog.textBox().enterText(elementName);
    nameDialog.button("LimitedSizeTextDialogOkButton").click();

    Pause.pause(500);

    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
//    System.out.println(editTree.component().getSelectionPath());
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");

    // Find the selected row (this will always be the newly added element)
    int[] rows = editTree.component().getSelectionRows();
    assertThat(rows).isNotNull().as("Selection in the edit tree is empty!");

    // selectPath() doesn't work on the tree since the root node is hidden
    // (bug in Swing/FEST?). This is used as a workaround:
    System.out.println("Selecting row " + rows[0]);    
    editTree.toggleRow(rows[0]);
    editTree.toggleRow(rows[0] + 1);
    editTree.selectRow(rows[0] + 2);
    JPanelFixture parameterFixture = new JPanelFixture(TechPackIdeStarter.getMyRobot(), CommonUtils
        .findParameterPanel());
    parameterFixture.textBox().enterText("Test description");

    // Repeat the workaround for the columns node.
    editTree.toggleRow(rows[0] + 3);
    editTree.selectRow(rows[0] + 4);
    TableContainer table = CommonUtils.findTablePanel();
    assertThat(table).isNotNull().as("The TableContainer of the tree could not be found.");

    JTableHeaderFixture columnHeaderFixture = new JTableHeaderFixture(TechPackIdeStarter.getMyRobot(), table.getTable()
        .getTableHeader());
    JPopupMenuFixture columnMenu = columnHeaderFixture.showPopupMenuAt(1);
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
    addRowItem.click();

    JTableFixture columnFixture = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());

    editTree.selectRow(rows[0] + 3);
    editTree.toggleRow(rows[0] + 3);
    editTree.toggleRow(rows[0] + 3);
    editTree.selectRow(rows[0] + 4);
    columnFixture.enterValue(TableCell.row(0).column(0), "name_" + elementName);
    columnFixture.enterValue(TableCell.row(0).column(1), "unsigned int");
    columnFixture.enterValue(TableCell.row(0).column(5), "true");
    columnFixture.enterValue(TableCell.row(0).column(4), "true");

    // save this row for use by the edit/delete cases.

    System.out.println("addElement(): Done.");
    return rows[0];
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
