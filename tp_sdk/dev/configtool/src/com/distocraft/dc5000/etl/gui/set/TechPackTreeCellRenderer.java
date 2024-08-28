package com.distocraft.dc5000.etl.gui.set;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.distocraft.dc5000.etl.gui.ConfigTool;
import com.distocraft.dc5000.etl.gui.HostNode;
import com.distocraft.dc5000.etl.rock.Meta_collection_sets;
import com.distocraft.dc5000.etl.rock.Meta_collections;

/**
 * @author lemminkainen Copyright Distocraft 2005 $id$
 */
public class TechPackTreeCellRenderer extends JLabel implements TreeCellRenderer {

  private JTree tree;

  protected boolean selected;

  protected boolean hasFocus;

  private boolean drawsFocusBorderAroundIcon;

  transient protected Icon leafIcon;

  transient protected Icon hostIcon;

  transient protected Icon typeIcon;

  transient protected Icon techPackIcon;

  transient protected Icon setIcon;

  protected Color textSelectionColor = UIManager.getColor("Tree.selectionForeground");

  protected Color textNonSelectionColor = UIManager.getColor("Tree.textForeground");

  protected Color backgroundSelectionColor = UIManager.getColor("Tree.selectionBackground");

  protected Color backgroundNonSelectionColor = UIManager.getColor("Tree.textBackground");

  protected Color borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");

  public TechPackTreeCellRenderer() {
    setHorizontalAlignment(JLabel.LEFT);

    leafIcon = UIManager.getIcon("Tree.leafIcon");

    hostIcon = ConfigTool.dataConnection;
    typeIcon = ConfigTool.folder;
    techPackIcon = ConfigTool.box;
    setIcon = ConfigTool.chain;

    Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
    drawsFocusBorderAroundIcon = (value != null && ((Boolean) value).booleanValue());
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {

    String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

    this.tree = tree;
    this.hasFocus = hasFocus;
    setText(stringValue);
    if (sel)
      setForeground(textSelectionColor);
    else
      setForeground(textNonSelectionColor);

    if (value instanceof HostNode) {
      if (tree.isEnabled())
        setIcon(hostIcon);
      else
        setDisabledIcon(hostIcon);
    } else if (value instanceof TypeNode) {
      if (tree.isEnabled())
        setIcon(typeIcon);
      else
        setDisabledIcon(typeIcon);
    } else if (value instanceof TechPackNode) {
      TechPackNode node = (TechPackNode) value;
      Meta_collection_sets metaCollectionSet = node.getMeta();
      if (tree.isEnabled()) {
        if (metaCollectionSet.getEnabled_flag().equalsIgnoreCase("Y") == true) {
          setIcon(techPackIcon);
        } else {
          Icon disabledMetaCollectionSetsIcon = createInactiveIcon(techPackIcon);
          setIcon(disabledMetaCollectionSetsIcon);
        }

      } else
        setDisabledIcon(techPackIcon);

    } else if (value instanceof SetNode) {

      SetNode node = (SetNode) value;
      Meta_collections metaCollectionSet = node.getSet();
      if (tree.isEnabled()) {
        if (metaCollectionSet.getEnabled_flag().equalsIgnoreCase("Y") == true) {
          setIcon(setIcon);
        } else {
          Icon disabledSetIcon = createInactiveIcon(setIcon);
          setIcon(disabledSetIcon);
        }

      } else
        setDisabledIcon(setIcon);
    } else {
      if (tree.isEnabled())
        setIcon(leafIcon);
      else
        setDisabledIcon(leafIcon);
    }

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

  /**
   * This method creates the inactive icon from the active icon. Inactive icon is the active icon filtered by the grayfilter. 
   * @param targetIcon is active icon used to create inactive icon
   * @return Returns the inactive Icon-object.
   */
  private Icon createInactiveIcon(Icon targetIcon) {
    ImageIcon tempImageIcon = (ImageIcon) targetIcon;
    ImageIcon inactiveImageIcon = new ImageIcon();
    inactiveImageIcon.setImage(GrayFilter.createDisabledImage(tempImageIcon.getImage()));

    return (Icon) inactiveImageIcon;
  }

}
