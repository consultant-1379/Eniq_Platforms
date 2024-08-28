package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
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

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.measurement.MeasurementTypeData;

/**
 * This class is the table cell editor in the UniverseComputedObject table row
 * for the Parameters column and used for editing the related UniverseParameters
 * data.
 * 
 * @author epiituo
 * @author eheitur
 */
@SuppressWarnings("serial")
public class ParameterCellExpandableSubTable extends JPanel {

	private static final Logger logger = Logger
			.getLogger(ParameterCellExpandableSubTable.class.getName());

	public static final String UNIVERSEPARAMETERS = "Universe Parameters";

	private JTextField textField;

	private JButton button;

	private EditDialog editDialog;

	private CellEditor cellEditor;

	//private boolean editable;

	//private String versionId;

	private DataModelController dataModelController;

	public static final String ADD = "Add Empty";

	public static final String DELETE = "Delete";

	public static final String MOVE_UP = "Move Up";

	public static final String MOVE_DOWN = "Move Down";

	public static final String DUPLICATE = "Duplicate";

	// public ParameterCellExpandableSubTable(CellEditor cellEditor,
	// String[] typenameOptions, String[] nameOptions, boolean editable) {
	public ParameterCellExpandableSubTable(CellEditor cellEditor,
			DataModelController dataModelController, String versionId,
			boolean editable) {
		this.cellEditor = cellEditor;
		// this.typenameOptions = typenameOptions;
		// this.nameOptions = nameOptions;
		//this.editable = editable;
		this.dataModelController = dataModelController;
		//this.versionId = versionId;

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
		this.editDialog.setTitle(UNIVERSEPARAMETERS);
		Utils.center(this.editDialog);
		this.editDialog.setVisible(true);
	}

	public String getText() {
		return this.textField.getText();
	}

	public void setText(String t) {
		this.textField.setText(t);
	}

	/**
	 * The edit dialog for the universe parameters, including the table for the
	 * data and ok/cancel buttons.
	 * 
	 * @author eheitur
	 * 
	 */
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

		private ParameterTableModel tableModel;

		private JButton okButton;

		private JButton cancelButton;

		private JTextField targetTextField;

		private String versionId;

		//private int modifiedRow = -1;

		// private String[] typenameOptions;

		// private String[] nameOptions;

		/**
		 * Constructor.
		 * 
		 * @param targetTextField
		 *            The text field containing the model as a string.
		 * @param typenameOptions
		 *            The measurement type names for this techpack
		 * @param versionId
		 *            The versionId of the techpack. Used for getting the
		 *            measurement type names + keys and counters.
		 */
		public EditDialog(JTextField targetTextField) {
			this.targetTextField = targetTextField;
			// this.typenameOptions = typenameOptions;

			// Create a grid bag layout instance. This instance will be reused
			// with all the dialog's components.
			GridBagLayout gridBagLayout = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			this.setLayout(gridBagLayout);

			// Create the edit panel and its contents
			this.editPanel = new JPanel(gridBagLayout);

			this.tableModel = new ParameterTableModel(this.targetTextField
					.getText());
			this.table = createTable(tableModel);
			JScrollPane scrollPane = new JScrollPane(this.table);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			Dimension preferredSize = new Dimension(PREFERRED_EDIT_PANEL_WIDTH,
					PREFERRED_EDIT_PANEL_HEIGHT);
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
			this.cancelButton
					.addActionListener(new CancelButtonActionListener());

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
			JTable table = new JTable();
			table.setModel(myTableModel);

			TableSelectionListener listener = new TableSelectionListener();
			this.tableModel.addTableModelListener(listener);
			table.addMouseListener(listener);
			table.getTableHeader().addMouseListener(listener);

			setColumnWidths(table);
			setColumnEditors(table);
			setColumnRenderers(table);

			return table;
		}

