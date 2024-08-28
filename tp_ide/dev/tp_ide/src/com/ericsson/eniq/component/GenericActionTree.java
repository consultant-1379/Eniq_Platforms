/**
 * 
 */
package com.ericsson.eniq.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author eheijun
 * 
 */
@SuppressWarnings("serial")
public class GenericActionTree extends JTree {

  private List<Action> rootactions;

  private List<Action> actions;

  private List<Action> subactions;

  /**
   * Submenus for the popup menu
   */
  // private List<GenericSubMenu> subMenus;

  private JPopupMenu popup;

  private GenericActionNode selected;

  private String subtitle;

  /**
   * Constructor
   */
  public GenericActionTree(final DefaultTreeModel model) {
    super(model);
    rootactions = new ArrayList<Action>();
    actions = new ArrayList<Action>();
    subactions = new ArrayList<Action>();
    // subMenus = new ArrayList<GenericSubMenu> ();
    popup = new JPopupMenu();
    addMouseListener(new PopupMenuListener(popup));
    selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  /**
   * 
   * @param a
   */
  public void addRootAction(final Action a) {
    rootactions.add(a);
  }

  /**
   * 
   * @param index
   * @param a
   */
  public void addRootAction(final int index, final Action a) {
    rootactions.add(index, a);
  }

  /**
   * 
   * @param a
   */
  public void removeRootAction(final Action a) {
    rootactions.remove(a);
  }

  /**
   * 
   * @param index
   */
  public void removeRootAction(final int index) {
    rootactions.remove(index);
  }

  /**
   * 
   * @param a
   */
  public void addAction(final Action a) {
    actions.add(a);
  }

  /**
   * 
   * @param index
   * @param a
   */
  public void addAction(final int index, final Action a) {
    actions.add(index, a);
  }

  /**
   * 
   * @param a
   */
  public void removeAction(final Action a) {
    actions.remove(a);
  }

  /**
   * 
   * @param index
   */
  public void removeAction(final int index) {
    actions.remove(index);
  }

  /**
   * 
   * @param title
   * @param a
   */
  public void addSubAction(final String title, final Action a) {
    subtitle = title;
    subactions.add(a);
  }

  /**
   * 
   * @param index
   * @param a
   */
  public void addSubAction(final int index, final Action a) {
    subactions.add(index, a);
  }

  /**
   * 
   * @param a
   */
  public void removeSubAction(final Action a) {
    subactions.remove(a);
  }

  /**
   * 
   * @param index
   */
  public void removeSubAction(final int index) {
    subactions.remove(index);
  }

  /**
   * Returns list of actions
   * 
   * @return
   */
  List<Action> getActions() {
    return this.actions;
  }

  /**
   * Returns list of root actions
   * 
   * @return
   */
  List<Action> getRootActions() {
    return this.rootactions;
  }

  /**
   * Returns list of sub actions
   * 
   * @return
   */
  List<Action> getSubActions() {
    return this.subactions;
  }

  /**
   * 
   */
  private void refreshPopupMenu() {
    popup.removeAll();
    for (final Iterator<Action> it = rootactions.iterator(); it.hasNext();) {
      final Action a = it.next();
      final JMenuItem menuItem = new JMenuItem();
      menuItem.setAction(a);
      // set name for injection
      menuItem.setName((String) a.getValue(Action.NAME));
      popup.add(menuItem);
    }
    if (actions.size() > 0) {
      popup.addSeparator();
      for (final Iterator<Action> it = actions.iterator(); it.hasNext();) {
        final Action a = it.next();
        final JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(a);
        // set name for injection
        menuItem.setName((String) a.getValue(Action.NAME));
        popup.add(menuItem);
      }
      if (subactions.size() > 0) {
        popup.addSeparator();
        final JMenu submenu = new JMenu();
        submenu.setText(subtitle);
        boolean submenuEnabled = false;
        for (final Iterator<Action> it = subactions.iterator(); it.hasNext();) {
          final Action a = it.next();
          submenuEnabled = submenuEnabled || a.isEnabled();
          final JMenuItem menuItem = new JMenuItem();
          menuItem.setAction(a);
          // set name for injection
          menuItem.setName((String) a.getValue(Action.NAME));
          submenu.add(menuItem);
        }
        submenu.setEnabled(submenuEnabled);
        popup.add(submenu);
      }
    }

    popup.setOpaque(true);
    popup.setLightWeightPopupEnabled(true);

  }

  private class PopupMenuListener extends MouseAdapter {

    private final JPopupMenu menu;

    PopupMenuListener(final JPopupMenu menu) {
      this.menu = menu;
    }

    public void mousePressed(final MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(final MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(final MouseEvent e) {
      if (e.isPopupTrigger()) {
        refreshPopupMenu();
        menu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  public GenericActionNode getSelected() {
    return selected;
  }

  public void setSelected(final GenericActionNode selected) {
    this.selected = selected;
  }

}
