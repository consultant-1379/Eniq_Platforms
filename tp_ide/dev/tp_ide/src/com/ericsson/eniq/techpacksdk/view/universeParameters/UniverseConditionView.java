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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import com.distocraft.dc5000.repository.dwhrep.Universecondition;
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
public class UniverseConditionView extends GenericPanel {

  private static final Logger logger = Logger.getLogger(UniverseConditionView.class.getName());

  public static final Color WHITE = Color.WHITE;

  private SingleFrameApplication application;

  private UniverseTabs parentPanel;

  private boolean saveEnabled = false;

  private boolean cancelEnabled = false;

  private boolean tableUCChanged = false;

  boolean editable = true;

  private JTable universeConditionT = new JTable();

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

  public UniverseConditionView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, boolean editable, UniverseTabs parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    this.editable = editable;
    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    tmpversioning = versioning;

    this.extensions = dataModelController.getUniverseConditionDataModel().getUniverseExtensions(
        tmpversioning.getVersionid());

    this.addComponentListener(new MyCompListener());

    // ************** Text panel **********************
    int width = 0;
    for (int colWidt : UniverseConditionTableModel.myColumnWidths)
      width += colWidt;

    Dimension tableDim = new Dimension(width, 300);

    JPanel txtPanel = new JPanel(new GridBagLayout());
    JScrollPane txtPanelS = new JScrollPane(txtPanel);

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // Universe tables
    JLabel universecondition = new JLabel("Universe condition");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(universecondition, c);

    universeConditionT = new UniverseConditionTable(editable);

    UniverseConditionTableModel uctm = dataModelController.getUniverseConditionDataModel().getUCTableModel();
    setModel(universeConditionT, uctm);

    addTableModelListener(uctm, new TableUSelectionListener());

    universeConditionT.setBackground(WHITE);

    // universeConditionT.setEnabled(editable);

    if (editable) {
      universeConditionT.addMouseListener(new TableUSelectionListener());
      universeConditionT.getTableHeader().addMouseListener(new TableUSelectionListener());
    }

    setColumnWidths(universeConditionT);

    setColumnEditors(universeConditionT);
    setColumnRenderers(universeConditionT);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JScrollPane scrollPane = new JScrollPane(universeConditionT);
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
    cancel.setName("UniverseConditionCancel");

    save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");
    save.setName("UniverseConditionSave");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("UniverseConditionClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    buttonPanel.add(errorMessageComponent);

    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    // ************** right & left panels, left panel contains
    // **********************

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
   * Refresh this view
   * 
   */
  public void refresh() {
    dataModelController.getUniverseConditionDataModel().refresh(tmpversioning);

    UniverseConditionTableModel utm = dataModelController.getUniverseConditionDataModel().getUCTableModel();

    addTableModelListener(utm, new TableUSelectionListener());
    setModel(universeConditionT, utm);

    setColumnEditors(universeConditionT);
    setColumnRenderers(universeConditionT);

    tableUCChanged = false;
    setSaveEnabled(false);
    setCancelEnabled(false);
    handleButtons();
    frame.repaint();
  }

  /**
   * Method for setting the column editor of the Description and IsElement
   * columns.
   */
  public void setColumnEditors(final JTable theTable) {

    // Set editor for Classname
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.classnameColumnIdx);
    LimitedSizeCellEditor classnameColumnEditor = new LimitedSizeCellEditor(
        UniverseConditionTableModel.myColumnWidths[UniverseConditionTableModel.classnameColumnIdx], Universecondition
            .getClassnameColumnSize(), true);
    classnameColumn.setCellEditor(classnameColumnEditor);

    // Set editor for universecondition
    final TableColumn universeconditionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.conditionColumnIdx);
    LimitedSizeCellEditor universeconditionColumnEditor = new LimitedSizeCellEditor(
        UniverseConditionTableModel.myColumnWidths[UniverseConditionTableModel.conditionColumnIdx], Universecondition
            .getUniverseconditionColumnSize(), true);
    universeconditionColumn.setCellEditor(universeconditionColumnEditor);

