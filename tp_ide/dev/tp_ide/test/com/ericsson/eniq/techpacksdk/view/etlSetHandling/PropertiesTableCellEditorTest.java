package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import static org.junit.Assert.*;

import java.util.*;

import javax.swing.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import ssc.rockfactory.RockResultSet;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.rock.*;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.view.actionViews.*;

public class PropertiesTableCellEditorTest extends BaseUnitTestX {

    Meta_transfer_actions mockMetaTransferActions = null;

    RockFactory mockRockFactory = null;

    List<Meta_collections> listOfMetaMetaCollections = null;

    List<Meta_collection_sets> listOfMetaMetaCollectionSets = null;

    @Before
    public void setUp() throws Exception {
        StaticProperties.giveProperties(new Properties());
        recreateMockeryContext();

        mockRockFactory = context.mock(RockFactory.class);
        createListofMetaCollectionsForTest();
        createListofMetaCollectionSetsForTest();
        createMockMetaTransferActions();
    }

    @Test
    public void checkThatCorrectItemsAreInActionTypeJComboBoxWhenSetNameIsNOTAnEventSet() throws Exception {
        createMockRockFactoryExpectations();

        context.checking(new Expectations() {

            {
                one(listOfMetaMetaCollectionSets.get(0)).getCollection_set_name();
                will(returnValue("MockCollectionName"));
            }
        });
        final boolean isSelected = true;
        final int row = 0;
        final int col = 0;

        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "PM");
        propertiesTableCellEditor.getTableCellEditorComponent(new JTable(), mockMetaTransferActions, isSelected, row, col);
        propertiesTableCellEditor.createFrame();
        final JComboBox actionTypesComboBox = propertiesTableCellEditor.getAType();

        // Check that correct number of ActionItems exist in comboBox
        assertEquals(Constants.ACTIONTYPEITEMS.length, actionTypesComboBox.getModel().getSize());

