package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.group.GroupTypeFactory;

@SuppressWarnings("serial")
public class ManageGroupView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManageGroupView.class.getName());

  private final SingleFrameApplication application;

  private final GroupTypeFactory treeDataFactory;

  private final TableTreeComponent groupTypeTableTree;

  private final DataModelController dataModelController;

  private final GeneralTechPackTab parentPanel;

  private final boolean editable;

  private boolean saveEnabled = true;

  private boolean cancelEnabled = true;

  private final JFrame frame;

  List<String> tableTreeErrors;

  private final ErrorMessageComponent errorMessageComponent;

  public ManageGroupView(final SingleFrameApplication application, final DataModelController dataModelController,
      final Versioning versioning, final GeneralTechPackTab parentPanel, final JFrame frame) {
    super(new GridBagLayout());

    this.application = application;
    this.dataModelController = dataModelController;
    this.parentPanel = parentPanel;
    this.editable = parentPanel.editable;
    this.frame = frame;

    this.treeDataFactory = new GroupTypeFactory(application, dataModelController.getGroupTypeDataModel(), editable,
        versioning);

    this.groupTypeTableTree = new TableTreeComponent(this.treeDataFactory);
    this.groupTypeTableTree.addDocumentListener(new GroupTableTreeListener());
    this.groupTypeTableTree.setName("GroupTypeTree");

    final JScrollPane scrollPane = new JScrollPane(this.groupTypeTableTree);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    final JButton cancel = new JButton(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("GroupViewCancel");

    final JButton save = new JButton(getAction("save"));
    save.setEnabled(editable);
    save.setName("GroupViewSave");

    final JButton close = new JButton(getParentAction("closeDialog"));
    close.setEnabled(true);
    close.setName("GroupViewClose");

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

  private class GroupTableTreeListener implements DocumentListener {

    @Override
    public void changedUpdate(final DocumentEvent e) {
      logger.info("changedUpdate");

      setScreenMessage(null);
      tableTreeErrors = groupTypeTableTree.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }
      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
      // nothing to do
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
      // nothing to do
    }
  }

  private class SaveTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public SaveTask(final List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);

        // Save the changes in the Group type tree.
        groupTypeTableTree.saveChanges();

        dataModelController.rockObjectsModified(dataModelController.getGroupTypeDataModel());
        
        groupTypeTableTree.setModelForTree();

        (parentPanel).setTPActivationModified(TPActivationModifiedEnum.MODIFIED_OTHER);
        
        dataModelController.getRockFactory().getConnection().commit();

      } catch (Exception e) {
        // ERROR in save operation. Make a roll back and refresh the
        // contents of the TableTreeComponent.
        try {
          dataModelController.getRockFactory().getConnection().rollback();
          groupTypeTableTree.setModelForTree();
        } catch (Exception ex) {
          throw ex;
        }
        throw e;
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
    protected void succeeded(final Void ignored) {
      logger.log(Level.INFO, "Groups successfully saved");
      TreeState.loadExpansionState(groupTypeTableTree, treeState);
      setSaveEnabled(false);
      setCancelEnabled(false);
      setScreenMessage(null);
      getParentAction("enableTabs").actionPerformed(null);

      // Fire an event telling any listeners that we have changed something
      // here!!
      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
    }

    @Override
    protected void failed(final Throwable e) {
      logger.log(Level.SEVERE, "Groups unsuccessfully saved", e);
      JOptionPane.showMessageDialog(null, e.getMessage(),
          application.getContext().getResourceMap().getString("save.error.caption"), JOptionPane.ERROR_MESSAGE);
    }
  }

  private class DiscardTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public DiscardTask(final List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      try {
        groupTypeTableTree.discardChanges();
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(groupTypeTableTree, treeState);
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
    logger.info("Saving ManageGroupingView");

    final List<List<Object>> list = TreeState.saveExpansionState(groupTypeTableTree);
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();
    }
    final Task<Void, Void> saveTask = new SaveTask(list);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));

    return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {	
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) groupTypeTableTree.getModel().getRoot();
		if(root != null){
			Enumeration<TreePath> e= groupTypeTableTree.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
		    			groupTypeTableTree.collapsePath(current);
			    	}
		    	}
		     }
		}
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task<Void, Void> discard() {
	  logger.info("Discarding chnages in ManageGroupingView");
    final List<List<Object>> list = TreeState.saveExpansionState(groupTypeTableTree);
    if(list.size() > 0){
		  collapseBeforeSaveOrCancel();
  }
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

  private void setScreenMessage(final List<String> message) {
    errorMessageComponent.setValue(message);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  private void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  private void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

}
