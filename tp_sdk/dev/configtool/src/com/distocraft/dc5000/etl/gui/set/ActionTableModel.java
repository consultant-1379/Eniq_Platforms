package com.distocraft.dc5000.etl.gui.set;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actionsFactory;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
class ActionTableModel extends AbstractTableModel {

  private static final String[] columns = { "Order", "Name", "Type", "Status", "Description" };

  private Vector actions = new Vector(10);

  private Meta_collections set;

  private RockFactory rock;

  ActionTableModel(Meta_collections set, RockFactory rock) throws Exception {
    this.set = set;
    this.rock = rock;

    Meta_transfer_actions filt = new Meta_transfer_actions(rock);
    filt.setCollection_set_id(set.getCollection_set_id());
    filt.setVersion_number(set.getVersion_number());
    filt.setCollection_id(set.getCollection_id());

    Meta_transfer_actionsFactory fact = new Meta_transfer_actionsFactory(rock, filt);

    Vector actionsVector = fact.get();
    Enumeration e = actionsVector.elements();

    while (e.hasMoreElements()) {
      Meta_transfer_actions mta = (Meta_transfer_actions) e.nextElement();

      addRow(mta);
    }

  }

  public Long getNextOrderNumber() {
    Long last = new Long(-1);

    for (int i = 0; i < actions.size(); i++) {
      Meta_transfer_actions mta = (Meta_transfer_actions) actions.get(i);
      Long cand = mta.getOrder_by_no();
      if (cand.longValue() > last.longValue())
        last = cand;
    }
    last = new Long(last.longValue() + 1);
    
    return last;
  }

  public Meta_transfer_actions getActionAt(int row) {
    return (Meta_transfer_actions) actions.get(row);
  }

  public String getColumnName(int col) {
    return columns[col];
  }

  public Class getColumnClass(int col) {
    if (col == 0)
      return Long.class;
    else if (col == 3)
      return Boolean.class;
    else
      return String.class;
  }

  public Object getValueAt(int row, int col) {
    Meta_transfer_actions action = (Meta_transfer_actions) actions.get(row);

    if (col == 0)
      return action.getOrder_by_no();
    else if (col == 1)
      return action.getTransfer_action_name();
    else if (col == 2)
      return action.getAction_type();
    else if (col == 3) {
      String sval = action.getEnabled_flag();
      if (sval != null && sval.equalsIgnoreCase("Y"))
        return new Boolean(true);
      else
        return new Boolean(false);
    } else if (col == 4)
      return action.getDescription();
    else
      return "undefined";
  }

  public int getColumnCount() {
    return columns.length;
  }

  public int getRowCount() {
    return actions.size();
  }

  int addRow(Meta_transfer_actions action) {

    if (action == null)
      return 0;
    actions.add(action);
    fireTableRowsInserted(actions.size(), actions.size());
    return actions.size() - 1;
  }

  public Long getNextActionID() {
    Enumeration e = actions.elements();
    Long biggest = new Long(-1L);
    while (e.hasMoreElements()) {
      Meta_transfer_actions mta = (Meta_transfer_actions) e.nextElement();

      if (mta.getTransfer_action_id().longValue() > biggest.longValue()) {
        biggest = mta.getTransfer_action_id();
      }
    }

    return new Long(biggest.longValue() + 1L);
  }

}
