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
public class DefaultTimeHandlerView extends GenericTransformationView {

 
  
  private JTextField oldformatTF = null;
  private JTextField oldlocaleTF = null;
  private JTextField oldtimezoneTF = null;
  private JTextField newformatTF = null;
  private JTextField newlocaleTF = null;
  private JTextField newtimezoneTF = null;
  
  
  
  public DefaultTimeHandlerView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();
    String name;
    String toolTip;
    JLabel label;
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;


    name = "oldformat";
    toolTip = "The timestamp format of source field. For example yyyyMMddHHmmss.";
    c.gridx = 0;
    c.gridy = 0;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldformatTF = new JTextField(25);
    oldformatTF.setName(name);
    oldformatTF.setToolTipText(toolTip);
    oldformatTF.setText(getOrigValue(name));
    textFieldContainer.add(oldformatTF);
    parent.add(oldformatTF, c);

    name = "oldlocale";
    toolTip = "The locale of source field.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldlocaleTF = new JTextField();
    oldlocaleTF.setName(name);
    oldlocaleTF.setToolTipText(toolTip);
    oldlocaleTF.setText(getOrigValue(name));
    textFieldContainer.add(oldlocaleTF);
    parent.add(oldlocaleTF, c);

    name = "oldtimezone";
    toolTip = "The timezone of source field.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldtimezoneTF = new JTextField();
    oldtimezoneTF.setName(name);
    oldtimezoneTF.setToolTipText(toolTip);
    oldtimezoneTF.setText(getOrigValue(name));
    textFieldContainer.add(oldtimezoneTF);
    parent.add(oldtimezoneTF, c);

    name = "newformat";
    toolTip = "The timestamp format of target field. For example yyyyMMddHHmmss.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    newformatTF = new JTextField();
    newformatTF.setName(name);
    newformatTF.setToolTipText(toolTip);
    newformatTF.setText(getOrigValue(name));
    textFieldContainer.add(newformatTF);
    parent.add(newformatTF, c);

    name = "newlocale";
    toolTip = "The locale of target field.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    newlocaleTF = new JTextField();
    newlocaleTF.setName(name);
    newlocaleTF.setToolTipText(toolTip);
    newlocaleTF.setText(getOrigValue(name));
    textFieldContainer.add(newlocaleTF);
    parent.add(newlocaleTF, c);

    name = "newtimezone";
    toolTip = "The timezone of target field.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    newtimezoneTF = new JTextField();
    newtimezoneTF.setName(name);
    newtimezoneTF.setToolTipText(toolTip);
    newtimezoneTF.setText(getOrigValue(name));
    textFieldContainer.add(newtimezoneTF);
    parent.add(newtimezoneTF, c);

   

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
