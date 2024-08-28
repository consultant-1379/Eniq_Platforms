package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * Table cell renderer for a pair component including a description text field
 * and a edit button.
 * 
 * @author eheitur
 * 
 */
public class ConfigTableCellRenderer extends JPanel implements TableCellRenderer {

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
  public ConfigTableCellRenderer() {

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
    // gbc.insets = new Insets(2, 2, 2, 2);
    this.add(textField, gbc);

    // Edit button
    // editButton.setBorderPainted(false);
    gbc = new GridBagConstraints();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    // gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    // gbc.insets = new Insets(2, 2, 2, 2);
    this.add(editButton, gbc);
  }

  /**
   * Method for getting the renderer component. Sets the cell colouring and the
   * text value shown in the text box.
   */
  public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// Set the foreground and background colours for the panel and the text
		// field.
		// TODO: Is the set for the panel background and foreground colours
		// needed at all?
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
    Transformation transformation = (Transformation)value;
    textField.setText(transformation.getConfig());
		return this;
	}
}
