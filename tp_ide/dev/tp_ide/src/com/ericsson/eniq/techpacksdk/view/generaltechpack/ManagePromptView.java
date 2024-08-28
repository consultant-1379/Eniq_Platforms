package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import tableTree.TableTreeComponent;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.prompts.PromptImplementorFactory;

@SuppressWarnings("serial")
public class ManagePromptView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManagePromptView.class.getName());

  private final SingleFrameApplication application;

  private final PromptImplementorFactory treeDataFactory;

  private final TableTreeComponent promptImplementorTableTree;

  private final DataModelController dataModelController;

  private final GeneralTechPackTab parentPanel;

  private final Versioning versioning;

  private final boolean editable;

  private boolean saveEnabled = true;

  private boolean cancelEnabled = true;

  private final JFrame frame;

  Vector<String> tableTreeErrors;

  private ErrorMessageComponent errorMessageComponent;

  private class PromptImplementorTableTreeListener implements DocumentListener {

    public void changedUpdate(final DocumentEvent e) {

      setScreenMessage(null);
      tableTreeErrors = promptImplementorTableTree.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }
      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
    }
  }

  private class SaveTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public SaveTask(List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        promptImplementorTableTree.saveChanges();
        dataModelController.getRockFactory().getConnection().commit();
        dataModelController.rockObjectsModified(dataModelController.getPromptImplementorTypeDataModel());
        promptImplementorTableTree.setModelForTree();
      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
          promptImplementorTableTree.setModelForTree();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void succeeded(Void ignored) {
      logger.log(Level.INFO, "Prompts successfully saved");
      TreeState.loadExpansionState(promptImplementorTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);

      //Fire an event telling any listeners that we have changed something here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }

    @Override
    protected void failed(Throwable e) {
      logger.log(Level.SEVERE, "Prompts unsuccessfully saved", e);
      JOptionPane.showMessageDialog(null, e.getMessage(), application.getContext().getResourceMap().getString(
          "save.error.caption"), JOptionPane.ERROR_MESSAGE);
    }

  }

  private class DiscardTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public DiscardTask(List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        promptImplementorTableTree.discardChanges();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(promptImplementorTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
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
    final List<List<Object>> list = TreeState.saveExpansionState((JTree) promptImplementorTableTree);
    final Task<Void, Void> saveTask = new SaveTask(list);
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
    final List<List<Object>> list = TreeState.saveExpansionState((JTree) promptImplementorTableTree);
    final Task<Void, Void> discardTask = new DiscardTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    discardTask.setInputBlocker(new BusyIndicatorInputBlocker(discardTask, busyIndicator));

    return discardTask;

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

  public ManagePromptView(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, GeneralTechPackTab parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.application = application;
    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.parentPanel = parentPanel;
    this.editable = parentPanel.editable;
    this.frame = frame;

    this.treeDataFactory = new PromptImplementorFactory(application, dataModelController
        .getPromptImplementorTypeDataModel(), editable);
    this.treeDataFactory.setVersioning(this.versioning);

    this.promptImplementorTableTree = new TableTreeComponent(this.treeDataFactory);
    this.promptImplementorTableTree.addDocumentListener(new PromptImplementorTableTreeListener());

    final JScrollPane scrollPane = new JScrollPane(this.promptImplementorTableTree);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final JButton cancel = new JButton(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("PromptViewCancel");

    final JButton save = new JButton(getAction("save"));
    save.setEnabled(editable);
    save.setName("PromptViewSave");

    final JButton close = new JButton(getParentAction("closeDialog"));
    close.setEnabled(true);
    close.setName("PromptViewClose");

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

    setSaveEnabled(false);
    setCancelEnabled(false);

  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
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

}
