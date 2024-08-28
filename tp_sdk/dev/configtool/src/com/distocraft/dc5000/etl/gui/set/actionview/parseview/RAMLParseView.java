package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RAMLParseView implements ParseView {

  private static final String[] VENDOR_ID_FROM = { "data","file" };

  private final JPanel parent;
  
  private final JTextField vendorIDMask;

  private final JComboBox vendorIDFrom;

  public RAMLParseView(final Properties p, final JPanel par) {

    this.parent = par;
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    //

    vendorIDFrom = new JComboBox(VENDOR_ID_FROM);
    vendorIDFrom.setToolTipText("Defines where the vendorID is read from.");
    int iv = 0;
    try {
      iv = getIndex(VENDOR_ID_FROM,p.getProperty("RAMLParser.readVendorIDFrom", "data"),0);
    } catch (Exception e) {
    }
    vendorIDFrom.setSelectedIndex(iv);

    c.fill = GridBagConstraints.NONE;
    JLabel jl1 = new JLabel("VendorID from");
    jl1.setToolTipText("Defines where the vendorID is read from.");
    parent.add(jl1, c);

    c.gridx = 1;
    parent.add(vendorIDFrom, c);

    //

    vendorIDMask = new JTextField(p.getProperty("RAMLParser.vendorIDMask", ""), 35);
    vendorIDMask.setToolTipText("Regular expression pattern for VendorID.");
    c.gridy = 1;
    c.gridx = 0;
    JLabel jl2 = new JLabel("VendorID Mask");
    jl2.setToolTipText("Regular expression pattern for VendorID.");
    parent.add(jl2, c);

    c.gridx = 1;
    parent.add(vendorIDMask, c);

    //
    
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

    s.add("RAMLParser.readVendorIDFrom");
    //s.add("RAMLParser.dtdfile");
    s.add("RAMLParser.vendorIDMask");
   
    return s;
  }
  

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("RAMLParser.readVendorIDFrom",getString(VENDOR_ID_FROM,vendorIDFrom.getSelectedIndex()));
    //p.setProperty("RAMLParser.dtdfile",dtdFile.getText());
    p.setProperty("RAMLParser.vendorIDMask",vendorIDMask.getText());
  
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
  
  public String validate() {
   
    try {
      
      Pattern.compile(vendorIDMask.getText());
    } catch(PatternSyntaxException  pse) {
      return "VendorID Mask: Not a valid regExp pattern\n";
    }
    return "";
  }

  
}
