package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.Color;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Externalstatement;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.GenericPanel;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextField;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.ExternalStatementDataModel.DESTableModel;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.ExternalStatementDataModel.ESTableModel;

public class ManageExternalStatementView extends GenericPanel {

  Vector<String> tableTreeErrors = new Vector<String>();

  private ErrorMessageComponent errorMessageComponent;

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(ManageExternalStatementView.class.getName());

  public static final Color WHITE = Color.WHITE;

  private SingleFrameApplication application;

  private GeneralTechPackTab parentPanel;

  private boolean saveEnabled = false;

  private boolean cancelEnabled = false;

  private boolean tableESChanged = false;

  private boolean tableDESChanged = false;

  boolean editable = true;

  private JTable externalstatementT = new JTable();

  private JTable deexternalstatementT = new JTable();

  private DataModelController dataModelController;

  private JFrame frame;

  Versioning tmpversioning;

  public ManageExternalStatementView(final SingleFrameApplication application,
      final DataModelController dataModelController, final Versioning versioning, final boolean editable,
      final GeneralTechPackTab parentPanel, final JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;

    this.dataModelController = dataModelController;

    this.application = application;

    this.parentPanel = parentPanel;

    this.editable = editable;

    tmpversioning = versioning;

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 3;
    c.gridx = 0;
    c.gridy = 0;

    // External Statements
    this.add(new JLabel("External Statements"), c);

    externalstatementT = new JTable();
    externalstatementT.setRowSelectionAllowed(true);
    externalstatementT.setName("ExternalStatementTable");

    final ESTableModel estm = this.dataModelController.getExternalStatementDataModel().getESTableModel();
    addTableModelListener(estm, new TableESSelectionListener());
    setModel(externalstatementT, estm);
    externalstatementT.setBackground(WHITE);
    // externalstatementT.setEnabled(this.editable);

    // if (this.editable) {
    externalstatementT.addMouseListener(new TableESSelectionListener());
    externalstatementT.getTableHeader().addMouseListener(new TableESSelectionListener());
    // }

    final LimitedSizeTextField statementNameField = new LimitedSizeTextField(255, Externalstatement
        .getStatementnameColumnSize(), true);
    statementNameField.setBorder(BorderFactory.createEmptyBorder());
    statementNameField.setName("StatementNameField");
    final DefaultCellEditor statementNameEditor = new DefaultCellEditor(statementNameField);
    statementNameEditor.setClickCountToStart(2);

    final LimitedSizeTextField StatementField = new LimitedSizeTextField(32000, Externalstatement
        .getStatementColumnSize(), true);
    StatementField.setBorder(BorderFactory.createEmptyBorder());
    StatementField.setName("StatemetnField");
    final DefaultCellEditor statementEditor = new DefaultCellEditor(StatementField);
    statementEditor.setClickCountToStart(2);

    // Set the name and statement column editors enabled based on the
    // current edit/view mode.
    statementNameEditor.getComponent().setEnabled(editable);
    statementEditor.getComponent().setEnabled(editable);

    externalstatementT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
    externalstatementT.getColumnModel().getColumn(0)
        .setCellEditor(new StringActionTableCellEditor(statementNameEditor));
    setConnectionColumn(externalstatementT, externalstatementT.getColumnModel().getColumn(1));
    externalstatementT.getColumnModel().getColumn(2).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
    externalstatementT.getColumnModel().getColumn(2).setCellEditor(new StringActionTableCellEditor(statementEditor));

    externalstatementT.setToolTipText("");

    c.fill = GridBagConstraints.BOTH;
    c.gridy = 1;
    c.weighty = 1;

    final JScrollPane scrollPane = new JScrollPane(externalstatementT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    // scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setName("ExternalStatementPane");
    this.add(scrollPane, c);

    // De-External Statements
    c.fill = GridBagConstraints.NONE;
    c.gridy = 2;
    c.weighty = 0;
    this.add(new JLabel("De-External Statements"), c);

    deexternalstatementT = new JTable();
    deexternalstatementT.setRowSelectionAllowed(true);
    deexternalstatementT.setName("DeExternalStatementTable");

    final DESTableModel destm = this.dataModelController.getExternalStatementDataModel().getDESTableModel();
    addTableModelListener(destm, new TableDESSelectionListener());
    setModel(deexternalstatementT, destm);
    deexternalstatementT.setBackground(WHITE);
    // deexternalstatementT.setEnabled(editable);

    // if (editable) {
    deexternalstatementT.addMouseListener(new TableDESSelectionListener());
    deexternalstatementT.getTableHeader().addMouseListener(new TableDESSelectionListener());
    // }

    final LimitedSizeTextField destatementNameField = new LimitedSizeTextField(255, Externalstatement
        .getStatementnameColumnSize(), true);
    destatementNameField.setBorder(BorderFactory.createEmptyBorder());
    destatementNameField.setName("DeStatementNameField");
    DefaultCellEditor destatementNameEditor = new DefaultCellEditor(destatementNameField);
    destatementNameEditor.setClickCountToStart(2);

    final LimitedSizeTextField deStatementField = new LimitedSizeTextField(32000, Externalstatement
        .getStatementColumnSize(), true);
    deStatementField.setName("DeStatementField");
    deStatementField.setBorder(BorderFactory.createEmptyBorder());
    final DefaultCellEditor destatementEditor = new DefaultCellEditor(deStatementField);
    destatementEditor.setClickCountToStart(2);

    // Set the name and statement column editors enabled based on the
    // current edit/view mode.
    destatementNameEditor.getComponent().setEnabled(editable);
    destatementEditor.getComponent().setEnabled(editable);
    deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
    deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
        new StringActionTableCellEditor(destatementNameEditor));
    setConnectionColumn(deexternalstatementT, deexternalstatementT.getColumnModel().getColumn(1));
    deexternalstatementT.getColumnModel().getColumn(2).setCellEditor(
        new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
    deexternalstatementT.getColumnModel().getColumn(2)
        .setCellEditor(new StringActionTableCellEditor(destatementEditor));

    deexternalstatementT.setToolTipText("");

    c.fill = GridBagConstraints.BOTH;
    c.gridy = 3;
    c.weighty = 1;
    externalstatementT.repaint();
    final JScrollPane descrollPane = new JScrollPane(deexternalstatementT, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    descrollPane.setName("DeExternalStatementScrollPane");
    this.add(descrollPane, c);

    // View mode
    if (this.editable == false) {

      // externalstatementT.setEnabled(false);
      // deexternalstatementT.setEnabled(false);
    }

    // ************** buttons **********************
    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.SOUTHEAST;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;
    c.gridy = 4;

    final JButton cancel = new JButton("Discard");
    cancel.setActionCommand("discard");
    cancel.setAction(getAction("discard"));
    cancel.setToolTipText("Discard");
    cancel.setName("ExternalStatementsViewCancel");

    c.weightx = 0;
    c.gridx = 1;

    final JButton save = new JButton("Save");
    save.setActionCommand("save");
    save.setAction(getAction("save"));
    save.setToolTipText("Save");
    save.setName("ExternalStatementsViewSave");

    c.gridx = 2;

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getParentAction("closeDialog"));
    closeDialog.setName("ExternalStatementsViewClose");

    closeDialog.setEnabled(true);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

    this.add(buttonPanel, c);

    setScreenMessage(null);
  }

  public void setConnectionColumn(JTable table, TableColumn connectionColumn) {

    // Set the combobox editor. It is enabled based on the edit mode.
    JComboBox con = new JComboBox();
    con.addItem("dwh");
    con.addItem("dwhrep");
    DefaultCellEditor editor = new DefaultCellEditor(con);
    editor.getComponent().setEnabled(editable);
    connectionColumn.setCellEditor(editor);

    // Set up the renderer for connection cells.
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    connectionColumn.setCellRenderer(renderer);
  }

  @Action(enabledProperty = "saveEnabled")
  public Task save() {

    logger.log(Level.INFO, "Save External Statements");
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

      logger.log(Level.INFO, "Save External Statements");

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getExternalStatementDataModel().save();
        
        
        ((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        dataModelController.getRockFactory().getConnection().commit();


      } catch (Exception e) {

        logger.warning("Error saving External Statement" + e);

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

      tableESChanged = false;
      tableDESChanged = false;
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);
      
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);

      return null;
    }
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.info("Discard Edit External Statements");

      dataModelController.getExternalStatementDataModel().refresh(tmpversioning);

      ESTableModel estm = dataModelController.getExternalStatementDataModel().getESTableModel();
      addTableModelListener(estm, new TableESSelectionListener());
      setModel(externalstatementT, estm);

      LimitedSizeTextField statementNameField = new LimitedSizeTextField(255, Externalstatement
          .getStatementnameColumnSize(), true);
      statementNameField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor statementNameEditor = new DefaultCellEditor(statementNameField);
      statementNameEditor.setClickCountToStart(2);

      LimitedSizeTextField StatementField = new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(),
          true);
      StatementField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor statementEditor = new DefaultCellEditor(StatementField);
      statementEditor.setClickCountToStart(2);

