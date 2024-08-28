package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import static com.ericsson.eniq.techpacksdk.common.Constants.BH_PRODUCT_PLACE_HOLDER_PREFIX;
import org.jdesktop.application.Application;

import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import java.util.Vector;
import java.util.logging.Logger;

/**
 * Concrete class that models the Busy Hour Product Place Holder table.
 *
 * @author ejarsav
 * @author eheitur
 */
public class BusyhourProductPlaceholderTableModel extends BusyhourAbstractPlaceholderTableModel {

    private static final long serialVersionUID = -5150345618080149224L;

    private static final Logger logger = Logger.getLogger(BusyhourProductPlaceholderTableModel.class.getName());
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
    private static final String myTableName = "Product Placeholders";

    /**
     * Max number of rows shown before adding scrollbars
     */
    private static final int maxRowsShown = 20;

    /**
     * Constructor. Initializes the column names, widths and table name.
     */
    public BusyhourProductPlaceholderTableModel(Application application, RockFactory rockFactory,
                                                Vector<TableInformation> tableInformations, final DataModelController dataModelController, boolean editable, final BusyHourData emptyBusyHourData) {
        super(application, dataModelController, rockFactory, tableInformations, editable, emptyBusyHourData);

    }

    /**
     * Static method that returns the table type and its corresponding column
     * names
     *
     * @return
     */
    public static TableInformation createTableTypeInfo() {
        return new TableInformation(myTableName, myColumnNames, myColumnWidths, maxRowsShown);
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
        return BH_PRODUCT_PLACE_HOLDER_PREFIX;
    }

    public Logger getLogger() {
        return logger;
    }

}
