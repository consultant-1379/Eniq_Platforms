package com.ericsson.eniq.techpacksdk;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.InterfacedependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.GenericActionNode;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
//import com.ericsson.eniq.techpacksdk.parserDebugger.ParserDebuggerView;
import com.ericsson.eniq.techpacksdk.view.generalInterface.GeneralInterfaceTab;
import com.ericsson.eniq.techpacksdk.view.newInterface.ManageNewInterfaceView;

@SuppressWarnings("serial")
public class ManageInterfaceTab extends JPanel {

  private SingleFrameApplication application;

  private DataTreeNode selectedNode;

  private static final Logger logger = Logger.getLogger(ManageInterfaceTab.class.getName());

  private GenericActionTree tpTree;

  private boolean refreshEnabled = false;

  public boolean isRefreshEnabled() {
    return refreshEnabled;
  }

  public void setRefreshEnabled(boolean refreshEnabled) {
    boolean oldvalue = this.refreshEnabled;
    this.refreshEnabled = refreshEnabled;
    firePropertyChange("refreshEnabled", oldvalue, refreshEnabled);
  }

  private boolean lockEnabled = false;

  private boolean unlockEnabled = false;

  private boolean activateEnabled = false;

  private boolean deActivateEnabled = false;

  private boolean testEnabled = false;

  private boolean installEnabled = false;

  private boolean deleteEnabled = false;

  private boolean viewEnabled = false;

  private boolean editEnabled = false;

  private boolean newEnabled = true;

  private boolean migrateEnabled = false;

  private String editing = null;

  private String testing = null;

  DataModelController dataModelController;

  InterfaceTreeDataModel interfaceTreeDataModel;

  private ImageIcon ericssonLogo;
  
  public boolean alreadyIntfLocked = false; 

