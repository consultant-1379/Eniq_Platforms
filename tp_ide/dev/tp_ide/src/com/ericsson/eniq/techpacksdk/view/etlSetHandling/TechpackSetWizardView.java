package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TableTreeComponent;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.common.setWizards.CreateAggregatorSet;
import com.ericsson.eniq.common.setWizards.CreateDWHMSet;
import com.ericsson.eniq.common.setWizards.CreateLoaderSet;
import com.ericsson.eniq.common.setWizards.CreateLoaderSetFactory;
import com.ericsson.eniq.common.setWizards.CreateTPDirCheckerSet;
import com.ericsson.eniq.common.setWizards.CreateTPDirCheckerSetFactory;
import com.ericsson.eniq.common.setWizards.CreateTPDiskmanagerSet;
import com.ericsson.eniq.common.setWizards.CreateTopologyLoaderSet;
import com.ericsson.eniq.common.setWizards.CreateTopologyLoaderSetFactory;

@SuppressWarnings("serial")
public class TechpackSetWizardView extends JPanel {

  private static final Logger logger = Logger.getLogger(TechpackSetWizardView.class.getName());

  String templateVersions[] = { "5.2" };

  String existingSet[] = { "Overwrite", "Skip" };

  private final SingleFrameApplication application;

  protected final DataModelController dataModelController;

  protected final String setName;

  protected final String setVersion;

  private final String setType;

  protected final String versionid;

  private JCheckBox dwhSets;

  private JCheckBox loaderSets;

  private JCheckBox tloaderSets;

  private JCheckBox aggSets;

  private JCheckBox aggSetDay;
  
  private JCheckBox aggSetRankBH;
  
  private JCheckBox aggSetDayBh;
  
  private JCheckBox aggSetCount;

  private JCheckBox directoryCheckers;

  private JCheckBox diskManagers;

  protected JCheckBox scheduling;

  protected final JComboBox templateVersionCB;

  protected final JComboBox existingSetsCB;

  protected final TableTreeComponent myTTC;

  protected final JFrame frame;

  private final JFrame fFrame;

  private final JTable measUseTable;

  private final JTextField edt = new JTextField();

  private final JCheckBox edb = new JCheckBox();

