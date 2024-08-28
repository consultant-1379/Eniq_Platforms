package com.ericsson.eniq.techpacksdk;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.GenericActionNode;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.component.TreeState;

import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.createDocuments.CreateTechPackDescriptionView;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;
import com.ericsson.eniq.techpacksdk.view.newTechPack.*;


@SuppressWarnings("serial")
public class ManageTechPackView extends JPanel {

  private static final Logger logger = Logger.getLogger(ManageTechPackView.class.getName());

  final private SingleFrameApplication application;

  final GenericActionTree tpTree;

  private DataTreeNode selectedNode;

  private String techpackUnderModification = null;
  
  boolean isActive=false;

  private final NewTechPackFunctionality NewTechPack;

  private boolean compareEnabled = true;

  private boolean deleteEnabled = false;

  private boolean descriptionDocEnabled = false;

  private boolean editEnabled = false;

  private boolean installEnabled = false;

  private boolean lockEnabled = false;

  private boolean migrateEnabled = false;

  private boolean newEnabled = false;

  private boolean refreshEnabled = false;

  private boolean unlockEnabled = false;

  private boolean viewEnabled = false;
  
  public boolean editStatus; 
  
  public boolean alreadyLocked = false;
  
  public static boolean twist = false; //EQEV-4298 similar in techpack

