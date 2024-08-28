package com.distocraft.dc5000.etl.gui.set.actionview;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * 
 */
public class ReloadDBLookupsActionView implements ActionView {

  private JTextField tableName;

  private Meta_transfer_actions action;

  private Properties orig;

  public ReloadDBLookupsActionView(JPanel parent, Meta_transfer_actions action) {
    this.action = action;

    orig = new Properties();

    if (action != null) {

      String act_cont = action.getAction_contents();

      if (act_cont != null && act_cont.length() > 0) {

        try {
          ByteArrayInputStream bais = new ByteArrayInputStream(act_cont.getBytes());
          orig.load(bais);
          bais.close();
        } catch (Exception e) {
          System.out.println("Error loading action contents");
          e.printStackTrace();
        }
      }
    }

    parent.removeAll();

    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    JLabel aTableName = new JLabel("TableName");
    aTableName.setToolTipText("Name of the table which lookups are reloaded. If left empty all lookups are reloaded.");
    parent.add(aTableName, c);

    c.weightx = 1;
    c.gridx = 1;
    tableName = new JTextField(20);
    tableName.setToolTipText("Name of the table which lookups are reloaded. If left empty all lookups are reloaded.");
    tableName.setText(orig.getProperty("tableName", ""));
    parent.add(tableName, c);

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return "";
  }

  public String validate() {
    String error = "";
    return error;
  }

  public String getContent() throws Exception {
    Properties p = new Properties();
    p.setProperty("tableName", tableName.getText());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    p.store(baos, "");

    return baos.toString();
  }

  public String getWhere() throws Exception {
    return "";
  }

  public boolean isChanged() {
    return true;
  }

}
