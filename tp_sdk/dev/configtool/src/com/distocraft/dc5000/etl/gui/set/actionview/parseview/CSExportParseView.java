package com.distocraft.dc5000.etl.gui.set.actionview.parseview;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class CSExportParseView implements ParseView {

  private static final String[] VENDOR_ID_FROM = { "data", "file" };

  private static final String[] TRUE_FALSE = { "true", "false" };

  private final JPanel parent;

  private final JTextField vendorIDMask;

  private final JTextField headerVendorID;

  private final JComboBox vendorIDFrom;

  private final JComboBox extractHeaderRows;

  private final JLabel jl2;

  public CSExportParseView(Properties p, JPanel par) {

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
      iv = getIndex(VENDOR_ID_FROM, p.getProperty("readVendorIDFrom", "data"), 0);
    } catch (Exception e) {
    }
    vendorIDFrom.setSelectedIndex(iv);

    c.fill = GridBagConstraints.NONE;
    JLabel jl1 = new JLabel("VendorID from");
    jl1.setToolTipText("Defines where the vendorID is read from.");
    parent.add(jl1, c);

    c.gridx = 1;
    c.gridy = 0;
    parent.add(vendorIDFrom, c);

    //

    vendorIDMask = new JTextField(p.getProperty("vendorIDMask", ""), 35);
    vendorIDMask.setToolTipText("Regular expression pattern for VendorID.");
    c.gridy = 1;
    c.gridx = 0;
    JLabel jl21 = new JLabel("VendorID Mask");
    jl21.setToolTipText("Regular expression pattern for VendorID.");
    parent.add(jl21, c);

    c.gridx = 1;
    parent.add(vendorIDMask, c);

    //

    extractHeaderRows = new JComboBox(TRUE_FALSE);
    extractHeaderRows.setToolTipText("Defines is the header row(s) handled with different VendorID");
    int ivi = 0;
    try {
      ivi = getIndex(TRUE_FALSE, p.getProperty("extractHeaderRows", "true"), 0);
    } catch (Exception e) {
    }
    extractHeaderRows.setSelectedIndex(ivi);
    c.gridy = 2;
    c.gridx = 0;
    c.fill = GridBagConstraints.NONE;
    JLabel jl11 = new JLabel("Header row extract");
    jl11.setToolTipText("Defines is the header row(s) handled with different VendorID");
    parent.add(jl11, c);

    c.gridx = 1;

    parent.add(extractHeaderRows, c);

    extractHeaderRows.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent ae) {
        headerVendorID.setEnabled(extractHeaderRows.getSelectedIndex() == 0);
        jl2.setEnabled(extractHeaderRows.getSelectedIndex() == 0);
      }
    });

    //
    headerVendorID = new JTextField(p.getProperty("headerVendorID", ""), 35);
    headerVendorID.setToolTipText("Defines the header VendorID.");
    c.gridy = 3;
    c.gridx = 0;
    jl2 = new JLabel("Header VendorID");
    jl2.setToolTipText("Defines the header VendorID.");
    parent.add(jl2, c);

    c.gridx = 1;
    parent.add(headerVendorID, c);

    //

    /*
     * dtdFile = new JTextField(p.getProperty("dtdfile", ""), 35);
     * dtdFile.setToolTipText("Defines the DTD-file that is used."); c.gridy =
     * 4; c.gridx = 0; JLabel jl3 = new JLabel("DTD File");
     * jl3.setToolTipText("Defines the DTD-file that is used."); parent.add(jl3,
     * c);
     * 
     * c.gridx = 1; parent.add(dtdFile, c);
     */

    c.gridx = 2;
    c.gridy = 9;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1, 1)), c);

  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();

    s.add("readVendorIDFrom");
    s.add("dtdfile");
    s.add("vendorIDMask");
    s.add("extractHeaderRows");
    s.add("headerVendorID");

    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty("readVendorIDFrom", getString(VENDOR_ID_FROM, vendorIDFrom.getSelectedIndex()));
    // p.setProperty("dtdfile",dtdFile.getText());
    p.setProperty("vendorIDMask", vendorIDMask.getText());
    p.setProperty("extractHeaderRows", getString(TRUE_FALSE, extractHeaderRows.getSelectedIndex()));
    p.setProperty("headerVendorID", headerVendorID.getText());

  }

  private int getIndex(final String[] strA, final String str, final int defi) {

    for (int i = 0; i < strA.length; i++) {
      final String tmp = strA[i];
      if (tmp.equalsIgnoreCase(str)) {
        return i;
      }
    }

    return defi;
  }

  private String getString(final String[] strA, final int ind) {

    try {

      return strA[ind];

    } catch (Exception e) {

    }

    return "";
  }

  public String validate() {

    try {

      Pattern.compile(vendorIDMask.getText());
    } catch (PatternSyntaxException pse) {
      return "VendorID Mask: Not a valid regExp pattern\n";
    }
    return "";
  }
}
