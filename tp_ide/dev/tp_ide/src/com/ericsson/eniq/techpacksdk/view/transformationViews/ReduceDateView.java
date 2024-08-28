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
public class ReduceDateView extends GenericTransformationView {

  private JTextField formatTF = null;

  private JTextField secondsTF = null;

  private JTextField secondsfieldTF = null;

  public ReduceDateView(JPanel parent, Transformation transformation) {
    super();

    setTransformation(transformation);
    parent.removeAll();
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    String name = "format";
    String toolTip = "Format of the input date string. For example yyyyMMddHHmmss.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    formatTF = new JTextField();
    formatTF.setName(name);
    formatTF.setToolTipText(toolTip);
    formatTF.setText(getOrigValue(name));
    textFieldContainer.add(formatTF);
    parent.add(formatTF, c);

    name = "seconds";
    toolTip = "Number of seconds to reduce from the date given in source.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    secondsTF = new JTextField(25);
    secondsTF.setName(name);
    secondsTF.setToolTipText(toolTip);
    secondsTF.setText(getOrigValue(name));
    textFieldContainer.add(secondsTF);
    parent.add(secondsTF, c);

    name = "secondsfield";
    toolTip = "Name of the field to take the number of seconds to reduce from the date given in source.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    secondsfieldTF = new JTextField(25);
    secondsfieldTF.setName(name);
    secondsfieldTF.setToolTipText(toolTip);
    secondsfieldTF.setText(getOrigValue(name));
    textFieldContainer.add(secondsfieldTF);
    parent.add(secondsfieldTF, c);

  }

  /**
   * 
   * @return
   */
  public String validate() {
    StringBuilder error = new StringBuilder("");

    if (formatTF.getText().equals("") )
      error.append("Format must be filled.\n");

    return error.toString();
  }
}
