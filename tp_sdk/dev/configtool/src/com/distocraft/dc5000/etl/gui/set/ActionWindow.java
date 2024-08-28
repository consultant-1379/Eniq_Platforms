package com.distocraft.dc5000.etl.gui.set;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.gui.set.actionview.ActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.AggregationActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.AggregationRuleCopyActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.AlarmHandlerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.AlarmMarkupActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.CreateDirActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.DWHMigrateActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.DirectoryDiskmanagerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.DiskManagerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.DistributeActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.DuplicateCheckActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.GateKeeperActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.GenericActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.GenericEmptyActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.JDBCMediationActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.JoinActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.LoadActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.LogSessionLoaderActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.ManualReAggregationActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.MediationActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.ParseActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.PartitionedLoadActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.PartitionedSQLExecuteActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.RefreshDBLookupActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.ReloadDBLookupsActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SMTPMediationActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SNMPPollerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SQLExecuteActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SQLExtractActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SetTypeTriggerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SystemCallActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.SystemMonitorActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.TableCleanerActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.TriggerScheduledSetActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.UnPartitionedLoadActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.UncompressActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.UpdateDimSessionActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.UpdateMonitoredTypesActionView;
import com.distocraft.dc5000.etl.gui.set.actionview.UpdateMonitoringActionView;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.distocraft.dc5000.etl.rock.Meta_databasesFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class ActionWindow extends JDialog {

  private RockFactory rock;

  private Meta_collection_sets techPack;

  private Meta_collections set;

  private Meta_transfer_actions action;

  private JTextField name;

  private JTextField aOrder;

  private JPanel actionPanel;

  private ActionView actionView;

  private String[] conn_names;

  private Long[] conn_ids;

  private boolean commit = false;

  private boolean newAction = false;

  private String origName = null;

  private Long origOrder = null;

  ActionWindow(JFrame parent, Meta_collections set, Meta_collection_sets tpmeta, RockFactory rock,
      Meta_transfer_actions xaction, Long rowNew) {
    super(parent, true);

    this.rock = rock;
    this.set = set;
    this.techPack = tpmeta;
    this.action = xaction;

    try {

      if (this.action == null) { // new Action
        action = new Meta_transfer_actions(rock);
        action.setTransfer_action_id(getNextActionID());
        action.setVersion_number(tpmeta.getVersion_number());
        action.setCollection_set_id(tpmeta.getCollection_set_id());
        action.setCollection_id(set.getCollection_id());
        action.setOrder_by_no(rowNew);
        action.setConnection_id(new Long(0L));
        newAction = true;
      } else {
        origName = action.getTransfer_action_name();
        origOrder = action.getOrder_by_no();
      }

      Meta_databases mdb = new Meta_databases(rock);
      mdb.setType_name("USER");
      Meta_databasesFactory mdf = new Meta_databasesFactory(rock, mdb);
      Vector cons = mdf.get();

      conn_names = new String[cons.size()];
      conn_ids = new Long[cons.size()];

      for (int i = 0; i < cons.size(); i++) {
        Meta_databases md = (Meta_databases) cons.get(i);
        conn_names[i] = md.getConnection_name();
        conn_ids[i] = md.getConnection_id();
      }

      setTitle("Action " + action.getTransfer_action_id());

      final Container con = getContentPane();
      con.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(2, 4, 2, 2);

      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 1;
      c.gridx = 1;
      c.gridwidth = 2;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      con.add(new JLabel("Action"), c);

      c.gridy = 2;
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridy = 3;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Package"), c);

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JLabel tid = new JLabel(techPack.getCollection_set_name() + " " + techPack.getVersion_number());
      con.add(tid, c);

      // c.gridy = 4 removed

      c.gridx = 1;
      c.gridy = 5;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Set"), c);

      Long setID = action.getCollection_id();

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JLabel sid = new JLabel(set.getCollection_name());
      con.add(sid, c);

      c.gridx = 1;
      c.gridy = 6;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(new JLabel("Action ID"), c);

      Long actionID = action.getTransfer_action_id();

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JLabel aid = new JLabel(actionID.toString());
      con.add(aid, c);

      c.gridx = 1;
      c.gridy = 7;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_aOrder = new JLabel("Action Order");
      l_aOrder.setToolTipText("Order number of the action in the set.");
      con.add(l_aOrder, c);

      c.gridx = 2;
      aOrder = new JTextField(new NumericDocument(), getLong(action.getOrder_by_no()), 3);
      aOrder.setToolTipText("Order number of the action in the set.");
      con.add(aOrder, c);

      c.gridx = 1;
      c.gridy = 8;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_name = new JLabel("Name");
      l_name.setToolTipText("Name of the action.");
      con.add(l_name, c);

      origName = action.getTransfer_action_name();

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      name = new JTextField(action.getTransfer_action_name(), 20);
      name.setToolTipText("Name of the action.");
      con.add(name, c);

      c.gridx = 1;
      c.gridy = 9;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_description = new JLabel("Description");
      l_description.setToolTipText("Description of the action.");
      con.add(l_description, c);

      c.gridx = 2;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      JTextField description = new JTextField(action.getDescription(), 20);
      description.setToolTipText("Description of the action.");
      con.add(description, c);

      c.gridx = 1;
      c.gridy = 10;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_dbcon = new JLabel("DB Connection");
      l_dbcon.setToolTipText("Database connection used by the action.");
      con.add(l_dbcon, c);

      c.gridx = 2;
      c.weightx = 0;
      JComboBox dbcon = new JComboBox(conn_ids);
      dbcon.setToolTipText("Database connection used by the action.");
      dbcon.setRenderer(new ListCellRenderer() {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
          Long mc = (Long) value;
          for (int i = 0; i < conn_ids.length; i++) {
            if (conn_ids[i].equals(mc))
              return new JLabel(conn_names[i]);
          }

          return new JLabel(String.valueOf(mc));
        }
      });
      for (int i = 0; i < conn_ids.length; i++) {
        if (conn_ids[i].equals(action.getConnection_id()))
          dbcon.setSelectedIndex(i);
      }
      con.add(dbcon, c);

      c.gridx = 1;
      c.gridy = 11;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_enstatus = new JLabel("Status");
      l_enstatus.setToolTipText("Execution status of this action.");
      con.add(l_enstatus, c);

      c.gridx = 2;
      JComboBox enstatus = new JComboBox(ConfigTool.STATUSES);
      enstatus.setToolTipText("Execution status of this action.");
      if ("n".equalsIgnoreCase(action.getEnabled_flag()))
        enstatus.setSelectedIndex(1);
      else
        enstatus.setSelectedIndex(0);
      con.add(enstatus, c);

      c.gridx = 1;
      c.gridy = 12;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      JLabel l_type = new JLabel("Type");
      l_type.setToolTipText("Type of this action.");
      con.add(l_type, c);

      c.gridx = 2;
      final JComboBox type = new JComboBox(ConfigTool.ACTION_TYPES);
      type.setToolTipText("Type of this action.");
      type.setEditable(true);
      if (action.getAction_type() != null) {
        int selix = -1;
        for (int i = 0; i < ConfigTool.ACTION_TYPES.length; i++) {
          if (ConfigTool.ACTION_TYPES[i].equals(action.getAction_type()))
            selix = i;
        }
        if (selix >= 0) {
          type.setSelectedIndex(selix);
        } else {
          type.addItem(action.getAction_type());
          type.setSelectedIndex(type.getItemCount() - 1);
        }
      }
      type.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          String stype = (String) type.getSelectedItem();
          actionView = getActionView(stype, actionPanel, null);
          pack();
        }
      });
      con.add(type, c);

      c.gridx = 1;
      c.gridy = 13;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      con.add(Box.createRigidArea(new Dimension(10, 10)), c);

      actionPanel = new JPanel(new GridBagLayout());
      JScrollPane scr = new JScrollPane(actionPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
          JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      actionView = getActionView((String) type.getSelectedItem(), actionPanel, action);

      c.gridx = 1;
      c.gridy = 14;
      c.gridwidth = 2;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 1;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(scr, c);

      c.gridy = 15;
      c.gridx = 1;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      Component cx = Box.createRigidArea(new Dimension(10, 10));
      con.add(cx, c);

      c.gridy = 16;
      c.fill = GridBagConstraints.NONE;
      c.insets = new Insets(2, 4, 2, 2);
      JButton discard = new JButton("Discard", ConfigTool.delete);
      discard.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {
          commit = false;
          setVisible(false);
        }
      });
      con.add(discard, c);

      c.gridx = 2;
      JButton save = new JButton("Save", ConfigTool.check);
      save.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent ae) {

          String error = "";

          String xname = name.getText();
          if (xname == null || xname.trim().length() <= 0) {
            error += "Parameter name must be defined\n";
          } else {
            xname = xname.trim();
            for (int i = 0; i < xname.length(); i++) {
              if (Character.isWhitespace(xname.charAt(i))) {
                error += "Parameter name must NOT contain whitespaces\n";
                break;
              }
            }

            if (newAction || (!newAction && !xname.equals(origName))) {

              try {
                Meta_transfer_actions where = new Meta_transfer_actions(ActionWindow.this.rock);
                where.setVersion_number(action.getVersion_number());
                where.setCollection_set_id(action.getCollection_set_id());
                where.setCollection_id(action.getCollection_id());
                where.setTransfer_action_name(xname);

                Meta_transfer_actionsFactory fac = new Meta_transfer_actionsFactory(ActionWindow.this.rock, where);

                Vector c = fac.get();

                if (c.size() > 0)
                  error += "Name of the action must be unique\n";
                c.clear();

              } catch (Exception e) {
                error += "Name validation failed: " + e.getMessage() + "\n";
              }

            }

          }

          try {
            long currentOrder = Long.parseLong(aOrder.getText());

            if (currentOrder < 0L)
              throw new NumberFormatException();

            if (newAction || (!newAction && origOrder.longValue() != currentOrder)) {

              Meta_transfer_actions where = new Meta_transfer_actions(ActionWindow.this.rock);
              where.setVersion_number(action.getVersion_number());
              where.setCollection_set_id(action.getCollection_set_id());
              where.setCollection_id(action.getCollection_id());
              where.setOrder_by_no(new Long(currentOrder));

              Meta_transfer_actionsFactory fac = new Meta_transfer_actionsFactory(ActionWindow.this.rock, where);

              Vector c = fac.get();

              if (c.size() > 0)
                error += "Action order must be unique\n";
              c.clear();

            }

          } catch (NumberFormatException nfe) {
            error += "Action order must be numeric and >= 0\n";
          } catch (Exception e) {
            error += "Action order validation failed: " + e.getMessage() + "\n";
          }

          error += actionView.validate();

          if (error.length() > 0) {
            JOptionPane.showMessageDialog(ActionWindow.this, error, "Invalid configuration", JOptionPane.ERROR_MESSAGE);

            ConfigTool.reloadConfig();

            return;
          }

          commit = true;
          setVisible(false);
        }
      });
      con.add(save, c);

      c.gridx = 3;
      c.insets = new Insets(0, 0, 0, 0);
      con.add(Box.createRigidArea(new Dimension(5, 5)), c);

      pack();

      setVisible(true); // Blocks until window is closed

      if (!commit)
        return;

      action.setTransfer_action_name(name.getText().trim());
      action.setDescription(description.getText().trim());
      action.setConnection_id((Long) dbcon.getSelectedItem());
      action.setOrder_by_no(new Long(aOrder.getText().trim()));

      if (((String) enstatus.getSelectedItem()).equals("enabled"))
        action.setEnabled_flag("Y");
      else
        action.setEnabled_flag("N");
      action.setAction_type((String) type.getSelectedItem());
      action.setWhere_clause(actionView.getWhere());
      action.setAction_contents(actionView.getContent());

      if (!newAction) {
        action.updateDB();
      } else {
        action.insertDB(false, false);
      }

    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.close();

      JOptionPane.showMessageDialog(ActionWindow.this, sw.toString(), "General failure", JOptionPane.ERROR_MESSAGE);
    }

  }

  private ActionView getActionView(String stype, JPanel parent, Meta_transfer_actions act) {
    if (stype.equals("Parse")) {
      return new ParseActionView(parent, act, this);
    } else if (stype.equals("Load")) {
      return new LoadActionView(parent, act);
    } else if (stype.equals("Partitioned Loader") || stype.equals("Loader")) {
      return new PartitionedLoadActionView(parent, act);
    } else if (stype.equals("UnPartitioned Loader")) {
      return new UnPartitionedLoadActionView(parent, act);
    } else if (stype.equals("Join")) {
      return new JoinActionView(parent, act, this);
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
      return new UpdateDimSessionActionView(parent, act, this);
    } else if (stype.equals("Diskmanager")) {
      return new DiskManagerActionView(parent, act);
    } else if (stype.equals("CreateDir")) {
      return new CreateDirActionView(parent, act);
    } else if (stype.equals("TriggerScheduledSet")) {
      return new TriggerScheduledSetActionView(parent, act);
    } else if (stype.equals("Distribute")) {
      return new DistributeActionView(parent, act);
    } else if (stype.equals("UpdateMonitoring")) {
      return new UpdateMonitoringActionView(parent, act, this);
    } else if (stype.equals("ManualReAggregation")) {
      return new ManualReAggregationActionView(parent, act);
    } else if (stype.equals("DirectoryDiskmanager")) {
      return new DirectoryDiskmanagerActionView(parent, act, this);
    } else if (stype.equals("TableCleaner")) {
      return new TableCleanerActionView(parent, act);
    } else if (stype.equals("DWHMigrate")) {
      return new DWHMigrateActionView(parent, act, this);
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
    } else if (stype.equals("UpdatePlan") || stype.equals("AlarmInterfaceUpdate")
        || stype.equalsIgnoreCase("AutomaticAggregation") || stype.equals("AutomaticREAggregati")
        || stype.equals("JVMMonitor") || stype.equals("VersionUpdate") || stype.equals("StorageTimeAction")
        || stype.equals("PartitionAction") || stype.equals("TableCheck") || stype.equals("SanityCheck")
        || stype.equals("ReloadTransformation") || stype.equals("ReloadProperties")) {
      return new GenericEmptyActionView(parent, act, stype);
    } else {
      return new GenericActionView(parent, act, stype);
    }

  }

  Long getNextActionID() throws Exception {

    Meta_transfer_actions whre = new Meta_transfer_actions(rock);
    Meta_transfer_actionsFactory afact = new Meta_transfer_actionsFactory(rock, whre);

    Enumeration e = afact.get().elements();
    Long biggest = new Long(-1L);
    while (e.hasMoreElements()) {
      Meta_transfer_actions mta = (Meta_transfer_actions) e.nextElement();
      Long cid = mta.getTransfer_action_id();
      if (cid.longValue() > biggest.longValue())
        biggest = cid;
    }

    return new Long(biggest.longValue() + 1);
  }

  Meta_transfer_actions getAction() {
    return action;
  }

  boolean committed() {
    return commit;
  }

  boolean isNew() {
    return newAction;
  }

  private Long getLong(String str) {
    if (str == null || str.length() == 0)
      return null;
    else {
      try {
        return new Long(str);
      } catch (NumberFormatException nfe) {
        return null;
      }
    }

  }

  private String getLong(Long lon) {
    if (lon == null)
      return "";
    else
      return lon.toString();
  }

  private boolean longEquals(Long l1, Long l2) {
    if (l1 != null && l2 == null)
      return false;

    if (l1 == null && l2 != null)
      return false;

    if (l1 == null && l2 == null)
      return true;

    return l1.equals(l2);

  }

}
