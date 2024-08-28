package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * @author melantie Copyright Distocraft 2005
 * 
 * $id$
 */

public class SetTypeTriggerActionView implements ActionView {

  private String type;

  private JComboBox setType;

  public SetTypeTriggerActionView(JPanel parent, Meta_transfer_actions action) {

    parent.removeAll();

    Properties p = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          p.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading action contents");
          e.printStackTrace();
        }
      }
    }

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    setType = new JComboBox(ConfigTool.SET_TYPES);
    setType.setEditable(true);
    String tig_ix = "";
    try {
      tig_ix = p.getProperty("setType", "");
    } catch (Exception e) {
    }
    setType.setSelectedItem(tig_ix);

    c.gridy = 1;
    c.gridx = 0;
    JLabel l_setType = new JLabel("Set Type");
    l_setType.setToolTipText("Triggered set type.");
    parent.add(l_setType, c);

    c.gridx = 1;
    setType.setToolTipText("Triggered set type.");
    parent.add(setType, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return type;
  }

  public String validate() {
    if (setType.getSelectedIndex() < 0)
      return "Select Set type\n";
    else
      return "";
  }

  public String getContent() throws Exception {

    Properties p = new Properties();
    p.put("setType", (String) setType.getSelectedItem());
    return propertyToString(p);

  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

  protected Properties stringToProperty(String str) {

    Properties prop = new Properties();

    try {

      if (str != null && str.length() > 0) {

        ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
        prop.load(bais);
        bais.close();
      }

    } catch (Exception e) {

    }

    return prop;

  }

  protected String propertyToString(Properties prop) {

    try {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      prop.store(baos, "");

      return baos.toString();

    } catch (Exception e) {

    }

    return "";

  }

}
