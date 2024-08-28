/**
 * A simple helper class for handling basic operations
 * in measurement types -view
 * 
 * PRE-CONDITIONS: 
 * - Techpack must be opened in edit view
 * - Measurement types-tab must be selected
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.tree.TreePath;

import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.data.TableCell;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;

import tableTreeUtils.DescriptionComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.RadiobuttonComponent;
import tableTreeUtils.TableContainer;
import tableTreeUtils.UniverseExtensionComponent;

import com.ericsson.eniq.component.TableComponent;

/**
 * @author epetrmi
 * 
 */
abstract public class MeasurementTypesUtils {

  /**
   * Adds a new measurement counter for given measurement type with given
   * parameters
   * 
   * @param robot
   *          - some robotFixture
   * @param measurementTypesTree
   *          - the tree that is edited (contains measurement types)
   * @param row
   *          - row number of measurement type WHEN all measurement types are
   *          collapsed
   * @param cName
   *          - name of the counter
   * @param cDataType
   *          - dataType of the counter
   * @param cDataSize
   *          - dataSize of the counter
   */
  public static void addMeasurementCounter(Robot robot, JTreeFixture measurementTypesTree, int row, String cName,
      String cDataType, String cDataSize) {

    TableContainer[] tableContainers = CommonUtils.findTableContainers(measurementTypesTree.target.getModel());
    JPopupMenuFixture columnMenu = measurementTypesTree.showPopupMenuAt(row + 4); // Counters
    // table
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
    addRowItem.click();

    // Select the counters table row
    Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
    Point measurementTypesTreeLocation = measurementTypesTree.target.getLocationOnScreen();
    int relativePointerLocationX = pointerLocation.x - measurementTypesTreeLocation.x;
    int relativePointerLocationY = pointerLocation.y - measurementTypesTreeLocation.y;
    Point relativePointerLocation = new Point(relativePointerLocationX, relativePointerLocationY);
    robot.click(measurementTypesTree.target, relativePointerLocation);
    robot.click(measurementTypesTree.target, relativePointerLocation);

    // Create a new entry into the counters table
    int index = getTableContainerMeasurementCounterIndex(row);
    JTable countersTableTarget = tableContainers[index].getTable();
    JTableFixture countersTable = new JTableFixture(robot, countersTableTarget);
    final int editRowNumber = countersTable.rowCount() > 0 ? countersTable.rowCount() - 1 : 0;
    JTableCellFixture nameCell = countersTable.cell(TableCell.row(editRowNumber).column(0));
    nameCell.enterValue(cName);
    JTableCellFixture dataTypeCell = countersTable.cell(TableCell.row(editRowNumber).column(1));
    dataTypeCell.enterValue(cDataType);
    JTableCellFixture dataSizeCell = countersTable.cell(TableCell.row(editRowNumber).column(2));
    dataSizeCell.enterValue(cDataSize);
  }

