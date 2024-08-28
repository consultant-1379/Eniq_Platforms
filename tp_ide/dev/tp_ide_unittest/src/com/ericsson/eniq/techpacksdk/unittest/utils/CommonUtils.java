/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;

import static org.fest.assertions.Assertions.*;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.fest.swing.core.BasicComponentFinder;
import org.fest.swing.core.ComponentFinder;
import org.fest.swing.core.ComponentMatcher;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.exception.LocationUnavailableException;
import org.fest.swing.exception.WaitTimedOutError;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JPopupMenuFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Pause;
import org.fest.swing.timing.Timeout;

import ssc.rockfactory.TableModificationLogger;
import tableTree.TableTreeComponent;
import tableTreeUtils.PairComponent;
import tableTreeUtils.ParameterPanel;
import tableTreeUtils.TableContainer;

import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.unittest.fest.BusyIndicatorNotShowingCondition;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.ManageGeneralPropertiesView;

/**
 * @author epetrmi
 * @author eheitur
 * 
 */
public class CommonUtils {

  // Matcher for the Busy Indicator
  private static ComponentMatcher busyIndicatorMatcher = new ComponentMatcher() {

    public boolean matches(final Component c) {
      if (!(c instanceof BusyIndicator)) {
        return false;
      } else {
        return c.isVisible();
      }
    }
  };

  /**
   * Waits for a busy indicator to show and disappear. The timeout is specified
   * as a parameter in milliseconds.
   * 
   * @param timeout
   *          The timeout in milliseconds
   */
  public static void waitForBusyIndicator(final int timeout) {

    // Wait for the busy indicator to show. In case the task os "too fast" the
    // busy indicator matcher will fail, as it requires the busy indicator to be
    // visible. In this case the exception is caught and ignored.
    try {
      // Find the visible busy indicator. In case there is no BusyIndicator, or
      // the component is not visible, then there will be a
      // ComponentLookupException.
      final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
      // BusyIndicator busyInd = (BusyIndicator)
      // finder.find(busyIndicatorMatcher);
      finder.find(busyIndicatorMatcher);
      // BusyIndicatorFixture busyFixture = new
      // BusyIndicatorFixture(TechPackIdeStarter.myRobot, busyInd);
      // assertThat(busyFixture.requireVisible()).as("BusyIndicator should be visible.");

      // Wait for the busy indicator to disappear
      System.out.println("waitForBusyIndicator(): Waiting maximum of " + (timeout / 1000)
          + " seconds for busy indicator to hide... ");
      Pause.pause(new BusyIndicatorNotShowingCondition("Busy indicator should disappear within " + (timeout / 1000)
          + " seconds."), Timeout.timeout(timeout));
      System.out.println("waitForBusyIndicator(): Busy indicator hidden.");
    } catch (final ComponentLookupException cle) {
      // Ignore the exception and continue.
      System.out.println("waitForBusyIndicator(): Visible busy indicator not found. Ignoring.");
    }
  }

