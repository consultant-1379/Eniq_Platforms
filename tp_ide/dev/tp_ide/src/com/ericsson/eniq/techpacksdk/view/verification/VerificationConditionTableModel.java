/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.verification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;

import com.distocraft.dc5000.repository.dwhrep.Verificationcondition;
import com.ericsson.eniq.techpacksdk.ComboTableCellEditor;
import com.ericsson.eniq.techpacksdk.ComboTableCellRenderer;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class VerificationConditionTableModel extends AbstractTableModel {

	private static final String[] columnNames = { "Measurement Type", "Level",
			"Condition Class", "Condition", "Prompt1 Text", "Prompt1 Value",
			"Prompt2 Text", "Prompt2 Value", "Prompt3 Text", "Prompt3 Value",
			"Object Condition" };

	private static final Integer[] columnWidths = { 150, 150, 150, 150, 150,
			150, 150, 150, 150, 150, 150 };

	private static final int meastypeColumnIdx = 0;

	private static final int measlevelColumnIdx = 1;

	private static final int conditionclassColumnIdx = 2;

	private static final int conditionColumnIdx = 3;

	private static final int prompt1textColumnIdx = 4;

	private static final int prompt1valueColumnIdx = 5;

	private static final int prompt2textColumnIdx = 6;

	private static final int prompt2valueColumnIdx = 7;

	private static final int prompt3textColumnIdx = 8;

	private static final int prompt3valueColumnIdx = 9;

	private static final int objectConditionColumnIdx = 10;

	final List<Verificationcondition> data;

	private final List<Verificationcondition> dirtyList = new ArrayList<Verificationcondition>();

	/**
	 * @param data
	 */
	public VerificationConditionTableModel(final Application application,
			final DataModelController dataModelController,
			final List<Verificationcondition> data) {
		super();
		this.data = data;
	}

	public Class<? extends Object> getColumnClass(final int col) {
		final Object obj = getValueAt(0, col);
		if (obj != null) {
			return obj.getClass();
		} else {
			return String.class;
		}
	}

	private Object getColumnValue(
			final Verificationcondition verificationcondition, final int col) {
		if (verificationcondition != null) {
			switch (col) {
			case meastypeColumnIdx:
				return Utils.replaceNull(verificationcondition.getFacttable());
			case measlevelColumnIdx:
				return Utils.replaceNull(verificationcondition.getVerlevel());
			case conditionclassColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getConditionclass());
			case conditionColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getVercondition());
			case prompt1textColumnIdx:
				return Utils
						.replaceNull(verificationcondition.getPromptname1());
			case prompt1valueColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getPromptvalue1());
			case prompt2textColumnIdx:
				return Utils
						.replaceNull(verificationcondition.getPromptname2());
			case prompt2valueColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getPromptvalue2());
			case prompt3textColumnIdx:
				return Utils
						.replaceNull(verificationcondition.getPromptname3());
			case prompt3valueColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getPromptvalue3());
			case objectConditionColumnIdx:
				return Utils.replaceNull(verificationcondition
						.getObjectcondition());
			default:
				break;
			}
		}
		return null;
	}

	private void setColumnValue(
			final Verificationcondition verificationcondition, final int col,
			final Object value) {
		boolean valueChanged = false;
		switch (col) {
		case meastypeColumnIdx:
			verificationcondition.setFacttable((String) value);
			valueChanged = true;
			break;
		case measlevelColumnIdx:
			verificationcondition.setVerlevel((String) value);
			valueChanged = true;
			break;
		case conditionclassColumnIdx:
			verificationcondition.setConditionclass((String) value);
			valueChanged = true;
			break;
		case conditionColumnIdx:
			verificationcondition.setVercondition((String) value);
			valueChanged = true;
			break;
		case prompt1textColumnIdx:
			verificationcondition.setPromptname1((String) value);
			valueChanged = true;
			break;
		case prompt1valueColumnIdx:
			verificationcondition.setPromptvalue1((String) value);
			valueChanged = true;
			break;
		case prompt2textColumnIdx:
			verificationcondition.setPromptname2((String) value);
			valueChanged = true;
			break;
		case prompt2valueColumnIdx:
			verificationcondition.setPromptvalue2((String) value);
			valueChanged = true;
			break;
		case prompt3textColumnIdx:
			verificationcondition.setPromptname3((String) value);
			valueChanged = true;
			break;
		case prompt3valueColumnIdx:
			verificationcondition.setPromptvalue3((String) value);
			valueChanged = true;
			break;
		case objectConditionColumnIdx:
			verificationcondition.setObjectcondition((String) value);
			valueChanged = true;
			break;
		default:
			break;
		}
		if (valueChanged) {
			if (!dirtyList.contains(verificationcondition)) {
				dirtyList.add(verificationcondition);
			}
		}
	}

	public String getColumnName(final int col) {
		return columnNames[col].toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(final int row, final int col) {
		Object result = null;
		if (data != null) {
			if (data.size() > row) {
				if ((data.get(row) != null)
						&& (data.get(row) instanceof Verificationcondition)) {
					final Verificationcondition Verificationcondition = (Verificationcondition) data
							.get(row);
					result = getColumnValue(Verificationcondition, col);
				}
			}
		}
		return result;
	}

	public void setValueAt(final Object value, final int row, final int col) {
		if (data != null) {
			if (data.size() > row) {
				final Verificationcondition verificationcondition = (Verificationcondition) data
						.get(row);
				setColumnValue(verificationcondition, col, value);
				fireTableDataChanged();
			}
		}
	}

	public void addRow(final Verificationcondition verificationcondition) {
		data.add(verificationcondition);
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}

	public Verificationcondition removeRow(final int row) {
		final Verificationcondition verificationcondition = data.remove(row);
		if (dirtyList.contains(verificationcondition)) {
			dirtyList.remove(verificationcondition);
		}
		fireTableRowsDeleted(row, row);
		return verificationcondition;
	}

	public boolean isCellEditable(final int row, final int col) {
		return true;
	}

	public boolean isDirty() {
		return dirtyList.size() > 0;
	}

	public void clearDirty() {
		dirtyList.clear();
	}

	public void clear() {
		final int rows = data.size();
		data.clear();
		dirtyList.clear();
		fireTableRowsDeleted(0, rows);
	}

	public void save() throws Exception {
		while (isDirty()) {
			final Verificationcondition verificationcondition = dirtyList
					.remove(0);
			verificationcondition.saveToDB();
		}
	}

	public List<Verificationcondition> getData() {
		return data;
	}

	public void setData(final List<Verificationcondition> data) {
		clear();
		for (final Iterator<Verificationcondition> iter = data.iterator(); iter
				.hasNext();) {
			addRow(iter.next());
		}
	}

	/**
	 * Method for setting the preferred column widths
	 */
	public void setColumnWidths(final JTable theTable) {
		for (int ind = 0; ind < columnWidths.length; ind++) {
			if (theTable.getColumnModel().getColumnCount() <= ind) {
				final TableColumn col = theTable.getColumnModel()
						.getColumn(ind);
				col.setPreferredWidth(columnWidths[ind]);
			}
		}
	}

	/**
	 * Method for setting the column editors
	 */
	public void setColumnEditors(final JTable theTable) {
		// Set editor for mtype
		final TableColumn meastypeColumn = theTable.getColumnModel().getColumn(
				meastypeColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor = new LimitedSizeCellEditor(
				columnWidths[meastypeColumnIdx], Verificationcondition
						.getFacttableColumnSize(), true);
		meastypeColumn.setCellEditor(limitedTextEditor);
		// Set editor for level
		final TableColumn levelColumn = theTable.getColumnModel().getColumn(
				measlevelColumnIdx);
		final ComboTableCellEditor levelColumnComboEditor = new ComboTableCellEditor(
				Constants.REPORTCONDITIONLEVELS, false);
		levelColumn.setCellEditor(levelColumnComboEditor);
		// Set editor for Conditionclass
		final TableColumn conditionclassColumn = theTable.getColumnModel()
				.getColumn(conditionclassColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor3 = new LimitedSizeCellEditor(
				columnWidths[conditionclassColumnIdx], Verificationcondition
						.getConditionclassColumnSize(), true);
		conditionclassColumn.setCellEditor(limitedTextEditor3);
		// Set editor for Vercondition
		final TableColumn verconditionColumn = theTable.getColumnModel()
				.getColumn(conditionColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor4 = new LimitedSizeCellEditor(
				columnWidths[conditionColumnIdx], Verificationcondition
						.getVerconditionColumnSize(), true);
		verconditionColumn.setCellEditor(limitedTextEditor4);
		// Set editor for Promptname1
		final TableColumn promptname1Column = theTable.getColumnModel()
				.getColumn(prompt1textColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor5 = new LimitedSizeCellEditor(
				columnWidths[prompt1textColumnIdx], Verificationcondition
						.getPromptname1ColumnSize(), true);
		promptname1Column.setCellEditor(limitedTextEditor5);
		// Set editor for Promptvalue1
		final TableColumn promptvalue1Column = theTable.getColumnModel()
				.getColumn(prompt1valueColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor6 = new LimitedSizeCellEditor(
				columnWidths[prompt1valueColumnIdx], Verificationcondition
						.getPromptvalue1ColumnSize(), true);
		promptvalue1Column.setCellEditor(limitedTextEditor6);
		// Set editor for Promptname2
		final TableColumn promptname2Column = theTable.getColumnModel()
				.getColumn(prompt2textColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor7 = new LimitedSizeCellEditor(
				columnWidths[prompt2textColumnIdx], Verificationcondition
						.getPromptname2ColumnSize(), false);
		promptname2Column.setCellEditor(limitedTextEditor7);
		// Set editor for Promptvalue2
		final TableColumn promptvalue2Column = theTable.getColumnModel()
				.getColumn(prompt2valueColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor8 = new LimitedSizeCellEditor(
				columnWidths[prompt2valueColumnIdx], Verificationcondition
						.getPromptvalue2ColumnSize(), false);
		promptvalue2Column.setCellEditor(limitedTextEditor8);
		// Set editor for Promptname3
		final TableColumn promptname3Column = theTable.getColumnModel()
				.getColumn(prompt3textColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor9 = new LimitedSizeCellEditor(
				columnWidths[prompt3textColumnIdx], Verificationcondition
						.getPromptname3ColumnSize(), false);
		promptname3Column.setCellEditor(limitedTextEditor9);
		// Set editor for Promptvalue3
		final TableColumn promptvalue3Column = theTable.getColumnModel()
				.getColumn(prompt3valueColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor10 = new LimitedSizeCellEditor(
				columnWidths[prompt3valueColumnIdx], Verificationcondition
						.getPromptvalue3ColumnSize(), false);
		promptvalue3Column.setCellEditor(limitedTextEditor10);
		// Set editor for objectcondition
		final TableColumn objectconditionColumn = theTable.getColumnModel()
				.getColumn(objectConditionColumnIdx);
		final LimitedSizeCellEditor limitedTextEditor11 = new LimitedSizeCellEditor(
				columnWidths[objectConditionColumnIdx], Verificationcondition
						.getObjectconditionColumnSize(), false);
		objectconditionColumn.setCellEditor(limitedTextEditor11);
	}

	/**
	 * Method for setting the column renderers
	 */
	public void setColumnRenderers(final JTable theTable) {
		final TableColumn meastypeColumn = theTable.getColumnModel().getColumn(
				meastypeColumnIdx);
		final LimitedSizeTextTableCellRenderer meastypeComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getFacttableColumnSize(), true);
		meastypeColumn.setCellRenderer(meastypeComboRenderer);
		// 
		final TableColumn levelColumn = theTable.getColumnModel().getColumn(
				measlevelColumnIdx);
		final ComboTableCellRenderer levelComboRenderer = new ComboTableCellRenderer(
				Constants.REPORTCONDITIONLEVELS);
		levelColumn.setCellRenderer(levelComboRenderer);
		//
		final TableColumn verlevelColumn = theTable.getColumnModel().getColumn(
				measlevelColumnIdx);
		final LimitedSizeTextTableCellRenderer measlevelComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getVerlevelColumnSize(), true);
		verlevelColumn.setCellRenderer(measlevelComboRenderer);
		//
		final TableColumn conditionclassColumn = theTable.getColumnModel()
				.getColumn(conditionclassColumnIdx);
		final LimitedSizeTextTableCellRenderer conditionclassComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getConditionclassColumnSize(), true);
		conditionclassColumn.setCellRenderer(conditionclassComboRenderer);
		//
		final TableColumn verconditionColumn = theTable.getColumnModel()
				.getColumn(conditionColumnIdx);
		final LimitedSizeTextTableCellRenderer verconditionComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getVerconditionColumnSize(), true);
		verconditionColumn.setCellRenderer(verconditionComboRenderer);
		//
		final TableColumn promptname1Column = theTable.getColumnModel()
				.getColumn(prompt1textColumnIdx);
		final LimitedSizeTextTableCellRenderer promptname1ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptname1ColumnSize(), true);
		promptname1Column.setCellRenderer(promptname1ComboRenderer);
		//
		final TableColumn promptvalue1Column = theTable.getColumnModel()
				.getColumn(prompt1valueColumnIdx);
		final LimitedSizeTextTableCellRenderer promptvalue1ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptvalue1ColumnSize(), true);
		promptvalue1Column.setCellRenderer(promptvalue1ComboRenderer);
		//
		final TableColumn promptname2Column = theTable.getColumnModel()
				.getColumn(prompt2textColumnIdx);
		final LimitedSizeTextTableCellRenderer promptname2ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptname2ColumnSize(), false);
		promptname2Column.setCellRenderer(promptname2ComboRenderer);
		//
		final TableColumn promptvalue2Column = theTable.getColumnModel()
				.getColumn(prompt2valueColumnIdx);
		final LimitedSizeTextTableCellRenderer promptvalue2ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptvalue2ColumnSize(), false);
		promptvalue2Column.setCellRenderer(promptvalue2ComboRenderer);
		//
		final TableColumn promptname3Column = theTable.getColumnModel()
				.getColumn(prompt3textColumnIdx);
		final LimitedSizeTextTableCellRenderer promptname3ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptname3ColumnSize(), false);
		promptname3Column.setCellRenderer(promptname3ComboRenderer);
		//
		final TableColumn promptvalue3Column = theTable.getColumnModel()
				.getColumn(prompt3valueColumnIdx);
		final LimitedSizeTextTableCellRenderer promptvalue3ComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getPromptvalue3ColumnSize(), false);
		promptvalue3Column.setCellRenderer(promptvalue3ComboRenderer);
		//
		final TableColumn objectconditionColumn = theTable.getColumnModel()
				.getColumn(objectConditionColumnIdx);
		final LimitedSizeTextTableCellRenderer objectconditionComboRenderer = new LimitedSizeTextTableCellRenderer(
				Verificationcondition.getObjectconditionColumnSize(), false);
		objectconditionColumn.setCellRenderer(objectconditionComboRenderer);
	}

	public Vector<String> validateData() {

		final Vector<String> errorStrings = new Vector<String>();
		for (final Iterator<Verificationcondition> iter = data.iterator(); iter
				.hasNext();) {
			final Verificationcondition verificationcondition1 = iter.next();
			if (Utils.replaceNull(verificationcondition1.getFacttable()).trim()
					.equals("")) {
				errorStrings.add(columnNames[meastypeColumnIdx]
						+ " is required");
			} else if (Utils.replaceNull(verificationcondition1.getVerlevel())
					.trim().equals("")) {
				errorStrings.add(columnNames[measlevelColumnIdx]
						+ " is required");
			} else if (Utils.replaceNull(
					verificationcondition1.getConditionclass()).trim().equals(
					"")) {
				errorStrings.add(columnNames[conditionclassColumnIdx]
						+ " is required");
			} else if (Utils.replaceNull(
					verificationcondition1.getVercondition()).trim().equals("")) {
				errorStrings.add(columnNames[conditionColumnIdx]
						+ " is required");
			} else if (Utils.replaceNull(
					verificationcondition1.getPromptname1()).trim().equals("")) {
				errorStrings.add(columnNames[prompt1textColumnIdx]
						+ " is required");
			} else if (Utils.replaceNull(
					verificationcondition1.getPromptvalue1()).trim().equals("")) {
				errorStrings.add(columnNames[prompt1valueColumnIdx]
						+ " is required");
			} else {
				for (final Iterator<Verificationcondition> iter2 = data.iterator(); iter2
						.hasNext();) {
					final Verificationcondition verificationcondition2 = iter2.next();
					if (verificationcondition2 != verificationcondition1) {
						if (verificationcondition2.getVerlevel().equals(
								verificationcondition1.getVerlevel())
								&& (verificationcondition2.getVercondition()
										.equals(verificationcondition1
												.getVercondition()))
								&& (verificationcondition2.getConditionclass()
										.equals(verificationcondition1
												.getConditionclass()))) {
							errorStrings
									.add("Verification Condition with Level: "
											+ verificationcondition1
													.getVerlevel()
											+ ", Condition Class: "
											+ verificationcondition1
													.getConditionclass()
											+ ",and Condition: "
											+ verificationcondition1
													.getVercondition()
											+ " is not unique.");
						}
					}
				}
			}
		}
		return errorStrings;
	}

	/**
	 * Returns the verification condition object from the given row.
	 * 
	 * @param rowIndex
	 * @return the verification condition object. Null in case there is no such
	 *         row in the table.
	 */
	public Verificationcondition getRow(final int rowIndex) {
		if (rowIndex > -1 && rowIndex < data.size()) {
			return data.get(rowIndex);
		} else{
			return null;
		}
	}

}
