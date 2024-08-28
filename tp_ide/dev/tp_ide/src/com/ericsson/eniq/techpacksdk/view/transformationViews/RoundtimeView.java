package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * 
 * @author etogust
 * 
 */
public class RoundtimeView extends GenericTransformationView {

  private JTextField formatTF = null;

  private JTextField minuteTF = null;

  private JTextField hourTF = null;

  private JTextField oneValue = new JTextField();

  public RoundtimeView(JPanel parent, Transformation transformation) {
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
    c.gridy++;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    formatTF = new JTextField(25);
    formatTF.setName(name);
    formatTF.setToolTipText(toolTip);
    formatTF.setText(getOrigValue(name));
    textFieldContainer.add(formatTF);
    oneValue.add(formatTF);
    parent.add(formatTF, c);

    name = "minute";
    toolTip = "If contains any data, the date in source is rounded to the nearest minute.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    minuteTF = new JTextField(25);
    minuteTF.setName(name);
    minuteTF.setToolTipText(toolTip);
    minuteTF.setText(getOrigValue(name));
    textFieldContainer.add(minuteTF);
    oneValue.add(minuteTF);
    parent.add(minuteTF, c);

    name = "hour";
    toolTip = "If contains any data, the date in source is rounded to the nearest hour.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    hourTF = new JTextField(25);
    hourTF.setName(name);
    hourTF.setToolTipText(toolTip);
    hourTF.setText(getOrigValue(name));
    textFieldContainer.add(hourTF);
    oneValue.add(hourTF);
    // oneValue.doValidation();
    parent.add(hourTF, c);

  }

  /**
   * 
   * @return
   */
  public String validate() {
    StringBuilder error = new StringBuilder("");
    int i = 0;
    
    if (formatTF.getText().trim().length()>0) {
      i++;
    }
    
    if (i < 1) {
      error.append("Format must be filled.\n");
    }
    

    return error.toString();
  }
}
