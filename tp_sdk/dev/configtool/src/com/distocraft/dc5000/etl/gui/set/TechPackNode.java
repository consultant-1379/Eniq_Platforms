package com.distocraft.dc5000.etl.gui.set;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.etl.rock.Meta_schedulingsFactory;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class TechPackNode extends DefaultMutableTreeNode implements TechPackTreeNode {

  private Meta_collection_sets meta;

  private TechPackTree tptree;

  private JFrame frame;

  private RockFactory rock;

  /**
   * Creates this node and subNodes
   * 
   * @param meta
   * @throws Exception
   */
  public TechPackNode(Meta_collection_sets meta, RockFactory rock, TechPackTree tptree, JFrame frame) throws Exception {
    super(meta.getCollection_set_name() + " " + meta.getVersion_number());

    this.meta = meta;
    this.tptree = tptree;
    this.frame = frame;
    this.rock = rock;

    Meta_collections m = new Meta_collections(rock);
    m.setCollection_set_id(meta.getCollection_set_id());
    m.setVersion_number(meta.getVersion_number());

    Meta_collectionsFactory mcf = new Meta_collectionsFactory(rock, m);

    Vector v = mcf.get();
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      Meta_collections mcol = (Meta_collections) e.nextElement();

      SetNode sn = new SetNode(mcol, meta, rock, tptree, frame);

      int i = 0;
      for (; i < this.getChildCount(); i++) {
        SetNode tpx = (SetNode) this.getChildAt(i);
        String tpx_uo = (String) tpx.getUserObject();
        String tpn_uo = (String) sn.getUserObject();

        if (tpx_uo.compareToIgnoreCase(tpn_uo) >= 0) {
          break;
        }

      }

      this.insert(sn, i);

      // this.add(sn);

    }

  }

  public JComponent getTable() {
    JPanel pan = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 0;
    c.weighty = 0;
    pan.add(Box.createRigidArea(new Dimension(10, 5)), c);

    c.gridx = 1;
    c.gridy = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    pan.add(new JLabel(meta.getCollection_set_name() + " version " + meta.getVersion_number()), c);

    c.gridy = 2;
    String desc = meta.getDescription();
    if (desc == null || desc.length() <= 0) {
      desc = "No description available";
    }
    pan.add(new JLabel(desc), c);

    c.gridy = 3;
    pan.add(new JLabel("Package ID " + meta.getCollection_set_id()), c);

    c.gridy = 4;
    String stat = meta.getEnabled_flag();
    if (stat.equalsIgnoreCase("Y"))
      stat = "Status: enabled";
    else
      stat = "Status: disabled";
    pan.add(new JLabel(stat), c);

    c.gridy = 5;
    c.gridx = 2;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    pan.add(Box.createRigidArea(new Dimension(10, 5)), c);

    return pan;
  }

  Long getID() {
    return meta.getCollection_set_id();
  }

  public Meta_collection_sets getMeta() {
    return meta;
  }

  public String getTechPackName() {
    return meta.getCollection_set_name();
  }

  public String getTechPackVersion() {
    return meta.getVersion_number();
  }

  public Long getTechPackID() {
    return meta.getCollection_set_id();
  }

  public void deleteTechPack() throws Exception {

    // fetch sets
    Meta_collections mc = new Meta_collections(rock);
    mc.setCollection_set_id(meta.getCollection_set_id());
    Meta_collectionsFactory mcF = new Meta_collectionsFactory(rock, mc);
    Iterator iter = mcF.get().iterator();
    while (iter.hasNext()) {

      Meta_collections m = (Meta_collections) iter.next();

      // fetch actions
      Meta_transfer_actions mta = new Meta_transfer_actions(rock);
      mta.setCollection_id(m.getCollection_id());
      mta.setCollection_set_id(meta.getCollection_set_id());
      Meta_transfer_actionsFactory mtaF = new Meta_transfer_actionsFactory(rock, mta);
      Iterator iter2 = mtaF.get().iterator();
      while (iter2.hasNext()) {

        Meta_transfer_actions a = (Meta_transfer_actions) iter2.next();

        // remove action
        a.deleteDB();
      }

      // fetch schedulings
      Meta_schedulings ms = new Meta_schedulings(rock);
      ms.setCollection_id(m.getCollection_id());
      ms.setCollection_set_id(meta.getCollection_set_id());
      Meta_schedulingsFactory msF = new Meta_schedulingsFactory(rock, ms);
      Iterator iter3 = msF.get().iterator();
      while (iter3.hasNext()) {

        Meta_schedulings a = (Meta_schedulings) iter3.next();

        // remove schedulings
        a.deleteDB();
      }

      // remove set
      m.deleteDB();
    }

    // remove techpack
    meta.deleteDB();

  }

  public Meta_collection_sets getTechPack() {
    return meta;
  }

  Long getNextSetID(RockFactory rock) {

    long maxValue = 0;
    Statement stmtc = null;
    Connection con = null;

    try {

      // get max value from DB

      con = rock.getConnection();
      stmtc = con.createStatement();
      stmtc.getConnection().commit();
      String queryClause = "select COLLECTION_ID from META_COLLECTIONS order by COLLECTION_ID desc";
      ResultSet rSet = stmtc.executeQuery(queryClause);
      stmtc.getConnection().commit();
      maxValue = 0;

      if (rSet.next())
        maxValue = (int) rSet.getInt("COLLECTION_ID") + 1;

      stmtc.close();

    } catch (Exception e) {
      try {
        if (stmtc != null)
          stmtc.close();

        if (con != null) {
          con.close();
        }

      } catch (Exception ee) {

      }

    }

    return new Long(maxValue);

  }

}
