package tableTree;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * Table cell editor for combo boxes.
 * 
 * @author ejeahei enaland
 * 
 */
public class ComboBoxTableCellEditor extends DefaultCellEditor {

    private static final long serialVersionUID = 138274068645143219L;

    /**
     * Constructor, creates a combo box based on the given combo items.
     * 
     * @param items
     */
    public ComboBoxTableCellEditor(Object[] items) {
	super(new JComboBox(items));
    }

    /**
     * Overridden version of the method. Special implementation is needed for
     * handling possible null values from the database. Returns the combo box
     * table cell editor component.
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param rowIndex
     * @param vColIndex
     * @return the editor component
     */
    public Component getTableCellEditorComponent(javax.swing.JTable table,
	    java.lang.Object value, boolean isSelected, int rowIndex,
	    int vColIndex) {

	((JComboBox) this.getComponent()).setSelectedIndex(-1);
	for (int ind = 0; ind < ((JComboBox) this.getComponent())
		.getItemCount(); ind++) {
	    if (((JComboBox) this.getComponent()).getItemAt(ind).equals(value)) {
		((JComboBox) this.getComponent()).setSelectedItem(value);
		break;
	    }
	}

	return this.getComponent();

    }

}
