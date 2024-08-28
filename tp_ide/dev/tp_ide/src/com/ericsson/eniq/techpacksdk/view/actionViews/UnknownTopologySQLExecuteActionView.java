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

public class UnknownTopologySQLExecuteActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("UnknownTopologySQLExecuteActionView");

  private transient final JTextArea template;

  private transient final JTextField sourceType;

  private transient final JTextField targetTable;

  public UnknownTopologySQLExecuteActionView(final JPanel parent, final Meta_transfer_actions action) {

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

    final JLabel l_stype = new JLabel("Source Type");
    l_stype.setToolTipText("StorageID of source type.");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_stype, gridbag);

    sourceType = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(sourceType, gridbag);

    final JLabel l_ttype = new JLabel("Target Table");
    l_ttype.setToolTipText("Name of target table. (NOT catenated with : and level name to get storageID for type).");

    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;
    parent.add(l_ttype, gridbag);

    targetTable = new JTextField(20);

    gridbag.weightx = 1;
    gridbag.gridx = 1;
    parent.add(targetTable, gridbag);

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

      sourceType.setText(prop.getProperty("sourceType", ""));
      targetTable.setText(prop.getProperty("targetTable", ""));
    }

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  @Override
  public String getType() {
    return Constants.UNKNOWN_TOPOLOGY;
  }

  @Override
  public String validate() {
    final StringBuffer ret = new StringBuffer(90);

    if (template.getText().trim().length() <= 0) {
      ret.append("Parameter SQL Template must be defined\n");
    }

    if (sourceType.getText().trim().length() <= 0) {
      ret.append("Parameter Source Type must be defined\n");
    }

    return ret.toString();
  }

  @Override
  public String getContent() {
    return template.getText().trim();
  }

  @Override
  public String getWhere() throws IOException {

    final Properties props = new Properties();
    props.setProperty("sourceType", sourceType.getText().trim());
    props.setProperty("targetTable", targetTable.getText().trim());

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }

}
