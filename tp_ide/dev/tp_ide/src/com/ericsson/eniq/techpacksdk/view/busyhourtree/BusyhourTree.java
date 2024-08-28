package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;

public class BusyhourTree extends JTree {

  private static final Logger logger = Logger.getLogger(BusyhourTree.class.getName());

  private static final long serialVersionUID = 1L;

  private BusyhourTreeModelListener listener = null;

  private BusyhourTreeModel bhtm;

  // private DataModelController dataModelController;

  // private SingleFrameApplication application;

  // private GeneralTechPackTab parentPanel;

  public BusyhourTree(JFrame frame, SingleFrameApplication application, GeneralTechPackTab parentPanel,
      Versioning versioning, DataModelController dataModelController, boolean editable, DocumentListener dl) {

    // this.dataModelController = dataModelController;
    listener = new BusyhourTreeModelListener(dataModelController, frame, application, this, editable);
		//default the popup menu editable to false i.e. It should not be possible to add/remove/duplicate
		// the busy hour supports in the tree regardless of edit/view
    bhtm = new BusyhourTreeModel(application, versioning, dataModelController, editable, this, listener, dl);
    listener.setTtm(bhtm);
    this.setModel(bhtm);
    this.addTreeExpansionListener(listener);
    this.setEditable(true);
    this.setCellRenderer(new TreeRenderer());
    this.setCellEditor(new TreeEditor(this));
    this.setRootVisible(false);
    this.setShowsRootHandles(true);
    // this.application = application;
    // this.parentPanel = parentPanel;

    // Remove some of the auto scrolling in the tree
    this.setAutoscrolls(false);

    this.setScrollsOnExpand(false);

    // Double-click should edit, not expand
    this.setToggleClickCount(0);

  }

  public void addDocumentListener(DocumentListener dl) {
    bhtm.addDocumentListener(dl);
  }

  public void update() {
    List<List<Object>> list = TreeState.saveExpansionState(this);
    bhtm.update();
    TreeState.loadExpansionState(this, list);
  }

  public void save() {
    logger.log(Level.INFO, "save");
    List<List<Object>> list = TreeState.saveExpansionState(this);
    bhtm.save();
    TreeState.loadExpansionState(this, list);
  }

  public void discard() {
    logger.log(Level.INFO, "discard");
    List<List<Object>> list = TreeState.saveExpansionState(this);
    bhtm.discard();
    TreeState.loadExpansionState(this, list);
  }

  public List<String> validateData() {
    return bhtm.validateData();
  }

  public BusyhourTreeModel getBhTreeModel() {
    return bhtm;
  }

  public void setBhTreeModel(BusyhourTreeModel treeModel) {
    this.bhtm = treeModel;
  }

  // private javax.swing.Action getParentAction(final String actionName) {
  // if (application != null) {
  // return application.getContext().getActionMap(parentPanel).get(actionName);
  // }
  // return null;
  // }

  List<String> tableTreeErrors;

  private boolean wizardEnabled = true;

  public boolean isWizardEnabled() {
    return wizardEnabled;
  }

  public void setWizardEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.wizardEnabled;
    this.wizardEnabled = saveEnabled;
    firePropertyChange("wizardEnabled", oldvalue, saveEnabled);
  }

  private boolean cancelEnabled = true;

  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

}
