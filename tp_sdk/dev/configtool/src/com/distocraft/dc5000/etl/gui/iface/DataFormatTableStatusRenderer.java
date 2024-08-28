package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.distocraft.dc5000.etl.gui.TableSorter;
import com.distocraft.dc5000.etl.gui.schedule.ScheduleTableModel;
import com.distocraft.dc5000.etl.rock.Meta_schedulings;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;


public class DataFormatTableStatusRenderer extends JLabel implements TableCellRenderer, Serializable {

  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  
  public static final Color DISABLE_BACKGROUND = new Color(235,235,235);
  public static final Color DISABLE_FOREGROUND = Color.BLACK;

  public DataFormatTableStatusRenderer() {
    super();
    
    setOpaque(true);
    setBorder(noFocusBorder);
  }

  public void updateUI() {
    super.updateUI();
    setForeground(null);
    setBackground(null);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    
    TableSorter ts = (TableSorter)table.getModel();
    
    DataFormatTableModel dftm = (DataFormatTableModel)ts.getTableModel();
    
    Interfacemeasurement im = dftm.getMeasurementAt(ts.modelIndex(row));
        
    if (isSelected) {
      super.setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      if(im.getStatus().intValue() != 1) {
        super.setForeground(DISABLE_FOREGROUND);
        super.setBackground(DISABLE_BACKGROUND);
      } else {
        super.setForeground(table.getForeground());
        super.setBackground(table.getBackground());
      }
    }

    setFont(table.getFont());

    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
      if (table.isCellEditable(row, column)) {
        super.setForeground(UIManager.getColor("Table.focusCellForeground"));
        super.setBackground(UIManager.getColor("Table.focusCellBackground"));
      }
    } else {
      setBorder(noFocusBorder);
    }

    Integer ival = (Integer)value;
    
    if(ival != null) {
      if(ival.intValue() == 1) {
        setText("enabled");
      } else {
        setText("disabled");
      }
    } else {
      setText("null");
    }
    
    return this;
  }

  public boolean isOpaque() {
    Color back = getBackground();
    Component p = getParent();
    if (p != null) {
      p = p.getParent();
    }

    boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
    return !colorMatch && super.isOpaque();
  }

  public void validate() {}

  public void revalidate() {}

  public void repaint(long tm, int x, int y, int width, int height) {}

  public void repaint(Rectangle r) {}

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if (propertyName == "text") {
      super.firePropertyChange(propertyName, oldValue, newValue);
    }
  }

  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

  public static class UIResource extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource {}

}
