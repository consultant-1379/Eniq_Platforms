package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.distocraft.dc5000.repository.dwhrep.Transformation;


/**
 * 
 * @author etogust
 * 
 */
public class LookupView extends GenericTransformationView {

  private JTextField patternTF = null;

  private JTextField ipatternTF = null;

  private JTextField epatternTF = null;
  
  public LookupView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    // pattern field
    String name = "pattern";
    String toolTip = "Regular expression pattern to return the first group of the matching pattern.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    patternTF = new JTextField(25);

    patternTF.setName(name);
    patternTF.setToolTipText(toolTip);
    patternTF.setText(getOrigValue(name));
    textFieldContainer.add(patternTF);
    parent.add(patternTF, c);

    // ipattern field
    name = "ipattern";
    toolTip = "Regular expression pattern to return the matching part from the beginning of the string.";
    c.gridx = 0;
    c.gridy = 1;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    ipatternTF = new JTextField();

    ipatternTF.setName(name);
    ipatternTF.setToolTipText(toolTip);
    ipatternTF.setText(getOrigValue(name));
    textFieldContainer.add(ipatternTF);
    parent.add(ipatternTF, c);

    // epattern field
    name = "epattern";
    toolTip = "Regular expression pattern to return the matching part to the end of the string.";
    c.gridx = 0;
    c.gridy = 2;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    epatternTF = new JTextField();

    epatternTF.setName(name);
    epatternTF.setToolTipText(toolTip);
    epatternTF.setText(getOrigValue(name));
    textFieldContainer.add(epatternTF);
    parent.add(epatternTF, c);

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
    for (JTextComponent tf : textFieldContainer) {
      String text = tf.getText();
      try {
        Pattern.compile(text);
      } catch (Exception e) {
        error.append(tf.getName() + " must be valid regExp.\n");
      }
    }

    return error.toString();
  }
}