  public TechpackSetWizardView(final SingleFrameApplication application, final DataModelController dataModelController,
      final TableTreeComponent myTTC, final JFrame frame, final JFrame fFrame, final String setName,
      final String setVersion, final String versionid, final String setType, final boolean editable) {
    super(new GridBagLayout());

    this.dataModelController = dataModelController;
    this.application = application;
    this.setName = setName;
    this.setVersion = setVersion;
    this.setType = setType;
    this.versionid = versionid;

    this.myTTC = myTTC;
    this.myTTC.setName("sasTree"); // Sets/Actions/Schedulings tree

    this.frame = frame;

    this.fFrame = fFrame;

    final Dimension fieldDim = new Dimension(250, 25);

    final JPanel txtPanel = new JPanel(new GridBagLayout());
    final JScrollPane txtPanelS = new JScrollPane(txtPanel);

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    // template

    final JLabel templateVersion = new JLabel("Template Version");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    txtPanel.add(templateVersion, c);

    templateVersionCB = new JComboBox(templateVersions);
    templateVersionCB.setSelectedIndex(0);
    templateVersionCB.setPreferredSize(fieldDim);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    txtPanel.add(templateVersionCB, c);

    // Generate

    final JLabel generate = new JLabel("Generate");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    txtPanel.add(generate, c);

    final int gridyPosition = createCheckBoxesForSets(txtPanel, c);

    // Element type

    final JLabel existingSets = new JLabel("Existing Sets");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = gridyPosition + 1;
    c.weightx = 0;
    txtPanel.add(existingSets, c);

    existingSetsCB = new JComboBox(existingSet);
    existingSetsCB.setPreferredSize(fieldDim);
    existingSetsCB.setSelectedItem(dataModelController.getEditInterfaceDataModel().getFormat());
    existingSetsCB.setName("TechpackSetWizardViewExistinSetsComboBox");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 1;
    c.gridy = gridyPosition + 1;
    c.weightx = 1;
    txtPanel.add(existingSetsCB, c);

    // ***************************
    final Dimension tableDim = new Dimension(400, 200);
    final JLabel tppedency = new JLabel("Measurements to Use");
    c.fill = GridBagConstraints.NONE;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.gridx = 0;
    c.gridy = gridyPosition + 2;
    c.weightx = 0;
    txtPanel.add(tppedency, c);

    final JPanel jp = new JPanel();

    measUseTable = new JTable();

    final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Measurement");
    columnNames.add("Use");

    final TPDTableModel tpdtm = new TPDTableModel(columnNames, versionid);
    tpdtm.addTableModelListener(new TableTPDSelectionListener());
    measUseTable.setModel(tpdtm);
    measUseTable.setBackground(Color.WHITE);
    measUseTable.setEnabled(editable);
    measUseTable.addMouseListener(new TableTPDSelectionListener());
    measUseTable.getTableHeader().addMouseListener(new TableTPDSelectionListener());
    measUseTable.setToolTipText("");
    measUseTable.setName("MeasurementsToUse");

    edt.setEditable(false);
    measUseTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(edt));
    measUseTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(edb));
    measUseTable.getColumnModel().getColumn(1).setCellRenderer(new MyCellRenderer());

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 5;
    c.weightx = 0;
    c.weighty = 0;
    final JScrollPane scrollPane = new JScrollPane(measUseTable);
    scrollPane.setPreferredSize(tableDim);
    jp.add(scrollPane, c);

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = gridyPosition + 3;
    c.weightx = 0;
    txtPanel.add(jp, c);

    // ***************************

    // ************** buttons **********************

    final JButton cancel = new JButton("Discard");
    cancel.setAction(getAction("discard"));
    cancel.setEnabled(editable);
    cancel.setName("TechpackSetWizardViewDiscard");

    final JButton save = new JButton("Create");
    save.setAction(getAction("create"));
    save.setEnabled(editable);
    save.setName("TechpackSetWizardViewCreate");

    // ************** button panel **********************

    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    buttonPanel.add(save);
    buttonPanel.add(cancel);

    c.gridheight = 1;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.NORTHWEST;

    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;

    this.add(txtPanelS, c);

    c.weightx = 0;
    c.weighty = 0;
    c.gridx = 0;
    c.gridy = 1;

    this.add(buttonPanel, c);

  }

  /**
   * Creates all the check boxes for all the different sets
   * 
   * @param txtPanel
   * @param constraints
   */
  protected int createCheckBoxesForSets(final JPanel txtPanel, final GridBagConstraints constraints) {
    dwhSets = new JCheckBox("DWH Sets");
    dwhSets.setName("DWH Sets");
    dwhSets.setSelected(true);
    dwhSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 3;
    constraints.weightx = 0;
    txtPanel.add(dwhSets, constraints);

    loaderSets = new JCheckBox("Loader Sets");
    loaderSets.setName("Loader Sets");
    loaderSets.setSelected(true);
    loaderSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 4;
    constraints.weightx = 0;
    txtPanel.add(loaderSets, constraints);

    tloaderSets = new JCheckBox("Topology Loader Sets");
    tloaderSets.setName("Topology Loader Sets");
    tloaderSets.setSelected(true);
    tloaderSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 5;
    constraints.weightx = 0;
    txtPanel.add(tloaderSets, constraints);

    aggSets = new JCheckBox("Aggregator Sets");
    aggSets.setName("Aggregator Sets");
    aggSets.setSelected(true);
    aggSets.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 6;
    constraints.weightx = 0;
    txtPanel.add(aggSets, constraints);

    directoryCheckers = new JCheckBox("Directory Checker Sets");
    directoryCheckers.setName("Directory Checker Sets");
    directoryCheckers.setSelected(true);
    directoryCheckers.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 7;
    constraints.weightx = 0;
    txtPanel.add(directoryCheckers, constraints);

    diskManagers = new JCheckBox("Disk Manager Sets");
    diskManagers.setName("Disk Manager Sets");
    diskManagers.setSelected(true);
    diskManagers.addActionListener(new MyActionListener());
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 8;
    constraints.weightx = 0;
    txtPanel.add(diskManagers, constraints);

    scheduling = new JCheckBox("Schedulings");
    scheduling.setName("Schedulings");
    scheduling.setSelected(true);
    constraints.fill = GridBagConstraints.NONE;
    constraints.gridx = 0;
    constraints.gridy = 9;
    constraints.weightx = 0;
    txtPanel.add(scheduling, constraints);

    return constraints.gridy;
  }

  protected boolean createTPSets(final Vector<String> skiplist, Meta_collection_sets metaCollectionSet) {
	boolean successful = true;
    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);
      dataModelController.getEtlRockFactory().getConnection().setAutoCommit(false);

      if (dwhSets.isSelected()) {

        final CreateDWHMSet cls = new CreateDWHMSet(setName, setVersion, dataModelController.getEtlRockFactory(), metaCollectionSet
            .getCollection_set_id().longValue(), metaCollectionSet.getCollection_set_name(), scheduling.isSelected());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cls.removeSets();
          cls.create(false);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cls.create(true);
        }
      }

      // Create loader sets:
      if (loaderSets.isSelected()) {

        final CreateLoaderSet cls = CreateLoaderSetFactory.createLoaderSet(templateVersions[templateVersionCB
            .getSelectedIndex()], setName, setVersion, versionid, dataModelController.getRockFactory(),
            dataModelController.getEtlRockFactory(), (int) metaCollectionSet.getCollection_set_id().longValue(), metaCollectionSet
                .getCollection_set_name(), scheduling.isSelected());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cls.removeSets(skiplist);
          cls.create(false, skiplist);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cls.create(true, skiplist);
        }
      }

      if (aggSets.isSelected()) {
        final CreateAggregatorSet cas = new CreateAggregatorSet(templateVersions[templateVersionCB.getSelectedIndex()],
            setName, setVersion, versionid, dataModelController.getRockFactory(), dataModelController
                .getEtlRockFactory(), (int) metaCollectionSet.getCollection_set_id().longValue(), scheduling.isSelected());
        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cas.removeSets(skiplist);
          cas.create(false, skiplist);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cas.create(true, skiplist);
        }
      }

      if (directoryCheckers.isSelected()) {
        final CreateTPDirCheckerSet cdc = CreateTPDirCheckerSetFactory.createTPDirCheckerSet(setName, setVersion,
            versionid, setType, dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(), metaCollectionSet
                .getCollection_set_id().longValue(), metaCollectionSet.getCollection_set_name());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cdc.removeSets(tloaderSets.isSelected());
          cdc.create(tloaderSets.isSelected(), false);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cdc.create(tloaderSets.isSelected(), true);
        }
      }

      // Create disk manager sets:
      if (diskManagers.isSelected()) {
        final CreateTPDiskmanagerSet cdm = new CreateTPDiskmanagerSet(setName, setVersion, dataModelController
            .getEtlRockFactory(), metaCollectionSet.getCollection_set_id().longValue(), metaCollectionSet.getCollection_set_name());
        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          cdm.removeSets();
          cdm.create(false, scheduling.isSelected());

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          cdm.create(true, scheduling.isSelected());
        }
      }

      // Create topology loader sets:
      if (tloaderSets.isSelected()) {
        final CreateTopologyLoaderSet tls = CreateTopologyLoaderSetFactory.createTopologyLoaderSet(
            templateVersions[(templateVersionCB.getSelectedIndex())], setName, setVersion, versionid,
            dataModelController.getRockFactory(), dataModelController.getEtlRockFactory(), (int) metaCollectionSet
                .getCollection_set_id().longValue(), metaCollectionSet.getCollection_set_name(), scheduling
                .isSelected());

        if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Overwrite")) {
          // remove existing ones
          tls.removeSets(skiplist);
          tls.create(false, skiplist);

        } else if (((String) existingSetsCB.getSelectedItem()).equalsIgnoreCase("Skip")) {
          // if set is allready created, skip it
          tls.create(true, skiplist);
        }
      }

      dataModelController.getRockFactory().getConnection().commit();
      dataModelController.getEtlRockFactory().getConnection().commit();

      myTTC.discardChanges();

    } catch (final Exception e) {
      successful = false ;
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {

      frame.dispose();

      try {

        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
        dataModelController.getEtlRockFactory().getConnection().setAutoCommit(true);

      } catch (final Exception e) {
    	successful = false;
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }
    return successful;
  }

  private Meta_collection_sets getMetaCollectionSet() throws SQLException, RockException {
    // Try to find weather specified tp exists or do we need to create it

    final Meta_collection_sets mwhere = new Meta_collection_sets(dataModelController.getEtlRockFactory());
    mwhere.setCollection_set_name(setName);
    mwhere.setVersion_number(setVersion);

    final Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(dataModelController.getEtlRockFactory(),
        mwhere);
    final Vector tps = mcsf.get();

    Meta_collection_sets metaCollectionSet = null;

    if (tps.size() <= 0) { // not exists -> create
      metaCollectionSet = new Meta_collection_sets(dataModelController.getEtlRockFactory());
      metaCollectionSet.setCollection_set_id(getNextTechPackID(dataModelController.getEtlRockFactory()));
      metaCollectionSet.setCollection_set_name(setName);
      metaCollectionSet.setVersion_number(setVersion);

      if (setType.equalsIgnoreCase("custom")) {
        metaCollectionSet.setType("Custompack");
        metaCollectionSet.setDescription("CustomPack " + setName + " " + setVersion + " by TPIDE b");
      } else if (setType.equalsIgnoreCase("system")) {
        throw new UnsupportedOperationException("Wizard cannot be used with SYSTEM techpacks.");
      } else {
        metaCollectionSet.setType("Techpack");
        metaCollectionSet.setDescription("TechPack " + setName + " " + setVersion + " by TPIDE b");
      }

      metaCollectionSet.setEnabled_flag("Y");

      metaCollectionSet.insertDB(false, false);

    } else {
      metaCollectionSet = (Meta_collection_sets) tps.get(0);
    }
    return metaCollectionSet;
  }

  protected boolean isAnySetSelected() {
    return dwhSets.isSelected() || loaderSets.isSelected() || tloaderSets.isSelected() || aggSets.isSelected()
        || directoryCheckers.isSelected() || diskManagers.isSelected();
  }

  private class WizardTask extends Task<Boolean, Void> {

    public WizardTask(final Application app) {
      super(app);
    }
    
    @Override
    protected void succeeded(Boolean setsCreatedOk) {
      logger.fine("Finished creating sets, setsCreatedOk = " + setsCreatedOk);
      if (setsCreatedOk) {
        JOptionPane.showMessageDialog(fFrame, "Finished creating sets", "Creating sets",
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(fFrame, "Error creating sets", "Creating sets", JOptionPane.ERROR_MESSAGE);
      }
    }

    @Override
    protected void failed(java.lang.Throwable cause) {
      JOptionPane.showMessageDialog(fFrame, "Finished creating sets", "Creating sets", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void cancelled() {
      JOptionPane.showMessageDialog(fFrame, "Cancelled set creation", "Creating sets", JOptionPane.WARNING_MESSAGE);
    }


    @Override
    protected Boolean doInBackground() throws Exception {
      fFrame.setEnabled(false);
      boolean successful = true;
      try{
    	  // Create a skiplist containing the measurement type names which should be
          // skipped when (re)generating the sets.
          final Vector<String> skiplist = new Vector<String>();
          for (int i = 0; i < measUseTable.getModel().getRowCount(); i++) {
            if (!(Boolean) measUseTable.getModel().getValueAt(i, 1)) {
              skiplist.add(((String) measUseTable.getModel().getValueAt(i, 0)).toUpperCase());
            }
          }
          Meta_collection_sets metaCollectionSet = getMetaCollectionSet();
          // Create the sets.
          successful = createTPSets(skiplist, metaCollectionSet);
          fFrame.setEnabled(true);
      } catch (Exception exc) {
          logger.info("Error creating sets: " + exc.toString());
          successful = false;
      }
      return successful;
    }
  }

  /**
   * create action
   * 
   * @return
   */
  @Action
  public Task create() {
    final Task WizardTask = new WizardTask(application);
    final BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    WizardTask.setInputBlocker(new BusyIndicatorInputBlocker(WizardTask, busyIndicator));

    return WizardTask;
  }

  /**
   * discard action
   * 
   * @return
   */
  @Action
  public void discard() {
    logger.log(Level.INFO, "cancel");
    frame.dispose();
  }

  public static Long getNextTechPackID(final RockFactory rock) throws SQLException, RockException {

    final Meta_collection_sets csw = new Meta_collection_sets(rock);
    final Meta_collection_setsFactory csf = new Meta_collection_setsFactory(rock, csw);

    long largest = -1;
    final Vector dbVec = csf.get();
    for (int i = 0; i < dbVec.size(); i++) {
      final Meta_collection_sets mc = (Meta_collection_sets) dbVec.elementAt(i);
      if (largest < mc.getCollection_set_id()) {
        largest = mc.getCollection_set_id();
      }
    }

    logger.fine("new TechPackID " + (largest + 1));

    return new Long(largest + 1);

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

  protected class MyActionListener implements ActionListener {

	  @Override
    public void actionPerformed(final ActionEvent e) {

      if (!(isAnySetSelected())) {
        scheduling.setSelected(false);
        scheduling.setEnabled(false);
      } else {
        scheduling.setEnabled(true);
      }
    }
  }

  class TPDTableModel extends AbstractTableModel {

    /**
       * 
       */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<String> rowData = new Vector<String>();

    private final Vector<Boolean> isActive = new Vector<Boolean>();

    public TPDTableModel(final List<String> columnNames, final String versionid) {
      try {

        this.columnNames = columnNames;

        final Measurementtype mc = new Measurementtype(dataModelController.getRockFactory());
        mc.setVersionid(versionid);
        final MeasurementtypeFactory mcF = new MeasurementtypeFactory(dataModelController.getRockFactory(), mc);

        final Iterator<Measurementtype> mcFI = mcF.get().iterator();
        while (mcFI.hasNext()) {
          final Measurementtype tmpTpd1 = mcFI.next();
          rowData.add(tmpTpd1.getTypename());
          isActive.add(new Boolean(true));
        }

        final Referencetable rc = new Referencetable(dataModelController.getRockFactory());
        rc.setVersionid(versionid);
        final ReferencetableFactory rcF = new ReferencetableFactory(dataModelController.getRockFactory(), rc);

        final Iterator<Referencetable> rcFI = rcF.get().iterator();
        while (rcFI.hasNext()) {
          final Referencetable tmpTpd1 = rcFI.next();

          // Ignore the SELECT_XXX_AGGLEVEL reference type from the base
          // techpack.
          if (tmpTpd1.getObjectname().contains("SELECT_") && tmpTpd1.getObjectname().contains("_AGGLEVEL")) {
            continue;
          }

          rowData.add(tmpTpd1.getTypename());
          isActive.add(new Boolean(true));
        }

        // Sort the entries in the table
        final String[] tmpRowData = new String[rowData.size()];
        rowData.copyInto(tmpRowData);
        Arrays.sort(tmpRowData);
        rowData = new Vector<String>();
        for (int i = 0; i < tmpRowData.length; i++) {
          rowData.add(tmpRowData[i]);
        }

      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    @Override
    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    @Override
    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Techpackdependency> getDeletedRows() {
      return null;
    }

    @Override
    public int getColumnCount() {
      return columnNames.size();
    }

    @Override
    public Object getValueAt(final int row, final int col) {

      // Tech Pack name
      if (col == 0) {
        return rowData.get(row);
      }

      if (col == 1) {
        return isActive.get(row);
      }
      return null;
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    @Override
    public void setValueAt(final Object value, final int row, final int col) {

      try {

        if (col == 0) {
          rowData.set(row, (String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          isActive.set(row, (Boolean) value);
          this.fireTableChanged(null);
        }
      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

    }

    public void addNewRow(final Techpackdependency row) {

    }

    public void removeRow(final int row) {

    }
  }

  private class TableTPDSelectionListener implements TableModelListener, ActionListener, MouseListener {

	  @Override
    public void tableChanged(final TableModelEvent e) {

      measUseTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(edt));
      measUseTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(edb));
      measUseTable.getColumnModel().getColumn(1).setCellRenderer(new MyCellRenderer());
    }

	  @Override
    public void actionPerformed(final ActionEvent e) {

    }

	  @Override
    public void mouseClicked(final MouseEvent e) {
      displayTPDMenu(e);
    }

	  @Override
    public void mouseEntered(final MouseEvent e) {

    }

	  @Override
    public void mouseExited(final MouseEvent e) {

    }

	  @Override
    public void mousePressed(final MouseEvent e) {
      displayTPDMenu(e);
    }

	  @Override
    public void mouseReleased(final MouseEvent e) {

      displayTPDMenu(e);
    }
  }

  private void displayTPDMenu(final MouseEvent e) {

    if (e.isPopupTrigger()) {
      createTPDPopup(e).show(e.getComponent(), e.getX(), e.getY());
    }
  }

  private JPopupMenu createTPDPopup(final MouseEvent e) {
    JPopupMenu popupTPD;
    JMenuItem miTPD;
    popupTPD = new JPopupMenu();

    miTPD = new JMenuItem();
    miTPD.setAction(getAction("pickall"));
    popupTPD.add(miTPD);

    miTPD = new JMenuItem();
    miTPD.setAction(getAction("discall"));
    popupTPD.add(miTPD);

    if (e.getSource() instanceof JTable) {

      miTPD = new JMenuItem();
      miTPD.setAction(getAction("pick"));
      popupTPD.add(miTPD);

      miTPD = new JMenuItem();
      miTPD.setAction(getAction("disc"));
      popupTPD.add(miTPD);

    }

    popupTPD.setOpaque(true);
    popupTPD.setLightWeightPopupEnabled(true);

    return popupTPD;

  }

  @Action
  public void disc() {

    final int[] selectedRows = measUseTable.getSelectedRows();

    for (int i = 0; i < selectedRows.length; i++) {
      measUseTable.getModel().setValueAt(new Boolean(false), selectedRows[i], 1);
    }
  }

  @Action
  public void pick() {

    final int[] selectedRows = measUseTable.getSelectedRows();

    for (int i = 0; i < selectedRows.length; i++) {
      measUseTable.getModel().setValueAt(new Boolean(true), selectedRows[i], 1);
    }
  }

  @Action
  public void discall() {
    for (int i = 0; i < measUseTable.getModel().getRowCount(); i++) {
      measUseTable.getModel().setValueAt(new Boolean(false), i, 1);
    }
  }

  @Action
  public void pickall() {
    for (int i = 0; i < measUseTable.getModel().getRowCount(); i++) {
      measUseTable.getModel().setValueAt(new Boolean(true), i, 1);
    }
  }

  public class MyCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
        final boolean hasFocus, final int row, final int column) {

      final JCheckBox cb = new JCheckBox();

      cb.setSelected((Boolean) value);
      cb.setFocusable(false);

      if (isSelected) {
        cb.setBackground(table.getSelectionBackground());
      } else {
        cb.setBackground(table.getBackground());
      }

      return cb;
    }
  }
}
