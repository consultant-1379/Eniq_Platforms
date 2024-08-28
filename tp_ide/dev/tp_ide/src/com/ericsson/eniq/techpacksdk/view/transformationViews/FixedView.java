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
public class FixedView extends GenericTransformationView{

  private JTextField valueTF = null;

  public FixedView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();

    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    
    
    // format field
    String name = "value";
    String toolTip = "The value to set to target";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);    
    c.gridx = 1;
    c.gridy = 0;
    valueTF = new JTextField(25);
    valueTF.setName(name);
    valueTF.setToolTipText(toolTip);
    valueTF.setText(getOrigValue(name));
    textFieldContainer.add(valueTF);
    parent.add(valueTF, c);
    
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * 
   * @return
   */
  public String validate() {
    if (valueTF.getText().equals(""))
      return "Value must be filled.\n";      

    return "";
  }

}
