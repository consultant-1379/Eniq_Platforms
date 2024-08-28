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
public class FieldTokenizerView extends GenericTransformationView {
  
  private JTextField delimTF = null;

  public FieldTokenizerView(JPanel parent, Transformation transformation) {
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
    String name = "delim";
    String toolTip = "Delimeter string to use in tokenizing delimeted string.";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    delimTF = new JTextField(25);
    delimTF .setName(name);
    delimTF .setToolTipText(toolTip);
    delimTF .setText(getOrigValue(name));
    textFieldContainer.add(delimTF );
    parent.add(delimTF , c);

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

    if (delimTF.getText().equals(""))
      error.append("Delim is mandatory field.\n");
    
    return error.toString();
  }
}