  /**
   * Constructor.
   * 
   * @param application
   * @param dataModelController
   */
  public ManageTechPackView(final SingleFrameApplication application, final DataModelController dataModelController) {

    super(new GridBagLayout());
    this.application = application;
    this.dataModelController = dataModelController;

    ericssonLogo = application.getContext().getResourceMap(getClass()).getImageIcon("ericsson.icon");

    // creating NewTechPackFunctionality object
    NewTechPack = new NewTechPackFunctionality();

    // create tree panel

    tpTree = new GenericActionTree(dataModelController.getTechPackTreeDataModel());
    tpTree.addRootAction(getAction("refresh"));
    tpTree.addRootAction(getAction("addnew"));
    tpTree.addAction(getAction("view"));
    tpTree.addAction(getAction("edit"));
    tpTree.addAction(getAction("remove"));
    tpTree.addAction(getAction("lock"));
    tpTree.addAction(getAction("unlock"));
    tpTree.addAction(getAction("migrate"));
    tpTree.addSubAction("Create documents", getAction("descriptionDoc"));

    tpTree.setToolTipText("");
    tpTree.setCellRenderer(new TPTreeRenderer(dataModelController));
    tpTree.addTreeSelectionListener(new SelectionListener());
    tpTree.setName("TechPackTree");

    final JScrollPane techPackPanel = new JScrollPane(tpTree);

    // create button panel
    final JButton refreshtpButton = new JButton("Refresh");
    refreshtpButton.setAction(getAction("refresh"));
    refreshtpButton.setName("TechPackRefresh");

    final JButton removetpButton = new JButton("Remove");
    removetpButton.setAction(getAction("remove"));
    removetpButton.setName("TechPackRemove");

    final JButton newtpButton = new JButton("New");
    newtpButton.setAction(getAction("addnew"));
    newtpButton.setName("TechPackAddNew");

    final JButton viewtpButton = new JButton("View");
    viewtpButton.setAction(getAction("view"));
    viewtpButton.setName("TechPackView");

    final JButton edittpButton = new JButton("Edit");
    edittpButton.setAction(getAction("edit"));
    edittpButton.setName("TechPackEdit");

    final JButton installtpButton = new JButton("Install");
    installtpButton.setAction(getAction("install"));
    installtpButton.setName("TechPackInstall");

//    final JButton comparetpButton = new JButton("Compare");
//    comparetpButton.setAction(getAction("compare"));
//    comparetpButton.setName("TechPackCompare");

    final JButton locktpButton = new JButton("Lock");
    locktpButton.setAction(getAction("lock"));
    locktpButton.setName("TechPackLock");

    final JButton unlocktpButton = new JButton("Unlock");
    unlocktpButton.setAction(getAction("unlock"));
    unlocktpButton.setName("TechPackUnlock");

    final JButton migrateButton = new JButton("Migrate");
    migrateButton.setAction(getAction("migrate"));
    migrateButton.setName("TechPackMigrate");

    final JButton quitButton = new JButton("Quit");
    quitButton.setAction(getAction("quit"));
    quitButton.setName("TechPackQuit");

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(refreshtpButton);
    buttonPanel.add(removetpButton);
    //buttonPanel.add(comparetpButton);
    buttonPanel.add(installtpButton);
    buttonPanel.add(newtpButton);
    buttonPanel.add(viewtpButton);
    buttonPanel.add(edittpButton);
    buttonPanel.add(migrateButton);

    buttonPanel.add(locktpButton);
    buttonPanel.add(unlocktpButton);
    buttonPanel.add(quitButton);

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

    this.add(techPackPanel, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

    setNewEnabled(true);
    this.setRefreshEnabled(true);

  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }

  /**
   * Updates the enabled and disabled properties for all the buttons and menu
   * options based on the selected node in the tree.
   * 
   * @param selectedNode
   */
  public void setSelectedNode(final DataTreeNode selectedNode) {

    //boolean compareIsEnabled;
    boolean deleteIsEnabled;
    boolean descriptionDocIsEnabled;
    boolean editIsEnabled;
    boolean installIsEnabled;
    boolean lockIsEnabled;
    boolean migrateIsEnabled;
    boolean newIsEnabled;
    boolean refreshIsEnabled;
    boolean unlockIsEnabled;
    boolean viewIsEnabled;
    boolean allowToMigrate = false;
    
    // Flag for allowing editing of the techpack.
    boolean allowToEdit = false;

    final DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);
    
    // Check Edit Status
    editStatus = GeneralTechPackTab.getEditIsOpened();
    logger.log(Level.FINE, "editStatus is : " +editStatus);
    
    if(!editStatus){

    // Default values.
    newIsEnabled = true;
    refreshIsEnabled = true;
    //compareIsEnabled = false;
    migrateIsEnabled = false;
    descriptionDocIsEnabled = true;

    // Enabled values for option depend on the selection of a techpack.
    if (this.selectedNode == null) {
      // A techpack is not selected.
      lockIsEnabled = false;
      unlockIsEnabled = false;
      editIsEnabled = false;
      installIsEnabled = false;
      viewIsEnabled = false;
      descriptionDocIsEnabled = false;
      migrateIsEnabled = false;
      deleteIsEnabled = false;
    } else {

      // A techpack is selected.

      // Get the versioning for the selected techpack.
      final Versioning selectedVersioning = (Versioning) this.selectedNode.getRockDBObject();

      // Get the current user.
      final User user = this.dataModelController.getUser();

      // A flag for custom techpack type.
      final boolean isCustomTP = Utils.isCustomTP(selectedVersioning.getTechpack_type());
      
      // Allow editing of techpacks based on user authorisation.
      if (isCustomTP && user.authorize(User.DO_CUSTOMTP)) {
        allowToEdit = true;
      } else if (!isCustomTP && user.authorize(User.DO_PRODUCTTP)) {
        allowToEdit = true;
      }
      
      // If the user is already editing a techpack, then all the options
      // (except Quit).
      if (techpackUnderModification != null) {
        deleteIsEnabled = false;
        editIsEnabled = false;
        installIsEnabled = false;
        newIsEnabled = false;
        refreshIsEnabled = true;
        lockIsEnabled = true;
        unlockIsEnabled = false;
        viewIsEnabled = true;
      }

      viewIsEnabled = true;

      // If a non-migrated TP, then both Users and RnD should be able to migrate
      // it. This means Users will need to be able to Lock first, then Migrate.
      // HK67521:Migration not supported with user access to IDE, 2009-06-09
      final String eLevel = selectedVersioning.getEniq_level();
      if ((eLevel == null || eLevel.equalsIgnoreCase("1.0"))) {
        allowToMigrate = true;
      }

      lockIsEnabled = (this.selectedNode.locked == null || this.selectedNode.locked.equals(""))
          && (allowToEdit || allowToMigrate);

      unlockIsEnabled = this.dataModelController.getUserName().equalsIgnoreCase(this.selectedNode.locked);

      // Set editing enabled when 1) another techpack is not being edited, 2)
      // the current user has this techpack locked, 3) editing is authorised,
      // and 4) the techpack level is not too old.
      editIsEnabled = techpackUnderModification == null
          && this.dataModelController.getUserName().equalsIgnoreCase(this.selectedNode.locked) && allowToEdit
          && (eLevel != null && eLevel.equalsIgnoreCase(Constants.CURRENT_TECHPACK_ENIQ_LEVEL));

      // Set delete enabled based on if editing is enabled.
      deleteIsEnabled = editIsEnabled;

      isActive = dataModelController.getDWHTreeDataModel().getActiveTechpacks().contains(
          selectedNode.toString());
      installIsEnabled = this.dataModelController.getUserName().equalsIgnoreCase(this.selectedNode.locked)
          && allowToEdit;

      //refreshIsEnabled = newEnabled;

      if (unlockIsEnabled && (eLevel == null || eLevel.equalsIgnoreCase("1.0"))) {
        // Migrate is needed
        editIsEnabled = false;
        deleteIsEnabled = true && allowToEdit;
        installIsEnabled = false;
        migrateIsEnabled = true && (allowToEdit || allowToMigrate);
      }

      if (eLevel != null && eLevel.equalsIgnoreCase("MIGRATED")) {
        // Already migrated
        editIsEnabled = false;
        deleteIsEnabled = true && allowToEdit;
        //compareIsEnabled = false;
        installIsEnabled = false;
        viewIsEnabled = true;
        descriptionDocIsEnabled = false;
        migrateIsEnabled = false;
      }

    } // end if (this.selectedNode == null)
    }else{
    	newIsEnabled = true;
    	refreshIsEnabled = true;
    	editIsEnabled = false;
        installIsEnabled = false;
        viewIsEnabled = true;
        descriptionDocIsEnabled = false;
        migrateIsEnabled = false;
        deleteIsEnabled = false;
        
        lockIsEnabled = (this.selectedNode.locked == null || this.selectedNode.locked.equals(""))
                && (allowToEdit || allowToMigrate);

        unlockIsEnabled = this.dataModelController.getUserName().equalsIgnoreCase(this.selectedNode.locked);
    }

    // Set the enabled properties based on the values set above.
    this.setNewEnabled(newIsEnabled);
    this.setDeleteEnabled(deleteIsEnabled);
    this.setLockEnabled(lockIsEnabled);
    this.setUnlockEnabled(unlockIsEnabled);
    this.setEditEnabled(editIsEnabled);
    //this.setCompareEnabled(compareIsEnabled);
    this.setInstallEnabled(installIsEnabled);
    this.setViewEnabled(viewIsEnabled);
    this.setMigrateEnabled(migrateIsEnabled);
    this.setRefreshEnabled(refreshIsEnabled);
    this.setDescriptionDocEnabled(descriptionDocIsEnabled);

  }

  public boolean isLockEnabled() {
    return lockEnabled;
  }