  /**
   * Adds a new measurement key for given measurement type with given parameters
   * 
   * @param robot
   *          - some robotFixture
   * @param measurementTypesTree
   *          - the tree that is edited (contains measurement types)
   * @param row
   *          - row number of measurement type WHEN all measurement types are
   *          collapsed
   * @param cName
   *          - name of the key
   * @param cDataType
   *          - dataType of the key
   * @param cDataSize
   *          - dataSize of the key
   */
  public static TableContainer[] addMeasurementKey(Robot robot, JTreeFixture measurementTypesTree, int row,
      String name, String dataType, String dataSize, boolean isElement) {
    JPopupMenuFixture columnMenu = measurementTypesTree.showPopupMenuAt(row + 3);
    assertThat(columnMenu).isNotNull().as("Could not pop up menu from header");

    JMenuItemFixture addRowItem = columnMenu.menuItemWithPath("Add Row");
    assertThat(addRowItem).isNotNull().as("Menu item \"Add Row\" could not be found");
    addRowItem.click();

    // Select the keys table row
    Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
    Point measurementTypesTreeLocation = measurementTypesTree.target.getLocationOnScreen();
    int relativePointerLocationX = pointerLocation.x - measurementTypesTreeLocation.x;
    int relativePointerLocationY = pointerLocation.y - measurementTypesTreeLocation.y;
    Point relativePointerLocation = new Point(relativePointerLocationX, relativePointerLocationY);
    robot.click(measurementTypesTree.target, relativePointerLocation);
    robot.click(measurementTypesTree.target, relativePointerLocation);

    // Create a new entry into the keys table
    TableContainer[] tableContainers = CommonUtils.findTableContainers(measurementTypesTree.target.getModel());
    int index = getTableContainerMeasurementKeyIndex(row);
    JTable keysTableTarget = tableContainers[index].getTable();
    JTableFixture keysTable = new JTableFixture(robot, keysTableTarget);

    final int editRowNumber = keysTable.rowCount() > 0 ? keysTable.rowCount() - 1 : 0;
    JTableCellFixture nameCell = keysTable.cell(TableCell.row(editRowNumber).column(0));
    nameCell.enterValue(name);
    JTableCellFixture dataTypeCell = keysTable.cell(TableCell.row(editRowNumber).column(1));
    dataTypeCell.enterValue(dataType);
    JTableCellFixture dataSizeCell = keysTable.cell(TableCell.row(editRowNumber).column(2));
    dataSizeCell.enterValue(dataSize);
    if(isElement) {
    	JTableCellFixture isElementCell = keysTable.cell(TableCell.row(0).column(8));
    	isElementCell.click(); //One key needs to be selected as the Element.
    }

    return tableContainers;
  }

  /**
   * Selects parameters-node with given row number (E.g. Used to open parameters
   * for certain measurement type)
   * 
   * @param measurementTypesTree
   *          - edited tree
   * @param originalRow
   * @return JPanelFixture for parameter-panel
   */
  public static JPanelFixture selectParameters(JTreeFixture measurementTypesTree, int originalRow) {
    CommonUtils.collapseTree(measurementTypesTree, 0);
    measurementTypesTree.selectRow(originalRow);
    measurementTypesTree.toggleRow(originalRow);
    measurementTypesTree.toggleRow(originalRow + 1);
    measurementTypesTree.toggleRow(originalRow + 2);
    measurementTypesTree.selectRow(originalRow + 2);
    // System.out.println("Selecting PARAMS : originalrow="+originalRow+", finalrow="+(originalRow+4));
    return new JPanelFixture(TechPackIdeStarter.getMyRobot(), CommonUtils.findParameterPanel());
  }

  /**
   * Selects keys-node with given row number
   * 
   * @param measurementTypesTree
   *          - edited tree
   * @param originalRow
   */
  public static JTable selectKeys(JTreeFixture measurementTypesTree, int originalRow) {
    CommonUtils.collapseTree(measurementTypesTree, 0);
    measurementTypesTree.selectRow(originalRow);
    measurementTypesTree.toggleRow(originalRow);
    measurementTypesTree.toggleRow(originalRow + 2);
    measurementTypesTree.toggleRow(originalRow + 3);
    measurementTypesTree.selectRow(originalRow + 3);

    // System.out.println("Selecting KEYS : originalrow=" + originalRow +
    // ", finalrow=" + (originalRow + 3));

    TableContainer[] tableContainers = CommonUtils.findTableContainers(measurementTypesTree.target.getModel());
    int index = getTableContainerMeasurementKeyIndex(originalRow);
    JTable result = tableContainers[index].getTable();

    return result;
  }

