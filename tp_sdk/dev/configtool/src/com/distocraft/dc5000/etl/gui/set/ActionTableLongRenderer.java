package com.distocraft.dc5000.etl.gui.set;

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
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;


/**
 * @author lemminkainen
 * Copyright Distocraft 2005
 * 
 * $id$
 */
public class ActionTableLongRenderer extends JLabel implements TableCellRenderer, Serializable {

  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  
  public static final Color DISABLE_BACKGROUND = new Color(235, 235, 235);

  public static final Color DISABLE_FOREGROUND = Color.BLACK;


  public ActionTableLongRenderer() {
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
    
    TableSorter ts = (TableSorter) table.getModel();

    ActionTableModel stm = (ActionTableModel) ts.getTableModel();

    Meta_transfer_actions act = stm.getActionAt(ts.modelIndex(row));

    if (isSelected) {
      super.setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      if (act.getEnabled_flag().equalsIgnoreCase("y")) {
        super.setForeground(table.getForeground());
        super.setBackground(table.getBackground());
      } else {
        super.setBackground(DISABLE_BACKGROUND);
        super.setForeground(DISABLE_FOREGROUND);
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

    Long val = (Long)value;
    
    setText(val.toString());
    
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
