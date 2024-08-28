package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;



public class UpdateCollectedDataActionViewTest extends BaseUnitTestX {

  private UpdateCollectedDataActionView objUnderTest;

  private Meta_transfer_actions mockMetaTransferActions = null;

  private final static String EXPECTED_ACTION_CONTENT = "Test Action Content";

  private final static String EXPECTED_WHERE_CLAUSE = "sourceType=TestSourceType\ntargetType=TestTargetType";

  private final JPanel parent = new JPanel();

  @Before
  public void setUp() {
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName");
    objUnderTest = new UpdateCollectedDataActionView(parent, mockMetaTransferActions);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("UpdateCollectedData", objUnderTest.getType());
  }

  @Test
  public void checkThatValidateReturnsEmptyStringWhenVelocityTemplateIsNotEmpty() {
    assertEquals("", objUnderTest.validate());
  }

  @Test
  public void checkThatValidateMessageReturnsMessageWhenVelocityTemplateIsEmpty() {
    objUnderTest = new UpdateCollectedDataActionView(parent, null);
    assertEquals("Parameter velocity Template must be defined\nParameter Source Type "
        + "must be defined\nParameter Target Type must be defined\n", objUnderTest.validate());
  }

  @Test
  public void checkThatGetContentReturnsCorrectString() {
    assertEquals("Test Action Content", objUnderTest.getContent());
  }

  @Test
  public void checkThatGetWhereReturnsCorrectString() throws IOException {
    assertTrue(objUnderTest.getWhere().contains("sourceType=TestSourceType"));
    assertTrue(objUnderTest.getWhere().contains("targetType=TestTargetType"));
  }

  @Test
  public void checkThatIsChangedReturnsTrue() {
    assertEquals(true, objUnderTest.isChanged());
  }

  private void createMockMetaTransferActions(final String name) {
    mockMetaTransferActions = context.mock(Meta_transfer_actions.class, name);

    context.checking(new Expectations() {

      {
        allowing(mockMetaTransferActions).getAction_contents();
        will(returnValue(EXPECTED_ACTION_CONTENT));
        allowing(mockMetaTransferActions).getWhere_clause();
        will(returnValue(EXPECTED_WHERE_CLAUSE));
      }
    });
  }
}
