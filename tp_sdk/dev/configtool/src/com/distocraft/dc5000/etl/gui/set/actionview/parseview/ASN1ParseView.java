package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ASN1ParseView implements ParseView {

  private static final String[] SOURCE_TYPES = { "MGW","SGSN","MSC" };
  
  private static final String[] MEAS_TYPES = { "PrintableString","GraphicString","UTF8String" };

  private final JPanel parent;
  
  private final JComboBox sourceType;

  private final JComboBox measTypeDefinition;


  public ASN1ParseView(Properties p, JPanel par) {

    this.parent = par;
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    //

    measTypeDefinition = new JComboBox(MEAS_TYPES);
    int im = 0;
    try {
      im = getIndex(MEAS_TYPES,p.getProperty("measTypeDefinition", "data"),0);
    } catch (Exception e) {
    }
    measTypeDefinition.setSelectedIndex(im);
    measTypeDefinition.setToolTipText("Defines the measurementTypes string type in the ASN1 file.");
    c.gridy = 1;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel jl1 = new JLabel("Measurement Definition");
    jl1.setToolTipText("Defines the measurementTypes string type in the ASN1 file.");
    parent.add(jl1, c);
    
    c.gridx = 1;
    parent.add(measTypeDefinition, c);


    //

    sourceType = new JComboBox(SOURCE_TYPES);
    int is = 0;
    try {
      is = getIndex(SOURCE_TYPES,p.getProperty("sourceType", "data"),0);
    } catch (Exception e) {
    }
    sourceType.setSelectedIndex(is);
    sourceType.setToolTipText("Defines the type of the sourcefile.");
    c.gridy = 2;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel jl2 = new JLabel("Source Type");
    jl2.setToolTipText("Defines the type of the sourcefile.");
    parent.add(jl2, c);

    c.gridx = 1;
    parent.add(sourceType, c);


    
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

    s.add("measTypeDefinition");
    s.add("sourceType");
   
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
    
    p.setProperty("measTypeDefinition",getString(MEAS_TYPES,measTypeDefinition.getSelectedIndex()));
    p.setProperty("sourceType",getString(SOURCE_TYPES,sourceType.getSelectedIndex()));
      
  }

  
  private int getIndex(final String[] strA, final String str, final int defi){
    
    for ( int i = 0; i < strA.length ; i++){
      final String tmp = strA[i];
      if (tmp.equalsIgnoreCase(str)) {
        return i;
      }
    }
    
    return defi;
  }
  
  
  private String getString(final String[] strA, final int ind){
    
  try {
    
    return strA[ind];

  } catch (Exception e){
    
  }
   
    return "";
  }
}
