package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.JUnit4TestAdapter;
import org.fest.assertions.Fail;
import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
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
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;
import tableTree.TableTreeComponent;
import tableTreeUtils.TableContainer;

import com.ericsson.eniq.techpacksdk.unittest.fest.TTTableCellWriter;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;

/**
 * This test class contains unit test cases for the TechPackIDE: Data Formats View 
 *  
 */
public class DataFormatsViewTest {
  
  private static String df1 = "ascii";
  
  private static String df2 = "csexport";   
  
  private static FrameFixture myEditWindow = null;

  private static JTabbedPaneFixture myViewPanel = null;

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(DataFormatsViewTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: DataFormatsViewTest");
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

    myViewPanel.selectTab("Data Formats");
    
    //Pause to give IDE time to load tab
    System.out.println("setUpBeforeClass(): Pausing TechPack IDE to give it time to load the Tab" );
    Pause.pause(30000);
    
    assertThat(myEditWindow.button("DataFormatSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("DataFormatCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("DataFormatClose").target.isEnabled()).isTrue();

    CommonUtils.startTableLogging(TestSetupConstants.TABLE_CHANGE_LOGGING_FLAG);
    myEditWindow.maximize();
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
      myEditWindow.button("DataFormatClose").click();
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
   * Test add new data formats ascii and csexport for multiple parsers
   * @throws Exception
   */
  @Test
  public void testAddDataFormats() throws Exception {
    System.out.println("testAddDataFormats(): Start."); 
    
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");
    
    myViewPanel.selectTab("Data Formats"); 
    
    JTreeFixture dataFormatsTree = myEditWindow.tree("DataFormatTree");
    assertThat(dataFormatsTree).isNotNull().as("DataFormatsTree should not be null"); 
    
    dataFormatsTree.target.setRootVisible(true);
    
    
    addElement(myViewPanel, df1);
    addElement(myViewPanel, df2);

    //verify the buttons status and click save.
    clickSave();   
   
    System.out.println("testAddDataFormats(): Tables modified are: "+ CommonUtils.listModifiedTables() + ".");
    
    System.out.println("testAddDataFormats(): Done.");
  }

  /**
   * Tests modify one of the DataFormats and save changes
   * @throws Exception
   */
  @Test
  public void testModifyDataFormats() throws Exception {
    System.out.println("testModifyDataFormats: Start.");
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");
    
    myViewPanel.selectTab("Data Formats"); 
    
    JTreeFixture dataFormatsTree = myEditWindow.tree("DataFormatTree");
    assertThat(dataFormatsTree).isNotNull().as("DataFormatsTree should not be null"); 
    
    JTableFixture tableFix = getDataFormatsTable(dataFormatsTree, df1);
	    
	//final int editRowNumber = tableFix.rowCount() > 0 ? tableFix.rowCount() - 1 : 0;
	    
	JTableCellFixture targetCell = tableFix.cell(TableCell.row(0).column(1));
	Component cellEditor = targetCell.editor();
	JTextComponentFixture editorFixture = new JTextComponentFixture(TechPackIdeStarter.getMyRobot(), (JTextField) cellEditor);
	targetCell.startEditing();
	String DataID = editorFixture.target.getText();
	editorFixture.deleteText();	
	editorFixture.enterText("Test_DataID").pressAndReleaseKeys(KeyEvent.VK_ENTER);
	targetCell.stopEditing();

	// Save Changes 
	clickSave();	
 
    System.out.println("testModifyDataFormats(): Tables modified are: " + CommonUtils.listModifiedTables() + ".");
    
    //DB Assertions
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testModifyDataFormats", TestSetupConstants.DB_DWHREP,
    		"MEASUREMENTKEY", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testModifyDataFormats", TestSetupConstants.DB_DWHREP,
    		"MEASUREMENTCOLUMN", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testModifyDataFormats", TestSetupConstants.DB_DWHREP,
    		"DATAITEM", null));
    DatabaseTester.testTableList(tables);
    tables = null;
    
    System.out.println("testModifyDataFormats: Done.");
  }

