package com.ericsson.eniq.component;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Class for generic submenu,
 * 
 * @author etogust
 * 
 */

@SuppressWarnings("serial")
public class GenericSubMenu extends JMenu {

  /**
   * Items in the submenu
   */
  private GenericActionTree myActionTree;

  // private GenericSubMenu() {
  // }

  public GenericSubMenu(final GenericActionTree at, final String title) {
    super(title);
    myActionTree = at;
  }

  /**
   * Submenu refresh
   */
  public void refreshSubMenu() {
    removeAll();

    setName("subMenu");

    for (Action a : myActionTree.getActions()) {
      final JMenuItem menuItem = new JMenuItem();
      menuItem.setAction(a);
      menuItem.setName((String) a.getValue(Action.NAME));
      add(menuItem);
    }

    getPopupMenu().add(this);
  }
}
