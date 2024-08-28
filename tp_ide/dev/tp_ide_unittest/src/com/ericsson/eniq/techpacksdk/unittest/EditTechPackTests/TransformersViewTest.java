package com.ericsson.eniq.techpacksdk.unittest.EditTechPackTests;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import tableTree.TableTreeComponent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
import org.junit.Test;

import ssc.rockfactory.TableModificationLogger;
import tableTreeUtils.TableContainer;

import com.ericsson.eniq.techpacksdk.unittest.fest.TTTableCellWriter;
import com.ericsson.eniq.techpacksdk.unittest.utils.CommonUtils;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseAssert;
import com.ericsson.eniq.techpacksdk.unittest.utils.DatabaseTester;
import com.ericsson.eniq.techpacksdk.unittest.utils.TechPackIdeStarter;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestSetupConstants;
import com.ericsson.eniq.techpacksdk.unittest.utils.TestTableItem;
import com.ericsson.eniq.techpacksdk.unittest.utils.TransformersUtils;

/**
 * This test class contains unit test cases for the TechPackIDE: Manage Transformers
 *  View
 */
public class TransformersViewTest { 

  private static FrameFixture myEditWindow = null;

  private static JTabbedPaneFixture myViewPanel = null;
  
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TransformersViewTest.class);
  }

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    System.out.println("setUpBeforeClass()");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("Executing test class: TransformersViewTest");
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
    tpTree.selectPath(path);
    System.out.println("setUpBeforeClass(): Click edit.");
    CommonUtils.refreshIde();   

    assertThat(TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()).as(
    "Edit button should be enabled.").isTrue();
    myEditWindow = CommonUtils.openTechPackEditWindow(path, 40000);    
    assertThat(myEditWindow).isNotNull().as("The edit window should be shown.");

    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible.");

    myViewPanel.selectTab("Transformers");
    Pause.pause(5000);
    
    assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled()).isTrue();

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
      myEditWindow.button("ManageTransformerViewClose").click();
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
   * Tests creation of a new Transformers type, and saving the values to the
   * database.
   * 
   * @throws Exception
   */
  @Test
  public void testEditExistingTransformerDescriptionAndSave() throws Exception {
    System.out.println("testEditExistingTransformerDescriptionAndSave(): Start.");
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("Transformers");    

    JTreeFixture transformersTree = myEditWindow.tree("ManageTransformerViewTree");
    assertThat(transformersTree).isNotNull().as("TransformersTree should not be null");   
    
    myEditWindow.maximize();
    int rows = transformersTree.component().getRowCount();
    
    assertThat(rows >= 1).as("There must be atleast mdc node in Transformers tab");    

    JPanelFixture parameterFix = TransformersUtils.selectParameters(transformersTree,"mdc", "ALL");
    parameterFix.textBox().deleteText();
    parameterFix.textBox().enterText("test Description");
    parameterFix.click();  
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();

    //verify the buttons status and click save.
    clickSave();  
   
    logger.stopLogging();
    
    transformersTree.selectPath(transformersTree.target.getModel().getRoot().toString()
			+transformersTree.separator()+"mdc");  
    transformersTree.target.collapseRow(transformersTree.target.getSelectionRows()[0]);
    System.out.println("testEditExistingTransformerDescriptionAndSave(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
    // Database verification
    List<TestTableItem> tables = new Vector<TestTableItem>();
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingTransformerDescriptionAndSave", TestSetupConstants.DB_DWHREP,
        "TRANSFORMER", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingTransformerDescriptionAndSave", TestSetupConstants.DB_DWHREP,
        "TRANSFORMATION", null));
    tables.add(new TestTableItem(this.getClass().getName(), "testEditExistingTransformerDescriptionAndSave", TestSetupConstants.DB_DWHREP,
        "TPACTIVATION", null));
    
    myEditWindow.normalize();
    String[] modifiedTables = DatabaseTester.testTableList(tables);
    DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
    tables = null;

    System.out.println("testEditExistingTransformerDescriptionAndSave(): Done.");
  }
  
  /**
   * Tests creation of a new Transformers type, and Discarding the changes.
   * 
   * @throws Exception
   */
  @Test
  public void testEditExistingTransformerAndDiscard() throws Exception {
    System.out.println("testEditExistingTransformerAndDiscard(): Start.");
    
    myEditWindow.maximize();
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

    myViewPanel.selectTab("Transformers");    

    JTreeFixture transformersTree = myEditWindow.tree("ManageTransformerViewTree");
    assertThat(transformersTree).isNotNull().as("TransformersTree should not be null");       
   
    int rows = transformersTree.component().getRowCount();
    
    assertThat(rows >= 1).as("There must be atleast mdc node in Transformers tab");
    
    JPanelFixture parameterFix = TransformersUtils.selectParameters(transformersTree, "mdc","DC_E_TEST_AAL2AP");

    String descriptionBeforeEdit = parameterFix.textBox().text();
    parameterFix.textBox().deleteText();
    parameterFix.textBox().enterText("Description");
    Pause.pause(2000);
    parameterFix.click();  

    //verify the buttons status and click save.
    clickCancel();

//    String descriptionAfterEdit = parameterFix.textBox().text();
    System.out.println("testEditExistingTransformerAndDiscard(): Tables modified are: " 
    		  + CommonUtils.listModifiedTables() + ".");
    
    // Database verification
    // None of the tables will be touched by logger as changes are discarded rather than saved.
    myEditWindow.normalize();
    assertThat(descriptionBeforeEdit.equals("")).as("Description should be the same as before discarding the changes");


    System.out.println("testEditExistingTransformerAndDiscard(): Done.");
  }
  
  

 /**
  * Test first verifies that user can not add Transformers manually.
  * A Data Format node is added and it is verified that newly added
  * node appears in the Transformers Tree.
  * 
  * @throws Exception
  */  
  @Test
  public void testAddTransformerNodeViaDataFormat() throws Exception {
    System.out.println("testAddTransformerNodeViaDataFormat(): Start."); 
    myEditWindow.maximize();
    
    myViewPanel = myEditWindow.tabbedPane();
    assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");
    
    myViewPanel.selectTab("Transformers");    

    JTreeFixture transformerTree = myEditWindow.tree("ManageTransformerViewTree");
    assertThat(transformerTree).isNotNull().as("TransformersTree should not be null"); 
    
    //verify right-click menu does not appear 
    //not to enable user to add Transformer node manually    
    assertThat(transformerTree.showPopupMenu()).isNotNull();
    assertThat(transformerTree.showPopupMenu().menuLabels().length == 0);
    
    Pause.pause(2000);
    
    myViewPanel.selectTab("Data Formats");
    Pause.pause(20000);
    JTreeFixture dataFormatsTree = myEditWindow.tree("DataFormatTree");
    assertThat(dataFormatsTree).isNotNull().as("DataFormatsTree should not be null"); 
    
    final JPopupMenuFixture dFormatsMenu = dataFormatsTree.showPopupMenu();
    JMenuItemFixture item = dFormatsMenu.menuItemWithPath("Add Node");
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
    nameDialog.textBox().enterText(TestSetupConstants.NEW_TRANSFORMER);
    
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
    
    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    myEditWindow.button("DataFormatSave").click();
    
    CommonUtils.waitForBusyIndicator(60000);
    
    assertThat(myEditWindow.button("DataFormatSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("DataFormatCancel").target.isEnabled()).isFalse();
    
    myViewPanel.selectTab("Transformers"); 
    transformerTree.target.setRootVisible(true);
    String transformerNodePath = transformerTree.target.getModel().getRoot().toString()
	+ transformerTree.separator() + TestSetupConstants.NEW_TRANSFORMER.toLowerCase();
    
    transformerTree.selectPath(transformerNodePath);
    TreePath transformerPath = transformerTree.target.getSelectionPath();
    assertThat(transformerPath.toString().equals("[root, " + TestSetupConstants.NEW_TRANSFORMER.toLowerCase() + "]"));
    
    logger.stopLogging();
    System.out.println("testAddTransformerNodeViaDataFormat(): Tables modified by are: " + CommonUtils.listModifiedTables() + ".");

    try {
        // DATABASE ASSERTION
        List<TestTableItem> tables = new Vector<TestTableItem>();
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "DATAFORMAT", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "TRANSFORMER", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTKEY", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTCOLUMN", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "DATAITEM", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "MEASUREMENTCOUNTER", null));
        tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformerNodeViaDataFormat",
            TestSetupConstants.DB_DWHREP, "REFERENCECOLUMN", null));

        String[] modifiedTables = DatabaseTester.testTableList(tables);
        DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);

      } finally {    
    	  myEditWindow.normalize();
    	  assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled()).isFalse();
    	    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled()).isFalse();
    	    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled()).isTrue();
          
      }
      
      System.out.println("testAddTransformerNodeViaDataFormat(): Done.");

  }
      
   /**
     * Add a transformation to ALL measurement types.
     * Test adds a row in Common Transformation table
     * of ALL node.
     * Finally test verifies if transformation is 
    * added to Common transformations under one of measurement types
     * @throws Exception
    */
   @Test
   public void testAddCommonTransformationToALL() throws Exception {
     System.out.println("testAddTransformationToALL(): Start.");
     
     String measToVerify = "DC_E_TEST_AAL2AP";
     myEditWindow.maximize();
     myViewPanel = myEditWindow.tabbedPane();
     assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

     myViewPanel.selectTab("Transformers");    

     JTreeFixture transformersTree = myEditWindow.tree("ManageTransformerViewTree");
     assertThat(transformersTree).isNotNull().as("TransformersTree should not be null");  
    
     int rows = transformersTree.component().getRowCount();
        
     assertThat(rows >= 1).as("There must be atleast mdc node in Transformers tab"); 
     
     transformersTree.target.setRootVisible(true);	 
	    transformersTree.selectPath(transformersTree.target.getModel().getRoot().toString()
	    				+transformersTree.separator()+TestSetupConstants.NEW_TRANSFORMER.toLowerCase());   
	    int[] rowsNow = transformersTree.component().getSelectionRows();
	    assertThat(rowsNow).isNotNull().as("Selection in the Transformers tree is empty!");
	    transformersTree.toggleRow(rowsNow[0]);
	    transformersTree.toggleRow(rowsNow[0]+1);
	    transformersTree.component().setSelectionRow(rowsNow[0]+1);
	    transformersTree.component().startEditingAtPath(transformersTree.component().getSelectionPath());

	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) transformersTree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for node ALL is not found.");
	    
	  
	    ttcFix.selectPath("ALL");
	    int allRow = ttcFix.target.getSelectionRows()[0];
	    ttcFix.toggleRow(allRow);
	    int transformationRow = ttcFix.target.getSelectionRows()[0]+2;	  
	    ttcFix.toggleRow(transformationRow);	    
	    Pause.pause(1000);
	    ttcFix.selectRow(transformationRow);
	    //ttcFix.selectPath(ttcPath+"/"+"Common Transformations");
	    ttcFix.selectRow(ttcFix.target.getSelectionRows()[0]+1);
	    Pause.pause(2000);
	    TableContainer table = CommonUtils.findTablePanel();	   
	   
	    JTableFixture tableFix = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
	    tableFix.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
	    
	    JPopupMenuFixture columnMenu = tableFix.tableHeader().showPopupMenuAt(0); 
	    
	    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

	    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
	    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
	    addRowItem.click();	   
	    Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
	    Point measurementTypesTreeLocation = ttcFix.target.getLocationOnScreen();
	    int relativePointerLocationX = pointerLocation.x - measurementTypesTreeLocation.x;
	    int relativePointerLocationY = pointerLocation.y - measurementTypesTreeLocation.y;
	    Point relativePointerLocation = new Point(relativePointerLocationX, relativePointerLocationY);
	    TechPackIdeStarter.getMyRobot().click(ttcFix.target, relativePointerLocation);	    
	   
	    final int editRowNumber = tableFix.rowCount() > 0 ? tableFix.rowCount() - 1 : 0;
	    //Enter Source
	    JTableCellFixture nameCell = tableFix.cell(TableCell.row(editRowNumber).column(0));
	    nameCell.enterValue(TestSetupConstants.TRANSFORMATION_SOURCE);
	    
	    //Enter Target
	    JTableCellFixture targetCell = tableFix.cell(TableCell.row(editRowNumber).column(1));	    
	    targetCell.enterValue("TEST_TARGET");
	    
	    //Enter config
	    JTableCellFixture configCell = tableFix.cell(TableCell.row(editRowNumber).column(3));	
	    configCell.startEditing();
	    
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
	    FrameFixture editFrame = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

	        @Override
	        protected boolean isMatching(JFrame dialog) {
	          return dialog.getTitle().equalsIgnoreCase("Edit config of Transformation: ALL");
	        }
	      }).withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
	   
	    editFrame.comboBox().selectItem("fixed");
	    editFrame.textBox("value").enterText("10");
	    Pause.pause(2000);
	    editFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {

	        @Override
	        protected boolean isMatching(JButton button) {
	          return button.getText().equalsIgnoreCase("save");
	        }
	      }).click();
	    configCell.stopEditing();

	    JTableFixture tableFix1 = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
	    tableFix1.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
	    JTableCellFixture transformersCell = tableFix1.cell(TableCell.row(editRowNumber).column(5));	
	    transformersCell.startEditing();
	    JButtonFixture edit = myEditWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {

	        @Override
	        protected boolean isMatching(JButton b) {
	          if ("...".equals(b.getText()) && b.isVisible()) {
	            return true;
	          } else {
	            return false;
	          }
	        }
	      });
	    edit.click();
	   DialogFixture editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Transformers");
	   JTableFixture transfomersTable = editDialog.table();
	   JTableCellFixture enableCell = transfomersTable.cell(TableCell.row(transfomersTable.target.getRowCount()-1).column(1));
	   enableCell.enterValue("false");
	   
	   transfomersTable.tableHeader().showPopupMenuAt(0).menuItemWithPath("Pick All").click();	   

	    
	   editDialog.button("TransformersOkButton").click();
	   transformersCell.stopEditing();
	   
     
     TableModificationLogger logger = TableModificationLogger.instance();
     logger.reset();
     logger.startLogging();
     
     //click-Save
     clickSave();
     
     logger.stopLogging();
     
     //Verify that transformation is added to all the measurement types
     JTableFixture measTransformationFix = TransformersUtils.selectTransformations(transformersTree, TestSetupConstants.NEW_TRANSFORMER, measToVerify);   
     List<String> sources = new ArrayList<String>();
     for (int i=0;i<measTransformationFix.target.getRowCount();i++) {
     	String source = measTransformationFix.cell((TableCell.row(i).column(0))).value();
     	sources.add(source);     	
     }
     assertTrue(sources.contains(TestSetupConstants.TRANSFORMATION_SOURCE));
     
     
     System.out.println("testAddTransformationToALL(): Tables modified by are: " + CommonUtils.listModifiedTables() + ".");

  // Database verification
     List<TestTableItem> tables = new Vector<TestTableItem>();
     tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformationToALL", TestSetupConstants.DB_DWHREP,
         "TPACTIVATION", null));
     tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformationToALL", TestSetupConstants.DB_DWHREP,
         "TRANSFORMATION", null));
     
     myEditWindow.normalize();
     String[] modifiedTables = DatabaseTester.testTableList(tables);
     DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
     tables = null;
     
     System.out.println("testAddTransformationToALL(): Done.");

     
   }   
   
  
  /**
   * Open Common Transformation--
   * Disable Transformation for DC_E_TEST_AAL2AP
   * Finally open transformation under  DC_E_TEST_AAL2AP 
   * and verify disabled transformation does not exist.
   * @throws Exception
  */
 @Test
 public void testDisableTransformationForMT() throws Exception {
   System.out.println("testDisableTransformationForMT(): Start.");
   String measurementToDisable = "DC_E_TEST_AAL2AP";
   myEditWindow.maximize();
   myViewPanel = myEditWindow.tabbedPane();
   assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

   myViewPanel.selectTab("Transformers");    
   Pause.pause(5000);

   JTreeFixture transformersTree = myEditWindow.tree("ManageTransformerViewTree");
   assertThat(transformersTree).isNotNull().as("TransformersTree should not be null");  
  
   int rows = transformersTree.component().getRowCount();
      
   assertThat(rows >= 1).as("There must be atleast mdc node in Transformers tab"); 
   
   JTableFixture transformationFix = TransformersUtils.selectTransformations(transformersTree, TestSetupConstants.NEW_TRANSFORMER, "ALL");
   int editRowNumber = transformationFix.target.getRowCount()-1;
   for (int i=0;i<transformationFix.target.getRowCount();i++) {
   	String source = transformationFix.cell((TableCell.row(i).column(0))).value();
   	if (source.equalsIgnoreCase(TestSetupConstants.TRANSFORMATION_SOURCE)){
   		editRowNumber = i;
   		break;
   	}
   }
	JTableCellFixture transformersCell = transformationFix.cell(TableCell.row(editRowNumber).column(5));	
	transformersCell.startEditing();
	
	JButtonFixture edit = myEditWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {

	        @Override
	        protected boolean isMatching(JButton b) {
	          if ("...".equals(b.getText()) && b.isVisible()) {
	            return true;
	          } else {
	            return false;
	          }
	        }
	      });
	 edit.click();
	 DialogFixture editDialog = CommonUtils.findDialogWithTitleName(TechPackIdeStarter.getMyRobot(), "Transformers");
	 JTableFixture transfomersTable = editDialog.table();
	 for (int i=0;i<transfomersTable.target.getRowCount();i++) {
	    	String MT = transfomersTable.cell((TableCell.row(i).column(0))).value();
	    	if (MT.equalsIgnoreCase(measurementToDisable)){
	    		JTableCellFixture enableCell = transfomersTable.cell(TableCell.row(i).column(1));
	    		 enableCell.enterValue("false");
	    		break;
	    	}
	    }

	    
	 editDialog.button("TransformersOkButton").click();
	 transformersCell.stopEditing();
	   
   
   TableModificationLogger logger = TableModificationLogger.instance();
   logger.reset();
   logger.startLogging();
   
   //click-Save
   clickSave();    
   logger.stopLogging();
   
   JTableFixture measTransformationFix = TransformersUtils.selectTransformations(transformersTree, TestSetupConstants.NEW_TRANSFORMER, measurementToDisable);   
   List<String> sources = new ArrayList<String>();
   for (int i=0;i<measTransformationFix.target.getRowCount();i++) {
   	String source = measTransformationFix.cell((TableCell.row(i).column(0))).value();
   	sources.add(source);     	
   }
   assertThat(sources.contains(TestSetupConstants.TRANSFORMATION_SOURCE)).isFalse();
   System.out.println("testDisableTransformationForMT(): Tables modified by are: " + CommonUtils.listModifiedTables() + ".");

