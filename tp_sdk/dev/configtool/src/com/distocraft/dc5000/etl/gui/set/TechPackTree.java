package com.distocraft.dc5000.etl.gui.set;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.gui.HostNode;
import com.distocraft.dc5000.etl.gui.SystemStructureListener;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class TechPackTree extends JPanel implements SystemStructureListener {

  private RockFactory rock;

  private SetTab st;

  private JFrame frame;

  private String connectionID;

  private JTree tree = null;

  private DefaultTreeModel model = null;

  private Meta_transfer_actions[] actionClipboard = null;

  private Meta_collections setClipboard = null;

  /**
   * Constructs TechPackTree. This must be executed outside event dispatching
   * thread.
   * 
   * @param rock
   */
  public TechPackTree(RockFactory rock, SetTab st, JFrame frame, String connectionID) throws Exception {
    super(new GridBagLayout());

    this.rock = rock;
    this.st = st;
    this.frame = frame;
    this.connectionID = connectionID;

    refresh();

  }

  public void metaDataChange() {
  }

  public void techPackChange() {
    try {
      refresh();
    } catch (Exception e) {
    }
  }

  public void setChange() {
    try {
      refresh();
    } catch (Exception e) {
    }
  }

public void refresh() throws Exception {

    Enumeration expanded = null;

    if (tree != null) {
      TreePath tp = new TreePath(model.getRoot());
      expanded = tree.getExpandedDescendants(tp);
    }

    this.removeAll();

    HostNode root = new HostNode(connectionID);

    model = new DefaultTreeModel(root);

    Meta_collection_sets m = new Meta_collection_sets(rock);
    Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(rock, m);

    HashMap typeNodes = new HashMap();

    TypeNode in = new TypeNode("Interface");
    root.add(in);
    typeNodes.put("Interface", in);

    TypeNode tpw = new TypeNode("Techpack");
    root.add(tpw);
    typeNodes.put("Techpack", tpw);

    Vector tps = mcsf.get();
    Enumeration en = tps.elements();
    while (en.hasMoreElements()) {
      Meta_collection_sets mcs = (Meta_collection_sets) en.nextElement();

      TechPackNode tpn = new TechPackNode(mcs, rock, this, frame);

      String type = mcs.getType();
      if (type == null || type.length() <= 0)
        type = "Other";

      TypeNode tn_parent = (TypeNode) typeNodes.get(type);

      if (tn_parent == null) { // Parent not already added

        tn_parent = new TypeNode(type);
        typeNodes.put(type, tn_parent);

        String par_uo = (String) tn_parent.getUserObject();

        int j = 0;
        for (; j < root.getChildCount(); j++) {
          TypeNode tn = (TypeNode) root.getChildAt(j);

          String tn_uo = (String) tn.getUserObject();

          if (tn_uo.compareToIgnoreCase(par_uo) >= 0) {
            break;
          }

        }

        root.insert(tn_parent, j);

      }

      String tpn_uo = (String) tpn.getUserObject();

      int i = 0;
      for (; i < tn_parent.getChildCount(); i++) {
        TechPackNode tpx = (TechPackNode) tn_parent.getChildAt(i);
        String tpx_uo = (String) tpx.getUserObject();

        if (tpx_uo.compareToIgnoreCase(tpn_uo) >= 0) {
          break;
        }

      }

      tn_parent.insert(tpn, i);

    }

    tree = new JTree(model);
    tree.setShowsRootHandles(true);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(st);
    tree.addMouseListener(st);
    tree.setCellRenderer(new TechPackTreeCellRenderer());

    if (expanded != null) {
      try {
        
        tree.expandPath(new TreePath(new Object[] { root }));
        
        while (expanded.hasMoreElements()) {
          TreePath tpp = (TreePath) expanded.nextElement();
          Object[] path = tpp.getPath();
          
          if(path.length < 2)
            continue;
          
          DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)path[1];
          
          boolean f1 = false;
          Enumeration i1 = root.children();
          
          DefaultMutableTreeNode d1 = null;
          
          while(i1.hasMoreElements()) {
            d1 = (DefaultMutableTreeNode)i1.nextElement();
            
            if(d1.getUserObject().equals(n1.getUserObject())) {
              tree.expandPath(new TreePath(new Object[] { root,d1 }));
              f1 = true;
              break;
            }
            
          }
          
          if(!f1)
            continue;
          
          if(path.length < 3)
            continue;
          
          DefaultMutableTreeNode n2 = (DefaultMutableTreeNode)path[2];
          
          boolean f2 = false;
          Enumeration i2 = d1.children();
          
          DefaultMutableTreeNode d2 = null;
          
          while(i2.hasMoreElements()) {
            d2 = (DefaultMutableTreeNode)i2.nextElement();
            
            if(d2.getUserObject().equals(n2.getUserObject())) {
              tree.expandPath(new TreePath(new Object[] { root,d1,d2 }));
              f2 = true;
              break;
            }
            
          }
          
        }
          
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    JScrollPane scroll = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;

    this.add(scroll, c);

    invalidate();
    revalidate();
    repaint();

  }  public void clearSelection() {
    tree.clearSelection();
  }

  public static Long getNextTechPackID(RockFactory rock) throws Exception {

    Meta_collection_sets csw = new Meta_collection_sets(rock);
    Meta_collection_setsFactory csf = new Meta_collection_setsFactory(rock, csw);

    long largest = -1;
    Vector dbVec = csf.get();
    for (int i = 0; i < dbVec.size(); i++) {
      Meta_collection_sets mc = (Meta_collection_sets) dbVec.elementAt(i);
      if (largest < mc.getCollection_set_id().longValue())
        largest = mc.getCollection_set_id().longValue();
    }

    System.out.println("new TechPackID " + (largest + 1));

    return new Long(largest + 1);

  }

  public void addTechPack(TypeNode tn, Meta_collection_sets mcs) throws Exception {
    TechPackNode tpn = new TechPackNode(mcs, rock, this, frame);
    model.insertNodeInto(tpn, tn, tn.getChildCount());
  }

  public void removeTechPack(TechPackNode tpn) throws Exception {
    tpn.deleteTechPack();
    model.removeNodeFromParent(tpn);
  }

  public void addSet(TechPackNode tpn, Meta_collections set) throws Exception {
    SetNode sn = new SetNode(set, tpn.getMeta(), rock, this, frame);
    model.insertNodeInto(sn, tpn, tpn.getChildCount());
  }

  public void removeSet(SetNode sn) throws Exception {
    sn.deleteSet();
    model.removeNodeFromParent(sn);
  }

  public TreePath getSelectionPath() {
    return tree.getSelectionPath();
  }

  public Meta_transfer_actions[] getActionClipboard() {
    return actionClipboard;
  }

  public void setActionClipboard(Meta_transfer_actions[] mta) {
    actionClipboard = mta;
  }

  public Meta_collections getSetClipboard() {
    return setClipboard;
  }

  public void setSetClipboard(Meta_collections mc) {
    setClipboard = mc;
  }

}
