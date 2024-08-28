package com.ericsson.eniq.techpacksdk;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;
//import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
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

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Countingmanagement;
import com.distocraft.dc5000.repository.dwhrep.CountingmanagementFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhcolumn;
import com.distocraft.dc5000.repository.dwhrep.DwhcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;
import com.distocraft.dc5000.repository.dwhrep.DwhpartitionFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.DwhtechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatementstatus;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementstatusFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementvector;
import com.distocraft.dc5000.repository.dwhrep.MeasurementvectorFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DWHTreeDataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.repreplacement.ActivationCacheTPIDE;
import com.ericsson.eniq.techpacksdk.repreplacement.PartitionActionTPIDE;
import com.ericsson.eniq.techpacksdk.repreplacement.PhysicalTableCacheTPIDE;
import com.ericsson.eniq.techpacksdk.repreplacement.StorageTimeActionTPIDE;
import com.ericsson.eniq.techpacksdk.repreplacement.VersionUpdateActionTPIDE;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;
//import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;

@SuppressWarnings("serial")
public class ManageDWHTab extends JPanel implements PropertyChangeListener {

  private static final Logger LOGGER = Logger.getLogger(ManageDWHTab.class.getName());

  private SingleFrameApplication application;

  protected DataTreeNode selectedNode;

  private DWHTreeDataModel dwhTreeDataModel;

  private GenericActionTree tpTree;

  private boolean activateEnabled = false;

  private boolean activateBHEnabled = false;

  private boolean deActivateEnabled = false;

  private boolean upgradeEnabled = false;

  private DataModelController dataModelController;

  private static final String DELTA = "_DELTA";

  private static final String DISTINCT_DATES = "_DISTINCT_DATES";

  private static final String COUNT_TABLELEVEL = "COUNT";

  private static final String RAW_TABLELEVEL = "RAW";

  /**
   * Used for Unit tests
   *
   * @param dataModelController
   */
  protected ManageDWHTab(final DataModelController dataModelController) {
    this.dwhTreeDataModel = dataModelController.getDWHTreeDataModel();
    this.dataModelController = dataModelController;
  }

  public ManageDWHTab(final SingleFrameApplication application, final DataModelController dataModelController) {
    super(new GridBagLayout());
    this.application = application;
    this.dwhTreeDataModel = dataModelController.getDWHTreeDataModel();
    this.dataModelController = dataModelController;

    application.getMainFrame().addPropertyChangeListener(this);
    // ************** TPTree panel **********************

    tpTree = new GenericActionTree(dwhTreeDataModel);
    tpTree.addRootAction(getAction("refresh"));
    tpTree.addAction(getAction("upgrade"));
    tpTree.addAction(getAction("deactivate"));
    tpTree.addAction(getAction("activate"));
    tpTree.addAction(getAction("activateBH"));

    tpTree.setCellRenderer(new DWHTreeRenderer());
    tpTree.addTreeSelectionListener(new SelectionListener());
    tpTree.setToolTipText("");
    tpTree.setName("dwhTree");
    final JScrollPane tpScrollPane = new JScrollPane(tpTree);

    // ************** buttons **********************

    // JButton refresh = new JButton("Refresh");
    // refresh.setActionCommand("refresh");
    // refresh.setAction(getAction("refresh"));
    // refresh.setToolTipText("Refresh");
    // refresh.setName("DWHRefresh");

    final JButton upgrade = new JButton("Upgrade");
    upgrade.setActionCommand("upgrade");
    upgrade.setAction(getAction("upgrade"));
    upgrade.setToolTipText("Upgrade");
    upgrade.setName("DWHUpgrade");

    final JButton deactivate = new JButton("DeActivate");
    deactivate.setActionCommand("deactivate");
    deactivate.setAction(getAction("deactivate"));
    deactivate.setToolTipText("DeActivate");
    deactivate.setName("DWHDeactivate");

    final JButton activate = new JButton("Activate");
    activate.setActionCommand("activate");
    activate.setAction(getAction("activate"));
    activate.setToolTipText("Activate");
    activate.setName("DWHActivate");

    final JButton activateBH = new JButton("Activate Busy Hour");
    activateBH.setActionCommand("activateBH");
    activateBH.setAction(getAction("activateBH"));
    activateBH.setToolTipText("Activate Busy Hour");
    activateBH.setName("DWHBHActivate");
//    activateBH.addActionListener(l)

    final JButton quit = new JButton("Quit");
    quit.setAction(getAction("quit"));
    quit.setName("DWHQuit");

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    // buttonPanel.add(refresh);
    buttonPanel.add(upgrade);
    buttonPanel.add(activateBH);
    buttonPanel.add(activate);
    buttonPanel.add(deactivate);
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

  }

  private class RefereshTask extends Task<Void, Void> {

    public RefereshTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      if ((tpTree != null)) {
    	  final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
        try {
          dataModelController.getDWHTreeDataModel().refresh();
        } finally {
          TreeState.loadExpansionState(tpTree, eList);
        }
      }

