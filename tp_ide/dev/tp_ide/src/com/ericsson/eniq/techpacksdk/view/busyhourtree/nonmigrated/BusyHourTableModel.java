/**
 *
 */
package com.ericsson.eniq.techpacksdk.view.busyhourtree.nonmigrated;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.ericsson.eniq.techpacksdk.LimitedSizeCellEditor;
import com.ericsson.eniq.techpacksdk.LimitedSizeTextTableCellRenderer;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourRankkeysCellEditor;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourRankkeysCellRenderer;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourSourceCellEditor;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourSourceCellRenderer;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.RankTableCellEditor;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.RankTableCellRenderer;
import com.ericsson.eniq.techpacksdk.view.universeParameters.DescriptionCellEditor;
import org.jdesktop.application.Application;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.Vector;

/**
 * @author eheijun
 * @author eheitur
 */
@SuppressWarnings("serial")
public class BusyHourTableModel extends AbstractTableModel {

	private static final String[] columnNames = {"Target TP", "Rank Table", "Object", "Type", "Element BH",
		 "Description", "Source", "Where", "Criteria", "Keys"};

	private static final Integer[] columnWidths = {150, 150, 150, 150, 50, 250, 250, 150, 150, 150};

	private static final int targetTPColumnIdx = 0;

	private static final int rankTableColumnIdx = 1;

	private static final int bhobjectColumnIdx = 2;

	private static final int bhtypeColumnIdx = 3;

	private static final int bhelementColumnIdx = 4;

	private static final int descriptionColumnIdx = 5;

	private static final int bhsourceColumnIdx = 6;

	private static final int bhwhereColumnIdx = 7;

	private static final int bhcriteriaColumnIdx = 8;

	private static final int bhkeysColumnIdx = 9;

	private final Application application;

	private final DataModelController dataModelController;

	private Vector<BusyHourData> data;

	public BusyHourTableModel(final Application application, final DataModelController dataModelController,
														final Vector<BusyHourData> data) {
		super();
		this.application = application;
		this.dataModelController = dataModelController;
		this.data = data;
	}

	public Class getColumnClass(final int col) {
		final Object obj = getValueAt(0, col);
		if (obj != null) {
			return obj.getClass();
		} else {
			return String.class;
		}
	}

	private Object getColumnValue(final BusyHourData busyhourdata, final int col) {
		if (busyhourdata != null) {
			switch (col) {
				case targetTPColumnIdx:
					return Utils.replaceNull(busyhourdata.getBusyhour().getTargetversionid());
				case rankTableColumnIdx:
					return busyhourdata.getBusyhour();
				case bhobjectColumnIdx:
					return Utils.replaceNull(busyhourdata.getBusyhour().getBhobject());
				case bhtypeColumnIdx:
					return Utils.replaceNull(busyhourdata.getBusyhour().getBhtype());
				case bhelementColumnIdx:
					return Utils.integerToBoolean(busyhourdata.getBusyhour().getBhelement());
				case descriptionColumnIdx:
					return Utils.replaceNull(busyhourdata.getBusyhour().getDescription());
				case bhsourceColumnIdx:
					return busyhourdata.getBusyhourSourceTableModel();
				case bhwhereColumnIdx:
					return busyhourdata.getBusyhour().getWhereclause();
				case bhcriteriaColumnIdx:
					return busyhourdata.getBusyhour().getBhcriteria();
				case bhkeysColumnIdx:
					return busyhourdata.getBusyhourRankkeysTableModel();
				default:
					break;
			}
		}
		return null;
	}

