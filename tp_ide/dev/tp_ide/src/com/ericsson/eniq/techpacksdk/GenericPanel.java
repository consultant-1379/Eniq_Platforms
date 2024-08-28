/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * The purpose of this class is to provide memoryleak-safe way to use long
 * living tablemodel objects as model of the table which is put to JPanel.
 * 
 * @author etogust
 * 
 */
public class GenericPanel extends JPanel {

  static final long serialVersionUID = 1L;

  /**
   * Map where tables are matched to their listeners
   */
  private Map<AbstractTableModel, List<TableModelListener>> addedListeners = new HashMap<AbstractTableModel, List<TableModelListener>>();

  private Map<JTable, AbstractTableModel> tableModels = new HashMap<JTable, AbstractTableModel>();

  /**
   * Default Constructor
   */
  public GenericPanel() {
  }

  /**
   * Forward to super
   */
  public GenericPanel(GridBagLayout gbl) {
    super(gbl);
  }

  /**
   * Adds listener to table and maps table to listener
   * 
   * @param table
   * @param tml
   */
  public void addTableModelListener(AbstractTableModel tableModel, TableModelListener tml) {
    tableModel.addTableModelListener(tml);

    List<TableModelListener> listeners = addedListeners.get(tableModel);

    if (listeners == null)
      listeners = new ArrayList<TableModelListener>();

    listeners.add(tml);

    addedListeners.put(tableModel, listeners);
  }

  /**
   * Sets model of the table and maps model to table
   * 
   * @param table
   * @param atm
   */
  public void setModel(JTable table, AbstractTableModel atm) {
    table.setModel(atm);
    tableModels.put(table, atm);

  }

  /**
   * This should get called, when Panel is disposed. Removes all added
   * listeners, also the table, in order to free this panel to GC.
   * 
   */
  public void cleanUp() {
    // remove table from listener list
    Set<JTable> tableKeys = tableModels.keySet();
    for (JTable t : tableKeys) {
      AbstractTableModel atm = tableModels.get(t);
      atm.removeTableModelListener(t);
    }

    // remove all the added listeners
    Set<AbstractTableModel> tableModelKeys = addedListeners.keySet();
    for (AbstractTableModel atm : tableModelKeys) {

      List<TableModelListener> listeners = addedListeners.get(atm);

      for (TableModelListener tml : listeners)
        atm.removeTableModelListener(tml);
    }
  }

  /**
   * Parent removes this component, call clean up, so the long living tablemodel
   * object will have same size as before this panel
   */
  public void removeNotify() {
    super.removeNotify();
    cleanUp();
  }

  /**
   * Testing purposes only.
   */
  public void finalize() throws Throwable{
    super.finalize();
  }

}
