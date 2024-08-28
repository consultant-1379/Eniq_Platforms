package tableTree;

import javax.swing.DefaultCellEditor;

import tableTreeUtils.LimitedRangeIntegerField;

/**
 * Table cell editor for integer fields with limited value range.
 * 
 * @author eheitur
 * 
 */
public class LimitedRangeIntegerTableCellEditor extends DefaultCellEditor {

    private static final long serialVersionUID = 8599483547945333667L;

    /**
     * Constructor, creates a limited range integer field. NOTE: The required
     * field has no real effect here, because it is not possible to empty the
     * cell value for an integer. The cell editing is always canceled if a null
     * or an empty value is given.
     * 
     * @param minvalue
     * @param maxvalue
     * @param required
     */
    public LimitedRangeIntegerTableCellEditor(int minvalue, int maxvalue,
	    boolean required) {
	super(new LimitedRangeIntegerField(minvalue, maxvalue, required));
    }

    /**
     * Overridden version of the method to be able to set the old value back in
     * case a null or an empty value is entered as the number.
     */
    @Override
    public boolean stopCellEditing() {
	// If the edited value is either null or empty, then cancel the cell
	// editing because it is not possible to set that value for an integer.
	if (this.getCellEditorValue().equals("")
		|| this.getCellEditorValue() == null) {
	    super.cancelCellEditing();
	}
	return super.stopCellEditing();
    }
}
