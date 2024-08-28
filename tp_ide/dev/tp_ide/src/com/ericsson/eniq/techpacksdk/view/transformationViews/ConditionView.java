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
public class ConditionView extends GenericTransformationView {

  private JTextField factorTF = null;

  private JTextField factorfieldTF = null;

  private JTextField oneFactor = null;

  private JTextField result1TF = null;

  private JTextField result1fieldTF = null;

  private JTextField oneResult1 = null;

  private JTextField result2TF = null;

  private JTextField result2fieldTF = null;

  private JTextField oneResult2 = null;

  public ConditionView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    oneFactor = new JTextField();
    // factor
    String name = "factor";
    String toolTip = "String where Source is compared to.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    factorTF = new JTextField(25);
    factorTF.setName(name);
    factorTF.setToolTipText(toolTip);
    factorTF.setText(getOrigValue(name));
    textFieldContainer.add(factorTF);
    oneFactor.add(factorTF);
    parent.add(factorTF, c);

    // factorfield
    name = "factorfield";
    toolTip = "The field where Source is compared to (if factor is not set)";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    factorfieldTF = new JTextField(25);
    factorfieldTF.setName(name);
    factorfieldTF.setToolTipText(toolTip);
    factorfieldTF.setText(getOrigValue(name));
    textFieldContainer.add(factorfieldTF);
    oneFactor.add(factorfieldTF);
    parent.add(factorfieldTF, c);
    //oneFactor.doValidation();

    oneResult1 = new JTextField();
    // result1
    name = "result1";
    toolTip = "Value which is set to Target if Source and factor equals.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    result1TF = new JTextField(25);
    result1TF.setName(name);
    result1TF.setToolTipText(toolTip);
    result1TF.setText(getOrigValue(name));
    textFieldContainer.add(result1TF);
    oneResult1.add(result1TF);
    parent.add(result1TF, c);

    // result1field
    name = "result1field";
    toolTip = "Field, which value is set to Target if Source and factor equals (if result1 is not set)";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    result1fieldTF = new JTextField(25);
    result1fieldTF.setName(name);
    result1fieldTF.setToolTipText(toolTip);
    result1fieldTF.setText(getOrigValue(name));
    textFieldContainer.add(result1fieldTF);
    oneResult1.add(result1fieldTF);
    parent.add(result1fieldTF, c);

    oneResult2 = new JTextField();
    // result2
    name = "result2";
    toolTip = "Value which is set to Target if Source and factor do not equal.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    result2TF = new JTextField(25);
    result2TF.setName(name);
    result2TF.setToolTipText(toolTip);
    result2TF.setText(getOrigValue(name));
    textFieldContainer.add(result2TF);
    oneResult2.add(result2TF);
    parent.add(result2TF, c);

    // result1field
    name = "result2field";
    toolTip = "Field, which is set to Target if Source and factor do not equal.(if result2 is not set)";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    result2fieldTF = new JTextField(25);
    result2fieldTF.setName(name);
    result2fieldTF.setToolTipText(toolTip);
    result2fieldTF.setText(getOrigValue(name));
    textFieldContainer.add(result2fieldTF);
    oneResult2.add(result2fieldTF);
    parent.add(result2fieldTF, c);

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

    if (factorfieldTF.getText().equals("") && factorTF.equals(""))
      error.append("Factor or factorfield1 is mandatory field.\n");

    if (result1TF.getText().equals("") && result1fieldTF.equals(""))
      error.append("Result1 or result1field is mandatory field.\n");

    if (result2TF.getText().equals("") && result2fieldTF.equals(""))
      error.append("Result2 or result2field is mandatory field.\n");

    return error.toString();
  }
}
