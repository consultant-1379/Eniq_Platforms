package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.assertEquals;
import javax.swing.JPanel;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class CreateCollectedDataFilesActionViewTest extends BaseUnitTestX {

  private CreateCollectedDataFilesActionView objUnderTest;

  private Meta_transfer_actions mockMetaTransferActions = null;

  private final static String EXPECTED_ACTION_CONTENT = "Test Action Content";

  private final JPanel parent = new JPanel();
  @Before
  public void setUp() {
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName");
    objUnderTest = new CreateCollectedDataFilesActionView(parent, mockMetaTransferActions);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("CreateCollectedData", objUnderTest.getType());
  }

  @Test
  public void checkThatValidateReturnsEmptyStringWhenVelocityTemplateIsNotEmpty() {
    assertEquals("", objUnderTest.validate());
  }

  @Test
  public void checkThatValidateMessageReturnsMessageWhenVelocityTemplateIsEmpty() {
    objUnderTest = new CreateCollectedDataFilesActionView(parent, null);
    assertEquals("Parameter velocity Template must be defined\n", objUnderTest.validate());
  }

  @Test
  public void checkThatGetContentReturnsCorrectString() {
    assertEquals("Test Action Content", objUnderTest.getContent());
  }

  @Test
  public void checkThatGetWhereReturnsCorrectString() {
    assertEquals("", objUnderTest.getWhere());
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
      }
    });
  }
}
