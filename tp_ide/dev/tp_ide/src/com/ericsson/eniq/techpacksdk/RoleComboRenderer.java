package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jdesktop.application.ResourceMap;

@SuppressWarnings("serial")
class RoleComboRenderer extends DefaultListCellRenderer {

  private final ResourceMap resourceMap;
  
  public RoleComboRenderer(final ResourceMap resourceMap) {
    this.resourceMap = resourceMap;
  }
  
  public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
      final boolean hasFocus) {
    final JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
    if(comp.getText().equals(User.ADMIN)) {
      comp.setIcon(resourceMap.getImageIcon("Admin.icon"));
    } else if(comp.getText().equals(User.USER)) {
      comp.setIcon(resourceMap.getImageIcon("User.icon"));
    } else if(comp.getText().equals(User.RND)) {
      comp.setIcon(resourceMap.getImageIcon("RnDUser.icon"));
    } else {
      comp.setIcon(resourceMap.getImageIcon("QuestionMark.icon"));
    }
    return comp;
  }

}
