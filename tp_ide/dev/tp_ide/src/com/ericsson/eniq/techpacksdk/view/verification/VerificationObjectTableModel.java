/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.verification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;

import com.distocraft.dc5000.repository.dwhrep.Verificationobject;
import com.ericsson.eniq.techpacksdk.ComboTableCellEditor;
import com.ericsson.eniq.techpacksdk.ComboTableCellRenderer;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class VerificationObjectTableModel extends AbstractTableModel {

  private static final String[] columnNames = { "Measurement Type", "Level", "Class", "Object" };

  private static final Integer[] columnWidths = { 150, 150, 150, 150 };

  private static final int meastypeColumnIdx = 0;

  private static final int measlevelColumnIdx = 1;

  private static final int objectclassColumnIdx = 2;

  private static final int objectnameColumnIdx = 3;

  final List<Verificationobject> data;

  private final List<Verificationobject> dirtyList = new ArrayList<Verificationobject>();

  /**
   * @param data
   */
  public VerificationObjectTableModel(final Application application, final DataModelController dataModelController,
      final List<Verificationobject> data) {
    super();
    this.data = data;
  }

  public Class<? extends Object> getColumnClass(final int col) {
    final Object obj = getValueAt(0, col);
    if (obj != null) {
      return obj.getClass();
    } else {
      return String.class;
    }
  }

  private Object getColumnValue(final Verificationobject verificationobject, final int col) {
    if (verificationobject != null) {
      switch (col) {
      case meastypeColumnIdx:
        return Utils.replaceNull(verificationobject.getMeastype());
      case measlevelColumnIdx:
        return Utils.replaceNull(verificationobject.getMeaslevel());
      case objectclassColumnIdx:
        return Utils.replaceNull(verificationobject.getObjectclass());
      case objectnameColumnIdx:
        return Utils.replaceNull(verificationobject.getObjectname());
      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Verificationobject verificationobject, final int col, final Object value) {
    boolean valueChanged = false;
    switch (col) {
    case meastypeColumnIdx:
      verificationobject.setMeastype((String) value);
      valueChanged = true;
      break;
    case measlevelColumnIdx:
      verificationobject.setMeaslevel((String) value);
      valueChanged = true;
      break;
    case objectclassColumnIdx:
      verificationobject.setObjectclass((String) value);
      valueChanged = true;
      break;
    case objectnameColumnIdx:
      verificationobject.setObjectname((String) value);
      valueChanged = true;
      break;
    default:
      break;
    }
    if (valueChanged) {
      if (!dirtyList.contains(verificationobject)) {
        dirtyList.add(verificationobject);
      }
    }
  }

  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    if (data != null) {
      return data.size();
    }
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(final int row, final int col) {
    Object result = null;
    if (data != null) {
      if (data.size() > row) {
        if ((data.get(row) != null) && (data.get(row) instanceof Verificationobject)) {
          final Verificationobject Verificationobject = (Verificationobject) data.get(row);
          result = getColumnValue(Verificationobject, col);
        }
      }
    }
    return result;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    if (data != null) {
      if (data.size() > row) {
        final Verificationobject Verificationobject = (Verificationobject) data.get(row);
        setColumnValue(Verificationobject, col, value);
        fireTableDataChanged();
      }
    }
  }

  public void addRow(final Verificationobject verificationobject) {
    data.add(verificationobject);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Verificationobject removeRow(final int row) {
    final Verificationobject verificationobject = data.remove(row);
    if (dirtyList.contains(verificationobject)) {
      dirtyList.remove(verificationobject);
    }
    fireTableRowsDeleted(row, row);
    return verificationobject;
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public boolean isDirty() {
    return dirtyList.size() > 0;
  }

  public void clearDirty() {
    dirtyList.clear();
  }

  public void clear() {
    final int rows = data.size();
    data.clear();
    dirtyList.clear();
    fireTableRowsDeleted(0, rows);
  }

  public void save() throws Exception {
    while (isDirty()) {
      final Verificationobject verificationobject = dirtyList.remove(0);
      verificationobject.saveToDB();
    }
  }

  public List<Verificationobject> getData() {
    return data;
  }

  public void setData(final List<Verificationobject> data) {
    clear();
    for (final Iterator<Verificationobject> iter = data.iterator(); iter.hasNext();) {
      addRow(iter.next());
    }
  }

  /**
   * Method for setting the preferred column widths
   */
  public void setColumnWidths(final JTable theTable) {
    for (int ind = 0; ind < columnWidths.length; ind++) {
      if (theTable.getColumnModel().getColumnCount() <= ind) {
        final TableColumn col = theTable.getColumnModel().getColumn(ind);
        col.setPreferredWidth(columnWidths[ind]);
      }
    }
  }

  /**
   * Method for setting the column editors
   */
  public void setColumnEditors(final JTable theTable) {
    // Set editor for mtype
    final TableColumn meastypeColumn = theTable.getColumnModel().getColumn(meastypeColumnIdx);
    final LimitedSizeCellEditor limitedTextEditor = new LimitedSizeCellEditor(columnWidths[meastypeColumnIdx],
        Verificationobject.getMeastypeColumnSize(), true);
    meastypeColumn.setCellEditor(limitedTextEditor);
    // Set editor for level
    final TableColumn levelColumn = theTable.getColumnModel().getColumn(measlevelColumnIdx);
    final ComboTableCellEditor levelColumnComboEditor = new ComboTableCellEditor(Constants.REPORTOBJECTLEVELS, false);
    levelColumn.setCellEditor(levelColumnComboEditor);
    // Set editor for objectclass
    final TableColumn objectclassColumn = theTable.getColumnModel().getColumn(objectclassColumnIdx);
    final LimitedSizeCellEditor limitedTextEditor2 = new LimitedSizeCellEditor(columnWidths[objectclassColumnIdx],
        Verificationobject.getObjectclassColumnSize(), true);
    objectclassColumn.setCellEditor(limitedTextEditor2);
    // Set editor for objectname
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(objectnameColumnIdx);
    final LimitedSizeCellEditor limitedTextEditor3 = new LimitedSizeCellEditor(columnWidths[objectnameColumnIdx],
        Verificationobject.getObjectnameColumnSize(), true);
    objectnameColumn.setCellEditor(limitedTextEditor3);
  }

  /**
   * Method for setting the column renderers
   */
  public void setColumnRenderers(final JTable theTable) {
    // Set renderer for mtype
    final TableColumn meastypeColumn = theTable.getColumnModel().getColumn(meastypeColumnIdx);
    final LimitedSizeTextTableCellRenderer meastypeComboRenderer = new LimitedSizeTextTableCellRenderer(
        Verificationobject.getMeastypeColumnSize(), true);
    meastypeColumn.setCellRenderer(meastypeComboRenderer);
    // Set renderer for measlevel
    final TableColumn levelColumn = theTable.getColumnModel().getColumn(measlevelColumnIdx);
    final ComboTableCellRenderer levelComboRenderer = new ComboTableCellRenderer(Constants.REPORTOBJECTLEVELS);
    levelColumn.setCellRenderer(levelComboRenderer);
    // Set renderer for objectclass
    final TableColumn objectclassColumn = theTable.getColumnModel().getColumn(objectclassColumnIdx);
    final LimitedSizeTextTableCellRenderer objectclassComboRenderer = new LimitedSizeTextTableCellRenderer(
        Verificationobject.getObjectclassColumnSize(), true);
    objectclassColumn.setCellRenderer(objectclassComboRenderer);
    // Set renderer for objectname
    final TableColumn objectnameColumn = theTable.getColumnModel().getColumn(objectnameColumnIdx);
    final LimitedSizeTextTableCellRenderer objectnameComboRenderer = new LimitedSizeTextTableCellRenderer(
        Verificationobject.getObjectnameColumnSize(), true);
    objectnameColumn.setCellRenderer(objectnameComboRenderer);
  }

  public Vector<String> validateData() {
    final Vector<String> errorStrings = new Vector<String>();
    for (final Iterator<Verificationobject> iter = data.iterator(); iter.hasNext();) {
      final Verificationobject verificationobject1 = iter.next();
      if (Utils.replaceNull(verificationobject1.getMeastype()).trim().equals("")) {
        errorStrings.add(columnNames[meastypeColumnIdx] + " is required");
      } else if (Utils.replaceNull(verificationobject1.getMeaslevel()).trim().equals("")) {
        errorStrings.add(columnNames[measlevelColumnIdx] + " is required");
      } else if (Utils.replaceNull(verificationobject1.getObjectclass()).trim().equals("")) {
        errorStrings.add(columnNames[objectclassColumnIdx] + " is required");
      } else if (Utils.replaceNull(verificationobject1.getObjectname()).trim().equals("")) {
        errorStrings.add(columnNames[objectnameColumnIdx] + " is required");
      } else {
        for (final Iterator<Verificationobject> iter2 = data.iterator(); iter2.hasNext();) {
          final Verificationobject verificationobject2 = iter2.next();
          if (verificationobject2 != verificationobject1) {
            if (verificationobject2.getMeaslevel().equals(verificationobject1.getMeaslevel())
                && (verificationobject2.getObjectname().equals(verificationobject1.getObjectname()))
                && (verificationobject2.getObjectname().equals(verificationobject1.getObjectname()))
                && (verificationobject2.getObjectclass().equals(verificationobject1.getObjectclass()))) {
              errorStrings.add("Verification Object with Level: " + verificationobject1.getMeaslevel() + ", Class: "
                  + verificationobject1.getObjectclass() + " and Object: " + verificationobject1.getObjectname()
                  + " is not unique");
            }
          }
        }
      }
    }
    return errorStrings;
  }

  /**
   * Returns the verification object from the given row.
   * 
   * @param rowIndex
   * @return the verification object. Null in case there is no such row in the
   *         table.
   */
  public Verificationobject getRow(final int rowIndex) {
    if (rowIndex > -1 && rowIndex < data.size()) {
      return data.get(rowIndex);
    } else{
    	return null;
    }
  }
}
