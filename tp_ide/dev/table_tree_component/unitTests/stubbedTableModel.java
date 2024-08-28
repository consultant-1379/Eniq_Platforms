package unitTests;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Vector;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

public class stubbedTableModel extends TTTableModel {

    String[] columnNames = { "1", "2" };

    private static final long serialVersionUID = -2085363530666596818L;

    public stubbedRockDBObject newObject = null;

    public stubbedTableModel(RockFactory RF, Vector<TableInformation> tableInfos) {
	super(RF, tableInfos, true);
	setColumnNames(columnNames);
    }

    @Override
    public RockDBObject createNew() {
	return newObject;
    }

    @Override
    protected void deleteData(Object rockObject) throws SQLException,
	    RockException {
	newObject.deleteDB();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
	return ((stubbedRockDBObject) data.elementAt(rowIndex)).value0;
    }

    @Override
    protected void saveData(Object rockObject) throws SQLException,
	    RockException {
	newObject.saveDB();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
	if (value != null) { 
	    newObject.value0 = ((Integer) value).intValue();
	}  else newObject.value0 = 0;  
    }

    @Override
    public Object getColumnValueAt(Object dataObject, int columnIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object getOriginalValueAt(int rowIndex, int columnIndex) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object copyOf(Object toBeCopied) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getColumnFilterForTableType(int column) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isColumnFilteredForTableType(int column) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void update(Observable sourceObject, Object sourceArgument) {
	// TODO Auto-generated method stub

    }

    @Override
    public Vector<String> validateData() {
	// TODO Auto-generated method stub
	return new Vector<String>();
    }

}
