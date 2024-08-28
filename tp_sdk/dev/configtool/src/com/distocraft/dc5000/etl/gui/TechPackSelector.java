package com.distocraft.dc5000.etl.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collection_setsFactory;

/**
 * Copyright Distocraft 2005 <br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class TechPackSelector extends JScrollPane implements SystemStructureListener {

  private JPanel canvas;
  private String connID;
  private RockFactory rock;
  private JTree tree;
  
  private List listen = new ArrayList(2);
  private List mlisten = new ArrayList(2);

  public TechPackSelector(String connID, RockFactory rock) {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    this.canvas = new JPanel(new GridBagLayout());
    this.connID = connID;
    this.rock = rock;
    
    refresh();
  }
  
  public void metaDataChange() {
    refresh();
  }

  public void techPackChange() {
    refresh();
  }
  
  public void setChange() {}
  
  public void refresh() {
    
    canvas.removeAll();

    GridBagConstraints c = new GridBagConstraints();

    String error = null;

    try {

      Meta_collection_sets mcswhere = new Meta_collection_sets(rock);
      Meta_collection_setsFactory mcsf = new Meta_collection_setsFactory(rock,mcswhere);
      
      Vector tps = mcsf.get();
      
      if(tps == null || tps.size() <= 0)
        throw new IllegalArgumentException("No techpacks available");
      
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(connID);

      DefaultTreeModel dtm = new DefaultTreeModel(root);
      
      tree = new JTree(dtm);
      tree.setShowsRootHandles(false);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.setCellRenderer(new TechPackSelectorCellRenderer());
      
      HashMap types = new HashMap();
      
      Iterator it = tps.iterator();
      while (it.hasNext()) {
        Meta_collection_sets tp = (Meta_collection_sets) it.next();
        
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(tp);
        
        String type = tp.getType();
        if(type == null || type.length() <= 0)
          type = "Other";
        
        DefaultMutableTreeNode tn_parent = (DefaultMutableTreeNode)types.get(type);
        
        if(tn_parent == null) { //Parent not already added
          
          tn_parent = new DefaultMutableTreeNode(type);
          types.put(type,tn_parent);
        
          String par_uo = (String)tn_parent.getUserObject();
          
          int j = 0;
          for(; j < root.getChildCount() ; j++) {
             DefaultMutableTreeNode tnx = (DefaultMutableTreeNode)root.getChildAt(j);
            
             String tn_uo = (String)tnx.getUserObject();
             
             if (tn_uo.compareToIgnoreCase(par_uo) >= 0) {
               break;
             }
             
          }
          
          root.insert(tn_parent,j);
        
        }
        
        int i = 0;
        for( ; i < tn_parent.getChildCount() ; i++) {
          DefaultMutableTreeNode tpx = (DefaultMutableTreeNode)tn_parent.getChildAt(i);
          Meta_collection_sets tpx_uo = (Meta_collection_sets)tpx.getUserObject();
          String stpx = tpx_uo.getCollection_set_name()+" "+tpx_uo.getVersion_number();
          String stpn = tp.getCollection_set_name()+" "+tp.getVersion_number();
          
          if(stpx.compareToIgnoreCase(stpn) >= 0) {
            break;
          }
          
        }
        
        tn_parent.insert(tn,i);
        
      }

      tree.expandRow(0);

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
      
      c.weightx = 1;
      c.weighty = 1;
      c.fill = GridBagConstraints.BOTH;
      c.anchor = GridBagConstraints.NORTHWEST;

      canvas.add(tree, c);
      
    } catch (IllegalArgumentException iae) {
      error = iae.getMessage();
    } catch (Exception e) {
      error = "Error listing Techpacks: " + e.getMessage();
      e.printStackTrace();
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
    if(tree != null)
      tree.addTreeSelectionListener(tsl);
  }
  
  public void addMouseListener(MouseListener ml) {
    mlisten.add(ml);
  	if (tree!=null){
  	    tree.addMouseListener(ml);	
  	}

  }
  
  public Meta_collection_sets getSelectedTechPack() {
    TreePath tp = tree.getSelectionPath();
    if(tp != null || tp.getPathCount() > 1 || tp.getLastPathComponent() instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)tp.getLastPathComponent();
      
      if(dmtn.getUserObject() instanceof Meta_collection_sets) {
        return (Meta_collection_sets) dmtn.getUserObject();
      }
    }
    return null;
  }

}
