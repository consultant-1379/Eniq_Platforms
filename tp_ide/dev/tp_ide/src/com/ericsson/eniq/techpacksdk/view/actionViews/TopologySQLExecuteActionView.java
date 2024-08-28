package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

public class TopologySQLExecuteActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("TopologySQLExecuteActionView");

  private transient final JTextArea template;

  private transient final JTextField tableName;

  public TopologySQLExecuteActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    final JLabel l_timelevels = new JLabel("Template");
    l_timelevels.setToolTipText("SQL Template for topology.");

    final GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.fill = GridBagConstraints.NONE;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;
    parent.add(l_timelevels, gridbag);

    template = new JTextArea(20, 20);
    template.setName("SQL Template Clause");
    template.setLineWrap(true);
    template.setWrapStyleWord(true);
    final JScrollPane scrollPane = new JScrollPane(template, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    gridbag.fill = GridBagConstraints.BOTH;
    gridbag.gridy++;

    parent.add(scrollPane, gridbag);

    final JLabel l_stype = new JLabel("Table Name");
    l_stype.setToolTipText("Name of topology table.");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_stype, gridbag);

    tableName = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(tableName, gridbag);

    if (action != null) {

      final String temp = action.getAction_contents();

      if (temp != null) {
        template.setText(temp);
      }

      final Properties prop = new Properties();

      final String str = action.getWhere_clause();

      try {

        if (str != null && str.length() > 0) {

          final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }

      } catch (Exception e) {
        LOG.log(Level.INFO, "Reading parameters failed", e);
      }

      tableName.setText(prop.getProperty("tableName", ""));
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return Constants.TOPOLOGY_SQL_EXECUTE;
  }

  public String validate() {
    final StringBuffer ret = new StringBuffer(90);

    if (template.getText().trim().length() <= 0) {
      ret.append("Parameter SQL Template must be defined\n");
    }

    if (tableName.getText().trim().length() <= 0) {
      ret.append("Parameter Topology Table Name must be defined\n");
    }

    return ret.toString();
  }

  public String getContent() {
    return template.getText().trim();
  }

  public String getWhere() throws IOException {

    final Properties props = new Properties();
    props.setProperty("tableName", tableName.getText().trim());

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  public boolean isChanged() {
    return true;
  }

}
