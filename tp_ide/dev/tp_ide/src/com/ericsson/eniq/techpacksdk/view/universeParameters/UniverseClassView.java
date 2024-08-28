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
import javax.swing.JOptionPane;
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

import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.GenericPanel;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class UniverseClassView extends GenericPanel {

  private static final Logger logger = Logger.getLogger(UniverseClassView.class.getName());

  public static final Color WHITE = Color.WHITE;

  private SingleFrameApplication application;

  private UniverseTabs parentPanel;

  private boolean saveEnabled = false;

  private boolean cancelEnabled = false;

  private boolean tableUCChanged = false;

  boolean editable = true;

  private JTable universeClassT = new JTable();

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

  public UniverseClassView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    this.editable = editable;
    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    tmpversioning = versioning;

    this.extensions = dataModelController.getUniverseClassDataModel().getUniverseExtensions(
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

    // Universeclass
    JLabel universeclass = new JLabel("Universe class");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(universeclass, c);

    universeClassT = new UniverseClassTable(editable);

    UniverseClassTableModel unvclasstm = dataModelController.getUniverseClassDataModel().getUCTableModel();
    setModel(universeClassT, unvclasstm);

    addTableModelListener(unvclasstm, new TableUCSelectionListener());

    universeClassT.setBackground(WHITE);

    // universeClassT.setEnabled(editable);

    if (editable) {
      universeClassT.addMouseListener(new TableUCSelectionListener());
      universeClassT.getTableHeader().addMouseListener(new TableUCSelectionListener());
    }

    setColumnWidths(universeClassT);

    setColumnEditors(universeClassT);
    setColumnRenderers(universeClassT);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(universeClassT);
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
    cancel.setName("UniverseClassCancel");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");
    save.setName("UniverseClassSave");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseClassClose");

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
   * Overridden method for setting the column editor of the Description and
   * IsElement columns.
   */
  public void setColumnEditors(final JTable theTable) {

    // Set editor for parent
    final TableColumn parentColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.parentColumnIdx);
    LimitedSizeCellEditor parentColumnEditor = new LimitedSizeCellEditor(
        UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.parentColumnIdx], Universeclass
            .getParentColumnSize(), false);
    parentColumn.setCellEditor(parentColumnEditor);

    // Set editor for class name
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.classnameColumnIdx);
    LimitedSizeCellEditor classnameColumnEditor = new LimitedSizeCellEditor(
        UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.classnameColumnIdx], Universeclass
            .getClassnameColumnSize(), true);
    classnameColumn.setCellEditor(classnameColumnEditor);

    // Set editor for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.descriptionColumnIdx);
    DescriptionCellEditor descriptionColumnEditor = new DescriptionCellEditor(Universeclass.getDescriptionColumnSize(),
        false, this.editable);
    descriptionColumn.setCellEditor(descriptionColumnEditor);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.universeextensionColumnIdx);
    // final ComboBoxTableCellEditor universeextensionColumnComboEditor = new
    // ComboBoxTableCellEditor(extensions);
    UniverseExtCellEditor univExtEditor = new UniverseExtCellEditor(this.extensions, true);
    universeextensionColumn.setCellEditor(univExtEditor);
  }

  /**
   * Overridden method for setting the column renderer. Used to set a renderer
   * for the description field and isElement combo box.
   */
  public void setColumnRenderers(final JTable theTable) {

    // Set renderer for parent
    final TableColumn parentColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.parentColumnIdx);
    final LimitedSizeTextTableCellRenderer parentRenderer = new LimitedSizeTextTableCellRenderer(Universeclass
        .getParentColumnSize(), false);
    parentColumn.setCellRenderer(parentRenderer);

    // Set renderer for class name
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.classnameColumnIdx);
    final LimitedSizeTextTableCellRenderer classnameRenderer = new LimitedSizeTextTableCellRenderer(Universeclass
        .getClassnameColumnSize(), true);
    classnameColumn.setCellRenderer(classnameRenderer);

    // Set renderer for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.descriptionColumnIdx);
    final LimitedSizeTextTableCellRenderer descriptionRenderer = new LimitedSizeTextTableCellRenderer(Universeclass
        .getDescriptionColumnSize(), false);
    descriptionColumn.setCellRenderer(descriptionRenderer);

    // Set renderer for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.universeextensionColumnIdx);
    // final ComboTableCellRenderer universeextensionColumnComboRenderer = new
    // ComboTableCellRenderer(extensions);
    // universeextensionColumn.setCellRenderer(universeextensionColumnComboRenderer);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);

    theTable.setVerifyInputWhenFocusTarget(true);

  }

  private void setColumnWidths(final JTable theTable) {
    final TableColumn parentColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.parentColumnIdx);
    parentColumn.setPreferredWidth(UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.parentColumnIdx]);

    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(UniverseClassTableModel.classnameColumnIdx);
    classnameColumn
        .setPreferredWidth(UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.classnameColumnIdx]);

    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.descriptionColumnIdx);
    descriptionColumn
        .setPreferredWidth(UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.descriptionColumnIdx]);

    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseClassTableModel.universeextensionColumnIdx);
    universeextensionColumn
        .setPreferredWidth(UniverseClassTableModel.myColumnWidths[UniverseClassTableModel.universeextensionColumnIdx]);
  }

  /**
   * Perform refresh
   * 
   */
  public void refresh() {

    dataModelController.getUniverseClassDataModel().refresh(tmpversioning);

    UniverseClassTableModel uctm = dataModelController.getUniverseClassDataModel().getUCTableModel();

    addTableModelListener(uctm, new TableUCSelectionListener());
    setModel(universeClassT, uctm);

    setColumnEditors(universeClassT);
    setColumnRenderers(universeClassT);

    tableUCChanged = false;
    setSaveEnabled(false);
    setCancelEnabled(false);
    frame.repaint();
    handleButtons();

  }

  @Action(enabledProperty = "saveEnabled")
  public Task save() {

    logger.log(Level.INFO, "Save UniverseClass");
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

      String warning = dataModelController.getUniverseClassDataModel().isOkToSave();
      if (warning.length() > 0
          && JOptionPane.showConfirmDialog(frame, warning, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
        return null;

      logger.log(Level.INFO, "Save Universe Class");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getUniverseClassDataModel().save();
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error saving Universe Class" + e);

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

      tableUCChanged = false;
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

      logger.info("Discard Edit Universe Class");

      // refresh();
      // parentPanel.setCaller(UniverseClassView.this);
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

  // Universe Class mouse menu
  @Action
  public void addemptyuc() {
    logger.fine("ADD " + dataModelController.getUniverseClassDataModel().getUCTableModel().getRowCount());
    if (universeClassT.getSelectedRows().length == 0) {
      dataModelController.getUniverseClassDataModel().getUCTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
          universeClassT.getSelectedRow());
    } else {
      int[] tmp = universeClassT.getSelectedRows().clone();
      for (int i = universeClassT.getSelectedRows().length - 1; i >= 0; i--) {
        dataModelController.getUniverseClassDataModel().getUCTableModel().addEmptyNewRow(tmpversioning.getVersionid(),
            tmp[i]);
      }
    }
    dataModelController.getUniverseClassDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void removeuc() {

    logger.log(Level.INFO, "remove row from Universe Class table");
    for (int i = universeClassT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseClassDataModel().getUCTableModel().removeRow(universeClassT.getSelectedRows()[i],
          frame);
    }
    dataModelController.getUniverseClassDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowuc() {

    logger.log(Level.INFO, "duplicate row in Universe Class table");
    for (int i = universeClassT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseClassDataModel().getUCTableModel().duplicateRow(
          universeClassT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseClassDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupuc() {

    logger.log(Level.INFO, "Move row up row in Universe Class table");
    for (int i = 0; i < universeClassT.getSelectedRows().length; i++) {
      dataModelController.getUniverseClassDataModel().getUCTableModel().moveupRow(universeClassT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseClassDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdownuc() {

    logger.log(Level.INFO, "Move row down row in Universe Class table");
    for (int i = universeClassT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseClassDataModel().getUCTableModel()
          .movedownRow(universeClassT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseClassDataModel().getUCTableModel().fireTableDataChanged();
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

    int selected = universeClassT.getSelectedRow();
    // Object o = e.getSource();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removeuc"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterowuc"));
        miUC.setText("Duplicate row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move up rows");
        miUC.setAction(getAction("moverowupuc"));
        miUC.setText("Move Up Row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Move down row");
        miUC.setAction(getAction("moverowdownuc"));
        miUC.setText("Move down row");
        popupUC.add(miUC);

      }

      miUC = new JMenuItem("Add Empty");
      miUC.setText("Add empty row");
      miUC.setAction(getAction("addemptyuc"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);

    } else {

      miUC = new JMenuItem("Add Empty");
      miUC.setAction(getAction("addemptyuc"));
      miUC.setText("Add empty row");
      popupUC.add(miUC);
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  private void displayUCMenu(MouseEvent e) {

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
      UniverseClassDataModel model = dataModelController.getUniverseClassDataModel();
      errors = model.validateData();
    } catch (Exception e) {
      errors.clear();
      e.printStackTrace();
    }

    if (errors.size() > 0) {
      setScreenMessage(errors);
    }

    boolean hasErrors = errors.size() > 0;
    if (tableUCChanged) {
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

  private class TableUCSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableUCChanged = true;
      handleButtons();
      setColumnRenderers(universeClassT);
      setColumnEditors(universeClassT);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayUCMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayUCMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayUCMenu(e);
    }
  }

  private class UniverseClassTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseClassTable(boolean editable) {
      super();
      this.editable = editable;
    }

    public boolean isEditable() {
      return this.editable;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      if (this.editable) {
        return true;
      } else {
        if (column == UniverseClassTableModel.descriptionColumnIdx) {
          return true;
        } else {
          return false;
        }
      }
    }
  }
}
