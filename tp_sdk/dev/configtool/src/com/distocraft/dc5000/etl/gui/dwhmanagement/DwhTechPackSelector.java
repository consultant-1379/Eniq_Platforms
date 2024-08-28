package com.distocraft.dc5000.etl.gui.dwhmanagement;

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

import com.distocraft.dc5000.etl.gui.activation.ActivationSelectorCellRenderer;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.DwhtechpacksFactory;
//import com.distocraft.dc5000.repository.dwhrep.Tpactivation;

/**
 * Copyright Distocraft 2006 <br>
 * <br>
 * $id$
 * 
 * @author berggren
 */
public class DwhTechPackSelector extends JScrollPane {

  private List treeListeners = new ArrayList(5);

  private JPanel canvas;

  private JTree tree;

  private RockFactory dwhrepRockFactory;

  private String connectionID;

  private Dwhtechpacks dwhTechPack;

  public DwhTechPackSelector(String connectionID, RockFactory dwhrepRockFactory, Dwhtechpacks DwhTechPack) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.connectionID = connectionID;
    this.dwhrepRockFactory = dwhrepRockFactory;

    canvas = new JPanel(new GridBagLayout());

    refresh(DwhTechPack);

    super.setViewportView(canvas);
  }

  public void refresh() {
    refresh(dwhTechPack);
  }

  public void refresh(Dwhtechpacks dwhTechPack) {

    this.dwhTechPack = dwhTechPack;

    canvas.removeAll();

    GridBagConstraints c = new GridBagConstraints();

    String error = null;

    try {
      // Select all DWHTechPacks from the database.
      Dwhtechpacks whereDwhTechPack = new Dwhtechpacks(this.dwhrepRockFactory);
      DwhtechpacksFactory dwhTechPackFactory = new DwhtechpacksFactory(this.dwhrepRockFactory, whereDwhTechPack);
      Vector allDwhTechPacks = dwhTechPackFactory.get();

      Iterator allDwhTechPacksIterator = allDwhTechPacks.iterator();

      if (allDwhTechPacksIterator.hasNext()) {
        // Create the root node for the tree.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.connectionID);

        DefaultTreeModel dtm = new DefaultTreeModel(root);

        tree = new JTree(dtm);
        tree.setShowsRootHandles(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new DwhTechPackSelectorCellRenderer());

        // Parse through all DwhTechPacks and set them to the tree (under the root) in alpahabetical order.
        while (allDwhTechPacksIterator.hasNext()) {
          Dwhtechpacks newDwhTechPack = (Dwhtechpacks) allDwhTechPacksIterator.next();

          DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(newDwhTechPack);

          String newDwhTechPackDescription = newDwhTechPack.getTechpack_name() + " " + newDwhTechPack.getVersionid();

          int j = 0;
          // Find the place for the DwhTechPack in the tree.
          for (; j < root.getChildCount(); j++) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) root.getChildAt(j);

            Dwhtechpacks currentDwhTechPack = (Dwhtechpacks) tn.getUserObject();
            String currentDwhTechPackDescription = currentDwhTechPack.getTechpack_name() + " " + currentDwhTechPack.getVersionid();

            if (currentDwhTechPackDescription.compareToIgnoreCase(newDwhTechPackDescription) >= 0) {
              break;
            }

          }
          // Add DwhTechPack to the tree component.
          root.insert(dmt, j);
        }
      } else {
        // Create the root node for the tree.
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.connectionID);

        DefaultTreeModel dtm = new DefaultTreeModel(root);

        tree = new JTree(dtm);
        tree.setShowsRootHandles(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new DwhTechPackSelectorCellRenderer());
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

      if (this.dwhTechPack != null) { // set preselection
        // TODO select selected node from meta tree

      }

    } catch (Exception e) {
      error = "Error listing DwhTechPackSelector: " + e.getMessage();
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

  public Dwhtechpacks getSelectedDwhTechPack() {
    TreePath tp = tree.getSelectionPath();
    if (tp != null || tp.getPathCount() > 1 || tp.getLastPathComponent() instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dmtn.getUserObject() instanceof Dwhtechpacks) {
        return (Dwhtechpacks) dmtn.getUserObject();
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
