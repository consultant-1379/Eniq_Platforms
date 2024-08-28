package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.TableCellEditor;

import com.distocraft.dc5000.etl.rock.*;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.view.actionViews.*;

public class PropertiesTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(PropertiesTableCellEditor.class.getName());

    // The editor will show the same components inside the cell as the renderer.
    // Create a panel and a text field and an edit button.

    private final JPanel panel = new JPanel(new GridBagLayout());

    private final JTextField textField = new JTextField("dummy");

    private final JButton editButton = new JButton("...");

    private ActionView aView;

    private JPanel p;

    private JComboBox aType;

    private String setName;

    private String techpackName;

    private final String techPackType;

    private String oldtype = "";

    private boolean active = false;

    /**
     * Constants for the button and text field actions used.
     */
    public static final String EDITTEXT = "edittext";

    public static final String EDIT = "edit";

    public static final String OK = "ok";

    public static final String CANCEL = "cancel";

    public static final String ATYPECHANGED = "atypechange";

    /**
     * The value of the edited data
     */
    private Meta_transfer_actions origMta;

    private Meta_transfer_actions mta;

    /**
     * The edit frame that is displayed when the edit action is triggered.
     */
    protected JFrame editFrame = null;

    private final boolean editable;

    /**
     * The edit pane contained within the edit frame. Used to set and get the data.
     */
    // private JEditorPane editPane = null;
    /**
     * Constructor. Initiates the text field and the edit button in the table cell.
     */
    public PropertiesTableCellEditor(final boolean editable, final String techpackType) {

        GridBagConstraints gbc = null;

        this.editable = editable;
        this.techPackType = techpackType;

        // Configure the JPanel
        panel.setBackground(Color.white);

        // Configure the edit button and text box action commands and listeners.
        editButton.setActionCommand(EDIT);
        editButton.addActionListener(this);

        textField.setActionCommand(EDITTEXT);
        textField.addActionListener(this);
        textField.setEnabled(false);

        // Configure the layout and add the text field and the button to the
        // panel.

        gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        // Text field
        textField.setBorder(null);

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        // gbc.insets = new Insets(2, 2, 2, 2);
        panel.add(textField, gbc);

        // Edit button
        // editButton.setBorderPainted(false);
        gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        // gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 3;
        gbc.gridy = 0;
        // gbc.insets = new Insets(2, 2, 2, 2);
        panel.add(editButton, gbc);
    }

    /**
     * This returns the value to the edited cell when fireEditingStopped has been called.
     */
    @Override
    public Object getCellEditorValue() {
        return origMta;
    }

    /**
     * This returns the panel as the editor to the table. This is also where we get the initial value for the text field.
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int col) {

        if (active) {
            aType.setSelectedItem(mta.getAction_type());
            editFrame.setVisible(false);
            active = false;
            editFrame.dispose();
        }

        // Store the cell value for when we start editing
        origMta = (Meta_transfer_actions) value;
        mta = (Meta_transfer_actions) origMta.clone();
        textField.setText(origMta.getAction_contents());

        return panel;
    }

    /**
     * This is the listener callback for the three buttons (the panels edit button, and the dialog's OK and CANCEL buttons) and the text field editing
     * done action.
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        try {

            if (ATYPECHANGED.equals(e.getActionCommand())) {

                // Action type has been changed

                if (!oldtype.equalsIgnoreCase((String) aType.getSelectedItem())) {

                    if (active && oldtype.trim().length() > 0) {
                        mta.setAction_contents("");
                    }

                    // first = false;
                    aView = getActionView((String) aType.getSelectedItem(), p, mta);
                    oldtype = (String) aType.getSelectedItem();
                }

            } else if (EDIT.equals(e.getActionCommand())) {
                // Starting to edit the cell.
                // Create the edit frame, set the text and show the frame.
                if (!active) {
                    editFrame = createFrame();
                }
                if (editFrame == null) {
                    active = false;
                } else {
                    editFrame.setVisible(true);
                    active = true;
                }
            } else if (EDITTEXT.equals(e.getActionCommand())) {
                // Text box editing done. Store data.
                mta.setAction_contents(aView.getContent());
                mta.setWhere_clause(aView.getWhere());
                fireEditingStopped();
            } else if (OK.equals(e.getActionCommand())) {
                // Editing done, OK clicked.
                // Store the data, hide and dispose the frame.
                mta.setAction_contents(aView.getContent());
                mta.setWhere_clause(aView.getWhere());
                mta.setAction_type((String) aType.getSelectedItem());
                origMta = mta;
                editFrame.setVisible(false);
                active = false;
                editFrame.dispose();
                fireEditingStopped();
            } else { // CANCEL
                // Editing done, CANCEL clicked.
                // Hide and dispose the frame.
                aType.setSelectedItem(mta.getAction_type());
                editFrame.setVisible(false);
                active = false;
                editFrame.dispose();
                fireEditingCanceled();
            }

        } catch (Exception ex) {
            ExceptionHandler.instance().handle(ex);
            ex.printStackTrace();
        }
    }

    /**
     * Helper method to create the pane and frame.
     * 
     * @return
     */
    protected JFrame createFrame() {

        try {
            final Meta_collections mc = new Meta_collections(mta.getRockFactory());
            mc.setCollection_id(mta.getCollection_id());
            final Meta_collectionsFactory mcF = new Meta_collectionsFactory(mta.getRockFactory(), mc);
            techpackName = (mcF.getElementAt(0)).getCollection_name();

            final Meta_collection_sets mcs = new Meta_collection_sets(mta.getRockFactory());
            mcs.setCollection_set_id(mta.getCollection_set_id());
            final Meta_collection_setsFactory mcsF = new Meta_collection_setsFactory(mta.getRockFactory(), mcs);
            setName = (mcsF.getElementAt(0)).getCollection_set_name();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please save Parameter Type first to edit properties of this Action.", "Warning!!!!",
                    JOptionPane.WARNING_MESSAGE);
            LOGGER.warning(e.getMessage());
            return null;
        }

        // Create frame
        final JFrame thisFrame = new JFrame("Edit Action: " + techpackName + "/" + setName + "/" + mta.getTransfer_action_name());
        // thisFrame.setAlwaysOnTop(true);

        // Layout the contents of the frame
        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();
        thisFrame.setLayout(gbl);

        p = new JPanel();
        p.setLayout(gbl);

        aView = getActionView(mta.getAction_type(), p, mta);
        p.setEnabled(editable);

        // place the chooser to layout

        final JLabel name = new JLabel("Action Type");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(30, 30, 30, 0);
        gbl.setConstraints(name, gbc);
        thisFrame.add(name);
        if (techPackType.equalsIgnoreCase(Constants.ENIQ_EVENT)) {
            aType = new JComboBox(Constants.EVENTSACTIONTYPEITEMS);
        } else {
            aType = new JComboBox(Constants.ACTIONTYPEITEMS);
        }
        aType.setActionCommand(ATYPECHANGED);
        aType.addActionListener(this);
        aType.setSelectedItem(((Meta_transfer_actions) mta.clone()).getAction_type());
        aType.setEnabled(editable);

        gbc.insets = new Insets(30, 10, 30, 30);
        gbc.gridx = 1;
        gbc.gridy = 0;

        gbl.setConstraints(aType, gbc);
        thisFrame.add(aType);

        // Place the pane in the layout

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 30, 30, 30);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 4;
        gbl.setConstraints(p, gbc);
        final JScrollPane sp = new JScrollPane(p);
        gbl.setConstraints(sp, gbc);
        thisFrame.add(sp);

        // Create buttons
        final JButton ok = new JButton("Save");
        final JButton cancel = new JButton("Cancel");

        ok.setPreferredSize(cancel.getPreferredSize());
        ok.setActionCommand(OK);
        ok.addActionListener(this);
        ok.setEnabled(editable);

        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);

        // Place the buttons in the layout
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        gbc.gridy = 3;
        gbc.gridx = 2;
        gbl.setConstraints(ok, gbc);
        thisFrame.add(ok);

        gbc.gridy = 3;
        gbc.gridx = 3;
        gbl.setConstraints(cancel, gbc);
        thisFrame.add(cancel);

        // And we're done
        thisFrame.pack();
        return thisFrame;
    }

    protected ActionView getActionView(final String stype, final JPanel parent, final Meta_transfer_actions act) {

        if (editFrame != null) {
            editFrame.setSize(600, 600);
        }

        if (stype == null) {
            return new GenericActionView(parent, act, stype);
        }

        final JDialog j = new JDialog(editFrame);
        j.setAlwaysOnTop(true);

        if (stype.equals("Parse")) {
            return new ParseActionView(parent, act, j, this.setName);
        } else if (stype.equals("Load")) {
            return new LoadActionView(parent, act);
        } else if (stype.equals("Partitioned Loader") || stype.equals("Loader")) {
            return new PartitionedLoadActionView(parent, act);
        } else if (stype.equals(Constants.EVENT_LOADER)) {
            return new EventLoadActionView(parent, act);
        } else if (stype.equals("UnPartitioned Loader")) {
            return new UnPartitionedLoadActionView(parent, act);
        } else if (stype.equals("Join")) {
            return new JoinActionView(parent, act, null);
        } else if (stype.equals("Uncompress")) {
            return new UncompressActionView(parent, act);
        } else if (stype.equals("System Call")) {
            return new SystemCallActionView(parent, act);
        } else if (stype.equals("Mediation")) {
            return new MediationActionView(parent, act);
        } else if (stype.equals("Aggregation")) {
            return new AggregationActionView(parent, act);
        } else if (stype.equals("SQL Execute")) {
            return new SQLExecuteActionView(parent, act);
        } else if (stype.equals("SQL Extract")) {
            return new SQLExtractActionView(parent, act);
        } else if (stype.equals("SessionLog Loader")) {
            return new LogSessionLoaderActionView(parent, act);
        } else if (stype.equals("JDBC Mediation")) {
            return new JDBCMediationActionView(parent, act);
        } else if (stype.equals("SMTP Mediation")) {
            return new SMTPMediationActionView(parent, act);
        } else if (stype.equals("AlarmHandler")) {
            return new AlarmHandlerActionView(parent, act);
        } else if (stype.equals("AlarmMarkup")) {
            return new AlarmMarkupActionView(parent, act);
        } else if (stype.equals("SNMP Poller")) {
            return new SNMPPollerActionView(parent, act);
        } else if (stype.equals("UpdateDimSession")) {
            return new UpdateDimSessionActionView(parent, act, null);
        } else if (stype.equals("Diskmanager")) {
            return new DiskManagerActionView(parent, act);
        } else if (stype.equals("CreateDir")) {
            return new CreateDirActionView(parent, act);
        } else if (stype.equals("CreateAlarmFile")) {
            return new CreateAlarmFileActionView(parent, act);
        } else if (stype.equals("TriggerScheduledSet")) {
            return new TriggerScheduledSetActionView(parent, act);
        } else if (stype.equals("Distribute")) {
            return new DistributeActionView(parent, act);
        } else if (stype.equals("UpdateMonitoring")) {
            return new UpdateMonitoringActionView(parent, act, null);
        } else if (stype.equals("ManualReAggregation")) {
            return new ManualReAggregationActionView(parent, act);
        } else if (stype.equals("DirectoryDiskmanager")) {
            return new DirectoryDiskmanagerActionView(parent, act, null);
        } else if (stype.equals("TableCleaner")) {
            return new TableCleanerActionView(parent, act);
        } else if (stype.equals("DWHMigrate")) {
            return new DWHMigrateActionView(parent, act, null);
        } else if (stype.equals("GateKeeper")) {
            return new GateKeeperActionView(parent, act);
        } else if (stype.equals("SetTypeTrigger")) {
            return new SetTypeTriggerActionView(parent, act);
        } else if (stype.equals("AggregationRuleCopy")) {
            return new AggregationRuleCopyActionView(parent, act);
        } else if (stype.equals("UpdateMonitoredTypes")) {
            return new UpdateMonitoredTypesActionView(parent, act);
        } else if (stype.equals("PartitionedSQLExec")) {
            return new PartitionedSQLExecuteActionView(parent, act);
        } else if (stype.equals("System Monitor")) {
            return new SystemMonitorActionView(parent, act);
        } else if (stype.equals("DuplicateCheck")) {
            return new DuplicateCheckActionView(parent, act);
        } else if (stype.equals("ReloadDBLookups")) {
            return new ReloadDBLookupsActionView(parent, act);
        } else if (stype.equals("RefreshDBLookup")) {
            return new RefreshDBLookupActionView(parent, act);
        } else if (stype.equals("EBSUpdate")) {
            act.setWhere_clause("typeName=EBSUpdate");
            return new GenericEmptyActionView(parent, act, stype);
        } else if (stype.equals(Constants.COUNTING_INTERVALS)) {
            return new CountingIntervalsActionView(parent);
        } else if (stype.equals(Constants.UPDATE_COUNT_INTERVALS)) {
            return new UpdateCountIntervalsActionView(parent, act);
        } else if (stype.equals(Constants.STORE_COUNTING_DATA)) {
            return new StoreCountingDataActionView(parent);
        } else if (stype.equals(Constants.COUNTING_ACTION)) {
            return new CountActionView(parent, act);
        } else if (stype.equals(Constants.COUNTING_DAY_ACTION)) {
            return new CountDayActionView(parent, act);
        } else if (stype.equals(Constants.COUNTING_TRIGGER)) {
            return new CountTriggerActionView(parent, act);
        } else if (stype.equals(Constants.COUNTING_DAY_TRIGGER)) {
            return new CountDayTriggerActionView(parent, act);
        } else if (stype.equals(Constants.CREATE_COLLECTED_DATA_FILES)) {
            return new CreateCollectedDataFilesActionView(parent, act);
        } else if (stype.equals(Constants.UPDATE_COLLECTED_DATA_FILES)) {
            return new UpdateCollectedDataActionView(parent, act);
        } else if (stype.equals(Constants.BACKUP_TRIGGER)) {
            return new BackupTriggerActionView(parent);
        } else if (stype.equals(Constants.BACKUP_COUNT_DAY)) {
            return new BackupCountDayActionView(parent);
        } else if (stype.equals(Constants.BACKUP_TABLES)) {
            return new BackupTablesActionView(parent, act);
        } else if (stype.equals(Constants.BACKUP_LOADER)) {
            return new BackupLoaderActionView(parent);
        } else if (stype.equals(Constants.RESTORE)) {
            return new RestoreActionView(parent, act);
        } else if (stype.equals(Constants.TOPOLOGY_SQL_EXECUTE)) {
            return new TopologySQLExecuteActionView(parent, act);
        } else if (stype.equals(Constants.UNKNOWN_TOPOLOGY)) {
            return new UnknownTopologySQLExecuteActionView(parent, act);
        } else if (stype.equals(Constants.IMSI_TO_IMEI)) {
            return new IMSItoIMEISQLExecuteActionView(parent, act);
        } else if (stype.equals(Constants.GATE_KEEPER_PROPERTY)) {
            return new GateKeeperPropertyActionView(parent, act);
        } else if (stype.equals(Constants.TIMEBASE_PARTITION_LOADER)) {
            return new TimeBasePartitionLoaderActionView(parent, act);
        } else if (stype.equals(Constants.COUNT_REAGG_ACTION)) {
            return new CountingReAggActionView(parent, act);
        } else if (stype.equals(Constants.UPDATE_HASH_IDS)) {
            return new UpdateHashIdsActionView(parent, act);
        } else if (stype.equals(Constants.HISTORY_SQL_EXECUTE)) {
            return new SQLExecuteActionView(parent, act);
        } else if (stype.equals("UpdatePlan") || stype.equals("AlarmInterfaceUpdate") || stype.equalsIgnoreCase("AggRuleCacheRefresh")
                || stype.equalsIgnoreCase("AutomaticAggregation") || stype.equals("AutomaticREAggregati") || stype.equals("JVMMonitor")
                || stype.equals("VersionUpdate") || stype.equals("StorageTimeAction") || stype.equals("PartitionAction")
                || stype.equals("TableCheck") || stype.equals("SanityCheck") || stype.equals("ReloadTransformation")
                || stype.equals("ReloadProperties") || stype.equals("TriggerDeltaView") || stype.equals("BackupTopologyData") || stype.equals("BackupAggregatedData")) {
            return new GenericEmptyActionView(parent, act, stype);
        } else {
            return new GenericActionView(parent, act, stype);
        }

    }

    /**
     * Gets the combo box representing the drop down of action types that can selected. Method needed for testing purposes
     * 
     * @return
     */
    protected JComboBox getAType() {
        return aType;
    }

}
