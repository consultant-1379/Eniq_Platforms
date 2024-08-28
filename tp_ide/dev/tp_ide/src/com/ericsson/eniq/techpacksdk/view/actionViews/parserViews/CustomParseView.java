package com.ericsson.eniq.techpacksdk.view.actionViews.parserViews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Allow the user to specify a custom Parser.
 * Has a text field which allows the user to set the "parserType" property.
 * Value should be the name of the parser class, e.g. com.ericsson.eniq.etl.hlr.HLRParser
 * 
 * @author eeoidiv
 *
 */
public class CustomParseView implements ParseView {

  private final JPanel parent;

  private final JTextField parserType_custom;
  private static String PARSER_TYPE = "parserType";
  
  public CustomParseView(Properties p, JPanel par) {

    this.parent = par;

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    parent.setLayout(new GridBagLayout());

    // Custom parserType
    String val = p.getProperty(CustomParseView.PARSER_TYPE, "");
    if(val.equals("CUSTOM")) {
      val = "";
    }
    parserType_custom = new JTextField(val, 30);
    
    parserType_custom.setToolTipText("Enter a custom parser implementation.");
    c.gridx = 0;
    JLabel l_tag_id = new JLabel("Custom Parser");
    l_tag_id.setToolTipText("Enter a custom parser implementation.");
    parent.add(l_tag_id, c);
    c.gridx = 1;
    parent.add(parserType_custom, c);

    // Finishing touch for the ui.
    c.gridx = 2;
    c.gridy = 5;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    parent.add(Box.createRigidArea(new Dimension(1, 1)), c);

  }

  public String validate() {
    return "";
  }

  /**
   * Returns a set of parameter names that ParseView represents.
   * 
   * @return Set of parameter names
   */
  public Set getParameterNames() {
    final Set s = new HashSet();
    s.add(CustomParseView.PARSER_TYPE);
    return s;
  }

  /**
   * Sets ParseView specific parameters to a properties object.
   * 
   * @param p
   *          Properties object
   */
  public void fillParameters(final Properties p) {
    p.setProperty(CustomParseView.PARSER_TYPE, parserType_custom.getText());
  }

}
