package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;


public class BackupCountDayActionViewTest {

  BackupCountDayActionView objUnderTest;

  @Before
  public void setUp() {
    final JPanel parent = new JPanel();
    objUnderTest = new BackupCountDayActionView(parent);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("BackupCountDay", objUnderTest.getType());
  }

  @Test
  public void checkThatValidateReturnsCorrectString() {
    assertEquals("", objUnderTest.validate());
  }

  @Test
  public void checkThatGetContentReturnsCorrectString() {
    assertEquals("", objUnderTest.getContent());
  }

  @Test
  public void checkThatGetWhereReturnsCorrectString() {
    assertEquals("", objUnderTest.getWhere());
  }

  @Test
  public void checkThatIsChangedReturnsTrue() {
    assertEquals(true, objUnderTest.isChanged());
  }
}