  /**
   * Selects counters-node with given row number
   * 
   * @param measurementTypesTree
   *          - edited tree
   * @param originalRow
   */
  public static JTable selectCounters(JTreeFixture measurementTypesTree, int originalRow) {
    CommonUtils.collapseTree(measurementTypesTree, 0);
    measurementTypesTree.selectRow(originalRow);
    measurementTypesTree.toggleRow(originalRow);
    measurementTypesTree.toggleRow(originalRow + 3);
    measurementTypesTree.toggleRow(originalRow + 4);
    measurementTypesTree.selectRow(originalRow + 4);

    TableContainer[] tableContainers = CommonUtils.findTableContainers(measurementTypesTree.target.getModel());
    int index = getTableContainerMeasurementCounterIndex(originalRow);
    JTable result = tableContainers[index].getTable();

    // System.out.println("Selecting COUNTER : originalrow=" + originalRow +
    // ", finalrow=" + (originalRow + 4));

    return result;
  }

  /**
   * ##TODO## Return type can be removed. Last assertion is not done currently
   * Adds a new measurement type
   * 
   * @param editFrame
   * @return - JTreeFixture object.
   */
  public static JTreeFixture addMeasurementType(Robot robot, JTreeFixture measurementTypesTree, String measTypeName) {

    // Open the pop up -menu with a right click
    measurementTypesTree.rightClick();

    JPopupMenu targetPopupMenu = robot.findActivePopupMenu();
    assertThat(targetPopupMenu).isNotNull();
    JPopupMenuFixture popupMenu = new JPopupMenuFixture(robot, targetPopupMenu);

    JMenuItemFixture addElementMenuItem = popupMenu.menuItemWithPath("Add Element");
    addElementMenuItem.click();

    // Create a matcher for the name dialog
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

    // Enter the measurement type name, and press OK button
    DialogFixture nameDialog = new DialogFixture(robot, targetDialog);
    nameDialog.textBox().deleteText();
    nameDialog.textBox().enterText(measTypeName);
    nameDialog.button("LimitedSizeTextDialogOkButton").click();

    // The newly-added measurement type should now be selected in the tree
    // String selectionPath =
    // measurementTypesTree.target.getSelectionPath().toString();
    // assertThat(selectionPath.equals("[root, " + TECHPACK_NAME + "_" +
    // MEASUREMENT_TYPE_NAME + "]"));
    return measurementTypesTree;
  }

  /**
   * ##TODO## consider moving these tablecontainer methods to other place
   * 
   * Counts index value of Tablecontainer
   * 
   * @param row
   *          - measurementType row number
   * @return
   */
  public static int getTableContainerIndex(int row) {
    int index = row * 2 + 0;// every MT_ROW has 2 subrows (keys&counters). +0
    // means Keys (and +1 would mean counters)
    return index;
  }

  /**
   * Returns index for a tableContainer/keys with measurement type row number
   * 
   * @param row
   * @return
   */
  public static int getTableContainerMeasurementKeyIndex(int row) {
    return (getTableContainerIndex(row) + 0);
  }

  /**
   * Returns index for a tableContainer/counter with measurement type row number
   * 
   * @param row
   * @return
   */
  public static int getTableContainerMeasurementCounterIndex(int row) {
    return (getTableContainerIndex(row) + 1);
  }

  /**
   * Finds menu using pairComponentName
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param robot
   * @param pairComponentName
   * @return JComboBoxFixture
   */
  public static JComboBoxFixture findJComboBoxMenuWithPairComponentName(Robot robot, String pairComponentName) {
    PairComponent p = CommonUtils.findPairComponentWithText(pairComponentName);
    JComboBox cBox = (JComboBox) p.getComponent();
    return new JComboBoxFixture(robot, cBox);
  }

  /**
   * ##TODO## This is now NOT done by using a fixture Finds radiobutton-set
   * using pairComponentName
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param pairComponentName
   * @return RadiobuttonComponent
   */
  public static RadiobuttonComponent findRadiobuttonComponentWithPairComponentName(String pairComponentName) {
    PairComponent p = CommonUtils.findPairComponentWithText(pairComponentName);
    return (RadiobuttonComponent) p.getComponent();
  }

