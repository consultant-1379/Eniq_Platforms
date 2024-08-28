package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.component.ExceptionHandler;

/**
 * Copyright Distocraft 2005<br>
 * <br>
 * $id$
 * 
 * @author lemminkainen
 */
public class JoinActionView implements ActionView {

  private JTextField typeid;

  private JTextField in;

  private JTextField out;

  private JTextField measPattern;

  private JTextField source;

  private JComboBox mode;

  private GenericPropertiesView gpv;

  private Meta_transfer_actions action;

  public JoinActionView(JPanel parent, Meta_transfer_actions action, JDialog pdialog) {
    this.action = action;

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    Properties actionContent = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {
        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          actionContent.load(bais);
        } catch (Exception e) {
          ExceptionHandler.instance().handle(e);
          e.printStackTrace();
        }
      }
    }

    typeid = new JTextField(actionContent.getProperty("joiner.typeID", ""), 20);
    typeid.setToolTipText("Used to retrieve the correct dataformat (with: Interface)");
    JLabel l_typeid = new JLabel("DataformatID");
    l_typeid.setToolTipText("Used to retrieve the correct dataformat (with: Interface)");
    parent.add(l_typeid, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(typeid, c);

    in = new JTextField(actionContent.getProperty("joiner.in", ""), 20);
    in.setToolTipText("Input directory where joinable files are read.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    JLabel l_in = new JLabel("IN directory");
    l_in.setToolTipText("Input directory where joinable files are read.");
    parent.add(l_in, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(in, c);

    out = new JTextField(actionContent.getProperty("joiner.out", ""), 20);
    out.setToolTipText("Output directory where joined files are created.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    JLabel l_out = new JLabel("OUT directory");
    l_out.setToolTipText("Output directory where joined files are created.");
    parent.add(l_out, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(out, c);

    measPattern = new JTextField(actionContent.getProperty("joiner.measPattern", ""), 20);
    measPattern.setToolTipText("RegExp mask to retrieve measurement type from filename.");
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    JLabel l_measPattern = new JLabel("Measurement Pattern");
    l_measPattern.setToolTipText("RegExp mask to retrieve measurement type from filename.");
    parent.add(l_measPattern, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(measPattern, c);

    source = new JTextField(actionContent.getProperty("joiner.source", ""), 20);
    source.setToolTipText("Used to retrieve the correct dataformat (with: DataformatID)");

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 5;
    c.weightx = 0;
    JLabel l_source = new JLabel("Interface");
    l_source.setToolTipText("Used to retrieve the correct dataformat (with: DataformatID)");
    parent.add(l_source, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(source, c);

    String[] items = { "", "incomplete", "full" };

    mode = new JComboBox(items);
    mode.setEditable(true);
    mode.setToolTipText("Defines the joining mode.");
    String mod = actionContent.getProperty("joiner.mode", "");

    boolean found = false;
    for (int i = 0; i < items.length; i++) {
      if (items[i].equalsIgnoreCase(mod)) {
        mode.setSelectedIndex(i);
        found = true;
        break;
      }
    }

    if (!found) {
      mode.addItem(mod);
      mode.setSelectedItem(mod);
    }

    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 6;
    c.weightx = 0;
    JLabel l_mode = new JLabel("Mode");
    l_mode.setToolTipText("Defines the joining mode.");
    parent.add(l_mode, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.gridx = 1;
    parent.add(mode, c);

    // -----

    HashSet<String> hs = new HashSet<String>();
    hs.add("joiner.typeID");
    hs.add("joiner.in");
    hs.add("joiner.out");
    hs.add("joiner.measPattern");
    hs.add("joiner.source");
    hs.add("joiner.mode");

    gpv = new GenericPropertiesView(actionContent, hs, pdialog);

    c.gridx = 0;
    c.gridy = 7;
    c.gridwidth = 2;
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;

    parent.add(gpv, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "Join";
  }

  public String getContent() throws Exception {
    Properties p = gpv.getProperties();

    if (typeid.getText().length() > 0)
      p.setProperty("joiner.typeID", typeid.getText());

    if (in.getText().length() > 0)
      p.setProperty("joiner.in", in.getText());

    if (out.getText().length() > 0)
      p.setProperty("joiner.out", out.getText());

    if (out.getText().length() > 0)
      p.setProperty("joiner.measPattern", measPattern.getText());

    if (source.getText().length() > 0)
      p.setProperty("joiner.source", source.getText());

    String mod = (String) mode.getSelectedItem();
    if (mod.length() > 0)
      p.setProperty("joiner.mode", mod);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    if (action == null)
      return true;
    else
      return typeid.getText().trim().equals(action.getAction_contents());
  }

  public String validate() {

    try {

      Pattern.compile(measPattern.getText());
    } catch (PatternSyntaxException pse) {
      return "Measurement Pattern: Not a valid regExp pattern\n";
    }
    return "";
  }

}
