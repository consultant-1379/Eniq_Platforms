package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

import org.jdesktop.application.Application;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.ComboBoxTableCellEditor;
import tableTree.ComboBoxTableCellRenderer;
import tableTree.DescriptionCellEditor;
import tableTree.DescriptionCellRenderer;
import tableTree.ShowOnlyCellEditor;
import tableTree.ShowOnlyCellRenderer;
import tableTree.TTTableModel;
import tableTreeUtils.TableContainer;
import tableTreeUtils.TableInformation;
import tableTreeUtils.TablePopupMenuListener;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class BusyhourAbstractPlaceholderTableModel extends TTTableModel implements CellEditorListener {

    public static final long serialVersionUID = -5150345618080149224L;

    private final Logger logger;

    private final int descriptionColumnIdx = 0;

    private final int sourceColumnIdx = 1;

    private final int whereColumnIdx = 2;

    private final int criteriaColumnIdx = 3;

    private final int keysColumnIdx = 4;

    private final int aggregationTypeColumnIdx = 5;

    private final int mappedTypesColumnIdx = 6;

    //private final int groupingColumnIdx = 7;
    
    private final int clauseColumnIdx = 7;

    private final int enabledColumnIdx = 8;
    
    private final Application application;
    
    private final RockFactory rockFactory;

    private DataModelController dataModelController;


    private BusyhourAggTypeCellEditor aggTypeEditor = null;
    //private boolean disableGrouping;
    
    private BusyHourData emptyBusyHourData = null;

    /*
    * Constructor. Initializes the column names, widths and table name.
    * @param application app
    * @param rockFactory rock
    * @param tableInformations table info
    * @param editable is editable
    */
    public BusyhourAbstractPlaceholderTableModel(final Application application, final DataModelController dataModelController, final RockFactory rockFactory,
                                                 final Vector<TableInformation> tableInformations,
                                                 final boolean editable, final BusyHourData emptyBusyHourData) {
        super(rockFactory, tableInformations, editable);
        this.rockFactory = rockFactory;
        this.application = application;
        this.dataModelController = dataModelController;
        this.setColumnNames(getColumnNames());
        this.setColumnWidths(getColumnWidths());
        this.setTableName(getTableName());
        this.emptyBusyHourData = emptyBusyHourData;
        logger = getLogger();
    }

    public abstract String[] getColumnNames();

    public abstract int[] getColumnWidths();

    public abstract String getTableName();

    public abstract int getMaxRowsToShow();

    public abstract String getPlaceholderPrefix();

    public abstract Logger getLogger();

	protected boolean isEmpty(final BusyHourData bhd){
		final String form = bhd.getBusyhour().getBhcriteria();
		return form == null || form.length() == 0;
	}
	protected boolean areEmptyPlaceholders(final List<Object> data){
		final int index = getNextEmptyPlaceholder(data);
		return index != -1;
	}
	protected int getNextEmptyPlaceholder(final List<Object> data){
		for(int i=0;i<data.size();i++){
			final BusyHourData bhd = (BusyHourData)data.get(i);
			if(isEmpty(bhd)){
				return i;
			}
		}
		return -1;
	}

    @Override
    public Object getOriginalValueAt(final int row, final int col) {

        BusyHourData busyhourdata = (BusyHourData) data.elementAt(row);

        switch (col) {
            case descriptionColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getDescription());
            case sourceColumnIdx:
                return busyhourdata.getBusyhourSourceTableModel();
            case whereColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getWhereclause());
            case criteriaColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getBhcriteria());
            case keysColumnIdx:
                return busyhourdata.getBusyhourRankkeysTableModel();
            case aggregationTypeColumnIdx:
                return busyhourdata.getBusyhourRankkeysTableModel();
            case mappedTypesColumnIdx:
                return busyhourdata.getBusyhourMappingsTableModel();