      // Set the name and statement column editors enabled based on the
      // current edit/view mode.
      statementNameEditor.getComponent().setEnabled(editable);
      statementEditor.getComponent().setEnabled(editable);

      externalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
      externalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new StringActionTableCellEditor(statementNameEditor));
      setConnectionColumn(externalstatementT, externalstatementT.getColumnModel().getColumn(1));
      externalstatementT.getColumnModel().getColumn(2).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
      externalstatementT.getColumnModel().getColumn(2).setCellEditor(new StringActionTableCellEditor(statementEditor));
      externalstatementT.setToolTipText("");

      DESTableModel destm = dataModelController.getExternalStatementDataModel().getDESTableModel();
      addTableModelListener(destm, new TableDESSelectionListener());
      setModel(deexternalstatementT, destm);

      LimitedSizeTextField destatementNameField = new LimitedSizeTextField(255, Externalstatement
          .getStatementnameColumnSize(), true);
      destatementNameField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor destatementNameEditor = new DefaultCellEditor(destatementNameField);
      destatementNameEditor.setClickCountToStart(2);

      LimitedSizeTextField deStatementField = new LimitedSizeTextField(32000, Externalstatement
          .getStatementColumnSize(), true);
      deStatementField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor destatementEditor = new DefaultCellEditor(deStatementField);
      destatementEditor.setClickCountToStart(2);

      // Set the name and statement column editors enabled based on the
      // current edit/view mode.
      destatementNameEditor.getComponent().setEnabled(editable);
      destatementEditor.getComponent().setEnabled(editable);

      deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
      deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new StringActionTableCellEditor(statementNameEditor));
      setConnectionColumn(deexternalstatementT, deexternalstatementT.getColumnModel().getColumn(1));
      deexternalstatementT.getColumnModel().getColumn(2).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
      deexternalstatementT.getColumnModel().getColumn(2)
          .setCellEditor(new StringActionTableCellEditor(statementEditor));
      deexternalstatementT.setToolTipText("");

      tableESChanged = false;
      tableDESChanged = false;
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);

      getParentAction("enableTabs").actionPerformed(null);
      frame.repaint();

      return null;
    }
  }

  /**
   * DiscardTask action
   * 
   * @return
   */
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

  // External Statement mouse menu
  @Action
  public void addemptyes() {

    logger.fine("ADD " + this.dataModelController.getExternalStatementDataModel().getESTableModel().getRowCount());

    if (this.externalstatementT.getCellEditor() != null) {
      this.externalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.externalstatementT.getSelectedRows();

    if (cnt.length == 0) {
      dataModelController.getExternalStatementDataModel().getESTableModel().addEmptyNewRow(-1);
    } else {
      for (int i = cnt.length - 1; i >= 0; i--) {
        dataModelController.getExternalStatementDataModel().getESTableModel().addEmptyNewRow(cnt[i]);
      }
    }
    this.dataModelController.getExternalStatementDataModel().getESTableModel().fireTableDataChanged();
  }

  @Action
  public void removees() {

    logger.log(Level.INFO, "removees");

    if (externalstatementT.getCellEditor() != null) {
      externalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.externalstatementT.getSelectedRows();

    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getESTableModel().removeRow(cnt[i]);
    }
    dataModelController.getExternalStatementDataModel().getESTableModel().fireTableDataChanged();
  }

  @Action
  public void moveupes() {

    logger.log(Level.INFO, "moveupes");

    if (externalstatementT.getCellEditor() != null) {
      externalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.externalstatementT.getSelectedRows();

    for (int i = 0; i < cnt.length; i++) {
      dataModelController.getExternalStatementDataModel().getESTableModel().moveUpRow(cnt[i]);
    }
    dataModelController.getExternalStatementDataModel().getESTableModel().fireTableDataChanged();
  }

  @Action
  public void movedownes() {

    logger.log(Level.INFO, "movedownes");

    if (externalstatementT.getCellEditor() != null) {
      externalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.externalstatementT.getSelectedRows();

    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getESTableModel().moveDownRow(cnt[i]);
    }

    dataModelController.getExternalStatementDataModel().getESTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowes() {

    logger.log(Level.INFO, "duplicaterowes");

    if (externalstatementT.getCellEditor() != null) {
      externalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.externalstatementT.getSelectedRows();

    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getESTableModel().duplicateRow(cnt[i]);
    }

    dataModelController.getExternalStatementDataModel().getESTableModel().fireTableDataChanged();
  }

  // De-External Statement mouse menu
  @Action
  public void addemptydes() {
    logger.fine("ADD " + dataModelController.getExternalStatementDataModel().getDESTableModel().getRowCount());

    if (deexternalstatementT.getCellEditor() != null) {
      deexternalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.deexternalstatementT.getSelectedRows();

    if (cnt.length == 0) {
      dataModelController.getExternalStatementDataModel().getDESTableModel().addEmptyNewRow(-1);
    } else {

      for (int i = cnt.length - 1; i >= 0; i--) {
        dataModelController.getExternalStatementDataModel().getDESTableModel().addEmptyNewRow(cnt[i]);
      }
    }

    dataModelController.getExternalStatementDataModel().getDESTableModel().fireTableDataChanged();
  }

  @Action
  public void removedes() {

    logger.log(Level.INFO, "removedes");

    if (deexternalstatementT.getCellEditor() != null) {
      deexternalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.deexternalstatementT.getSelectedRows();

    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getDESTableModel().removeRow(cnt[i]);
    }

    dataModelController.getExternalStatementDataModel().getDESTableModel().fireTableDataChanged();
  }

  @Action
  public void moveupdes() {

    logger.log(Level.INFO, "moveupdes");

    if (deexternalstatementT.getCellEditor() != null) {
      deexternalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.deexternalstatementT.getSelectedRows();
    for (int i = 0; i < cnt.length; i++) {
      dataModelController.getExternalStatementDataModel().getDESTableModel().moveUpRow(cnt[i]);
    }
    dataModelController.getExternalStatementDataModel().getDESTableModel().fireTableDataChanged();
  }

  @Action
  public void movedowndes() {

    logger.log(Level.INFO, "movedowndes");

    if (deexternalstatementT.getCellEditor() != null) {
      deexternalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.deexternalstatementT.getSelectedRows();

    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getDESTableModel().moveDownRow(cnt[i]);
    }

    dataModelController.getExternalStatementDataModel().getDESTableModel().fireTableDataChanged();
  }

  @Action
  public void duplicaterowdes() {

    logger.log(Level.INFO, "duplicaterowdes");

    if (deexternalstatementT.getCellEditor() != null) {
      deexternalstatementT.getCellEditor().cancelCellEditing();
    }

    int cnt[] = this.deexternalstatementT.getSelectedRows();
    for (int i = cnt.length - 1; i >= 0; i--) {
      dataModelController.getExternalStatementDataModel().getDESTableModel().duplicateRow(cnt[i]);
    }
    dataModelController.getExternalStatementDataModel().getDESTableModel().fireTableDataChanged();
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
  // External Statement popup menu
  // ************************************************
  private JPopupMenu createESPopup(MouseEvent e) {

    JPopupMenu popupES;
    JMenuItem miES;
    popupES = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (this.externalstatementT.getSelectedRow() > -1) {

        miES = new JMenuItem("Remove");
        miES.setAction(getAction("removees"));
        popupES.add(miES);
      }

      miES = new JMenuItem("Add Empty");
      miES.setAction(getAction("addemptyes"));
      popupES.add(miES);

      miES = new JMenuItem("Duplicate rows");
      miES.setAction(getAction("duplicaterowes"));
      popupES.add(miES);

      miES = new JMenuItem("Move up row");
      miES.setAction(getAction("moveupes"));
      popupES.add(miES);

      miES = new JMenuItem("Move up down");
      miES.setAction(getAction("movedownes"));
      popupES.add(miES);

    } else {

      miES = new JMenuItem("Add Empty");
      miES.setAction(getAction("addemptyes"));
      popupES.add(miES);
    }

    popupES.setOpaque(true);
    popupES.setLightWeightPopupEnabled(true);

    return popupES;
  }

  private void displayESMenu(MouseEvent e) {

    // Show pop-up menu (if edit mode is active)
    if (e.isPopupTrigger() && editable) {
      createESPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  // ************************************************
  // DE-External Statement popup menu
  // ************************************************
  private JPopupMenu createDESPopup(MouseEvent e) {

    JPopupMenu popupDES;
    JMenuItem miDES;
    popupDES = new JPopupMenu();

    if (e.getSource() instanceof JTable) {
      if (deexternalstatementT.getSelectedRow() > -1) {
        miDES = new JMenuItem("Remove");
        miDES.setAction(getAction("removedes"));
        popupDES.add(miDES);
      }

      miDES = new JMenuItem("Add Empty");
      miDES.setAction(getAction("addemptydes"));
      popupDES.add(miDES);

      miDES = new JMenuItem("Duplicate row");
      miDES.setAction(getAction("duplicaterowdes"));
      popupDES.add(miDES);

      miDES = new JMenuItem("Move up row");
      miDES.setAction(getAction("moveupdes"));
      popupDES.add(miDES);

      miDES = new JMenuItem("Move down row");
      miDES.setAction(getAction("movedowndes"));
      popupDES.add(miDES);

    } else {

      miDES = new JMenuItem("Add Empty");
      miDES.setAction(getAction("addemptydes"));
      popupDES.add(miDES);
    }

    popupDES.setOpaque(true);
    popupDES.setLightWeightPopupEnabled(true);

    return popupDES;
  }

  private void displayenu(MouseEvent e) {

    // Show pop-up menu (if edit mode is active)
    if (e.isPopupTrigger() && editable) {
      createDESPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(boolean saveEnabled) {
    boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(boolean cancelEnabled) {
    boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  private void handleButtons() {

    boolean saveIsEnabled = false;

    setScreenMessage(null);
    try {
      ExternalStatementDataModel model = dataModelController.getExternalStatementDataModel();
      tableTreeErrors = model.validateData();
    } catch (Exception e) {
      tableTreeErrors.clear();
      e.printStackTrace();
    }
    if (tableTreeErrors.size() > 0) {
      setScreenMessage(tableTreeErrors);
    }

    boolean hasErrors = tableTreeErrors.size() > 0;

    if (tableESChanged || tableDESChanged) {
      saveIsEnabled = hasErrors ? false : true;
      getParentAction("disableTabs").actionPerformed(null);
      this.setCancelEnabled(true);
    } else {
      this.setCancelEnabled(false);
    }
    this.setSaveEnabled(saveIsEnabled);

    // Disable save in view mode.
    if (!editable) {
      this.setSaveEnabled(false);
    }
  }

  /**
   * Sets the messages for errorMessageComponent
   * 
   * @param message
   */
  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class TableESSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableESChanged = true;
      handleButtons();

      LimitedSizeTextField statementNameField = new LimitedSizeTextField(255, Externalstatement
          .getStatementnameColumnSize(), true);
      statementNameField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor statementNameEditor = new DefaultCellEditor(statementNameField);
      statementNameEditor.setClickCountToStart(2);

      LimitedSizeTextField StatementField = new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(),
          true);
      StatementField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor statementEditor = new DefaultCellEditor(StatementField);
      statementEditor.setClickCountToStart(2);

      // Set the name and statement column editors enabled based on the
      // current edit/view mode.
      statementNameEditor.getComponent().setEnabled(editable);
      statementEditor.getComponent().setEnabled(editable);

      externalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
      externalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new StringActionTableCellEditor(statementNameEditor));
      setConnectionColumn(externalstatementT, externalstatementT.getColumnModel().getColumn(1));
      externalstatementT.getColumnModel().getColumn(2).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
      externalstatementT.getColumnModel().getColumn(2).setCellEditor(new StringActionTableCellEditor(statementEditor));
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

      // JTableHeader header = externalstatementT.getTableHeader();

      // TableColumnModel columns = header.getColumnModel();

      // if (!columns.getColumnSelectionAllowed())
      // return;
      /*
       * int column = externalstatementT.columnAtPoint(e.getPoint());
       * 
       * if (column == -1) return;
       * 
       * int count = externalstatementT.getRowCount();
       * 
       * if (count != 0) externalstatementT.setRowSelectionInterval(0, count -
       * 1);
       * 
       * ListSelectionModel selection = externalstatementT.getSelectionModel();
       * 
       * if (e.isShiftDown()) { int anchor =
       * selection.getAnchorSelectionIndex(); int lead =
       * selection.getLeadSelectionIndex();
       * 
       * if (anchor != -1) { boolean old = selection.getValueIsAdjusting();
       * selection.setValueIsAdjusting(true);
       * 
       * boolean anchorSelected = selection.isSelectedIndex(anchor);
       * 
       * if (lead != -1) { if (anchorSelected)
       * selection.removeSelectionInterval(anchor, lead); else
       * selection.addSelectionInterval(anchor, lead); // The latter is quite
       * unintuitive. }
       * 
       * if (anchorSelected) selection.addSelectionInterval(anchor, column);
       * else selection.removeSelectionInterval(anchor, column);
       * 
       * selection.setValueIsAdjusting(old); } else
       * selection.setSelectionInterval(column, column); } else if
       * (e.isControlDown()) { if (selection.isSelectedIndex(column))
       * selection.removeSelectionInterval(column, column); else
       * selection.addSelectionInterval(column, column); } else {
       * selection.setSelectionInterval(column, column); }
       */

      /*
       * int start = JScrollPane.getVerticalScrollBar().setValue();
       * 
       * ListSelectionModel selectionModel =
       * externalstatementT.getSelectionModel();
       * selectionModel.setSelectionInterval(start, end); /
       * externalstatementT.setSelectionMode
       * (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       * 
       * externalstatementT.getSelectionModel().addListSelectionListener(new
       * ListSelectionListener(){
       * 
       * public void valueChanged(ListSelectionEvent evt) { if
       * (evt.getValueIsAdjusting()) return; }
       */

      displayESMenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

      displayESMenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayESMenu(e);
    }
  }

  private class TableDESSelectionListener implements TableModelListener, ActionListener, MouseListener {

    public void tableChanged(TableModelEvent e) {
      tableDESChanged = true;
      handleButtons();

      LimitedSizeTextField destatementNameField = new LimitedSizeTextField(255, Externalstatement
          .getStatementnameColumnSize(), true);
      destatementNameField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor destatementNameEditor = new DefaultCellEditor(destatementNameField);
      destatementNameEditor.setClickCountToStart(2);

      LimitedSizeTextField deStatementField = new LimitedSizeTextField(32000, Externalstatement
          .getStatementColumnSize(), true);
      deStatementField.setBorder(BorderFactory.createEmptyBorder());
      DefaultCellEditor destatementEditor = new DefaultCellEditor(deStatementField);
      destatementEditor.setClickCountToStart(2);

      // Set the name and statement column editors enabled based on the
      // current edit/view mode.
      destatementNameEditor.getComponent().setEnabled(editable);
      destatementEditor.getComponent().setEnabled(editable);

      deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(255, Externalstatement.getStatementnameColumnSize(), true)));
      deexternalstatementT.getColumnModel().getColumn(0).setCellEditor(
          new StringActionTableCellEditor(destatementNameEditor));
      setConnectionColumn(deexternalstatementT, deexternalstatementT.getColumnModel().getColumn(1));
      deexternalstatementT.getColumnModel().getColumn(2).setCellEditor(
          new DefaultCellEditor(new LimitedSizeTextField(32000, Externalstatement.getStatementColumnSize(), true)));
      deexternalstatementT.getColumnModel().getColumn(2).setCellEditor(
          new StringActionTableCellEditor(destatementEditor));

    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
      displayenu(e);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      displayenu(e);
    }

    public void mouseReleased(MouseEvent e) {
      displayenu(e);
    }
  }
}
