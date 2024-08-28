package com.ericsson.eniq.techpacksdk;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.GenericActionTree;
import com.ericsson.eniq.component.TreeState;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackTreeDataModel;

@SuppressWarnings("serial")
public class LockAdminPanel extends JPanel {

  private static final Logger logger = Logger.getLogger(LockAdminPanel.class.getName());

  private final TechPackTreeDataModel tdm;

  private final InterfaceTreeDataModel idm;

  private final ResourceMap resourceMap;

  private final SingleFrameApplication application;

  private final GenericActionTree tpTree;

  private final GenericActionTree ifTree;

  LockAdminPanel(final DataModelController dataModelController, final ResourceMap resourceMap,
      final SingleFrameApplication application) {
    this.resourceMap = resourceMap;
    this.application = application;

    tdm = dataModelController.getTechPackTreeDataModel();
    idm = dataModelController.getInterfaceTreeDataModel();

    ifTree = new GenericActionTree(idm);
    tpTree = new GenericActionTree(tdm);

    setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(2, 2, 2, 2);
    c.weightx = 1;
    c.weighty = 1;
    c.gridwidth = 1;

    tpTree.setToolTipText("");
    tpTree.setCellRenderer(new LockTreeRenderer());
    tpTree.addTreeSelectionListener(new LockTreeSelectionListener(ifTree));
    tpTree.addAction(application.getContext().getActionMap(this).get("forceunlock"));

    final JScrollPane tpscrollPane = new JScrollPane(tpTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    add(tpscrollPane, c);

    c.gridx = 1;
    c.gridwidth = 2;

    ifTree.setToolTipText("");
    ifTree.setCellRenderer(new LockTreeRenderer());
    ifTree.addTreeSelectionListener(new LockTreeSelectionListener(tpTree));
    ifTree.addAction(application.getContext().getActionMap(this).get("forceunlock"));

    final JScrollPane ifscrollPane = new JScrollPane(ifTree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    add(ifscrollPane, c);

    c.anchor = GridBagConstraints.SOUTHEAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1;
    c.weighty = 0;
    c.gridwidth = 1;
    c.gridy = 1;

    final JButton unlock = new JButton(application.getContext().getActionMap(this).get("forceunlock"));

    add(unlock, c);

    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 0;
    c.gridx = 2;

    final JButton refresh = new JButton(application.getContext().getActionMap(this).get("refreshlockadmin"));

    add(refresh, c);

  }

  private boolean lockAdminSelect = false;

  /**
   * Getter of descriptionDocEnabled property
   * 
   * @return
   */
  public boolean isLockAdminSelect() {
    return lockAdminSelect;
  }

  /**
   * Setter of createDocEnabled property
   * 
   * @param createDocEnabled
   */
  public void setLockAdminSelect(boolean ena) {
    boolean oldvalue = this.lockAdminSelect;
    this.lockAdminSelect = ena;
    firePropertyChange("lockAdminSelect", oldvalue, ena);
  }

  @Action(block = BlockingScope.APPLICATION, enabledProperty = "lockAdminSelect")
  public Task<Void, Void> forceunlock() {
    Task<Void, Void> forceUnlockTask = null;

    if (tpTree != null && ifTree != null) {
      List<List<Object>> tList = TreeState.saveExpansionState(tpTree);
      List<List<Object>> iList = TreeState.saveExpansionState(ifTree);
      Object rockO = null;

      TreePath t = tpTree.getSelectionPath();
      if (t == null) {
        t = ifTree.getSelectionPath();
      }
      if (t == null) {
        return null;
      }

      Object pointed = t.getLastPathComponent();
      if (pointed instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        Object tmp = node.getUserObject();
        if (tmp instanceof DataTreeNode) {
          DataTreeNode dtn = (DataTreeNode) tmp;
          if (dtn.locked != null && dtn.locked.length() > 0) {

            rockO = dtn.getRockDBObject();

            if (rockO == null || !(rockO instanceof Versioning || rockO instanceof Datainterface)) {
              return null;
            }

            int selectedValue = JOptionPane.showConfirmDialog(LockAdminPanel.this, resourceMap
                .getString("forceunlock.confirm.message"), resourceMap.getString("forceunlock.confirm.title"),
                JOptionPane.YES_NO_OPTION);

            if (selectedValue == JOptionPane.NO_OPTION) {
              return null;
            }
          } else {
            return null;
          }
        } else {
          return null;
        }
      } else {
        return null;
      }
      setLockAdminSelect(false);

      try {
        forceUnlockTask = new ForceUnLockTask(tList, tList, rockO);
        BusyIndicator busyIndicator = new BusyIndicator();
        application.getMainFrame().setGlassPane(busyIndicator);
        forceUnlockTask.setInputBlocker(new BusyIndicatorInputBlocker(forceUnlockTask, busyIndicator));
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error while unlocking", e);
        JOptionPane.showMessageDialog(LockAdminPanel.this, e.getMessage(), "Error while unlocking",
            JOptionPane.ERROR_MESSAGE);
      }
    }
    return forceUnlockTask;
  }

  public class ForceUnLockTask extends Task<Void, Void> {

    private List<List<Object>> tpState;
    private List<List<Object>> ifState;
    
    private Object rockO;

    public ForceUnLockTask(final List<List<Object>> tpState, final List<List<Object>> ifState, final Object rockO) {
      super(application);
      this.tpState = tpState;
      this.ifState = ifState;
      this.rockO = rockO;
    }

    @Override
    protected Void doInBackground() throws Exception {
      if (rockO instanceof Versioning) {
        final Versioning vers = (Versioning) rockO;
        vers.setLockedby(null);
        vers.setLockdate(null);
        vers.saveDB();
        logger.info("Techpack " + vers.getTechpack_name() + " " + vers.getTechpack_version() + " (" + vers.getVersionid() + ") forcefully unlocked.");
      } else if (rockO instanceof Datainterface) {
        final Datainterface dif = (Datainterface) rockO;
        dif.setLockedby(null);
        dif.setLockdate(null);
        dif.saveDB();
        logger.info("Techpack " + dif.getInterfacename() + " " + dif.getInterfaceversion() + " forcefully unlocked.");
      }

      tdm.refresh();
      idm.refresh();
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(tpTree, tpState);
      TreeState.loadExpansionState(ifTree, ifState);
    }
  };

  @Action(block = BlockingScope.APPLICATION)
  public Task<Void, Void> refreshlockadmin() {
    Task<Void, Void> refreshDataTask = null;

    if ((tpTree != null)) {
      List<List<Object>> eList = TreeState.saveExpansionState(tpTree);
      try {
        refreshDataTask = new RefreshLockAdminTask(eList);
        BusyIndicator busyIndicator = new BusyIndicator();
        application.getMainFrame().setGlassPane(busyIndicator);
        refreshDataTask.setInputBlocker(new BusyIndicatorInputBlocker(refreshDataTask, busyIndicator));
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error while refreshing", e);
        JOptionPane.showMessageDialog(LockAdminPanel.this, e.getMessage(), "Error while refreshing",
            JOptionPane.ERROR_MESSAGE);
      }
    }
    return refreshDataTask;
  }

  public class RefreshLockAdminTask extends Task<Void, Void> {

    private List<List<Object>> treeState;

    public RefreshLockAdminTask(List<List<Object>> treeState) {
      super(application);
      this.treeState = treeState;
    }

    @Override
    protected Void doInBackground() throws Exception {
      tdm.refresh();
      return null;
    }

    @Override
    protected void finished() {
      TreeState.loadExpansionState(tpTree, treeState);
    }
  };

  public class LockTreeRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      Object aUserObject = null;

      if (value != null) {
        aUserObject = ((DefaultMutableTreeNode) value).getUserObject();

        if (aUserObject instanceof DataTreeNode) {

          final DataTreeNode t = (DataTreeNode) aUserObject;

          if (t.locked != null && t.locked.length() > 0) {

            setIcon(resourceMap.getIcon("Main.lockadmin.lockicon"));
            setToolTipText("Locked by " + t.locked);

          } else {
            setIcon(null);
            setToolTipText(null);
          }

        } else if (aUserObject instanceof String) {
          if (row == 0) {
            setIcon(resourceMap.getIcon("Main.lockadmin.rooticon"));
          }
        }
      }

      return this;

    }
  };

  private class LockTreeSelectionListener implements TreeSelectionListener {

    private final JTree anotherTree;

    private LockTreeSelectionListener(final JTree anotherTree) {
      this.anotherTree = anotherTree;
    }

    public void valueChanged(TreeSelectionEvent e) {

      if (e.getNewLeadSelectionPath() == null) {
        return;
      }

      anotherTree.clearSelection();

      TreePath t = e.getPath();
      Object pointed = t.getLastPathComponent();

      if (pointed instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pointed;
        Object tmp = node.getUserObject();

        if (tmp instanceof DataTreeNode) {
          DataTreeNode dtn = (DataTreeNode) tmp;

          if (dtn.locked != null && dtn.locked.length() > 0) {
            setLockAdminSelect(true);
            return;
          }

        }
      }
      setLockAdminSelect(false);
    }
  };

}
