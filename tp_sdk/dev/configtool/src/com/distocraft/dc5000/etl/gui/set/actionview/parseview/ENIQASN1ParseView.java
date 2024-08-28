package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JPanel;


public class ENIQASN1ParseView implements ParseView {

  private final JPanel parent;

  public ENIQASN1ParseView(final Properties p, final JPanel par) {

    this.parent = par;
    
    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    c.gridx = 2;
    c.gridy = 9;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1,1)),c);

  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    //s.add("measTypeDefinition");
    //s.add("sourceType");
   
    return s;
  }
  
  public String validate() {
    return "";
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    
    //p.setProperty("measTypeDefinition",getString(MEAS_TYPES,measTypeDefinition.getSelectedIndex()));
    //p.setProperty("sourceType",getString(SOURCE_TYPES,sourceType.getSelectedIndex()));
      
  }

}
