package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import junit.framework.JUnit4TestAdapter;

import org.fest.assertions.Fail;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.data.TableCell;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JMenuItemFixture;
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
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;

import com.ericsson.eniq.techpacksdk.unittest.fest.TTTableCellWriter;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

/**
 * This test class contains unit test cases for the TechPackIDE: Manage External
 *   Statements View
 */
public class ExternalStatementsViewTest { 
  
  private static FrameFixture myEditWindow = null;

  private static JTabbedPaneFixture myViewPanel = null; 
  
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ExternalStatementsViewTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: ExternalStatementViewTest");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

    TechPackIdeStarter starter = TechPackIdeStarter.getInstance();
    if (starter.startTechPackIde() == false) {
      System.out.println("setUpBeforeClass(): IDE startup failed");
      Fail.fail("TechPackIDE failed to start");    }

    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(tpTree).isNotNull().as("TechPackTree should not be null");
    //CommonUtils.refreshIde();    
    tpTree.selectPath(TestSetupConstants.TEST_SERVER); 
    String path = TestSetupConstants.TEST_SERVER + "/" + TestSetupConstants.TEST_TP_NAME + "/" + TestSetupConstants.TEST_TP_NAME + ":" + TestSetupConstants.TP_NEW_VERSION;   
    copyAndSelectTechPack(path);
    
    System.out.println("setUpBeforeClass(): Click edit.");
    CommonUtils.refreshIde();  
    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    myEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(myEditWindow).isNotNull().as("The edit window should be shown.");

    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible.");
    System.out.println("setUpBeforeClass(): Click External Statements Tab.");
    myViewPanel.selectTab("External Statements");
    System.out.println("setUpBeforeClass(): Pausing IDE to load External Statements Tab.");
    Pause.pause(8000);    

