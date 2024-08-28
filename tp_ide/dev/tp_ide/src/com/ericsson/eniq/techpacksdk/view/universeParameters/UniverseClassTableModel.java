package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe class table.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings("serial")
public class UniverseClassTableModel extends AbstractTableModel {

  static final int parentColumnIdx = 0;

  static final int classnameColumnIdx = 1;

  static final int descriptionColumnIdx = 2;

  static final int universeextensionColumnIdx = 3;

  static final String[] columnNames = { "Parent", "Class name", "Description", "Universe extension" };

  static final Integer[] myColumnWidths = { 150, 150, 150, 50 };

  final Vector<Universeclass> data;

  private final RockFactory rock;

  public UniverseClassTableModel(final Vector<Universeclass> data, RockFactory rock) {
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
    Universeclass a = data.get(row);
    a.setOrdernro(row+0l);
    return getColumnValue(a, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    Universeclass uc = data.get(row);
    setColumnValue(uc, col, value);
    fireTableCellUpdated(row, col);
  }

  public void addRow(final String parent, final String description, final String cname, final String ext)
      throws Exception {
    Universeclass found = null;
    for (int i = 0; i < data.size(); i++) {
      Universeclass uc = data.get(i);
      if (uc.getClassname().equals(cname) && uc.getUniverseextension().equals(ext)) {
        found = uc;
        break;
      }
    }

    if (found == null) {

      Universeclass uc = new Universeclass(rock);
      uc.setClassname(cname);
      uc.setDescription(description);
      uc.setParent(parent);
      uc.setUniverseextension(ext);
      uc.saveDB();

      addRow(uc);

    }

  }

  public void addRow(final Universeclass uc) {
    data.add(uc);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Universeclass removeRow(final int row, JFrame frame) {
    List<Universeclass> list = new ArrayList<Universeclass> ();
    
    list.add(data.get(row));
    
    String warning = UniverseClassDataModel.validateDelete(rock, list);
    
    if (warning.length() == 0
        || JOptionPane.showConfirmDialog(frame, warning, "Confirm Remove", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

      Universeclass uc = data.remove(row);
      fireTableRowsDeleted(row, row);
      return uc;
    }
    return null;
  }

  public Universeclass duplicateRow(final int row) {
    Universeclass uc = data.get(row);
    Universeclass newUC = (Universeclass) uc.clone();
    newUC.setOrdernro(row+0l);
    data.add(row, newUC);
    fireTableRowsDeleted(row, row);
    return uc;
  }
  
  public void moveupRow(int row) {
    if(row <= 0){
      return;
    }
    data.add(row - 1, (Universeclass) (data.get(row)));
    data.remove(row + 1);
  }
  
  public void movedownRow(int row) {
    if(row >= data.size()-1){
      return;
    }
    data.add(row + 2, (Universeclass) (data.get(row)));
    data.remove(row);
  }
  
  private String getColumnValue(final Universeclass universeclass, final int col) {
    if (universeclass != null) {
      switch (col) {
      case parentColumnIdx:
        return Utils.replaceNull(universeclass.getParent());

      case classnameColumnIdx:
        return Utils.replaceNull(universeclass.getClassname());

      case descriptionColumnIdx:
        return Utils.replaceNull(universeclass.getDescription());

      case universeextensionColumnIdx:
        return Utils.replaceNull(universeclass.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Universeclass universeclass, final int col, final Object value) {
    if (universeclass != null) {
      switch (col) {
      case parentColumnIdx:
        universeclass.setParent(Utils.replaceNull((String) value).trim());
        break;

      case classnameColumnIdx:
        universeclass.setClassname(Utils.replaceNull((String) value));
        break;

      case descriptionColumnIdx:
        universeclass.setDescription(Utils.replaceNull((String) value).trim());
        break;

      case universeextensionColumnIdx:
        universeclass.setUniverseextension(Utils.replaceNull((String) value).trim());
        break;

      default:
        break;
      }
    }
  }

  public void addEmptyNewRow(String version, int row) {

    Universeclass uc = new Universeclass(rock, true);
    uc.setOrdernro(row + 1l);
    uc.setVersionid(version);
    uc.setUniverseextension(Constants.UNIVERSEEXTENSIONTYPES[0]);
    data.add(row + 1, uc);

    this.fireTableChanged(null);
  }
};
