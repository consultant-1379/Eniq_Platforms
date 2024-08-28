package com.ericsson.eniq.component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

public class TreeState {

  private TreeState() { /* do not allow construct */ }
  
  public static List<List<Object>> saveExpansionState(final JTree tree) {
    
    final List<List<Object>> l = new ArrayList<List<Object>>();
    int row = 0;
    if (tree!=null){
    while (row < tree.getRowCount()) {

        final List<Object> li = new ArrayList<Object>();
        li.add(tree.isExpanded(row));
        li.add(tree.getSelectionRows());
        l.add(li);
        row++;
    }
    }
    return l;
  }

  public static void loadExpansionState(final JTree tree, final List<List<Object>> list) {
    for (int i = 0; i < list.size(); i++) {
        final List<Object> li = list.get(i);
        final int[] selected = (int[])li.get(1);
        if ((Boolean) li.get(0))
        {
            tree.expandRow(i);
        }
        
        if (selected != null){
        
        for (int si = 0 ; si < selected.length ; si++){
          if (selected[si]==i){
            tree.addSelectionRow(i);
          }
        }
        }
        
    }
  }

}
