package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class BusyHourMappingsTableModel extends AbstractTableModel implements SubTableModel {

  private static final int typeColumnIdx = 0;

  private static final int enableColumnIdx = 1;

  /**
   * Column names, used as headings for the columns.
   */
  private static final String[] columnNames = { "Measurement Type", "Enabled" };

  /**
   * Column widths, used to graphically layout the columns.
   */
  private static final int[] columnWidths = { 200, 75 };

  private Vector<Busyhourmapping> data;

  private List<Busyhourmapping> deletedData;

  private List<Busyhourmapping> modifiedData;

  private Vector<Busyhourmapping> copyOfTheData;

  /**
   * Rock factory, used to connect to the database.
   */
  private final RockFactory rockFactory;

  /**
   * @param data
   */
  public BusyHourMappingsTableModel(RockFactory rockFactory, List<Busyhourmapping> data) {
    super();
    this.rockFactory = rockFactory;
    setData(data);
  }

  private Object getColumnValue(final Busyhourmapping mapping, final int col) {
    if (mapping != null) {
      switch (col) {
      case typeColumnIdx:
        return Utils.replaceNull(mapping.getBhtargettype());
      case enableColumnIdx:
        if (mapping.getEnable() == 1) {
          return new Boolean(true);
        } else {
          return new Boolean(false);
        }
      default:
        break;
      }
    }
    return null;

  }

  private void setColumnValue(final Busyhourmapping mapping, final int col, final Object value) {
    boolean valueChanged = true;
    switch (col) {

    case typeColumnIdx:
      mapping.setBhtargettype(Utils.replaceNull((String) value));
      break;
    case enableColumnIdx:
      if ((Boolean) value) {
        mapping.setEnable(1);
      } else {
        mapping.setEnable(0);
      }
      break;
    default:
      // nothing changed
      valueChanged = false;
      break;
    }
    if (valueChanged) {
      if (!modifiedData.contains(mapping)) {
        modifiedData.add(mapping);
      }
    }
  }

  /**
   * Resets all mappings to enabled. All pending modifications are cleared
   * first.
   */
  public void resetBusyhourMappings() {
    if (isDirty()) {
      clearDirty();
    }
    for (Busyhourmapping mapping : data) {
      mapping.setEnable(1);
      modifiedData.add(mapping);
    }
  }

  @Override
  public Class<? extends Object> getColumnClass(final int col) {
    return String.class;
  }

  @Override
  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    if (data != null) {
      return data.size();
    }
    return 0;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int colIndex) {
    Object result = null;
    if (data != null) {
      if (data.size() > rowIndex) {
        if ((data.elementAt(rowIndex) != null) && (data.elementAt(rowIndex) instanceof Busyhourmapping)) {
          final Busyhourmapping Busyhourrankkeys = (Busyhourmapping) data.elementAt(rowIndex);
          result = getColumnValue(Busyhourrankkeys, colIndex);
        }
      }
    }
    return result;
  }

  @Override
  public void setValueAt(final Object value, final int row, final int col) {
    final Busyhourmapping Busyhourrankkeys = (Busyhourmapping) data.elementAt(row);
    setColumnValue(Busyhourrankkeys, col, value);
    fireTableDataChanged();
  }

  private void addRow(final Busyhourmapping mapping) {
    data.add(mapping);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  private Busyhourmapping removeRow(final int row) {
    final Busyhourmapping mapping = data.remove(row);
    fireTableRowsDeleted(row, row);
    return mapping;
  }

  @Override
  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public boolean isDirty() {
    return modifiedData.size() > 0;
  }

  public void clearDirty() {
    modifiedData.clear();
  }

  public void clear() {
    final int rows = data.size();
    data.clear();
    modifiedData.clear();
    deletedData.clear();
    fireTableRowsDeleted(0, rows);
  }

  public void save() throws Exception {
    for (Busyhourmapping key : deletedData) {
      key.deleteDB();
    }

    while (isDirty()) {
      final Busyhourmapping mapping = modifiedData.remove(0);
      if (!deletedData.contains(mapping)) {
        mapping.saveToDB();
      }
    }
  }

  @Override
  public Vector<Busyhourmapping> getData() {
    return data;
  }

  /**
   * Sets the busy hour mapping data to the model.
   * 
   * @param mappings
   */
  public void setData(final List<Busyhourmapping> mappings) {

    if (this.data == null) {
      this.data = new Vector<Busyhourmapping>();
      this.modifiedData = new ArrayList<Busyhourmapping>();
      if (deletedData == null)
        this.deletedData = new ArrayList<Busyhourmapping>();
    } else {
      clear();
    }
    for (Busyhourmapping map : mappings) {
      addRow(map);
      modifiedData.add(map);
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

  @Override
  public void startEditing() {
    // Take a copy of the data to be able to revert back to old data if editing
    // is cancelled.
    copyOfTheData = new Vector<Busyhourmapping>();
    for (Busyhourmapping original : data) {
      copyOfTheData.add((Busyhourmapping) original.clone());
    }
  }

  @Override
  public void stopEditing() {
    // Clear the copy of the data.
    copyOfTheData = null;
  }

  @Override
  public void cancelEditing() {
    // Revert to the copy of the data.
    setData(copyOfTheData);
  }

  @Override
  public RockDBObject createNew() {
    final Busyhourmapping mapping = new Busyhourmapping(rockFactory);
    // mapping.setVersionid(busyhour.getVersionid());
    // mapping.setTargetversionid(busyhour.getTargetversionid());
    // mapping.setBhlevel(busyhour.getBhlevel());
    // mapping.setBhobject(busyhour.getBhobject());
    // mapping.setBhtype(busyhour.getBhtype());
    // mapping.setTypeid(TYPEID);
    // mapping.setBhtargettype(BHTARGETTYPE);
    // mapping.setBhtargetlevel(BHTARGETLEVEL);
    // mapping.setEnable(ENABLE);
    return mapping;
  }

  @Override
  public void duplicateRow(final int[] selectedRows, final int times) {
    Object obj = null;
    for (int i = 0; i < times; i++) {
      for (int j = 0; j < selectedRows.length; j++) {
        obj = data.elementAt(selectedRows[j]);
        insertDataLast(copyOf(obj));
      }
    }
  }

  private Object copyOf(final Object toBeCopied) {
    final Busyhourmapping orig = (Busyhourmapping) toBeCopied;
    final Busyhourmapping copy = (Busyhourmapping) orig.clone();
    copy.setNewItem(true);
    return copy;
  }

  @Override
  public JPopupMenu getPopUpMenu(final TableComponentListener listener, final Component component) {

    JTable table = null;
    if (component instanceof JTable) {
      table = (JTable) component;
    } else if (component instanceof JTableHeader) {
      table = (JTable) ((JTableHeader) component).getTable();
    } else {
      System.out.println(component);
      return null;
    }

    MappingTableListener myListener = new MappingTableListener(null, null, table);

    JPopupMenu popupTPD;
    JMenuItem miTPD;
    popupTPD = new JPopupMenu();

    miTPD = new JMenuItem("Pick All");
    miTPD.setActionCommand(MappingTableListener.PICK_ALL);
    miTPD.addActionListener(myListener);
    popupTPD.add(miTPD);

    miTPD = new JMenuItem("Discard All");
    miTPD.setActionCommand(MappingTableListener.DISC_ALL);
    miTPD.addActionListener(myListener);
    popupTPD.add(miTPD);

    miTPD = new JMenuItem("Pick Selected");
    miTPD.setActionCommand(MappingTableListener.PICK);
    miTPD.addActionListener(myListener);
    miTPD.setEnabled(table.getSelectedRowCount() > 0);
    popupTPD.add(miTPD);

    miTPD = new JMenuItem("Discard Selected");
    miTPD.setActionCommand(MappingTableListener.DISC);
    miTPD.addActionListener(myListener);
    miTPD.setEnabled(table.getSelectedRowCount() > 0);
    popupTPD.add(miTPD);

    popupTPD.setOpaque(true);
    popupTPD.setLightWeightPopupEnabled(true);

    return popupTPD;

  }

  @Override
  public TableModel getTableModel() {
    return this;
  }

  @Override
  public void insertDataAtRow(final Object datum, final int index) {
    data.insertElementAt((Busyhourmapping) datum, index);
    this.fireTableDataChanged();
  }

  @Override
  public void insertDataLast(final Object datum) {
    data.insertElementAt((Busyhourmapping) datum, data.size());
    this.fireTableDataChanged();
  }

  @Override
  public void removeSelectedData(final int[] selectedRows) {
    for (int i = selectedRows.length - 1; i >= 0; i--) {
      final Busyhourmapping del = removeRow(selectedRows[i]);
      deletedData.add(del);
    }
  }

  public void removeAllData() {
    for (Busyhourmapping bhKey : data) {
      deletedData.add(bhKey);
    }
    modifiedData.clear();
    data.clear();
  }

  @Override
  public void setColumnEditors(final JTable editTable) {

    final TableColumn colType = editTable.getColumnModel().getColumn(typeColumnIdx);
    colType.setCellEditor(new DefaultCellEditor(new JTextField()));

    final TableColumn colEnable = editTable.getColumnModel().getColumn(enableColumnIdx);
    colEnable.setCellEditor(new DefaultCellEditor(new JCheckBox()));

  }

  @Override
  public void setColumnRenderers(final JTable editTable) {
    // Use a default renderer for the type column.

    // Use a custom renderer for the editable column.
    final TableColumn colEnable = editTable.getColumnModel().getColumn(enableColumnIdx);
    colEnable.setCellRenderer(new CustomCheckBoxRenderer());
  }

  public void removeFromDB() {
    try {
      for (Busyhourmapping key : deletedData) {
        key.deleteDB();
      }
      for (Busyhourmapping key : data) {
        key.deleteDB();
      }
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
  }

  @Override
  public Vector<String> validateData() {
    Vector<String> errorStrings = new Vector<String>();
    return errorStrings;
  }

  @Override
  public Object clone() {
    return null;
  }

  /**
   * Customised listener for the mapping table model.
   * 
   * @author eheitur
   * 
   */
  private class MappingTableListener extends TableComponentListener implements ActionListener {

    public static final String PICK = "Pick";

    public static final String DISC = "Discard";

    public static final String PICK_ALL = "Pick All";

    public static final String DISC_ALL = "Discard All";

    // private final JDialog dialog;

    private final JTable table;

    // private SubTableModel model;

    public MappingTableListener(JDialog dialog, SubTableModel model, JTable table) {
      super(dialog, model, table);
      // this.dialog = dialog;
      // this.model = model;
      this.table = table;
    }

    public void actionPerformed(final ActionEvent ae) {

      if (ae.getActionCommand().equals(PICK)) {
        pick();
      } else if (ae.getActionCommand().equals(DISC)) {
        disc();
      } else if (ae.getActionCommand().equals(PICK_ALL)) {
        pickall();
      } else if (ae.getActionCommand().equals(DISC_ALL)) {
        discall();
      }
      fireTableDataChanged();
    }

    public void disc() {

      int[] selectedRows = table.getSelectedRows();
      for (int row : selectedRows) {
        table.getModel().setValueAt(new Boolean(false), row, 1);
      }
    }

    public void pick() {

      int[] selectedRows = table.getSelectedRows();
      for (int row : selectedRows) {
        table.getModel().setValueAt(new Boolean(true), row, 1);
      }
    }

    public void discall() {
      for (int i = 0; i < table.getModel().getRowCount(); i++) {
        table.getModel().setValueAt(new Boolean(false), i, 1);
      }
    }

    public void pickall() {
      for (int i = 0; i < table.getModel().getRowCount(); i++) {
        table.getModel().setValueAt(new Boolean(true), i, 1);
      }
    }
  }

  // private void displayTPDMenu(MouseEvent e) {
  //
  // if (e.isPopupTrigger()) {
  // createTPDPopup(e).show(e.getComponent(), e.getX(), e.getY());
  // }
  // }

  /**
   * A custom renderer for the enable column check box. Background colour is set
   * when the table row is selected.
   * 
   * @author eheitur
   */
  private class CustomCheckBoxRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

      JCheckBox cb = new JCheckBox();

      cb.setSelected((Boolean) value);
      cb.setFocusable(false);

      if (isSelected) {
        cb.setBackground(table.getSelectionBackground());
      } else {
        cb.setBackground(table.getBackground());
      }

      return cb;
    }
  }

}
