package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universecondition;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe class table.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings("serial")
public class UniverseConditionTableModel extends AbstractTableModel {

  static final int classnameColumnIdx = 0;

  static final int conditionColumnIdx = 1;

  static final int descriptionColumnIdx = 2;

  static final int whereColumnIdx = 3;

  static final int autogenerateColumnIdx = 4;

  static final int oClassColumnIdx = 5;

  static final int objectColumnIdx = 6;

  static final int promptColumnIdx = 7;

  static final int multiselectionColumnIdx = 8;

  static final int freeTextColumnIdx = 9;

  static final int universeextensionColumnIdx = 10;

  static final String[] columnNames = { "Class name", "Condition", "Description", "Where", "Auto gen", "Object class", "Object",
      "Prompt", "Multi selection", "Free text", "Universe extension" };

  static final Integer[] myColumnWidths = { 100, 100, 100, 100, 100, 150, 100, 100, 100, 100, 50 };

  final Vector<Universecondition> data;

  private final RockFactory rock;

  public UniverseConditionTableModel(final Vector<Universecondition> data, RockFactory rock) {
    this.data = data;
    this.rock = rock;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(final int col) {
    if (col == autogenerateColumnIdx)
      return Boolean.class;

    if (col == multiselectionColumnIdx)
      return Boolean.class;

    if (col == freeTextColumnIdx)
      return Boolean.class;

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
    Universecondition a = data.get(row);
    a.setOrdernro(row+0l);
    return getColumnValue(a, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    Universecondition uc = data.get(row);
    setColumnValue(uc, col, value);
    fireTableCellUpdated(row, col);
  }

  public void addRow(final Universecondition u) {
    data.add(u);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Universecondition removeRow(final int row) {
    Universecondition u = data.remove(row);
    fireTableRowsDeleted(row, row);
    return u;
  }

  public Universecondition duplicateRow(final int row) {
    Universecondition u = data.get(row);
    Universecondition newU = (Universecondition) u.clone();
    newU.setOrdernro(row+0l);
    data.add(row, newU);
    fireTableRowsDeleted(row, row);
    return u;
  }
  
  public void moveupRow(int row) {
    if(row <= 0){
      return;
    }
    data.add(row - 1, (Universecondition) (data.get(row)));
    data.remove(row + 1);
  }
  
  public void movedownRow(int row) {
    if(row >= data.size()-1){
      return;
    }
    data.add(row + 2, (Universecondition) (data.get(row)));
    data.remove(row);
  }
  
  private Object getColumnValue(final Universecondition Universecondition, final int col) {
    if (Universecondition != null) {
      switch (col) {
      case classnameColumnIdx:
        return Utils.replaceNull(Universecondition.getClassname());

      case conditionColumnIdx:
        return Utils.replaceNull(Universecondition.getUniversecondition());

      case descriptionColumnIdx:
        return Utils.replaceNull(Universecondition.getDescription());

      case whereColumnIdx:
        return Utils.replaceNull(Universecondition.getCondwhere());

      case autogenerateColumnIdx:
        return Utils.integerToBoolean(Universecondition.getAutogenerate());

      case oClassColumnIdx:
        return Utils.replaceNull(Universecondition.getCondobjclass());

      case objectColumnIdx:
        return Utils.replaceNull(Universecondition.getCondobject());

      case promptColumnIdx:
        return Utils.replaceNull(Universecondition.getPrompttext());

      case multiselectionColumnIdx:
        return Utils.integerToBoolean(Universecondition.getMultiselection());

      case freeTextColumnIdx:
        return Utils.integerToBoolean(Universecondition.getFreetext());

      case universeextensionColumnIdx:
        return Utils.replaceNull(Universecondition.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Universecondition Universecondition, final int col, final Object value) {
    if (Universecondition != null) {
      switch (col) {

      case classnameColumnIdx:
        Universecondition.setClassname(Utils.replaceNull((String) value).trim());
        break;

      case conditionColumnIdx:
        Universecondition.setUniversecondition(Utils.replaceNull((String) value).trim());
        break;
      case descriptionColumnIdx:
        Universecondition.setDescription(Utils.replaceNull((String) value).trim());
        break;
      case whereColumnIdx:
        Universecondition.setCondwhere(Utils.replaceNull((String) value).trim());
        break;
      case oClassColumnIdx:
        Universecondition.setCondobjclass(Utils.replaceNull((String) value).trim());
        break;
      case objectColumnIdx:
        Universecondition.setCondobject(Utils.replaceNull((String) value).trim());
        break;
      case promptColumnIdx:
        Universecondition.setPrompttext(Utils.replaceNull((String) value).trim());
        break;
      case universeextensionColumnIdx:
        Universecondition.setUniverseextension(Utils.replaceNull((String) value).trim());
        break;

      case freeTextColumnIdx:
        Universecondition.setFreetext(Utils.booleanToInteger((Boolean) value));
        break;

      case autogenerateColumnIdx:
        Universecondition.setAutogenerate(Utils.booleanToInteger((Boolean) value));
        break;

      case multiselectionColumnIdx:
        Universecondition.setMultiselection(Utils.booleanToInteger((Boolean) value));
        break;

      default:
        break;
      }
    }
  }

  public void addEmptyNewRow(String version, int row) {

    Universecondition u = new Universecondition(rock, true);
    u.setOrdernro(row + 1l);
    u.setVersionid(version);
    u.setUniverseextension(Constants.UNIVERSEEXTENSIONTYPES[0]);
    data.add(row + 1, u);

    this.fireTableChanged(null);
  }
};
