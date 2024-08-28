package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
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

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.ericsson.eniq.component.ErrorMessageComponent;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class ETLSetHandlingView extends JPanel {

  public static int TECHPACK = 1;

  public static int INTERFACE = 2;

  public static int MAINTENANCE = 3;

  private static final Logger logger = Logger.getLogger(ETLSetHandlingView.class.getName());

  private final SingleFrameApplication application;

  private final String setName;

  private final String setVersion;

  private final String setType;

  private final String versionid;

  private int type = -1;

  private final TableTreeComponent myTTC;

  private final Object parentPanel;

  private boolean saveEnabled = true;

  private boolean wizardEnabled = true;

  private final JFrame fFrame;

  private final ETLFactory etlf;

  private final DataModelController dataModelController;

  boolean editable = true;

  private final ErrorMessageComponent errorMessageComponent;

  Vector<String> tableTreeErrors;

  private boolean cancelEnabled = true;

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

  public boolean isCancelEnabled() {
    return cancelEnabled;
  }

  public void setCancelEnabled(final boolean cancelEnabled) {
    final boolean oldvalue = this.cancelEnabled;
    this.cancelEnabled = cancelEnabled;
    firePropertyChange("cancelEnabled", oldvalue, cancelEnabled);
  }

  public boolean isWizardEnabled() {
    return wizardEnabled;
  }

  public void setWizardEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.wizardEnabled;
    this.wizardEnabled = saveEnabled;
    firePropertyChange("wizardEnabled", oldvalue, saveEnabled);
  }

  public boolean isSaveEnabled() {
    return saveEnabled;
  }

  public void setSaveEnabled(final boolean saveEnabled) {
    final boolean oldvalue = this.saveEnabled;
    this.saveEnabled = saveEnabled;
    firePropertyChange("saveEnabled", oldvalue, saveEnabled);
  }

  @Action(enabledProperty = "wizardEnabled")
  public void closeDialog() {
    getParentAction("closeDialog").actionPerformed(null);
  }

  private void setScreenMessage(final Vector<String> message) {
    errorMessageComponent.setValue(message);
  }

  private class ETLErrorTableTreeListener implements DocumentListener {

    @Override
    public void changedUpdate(final DocumentEvent e) {

      setScreenMessage(null);
      tableTreeErrors = myTTC.validateData();
      if (tableTreeErrors.size() > 0) {
        setScreenMessage(tableTreeErrors);
      }

      setSaveEnabled(tableTreeErrors.size() == 0);
      setCancelEnabled(true);
      getParentAction("disableTabs").actionPerformed(null);

    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
      // do nothing
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
      // do nothing
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
	
	        myTTC.saveChanges();
	
	        Vector<Meta_collections> v = dataModelController.getETLFactory().getRemoveList();
	        if (v != null) {
	          for (int i = 0; i < v.size(); i++) {
	            Meta_collections m = v.get(i);
	            m.deleteDB();
	          }
	          v.clear();
	        }
	      } catch (Exception e) {
	        dataModelController.getRockFactory().getConnection().rollback();
	        logger.warning(e.getMessage());
	      } finally {
	        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
	
	      }
	      dataModelController.getETLSetHandlingDataModel().refresh();
	      myTTC.setModelForTree();
	      return null;
	    }
	    
	    @Override
	    protected void succeeded(Void ignored) {
	      //After successful save, Load the tree expansion state of measurement types 
	      TreeState.loadExpansionState(myTTC, treeState);
	      setSaveEnabled(false);
	      // IDE #652 When changes are saved in Sets/Actions/Schedulings, cancel button remains active. 2009-07-06, ejohabd
	      setCancelEnabled(false);
	      setScreenMessage(null);
	      getParentAction("enableTabs").actionPerformed(null);
	      
	      //Fire an event telling any listeners that we have changed something here!!
	      application.getMainFrame().firePropertyChange("EditTP_saveButton", 0, 1);
	      logger.log(Level.INFO, "ETLSetHandlingView successfully saved");
	    }
  }

  /**
   * Save action
   * 
   * @return
   */

  @Action(enabledProperty = "saveEnabled")
  public Task save() {
	  logger.info("Saving ETLSetHandlingView");
	  final List<List<Object>> list = TreeState.saveExpansionState(myTTC);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();
	  }
	  final Task<Void, Void> saveTask = new SaveTask(list);

	  BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  saveTask.setInputBlocker(new BusyIndicatorInputBlocker(saveTask, busyIndicator));
	  return saveTask;
  }
  
  //Collapse all the nodes before save operation, this makes save operation faster
  private void collapseBeforeSaveOrCancel() {
	  DefaultMutableTreeNode root = (DefaultMutableTreeNode) myTTC.getModel().getRoot();
	  if(root != null){
		  Enumeration<TreePath> e= myTTC.getExpandedDescendants(new TreePath(root));
		    while (e != null && e.hasMoreElements()){
		    	TreePath current=e.nextElement();
		    	if(current != null){
		    		if(!current.getLastPathComponent().equals(root)){
			    		myTTC.collapsePath(current);
			    	}
		    	}
		     }
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
	    	  myTTC.discardChanges();
	        } catch (Exception e) {
	          ExceptionHandler.instance().handle(e);
	          e.printStackTrace();
	        } 
	        return null;
	    }
	    
	    @Override
	    protected void finished() {
	      logger.log(Level.INFO, "ETLSets changes successfully discarded");
	      TreeState.loadExpansionState(myTTC, treeState);
	      setSaveEnabled(false);
	      setCancelEnabled(false);
	      setScreenMessage(null);
	      getParentAction("enableTabs").actionPerformed(null);
	    }    
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action(enabledProperty = "cancelEnabled")
  public Task discard() {
	  logger.info("Discarding changes in ETLSetHandlingView");
	  final List<List<Object>> list = TreeState.saveExpansionState(myTTC);
	  if(list.size() > 0){
		  collapseBeforeSaveOrCancel();
	  }
	  
	  final Task<Void, Void> DiscardTask = new DiscardTask(list);

	  BusyIndicator busyIndicator = new BusyIndicator();
	  fFrame.setGlassPane(busyIndicator);
	  DiscardTask.setInputBlocker(new BusyIndicatorInputBlocker(DiscardTask, busyIndicator));
	  return DiscardTask;
  }

  /**
   * wizard action
   * 
   * @return
   */
  @Action(enabledProperty = "wizardEnabled")
  public void wizard() {

    logger.log(Level.INFO, "wizard");

    setWizardEnabled(false);
    JFrame frame = new JFrame();

    if (type == ETLSetHandlingView.INTERFACE) {

      InterfaceSetWizardView wizard = new InterfaceSetWizardView(application, dataModelController, myTTC, frame,
          fFrame, setName, setVersion, setType, editable);
      frame.add(wizard);
      frame.setSize(450, 350);
      frame.setTitle("Interface Set Wizard ");
      frame.setName("ETLSetHandlingViewInterfaceSetWizardFrame");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new WizardListener());
      frame.setVisible(true);

    } else if (type == ETLSetHandlingView.TECHPACK || type == ETLSetHandlingView.MAINTENANCE) {

      TechpackSetWizardView wizard = TechpackSetWizardViewFactory.createTechpackSetWizardView(application,
          dataModelController, myTTC, frame, fFrame, setName, setVersion, versionid, setType, editable);
      frame.add(wizard);
      frame.setSize(450, 650);
      frame.setTitle("Techpack Set Wizard ");
      frame.setName("ETLSetHandlingViewTechpackSetWizardFrame");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new WizardListener());
      frame.setVisible(true);

    } else {
      // error
      logger.warning("INVALID TYPE " + type);
    }
  }

  // private class ETLSetTableTreeListener implements DocumentListener {
  //
  // public void changedUpdate(final DocumentEvent e) {
  //
  // System.out.println("ETLSetTableTreeListener: changedUpdate()");
  //
  // // setSaveEnabled(true);
  // // getParentAction("disableTabs").actionPerformed(null);
  // }
  //
  // public void insertUpdate(final DocumentEvent e) {
  // // nothing to do
  // }
  //
  // public void removeUpdate(final DocumentEvent e) {
  // // nothing to do
  // }
  // }

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

  @Override
  public void finalize() throws Throwable {
    super.finalize();
  }

  public ETLSetHandlingView(final SingleFrameApplication application, DataModelController dataModelController,
      String setName, String setVersion, String versionid, String setType, int type, boolean editable,
      Object parentPanel, JFrame frame) {
    super(new GridBagLayout());

    this.fFrame = frame;
    this.application = application;
    this.dataModelController = dataModelController;
    this.editable = editable;
    this.type = type;
    this.setName = setName;
    this.setVersion = versionid.substring(versionid.lastIndexOf(":") + 1);
    this.versionid = versionid;
    this.setType = setType;

    this.parentPanel = parentPanel;

    // Add a component listener for tracking if the tree should be updated when
    // the component is shown.
    this.addComponentListener(new MyCompListener());

    // etlf = dataModelController.getETLFactory();

    etlf = new ETLFactory(this.dataModelController.getETLSetHandlingDataModel());
    etlf.setRemoveList(dataModelController.getETLFactory().getRemoveList());

    etlf.setSetName(setName);
    etlf.setSetVersion(this.setVersion);
    etlf.setEditable(editable);

    if (type == ETLSetHandlingView.TECHPACK) {
      etlf.setType("Techpack");
    } else if (type == ETLSetHandlingView.INTERFACE) {
      etlf.setType("Interface");
    } else if (type == ETLSetHandlingView.MAINTENANCE) {
      etlf.setType("Maintenance");
    }

    etlf.setTechpackType(setType);

    myTTC = new TableTreeComponent(etlf);
    // myTTC.addDocumentListener(new ETLSetTableTreeListener());
    myTTC.addDocumentListener(new ETLErrorTableTreeListener());

    JScrollPane scrollPane = new JScrollPane(myTTC);

    // ************** buttons **********************

    errorMessageComponent = new ErrorMessageComponent(application);
    errorMessageComponent.setValue(new Vector<String>());

    JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("ETLSetHandlingViewCancel");

    JButton save = new JButton("Save");
    save.setAction(getAction("save"));
    save.setEnabled(editable);
    save.setName("ETLSetHandlingViewSave");

    JButton wizard = new JButton("Set Wizard");
    wizard.setAction(getAction("wizard"));
    wizard.setEnabled(editable);
    wizard.setName("ETLSetHandlingViewWizard");

    final JButton closeDialog = new JButton("Close");
    closeDialog.setAction(getAction("closeDialog"));
    closeDialog.setEnabled(true);
    closeDialog.setName("ETLSetHandlingViewClose");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(errorMessageComponent);
    buttonPanel.add(wizard);
    buttonPanel.add(save);
    buttonPanel.add(cancel);
    buttonPanel.add(closeDialog);

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
    setScreenMessage(null);
  }

  private class WizardListener implements WindowListener {

    @Override
    public void windowActivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowActivated");
    }

    @Override
    public void windowClosed(WindowEvent e) {
      logger.log(Level.FINEST, "windowClosed");
      setWizardEnabled(true);
    }

    @Override
    public void windowClosing(WindowEvent e) {
      logger.log(Level.FINEST, "windowClosing");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeactivated");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeiconified");
    }

    @Override
    public void windowIconified(WindowEvent e) {
      logger.log(Level.FINEST, "windowIconified");
    }

    @Override
    public void windowOpened(WindowEvent e) {
      logger.log(Level.FINEST, "windowOpened");
    }
  }

  /**
   * The component listener is activated when the component is shown. It is then
   * checked if the table tree component needs to be updated due to changes in
   * the data model. 
   * 
   * @author eheitur
   * 
   */
  private class MyCompListener implements ComponentListener {

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
      if (dataModelController.getETLSetHandlingDataModel().dataUpdated) {
        logger.fine("ETLSetHandlingView: Refreshing the set tree due to changes in the data model.");
        final List<List<Object>> treeState = TreeState.saveExpansionState(myTTC);
        myTTC.setModelForTree();
        TreeState.loadExpansionState(myTTC, treeState);
        dataModelController.getETLSetHandlingDataModel().dataUpdated = false;
      }
    }
  }

}
