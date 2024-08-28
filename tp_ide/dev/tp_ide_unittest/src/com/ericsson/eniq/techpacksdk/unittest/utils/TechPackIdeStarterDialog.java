package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;


import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * A dialog for displaying the options for starting techpack ide. After the
 * dialog is closed, the techpack ide starter application can query the selected
 * options from the dialog with the methods with suffix "selected()".
 * 
 * @author epiituo
 * 
 */
public class TechPackIdeStarterDialog extends JDialog {

  /**
   * Generated serial version UID
   */
  private static final long serialVersionUID = -9039918767001954604L;

  private static final String TABLE_MODIFICATION_LOGGER_OPTION_TEXT = "Start database modification logger";

  private static final String AUTOMATIC_LOGIN_TEXT = "Automatic login";

  private static final String SWING_HIERARCHY_UTILITY_TEXT = "Start swing hierarchy utility";

  private static final String OK_BUTTON_TEXT = "OK";

  private static final String CREATE_TEST_TP_TEXT = "Create an empty test techpack";

  private JCheckBox autoLoginCheckBox;

  private JCheckBox tableModificationLoggerCheckBox;

  private JCheckBox swingHierarchyUtilityCheckBox;

  private JButton okButton;

  private JCheckBox createTestTechpackCheckBox;

  private boolean closedWithOkButton = false;

  public static TechPackIdeStarterDialog showDialog(Frame parent) {
    TechPackIdeStarterDialog dialog = new TechPackIdeStarterDialog(parent);
    dialog.setVisible(true);
    return dialog;
  }

  public TechPackIdeStarterDialog(Frame parent) {
    super(parent, true);
    this.setLayout(new GridLayout(2, 1));
    JPanel checkBoxPanel1 = new JPanel();   
    this.tableModificationLoggerCheckBox = new JCheckBox(TABLE_MODIFICATION_LOGGER_OPTION_TEXT);
    this.swingHierarchyUtilityCheckBox = new JCheckBox(SWING_HIERARCHY_UTILITY_TEXT);
    this.autoLoginCheckBox = new JCheckBox(AUTOMATIC_LOGIN_TEXT);
    this.autoLoginCheckBox.setSelected(true);
    this.createTestTechpackCheckBox = new JCheckBox(CREATE_TEST_TP_TEXT);
    JPanel checkBoxPanel2 = new JPanel();
    checkBoxPanel2.add(this.tableModificationLoggerCheckBox);
    checkBoxPanel2.add(this.swingHierarchyUtilityCheckBox);
    checkBoxPanel2.add(this.autoLoginCheckBox);
    checkBoxPanel2.add(this.createTestTechpackCheckBox);
    this.autoLoginCheckBox.setSelected(true);

    JPanel upperPanel = new JPanel(new GridLayout(3, 1));
    upperPanel.add(checkBoxPanel1);
    upperPanel.add(checkBoxPanel2);
    this.add(upperPanel);

    this.okButton = new JButton(OK_BUTTON_TEXT);
    this.okButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        closedWithOkButton = true;
        setVisible(false);
      }
    });

    JPanel lowerPanel = new JPanel();
    lowerPanel.add(this.okButton);
    this.add(lowerPanel);

    Utils.center(this);
    this.pack();
  }
  public boolean tableModificationLoggerSelected() {
    return this.tableModificationLoggerCheckBox.isSelected();
  }

  public boolean swingHierarchyUtilitySelected() {
    return this.swingHierarchyUtilityCheckBox.isSelected();
  }

  public boolean automaticLoginSelected() {
    return this.autoLoginCheckBox.isSelected();
  }

  public boolean createTesttechpackSelected() {
    return this.createTestTechpackCheckBox.isSelected();
  }

  public boolean closedWithOkButton() {
    return this.closedWithOkButton;
  }
}
