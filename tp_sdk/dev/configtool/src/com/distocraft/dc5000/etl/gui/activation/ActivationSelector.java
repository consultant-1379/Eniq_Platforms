package com.distocraft.dc5000.etl.gui.activation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

/**
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class ActivationSelector extends JScrollPane {

  private List treeListeners = new ArrayList(5);

  private JPanel canvas;

  private JTree tree;

  private RockFactory dwhrepRockFactory;

  private String connectionID;

  private Tpactivation tpactivation;

  public ActivationSelector(String connectionID, RockFactory dwhrepRockFactory, Tpactivation tpactivation) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.connectionID = connectionID;
    this.dwhrepRockFactory = dwhrepRockFactory;

    canvas = new JPanel(new GridBagLayout());

    refresh(tpactivation);

    super.setViewportView(canvas);

  }

  public void refresh() {
    refresh(tpactivation);
  }

  public void refresh(Tpactivation tpactivation) {

    this.tpactivation = tpactivation;

    canvas.removeAll();

    GridBagConstraints c = new GridBagConstraints();

    String error = null;

    try {
      // Select all TPActivations from the database.
      Tpactivation whereTpactivation = new Tpactivation(this.dwhrepRockFactory);
      TpactivationFactory tpactivationFactory = new TpactivationFactory(this.dwhrepRockFactory, whereTpactivation);
      Vector allTpactivations = tpactivationFactory.get();

      Iterator allTpactivationsIterator = allTpactivations.iterator();

      if (allTpactivationsIterator.hasNext()) {
        // Create the root node for the tree.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.connectionID);

        DefaultTreeModel dtm = new DefaultTreeModel(root);

        tree = new JTree(dtm);
        tree.setShowsRootHandles(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new ActivationSelectorCellRenderer());

        // Parse through all TPActivations and set them to the tree (under the root) in alpahabetical order.
        while (allTpactivationsIterator.hasNext()) {
          Tpactivation newTpactivation = (Tpactivation) allTpactivationsIterator.next();

          DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(newTpactivation);

          String newTpactivationDescription = newTpactivation.getTechpack_name();

          int j = 0;
          // Find the place for the TPActivation in the tree.
          for (; j < root.getChildCount(); j++) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) root.getChildAt(j);

            Tpactivation currentTpactivation = (Tpactivation) tn.getUserObject();
            String currentTpactivationDescription = currentTpactivation.getTechpack_name();

            if (currentTpactivationDescription.compareToIgnoreCase(newTpactivationDescription) >= 0) {
              break;
            }

          }
          // Add TPActivation to the tree component.
          root.insert(dmt, j);

        }

      } else {
        // Create the root node for the tree.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.connectionID);

        DefaultTreeModel dtm = new DefaultTreeModel(root);

        tree = new JTree(dtm);
        tree.setShowsRootHandles(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new ActivationSelectorCellRenderer());
      }

      if (tree != null) {
        for (int i = 0; i < tree.getRowCount(); i++)
          tree.expandRow(i);
      }

      c.weightx = 1;
      c.weighty = 1;
      c.fill = GridBagConstraints.BOTH;
      c.anchor = GridBagConstraints.NORTHWEST;

      if (tree != null) {
        canvas.add(tree, c);
        Iterator i = treeListeners.iterator();
        while (i.hasNext()) {
          Object o = i.next();
          if (o instanceof TreeSelectionListener)
            tree.addTreeSelectionListener((TreeSelectionListener) o);
          if (o instanceof MouseListener)
            tree.addMouseListener((MouseListener) o);
        }
      }

      if (this.tpactivation != null) { // set preselection
        // TODO select selected node from meta tree

      }

    } catch (Exception e) {
      error = "Error listing ActivationSelector: " + e.getMessage();
      e.printStackTrace();
    }

    if (error != null) {
      c.weightx = 0;
      c.weighty = 0.5;
      c.fill = GridBagConstraints.VERTICAL;
      c.anchor = GridBagConstraints.NORTHWEST;
      canvas.add(Box.createRigidArea(new Dimension(5, 5)), c);

      c.gridx = 1;
      c.gridy = 1;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      canvas.add(new JLabel(error), c);

      c.gridx = 2;
      c.gridy = 2;
      c.weightx = 0;
      c.weighty = 0.5;
      c.fill = GridBagConstraints.VERTICAL;
      canvas.add(Box.createRigidArea(new Dimension(5, 5)), c);

    }

    canvas.invalidate();
    canvas.revalidate();
    canvas.repaint();

  }

  public void addTreeSelectionListener(TreeSelectionListener tsl) {
    treeListeners.add(tsl);
    if (tree != null)
      tree.addTreeSelectionListener(tsl);
  }

  public Tpactivation getSelectedTpactivation() {
    TreePath tp = tree.getSelectionPath();
    if (tp != null || tp.getPathCount() > 1 || tp.getLastPathComponent() instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dmtn.getUserObject() instanceof Tpactivation) {
        return (Tpactivation) dmtn.getUserObject();
      }
    }
    return null;
  }

  public void addMouseListener(MouseListener ml) {
    treeListeners.add(ml);
    if (tree != null)
      tree.addMouseListener(ml);

  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (tree != null)
      tree.setEnabled(enabled);
  }

}