  /**
   * Finds checkbox with given pairComponentName and returns it as fixture
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param robot
   * @param pairComponentName
   * @return JCheckBoxFixture object
   */
  public static JCheckBoxFixture findJCheckBoxWithPairComponentName(Robot robot, String pairComponentName) {
    PairComponent p = CommonUtils.findPairComponentWithText(pairComponentName);
    JCheckBox component = (JCheckBox) p.getComponent();
    return new JCheckBoxFixture(robot, component);
  }

  /**
   * Finds description field from given panelFixture
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JTextComponentFixture object
   */
  public static JTextComponentFixture findJTextComponentDescriptionWithJPanelFixture(JPanelFixture parameterFixture) {
    JTextComponentFixture textFix = parameterFixture.textBox(new GenericTypeMatcher<JTextComponent>(
        JTextComponent.class) {

      @Override
      protected boolean isMatching(JTextComponent c) {
        DescriptionComponent o = (DescriptionComponent) c.getParent();
        PairComponent pc = (PairComponent) o.getParent();
        if ("Description".equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return textFix;
  }

  /**
   * Finds Classification-field
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JTextComponentFixture object
   */
  public static JTextComponentFixture findJTextComponentClassificationWithJPanelFixture(JPanelFixture parameterFixture) {
    JTextComponentFixture textFix = parameterFixture.textBox(new GenericTypeMatcher<JTextComponent>(
        JTextComponent.class) {

      @Override
      protected boolean isMatching(JTextComponent c) {
        PairComponent pc = (PairComponent) c.getParent();
        if ("Classification".equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return textFix;
  }

  /**
   * Sets basic parameters for measurement type
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * 
   * @param robot
   * @param parameterFixture
   * @param sizing
   *          - value is set to sizing field (if null the field is skipped and
   *          not set)
   * @param tableType
   *          - value is set to tableType field (if null the field is skipped
   *          and not set)
   * @param isTotalAggregation
   *          - true = checked, false unchecked
   * @param isElementBHSupport
   *          - true = checked, false unchecked
   * @param isDataFormatSupport
   *          - true = checked, false unchecked
   * @param classification
   *          - value is set to classification field (if null the field is
   *          skipped and not set)
   * @param universeExt
   *          - value is set to universeExt field (if null the field is skipped
   *          and not set)
   * @param description
   *          - value is set to description field (if null the field is skipped
   *          and not set)
   * @param addedObjBHSupportRows
   *          - values are added as rows in BHSupport dialog (if null or
   *          arrayLength=0 the operation is skipped)
   */
  public static void setBasicMeasurementypeParams(Robot robot, JPanelFixture parameterFixture, String sizing,
      String tableType, boolean isTotalAggregation, boolean isElementBHSupport, boolean isDataFormatSupport,
      String classification, String universeExt, String description, String[] addedObjBHSupportRows,
      String[] addedVendorReleaseRows) {

    // Init
    JTextComponentFixture textFix = null;
    JCheckBoxFixture f = null;

    // Edit Params/Sizing
    if (sizing != null) {
      MeasurementTypesUtils.findJComboBoxMenuWithPairComponentName(robot, "Sizing").selectItem(sizing);
    }

    if (tableType != null) {
      // Edit Params/Table Type
      // TODO: BUG!! The radio button selection is changed, but the button is
      // not "clicked", thus the model is not changed.
      // MeasurementTypesUtils.findRadiobuttonComponentWithPairComponentName("Table Type").setSelectedButton(tableType);

      // Fix
      MeasurementTypesUtils.findRadiobuttonComponentWithPairComponentName("Table Type").setSelectedButton(tableType);

    }

    // Edit Params/Total Aggregation
    f = MeasurementTypesUtils.findJCheckBoxWithPairComponentName(robot, "Total Aggregation");
    if (isTotalAggregation)
      f.check();
    else
      f.uncheck();

    // Edit Params/Element BH Support
    f = MeasurementTypesUtils.findJCheckBoxWithPairComponentName(robot, "Element BH Support");
    if (isElementBHSupport)
      f.check();
    else
      f.uncheck();

    // Edit Params/Data format support
    f = MeasurementTypesUtils.findJCheckBoxWithPairComponentName(robot, "Data Format Support");
    if (isDataFormatSupport)
      f.check();
    else
      f.uncheck();

    // Edit Params/Classification
    if (classification != null) {
      textFix = MeasurementTypesUtils.findJTextComponentClassificationWithJPanelFixture(parameterFixture);
      textFix.selectAll();
      textFix.enterText(classification);
    }

    // Edit Params/Universe ext.
    if (universeExt != null) {
      // MeasurementTypesUtils.findJComboBoxMenuWithPairComponentName(robot,
      // "Universe ext.").selectItem( universeExt );
      if (universeExt != null && universeExt.length() > 0) {
        findParameterPanelUnivExtButton(parameterFixture).click();

        // Get the dialog
        DialogFixture ueDialogFixture = findUnivExtDialog(robot);

        // First remove the existing rows.
        clearRowsFromUnivExtDialogTable(ueDialogFixture);

        // Add the new rows.
        addRowsToUnivExtDialogTable(ueDialogFixture, universeExt);

        // Click ok.
        ueDialogFixture.button("UnivExtComponentOkButton").click();
      }
    }

    // Edit Params/Description
    if (description != null) {
      textFix = MeasurementTypesUtils.findJTextComponentDescriptionWithJPanelFixture(parameterFixture);
      textFix.selectAll();
      textFix.enterText(description);
    }

    // Open ObjBHSupportDialog by clicking button
    if (addedObjBHSupportRows != null && addedObjBHSupportRows.length > 0) {

      // Open Object BH Support dialog
      findParameterPanelBHSupportButton(parameterFixture).click();
      DialogFixture bhsDialogFixture = findBHSupportDialog(robot);

      // Clear the existing Object BH Support values.
      clearRowsFromObjectBHDialogTable(robot, bhsDialogFixture);

      // Add rows and click ok
      for (int i = 0; i < addedObjBHSupportRows.length; i++) {
        addRowToObjectBHDialogTable(robot, bhsDialogFixture, addedObjBHSupportRows[i]);
      }
      bhsDialogFixture.button("ObjectBHSupportOkButton").click();
    }

    // Open Vendor release dialog by clicking button
    if (addedVendorReleaseRows != null && addedVendorReleaseRows.length > 0) {
      findParameterPanelBHSupportButton(parameterFixture).click();
      findParameterPanelDeltaCalcButton(parameterFixture).click();

      // Add rows to ObjBHSupportTable in Dialog and click ok
      DialogFixture bhsDialogFixture = findBHSupportDialog(robot);
      DialogFixture deltaCalcDialogFixture = findDeltaCalcDialog(robot);
      for (int i = 0; i < addedVendorReleaseRows.length; i++) {
        addRowToObjectBHDialogTable(robot, bhsDialogFixture, addedVendorReleaseRows[i]);
        addRowToDelcaCalcSupportDialogTable(deltaCalcDialogFixture, addedVendorReleaseRows);
      }
      bhsDialogFixture.button("ObjectBHSupportOkButton").click();
    }

  }

  private static void addRowToDelcaCalcSupportDialogTable(DialogFixture deltaCalcDialogFixture,
      String[] addedVendorReleaseRows) {
    // TODO Auto-generated method stub

  }

  private static DialogFixture findDeltaCalcDialog(Robot robot) {
    // TODO Auto-generated method stub
    return null;
  }

  private static DialogFixture findParameterPanelDeltaCalcButton(JPanelFixture parameterFixture) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Adds ObjBHSupport row to table in dialog
   * 
   * PRE-CONDITION: ObjBHSupport dialog must on the screen
   * 
   * @param robot
   * @param bhsDialogFixture
   * @param addedRowText
   */
  public static void addRowToObjectBHDialogTable(Robot robot, DialogFixture bhsDialogFixture, String addedRowText) {
    JTableFixture table = bhsDialogFixture.table();
    JPopupMenuFixture popupMenu = table.tableHeader().showPopupMenuAt(0);
    JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

      @Override
      protected boolean isMatching(JMenuItem item) {
        return item.getText().equals("Add Row");
      }
    });
    menuItem.click();

    // Enter name
    JTable t = table.component();
    int index = (t.getRowCount() - 1);
    JTableCellFixture cell = table.cell(TableCell.row(index).column(0));

    // Hack: need to get the real editor instead of just entering the value,
    // since the object bh support value might not exist in the combo box
    // values.

    // cell.enterValue(addedRowText);
    JComboBox editor = (JComboBox) cell.editor();
    JComboBoxFixture editorFixture = new JComboBoxFixture(robot, (JComboBox) editor);
    cell.startEditing();
    editorFixture.target.setSelectedItem(addedRowText);
    cell.stopEditing();

    bhsDialogFixture.pressAndReleaseKeys(KeyEvent.VK_ENTER);
  }

  /**
   * Clears ObjBHSupport rows from the table in the dialog.
   * 
   * PRE-CONDITION: ObjBHSupport dialog must on the screen
   * 
   * @param robot
   * @param bhsDialogFixture
   */
  public static void clearRowsFromObjectBHDialogTable(Robot robot, DialogFixture bhsDialogFixture) {
    JTableFixture table = bhsDialogFixture.table();

    for (int i = 0; i < table.component().getRowCount(); i++) {

      JPopupMenuFixture popupMenu = table.cell(TableCell.row(0).column(0)).showPopupMenu();
      JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

        @Override
        protected boolean isMatching(JMenuItem item) {
          return item.getText().equals("Remove Row");
        }
      });
      menuItem.click();
    }
  }

  /**
   * Adds Universe Extension rows to the table in the dialog
   * 
   * PRE-CONDITION: Universe Extensions dialog must on the screen
   * 
   * 
   * @param ueDialogFixture
   *          the universe extensions dialog fixture.
   * @param addedRowsString
   *          a comma separated values of the rows to be added.
   */
  public static void addRowsToUnivExtDialogTable(DialogFixture ueDialogFixture, String addedRowsString) {
    JTableFixture table = ueDialogFixture.table();

    // Split the input string to universe extensions
    String[] extensions = addedRowsString.split(",");

    // Add the extension entries to the table as separate rows.
    for (int i = 0; i < extensions.length; i++) {

      JPopupMenuFixture popupMenu = table.tableHeader().showPopupMenuAt(0);
      JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

        @Override
        protected boolean isMatching(JMenuItem item) {
          return item.getText().equals("Add Empty");
        }
      });
      menuItem.click();

      // Enter name
      JTable t = table.component();
      int index = (t.getRowCount() - 1);
      JTableCellFixture cell = table.cell(TableCell.row(index).column(1));
      cell.enterValue(extensions[i]);
      // cell.stopEditing();
      ueDialogFixture.pressAndReleaseKeys(KeyEvent.VK_ENTER);
    }

  }

  /**
   * Clears Universe Extension rows from the table in the dialog
   * 
   * PRE-CONDITION: Universe Extensions dialog must on the screen
   * 
   * 
   * @param ueDialogFixture
   *          the universe extensions dialog fixture.
   */
  public static void clearRowsFromUnivExtDialogTable(DialogFixture ueDialogFixture) {
    JTableFixture table = ueDialogFixture.table();

    // Remove the extension entries to the table as separate rows.
    for (int i = 0; i < table.rowCount(); i++) {

      // Select number columns of the first row
      table.selectCell(TableCell.row(0).column(0));
      // Select delete from the pop-up
      JPopupMenuFixture popupMenu = table.showPopupMenuAt(TableCell.row(0).column(0));
      JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

        @Override
        protected boolean isMatching(JMenuItem item) {
          return item.getText().equals("Delete");
        }
      });
      menuItem.click();
    }

  }