  public ManageInterfaceTab(SingleFrameApplication application, DataModelController dataModelController,
      InterfaceTreeDataModel interfaceTreeDataModel) {
    super(new GridBagLayout());
    this.application = application;
    this.dataModelController = dataModelController;

    ericssonLogo = application.getContext().getResourceMap(getClass()).getImageIcon("ericsson.icon");

    // ************** TPTree panel **********************

    this.interfaceTreeDataModel = interfaceTreeDataModel;

    tpTree = new GenericActionTree(interfaceTreeDataModel);
    tpTree.addRootAction(getAction("refresh"));
    tpTree.addRootAction(getAction("addnew"));
    //tpTree.addAction(getAction("test"));
    tpTree.addAction(getAction("delete"));
    tpTree.addAction(getAction("deactivate"));
    tpTree.addAction(getAction("activate"));
    tpTree.addAction(getAction("lock"));
    tpTree.addAction(getAction("unlock"));
    tpTree.addAction(getAction("view"));
    tpTree.addAction(getAction("edit"));
    tpTree.addAction(getAction("install"));
    tpTree.addAction(getAction("migrate"));
    tpTree.setCellRenderer(new InterfaceTreeRenderer(dataModelController));
    tpTree.addTreeSelectionListener(new SelectionListener());
    tpTree.setToolTipText("");
    tpTree.setName("InterfaceTree");
    final JScrollPane tpScrollPane = new JScrollPane(tpTree);

    // ************** buttons **********************

//    JButton test = new JButton("Test");
//    test.setActionCommand("test");
//    test.setAction(getAction("test"));
//    test.setToolTipText("Test");
//    test.setName("InterfaceTest");

    JButton refresh = new JButton("Refresh");
    refresh.setActionCommand("refresh");
    refresh.setAction(getAction("refresh"));
    refresh.setToolTipText("Refresh");
    refresh.setName("InterfaceRefresh");

    JButton delete = new JButton("Delete");
    delete.setActionCommand("delete");
    delete.setAction(getAction("delete"));
    delete.setToolTipText("Delete");
    delete.setName("InterfaceDelete");

    JButton newi = new JButton("New");
    newi.setActionCommand("new");
    newi.setAction(getAction("addnew"));
    newi.setToolTipText("New");
    newi.setName("InterfaceNew");

    JButton deactivate = new JButton("DeActivate");
    deactivate.setActionCommand("deactivate");
    deactivate.setAction(getAction("deactivate"));
    deactivate.setToolTipText("DeActivate");
    deactivate.setName("InterfaceDeActivate");

    JButton activate = new JButton("Activate");
    activate.setActionCommand("activate");
    activate.setAction(getAction("activate"));
    activate.setToolTipText("Activate");
    activate.setName("InterfaceActivate");

    JButton viewi = new JButton("View");
    viewi.setActionCommand("view");
    viewi.setAction(getAction("view"));
    viewi.setToolTipText("View");
    viewi.setName("InterfaceView");

    JButton edit = new JButton("Edit");
    edit.setActionCommand("edit");
    edit.setAction(getAction("edit"));
    edit.setToolTipText("Edit");
    edit.setName("InterfaceEdit");

    JButton lock = new JButton("Lock");
    lock.setActionCommand("lock");
    lock.setAction(getAction("lock"));
    lock.setToolTipText("Lock");
    lock.setName("InterfaceLock");

    JButton unlock = new JButton("Unlock");
    unlock.setActionCommand("unlock");
    unlock.setAction(getAction("unlock"));
    unlock.setToolTipText("Unlock");
    unlock.setName("InterfaceUnlock");

    JButton install = new JButton("Install");
    install.setActionCommand("install");
    install.setAction(getAction("install"));
    install.setToolTipText("Install");
    install.setName("InterfaceInstall");

    // JButton migrate = new JButton("Migrate");
    // migrate.setActionCommand("migrate");
    // migrate.setAction(getAction("migrate"));
    // migrate.setToolTipText("Migrate");
    // migrate.setName("InterfaceMigrate");

    final JButton quit = new JButton("Quit");
    quit.setAction(getAction("quit"));
    quit.setName("InterfaceQuit");

    // ************** button panel **********************

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    // buttonPanel.add(refresh);
//    buttonPanel.add(test);
    buttonPanel.add(viewi);
    buttonPanel.add(edit);
    buttonPanel.add(delete);
    buttonPanel.add(newi);
    buttonPanel.add(activate);
    buttonPanel.add(deactivate);
    buttonPanel.add(lock);
    buttonPanel.add(unlock);
    buttonPanel.add(install);
    // buttonPanel.add(migrate);
    buttonPanel.add(quit);

    // insert both panels in main view

    final GridBagConstraints c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(tpScrollPane, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

    this.setRefreshEnabled(true);

  }

//  @Action(enabledProperty = "testEnabled")
//  public void test() {
//    logger.log(Level.INFO, "tested");
//
//    setTestEnabled(false);
//    testing = selectedNode.toString();
//    JFrame frame = new JFrame();
//    frame.setName("ParserDebuggerWindow");
//    frame.setIconImage(ericssonLogo.getImage());
//
//    ParserDebuggerView pdv = new ParserDebuggerView(application, dataModelController, frame, selectedNode
//        .getRockDBObject(), ((Datainterface) selectedNode.getRockDBObject()));
//
//    frame.add(pdv);
//    // frame.setSize(this.application.getMainFrame().getWidth(),
//    // this.application.getMainFrame().getHeight());
//    frame.setSize(1000, 600);
//    frame.setTitle("Parser Debugger " + selectedNode);
//    frame.addWindowListener(new ParseDebugListener());
//    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    frame.setVisible(true);
//  }

  private class DeleteTask extends Task<Void, Void> {

    public DeleteTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "deleted");

      List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

        GenericActionNode current = tpTree.getSelected();
        if (current instanceof DataTreeNode) {
          DataTreeNode node = (DataTreeNode) current;
          Datainterface di = (Datainterface) node.getRockDBObject();

          int selectedValue = JOptionPane.showConfirmDialog(null, "Are you sure that you want to remove "
              + di.getInterfacename() + " Interface", "Remove Interface?", JOptionPane.YES_NO_OPTION);

          if (selectedValue == JOptionPane.YES_OPTION) {

            String name = di.getInterfacename();
            String version = di.getInterfaceversion();

            // fetch interface measurements
            Interfacemeasurement aim = new Interfacemeasurement(dataModelController.getRockFactory());

            aim.setInterfacename(name);
            aim.setInterfaceversion(version);
            InterfacemeasurementFactory aimf = new InterfacemeasurementFactory(dataModelController.getRockFactory(),
                aim);
            Vector<Interfacemeasurement> ims = aimf.get();

            if (ims != null) {
              Iterator<Interfacemeasurement> i = ims.iterator();

              while (i.hasNext()) {
                Interfacemeasurement im = i.next();

                // remove interface measurement
                im.deleteDB();
              }
            }

            // fetch interface techpack
            Interfacetechpacks aimt = new Interfacetechpacks(dataModelController.getRockFactory());
            aimt.setInterfacename(name);
            aimt.setInterfaceversion(version);
            InterfacetechpacksFactory aimtf = new InterfacetechpacksFactory(dataModelController.getRockFactory(), aimt);
            Vector<Interfacetechpacks> imts = aimtf.get();

            if (imts != null) {
              Iterator<Interfacetechpacks> i = imts.iterator();

              while (i.hasNext()) {
                Interfacetechpacks imt = i.next();

                // delete interface techpack
                imt.deleteDB();
              }
            }

            // fetch interface depencies
            Interfacedependency infdt = new Interfacedependency(dataModelController.getRockFactory());
            infdt.setInterfacename(name);
            infdt.setInterfaceversion(version);
            InterfacedependencyFactory infdtf = new InterfacedependencyFactory(dataModelController.getRockFactory(),
                infdt);
            Vector<Interfacedependency> infdtfI = infdtf.get();

            if (infdtfI != null) {
              Iterator<Interfacedependency> i = infdtfI.iterator();

              while (i.hasNext()) {
                Interfacedependency imt = i.next();

                // delete interface depedencies
                imt.deleteDB();
              }
            }

            dataModelController.getETLSetHandlingDataModel().deleteINTFSets(name, version);

            // delete data interface
            di.deleteDB();
          }
        }

        dataModelController.rockObjectsModified(interfaceTreeDataModel);
        interfaceTreeDataModel.refresh();

        dataModelController.getRockFactory().getConnection().commit();
        dataModelController.getEtlRockFactory().getConnection().commit();

      } catch (Exception e) {

        try {
          dataModelController.getRockFactory().getConnection().rollback();
          dataModelController.getEtlRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }

        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
          dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }

        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }

      return null;
    }
  }

  @Action(enabledProperty = "deleteEnabled")
  public Task<Void, Void> delete() {
    final Task<Void, Void> deleteTask = new DeleteTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    deleteTask.setInputBlocker(new BusyIndicatorInputBlocker(deleteTask, busyIndicator));

    return deleteTask;
  }

  private class RefereshTask extends Task<Void, Void> {

    public RefereshTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      if ((tpTree != null)) {
        List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          dataModelController.getInterfaceTreeDataModel().refresh();
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
        }
      }
      return null;
    }
  }

  @Action(block = BlockingScope.APPLICATION, enabledProperty = "refreshEnabled")
  public Task<Void, Void> refresh() {
    final Task<Void, Void> refereshTask = new RefereshTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    refereshTask.setInputBlocker(new BusyIndicatorInputBlocker(refereshTask, busyIndicator));

    return refereshTask;

  }

  @Action(enabledProperty = "newEnabled")
  public void addnew() {

    this.setNewEnabled(false);
    setRefreshEnabled(false);
    logger.log(Level.INFO, "addnew");

    JFrame frame = new JFrame();
    frame.setName("CreateNewInterface");
    frame.setIconImage(ericssonLogo.getImage());
	
    //EQEV-2862 Autolock Interface starts
    lock();
    //EQEV-2862 Autolock Interface end
	
    Datainterface di = null;
    if (selectedNode != null) {
      di = (Datainterface) selectedNode.getRockDBObject();
    }

    if(!alreadyIntfLocked ){
    dataModelController.getNewInterfaceDataModel().newInterface();
    dataModelController.getNewInterfaceDataModel().refresh();
    ManageNewInterfaceView newInterfaceView = new ManageNewInterfaceView(di, application, dataModelController, frame);

    frame.add(newInterfaceView);
    frame.setSize(700, 500);
    frame.setTitle("New Interface");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new NewInterfaceListener());
    frame.setVisible(true);
    }else{
    	JOptionPane.showMessageDialog(this, "Can't Create new Interface, because " + di.getInterfacename() + ":" + di.getInterfaceversion()
                + " was already locked by user " + di.getLockedby() + " at " + di.getLockdate());
    	
    	// To set new and refresh button enable if we can't create new due to already locked by other user.
    	setNewEnabled(true);
        setRefreshEnabled(true);

        refresh();
    }

  }

  public void deactivateInterface(Datainterface i) throws Exception {

    Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
    mcs.setCollection_set_name(i.getInterfacename());
    mcs.setVersion_number(i.getInterfaceversion());
    Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(), mcs);

    if (mcsF != null) {

      Meta_collection_sets metacs = (Meta_collection_sets) mcsF.getElementAt(0);

      Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
      mc.setCollection_set_id(metacs.getCollection_set_id());
      mc.setVersion_number(i.getInterfaceversion());
      Meta_collectionsFactory mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);

      Iterator<Meta_collections> mcFI = mcF.get().iterator();
      while (mcFI.hasNext()) {
        Meta_collections metac = mcFI.next();
        metac.setEnabled_flag("N");
        metac.saveDB();
      }

      metacs.setEnabled_flag("N");
      metacs.saveDB();

    }

  }

  private class DeactivateTask extends Task<Void, Void> {

    public DeactivateTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "deactivated");
      List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        GenericActionNode current = tpTree.getSelected();
        if (current instanceof DataTreeNode) {

          DataTreeNode node = (DataTreeNode) current;

          interfaceTreeDataModel.deactivateInterface((Datainterface) node.getRockDBObject());

          /*
           * 
           * Datainterface i = (Datainterface) node.getRockDBObject();
           * 
           * Meta_collection_sets mcs = new
           * Meta_collection_sets(dataModelController.getEtlRockFactory());
           * mcs.setCollection_set_name(i.getInterfacename());
           * mcs.setVersion_number(i.getInterfaceversion());
           * Meta_collection_setsFactory mcsF = new
           * Meta_collection_setsFactory(dataModelController
           * .getEtlRockFactory(), mcs);
           * 
           * if (mcsF != null) {
           * 
           * Meta_collection_sets metacs =
           * (Meta_collection_sets)mcsF.getElementAt(0);
           * 
           * Meta_collections mc = new
           * Meta_collections(dataModelController.getEtlRockFactory());
           * mc.setCollection_set_id(metacs.getCollection_set_id());
           * mc.setVersion_number(i.getInterfaceversion());
           * Meta_collectionsFactory mcF = new
           * Meta_collectionsFactory(dataModelController.getEtlRockFactory(),
           * mc);
           * 
           * Iterator<Meta_collections> mcFI = mcF.get().iterator(); while
           * (mcFI.hasNext()) { Meta_collections metac = mcFI.next();
           * metac.setEnabled_flag("N"); metac.saveDB(); }
           * 
           * metacs.setEnabled_flag("N"); metacs.saveDB();
           * 
           * }
           */
        }
        interfaceTreeDataModel.refresh();
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }
      return null;
    }
  }

  @Action(enabledProperty = "deActivateEnabled")
  public Task<Void, Void> deactivate() {
    final Task<Void, Void> deactivateTask = new DeactivateTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    deactivateTask.setInputBlocker(new BusyIndicatorInputBlocker(deactivateTask, busyIndicator));

    return deactivateTask;
  }

  private class ActivateTask extends Task<Void, Void> {

    public ActivateTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "activated");
      List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {

        GenericActionNode current = tpTree.getSelected();

        if (current instanceof DataTreeNode) {

          DataTreeNode node = (DataTreeNode) current;
          Datainterface i = (Datainterface) node.getRockDBObject();

          if (interfaceTreeDataModel.isAnyInterfaceActive(i.getInterfacename())) {
            JOptionPane.showMessageDialog(null, "There is allready an active interface");
            return null;
          }

          interfaceTreeDataModel.activateInterface((Datainterface) node.getRockDBObject());

          /*
           * i.setStatus(new Long(1)); interfaceTreeDataModel.modObj(i);
           * 
           * ActivateInterface ai = new ActivateInterface(i.getInterfacename(),
           * i.getInterfaceversion(), dataModelController.getRockFactory());
           * 
           * ai.activateInterface();
           * 
           * Meta_collection_sets mcs = new
           * Meta_collection_sets(dataModelController.getEtlRockFactory());
           * mcs.setCollection_set_name(i.getInterfacename());
           * mcs.setVersion_number(i.getInterfaceversion());
           * Meta_collection_setsFactory mcsF = new
           * Meta_collection_setsFactory(dataModelController
           * .getEtlRockFactory(), mcs);
           * 
           * if (mcsF != null) {
           * 
           * Meta_collection_sets metacs =
           * (Meta_collection_sets)mcsF.getElementAt(0);
           * 
           * Meta_collections mc = new
           * Meta_collections(dataModelController.getEtlRockFactory());
           * mc.setCollection_set_id(metacs.getCollection_set_id());
           * mc.setVersion_number(i.getInterfaceversion());
           * Meta_collectionsFactory mcF = new
           * Meta_collectionsFactory(dataModelController.getEtlRockFactory(),
           * mc);
           * 
           * Iterator<Meta_collections> mcFI = mcF.get().iterator(); while
           * (mcFI.hasNext()) { Meta_collections metac = mcFI.next();
           * metac.setEnabled_flag("Y"); metac.saveDB(); }
           * 
           * 
           * metacs.setEnabled_flag("Y"); metacs.saveDB();
           * 
           * }
           */

          // Restart the engine after interface activation. Graceful restart is
          // used with 240 seconds timeout. After the engine has been
          // successfully restarted, the scheduler connection is refreshed with
          // 120 seconds timeout. The directory checker is also executed.
          try {
            logger.log(Level.INFO, "Restarting the engine after interface activation.");
            dataModelController.getEngine().fastGracefulEngineRestart(240);
            logger.log(Level.INFO, "Refreshing scheduler connection.");
            dataModelController.getScheduler().refreshSchedulerConnectionAfterEngineRestart(120);
            logger.log(Level.INFO, "Running the directory checker for the interface.");
            dataModelController.getEngine().rundirectoryCheckerSet(i.getInterfacename());

          } catch (RemoteException e) {
            // Engine or schduler restart failed. Show user a message about
            // manual restart.
            logger.log(Level.SEVERE, "Engine or scheduler restart, or directory checker execution failed.");
            JOptionPane
                .showMessageDialog(
                    null,
                    "Restart of the engine or scheduler, or running of the directory checker " +
                    "failed. Please restart the engine manually and restart also the TechPackIDE. " +
                    "Run the directory checker from the AdminUI.",
                    "Interface activation", JOptionPane.INFORMATION_MESSAGE);
          }

          // Refresh the tree model.
          interfaceTreeDataModel.refresh();
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }

      return null;
    }
  }

  @Action(enabledProperty = "activateEnabled")
  public Task<Void, Void> activate() {
    final Task<Void, Void> activateTask = new ActivateTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    activateTask.setInputBlocker(new BusyIndicatorInputBlocker(activateTask, busyIndicator));

    return activateTask;
  }

  @Action(enabledProperty = "lockEnabled")
  public void lock() {
	  
	  String lockedBy = null;
	  String currentUser =  null;
	  
	    logger.log(Level.INFO, "Interface locked");
	    List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
	    try {
	      GenericActionNode current = tpTree.getSelected();
	      if (current instanceof DataTreeNode) {
	        DataTreeNode node = (DataTreeNode) current;
	        Datainterface i = (Datainterface) node.getRockDBObject();

	        /*Datainterface newi = new Datainterface(dataModelController.getRockFactory());
	        newi.setInterfacename(i.getInterfacename());
	        newi.setInterfaceversion(i.getInterfaceversion());
	        DatainterfaceFactory newiF = new DatainterfaceFactory(dataModelController.getRockFactory(), newi);*/

	        lockedBy = i.getLockedby();
	        currentUser =  LoginTask.getUserName() ;
	        	        
	        if (lockedBy == null ){
	        	lockedBy = "";
	        }
	        
	        if ( currentUser.equalsIgnoreCase(lockedBy) || lockedBy == "" ){
	        	alreadyIntfLocked = false; 
	        	i.setLockedby(dataModelController.getUserName());
	            i.setLockdate(new Timestamp(System.currentTimeMillis()));
	            interfaceTreeDataModel.modObj(i);
	          
	        } else{
	        	alreadyIntfLocked = true; 
	        	JOptionPane.showMessageDialog(this, "Interface " + i.getInterfacename() + ":" + i.getInterfaceversion()
	                    + " already locked by user " + i.getLockedby() + " at "
	                    + i.getLockdate());

	                refresh();
	                return;
	        }

	      }
	      interfaceTreeDataModel.refresh();
	    } catch (Exception e) {
	      ExceptionHandler.instance().handle(e);
	      e.printStackTrace();
	    } finally {
	      TreeState.loadExpansionState(tpTree, eList);
	      tpTree.requestFocus();
	    }
	  }

  @Action(enabledProperty = "unlockEnabled")
  public void unlock() {
    logger.log(Level.INFO, "unlocked");
    List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
    try {
      GenericActionNode current = tpTree.getSelected();
      if (current instanceof DataTreeNode) {
        DataTreeNode node = (DataTreeNode) current;
        Datainterface i = (Datainterface) node.getRockDBObject();
        i.setLockedby("");
        i.setLockdate(null);
        interfaceTreeDataModel.modObj(i);
      }
      interfaceTreeDataModel.refresh();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      TreeState.loadExpansionState(tpTree, eList);
      tpTree.requestFocus();
    }

  }

  private class MigrateTask extends Task<Void, Void> {

    public MigrateTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      if ((tpTree != null)) {

        try {

          Datainterface di = ((Datainterface) selectedNode.getRockDBObject());

          dataModelController.getRockFactory().getConnection().setAutoCommit(false);
          dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

          String rstate = "";

          // SETS
          Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
          mcs.setCollection_set_name(di.getInterfacename());
          Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
              mcs, true);
          Vector<Meta_collection_sets> tpList = mcsF.get();

          for (int tps = 0; tps < tpList.size(); tps++) {
            Meta_collection_sets tp = tpList.get(tps);
            rstate = tp.getVersion_number();

            dataModelController.getETLSetHandlingDataModel().copyRenameSets(di.getInterfacename(),
                tp.getVersion_number(), di.getInterfacename(), di.getInterfaceversion());

            dataModelController.getETLSetHandlingDataModel().deleteINTFSets(di.getInterfacename(),
                tp.getVersion_number());
          }

          dataModelController.getInterfaceTreeDataModel().migrate(di.getInterfacename(), di.getInterfaceversion(),
              "MIGRATED", rstate);

          interfaceTreeDataModel.deactivateInterface(di);

          dataModelController.getRockFactory().getConnection().commit();
          dataModelController.getEtlRockFactory().getConnection().commit();

        } catch (Exception e) {

          logger.warning("Error migrating Interface " + e);

          try {
            dataModelController.getRockFactory().getConnection().rollback();
            dataModelController.getEtlRockFactory().getConnection().rollback();
          } catch (Exception ex) {
            ExceptionHandler.instance().handle(ex);
            ex.printStackTrace();
          }

          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        } finally {

          try {
            dataModelController.getRockFactory().getConnection().setAutoCommit(true);
            dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);
          } catch (Exception e) {
            ExceptionHandler.instance().handle(e);
            e.printStackTrace();
          }
        }

        List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          dataModelController.getInterfaceTreeDataModel().refresh();
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
        }
      }
      return null;
    }
  }

  @Action(enabledProperty = "migrateEnabled")
  public Task<Void, Void> migrate() throws Exception {

    final Task<Void, Void> migrateTask = new MigrateTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    migrateTask.setInputBlocker(new BusyIndicatorInputBlocker(migrateTask, busyIndicator));

    return migrateTask;

  }

  private class ViewTask extends Task<Void, Void> {

    public ViewTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      logger.log(Level.INFO, "viewed");

      JFrame frame = new JFrame();
      frame.setName("ViewInterfaceWindow");
      frame.setIconImage(ericssonLogo.getImage());

      dataModelController.getEditInterfaceDataModel().setInterface(((DataTreeNode) selectedNode));
      dataModelController.getEditInterfaceDataModel().refresh();
      GeneralInterfaceTab editTab = new GeneralInterfaceTab(application, dataModelController, frame, false,
          selectedNode);

      frame.add(editTab);
      frame.setSize(700, 500);
      frame.setTitle("View Interface " + selectedNode);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setVisible(true);

      return null;
    }
  }

  @Action(enabledProperty = "viewEnabled")
  public Task<Void, Void> view() {
    final Task<Void, Void> viewTask = new ViewTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    viewTask.setInputBlocker(new BusyIndicatorInputBlocker(viewTask, busyIndicator));

    return viewTask;
  }

  private class EditTask extends Task<Void, Void> {

    public EditTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      setEditEnabled(false);
      editing = selectedNode.toString();
      logger.log(Level.INFO, "edited");

      JFrame frame = new JFrame();
      frame.setName("EditInterfaceWindow");
      frame.setIconImage(ericssonLogo.getImage());

      dataModelController.getEditInterfaceDataModel().setInterface(((DataTreeNode) selectedNode));
      dataModelController.getEditInterfaceDataModel().refresh();
      GeneralInterfaceTab editTab = new GeneralInterfaceTab(application, dataModelController, frame, true, selectedNode);

      frame.add(editTab);
      frame.setSize(700, 500);
      frame.setTitle("Edit Interface " + selectedNode);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new EditInterfaceListener());
      frame.setVisible(true);

      return null;
    }
  }

  @Action(enabledProperty = "editEnabled")
  public Task<Void, Void> edit() {
    final Task<Void, Void> editTask = new EditTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    editTask.setInputBlocker(new BusyIndicatorInputBlocker(editTask, busyIndicator));

    return editTask;
  }

  @Action(enabledProperty = "installEnabled")
  public void install() {
    logger.log(Level.INFO, "installed");

    List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
    dataModelController.getInterfaceTreeDataModel().refresh();
    TreeState.loadExpansionState(tpTree, eList);

    GenericActionNode current = tpTree.getSelected();

    if (current instanceof DataTreeNode) {

      JFrame frame = new JFrame();
      frame.setName("InstallInterfaceWindow");
      frame.setIconImage(ericssonLogo.getImage());

      DataTreeNode node = (DataTreeNode) tpTree.getSelected();
      Datainterface di = (Datainterface) node.getRockDBObject();

      CreateInstallationPackageView cip = new CreateInstallationPackageView(application, dataModelController, frame, di);
      frame.add(cip);
      frame.setSize(500, 200);
      frame.setTitle("Create installation package from Interface " + selectedNode);
      frame.addWindowListener(new EditInterfaceListener());
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setVisible(true);

    }
  }

  public Application getApplication() {
    return application;
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
  }

  private class SelectionListener implements TreeSelectionListener {

    public void valueChanged(TreeSelectionEvent e) {
      TreePath t = e.getPath();
      Object pointed = t.getLastPathComponent();
      if (pointed instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        Object tmp = node.getUserObject();
        if (tmp instanceof DataTreeNode) {
          setSelectedNode((DataTreeNode) tmp);
        } else {
          setSelectedNode(null);
        }
      } else {
        setSelectedNode(null);
      }
      logger.fine("Selected " + getSelectedNode());
    }
  }

  private class NewInterfaceListener implements WindowListener {

    public void windowActivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowActivated");
    }

    public void windowClosed(WindowEvent e) {
      logger.log(Level.FINEST, "windowClosed");
      setNewEnabled(true);
      setRefreshEnabled(true);

      List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        dataModelController.rockObjectsModified(dataModelController.getNewInterfaceDataModel());
      } catch (Exception ee) {

      }
      TreeState.loadExpansionState(tpTree, eList);

    }

    public void windowClosing(WindowEvent e) {
      logger.log(Level.FINEST, "windowClosing");
    }

    public void windowDeactivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeactivated");
    }

    public void windowDeiconified(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeiconified");
    }

    public void windowIconified(WindowEvent e) {
      logger.log(Level.FINEST, "windowIconified");
    }

    public void windowOpened(WindowEvent e) {
      logger.log(Level.FINEST, "windowOpened");
    }
  }

  private class EditInterfaceListener implements WindowListener {

    public void windowActivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowActivated");
    }

    public void windowClosed(WindowEvent e) {
      logger.log(Level.FINEST, "windowClosed");
	   //EQEV-4605 starts
      setDeleteEnabled(true);
      //EQEV-4605 ends
      setEditEnabled(true);
      setNewEnabled(true);	//EQEV-4298
      setRefreshEnabled(true);
      editing = null;
    }

    public void windowClosing(WindowEvent e) {
	
      logger.log(Level.FINEST, "windowClosing");
    }

    public void windowDeactivated(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeactivated");
    }

    public void windowDeiconified(WindowEvent e) {
      logger.log(Level.FINEST, "windowDeiconified");
    }

    public void windowIconified(WindowEvent e) {
      logger.log(Level.INFO, "windowIconified");
    }

    public void windowOpened(WindowEvent e) {
	//EQEV-4605 Starts
    	setDeleteEnabled(false);
    	//EQEV-4605 ends
	setNewEnabled(false);	//EQEV-4298
      logger.log(Level.FINEST, "windowOpened");
    }
  }

  public boolean isInstallEnabled() {
    return installEnabled;
  }

  public void setInstallEnabled(boolean installEnabled) {
    boolean oldvalue = this.installEnabled;
    this.installEnabled = installEnabled;
    firePropertyChange("installEnabled", oldvalue, installEnabled);
  }

  public boolean isMigrateEnabled() {
    return migrateEnabled;
  }

  public void setMigrateEnabled(boolean migrateEnabled) {
    boolean oldvalue = this.migrateEnabled;
    this.migrateEnabled = migrateEnabled;
    firePropertyChange("migrateEnabled", oldvalue, migrateEnabled);
  }

  public boolean isLockEnabled() {
    return lockEnabled;
  }

  public void setLockEnabled(boolean lockEnabled) {
    boolean oldvalue = this.lockEnabled;
    this.lockEnabled = lockEnabled;
    firePropertyChange("lockEnabled", oldvalue, lockEnabled);
  }

  public boolean isUnlockEnabled() {
    return unlockEnabled;
  }

  public void setUnlockEnabled(boolean unlockEnabled) {
    boolean oldvalue = this.unlockEnabled;
    this.unlockEnabled = unlockEnabled;
    firePropertyChange("unlockEnabled", oldvalue, unlockEnabled);
  }

  public boolean isEditEnabled() {
    return editEnabled;
  }

  public void setEditEnabled(boolean editEnabled) {
    boolean oldvalue = this.editEnabled;
    this.editEnabled = editEnabled;
    firePropertyChange("editEnabled", oldvalue, editEnabled);
  }

  public boolean isNewEnabled() {
    return newEnabled;
  }

  public void setNewEnabled(boolean newEnabled) {
    boolean oldvalue = this.newEnabled;
    this.newEnabled = newEnabled;
    firePropertyChange("newEnabled", oldvalue, newEnabled);
  }

  public boolean isDeleteEnabled() {
    return deleteEnabled;
  }

  public void setDeleteEnabled(boolean deleteEnabled) {
    boolean oldvalue = this.deleteEnabled;
    this.deleteEnabled = deleteEnabled;
    firePropertyChange("deleteEnabled", oldvalue, deleteEnabled);
  }

  public boolean isViewEnabled() {
    return viewEnabled;
  }

  public void setViewEnabled(boolean viewEnabled) {
    boolean oldvalue = this.viewEnabled;
    this.viewEnabled = viewEnabled;
    firePropertyChange("viewEnabled", oldvalue, viewEnabled);
  }

  public boolean isTestEnabled() {
    return testEnabled;
  }

  public void setTestEnabled(boolean testEnabled) {
    boolean oldvalue = this.testEnabled;
    this.testEnabled = testEnabled;
    firePropertyChange("testEnabled", oldvalue, testEnabled);
  }

  public boolean isDeActivateEnabled() {
    return deActivateEnabled;
  }

  public void setDeActivateEnabled(boolean deActivateEnabled) {
    boolean oldvalue = this.deActivateEnabled;
    this.deActivateEnabled = deActivateEnabled;
    firePropertyChange("deActivateEnabled", oldvalue, deActivateEnabled);
  }

  public boolean isActivateEnabled() {
    return activateEnabled;
  }

  public void setActivateEnabled(boolean activateEnabled) {
    boolean oldvalue = this.activateEnabled;
    this.activateEnabled = activateEnabled;
    firePropertyChange("activateEnabled", oldvalue, activateEnabled);
  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }

  public void setSelectedNode(DataTreeNode selectedNode) {
    DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);

    boolean lockIsEnabled;
    boolean unlockIsEnabled;
    boolean activeIsEnabled;
    boolean deActiveIsEnabled;
    boolean installIsEnabled;
    boolean testIsEnabled;
    boolean deleteIsEnabled;
    boolean viewsEnabled;
    boolean editIsEnabled;
    boolean migrateIsEnabled;
    boolean refreshIsEnabled;

    if (this.selectedNode == null) {
      // nothing selected
      lockIsEnabled = false;
      installIsEnabled = false;
      unlockIsEnabled = false;
      deActiveIsEnabled = false;
      activeIsEnabled = false;
      testIsEnabled = false;
      deleteIsEnabled = false;
      viewsEnabled = false;
      editIsEnabled = false;
      migrateIsEnabled = false;
      refreshIsEnabled = true;

    } else {

      refreshIsEnabled = true;

      // INTF selected
      lockIsEnabled = this.selectedNode.locked == null || this.selectedNode.locked.equals("");
      unlockIsEnabled = this.dataModelController.getUserName().equals(this.selectedNode.locked);

      // if locked the we can activate or inactivate
      if (this.dataModelController.getUserName().equals(this.selectedNode.locked)) {

        // locked
        deActiveIsEnabled = this.selectedNode.active;
        activeIsEnabled = !deActiveIsEnabled;
        editIsEnabled = true;

        if (this.selectedNode.active) {
          //EQEV-4605 Starts
          deleteIsEnabled = true;
        //EQEV-4605 ends
          installIsEnabled = true;
        } else {
          deleteIsEnabled = true;
          installIsEnabled = false;
        }

        migrateIsEnabled = false;

        if (this.selectedNode.active) {
          testIsEnabled = true;
        } else {
          testIsEnabled = false;
        }

        // if eniq level is null or 1.0 then must emigrate
        String eLevel = ((Datainterface) this.selectedNode.getRockDBObject()).getEniq_level();
        if (eLevel == null || eLevel.equalsIgnoreCase("1.0")) {
          // must emigrate
          migrateIsEnabled = true;
          deActiveIsEnabled = false;
          activeIsEnabled = false;
          editIsEnabled = false;
          installIsEnabled = false;
          testIsEnabled = false;
        }

        if (eLevel != null && eLevel.equalsIgnoreCase("MIGRATED")) {
          // must emigrate
          migrateIsEnabled = false;
          deActiveIsEnabled = false;
          activeIsEnabled = false;
          editIsEnabled = false;
          installIsEnabled = false;
          testIsEnabled = false;
        }

      } else {

        // not locked
        deActiveIsEnabled = false;
        activeIsEnabled = false;
        editIsEnabled = false;
        deleteIsEnabled = false;
        installIsEnabled = false;
        migrateIsEnabled = false;
        testIsEnabled = false;
      }

      viewsEnabled = true;

      refreshIsEnabled = newEnabled;

      if (editing != null) {
        editIsEnabled = false;
        refreshIsEnabled = false;
      }
    }

    this.setInstallEnabled(installIsEnabled);
    this.setLockEnabled(lockIsEnabled);
    this.setUnlockEnabled(unlockIsEnabled);

    this.setActivateEnabled(activeIsEnabled);
    this.setDeActivateEnabled(deActiveIsEnabled);

    this.setTestEnabled(testIsEnabled);
    this.setDeleteEnabled(deleteIsEnabled);
    this.setViewEnabled(viewsEnabled);
    this.setEditEnabled(editIsEnabled);
    this.setMigrateEnabled(migrateIsEnabled);
    this.setRefreshEnabled(refreshIsEnabled);
  }

  private class ParseDebugListener implements WindowListener {

    public void windowActivated(WindowEvent e) {
      logger.log(Level.INFO, "windowActivated");
    }

    public void windowClosed(WindowEvent e) {
      logger.log(Level.INFO, "windowClosed");
      if (testing != null) {
        setTestEnabled(true);
        testing = null;
      }
    }

    public void windowClosing(WindowEvent e) {
      logger.log(Level.INFO, "windowClosing");
    }

    public void windowDeactivated(WindowEvent e) {
      logger.log(Level.INFO, "windowDeactivated");
    }

    public void windowDeiconified(WindowEvent e) {
      logger.log(Level.INFO, "windowDeiconified");
    }

    public void windowIconified(WindowEvent e) {
      logger.log(Level.INFO, "windowIconified");
    }

    public void windowOpened(WindowEvent e) {
      logger.log(Level.INFO, "windowOpened");
    }
  }

}
