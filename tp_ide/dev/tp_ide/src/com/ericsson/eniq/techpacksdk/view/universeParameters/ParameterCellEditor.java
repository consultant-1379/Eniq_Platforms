package com.ericsson.eniq.techpacksdk.view.universeParameters;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class ParameterCellEditor extends DefaultCellEditor {

	//private GenericUniverseTableModel<Universeparameters> subTableModel;
	private boolean editable;
	
	public ParameterCellEditor(DataModelController dataModelController, String versionId, boolean editable) {
		super(new JTextField()); // Use a dummy textfield, it will be replaced with
    							 // a ExpandableSubTable component.
    	
    	this.editable = editable;
    	
    	// Replace the editor component with a ExpandableSubTable component
    	final ParameterCellExpandableSubTable textField = new ParameterCellExpandableSubTable(this, dataModelController, versionId,  this.editable);
    	this.editorComponent = textField;
    	this.clickCountToStart = 1;
    	
        this.delegate = new EditorDelegate() {
            public void setValue(Object value) {
            	textField.setText((value != null) ? value.toString() : "");
            }
            public Object getCellEditorValue() {
            	return textField.getText();
            }
        };
    }

    @Override
    public boolean stopCellEditing() { 
	    if (this.editable) {
	    	return super.stopCellEditing();
	    } else {
	    	return true;
	    }
	}

    @Override
    public void cancelCellEditing() { 
    	if (this.editable) {
	    	super.cancelCellEditing();
    	}
    }
}