  /**
   * Finds Button that opens BH-support dialog
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JButtonFixture object
   */
  public static JButtonFixture findParameterPanelBHSupportButton(JPanelFixture parameterFixture) {
    JButtonFixture fix = parameterFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton b) {
        TableComponent o = (TableComponent) b.getParent();
        PairComponent pc = (PairComponent) o.getParent();
        if ("Object BH Support".equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return fix;
  }

  /**
   * Finds Button that opens universe extension dialog
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JButtonFixture object
   */
  public static JButtonFixture findParameterPanelUnivExtButton(JPanelFixture parameterFixture) {
    JButtonFixture fix = parameterFixture.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(JButton b) {
        UniverseExtensionComponent o = (UniverseExtensionComponent) b.getParent();
        PairComponent pc = (PairComponent) o.getParent();
        if ("Universe Extensions".equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return fix;
  }

  /**
   * Finds ObjBHSupport dialog from screen
   * 
   * PRE-CONDITION: ObjBHSupportDialog must be created and visible on the screen
   * 
   * @param robot
   * @return DialogFixture object
   */
  public static DialogFixture findBHSupportDialog(Robot robot) {
    ComponentMatcher dialogMatcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        boolean ret = false;
        if (c != null && c instanceof JDialog) {
          JDialog o = (JDialog) c;
          if ("ObjectBHSupport".equals(o.getTitle())) {
            ret = o.isVisible();
          }
        }
        return ret;
      }
    };

    // Find BHS dialog
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JDialog targetDialog = (JDialog) finder.find(dialogMatcher);

    return new DialogFixture(robot, targetDialog);
  }

  /**
   * Finds Universe Extensions dialog from screen
   * 
   * PRE-CONDITION: Universe Extension Dialog must be created and visible on the
   * screen
   * 
   * @param robot
   * @return DialogFixture object
   */
  public static DialogFixture findUnivExtDialog(Robot robot) {
    ComponentMatcher dialogMatcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        boolean ret = false;
        if (c != null && c instanceof JDialog) {
          JDialog o = (JDialog) c;
          if ("Universe Extensions".equals(o.getTitle())) {
            ret = o.isVisible();
          }
        }
        return ret;
      }
    };