  /**
   * Creates a new techpack with the given mandatory parameter values.
   * TechPackIDE must be running and the main window visible before calling this
   * method.
   * 
   * @param name
   * @param version
   * @param product
   * @param rstate
   * @param universe
   * @param baseVersionId
   *          versionId of the base techpack.
   * @return
   */
  public static boolean createNewTechPack(String name, String version, String product, String rstate, String universe,
      String baseVersiondId) {
    System.out.println("createNewTechPack(): Start.");

    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    final JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    tpTree.selectPath(TestSetupConstants.TEST_SERVER);
    
    // Click New button
    System.out.print("createNewTechPack(): Opening new techpack window...");
    TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

    // Wait for the new tech pack window to show
    final FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(10000).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(newTPWindow).isNotNull();
    System.out.print("Done.\n");

    // Fill in the fields: name, version, product, r-state, universe name.
    newTPWindow.textBox("Name").enterText(name);
    // Values are entered in lower case to speed up the tests. These values are
    // converted automatically to upper case by IDE.
    newTPWindow.textBox("Version").enterText(version.toLowerCase());
    newTPWindow.textBox("Product").enterText(product.toLowerCase());
    newTPWindow.textBox("Rstate").enterText(rstate.toLowerCase());
    newTPWindow.textBox("UniverseName").enterText(universe.toLowerCase());

    // Select the base techpack
    newTPWindow.comboBox("BaseDefinition").selectItem(baseVersiondId);

    // Check that create is enabled
    if (!newTPWindow.button("CreateNewTechpack").target.isEnabled()) {
      System.out.println("createNewTechPack(): Create button is disabled. New techpack creation failed.");
      return false;
    }

    // Click create
    System.out.println("createNewTechPack(): Creating new techpack.");
    newTPWindow.button("CreateNewTechpack").click();

    // Wait for busy indicator
    waitForBusyIndicator(10000);

    // NewTechPack window should be closed.
    assertThat(newTPWindow.requireNotVisible()).as("NewTechPack window should be closed.");
    System.out.println("createNewTechPack(): Waiting for max 10 seconds for main window activating.");
    try {
      TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000));
    } catch (final WaitTimedOutError wtoe) {
      // main window is not visible after timeout
      System.out.println("createNewTechPack(): Creation failed. Main window is not active.");
      return false;
    }

    System.out.println("createNewTechPack(): Done.");
    return true;

  }

  /**
   * This method executes a refresh in the TechPackIDE. This can be used for
   * example after reinitializing the in-memory databases. The TechPackIDE must
   * be running and the main window must be visible before calling this method.
   */
  public static void refreshIde() {
    System.out.println("refreshIde(): Start.");

    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    // Get the techpack tree
    final JTreeFixture tpTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");

    // Show a pop-up menu to be able to refresh.
    final JPopupMenuFixture rightMenu = tpTree.showPopupMenu();
    assertThat(rightMenu).isNotNull().as("A popup should be presented.");

    // Find and click the "Refresh" menu item
    rightMenu.menuItemWithPath("Refresh").click();

    // Wait for the busy indicator
    waitForBusyIndicator(10000);

    System.out.println("refreshIde(): Done.");
  }

  /**
   * Finds and returns a showing TableContainer (in a TableTreeComponent).
   * 
   * @return a visible TableContainer
   */
  public static TableContainer findTablePanel() {
    final ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(final Component c) {
        if (!(c instanceof TableContainer))
          return false;
        else
          return c.isVisible();
      }
    };

    final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();

    TableContainer target = null;
    try {
      target = (TableContainer) finder.find(matcher);
    } catch (final ComponentLookupException e) {
      System.out.println("findTablePanel(): No visible TableContainer component found!");
      return null;
    }
    return target;
  }

  /**
   * Finds and returns a showing ParameterPanel (in a TableTreeComponent).
   * 
   * @return a visible ParameterPanel
   */
  public static ParameterPanel findParameterPanel() {
    final ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(final Component c) {
        if (!(c instanceof ParameterPanel))
          return false;
        else
          return c.isVisible();
      }
    };

    final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();

    ParameterPanel target = null;
    try {
      target = (ParameterPanel) finder.find(matcher);
    } catch (final ComponentLookupException e) {
      System.out.println("findParameterPanel(): No visible Parameterpanel component found!");
      return null;
    }
    return target;
  }

  /**
   * Selects a tree pop-up menu option "Expand/Collapse - Collapse All Nodes"
   * from a (TableTreeComponent) JTreeFixture.
   * 
   * @param tree
   *          the tree fixture
   * 
   * @param rowNumber
   *          the row number from where the right-click pop-up is opened.
   */
  public static void collapseTree(final JTreeFixture tree, final int rowNumber) {
    tree.selectRow(rowNumber);

    final JPopupMenuFixture collapseMenu = tree.showPopupMenuAt(rowNumber);
    assertThat(collapseMenu).isNotNull().as("Could not pop up menu from header");

    final JMenuItemFixture collapseItem = collapseMenu.menuItemWithPath("Expand/Collapse", "Collapse All Nodes");
    assertThat(collapseItem).isNotNull().as("Menu item \"Collapse Tree\" could not be found");
    collapseItem.click();
  }

  /**
   * Finds a visible PairComponent with the matching text as a title.
   * 
   * @param text
   * @return the component
   */
  public static PairComponent findPairComponentWithText(final String text) {

    final ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(final Component c) {
        if (!(c instanceof PairComponent)) {
          return false;
        } else {
          final PairComponent pairComponent = (PairComponent) c;
          return pairComponent.getTitle().equals(text) && pairComponent.isVisible();
        }
      }
    };

    final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();

    PairComponent target = null;
    try {
      target = (PairComponent) finder.find(matcher);
    } catch (final ComponentLookupException e) {
      System.out.println("findPairComponentWithText(): No components found.");
      return null;
    }
    return target;
  }

  /**
   * Finds all the TableContainers from a tree model.
   * 
   * @param treeModel
   * @return array of TableContainers
   */
  public static TableContainer[] findTableContainers(final TreeModel treeModel) {
    final Vector<TableContainer> resultVector = new Vector<TableContainer>();

    final Vector<Object> temp = new Vector<Object>();
    temp.add(treeModel.getRoot());
    while (!temp.isEmpty()) {
      final Object node = temp.remove(0);
      final int numberOfChildren = treeModel.getChildCount(node);
      for (int i = 0; i < numberOfChildren; ++i) {
        temp.add(treeModel.getChild(node, i));
      }

      if (node instanceof DefaultMutableTreeNode) {
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) node;
        if (defaultMutableTreeNode.getUserObject() instanceof TableContainer) {
          resultVector.add((TableContainer) defaultMutableTreeNode.getUserObject());
        }
      }
    }

    final TableContainer[] result = new TableContainer[resultVector.size()];
    return resultVector.toArray(result);
  }

  /**
   * @param tpTreePath
   *          The tree path for the techpack, e.g.
   *          "etlrep/DC_E_TDRBS/DC_E_TDRBS:PA1"
   * @param timeout
   *          Timeout for the edit window to open in milliseconds.
   * @return the edit widow frame fixture. Null if open failed.
   */
  public static FrameFixture openTechPackEditWindow(final String tpTreePath, final int timeout) {

    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");   

    // Select techpack from the tree
    try {
      System.out.println("openTechPackEditWindow(): Selecting techpack: " + tpTreePath);
      techPackTree.selectPath(tpTreePath);
    } catch (final LocationUnavailableException lue) {
      // Failure: The techpack selection failed
      System.out.println("openTechPackEditWindow(): Techpack does not exist!");
      return null;
    }

    // Lock the techpack, if not already locked.
    if (TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()) {
      TechPackIdeStarter.getMyWindow().button("TechPackLock").click();
      System.out.println("openTechPackEditWindow(): Techpack locked.");
    } else {
      System.out.println("openTechPackEditWindow(): Techpack was already locked.");
    }

    Pause.pause(5000);

    // Make sure edit button is showing
    if (!TechPackIdeStarter.getMyWindow().button("TechPackEdit").target.isEnabled()) {
      System.out.println("openTechPackEditWindow(): Edit button is not enabled!");
      return null;
    }

    Pause.pause(5000);

    // Click "Edit"-button
    System.out.println("openTechPackEditWindow(): Clicking edit.");
    // There are two ways below. Both works, but normal button is faster to
    // execute.
    TechPackIdeStarter.getMyWindow().button("TechPackEdit").click();
    // techPackTree.showPopupMenuAt(treePath).menuItem("Edit...").click();

    // Find frame and click "Measurement Types" -tab
    final FrameFixture editFrame = WindowFinder.findFrame("EditTechPackWindow").withTimeout(timeout).using(
        TechPackIdeStarter.getMyRobot());
    return editFrame;
  }

  /**
   * Copy a techpack. The techpack is locked first, if not already locked.
   * 
   * @param tpTreePath
   *          The tree path for the techpack, e.g.
   *          "etlrep/DC_E_TDRBS/DC_E_TDRBS:PA1"
   * @param timeout
   *          Timeout for the new techpack window to open in milliseconds.
   * @param newVersion
   *          The new version number for the copy, e.g. "PA2"
   * @param newBaseTechPack
   *          The versionId of the base techpack, e.g. "TP_BASE:BASE_TP_090421"
   * @return
   */
  public static void copyTechPack(String tpTreePath, int timeout, String newVersion, String newBaseTechPack) {

    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");

    // Select techpack from the tree
    try {
      System.out.println("copyTechPack(): Selecting techpack: " + tpTreePath);
      techPackTree.selectPath(tpTreePath);
    } catch (LocationUnavailableException lue) {
      // Failure: The techpack selection failed
      System.out.println("copyTechPack(): Techpack does not exist!");
    }

    // Lock the techpack, if not already locked.
    if (TechPackIdeStarter.getMyWindow().button("TechPackLock").target.isEnabled()) {
      TechPackIdeStarter.getMyWindow().button("TechPackLock").click();
      System.out.println("copyTechPack(): Techpack locked.");
    } else {
      System.out.println("copyTechPack(): Techpack was already locked.");
    }

    // Check that unlock is enabled
    assertThat(
        TechPackIdeStarter.getMyWindow().button("TechPackUnlock").requireEnabled(Timeout.timeout(5000)).target
            .isEnabled()).as("Unlock button should be enabled.").isTrue();

    // Click New button
    System.out.println("copyTechPack(): Opening new techpack window.");
    TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

    // Wait for the new techpack window to show
    FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(timeout).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(newTPWindow).isNotNull();

    // Check that create is disabled
    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).isFalse();

    // Select the from techpack (NOTE: Should already be selected, but just to
    // be sure.)
    JTreeFixture newTpTree = newTPWindow.tree("NewTechPackTree");
    assertThat(newTpTree).isNotNull().as("TechPackTree should not be null");
    newTpTree.selectPath(tpTreePath);

    // Enter new techpack information
    newTPWindow.textBox("Version").deleteText();
    newTPWindow.textBox("Version").enterText(newVersion);
    //newTPWindow.textBox("UniverseName").deleteText();
    //newTPWindow.textBox("UniverseName").enterText("Universe");
    // enterRowsInUeTable(newTPWindow.table("UniverseExtension"), newTPWindow);
    //newTPWindow.comboBox("BaseDefinition").selectItem(newBaseTechPack);
    // Check that create is enabled
    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).as("Create button should be enabled")
        .isTrue();

    // Click create
    System.out.println("copyTechPack(): Creating new techpack.");
    newTPWindow.button("CreateNewTechpack").click();

    // Wait for busy indicator
    CommonUtils.waitForBusyIndicator(30000);

    // NewTechPack window should be closed.
    assertThat(newTPWindow.requireNotVisible()).as("NewTechPack window should be closed.");

    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
        "Main window should be visible after closing " + "the new techpack window.");

    System.out.println("copyTechPack(): New TechPack version created.");

  }

  /**
   * Copy a techpack. Techpack is not locked first.
   * 
   * @param tpTreePath
   *          The tree path for the techpack, e.g.
   *          "etlrep/DC_E_TDRBS/DC_E_TDRBS:PA1"
   * @param timeout
   *          Timeout for the new techpack window to open in milliseconds.
   * @param newVersion
   *          The new version number for the copy, e.g. "PA2"
   * @param newBaseTechPack
   *          The versionId of the base techpack, e.g. "TP_BASE:BASE_TP_090421"
   * @return
   */
  public static void copyTechPackNoLock(String tpTreePath, int timeout, String newVersion, String newBaseTechPack) {

    // Select the Manage TechPack tab
    TechPackIdeStarter.getMyMainPanel().selectTab("Manage TechPack");

    JTreeFixture techPackTree = TechPackIdeStarter.getMyWindow().tree("TechPackTree");
    assertThat(techPackTree).isNotNull().as("TechPackTree should not be null");

    // Select techpack from the tree
    try {
      System.out.println("copyTechPack(): Selecting techpack: " + tpTreePath);
      techPackTree.selectPath(tpTreePath);
    } catch (LocationUnavailableException lue) {
      // Failure: The techpack selection failed
      System.out.println("copyTechPack(): Techpack does not exist!");
    }

    // Click New button
    System.out.println("copyTechPack(): Opening new techpack window.");
    TechPackIdeStarter.getMyWindow().button("TechPackAddNew").click();

    // Wait for the new techpack window to show
    FrameFixture newTPWindow = WindowFinder.findFrame("NewTechPackWindow").withTimeout(timeout).using(
        TechPackIdeStarter.getMyRobot());
    assertThat(newTPWindow).isNotNull();

    // Check that create is disabled
    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).isFalse();

    // Select the from techpack (NOTE: Should already be selected, but just to
    // be sure.)
    JTreeFixture newTpTree = newTPWindow.tree("NewTechPackTree");
    assertThat(newTpTree).isNotNull().as("TechPackTree should not be null");
    newTpTree.selectPath(tpTreePath);

    // Enter new techpack information
    newTPWindow.textBox("Version").deleteText();
    newTPWindow.textBox("Version").enterText(newVersion);
    //newTPWindow.textBox("UniverseName").deleteText();
    //newTPWindow.textBox("UniverseName").enterText("Universe");
    // enterRowsInUeTable(newTPWindow.table("UniverseExtension"), newTPWindow);
    //newTPWindow.comboBox("BaseDefinition").selectItem(newBaseTechPack);
    // Check that create is enabled
    assertThat(newTPWindow.button("CreateNewTechpack").target.isEnabled()).as("Create button should be enabled")
        .isTrue();

    // Click create
    System.out.println("copyTechPack(): Creating new techpack.");
    newTPWindow.button("CreateNewTechpack").click();

    // Wait for busy indicator
    CommonUtils.waitForBusyIndicator(60000);

    // NewTechPack window should be closed.
    assertThat(newTPWindow.requireNotVisible()).as("NewTechPack window should be closed.");

    assertThat(TechPackIdeStarter.getMyMainPanel().requireEnabled(Timeout.timeout(10000))).as(
        "Main window should be visible after closing " + "the new techpack window.");

    System.out.println("copyTechPack(): New TechPack version created.");

  }

  /**
   * Finds dialog from screen with it's titlename
   * 
   * PRE-CONDITION: Dialog must be created and visible on the screen
   * 
   * @param robot
   * @return DialogFixture object
   */
  public static DialogFixture findDialogWithTitleName(final Robot robot, final String titleName) {
    final ComponentMatcher dialogMatcher = new ComponentMatcher() {

      public boolean matches(final Component c) {
        boolean ret = false;
        if (c != null && c instanceof JDialog) {
          final JDialog o = (JDialog) c;
          if (titleName != null && titleName.equals(o.getTitle())) {
            ret = o.isVisible();
          }
        }
        return ret;
      }
    };

    final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
    final JDialog targetDialog = (JDialog) finder.find(dialogMatcher);

    return new DialogFixture(robot, targetDialog);
  }

  /**
   * A utility method for adding a vendor release entry to a techpack general
   * properties window.
   * 
   * Prerequisites: The edit window for the techpack must be open.
   * 
   * @param editFrame
   *          The edit frame fixture for a techpack.
   * @param robot
   *          The robot fixture
   * @param vrValue
   *          The vendor release value to be added.
   * @param saveChanges
   *          If true, the changes are saved after adding the entry.
   */
  public static void addVendorRelease(final FrameFixture editFrame, final Robot robot, final String vrValue,
      final boolean saveChanges) {
    // Create a matcher for the name dialog
    final ComponentMatcher matcher = new ComponentMatcher() {

      public boolean matches(final Component c) {
        if (!(c instanceof ManageGeneralPropertiesView))
          return false;
        return ((ManageGeneralPropertiesView) c).isVisible();
      }
    };

    // Get the dialog
    final ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
    final ManageGeneralPropertiesView generalPropertiesViewTarget = (ManageGeneralPropertiesView) finder.find(matcher);
    assertThat(generalPropertiesViewTarget).isNotNull();
    final JPanelFixture generalPropertiesView = new JPanelFixture(robot, generalPropertiesViewTarget);
    final JTableFixture vendorReleaseTable = generalPropertiesView.table("VendorRelease");

    // Add a row
    final JPopupMenuFixture popupMenu = vendorReleaseTable.tableHeader().showPopupMenuAt(0);
    final JMenuItemFixture menuItem = popupMenu.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

      @Override
      protected boolean isMatching(final JMenuItem item) {
        return item.getText().equals("Add");
      }
    });
    menuItem.click();

    // Get the vendor release dialog, and enter name for the vendor release.
    final DialogFixture vendorReleaseDialog = WindowFinder.findDialog(new GenericTypeMatcher<JDialog>(JDialog.class) {

      @Override
      protected boolean isMatching(final JDialog dialog) {
        return dialog.getTitle().equals("Vendor Release");
      }
    }).using(robot);
    vendorReleaseDialog.textBox().enterText(vrValue);
    final JButtonFixture okButton = vendorReleaseDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

      @Override
      protected boolean isMatching(final JButton button) {
        return button.getText().equalsIgnoreCase("ok");
      }
    });
    okButton.click();

    if (saveChanges) {
      generalPropertiesView.button("ManageGeneralPropertiesSave").click();
    }
  }

  /**
   * Finds a TableTreeComponent from screen with a specific name.
   * 
   * PRE-CONDITION: TTC must be created and visible on the screen. The name must
   * have been defined for the TTC (with setName()).
   * 
   * @param robot
   * @param componentName
   * @return the JTree fixture
   */
  public static JTreeFixture findTTCWithName(Robot robot, final String componentName) {
    ComponentMatcher ttcMatcher = new ComponentMatcher() {

      public boolean matches(Component c) {
        boolean ret = false;
        if (c != null && c instanceof TableTreeComponent) {
          TableTreeComponent o = (TableTreeComponent) c;
          if (componentName != null && componentName.equals(o.getName())) {
            ret = o.isVisible();
          }
        }
        return ret;
      }
    };

    ComponentFinder finder = (ComponentFinder) BasicComponentFinder.finderWithCurrentAwtHierarchy();
    TableTreeComponent targetTTC = (TableTreeComponent) finder.find(ttcMatcher);

    return new JTreeFixture(robot, targetTTC);
  }

  /**
   * Resets the table modification logger.
   * 
   * @param loggingEnabled
   *          Set to true if logging is enabled. If set to false, the method has
   *          no effect.
   */
  public static void resetTableLogging(boolean loggingEnabled) {
    // Reset logger
    if (loggingEnabled) {
      TableModificationLogger.instance().reset();
    }
  }

  /**
   * Prints all the modified database tables collected by the database table
   * modification logger to System.out in alphabetical order.
   * 
   * @param loggingEnabled
   *          Set to true if logging is enabled. If set to false, the method has
   *          no effect.
   */
  public static void printModifiedTables(boolean loggingEnabled) {
    // Print the changed database tables in the last test
    if (loggingEnabled) {
      System.out.println("printModifiedTables(): Database tables changed in the previous test:");
      String[] changedTables = TableModificationLogger.instance().get();
      //String[] logEntriesInCorrectForm = new String[changedTables.length];     
      if (changedTables.length == 0) {
        System.out.println("(none)");
      } else {
        for (int i = 0; i < changedTables.length; ++i) {
        	String[] tokens = changedTables[i].split(" ");
        	String tableName = tokens[tokens.length - 1];
            System.out.println(tableName);
        }
      }
    }
  }
  
  /**
   * Prints all the modified database tables collected by the database table
   * modification logger to System.out in alphabetical order.
   * 
   * @param loggingEnabled
   *          Set to true if logging is enabled. If set to false, the method has
   *          no effect.
   */ 
  