//            case groupingColumnIdx:
//                Utils.replaceNull(busyhourdata.getBusyhour().getGrouping());
            case enabledColumnIdx:
                return 1 == Utils.replaceNull(busyhourdata.getBusyhour().getEnable());
            case clauseColumnIdx:
            	String clause = "N/A";
            	try {
            		clause = StorageTimeAction.getPlaceholderCreateStatement(busyhourdata.getBusyhour(), busyhourdata.getBusyhour().getVersionid(), busyhourdata.getVersioning().getTechpack_name(), rockFactory);
            	} catch(Exception e){
            		//
            	}
            	return Utils.replaceNull(clause);
            default:
                break;
        }

        return null;
    }

    @Override
    public Object getValueAt(final int row, final int col) {

        BusyHourData busyhourdata = (BusyHourData) data.elementAt(row);

        switch (col) {
            case descriptionColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getDescription());
            case sourceColumnIdx:
                return busyhourdata.getBusyhourSourceTableModel();
            case whereColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getWhereclause());
            case criteriaColumnIdx:
                return Utils.replaceNull(busyhourdata.getBusyhour().getBhcriteria());
            case keysColumnIdx:
                return busyhourdata.getBusyhourRankkeysTableModel();
            case aggregationTypeColumnIdx:
                return busyhourdata.getBusyhour();
            case mappedTypesColumnIdx:
                return busyhourdata.getBusyhourMappingsTableModel();