// Database verification
   List<TestTableItem> tables = new Vector<TestTableItem>();
   tables.add(new TestTableItem(this.getClass().getName(), "testDisableTransformationForMT", TestSetupConstants.DB_DWHREP,
       "TPACTIVATION", null));
   tables.add(new TestTableItem(this.getClass().getName(), "testDisableTransformationForMT", TestSetupConstants.DB_DWHREP,
       "TRANSFORMATION", null));
   
   myEditWindow.normalize();
   String[] modifiedTables = DatabaseTester.testTableList(tables);
   DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
   tables = null;
   
   System.out.println("testDisableTransformationForMT(): Done.");

   
 }
 
 /**
  * Add a transformation to a measurement type.
  * Test adds a row in Transformation table
  * of given MT node. 
  * Finally test verifies if transformation is 
  * added to Common transformations under ALL
  * @throws Exception
 */
@Test
public void testAddTransformationToOneMT() throws Exception {
  System.out.println("testAddTransformationToOneMT(): Start.");
  
  String measurementType = "DC_E_TEST_AAL2AP";
  myEditWindow.maximize();
  myViewPanel = myEditWindow.tabbedPane();
  assertThat(myViewPanel).isNotNull().as("The panel should be visible by now.");

  myViewPanel.selectTab("Transformers");    

  JTreeFixture transformersTree = myEditWindow.tree("ManageTransformerViewTree");
  assertThat(transformersTree).isNotNull().as("TransformersTree should not be null");        
 
  int rows = transformersTree.component().getRowCount();
     
  assertThat(rows >= 1).as("There must be atleast mdc node in Transformers tab"); 
  
 
 addTransformation(transformersTree, TestSetupConstants.NEW_TRANSFORMER, measurementType);  
  
  TableModificationLogger logger = TableModificationLogger.instance();
  logger.reset();
  logger.startLogging();
  
  //click-Save
  clickSave();
  
  logger.stopLogging();  
  
  //Verify that transformation is added to all the measurement types
  JTableFixture measTransformationFix = TransformersUtils.selectTransformations(transformersTree, TestSetupConstants.NEW_TRANSFORMER, "ALL");   
  List<String> sources = new ArrayList<String>();
  for (int i=0;i<measTransformationFix.target.getRowCount();i++) {
  	String source = measTransformationFix.cell((TableCell.row(i).column(0))).value();
  	sources.add(source);     	
  }
  assertTrue(sources.contains("test"));
  
  System.out.println("testAddTransformationToOneMT(): Tables modified by are: " + CommonUtils.listModifiedTables() + ".");

// Database verification
  List<TestTableItem> tables = new Vector<TestTableItem>();
  tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformationToOneMT", TestSetupConstants.DB_DWHREP,
      "TPACTIVATION", null));
  tables.add(new TestTableItem(this.getClass().getName(), "testAddTransformationToOneMT", TestSetupConstants.DB_DWHREP,
      "TRANSFORMATION", null));
  
  String[] modifiedTables = DatabaseTester.testTableList(tables);
  DatabaseAssert.assertModifiedTablesEqual(modifiedTables, logger);
  tables = null;
  
  System.out.println("testAddTransformationToOneMT(): Done.");
}
  
  // ///////////////////////////
  // HELPER METHODS
  // ///////////////////////////
  
  /**
   * A common method to add transformation 
   * under a measurement type.
   * @param  
   */
  
  private static void addTransformation(JTreeFixture transformersTree, String transformerNode,final String ttc) {
	  
	    transformersTree.target.setRootVisible(true);
	 
	    transformersTree.selectPath(transformersTree.target.getModel().getRoot().toString()
	    				+transformersTree.separator()+transformerNode.toLowerCase());   
	    int[] rowsNow = transformersTree.component().getSelectionRows();
	    assertThat(rowsNow).isNotNull().as("Selection in the Transformers tree is empty!");
	    //transformersTree.toggleRow(rowsNow[0]);
	    //transformersTree.toggleRow(rowsNow[0]+1);
	    transformersTree.component().setSelectionRow(rowsNow[0]+1);
	    transformersTree.component().startEditingAtPath(transformersTree.component().getSelectionPath());

	    DefaultMutableTreeNode comp = (DefaultMutableTreeNode) transformersTree.target.getLastSelectedPathComponent();
	    JTreeFixture ttcFix = new JTreeFixture(TechPackIdeStarter.getMyRobot(), (TableTreeComponent) comp.getUserObject());
	    assertThat(ttcFix).isNotNull().as("The TTC for node ALL is not found.");
	    
	  
	    ttcFix.selectPath(ttc);	   
	    int allRow = ttcFix.target.getSelectionRows()[0];
	    CommonUtils.collapseTree(ttcFix, allRow);
	    ttcFix.toggleRow(allRow);
	    int transformationRow = ttcFix.target.getSelectionRows()[0]+2;	  
	    ttcFix.toggleRow(transformationRow);	    
	    Pause.pause(1000);
	    ttcFix.selectRow(transformationRow);
	    //ttcFix.selectPath(ttcPath+"/"+"Common Transformations");
	    ttcFix.selectRow(ttcFix.target.getSelectionRows()[0]+1);
	    Pause.pause(2000);
	    TableContainer table = CommonUtils.findTablePanel();	   
	   
	    JTableFixture tableFix = new JTableFixture(TechPackIdeStarter.getMyRobot(), table.getTable());
	    tableFix.cellWriter(new TTTableCellWriter(TechPackIdeStarter.getMyRobot()));
	    
	    JPopupMenuFixture columnMenu = tableFix.tableHeader().showPopupMenuAt(0); 
	    
	    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

	    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
	    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
	    addRowItem.click();	   
	    Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
	    Point measurementTypesTreeLocation = ttcFix.target.getLocationOnScreen();
	    int relativePointerLocationX = pointerLocation.x - measurementTypesTreeLocation.x;
	    int relativePointerLocationY = pointerLocation.y - measurementTypesTreeLocation.y;
	    Point relativePointerLocation = new Point(relativePointerLocationX, relativePointerLocationY);
	    TechPackIdeStarter.getMyRobot().click(ttcFix.target, relativePointerLocation);	    
	   
	    final int editRowNumber = tableFix.rowCount() > 0 ? tableFix.rowCount() - 1 : 0;
	    //Enter Source
	    JTableCellFixture nameCell = tableFix.cell(TableCell.row(editRowNumber).column(0));
	    nameCell.enterValue("test");
	    
	    //Enter Target
	    JTableCellFixture targetCell = tableFix.cell(TableCell.row(editRowNumber).column(1));	    
	    targetCell.enterValue("TEST_TARGET");
	    
	    //Enter config
	    JTableCellFixture configCell = tableFix.cell(TableCell.row(editRowNumber).column(3));	
	    configCell.startEditing();
	    
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
	    FrameFixture editFrame = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {

	        @Override
	        protected boolean isMatching(JFrame dialog) {
	          return dialog.getTitle().contains("Edit config of Transformation:");
	        }
	      }).withTimeout(30000).using(TechPackIdeStarter.getMyRobot());
	   
	    editFrame.comboBox().selectItem("fixed");
	    editFrame.textBox("value").enterText("10");
	    Pause.pause(2000);
	    editFrame.button(new GenericTypeMatcher<JButton>(JButton.class) {

	        @Override
	        protected boolean isMatching(JButton button) {
	          return button.getText().equalsIgnoreCase("save");
	        }
	      }).click();
	    configCell.stopEditing();
	    
	   
  }

  /**
   * Clicks the save button in the Transformers view window.
   */
  private void clickSave() {
    System.out.println("clickSave()");

    assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled());

    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled());

    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled());
  

    myEditWindow.button("ManageTransformerViewSave").click();    
  
    CommonUtils.waitForBusyIndicator(60000); 
   
    assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled()).isFalse();
    
    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled()).isFalse();

    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled());
  }

  /**
   * Clicks the cancel button in the Transformers view window.
   */
  private void clickCancel() {
    System.out.println("clickCancel()");
    assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled());
    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled());
    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled());

    TableModificationLogger logger = TableModificationLogger.instance();
    logger.reset();
    logger.startLogging();
    
    myEditWindow.button("ManageTransformerViewCancel").click();

    Pause.pause(2000);
    logger.stopLogging();

    assertThat(myEditWindow.button("ManageTransformerViewSave").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ManageTransformerViewCancel").target.isEnabled()).isFalse();
    assertThat(myEditWindow.button("ManageTransformerViewClose").target.isEnabled());
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