    // Find Universe extensions dialog
    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    JDialog targetDialog = (JDialog) finder.find(dialogMatcher);

    return new DialogFixture(robot, targetDialog);
  }
  
  
  /**
   * Checks if the given measurementType exists in the tree.
   * IF NOT it creates a new measurement and selects it.
   * @param measurementType 
   */
  
  public static int measurementTypeSanityCheck(String measurementType) {
	  int measurementTypeRow = 0;
	  FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(300000).using(TechPackIdeStarter.getMyRobot());

	    editFrame.tabbedPane().selectTab("Measurement Types");

	    Pause.pause(2, TimeUnit.SECONDS);
	  
	    JTreeFixture measurementTypesTree = editFrame.tree();
	    
	    TreePath measurementPath = measurementTypesTree.target.getNextMatch(measurementType, 0, Position.Bias.Forward);
		  
		  if (measurementPath != null) {
			measurementTypeRow = measurementTypesTree.target.getRowForPath(measurementPath);
			  
		  }else {
			  MeasurementTypesUtils.addMeasurementType(TechPackIdeStarter.getMyRobot(), measurementTypesTree, measurementType);		 
			  measurementTypeRow = measurementTypesTree.target.getRowForPath(measurementTypesTree.target.getSelectionPath());
			  MeasurementTypesUtils.selectKeys(measurementTypesTree, measurementTypeRow );
			  MeasurementTypesUtils.addMeasurementKey(TechPackIdeStarter.getMyRobot(), measurementTypesTree, measurementTypeRow, "testKey", "varchar", "100", true);
			  MeasurementTypesUtils.selectCounters(measurementTypesTree, measurementTypeRow);		    
			  MeasurementTypesUtils.addMeasurementCounter(TechPackIdeStarter.getMyRobot(), measurementTypesTree, measurementTypeRow, "COUNTER1", "varchar", "50");
		  }
		return measurementTypeRow;
	  
		  
	  }

}
