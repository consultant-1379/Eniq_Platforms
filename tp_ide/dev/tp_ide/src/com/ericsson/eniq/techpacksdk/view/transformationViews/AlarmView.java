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
public class AlarmView extends GenericTransformationView {

  private JTextField formatTF = null;

  private JTextField filenamepatternTF = null;

  private JTextField oneValue = null;

  
  public AlarmView(JPanel parent, Transformation transformation) {
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
    String name = "filenamePattern";
    String toolTip = "Pattern to return the first group of the matching pattern.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    filenamepatternTF = new JTextField(25);
    filenamepatternTF.setName(name);
    filenamepatternTF.setToolTipText(toolTip);
    filenamepatternTF.setText(getOrigValue(name));
    textFieldContainer.add(filenamepatternTF);
    oneValue.add(filenamepatternTF);
    parent.add(filenamepatternTF, c);

    // factorfield
    name = "format";
    toolTip = "Time format. For example yyyyMMddHHmmss";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
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

    if (formatTF.getText().equals("") && filenamepatternTF.equals(""))
      error.append("Format or filenamepattern is mandatory field.\n");

    return error.toString();
  }
}
