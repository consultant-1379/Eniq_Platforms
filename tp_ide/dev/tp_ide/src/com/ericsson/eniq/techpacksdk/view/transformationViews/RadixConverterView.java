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
public class RadixConverterView extends GenericTransformationView {

  private JTextField fromRadixTF = null;
  private JTextField toRadixNameTF = null;

  public RadixConverterView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    //fromRadix
    String fromRadixName = "fromRadix";
    String toolTip = "Default: 10";
    c.gridx = 0;
    c.gridy = 0;
    JLabel fromRadixLabel = new JLabel(fromRadixName);
    fromRadixLabel.setToolTipText(toolTip);
    parent.add(fromRadixLabel, c);
    c.gridx = 1;
    fromRadixTF = new JTextField(25);
    fromRadixTF.setName(fromRadixName);
    fromRadixTF.setToolTipText(toolTip);
    fromRadixTF.setText(getOrigValue(fromRadixName));
    textFieldContainer.add(fromRadixTF);
    parent.add(fromRadixTF, c);

    //toRadix
    String toRadixName = "toRadix";
    toolTip = "Default: 16";
    c.gridx = 0;
    c.gridy = 1;
    JLabel toRadixLabel = new JLabel(toRadixName);
    toRadixLabel.setToolTipText(toolTip);
    parent.add(toRadixLabel, c);
    c.gridx = 1;
    toRadixNameTF = new JTextField();
    toRadixNameTF.setName(toRadixName);
    toRadixNameTF.setToolTipText(toolTip);
    toRadixNameTF.setText(getOrigValue(toRadixName));
    textFieldContainer.add(toRadixNameTF);
    parent.add(toRadixNameTF, c);
    
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
    return error.toString();
  }
}