      return null;
    }
  }

  @Action
  public Task<Void, Void> refresh() {
    final Task<Void, Void> RefereshTask = new RefereshTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    RefereshTask.setInputBlocker(new BusyIndicatorInputBlocker(RefereshTask, busyIndicator));

    return RefereshTask;
  }

  private class UpgradeTask extends Task<Void, Void> {

    public UpgradeTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      LOGGER.log(Level.INFO, "upgraded");

      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {

        /*
         * dataModelController.getRockFactory().getConnection().setAutoCommit(false
         * );
         * dataModelController.getDwhRockFactory().getConnection().setAutoCommit
         * (false);
         * dataModelController.getEtlRockFactory().getConnection().setAutoCommit
         * (false);
         */

    	  final String tpName = ((Versioning) selectedNode.getRockDBObject()).getTechpack_name();
    	  final String newVersion = ((Versioning) selectedNode.getRockDBObject()).getVersionid();
        String oldVersion = "";

        final Tpactivation tpa = new Tpactivation(dataModelController.getRockFactory());
        tpa.setTechpack_name(tpName);
        final TpactivationFactory tpaF = new TpactivationFactory(dataModelController.getRockFactory(), tpa);

        if (tpaF != null && tpaF.get() != null && !tpaF.get().isEmpty()) {
          oldVersion = ((Tpactivation) tpaF.getElementAt(0)).getVersionid();
          oldVersion.replace("((", "");
          oldVersion.replace("))", "");
        }

        // version = version.substring(version.indexOf(":")+1);
        newVersion.replace("((", "");
        newVersion.replace("))", "");

        final int selectedValue = JOptionPane
            .showConfirmDialog(
                application.getMainFrame(),
                "Are you sure that you want to upgrade from "
                    + oldVersion
                    + " to "
                    + newVersion
                    + " Tech Pack in DWHM.\n Engine profile will be set to NoLoads before UPGRADE is done, this can take a while.",
                "Upgrade to Tech Pack in DWHM?", JOptionPane.YES_NO_OPTION);

        if (selectedValue == JOptionPane.YES_OPTION) {

          dataModelController.setAndWaitActiveExecutionProfile("NoLoads");

          final String versionId = ((Versioning) selectedNode.getRockDBObject()).getVersionid();
          final String techPackType = ((Versioning) selectedNode.getRockDBObject()).getTechpack_type();
          final String techpack = ((Versioning) selectedNode.getRockDBObject()).getTechpack_name();

          // update existing activation
          createTPActivation(tpName, versionId, techPackType);

          final VersionUpdateActionTPIDE v = new VersionUpdateActionTPIDE(dataModelController.getRockFactory(), dataModelController
              .getDwhRockFactory(), dataModelController.getEtlRockFactory(), tpName, LOGGER);

          v.execute();

          // Init Activation Cache
          ActivationCacheTPIDE.initialize(dataModelController.getEtlRockFactory(), dataModelController.getRockFactory()
              .getDbURL(), dataModelController.getRockFactory().getUserName(), dataModelController.getRockFactory()
              .getPassword());

          // Init Loader PhysicalTable Cache
          PhysicalTableCacheTPIDE.initialize(dataModelController.getEtlRockFactory(), dataModelController.getRockFactory()
              .getDbURL(), dataModelController.getRockFactory().getUserName(), dataModelController.getRockFactory()
              .getPassword(), dataModelController.getDwhRockFactory().getDbURL(), dataModelController
              .getDwhRockFactory().getUserName(), dataModelController.getDwhRockFactory().getPassword());

          // Clear Counter Management Cache in Engine, for all related storageIds
          clearCountingManagementCache(techpack);

          // Initialise the Volume Partition Loader Helper
          // VolumePartitionSQLHelper.initialize(dataModelController.getEtlRockFactory(),
          // dataModelController
          // .getRockFactory().getDbURL(),
          // dataModelController.getRockFactory().getUserName(),
          // dataModelController
          // .getRockFactory().getPassword(),
          // dataModelController.getDwhRockFactory().getDbURL(),
          // dataModelController
          // .getDwhRockFactory().getUserName(),
          // dataModelController.getDwhRockFactory().getPassword());

          // check storagetime
          new StorageTimeActionTPIDE(dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(),
              dataModelController.getDwhRockFactory(), dataModelController.getDbaDwhRockFactory(), techpack, LOGGER,
              true);

          // create partitions
          new PartitionActionTPIDE(dataModelController.getRockFactory(), dataModelController.getDwhRockFactory(), techpack,
              LOGGER);

          checkforReacivateViews(versionId); // check if any newly added/modified BusyHour Placeholder

          // activate (enable) new
          final Meta_collection_sets nmcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
          nmcs.setCollection_set_name(tpName);
          nmcs.setVersion_number(newVersion.substring(newVersion.indexOf(":") + 1));
          final Meta_collection_setsFactory nmcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
              nmcs);

          if (nmcsF != null && nmcsF.get() != null && nmcsF.get().size() > 0) {

        	  final Meta_collection_sets nmetacs = (Meta_collection_sets) nmcsF.getElementAt(0);

        	  final Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
            mc.setCollection_set_id(nmetacs.getCollection_set_id());
            mc.setVersion_number(newVersion.substring(newVersion.indexOf(":") + 1));
            final Meta_collectionsFactory mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);

            final Iterator<Meta_collections> mcFI = mcF.get().iterator();
            while (mcFI.hasNext()) {
            	final Meta_collections metac = (Meta_collections) mcFI.next();
              metac.setEnabled_flag("Y");
              metac.saveDB();
            }

            nmetacs.setEnabled_flag("Y");
            nmetacs.saveDB();
          }

          // deactivate old
          if(oldVersion != null && oldVersion.length() != 0){
        	  final Meta_collection_sets omcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
              omcs.setCollection_set_name(tpName);
              omcs.setVersion_number(oldVersion.substring(newVersion.indexOf(":") + 1));
              final Meta_collection_setsFactory omcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
                  omcs);

              //20120202 eanguan :: to check whether there are any sets or not
              if (omcsF != null && omcsF.size() > 0) {

            	  final Meta_collection_sets ometacs = (Meta_collection_sets) omcsF.getElementAt(0);

            	  final Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
                mc.setCollection_set_id(ometacs.getCollection_set_id());
                mc.setVersion_number(oldVersion.substring(newVersion.indexOf(":") + 1));
                final Meta_collectionsFactory mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);

                final Iterator<Meta_collections> mcFI = mcF.get().iterator();
                while (mcFI.hasNext()) {
                	final Meta_collections metac = (Meta_collections) mcFI.next();
                  metac.setEnabled_flag("N");
                  metac.saveDB();
                }

                ometacs.setEnabled_flag("N");
                ometacs.saveDB();
              }else{
            	  LOGGER.log(Level.WARNING, " No META_COLLECTION_SETS present in this TP. ");
              }
          }else{
        	  LOGGER.log(Level.INFO, " No Deactivation needed as there was no old active Techpack. ");
          }


          /*
           * dataModelController.getRockFactory().getConnection().commit();
           * dataModelController.getDwhRockFactory().getConnection().commit();
           * dataModelController.getEtlRockFactory().getConnection().commit();
           */
          dwhTreeDataModel.refresh();

        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
        /*
         * dataModelController.getRockFactory().getConnection().setAutoCommit(true
         * );
         * dataModelController.getDwhRockFactory().getConnection().setAutoCommit
         * (true);
         * dataModelController.getEtlRockFactory().getConnection().setAutoCommit
         * (true);
         */
      }

      dataModelController.setAndWaitActiveExecutionProfile("Normal");

      return null;
    }
  }

  /**
   * Clear any entries for this techpack from the Counting Management Cache (for counter aggregations).
   *
   * @param techpackName
   * @throws SQLException
   * @throws RockException
   * @throws RemoteException
   */
  private void clearCountingManagementCache(final String techpackName) throws SQLException, RockException, RemoteException {
    // TPs from the DB, get storage ids, clear cache remotely.
	  final RockFactory dwhRepRock = dataModelController.getRockFactory();

	  final Dwhtype dwhTypeCondition = new Dwhtype(dwhRepRock);
    dwhTypeCondition.setTechpack_name(techpackName);
    final DwhtypeFactory dwhTypeFactory = new DwhtypeFactory(dwhRepRock, dwhTypeCondition);
    final Vector<Dwhtype> types = dwhTypeFactory.get();

    for (Dwhtype type : types) {
      if (isCounterStorage(type.getStorageid())) {
        dataModelController.getEngine().clearCountingManagementCache(type.getStorageid());
      }
    }
  }


  @Action(enabledProperty = "upgradeEnabled")
  public Task<Void, Void> upgrade() {

    final Task<Void, Void> UpgradeTask = new UpgradeTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    UpgradeTask.setInputBlocker(new BusyIndicatorInputBlocker(UpgradeTask, busyIndicator));

    return UpgradeTask;
  }

  private class DeactivateTask extends Task<Void, Void> {

    public DeactivateTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      LOGGER.log(Level.INFO, "deactivated");
      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);

      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        dataModelController.getDwhRockFactory().getConnection().setAutoCommit(false);

        // String tpName = ((Versioning)
        // selectedNode.getRockDBObject()).getTechpack_name();
        final String version = ((Versioning) selectedNode.getRockDBObject()).getVersionid();
        version.replace("((", "");
        version.replace("))", "");

        final int selectedValue = JOptionPane
            .showConfirmDialog(
                application.getMainFrame(),
                "Are you sure that you want to de-activate "
                    + version
                    + " Tech Pack in DWHM.\n Engine profile will be set to NoLoads before DEACTIVATE is done, this can take a while.",
                "De-activate Tech Pack in DWHM?", JOptionPane.YES_NO_OPTION);

        if (selectedValue == JOptionPane.YES_OPTION) {

          dataModelController.setAndWaitActiveExecutionProfile("NoLoads");

          removeDWH();

          dataModelController.getRockFactory().getConnection().commit();
          dataModelController.getDwhRockFactory().getConnection().commit();

          /*
           * Interfacedependency idp = new
           * Interfacedependency(dataModelController.getRockFactory());
           * idp.setTechpackname(ver.getTechpack_name());
           * idp.setTechpackversion(ver.getTechpack_version());
           * InterfacedependencyFactory idpF = new
           * InterfacedependencyFactory(dataModelController
           * .getRockFactory(),idp); Iterator<Interfacedependency> idpFI =
           * idpF.get().iterator(); while(idpFI.hasNext()){ Interfacedependency
           * i = (Interfacedependency)idpFI.next(); Datainterface di = new
           * Datainterface(dataModelController.getRockFactory());
           * di.setInterfacename(i.getInterfacename());
           * di.setInterfaceversion(i.getInterfaceversion());
           * DatainterfaceFactory diF = new
           * DatainterfaceFactory(dataModelController.getRockFactory(),di); if
           * (diF!=null){
           * dataModelController.getInterfaceTreeDataModel().deactivateInterface
           * (diF.getElementAt(0)); } }
           */

          dwhTreeDataModel.refresh();
        }

      } catch (Exception e) {
        try {
          dataModelController.getRockFactory().getConnection().rollback();
          dataModelController.getDwhRockFactory().getConnection().rollback();
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      } finally {

        try {
          dataModelController.getRockFactory().getConnection().setAutoCommit(true);
          dataModelController.getDwhRockFactory().getConnection().setAutoCommit(true);
        } catch (Exception ex) {
          ExceptionHandler.instance().handle(ex);
          ex.printStackTrace();
        }
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }

      dataModelController.setAndWaitActiveExecutionProfile("Normal");

      return null;
    }
  }

  @Action(enabledProperty = "deActivateEnabled")
  public Task<Void, Void> deactivate() {
    final Task<Void, Void> DeactivateTask = new DeactivateTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    DeactivateTask.setInputBlocker(new BusyIndicatorInputBlocker(DeactivateTask, busyIndicator));

    return DeactivateTask;
  }

  protected void removeDWH() throws Exception {

    final String tpName = ((Versioning) this.selectedNode.getRockDBObject()).getTechpack_name();
    final String versionid = ((Versioning) this.selectedNode.getRockDBObject()).getVersionid();

    final Tpactivation tpa = new Tpactivation(dataModelController.getRockFactory());
    tpa.setTechpack_name(tpName);

    final TpactivationFactory tpaF = new TpactivationFactory(dataModelController.getRockFactory(), tpa);
      for (Tpactivation tpatmp : tpaF.get()) {
          removeTypeActivations(tpatmp);
          final Dwhtechpacks dwhtp = new Dwhtechpacks(dataModelController.getRockFactory());
      dwhtp.setVersionid(tpatmp.getVersionid());
          final DwhtechpacksFactory dwhtpF = new DwhtechpacksFactory(dataModelController.getRockFactory(), dwhtp);
          for (Dwhtechpacks dwhtptmp : dwhtpF.get()) {
              removeExternalStatementStatuss(dwhtptmp);
              removePartitionsAndViewDefs(dwhtptmp);
        LOGGER.log(Level.INFO, "Removing DWHTechpacks " + dwhtptmp.getTechpack_name());
        dwhtptmp.deleteDB();
      }
      LOGGER.log(Level.INFO, "Removing TPactivations " + tpatmp.getTechpack_name());
      // delete TPactivations
      tpatmp.deleteDB();

      // DROP BH VIEWS
      // Create the view dropping statements for the busy hour views (excluding
      // custom busy hours).
          dropBhViews(versionid, dataModelController.getRockFactory(), dataModelController.getDwhRockFactory());

          // Set the enabled flad to "N" for the meta collection sets and related
          // meta collections.
          disableCollectionAndSets(tpatmp);
      }
  }

    private void disableCollectionAndSets(final Tpactivation tpatmp) throws SQLException, RockException {
        final Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
        mcs.setCollection_set_name(tpatmp.getTechpack_name());
        mcs.setVersion_number(tpatmp.getVersionid().substring(tpatmp.getVersionid().indexOf(":") + 1));
        final Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(), mcs);
        if (mcsF.get() != null && mcsF.get().size() > 0) {
            final Meta_collection_sets metacs = mcsF.getElementAt(0);
            final Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
            mc.setCollection_set_id(metacs.getCollection_set_id());
            mc.setVersion_number(tpatmp.getVersionid().substring(tpatmp.getVersionid().indexOf(":") + 1));
            final Meta_collectionsFactory mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);
            @SuppressWarnings({"unchecked"}) final List<Meta_collections> metaColls = mcF.get();
            for (Meta_collections metac : metaColls) {
                metac.setEnabled_flag("N");
                metac.saveDB();
            }
            metacs.setEnabled_flag("N");
            metacs.saveDB();
        }
    }

    private void dropBhViews(final String versionid, final RockFactory dwhrep, final RockFactory dwhdb) {
        try {
            final List<String> dropClauses = generateBhPlaceholderDropClauses(versionid, dwhrep);
            // Execute the view BH view drop clauses.
            //
            // The view clauses are executed one-by-one to avoid not executing all
            // the rest of the statements when an error occurs (e.g. due to non
            // existing view).
            //
            // NOTE: There might be a performance issue when not executing the
            // clauses as one statement. This has not been measured.
            try {
                if (!dropClauses.isEmpty()) {
                    LOGGER.log(Level.INFO, "Dropping BH views.");
                    LOGGER.log(Level.FINEST, "Dropping BH views with clauses:\n" + dropClauses);
                    // Create the database connection and the SQL statement.
                    final Statement s = dwhdb.getConnection().createStatement();
                    for (String stmt : dropClauses) {
                        try {
                            // Execute the statement to drop one view.
                            s.execute(stmt);
                        } catch (Exception e) {
                            LOGGER.log(Level.FINEST,"Error while dropping BH view: " + stmt + ". Exception: " + e);
            }
          }
          // Close the statement and the database connection
          s.close();
        } else {
          LOGGER.log(Level.FINE, "No BH views to be dropped.");
        }
      } catch (Exception e) {
        LOGGER.warning("Error while dropping BH views: " + e + " : " + dropClauses);
      }
    } catch (Exception e) {
      LOGGER.warning("Error dropping BH views. " + e);
    }
  }

  private List<String> generateBhPlaceholderDropClauses(final String versionid, final RockFactory reprock)
      throws SQLException, RockException {
    final List<String> viewClauses = new ArrayList<String>();
    final Measurementtype mt_cond = new Measurementtype(reprock);
        mt_cond.setVersionid(versionid);
        final MeasurementtypeFactory mt_condF = new MeasurementtypeFactory(reprock, mt_cond);
        for (Measurementtype mt : mt_condF.get()) {
            // OBJBH
            final Measurementobjbhsupport mobhs = new Measurementobjbhsupport(reprock);
          mobhs.setTypeid(mt.getTypeid());
            final MeasurementobjbhsupportFactory mobhsF = new MeasurementobjbhsupportFactory(reprock, mobhs);
            if (mobhsF.get() != null && mobhsF.get().size() > 0) {
                for (Measurementobjbhsupport measObjbhs : mobhsF.get()) {
                    // OBJBH
                    final String bhlevel = mt.getTypename();
                    // Busyhour
                    final Busyhour bh_cond = new Busyhour(reprock);
              bh_cond.setVersionid(mt.getVersionid());
              bh_cond.setBhlevel(bhlevel);
              bh_cond.setBhobject(measObjbhs.getObjbhsupport());
              bh_cond.setBhelement(0); // 0 = obj
                    final BusyhourFactory vc_condF = new BusyhourFactory(reprock, bh_cond);
                    for (Busyhour bh : vc_condF.get()) {
                // OBJBH view deletion starts
                    	final String name = mt.getTypename() + "_RANKBH_" + measObjbhs.getObjbhsupport() + Constants.UNDERSCORE
                + bh.getBhtype();
                LOGGER.log(Level.FINEST, "Adding OBJBH view: " + name + " to the drop list.");
                        viewClauses.add("DROP VIEW " + name);
                    }
              }
            }
            // ELEMBH
            if (mt.getElementbhsupport() != null && mt.getRankingtable() != null
                    && mt.getElementbhsupport() > 0 && mt.getRankingtable() > 0) {
                // Busyhour
                final Busyhour bh_cond = new Busyhour(reprock);
            bh_cond.setVersionid(mt.getVersionid());
            bh_cond.setBhelement(1); // 1 = elem
                final BusyhourFactory vc_condF = new BusyhourFactory(reprock, bh_cond);
                for (Busyhour bh : vc_condF.get()) {
              // ELEMBH view deletion starts
                	final String name = mt.getVendorid() + "_ELEMBH_RANKBH_" + bh.getBhobject() + Constants.UNDERSCORE + bh.getBhtype();
              LOGGER.log(Level.FINEST, "Adding ELEMBH view: " + name + " to the drop list.");
                    viewClauses.add("DROP VIEW " + name);
            }
          }
        }
        // Create the view dropping statements for the custom busy hour views,
        // i.e. busy hours pointing to ranking tables in other techpacks.
        // Iterate through all busy hour entries for this techpack.
        final Busyhour obj_bh = new Busyhour(reprock);
        obj_bh.setVersionid(versionid);
        final BusyhourFactory vc_condF = new BusyhourFactory(reprock, obj_bh);
        for (Busyhour bh : vc_condF.get()) {
          // Handle only the busy hours with target versionId being different
          // than the current versionId, since here only custom BH entries are
          // taken care of.
          if (!bh.getVersionid().equals(bh.getTargetversionid())) {
            // Get view name based on if the busy hour is OBJBH or ELEMBH.
            String name = "";
            if (bh.getBhelement().equals(0)) {
              // OBJBH
          name = bh.getBhlevel() + "_RANKBH_" + bh.getBhobject() + Constants.UNDERSCORE + bh.getBhtype();
            } else {
              // ELEMBH
                    final Versioning v = new Versioning(reprock, true);
              v.setVersionid(bh.getTargetversionid());
              final VersioningFactory vF = new VersioningFactory(reprock, v, true);
          final Versioning targetTP = vF.get().elementAt(0);
          name = targetTP.getTechpack_name() + "_ELEMBH_RANKBH_" + bh.getBhobject() + Constants.UNDERSCORE
              + bh.getBhtype();
        }
            // Add the drop statement to the list.
            LOGGER.log(Level.FINEST, "Adding custom BH view: " + name + " to the drop list.");
                viewClauses.add("DROP VIEW " + name);
          }
        }
    return viewClauses;
  }

  private void removePartitionsAndViewDefs(final Dwhtechpacks dwhtptmp) throws SQLException, RockException,
      RemoteException {
    final Dwhtype dwhtype = new Dwhtype(dataModelController.getRockFactory());
        dwhtype.setTechpack_name(dwhtptmp.getTechpack_name());
        final DwhtypeFactory dwhtypeF = new DwhtypeFactory(dataModelController.getRockFactory(), dwhtype);
        for (Dwhtype dwhtypetmp : dwhtypeF.get()) {
            boolean containsView = false;

            // DWHColumn
            final Dwhcolumn dwhcolumn = new Dwhcolumn(dataModelController.getRockFactory());
            dwhcolumn.setStorageid(dwhtypetmp.getStorageid());
            final DwhcolumnFactory dwhcolumnF = new DwhcolumnFactory(dataModelController.getRockFactory(), dwhcolumn);
            for (Dwhcolumn dwhcolumntmp : dwhcolumnF.get()) {
                if (dwhcolumntmp.getIncludesql() == 1) {
                    containsView = true;
                }
                LOGGER.log(Level.INFO, "Removing Dwhcolumn " + dwhcolumntmp.getDataname());
                // Delete DWHColumns
                dwhcolumntmp.deleteDB();
            }
            // DWHPartition
            final Dwhpartition dwhpar = new Dwhpartition(dataModelController.getRockFactory());
            dwhpar.setStorageid(dwhtypetmp.getStorageid());
            final DwhpartitionFactory dwhparF = new DwhpartitionFactory(dataModelController.getRockFactory(), dwhpar);
            for (Dwhpartition dwhpartmp : dwhparF.get()) {
                try {
                    LOGGER.log(Level.INFO, "Dropping table " + dwhpartmp.getTablename());
                    // remove actual tables from dwhRep
                    dataModelController.getDwhRockFactory().getConnection().createStatement().execute(
                            "Drop table " + dwhpartmp.getTablename());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Could not drop table " + dwhpartmp.getTablename());
                }
                LOGGER.log(Level.INFO, "Removing DWHPartitions " + dwhpartmp.getTablename());
                // delete DWHPartitions
                dwhpartmp.deleteDB();

            }
            // remove views....
            if (dwhtypetmp.getViewtemplate() != null) {
                if (!dwhtypetmp.getViewtemplate().isEmpty()) {
                    try {
		                // remove views from dwh
		                if (COUNT_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel())) {
		                	// when count table level remove delta view
		                	final String viewName = dwhtypetmp.getTypename() + DELTA;
		                	LOGGER.log(Level.INFO, "Dropping view " + viewName);
		                	try {
			                    dataModelController.getDwhRockFactory().getConnection().createStatement().execute(
			                    "Drop view " + viewName);
		                	} catch (Exception e) {
		                        LOGGER.log(Level.WARNING, "Could not drop view " + viewName);
		                    }
		                }
		                //20100816, eeoidiv, HL88981 Tech Pack IDE: Tries to remove delta/distinct views even from non-avc TPs
		                // See if there is a count level table in dwhtype
		                if( (doesHaveCountTableLevel(dwhtypetmp.getTypename(), dataModelController.getRockFactory()) && RAW_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel()))
		                		|| COUNT_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel()) ) {
		                	// when count or raw table level remove distinct dates view
		                	final String viewName = dwhtypetmp.getBasetablename() + DISTINCT_DATES;
		                	LOGGER.log(Level.INFO, "Dropping view " + viewName);
		                	try {
			                    dataModelController.getDwhRockFactory().getConnection().createStatement().execute(
			                    "Drop view " + viewName);
		                	} catch (Exception e) {
		                        LOGGER.log(Level.WARNING, "Could not drop view " + viewName);
		                    }
		                }
                        LOGGER.log(Level.INFO, "Dropping view " + dwhtypetmp.getBasetablename());
                        // remove base view
                        dataModelController.getDwhRockFactory().getConnection().createStatement().execute(
                                "Drop view " + dwhtypetmp.getBasetablename());
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Could not drop view " + dwhtypetmp.getBasetablename());
                    }
                }
            }
            // remove public views....
            if (dwhtypetmp.getPublicviewtemplate() != null) {
                if (!dwhtypetmp.getPublicviewtemplate().isEmpty()) {
                    if (containsView) {

                        try {
                            LOGGER.log(Level.INFO, "Dropping public view " + dwhtypetmp.getBasetablename());
                            // remove view from dwh
                            dataModelController.getDbaDwhRockFactory().getConnection().createStatement().execute(
                                    "Drop view dcpublic." + dwhtypetmp.getBasetablename());
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Could not drop public view " + dwhtypetmp.getBasetablename());
                        }
                    }
                }
            }
            if (dwhtypetmp.getTablelevel().equals("DAYBH")) {
                final String calcTableName = dwhtypetmp.getBasetablename() + "_CALC";
                try {
                    LOGGER.log(Level.INFO, "Dropping cal table " + calcTableName);
                    final String sql = "Drop table " + calcTableName;
                    final Statement s = dataModelController.getDwhRockFactory().getConnection().createStatement();
                    s.execute(sql);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Could not drop calc table " + calcTableName);
                }
            }

            // If its an ENIQ Events techpack, then the overall views need to be
            // dropped
            if (isEniqEventsTechPack(dwhtypetmp)) {
              dropOverallViewForEvents(dwhtypetmp);
            }

            // Remove Counter Management Cache State associated with the partitions, if applicable.
            if (isCounterStorage(dwhtypetmp.getStorageid())) {
              // Clear Counter Management data from the DB.
              deleteCounterStorageDetails(dwhtypetmp.getStorageid());
              // Clear Counter Management Cache in Engine
              dataModelController.getEngine().clearCountingManagementCache(dwhtypetmp.getStorageid());
            }

            LOGGER.log(Level.INFO, "Removing DWHTypes " + dwhtypetmp.getBasetablename());
            // delete DWHTypes
            dwhtypetmp.deleteDB();
        }
    }

    protected boolean doesHaveCountTableLevel(final String typeName, final RockFactory dwhrepRock) {
    	boolean returnValue = false;

		// set DWHType condition to check does the type have COUNT table level
    	final Dwhtype dwhTypeCond = new Dwhtype(dwhrepRock);
		dwhTypeCond.setTypename(typeName);
		dwhTypeCond.setTablelevel(COUNT_TABLELEVEL);

		DwhtypeFactory dwhTypeResult = null;

		try {
			// get the result by DWHType condition
			dwhTypeResult = new DwhtypeFactory(dwhrepRock, dwhTypeCond);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error occured when querying DWHType with typename and COUNT tablelevel. " + e.toString());
		}

		if (null == dwhTypeResult) {
			// when error happened querying the DWHType information
			returnValue = false;
		} else if (dwhTypeResult.get().isEmpty()) {
			// when measurement type does not have COUNT table level
			returnValue = false;
		} else {
			// when measurement type does have COUNT table level
			returnValue = true;
		}

		return returnValue;
	} // doesHaveCountTableLevel

    /**
     * Test if Countingmanagement info is maintained for this storageId type.
     *
     * @param storageId
     * @return
     */
    private boolean isCounterStorage(final String storageId) {
      return (storageId.endsWith(Constants.TYPESEPARATOR + Constants.RAWLEVEL));
    }

    /**
     * Delete all Countingmanagement info from the DB for this storageId.
     *
     * @param storageId
     * @throws SQLException
     * @throws RockException
     */
    private void deleteCounterStorageDetails(final String storageId) throws SQLException, RockException {
    	final RockFactory dwhRepRock = dataModelController.getRockFactory();

    	final Countingmanagement countingManagementCondition = new Countingmanagement(dwhRepRock);
      countingManagementCondition.setStorageid(storageId);
      final CountingmanagementFactory countingManagementFactory = new CountingmanagementFactory(dwhRepRock,
          countingManagementCondition);
      final Vector<Countingmanagement> countingManagementList = countingManagementFactory.get();

      // Remove CountingManagment info for each partition from the DB.
      for (Countingmanagement element : countingManagementList) {
        element.deleteDB();
      }
    }

    private boolean isEniqEventsTechPack(final Dwhtype dwhtypetmp) {
      return dwhtypetmp.getTechpack_name().startsWith(Constants.EVENT_E);
    }

    protected void dropOverallViewForEvents(final Dwhtype dwhtypetmp) {
      String overallViewName = null;
      final String baseTableName = dwhtypetmp.getBasetablename();

      if (baseTableName.contains(Constants.SUC)) {
        overallViewName = baseTableName.replace(Constants.SUC, Constants.UNDERSCORE);
      } else if (baseTableName.contains(Constants.ERR)) {
        overallViewName = baseTableName.replace(Constants.ERR, Constants.UNDERSCORE);
      }

      // Drop the time range views for user dc
      if (baseTableName.contains(Constants.RAWLEVEL)) {
        attemptToDropOverallView(overallViewName + "_TIMERANGE", dataModelController.getDwhRockFactory(), "dc");
        attemptToDropOverallView(baseTableName + "_TIMERANGE", dataModelController.getDwhRockFactory(), "dc");
      }

      if (overallViewName != null) {
        // Drop the overall view for user dc
        attemptToDropOverallView(overallViewName, dataModelController.getDwhRockFactory(), "dc");
        // Drop the overall view for user dcpublic
        attemptToDropOverallView(overallViewName, dataModelController.getDbaDwhRockFactory(), "dcpublic");
      }
    }

    /**
     * Attempts to drop overall view. May fail to drop view if somebody is accessing the view at the same time. When
     * re-activating techpack, another attempt is made to drop the view if the view was not successfully dropped here (see
     * the velocity templates on view creation for more info on this)
     *
     * @param overallViewName
     * @param dbConnection
     * @param user
     */
    private void attemptToDropOverallView(final String overallViewName, final RockFactory dbConnection, final String user) {
      try {
        LOGGER.log(Level.INFO, "Dropping view " + overallViewName + " for user " + user);
        // remove view from dwhRep
        dbConnection.getConnection().createStatement().execute("Drop view " + user + "." + overallViewName);
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Could not drop overall view " + overallViewName + " for user " + user);
      }
    }


	private void removeExternalStatementStatuss(final Dwhtechpacks dwhtptmp) throws SQLException, RockException {
        // Externalstatements
        final Externalstatementstatus ext = new Externalstatementstatus(dataModelController.getRockFactory());
        ext.setTechpack_name(dwhtptmp.getTechpack_name());
        final ExternalstatementstatusFactory extF = new ExternalstatementstatusFactory(
                dataModelController.getRockFactory(), ext);
        for (Externalstatementstatus exttmp : extF.get()) {
            LOGGER.log(Level.INFO, "Removing ExternalStatementStatues " + exttmp.getStatementname());
            // delete ExternalStatementStatues
            exttmp.deleteDB();
        }
    }

    private void removeTypeActivations(final Tpactivation tpatmp) throws SQLException, RockException {
        final Typeactivation typeAct = new Typeactivation(dataModelController.getRockFactory());
        typeAct.setTechpack_name(tpatmp.getTechpack_name());
        final TypeactivationFactory typeActF = new TypeactivationFactory(dataModelController.getRockFactory(), typeAct);
        for (Typeactivation typeActtmp : typeActF.get()) {
            // delete Typeactivations
            typeActtmp.deleteDB();
        }
    }

    private Tpactivation getPredecessorTPActivation(final String tpname) {
    Tpactivation targetTPActivation = null;
    try {
    	final Tpactivation tpa = new Tpactivation(dataModelController.getRockFactory());
        tpa.setTechpack_name(tpname);

        final TpactivationFactory tpaF = new TpactivationFactory(dataModelController.getRockFactory(), tpa);

        if (tpaF!=null && tpaF.size()>0){
          targetTPActivation = tpaF.getElementAt(0);
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    return targetTPActivation;
  }


  private void createTPActivation(final String tpname, final String versionId, final String techPackType) {
    try {
      boolean newActivation = false;
      Tpactivation targetTPActivation = new Tpactivation(dataModelController.getRockFactory());
      Tpactivation predecessorTPActivation = null;
      predecessorTPActivation = getPredecessorTPActivation(tpname);

      if (predecessorTPActivation != null) {
        // Update the previous installation of the tech pack.
        // The new tech pack is the same as the old one, except the new
        // versionid.
        targetTPActivation = predecessorTPActivation;

        targetTPActivation.setVersionid(versionId);
        targetTPActivation.setModified(0);//set back to default modification level.
        targetTPActivation.updateDB();
      } else {
        // Insert the tech pack activation data.
        targetTPActivation.setTechpack_name(tpname);
        targetTPActivation.setStatus("ACTIVE");
        targetTPActivation.setVersionid(versionId);
        targetTPActivation.setType(techPackType);
        targetTPActivation.setModified(0);
        targetTPActivation.insertDB();
        newActivation = true;
      }

      // Insert or update the values in table TypeActivation.
      saveTypeActivationData(newActivation, targetTPActivation);

    } catch (Exception e) {
      LOGGER.warning("Creating TPActivation failed.");
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  protected void saveTypeActivationData(final boolean newActivation, final Tpactivation tpactivation) {
    try {

      // This vector holds the TypeActivations to be updated.
    	final Vector<Typeactivation> typeActivations = new Vector<Typeactivation>();
    	final Vector<String> createdTypes = new Vector<String>();

    	final String targetVersionId = tpactivation.getVersionid();

      // First get the TypeActivations of type Measurement.
      // Get all MeasurementTypes related to this VersionID.
    	final Measurementtype whereMeasurementType = new Measurementtype(tpactivation.getRockFactory());
      whereMeasurementType.setVersionid(targetVersionId);
      final MeasurementtypeFactory measurementtypeFactory = new MeasurementtypeFactory(tpactivation.getRockFactory(),
          whereMeasurementType);

      final Vector<Measurementtype> targetMeasurementTypes = measurementtypeFactory.get();
      final Iterator<Measurementtype> targetMeasurementTypesIterator = targetMeasurementTypes.iterator();

      while (targetMeasurementTypesIterator.hasNext()) {
    	  final Measurementtype targetMeasurementType = (Measurementtype) targetMeasurementTypesIterator.next();
    	  final String targetTypeId = targetMeasurementType.getTypeid();
    	  final String targetTypename = targetMeasurementType.getTypename(); // Typename

        if (targetMeasurementType.getJoinable() != null && targetMeasurementType.getJoinable().length() != 0) {
          // Adding new PREV_ table.

        	final Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
          preTypeActivation.setTypename(targetTypename + "_PREV");
          preTypeActivation.setTablelevel("PLAIN");
          preTypeActivation.setStoragetime(Long.valueOf(-1));
          preTypeActivation.setType("Measurement");
          preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
          preTypeActivation.setStatus(tpactivation.getStatus());
          preTypeActivation.setPartitionplan(null);
          typeActivations.add(preTypeActivation);
        }

        if (targetMeasurementType.getVectorsupport() != null
            && targetMeasurementType.getVectorsupport().intValue() == 1) {

          // Adding new Vectorcounter
        	final Measurementvector mv_cond = new Measurementvector(dataModelController.getRockFactory());
          mv_cond.setTypeid(targetMeasurementType.getTypeid());
          final MeasurementvectorFactory vc_condF = new MeasurementvectorFactory(dataModelController.getRockFactory(),
              mv_cond);

          final Iterator<Measurementvector> vcIter = vc_condF.get().iterator();

          while (vcIter.hasNext()) {

        	  final Measurementvector vc = (Measurementvector) vcIter.next();

            // replace DC with DIM in DC_X_YYY_ZZZ
        	  final String typename = "DIM"
                + targetMeasurementType.getTypename().substring(
                    targetMeasurementType.getTypename().indexOf(Constants.UNDERSCORE)) + Constants.UNDERSCORE
                + vc.getDataname();

            if (createdTypes.contains(typename)) {
              // all ready exists
            } else {

              // Adding new vector counter table.
            	final Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
              preTypeActivation.setTypename(typename);
              preTypeActivation.setTablelevel("PLAIN");
              preTypeActivation.setStoragetime(Long.valueOf(-1));
              preTypeActivation.setType("Reference");
              preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
              preTypeActivation.setStatus(tpactivation.getStatus());
              preTypeActivation.setPartitionplan(null);
              typeActivations.add(preTypeActivation);
              createdTypes.add(typename);
            }
          }
        }

        final Measurementobjbhsupport mobhs = new Measurementobjbhsupport(tpactivation.getRockFactory());
        mobhs.setTypeid(targetMeasurementType.getTypeid());
        final MeasurementobjbhsupportFactory mobhsF = new MeasurementobjbhsupportFactory(tpactivation.getRockFactory(), mobhs);

        // ELEMBH
        if ((targetMeasurementType.getElementbhsupport() != null && targetMeasurementType.getElementbhsupport()
            .intValue() == 1)) {
          // replace DC_E_XXX with DIM_E_XXX_ELEMBH_BHTYPE
        	final String typename = "DIM"
              + targetMeasurementType.getVendorid().substring(
                  targetMeasurementType.getVendorid().indexOf(Constants.UNDERSCORE))
              + "_ELEMBH_BHTYPE";
          if (createdTypes.contains(typename)) {
            // all ready exists
          } else {
            // Adding new ELEMBH table.
        	  final Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
            preTypeActivation.setTypename(typename);
            preTypeActivation.setTablelevel("PLAIN");
            preTypeActivation.setStoragetime(Long.valueOf(-1));
            preTypeActivation.setType("Reference");
            preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
            preTypeActivation.setStatus(tpactivation.getStatus());
            preTypeActivation.setPartitionplan(null);
            typeActivations.add(preTypeActivation);
            createdTypes.add(typename);
          }
        }

        // OBJBH
        if (mobhsF != null && !mobhsF.get().isEmpty()) {
          // replace DC_E_XXX_YYY with DIM_E_XXX_YYY_BHTYPE
        	final String typename = "DIM"
              + targetMeasurementType.getTypename().substring(
                  targetMeasurementType.getTypename().indexOf(Constants.UNDERSCORE))
              + "_BHTYPE";
          if (createdTypes.contains(typename)) {
            // all ready exists
          } else {
            // Adding new OBJBH table.
        	  final Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
            preTypeActivation.setTypename(typename);
            preTypeActivation.setTablelevel("PLAIN");
            preTypeActivation.setStoragetime(Long.valueOf(-1));
            preTypeActivation.setType("Reference");
            preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
            preTypeActivation.setStatus(tpactivation.getStatus());
            preTypeActivation.setPartitionplan(null);
            typeActivations.add(preTypeActivation);
            createdTypes.add(typename);
          }
        }

        if (targetMeasurementType.getLoadfile_dup_check() != null && targetMeasurementType.getLoadfile_dup_check() == 1) {
          final String typename = targetMeasurementType.getTypename() + "_DUBCHECK";

          final Typeactivation preTypeActivation = new Typeactivation(tpactivation.getRockFactory());
          preTypeActivation.setTypename(typename);
          preTypeActivation.setTablelevel("PLAIN");
          preTypeActivation.setStoragetime(Long.valueOf(-1));
          preTypeActivation.setType("Reference");
          preTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
          preTypeActivation.setStatus(tpactivation.getStatus());
          preTypeActivation.setPartitionplan(null);
          typeActivations.add(preTypeActivation);
          createdTypes.add(typename);
        }

        final Measurementtable whereMeasurementTable = new Measurementtable(tpactivation.getRockFactory());
        whereMeasurementTable.setTypeid(targetTypeId);
        final MeasurementtableFactory measurementTableFactory = new MeasurementtableFactory(tpactivation.getRockFactory(),
            whereMeasurementTable);
        final Vector<Measurementtable> targetMeasurementTables = measurementTableFactory.get();
        final Iterator<Measurementtable> targetMeasurementTablesIterator = targetMeasurementTables.iterator();

        while (targetMeasurementTablesIterator.hasNext()) {
        	final Measurementtable targetMeasurementTable = (Measurementtable) targetMeasurementTablesIterator.next();
        	final String targetTableLevel = targetMeasurementTable.getTablelevel(); // Tablelevel

          // All the needed data is gathered from tables.
          // Add the Typeactivation of type Measurement to
          // typeActivations-vector to be saved later.
        	final Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
          targetTypeActivation.setTypename(targetTypename);
          targetTypeActivation.setTablelevel(targetTableLevel);
          targetTypeActivation.setStoragetime(Long.valueOf(-1));
          targetTypeActivation.setType("Measurement");
          targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
          targetTypeActivation.setStatus(tpactivation.getStatus());
          targetTypeActivation.setPartitionplan(targetMeasurementTable.getPartitionplan());
          typeActivations.add(targetTypeActivation);
        }
      }

      // Next get the TypeActivations of type Reference.
      // Get all ReferenceTables related to this VersionID.
      final Referencetable whereReferenceTable = new Referencetable(tpactivation.getRockFactory());
      whereReferenceTable.setVersionid(targetVersionId);
      final ReferencetableFactory referenceTableFactory = new ReferencetableFactory(tpactivation.getRockFactory(),
          whereReferenceTable);
      final Vector<Referencetable> targetReferenceTables = referenceTableFactory.get();
      final Iterator<Referencetable> targetReferenceTablesIterator = targetReferenceTables.iterator();

      while (targetReferenceTablesIterator.hasNext()) {
    	  final Referencetable targetReferenceTable = (Referencetable) targetReferenceTablesIterator.next();
    	  final String typename = targetReferenceTable.getTypename();

    	  final Typeactivation targetTypeActivation1 = new Typeactivation(tpactivation.getRockFactory());
        targetTypeActivation1.setTypename(typename);
        targetTypeActivation1.setType("Reference");
        targetTypeActivation1.setTablelevel("PLAIN");
        targetTypeActivation1.setStoragetime(Long.valueOf(-1));
        targetTypeActivation1.setTechpack_name(tpactivation.getTechpack_name());
        targetTypeActivation1.setStatus(tpactivation.getStatus());
        typeActivations.add(targetTypeActivation1);
        createdTypes.add(typename);
        // eeoidiv 20091203 : Timed Dynamic topology handling in ENIQ, WI 6.1.2,
        // (284/159 41-FCP 103 8147) Improved WRAN Topology in ENIQ
        // if reference tables update policy is dynamic(2) or timed dynamic(3)
        // 20110830 EANGUAN :: Adding comparison for policy number 4 for History Dynamic (for SON)
        if ((targetReferenceTable.getUpdate_policy() != null) && ((targetReferenceTable.getUpdate_policy() == 2)
                || (targetReferenceTable.getUpdate_policy() == 3) || (targetReferenceTable.getUpdate_policy() == 4))) {

          if (!createdTypes.contains(typename + "_CURRENT_DC")) {

            // create current_dc tables
            // and the table does not already contain such a row

        	  final Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
            targetTypeActivation.setTypename(typename + "_CURRENT_DC");
            targetTypeActivation.setType("Reference");
            targetTypeActivation.setTablelevel("PLAIN");
            targetTypeActivation.setStoragetime(Long.valueOf(-1));
            targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
            targetTypeActivation.setStatus(tpactivation.getStatus());
            typeActivations.add(targetTypeActivation);

            createdTypes.add(typename + "_CURRENT_DC");
          }

          // eromsza: History Dynamic: If data format support is false, don't handle _HIST and _CALC tables
          boolean dataFormatSupport = true;
          if (targetReferenceTable.getDataformatsupport() != null) {
              dataFormatSupport = targetReferenceTable.getDataformatsupport().intValue() == 1;
          }

          // eeoidiv,20110926:Automatically create _CALC table for update policy=4=HistoryDynamic (like _CURRENT_DC).
          if(targetReferenceTable.getUpdate_policy() == 4 && dataFormatSupport) {
	          if (!createdTypes.contains(typename + "_CALC")) {
	              // create _CALC tables for HistoryDynamic(4)
	              // and the table does not already contain such a row
	          	  final Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
	              targetTypeActivation.setTypename(typename + "_CALC");
	              targetTypeActivation.setType("Reference");
	              targetTypeActivation.setTablelevel("PLAIN");
	              targetTypeActivation.setStoragetime(Long.valueOf(-1));
	              targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
	              targetTypeActivation.setStatus(tpactivation.getStatus());
	              typeActivations.add(targetTypeActivation);
	              createdTypes.add(typename + "_CALC");
	          }
	          if (!createdTypes.contains(typename + "_HIST_RAW")) {
	              // create _HIST_RAW tables for HistoryDynamic(4)
	              // and the table does not already contain such a row
	          	  final Typeactivation targetTypeActivation = new Typeactivation(tpactivation.getRockFactory());
	              targetTypeActivation.setTypename(typename + "_HIST_RAW");
	              targetTypeActivation.setType("Reference");
	              targetTypeActivation.setTablelevel("PLAIN");
	              targetTypeActivation.setStoragetime(Long.valueOf(-1));
	              targetTypeActivation.setTechpack_name(tpactivation.getTechpack_name());
	              targetTypeActivation.setStatus(tpactivation.getStatus());
	              typeActivations.add(targetTypeActivation);
	              createdTypes.add(typename + "_HIST_RAW");
	          }
          }//if(targetReferenceTable.getUpdate_policy() == 4)
        }
      }

      final Vector<String> duplicateCheck = new Vector<String>();

      // Now the vector typeActivations holds the Typeactivation-objects ready
      // to be saved. Start saving the values.
      if (newActivation) {
    	  final Iterator<Typeactivation> typeActivationsIterator = typeActivations.iterator();
        while (typeActivationsIterator.hasNext()) {
        	final Typeactivation targetTypeActivation = (Typeactivation) typeActivationsIterator.next();
        	final String uniqueName = targetTypeActivation.getTechpack_name() + "/" + targetTypeActivation.getTypename() + "/"
              + targetTypeActivation.getTablelevel();
          if (!duplicateCheck.contains(uniqueName)) {
            // Tpactivation is new. Just insert the values.
            targetTypeActivation.insertDB();
            duplicateCheck.add(uniqueName);
          }
        }
      } else {
        // Update the values in TypeActivation table.
        updateTypeActivationsTable(typeActivations, tpactivation.getTechpack_name());
      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

  }

  private void updateTypeActivationsTable(final Vector<Typeactivation> newTypeActivations, final String techpackName) {
    try {
      // These two hashmaps contain the keys and objects to compare between new
      // and existing TypeActivations.
    	final HashMap<String, Typeactivation> existingTypeActivationsMap = new HashMap<String, Typeactivation>();
    	final HashMap<String, Typeactivation> newTypeActivationsMap = new HashMap<String, Typeactivation>();

    	Iterator<Typeactivation> newTypeActivationsIterator = newTypeActivations.iterator();
      while (newTypeActivationsIterator.hasNext()) {
    	  final Typeactivation currentNewTypeActivation = (Typeactivation) newTypeActivationsIterator.next();
        // Create a string that identifies the TypeActivation.
        // This string is used as a key value when comparing between new and
        // existing TypeActivations.
        // This id string is generated by the primary keys of this object.
        // For example. TECH_PACK_NAME;TYPE_NAME;TABLE_LEVEL
    	  final String idString = currentNewTypeActivation.getTechpack_name() + ";" + currentNewTypeActivation.getTypename()
            + ";" + currentNewTypeActivation.getTablelevel();
        newTypeActivationsMap.put(idString, currentNewTypeActivation);
      }

      // Get the existing typeactivations.
      Typeactivation whereTypeActivation = new Typeactivation(dataModelController.getRockFactory());
      whereTypeActivation.setTechpack_name(techpackName);
      final TypeactivationFactory typeActivationRockFactory = new TypeactivationFactory(dataModelController.getRockFactory(),
          whereTypeActivation);
      final Vector<Typeactivation> existingTypeActivations = typeActivationRockFactory.get();
      final Iterator<Typeactivation> existingTypeActivationsIterator = existingTypeActivations.iterator();

      while (existingTypeActivationsIterator.hasNext()) {
    	  final Typeactivation currentExistingTypeActivation = (Typeactivation) existingTypeActivationsIterator.next();
        // Create a string that identifies the TypeActivation and add it to the
        // existing TypeActivations map.
    	  final String idString = currentExistingTypeActivation.getTechpack_name() + ";"
            + currentExistingTypeActivation.getTypename() + ";" + currentExistingTypeActivation.getTablelevel();
        existingTypeActivationsMap.put(idString, currentExistingTypeActivation);
      }

      final HashMap<String, String> existingTypeActivationsIdStringsMap = new HashMap<String, String>();

      // First iterate through the existing TypeActivations and remove the
      // duplicate TypeActivations from the new TypeActivations.
      final Set<String> existingTypeActivationsIdStrings = existingTypeActivationsMap.keySet();
      final Iterator<String> existingTypeActivationsIdStringsIterator = existingTypeActivationsIdStrings.iterator();

      while (existingTypeActivationsIdStringsIterator.hasNext()) {
    	  final String currentIdString = (String) existingTypeActivationsIdStringsIterator.next();

        if (newTypeActivationsMap.containsKey(currentIdString)) {

          // Update the value of PARTITIONPLAN of the existing TypeActivation.
        	final String[] primKeyValues = currentIdString.split(";");
        	final String techPackName = primKeyValues[0];
        	final String typeName = primKeyValues[1];
        	final String tableLevel = primKeyValues[2];

          whereTypeActivation = new Typeactivation(dataModelController.getRockFactory());
          whereTypeActivation.setTechpack_name(techPackName);
          whereTypeActivation.setTypename(typeName);
          whereTypeActivation.setTablelevel(tableLevel);

          final TypeactivationFactory typeActivationFactory = new TypeactivationFactory(dataModelController.getRockFactory(),
              whereTypeActivation);
          final Typeactivation targetTypeActivation = (Typeactivation) typeActivationFactory.get().get(0);

          final Typeactivation newTypeActivation = (Typeactivation) newTypeActivationsMap.get(currentIdString);

          if (targetTypeActivation != null) {
            targetTypeActivation.setPartitionplan(newTypeActivation.getPartitionplan());
            targetTypeActivation.updateDB();
          } else {
            LOGGER.warning("Failed to update partitionplan of existing TypeActivation entry.");
          }

          // Remove this from newTypeActivations.
          newTypeActivationsMap.remove(currentIdString);
          existingTypeActivationsIdStringsMap.put(currentIdString, "");
        }
      }

      final Set<String> duplicateExistingTypeActivationIdStrings = existingTypeActivationsIdStringsMap.keySet();
      final Iterator<String> duplicateExistingTypeActivationIdStringsIterator = duplicateExistingTypeActivationIdStrings
          .iterator();
      while (duplicateExistingTypeActivationIdStringsIterator.hasNext()) {
    	  final String targetTypeActivationIdString = (String) duplicateExistingTypeActivationIdStringsIterator.next();
        // Don't do anything to the existing TypeActivation in the database.
        existingTypeActivationsMap.remove(targetTypeActivationIdString);
      }

      // Now the two HashMaps should contain new and obsolete values.
      // New TypeActivations are simply inserted to database.
      final Collection<Typeactivation> newTypeActivationsCollection = newTypeActivationsMap.values();
      newTypeActivationsIterator = newTypeActivationsCollection.iterator();
      while (newTypeActivationsIterator.hasNext()) {
    	  final Typeactivation currentNewTypeActivation = (Typeactivation) newTypeActivationsIterator.next();
        LOGGER.log(Level.FINE, "Inserting new TypeActivation " + currentNewTypeActivation.getTechpack_name() + " "
            + currentNewTypeActivation.getTypename() + " during tech pack update.");
        currentNewTypeActivation.insertDB();
      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  protected class ActivateTask extends Task<Void, Void> {

    public ActivateTask(final Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {

      LOGGER.log(Level.FINEST, "Activation task started.");

      final List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {

        // NOTE: A roll back cannot be done because of the row lock in IQ
        // dataModelController.getRockFactory().getConnection().setAutoCommit(false);
        // dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);
        // dataModelController.getDwhRockFactory().getConnection().setAutoCommit(false);
        // dataModelController.getDbaDwhRockFactory().getConnection().setAutoCommit(false);

    	  final String tpName = ((Versioning) selectedNode.getRockDBObject()).getTechpack_name();
    	  final String version = ((Versioning) selectedNode.getRockDBObject()).getVersionid();
        version.replace("((", "");
        version.replace("))", "");

        final int selectedValue = JOptionPane
            .showConfirmDialog(
                application.getMainFrame(),
                "Are you sure that you want to activate "
                    + version
                    + " Tech Pack in DWHM, any active techpack will be de-activated first.\n Engine profile will be set to NoLoads before ACTIVATE is done, this can take a while.",
                "Activate Tech Pack in DWHM?", JOptionPane.YES_NO_OPTION);

        if (selectedValue == JOptionPane.YES_OPTION) {

          LOGGER.log(Level.INFO, "Techpack activation started.");

          dataModelController.setAndWaitActiveExecutionProfile("NoLoads");

          final String versionId = ((Versioning) selectedNode.getRockDBObject()).getVersionid();
          final String techPackType = ((Versioning) selectedNode.getRockDBObject()).getTechpack_type();
          final String techpack = ((Versioning) selectedNode.getRockDBObject()).getTechpack_name();

          // remove old activation
          LOGGER.log(Level.FINE, "Removing old activation.");
          removeDWH();

          // create new activation
          LOGGER.log(Level.FINE, "Creating new activation.");
          createTPActivation(tpName, versionId, techPackType);

          LOGGER.log(Level.FINE, "Executing version update action.");
          try{
          final VersionUpdateActionTPIDE v = new VersionUpdateActionTPIDE(dataModelController.getRockFactory(), dataModelController
              .getDwhRockFactory(), dataModelController.getEtlRockFactory(), tpName, LOGGER);

          v.execute();
          }
          catch(Exception e1){
        	  LOGGER.log(Level.INFO, "Techpack activation is going on.");

          }
          // Init Activation Cache
          LOGGER.log(Level.FINE, "Initialising activation cache.");
          ActivationCacheTPIDE.initialize(dataModelController.getEtlRockFactory(), dataModelController.getRockFactory()
              .getDbURL(), dataModelController.getRockFactory().getUserName(), dataModelController.getRockFactory()
              .getPassword());

          // Init Loader PhysicalTable Cache
          LOGGER.log(Level.FINE, "Initialising physical table cache.");
          PhysicalTableCacheTPIDE.initialize(dataModelController.getEtlRockFactory(), dataModelController.getRockFactory()
              .getDbURL(), dataModelController.getRockFactory().getUserName(), dataModelController.getRockFactory()
              .getPassword(), dataModelController.getDwhRockFactory().getDbURL(), dataModelController
              .getDwhRockFactory().getUserName(), dataModelController.getDwhRockFactory().getPassword());

          // Initialise the Volume Partition Loader Helper
          // VolumePartitionSQLHelper.initialize(dataModelController.getEtlRockFactory(),
          // dataModelController
          // .getRockFactory().getDbURL(),
          // dataModelController.getRockFactory().getUserName(),
          // dataModelController
          // .getRockFactory().getPassword(),
          // dataModelController.getDwhRockFactory().getDbURL(),
          // dataModelController
          // .getDwhRockFactory().getUserName(),
          // dataModelController.getDwhRockFactory().getPassword());

          // check storagetime
          LOGGER.log(Level.FINE, "Executing storage time action.");
          new StorageTimeActionTPIDE(dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(),
              dataModelController.getDwhRockFactory(), dataModelController.getDbaDwhRockFactory(), techpack, LOGGER,
              true);

          // create partitions
          LOGGER.log(Level.FINE, "Creating partitions.");
          new PartitionActionTPIDE(dataModelController.getRockFactory(), dataModelController.getDwhRockFactory(), techpack,
              LOGGER);

          LOGGER.log(Level.FINE, "Disabling non active busy hour aggregation sets.");
          final Versioning ver = ((Versioning) selectedNode.getRockDBObject());

          /*
           * // set all sets and techpacks active but // if bh aggregation is
           * disabled (busyhour.enabled is 0) corresponding set is set to
           * disabled and // also the corresponding aggregation rule is also
           * disabled.
           *
           * Busyhour bh = new Busyhour(dataModelController.getRockFactory());
           * bh.setVersionid(ver.getVersionid()); BusyhourFactory bhF = new
           * BusyhourFactory(dataModelController.getRockFactory(),bh);
           * Iterator<Busyhour> bhFI = bhF.get().iterator(); while
           * (bhFI.hasNext()) { Busyhour bhour = bhFI.next();
           *
           * if (bhour.getEnable().intValue() == 0){
           *
           * // AggregationRules
           *
           * // rankbh String atype = "RANKBH"; Aggregationrule aggr = new
           * Aggregationrule(dataModelController.getRockFactory());
           *
           *
           * aggr.setAggregation(bhour.getBhlevel()+"_"+atype+"_"+bhour.getBhobject
           * ()+"_"+bhour.getBhtype()); AggregationruleFactory aggrF = new
           * AggregationruleFactory(dataModelController.getRockFactory(),aggr);
           * if (aggrF != null && aggrF.getElementAt(0)!=null){
           * aggrF.getElementAt(0).setEnable(0); aggrF.getElementAt(0).saveDB();
           * }
           *
           * // weekbh // rankbh atype = "WEEKRANKBH"; aggr = new
           * Aggregationrule(dataModelController.getRockFactory());
           * aggr.setAggregation
           * (bhour.getBhlevel()+"_"+atype+"_"+bhour.getBhobject
           * ()+"_"+bhour.getBhtype()); aggrF = new
           * AggregationruleFactory(dataModelController.getRockFactory(),aggr);
           * if (aggrF != null && aggrF.getElementAt(0)!=null){
           * aggrF.getElementAt(0).setEnable(0); aggrF.getElementAt(0).saveDB();
           * }
           *
           * // monthbh // rankbh atype = "MONTHRANKBH"; aggr = new
           * Aggregationrule(dataModelController.getRockFactory());
           * aggr.setAggregation
           * (bhour.getBhlevel()+"_"+atype+"_"+bhour.getBhobject
           * ()+"_"+bhour.getBhtype()); aggrF = new
           * AggregationruleFactory(dataModelController.getRockFactory(),aggr);
           * if (aggrF != null && aggrF.getElementAt(0)!=null){
           * aggrF.getElementAt(0).setEnable(0); aggrF.getElementAt(0).saveDB();
           * } } }
           */

          checkforReacivateViews(versionId); // check if any newly added/modified BusyHour Placeholder

          final Meta_collection_sets mcs = new Meta_collection_sets(dataModelController.getEtlRockFactory());
          mcs.setCollection_set_name(ver.getTechpack_name());
          mcs.setVersion_number(ver.getVersionid().substring(ver.getVersionid().indexOf(":") + 1));
          final Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
              mcs);

          Meta_collectionsFactory mcF = null;
          Iterator<Meta_collections> mcFI = null;

          if (mcsF != null && mcsF.get() != null && mcsF.get().size() > 0) {

        	  final Meta_collection_sets metacs = (Meta_collection_sets) mcsF.getElementAt(0);
        	  final Meta_collections mc = new Meta_collections(dataModelController.getEtlRockFactory());
            mc.setCollection_set_id(metacs.getCollection_set_id());
            mc.setVersion_number(ver.getVersionid().substring(ver.getVersionid().indexOf(":") + 1));
            mcF = new Meta_collectionsFactory(dataModelController.getEtlRockFactory(), mc);
            mcFI = mcF.get().iterator();

            if (mcFI != null) {
              while (mcFI.hasNext()) {
            	  final Meta_collections metac = (Meta_collections) mcFI.next();
                metac.setEnabled_flag("Y");
                metac.saveDB();
              }
            }

            metacs.setEnabled_flag("Y");
            metacs.saveDB();
          }

          /*
           * dataModelController.getRockFactory().getConnection().commit();
           * dataModelController.getEtlRockFactory().getConnection().commit();
           * dataModelController.getDwhRockFactory().getConnection().commit();
           * dataModelController
           * .getDbaDwhRockFactory().getConnection().commit();
           */
          /*
           * Interfacedependency idp = new
           * Interfacedependency(dataModelController.getRockFactory());
           * idp.setTechpackname(ver.getTechpack_name());
           * idp.setTechpackversion(ver.getTechpack_version());
           * InterfacedependencyFactory idpF = new
           * InterfacedependencyFactory(dataModelController
           * .getRockFactory(),idp); Iterator<Interfacedependency> idpFI =
           * idpF.get().iterator(); while(idpFI.hasNext()){ Interfacedependency
           * i = (Interfacedependency)idpFI.next(); Datainterface di = new
           * Datainterface(dataModelController.getRockFactory());
           * di.setInterfacename(i.getInterfacename());
           * di.setInterfaceversion(i.getInterfaceversion());
           * DatainterfaceFactory diF = new
           * DatainterfaceFactory(dataModelController.getRockFactory(),di); if
           * (diF!=null){
           * dataModelController.getInterfaceTreeDataModel().activateInterface
           * (diF.getElementAt(0)); } }
           */

          // Run the directory checker set.
          try {
            LOGGER.log(Level.INFO, "Running the directory checker for the techpack.");
            if (techpack.equalsIgnoreCase(Constants.ALARM_INTERFACES_TECHPACK_NAME) ||
                techpack.equalsIgnoreCase(Constants.DC_Z_ALARM_TECHPACK_NAME)) {
              final boolean ranSuccessfully = runAlarmDirCheckers(techpack, mcF);
              if (!ranSuccessfully) {
                LOGGER.log(Level.WARNING, "Failed to run directory checker sets for : " + techpack);
              }
            } else {
            dataModelController.getEngine().rundirectoryCheckerSet(techpack);
            }
          } catch (RemoteException e) {
            // Directory checker failed. Show user a message about running it
            // manually.
            LOGGER.log(Level.SEVERE, "Directory checker execution failed.");
            JOptionPane.showMessageDialog(application.getMainFrame(),
                "Directory checker execution failed. Please run the directory " + "checker manually from the AdminUI.",
                "Techpack activation", JOptionPane.INFORMATION_MESSAGE);
          }

          // Refresh the tree model
          LOGGER.log(Level.FINE, "Refreshing the DWH tree model.");
          dwhTreeDataModel.refresh();

        }

      } catch (Exception e) {

        LOGGER.log(Level.SEVERE, "Techpack activation failed.");
        JOptionPane.showMessageDialog(application.getMainFrame(), "Techpack activation failed.", "Techpack activation",
            JOptionPane.ERROR_MESSAGE);

        /*
         * dataModelController.getRockFactory().getConnection().rollback();
         * dataModelController.getEtlRockFactory().getConnection().rollback();
         * dataModelController.getDwhRockFactory().getConnection().rollback();
         * dataModelController
         * .getDbaDwhRockFactory().getConnection().rollback();
         */
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();

      } finally {
        /*
         * dataModelController.getRockFactory().getConnection().setAutoCommit(true
         * );
         * dataModelController.getEtlRockFactory().getConnection().setAutoCommit
         * (true);
         * dataModelController.getDwhRockFactory().getConnection().setAutoCommit
         * (true);
         * dataModelController.getDbaDwhRockFactory().getConnection().setAutoCommit
         * (true);
         */
        TreeState.loadExpansionState(tpTree, eList);
        tpTree.requestFocus();
      }

      dataModelController.setAndWaitActiveExecutionProfile("Normal");
      return null;
    }
  }

  /**
   * Runs the directory checkers for the AlarmInterfaces and DC_Z_ALARM tech packs.
   * @param techpack          The name of the tech pack.
   * @param mcF               The Meta_collections factory.
   * @throws RemoteException
   */
  protected boolean runAlarmDirCheckers(final String techpack, final Meta_collectionsFactory mcF) throws RemoteException {
    boolean ranSuccessfully = true;
    // Check if the iterator has elements:
    boolean iteratorHasElements = false;
    Iterator<Meta_collections> mcFI = null;
    if (mcF != null) {
      // Get the meta collections iterator:
      mcFI = mcF.get().iterator();
      if (mcFI != null) {
        iteratorHasElements = mcFI.hasNext();
      }
    }
    int noOfSetsRun = 0;

    try {
      if (iteratorHasElements) {
        while (mcFI.hasNext()) {
        	final Meta_collections metac = (Meta_collections) mcFI.next();
          final String collectionName = metac.getCollection_name();

          if (collectionName.startsWith("Directory_Checker")) {
            try {
              dataModelController.getEngine().rundirectoryCheckerSetForAlarmInterfaces(techpack, collectionName);
              LOGGER.log(Level.FINE, "Ran the directory checker: " + collectionName + " for " + techpack);
              noOfSetsRun++;
            } catch (Exception exc) {
              LOGGER.log(Level.WARNING, "Error running the directory checker " + collectionName + " for " + techpack);
              ranSuccessfully = false;
              break;
            }
          }
        }
      } else {
        // No meta collections were found, log the error:
        LOGGER.log(Level.WARNING, "No meta collection sets were found for the techpack: " + techpack);
        ranSuccessfully = false;
      }
    } catch (Exception exc) {
      LOGGER.log(Level.WARNING, "Error running alarm interface directory checkers: " + exc.toString());
      ranSuccessfully = false;
    }

    if (noOfSetsRun ==0) {
      LOGGER.log(Level.WARNING, "No directory checkers were run for alarm interface tech pack");
      ranSuccessfully = false;
    }
    return ranSuccessfully;
  }

  @Action(enabledProperty = "activateBHEnabled")
  public Task<Void, Void> activateBH() {
    final Task<Void, Void> ActivateBHTask = new ActivateBHTask(application, dataModelController, this, LOGGER);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    ActivateBHTask.setInputBlocker(new BusyIndicatorInputBlocker(ActivateBHTask, busyIndicator));

    return ActivateBHTask;
  }

  @Action(enabledProperty = "activateEnabled")
  public Task<Void, Void> activate() {
    final Task<Void, Void> ActivateTask = new ActivateTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    application.getMainFrame().setGlassPane(busyIndicator);
    ActivateTask.setInputBlocker(new BusyIndicatorInputBlocker(ActivateTask, busyIndicator));

    return ActivateTask;
  }

  public boolean isDeActivateEnabled() {
    return deActivateEnabled;
  }

  public void setDeActivateEnabled(final boolean deActivateEnabled) {
	  final boolean oldvalue = this.deActivateEnabled;
    this.deActivateEnabled = deActivateEnabled;
    firePropertyChange("deActivateEnabled", oldvalue, deActivateEnabled);
  }

  private javax.swing.Action getAction(final String actionName) {
    return application.getContext().getActionMap(this).get(actionName);
  }

  public boolean isActivateBHEnabled() {
    return activateBHEnabled;
  }

  public void setActivateBHEnabled(final boolean activateBHEnabled) {
	  final boolean oldvalue = this.activateBHEnabled;
    this.activateBHEnabled = activateBHEnabled;
    firePropertyChange("activateBHEnabled", oldvalue, activateBHEnabled);
  }

  public boolean isActivateEnabled() {
    return activateEnabled;
  }

  public void setActivateEnabled(final boolean activateEnabled) {
	  final boolean oldvalue = this.activateEnabled;
    this.activateEnabled = activateEnabled;
    firePropertyChange("activateEnabled", oldvalue, activateEnabled);
  }

  public boolean isUpgradeEnabled() {
    return upgradeEnabled;
  }

  public void setUpgradeEnabled(final boolean upgradeEnabled) {
	  final boolean oldvalue = this.upgradeEnabled;
    this.upgradeEnabled = activateEnabled;
    firePropertyChange("upgradeEnabled", oldvalue, upgradeEnabled);
  }

  public Application getApplication() {
    return application;
  }

  public void setApplication(final SingleFrameApplication application) {
    this.application = application;
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
      LOGGER.fine("Selected " + getSelectedNode());
    }
  }

  public DataTreeNode getSelectedNode() {
    return selectedNode;
  }


  /**
   * This method is executed every time a TP is selected in the Manage DWH view.
   * @param selectedNode
   */
  public void setSelectedNode(final DataTreeNode selectedNode) {
	  final DataTreeNode exSelectedNode = this.selectedNode;
    this.selectedNode = selectedNode;
    firePropertyChange("selectedNode", exSelectedNode, selectedNode);
    tpTree.setSelected(selectedNode);
    setActiveStateOfButtons();
  }

  /**
   * A private method to separate the GUI stuff from the backend stuff.
   * This makes it easier to test.
   */
  private void setActiveStateOfButtons() {
	  boolean deActiveIsEnabled = false;
	  boolean activeIsEnabled = false;
	  boolean upgradeIsEnabled = false;
	  boolean activeBHIsEnabled = false;
	  try {
		  if (this.selectedNode != null) {
			  deActiveIsEnabled = this.selectedNode.isActive();
			  activeIsEnabled = !deActiveIsEnabled;
			  upgradeIsEnabled  = !deActiveIsEnabled;

			  if (this.selectedNode.isActive()) {
				  final String techPackName = ((Versioning) this.selectedNode.getRockDBObject()).getTechpack_name();
				  final Tpactivation tpActivation = dwhTreeDataModel.getActiveVersion(techPackName);


				  if ((tpActivation != null) && tpActivation.getModified() == 1){
					  final Versioning ver = ((Versioning)this.selectedNode.getRockDBObject());
					  final String versionID = ver.getVersionid();

					  final RockFactory rock = this.dataModelController.getRockFactory();
					  final Busyhour busyhourWhere = new Busyhour(rock);
					  busyhourWhere.setVersionid(versionID);
					  final BusyhourFactory busyhourFactoryWhere = new BusyhourFactory(rock, busyhourWhere, true);
					  final Vector<Busyhour> bhVector = busyhourFactoryWhere.get();
					  final Iterator<Busyhour> itr = bhVector.iterator();

					  while(itr.hasNext()){
						  final Busyhour bh = itr.next();
						  if(!bh.isModified() && bh.getReactivateviews() == 1){
							  activeBHIsEnabled = true;
							  break;
						  }
					  }
				  }
			  }
		  }
	  }catch (SQLException e) {
		  activeBHIsEnabled = false;
	  } catch (RockException e) {
		  activeBHIsEnabled = false;
	  }
	  this.setActivateEnabled(activeIsEnabled);
	  this.setDeActivateEnabled(deActiveIsEnabled);
	  //this.setUpgradeEnabled(upgradeIsEnabled);
	  this.setActivateBHEnabled(activeBHIsEnabled);
  }


  /**
   * Listens for events from other panes.
   * use this code if you need to catch an event here:
   * application.getMainFrame().firePropertyChange("<name_of_the_event>", 0, 1);
   */
  public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
    //If the save button is pressed in any of the TAB While Editing a TP (i.e.
    //if the user makes a change to a Placeholder)
    //Then we need to re-evaluate the status of the buttons in this window.
    if(propertyChangeEvent.getPropertyName().equalsIgnoreCase("EditTP_saveButton")){
      setActiveStateOfButtons();
    }
  }


  //Reactivate views for newly created or modified placeholders.
	protected void checkforReacivateViews(final String versionId)throws Exception {
		final Busyhour where = new Busyhour(dataModelController
				.getRockFactory());
		where.setVersionid(versionId);
		where.setReactivateviews(1);
		final BusyhourFactory fac = new BusyhourFactory(dataModelController
				.getRockFactory(), where);
		final List<Busyhour> toDoBh = fac.get();
		LOGGER.log(Level.INFO, "Reactivating " + toDoBh.size() + " views");
		for (Busyhour bhCheck : toDoBh) {
			reactivateViews(bhCheck);
		}
	}
  /**
   * Reactivate views for newly created or modified Busyhour Placeholders.
   * for TR HL82079
   * @author ejohabd 20100726
   */
  protected void reactivateViews(final Busyhour bh) throws Exception {

			final List<BusyHourData> data = dataModelController
					.getBusyhourHandlingDataModel().getBusyHourData(
							bh.getTargetversionid());
			for (BusyHourData bhd : data) {
				final Busyhour bhdbh = bhd.getBusyhour();
				if ( (bhdbh.getVersionid().equals(bh.getVersionid())) && (bhdbh.getBhlevel().equals(bh.getBhlevel())) && (bhdbh.getBhtype().equals(bh.getBhtype()))) {
					bhdbh.setReactivateviews(0);
					bhdbh.updateDB();
					break;
				}
			}
	}
}