		private void setColumnEditors(JTable table) {
			TableColumnModel tableColumnModel = table.getColumnModel();

			TableColumn typenameColumn = tableColumnModel
					.getColumn(ParameterTableModel.TYPENAME_COLUMN_INDEX);
			JComboBox typenameComboBox = new JComboBox(getMeasTypeNames());
			typenameComboBox.setEditable(true);
			DefaultCellEditor typenameColumnEditor = new DefaultCellEditor(
					typenameComboBox);
			typenameColumn.setCellEditor(typenameColumnEditor);

			TableColumn nameColumn = tableColumnModel
					.getColumn(ParameterTableModel.NAME_COLUMN_INDEX);

			// The values in the name nameComboBox depend on the
			// typename value in the same row.
			JComboBox nameComboBox = null;

			// if (modifiedRow > -1) {
			if (table.getSelectedRow() > -1) {
				// System.out.println("setColumnEditors(): DEBUG: Selected row: "
				// + table.getSelectedRow());

				String selectedMeasType = (String) tableModel.getValueAt(table
						.getSelectedRow(),
						ParameterTableModel.TYPENAME_COLUMN_INDEX);

				// System.out.println("setColumnEditors(): DEBUG: typename: "
				// + selectedMeasType);

				String[] keysAndCounters = getMeasTypeKeysAndCounters(selectedMeasType);
				if (keysAndCounters != null) {
					nameComboBox = new JComboBox(keysAndCounters);
				} else
					nameComboBox = new JComboBox();

			} else {
				// System.out
				// .println("setColumnEditors(): DEBUG: No typenameComboBox selection: ");

				nameComboBox = new JComboBox(new String[0]);
			}

			nameComboBox.setEditable(true);
			DefaultCellEditor nameColumnEditor = new DefaultCellEditor(
					nameComboBox);
			nameColumn.setCellEditor(nameColumnEditor);
		}

		private void setColumnRenderers(JTable table) {
			TableColumnModel tableColumnModel = table.getColumnModel();

			TableColumn typenameColumn = tableColumnModel
					.getColumn(ParameterTableModel.TYPENAME_COLUMN_INDEX);
			int typeNameSizeLimit = 255;
			LimitedSizeTextTableCellRenderer typenameColumnRenderer = new LimitedSizeTextTableCellRenderer(
					typeNameSizeLimit, true);
			typenameColumn.setCellRenderer(typenameColumnRenderer);

			TableColumn nameColumn = tableColumnModel
					.getColumn(ParameterTableModel.NAME_COLUMN_INDEX);
			int nameSizeLimit = 255;
			LimitedSizeTextTableCellRenderer nameColumnRenderer = new LimitedSizeTextTableCellRenderer(
					nameSizeLimit, true);
			nameColumn.setCellRenderer(nameColumnRenderer);
		}

