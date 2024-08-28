/**
 * 
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTextComponentFixture;

import tableTreeUtils.PairComponent;

/**
 * @author epetrmi
 * 
 */
public abstract class SetsActionsSchedulingsUtils {

  /**
   * Finds textfield-fixture with title from given panelFixture
   * 
   * PRE-CONDITION: Param-node must be selected/visible
   * 
   * @param parameterFixture
   * @return JTextComponentFixture object
   */
  public static JTextComponentFixture findJTextComponentWithTitleAndJPanelFixture(final String textComponentTitle,
      JPanelFixture parameterFixture) {
    JTextComponentFixture textFix = parameterFixture.textBox(new GenericTypeMatcher<JTextComponent>(JTextComponent.class) {
    
      @Override
      protected boolean isMatching(JTextComponent c) {
        PairComponent pc = (PairComponent) c.getParent();
        if (textComponentTitle != null && textComponentTitle.equals(pc.getTitle())) {
          return true;
        } else {
          return false;
        }
      }
    });
    return textFix;
  }

}