	public String getColumnName(final int col) {
		return columnNames[col];
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
				if (data.elementAt(row) != null) {
					final BusyHourData busyHourData = data.elementAt(row);
					result = getColumnValue(busyHourData, col);
				}
			}
		}
		return result;
	}

	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public void setColumnWidths(final JTable theTable) {
		for (int ind = 0; ind < columnWidths.length; ind++) {
			if (theTable.getColumnModel().getColumnCount() <= ind) {
				final TableColumn col = theTable.getColumnModel().getColumn(ind);
				col.setPreferredWidth(columnWidths[ind]);
			}
		}
	}

	public void setColumnEditors(final JTable theTable) {
		// Set editor for targetTPs
		final TableColumn targetTPColumn = theTable.getColumnModel().getColumn(targetTPColumnIdx);
		final LimitedSizeCellEditor targetTPColumnEditor = new LimitedSizeCellEditor(columnWidths[bhtypeColumnIdx],
			 Busyhour.getTargetversionidColumnSize(), true);
		targetTPColumn.setCellEditor(targetTPColumnEditor);
		// Set editor for ranktable
		final TableColumn rankTableColumn = theTable.getColumnModel().getColumn(rankTableColumnIdx);
		final RankTableCellEditor rankTableColumnEditor = new RankTableCellEditor(dataModelController, true);
		rankTableColumn.setCellEditor(rankTableColumnEditor);
		// Set editor for type
		final TableColumn typeColumn = theTable.getColumnModel().getColumn(bhtypeColumnIdx);
		final LimitedSizeCellEditor typeColumnEditor = new LimitedSizeCellEditor(columnWidths[bhtypeColumnIdx], Busyhour
			 .getBhtypeColumnSize(), true);
		typeColumn.setCellEditor(typeColumnEditor);
		// Set editor for description
		final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
		DescriptionCellEditor descriptionColumnEditor = new DescriptionCellEditor(Busyhour.getDescriptionColumnSize(),
			 false, false);
		descriptionColumn.setCellEditor(descriptionColumnEditor);
		// Set editor for source
		final TableColumn bhsourceColumn = theTable.getColumnModel().getColumn(bhsourceColumnIdx);
		bhsourceColumn.setCellEditor(new BusyHourSourceCellEditor(application, false));
		// Set editor for where
		final TableColumn whereColumn = theTable.getColumnModel().getColumn(bhwhereColumnIdx);
		final DescriptionCellEditor whereColumnEditor = new DescriptionCellEditor(Busyhour.getWhereclauseColumnSize(), false,
			 false);
		whereColumn.setCellEditor(whereColumnEditor);
		// Set editor for criteria
		final TableColumn criteriaColumn = theTable.getColumnModel().getColumn(bhcriteriaColumnIdx);
		final DescriptionCellEditor criteriaColumnEditor = new DescriptionCellEditor(Busyhour.getBhcriteriaColumnSize(), false,
			 false);
		criteriaColumn.setCellEditor(criteriaColumnEditor);
		// Set editor for keys
		final TableColumn bhrankkeysColumn = theTable.getColumnModel().getColumn(bhkeysColumnIdx);
		bhrankkeysColumn.setCellEditor(new BusyHourRankkeysCellEditor(application, false));
	}

	public void setColumnRenderers(final JTable theTable) {
		// Set renderer for target tp
		final TableColumn targetTPColumn = theTable.getColumnModel().getColumn(targetTPColumnIdx);
		final LimitedSizeTextTableCellRenderer targetTPRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getTargetversionidColumnSize(), true);
		targetTPColumn.setCellRenderer(targetTPRenderer);
		// Set renderer for ranktable
		final TableColumn rankTableColumn = theTable.getColumnModel().getColumn(rankTableColumnIdx);
		final RankTableCellRenderer rankTableRenderer = new RankTableCellRenderer(false);
		rankTableColumn.setCellRenderer(rankTableRenderer);
		// Set renderer for object/element
		final TableColumn objectColumn = theTable.getColumnModel().getColumn(bhobjectColumnIdx);
		final LimitedSizeTextTableCellRenderer objectRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getBhobjectColumnSize(), true);
		objectColumn.setCellRenderer(objectRenderer);
		// Set renderer for type
		final TableColumn typeColumn = theTable.getColumnModel().getColumn(bhtypeColumnIdx);
		final LimitedSizeTextTableCellRenderer typeRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getBhtypeColumnSize(), true);
		typeColumn.setCellRenderer(typeRenderer);
		// Set renderer for description
		final TableColumn descriptionColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
		final LimitedSizeTextTableCellRenderer descriptionRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getDescriptionColumnSize(), false);
		descriptionColumn.setCellRenderer(descriptionRenderer);
		// Set source renderer
		final TableColumn bhsourceColumn = theTable.getColumnModel().getColumn(bhsourceColumnIdx);
		bhsourceColumn.setCellRenderer(new BusyHourSourceCellRenderer(application, false));
		final TableColumn whereColumn = theTable.getColumnModel().getColumn(bhwhereColumnIdx);
		final LimitedSizeTextTableCellRenderer whereRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getWhereclauseColumnSize(), false);
		whereColumn.setCellRenderer(whereRenderer);
		// Set renderer for bh criteria
		final TableColumn criteriaColumn = theTable.getColumnModel().getColumn(bhcriteriaColumnIdx);
		final LimitedSizeTextTableCellRenderer criteriaRenderer = new LimitedSizeTextTableCellRenderer(Busyhour
			 .getBhcriteriaColumnSize(), false);
		criteriaColumn.setCellRenderer(criteriaRenderer);
		// Set keyvalue renderer
		final TableColumn bhrankkeysColumn = theTable.getColumnModel().getColumn(bhkeysColumnIdx);
		bhrankkeysColumn.setCellRenderer(new BusyHourRankkeysCellRenderer(application, false));
	}

}
