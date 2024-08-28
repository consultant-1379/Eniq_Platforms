package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import static com.ericsson.eniq.techpacksdk.common.Constants.BH_CUSTOM_PLACE_HOLDER_PREFIX;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

import org.jdesktop.application.Application;
import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TablePopupMenuListener;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.util.Vector;
import java.util.logging.Logger;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Concrete class that models the Busy Hour Custom Place Holder table.
 *
 * @author ejarsav
 * @author eheitur
 */
public class BusyhourCustomPlaceholderTableModel extends BusyhourAbstractPlaceholderTableModel {

    private static final long serialVersionUID = -5150345618080149224L;

    private static final Logger logger = Logger.getLogger(BusyhourCustomPlaceholderTableModel.class.getName());

    /**
     * Column names, used as headings for the columns.
     */
    private static final String[] myColumnNames = {"Description", "Source", "Where", "Formula", "Keys",
            "Busy Hour Type", "Mapped Types", "Clause", "Enabled"};

    /**
     * Column widths, used to graphically layout the columns.
     */
    private static final int[] myColumnWidths = {150, 150, 150, 150, 150, 100, 100, 30, 30};

    /**
     * The table type/name
     */
    private static final String myTableName = "Custom Placeholders";

    /**
     * Max number of rows shown before adding scrollbars
     */
    private static final int maxRowsShown = 20;
	private final BusyhourAbstractPlaceholderTableModel prodModel;

    /**
     * Constructor. Initializes the column names, widths and table name.
     */
    public BusyhourCustomPlaceholderTableModel(Application application, RockFactory rockFactory,
                                               Vector<TableInformation> tableInformations, final DataModelController dataModelController, boolean editable,
                                               final BusyHourData emptyBusyHourData, final BusyhourAbstractPlaceholderTableModel dest) {
        super(application, dataModelController, rockFactory, tableInformations, editable, emptyBusyHourData);
			prodModel = dest;

    }

    public String[] getColumnNames() {
        return myColumnNames;
    }

    public int[] getColumnWidths() {
        return myColumnWidths;
    }

    public String getTableName() {
        return myTableName;
    }

    public int getMaxRowsToShow() {
        return maxRowsShown;
    }

    public String getPlaceholderPrefix() {
        return BH_CUSTOM_PLACE_HOLDER_PREFIX;
    }

    public Logger getLogger() {
        return logger;
    }

	public static final String MOVE_TO_PRODUCT = "Copy To Product";
	@Override
	public JPopupMenu getPopupMenu(final Component targetComponent, final TableContainer tableContainer) {
		final JPopupMenu menu = super.getPopupMenu(targetComponent, tableContainer);
		final int copyFromRow = tableContainer.getTable().getSelectedRow();
		final TablePopupMenuListener listener = new TablePopupMenuListener(tableContainer) {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				final BusyHourData busyhourdata = (BusyHourData) data.elementAt(copyFromRow);
				try {
					prodModel.copyTo(busyhourdata);
				} catch (Exception e) {
					 JOptionPane.showMessageDialog(null, e.getMessage()); // placeholders full....
				}
				prodModel.refreshTable();
				//System.out.println("moved Custom Placeholder index["+copyFromRow+"] to Product Placeholder");
			}
		};
		final JMenuItem moveItem = newMenuItem(MOVE_TO_PRODUCT, listener, true, true, true, true, false);
		boolean enableCopy = areEmptyPlaceholders(prodModel.getData());
		if(enableCopy){
			int[] selectedRows = tableContainer.getTable().getSelectedRows();
			if(data.isEmpty() || data.size() < copyFromRow){
				enableCopy = false;
			} else if(selectedRows == null || selectedRows.length==0){
				enableCopy = false;
			} else {
				final BusyHourData busyhourdata = (BusyHourData) data.elementAt(copyFromRow);
				final String form = busyhourdata.getBusyhour().getBhcriteria();
				if(form == null || form.length() == 0){
					enableCopy = false;
				}
			}
		}
		// need to check if there are available product placeholder available/empty
		moveItem.setEnabled(enableCopy);
		menu.add(moveItem);
		return menu;
	}

	/**
     * Static method that returns the table type and its corresponding column
     * names
     *
     * @return v
     */
    public static TableInformation createTableTypeInfo() {
        return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
    }

}
