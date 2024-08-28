package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class CountingReAggActionViewTest extends BaseUnitTestX {

  CountingReAggActionView objUnderTest;
  private Meta_transfer_actions mockMetaTransferActions = null;

  private final static String EXPECTED_ACTION_CONTENT = "Test Action Content";

  private final static String EXPECTED_WHERE_CLAUSE = "tablename=EVENT_E_TEST:PLAIN";

  private final JPanel parent = new JPanel();
  @Before
  public void setUp() {
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", EXPECTED_WHERE_CLAUSE);
        objUnderTest = new CountingReAggActionView(parent, mockMetaTransferActions);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("CountReAggAction", objUnderTest.getType());
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
  public void checkThatGetWhereReturnsCorrectString() throws IOException {
    assertTrue(objUnderTest.getWhere().contains("isReaggBasedOn15min=false"));
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