  /**
   * NOTE: This test is specific to ENIQ-Events
   * This test adds a new Reference Type 
   * and save the changes 
   * This should not change the DATAIDs of Data Formats 
   * thats are modified manually in previous test.
   * @throws Exception
   */
  @Test
  public void testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS() throws Exception {
    System.out.println("testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS: Start.");
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");
    
    myViewPanel.selectTab("Reference Types"); 
    
    System.out.println("setUpBeforeClass(): Pausing TechPack IDE to give it time to load the Tab" );
    Pause.pause(10000);
    
    JTreeFixture refTypeTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(refTypeTree).isNotNull().as("ReferenceTypeTree should not be null");
    
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
    nameDialog.textBox().enterText("testDataFormatRefType");
    nameDialog.button("LimitedSizeTextDialogOkButton").click();

    Pause.pause(5000);

    JTreeFixture editTree = myEditWindow.tree("ReferenceTypeTree");
    assertThat(editTree).isNotNull().as("ReferenceTypeTree should not be null");
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    // Save the changes
    assertThat(myEditWindow.button("ReferenceViewSave").target.isEnabled());
    assertThat(myEditWindow.button("ReferenceViewCancel").target.isEnabled());
    assertThat(myEditWindow.button("ReferenceViewClose").target.isEnabled());    
    
    myEditWindow.button("ReferenceViewSave").click();   
    CommonUtils.waitForBusyIndicator(120000);
    logger.stopLogging(); 
    
    System.out.println("testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS(): Tables modified are: " + CommonUtils.listModifiedTables() + ".");
    
    //Assertion: verify that DATAID changed in the above test case remains the same.
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");
    
    myViewPanel.selectTab("Data Formats"); 
    
    JTreeFixture dataFormatsTree = myEditWindow.tree("DataFormatTree");
    assertThat(dataFormatsTree).isNotNull().as("DataFormatsTree should not be null"); 
    
    dataFormatsTree.selectPath(dataFormatsTree.target.getModel().getRoot().toString()
			+dataFormatsTree.separator()+df1);
    dataFormatsTree.toggleRow(dataFormatsTree.target.getSelectionRows()[0]);
    
    JTableFixture tableFix = getDataFormatsTable(dataFormatsTree, df1);
    
	//final int editRowNumber = tableFix.rowCount() > 0 ? tableFix.rowCount() - 1 : 0;
	    
	JTableCellFixture targetCell = tableFix.cell(TableCell.row(0).column(1));
	Component cellEditor = targetCell.editor();
	JTextComponentFixture editorFixture = new JTextComponentFixture(TechPackIdeStarter.getMyRobot(), (JTextField) cellEditor);	
	assertThat("Test_DataID".equals(editorFixture.target.getText())).as("Data ID must not change after its been changed manually");
	
	//Database tables assertions
	List<TestTableItem> tables = new Vector<TestTableItem>();
    
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"DATAITEM", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"REFERENCECOLUMN", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"REFERENCETABLE", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"DATAFORMAT", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"TRANSFORMER", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS", 
    		TestSetupConstants.DB_DWHREP,"TPACTIVATION", null));
    
    DatabaseTester.testTableList(tables);
    tables = null;
	System.out.println("testAddReferenceTypeAndVerifyDATAIDsForENIQ_EVENTS: Done.");
    
  }
  
  // ///////////////////////////
  // HELPER METHODS
  // ///////////////////////////

  /**
   * Clicks the save button in the reference view window.
   */
  private void clickSave() {
    System.out.println("clickSave()");

    assertThat(myEditWindow.button("DataFormatSave").target.isEnabled());

    assertThat(myEditWindow.button("DataFormatCancel").target.isEnabled());

    assertThat(myEditWindow.button("DataFormatClose").target.isEnabled());
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    myEditWindow.button("DataFormatSave").click();
    
    logger.stopLogging(); 
    CommonUtils.waitForBusyIndicator(120000);
    
    assertThat(myEditWindow.button("DataFormatSave").target.isEnabled()).isFalse();    
    assertThat(myEditWindow.button("DataFormatCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("DataFormatClose").target.isEnabled());
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
  private static void addElement(JTabbedPaneFixture myViewPanel, String elementName) {
    System.out.println("addElement(): Start.");
    JPopupMenuFixture rightMenu = myViewPanel.showPopupMenu();
    assertThat(rightMenu).isNotNull().as("A popup should be presented.");

    // Find and click the "Add Element" menu item
    JMenuItemFixture item = rightMenu.menuItemWithPath("Add Node");
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
    JButtonFixture okButton = nameDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

        @Override
        protected boolean isMatching(JButton button) {
          return button.getText().equalsIgnoreCase("ok");
        }
      });
    okButton.click();

    CommonUtils.waitForBusyIndicator(60000);        
    assertThat(myEditWindow.button("DataFormatSave").target.isEnabled());
    assertThat(myEditWindow.button("DataFormatCancel").target.isEnabled());

    JTreeFixture editTree = myEditWindow.tree("DataFormatTree");
//    System.out.println(editTree.component().getSelectionPath());
    assertThat(editTree).isNotNull().as("DataFormatTree should not be null");
    
    System.out.println("addElement(): Done.");
    
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
  
  private static JTableFixture getDataFormatsTable(JTreeFixture dataFormatsTree, String dfNode) { 	
	    
	    dataFormatsTree.target.setRootVisible(true);

	    try {
	    	System.out.println("Selecting Data formats node "+dfNode);
	    	dataFormatsTree.target.clearSelection();
	    	dataFormatsTree.selectPath(dataFormatsTree.target.getModel().getRoot().toString()
					+dataFormatsTree.separator()+dfNode);
	    } catch (LocationUnavailableException lue) {
		      System.out.println("Data Formats Node" +dfNode+ "does not exist");
		}
	    
	    if (dataFormatsTree.target.getSelectionPath() == null){
	    	  addElement(myViewPanel, dfNode);
	    }
	    
	    int[] slectedRow = dataFormatsTree.target.getSelectionRows();
	    assertThat(slectedRow).isNotNull().as("At least one node should be selected to get its row number");
	    dataFormatsTree.toggleRow(slectedRow[0]);
	    dataFormatsTree.toggleRow(slectedRow[0]+1);
	    dataFormatsTree.component().setSelectionRow(slectedRow[0]+1);
	    dataFormatsTree.component().startEditingAtPath(dataFormatsTree.component().getSelectionPath());
		   
	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) dataFormatsTree.target.getLastSelectedPathComponent();
		JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
		assertThat(ttcFix).isNotNull().as("The TTC for Measurement Type node is not found.");
		    
		ttcFix.selectRow(0);
		// int[] row = dataFormatsTree.target.getSelectionRows();
		ttcFix.toggleRow(ttcFix.target.getSelectionRows()[0]);
		int dfRow = ttcFix.target.getSelectionRows()[0]+2;	  
		ttcFix.toggleRow(dfRow);	    
		Pause.pause(500);
		ttcFix.selectRow(dfRow);
		ttcFix.selectRow(ttcFix.target.getSelectionRows()[0]+1);
		Pause.pause(2000);
		TableContainer table = CommonUtils.findTablePanel();
		    
		JTableFixture tableFix = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
		tableFix.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
		
		return tableFix;
  }
}