    assertThat(myEditWindow.button("ExternalStatementsViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ExternalStatementsViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ExternalStatementsViewClose").target.isEnabled()).isTrue();

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
      myEditWindow.button("ExternalStatementsViewClose").click();
   }

    System.out.println("tearDownAfterClass(): Pausing " + "for 3 seconds after closing.");
    Pause.pause(3000);

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
   * Tests creation of a new empty statement, and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testAddEmptyExternalStatementAndSave() throws Exception {
    System.out.println("testAddEmptyExternalStatementAndSave(): Start.");
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("External Statements");   
    
    myEditWindow.maximize();    
 
    JTableFixture statementTable = myEditWindow.table("ExternalStatementTable");
    statementTable.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
    JPopupMenuFixture columnMenu = statementTable.tableHeader().showPopupMenuAt(0); 
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("addemptyes");
    assertThat(addRowItem).isNotNull().as("Menu item \"addemptyes\" could not be found");
    addRowItem.click();    
    
    JTableCellFixture statementCell = statementTable.cell(TableCell.row(0).column(2)); 
    statementCell.startEditing();
    
    JButtonFixture bFix = myEditWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {

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
    
    DialogFixture editDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {

        @Override
        protected boolean isMatching(JDialog dialog) {
          return dialog.getTitle().equalsIgnoreCase("statement");
        }
      }).withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
    editDialog.textBox().enterText(TestSetupConstants.statement);    
    Pause.pause(2000);
    editDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("ok");
        }
      }).click();
    statementCell.stopEditing();
    
    //Edit-> Name of statement
    JTableCellFixture nameCell = statementTable.cell(TableCell.row(0).column(0));
    nameCell.startEditing();
    JButtonFixture nameEditButton = myEditWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton b) {
          if ("...".equals(b.getText()) && b.isVisible()) {
            return true;
          } else {
            return false;
          }
        }
      });
    nameEditButton.click();
    
    DialogFixture nameDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {

        @Override
        protected boolean isMatching(JDialog dialog) {
          return dialog.getTitle().equalsIgnoreCase("Name");
        }
      }).withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
    nameDialog.textBox().enterText("testStatement");    
    Pause.pause(2000);
    nameDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("ok");
        }
      }).click();
    nameCell.stopEditing();
    
    JTableCellFixture connectionCell = statementTable.cell(TableCell.row(0).column(1));
    connectionCell.enterValue("dwhrep");
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    //verify the buttons status and click save.
    clickSave();    
    CommonUtils.waitForBusyIndicator(10000); 
    logger.stopLogging();
  
    System.out.println("testAddEmptyExternalStatementAndSave(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
    
    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testAddEmptyExternalStatementAndSave", TestSetupConstants.DB_DWHREP,
        "EXTERNALSTATEMENT", null)); 
    tables.add(new TestTableItem(this.getClass().getName(), "testAddEmptyExternalStatementAndSave", 
    	TestSetupConstants.DB_DWHREP,"TPACTIVATION", null));
    String [] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;

    System.out.println("testAddEmptyExternalStatementAndSave(): Done.");
  }
  
  /**
   * Tests edit one External Statement, 
   * and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testEditStatementAndSave() throws Exception {
    System.out.println("testEditStatementAndSave(): Start.");
   
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("External Statements");    
    JTableFixture statementTable = myEditWindow.table("ExternalStatementTable");   
    
    JTableCellFixture connectionCell = statementTable.cell(TableCell.row(0).column(1)); 
    String connection = connectionCell.value();
    connectionCell.enterValue(connection);    
    Pause.pause(2000);
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    //verify buttons status 
    //click- Save    
    clickSave();
    
    CommonUtils.waitForBusyIndicator(5000);
    logger.stopLogging();
    
    System.out.println("testEditStatementAndSave(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");
    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testEditStatementAndSave", 
    	TestSetupConstants.DB_DWHREP,"EXTERNALSTATEMENT", null)); 
    tables.add(new TestTableItem(this.getClass().getName(), "testEditStatementAndSave", 
    	TestSetupConstants.DB_DWHREP,"TPACTIVATION", null));
    
    String [] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;
    
    System.out.println("testEditStatementAndSave(): Done.");

    
  }
  
  /**
   * Tests edit one External Statement, 
   * and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testEditStatementAndDiscard() throws Exception {
    System.out.println("testEditStatementAndDiscard(): Start.");
   
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("External Statements");    
    JTableFixture statementTable = myEditWindow.table("ExternalStatementTable");   
    
    JTableCellFixture nameCell = statementTable.cell(TableCell.row(0).column(0)); 
    statementTable.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
    String name = nameCell.value();
    nameCell.startEditing();
    JButtonFixture nameEditButton = myEditWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton b) {
          if ("...".equals(b.getText()) && b.isVisible()) {
            return true;
          } else {
            return false;
          }
        }
      });
    nameEditButton.click();
    
    DialogFixture nameDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {

        @Override
        protected boolean isMatching(JDialog dialog) {
          return dialog.getTitle().equalsIgnoreCase("Name");
        }
      }).withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
    nameDialog.textBox().enterText("change name discarding");    
    Pause.pause(2000);
    nameDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("ok");
        }
      }).click();
    nameCell.stopEditing();  
    Pause.pause(2000);  
    
    //verify buttons status 
    //click- Save    
    clickCancel();    
    CommonUtils.waitForBusyIndicator(5000);
   
    //Assertion
    String afterEdit = nameCell.value();
    
    assertEquals(afterEdit,name);
    
    System.out.println("testEditStatementAndDiscard(): Done.");

    
  }
  
  /**
   * Tests deletion of External Statement, 
   * and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testRemoveExternalStatementAndSave() throws Exception {
    System.out.println("testRemoveExternalStatementAndSave(): Start.");
   
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("External Statements");    
    JTableFixture statementTable = myEditWindow.table("ExternalStatementTable");   
    
    JTableCellFixture nameCell = statementTable.cell(TableCell.row(0).column(0)); 
    nameCell.select();   
    JPopupMenuFixture columnMenu = nameCell.showPopupMenu(); 
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture removeES = columnMenu.menuItemWithPath("removees");
    assertThat(removeES).isNotNull().as("Menu item \"removees\" could not be found");
    
    removeES.click();
    
    Pause.pause(2000);
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    //verify buttons status 
    //click- Save    
    clickSave();
    
    CommonUtils.waitForBusyIndicator(5000);
    logger.stopLogging();
    
    System.out.println("testRemoveExternalStatementAndSave(): Tables modified are: " 
  		  + CommonUtils.listModifiedTables() + ".");
    
    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveExternalStatementAndSave", 
    	TestSetupConstants.DB_DWHREP,"EXTERNALSTATEMENT", null)); 
    tables.add(new TestTableItem(this.getClass().getName(), "testRemoveExternalStatementAndSave", 
    	TestSetupConstants.DB_DWHREP,"TPACTIVATION", null));
    String [] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;
    
    System.out.println("testRemoveExternalStatementAndSave(): Done.");

    
  }
  
   
  // ///////////////////////////
  // HELPER METHODS
  // ///////////////////////////

  /**
   * Clicks the save button in the Transformers view window.
   */
  private void clickSave() {
    System.out.println("clickSave()");

    assertThat(myEditWindow.button("ExternalStatementsViewSave").target.isEnabled());

    assertThat(myEditWindow.button("ExternalStatementsViewCancel").target.isEnabled());

    assertThat(myEditWindow.button("ExternalStatementsViewClose").target.isEnabled());   
    
    //click Save
    myEditWindow.button("ExternalStatementsViewSave").click();  

    Pause.pause(3000);
    
    assertThat(myEditWindow.button("ExternalStatementsViewSave").target.isEnabled()).isFalse();
    
    assertThat(myEditWindow.button("ExternalStatementsViewCancel").target.isEnabled()).isFalse();

    assertThat(myEditWindow.button("ExternalStatementsViewClose").target.isEnabled());
  }

  /**
   * Clicks the cancel button in the Transformers view window.
   */
  private void clickCancel() {
    System.out.println("clickCancel()");
    assertThat(myEditWindow.button("ExternalStatementsViewSave").target.isEnabled());
    assertThat(myEditWindow.button("ExternalStatementsViewCancel").target.isEnabled());
    assertThat(myEditWindow.button("ExternalStatementsViewClose").target.isEnabled());

    myEditWindow.button("ExternalStatementsViewCancel").click();

    Pause.pause(2000);   

    assertThat(myEditWindow.button("ExternalStatementsViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ExternalStatementsViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ExternalStatementsViewClose").target.isEnabled());
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
	      System.out.println("copyAndSelectTechPack():New techpack path "+tp +" does not exist.");
		}
	    
	    if (techPackTree.target.getSelectionPath() == null) {	
	    	System.out.println("copyAndSelectTechPack(): Creating Test TechPack....");
	    	techPackTree.selectPath(TestSetupConstants.TEST_SERVER);
	    	CommonUtils.copyTechPack(TestSetupConstants.TEST_TP_PATH, 30000,TestSetupConstants.TP_NEW_VERSION,
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
