package com.distocraft.dc5000.etl.gui.set;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class SetNode extends DefaultMutableTreeNode implements TechPackTreeNode {

  private Meta_collections meta;
  private Meta_collection_sets tpmeta;
  private RockFactory rock;
  private TechPackTree tptree;
  private JFrame frame;
  
  /**
   * Creates this node and subNodes
   * 
   * @param meta
   * @throws Exception
   */
  public SetNode(Meta_collections meta, Meta_collection_sets tpmeta, RockFactory rock, TechPackTree tptree, JFrame frame) throws Exception {
    super(meta.getCollection_name());

    this.meta = meta;
    this.tpmeta = tpmeta;
    this.rock = rock;
    this.tptree = tptree;
    this.frame = frame;

  }

  public JComponent getTable() {

    JPanel con = new JPanel(new GridBagLayout());
    
    try {
      
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0;
      c.weighty = 0;
      con.add(Box.createRigidArea(new Dimension(10,5)),c);
      
      c.gridx = 1;
      c.gridy = 1;
      c.weightx = 1;
      con.add(new JLabel(tpmeta.getCollection_set_name()+" version "+meta.getVersion_number()),c);
      
      c.gridy = 2;
      con.add(new JLabel("Set "+meta.getCollection_name()),c);
      
      c.gridy = 3;
      con.add(new JLabel("Set type "+meta.getSettype()),c);
      
      c.gridy = 4;
      con.add(new JLabel("Priority "+meta.getPriority()),c);
      
      c.gridy = 5;
      con.add(new JLabel("Queue time limit "+meta.getQueue_time_limit()),c);
      
      c.gridy = 6;
      String stat = meta.getEnabled_flag();
      if(stat.equalsIgnoreCase("Y"))
        stat = "Status: enabled";
      else
        stat = "Status: disabled";
      con.add(new JLabel(stat),c);
      
      c.gridy = 7;
      c.gridx = 2;
      c.weightx = 0;
      con.add(Box.createRigidArea(new Dimension(10,5)),c);
      
      c.gridx = 0;
      c.gridy = 8;
      c.weightx = 1;
      c.weighty = 1;
      c.gridwidth = 3;
      c.fill = GridBagConstraints.BOTH;
      con.add(new ActionTable(meta,tpmeta,rock,tptree,frame),c);
      
    } catch (Exception e) { // ActionTable failed
      con.removeAll();
      
      GridBagConstraints c = new GridBagConstraints();
      
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      con.add(Box.createRigidArea(new Dimension(20,20)),c);
      
      c.gridy = 1;
      c.gridx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      con.add(new JLabel(e.getMessage()), c);
      
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 1;
      c.weightx = 1;
      con.add(Box.createRigidArea(new Dimension(20,20)),c);

    }
    
    return con;
  }
  
  public void deleteSet() throws Exception {

    // fetch action
    Meta_transfer_actions mta = new Meta_transfer_actions(rock);
    mta.setCollection_id(meta.getCollection_id());
    mta.setCollection_set_id(meta.getCollection_set_id());
    Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock,mta);
    Iterator iter = mtaF.get().iterator();
    while (iter.hasNext()){
      
      Meta_transfer_actions a = (Meta_transfer_actions)iter.next();
      
      // remove action
      a.deleteDB();
      
    }
    
    
    // fetch schedulings
    Meta_schedulings ms = new Meta_schedulings(rock);
    ms.setCollection_id(meta.getCollection_id());
    ms.setCollection_set_id(meta.getCollection_set_id());
    Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock,ms);
    Iterator iter3 = msF.get().iterator();
    while (iter3.hasNext()){
      
      Meta_schedulings a = (Meta_schedulings)iter3.next();
      
      // remove schedulings
      a.deleteDB();       
    }
    
    // delete techpack
    meta.deleteDB();
  }
  
  public Long getSetID() {
    return meta.getCollection_id();
  }
  
  public String getSetName() {
    return meta.getCollection_name();
  }
  
  public String getTechPackName() {
    return tpmeta.getCollection_set_name();
  }
  
  public String getTechPackVersion() {
    return tpmeta.getVersion_number();
  }

  public Long getTechPackID() {
    return meta.getCollection_set_id();
  }
  
  public Meta_collections getSet() {
    return meta;
  }
  
  public Meta_collection_sets getTechPack() {
    return tpmeta;
  }
  
  public int getNextOrderNo() {
    try {
    
      Meta_transfer_actions mtwhre = new Meta_transfer_actions(rock);
      mtwhre.setCollection_set_id(getTechPackID());
      mtwhre.setVersion_number(getTechPackVersion());
      mtwhre.setCollection_id(getSetID());
      Meta_transfer_actionsFactory mtaf = new Meta_transfer_actionsFactory(rock,mtwhre);
      
      int ret = -1;

      Iterator i = mtaf.get().iterator();
      
      while(i.hasNext()) {
        Meta_transfer_actions mta = (Meta_transfer_actions)i.next();
        if(mta.getOrder_by_no().intValue() > ret)
          ret = mta.getOrder_by_no().intValue();
      }
      
      return ret+1;
      
    } catch(Exception e) {
      e.printStackTrace();
      return 0;
    }
  
  }
    
  Long getNextActionID() throws Exception {
    
    Meta_transfer_actions mta = new Meta_transfer_actions(rock);
    mta.setCollection_set_id(meta.getCollection_set_id());
    mta.setVersion_number(meta.getVersion_number());
    mta.setCollection_id(meta.getCollection_id());
    Meta_transfer_actionsFactory mtaf = new Meta_transfer_actionsFactory(rock,mta);
    
    Enumeration e = mtaf.get().elements();
    
    Long biggest = new Long(-1L);
    while(e.hasMoreElements()) {
      Meta_transfer_actions sn = (Meta_transfer_actions)e.nextElement();
      Long cid = sn.getTransfer_action_id();
      if(cid.longValue() > biggest.longValue())
        biggest = cid;
    }
    return new Long(biggest.longValue() + 1);
    
  }
}