    // Set editor for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.descriptionColumnIdx);
    DescriptionCellEditor descriptionColumnEditor = new DescriptionCellEditor(Universecondition
        .getDescriptionColumnSize(), false, this.editable);
    descriptionColumn.setCellEditor(descriptionColumnEditor);

    // Set editor for condWhere
    final TableColumn condWhereColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.whereColumnIdx);
    DescriptionCellEditor condWhereColumnEditor = new DescriptionCellEditor(Universecondition.getCondwhereColumnSize(),
        false, this.editable);
    condWhereColumn.setCellEditor(condWhereColumnEditor);

    // Set editor for objectclass
    final TableColumn oclassColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.oClassColumnIdx);
    LimitedSizeCellEditor oClassColumnEditor = new LimitedSizeCellEditor(
        UniverseConditionTableModel.myColumnWidths[UniverseConditionTableModel.oClassColumnIdx], Universecondition
            .getCondobjclassColumnSize(), false);
    oclassColumn.setCellEditor(oClassColumnEditor);

    // Set editor for object
    final TableColumn objectColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.objectColumnIdx);
    LimitedSizeCellEditor objectColumnEditor = new LimitedSizeCellEditor(
        UniverseConditionTableModel.myColumnWidths[UniverseConditionTableModel.objectColumnIdx], Universecondition
            .getCondobjectColumnSize(), false);
    objectColumn.setCellEditor(objectColumnEditor);

    // Set editor for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.promptColumnIdx);
    LimitedSizeCellEditor promptColumnEditor = new LimitedSizeCellEditor(
        UniverseConditionTableModel.myColumnWidths[UniverseConditionTableModel.promptColumnIdx], Universecondition
            .getPrompttextColumnSize(), false);
    promptColumn.setCellEditor(promptColumnEditor);

    // Set editor for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.universeextensionColumnIdx);
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

    // Set renderer for Classname
    final TableColumn classnameColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.classnameColumnIdx);
    LimitedSizeTextTableCellRenderer classnameColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getClassnameColumnSize(), true);
    classnameColumn.setCellRenderer(classnameColumnRenderer);

    // Set renderer for universecondition
    final TableColumn universeconditionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.conditionColumnIdx);
    LimitedSizeTextTableCellRenderer universeconditionColumnRenderer = new LimitedSizeTextTableCellRenderer(
        Universecondition.getUniverseconditionColumnSize(), true);
    universeconditionColumn.setCellRenderer(universeconditionColumnRenderer);

    // Set renderer for description
    final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.descriptionColumnIdx);
    LimitedSizeTextTableCellRenderer descriptionColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getDescriptionColumnSize(), false);
    descriptionColumn.setCellRenderer(descriptionColumnRenderer);

    // Set renderer for condWhere
    final TableColumn condWhereColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.whereColumnIdx);
    LimitedSizeTextTableCellRenderer condWhereColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getCondwhereColumnSize(), false);
    condWhereColumn.setCellRenderer(condWhereColumnRenderer);

    // Set renderer for objectclass
    final TableColumn oclassColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.oClassColumnIdx);
    LimitedSizeTextTableCellRenderer oClassColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getCondobjclassColumnSize(), false);
    oclassColumn.setCellRenderer(oClassColumnRenderer);

    // Set renderer for object
    final TableColumn objectColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.objectColumnIdx);
    LimitedSizeTextTableCellRenderer objectColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getCondobjectColumnSize(), false);
    objectColumn.setCellRenderer(objectColumnRenderer);

    // Set renderer for prompt
    final TableColumn promptColumn = theTable.getColumnModel().getColumn(UniverseConditionTableModel.promptColumnIdx);
    LimitedSizeTextTableCellRenderer promptColumnRenderer = new LimitedSizeTextTableCellRenderer(Universecondition
        .getPrompttextColumnSize(), false);
    promptColumn.setCellRenderer(promptColumnRenderer);

    // Set renderer for universe extension
    final TableColumn universeextensionColumn = theTable.getColumnModel().getColumn(
        UniverseConditionTableModel.universeextensionColumnIdx);
    // final ComboTableCellRenderer universeextensionColumnComboRenderer = new
    // ComboTableCellRenderer(extensions);
    // universeextensionColumn.setCellRenderer(universeextensionColumnComboRenderer);
    DefaultTableCellRenderer universeExtColumnRenderer = new DefaultTableCellRenderer();
    universeextensionColumn.setCellRenderer(universeExtColumnRenderer);

    theTable.setVerifyInputWhenFocusTarget(true);
  }

  private void setColumnWidths(final JTable theTable) {

    for (int i = 0; i < UniverseConditionTableModel.myColumnWidths.length; i++) {
      int widt = UniverseConditionTableModel.myColumnWidths[i];

      TableColumn column = theTable.getColumnModel().getColumn(i);

      column.setPreferredWidth(widt);
    }
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

      logger.log(Level.INFO, "Save UniverseCondition");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getUniverseConditionDataModel().save();
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {

        logger.warning("Error saving Universe Condition" + e);

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
    logger.fine("ADD " + dataModelController.getUniverseConditionDataModel().getUCTableModel().getRowCount());
    if (universeConditionT.getSelectedRows().length == 0) {
      dataModelController.getUniverseConditionDataModel().getUCTableModel().addEmptyNewRow(
          tmpversioning.getVersionid(), universeConditionT.getSelectedRow());
    } else {
      int[] tmp = universeConditionT.getSelectedRows().clone();
      for (int i = universeConditionT.getSelectedRows().length - 1; i >= 0; i--) {
        dataModelController.getUniverseConditionDataModel().getUCTableModel().addEmptyNewRow(
            tmpversioning.getVersionid(), tmp[i]);
      }
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void removeu() {

    logger.log(Level.INFO, "remove row from Universe Condition table");
    for (int i = universeConditionT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseConditionDataModel().getUCTableModel().removeRow(
          universeConditionT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowu() {

    logger.log(Level.INFO, "duplicate row in Universe Condition table");
    for (int i = universeConditionT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseConditionDataModel().getUCTableModel().duplicateRow(
          universeConditionT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowupu() {

    logger.log(Level.INFO, "Move row up row in Universe Condition table");
    for (int i = 0; i < universeConditionT.getSelectedRows().length; i++) {
      dataModelController.getUniverseConditionDataModel().getUCTableModel().moveupRow(
          universeConditionT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
  }

  @Action
  public void moverowdownu() {

    logger.log(Level.INFO, "Move row down row in Universe Condition table");
    for (int i = universeConditionT.getSelectedRows().length - 1; i >= 0; i--) {
      dataModelController.getUniverseConditionDataModel().getUCTableModel().movedownRow(
          universeConditionT.getSelectedRows()[i]);
    }
    dataModelController.getUniverseConditionDataModel().getUCTableModel().fireTableDataChanged();
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

    int selected = universeConditionT.getSelectedRow();
    // Object o = e.getSource();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removeu"));
        miUC.setText("Remove row");
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate");
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

  private void displayUMenu(MouseEvent e) {

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
      UniverseConditionDataModel model = dataModelController.getUniverseConditionDataModel();
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

  private class TableUSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableUCChanged = true;
      handleButtons();
      setColumnRenderers(universeConditionT);
      setColumnEditors(universeConditionT);
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayUMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayUMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayUMenu(e);
    }
  }

  private class UniverseConditionTable extends JTable {

    public static final String EMPTY_CELL_VALUE = "";

    private boolean editable;

    public UniverseConditionTable(boolean editable) {
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
        if (column == UniverseConditionTableModel.descriptionColumnIdx) {
          return true;
        }
        if (column == UniverseConditionTableModel.whereColumnIdx) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  private class MyCompListener implements ComponentListener {

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
      if (dataModelController.getUniverseConditionDataModel().dataRefreshed) {
        refresh();
        dataModelController.getUniverseConditionDataModel().dataRefreshed = false;
      }
    }
  }
}
