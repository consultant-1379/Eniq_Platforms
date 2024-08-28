package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.ErrorDialog;
import com.distocraft.dc5000.etl.gui.SystemStructureListener;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;

public class InterfaceTree extends JScrollPane implements SystemStructureListener {

  private String connID;
  private RockFactory dwhrepRock;
  
  private JFrame frame;

  private JPanel canvas;

  private JTree tree;

  private List listen = new ArrayList(2);

  private List mlisten = new ArrayList(2);

  public InterfaceTree(String connID, RockFactory dwhrepRock, JFrame frame) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.connID = connID;
    this.dwhrepRock = dwhrepRock;
    this.frame = frame;

    this.canvas = new JPanel(new GridBagLayout());
    
    refresh();

  }

  public void refresh() {

    canvas.removeAll();

    GridBagConstraints c = new GridBagConstraints();

    String error = null;

    try {

      try {
        dwhrepRock.getConnection().commit();
      } catch (Exception e) {
      }
         
      //Query query = ses.createQuery("from DataInterface");
      //query.setCacheMode(CacheMode.REFRESH);
      //List qList = query.list();

      Datainterface adi = new Datainterface(dwhrepRock);
      DatainterfaceFactory aimf = new DatainterfaceFactory(dwhrepRock,adi);
      List qList = aimf.get();
      
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(connID);

      DefaultTreeModel dtm = new DefaultTreeModel(root);

      tree = new JTree(dtm);
      tree.setShowsRootHandles(false);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.setCellRenderer(new InterfaceTreeCellRenderer());

      Iterator li = listen.iterator();
      while(li.hasNext()) {
        TreeSelectionListener tsl = (TreeSelectionListener)li.next();
        tree.addTreeSelectionListener(tsl);
      }
      
      Iterator mi = mlisten.iterator();
      while(mi.hasNext()) {
        MouseListener ml = (MouseListener)mi.next();
        tree.addMouseListener(ml);
      }
      
      if (qList != null && qList.size() > 0) {
        Iterator it = qList.iterator();
        while (it.hasNext()) {
          Datainterface di = (Datainterface) it.next();

          InterfaceTreeNode itn = new InterfaceTreeNode(di);

          int i = 0;
          for (; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode tpx = (DefaultMutableTreeNode) root.getChildAt(i);
            String stpx = (String) tpx.getUserObject();
            String stpn = (String) itn.getUserObject();

            if (stpx.compareToIgnoreCase(stpn) >= 0) {
              break;
            }

          }

          root.insert(itn, i);

        }

      }

      for (int i = 0; i < tree.getRowCount(); i++)
        tree.expandRow(i);

      try {
        dwhrepRock.getConnection().commit();
      } catch (Exception e) {
      }

      c.weightx = 1;
      c.weighty = 1;
      c.fill = GridBagConstraints.BOTH;
      c.anchor = GridBagConstraints.NORTHWEST;

      canvas.add(tree, c);

    } catch (Exception e) {
      error = "Error listing Interfaces: " + e.getMessage();
    }

    if (error != null) {
      canvas.removeAll();

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

    super.setViewportView(canvas);

  }

  public void addTreeSelectionListener(TreeSelectionListener tsl) {
    listen.add(tsl);
    if (tree != null)
      tree.addTreeSelectionListener(tsl);
  }

  public void addMouseListener(MouseListener ml) {
    mlisten.add(ml);
    if (tree != null)
      tree.addMouseListener(ml);
  }

  public void metaDataChange() {
  }

  public void techPackChange() {
  }

  public void setChange() {
  }

}
