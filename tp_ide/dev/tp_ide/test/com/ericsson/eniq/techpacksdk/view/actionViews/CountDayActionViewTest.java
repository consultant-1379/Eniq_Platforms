/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2012 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

/**
 * @author epaujor
 * @since 2012
 * 
 */
public class CountDayActionViewTest extends BaseUnitTestX {

  CountDayActionView objUnderTest;

  private Meta_transfer_actions mockMetaTransferActions = null;

  private final static String EXPECTED_ACTION_CONTENT = "Test Action Content";

  private final static String EXPECTED_WHERE_CLAUSE = "targetType=EVENT_E_SGEH_VEND_HIER3_EVENTID_SUC\nsourceType=EVENT_E_SGEH_SUC:RAW";

  private final JPanel parent = new JPanel();

  @Before
  public void setUp() {
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", EXPECTED_WHERE_CLAUSE);
    objUnderTest = new CountDayActionView(parent, mockMetaTransferActions);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("CountingDayAction", objUnderTest.getType());
  }

  @Test
  public void checkThatValidateReturnsCorrectString() throws IOException {
    objUnderTest = new CountDayActionView(parent, null);
    assertEquals("Parameter SQL Template must be defined\n" + "Parameter Target Type must be defined\n"
        + "Parameter Source Type must be defined\n", objUnderTest.validate());
  }

  @Test
  public void checkThatGetContentReturnsCorrectString() {
    assertEquals("Test Action Content", objUnderTest.getContent());
  }
  
  @Test
  public void checkThatGetWhereReturnsCorrectStrings() throws IOException {
    assertTrue(objUnderTest.getWhere().contains("targetType=EVENT_E_SGEH_VEND_HIER3_EVENTID_SUC"));
    assertTrue(objUnderTest.getWhere().contains("sourceType=EVENT_E_SGEH_SUC\\:RAW"));
  }
  
  @Test
  public void checkThatIsChangedReturnsTrue() {
    assertEquals(true, objUnderTest.isChanged());
  }
  
  private void createMockMetaTransferActions(final String name, final String expectedWhereClause) {
    mockMetaTransferActions = context.mock(Meta_transfer_actions.class, name);

    context.checking(new Expectations() {

      {
        allowing(mockMetaTransferActions).getAction_contents();
        will(returnValue(EXPECTED_ACTION_CONTENT));
        allowing(mockMetaTransferActions).getWhere_clause();
        will(returnValue(expectedWhereClause));
      }
    });
  }
}
