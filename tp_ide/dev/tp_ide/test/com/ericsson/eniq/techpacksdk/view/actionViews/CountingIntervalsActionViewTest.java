package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;



public class CountingIntervalsActionViewTest {

  CountingIntervalsActionView objUnderTest;

  @Before
  public void setUp() {
    final JPanel parent = new JPanel();
    objUnderTest = new CountingIntervalsActionView(parent);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("CountingIntervals", objUnderTest.getType());
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
