package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.generaltechpack.GeneralTechPackTab;

@SuppressWarnings("serial")
public class UniverseTabs extends JPanel {

  JFrame frame;

  private final SingleFrameApplication application;

  boolean editable = true;

  private GeneralTechPackTab parentPanel;
  
  DataModelController dataModelController;

  JTable tpT;

  JTable tppedencyT;

  private JTabbedPane tabbedPanel;
  
  private Object caller;
  
  UniverseTablesView UniverseTablesTab;

  UniverseClassView UniverseClassTab;

  UniverseObjectView UniverseObjectTab;

  UniverseConditionView UniverseConditionTab;

  UniverseJoinView UniverseJoinsTab;
  
  UniverseGenerateView UniverseGenerateTab;
  
  UniverseFormulasView universeFormulasTab;

  public UniverseTabs(final SingleFrameApplication application, DataModelController dataModelController,
      Versioning versioning, GeneralTechPackTab parentPanel, boolean editable, JFrame frame) {
    super(new GridBagLayout());

    this.frame = frame;
    this.editable = editable;
    this.application = application;
    this.dataModelController = dataModelController;
    this.parentPanel = parentPanel;
    tabbedPanel = new JTabbedPane();
    tabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);

    // Generate tab
    UniverseGenerateTab = new UniverseGenerateView(this.application, this.dataModelController, versioning, this.editable,
        this, this.frame);
    tabbedPanel.addTab("Generate", null, UniverseGenerateTab);

    // Tables tab
    UniverseTablesTab = new UniverseTablesView(this.application, this.dataModelController, versioning, this.editable,
        this, this.frame);
    tabbedPanel.addTab("Tables", null, UniverseTablesTab);

    // Class tab
    UniverseClassTab = new UniverseClassView(this.application, this.dataModelController, versioning,
        this.editable, this, this.frame);
    tabbedPanel.addTab("Classes", null, UniverseClassTab);

    // Object tab
    UniverseObjectTab = new UniverseObjectView(this.application, this.dataModelController, versioning, this.editable,
        this, this.frame);
    tabbedPanel.addTab("Objects", null, UniverseObjectTab);

    // Condition tab
    UniverseConditionTab = new UniverseConditionView(this.application, this.dataModelController, versioning,
        this.editable, this, this.frame);
    tabbedPanel.addTab("Conditions", null, UniverseConditionTab);

    // Joins tab
    UniverseJoinsTab = new UniverseJoinView(this.application, this.dataModelController, versioning, this.editable,
        this, this.frame);
    tabbedPanel.addTab("Joins", null, UniverseJoinsTab);
    
    // Formulas tab
    universeFormulasTab = new UniverseFormulasView(this.application, this.dataModelController, versioning, this.editable,
    	this, this.frame);
    tabbedPanel.addTab("Formulas", null, universeFormulasTab);
    
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(tabbedPanel, gbc);
  }
  
  public void setCaller(Object obj){
    this.caller = obj;
  }
  
  public boolean isEditable() {
    return editable;
  }

  /**
   * close dialog action
   * 
   * @return
   */
  @Action
  public void closeDialog() {
    this.frame.dispose();
  }

  /**
   * disableTabs
   * 
   * @return
   */
  @Action
  public void disableTabs() {
    for (int i = 0; i < tabbedPanel.getComponentCount(); i++) {
      tabbedPanel.setEnabledAt(i, false);
    }
    getParentAction("disableTabs").actionPerformed(null);
  }

  /**
   * enableTabs
   * 
   * @return
   */
  @Action
  public void enableTabs() {
    for (int i = 0; i < tabbedPanel.getComponentCount(); i++) {
      tabbedPanel.setEnabledAt(i, true);
    }
    // Refresh all the universe views
    if (caller != UniverseTablesTab)
      UniverseTablesTab.refresh();
    if (caller != UniverseClassTab)
      UniverseClassTab.refresh();
    if (caller != UniverseObjectTab)
      UniverseObjectTab.refresh();
    if (caller != UniverseConditionTab)
      UniverseConditionTab.refresh();
    if (caller != UniverseJoinsTab)
      UniverseJoinsTab.refresh();
    if (caller != UniverseGenerateTab)
      UniverseGenerateTab.refresh();
    if (caller != universeFormulasTab)
      universeFormulasTab.refresh();

    getParentAction("enableTabs").actionPerformed(null);
  }

  /**
   * Helper function, returns action by name from parent panel
   * 
   * @param actionName
   * @return
   */
  private javax.swing.Action getParentAction(final String actionName) {
    if (application != null) {
      return application.getContext().getActionMap(parentPanel).get(actionName);
    }
    return null;
  }

}
