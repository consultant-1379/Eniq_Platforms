package com.distocraft.dc5000.etl.gui.iface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.distocraft.dc5000.etl.gui.ConfigTool;

public class InterfaceTreeCellRenderer extends JLabel implements TreeCellRenderer {

  private JTree tree;

  protected boolean selected;

  protected boolean hasFocus;

  private boolean drawsFocusBorderAroundIcon;

  transient protected Icon leafIcon;

  transient protected Icon rootIcon;

  protected Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");

  protected Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");

  protected Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");

  protected Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");

  protected Color borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");

  public InterfaceTreeCellRenderer() {
    setHorizontalAlignment(JLabel.LEFT);

    leafIcon = ConfigTool.plug;

    rootIcon = ConfigTool.dataConnection;

    Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
    drawsFocusBorderAroundIcon = (value != null && ((Boolean) value).booleanValue());
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {

    this.tree = tree;
    this.hasFocus = hasFocus;

    DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;

    setText((String) dmtn.getUserObject());

    if (dmtn instanceof InterfaceTreeNode) { // interface
      
      if (tree.isEnabled())
        setIcon(leafIcon);
      else
        setDisabledIcon(leafIcon);

    } else { // root

      if (tree.isEnabled())
        setIcon(rootIcon);
      else
        setDisabledIcon(rootIcon);

    }

    if (sel)
      setForeground(textSelectionColor);
    else
      setForeground(textNonSelectionColor);

    setEnabled(tree.isEnabled());

    setComponentOrientation(tree.getComponentOrientation());

    selected = sel;

    return this;
  }

  public void paint(Graphics g) {
    Color bColor;

    if (selected) {
      bColor = backgroundSelectionColor;
    } else {
      bColor = backgroundNonSelectionColor;
      if (bColor == null)
        bColor = getBackground();
    }
    int imageOffset = -1;
    if (bColor != null) {
      Icon currentI = getIcon();

      imageOffset = getLabelStart();
      g.setColor(bColor);
      if (getComponentOrientation().isLeftToRight()) {
        g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight());
      } else {
        g.fillRect(0, 0, getWidth() - 1 - imageOffset, getHeight());
      }
    }

    if (hasFocus) {
      if (drawsFocusBorderAroundIcon) {
        imageOffset = 0;
      } else if (imageOffset == -1) {
        imageOffset = getLabelStart();
      }
      Color bsColor = borderSelectionColor;

      if (bsColor != null) {
        g.setColor(bsColor);
        if (getComponentOrientation().isLeftToRight()) {
          g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
        } else {
          g.drawRect(0, 0, getWidth() - 1 - imageOffset, getHeight() - 1);
        }
      }
    }
    super.paint(g);
  }

  private int getLabelStart() {
    Icon currentI = getIcon();
    if (currentI != null && getText() != null) {
      return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
    }
    return 0;
  }

  public Dimension getPreferredSize() {
    Dimension retDimension = super.getPreferredSize();

    if (retDimension != null)
      retDimension = new Dimension(retDimension.width + 3, retDimension.height);
    return retDimension;
  }

  public void validate() {
  }

  public void revalidate() {
  }

  public void repaint(long tm, int x, int y, int width, int height) {
  }

  public void repaint(Rectangle r) {
  }

  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    if (propertyName == "text")
      super.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
  }

  public void firePropertyChange(String propertyName, char oldValue, char newValue) {
  }

  public void firePropertyChange(String propertyName, short oldValue, short newValue) {
  }

  public void firePropertyChange(String propertyName, int oldValue, int newValue) {
  }

  public void firePropertyChange(String propertyName, long oldValue, long newValue) {
  }

  public void firePropertyChange(String propertyName, float oldValue, float newValue) {
  }

  public void firePropertyChange(String propertyName, double oldValue, double newValue) {
  }

  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
  }

}
