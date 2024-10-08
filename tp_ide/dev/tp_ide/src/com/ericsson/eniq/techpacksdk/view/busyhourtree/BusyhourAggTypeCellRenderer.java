package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Table cell renderer for a busyhour ranktable (= bhlevel) including a description text field
 * and a edit button.
 * 
 * @author eheitur
 * @author eheijun
 * 
 */
public class BusyhourAggTypeCellRenderer extends JPanel implements TableCellRenderer {

  // Create a panel and a text field and an edit button.
  JTextField textField = new JTextField("dummy");

  JButton editButton = new JButton("...");

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor which adds the text field and the button to the panel.
   * 
   * @param items
   */
  public BusyhourAggTypeCellRenderer(boolean editable) {

    GridBagConstraints gbc = null;

    // Set the layout for the panel
    this.setLayout(new GridBagLayout());

    // Configure the layout and add the text field and the button to the
    // panel.

    // Text field
    textField.setBorder(null);
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    textField.setEditable(true);
    textField.setEnabled(true);
    this.add(textField, gbc);

    // Edit button
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    this.add(editButton, gbc);

  }

  /**
   * Method for getting the renderer component. Sets the cell colouring and the
   * text value shown in the text box.
   */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {

    // Set the foreground and background colours for the panel and the text
    // field.
    if (isSelected) {
      
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
      this.textField.setForeground(table.getSelectionForeground());
      this.textField.setBackground(table.getSelectionBackground());
      
    } else {
      
      setForeground(table.getForeground());
      setBackground(table.getBackground());
      this.textField.setForeground(table.getForeground());
      this.textField.setBackground(table.getBackground());
    }
    
    if (value instanceof Busyhour) {
      Busyhour bh = (Busyhour) value;
      textField.setText(Utils.showAggtypeGUI(bh.getAggregationtype()));
    } else {
      textField.setText("?");
    }
    
    textField.setEditable(false);
    
    return this;
  }
}
