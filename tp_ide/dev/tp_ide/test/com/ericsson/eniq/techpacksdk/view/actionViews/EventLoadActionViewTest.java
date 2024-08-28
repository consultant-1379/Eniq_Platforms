package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class EventLoadActionViewTest extends BaseUnitTestX {

    private static final String USE_NAMED_PIPE = "useNamedPipe=false";

    private static final String USE_SNAPPY = "useSnappy=false";

    private static final String IS_PARALLEL_LOAD_ALLOWED = "isParallelLoadAllowed=false";

    private final String tableName = "tablename=testTableName";

    private final String techpack = "techpack=testTechPackName";

    private final String taildir = "taildir=testTailDirTest";

    private final String dateformat = "dateformat=yyyy-MM-DD";

    private String versionDir = "versiondir=testVersionDir";

    private final String fileDuplicateCheck = "fileDuplicateCheck=true";

    private String expectedWhereClause = tableName + "\n" + techpack + "\n" + taildir + "\n" + dateformat + "\n" + versionDir + "\n"
            + fileDuplicateCheck + "\n";

    private final String expectedActionContent = "Test Action Content";

    private Meta_transfer_actions mockMetaTransferActions = null;

    @Before
    public void setUp() throws Exception {
        recreateMockeryContext();
        createMockMetaTransferActions("mockMetaTransferActionsName");
    }

    @Test
    public void checkThatWhereClauseAndContentAreCorrect() throws Exception {
        JPanel parent = new JPanel();
        EventLoadActionView actionView = new EventLoadActionView(parent, mockMetaTransferActions);
        assertTrue(actionView.getWhere().contains(tableName));
        assertTrue(actionView.getWhere().contains(techpack));
        assertTrue(actionView.getWhere().contains(taildir));
        assertTrue(actionView.getWhere().contains(dateformat));
        assertTrue(actionView.getWhere().contains(versionDir));
        assertTrue(actionView.getWhere().contains(fileDuplicateCheck));
        assertTrue(actionView.getWhere().contains(USE_NAMED_PIPE));
        assertTrue(actionView.getWhere().contains(USE_SNAPPY));
        assertTrue(actionView.getWhere().contains(IS_PARALLEL_LOAD_ALLOWED));
        assertTrue(actionView.getContent().equals(expectedActionContent));
    }

    @Test
    public void checkThatCorrectErrorMessageIsReturnedWhenDirectoryVersionIsNotAnInteger() throws Exception {
        JPanel parent = new JPanel();
        EventLoadActionView actionView = new EventLoadActionView(parent, mockMetaTransferActions);
        assertEquals("Parameter Directory Version: Invalid Integer.\n", actionView.validate());
    }

    @Test
    public void checkThatEmptyMessageIsReturnedWhenDirectoryVersionIsAnInteger() throws Exception {
        // Change versionDir to valid integer
        versionDir = "versiondir=123";
        // Update expectedWhereClause
        expectedWhereClause = tableName + "\n" + techpack + "\n" + taildir + "\n" + dateformat + "\n" + versionDir + "\n" + fileDuplicateCheck + "\n";
        // Recreate mockMetaTransferActions with newWhereClause
        createMockMetaTransferActions("newMockMetaTransferActionsWithUpdatedWhereClause");

        JPanel parent = new JPanel();
        EventLoadActionView actionView = new EventLoadActionView(parent, mockMetaTransferActions);
        assertEquals("", actionView.validate());
    }

    @Test
    public void checkThatCorrectErrorMessageIsReturnedWhenDirectoryVersionIsEmpty() throws Exception {
        // Change versionDir to valid integer
        versionDir = "versiondir=";
        // Update expectedWhereClause
        expectedWhereClause = tableName + "\n" + techpack + "\n" + taildir + "\n" + dateformat + "\n" + versionDir + "\n" + fileDuplicateCheck + "\n";
        // Recreate mockMetaTransferActions with newWhereClause
        createMockMetaTransferActions("newMockMetaTransferActionsWithUpdatedWhereClause");

        JPanel parent = new JPanel();
        EventLoadActionView actionView = new EventLoadActionView(parent, mockMetaTransferActions);
        assertEquals("Parameter Directory Version must be defined.\n", actionView.validate());
    }

    @Test
    public void checkThatGetTypeReturnsCorrectString() throws Exception {
        JPanel parent = new JPanel();
        EventLoadActionView actionView = new EventLoadActionView(parent, mockMetaTransferActions);
        assertEquals("EventLoader", actionView.getType());
    }

    private void createMockMetaTransferActions(String name) {
        mockMetaTransferActions = context.mock(Meta_transfer_actions.class, name);

        context.checking(new Expectations() {

            {
                allowing(mockMetaTransferActions).getAction_contents();
                will(returnValue(expectedActionContent));
                allowing(mockMetaTransferActions).getWhere_clause();
                will(returnValue(expectedWhereClause));
            }
        });
    }
}
