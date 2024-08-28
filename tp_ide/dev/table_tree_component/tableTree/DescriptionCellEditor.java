package tableTree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import tableTreeUtils.DescriptionComponent;

/**
 * This class is a customized table cell editor for table columns of type
 * DescriptionComponent. It is used e.g. in the MeasurementTypeKeyTableModel for
 * the Description column.
 * 
 * @author eheitur
 */
public class DescriptionCellEditor extends AbstractCellEditor implements
	TableCellEditor, ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The description component
     */
    private DescriptionComponent theComp = null;

    /**
     * Current text value of the cell
     */
    private String currentValue = "";

    /**
     * Is the tree editable
     */
    private boolean treeEditable = true;

    /**
     * Constructor.
     * 
     * @param editable
     *                true if the tree is editable.
     */
    public DescriptionCellEditor(boolean editable) {
	this.treeEditable = editable;
    }

    /**
     * This returns the description component as the editor to the table. This
     * is also where we get the initial value for the components text field.
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param col
     * @return the editor component
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int col) {

	// Create a new description component with the current string value and
	// the number of characters in the text field. NOTE: The number of
	// characters does not affect anything when used in the table cell. That
	// is why a zero is used.
	currentValue = (String) value;
	theComp = new DescriptionComponent(currentValue, 0);

	// Add the action lister, so that we can catch the description action
	// when the user updates the cell value.
	theComp.addActionListener(this);

	// Set the component enabled based on the tree enabled status.
	theComp.setEnabled(this.treeEditable);

	return theComp;
    }

    /**
     * Overridden version of the method. Returns the current text value.
     * 
     * @return the current text value
     */
    public Object getCellEditorValue() {
	return currentValue;
    }

    /**
     * Overridden version of the method. The description action is caught so
     * that the editing can be stopped when the used updates the value in the
     * cell.
     * 
     * @param e
     *                the action event
     */
    public void actionPerformed(ActionEvent e) {
	fireEditingStopped();
    }

    /**
     * Overridden version of the method. In addition to passing on the editing
     * stopped action, the current text value is stored from the components
     * textField.
     */
    protected void fireEditingStopped() {
	if (!(currentValue.equals(theComp.getTextField().getText()))) {
	    currentValue = theComp.getTextField().getText();
	    // stopCellEditing();
	    super.fireEditingStopped();
	} else if (currentValue.equals(theComp.getTextField().getText())) {
	    fireEditingCanceled();
	}
    }

}
