package com.distocraft.dc5000.etl.gui.set;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.NumericDocument;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class SetWindow extends JDialog {

  private Meta_collections sm;

  private Meta_collections pasteSet = null;

  private boolean newSet = false;

  private boolean committed = false;

  private boolean paste = false;

  public SetWindow(JFrame frame, RockFactory rock, TechPackTree tpTree, TechPackNode tpn, Meta_collections sx,
      Meta_collections pasteSet) {
    super(frame, true);

    this.sm = sx;
    this.pasteSet = pasteSet;

    if (pasteSet == null) { // Create a new set

      if (sm == null) {

        sm = new Meta_collections(rock);

        sm.setCollection_set_id(tpn.getTechPackID());
        sm.setVersion_number(tpn.getTechPackVersion());
        sm.setCollection_id(tpn.getNextSetID(rock));

        setTitle("New Set " + sm.getCollection_id());

        newSet = true;

      } else {
        setTitle("Set " + sm.getCollection_id());
      }

    } else { // Create Set by Paste

      sm = new Meta_collections(rock);

      sm.setCollection_set_id(tpn.getTechPackID());
      sm.setVersion_number(tpn.getTechPackVersion());
      sm.setCollection_id(tpn.getNextSetID(rock));

      sm.setCollection_name(pasteSet.getCollection_name());
      sm.setSettype(pasteSet.getSettype());
      sm.setPriority(pasteSet.getPriority());
      sm.setQueue_time_limit(pasteSet.getQueue_time_limit());
      sm.setEnabled_flag(pasteSet.getEnabled_flag());

      setTitle("Pasted Set " + sm.getCollection_id());

      paste = true;

    }

    Container con = getContentPane();
    con.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);

    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 1;
    c.gridx = 1;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    String hdr = "Modify parameters of the Set";
    if (newSet)
      hdr = "Define parameters of new Set";
    if (paste)
      hdr = "Review parameters of pasted Set";
    con.add(new JLabel(hdr), c);

    c.gridy = 2;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 3;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Package"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    con.add(new JLabel(tpn.getTechPackName() + " " + tpn.getTechPackVersion()), c);

    // c.gridy = 4 removed

    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("ID"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    JLabel sid = new JLabel(sm.getCollection_id().toString());
    con.add(sid, c);

    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Name"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    final JTextField name = new JTextField(sm.getCollection_name(), 20);
    con.add(name, c);

    c.gridx = 1;
    c.gridy = 7;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Type"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    JComboBox type = new JComboBox(ConfigTool.SET_TYPES);
    type.setSelectedIndex(0);
    boolean found = false;
    for (int i = 0; i < ConfigTool.SET_TYPES.length; i++) {
      if (ConfigTool.SET_TYPES[i].equals(sm.getSettype())) {
        type.setSelectedIndex(i);
        found = true;
        break;
      }
    }
    if (!found) {
      type.insertItemAt(sm.getSettype(), 0);
      type.setSelectedIndex(0);
    }
    con.add(type, c);

    c.gridx = 1;
    c.gridy = 8;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Priority"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    final JTextField priority = new JTextField(new NumericDocument(),(sm.getPriority() != null ? sm.getPriority().toString() : ""), 5);
    priority.setToolTipText("Priority of this set. Priority is relative to other sets.");
    con.add(priority, c);

    c.gridx = 1;
    c.gridy = 9;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Queue time limit"), c);

    c.gridx = 2;
    final JTextField timelimit = new JTextField(new NumericDocument(),(sm.getQueue_time_limit() != null ? sm.getQueue_time_limit().toString()
        : ""), 5);
    timelimit.setToolTipText("Time limit in minutes that the set waits in queue before priority starts to rise.");
    con.add(timelimit, c);

    c.gridx = 1;
    c.gridy = 10;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(new JLabel("Status"), c);

    c.gridx = 2;
    c.gridwidth = 2;
    JComboBox enstatus = new JComboBox(ConfigTool.STATUSES);
    if ("n".equalsIgnoreCase(sm.getEnabled_flag()))
      enstatus.setSelectedIndex(1);
    else
      enstatus.setSelectedIndex(0);
    con.add(enstatus, c);

    c.gridy = 11;
    c.gridx = 4;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    con.add(Box.createRigidArea(new Dimension(5, 5)), c);

    c.gridy = 12;
    c.gridx = 1;
    JButton discard = new JButton("Cancel", ConfigTool.delete);
    discard.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent ae) {
        setVisible(false);
      }
    });
    con.add(discard, c);

    c.gridx = 2;
    JButton save = new JButton((newSet || paste) ? "Create" : "Modify", ConfigTool.newIcon);
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
        }

        Long xprio = getLong(priority.getText().trim());
        if (xprio == null) {
            error += "Parameter priority must be defined\n";
        } else if (xprio.longValue() < 0 || xprio.longValue() > 15) {
          error += "Parameter priority must be a numeric value [0,15]\n";
        }

        Long xqlim = getLong(timelimit.getText().trim());
        if (xqlim == null) {
            error += "Parameter queue time limit must be defined";
        } else if (xqlim.longValue() <= 0) {
          error += "Parameter queue time limit must be > 0\n";
        }

        if (error.length() > 0) {
          JOptionPane.showMessageDialog(SetWindow.this, error, "Invalid configuration", JOptionPane.ERROR_MESSAGE);
          return;
        }

        committed = true;
        setVisible(false);
      }
    });
    con.add(save, c);

    pack();
    setVisible(true);

    if (!committed)
      return;

    try {
      sm.setCollection_name(name.getText().trim());
      sm.setSettype((String) type.getSelectedItem());
      sm.setPriority(getLong(priority.getText().trim()));
      sm.setQueue_time_limit(getLong(timelimit.getText().trim()));
      if (enstatus.getSelectedIndex() == 0) {
        sm.setEnabled_flag("Y");
      } else {
        sm.setEnabled_flag("N");
      }

      sm.setHold_flag("N");
      sm.setMax_errors(new Long(0));
      sm.setMax_fk_errors(new Long(0));
      sm.setMax_col_limit_errors(new Long(0));
      sm.setCheck_fk_error_flag("N");
      sm.setCheck_col_limits_flag("N");
      sm.setFoldable_flag("Y");

      if (newSet || paste) {
        sm.insertDB(false, false);
        tpTree.addSet(tpn, sm);
      } else {
        sm.updateDB();
        // update tree
      }

    } catch (Exception e) {
      ErrorDialog ed = new ErrorDialog(frame, "Error", "Unable to create set", e);
    }

    if (paste) { // copy actions to pasted set
      try {
        Meta_transfer_actions mta = new Meta_transfer_actions(pasteSet.getRockFactory());
        mta.setCollection_set_id(pasteSet.getCollection_set_id());
        mta.setCollection_id(pasteSet.getCollection_id());

        Meta_transfer_actionsFactory mtaf = new Meta_transfer_actionsFactory(pasteSet.getRockFactory(), mta);

        Vector tps = mtaf.get();
        Enumeration en = tps.elements();
        while (en.hasMoreElements()) {
          Meta_transfer_actions act = (Meta_transfer_actions) en.nextElement();

          Meta_transfer_actions actnew = new Meta_transfer_actions(act.getRockFactory());
          actnew.setVersion_number(sm.getVersion_number());
          actnew.setTransfer_action_id(getNextActionID(act.getRockFactory()));
          actnew.setCollection_id(sm.getCollection_id());
          actnew.setCollection_set_id(sm.getCollection_set_id());
          actnew.setAction_type(act.getAction_type());
          actnew.setTransfer_action_name(act.getTransfer_action_name());
          actnew.setOrder_by_no(act.getOrder_by_no());
          actnew.setDescription(act.getDescription());
          actnew.setWhere_clause(act.getWhere_clause());
          actnew.setAction_contents(act.getAction_contents());
          actnew.setEnabled_flag(act.getEnabled_flag());
          actnew.setConnection_id(act.getConnection_id());

          actnew.insertDB(false, false);
        }

      } catch (Exception e) {
        ErrorDialog ed = new ErrorDialog(frame, "Error", "Error creating actions to pasted set", e);
      }
    }

  }

  private Long getNextActionID(RockFactory rock) throws Exception {

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

  public boolean committed() {
    return committed;
  }

  private Long getLong(String str) {
    if (str == null || str.length() <= 0)
      return null;

    try {
      return new Long(str);
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

}