//            case groupingColumnIdx:
//                return Utils.replaceNull(busyhourdata.getBusyhour().getGrouping());
            case enabledColumnIdx:
                return 1 == Utils.replaceNull(busyhourdata.getBusyhour().getEnable());
            case clauseColumnIdx:
            	String clause = "N/A";
            	try {
            		clause = StorageTimeAction.getPlaceholderCreateStatement(busyhourdata.getBusyhour(), busyhourdata.getBusyhour().getVersionid(), busyhourdata.getVersioning().getTechpack_name(), rockFactory);
            	} catch(Exception e){
            		//
            	}
            	return Utils.replaceNull(clause);
            default:
                break;
        }

        return null;
    }

    /**
     * getter for EmptyBusyHourData
     * This is used to help create empty rows in PP/CP tables.
     * @return
     */
    public BusyHourData getEmptyBusyHourData(){
    	return this.emptyBusyHourData;
    }
    
    /**
     * setter for EmptyBusyHourData
     * This is used to help create empty rows in PP/CP tables.
     * @param emptyBusyHourData
     */
    public void setEmptyBusyHourData(final BusyHourData emptyBusyHourData){
    	this.emptyBusyHourData = emptyBusyHourData;
    }
    
    
    public Object getColumnValueAt(final Object dataObject, final int col) {

        final BusyHourData busyhourdata = (BusyHourData) dataObject;

        if (busyhourdata != null) {
            switch (col) {
                case descriptionColumnIdx:
                    return Utils.replaceNull(busyhourdata.getBusyhour().getDescription());
                case sourceColumnIdx:
                    return busyhourdata.getBusyhourSourceTableModel();
                case whereColumnIdx:
                    return Utils.replaceNull(busyhourdata.getBusyhour().getWhereclause());
                case criteriaColumnIdx:
                    return Utils.replaceNull(busyhourdata.getBusyhour().getBhcriteria());
                case keysColumnIdx:
                    return busyhourdata.getBusyhourRankkeysTableModel();
                case aggregationTypeColumnIdx:
                    return busyhourdata.getBusyhour();
                case mappedTypesColumnIdx:
                    return busyhourdata.getBusyhourMappingsTableModel();
//                case groupingColumnIdx:
//                    return Utils.replaceNull(busyhourdata.getBusyhour().getGrouping());
                case enabledColumnIdx:
                    return 1 == Utils.replaceNull(busyhourdata.getBusyhour().getEnable());
                case clauseColumnIdx:
                	String clause = "N/A";
                	try {
                		clause = StorageTimeAction.getPlaceholderCreateStatement(busyhourdata.getBusyhour(), busyhourdata.getBusyhour().getVersionid(), busyhourdata.getVersioning().getTechpack_name(), rockFactory);
                	} catch(Exception e){
                		//
                	}
                	return Utils.replaceNull(clause);
                default:
                    break;
            }
        }
        return null;
    }

    public void setValueAt(final Object value, final int row, final int col) {

        final BusyHourData busyhourdata = (BusyHourData) data.elementAt(row);

        boolean valueChanged = true;
        boolean redoViews = true;
        switch (col) {
            case descriptionColumnIdx:
                busyhourdata.getBusyhour().setDescription((String) value);
                redoViews = true; //Want button enabled when Description changed.//eeoidiv,20110331, HN99006:BH descriptions not update in BHTYPE tables. 
                break;
            case sourceColumnIdx:
                if (value instanceof Vector<?>) {
                    busyhourdata.setBusyhourSource((Vector<Busyhoursource>) value);
                    busyhourdata.generateBusyHourRankKeys(true);
                }
                break;
            case whereColumnIdx:
                busyhourdata.getBusyhour().setWhereclause((String) value);
                break;
            case criteriaColumnIdx:
                busyhourdata.getBusyhour().setBhcriteria((String) value);
                break;
            case keysColumnIdx:
                if (value instanceof Vector<?>) {
                    busyhourdata.setBusyhourRankkeys((Vector<Busyhourrankkeys>) value);
                }
                break;
            case aggregationTypeColumnIdx:
                Busyhour edited = (Busyhour) value;
                busyhourdata.setBusyhour(edited);
                break;
            case mappedTypesColumnIdx:
                if (value instanceof Vector<?>) {
                    busyhourdata.setBusyhourMappings((Vector<Busyhourmapping>) value);
                }
                break;
//            case groupingColumnIdx:
//                busyhourdata.getBusyhour().setGrouping((String) value);
//                redoViews = false;
//                break;
            case enabledColumnIdx:
                if ((Boolean) value) {
                    busyhourdata.getBusyhour().setEnable(1);
                    redoViews = true;
                } else {
                    busyhourdata.getBusyhour().setEnable(0);
                    redoViews = false;
                }
                break;

            default:
                // nothing changed
                valueChanged = false;
                break;
        }

        if (redoViews) {
            busyhourdata.getBusyhour().setReactivateviews(1);
        }
        if (valueChanged) {
            refreshTable();
            fireTableDataChanged();
        }

    }

    /**
     * Overridden method for setting the column editor of the Description column.
     */
    public void setColumnEditors(final JTable theTable) {

        // Set editor for description
        final TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
        descColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

        // Set editor for source
        final TableColumn bhsourceColumn = theTable.getColumnModel().getColumn(sourceColumnIdx);
        bhsourceColumn.setCellEditor(new BusyHourSourceCellEditor(application, this.isTreeEditable()));

        // Set editor for where
        final TableColumn whereColumn = theTable.getColumnModel().getColumn(whereColumnIdx);
        whereColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

        // Set editor for criteria
        final TableColumn criteriaColumn = theTable.getColumnModel().getColumn(criteriaColumnIdx);
        criteriaColumn.setCellEditor(new DescriptionCellEditor(this.isTreeEditable()));

        // Set editor for rank keys
        final TableColumn bhrankkeysColumn = theTable.getColumnModel().getColumn(keysColumnIdx);
        bhrankkeysColumn.setCellEditor(new BusyHourRankkeysCellEditor(application, this.isTreeEditable()));

        // Set editor for aggtype

        final TableColumn bhaggregationtypeColumn = theTable.getColumnModel().getColumn(aggregationTypeColumnIdx);
        aggTypeEditor = new BusyhourAggTypeCellEditor(null, this.isTreeEditable());
        bhaggregationtypeColumn.setCellEditor(aggTypeEditor);
        aggTypeEditor.addCellEditorListener(this);

        // Set editor for busyhour mapping
        final TableColumn busyhourmappingColumn = theTable.getColumnModel().getColumn(mappedTypesColumnIdx);
        busyhourmappingColumn.setCellEditor(new BusyHourMappingsCellEditor(application, this.isTreeEditable()));

        // Set editor for grouping
//        final TableColumn groupingColumn = theTable.getColumnModel().getColumn(groupingColumnIdx);
//        groupingColumn.setCellEditor(new ComboBoxTableCellEditor(Constants.BH_GROUPING_TYPES));

        // Set editor for clause
        final TableColumn clauseColumn = theTable.getColumnModel().getColumn(clauseColumnIdx);
        clauseColumn.setCellEditor(new ShowOnlyCellEditor(this.isTreeEditable()));

        // Default editor for enable
    }

    /**
     * Overridden method for setting the column renderer. Not used.
     */
    public void setColumnRenderers(final JTable theTable) {

        // Set renderer for description
        final TableColumn descColumn = theTable.getColumnModel().getColumn(descriptionColumnIdx);
        descColumn.setCellRenderer(new DescriptionCellRenderer());

        // Set renderer for source
        final TableColumn bhsourceColumn = theTable.getColumnModel().getColumn(sourceColumnIdx);
        bhsourceColumn.setCellRenderer(new BusyHourSourceCellRenderer(null, this.isTreeEditable()));

        // Set renderer for wherecolumn
        final TableColumn whereColumn = theTable.getColumnModel().getColumn(whereColumnIdx);
        whereColumn.setCellRenderer(new DescriptionCellRenderer());

        // Set renderer for bh criteria
        final TableColumn criteriaColumn = theTable.getColumnModel().getColumn(criteriaColumnIdx);
        criteriaColumn.setCellRenderer(new DescriptionCellRenderer());

        // Set renderer for rank keys
        final TableColumn bhrankkeysColumn = theTable.getColumnModel().getColumn(keysColumnIdx);
        bhrankkeysColumn.setCellRenderer(new BusyHourRankkeysCellRenderer(null, this.isTreeEditable()));

        // Set renderer for aggregation type
        final TableColumn bhaggregationtypeColumn = theTable.getColumnModel().getColumn(aggregationTypeColumnIdx);
        bhaggregationtypeColumn.setCellRenderer(new BusyhourAggTypeCellRenderer(this.isTreeEditable()));

        // Set renderer for busyhour mapping
        final TableColumn busyhourmappingColumn = theTable.getColumnModel().getColumn(mappedTypesColumnIdx);
        busyhourmappingColumn.setCellRenderer(new BusyHourMappingsCellRenderer(null, this.isTreeEditable()));

        // Set renderer for grouping
//        final TableColumn groupingColumn = theTable.getColumnModel().getColumn(groupingColumnIdx);
//        final ComboBoxTableCellRenderer rend = new ComboBoxTableCellRenderer(Constants.BH_GROUPING_TYPES);
//        rend.setEnabled(!disableGrouping);
//        groupingColumn.setCellRenderer(rend);

        // Set renderer for clause
        final TableColumn clauseColumn = theTable.getColumnModel().getColumn(clauseColumnIdx);
        clauseColumn.setCellRenderer(new ShowOnlyCellRenderer());
        
        // Default renderer for enable
    }

    /**
     * Overridden method for creating specifically new Measurementcounters.
     */
    public RockDBObject createNew() {
    	return null;
    }
    
