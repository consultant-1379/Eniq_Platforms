package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universetable;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe class table.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings("serial")
public class UniverseTablesTableModel extends AbstractTableModel {


  static final int ownerColumnIdx = 0;

  static final int nameColumnIdx = 1;

  static final int aliasColumnIdx = 2;

  static final int universeextensionColumnIdx = 3;

  static final String[] columnNames = { "Owner", "Name", "Alias", "Universe extension" };

  static final Integer[] myColumnWidths= { 150, 150, 150, 50 };

  final Vector<Universetable> data;
  
  private final RockFactory rock;

  public UniverseTablesTableModel(final Vector<Universetable> data, RockFactory rock) {
    this.data = data;
    this.rock = rock;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(final int col) {
    return String.class;
  }

  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  public int getRowCount() {
    return data.size();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public Object getValueAt(final int row, final int col) {
    if (row >= data.size()){
      return null;
    }
    Universetable a = data.get(row);
    a.setOrdernro(row+0l);
    return getColumnValue(a, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    Universetable uc = data.get(row);
    setColumnValue(uc, col, value);
    fireTableCellUpdated(row, col);
  }

  public void addRow(final String owner, final String name, final String alias, final String ext)
      throws Exception {
    Universetable found = null;
    for (int i = 0; i < data.size(); i++) {
      Universetable ut = data.get(i);
      if (ut.getOwner().equals(owner) && ut.getUniverseextension().equals(ext)) {
        found = ut;
        break;
      }
    }

    if (found == null) {

      Universetable ut = new Universetable(rock);
      ut.setOwner(owner);
      ut.setTablename(name);
      ut.setAlias(alias);
      ut.setUniverseextension(ext);
      ut.saveDB();

      addRow(ut);

    }

  }

  public void addRow(final Universetable u) {
    data.add(u);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Universetable removeRow(final int row) {
    Universetable u = data.remove(row);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public Universetable duplicateRow(final int row) {
    Universetable u = data.get(row);
    Universetable newU = (Universetable)u.clone();
    newU.setOrdernro(row+0l);
    data.add (row, newU);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public void moveupRow(int row) {
    if(row <= 0){
      return;
    }
    data.add(row - 1, (Universetable) (data.get(row)));
    data.remove(row + 1);
  }
  
  public void movedownRow(int row) {
    if(row >= data.size()-1){
      return;
    }  
    data.add(row + 2, (Universetable) (data.get(row)));
    data.remove(row);
  }

  private String getColumnValue(final Universetable Universetable, final int col) {
    if (Universetable != null) {
      switch (col) {
      case ownerColumnIdx:
        return Utils.replaceNull(Universetable.getOwner().toUpperCase());

      case nameColumnIdx:
        return Utils.replaceNull(Universetable.getTablename());

      case aliasColumnIdx:
        return Utils.replaceNull(Universetable.getAlias());

      case universeextensionColumnIdx:
        return Utils.replaceNull(Universetable.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Universetable Universetable, final int col, final Object value) {
    if (Universetable != null) {
      switch (col) {
      case ownerColumnIdx:
        Universetable.setOwner(Utils.replaceNull((String) value).trim().toUpperCase());
        break;

      case nameColumnIdx:
        Universetable.setTablename(Utils.replaceNull((String) value));
        break;

      case aliasColumnIdx:
        Universetable.setAlias(Utils.replaceNull((String) value).trim());
        break;

      case universeextensionColumnIdx:
        Universetable.setUniverseextension(Utils.replaceNull((String) value).trim());
        break;

      default:
        break;
      }
    }
  }
  
  public void addEmptyNewRow(String version, int row) {

    Universetable u = new Universetable(rock, true);
    u.setOrdernro(row + 1l);
    u.setVersionid(version);
    u.setOwner(Constants.UNIVERSEOWNERTYPES[0]);
    u.setUniverseextension(Constants.UNIVERSEEXTENSIONTYPES[0]);
    data.add(row + 1, u);
    
    this.fireTableChanged(null);
  }
};
