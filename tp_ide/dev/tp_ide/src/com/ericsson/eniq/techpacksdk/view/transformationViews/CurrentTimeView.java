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
public class CurrentTimeView extends GenericTransformationView{

  private JTextField formatTF = null;

  private JTextField localeTF = null;

  private JTextField timezoneTF = null;

  

  public CurrentTimeView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();

    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

    // format field
    String name = "format";
    String toolTip = "Format of timestamp (SimpleDateFormat)";
    c.gridx = 0;
    c.gridy = 0;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);    
    c.gridx = 1;
    c.gridy = 0;
    formatTF = new JTextField();
    formatTF.setName(name);
    formatTF.setToolTipText(toolTip);
    formatTF.setText(getOrigValue(name));
    textFieldContainer.add(formatTF);
    parent.add(formatTF, c);
    
    // locale field
    name = "locale";
    toolTip = "Locale used (uses current locale of host if not defined)";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    localeTF = new JTextField(25);
    localeTF.setName(name);
    localeTF.setToolTipText(toolTip);
    localeTF.setText(getOrigValue(name));
    textFieldContainer.add(localeTF);
    parent.add(localeTF, c);

    // timezone field
    name = "timezone";
    toolTip = "Timezone used (uses current timezone of host if not defined)";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    timezoneTF = new JTextField(25);
    timezoneTF.setName(name);
    timezoneTF.setToolTipText(toolTip);
    timezoneTF.setText(getOrigValue(name));
    textFieldContainer.add(timezoneTF);
    parent.add(timezoneTF, c);
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * 
   * @return
   */
  public String validate() {
    if (formatTF.getText().length() < 1)
      return "Format must be filled.\n";      

    return "";
  }

}
