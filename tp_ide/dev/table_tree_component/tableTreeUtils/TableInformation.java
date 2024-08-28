package tableTreeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author enaland eheitur
 *
 */
public class TableInformation {
    /**
     * Path to the file containing the table properties
     */
    static String propsFile = "connection.properties";

    /**
     * The Properties instance reading and updating this specific table type's
     * properties
     */
    static Properties props = null;

    /**
     * The name of the table type
     */
    private String type;

    /**
     * The column names for this table type
     */
    private String[] defaultColNames;

    /**
     * The default column widths for the table type
     */
    private int[] defaultColWidths;

    /**
     * The total width of the table
     */
    private int tableWidth;

    /**
     * Maximum number of rows shown for a table before adding a vertical scroll
     * bar.
     */
    private int maxRowsShown;

    /**
     * The regular expression used for filtering each of the columns for this
     * table type. Set using setTableTypeColumnFilter().
     */
    protected String[] columnFilters = null;

    /**
     * Constructor. Initialises the table type specific data.
     * 
     * @param tableType
     *                The name of the table type
     * @param columnNames
     *                The names for the columns
     * @param columnWidths
     *                The widths for the columns
     * @param rowsShown
     *                The maximum number of rows shown in a table before a
     *                scroll bar
     */
    public TableInformation(String tableType, String[] columnNames,
	    int[] columnWidths, int rowsShown) {
	if (props == null)
	    props = readProps();
	type = tableType;
	defaultColNames = columnNames;
	defaultColWidths = columnWidths;
	maxRowsShown = rowsShown;
	columnFilters = new String[columnNames.length];
	this.setTableWidth(defaultColWidths);
	// colOrder = getColumnOrderFromProps();
	// colChangedWidths = getColumnWidthsFromProps();
    }

    /**
     * Gets the name of the table type
     * 
     * @return the table type
     */
    public String getType() {
	return type;
    }

    /**
     * Gets the column names in the original order before any possible user
     * changes.
     * 
     * @return the column names
     */
    public String[] getColumnNamesInOriginalOrder() {
	return defaultColNames;
    }

    /**
     * Get the original index of the column before any possible user changes.
     * 
     * @param name
     *                The name of the column
     * @return the original column index
     */
    public int getOriginalColumnIndexOfColumnName(String name) {
	int index = -1;
	for (int i = 0; i < defaultColNames.length; i++) {
	    if (defaultColNames[i].equals(name))
		index = i;
	}
	return index;
    }

    /**
     * Get the maximum number of rows shown in a table before adding scroll
     * bars.
     * 
     * @return the max number of rows shown in a table
     */
    public int getRowsShown() {
	return maxRowsShown;
    }

    /**
     * Sets the width of the table in pixels.
     * 
     * @param width
     *                The width of the table in pixels
     */
    public void setTableWidth(int width) {
	tableWidth = width;
    }

    /**
     * Sets the width of the table in pixels calculated as the sum of the widths
     * of the given column widths.
     * 
     * @param columnWidths
     *                The array of the column widths
     */
    public void setTableWidth(int[] columnWidths) {
	tableWidth = 0;
	for (int i = 0; i < columnWidths.length; i++) {
	    tableWidth += columnWidths[i];
	}
    }

    /**
     * Gets the table width in pixels
     * 
     * @return the table width
     */
    public int getTableWidth() {
	return tableWidth;
    }

    /**
     * Gets the column filter regex for the given column for this table type.
     * 
     * @param column
     * @return the filter regex string
     */
    public String getTableTypeColumnFilter(int column) {
	return columnFilters[column];
    }

    /**
     * Sets the column filter for this table type
     * 
     * @param column
     * @param filter
     */
    public void setTableTypeColumnFilter(int column, String filter) {
	if (filter != null) {
	    columnFilters[column] = filter;
	} else {
	    columnFilters[column] = "";
	}
    }

    /**
     * Clears the column filter for this table type
     * 
     * @param column
     */
    public void clearTableTypeColumnFilter(int column) {
	columnFilters[column] = "";
    }