  public void setLockEnabled(final boolean lockEnabled) {
    final boolean oldvalue = this.lockEnabled;
    this.lockEnabled = lockEnabled;
    firePropertyChange("lockEnabled", oldvalue, lockEnabled);
  }
  


  // private boolean deleteEnabled = false;
  //
  // public boolean isDeleteEnabled() {
  // return lockEnabled;
  // }
  //
  // public void setDeleteEnabled(boolean deleteEnabled) {
  // boolean oldvalue = this.deleteEnabled;
  // this.deleteEnabled = deleteEnabled;
  // firePropertyChange("deleteEnabled", oldvalue, deleteEnabled);
  // }

  // private TechPackTreeDataModel techPackTreeDataModel;

  private final DataModelController dataModelController;

  public boolean isUnlockEnabled() {
    return unlockEnabled;
  }

  public void setUnlockEnabled(final boolean unlockEnabled) {
    final boolean oldvalue = this.unlockEnabled;
    this.unlockEnabled = unlockEnabled;
    firePropertyChange("unlockEnabled", oldvalue, unlockEnabled);
  }

  public boolean isDescriptionDocEnabled() {
    return descriptionDocEnabled;
  }

  public void setDescriptionDocEnabled(final boolean descriptionDocEnabled) {
    final boolean oldvalue = this.descriptionDocEnabled;
    this.descriptionDocEnabled = descriptionDocEnabled;
    firePropertyChange("descriptionDocEnabled", oldvalue, descriptionDocEnabled);
  }

  public boolean isViewEnabled() {
    return viewEnabled;
  }

  public void setViewEnabled(final boolean viewEnabled) {
    final boolean oldvalue = this.viewEnabled;
    this.viewEnabled = viewEnabled;
    firePropertyChange("viewEnabled", oldvalue, viewEnabled);
  }

  public boolean isRefreshEnabled() {
    return refreshEnabled;
  }

  public void setRefreshEnabled(final boolean refreshEnabled) {
    final boolean oldvalue = this.refreshEnabled;
    this.refreshEnabled = refreshEnabled;
    firePropertyChange("refreshEnabled", oldvalue, refreshEnabled);
  }

  public boolean isEditEnabled() {
    return editEnabled;
  }

  public void setEditEnabled(final boolean editEnabled) {
    final boolean oldvalue = this.editEnabled;
    this.editEnabled = editEnabled;
    firePropertyChange("editEnabled", oldvalue, editEnabled);
  }

  public boolean isDeleteEnabled() {
    return deleteEnabled;
  }

  public void setDeleteEnabled(final boolean deleteEnabled) {
    final boolean oldvalue = this.deleteEnabled;
    this.deleteEnabled = deleteEnabled;
    firePropertyChange("deleteEnabled", oldvalue, deleteEnabled);
  }

  public boolean isNewEnabled() {
    return newEnabled;
  }

  public void setNewEnabled(final boolean newEnabled) {
    final boolean oldvalue = this.newEnabled;
    this.newEnabled = newEnabled;
    firePropertyChange("newEnabled", oldvalue, newEnabled);
  }

  public boolean isCompareEnabled() {
    return compareEnabled;
  }

  public void setCompareEnabled(final boolean compareEnabled) {
    final boolean oldvalue = this.compareEnabled;
    this.compareEnabled = compareEnabled;
    firePropertyChange("compareEnabled", oldvalue, compareEnabled);
  }

  public boolean isInstallEnabled() {
    return installEnabled;
  }

  public void setInstallEnabled(final boolean installEnabled) {
    final boolean oldvalue = this.installEnabled;
    this.installEnabled = installEnabled;
    firePropertyChange("installEnabled", oldvalue, installEnabled);
  }

