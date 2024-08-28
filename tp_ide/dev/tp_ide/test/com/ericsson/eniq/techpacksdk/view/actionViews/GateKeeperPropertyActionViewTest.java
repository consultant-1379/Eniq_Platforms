package com.ericsson.eniq.techpacksdk.view.actionViews;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.swing.JPanel;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.rock.Meta_transfer_actions;

public class GateKeeperPropertyActionViewTest extends BaseUnitTestX {

    private static final String EMPTY_STRING = "";

    GateKeeperPropertyActionView objUnderTest;

    private Meta_transfer_actions mockMetaTransferActions = null;

    private final static String TEST_PROPERTY_NAME = "TestPropertyName";

    private final JPanel parent = new JPanel();

    @Before
    public void setUp() {
        recreateMockeryContext();
        createMockMetaTransferActions("mockMetaTransferActionsName", EMPTY_STRING);
        objUnderTest = new GateKeeperPropertyActionView(parent, mockMetaTransferActions);
    }

    @Test
    public void checkThatGetTypeReturnsCorrectString() {
        assertEquals("GateKeeperProperty", objUnderTest.getType());
    }

    @Test
    public void checkThatValidateReturnsCorrectString() throws IOException {
        objUnderTest = new GateKeeperPropertyActionView(parent, null);
        assertEquals("Parameter Property Name must be defined\n", objUnderTest.validate());
    }

    @Test
    public void checkThatGetContentReturnsCorrectString() {
        assertEquals(TEST_PROPERTY_NAME, objUnderTest.getContent());
    }

    @Test
    public void checkThatGetWhereReturnsCorrectStringsForPlainStorageId() throws IOException {
        assertEquals(EMPTY_STRING, objUnderTest.getWhere());

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
                will(returnValue(TEST_PROPERTY_NAME));
                allowing(mockMetaTransferActions).getWhere_clause();
                will(returnValue(expectedWhereClause));
            }
        });
    }
}
