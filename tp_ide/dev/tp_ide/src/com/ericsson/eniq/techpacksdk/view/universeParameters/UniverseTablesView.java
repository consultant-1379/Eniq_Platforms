package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.ComboBoxTableCellEditor;

import com.distocraft.dc5000.repository.dwhrep.Universetable;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.ComboTableCellRenderer;
import com.ericsson.eniq.techpacksdk.GenericPanel;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class UniverseTablesView extends GenericPanel {

  private static final Logger logger = Logger.getLogger(UniverseTablesView.class.getName());

  public static final Color WHITE = Color.WHITE;

  private SingleFrameApplication application;

  private UniverseTabs parentPanel;

  private boolean saveEnabled = false;

  private boolean cancelEnabled = false;

  private boolean tableUTChanged = false;

  boolean editable = true;

  private JTable universeTablesT = new JTable();

  private DataModelController dataModelController;

  private JFrame frame;

  private String[] extensions = null;

  Versioning tmpversioning;

  /**
   * For error handling
   */
  Vector<String> errors = new Vector<String>();

  /**
   * For error handling
   */
  private ErrorMessageComponent errorMessageComponent;

  public UniverseTablesView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    tmpversioning = versioning;

    this.extensions = dataModelController.getUniverseTablesDataModel().getUniverseExtensions(
        tmpversioning.getVersionid());

    // ************** Text panel **********************
    Dimension tableDim = new Dimension(700, 300);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Universe tables
    JLabel universeclass = new JLabel("Universe tables");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(universeclass, c);

    universeTablesT = new JTable();

    UniverseTablesTableModel uttm = dataModelController.getUniverseTablesDataModel().getUTTableModel();
    setModel(universeTablesT, uttm);

    addTableModelListener(uttm, new TableUTSelectionListener());

    universeTablesT.setBackground(WHITE);
    universeTablesT.setEnabled(editable);

    if (editable) {
      universeTablesT.addMouseListener(new TableUTSelectionListener());
      universeTablesT.getTableHeader().addMouseListener(new TableUTSelectionListener());
    }
    setColumnWidths(universeTablesT);

    setColumnEditors(universeTablesT);
    setColumnRenderers(universeTablesT);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(universeTablesT);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setPreferredSize(tableDim);
    txtPanel.add(scrollPane, c);

    // ************** buttons **********************

    JButton cancel;
    JButton save;

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Discard");
    cancel.setName("UniverseTablesDiscard");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");
    save.setName("UniverseTablesSave");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseTablesClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    buttonPanel.add(errorMessageComponent);

    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    // ************** right & left panels, left panel contains
    // tab**********************

    JPanel lpanel = new JPanel(new BorderLayout());
    JPanel rpanel = new JPanel(new BorderLayout());
    lpanel.add(txtPanelS);

    // ************** Inner panel panel with right and left panels
    // **********************

    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.LINE_AXIS));
    innerPanel.add(lpanel, BorderLayout.LINE_START);
    innerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    innerPanel.add(rpanel, BorderLayout.LINE_START);

    // ************** Main panel with inner panel and button panel
    // **********************

    // insert both panels in main view

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(innerPanel, gbc);

    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.gridx = 0;
    gbc.gridy = 1;

    this.add(buttonPanel, gbc);

    frame.repaint();
  }

  /**
   * Perform refresh
   * 
   */
  public void refresh() {

    dataModelController.getUniverseTablesDataModel().refresh(tmpversioning);

    UniverseTablesTableModel uttm = dataModelController.getUniverseTablesDataModel().getUTTableModel();

    addTableModelListener(uttm, new TableUTSelectionListener());
    setModel(universeTablesT, uttm);

    setColumnEditors(universeTablesT);
    setColumnRenderers(universeTablesT);

    tableUTChanged = false;
    setSaveEnabled(false);
    setCancelEnabled(false);
    frame.repaint();
    handleButtons();

  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  public void setColumnEditors(final JTable theTable) {

    // Set editor for Owner
    final TableColumn ownerColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.ownerColumnIdx);
    final ComboBoxTableCellEditor ownerColumnComboEditor = new ComboBoxTableCellEditor(Constants.UNIVERSEOWNERTYPES);
    ownerColumn.setCellEditor(ownerColumnComboEditor);

    // Set editor for name
    final TableColumn nameColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.nameColumnIdx);
    LimitedSizeCellEditor nameColumnEditor = new LimitedSizeCellEditor(
        UniverseTablesTableModel.myColumnWidths[UniverseTablesTableModel.nameColumnIdx], Universetable
            .getTablenameColumnSize(), true);
    nameColumn.setCellEditor(nameColumnEditor);

    // Set editor for Alias
    final TableColumn aliasColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.aliasColumnIdx);
    LimitedSizeCellEditor aliasColumnEditor = new LimitedSizeCellEditor(
        UniverseTablesTableModel.myColumnWidths[UniverseTablesTableModel.aliasColumnIdx], Universetable
            .getAliasColumnSize(), false);
    aliasColumn.setCellEditor(aliasColumnEditor);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseTablesTableModel.universeextensionColumnIdx);
    // final ComboBoxTableCellEditor universeextensionColumnComboEditor = new
    // ComboBoxTableCellEditor(extensions);
    // universeextensionColumn.setCellEditor(universeextensionColumnComboEditor);
    UniverseExtCellEditor univExtEditor = new UniverseExtCellEditor(this.extensions, true);
    universeextensionColumn.setCellEditor(univExtEditor);

  }

  /**
   * Method for setting the column renderer. Used to set a renderer for the
   * description field and isElement combo box.
   */
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for owner
    final TableColumn ownerColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.ownerColumnIdx);
    final ComboTableCellRenderer ownerColumnComboRenderer = new ComboTableCellRenderer(Constants.UNIVERSEOWNERTYPES);
    ownerColumn.setCellRenderer(ownerColumnComboRenderer);
    theTable.setVerifyInputWhenFocusTarget(true);

    // Set renderer for name
    final TableColumn nameColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.nameColumnIdx);
    final LimitedSizeTextTableCellRenderer nameRenderer = new LimitedSizeTextTableCellRenderer(Universetable
        .getTablenameColumnSize(), true);
    nameColumn.setCellRenderer(nameRenderer);

    // Set renderer for alias
    final TableColumn aliasColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.aliasColumnIdx);
    final LimitedSizeTextTableCellRenderer aliasRenderer = new LimitedSizeTextTableCellRenderer(Universetable
        .getAliasColumnSize(), false);
    aliasColumn.setCellRenderer(aliasRenderer);

    // Set renderer for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseTablesTableModel.universeextensionColumnIdx);
    // final ComboTableCellRenderer universeextensionColumnComboRenderer = new
    // ComboTableCellRenderer(extensions);
    // universeextensionColumn.setCellRenderer(universeextensionColumnComboRenderer);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);

    theTable.setVerifyInputWhenFocusTarget(true);
  }

  private void setColumnWidths(final JTable theTable) {
    final TableColumn ownerColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.ownerColumnIdx);
    ownerColumn.setPreferredWidth(UniverseTablesTableModel.myColumnWidths[UniverseTablesTableModel.ownerColumnIdx]);

    final TableColumn nameColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.nameColumnIdx);
    nameColumn.setPreferredWidth(UniverseTablesTableModel.myColumnWidths[UniverseTablesTableModel.nameColumnIdx]);

    final TableColumn aliasColumn = theTable.getColumnModel().getColumn(UniverseTablesTableModel.aliasColumnIdx);
    aliasColumn.setPreferredWidth(UniverseTablesTableModel.myColumnWidths[UniverseTablesTableModel.aliasColumnIdx]);

    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseTablesTableModel.universeextensionColumnIdx);
    universeextensionColumn
        .setPreferredWidth(UniverseClassTableModel.myColumnWidths[UniverseTablesTableModel.universeextensionColumnIdx]);
  }

  @Action(enabledProperty = "saveEnabled")
  public Task save() {

    logger.log(Level.INFO, "Save UniverseTable");
    final Task SaveTask = new SaveTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    SaveTask.setInputBlocker(new BusyIndicatorInputBlocker(SaveTask, busyIndicator));

    return SaveTask;
  }

  private class SaveTask extends Task<Void, Void> {

    public SaveTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "Save Universe Table");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getUniverseTablesDataModel().save();
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error saving Universe Table" + e);

        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }

        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }

      tableUTChanged = false;
      setSaveEnabled(false);
      setCancelEnabled(false);
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.info("Discard Edit Universe Table");
      getParentAction("enableTabs").actionPerformed(null);
      return null;
    }
  }

  @Action(enabledProperty = "cancelEnabled")
  public Task discard() {
    final Task DiscardTask = new DiscardTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    DiscardTask.setInputBlocker(new BusyIndicatorInputBlocker(DiscardTask, busyIndicator));

    return DiscardTask;
  }

  @Action
  public void close() {
    logger.log(Level.INFO, "close");
    frame.dispose();
  }

  // Universe table mouse menu
  @Action
  public void addemptyu() {
    logger.fine("ADD " + dataModelController.getUniverseTablesDataModel().getUTTableModel().getRowCount());
    if (universeTablesT.getSelectedRows().length == 0) {
      dataModelController.getUniverseTablesDataModel().getUTTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
          universeTablesT.getSelectedRow());
    } else {
      int[] tmp = universeTablesT.getSelectedRows().clone();
      for (int i = universeTablesT.getSelectedRows().length - 1; i >= 0; i--) {
        dataModelController.getUniverseTablesDataModel().getUTTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
            tmp[i]);
      }
    }
    dataModelController.getUniverseTablesDataModel().getUTTableModel().fireTableDataChanged();
  }

  @Action
  public void removeu() {

    logger.log(Level.INFO, "remove row from UniverseTable table");
    for (int i = universeTablesT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseTablesDataModel().getUTTableModel()
          .removeRow(universeTablesT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseTablesDataModel().getUTTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowu() {

    logger.log(Level.INFO, "duplicate row in UniverseTable table");
    for (int i = universeTablesT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseTablesDataModel().getUTTableModel().duplicateRow(
          universeTablesT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseTablesDataModel().getUTTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupu() {

    logger.log(Level.INFO, "Move row up row in UniverseTable table");
    for (int i = 0; i < universeTablesT.getSelectedRows().length; i++) {
      dataModelController.getUniverseTablesDataModel().getUTTableModel()
          .moveupRow(universeTablesT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseTablesDataModel().getUTTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdownu() {

    logger.log(Level.INFO, "Move row down row in UniverseTable table");
    for (int i = universeTablesT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseTablesDataModel().getUTTableModel().movedownRow(
          universeTablesT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseTablesDataModel().getUTTableModel().fireTableDataChanged();
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  /**
   * Helper function, returns action by name from parent panel
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  // ************************************************
  // Universe Class popup menu
  // ************************************************
  private JPopupMenu createUCPopup(MouseEvent e) {
    JPopupMenu popupUC;
    JMenuItem miUC;
    popupUC = new JPopupMenu();

    int selected = universeTablesT.getSelectedRow();
    // Object o = e.getSource();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removeu"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterowu"));
        miUC.setText("Duplicate row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move up row");
        miUC.setAction(getAction("moverowupu"));
        miUC.setText("Move up row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move down row");
        miUC.setAction(getAction("moverowdownu"));
        miUC.setText("Move down row");
        popupUC.add(miUC);

      }

      miUC = new JMenuItem("Add Empty");
      miUC.setText("Add empty row");
      miUC.setAction(getAction("addemptyu"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);

    } else {

      miUC = new JMenuItem("Add Empty");
      miUC.setAction(getAction("addemptyu"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  private void displayUTMenu(MouseEvent e) {

    if (e.isPopupTrigger()) {
      createUCPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(boolean cancelEnabled) {
    boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  private void handleButtons() {
    boolean saveIsEnabled = false;
    setScreenMessage(null);

    try {
      UniverseTablesDataModel model = dataModelController.getUniverseTablesDataModel();
      errors = model.validateData();
    } catch (Exception e) {
      errors.clear();
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    if (errors.size() > 0) {
      setScreenMessage(errors);
    }

    boolean hasErrors = errors.size() > 0;
    if (tableUTChanged) {
      saveIsEnabled = hasErrors ? false : true;
      getParentAction("disableTabs").actionPerformed(null);
      this.setCancelEnabled(true);
    } else {
      this.setCancelEnabled(false);
    }
    this.setSaveEnabled(saveIsEnabled);
  }

  /**
   * Error handling, sets the message for errorMessageComponent
   * 
   * @param message
   */
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class TableUTSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableUTChanged = true;
      handleButtons();
      setColumnRenderers(universeTablesT);
      setColumnEditors(universeTablesT);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayUTMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayUTMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayUTMenu(e);
    }
  }
}
