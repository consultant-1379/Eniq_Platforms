package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.fest.swing.hierarchy.ComponentHierarchy;
import org.fest.swing.hierarchy.NewHierarchy;

@SuppressWarnings("serial")
public class SwingHierarchyUtility extends JFrame {

  private JTree hierarchyTree;

  private JButton resetButton;

  private JButton getButton;

  public static void main(String[] args) {
    SwingHierarchyUtility swingHierarchyUtility = new SwingHierarchyUtility();
    swingHierarchyUtility.setVisible(true);

    while (true) {
      // Iternal loop
    }
  }

  public SwingHierarchyUtility() {
    super("Swing component hierarchy");
    this.setPreferredSize(new Dimension(200, 300));

    // Create layout for the frame
    this.setLayout(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints();

    // Construct the tree panel
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
    TreeModel model = new DefaultTreeModel(rootNode);
    this.hierarchyTree = new JTree(model);
    JScrollPane textPanelS = new JScrollPane(this.hierarchyTree);
    gc.anchor = GridBagConstraints.NORTH;
    gc.fill = GridBagConstraints.BOTH;
    gc.gridx = 0;
    gc.gridy = 0;
    gc.weightx = 1;
    gc.weighty = 1;
    this.add(textPanelS, gc);

    // Construct the button panel
    JPanel buttonPanel = new JPanel(new FlowLayout());

    this.resetButton = new JButton("Reset");
    this.resetButton.addActionListener(new ResetButtonActionListener());
    buttonPanel.add(this.resetButton);

    this.getButton = new JButton("Get");
    this.getButton.addActionListener(new GetButtonActionListener());
    buttonPanel.add(this.getButton);

    gc.anchor = GridBagConstraints.SOUTH;
    gc.fill = GridBagConstraints.NONE;
    gc.gridx = 0;
    gc.gridy = 1;
    gc.weightx = 0;
    gc.weighty = 0;
    this.add(buttonPanel, gc);

    this.pack();

  }

  private class ResetButtonActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
      TreeModel model = new DefaultTreeModel(rootNode);
      hierarchyTree.setModel(model);
      hierarchyTree.updateUI();
    }
  }

  private class GetButtonActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Swing component hierarchy");
      TreeModel model = new DefaultTreeModel(rootNode);

      ComponentHierarchy componentHierarchy = NewHierarchy.includeExistingComponents();
      Iterator<? extends Container> rootsIterator = componentHierarchy.roots().iterator();
      while (rootsIterator.hasNext()) {
        Container rootContainer = rootsIterator.next();
        String containerAsString = rootContainer.toString();
        DefaultMutableTreeNode firstLevelNode = new DefaultMutableTreeNode(containerAsString);

        Component[] rootContainersComponents = rootContainer.getComponents();
        for (int i = 0; i < rootContainersComponents.length; ++i) {
          DefaultMutableTreeNode secondLevelSubTreeRoot = createSubTree(componentHierarchy, rootContainersComponents[i]);
          firstLevelNode.add(secondLevelSubTreeRoot);
        }

        rootNode.add(firstLevelNode);
      }

      hierarchyTree.setModel(model);
      hierarchyTree.updateUI();
    }

    private DefaultMutableTreeNode createSubTree(ComponentHierarchy componentHierarchy, Component component) {
      String componentAsString = component.toString();

      // Create a tree node
      DefaultMutableTreeNode resultSubTree = new DefaultMutableTreeNode(componentAsString);

      // Get the component's child components
      Iterator<Component> childrenIter = componentHierarchy.childrenOf(component).iterator();
      while (childrenIter.hasNext()) {
        DefaultMutableTreeNode childSubTree = createSubTree(componentHierarchy, childrenIter.next());
        resultSubTree.add(childSubTree);
      }

      return resultSubTree;
    }

  }
}
