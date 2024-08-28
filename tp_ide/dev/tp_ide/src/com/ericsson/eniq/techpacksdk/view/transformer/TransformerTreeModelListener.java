package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tableTree.TableTreeComponent;
import tableTreeUtils.TreesInTreeListener;


public class TransformerTreeModelListener extends TreesInTreeListener{

    static JTree parentTree = null;
    private final String ADD = "Add Node";
    private final String REMOVE = "Remove Node";
    private final String RENAME = "Rename Node";
    private DefaultMutableTreeNode nodeToDelete=null;
    private TransformerTreeModel ttm;
    private Component c;   
    
    public TransformerTreeModelListener(JTree inTree, Component c){
      super(inTree);
        parentTree = inTree;
    }
    
    public void mouseClicked(MouseEvent e) {
        Point clickedPoint = new Point(e.getX(),e.getY());
        SwingUtilities.convertPointToScreen(clickedPoint, (Component) e.getSource());
        SwingUtilities.convertPointFromScreen(clickedPoint, parentTree);
        TreePath path = parentTree.getPathForLocation(clickedPoint.x,clickedPoint.y);
        parentTree.setSelectionPath(path);

        DefaultMutableTreeNode clickedNode=null;
        if(path!=null)clickedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        
        if(e.getButton() == 3 && ((clickedNode != null && !clickedNode.isLeaf()) || path==null)){ // Right-click
        JPopupMenu menu = getPopupMenu(clickedNode);
        menu.show(parentTree, (int)clickedPoint.getX(), (int)clickedPoint.getY());
        }
        else if(e.getButton() == 1 && clickedNode != null && clickedNode.isLeaf()){
        if(path!=null && clickedNode.getUserObject() instanceof TableTreeComponent){
            if(parentTree.isEditing()==false){parentTree.startEditingAtPath(path);}
            DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            TableTreeComponent innerTree = (TableTreeComponent) theNode.getUserObject();
            innerTree.treeDidChange();

            ((DefaultTreeModel)parentTree.getModel()).nodeChanged(theNode);
            parentTree.cancelEditing();
            parentTree.startEditingAtPath(path);
        }
        }
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        // nothing
    }

    public void treeExpanded(TreeExpansionEvent event) {
      /*
        if(event.getSource() instanceof TreesTree){
            int row = parentTree.getRowForPath(event.getPath())+1;
            TreePath newPath = parentTree.getPathForRow(row);
            parentTree.startEditingAtPath(newPath);
        }
      */
    }

       public JPopupMenu getPopupMenu(DefaultMutableTreeNode node){
        JPopupMenu retMenu = new JPopupMenu();
        return retMenu;
        }
    
      public void actionPerformed(ActionEvent e) {
        
      }


      public TransformerTreeModel getTtm() {
        return ttm;
      }


      public void setTtm(TransformerTreeModel ttm) {
        this.ttm = ttm;
      }
}
