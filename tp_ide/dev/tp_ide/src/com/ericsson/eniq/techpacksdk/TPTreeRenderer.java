package com.ericsson.eniq.techpacksdk;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class TPTreeRenderer extends DefaultTreeCellRenderer {

  private DataModelController dataModelController;

  public TPTreeRenderer(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    Object aUserObject = null;

    if (value != null) {
      aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
      if (aUserObject instanceof DataTreeNode) {

        DataTreeNode t = (DataTreeNode) aUserObject;
        Versioning v = (Versioning) t.getRockDBObject();
        String toolTip = " ";

        toolTip += v.getTechpack_version() + " (";

        if (t.locked != null && t.locked.length() > 0) {

          if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
            setForeground(Color.GREEN);
            toolTip += "LOCKED BY " + t.locked;

          } else {
            setForeground(Color.RED);
            toolTip += "LOCKED BY " + t.locked;

          }

        } else {
          toolTip += "UNLOCKED ";
        }

        // Migrate is needed from ENIQ_LEVEL 1.0.
        // New copy needs to be taken from migrated version or if the version is
        // not the latest.
        if (v.getEniq_level() == null || v.getEniq_level().equalsIgnoreCase("1.0")) {

          if (t.locked != null && t.locked.length() > 0) {
            if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
              setForeground(Color.BLUE);
              toolTip += "  Version is too old, please migrate";
            }
          } else {
            setForeground(Color.GRAY);
            toolTip += "  Version is too old, please lock and migrate";
          }
        } else if (v.getEniq_level() == null || v.getEniq_level().equalsIgnoreCase("MIGRATED")) {
          toolTip += " Techpack is migrated, please create new copy from it.";
          if (t.locked != null && t.locked.length() > 0) {
            if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
              setForeground(Color.LIGHT_GRAY);
            }
          } else {
            setForeground(Color.GRAY);
          }
        } else if (v.getEniq_level() == null
            || !v.getEniq_level().equalsIgnoreCase(Constants.CURRENT_TECHPACK_ENIQ_LEVEL)) {
          toolTip += " Techpack is too old, please create new copy from it.";
          if (t.locked != null && t.locked.length() > 0) {
            if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
              setForeground(Color.LIGHT_GRAY);
            }
          } else {
            setForeground(Color.GRAY);
          }

        }

        toolTip += ")";
        setToolTipText(toolTip);

      }
    }

    return this;

  }
}
