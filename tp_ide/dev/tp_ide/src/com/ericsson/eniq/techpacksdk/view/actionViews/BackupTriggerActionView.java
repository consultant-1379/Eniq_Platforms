package com.ericsson.eniq.techpacksdk.view.actionViews;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;

import com.ericsson.eniq.techpacksdk.common.Constants;


public class BackupTriggerActionView implements ActionView {

  public BackupTriggerActionView(final JPanel parent) {

    parent.removeAll();

    final GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 0;
    c.weighty = 0;

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 1;

    parent.invalidate();
    parent.revalidate();
    parent.repaint();
  }

  public String getType() {
    return Constants.BACKUP_TRIGGER;
  }

  public String validate() {
    return "";
  }

  public String getContent() {
    return "";
  }

  public String getWhere() {
    return "";
  }

  public boolean isChanged() {
    return true;
  }
}
