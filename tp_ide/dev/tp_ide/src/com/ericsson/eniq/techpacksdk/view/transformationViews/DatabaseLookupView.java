package com.ericsson.eniq.techpacksdk.view.transformationViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.repository.dwhrep.Transformation;

/**
 * 
 * @author etogust
 * 
 */
public class DatabaseLookupView extends GenericTransformationView {
  
  private JTextArea sqlTA = null;
  private JTextField basetableTF = null;

  public DatabaseLookupView(JPanel parent, Transformation transformation) {
    super();
    setTransformation(transformation);
    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;

//  sql
    String name = "sql";
    String toolTip = "<html>An sql clause that returns two values. First column is name and second column is value.<br>"
      + "For example:<br>"
      + "select DURATIONMIN, TIMELEVEL from DIM_TIMELEVEL where TABLELEVEL = 'RAW'<br> DURATIONMIN is compared to source and if it equals, TIMELEVEL is set to target.</html>";
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    sqlTA = new JTextArea();
    sqlTA.setLineWrap(true);
    sqlTA.setMinimumSize(new Dimension(275,275));
    sqlTA.setPreferredSize(new Dimension(275,275));
    sqlTA.setName(name);
    sqlTA.setToolTipText(toolTip);
    String text = getOrigValue(name);
    sqlTA.setText(getOrigValue(name));
    JScrollPane scrollPane = 
      new JScrollPane(sqlTA,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
    textFieldContainer.add(sqlTA);
    parent.add(scrollPane, c);

//  sql
    name = "basetable";
    toolTip = "<html>This parameter defines basetables names that this databaselookup uses in DWH database.<br> Basetable can be found in table dwhrep.MeasurementTable column basetablename for measurement types and in table dwhrep.ReferenceTable column typename for reference types. Multiple tables should be separated with comma.<br>"
      + "For example:<br>"
      + "DIM_TIMELEVEL defines that this database lookup uses table with basetablename DIM_TIMELEVEL.</html>";
    c.gridx = 0;
    c.gridy++;
    label = new JLabel(name);
    label.setToolTipText(toolTip);
    parent.add(label, c);
    c.gridx = 1;
    basetableTF = new JTextField(25);
    basetableTF.setName(name);
    basetableTF.setToolTipText(toolTip);
    basetableTF.setText(getOrigValue(name));
    textFieldContainer.add(basetableTF);
    parent.add(basetableTF, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  /**
   * 
   * @return
   */
  public String validate() {
    if (sqlTA.getText().equals("") || basetableTF.getText().equals(""))
      return "Both sql and basetable must be filled";
    return "";
  }
}
