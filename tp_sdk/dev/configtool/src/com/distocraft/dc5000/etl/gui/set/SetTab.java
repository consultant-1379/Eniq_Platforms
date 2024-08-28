package com.distocraft.dc5000.etl.gui.set;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.HostNode;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.SystemStructureListener;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class SetTab extends JPanel implements Tab, TreeSelectionListener, MouseListener, SystemStructureListener {

  private JFrame frame;

  private SystemStructureNotificate ssn;

  private String connID;

  private UI ui;

  private JSplitPane split;

  private TechPackTree tpTree;

  private ActionTable aTable;

  private JButton wizard;

  private RockFactory rock;

  private RockFactory dwhrepRock;

  private Log log;

  public SetTab(JFrame frame, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.frame = frame;
    this.ssn = ssn;
    this.ui = ui;

    ssn.addSystemStructureListener(this);

    disconnected();

    log = LogFactory.getLog("SetTab");
  }

  public void connected(RockFactory rock, RockFactory dwhrepRock, String connectionID) {
    this.rock = rock;
    this.dwhrepRock = dwhrepRock;
    this.connID = connectionID;

    try {

      tpTree = new TechPackTree(rock, this, frame, connID);
      ssn.addSystemStructureListener(tpTree);
      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tpTree, new JPanel());

      this.removeAll();

      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 1;
      this.add(split, c);

      this.invalidate();
      this.revalidate();
      this.repaint();

    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
      disconnected();
    }
  }

  public class RemoveTechPackWorker extends SwingWorker {

    private TechPackNode tpn = null;

    public RemoveTechPackWorker(TechPackNode tpn) {
      this.tpn = tpn;

      ui.startOperation("Removing techpack...");

    }

    public Object construct() {
      if (tpn != null) {
        try {
          tpTree.removeTechPack(tpn);
        } catch (Exception e) {
          return e;
        }
      }

      return null;

    }

    public void finished() {

      ssn.techPackChange();

      int loc = split.getDividerLocation();
      split.setRightComponent(new JPanel());
      split.setDividerLocation(loc);

      if (get() != null)
        new ErrorDialog(frame, "Error", "Unable to remove TechPack", (Exception) get());

      ui.endOperation();
    }

  };

  public class NewTechPackAction extends AbstractAction {

    private TypeNode tn;

    private boolean create = false;

    public NewTechPackAction(TypeNode tn, String type) {
      this.tn = tn;
    }

    public void actionPerformed(ActionEvent a) {

      final JDialog jd = new JDialog(frame, true);
      jd.setTitle("New TechPack");

      try {

        Container con = jd.getContentPane();
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
        con.add(new JLabel("Define Parameters of new TechPack"), c);

        c.gridy = 2;
        con.add(Box.createRigidArea(new Dimension(5, 5)), c);

        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("ID"), c);

        Long techPackID = TechPackTree.getNextTechPackID(rock);

        log.warn("New TechPack with ID " + techPackID);

        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JLabel tid = new JLabel(techPackID.toString());
        con.add(tid, c);

        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("Name"), c);

        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JTextField name = new JTextField(20);
        con.add(name, c);

        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("Description"), c);

        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        JTextField description = new JTextField(20);
        con.add(description, c);

        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("Version"), c);

        c.gridx = 2;
        JTextField version = new JTextField(10);
        con.add(version, c);

        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("Status"), c);

        c.gridx = 2;
        JComboBox enstatus = new JComboBox(ConfigTool.STATUSES);
        enstatus.setSelectedIndex(0);
        con.add(enstatus, c);

        c.gridx = 1;
        c.gridy = 8;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(new JLabel("Type"), c);

        c.gridx = 2;
        JComboBox tptype = new JComboBox(ConfigTool.TECHPACK_TYPES);
        enstatus.setSelectedIndex(0);
        con.add(tptype, c);

        if (tn != null) {
          for (int i = 0; i < ConfigTool.TECHPACK_TYPES.length; i++) {
            if (ConfigTool.TECHPACK_TYPES[i].equals(tn.getType()))
              tptype.setSelectedIndex(i);
          }
        }

        c.gridy = 9;
        c.gridx = 3;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        con.add(Box.createRigidArea(new Dimension(5, 5)), c);

        c.gridy = 10;
        c.gridx = 1;
        JButton discard = new JButton("Cancel", ConfigTool.delete);
        discard.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            jd.setVisible(false);
          }
        });
        con.add(discard, c);

        c.gridx = 2;
        JButton save = new JButton("Create", ConfigTool.newIcon);
        save.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            create = true;
            jd.setVisible(false);
          }
        });
        con.add(save, c);

        jd.pack();
        jd.setVisible(true);

        if (!create)
          return;

        Meta_collection_sets tpm = new Meta_collection_sets(rock);
        tpm.setCollection_set_id(techPackID);
        tpm.setCollection_set_name(name.getText().trim());
        tpm.setDescription(description.getText().trim());
        tpm.setVersion_number(version.getText().trim());
        if (enstatus.getSelectedIndex() == 0) {
          tpm.setEnabled_flag("Y");
        } else {
          tpm.setEnabled_flag("N");
        }
        tpm.setType((String) tptype.getSelectedItem());

        tpm.insertDB(false, false);

        tpTree.addTechPack(tn, tpm);
        ssn.techPackChange();

      } catch (Exception e) {
        ErrorDialog ed = new ErrorDialog(frame, "Error", "Unable to create TechPack", e);
        jd.setVisible(false);
      }

    }
  };

  public class RemoveSetWorker extends SwingWorker {

    private SetNode sn = null;

    private RemoveSetWorker(SetNode sn) {
      this.sn = sn;

      int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove " + sn.getSetName() + "?",
          "Removing Set", JOptionPane.YES_NO_OPTION);

      if (n != 0) {
        this.sn = null;
      } else {
        ui.startOperation("Removing set...");
      }

    }

    public Object construct() {
      if (sn != null) {

        try {
          tpTree.removeSet(sn);
        } catch (Exception e) {
          return e;
        }

      }

      return null;
    }

    public void finished() {
      Exception e = (Exception) get();

      if (e != null)
        new ErrorDialog(frame, "Error", "Unable to remove set", e);

      ssn.setChange();

      int loc = split.getDividerLocation();
      split.setRightComponent(new JPanel());
      split.setDividerLocation(loc);

      ui.endOperation();

    }

  };

  public class SetWizardAction extends AbstractAction {

    private Meta_collection_sets tp = null;

    public SetWizardAction(TechPackNode tpn) {
      if (tpn != null)
        this.tp = tpn.getTechPack();
    }

    public void actionPerformed(ActionEvent ae) {

      int loc = split.getDividerLocation();

      SetWizardDialog swd = new SetWizardDialog(frame, connID, dwhrepRock, rock, tp, ui);

      try {
        tpTree.refresh();
        ssn.techPackChange();
        ssn.setChange();
      } catch (Exception e) {
      }

      split.setRightComponent(new JPanel());

      split.setDividerLocation(loc);

    }

  };

  public class AlarmSetWizardAction extends AbstractAction {

    private Meta_collection_sets tp = null;

    public AlarmSetWizardAction(TechPackNode tpn) {
      if (tpn != null)
        this.tp = tpn.getTechPack();
    }

    public void actionPerformed(ActionEvent ae) {

      int loc = split.getDividerLocation();

      new AlarmInterfaceWizardDialog(frame, connID, dwhrepRock, rock, tp, ui);

      try {
        tpTree.refresh();
        ssn.techPackChange();
        ssn.setChange();
      } catch (Exception e) {
      }

      split.setRightComponent(new JPanel());

      split.setDividerLocation(loc);

    }

  };

  public class PasteSetAction extends AbstractAction {

    private TechPackNode tpn;

    public PasteSetAction(TechPackNode tpn) {
      this.tpn = tpn;
    }

    public void actionPerformed(ActionEvent a) {
      if (tpTree.getSetClipboard() != null) {
        SetWindow sw = new SetWindow(frame, rock, tpTree, tpn, null, tpTree.getSetClipboard());
        ssn.setChange();
      }
    }

  };

  public void valueChanged(TreeSelectionEvent e) {

    TreePath path = e.getPath();

    TechPackTreeNode tptn = (TechPackTreeNode) path.getLastPathComponent();

    JComponent jt = tptn.getTable();

    int loc = split.getDividerLocation();

    split.setRightComponent(jt);

    split.setDividerLocation(loc);

  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {

    JTree comp = (JTree) e.getComponent();
    final TreePath tp = comp.getClosestPathForLocation(e.getX(), e.getY());

    if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup

      if (tp.getLastPathComponent() instanceof HostNode) { // Host-level menu
        HostNode host = (HostNode) tp.getLastPathComponent();

        JPopupMenu pop = new JPopupMenu("Host");

        // JMenuItem atp = new JMenuItem("Create a Package", ConfigTool.newIcon);
        // atp.addActionListener(new NewTechPackAction(host, null));
        // pop.add(atp);

        JMenuItem rf = new JMenuItem("Refresh Tree", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            try {
              setChange();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
        pop.add(rf);
        pop.show(e.getComponent(), e.getX(), e.getY());

      } else if (tp.getLastPathComponent() instanceof TypeNode) { // TP Type-level

        TypeNode tn = (TypeNode) tp.getLastPathComponent();
        String type = tn.getType();

        JPopupMenu pop = new JPopupMenu("Host");
        JMenuItem atp = new JMenuItem("Create a Package", ConfigTool.newIcon);
        atp.addActionListener(new NewTechPackAction(tn, type));
        pop.add(atp);
        if (type.equals("Techpack") || type.equals("Custompack")) {
          JMenuItem wi = new JMenuItem("Set Wizard", ConfigTool.bulb);
          wi.addActionListener(new SetWizardAction(null));
          pop.add(wi);
        }
        pop.show(e.getComponent(), e.getX(), e.getY());

      } else if (tp.getLastPathComponent() instanceof TechPackNode) { // TP-level
        // menu
        final TechPackNode tpn = (TechPackNode) tp.getLastPathComponent();
        JPopupMenu pop = new JPopupMenu("TechPack");
        JMenuItem etp = new JMenuItem("Edit", ConfigTool.draw);
        etp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            Meta_collection_sets tp = tpn.getTechPack();
            TechPackWindow sw = new TechPackWindow(frame, rock, tp);
            if (sw.committed())
              ssn.techPackChange();
          }
        });
        pop.add(etp);
        JMenuItem rtp = new JMenuItem("Remove this Package", ConfigTool.delete);
        rtp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            int n = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove " + tpn.getTechPackName()
                + " version " + tpn.getTechPackVersion() + "?", "Removing TechPack", JOptionPane.YES_NO_OPTION);

            if (n != 0) {
              return;
            }
            RemoveTechPackWorker rtmw = new RemoveTechPackWorker(tpn);
            rtmw.start();
          }
        });
        pop.add(rtp);
        JMenuItem atp = new JMenuItem("Create a Set", ConfigTool.newIcon);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            SetWindow sw = new SetWindow(frame, rock, tpTree, tpn, null, null);
            ssn.setChange();
          }
        });
        pop.add(atp);
        JMenuItem pas = new JMenuItem("Paste Set", ConfigTool.paste);
        pas.setEnabled(tpTree.getSetClipboard() != null);
        pas.addActionListener(new PasteSetAction(tpn));
        pop.add(pas);

        if (tpn.getTechPack().getCollection_set_name().equalsIgnoreCase("AlarmInterfaces")) {
          // Show the selection for the special Alarm Interface Wizard.
          JMenuItem alarmInterfaceWizardMenuItem = new JMenuItem("Alarm Interface Wizard", ConfigTool.bulb);
          alarmInterfaceWizardMenuItem.addActionListener(new AlarmSetWizardAction((TechPackNode) tp
              .getLastPathComponent()));
          pop.add(alarmInterfaceWizardMenuItem);

        } 
        pop.show(e.getComponent(), e.getX(), e.getY());

      } else if (tp.getLastPathComponent() instanceof SetNode) { // Set-level
        // menu
        final SetNode sn = (SetNode) tp.getLastPathComponent();
        JPopupMenu pop = new JPopupMenu("Set");
        JMenuItem ed = new JMenuItem("Edit", ConfigTool.draw);
        ed.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            Object[] path = tp.getPath();
            TechPackNode tpn = (TechPackNode) path[path.length - 2];

            SetWindow sw = new SetWindow(frame, rock, tpTree, tpn, sn.getSet(), null);
            if (sw.committed())
              ssn.setChange();
          }
        });
        pop.add(ed);
        JMenuItem rtp = new JMenuItem("Remove this Set", ConfigTool.delete);
        rtp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            RemoveSetWorker rsw = new RemoveSetWorker(sn);
            rsw.start();
          }
        });
        pop.add(rtp);

        JMenuItem cop = new JMenuItem("Copy Set", ConfigTool.copy);
        cop.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            tpTree.setSetClipboard(sn.getSet());
          }
        });
        pop.add(cop);
        pop.show(e.getComponent(), e.getX(), e.getY());
      }

    }
  }

  public void mousePressed(MouseEvent e) {
  }

  public void metaDataChange() {
  }

  public void techPackChange() {
  }

  public void setChange() {
    try {
      tpTree.clearSelection();
      tpTree.refresh();
      split.setRightComponent(new JPanel());

      this.invalidate();
      this.revalidate();
      this.repaint();
    } catch (Exception e) {
    }
  }

  public void disconnected() {
    this.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.VERTICAL;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 0.5;

    this.add(Box.createRigidArea(new Dimension(20, 20)), c);

    c.weightx = 1;
    c.weighty = 0;
    c.gridx = 1;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    this.add(new JLabel("Disconnected"), c);

    c.gridy = 2;
    this.add(new JLabel("Select Connection-Connect to connect"), c);

    c.fill = GridBagConstraints.VERTICAL;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weighty = 0.5;
    c.gridx = 2;
    c.gridy = 3;
    this.add(Box.createRigidArea(new Dimension(20, 20)), c);

    this.invalidate();
    this.validate();
    this.repaint();

  }
}
