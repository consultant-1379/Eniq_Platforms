package com.distocraft.dc5000.etl.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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



import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;


/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class MetaSelector extends JScrollPane {

  private List treeListeners = new ArrayList(5);

  private JPanel canvas;

  private JTree tree;

  private String connID;
  private RockFactory dwhrepRock;
  private Meta_collection_sets xtp;

  public MetaSelector(RockFactory dwhrepRock,String connID, Meta_collection_sets tp) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.connID = connID;
    this.dwhrepRock = dwhrepRock;
    canvas = new JPanel(new GridBagLayout());

    refresh(tp);

    super.setViewportView(canvas);

  }

  public void refresh() {
    refresh(xtp);
  }

  public void refresh(Meta_collection_sets tp) {

    this.xtp = tp;

    canvas.removeAll();

    GridBagConstraints c = new GridBagConstraints();

    String error = null;

    try {
      
      
      //Query query = s.createQuery("from Versioning order by VERSIONID");
      //query.setCacheMode(CacheMode.REFRESH);
      //List qList = query.list();
      
      Versioning aver = new Versioning(dwhrepRock);
      VersioningFactory averf= new VersioningFactory(dwhrepRock,aver);
      List qList = averf.get();
  
      if (qList != null && qList.size() > 0) {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(connID);

        DefaultTreeModel dtm = new DefaultTreeModel(root);

        tree = new JTree(dtm);
        tree.setShowsRootHandles(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new MetaSelectorCellRenderer());

        Iterator it = qList.iterator();
        while (it.hasNext()) {
          Versioning ver = (Versioning) it.next();
          DefaultMutableTreeNode dmt = new DefaultMutableTreeNode(ver);
          
          String ne_uo = ver.getTechpack_name()+" "+ver.getTechpack_version();

          int j = 0;
          for (; j < root.getChildCount(); j++) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) root.getChildAt(j);

            Versioning tnv = (Versioning)tn.getUserObject();
            String tn_uo = tnv.getTechpack_name()+" "+tnv.getTechpack_version();

            if (tn_uo.compareToIgnoreCase(ne_uo) >= 0) {
              break;
            }

          }

          root.insert(dmt, j);
        }

        for (int i = 0; i < tree.getRowCount(); i++)
          tree.expandRow(i);

        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;

        canvas.add(tree, c);

        Iterator i = treeListeners.iterator();
        while (i.hasNext()) {
          Object o = i.next();
          if (o instanceof TreeSelectionListener)
            tree.addTreeSelectionListener((TreeSelectionListener) o);
          if (o instanceof MouseListener)
            tree.addMouseListener((MouseListener) o);
        }

        if (tp != null) { // set preselection

          // TODO select selected node from meta tree

        }

      } else {
        error = "No metadata available";
      }

    } catch (Exception e) {
      error = "Error listing metadata: " + e.getMessage();
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

  public Versioning getSelectedMeta() {
    TreePath tp = tree.getSelectionPath();
    if (tp != null || tp.getPathCount() > 1 || tp.getLastPathComponent() instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp.getLastPathComponent();

      if (dmtn.getUserObject() instanceof Versioning) {
        return (Versioning) dmtn.getUserObject();
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