public static Vector<String> listModifiedTables() {
	  String[] changedTables = TableModificationLogger.instance().get();
	  ArrayList<String> tableNames = new ArrayList<String>();
	  if (changedTables.length == 0) {
	        System.out.println("(No table changed)");
	      } else {
	        for (int i = 0; i < changedTables.length; ++i) {
	        	String[] tokens = changedTables[i].split(" ");
	        	String tableName = tokens[tokens.length - 1];
	        	tableNames.add(tableName);
	        }
	      }
	return removeDuplicates(tableNames);	   
	  }

  /**
   * Stops the table modification logger.
   * 
   * @param loggingEnabled
   *          Set to true if logging is enabled. If set to false, the method has
   *          no effect.
   */
  public static void stopTableLogging(boolean loggingEnabled) {
    if (loggingEnabled) {
      TableModificationLogger.instance().stopLogging();
      System.out.println("stopTableLogging(): Stopped table change logging.");
    }
    
    
  }

  /**
   * Starts the table modification logger.
   * 
   * @param loggingEnabled
   *          Set to true if logging is enabled. If set to false, the method has
   *          no effect.
   */
  public static void startTableLogging(boolean loggingEnabled) {
    if (loggingEnabled) {
      TableModificationLogger.instance().stopLogging();
      System.out.println("startTableLogging(): Started table change logging.");
    }
  }
  
  /**
   * Removes Duplicate entries from list.
   * 
   * @param array
   * 
   */
  public static Vector<String> removeDuplicates(ArrayList<String> stringArray) {
	  
	    Vector<String> resultVector = new Vector<String>();
	    
	    final Iterator<String> it= stringArray.iterator();
	    while (it.hasNext()){
	    	String current = it.next();
	    	if (!resultVector.contains(current)){
	    		resultVector.add(current);
	    	}
	    }	  

	    //String[] result = new String[resultVector.size()];
	    //result = resultVector.toArray(result);
	    return resultVector;
	  }
}