  @Action(block = BlockingScope.APPLICATION, enabledProperty = "refreshEnabled")
  public Task<Void, Void> refresh() {
    Task<Void, Void> refreshDataTask = null;
    if ((tpTree != null)) {
      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        /*
         * GenericActionNode current = tpTree.getSelected(); if (current
         * instanceof DataTreeNode) {
         * 
         * 
         * if (tpTree.getSelected() != null) { DataTreeNode node =
         * (DataTreeNode) tpTree.getSelected(); Versioning v = (Versioning)
         * node.getRockDBObject();
         * dataModelController.getTechPackTreeDataModel().setSelected(v); } }
         */
        refreshDataTask = new RefreshDataTask(eList);
        final BusyIndicator busyIndicator = new BusyIndicator();
        application.getMainFrame().setGlassPane(busyIndicator);
        refreshDataTask.setInputBlocker(new BusyIndicatorInputBlocker(refreshDataTask, busyIndicator));
      } catch (final Exception e) {
        logger.log(Level.SEVERE, "Error refreshing Tech Pack Tree", e);
      }
    }
    return refreshDataTask;
  }

  @Action(enabledProperty = "newEnabled")
  public void addnew() {

	  //EQEV-4212
	  //techpackUnderModification = "new";
        
		Versioning ver = null;
		
	    final GenericActionNode current = tpTree.getSelected();
		// EQEV-2862 : Auto lock functionality
	    lock();
	    if (current instanceof DataTreeNode) {
	        final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
	        ver = ((Versioning) node.getRockDBObject());
	    }
	    if(!alreadyLocked){
	        this.setNewEnabled(false);
	        setRefreshEnabled(false);
	        logger.log(Level.INFO, "new created");
	        final JFrame frame = new JFrame();
	        frame.setName("CreateNewTechPack");
	        frame.setIconImage(ericssonLogo.getImage());
	
	        final ManageNewTechPackView newTechPackView = new ManageNewTechPackView(ver, application, dataModelController,
	            frame);
	        frame.add(newTechPackView);
	        frame.setSize(800, 600);
	        frame.setTitle("New Tech Pack");
	        frame.setName("NewTechPackWindow");
	        frame.setVisible(true);
	        frame.addWindowListener(new NewTechpackListener());
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }else{
	    	JOptionPane.showMessageDialog(this, "Can't create new TechPack because " + ver.getTechpack_name() + " : " + ver.getTechpack_version()
	                  + " already locked by user " + ver.getLockedby() + " at "
	                  + ver.getLockdate());
	    	refresh();
	    }

	}
	
    
   


  private final ImageIcon ericssonLogo;

  public boolean isMigrateEnabled() {
    return migrateEnabled;
  }

  public void setMigrateEnabled(final boolean migrateEnabled) {
    final boolean oldvalue = this.migrateEnabled;
    this.migrateEnabled = migrateEnabled;
    firePropertyChange("migrateEnabled", oldvalue, migrateEnabled);
  }

  private class MigrateTask extends Task<Void, Void> {

    public MigrateTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      if ((tpTree != null)) {

        try {

          final Versioning vi = ((Versioning) selectedNode.getRockDBObject());

          // Get the from state ENIQ_LEVEL
          final String fromEniqLevel = vi.getEniq_level();

          logger.log(Level.INFO, "Migrate of techpack " + vi.getVersionid() + " started.");

          dataModelController.getRockFactory().getConnection().setAutoCommit(false);
          dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

          logger.log(Level.FINE, "Migrating version info.");

          final String version = vi.getVersionid().substring(vi.getVersionid().lastIndexOf(":") + 1);
          dataModelController.getTechPackDataModel().migrate(vi.getVersionid(), "MIGRATED");

          // SETS
          if (fromEniqLevel.equals("1.0")) {
          logger.log(Level.FINE, "Migrating ETL sets.");
            final Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
         
          mcs.setCollection_set_name(vi.getTechpack_name());
          mcs.setVersion_number(vi.getTechpack_version());
            final Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController
                .getEtlRockFactory(), mcs, true);
            final Vector<Meta_collection_sets> tpList = mcsF.get();

          for (int tps = 0; tps < tpList.size(); tps++) {
              final Meta_collection_sets tp = tpList.get(tps);
            dataModelController.getETLSetHandlingDataModel().copyRenameSets(vi.getTechpack_name(),
                tp.getVersion_number(), vi.getTechpack_name(), version);
          }
          }

          // Measurements
          logger.log(Level.FINE, "Migrating measurement types.");
          dataModelController.getMeasurementTypeDataModel().migrate(vi.getVersionid(), vi.getTechpack_type(),
              fromEniqLevel);

          // References
          logger.log(Level.FINE, "Migrating reference types.");
          dataModelController.getReferenceTypeDataModel().migrate(vi.getVersionid(), fromEniqLevel);

          // Transformers and transformations
          logger.log(Level.FINE, "Migrating transformers and transformations.");
          dataModelController.getTransformerDataModel().migrate(vi.getVersionid(), fromEniqLevel);

          // Data Formats
          logger.log(Level.FINE, "Migrating data formats.");
          dataModelController.getDataformatDataModel().migrate(vi.getVersionid(), fromEniqLevel);

          // BusyHours
          logger.log(Level.FINE, "Migrating busy hours.");
          dataModelController.getBusyhourHandlingDataModel().migrate(vi, fromEniqLevel);

          logger.log(Level.FINE, "Committing changes to the database.");
          dataModelController.getRockFactory().getConnection().commit();
          dataModelController.getEtlRockFactory().getConnection().commit();

          logger.log(Level.FINE, "Reloading the scheduler.");
          dataModelController.schedulerReload();

        } catch (final Exception e) {

          logger.warning("Error migrating Techpack " + e);

          try {
            dataModelController.getRockFactory().getConnection().rollback();
            dataModelController.getEtlRockFactory().getConnection().rollback();
          } catch (final Exception ex) {
            ExceptionHandler.instance().handle(ex);
            ex.printStackTrace();
          }

          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        } finally {

          try {
            dataModelController.getRockFactory().getConnection().setAutoCommit(true);
            dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);
          } catch (final Exception e) {
            ExceptionHandler.instance().handle(e);
            e.printStackTrace();
          }
        }

        logger.log(Level.INFO, "Migrate of techpack finished.");
        
        final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          dataModelController.getTechPackTreeDataModel().refresh();
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
        }
      }
      return null;
    }
  }

  @Action(enabledProperty = "migrateEnabled")
  public Task<Void, Void> migrate() throws Exception {

    final Task<Void, Void> MigrateTask = new MigrateTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    MigrateTask.setInputBlocker(new BusyIndicatorInputBlocker(MigrateTask, busyIndicator));

    return MigrateTask;

  }

  private class RemoveTask extends Task<Void, Void> {

    public RemoveTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      if ((tpTree != null) && (tpTree.getSelected() != null)) {
        logger.log(Level.INFO, "remove tech pack");
        final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          try {
            final GenericActionNode current = tpTree.getSelected();
            if (current instanceof DataTreeNode) {
              final DataTreeNode node = (DataTreeNode) tpTree.getSelected();

              // Versioning ver = ((Versioning) node.getRockDBObject());
              final String tpname = ((Versioning) node.getRockDBObject()).getVersionid();

              // Get the techpack version. The version depends on the migrate
              // status of the techpack. The version part from version_id is
              // used for migrated, and
              // the techpack_version (=r-state) is used for the non-migrated
              // techpacks.
              String version = null;
              if (((Versioning) node.getRockDBObject()).getEniq_level().equals("1.0")) {
                version = ((Versioning) node.getRockDBObject()).getTechpack_version();
                logger.log(Level.FINE, "Removing non-migrated techpack with version: " + version);
              } else {
                version = tpname.substring(tpname.lastIndexOf(":") + 1);
                logger.log(Level.FINE, "Removing techpack with version: " + version);
              }

              // Vector<Interfacedependency> deactivatedInterfaces = new
              // Vector<Interfacedependency>();

              /*
               * // deactivate interface Interfacedependency idp = new
               * Interfacedependency(dataModelController.getRockFactory());
               * idp.setTechpackname(ver.getTechpack_name());
               * idp.setTechpackversion(ver.getTechpack_version());
               * InterfacedependencyFactory idpF = new
               * InterfacedependencyFactory
               * (dataModelController.getRockFactory(),idp);
               * Iterator<Interfacedependency> idpFI = idpF.get().iterator();
               * while(idpFI.hasNext()){ Interfacedependency i =
               * (Interfacedependency)idpFI.next();
               * deactivatedInterfaces.add(i); Datainterface di = new
               * Datainterface(dataModelController.getRockFactory());
               * di.setInterfacename(i.getInterfacename());
               * di.setInterfaceversion(i.getInterfaceversion());
               * DatainterfaceFactory diF = new
               * DatainterfaceFactory(dataModelController.getRockFactory(),di);
               * if (diF!=null){
               * dataModelController.getInterfaceTreeDataModel().
               * deactivateInterface(diF.getElementAt(0)); } }
               */

              // Remove techpack of the given name from the DWHREP database
              NewTechPack.removeTechPack(tpname, dataModelController.getRockFactory());

              // Remove the ETL sets from ETLREP database.
              dataModelController.getETLSetHandlingDataModel().deleteTPSets(
                  ((Versioning) node.getRockDBObject()).getTechpack_name(), version);

              // Notify the depending models
              dataModelController.rockObjectsModified(dataModelController.getTechPackDataModel());

              /*
               * // activate the deActivated interfaces
               * Iterator<Interfacedependency> iter =
               * deactivatedInterfaces.iterator(); while (iter.hasNext()){
               * Interfacedependency i = (Interfacedependency)iter.next();
               * Datainterface di = new
               * Datainterface(dataModelController.getRockFactory());
               * di.setInterfacename(i.getInterfacename());
               * di.setInterfaceversion(i.getInterfaceversion());
               * DatainterfaceFactory diF = new
               * DatainterfaceFactory(dataModelController.getRockFactory(),di);
               * 
               * if (diF != null){
               * dataModelController.getInterfaceTreeDataModel(
               * ).activateInterface(diF.getElementAt(0)); }
               * 
               * }
               */
              dataModelController.getTechPackTreeDataModel().refresh();
            }
          } catch (final Exception e) {
            logger.log(Level.SEVERE, "Tech Pack remove error", e);
          }
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
          tpTree.requestFocus();
        }

      } else {
        JOptionPane.showMessageDialog(null, "Select removed Tech Pack.");
      }
      return null;
    }
  }

  private String isActiveInterfaces(final String techpack, final Versioning versioning) {

    String result = "";

    try {

      // check if we find interfaces for this techpack...

      final Interfacetechpacks itp = new Interfacetechpacks(dataModelController.getRockFactory());
      itp.setTechpackname(techpack);
      itp.setTechpackversion(versioning.getTechpack_version());
      final InterfacetechpacksFactory itpF = new InterfacetechpacksFactory(dataModelController.getRockFactory(), itp);

      final Vector<Interfacetechpacks> tmp = new Vector<Interfacetechpacks>();

      if (itpF != null && itpF.size() > 0) {
        tmp.addAll(itpF.get());

        final Iterator<Interfacetechpacks> tmpI = tmp.iterator();
        while (tmpI.hasNext()) {
          final Interfacetechpacks i = tmpI.next();

          final Interfacemeasurement di = new Interfacemeasurement(dataModelController.getRockFactory());
          di.setInterfacename(i.getInterfacename());
          di.setInterfaceversion(i.getInterfaceversion());
          final InterfacemeasurementFactory diF = new InterfacemeasurementFactory(dataModelController.getRockFactory(),
              di);
          if (diF != null) {
            if (diF.getElementAt(0).getStatus() == 1l) {
              if (result.length() == 0) {
                result += diF.getElementAt(0).getInterfacename() + "/" + diF.getElementAt(0).getInterfaceversion();
              } else {
                result += ", " + diF.getElementAt(0).getInterfacename() + "/"
                    + diF.getElementAt(0).getInterfaceversion();
              }
            }
          }
        }

      } else {

        // if we did not find any interfaces we must check with version N/A to
        // find out non migrated interfaces...

        final Interfacetechpacks itp2 = new Interfacetechpacks(dataModelController.getRockFactory());
        itp2.setTechpackname(techpack);
        itp2.setTechpackversion("N/A");
        final InterfacetechpacksFactory itp2F = new InterfacetechpacksFactory(dataModelController.getRockFactory(),
            itp2);

        if (itp2F != null && itp2F.size() > 0) {
          tmp.addAll(itp2F.get());
        }

        final Iterator<Interfacetechpacks> tmpI = tmp.iterator();

        while (tmpI.hasNext()) {
          final Interfacetechpacks i = tmpI.next();

          final Interfacemeasurement di = new Interfacemeasurement(dataModelController.getRockFactory());
          di.setInterfacename(i.getInterfacename());
          di.setInterfaceversion(i.getInterfaceversion());
          final InterfacemeasurementFactory diF = new InterfacemeasurementFactory(dataModelController.getRockFactory(),
              di);

          if (diF != null && diF.get().size() > 0) {

            final Iterator<Interfacemeasurement> diFI = diF.get().iterator();
            while (diFI.hasNext()) {
              final Interfacemeasurement it = diFI.next();

              final Dataformat dim = new Dataformat(dataModelController.getRockFactory());
              dim.setDataformatid(it.getDataformatid());
              dim.setVersionid(versioning.getVersionid());
              final DataformatFactory dimF = new DataformatFactory(dataModelController.getRockFactory(), dim);

              if (dimF.get().size() > 0) {

                result += diF.getElementAt(0).getInterfacename() + "/" + diF.getElementAt(0).getInterfaceversion();
                break;
              }
            }
          }
        }
      }
    } catch (final Exception e) {

    }

    return result;
  }

  @Action(enabledProperty = "deleteEnabled")
  public Task<Void, Void> remove() {

    if (dataModelController.getDWHTreeDataModel().getActiveTechpacks().contains(selectedNode.toString())) {
      JOptionPane.showMessageDialog(null, "This techpack (" + selectedNode
          + ") is active in DWHM, please deactivate techpack before removing it");
      return null;
    }

    final String result = isActiveInterfaces(
        selectedNode.toString().substring(0, selectedNode.toString().indexOf(":")), ((Versioning) selectedNode
            .getRockDBObject()));

    if (result.length() > 0) {
      JOptionPane.showMessageDialog(null, "This techpack (" + selectedNode + ") has active interfaces (" + result
          + "), Migrate/de-activate them before removing tech pack.");
      return null;
    }

    final int selectedValue = JOptionPane.showConfirmDialog(null, "Are you sure that you want to remove "
        + selectedNode + " Tech Pack", "Remove Tech Pack?", JOptionPane.YES_NO_OPTION);

    if (selectedValue == JOptionPane.YES_OPTION) {
      final Task<Void, Void> RemoveTask = new RemoveTask(application);
      final BusyIndicator busyIndicator = new BusyIndicator();

      application.getMainFrame().setGlassPane(busyIndicator);
      RemoveTask.setInputBlocker(new BusyIndicatorInputBlocker(RemoveTask, busyIndicator));

      return RemoveTask;
    } else {
      // JOptionPane.showMessageDialog(null, "Tech Pack " + tpname + " not
      // removed");
      return null;
    }

  }

  private class ViewTask extends Task<Void, Void> {

    public ViewTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      techpackUnderModification = selectedNode.toString();

      if ((tpTree != null) && (tpTree.getSelected() != null)) {
        final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          try {
            final GenericActionNode current = tpTree.getSelected();
            if (current instanceof DataTreeNode) {
              final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
              final Versioning v = (Versioning) node.getRockDBObject();

              final JFrame frame = new JFrame();
              frame.setName("ViewTechPackWindow");
              frame.setIconImage(ericssonLogo.getImage());

              dataModelController.getTechPackTreeDataModel().setSelected(v);
              dataModelController.getTechPackTreeDataModel().refresh();
              dataModelController.getEditGeneralTechPackDataModel().refresh(v);
              dataModelController.getExternalStatementDataModel().refresh(v);
              // dataModelController.getUniverseMainDataModel().refresh(v);
              dataModelController.getUniverseTablesDataModel().refresh(v);
              dataModelController.getUniverseJoinDataModel().refresh(v);
              dataModelController.getUniverseConditionDataModel().refresh(v);
              dataModelController.getUniverseClassDataModel().refresh(v);
              dataModelController.getUniverseObjectDataModel().refresh(v);
              dataModelController.getUniverseComputedObjectDataModel().refresh(v);
              dataModelController.getUniverseFormulasDataModel().refresh(v);

              final GeneralTechPackTab editTab = new GeneralTechPackTab(application, dataModelController, v, frame,
                  false);

              frame.add(editTab);
              frame.setSize(800, 600);
              // 20110825 EANGUAN :: Setting the title with Server Name and user Name on View window
              final String userName = LoginTask.getUserName() ;
              final String serverName = LoginTask.getServerName();
              frame.setTitle("Viewing TechPack: " + selectedNode + " | " + serverName + " | " + userName);
              frame.addWindowListener(new TechpackWindowListener());
              frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

              frame.setVisible(true);

            }
          } catch (final Exception e) {
            logger.log(Level.SEVERE, "Error viewing Tech Pack", e);
          }
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
        }
      }
      return null;
    }
  }

  @Action(enabledProperty = "viewEnabled")
  public Task<Void, Void> view() {
    final Task<Void, Void> viewTask = new ViewTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    viewTask.setInputBlocker(new BusyIndicatorInputBlocker(viewTask, busyIndicator));

    return viewTask;
  }

  private class EditTask extends Task<Void, Void> {

    public EditTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      final List<List<Object>> l = TreeState.saveExpansionState(tpTree);
      dataModelController.getTechPackTreeDataModel().refresh();
      TreeState.loadExpansionState(tpTree, l);

      techpackUnderModification = selectedNode.toString();
      if ((tpTree != null) && (tpTree.getSelected() != null)) {
        final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          try {

            final GenericActionNode current = tpTree.getSelected();

            if (current instanceof DataTreeNode) {

              final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
              final Versioning v = (Versioning) node.getRockDBObject();

              final JFrame frame = new JFrame();
              frame.setName("EditTechPackWindow");
              frame.setIconImage(ericssonLogo.getImage());

              dataModelController.getTechPackTreeDataModel().setSelected(v);
              dataModelController.getTechPackTreeDataModel().refresh();
              dataModelController.getEditGeneralTechPackDataModel().refresh(v);
              dataModelController.getExternalStatementDataModel().refresh(v);
              dataModelController.getUniverseTablesDataModel().refresh(v);
              dataModelController.getUniverseJoinDataModel().refresh(v);
              dataModelController.getUniverseClassDataModel().refresh(v);
              dataModelController.getUniverseConditionDataModel().refresh(v);
              dataModelController.getUniverseObjectDataModel().refresh(v);
              dataModelController.getUniverseComputedObjectDataModel().refresh(v);
              dataModelController.getUniverseFormulasDataModel().refresh(v);

              final GeneralTechPackTab editTab = new GeneralTechPackTab(application, dataModelController, v, frame,
                  true);

              frame.add(editTab);
              frame.setSize(800, 600);
              // 20110825 EANGUAN :: Setting the title with Server Name and user Name on Edit window
              final String userName = LoginTask.getUserName() ;
              final String serverName = LoginTask.getServerName();
              frame.setTitle("Editing TechPack: " + selectedNode + " | " + serverName + " | " + userName);
              frame.addWindowListener(new TechpackWindowListener());
              frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
              frame.setVisible(true);

            }
          } catch (final Exception e) {
            logger.log(Level.SEVERE, "Error editing Tech Pack", e);
          }
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
          // EQEV-4298 similar in techpack (START)
          setNewEnabled(false); 
          twist=true;
          // EQEV-4298 similar in techpack (END)
        }
      }
      return null;
    }
  }

  @Action(enabledProperty = "editEnabled", block = BlockingScope.APPLICATION)
  public Task<Void, Void> edit() {

    final Task<Void, Void> editTask = new EditTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    editTask.setInputBlocker(new BusyIndicatorInputBlocker(editTask, busyIndicator));

    return editTask;
  }

  @Action(enabledProperty = "lockEnabled")
  public void lock() {
	  
	  // EQEV-4212
	  String currentUser = null;
	  
	  if ((tpTree != null) && (tpTree.getSelected() != null)) {
      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        try {
          final GenericActionNode current = tpTree.getSelected();
          if (current instanceof DataTreeNode) {
            final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
            final Versioning v = (Versioning) node.getRockDBObject();

            /*final Versioning newv = new Versioning(dataModelController.getRockFactory());
            newv.setVersionid(v.getVersionid());
            final VersioningFactory newvF = new VersioningFactory(dataModelController.getRockFactory(), newv);*/
            
            //EQEV-4212
            String lockedBy = v.getLockedby();
            currentUser =  LoginTask.getUserName();
            
            // To avoid null pointer exception
            if (lockedBy == null ){
            	lockedBy = "";
            }
            
            if ( currentUser.equalsIgnoreCase(lockedBy) || lockedBy == "" ){
            	alreadyLocked = false;
            	v.setLockedby(dataModelController.getUserName());
                v.setLockdate(new Timestamp(System.currentTimeMillis()));
                dataModelController.getTechPackTreeDataModel().modObj(v);
            }else{
            	alreadyLocked = true;
            	JOptionPane.showMessageDialog(this, "Tech pack " + v.getTechpack_name() + ":" + v.getTechpack_version()
                        + " already locked by user " + v.getLockedby() + " at "
                        + v.getLockdate());

                	refresh();
                    return;
            }
            
          }

          dataModelController.getTechPackTreeDataModel().refresh();

        } catch (final Exception e) {
          logger.log(Level.SEVERE, "Error locking Tech Pack", e);
        }
      } finally {
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }

    }
  }

  @Action(enabledProperty = "unlockEnabled")
  public void unlock() {
    if ((tpTree != null) && (tpTree.getSelected() != null)) {
      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        try {
          if (tpTree.getSelected() instanceof DataTreeNode) {
            final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
            final Versioning v = (Versioning) node.getRockDBObject();
            v.setLockedby("");
            v.setLockdate(null);
            dataModelController.getTechPackTreeDataModel().modObj(v);
          }
          dataModelController.getTechPackTreeDataModel().refresh();
        } catch (final Exception e) {
          logger.log(Level.SEVERE, "Error unlocking Tech Pack", e);
        }
      } finally {
        TreeState.loadExpansionState(tpTree, eList);
      }
    }
  }
  
  /**
   * 20110823 EANGUAN :: To get the Tree state
   * @return
   */
  protected List<List<Object>> getTreeState(){
	  List<List<Object>> eList = null ;
	  if ((tpTree != null) && (tpTree.getSelected() != null)) {
	      eList = TreeState.saveExpansionState(tpTree);
	  }
	  return eList ;
  }
  
  /**
   * 20110823 EANGUAN :: To load the Tree state
   * @param eList
   */
  protected void loadTreeState(List<List<Object>> eList){
	  if ((tpTree != null) && (tpTree.getSelected() != null)) {
		  TreeState.loadExpansionState(tpTree, eList);
	  }
  }

