package tableTreeUtils;

import java.awt.Color;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import tableTree.CustomParameterComponent;

import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Universe extension component is a custom parameter component, i.e. a pair of
 * a text field and an edit button. The text cannot be edited directly in the
 * text field, but in a separate editor window opened by the edit button. The
 * editor window contains a table for adding universe extension entries.
 * 
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class UniverseExtensionComponent extends JPanel implements
		CustomParameterComponent {

	private static final Logger logger = Logger
			.getLogger(UniverseExtensionComponent.class.getName());

	/**
	 * The frame for the edit pane. This is static so one frame is displayed at
	 * a time.
	 */
	static JFrame thisFrame = null;

	/**
     * 
     */
	public static final String UNIVERSEEXTENSION = "Universe Extensions";

	/**
	 * the component's text field width, measured in number of characters
	 */
	private static int defaultTextFieldWidth = 12;

	/**
   * 
   */
	private EditDialog editDialog;

	/**
	 * The text field for showing the description text
	 */
	JTextField textField = null;

	/**
	 * The edit button for opening the editor window
	 */
	JButton editButton = new JButton("...");

	/**
	 * Action listener for when this is used in parameter panels
	 */
	ActionListener myListener = null;

	/**
	 * Action command for when the action listener is triggered
	 */
	String actionCommand = null;

	/**
	 * Indicator if the component should be enabled or not. This is used when
	 * the text field and the ok-button are disabled in the read-only mode.
	 */
	boolean isEnabled = false;
	
	public static final String ADD = "Add Empty";

	public static final String DELETE = "Delete";

	public static final String MOVE_UP = "Move Up";

	public static final String MOVE_DOWN = "Move Down";

	public static final String DUPLICATE = "Duplicate";

	/**
	 * A list of all universe extensions defined in the techpack.
	 */
	private String[] universeExtensionOptions;

	/**
	 * Constructor. Initiates the panel (with the text field and the edit
	 * button) and the edit frame.
	 * 
	 * @param value
	 *            the initial text value
	 * @param textFieldWidth
	 *            the width of the text field in characters. In case equal or
	 *            less than zero is specified, then the default value in
	 *            TableTreeConstants will be used.
	 * @param universeExtensionOptions
	 *            The list of universe extensions defined in the techpack.
	 * 
	 */
	public UniverseExtensionComponent(final String value, int textFieldWidth,
			final String[] universeExtensionOptions) {
		super(new GridBagLayout());

		// Store the available universe extension options
		this.universeExtensionOptions = universeExtensionOptions;
		

		// Create the new text field
		textField = new JTextField(value);

		// If the given text field width is equal or less than zero, then the
		// default value is used.
		if (textFieldWidth <= 0) {
			// textFieldWidth = TableTreeConstants.DEFAULT_TEXTFIELD_WIDTH;
			textFieldWidth = defaultTextFieldWidth;
		}

		// Get the character width from the current font of the text field (of
		// character 'W').
		final int charWidth = textField.getFontMetrics(textField.getFont())
				.charWidth('W');

		// Set the size of the text field to match the width calculated
		// previously (number of characters * character width).
		textField.setPreferredSize(new Dimension(charWidth * textFieldWidth,
				textField.getMinimumSize().height));

		// Store the new value
		// newValue = value;

		// Create the constraints
		GridBagConstraints gbc = null;

		// Configure the JPanel
		this.setBackground(Color.white);

		// Add the text field to the panel
		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(textField, gbc);

		// Add the edit button to the panel
		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 1;
		gbc.gridy = 0;
		final ActionListener buttonListener = new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				displayEditDialog();
			}
		};
		this.editButton.addActionListener(buttonListener);
		this.add(editButton, gbc);

	}

	/**
	 * A helper method for displaying the edit dialog.
	 */
	private void displayEditDialog() {
		// Dispose the possible previous dialog
		if (this.editDialog != null) {
			this.editDialog.dispose();
		}

		// Open a new edit dialog
		this.editDialog = new EditDialog(this.textField);
		this.editDialog.setTitle(UNIVERSEEXTENSION);
		Utils.center(this.editDialog);
		this.editDialog.setVisible(true);
	}

	/**
	 * Set the action listener for this instance
	 * 
	 * @param listener
	 *            the action listener
	 */
	public void addActionListener(final ActionListener listener) {
		myListener = listener;
	}

	/**
	 * Helper method for notifying the listeners that this custom parameter
	 * component value has changed.
	 */
	private void notifyListeners() {
		if (myListener != null){
			myListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
		}	
	}

	/**
	 * Update the value of this components textField and then return this
	 * component
	 * 
	 * @return the component
	 */
	public JComponent getComponent() {
		return this;
	}

	/**
	 * Use this to set the colors of the DescriptionComponent
	 * 
	 * @param panelBG
	 * @param panelFG
	 * @param textFieldBG
	 * @param textFieldFG
	 */
	public void setColors(final Color panelBG, final Color panelFG, final Color textFieldBG,
			final Color textFieldFG) {
		if (panelBG != null) {
			this.setBackground(panelBG);
		}
		if (panelFG != null) {
			this.setForeground(panelFG);
		}
		if (textFieldBG != null) {
			textField.setBackground(textFieldBG);
		}
		if (textFieldFG != null) {
			textField.setForeground(textFieldFG);
		}
	}

	/**
	 * Return the component's text field value
	 * 
	 * @return the text field value
	 */
	public Object getValue() {
		return textField.getText();
	}

	/**
	 * Set the action commmand for this component
	 * 
	 * @param command
	 *            the action command
	 */
	public void setActionCommand(final String command) {
		actionCommand = command;
	}

	/**
	 * Use this method for enabling/disabling the component depending on the
	 * tree's isEditable value
	 * 
	 * @param isEnabled
	 */
	public void setEnabled(final boolean isEnabled) {

		// The text field is never editable.
		this.textField.setEnabled(isEnabled);
		
		this.editButton.setEnabled(isEnabled); // IDE #686 Disabling the Edit button in view mode ,28/08/09, ejohabd 

		// Store the value, so that it can be used when the edit frame is
		// created.
		this.isEnabled = isEnabled;
	}

	/**
	 * The table model for the universe extension in the universe extension
	 * component.
	 * 
	 * @author eheitur
	 * 
	 */
	private class UniverseExtTableModel implements TableModel {

		public static final int NUMBER_COLUMN_INDEX = 0;

		public static final int EXT_COLUMN_INDEX = 1;

		Vector<String> data;

		Vector<TableModelListener> listeners;

		public int[] columnWidthArr = { 50, 150 };

		/**
		 * Constructor with no parameters.
		 */
		public UniverseExtTableModel() {
			this.data = new Vector<String>();
			this.listeners = new Vector<TableModelListener>();
		}

		/**
		 * Constructor with the string of comma separated values as a parameter.
		 * 
		 * @param sourceString
		 *            Comma separated values of universe extensions.
		 */
		public UniverseExtTableModel(final String sourceString) {
			this.data = new Vector<String>();
			this.listeners = new Vector<TableModelListener>();

			final String[] result = sourceString.split(",");
			for (int i = 0; i < result.length; i++){
				this.data.add(result[i].trim());
			}
		}

		public String toString() {
			final StringBuffer stringBuffer = new StringBuffer();
			final int rows = this.getRowCount();
			for (int i = 0; i < rows; ++i) {
				stringBuffer.append(getValueAt(i, EXT_COLUMN_INDEX));
				if (i < rows - 1) {
					stringBuffer.append(",");
				}
			}
			return stringBuffer.toString();
		}

		public void addTableModelListener(final TableModelListener l) {
			this.listeners.add(l);
		}

		public Class<?> getColumnClass(final int columnIndex) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(final int columnIndex) {
			if (columnIndex == NUMBER_COLUMN_INDEX) {
				return "Number";
			} else if (columnIndex == EXT_COLUMN_INDEX) {
				return "Universe Extension";
			} else {
				throw new ArrayIndexOutOfBoundsException(
						"Invalid columnIndex value: " + columnIndex);
			}
		}

		public int getRowCount() {
			return data.size();
		}

		public Object getValueAt(final int rowIndex, final int columnIndex) {
			if (columnIndex == NUMBER_COLUMN_INDEX) {
				final Integer value = new Integer(rowIndex);
				return value.toString();
			} else if (columnIndex == EXT_COLUMN_INDEX) {
				return data.elementAt(rowIndex);
			} else {
				throw new ArrayIndexOutOfBoundsException(
						"Invalid columnIndex value: " + columnIndex);
			}
		}

		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return (!(columnIndex == NUMBER_COLUMN_INDEX));
		}

		public void removeTableModelListener(final TableModelListener l) {
			this.listeners.remove(l);
		}

		public void addEmptyRow(final int rowBefore) {
			this.data.add(rowBefore + 1, new String(""));
		}

		public void duplicateRow(final int row) {
			final String rowValue = new String(this.data.elementAt(row));
			this.data.add(this.data.size(), rowValue);
		}

		public void removeRow(final int row) {
			this.data.remove(row);
		}

		/**
		 * Moves a row up. Nothing happens if the first row is moved up.
		 * 
		 * @param row
		 */
		public void moveRowUp(final int row) {
			// The first row cannot be moved up.
			if (row <= 0) {
				return;
			}

			final String rowValue = new String(this.data.elementAt(row));
			this.data.add(row - 1, rowValue);
			this.data.remove(row + 1);
		}

		/**
		 * Moves a row down. Nothing happens if the last row is moved down.
		 * 
		 * @param row
		 */
		public void moveRowDown(final int row) {
			// The last row cannot be moved down.
			if (row >= this.data.size() - 1) {
				System.out.println("no move down for row: " + row);
				return;
			}
			final String rowValue = new String(this.data.elementAt(row));
			this.data.add(row + 2, rowValue);
			this.data.remove(row);
		}

		public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
			if (columnIndex == NUMBER_COLUMN_INDEX) {
				// Do nothing
			} else if (columnIndex == EXT_COLUMN_INDEX) {
				this.data.set(rowIndex, (String) value);
			} else {
				throw new ArrayIndexOutOfBoundsException(
						"Invalid columnIndex value: " + columnIndex);
			}
			fireTableChanged(null);
		}

		/**
		 * @param e
		 */
		private void fireTableChanged(final TableModelEvent e) {
			final Iterator<TableModelListener> listenersIterator = this.listeners
					.iterator();
			while (listenersIterator.hasNext()) {
				listenersIterator.next().tableChanged(e);
			}
		}
	}

	/**
	 * Class for edit dialog displayed when clicking the '...' button.
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

		private UniverseExtTableModel tableModel;

		private JButton okButton;

		private JButton cancelButton;

		private JTextField targetTextField;

		public EditDialog(final JTextField targetTextField) {
			this.targetTextField = targetTextField;

			// Create a grid bag layout instance. This instance will be reused
			// with all the dialog's components.
			final GridBagLayout gridBagLayout = new GridBagLayout();
			final GridBagConstraints c = new GridBagConstraints();
			this.setLayout(gridBagLayout);

			// Create the edit panel and its contents
			this.editPanel = new JPanel(gridBagLayout);

			this.tableModel = new UniverseExtTableModel(this.targetTextField
					.getText());
			this.table = createTable(tableModel);
			final JScrollPane scrollPane = new JScrollPane(this.table);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			final Dimension preferredSize = new Dimension(PREFERRED_EDIT_PANEL_WIDTH,
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
			this.okButton.setName("UnivExtComponentOkButton");
			this.okButton.addActionListener(new OkButtonActionListener());
			
			this.cancelButton = new JButton(CANCEL_BUTTON_TEXT);
			this.cancelButton.setName("UnivExtComponentCancelButton");
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

		private JTable createTable(final TableModel myTableModel) {
			final JTable result = new JTable();
			result.setModel(myTableModel);

			final TableSelectionListener listener = new TableSelectionListener();
			this.tableModel.addTableModelListener(listener);
			result.addMouseListener(listener);
			result.getTableHeader().addMouseListener(listener);

			setColumnWidths(result);
			setColumnEditors(result);
			setColumnRenderers(result);
			
			return result;
		}

		private void setColumnEditors(final JTable table) {
			final TableColumnModel tableColumnModel = table.getColumnModel();

			final TableColumn extColumn = tableColumnModel
					.getColumn(UniverseExtTableModel.EXT_COLUMN_INDEX);
			final JComboBox extComboBox = new JComboBox(universeExtensionOptions);
			extComboBox.setEditable(true);
			final DefaultCellEditor extColumnEditor = new DefaultCellEditor(
					extComboBox);
			extColumn.setCellEditor(extColumnEditor);
		}

		private void setColumnRenderers(final JTable table) {
			final TableColumnModel tableColumnModel = table.getColumnModel();

			final TableColumn typenameColumn = tableColumnModel
					.getColumn(UniverseExtTableModel.EXT_COLUMN_INDEX);
			final int typeNameSizeLimit = 255;
			final LimitedSizeTextTableCellRenderer typenameColumnRenderer = new LimitedSizeTextTableCellRenderer(
					typeNameSizeLimit, true);
			typenameColumn.setCellRenderer(typenameColumnRenderer);
		}

		private void setColumnWidths(final JTable table) {
			// For each table column, set the column's width to the
			// corresponding
			// value in column widths array.
			for (int i = 0; i < this.tableModel.columnWidthArr.length; ++i) {
				final int width = this.tableModel.columnWidthArr[i];
				final TableColumn column = table.getColumnModel().getColumn(i);
				column.setPreferredWidth(width);
			}
		}

		/**
		 * Updates the target text field according to the table model contents
		 */
		private void updateTextField() {
			final String text = this.tableModel.toString();
			this.targetTextField.setText(text);
		}

		private JPopupMenu createPopupMenu(final MouseEvent e) {
			JPopupMenu popupMenu;
			JMenuItem menuItem;
			popupMenu = new JPopupMenu();

			final int selected = table.getSelectedRow();

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
					if (table.getSelectedRowCount() > 1){
						menuItem.setEnabled(false);
					}
						
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

		private void displayPopupMenu(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				final JPopupMenu popupMenu = createPopupMenu(e);
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		/**
		 * The listener for the Ok button click.
		 * 
		 * @author eheitur
		 * 
		 */
		private class OkButtonActionListener implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				// Update the target text field value acoording to the current
				// table
				// model contents.
				updateTextField();

				// Notify the listeners of this component that the value has
				// changed.
				notifyListeners();

				// Dispose the dialog.
				dispose();
			}
		}

		/**
		 * The listener for the Cancel button click.
		 * 
		 * @author eheitur
		 * 
		 */
		private class CancelButtonActionListener implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		}

		/**
		 * The listener for the table pop-up menu options.
		 * 
		 * @author eheitur
		 * 
		 */
		private class PopUpMenuActionListener implements ActionListener {

			/**
			 * Callback method for action events, i.e. for when one of the menu
			 * alternatives have been selected.
			 * 
			 * @param ae
			 *            the action event
			 */
			public void actionPerformed(final ActionEvent ae) {

				if (ae.getActionCommand().equals(ADD)) {
					// Add row
					final int selectedRow = table.getSelectedRow();
					logger
							.info("Universe parameters: Adding an empty row after the row number "
									+ selectedRow + ".");
					tableModel.addEmptyRow(selectedRow);

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

				tableModel.fireTableChanged(null);
			}
		}

		private class TableSelectionListener implements TableModelListener,
				ActionListener, MouseListener {

			public void tableChanged(final TableModelEvent e) {
				setColumnRenderers(table);
				setColumnEditors(table);
				setColumnWidths(table);

				// Validate. In case errors, disable the ok-button and set the
				// tooltip as the first error message.
				final Vector<String> errors = getErrors(EditDialog.this.table);
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
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent )
			 */
			public void actionPerformed(final ActionEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseClicked(final MouseEvent e) {
				displayPopupMenu(e);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseEntered(final MouseEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseExited(final MouseEvent e) {

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent
			 * )
			 */
			public void mousePressed(final MouseEvent e) {
				displayPopupMenu(e);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseReleased(final MouseEvent e) {
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
			private Vector<String> getErrors(final JTable myTable) {
				final int rows = myTable.getRowCount();
				final int columns = myTable.getColumnCount();
				final Vector<String> errors = new Vector<String>();

				// Check for empty values
				for (int row = 0; row < rows; ++row) {
					for (int column = 0; column < columns; ++column) {
						// Extension must not be empty
						if (column == UniverseExtTableModel.EXT_COLUMN_INDEX) {
							final String strValue = (String) (myTable.getValueAt(row,
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
					final String val1 = (String) (myTable.getValueAt(row,
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
						final String val2 = (String) (myTable.getValueAt(row2,
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
				if (errors.isEmpty()){
					return null;
				}else{
					return errors;
				}
			}
		}
	}

	public JTextField getTextField() {
		return textField;
	}
}
