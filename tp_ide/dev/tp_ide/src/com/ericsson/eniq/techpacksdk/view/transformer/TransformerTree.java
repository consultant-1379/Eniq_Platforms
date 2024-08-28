package com.ericsson.eniq.techpacksdk.view.transformer;

import java.awt.Component;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class TransformerTree extends JTree {

  private static final Logger logger = Logger.getLogger(TransformerTree.class.getName());

  private static final long serialVersionUID = 1L;

  private TransformerTreeModelListener listener = null;

  private TransformerTreeModel ttm;

  private final String ADD = "Add Node";

  private final String REMOVE = "Remove Node";

  private DefaultMutableTreeNode nodeToDelete;

  private DataModelController dataModelController;
  
  private Application application;

  public TransformerTree(SingleFrameApplication application, Versioning versioning, DataModelController dataModelController, boolean editable, DocumentListener dl, Component c) {

    this.dataModelController = dataModelController;
    listener = new TransformerTreeModelListener(this,c);
    ttm = new TransformerTreeModel(application, versioning, dataModelController, editable, this, listener, dl);
    listener.setTtm(ttm);
    this.setModel(ttm);
    this.addTreeExpansionListener(listener);
    this.setEditable(true);
    this.setCellRenderer(new TransformerTreeRenderer());
    this.setCellEditor(new TransformerTreeEditor(this));
    this.setRootVisible(false);
    this.setShowsRootHandles(true);

    // Remove some of the autoscrolling in the tree
    this.setAutoscrolls(false);

    this.setScrollsOnExpand(false);

    // Double-click should edit, not expand
    this.setToggleClickCount(0);

  }

  public void addDocumentListener(DocumentListener dl){
    ttm.addDocumentListener(dl);
  }
  
  public void update() {
    List list = TreeState.saveExpansionState(this);
    ttm.update();
    TreeState.loadExpansionState(this, list);
  }
  
  public void save() {
    logger.log(Level.INFO, "save");
    List list = TreeState.saveExpansionState(this);
    ttm.save();
    TreeState.loadExpansionState(this, list);
  }

  public void discard() {
    logger.log(Level.INFO, "discard");
    List list = TreeState.saveExpansionState(this);
    ttm.discard();
    TreeState.loadExpansionState(this, list);
  }
  
  public Vector<String> validateData() {
    return ttm.validateData();
  }

  public TransformerTreeModel getTtm() {
    return ttm;
  }

  public void setTtm(TransformerTreeModel ttm) {
    this.ttm = ttm;
  }
}
