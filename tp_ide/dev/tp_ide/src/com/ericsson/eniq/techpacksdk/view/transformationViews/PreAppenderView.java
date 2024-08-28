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
public class PreAppenderView extends GenericTransformationView {

  private JTextField fieldTF = null;

  private JTextField fixedTF = null;

  private JTextField oneValue = null;

  public PreAppenderView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    oneValue = new JTextField();
    // factor
    String name = "field";
    String toolTip = "Name of the field which is preappended to the field source.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    fieldTF = new JTextField(25);
    fieldTF.setName(name);
    fieldTF.setToolTipText(toolTip);
    fieldTF.setText(getOrigValue(name));
    textFieldContainer.add(fieldTF);
    oneValue.add(fieldTF);
    parent.add(fieldTF, c);

    // factorfield
    name = "fixed";
    toolTip = "Fixed value which is preappended to the field source.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    fixedTF = new JTextField(25);
    fixedTF.setName(name);
    fixedTF.setToolTipText(toolTip);
    fixedTF.setText(getOrigValue(name));
    textFieldContainer.add(fixedTF);
    oneValue.add(fixedTF);
    parent.add(fixedTF, c);
    //oneValue.doValidation();

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

    if (fieldTF.getText().equals("") && fixedTF.equals(""))
      error.append("Fixed or field is mandatory field.\n");

    return error.toString();
  }
}
