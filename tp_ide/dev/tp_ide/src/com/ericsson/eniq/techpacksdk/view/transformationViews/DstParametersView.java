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
public class DstParametersView extends GenericTransformationView {
  
  private JTextField timezoneTF = null;

  public DstParametersView(JPanel parent, Transformation transformation) {
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
    String name = "timezone";
    String toolTip = "ID of timezone used (uses current timezone of host if not defined)";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    timezoneTF = new JTextField(25);
    timezoneTF .setName(name);
    timezoneTF .setToolTipText(toolTip);
    timezoneTF .setText(getOrigValue(name));
    textFieldContainer.add(timezoneTF );
    parent.add(timezoneTF , c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * 
   * @return
   */
  public String validate() {
    return "";
  }
}
