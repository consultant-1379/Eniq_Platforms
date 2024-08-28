package com.ericsson.eniq.techpacksdk.view.dataFormat;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import tableTree.TableTreeComponent;
import tableTreeUtils.TreesInTreeListener;

import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;

public class DataformatTreeModelListener extends TreesInTreeListener {

  static JTree parentTree = null;

  private final String ADD = "Add Node";

  private final String REMOVE = "Remove Node";

  private final String RENAME = "Rename Node";

  private DefaultMutableTreeNode nodeToDelete = null;

  private TagTreeModel ttm;

  private final boolean editable;

  private Component c;

  private String addNewStr;

  private JFrame frame;

  private Application application;

  public DataformatTreeModelListener(JFrame frame, Application application, JTree inTree, Component c, boolean editable) {
    super(inTree);
    this.frame = frame;
    this.application = application;
    parentTree = inTree;
    this.editable = editable;
  }

  public void mouseClicked(MouseEvent e) {
    Point clickedPoint = new Point(e.getX(), e.getY());
    SwingUtilities.convertPointToScreen(clickedPoint, (Component) e.getSource());
    SwingUtilities.convertPointFromScreen(clickedPoint, parentTree);
    TreePath path = parentTree.getPathForLocation(clickedPoint.x, clickedPoint.y);
    parentTree.setSelectionPath(path);

    DefaultMutableTreeNode clickedNode = null;
    if (path != null)
      clickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();

    if (e.getButton() == 3 && ((clickedNode != null && !clickedNode.isLeaf()) || path == null)) { // Right
                                                                                                  // -
                                                                                                  // click
      JPopupMenu menu = getPopupMenu(clickedNode);
      menu.show(parentTree, (int) clickedPoint.getX(), (int) clickedPoint.getY());
    } else if (e.getButton() == 1 && clickedNode != null && clickedNode.isLeaf()) {
      if (path != null && clickedNode.getUserObject() instanceof TableTreeComponent) {
        if (parentTree.isEditing() == false) {
          parentTree.startEditingAtPath(path);
        }
        DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        TableTreeComponent innerTree = (TableTreeComponent) theNode.getUserObject();
        innerTree.treeDidChange();

        ((DefaultTreeModel) parentTree.getModel()).nodeChanged(theNode);
        parentTree.cancelEditing();
        parentTree.startEditingAtPath(path);
      }
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void treeCollapsed(TreeExpansionEvent event) {
    // nothing
  }

  public void treeExpanded(TreeExpansionEvent event) {
    /*
     * if(event.getSource() instanceof TreesTree){ int row =
     * parentTree.getRowForPath(event.getPath())+1; TreePath newPath =
     * parentTree.getPathForRow(row); parentTree.startEditingAtPath(newPath); }
     */
  }

  public JPopupMenu getPopupMenu(DefaultMutableTreeNode node) {
    JPopupMenu retMenu = new JPopupMenu();

    JMenuItem item;
    item = new JMenuItem(ADD);
    item.setActionCommand(ADD);
    item.addActionListener(this);
    item.setEnabled(editable);
    retMenu.add(item);

    if (node != null) {
      item = new JMenuItem(REMOVE);
      item.setActionCommand(REMOVE);
      item.setEnabled(editable);
      item.addActionListener(this);
      retMenu.add(item);
      item = new JMenuItem(RENAME);
      item.setActionCommand(RENAME);
      item.setEnabled(editable);
      item.addActionListener(this);
      retMenu.add(item);
      nodeToDelete = node;
    }
    return retMenu;
  }

  private class AddnewTask extends Task<Void, Void> {

    public AddnewTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {
      if (addNewStr != null) {
        ttm.addNew(addNewStr);
        addNewStr = null;
      }
      return null;
    }
  }

  @Action
  public Task<Void, Void> addnew() {

    final Task<Void, Void> addnewTask = new AddnewTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    addnewTask.setInputBlocker(new BusyIndicatorInputBlocker(addnewTask, busyIndicator));

    return addnewTask;
  }

  private class DeleteTask extends Task<Void, Void> {

    public DeleteTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {
      if (nodeToDelete != null) {
        ttm.remove(nodeToDelete);
        ((TagTreeModel) parentTree.getModel()).removeNodeFromParent(nodeToDelete);
        nodeToDelete = null;
      }
      return null;
    }
  }

  @Action
  public Task<Void, Void> delete() {

    final Task<Void, Void> deleteTask = new DeleteTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    deleteTask.setInputBlocker(new BusyIndicatorInputBlocker(deleteTask, busyIndicator));

    return deleteTask;
  }

  private class RenameTask extends Task<Void, Void> {

    public RenameTask(Application app) {
      super(app);
    }

    @Override
    protected Void doInBackground() throws Exception {
      if (addNewStr != null && nodeToDelete != null) {
        ttm.rename(nodeToDelete.toString(), addNewStr);
        nodeToDelete = null;
        addNewStr = null;
      }
      return null;
    }
  }

  @Action
  public Task<Void, Void> rename() {

    final Task<Void, Void> renameTask = new RenameTask(application);
    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);
    renameTask.setInputBlocker(new BusyIndicatorInputBlocker(renameTask, busyIndicator));

    return renameTask;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(ADD)) {
      nodeToDelete = null;

      String message = "New dataformat name";
      while (true) {

        String s = (String) JOptionPane.showInputDialog(c, message, "New dataformat", JOptionPane.QUESTION_MESSAGE);

        if (s != null) {
          if (s.length() > 0) {

            if (!ttm.getDataformats().contains(s)) {
              addNewStr = s;
              application.getContext().getActionMap(this).get("addnew").actionPerformed(null);
              break;
            } else {
              message = "Dataformat name (" + s + ") already exist, give new dataformat name";
            }
          }
        } else {
          break;
        }
      }
    } else if (e.getActionCommand().equals(REMOVE)) {

      application.getContext().getActionMap(this).get("delete").actionPerformed(null);

      // nodeToDelete = null;

    }
    if (e.getActionCommand().equals(RENAME)) {

      String message = "New dataformat name";
      while (true) {

        String s = (String) JOptionPane.showInputDialog(c, message, "Rename dataformat", JOptionPane.QUESTION_MESSAGE);

        if (s != null) {
          if (s.length() > 0) {

            if (!ttm.getDataformats().contains(s)) {
              addNewStr = s;
              application.getContext().getActionMap(this).get("rename").actionPerformed(null);

              // nodeToDelete = null;
              break;
            } else {
              message = "Dataformat name (" + s + ") already exist, give new dataformat name";
            }
          }
        } else {
          break;
        }
      }
    }
  }

  public TagTreeModel getTtm() {
    return ttm;
  }

  public void setTtm(TagTreeModel ttm) {
    this.ttm = ttm;
  }
}
