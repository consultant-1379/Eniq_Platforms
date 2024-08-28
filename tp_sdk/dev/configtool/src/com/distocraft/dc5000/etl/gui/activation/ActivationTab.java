package com.distocraft.dc5000.etl.gui.activation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.SwingWorker;
import com.distocraft.dc5000.etl.gui.SystemStructureNotificate;
import com.distocraft.dc5000.etl.gui.Tab;
import com.distocraft.dc5000.etl.gui.UI;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;

/**
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class ActivationTab extends JPanel implements Tab, MouseListener {

  private RockFactory dwhrepRockFactory;

  private UI ui;

  private JFrame frame;

  private JSplitPane split;

  public ActivationSelector activationSelector;

  public TypeActivationTable table = null;

  private RockFactory rockFactory = null;

  private String connectionId = null;

  /**
   * Constructor of this tab.
   * @param frame
   * @param rock
   * @param ssn
   * @param ui
   */
  public ActivationTab(JFrame frame, RockFactory rock, SystemStructureNotificate ssn, UI ui) {
    super(new GridBagLayout());

    this.ui = ui;
    this.frame = frame;

    this.disconnected();
  }

  /**
   * Just a wrapper to refresh the whole tab.
   *
   */
  public void refresh() {
    connected(this.rockFactory, this.dwhrepRockFactory, this.connectionId);
  }

  /**
   * This method shows the contents of the tab when the configtool is succesfully connected.
   * @param rockFactory Rockfactory to etlrep.
   * @param dwhrepRock Rockfactory to dwhrep.
   * @param connectionId Id of the connection.
   */
  public void connected(RockFactory rockFactory, RockFactory dwhrepRock, String connectionId) {

    this.dwhrepRockFactory = dwhrepRock;
    this.rockFactory = rockFactory;
    this.connectionId = connectionId;

    this.removeAll();

    JPanel left = new JPanel(new GridBagLayout());

    GridBagConstraints lc = new GridBagConstraints();
    lc.fill = GridBagConstraints.NONE;
    lc.anchor = GridBagConstraints.NORTHWEST;
    lc.weightx = 0;
    lc.weighty = 0;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridx = 1;
    lc.gridy = 1;
    left.add(new JLabel("Available techpacks"), lc);

    lc.gridy = 2;
    lc.gridx = 2;
    left.add(Box.createRigidArea(new Dimension(5, 5)), lc);

    lc.gridy = 3;
    lc.gridx = 0;
    lc.gridwidth = 3;
    lc.fill = GridBagConstraints.BOTH;
    lc.weightx = 1;
    lc.weighty = 1;

    this.activationSelector = new ActivationSelector(connectionId, this.dwhrepRockFactory, null);
    this.activationSelector.addMouseListener(this);
    this.activationSelector.addTreeSelectionListener(new ActivationSelectorListener(this.dwhrepRockFactory));
    left.add(this.activationSelector, lc);

    JPanel right = new JPanel(new GridBagLayout());

    GridBagConstraints rc = new GridBagConstraints();
    rc.fill = GridBagConstraints.NONE;
    rc.anchor = GridBagConstraints.NORTHWEST;
    rc.weightx = 0;
    rc.weighty = 0;

    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    rc.gridx = 1;
    rc.gridy = 1;
    rc.insets = new Insets(2, 2, 5, 2);
    right.add(new JLabel("Status of techpack"), rc);
    rc.gridy = 10;
    rc.gridx = 5;
    rc.weightx = 0;
    rc.weighty = 0;
    rc.gridwidth = 1;
    rc.fill = GridBagConstraints.NONE;
    right.add(Box.createRigidArea(new Dimension(10, 10)), rc);

    split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    this.add(split, c);

    this.invalidate();
    this.validate();
    this.repaint();
  }

  /**
   * This method removes all components from the container and shows the notification for connection.
   */
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

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    JTree comp = (JTree) e.getComponent();
    final TreePath tp = comp.getClosestPathForLocation(e.getX(), e.getY());

    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

    // The selected object is the top level connection, where a new Tpactiovation can be created.
    if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup
      if (!(dmtn.getUserObject() instanceof Tpactivation)) {

        JPopupMenu pop = new JPopupMenu("Activation");
        JMenuItem atp = new JMenuItem("Add new Activation", ConfigTool.newIcon);
        atp.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            // A new activation is created, set the last parameter as null.
            ActivationWindow aw = new ActivationWindow(frame, ActivationTab.this.dwhrepRockFactory, null, ui,
                activationSelector, table);
            if (activationSelector != null)
              activationSelector.refresh();
            if (table != null)
              table.refresh();
          }
        });
        pop.add(atp);
        JMenuItem rf = new JMenuItem("Refresh tree", ConfigTool.refresh);
        rf.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent ae) {
            if (activationSelector != null)
              activationSelector.refresh();
          }
        });
        pop.add(rf);
        pop.show(e.getComponent(), e.getX(), e.getY());
      } else {
        // The selected object is an existing activation.
        final Tpactivation selectedTpactivation = (Tpactivation) dmtn.getUserObject();

        if (e.getButton() == MouseEvent.BUTTON3) { // left click -> open popup

          JPopupMenu pop = new JPopupMenu("Activation");

          JMenuItem editActivation = new JMenuItem("Edit", ConfigTool.draw);
          editActivation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
              // Edit the selected activation.
              ActivationWindow aw = new ActivationWindow(frame, ActivationTab.this.dwhrepRockFactory,
                  selectedTpactivation, ui, activationSelector, table);
              if (activationSelector != null)
                activationSelector.refresh();
              if (table != null)
                table.refresh();

            }
          });
          pop.add(editActivation);

          JMenuItem rmd = new JMenuItem("Remove this Activation", ConfigTool.delete);
          rmd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
              RemoveActivationWorker removeActivationWorker = new RemoveActivationWorker(selectedTpactivation);
              removeActivationWorker.start();

            }
          });

          pop.add(rmd);

          JMenuItem rf = new JMenuItem("Refresh tree", ConfigTool.refresh);
          rf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
              if (activationSelector != null)
                activationSelector.refresh();
            }
          });
          pop.add(rf);

          pop.show(e.getComponent(), e.getX(), e.getY());

        }
        return;
      }
    }
  }

  public class ActivationSelectorListener implements TreeSelectionListener {

    private RockFactory dwhrepRockFactory;

    public ActivationSelectorListener(RockFactory dwhrepRockFactory) {
      this.dwhrepRockFactory = dwhrepRockFactory;
    }

    public void valueChanged(TreeSelectionEvent tse) {

      if (tse == null) {
        split.setRightComponent(new JPanel());
        return;

      }
      TreePath tp = tse.getPath();

      if (tp == null) {
        split.setRightComponent(new JPanel());
        return;
      }

      DefaultMutableTreeNode dtm = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dtm.getUserObject() instanceof Tpactivation) {
        Tpactivation tpactivation = (Tpactivation) dtm.getUserObject();

        JPanel right = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 6);

        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 1;
        c.gridy = 1;
        right.add(new JLabel("Techpack"), c);

        c.gridx = 2;
        right.add(new JLabel(tpactivation.getTechpack_name()), c);

        c.gridx = 1;
        c.gridy = 2;
        right.add(new JLabel("Status"), c);

        c.gridx = 2;

        if (tpactivation.getStatus().toString().equalsIgnoreCase("ACTIVE"))
          right.add(new JLabel("ACTIVE"), c);
        else
          right.add(new JLabel("INACTIVE"), c);

        c.gridx = 1;
        c.gridy = 3;
        right.add(new JLabel("VersionID"), c);

        c.gridx = 2;
        right.add(new JLabel(tpactivation.getVersionid()), c);

        c.gridx = 1;
        c.gridy = 4;
        right.add(new JLabel("Type"), c);

        c.gridx = 2;
        right.add(new JLabel(tpactivation.getType()), c);

        right.add(Box.createRigidArea(new Dimension(10, 5)), c);

        c.gridx = 1;
        c.gridy = 5;
        right.add(Box.createRigidArea(new Dimension(10, 20)), c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);

        table = new TypeActivationTable(tpactivation, this.dwhrepRockFactory, frame);
        right.add(table, c);

        try {
          split.setRightComponent(right);

        } catch (Exception e) {
          e.printStackTrace();
        }

      } else {
        split.setRightComponent(new JPanel());
      }

      if (ActivationTab.this.table == null) {
        split.setRightComponent(new JPanel());
      }

    }
  };

  public class RemoveActivationWorker extends SwingWorker {

    Tpactivation selectedTpactivation = null;

    public RemoveActivationWorker(Tpactivation selectedTpactivation) {
      ui.startOperation("Removing activation...");
      this.selectedTpactivation = selectedTpactivation;
    }

    public Object construct() {

      try {
        // First delete the TypeActivations of this Tpactivation.
        Typeactivation whereTypeActivation = new Typeactivation(ActivationTab.this.dwhrepRockFactory);
        whereTypeActivation.setTechpack_name(this.selectedTpactivation.getTechpack_name());
        TypeactivationFactory typeActivationFactory = new TypeactivationFactory(ActivationTab.this.dwhrepRockFactory,
            whereTypeActivation);
        Vector targetTypeActivations = typeActivationFactory.get();
        Iterator targetTypeActivationsIterator = targetTypeActivations.iterator();
        while (targetTypeActivationsIterator.hasNext()) {
          Typeactivation targetTypeActivation = (Typeactivation) targetTypeActivationsIterator.next();
          // Delete this TypeActivation.
          targetTypeActivation.deleteDB();
        }

        // Now all the TypeActivations of this TPActivation has been deleted, so this TPActivation can also be deleted.
        this.selectedTpactivation.deleteDB();

      } catch (RockException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      
      ConfigTool.reloadConfig();

      return null;
    }

    public void finished() {

      ActivationTab.this.refresh();

      ui.endOperation();
    }

  }

}
