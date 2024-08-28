package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Verificationobject;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.verification.VerificationObjectDataModel;
import com.ericsson.eniq.techpacksdk.view.verification.VerificationObjectTableModel;

@SuppressWarnings("serial")
public class ManageVerificationObjectView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManageVerificationObjectView.class.getName());

  private final SingleFrameApplication application;

  private final DataModelController dataModelController;

  private final GeneralTechPackTab parentPanel;

  private final Versioning versioning;

  private final boolean editable;

  private boolean saveEnabled = true;

  private boolean cancelEnabled = true;

  private boolean addEnabled = true;

  private boolean removeEnabled = true;

  private final JFrame frame;

  private ErrorMessageComponent errorMessageComponent;

  private VerificationObjectDataModel verificationObjectDataModel;

  private JTable datatable;

  private VerificationObjectTableModel verificationObjectTableModel;

  private List<Verificationobject> data;

  private List<Verificationobject> tobedeleted;

  private class VerificationobjectTableListener implements TableModelListener {

    public void tableChanged(TableModelEvent e) {
      fireTableDataChanged(true);
      getParentAction("disableTabs").actionPerformed(null);
    }
  }

  private class SaveTask extends Task<Void, Void> {

    public SaveTask() {
      super(application);
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        while (tobedeleted.size() > 0) {
          Verificationobject vo = tobedeleted.remove(0);
          vo.deleteDB();
        }
        verificationObjectTableModel.save();
        dataModelController.rockObjectsModified(dataModelController.getVerificationObjectDataModel());
        
        
        ((GeneralTechPackTab) parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }finally {
          try {
              dataModelController.getRockFactory().getConnection().setAutoCommit(true);
            } catch (Exception e) {
              ExceptionHandler.instance().handle(e);
              e.printStackTrace();
            }
      }
      return null;
    }

    @Override
    protected void succeeded(Void ignored) {
      logger.log(Level.INFO, "References successfully saved");
      fireTableDataChanged(false);
      getParentAction("enableTabs").actionPerformed(null);
      
      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, "References unsuccessfully saved", e);
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
    }
  }

  private class DiscardTask extends Task<Void, Void> {

    public DiscardTask() {
      super(application);
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        refresh();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void finished() {
      fireTableDataChanged(false);
      getParentAction("enableTabs").actionPerformed(null);
    }
  }

  /**
   * Save action
   * 
   * @return
   */
  @Action(enabledProperty = "saveEnabled")
  public Task<Void, Void> save() {
    final Task<Void, Void> saveTask = new SaveTask();
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
    final Task<Void, Void> discardTask = new DiscardTask();
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    discardTask.setInputBlocker(new BusyIndicatorInputBlocker(discardTask, busyIndicator));

    return discardTask;

  }

  /**
   * Add new verification object action
   */
  @Action(enabledProperty = "addEnabled")
  public void addemptyrow() {
    Verificationobject newvo = new Verificationobject(dataModelController.getRockFactory());
    newvo.setVersionid(versioning.getVersionid());
    newvo.setMeastype("ALL");
    newvo.setMeaslevel(Constants.REPORTOBJECTLEVELS[0]);
    newvo.setObjectclass("");
    newvo.setObjectname("");
    verificationObjectTableModel.addRow(newvo);
    fireTableDataChanged(true);
  }

  /**
   * Remove selected verification object action
   */
  @Action(enabledProperty = "removeEnabled")
  public void removerow() {
    final int ix = datatable.getSelectionModel().getMinSelectionIndex();
    if (ix >= 0) {
      tobedeleted.add(verificationObjectTableModel.removeRow(ix));
    }
    fireTableDataChanged(true);
  }

  /**
   * Duplicate selected verification object action
   */
  @Action(enabledProperty = "removeEnabled")
  public void duplicaterow() {

    // Get the selected row, and duplicate.
    final int row = datatable.getSelectionModel().getMinSelectionIndex();
    if (row >= 0) {
      // Get the source Verification Object
      Verificationobject oldObject = verificationObjectTableModel.getRow(row);

      // Create the new Verification Object
      Verificationobject newObject = new Verificationobject(dataModelController.getRockFactory());

      // Copy the data
      newObject.setVersionid(oldObject.getVersionid());
      newObject.setMeastype(oldObject.getMeastype());
      newObject.setMeaslevel(oldObject.getMeaslevel());
      newObject.setObjectclass(oldObject.getObjectclass());
      newObject.setObjectname(oldObject.getObjectname());

      // Add the new object to the table model
      verificationObjectTableModel.addRow(newObject);

      // Notify that table data changed.
      fireTableDataChanged(true);
    }
  }

  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(this).get(actionName);
    }
    return null;
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

  private void refresh() {
    verificationObjectDataModel.refresh();
    data = new Vector<Verificationobject>(verificationObjectDataModel.getVerificationobjectData());
    verificationObjectTableModel.setData(data);
    fireTableDataChanged(false);
  }

  public ManageVerificationObjectView(final SingleFrameApplication application,
      DataModelController dataModelController, Versioning versioning, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.application = application;
    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.parentPanel = parentPanel;
    this.editable = parentPanel.editable;
    this.frame = frame;

    verificationObjectDataModel = dataModelController.getVerificationObjectDataModel();

    verificationObjectDataModel.setCurrentVersioning(versioning);
    verificationObjectDataModel.refresh();

    data = new Vector<Verificationobject>(verificationObjectDataModel.getVerificationobjectData());
    tobedeleted = new ArrayList<Verificationobject>();

    MouseListener ml = new VerificationObjectTableSelectionListener();

    verificationObjectTableModel = new VerificationObjectTableModel(application, dataModelController, data);

    datatable = new JTable(verificationObjectTableModel);
    if (editable) {
      datatable.addMouseListener(ml);
      datatable.getTableHeader().addMouseListener(ml);
    }
    datatable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    datatable.getModel().addTableModelListener(new VerificationobjectTableListener());

    datatable.setRowHeight(22);

    datatable.setEnabled(this.editable);

    final JScrollPane scrollPane = new JScrollPane(datatable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    if (editable) {
      scrollPane.addMouseListener(ml);
    }

    verificationObjectTableModel.setColumnWidths(datatable);
    verificationObjectTableModel.setColumnEditors(datatable);
    verificationObjectTableModel.setColumnRenderers(datatable);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(null);

    final JButton cancel = new JButton(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("ReferenceViewCancel");

    final JButton save = new JButton(getAction("save"));
    save.setEnabled(editable);
    save.setName("ReferenceViewSave");

    final JButton close = new JButton(getParentAction("closeDialog"));
    close.setEnabled(true);
    close.setName("ReferenceViewClose");

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(close);

    final GridBagConstraints c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(scrollPane, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

    fireTableDataChanged(false);

  }

  private void fireTableDataChanged(boolean newvalue) {
    Vector<String> errorStrings = verificationObjectTableModel.validateData();
    setSaveEnabled(newvalue);
    if (errorStrings.size() > 0) {
      setSaveEnabled(false);
      errorMessageComponent.setValue(errorStrings);
    } else {
      errorMessageComponent.setValue(null);
    }
    setCancelEnabled(newvalue);
    setAddEnabled(editable);
    setRemoveEnabled(editable);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public boolean isAddEnabled() {
    return addEnabled;
  }

  public void setAddEnabled(boolean addEnabled) {
    final boolean oldvalue = this.addEnabled;
    this.addEnabled = addEnabled;
    firePropertyChange("addEnabled", oldvalue, addEnabled);
  }

  public boolean isRemoveEnabled() {
    return removeEnabled;
  }

  public void setRemoveEnabled(boolean removeEnabled) {
    final boolean oldvalue = this.removeEnabled;
    this.removeEnabled = removeEnabled;
    firePropertyChange("removeEnabled", oldvalue, removeEnabled);
  }

  // ************************************************
  // Table popup menu
  // ************************************************
  private JPopupMenu createPopup(MouseEvent e) {
    JPopupMenu popupUC;
    JMenuItem miUC;
    popupUC = new JPopupMenu();

    miUC = new JMenuItem("Add");
    miUC.setAction(getAction("addemptyrow"));
    popupUC.add(miUC);

    int selected = datatable.getSelectedRow();
    if (e.getSource() instanceof JTable) {
      if (selected > -1) {
        miUC = new JMenuItem("Remove");
        miUC.setAction(getAction("removerow"));
        popupUC.add(miUC);

        miUC = new JMenuItem("Duplicate rows");
        miUC.setAction(getAction("duplicaterow"));
        popupUC.add(miUC);
      }
    }

    popupUC.setOpaque(true);
    popupUC.setLightWeightPopupEnabled(true);

    return popupUC;
  }

  private class VerificationObjectTableSelectionListener implements MouseListener {

    /**
     * 
     */
    public VerificationObjectTableSelectionListener() {
      super();
    }

    private void displayPopupMenu(MouseEvent e) {
      if (e.isPopupTrigger()) {
        createPopup(e).show(e.getComponent(), e.getX(), e.getY());
      }
    }

    public void mouseClicked(MouseEvent e) {
      if (editable) {
        displayPopupMenu(e);
      }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
      if (editable) {
        displayPopupMenu(e);
      }
    }

    public void mouseReleased(MouseEvent e) {
      if (editable) {
        displayPopupMenu(e);
      }
    }

  }

}