    /**
     * Clears all the column filters for this table type
     */
    public void clearAllTableTypeColumnFilters() {
	for (int i = 0; i < columnFilters.length; i++) {
	    columnFilters[i] = "";
	}
    }

    //
    // NEW METHODS FOR HANDLING THE PROPERTIES
    //
    /**
     * Resets the column names and widths
     */
    public void resetColumns() {
	setColumnNamesInOrderToProperties(null);
	setColumnWidths(defaultColNames, defaultColWidths);
    }

    /**
     * Used to get the column widths from the properties file. If new column widths haven't been
     * specified earlier, the column widths are set to the defaults. The integer array return contain
     * the columns widths in the order corresponding to wanted column order
     * 
     * @return column widths
     */
    public synchronized int[] getColumnWidthsInOrderFromProperties() {
	String[] nameOrder = getColumnNamesInOrderFromProperties();
	int[] orderedWidths = new int[nameOrder.length];
	for (int i = 0; i < nameOrder.length; i++) {
	    String propKey = "table." + type + ".column.width." + nameOrder[i];
	    if (props.containsKey(propKey)
		    && props.getProperty(propKey) != null) {
		orderedWidths[i] = new Integer(props.getProperty(propKey));
	    } else {
		orderedWidths[i] = defaultColWidths[getOriginalColumnIndexOfColumnName(nameOrder[i])];
	    }
	}
	return orderedWidths;
    }

    /**
     * This method sets new column widths to the properties file
     * 
     * @param colNames
     * @param widths
     */
    public synchronized void setColumnWidths(String[] colNames, int[] widths) {
	String propKey = "";
	if (colNames.length == widths.length && colNames != null
		&& widths != null) {
	    for (int i = 0; i < colNames.length; i++) {
		propKey = "table." + type + ".column.width." + colNames[i];
		props.setProperty(propKey, widths[i] + "");
	    }
	}
	saveProps();
    }

  /**
   * This method returns a string array with the column header names in the
   * wanted order
   * 
   * @return column header names
   */
  public synchronized String[] getColumnNamesInOrderFromProperties() {
    String[] orderFromProperties = defaultColNames.clone();
    
    String propKey = "table." + type + ".column.order";
    String value = props.getProperty(propKey);
    
    if (props.containsKey(propKey) && value != null) {
      // Tokenize the value:
      StringTokenizer tokenizer = new StringTokenizer(value, ":");
      // Check if the tokens read matches the actual number of columns:
      if (tokenizer.countTokens() != orderFromProperties.length) {
        resetColumns();
        // Get the value again from the properties:
        value = props.getProperty(propKey);        
        if (props.containsKey(propKey) && value != null) {
          tokenizer = new StringTokenizer(value, ":");          
          int index = 0;
          while (tokenizer.hasMoreTokens()) {
            orderFromProperties[index] = tokenizer.nextElement().toString();
            index++;
          }
        }
      }
    }
    return orderFromProperties;
  }

    /**
     * This method sets the wanted column order to the properrties file based on the input
     * string array
     * 
     * @param names
     */
    public synchronized void setColumnNamesInOrderToProperties(String[] names) {
	String propKey = "table." + type + ".column.order";
	String value = "";
	if (names == null) {
	    names = defaultColNames.clone();
	}
	for (int i = 0; i < names.length; i++) {
	    value = value + names[i] + ":";
	}

	// System.out.println(propKey+"="+value);
	props.setProperty(propKey, value);
	saveProps();
    }

    /**
     * Creates a properties instance from the file specified by propsFile
     */
  protected synchronized Properties readProps() {
	Properties retProps = new Properties();
	FileInputStream fis = null;
	File file = new File(propsFile);

	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    if (file.canRead()) {
		fis = new FileInputStream(file);
		retProps.load(fis);
		fis.close();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return retProps;
    }

    /**
     * Saves the properties instance to the file specified by propsFile
     */
    static synchronized private void saveProps() {
	FileOutputStream fos = null;
	File file = new File(propsFile);

	try {
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    if (file.canWrite()) {
		fos = new FileOutputStream(file);
		props.store(fos, "Saved");
		fos.close();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