        // Check that each item in the comboBox is correct
        for (int i = 0; i < Constants.ACTIONTYPEITEMS.length; i++) {
            assertEquals(Constants.ACTIONTYPEITEMS[i], actionTypesComboBox.getItemAt(i));

        }
    }

    @Test
    public void checkThatCorrectItemsAreInActionTypeJComboBoxWhenSetNameIsAnEventSet() throws Exception {
        createMockRockFactoryExpectations();

        context.checking(new Expectations() {

            {
                one(listOfMetaMetaCollectionSets.get(0)).getCollection_set_name();
                will(returnValue("EVENT_E_SGEH"));
            }
        });
        final boolean isSelected = true;
        final int row = 0;
        final int col = 0;

        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");
        propertiesTableCellEditor.getTableCellEditorComponent(new JTable(), mockMetaTransferActions, isSelected, row, col);
        propertiesTableCellEditor.createFrame();
        final JComboBox actionTypesComboBox = propertiesTableCellEditor.getAType();

        // Check that correct number of ActionItems exist in comboBox
        assertEquals(Constants.EVENTSACTIONTYPEITEMS.length, actionTypesComboBox.getModel().getSize());

        // Check that each item in the comboBox is correct
        for (int i = 0; i < Constants.EVENTSACTIONTYPEITEMS.length; i++) {
            assertEquals(Constants.EVENTSACTIONTYPEITEMS[i], actionTypesComboBox.getItemAt(i));

        }
    }

    @Test
    public void checkThatGetActionViewReturnsVolumeLoadActionViewWhenActionTypeIsEventLoader() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("EventLoader", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof EventLoadActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountingIntervalsActionViewWhenActionTypeIsCountingIntervals() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountingIntervals", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountingIntervalsActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsGateKeeperActionViewWhenActionTypeIsGateKeeperProperty() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("GateKeeperProperty", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof GateKeeperPropertyActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsStoreCountingDataActionViewWhenActionTypeIsStoreCountingData() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("StoreCountingData", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof StoreCountingDataActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountActionViewWhenActionTypeIsCountingAction() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountingAction", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountDayActionViewWhenActionTypeIsCountingAction() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountingDayAction", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountDayActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCreateCollectedDataFilesActionViewWhenActionTypeIsCreateCollectedData() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CreateCollectedData", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CreateCollectedDataFilesActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsUpdateCollectedDataActionViewWhenActionTypeIsUpdateCollectedData() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("UpdateCollectedData", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof UpdateCollectedDataActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountTriggerActionViewWhenActionTypeIsCountingTrigger() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountingTrigger", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountTriggerActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountDayTriggerActionViewWhenActionTypeIsCountingTrigger() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountingDayTrigger", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountDayTriggerActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsBackupTriggerActionViewWhenActionTypeIsBackupTrigger() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("BackupTrigger", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof BackupTriggerActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsBackupLoaderActionViewWhenActionTypeIsBackupLoader() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("BackupLoader", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof BackupLoaderActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsBackupTablesActionViewWhenActionTypeIsBackupTables() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("BackupTables", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof BackupTablesActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsTopologySqlExecuteActionViewWhenActionTypeIsTopologySqlExecute() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("TopologySqlExecute", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof TopologySQLExecuteActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsHistorySqlExecuteActionViewWhenActionTypeIsTopologySqlExecute() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("HistorySqlExecute", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof SQLExecuteActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsUnknownTopologySqlExecuteActionViewWhenActionTypeIsTopologySqlExecute() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("UnknownTopology", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof UnknownTopologySQLExecuteActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsIMSItoIMEISqlExecuteActionViewWhenActionTypeIsTopologySqlExecute() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");
        final ActionView actionView = propertiesTableCellEditor.getActionView("IMSItoIMEI", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof IMSItoIMEISQLExecuteActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsBackupCountDayActionViewWhenActionTypeIsBackupCountDay() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("BackupCountDay", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof BackupCountDayActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsRestoreActionViewWhenActionTypeIsRestore() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("Restore", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof RestoreActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsCountingReAggActionViewWhenActionTypeIsCountReAggAction() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("CountReAggAction", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof CountingReAggActionView);
    }

    @Test
    public void checkThatGetActionViewReturnsRestoreActionViewWhenActionTypeIsUpdateCountIntervals() {
        final PropertiesTableCellEditor propertiesTableCellEditor = new PropertiesTableCellEditor(true, "ENIQ_EVENT");

        final ActionView actionView = propertiesTableCellEditor.getActionView("UpdateCountIntervals", new JPanel(), mockMetaTransferActions);

        assertTrue(actionView instanceof UpdateCountIntervalsActionView);
    }

    private void createListofMetaCollectionsForTest() {
        // Create mock Meta_databases
        final Meta_collections mockMetaCollections = context.mock(Meta_collections.class);

        context.checking(new Expectations() {

            {
                allowing(mockMetaCollections).setModifiedColumns(with(any(Set.class)));
                allowing(mockMetaCollections).setNewItem(with(any(boolean.class)));
                allowing(mockMetaCollections).getCollection_name();
                will(returnValue("MockCollectionName"));
                allowing(mockMetaCollections).setOriginal(with(any(Meta_collections.class)));

            }
        });

        listOfMetaMetaCollections = new ArrayList<Meta_collections>();
        // Add mock to list
        listOfMetaMetaCollections.add(mockMetaCollections);
    }

    private void createListofMetaCollectionSetsForTest() {
        // Create mock Meta_databases
        final Meta_collection_sets mockMetaCollectionSets = context.mock(Meta_collection_sets.class);

        context.checking(new Expectations() {

            {
                allowing(mockMetaCollectionSets).setModifiedColumns(with(any(Set.class)));
                allowing(mockMetaCollectionSets).setNewItem(with(any(boolean.class)));
                allowing(mockMetaCollectionSets).setOriginal(with(any(Meta_collection_sets.class)));
            }
        });

        listOfMetaMetaCollectionSets = new ArrayList<Meta_collection_sets>();
        // Add mock to list
        listOfMetaMetaCollectionSets.add(mockMetaCollectionSets);
    }

    private void createMockRockFactoryExpectations() throws Exception, Exception {
        final RockResultSet mockRockResultSet = context.mock(RockResultSet.class);

        context.checking(new Expectations() {

            {
                allowing(mockRockResultSet);
                one(mockRockFactory).setSelectSQL(with(any(boolean.class)), with(any(Meta_collections.class)));
                will(returnValue(mockRockResultSet));
                one(mockRockFactory).getData(with(any(Meta_collections.class)), with(any(RockResultSet.class)));
                will(returnValue(listOfMetaMetaCollections.iterator()));
                one(mockRockFactory).setSelectSQL(with(any(boolean.class)), with(any(Meta_collection_sets.class)));
                will(returnValue(mockRockResultSet));
                one(mockRockFactory).getData(with(any(Meta_collection_sets.class)), with(any(RockResultSet.class)));
                will(returnValue(listOfMetaMetaCollectionSets.iterator()));
            }
        });
    }

    private void createMockMetaTransferActions() {
        mockMetaTransferActions = context.mock(Meta_transfer_actions.class);

        context.checking(new Expectations() {

            {
                allowing(mockMetaTransferActions).clone();
                will(returnValue(mockMetaTransferActions));
                allowing(mockMetaTransferActions).getAction_contents();
                will(returnValue("MockActionContents"));
                allowing(mockMetaTransferActions).getTransfer_action_name();
                will(returnValue("MockTransferActionName"));
                allowing(mockMetaTransferActions).getAction_type();
                will(returnValue("MockActionType"));
                allowing(mockMetaTransferActions).getRockFactory();
                will(returnValue(mockRockFactory));
                allowing(mockMetaTransferActions).getWhere_clause();
                will(returnValue("timelevels=1"));
                allowing(mockMetaTransferActions).getCollection_id();
                will(returnValue(1L));
                allowing(mockMetaTransferActions).getCollection_set_id();
                will(returnValue(1L));
            }
        });
    }
}
