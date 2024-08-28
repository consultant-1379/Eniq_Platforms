package com.ericsson.eniq.techpacksdk;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class InterfaceTreeRenderer extends DefaultTreeCellRenderer {

  private DataModelController dataModelController;

  public InterfaceTreeRenderer(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    setForeground(Color.black);
    Object aUserObject = null;

    if (value != null) {
      aUserObject = ((DefaultMutableTreeNode) value).getUserObject();
      if (aUserObject instanceof DataTreeNode) {

        DataTreeNode t = (DataTreeNode) aUserObject;
        Datainterface di = (Datainterface) t.getRockDBObject();
        String toolTip = " ";

        toolTip += di.getRstate() + " (";

        if (t.locked != null && t.locked.length() > 0) {

          if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
            setForeground(Color.GREEN);
            // this.setIcon(new
            // ImageIcon("jar/com/ericsson/eniq/techpacksdk/resources/lock.png",
            // ""));
            toolTip += " LOCKED BY " + t.locked;

          } else {
            setForeground(Color.RED);
            // this.setIcon(new
            // ImageIcon("jar/com/ericsson/eniq/techpacksdk/resources/lock.png",
            // ""));
            toolTip += " LOCKED BY " + t.locked;
          }

        } else {
          toolTip += " UNLOCKED ";
          // this.setIcon(new
          // ImageIcon("jar/com/ericsson/eniq/techpacksdk/resources/lockOpen.png",
          // ""));
        }

        if (t.active == true) {

          toolTip += " ACTIVE";

        } else {

          toolTip += " INACTIVE";

        }

        // TODO: The handling of the interface migration needs to be updated if
        // new ENIQ versions are taken into use for interfaces. Use
        // Constants.CURRENT_INTERFACE_ENIQ_LEVEL.

        if (di.getEniq_level() == null || di.getEniq_level().equalsIgnoreCase("1.0")) {
          if (t.locked != null && t.locked.length() > 0) {
            if (t.locked.equalsIgnoreCase(dataModelController.getUserName())) {
              setForeground(Color.BLUE);
              toolTip += " Version is too old, please migrate";
            }
          } else {
            setForeground(Color.GRAY);
            toolTip += "  Version is too old, please lock and migrate";
          }
        } else if (di.getEniq_level() == null || di.getEniq_level().contains("MIGRATED")) {
          toolTip += " Interface is migrated, please create new copy from it.";
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