		private void setColumnWidths(JTable table) {
			// For each table column, set the column's width to the
			// corresponding
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
					// TODO: Delete is now limited to one row, since the
					// implementation
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

		private class ParameterTableModel implements TableModel {

			private class ParameterValue {

				public String typename;

				public String name;

				public ParameterValue(String typename, String name) {
					this.typename = typename;
					this.name = name;
				}
			}

			public static final int NUMBER_COLUMN_INDEX = 0;

			public static final int TYPENAME_COLUMN_INDEX = 1;

			public static final int NAME_COLUMN_INDEX = 2;

			Vector<ParameterValue> document;

			Vector<TableModelListener> listeners;

			public int[] columnWidthArr = { 50, 150, 150 };

			public ParameterTableModel() {
				this.document = new Vector<ParameterValue>();
				this.listeners = new Vector<TableModelListener>();
			}

			public ParameterTableModel(String sourceString) {
				this.document = new Vector<ParameterValue>();
				this.listeners = new Vector<TableModelListener>();

				StringTokenizer stringTokenizer = new StringTokenizer(
						sourceString, "[");
				while (stringTokenizer.hasMoreTokens()) {
					String parameterToken = stringTokenizer.nextToken(); // typename,
					// name]

					int closingBracketIndex = parameterToken.indexOf(']');
					parameterToken = parameterToken.substring(0,
							closingBracketIndex);

					StringTokenizer parameterTokenTokenizer = new StringTokenizer(
							parameterToken, ",");
					String typenameToken = parameterTokenTokenizer.nextToken()
							.trim(); // typename
					String nameToken = parameterTokenTokenizer.nextToken()
							.trim(); // name
					this.document.add(new ParameterValue(typenameToken,
							nameToken));
				}
			}

			public String toString() {
				StringBuffer stringBuffer = new StringBuffer();
				int rows = this.getRowCount();
				for (int i = 0; i < rows; ++i) {
					stringBuffer.append("[");
					stringBuffer.append(getValueAt(i, TYPENAME_COLUMN_INDEX));
					stringBuffer.append(", ");
					stringBuffer.append(getValueAt(i, NAME_COLUMN_INDEX));
					stringBuffer.append("] ");
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
				return 3;
			}

			public String getColumnName(int columnIndex) {
				if (columnIndex == NUMBER_COLUMN_INDEX) {
					return "Number";
				} else if (columnIndex == TYPENAME_COLUMN_INDEX) {
					return "Typename";
				} else if (columnIndex == NAME_COLUMN_INDEX) {
					return "Name";
				} else {
					throw new ArrayIndexOutOfBoundsException(
							"Invalid columnIndex value: " + columnIndex);
				}
			}

			public int getRowCount() {
				return document.size();
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == NUMBER_COLUMN_INDEX) {
					Integer value = new Integer(rowIndex);
					return value.toString();
				} else if (columnIndex == TYPENAME_COLUMN_INDEX) {
					ParameterValue rowValue = document.elementAt(rowIndex);
					return rowValue.typename;
				} else if (columnIndex == NAME_COLUMN_INDEX) {
					ParameterValue rowValue = document.elementAt(rowIndex);
					return rowValue.name;
				} else {
					throw new ArrayIndexOutOfBoundsException(
							"Invalid columnIndex value: " + columnIndex);
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
				this.document.add(rowBefore + 1, new ParameterValue("", ""));
			}

			public void duplicateRow(int row) {
				ParameterValue rowValue = new ParameterValue(this.document
						.elementAt(row).typename,
						this.document.elementAt(row).name);
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

				ParameterValue rowValue = new ParameterValue(this.document
						.elementAt(row).typename,
						this.document.elementAt(row).name);
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
				ParameterValue rowValue = new ParameterValue(this.document
						.elementAt(row).typename,
						this.document.elementAt(row).name);
				this.document.add(row + 2, rowValue);
				this.document.remove(row);
			}

			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if (columnIndex == NUMBER_COLUMN_INDEX) {
					// Do nothing
				} else if (columnIndex == TYPENAME_COLUMN_INDEX) {
					String strValue = (String) value;
					ParameterValue rowValue = this.document.get(rowIndex);
					rowValue.typename = strValue;
					this.document.set(rowIndex, rowValue);
				} else if (columnIndex == NAME_COLUMN_INDEX) {
					String strValue = (String) value;
					ParameterValue rowValue = this.document.get(rowIndex);
					rowValue.name = strValue;
					this.document.set(rowIndex, rowValue);
				} else {
					throw new ArrayIndexOutOfBoundsException(
							"Invalid columnIndex value: " + columnIndex);
				}
				fireTableChanged(new TableModelEvent(tableModel, rowIndex,
						rowIndex, columnIndex));
			}

			private void fireTableChanged(TableModelEvent e) {
				Iterator<TableModelListener> listenersIterator = this.listeners
						.iterator();
				while (listenersIterator.hasNext()) {
					listenersIterator.next().tableChanged(e);
				}
			}
		}

		// private class AddEmptyRowActionListener implements ActionListener {
		//
		// public void actionPerformed(ActionEvent e) {
		// int selectedRow = table.getSelectedRow();
		// logger.info("Edit dialog: Adding an empty row after the row number "
		// +
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
			 *            the action event
			 */
			public void actionPerformed(ActionEvent ae) {

				if (ae.getActionCommand().equals(ADD)) {
					// Add row
					int selectedRow = table.getSelectedRow();
					logger
							.info("Universe parameters: Adding an empty row after the row number "
									+ selectedRow + ".");
					tableModel.addEmptyRow(selectedRow); // versionId will be
					// set by the
					// data model.

				} else if (ae.getActionCommand().equals(DELETE)) {
					// Delete row
					// TODO: Remove for many rows does not work, since the rows
					// are
					// different after the first delete. Temporary fix is to
					// limit the
					// delete to just one row in the pop-up menu.
					for (int i = 0; i < table.getSelectedRows().length; i++) {
						logger.info("Universe parameters: Removing row number "
								+ table.getSelectedRows()[i] + ".");
						tableModel.removeRow(table.getSelectedRows()[i]);
					}
				} else if (ae.getActionCommand().equals(DUPLICATE)) {
					// Duplicate rows
					for (int i = 0; i < table.getSelectedRows().length; i++) {
						logger
								.info("Universe parameters: Duplicating the row number "
										+ table.getSelectedRows()[i] + ".");
						tableModel.duplicateRow(table.getSelectedRows()[i]);
					}
				} else if (ae.getActionCommand().equals(MOVE_UP)) {
					// Move rows up
					for (int i = 0; i < table.getSelectedRows().length; i++) {
						logger.info("Universe parameters: Moving row number "
								+ table.getSelectedRows()[i] + " up.");
						tableModel.moveRowUp(table.getSelectedRows()[i]);
					}
				} else if (ae.getActionCommand().equals(MOVE_DOWN)) {
					// Move rows down
					for (int i = table.getSelectedRows().length - 1; i >= 0; i--) {
						logger.info("Universe parameters: Moving row number "
								+ table.getSelectedRows()[i] + " down.");
						tableModel.moveRowDown(table.getSelectedRows()[i]);
					}
				} else {
					// ERROR: Invalid action command
					System.out
							.println(this.getClass()
									+ " actionPerformed(): Received invalid action command: "
									+ ae.getActionCommand());
				}

				tableModel.fireTableChanged(new TableModelEvent(tableModel));
			}
		}

