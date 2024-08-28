package com.ericsson.eniq.techpacksdk;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

@SuppressWarnings("serial")
public class ServerComboRenderer extends DefaultListCellRenderer {

  private final Icon ic;

  public ServerComboRenderer(Icon ic) {
    this.ic = ic;
  }

  public Component getListCellRendererComponent(final JList list, final Object value, final int index,
      final boolean isSelected, final boolean hasFocus) {
    final JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
    if (comp.getText().length() > 0) {
      comp.setIcon(ic);
    } else {
      comp.setIcon(null);
    }
    return comp;
  }

}
