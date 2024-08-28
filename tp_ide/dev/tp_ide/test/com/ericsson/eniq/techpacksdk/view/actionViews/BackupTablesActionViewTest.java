package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class BackupTablesActionViewTest extends BaseUnitTestX {

  BackupTablesActionView objUnderTest;

  private Meta_transfer_actions mockMetaTransferActions = null;

  private final static String EXPECTED_ACTION_CONTENT = "Test Action Content";

  private final static String EXPECTED_WHERE_CLAUSE = "tablename=EVENT_E_TEST:PLAIN";

  private final JPanel parent = new JPanel();

  @Before
  public void setUp() {
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", EXPECTED_WHERE_CLAUSE);
    objUnderTest = new BackupTablesActionView(parent, mockMetaTransferActions);
  }

  @Test
  public void checkThatGetTypeReturnsCorrectString() {
    assertEquals("BackupTables", objUnderTest.getType());
  }

  @Test
  public void checkThatValidateReturnsCorrectString() throws IOException {
    objUnderTest = new BackupTablesActionView(parent, null);
    assertEquals("Parameter Source Type must be defined\n", objUnderTest.validate());
  }

  @Test
  public void checkThatGetContentReturnsCorrectString() {
    assertEquals("", objUnderTest.getContent());
  }

  @Test
  public void checkThatGetWhereReturnsCorrectStringsForPlainStorageId() throws IOException {
    assertTrue(objUnderTest.getWhere().contains("tablename=EVENT_E_TEST\\:PLAIN"));
    assertTrue(objUnderTest.getWhere().contains("typeName=EVENT_E_TEST_CURRENT_DC"));
    assertTrue(objUnderTest.getWhere().contains("isVolBasedPartition=false"));
  }

  @Test
  public void checkThatGetWhereReturnsCorrectStringsForRawStorageId() throws IOException {
    final String storageId = "tablename=EVENT_E_TEST:RAW";
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", storageId);
    objUnderTest = new BackupTablesActionView(parent, mockMetaTransferActions);
    assertTrue(objUnderTest.getWhere().contains("tablename=EVENT_E_TEST\\:RAW"));
    assertTrue(objUnderTest.getWhere().contains("typeName=EVENT_E_TEST_CURRENT_DC"));
  }

  @Test
  public void checkThatGetWhereReturnsCorrectStringsForDayStorageId() throws IOException {
    final String storageId = "tablename=EVENT_E_TEST:DAY";
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", storageId);
    objUnderTest = new BackupTablesActionView(parent, mockMetaTransferActions);
    assertTrue(objUnderTest.getWhere().contains("tablename=EVENT_E_TEST\\:DAY"));
    assertTrue(objUnderTest.getWhere().contains("typeName=EVENT_E_TEST_CURRENT_DC"));
    assertTrue(objUnderTest.getWhere().contains("isVolBasedPartition=false"));
  }

  @Test
  public void checkThatGetWhereReturnsThrowsExceptionforIncorrectStorageId() throws IOException {
    final String storageId = "tablename=EVENT_E_TEST:TEST";
    recreateMockeryContext();
    createMockMetaTransferActions("mockMetaTransferActionsName", storageId);
    objUnderTest = new BackupTablesActionView(parent, mockMetaTransferActions);
    try {
      assertTrue(objUnderTest.getWhere().contains("isVolBasedPartition=false"));
      assertTrue(objUnderTest.getWhere().contains("tablename=EVENT_E_TEST\\:DAY"));
      fail("should not get here..");
    } catch (Exception e) {
      assertTrue(e instanceof IOException);
      assertEquals("storageId 'EVENT_E_TEST:TEST' is not known", e.getMessage());
    }
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