		// /**
		// * Listener for the table row removal.
		// *
		// * @author eheitur
		// *
		// */
		// private class RemoveRowsActionListener implements ActionListener {
		//
		// public void actionPerformed(ActionEvent e) {
		// for (int i = 0; i < table.getSelectedRows().length; i++) {
		// logger.info("Edit dialog: Removing row number " +
		// table.getSelectedRows()[i] + ".");
		// tableModel.removeRow(table.getSelectedRows()[i]);
		// }
		// tableModel.fireTableChanged(null);
		// }
		// }
		//
		// private class MoveRowsUpActionListener implements ActionListener {
		//
		// public void actionPerformed(ActionEvent e) {
		// for (int i = 0; i < table.getSelectedRows().length; i++) {
		// logger.info("Edit dialog: Moving row number " +
		// table.getSelectedRows()[i] + " up.");
		// tableModel.moveRowUp(table.getSelectedRows()[i]);
		// }
		// tableModel.fireTableChanged(null);
		// }
		// }
		//
		// private class MoveRowsDownActionListener implements ActionListener {
		//
		// public void actionPerformed(ActionEvent e) {
		// for (int i = 0; i < table.getSelectedRows().length; i++) {
		// logger.info("Edit dialog: Moving row number " +
		// table.getSelectedRows()[i] + " down.");
		// tableModel.moveRowDown(table.getSelectedRows()[i]);
		// }
		// tableModel.fireTableChanged(null);
		// }
		// }

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

