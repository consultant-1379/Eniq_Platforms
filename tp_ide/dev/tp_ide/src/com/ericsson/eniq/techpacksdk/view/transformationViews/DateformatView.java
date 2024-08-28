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
public class DateformatView extends GenericTransformationView {

  private JTextField oldformatTF = null;
  private JTextField newformatTF = null;
  private JTextField oldlocaleTF = null;
  private JTextField oldlocalefieldTF = null;
  private JTextField newlocaleTF = null;
  private JTextField newlocalefieldTF = null;
  private JTextField oldtimezoneTF = null;
  private JTextField oldtimezonefieldTF = null;
  private JTextField newtimezoneTF = null;
  private JTextField newtimezonefieldTF = null;

  
  
  public DateformatView(JPanel parent, Transformation transformation) {
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
    toolTip = "Old time format. For example yyyyMMddHHmmss";
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

    name = "newformat";
    toolTip = "New time format. For example yyyy-MM-dd ";
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

    name = "oldlocale";
    toolTip = "Java locale of the old date. For example sv";
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

    name = "oldlocalefield";
    toolTip = "Field where the java locale of the old date is read.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldlocalefieldTF = new JTextField();
    oldlocalefieldTF.setName(name);
    oldlocalefieldTF.setToolTipText(toolTip);
    oldlocalefieldTF.setText(getOrigValue(name));
    textFieldContainer.add(oldlocalefieldTF);
    parent.add(oldlocalefieldTF, c);

    name = "newlocale";
    toolTip = "Java locale of the new date. For example sv ";
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

    name = "newlocalefield";
    toolTip = "Field where the java locale of the new date is read. ";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    newlocalefieldTF = new JTextField();
    newlocalefieldTF.setName(name);
    newlocalefieldTF.setToolTipText(toolTip);
    newlocalefieldTF.setText(getOrigValue(name));
    textFieldContainer.add(newlocalefieldTF);
    parent.add(newlocalefieldTF, c);

    name = "oldtimezone";
    toolTip = "Offset of the old date. For example +0100 ";
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



    name = "oldtimezonefield";
    toolTip = "Field where the offset of the old date is read.";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    oldtimezonefieldTF = new JTextField();
    oldtimezonefieldTF.setName(name);
    oldtimezonefieldTF.setToolTipText(toolTip);
    oldtimezonefieldTF.setText(getOrigValue(name));
    textFieldContainer.add(oldtimezonefieldTF);
    parent.add(oldtimezonefieldTF, c);


    name = "newtimezone";
    toolTip = "Offset of the new date. For example +0100 ";
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

    name = "newtimezonefield";
    toolTip = "Field where the offset of the new date is read. ";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    newtimezonefieldTF = new JTextField();
    newtimezonefieldTF.setName(name);
    newtimezonefieldTF.setToolTipText(toolTip);
    newtimezonefieldTF.setText(getOrigValue(name));
    textFieldContainer.add(newtimezonefieldTF);
    parent.add(newtimezonefieldTF, c);

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

    if (oldformatTF.getText().equals("") || newformatTF.getText().equals(""))
      error.append("Both oldformat and newformat are mandatory fields.\n");
    
    return error.toString();
  }
}
