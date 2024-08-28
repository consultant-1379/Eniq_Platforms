package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

/**
 * Abstract generic class that helps to model universe related tables.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings("serial")
public abstract class GenericUniverseTableModel<E extends RockDBObject> extends AbstractTableModel {

  public final Vector<E> data;

  public final RockFactory rock;

  public String[] columnNameArr = null;

  public int[] columnWidtArr = null;

  /**
   * Table model constructor
   * 
   * @param data
   * @param rock
   */
  public GenericUniverseTableModel(final Vector<E> data, RockFactory rock) {
    this.data = data;
    this.rock = rock;
    initColumnDefinitions();
  }

  public int getColumnWidt(int col) {
    return columnWidtArr[col];
  }

  /**
   * Common method for all universe related tablemodels
   */
  public String getColumnName(final int col) {
    return columnNameArr[col].toString();
  }

  /**
   * Common method for all universe related tablemodels
   */
  public int getRowCount() {
    return data.size();
  }

  /**
   * Common method for all universe related tablemodels
   */
  public int getColumnCount() {
    return columnNameArr.length;
  }

  /**
   * Common method for all universe related tablemodels
   */
  public Object getValueAt(final int row, final int col) {

    if (row >= data.size()) {
      return null;
    }

    E a = data.get(row);
    return getColumnValue(a, row, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  /**
   * Common method for all universe related tablemodels
   */
  public void setValueAt(final Object value, final int row, final int col) {
    E u = data.get(row);
    setColumnValue(u, col, value);
    fireTableCellUpdated(row, col);
  }

  /**
   * Common method for all universe related tablemodels
   */
  public void addRow(final E u) {
    data.add(u);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  /**
   * Common method for all universe related tablemodels
   */
  public E removeRow(final int row) {
    E u = data.remove(row);
    fireTableRowsDeleted(row, row);
    return u;
  }

  /**
   * Common method for all universe related tablemodels
   */
  @SuppressWarnings("unchecked")
  public E duplicateRow(final int row) {
    E u = data.get(row);
    E newU = (E) u.clone();
    data.add(row, newU);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public void moveupRow(int row) {
    if (row <= 0) {
      return;
    }
    data.add(row - 1, (E) (data.get(row)));
    data.remove(row + 1);
  }

  public void movedownRow(int row) {
    if (row >= data.size() - 1) {
      return;
    }
    data.add(row + 2, (E) (data.get(row)));
    data.remove(row);
  }

  /**
   * Get column class method. This is called to determine default editor. For
   * check box, return Boolean, for example. Defaults to String class here
   */
  @SuppressWarnings("unchecked")
  public Class getColumnClass(final int col) {
    return String.class;
  }

  /**
   * Abstract methods for all universe related creation and initializing
   */
  abstract public void addEmptyNewRow(String version, int row);

  abstract Object getColumnValue(final E u, final int raw, final int col);

  abstract void setColumnValue(final E u, final int col, final Object value);

  abstract void initColumnDefinitions();

};
