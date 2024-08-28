package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Utils;

@SuppressWarnings("serial")
public class UniverseExtCellExpandableSubTable extends JPanel {

  private static final Logger logger = Logger.getLogger(UniverseExtCellExpandableSubTable.class.getName());

  private JTextField textField;

  private JButton button;

  private EditDialog editDialog;

  private CellEditor cellEditor;

  //private boolean editable;

  private String[] universeExtensionOptions;

  public static final String ADD = "Add Empty";

  public static final String DELETE = "Delete";

  public static final String MOVE_UP = "Move Up";

  public static final String MOVE_DOWN = "Move Down";

  public static final String DUPLICATE = "Duplicate";

  public UniverseExtCellExpandableSubTable(CellEditor cellEditor, String[] universeExtensionOptions, boolean editable) {
    this.cellEditor = cellEditor;
    this.universeExtensionOptions = universeExtensionOptions;
    //this.editable = editable;

    GridBagLayout gridBagLayout = new GridBagLayout();
    this.setLayout(gridBagLayout);

    // Create grid bag constraints object
    GridBagConstraints c = new GridBagConstraints();

    // Create a limited-sized textfield and add it to the panel
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1;
    c.weightx = 1;
    this.textField = new JTextField();
    // The text field is never directly editable.
    this.textField.setEditable(false);
    this.add(textField, c);

    // Create a button and add it to the panel
    c.anchor = GridBagConstraints.NORTHEAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1;
    c.weightx = 0;
    this.button = new JButton("...");
    ActionListener buttonListener = new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        displayEditDialog();
      }
    };
    this.button.addActionListener(buttonListener);
    this.add(button, c);
  }

  private void displayEditDialog() {
    // Dispose the possible previous dialog
    if (this.editDialog != null) {
      this.editDialog.dispose();
    }

    // Open a new edit dialog
    this.editDialog = new EditDialog(this.textField);
    Utils.center(this.editDialog);
    this.editDialog.setVisible(true);
  }

  public String getText() {
    return this.textField.getText();
  }

  public void setText(String t) {
    this.textField.setText(t);
  }

  private class EditDialog extends JDialog {

    // private static final String EDIT_DIALOG_CAPTION = "Edit";

    // private static final String VIEW_DIALOG_CAPTION = "View";

    private static final String OK_BUTTON_TEXT = "OK";

    private static final String CANCEL_BUTTON_TEXT = "Cancel";

    private static final int PREFERRED_EDIT_PANEL_WIDTH = 300;

    private static final int PREFERRED_EDIT_PANEL_HEIGHT = 200;

    private JPanel editPanel;

    private JPanel buttonPanel;

    private JTable table;

    private UniverseExtTableModel tableModel;

    private JButton okButton;

    private JButton cancelButton;

    private JTextField targetTextField;

    public EditDialog(JTextField targetTextField) {
      this.targetTextField = targetTextField;

      // Create a grid bag layout instance. This instance will be reused
      // with all the dialog's components.
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      this.setLayout(gridBagLayout);

      // Create the edit panel and its contents
      this.editPanel = new JPanel(gridBagLayout);

      this.tableModel = new UniverseExtTableModel(this.targetTextField.getText());
      this.table = createTable(tableModel);
      JScrollPane scrollPane = new JScrollPane(this.table);
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      Dimension preferredSize = new Dimension(PREFERRED_EDIT_PANEL_WIDTH, PREFERRED_EDIT_PANEL_HEIGHT);
      scrollPane.setPreferredSize(preferredSize);

      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(0, 0, 0, 0);
      c.weightx = 1;
      c.weighty = 1;
      this.editPanel.add(scrollPane, c);

      // Create the button panel and its contents
      this.buttonPanel = new JPanel(gridBagLayout);

      this.okButton = new JButton(OK_BUTTON_TEXT);
      this.okButton.addActionListener(new OkButtonActionListener());

      this.cancelButton = new JButton(CANCEL_BUTTON_TEXT);
      this.cancelButton.addActionListener(new CancelButtonActionListener());

      c.anchor = GridBagConstraints.EAST;
      c.fill = GridBagConstraints.NONE;
      c.insets = new Insets(2, 2, 2, 2);
      this.buttonPanel.add(this.okButton, c);

      c.anchor = GridBagConstraints.EAST;
      c.fill = GridBagConstraints.NONE;
      c.insets = new Insets(2, 2, 2, 2);
      this.buttonPanel.add(this.cancelButton, c);

      // Add the panels to the dialog
      c.anchor = GridBagConstraints.NORTH;
      c.fill = GridBagConstraints.BOTH;
      c.insets = new Insets(0, 0, 0, 0);
      c.gridy = 0;
      c.weightx = 1;
      c.weighty = 1;
      this.add(this.editPanel, c);

      c.anchor = GridBagConstraints.SOUTHEAST;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.insets = new Insets(0, 0, 0, 0);
      c.gridy = 1;
      c.weightx = 1;
      c.weighty = 0;
      this.add(this.buttonPanel, c);

      this.pack();
    }

    private JTable createTable(TableModel myTableModel) {
      JTable result = new JTable();
      result.setModel(myTableModel);

      TableSelectionListener listener = new TableSelectionListener();
      this.tableModel.addTableModelListener(listener);
      result.addMouseListener(listener);
      result.getTableHeader().addMouseListener(listener);

      setColumnWidths(result);
      setColumnEditors(result);
      setColumnRenderers(result);

      return result;
    }

    private void setColumnEditors(JTable table) {
      TableColumnModel tableColumnModel = table.getColumnModel();

      TableColumn extColumn = tableColumnModel.getColumn(UniverseExtTableModel.EXT_COLUMN_INDEX);
      JComboBox extComboBox = new JComboBox(universeExtensionOptions);
      extComboBox.setEditable(true);
      DefaultCellEditor extColumnEditor = new DefaultCellEditor(extComboBox);
      extColumn.setCellEditor(extColumnEditor);
    }

    private void setColumnRenderers(JTable table) {
      TableColumnModel tableColumnModel = table.getColumnModel();

      TableColumn typenameColumn = tableColumnModel.getColumn(UniverseExtTableModel.EXT_COLUMN_INDEX);
      int typeNameSizeLimit = 255;
      LimitedSizeTextTableCellRenderer typenameColumnRenderer = new LimitedSizeTextTableCellRenderer(typeNameSizeLimit,
          true);
      typenameColumn.setCellRenderer(typenameColumnRenderer);

      // TableColumn nameColumn =
      // tableColumnModel.getColumn(UniverseExtTableModel.NAME_COLUMN_INDEX);
      // int nameSizeLimit = 255;
      // LimitedSizeTextTableCellRenderer nameColumnRenderer = new
      // LimitedSizeTextTableCellRenderer(nameSizeLimit, true);
      // nameColumn.setCellRenderer(nameColumnRenderer);
    }

    private void setColumnWidths(JTable table) {
      // For each table column, set the column's width to the corresponding
      // value in column widths array.
      for (int i = 0; i < this.tableModel.columnWidthArr.length; ++i) {
        int width = this.tableModel.columnWidthArr[i];
        TableColumn column = table.getColumnModel().getColumn(i);
        column.setPreferredWidth(width);
      }
    }

    private void updateTextField() {
      String text = this.tableModel.toString();
      this.targetTextField.setText(text);
    }

    private JPopupMenu createPopupMenu(MouseEvent e) {
      JPopupMenu popupMenu;
      JMenuItem menuItem;
      popupMenu = new JPopupMenu();

      int selected = table.getSelectedRow();

      menuItem = new JMenuItem(ADD);
      menuItem.setText(ADD);
      menuItem.setActionCommand(ADD);
      menuItem.addActionListener(new PopUpMenuActionListener());
      popupMenu.add(menuItem);

      if (e.getSource() instanceof JTable) {
        if (selected > -1) {
          menuItem = new JMenuItem(DELETE);
          menuItem.setText(DELETE);
          menuItem.setActionCommand(DELETE);
          menuItem.addActionListener(new PopUpMenuActionListener());
          popupMenu.add(menuItem);
          // TODO: Delete is now limited to one row, since the implementation
          // does not support deletion of many rows yet.
          if (table.getSelectedRowCount() > 1)
            menuItem.setEnabled(false);

          menuItem = new JMenuItem(DUPLICATE);
          menuItem.setText(DUPLICATE);
          menuItem.setActionCommand(DUPLICATE);
          menuItem.addActionListener(new PopUpMenuActionListener());
          popupMenu.add(menuItem);

          menuItem = new JMenuItem(MOVE_UP);
          menuItem.setText(MOVE_UP);
          menuItem.setActionCommand(MOVE_UP);
          menuItem.addActionListener(new PopUpMenuActionListener());
          popupMenu.add(menuItem);

          menuItem = new JMenuItem(MOVE_DOWN);
          menuItem.setText(MOVE_DOWN);
          menuItem.setActionCommand(MOVE_DOWN);
          menuItem.addActionListener(new PopUpMenuActionListener());
          popupMenu.add(menuItem);

        }
      }

      popupMenu.setOpaque(true);
      popupMenu.setLightWeightPopupEnabled(true);

      return popupMenu;
    }

    private void displayPopupMenu(MouseEvent e) {
      if (e.isPopupTrigger()) {
        JPopupMenu popupMenu = createPopupMenu(e);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }

    private class UniverseExtTableModel implements TableModel {

      public static final int NUMBER_COLUMN_INDEX = 0;

      public static final int EXT_COLUMN_INDEX = 1;

      Vector<String> document;

      Vector<TableModelListener> listeners;

      public int[] columnWidthArr = { 50, 150 };

      public UniverseExtTableModel() {
        this.document = new Vector<String>();
        this.listeners = new Vector<TableModelListener>();
      }

      public UniverseExtTableModel(String sourceString) {
        this.document = new Vector<String>();
        this.listeners = new Vector<TableModelListener>();

        String[] result = sourceString.split(",");
        for (int i = 0; i < result.length; i++)
          this.document.add(result[i].trim());
      }

      public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        int rows = this.getRowCount();
        for (int i = 0; i < rows; ++i) {
          stringBuffer.append(getValueAt(i, EXT_COLUMN_INDEX));
          if (i < rows - 1) {
            stringBuffer.append(",");
          }
         }
        return stringBuffer.toString();
      }

      public void addTableModelListener(TableModelListener l) {
        this.listeners.add(l);
      }

      public Class<?> getColumnClass(int columnIndex) {
        return String.class;
      }

      public int getColumnCount() {
        return 2;
      }

      public String getColumnName(int columnIndex) {
        if (columnIndex == NUMBER_COLUMN_INDEX) {
          return "Number";
        } else if (columnIndex == EXT_COLUMN_INDEX) {
          return "Universe Extension";
        } else {
          throw new ArrayIndexOutOfBoundsException("Invalid columnIndex value: " + columnIndex);
        }
      }

      public int getRowCount() {
        return document.size();
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == NUMBER_COLUMN_INDEX) {
          Integer value = new Integer(rowIndex);
          return value.toString();
        } else if (columnIndex == EXT_COLUMN_INDEX) {
          return document.elementAt(rowIndex);
        } else {
          throw new ArrayIndexOutOfBoundsException("Invalid columnIndex value: " + columnIndex);
        }
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == NUMBER_COLUMN_INDEX) {
          return false;
        } else {
          return true;
        }
      }

      public void removeTableModelListener(TableModelListener l) {
        this.listeners.remove(l);
      }

      public void addEmptyRow(int rowBefore) {
        this.document.add(rowBefore + 1, new String(""));
      }

      public void duplicateRow(int row) {
        String rowValue = new String(this.document.elementAt(row));
        this.document.add(this.document.size(), rowValue);
      }

      public void removeRow(int row) {
        this.document.remove(row);
      }

      /**
       * Moves a row up. Nothing happens if the first row is moved up.
       * 
       * @param row
       */
      public void moveRowUp(int row) {
        // The first row cannot be moved up.
        if (row <= 0) {
          return;
        }

        String rowValue = new String(this.document.elementAt(row));
        this.document.add(row - 1, rowValue);
        this.document.remove(row + 1);
      }

      /**
       * Moves a row down. Nothing happens if the last row is moved down.
       * 
       * @param row
       */
      public void moveRowDown(int row) {
        // The last row cannot be moved down.
        if (row >= this.document.size() - 1) {
          System.out.println("no move down for row: " + row);
          return;
        }
        String rowValue = new String(this.document.elementAt(row));
        this.document.add(row + 2, rowValue);
        this.document.remove(row);
      }

      public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == NUMBER_COLUMN_INDEX) {
          // Do nothing
        } else if (columnIndex == EXT_COLUMN_INDEX) {
          this.document.set(rowIndex, (String) value);
        } else {
          throw new ArrayIndexOutOfBoundsException("Invalid columnIndex value: " + columnIndex);
        }
        fireTableChanged(null);
      }

      private void fireTableChanged(TableModelEvent e) {
        Iterator<TableModelListener> listenersIterator = this.listeners.iterator();
        while (listenersIterator.hasNext()) {
          listenersIterator.next().tableChanged(e);
        }
      }
    }

    // private class AddEmptyRowActionListener implements ActionListener {
    //
    // public void actionPerformed(ActionEvent e) {
    // int selectedRow = table.getSelectedRow();
    // logger.info("Edit dialog: Adding an empty row after the row number " +
    // selectedRow + ".");
    // tableModel.addEmptyRow(selectedRow); // versionId will be set by the
    // // data model.
    // tableModel.fireTableChanged(null);
    // }
    // }
    //
    // private class DuplicateRowsActionListener implements ActionListener {
    //
    // public void actionPerformed(ActionEvent e) {
    // int selectedRow = table.getSelectedRow();
    // logger.info("Edit dialog: Duplicatig the row number " + selectedRow +
    // ".");
    // tableModel.duplicateRow(selectedRow);
    // tableModel.fireTableChanged(null);
    // }
    // }

    private class PopUpMenuActionListener implements ActionListener {

      /**
       * Callback method for action events, i.e. for when one of the menu
       * alternatives have been selected.
       * 
       * @param ae
       *          the action event
       */
      public void actionPerformed(ActionEvent ae) {

        if (ae.getActionCommand().equals(ADD)) {
          // Add row
          int selectedRow = table.getSelectedRow();
          logger.info("Universe parameters: Adding an empty row after the row number " + selectedRow + ".");
          tableModel.addEmptyRow(selectedRow); 

        } else if (ae.getActionCommand().equals(DELETE)) {
          // Delete row
          // TODO: Remove for many rows does not work, since the rows are
          // different after the first delete. Temporary fix is to limit the
          // delete to just one row in the pop-up menu.
          for (int i = 0; i < table.getSelectedRows().length; i++) {
            logger.info("Universe parameters: Removing row number " + table.getSelectedRows()[i] + ".");
            tableModel.removeRow(table.getSelectedRows()[i]);
          }
        } else if (ae.getActionCommand().equals(DUPLICATE)) {
          // Duplicate rows
          for (int i = 0; i < table.getSelectedRows().length; i++) {
            logger.info("Universe parameters: Duplicating the row number " + table.getSelectedRows()[i] + ".");
            tableModel.duplicateRow(table.getSelectedRows()[i]);
          }
        } else if (ae.getActionCommand().equals(MOVE_UP)) {
          // Move rows up
          for (int i = 0; i < table.getSelectedRows().length; i++) {
            logger.info("Universe parameters: Moving row number " + table.getSelectedRows()[i] + " up.");
            tableModel.moveRowUp(table.getSelectedRows()[i]);
          }
        } else if (ae.getActionCommand().equals(MOVE_DOWN)) {
          // Move rows down
          for (int i = table.getSelectedRows().length - 1; i >= 0; i--) {
            logger.info("Universe parameters: Moving row number " + table.getSelectedRows()[i] + " down.");
            tableModel.moveRowDown(table.getSelectedRows()[i]);
          }
        } else {
          // ERROR: Invalid action command
          System.out.println(this.getClass() + " actionPerformed(): Received invalid action command: "
              + ae.getActionCommand());
        }

        tableModel.fireTableChanged(null);
      }
    }

    private class OkButtonActionListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
        updateTextField();
        cellEditor.stopCellEditing();
        dispose();
      }
    }

    private class CancelButtonActionListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    }

    private class TableSelectionListener implements TableModelListener, ActionListener, MouseListener {

      public void tableChanged(TableModelEvent e) {
        setColumnRenderers(table);
        setColumnEditors(table);
        setColumnWidths(table);

		// Validate. In case errors, disable the ok-button and set the
		// tooltip as the first error message.
		Vector<String> errors = getErrors(EditDialog.this.table);
		if (errors != null) {
			EditDialog.this.okButton.setEnabled(false);
			EditDialog.this.okButton.setToolTipText(errors
					.firstElement());
		} else {
			EditDialog.this.okButton.setEnabled(true);
			EditDialog.this.okButton.setToolTipText("");
		}
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
       * )
       */
      public void actionPerformed(ActionEvent e) {

      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
       */
      public void mouseClicked(MouseEvent e) {
        displayPopupMenu(e);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
       */
      public void mouseEntered(MouseEvent e) {

      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
       */
      public void mouseExited(MouseEvent e) {

      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
       */
      public void mousePressed(MouseEvent e) {
        displayPopupMenu(e);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
       */
      public void mouseReleased(MouseEvent e) {
        displayPopupMenu(e);
      }

		/**
		 * Checks if there are errors in the current universe extension
		 * definitions and returns the error messages.
		 * 
		 * @param myTable
		 * @return vector of error strings. Null in case there are no
		 *         errors.
		 */
		private Vector<String> getErrors(JTable myTable) {
			int rows = myTable.getRowCount();
			int columns = myTable.getColumnCount();
			Vector<String> errors = new Vector<String>();

			// Check for empty values
			for (int row = 0; row < rows; ++row) {
				for (int column = 0; column < columns; ++column) {
					// Extension must not be empty
					if (column == UniverseExtTableModel.EXT_COLUMN_INDEX) {
						String strValue = (String) (myTable.getValueAt(row,
								column));
						if (strValue.trim().isEmpty()) {
							errors
									.add("Empty universe extension value found at row: "
											+ row);
						}
					}
				}
			}

			// Check for duplicates and if "ALL" is not used alone.
			for (int row = 0; row < rows; row++) {
				// Get current value
				String val1 = (String) (myTable.getValueAt(row,
						UniverseExtTableModel.EXT_COLUMN_INDEX));

				// Check that "ALL" is used alone.
				if (val1.equals("ALL")) {
					if (rows > 1) {
						errors
								.add("ALL must not be used together with other universe extensions.");
						break;
					}
				}

				// Check the remaining rows for duplicates of the current
				// value
				for (int row2 = row + 1; row2 < rows; row2++) {
					String val2 = (String) (myTable.getValueAt(row2,
							UniverseExtTableModel.EXT_COLUMN_INDEX));
					if (val1.equals(val2)) {
						// Duplicate found
						errors.add("Duplicate value: " + val1);
						break;
					}
				}
			}

			// Check that the total length of the universe extensions as
			// comma separated values, does not exceed the DB max limit
			// of 12 characters.
			int numberOfCharacters = 0;
			for (int row = 0; row < rows; row++) {
				// Add the length of the extension to the total number of
				// characters.
				numberOfCharacters = numberOfCharacters
						+ ((String) (myTable.getValueAt(row,
								UniverseExtTableModel.EXT_COLUMN_INDEX)))
								.length();
				// If this is not the last one, the add one for the comma.
				if (row < rows - 1) {
					numberOfCharacters++;
				}
			}
			if (numberOfCharacters > 12) {
				errors
						.add("Defined universe extensions exceed the maximum database limit: "
								+ numberOfCharacters + " > 12.");
			}
			if (errors.isEmpty())
				return null;
			else
				return errors;
		}
     }
  }

}
