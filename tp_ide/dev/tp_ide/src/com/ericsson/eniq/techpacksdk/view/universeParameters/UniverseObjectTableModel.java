package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe object table.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings("serial")
public class UniverseObjectTableModel extends AbstractTableModel {

  static final int classnameColumnIdx = 0;

  static final int objectnameColumnIdx = 1;

  static final int descriptionColumnIdx = 2;

  static final int objecttypeColumnIdx = 3;

  static final int qualificationColumnIdx = 4;

  static final int aggregationColumnIdx = 5;

  static final int selectColumnIdx = 6;

  static final int whereColumnIdx = 7;

  static final int promptColumnIdx = 8;

  static final int universeextensionColumnIdx = 9;

  static final String[] columnNames = { "Class name", "Object name", "Description", "Object type", "Qualification",
      "Aggregation", "Select", "Where", "prompt", "Universe extension" };

  static final Integer[] myColumnWidths = { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 };

  final Vector<Universeobject> data;

  private final RockFactory rock;

  public UniverseObjectTableModel(final Vector<Universeobject> data, RockFactory rock) {
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
    if (row >= data.size()) {
      return null;
    }
    Universeobject a = data.get(row);
    a.setOrdernro(row + 0l);
    return getColumnValue(a, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    Universeobject u = data.get(row);
    setColumnValue(u, col, value);
    fireTableCellUpdated(row, col);
  }

  public void addRow(final Universeobject u) {
    data.add(u);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Universeobject removeRow(final int row) {
    Universeobject u = data.remove(row);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public Universeobject duplicateRow(final int row) {
    Universeobject u = data.get(row);
    Universeobject newU = (Universeobject) u.clone();
    newU.setOrdernro(row + 0l);
    data.add(row, newU);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public void moveupRow(int row) {
    if (row <= 0) {
      return;
    }
    data.add(row - 1, (Universeobject) (data.get(row)));
    data.remove(row + 1);
  }

  public void movedownRow(int row) {
    if (row >= data.size() - 1) {
      return;
    }
    data.add(row + 2, (Universeobject) (data.get(row)));
    data.remove(row);
  }

  private Object getColumnValue(final Universeobject o, final int col) {

    if (o != null) {
      switch (col) {
      case classnameColumnIdx:
        return Utils.replaceNull(o.getClassname());

      case objectnameColumnIdx:
        return Utils.replaceNull(o.getObjectname());

      case descriptionColumnIdx:
        return Utils.replaceNull(o.getDescription());

      case objecttypeColumnIdx:
        // Return capitalized string (first letter caps, others small), as old
        // SDK techpacks might have different upper / lower case used.
        String objectType = Utils.replaceNull(o.getObjecttype());
        return objectType.substring(0, 1).toUpperCase() + objectType.substring(1, objectType.length()).toLowerCase();

      case qualificationColumnIdx:
        return Utils.replaceNull(o.getQualification());

      case aggregationColumnIdx:
        return Utils.replaceNull(o.getAggregation()).toUpperCase();

      case selectColumnIdx:
        return Utils.replaceNull(o.getObjselect());

      case whereColumnIdx:
        return Utils.replaceNull(o.getObjwhere());

      case promptColumnIdx:
        return Utils.replaceNull(o.getPrompthierarchy());

      case universeextensionColumnIdx:
        return Utils.replaceNull(o.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Universeobject o, final int col, final Object value) {
    if (o != null) {
      switch (col) {

      case classnameColumnIdx:
        o.setClassname(Utils.replaceNull((String) value).trim());
        break;

      case objectnameColumnIdx:
        o.setObjectname(Utils.replaceNull((String) value).trim());
        break;

      case descriptionColumnIdx:
        o.setDescription(Utils.replaceNull((String) value).trim());
        break;

      case objecttypeColumnIdx:
        o.setObjecttype(Utils.replaceNull((String) value).trim());
        break;

      case qualificationColumnIdx:
        o.setQualification(Utils.replaceNull((String) value).trim());
        break;

      case aggregationColumnIdx:
        o.setAggregation(Utils.replaceNull((String) value).trim());
        break;

      case selectColumnIdx:
        o.setObjselect(Utils.replaceNull((String) value).trim());
        break;

      case whereColumnIdx:
        o.setObjwhere(Utils.replaceNull((String) value).trim());
        break;

      case promptColumnIdx:
        o.setPrompthierarchy(Utils.replaceNull((String) value).trim());
        break;

      case universeextensionColumnIdx:
        o.setUniverseextension(Utils.replaceNull((String) value).trim());
        break;
      default:
        break;
      }
    }
  }

  public void addEmptyNewRow(String version, int row) {

    Universeobject u = new Universeobject(rock, true);
    u.setOrdernro(row + 1l);
    u.setVersionid(version);
    u.setUniverseextension(Constants.UNIVERSEEXTENSIONTYPES[0]);
    u.setObjecttype(Constants.UNIVERSEOBJECTTYPES[0]);
    data.add(row + 1, u);

    this.fireTableChanged(null);
  }
};
