/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * @author eheijun
 *
 */
@SuppressWarnings("serial")
public class RoleComboEditor extends DefaultCellEditor {

  public RoleComboEditor(JComboBox comboBox) {
    super(comboBox);
  }

  public Component getTableCellEditorComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected,
      int rowIndex, int vColIndex) {

    JComboBox comboBox = (JComboBox) this.getComponent();
    comboBox.setSelectedIndex(-1);
    for (int ind = 0; ind < comboBox.getItemCount(); ind++) {
      if (comboBox.getItemAt(ind).equals(value)) {
        comboBox.setSelectedItem(value);
        break;
      }
    }
    
    //added for TR HO26863- Admin role should not be changed to user/rnd
    if( table.getValueAt(rowIndex, 0).equals("admin") ){
  	  comboBox.setEnabled(false);
    }
    else{
    	comboBox.setEnabled(true);
    }
    
    return this.getComponent();

  }

}