		private class TableSelectionListener implements TableModelListener,
				ActionListener, MouseListener {

			public void tableChanged(TableModelEvent e) {

				// System.out.println("tableChanged(): DEBUG!");

				setColumnRenderers(table);
				setColumnEditors(table);
				setColumnWidths(table);

				// Validate the data and disable the Ok-button in case
				// validation failed.
				if (!isValid(EditDialog.this.table)) {
					EditDialog.this.okButton.setEnabled(false);
				} else {
					EditDialog.this.okButton.setEnabled(true);
				}

				if (e != null) {
					int column = e.getColumn();
					// System.out.println("tableChanged(): DEBUG: column "
					// + column);
					if (column == ParameterTableModel.TYPENAME_COLUMN_INDEX
							|| column == TableModelEvent.ALL_COLUMNS) {
						// Typename changed. Update the modified row index
						// System.out
						// .println("tableChanged(): DEBUG: modified row "
						// + e.getFirstRow());
						//modifiedRow = e.getFirstRow();
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent )
			 */
			public void actionPerformed(ActionEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseClicked(MouseEvent e) {
				displayPopupMenu(e);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseEntered(MouseEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseExited(MouseEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent
			 * )
			 */
			public void mousePressed(MouseEvent e) {
				displayPopupMenu(e);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseReleased(MouseEvent e) {
				displayPopupMenu(e);
			}

			private boolean isValid(JTable myTable) {
				boolean validValues = true;
				int rows = myTable.getRowCount();
				int columns = myTable.getColumnCount();
				for (int row = 0; row < rows; ++row) {
					for (int column = 0; column < columns; ++column) {
						// Typename or name columns can not be empty
						if (column == ParameterTableModel.NAME_COLUMN_INDEX
								|| column == ParameterTableModel.TYPENAME_COLUMN_INDEX) {
							String strValue = (String) (myTable.getValueAt(row,
									column));
							if (strValue.trim().isEmpty()) {
								validValues = false;
							}
						}
					}
				}
				return validValues;
			}
		}

		/**
		 * Gets the measurement type names for the current techpack.
		 * 
		 * @return a sorted string array of measurement type data names.
		 */
		private String[] getMeasTypeNames() {
			// Get the Measurement Type objects
			List<MeasurementTypeData> measurements = dataModelController
					.getMeasurementTypeDataModel().getMeasurements();

			String[] names = new String[measurements.size()];

			// Get the type names
			for (int i = 0; i < measurements.size(); ++i) {
				names[i] = measurements.get(i).getMeasurementtypeExt()
						.getTypename();
			}
			Arrays.sort(names);
			return names;
		}

		/**
		 * Gets the measurement key and counter names for a measurement type. If
		 * the measurement type with the name is not found, then null is
		 * returned.
		 * 
		 * @param measTypeName
		 *            The type name of the measurement type.
		 * @return a sorted string array of measurement type key and counter
		 *         names. Null in no such measurement type.
		 */
		private String[] getMeasTypeKeysAndCounters(String measTypeName) {

			// Get the Measurement Type object matching the measurement type
			// name
			List<MeasurementTypeData> measurements = dataModelController
					.getMeasurementTypeDataModel().getMeasurements();
			Measurementtype measType = null;
			for (int i = 0; i < measurements.size(); ++i) {
				if (measurements.get(i).getMeasurementtypeExt().getTypename()
						.equals(measTypeName)) {
					measType = measurements.get(i).getMeasurementtypeExt()
							.getMeasurementtype();
					break;
				}
			}

			// If the match was found, then get all the keys and counters for
			// that measurement type in a sorted array.
			if (measType != null) {
				String[] keys = getMeasurementKeyDataNames(versionId, measType
						.getTypeid());
				String[] counters = getMeasurementCounterDataNames(versionId,
						measType.getTypeid());
				String[] names = new String[keys.length + counters.length];
				System.arraycopy(keys, 0, names, 0, keys.length);
				System.arraycopy(counters, 0, names, keys.length,
						counters.length);
				Arrays.sort(names);
				return names;
			} else
				// No match, return null.
				return null;
		}

		/**
		 * Gets all the keys for a specific techpack version and the measurement
		 * type with a typeId.
		 * 
		 * @param versionId
		 *            Techpack version
		 * @param typeId
		 *            Measurement type typeId.
		 * @return A string array of the results. Null in case a DB error
		 *         occurs.
		 */
		public String[] getMeasurementKeyDataNames(String versionId,
				String typeId) {
			String[] result = null;
			Vector<String> resultVector = new Vector<String>();

			try {

				Measurementkey measurementKey = new Measurementkey(
						dataModelController.getRockFactory());
				measurementKey.setTypeid(typeId);
				MeasurementkeyFactory mF = new MeasurementkeyFactory(
						dataModelController.getRockFactory(), measurementKey,
						true);
				Iterator<Measurementkey> queryResults = mF.get().iterator();
				while (queryResults.hasNext()) {
					Measurementkey queryResult = queryResults.next();
					String dataName = queryResult.getDataname();
					if (!resultVector.contains(dataName)) {
						resultVector.add(dataName);
					}
				}
			} catch (SQLException e) {
				logger.warning("Unable to retrieve measurement keys.\n" + e);
			} catch (RockException e) {
				logger.warning("Unable to retrieve measurement keys.\n" + e);
			}

			result = new String[resultVector.size()];
			result = resultVector.toArray(result);
			return result;
		}

		/**
		 * Gets all the counters for a specific techpack version and the
		 * measurement type with a typeId.
		 * 
		 * @param versionId
		 *            Techpack version
		 * @param typeId
		 *            Measurement type typeId.
		 * @return A string array of the results. Null in case a DB error
		 *         occurs.
		 */
		public String[] getMeasurementCounterDataNames(String versionId,
				String typeId) {
			String[] result = null;
			Vector<String> resultVector = new Vector<String>();

			try {

				Measurementcounter measurementCounter = new Measurementcounter(
						dataModelController.getRockFactory());
				measurementCounter.setTypeid(typeId);
				MeasurementcounterFactory mF = new MeasurementcounterFactory(
						dataModelController.getRockFactory(),
						measurementCounter, true);
				Iterator<Measurementcounter> queryResults = mF.get().iterator();
				while (queryResults.hasNext()) {
					Measurementcounter queryResult = queryResults.next();
					String dataName = queryResult.getDataname();
					if (!resultVector.contains(dataName)) {
						resultVector.add(dataName);
					}
				}
			} catch (SQLException e) {
				logger
						.warning("Unable to retrieve measurement counters.\n"
								+ e);
			} catch (RockException e) {
				logger
						.warning("Unable to retrieve measurement counters.\n"
								+ e);
			}

			result = new String[resultVector.size()];
			result = resultVector.toArray(result);
			return result;
		}
	}
}