//  @Action(enabledProperty = "compareEnabled")
//  public void compare() {
//
//    setCompareEnabled(false);
//
//    final JFrame frame = new JFrame();
//
//    final DeltaView pdv = new DeltaView(application, dataModelController, frame);
//
//    frame.add(pdv);
//    frame.setSize(1000, 600);
//    frame.setTitle("Compare ");
//    frame.addWindowListener(new CompareListener());
//    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    frame.setVisible(true);
//
//  }

  @Action(enabledProperty = "installEnabled")
  public void install() {

    final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
		dataModelController.getTechPackTreeDataModel().refresh();
		TreeState.loadExpansionState(tpTree, eList);

    final GenericActionNode current = tpTree.getSelected();

		if (current instanceof DataTreeNode) {
			if (!isActive) {
        final int selectedValue = JOptionPane.showConfirmDialog(null,
            "TechPack is not Active! Are you sure you want to create Installation package?", "TechPack is not Active",
								JOptionPane.YES_NO_OPTION);

				if (selectedValue == JOptionPane.YES_OPTION) {
					InstallTechPackWindow();
				}
			} else {
				InstallTechPackWindow();
			}
		}
		logger.log(Level.INFO, "Created");

	}
  
  public void InstallTechPackWindow() {
    final JFrame frame = new JFrame();
		frame.setName("InstallTechPackWindow");
		frame.setIconImage(ericssonLogo.getImage());

    final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
    final Versioning v = (Versioning) node.getRockDBObject();

    final CreateInstallationPackageView cip = new CreateInstallationPackageView(application, dataModelController,
        frame, v);
		frame.add(cip);
		frame.setSize(500, 200);
    frame.setTitle("Create installation package from Tech Pack " + selectedNode);
    frame.addWindowListener(new TechpackWindowListener());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Utils.center(frame);
		frame.setVisible(true);

	}

  @Action(enabledProperty = "descriptionDocEnabled")
  public void descriptionDoc() {

    final JFrame frame = new JFrame();
    frame.setName("Create Techpack Description");
    frame.setIconImage(ericssonLogo.getImage());

    final DataTreeNode node = (DataTreeNode) tpTree.getSelected();
    final Versioning v = (Versioning) node.getRockDBObject();

    final CreateTechPackDescriptionView ctdv = new CreateTechPackDescriptionView(application, dataModelController,
        frame);
    ctdv.setCurrent(v);

    frame.add(ctdv);
    frame.setSize(500, 200);
    frame.setTitle("Create Techpack description of " + selectedNode);
    frame.addWindowListener(new TechpackWindowListener());
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);
    frame.pack();
    logger.log(Level.INFO, "Created CreateTechPackDescriptionView window.");
  }

  /**
   * Helper function, returns action by name
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  private class TechpackWindowListener implements WindowListener {

    public void windowActivated(final WindowEvent e) {
      logger.log(Level.FINEST, "windowActivated");
    }

    public void windowClosed(final WindowEvent e) {
      logger.log(Level.FINEST, "windowClosed");

      techpackUnderModification = null;
      setSelectedNode(getSelectedNode());

    }

    public void windowClosing(final WindowEvent e) {
      logger.log(Level.FINEST, "windowClosing");
    }

    public void windowDeactivated(final WindowEvent e) {
      logger.log(Level.FINEST, "windowDeactivated");
    }

    public void windowDeiconified(final WindowEvent e) {
      logger.log(Level.FINEST, "windowDeiconified");
    }

    public void windowIconified(final WindowEvent e) {
      logger.log(Level.FINEST, "windowIconified");
    }

    public void windowOpened(final WindowEvent e) {
      logger.log(Level.FINEST, "windowOpened");
    }
  }

  private class SelectionListener implements TreeSelectionListener {

    public void valueChanged(final TreeSelectionEvent e) {
      final TreePath t = e.getPath();
      final Object pointed = t.getLastPathComponent();
      if (pointed instanceof DefaultMutableTreeNode) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        final Object tmp = node.getUserObject();
        if (tmp instanceof DataTreeNode) {
          setSelectedNode((DataTreeNode) tmp);
        } else {
          setSelectedNode(null);
        }
      } else {
        setSelectedNode(null);
      }
      //EQEV-4298 similar in techpack (START)
      if(twist)
      {
    	  setNewEnabled(false);
      }
      //EQEV-4298 similar in techpack (END)
    }
  }

  public class RefreshDataTask extends Task<Void, Void> {

    private final List<List<Object>> treeState;

    public RefreshDataTask(final List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      dataModelController.getTechPackTreeDataModel().refresh();
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(tpTree, treeState);
    }
  }

  private class NewTechpackListener implements WindowListener {

    public void windowActivated(final WindowEvent e) {
      logger.log(Level.FINEST, "windowActivated");
    }

    public void windowClosed(final WindowEvent e) {
      logger.log(Level.FINEST, "windowClosed");

      techpackUnderModification = null;
      setSelectedNode(getSelectedNode());

      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        dataModelController.rockObjectsModified(dataModelController.getNewTechPackDataModel());
      } catch (final Exception ee) {

      }
      TreeState.loadExpansionState(tpTree, eList);

    }

    public void windowClosing(final WindowEvent e) {
      logger.log(Level.FINEST, "windowClosing");
    }

    public void windowDeactivated(final WindowEvent e) {
      logger.log(Level.FINEST, "windowDeactivated");
    }

    public void windowDeiconified(final WindowEvent e) {
      logger.log(Level.FINEST, "windowDeiconified");
    }

    public void windowIconified(final WindowEvent e) {
      logger.log(Level.FINEST, "windowIconified");
    }

    public void windowOpened(final WindowEvent e) {
      logger.log(Level.FINEST, "windowOpened");
    }
  }

  /*private class CompareListener implements WindowListener {

    public void windowActivated(final WindowEvent e) {
      logger.log(Level.INFO, "windowActivated");
    }

    public void windowClosed(final WindowEvent e) {
      logger.log(Level.INFO, "windowClosed");
      setCompareEnabled(true);
    }

    public void windowClosing(final WindowEvent e) {
      logger.log(Level.INFO, "windowClosing");
    }

    public void windowDeactivated(final WindowEvent e) {
      logger.log(Level.INFO, "windowDeactivated");
    }

    public void windowDeiconified(final WindowEvent e) {
      logger.log(Level.INFO, "windowDeiconified");
    }

    public void windowIconified(final WindowEvent e) {
      logger.log(Level.INFO, "windowIconified");
    }

    public void windowOpened(final WindowEvent e) {
      logger.log(Level.INFO, "windowOpened");
    }
  }*/

}
