package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.timing.Pause;

public abstract class UniverseParametersUtils {
  
  public static JPanelFixture getTab(Robot robot, JTabbedPaneFixture tabbedPaneFixture, String tabName) {
    Component tabComponent = getTabAsComponent(robot, tabbedPaneFixture, tabName);
    if (tabComponent != null && tabComponent instanceof JPanel) {
      return new JPanelFixture(robot, (JPanel) tabComponent);
    }
    else {
      return null;
    }
  }
  
//  public static JTabbedPaneFixture getTabAsTabbedPane(RobotFixture robot, JTabbedPaneFixture tabbedPaneFixture, String tabName) {
//    Component tabComponent = getTabAsComponent(robot, tabbedPaneFixture, tabName);
//     if (tabComponent != null && tabComponent instanceof JTabbedPane) {
//      return new JTabbedPaneFixture(robot, (JTabbedPane) tabComponent);
//    }
//    else {
//      return null;
//    }
//  }
  
  public static Component getTabAsComponent(Robot robot, JTabbedPaneFixture tabbedPaneFixture, String tabName) {
    tabbedPaneFixture.selectTab(tabName);
    if (tabName.equals("Universe")){
    	Pause.pause(5000);
    }
    
    JTabbedPane tabbedPane = tabbedPaneFixture.component();
    String[] tabNames = tabbedPaneFixture.tabTitles();
    
    int tabIndex = -1;
    for (int i = 0; i < tabNames.length; i++) {
      if (tabNames[i].equals(tabName)) tabIndex = i;
    }

    if (tabIndex >= 0) {
      return tabbedPane.getComponentAt(tabIndex);
    }
    else {
      return null;
    }
    
  }
  
}
