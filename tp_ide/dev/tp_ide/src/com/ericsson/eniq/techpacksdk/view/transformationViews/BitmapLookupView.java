package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * 
 * @author etogust
 * 
 */
public class BitmapLookupView extends GenericTransformationView {
  
  private JTextField tablenameTF = null;

  public BitmapLookupView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();
  
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    //old
    String name = "tablename";
    String toolTip = "Name of the table where the bitmask values and names are retrieved.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    tablenameTF = new JTextField(25);
    
    tablenameTF .setName(name);
    tablenameTF .setToolTipText(toolTip);
    tablenameTF .setText(getOrigValue(name));
    textFieldContainer.add(tablenameTF );
    parent.add(tablenameTF , c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * 
   * @return
   */
  public String validate() {
    StringBuilder error = new StringBuilder("");

    if (tablenameTF.getText().equals(""))
      error.append("Tablename is mandatory field.\n");
    
    return error.toString();
  }
}
