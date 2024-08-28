package ttctests;

import java.util.Vector;

import javax.swing.JFrame;

import tableTree.TableTreeComponent;
import tableTreeUtils.FilterDialog;
import tableTreeUtils.TableInformation;

public class StubbedFilterDialog extends FilterDialog {

    public StubbedFilterDialog(TableTreeComponent inTree, Vector<TableInformation> inTableInfo) {
	super(inTree, inTableInfo);
    }

    
    @Override
    protected void clearFilter() {
	super.clearFilter();
    }


    @Override
    protected void setFilter() {
	super.setFilter();
    }

    public JFrame showDialog(){
	return super.showDialog();
    }

    public void setFilterString(String exp){
	filterTextField.setText(exp);
    }
    
    public void setSelectedColumn(int index){
	selectedColumn = index;
    }
    
    public void setSelectedTable(int index){
	selectedTable = index;
    }
}
