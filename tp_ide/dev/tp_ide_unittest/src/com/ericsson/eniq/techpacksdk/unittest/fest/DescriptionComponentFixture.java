package com.ericsson.eniq.techpacksdk.unittest.fest;

import javax.swing.JTextField;

import org.fest.swing.core.Robot;
import org.fest.swing.driver.ComponentDriver;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.GenericComponentFixture;
import org.fest.swing.fixture.JButtonFixture;

import tableTreeUtils.DescriptionComponent;

public class DescriptionComponentFixture extends GenericComponentFixture<DescriptionComponent> {

  DescriptionComponent targetComponent = null;

  public DescriptionComponentFixture(Robot robot, DescriptionComponent target) {
    super(robot, target);
    targetComponent = target;
  }

  public DescriptionComponentFixture(Robot robot, ComponentDriver driver, DescriptionComponent target) {
    super(robot, driver, target);
    targetComponent = target;
  }

  public void clickEditButton() {
    System.out.println("DescriptionComponentFixture clickEditButton(): simulating edit button click.");
    JButtonFixture bf = new JButtonFixture(robot, targetComponent.getEditButton());
    
    System.out.println("clickEditButton(): desc comp showing = " + targetComponent.isShowing());
    System.out.println("clickEditButton(): desc comp visible = " + targetComponent.isVisible());
    System.out.println("clickEditButton(): desc comp button showing = " + targetComponent.getEditButton().isShowing());
    System.out.println("clickEditButton(): desc comp button visible = " + targetComponent.getEditButton().isVisible());

    bf.click();
  }

  /*
  public void enterText(final String text) {
    System.out.println("DescriptionComponentFixture enterText(): Entering text: " + text);

    final JTextField textField = targetComponent.getTextField();

    GuiActionRunner.execute(new GuiTask() {

      public void executeInEDT() {
        textField.setText(text);
      }
    });
  }
  */
}
