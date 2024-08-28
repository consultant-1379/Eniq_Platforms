/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;
import com.ericsson.eniq.techpacksdk.common.Constants;

/**
 * Action view for BackupTables in IDE
 * 
 * @author epaujor
 * @since 2012
 * 
 */
public class BackupTablesActionView implements ActionView {

  private transient static final Logger LOG = Logger.getLogger("BackupTablesActionView");

  private transient final JTextField sourceType;

  private transient final JCheckBox isVolBasedPartition;

    public BackupTablesActionView(final JPanel parent, final Meta_transfer_actions action) {

    parent.removeAll();

    GridBagConstraints gridbag = new GridBagConstraints();
    gridbag.anchor = GridBagConstraints.NORTHWEST;
    gridbag.insets = new Insets(2, 2, 2, 2);
    gridbag.weightx = 0;
    gridbag.weighty = 0;

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

    isVolBasedPartition = new JCheckBox("Volume-based Partitioning");
    isVolBasedPartition.setToolTipText("Decides if partitioning type is volume or time-based partitioning");
    gridbag.weightx = 0;
    gridbag.gridx = 0;
    gridbag.gridy++;

    gridbag.weightx = 1;
    gridbag.gridx = 1;

    parent.add(isVolBasedPartition, gridbag);
    
    if (action != null) {

      final Properties prop = new Properties();

      final String str = action.getWhere_clause();

      try {

        if (str != null && str.length() > 0) {

          final ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
          prop.load(bais);
          bais.close();
        }

      } catch (IOException e) {
        LOG.log(Level.INFO, "Reading parameters failed", e);
      }

      sourceType.setText(prop.getProperty("tablename", ""));
      
      Boolean isVolBasedPart = Boolean.parseBoolean((prop.getProperty("isVolBasedPartition", "false")));
      isVolBasedPartition.setSelected(isVolBasedPart);
    } 
    
    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  @Override
  public String getType() {
    return Constants.BACKUP_TABLES;
  }

  @Override
  public String validate() {
    final StringBuffer ret = new StringBuffer(90);

    if (sourceType.getText().trim().length() <= 0) {
      ret.append("Parameter Source Type must be defined\n");
    }

    return ret.toString();
  }

  @Override
  public String getContent() {
    return "";
  }

  @Override
  public String getWhere() throws IOException {

    final Properties props = new Properties();
    final String storageId = sourceType.getText().trim();
    props.setProperty("tablename", storageId);
    // TopologyLoader has _CURRENT_DC table as "lockTable". Replace end of
    // storageId with _CURRENT_DC so that "lockTable" is the same
    // for this action. This will avoid BackupTablesAction and TopologyLoader
    // running at the same time
    if (storageId.endsWith(":PLAIN")) {
      props.setProperty("typeName", storageId.replace(":PLAIN", "_CURRENT_DC"));
    } else if (storageId.endsWith(":RAW")) {
      props.setProperty("typeName", storageId.replace(":RAW", "_CURRENT_DC"));
    } else if (storageId.endsWith(":DAY")) {
      props.setProperty("typeName", storageId.replace(":DAY", "_CURRENT_DC"));
    } else{
      throw new IOException("storageId '"+storageId+"' is not known");
    }

    String isVolBasedPart = String.valueOf(isVolBasedPartition.isSelected());
    props.put("isVolBasedPartition", isVolBasedPart);
    
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    props.store(baos, "");

    return baos.toString();
  }

  @Override
  public boolean isChanged() {
    return true;
  }

}
