package com.ericsson.eniq.techpacksdk.common.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import com.ericsson.eniq.component.SubTableModel;
import com.ericsson.eniq.component.TableComponentListener;

/**
 * Customised listener for the mapping table model.
 * 
 * @author eheitur
 * 
 */

public class MappingTableUtils {
	
	private static final Logger LOGGER = Logger.getLogger(MappingTableUtils.class.getName());

  private class MappingTableListener extends TableComponentListener implements ActionListener {

    public static final String PICK = "Pick";

    public static final String DISC = "Discard";

    public static final String PICK_ALL = "Pick All";

    public static final String DISC_ALL = "Discard All";

    private final JTable table;

    public MappingTableListener(final JDialog dialog, final SubTableModel model, final JTable table) {
      super(dialog, model, table);
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
      // fireTableDataChanged();
      ((AbstractTableModel) this.table.getModel()).fireTableDataChanged();
    }

    public void disc() {

      final int[] selectedRows = table.getSelectedRows();
      for (int row : selectedRows) {
        table.getModel().setValueAt(Boolean.FALSE, row, 1);
      }
    }

    public void pick() {

      final int[] selectedRows = table.getSelectedRows();
      for (int row : selectedRows) {
        table.getModel().setValueAt(Boolean.TRUE, row, 1);
      }
    }

    public void discall() {
      for (int i = 0; i < table.getModel().getRowCount(); i++) {
        table.getModel().setValueAt(Boolean.FALSE, i, 1);
      }
    }

    public void pickall() {
      for (int i = 0; i < table.getModel().getRowCount(); i++) {
        table.getModel().setValueAt(Boolean.TRUE, i, 1);
      }
    }
  } // end private class
  
  public CustomCheckBoxRenderer createCustomCheckBoxRenderer() {
    return new CustomCheckBoxRenderer();
  }

  /**
   * A custom renderer for the enable column check box. Background colour is set
   * when the table row is selected.
   * 
   * @author eheitur
   */
  @SuppressWarnings("serial")
  private class CustomCheckBoxRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
        final int row, final int column) {

      final JCheckBox cb = new JCheckBox();

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
  
  /**
   * Creates a new popup menu.
   * @param listener
   * @param component
   * @return
   */
  public JPopupMenu createPopupMenu(final TableComponentListener listener, final Component component) {
  
    JTable table = null;
    if (component instanceof JTable) {
      table = (JTable) component;
    } else if (component instanceof JTableHeader) {
      table = (JTable) ((JTableHeader) component).getTable();
    } else {
    	if(component != null){
    		LOGGER.warning("Component : " + component + " is  not an instance of JTable or JTableHeader" );
    	}
      return null;
    }
  
    final MappingTableListener myListener = new MappingTableListener(null, null, table);
  
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
}