//    public Object createNewEmptyPlaceholder(DataModelController dataModelController){
////		final BusyhourHandlingDataModel dModel = dmController.getBusyhourHandlingDataModel();
////		final List<BusyHourData> data = dModel.getBusyHourData();
////
////    	BusyHourData newBusyHourData = BusyHourData();
//    	BusyHourData bhData = new BusyHourData(dataModelController);
//    	return bhData;
//        //return new Busyhour(rockFactory);
//    }

    /**
     * Overridden version of this method for saving specifically BusyHourData.
     *
     * @throws RockException
     * @throws SQLException
     */
    protected void saveData(final Object rockObject) throws SQLException, RockException {
        final BusyHourData action = ((BusyHourData) rockObject);
        try {

            // Commented out, since there is no need to regenerate the aggregations,
            // because they are already there and do not depend on the changes in the
            // busy hours.
            //
            // Remove and re-generate aggregations
            //
            // dataModelController.getBusyhourHandlingDataModel().deleteAggregations(action.getBusyhour(),
            // false);
            // dataModelController.getBusyhourHandlingDataModel().createAggregations(action.getBusyhour(),
            // action.getBusyhourSource(), versioning.getVersionid());
        	      	       	    	
            action.save();
            // logger.info("saved");

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Overridden version of this method for deleting specifically BusyHourData.
     *
     * @throws RockException
     * @throws SQLException
     */
    protected void deleteData(final Object rockObject) throws SQLException, RockException {
        ((BusyHourData) rockObject).delete();
    }

    @Override
    public void clearData(final Object placeholder) throws SQLException, RockException {
        final BusyHourData bhData = (BusyHourData) placeholder;

        bhData.getBusyhourMappingsTableModel().resetBusyhourMappings();

        bhData.getBusyhourRankkeysTableModel().removeAllData();

        bhData.getBusyhourSourceTableModel().removeAllData(); // 20110211 eanguan, Clear state of Source filed.
        bhData.getBusyhourSource().clear(); // 20110211 eanguan, Clear Source List.
        Busyhour busyhour = bhData.getBusyhour();
        busyhour.setBhcriteria("");
        busyhour.setWhereclause("");
        busyhour.setDescription("");
        busyhour.setEnable(0);
        busyhour.setAggregationtype(Constants.BH_AGGREGATION_TYPPES[0]);
        busyhour.setOffset(0);
        busyhour.setWindowsize(60);
        busyhour.setLookback(0);
        busyhour.setP_threshold(0);
        busyhour.setN_threshold(0);
        busyhour.setClause("");
        busyhour.setGrouping(Constants.BH_GROUPING_TYPES[0]);
        busyhour.setReactivateviews(1);

    }

    @Override
    public Object copyOf(final Object toBeCopied) {
        return ((BusyHourData) toBeCopied).clone();
    }

    @Override
    public String getColumnFilterForTableType(final int column) {
        return null;
    }

    @Override
    public boolean isColumnFilteredForTableType(final int column) {
        return false;
    }

    @Override
    public void update(final Observable sourceObject, final Object sourceArgument) {
    }

    /**
     * Overridden version of the method to allow custom editor components to be
     * clicked even though the tree is not editable in the read-only mode.
     *
     * @param row The row to check
     * @param col The column to check
     * @return true if the cell is editable
     */
    public boolean isCellEditable(final int row, final int col) {
        // Always allow the editing for the column using a custom editor, so that
        // the edit button can be clicked also in the read-only mode. For all the
        // other columns, the editable value depends on the super class
        // implementation.
//        if (col == groupingColumnIdx) {
//            //Grouping is not required as Element BHs are not aggregated to DAYBH Tables
//            //but they use the RAW and RANKBH in reporting.
//            return !disableGrouping && super.isCellEditable(row, col);
//        } else {
            return col == descriptionColumnIdx || col == sourceColumnIdx || col == whereColumnIdx ||
                   col == criteriaColumnIdx    || col == keysColumnIdx   || col == aggregationTypeColumnIdx ||
                   col == mappedTypesColumnIdx || col == clauseColumnIdx || super.isCellEditable(row, col);
//        }
    }

	/**
	 * Set the RockDBObject data of the table.
	 * This is overridden from the super class as it checks to see if the Busyhours are ELEM types, if they are,
	 * the Grouping is disabled & defaults to None as its not required, as Element BHs are not aggregated to DAYBH Tables
	 * but they use the RAW and RANKBH in reporting
	 * @param inData vector of RockDBObjects
	 */
    @Override
    public void setData(final Vector<Object> inData) {
        for(Object o : inData){
            if(o instanceof BusyHourData){
                final BusyHourData bhd = (BusyHourData)o;
                //disableGrouping = bhd.getBusyhour().getBhlevel().endsWith("ELEMBH");
                break;
            }
        }
        super.setData(inData);
    }

    /**
     * This method is an override of the method in TTTableModel. This is used to
     * determine if adding a duplicate row will exceed the max allowed
     * placeholders.
     */
    @Override
    public void duplicateRow(final int[] selectedRows, final int times) {
    	if(selectedRows.length==0) {
    		JOptionPane.showMessageDialog(null, selectedRows.length + " rows selected. Please select and highlight at least one row.");
    		return;
    	}
    	int number = this.getRowCount() + (times * selectedRows.length);
    	if (number <= Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS) {
    		//For Busyhour doesn't suit to call super.duplicateRow(selectedRows, times);
    		Object sourceData = null;
    		for (int i = 0; i < times; i++) {
    			for (int j = 0; j < selectedRows.length; j++) {
    				sourceData = displayData.elementAt(selectedRows[j]);
    				Object newOne = null;
    				if(sourceData instanceof BusyHourData) {
    					// Need to change the Placeholder number.
    					try {
    						copyTo((BusyHourData)sourceData);
    					} catch (Exception e) {
    						logger.warning("Error copying row "+((BusyHourData)sourceData).getBusyhour().getBhtype()+ ", "+e.getMessage());
    					}
    				} else {
    					//Normal behaviour like super.duplicateRow(selectedRows, times);
    					copyOf(sourceData);
    					insertDataLast(newOne);
    				}//if(newOne instanceof BusyHourData)
    			} //for (int j = 0; j < selectedRows.length; j++)
    		} //for (int i = 0; i < times; i++)
    		refreshTable();
    		fireTableDataChanged();
    	} else {
    		JOptionPane.showMessageDialog(null, "Invalid number of rows. Duplicating " + selectedRows.length + " row(s), "
    				+ times + " time(s) exceeds the maximum number of allowed Placeholders ("
    				+ Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + ")");
    	}
    }
//    @Override
//    public void duplicateRow(final int[] selectedRows, final int times) {
//        int number = this.getRowCount() + (times * selectedRows.length);
//        if (number <= Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS) {
//            super.duplicateRow(selectedRows, times);
//        } else {
//            JOptionPane.showMessageDialog(null, "Invalid number of rows. Duplicating " + selectedRows.length + " row(s), "
//                    + times + " time(s) exceeds the maximum number of allowed Placeholders ("
//                    + Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + ")");
//        }
//    }

    /**
     * This method is an override of the method in TTTableModel. This is used to
     * determine if adding multiple rows will exceed the max allowed placeholders.
     */
    @Override
    public void addMultipleRows(final int times) {
    	int numberOfPlaceholders = this.getRowCount();
        if (times <= Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS) {
            for (int i = 0; i < times; i++) {
            	BusyHourData toBeInserted = this.getEmptyBusyHourData();
            	toBeInserted.setBHType(numberOfPlaceholders++);
            	toBeInserted.generateBusyHourMappings();
                if (toBeInserted != null) {
                  insertDataLast(toBeInserted.clone());
                }
              }
              refreshTable();
              this.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid number of rows. Adding " + times
                    + " row(s), exceeds the maximum number of allowed Placeholders ("
                    + Constants.MAX_NUMBER_OF_BH_PRODUCT_PLACE_HOLDERS + ")");
        }
    }

    //TODO: Need to improve the validation of Busyhours.
    @Override
    public Vector<String> validateData() {

        final Vector<String> errorStrings = new Vector<String>();
        
        int rowCount = 0;
        
        for (Object obj : data) {
        	//increment the number of the row we are currently looking at.
        	rowCount++;
        	
            final BusyHourData item = (BusyHourData) obj;

            // If the busy hour is marked enabled, or has data filled in, 
            // then check for missing mandatory values.
			final Busyhour bh = item.getBusyhour();	
			
            if ((1 == bh.getEnable()) || isBHNotEmpty(item)) {
                // At least one source needs to be defined.
            	
                if (Utils.replaceNull(item.getBusyhourSource().size()) <= 0) {
//                	int placholderNumber = getplacholderNumber(item);
                	String placholderName = getTableName().substring(0, getTableName().length()-1 ); // Get rid of last letter 's' from the Product/Custom Placeholders
                	errorStrings.add(item.getBusyhour().getTargetversionid()+ " " + item.getBusyhour().getBhlevel()+ " " + placholderName + " " + rowCount + ": At least one source is required.");//placholderNumber + ": At least one source is required.");
                }

                // Formula needs to be defined
                if (Utils.replaceNull(item.getBusyhour().getBhcriteria()).trim().equals("")) {
//                	int placholderNumber = getplacholderNumber(item);
                	String placholderName = getTableName().substring(0, getTableName().length()-1 ); // Get rid of last letter 's' from the Product/Custom Placeholders
                    errorStrings.add(item.getBusyhour().getTargetversionid()+ " " + item.getBusyhour().getBhlevel() + " " + placholderName + " " + rowCount + ": Formula is required."); //placholderNumber + ": Formula is required.");
                }

            }

            // Checking for duplicate values for BHs (BhLevel, BhType, BhObject, and
            // AggregationType)
            final Vector<Object> v = data;
//            for (int i = 0; i < v.size(); i++) {
//                for (int i2 = i + 1; i2 < v.size(); i2++) {
//                    if (((BusyHourData) v.get(i)).getBusyhour().getBhlevel().equals(
//                            ((BusyHourData) v.get(i2)).getBusyhour().getBhlevel())
//                            && ((BusyHourData) v.get(i)).getBusyhour().getBhtype().equals(
//                            ((BusyHourData) v.get(i2)).getBusyhour().getBhtype())
//                            && ((BusyHourData) v.get(i)).getBusyhour().getBhobject().equals(
//                            ((BusyHourData) v.get(i2)).getBusyhour().getBhobject())) {
//                        errorStrings.add("Duplicate busy hour defined for support"
//                                + ((BusyHourData) v.get(i)).getBusyhour().getBhlevel() + ".");
//                    }
//                }
//            }

            // Checking for duplicate values for Rank Keys for this BH.
            final List<Busyhourrankkeys> v2 = item.getBusyhourRankkeys();
            for (int i = 0; i < v2.size(); i++) {
                for (int i2 = i + 1; i2 < v2.size(); i2++) {
                    if (v2.get(i).equals(v2.get(i2))) {
                        errorStrings.add("Duplicate rank keys " + v2.get(i).getKeyname() + " for Busy Hour support "
                                + v2.get(i).getBhlevel() + "at row "+ rowCount +".");
                    }
                }
            }

            // Checking for duplicate values for Sources for this BH.
            final List<Busyhoursource> v3 = item.getBusyhourSource();
            for (int i = 0; i < v3.size(); i++) {
                for (int i2 = i + 1; i2 < v3.size(); i2++) {
                    if (v3.get(i).equals(v3.get(i2))) {
                        errorStrings.add("Duplicate sources " + v3.get(i).getTypename() + " for Busy Hour support "
                                + v3.get(i).getBhlevel() + "at row "+ rowCount +".");
                    }
                }
            }

        }

        return errorStrings;
    } // validateData
    
    /**
     * An unused BusyHour is Empty.
     * BusyHour considered to be in use if BhCriteria/Description/WhereClause if filled OR has at least 1 source.
     * @param bh
     * @return
     */
    protected boolean isBHNotEmpty(final BusyHourData item) {
    	boolean result = false;
    	final Busyhour bh = item.getBusyhour();
    	if (Utils.replaceNull(item.getBusyhourSource().size()) > 0) {
    		result = true;
    	}
    	if (!Utils.replaceNull(bh.getBhcriteria()).trim().equals("")) {
    		result = true;
        }
    	if (!Utils.replaceNull(bh.getDescription()).trim().equals("")) {
    		result = true;
        }
    	if (!Utils.replaceNull(bh.getWhereclause()).trim().equals("")) {
    		result = true;
        }
    	return result;
    }
    
    
    /**
    * Get the placeholder number.
    * @param bh
    * @return
    */

    protected int getplacholderNumber(final BusyHourData item){
		int placholderNumber = -1;
		String bhType = "";
		String bhLevel = "";
		try {
			final Busyhour busyhour = item.getBusyhour();
			bhLevel = busyhour.getBhlevel();
			bhType = busyhour.getBhtype(); // PP0/CP0/CTP_PP0/CTP_CP0
			final String placholderType = busyhour.getPlaceholdertype(); // CP/PP
			// Get number after placeholderType. //E.g. CTP_PP0=0
			final String placeHolderValue = bhType.substring(
					bhType.lastIndexOf(placholderType)
							+ placholderType.length(), bhType.length());
			placholderNumber = Integer.parseInt(placeHolderValue) + 1;
		} catch (Exception e) {
			placholderNumber = -1; // Default to zero on Error
			logger.warning("Error parsing PlaceHolder number for BhType: '" + bhType + "', bhLevel:" + bhLevel + ", Exception:" + e.getMessage());
		}
		return placholderNumber;
    }

//    /**
//     * Get the placeholder number.
//     * @param bh
//     * @return
//     */
//    protected int getplacholderNumber(final BusyHourData item){
//    	String placholderType=item.getBusyhour().getBhtype();
//    	String placeHolderValue = placholderType.substring(2, placholderType.length()); // Get rid of first two letters CP/PP
//    	int placholderNumber = Integer.parseInt(placeHolderValue)+1;
//    	return placholderNumber;
//    }
    
    /**
     * This method is called when the popup menu is created. It is used to
     * determine if the "Duplicate Row" menu option should be enabled. It should
     * only be enabled it there is at least 1 empty placeholder.
     * <p/>
     * This method is an override of the method in TTTableModel
     */
    @Override
    public boolean isTableDuplicateRowAllowed() {
        return true;
    }

    /**
     * This method is called when the popup menu is created. It is used to
     * determine if the "Delete Row" menu option should be enabled. This method is
     * an override of the method in TTTableModel
     */
    @Override
    public boolean isTableRemoveAllowed() {
        return false;
    }

    
    /**
     * This method is called when the popup menu is created. It is used to
     * determine if the "Add Row" and "Add Multiple Rows" menu options should be
     * enabled. This method is an override of the method in TTTableModel
     */
    
    @Override
    public boolean isTableAddAllowed() {
    	//eanguan 20110818 :: Disabling the add multiple rows/add row for Busy Hour Placeholder tables as it was causing issues 
    	// because if you add rows in BusyHourPlaceholder table then the change in number of rows will not reflect in other places
    	// and will cause mismatch in other places and database tables
    	// If you want to change the number of rows it can be done from the respective Element BusyHour Measurement under Measurement TAB
    	boolean result= checkTPType(emptyBusyHourData);
       	  return result;
        
    }
    
    public boolean checkTPType(BusyHourData bhd){
    	String techpackType = bhd.getVersioning().getTechpack_type();
   	    if(techpackType.equals("CUSTOM")){
 		   return true;
 	    }
 	    return false;
    }

    @Override
    public JPopupMenu getPopupMenu(final Component targetComponent, final TableContainer tableContainer) {
        final JPopupMenu menu = new JPopupMenu();

        final TablePopupMenuListener listener = new TablePopupMenuListener(tableContainer);

        menu.add(newMenuItem(TablePopupMenuListener.CLEAR_ROW, listener, true, true, true, true, false));
        
        //The functionality to delete is removed. Deletion causes problems for reordering in the placeholders.
        //WARNING: do not re-enable this option unless you intend to implement re-ordering and re-generation of Placeholders.
        //menu.add(newMenuItem(TablePopupMenuListener.DELETE, listener, true, true, true, true, false));
        
        menu.addSeparator();
        menu.add(newMenuItem(TablePopupMenuListener.DUPLICATE_ROW, listener, true, true, true, true, false));
//        menu.add(newMenuItem(TablePopupMenuListener.ADD, listener, true, false, true, true, false));
        menu.add(newMenuItem(TablePopupMenuListener.ADD_MULTIPLE_ROWS, listener, true, false, true, true, false));
        return menu;
    }


    public void editingStopped(final ChangeEvent e) {
        if(aggTypeEditor != null && e.getSource().equals(aggTypeEditor)){
            refreshTable();
            fireTableDataChanged();
        }
    }

    public void editingCanceled(final ChangeEvent e) {
        //ignored
    }
	public void copyTo(final BusyHourData sourceData) throws Exception {
		final int index = getNextEmptyPlaceholder(getData());
		if(index == -1 || index > getMaxRowsToShow()) {
			throw new Exception("Placeholders full.");
		}
		final BusyHourData dest = (BusyHourData)data.get(index);

		final Busyhour pp = dest.getBusyhour();
		final Busyhour sp = sourceData.getBusyhour();

		pp.setVersionid(sp.getVersionid());
    pp.setBhlevel(sp.getBhlevel());
    pp.setBhcriteria(sp.getBhcriteria());
    pp.setWhereclause(sp.getWhereclause());
    pp.setDescription(sp.getDescription());
    pp.setTargetversionid(sp.getTargetversionid());
    pp.setBhobject(sp.getBhobject());
    pp.setBhelement(sp.getBhelement());
    pp.setAggregationtype(sp.getAggregationtype());
    pp.setOffset(sp.getOffset());
    pp.setWindowsize(sp.getWindowsize());
    pp.setLookback(sp.getLookback());
    pp.setP_threshold(sp.getP_threshold());
    pp.setN_threshold(sp.getN_threshold());
    pp.setClause(sp.getClause());
    pp.setGrouping(sp.getGrouping());
    pp.setReactivateviews(1);
		pp.setEnable(sp.getEnable());

		final List<Busyhourrankkeys> newRankKeys = new ArrayList<Busyhourrankkeys>();
		for (Busyhourrankkeys key : sourceData.getBusyhourRankkeys()) {
      Busyhourrankkeys newKey = (Busyhourrankkeys) key.clone();
      newKey.setNewItem(true);
      newKey.setBhtype(pp.getBhtype());
			newRankKeys.add(newKey);
    }
		dest.setBusyhourRankkeys(newRankKeys);

		final List<Busyhoursource> newSources = new ArrayList<Busyhoursource>();
		for (Busyhoursource source : sourceData.getBusyhourSource()) {
      final Busyhoursource newSource = (Busyhoursource) source.clone();
      newSource.setNewItem(true);
			newSource.setBhtype(pp.getBhtype());
			newSources.add(newSource);
    }
		dest.setBusyhourSource(newSources);
		refreshTable();
		fireTableDataChanged();
	}
}
