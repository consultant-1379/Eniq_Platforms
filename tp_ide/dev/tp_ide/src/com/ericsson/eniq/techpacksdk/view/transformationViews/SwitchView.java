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
public class SwitchView extends GenericTransformationView {

  private JTextField oldTF = null;

  public SwitchView(JPanel parent, Transformation transformation) {
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
    String name = "old";
    String toolTip = "Value to compare source to. If old equals source then new is set to target.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldTF = new JTextField(25);
    oldTF.setName(name);
    oldTF.setToolTipText(toolTip);
    oldTF.setText(getOrigValue(name));
    textFieldContainer.add(oldTF);
    parent.add(oldTF, c);

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

    if (oldTF.getText().equals(""))
      error.append("Old is mandatory field.\n");
    
    return error.toString();
  }
}
