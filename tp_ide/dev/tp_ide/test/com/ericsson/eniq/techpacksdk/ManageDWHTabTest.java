package com.ericsson.eniq.techpacksdk;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easymock.classextension.EasyMock;
import org.hsqldb.util.DatabaseManagerSwing;
import org.jdesktop.application.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.etl.rock.Meta_collections;
import com.distocraft.dc5000.etl.rock.Meta_collectionsFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DWHTreeDataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.Engine;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;

public class ManageDWHTabTest{

  private static Method setActiveStateOfButtonsMethod;
  private static Method getPredecessorTPActivationMethod;
  
  private static Field selectedNodeField;
  private static Field activateEnabledField;
  private static Field deActivateEnabledField;
  private static Field upgradeEnabledField;
  private static Field activateBHEnabledField;
  
  private final static String _createStatementMetaFile = "TableCreateStatements.sql";
  private final static String _viewStatementMetaFile = "ViewCreateStatements.sql";
  private static final String TESTDB_DRIVER = "org.hsqldb.jdbcDriver";
  private static final String DWHREP_URL = "jdbc:hsqldb:mem:dwhrep";    
  //    private static final String TESTDB_DRIVER = "org.h2.Driver"; // need to user H2 as hsqldb wont let you create a view in schemaA based on a view in schemaB?!!
  //    private static final String DWHREP_URL = "jdbc:h2:mem:dwhrep;TRACE_LEVEL_SYSTEM_OUT=0"; // 0|OFF, 1|ERROR, 2|INFO, 3|DEBUG
  private RockFactory testDb = null;

  
  private static DataTreeNode mockedDataTreeNode;
  private static DataModelController mockedController;
  
  @Test
  public void testDoesHaveCountTableLevel() throws Exception {
      final String typeName = "DC_E_MGW_ATMPORT";//DC_E_MGW_ATMPORT HAS a COUNT tableLevel
      final DataModelController mockedController = createNiceMock(DataModelController.class);
      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
      final boolean actual = testInstance.doesHaveCountTableLevel(typeName, testDb);
      assertTrue("Should have found a COUNT table for MeasurementType: "+typeName, actual);
  } // testDoesHaveCountTableLevel
  
  @Test
  public void testDoesHaveCountTableLevel_No() throws Exception {
      final String typeName = "DC_E_MGW_ATMPORTBH";//DC_E_MGW_ATMPORTBH does not have a COUNT tableLevel
      final DataModelController mockedController = createNiceMock(DataModelController.class);
      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
      final boolean actual = testInstance.doesHaveCountTableLevel(typeName, testDb);
      assertFalse("Should NOT have found a COUNT table for MeasurementType: "+typeName, actual);
  } // testDoesHaveCountTableLevel_No
  
  @Test
  public void testRemovePartitionsAndViewDefs() throws Exception {
	  final String typeName = "DC_E_MGW_ATMPORTBH"; //DC_E_MGW_ATMPORTBH does not have a COUNT tableLevel
      final DataModelController mockedController = createNiceMock(DataModelController.class);
      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
      boolean actual = false;
      final String COUNT_TABLELEVEL = "COUNT";
      final String RAW_TABLELEVEL = "RAW";
      Dwhtype dwhtypetmp = new Dwhtype(testDb);
      dwhtypetmp.setTypename(typeName);
      dwhtypetmp.setTablelevel("RANKBH");
      // See if there is a count level table in dwhtype
      if( (testInstance.doesHaveCountTableLevel(typeName, testDb) && RAW_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel())) 
      		|| COUNT_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel()) ) {       
    	  actual = true;
      }
      assertFalse("Should NOT have found a COUNT table for MeasurementType: "+typeName, actual);
      final String typeName2 = "DC_E_MGW_ATMPORT";//DC_E_MGW_ATMPORT HAS a COUNT tableLevel
      dwhtypetmp.setTablelevel(RAW_TABLELEVEL);
      // See if there is a count level table in dwhtype
      if( (testInstance.doesHaveCountTableLevel(typeName2, testDb) && RAW_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel())) 
      		|| COUNT_TABLELEVEL.equalsIgnoreCase(dwhtypetmp.getTablelevel()) ) {       
    	  actual = true;
      }
      assertTrue("Should have found a COUNT table for MeasurementType: "+typeName, actual);
  } // testDoesHaveCountTableLevel
  
	@Test
	public void testreactivateViews_PlaceholderModified() throws Exception {
		final String tpName = "DC_E_MGW";
		final String versionId = tpName + ":((802))";
		ResultSet rs = null;
		int reactivateviews = 0;
		final boolean isBusyhourModified = false;
		List<BusyHourData> bhdList = new Vector<BusyHourData>();

		final Versioning mockedVersion = createNiceMock(Versioning.class);
		mockedVersion.setTechpack_name(tpName);
		mockedVersion.getVersionid();
		expectLastCall().andReturn(versionId);
		expectLastCall().anyTimes();
		replay(mockedVersion);
		final Busyhour mockedBusyhour = createNiceMock(Busyhour.class);
		mockedBusyhour.getReactivateviews();
		expectLastCall().andReturn(reactivateviews);
		expectLastCall().anyTimes();
		mockedBusyhour.isModified();
		expectLastCall().andReturn(isBusyhourModified);
		expectLastCall().anyTimes();
		replay(mockedBusyhour);

		final BusyhourHandlingDataModel mockedBusyhourHandlingDataModel = createNiceMock(BusyhourHandlingDataModel.class);
		mockedBusyhourHandlingDataModel.getCurrentVersioning();
		expectLastCall().andReturn(mockedVersion);
		mockedBusyhourHandlingDataModel.getBusyHourData(versionId);
		expectLastCall().andReturn(bhdList);
		expectLastCall().anyTimes();
		replay(mockedBusyhourHandlingDataModel);

		final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
		mockedController = createNiceMock(DataModelController.class);
		mockedController.getDWHTreeDataModel();
		expectLastCall().andReturn(mockedDWHTreeDataModel);
		mockedController.getRockFactory();
		expectLastCall().andReturn(testDb);
		expectLastCall().anyTimes();
		mockedController.getBusyhourHandlingDataModel();
		expectLastCall().andReturn(mockedBusyhourHandlingDataModel);
		expectLastCall().anyTimes();
		replay(mockedController);

		final BusyHourData busyHourData = new BusyHourData(mockedVersion,
				mockedController, false);
		Busyhour dataModelBusyhour = new Busyhour(testDb);
		dataModelBusyhour.setVersionid(versionId);
		dataModelBusyhour.setBhtype("PP0");
		dataModelBusyhour.setBhlevel("DC_E_MGW_ATMPORTBH");
		dataModelBusyhour.setTargetversionid(versionId);
		dataModelBusyhour.setReactivateviews(1);
		busyHourData.setBusyhour(dataModelBusyhour);
		bhdList.add(busyHourData);

		final ManageDWHTab testInstance = new ManageDWHTab(mockedController);

		final Statement stmt = testDb.getConnection().createStatement();
		String updateString = "update BusyHour set REACTIVATEVIEWS=1 where VERSIONID='DC_E_MGW:((802))' and BHLEVEL='DC_E_MGW_ATMPORTBH' and BHTYPE='PP0'";
		try {
			stmt.executeUpdate(updateString);
		} catch (SQLException se) {
			System.out.println("SQLexception");
		}
		
		// run method under test
		testInstance.checkforReacivateViews(versionId);

		// Result should be row value reset to 0 in dB
		String selectString = "select * from BusyHour  where VERSIONID='DC_E_MGW:((802))' and BHLEVEL='DC_E_MGW_ATMPORTBH' and BHTYPE='PP0'";
		int actual = -1;
		try {
			rs = stmt.executeQuery(selectString);
			while (rs.next()) {
				actual = rs.getInt("REACTIVATEVIEWS"); // Expect only 1 row
			}
		} catch (SQLException se) {
			System.out.println("SQLexception");
		}

		assertEquals("Changed data, do not want to REACTIVATEVIEWS", 0, actual);

	} // testreactivateViews_PlaceholderModified
  

	// Disabling this test, Will correct it afetrwards
	@SuppressWarnings("serial")
	@Ignore
  @Test
  public void testRemoveDWH() throws Exception {

    final DataModelController mockedController = createNiceMock(DataModelController.class);
    final RockFactory mDwhdb = createNiceMock(RockFactory.class);
    final Connection c = createNiceMock(Connection.class);
    final Statement s = createMock(Statement.class);

    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    mockedController.getEtlRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    mockedController.getDbaDwhRockFactory();
    expectLastCall().andReturn(mDwhdb);
    expectLastCall().anyTimes();
    mockedController.getDwhRockFactory();
    expectLastCall().andReturn(mDwhdb);
    expectLastCall().anyTimes();
    mDwhdb.getConnection();
    expectLastCall().andReturn(c);
    expectLastCall().anyTimes();
    c.createStatement();
    expectLastCall().andReturn(s);
    expectLastCall().anyTimes();

    final String tpName = "DC_E_MGW";
    final String versionId = tpName+":((802))";
    final String measType = "ATMPORT";

    mockDropTableCall(s, "DIM_E_MGW_ELEMBH_BHTYPE", 1);
    mockDropTableCall(s, "DIM_E_MGW_ATMPORTBH_BHTYPE", 1);
    mockDropViewCall(s, "dcpublic.DIM_E_MGW_ATMPORTBH_BHTYPE");
    mockDropTableCall(s, "DC_E_MGW_ELEMBH_RANKBH", 6);
    mockDropViewCall(s, tpName+"_ELEMBH_RANKBH");
    mockDropTableCall(s, "DC_E_MGW_ATMPORTBH_RANKBH", 6);
    mockDropViewCall(s, "DC_E_MGW_ATMPORTBH_RANKBH");
    mockDropViewCall(s, "dcpublic.DC_E_MGW_ATMPORTBH_RANKBH");
    
    mockDropTableCall(s, "DC_E_MGW_ATMPORT_DAY", 6);
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_DAY");
    mockDropViewCall(s, "dcpublic.DC_E_MGW_ATMPORT_DAY");
    mockDropTableCall(s, "DC_E_MGW_ATMPORT_DAYBH", 6);
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_DAYBH");
    mockDropViewCall(s, "dcpublic.DC_E_MGW_ATMPORT_DAYBH");
    
    mockDropTableCall(s, "DC_E_MGW_ATMPORT_DAYBH_CALC", 1);
    mockDropTableCall(s, "DC_E_MGW_ATMPORT_RAW", 6);
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_RAW_DISTINCT_DATES");
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_RAW");
    mockDropViewCall(s, "dcpublic.DC_E_MGW_ATMPORT_RAW");
    mockDropTableCall(s, "DC_E_MGW_ATMPORT_COUNT", 6);
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_DELTA");
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_COUNT_DISTINCT_DATES");
    mockDropViewCall(s, "DC_E_MGW_ATMPORT_COUNT");
    mockDropViewCall(s, "dcpublic.DC_E_MGW_ATMPORT_COUNT");
    
    mockDropPlaceHolders(s, "DC_E_MGW_ELEMBH_RANKBH_ELEM", 5);
    mockDropPlaceHolders(s, tpName + "_"+measType+"BH_RANKBH_"+measType, 5);
    s.close();
    expectLastCall().once();
    replay(s);
    replay(c);
    replay(mockedController);
    replay(mDwhdb);

    final Versioning mockedVersion = createNiceMock(Versioning.class);
    mockedVersion.getTechpack_name();
    expectLastCall().andReturn(tpName);
    mockedVersion.getVersionid();
    expectLastCall().andReturn(versionId);
    replay(mockedVersion);
    final DataTreeNode sNode = createNiceMock(DataTreeNode.class);
    sNode.getRockDBObject();
    expectLastCall().andReturn(mockedVersion);
    expectLastCall().anyTimes();
    replay(sNode);
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController){
      @Override
      public void removeDWH() throws Exception {
        super.removeDWH();
      }

      @Override
      public void setSelectedNode(DataTreeNode sNode) {
        try {
          selectedNodeField.set(this, sNode);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    };
    testInstance.setSelectedNode(sNode);
    try{
      testInstance.removeDWH();

    } catch (Throwable t){
      t.printStackTrace();
    }
    verify(s);
  }

  /**
   * The TP is:
   * Active -> TRUE
   * Modified -> FALSE
   * 
   * The Placeholder:
   * Needs Activation (Reactivateviews = 0)-> FALSE
   */
  @Test
  public void testActiveTP_NotModified_PlaceholderNotModified(){
    final String tpName = "DC_E_MGW";
    final String versionId = tpName+":((802))";

    final int reactivateviews = 0;
    final int tpModified = 0;
    final boolean tpActive = true;
    final boolean isBusyhourModified = false;
    
    SetActivateStateOfButtonsTestSetup(tpName, versionId, reactivateviews, tpModified, isBusyhourModified, tpActive);
 

    try{
      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);

      selectedNodeField.set(testInstance, mockedDataTreeNode);
      setActiveStateOfButtonsMethod.invoke(testInstance, null);
      
      
      assertFalse("Activate..., should NOT be Enabled",
          (Boolean)activateEnabledField.get(testInstance));
      
      assertTrue("Deactive..., should be Enabled",
          (Boolean)deActivateEnabledField.get(testInstance));
      
      assertFalse("Upgrade..., should NOT be Enabled",
          (Boolean)upgradeEnabledField.get(testInstance));
      
      assertFalse("Activate Busy Hour Criteria..., should not be Enabled",
          (Boolean)activateBHEnabledField.get(testInstance));


    }catch (IllegalAccessException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    } catch (IllegalArgumentException e) {
      fail("The Method: setActiveStateOfButtons, has changed in some way");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      fail("Couldn't access the Method: setActiveStateOfButtons");
    }
  }
 
  /**
   */
  @Test
  public void testActiveTP_BHCriteriaModified_PlaceholderModified(){
    final String tpName = "DC_E_MGW";
    final String versionId = tpName+":((802))";

    final int reactivateviews = 1;
    final int tpModified = 1;
    final boolean tpActive = true;
    final boolean isBusyhourModified = false;
    
    SetActivateStateOfButtonsTestSetup(tpName, versionId, reactivateviews, tpModified, isBusyhourModified, tpActive);
    
    try{
      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);

      selectedNodeField.set(testInstance, mockedDataTreeNode);
      setActiveStateOfButtonsMethod.invoke(testInstance, null);
      
      
      assertFalse("Activate..., should NOT be Enabled",
          (Boolean)activateEnabledField.get(testInstance));
      
      assertTrue("Deactive..., should be Enabled",
          (Boolean)deActivateEnabledField.get(testInstance));
      
      assertFalse("Upgrade..., should NOT be Enabled",
          (Boolean)upgradeEnabledField.get(testInstance));
      
      assertTrue("Activate Busy Hour Criteria..., should be Enabled",
          (Boolean)activateBHEnabledField.get(testInstance));


    }catch (IllegalAccessException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    } catch (IllegalArgumentException e) {
      fail("The Method: setActiveStateOfButtons, has changed in some way");
    } catch (InvocationTargetException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    }
  }

  /**
   */
  @Test
  public void testActiveTP_OtherModified_PlaceholderModified(){
    final String tpName = "DC_E_MGW";
    final String versionId = tpName+":((802))";
    
    final int reactivateviews = 1;
    final int tpModified = 2;
    final boolean tpActive = true;
    final boolean isBusyhourModified = false;
    
    SetActivateStateOfButtonsTestSetup(tpName, versionId, reactivateviews, tpModified, isBusyhourModified, tpActive);
      
      
      try {

      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);

      selectedNodeField.set(testInstance, mockedDataTreeNode);
      setActiveStateOfButtonsMethod.invoke(testInstance, null);
      
      
      assertFalse("Activate..., should NOT be Enabled",
          (Boolean)activateEnabledField.get(testInstance));
      
      assertTrue("Deactive..., should be Enabled",
          (Boolean)deActivateEnabledField.get(testInstance));
      
      assertFalse("Upgrade..., should NOT be Enabled",
          (Boolean)upgradeEnabledField.get(testInstance));
      
      assertFalse("Activate Busy Hour Criteria..., should NOT be Enabled",
          (Boolean)activateBHEnabledField.get(testInstance));


    }catch (IllegalAccessException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    } catch (IllegalArgumentException e) {
      fail("The Method: setActiveStateOfButtons, has changed in some way");
    } catch (InvocationTargetException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    }
  }

  private void SetActivateStateOfButtonsTestSetup(final String tpName, final String versionId, final int reactivateviews,
      final int tpModified, final boolean isBusyhourModified, final boolean tpActive) {
    List<BusyHourData> bhdList = new Vector<BusyHourData>();

    //Mock the Busyhour: Reactivateviews=reactivateviews, isModified=isBusyhourModified
    final Busyhour mockedBusyhour = createNiceMock(Busyhour.class);
    mockedBusyhour.getReactivateviews();
    expectLastCall().andReturn(reactivateviews);
    expectLastCall().anyTimes();
    mockedBusyhour.isModified();
    expectLastCall().andReturn(isBusyhourModified);
    expectLastCall().anyTimes();
    replay(mockedBusyhour);

    final Versioning mockedVersion1 = createNiceMock(Versioning.class);
    mockedVersion1.setTechpack_name(tpName);

    //Mock BusyHourData: Busyhour = mockedBusyhour
    final BusyHourData mockedBusyHourData = createNiceMock(BusyHourData.class);
    mockedBusyHourData.getBusyhour();
    expectLastCall().andReturn(mockedBusyhour);
    expectLastCall().anyTimes();
    replay(mockedBusyHourData);

    bhdList.add(mockedBusyHourData);

    final BusyhourHandlingDataModel mockedBusyhourHandlingDataModel = createNiceMock(BusyhourHandlingDataModel.class);
    mockedBusyhourHandlingDataModel.getCurrentVersioning();
    expectLastCall().andReturn(mockedVersion1);
    mockedBusyhourHandlingDataModel.getBusyHourData(versionId);
    expectLastCall().andReturn(bhdList);
    expectLastCall().anyTimes();
    replay(mockedBusyhourHandlingDataModel);

    //Mock Tpactivation: MODIFIED=tpModified
    final Tpactivation mockedTpactivation = createNiceMock(Tpactivation.class);
    mockedTpactivation.getModified();
    expectLastCall().andReturn(tpModified);
    replay(mockedTpactivation);

    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
    mockedDWHTreeDataModel.getActiveVersion(tpName);
    expectLastCall().andReturn(mockedTpactivation);
    replay(mockedDWHTreeDataModel);

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getBusyhourHandlingDataModel();
    expectLastCall().andReturn(mockedBusyhourHandlingDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    replay(mockedController);



    final Versioning mockedVersion = createNiceMock(Versioning.class);
    mockedVersion.getTechpack_name();
    expectLastCall().andReturn(tpName);
    mockedVersion.getVersionid();
    expectLastCall().andReturn(versionId);
    expectLastCall().anyTimes();
    replay(mockedVersion);

    //Mock DataTreeNode: isActive=true
    mockedDataTreeNode = createNiceMock(DataTreeNode.class);
    mockedDataTreeNode.getRockDBObject();
    expectLastCall().andReturn(mockedVersion);
    expectLastCall().anyTimes();
    mockedDataTreeNode.isActive();
    expectLastCall().andReturn(tpActive);
    expectLastCall().anyTimes();
    replay(mockedDataTreeNode);
  }

  @Test
  public void testSelectedActiveNodePlaceholderModified(){
    
    //DC_E_MGW_ATMPORTBH', 'PP0
    
    final String tpName = "DC_E_MGW";
    final String versionId = tpName+":((803))";
    
    final DataModelController mockedController = createNiceMock(DataModelController.class);

    try {

      final Versioning mockedVersion = createNiceMock(Versioning.class);
      mockedVersion.getTechpack_name();
      expectLastCall().andReturn(tpName);
      mockedVersion.getVersionid();
      expectLastCall().andReturn(versionId);
      replay(mockedVersion);

      final DataTreeNode sNode = createNiceMock(DataTreeNode.class);
      sNode.getRockDBObject();
      expectLastCall().andReturn(mockedVersion);
      expectLastCall().anyTimes();
      replay(sNode);

      final ManageDWHTab testInstance = new ManageDWHTab(mockedController);

      selectedNodeField.set(testInstance, sNode);
      setActiveStateOfButtonsMethod.invoke(testInstance, null);
      
      
      assertTrue("Activate..., should be Enabled",
          (Boolean)activateEnabledField.get(testInstance));
      
      assertFalse("Deactive..., should not be Enabled",
          (Boolean)deActivateEnabledField.get(testInstance));
      
      assertTrue("Upgrade..., should be Enabled",
          (Boolean)upgradeEnabledField.get(testInstance));
      
      assertFalse("Activate Busy Hour Criteria..., should not be Enabled",
          (Boolean)activateBHEnabledField.get(testInstance));


    }catch (IllegalAccessException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    } catch (IllegalArgumentException e) {
      fail("The Method: setActiveStateOfButtons, has changed in some way");
    } catch (InvocationTargetException e) {
      fail("Couldn't access the Method: setActiveStateOfButtons");
    }
  }
  
  @Test
  public void testGetPredecessorTPActivation() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    getPredecessorTPActivationMethod = ManageDWHTab.class.getDeclaredMethod("getPredecessorTPActivation", String.class);
    getPredecessorTPActivationMethod.setAccessible(true);

    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    replay(mockedController);

    
    final String techpackName = "DC_E_MGW";
    
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
    Object tpActivation = getPredecessorTPActivationMethod.invoke(testInstance, techpackName);
    assertNotNull(tpActivation);
    assertTrue(tpActivation instanceof Tpactivation);
  }
 
  @Test
  public void testGetPredecessorTPActivationDoesntExist() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    getPredecessorTPActivationMethod = ManageDWHTab.class.getDeclaredMethod("getPredecessorTPActivation", String.class);
    getPredecessorTPActivationMethod.setAccessible(true);

    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    replay(mockedController);

    
    final String techpackName = "DC_E_MGW2";
    
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
    Object tpActivation = getPredecessorTPActivationMethod.invoke(testInstance, techpackName);
    assertNull(tpActivation);
  }
  
  /**
   * Tests case where running directory checker sets for Alarm Interfaces tech pack runs ok.
   * @throws RemoteException
   */
  @Test
  public void testRunAlarmIntfDirCheckers() throws RemoteException {
    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
    final String techpackName = Constants.ALARM_INTERFACES_TECHPACK_NAME;

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
        
    Meta_collectionsFactory mcfMock = createNiceMock(Meta_collectionsFactory.class);
    Vector vectorMock = createNiceMock(Vector.class);
    Iterator<Meta_collections> iteratorMock = createNiceMock(Iterator.class);
    Meta_collections metaCollactionsMock = createNiceMock(Meta_collections.class);
    Engine mockEngine = createNiceMock(Engine.class);
    
    mcfMock.get();
    expectLastCall().andReturn(vectorMock);
    replay(mcfMock);

    vectorMock.iterator();
    expectLastCall().andReturn(iteratorMock);
    replay(vectorMock);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.next();
    expectLastCall().andReturn(metaCollactionsMock);
    replay(iteratorMock);
    
    metaCollactionsMock.getCollection_name();
    expectLastCall().andReturn("Directory_Checker_test");
    replay(metaCollactionsMock);
    
    mockedController.getEngine();
    expectLastCall().andReturn(mockEngine);
    replay(mockedController);
    
    mockEngine.rundirectoryCheckerSetForAlarmInterfaces(techpackName, "Directory_Checker_test");
    expectLastCall();
    replay(mockEngine);
        
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);    
    final boolean ranSuccessfully = testInstance.runAlarmDirCheckers(techpackName, mcfMock);
    
    verify(mockEngine);
    verify(mcfMock);
    verify(vectorMock);
    verify(iteratorMock);
    verify(metaCollactionsMock);
    verify(mockedController);
    
    assertTrue("runAlarmIntfDirCheckers() should return true if it runs successfully", ranSuccessfully);
  }
  
  /**
   * Tests case where running directory checker sets for Alarm Interfaces tech pack fails
   * because no Meta_collections sets are found for the tech pack.
   * @throws RemoteException
   */
  @Test
  public void testRunAlarmIntfDirCheckersWithFail() throws RemoteException {
    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
    final String techpackName = Constants.ALARM_INTERFACES_TECHPACK_NAME;

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    replay(mockedController);    
        
    Meta_collectionsFactory mcfMock = createNiceMock(Meta_collectionsFactory.class);
    Vector vectorMock = createNiceMock(Vector.class);
    Iterator<Meta_collections> iteratorMock = createNiceMock(Iterator.class);
    
    mcfMock.get();
    expectLastCall().andReturn(vectorMock);
    replay(mcfMock);

    vectorMock.iterator();
    expectLastCall().andReturn(iteratorMock);
    replay(vectorMock);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(false);
    replay(iteratorMock);    
        
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);    
    final boolean ranSuccessfully = testInstance.runAlarmDirCheckers(techpackName, mcfMock);
    
    verify(mcfMock);
    verify(vectorMock);
    verify(iteratorMock);
    verify(mockedController);
    
    assertFalse("runAlarmIntfDirCheckers() should return false if no sets are found", ranSuccessfully);
  }
  
  /**
   * Tests case where running directory checker sets for Alarm Interfaces tech pack fails
   * because no Meta_collections sets are found, but they are not directory checkers.
   * @throws RemoteException
   */
  @Test
  public void testRunAlarmIntfDirCheckersNoAlarmSets() throws RemoteException {
    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
    final Application mockedApplication = createNiceMock(Application.class);
    final String techpackName = Constants.ALARM_INTERFACES_TECHPACK_NAME;

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
    replay(mockedController);
        
    Meta_collectionsFactory mcfMock = createNiceMock(Meta_collectionsFactory.class);
    Vector vectorMock = createNiceMock(Vector.class);
    Iterator<Meta_collections> iteratorMock = createNiceMock(Iterator.class);
    Meta_collections metaCollactionsMock = createNiceMock(Meta_collections.class);
    
    mcfMock.get();
    expectLastCall().andReturn(vectorMock);
    replay(mcfMock);

    vectorMock.iterator();
    expectLastCall().andReturn(iteratorMock);
    replay(vectorMock);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.next();
    expectLastCall().andReturn(metaCollactionsMock);
    replay(iteratorMock);
    
    metaCollactionsMock.getCollection_name();
    expectLastCall().andReturn("incorrect name");
    replay(metaCollactionsMock);       
        
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);    
    final boolean ranSuccessfully = testInstance.runAlarmDirCheckers(techpackName, mcfMock);
    
    verify(mcfMock);
    verify(vectorMock);
    verify(iteratorMock);
    verify(metaCollactionsMock);
    verify(mockedController);
    
    assertFalse("runAlarmIntfDirCheckers() should return false if no set names match", ranSuccessfully);
  }
  
  /**
   * Tests case where running directory checker sets for Alarm Interfaces tech pack fails
   * because an exception is thrown by the Engine.
   * @throws RemoteException
   */
  @Test
  public void testRunAlarmIntfDirCheckersEngineException() throws RemoteException {
    final DWHTreeDataModel mockedDWHTreeDataModel = createNiceMock(DWHTreeDataModel.class);
    final String techpackName = Constants.ALARM_INTERFACES_TECHPACK_NAME;

    mockedController = createNiceMock(DataModelController.class);
    mockedController.getDWHTreeDataModel();
    expectLastCall().andReturn(mockedDWHTreeDataModel);
    mockedController.getRockFactory();
    expectLastCall().andReturn(testDb);
    expectLastCall().anyTimes();
        
    Meta_collectionsFactory mcfMock = createNiceMock(Meta_collectionsFactory.class);
    Vector vectorMock = createNiceMock(Vector.class);
    Iterator<Meta_collections> iteratorMock = createNiceMock(Iterator.class);
    Meta_collections metaCollactionsMock = createNiceMock(Meta_collections.class);
    Engine mockEngine = createNiceMock(Engine.class);
    
    mcfMock.get();
    expectLastCall().andReturn(vectorMock);
    replay(mcfMock);

    vectorMock.iterator();
    expectLastCall().andReturn(iteratorMock);
    replay(vectorMock);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.hasNext();
    expectLastCall().andReturn(true);
    
    iteratorMock.next();
    expectLastCall().andReturn(metaCollactionsMock);
    replay(iteratorMock);
    
    metaCollactionsMock.getCollection_name();
    expectLastCall().andReturn("Directory_Checker_test");
    replay(metaCollactionsMock);
    
    mockedController.getEngine();
    expectLastCall().andReturn(mockEngine);
    replay(mockedController);
    
    mockEngine.rundirectoryCheckerSetForAlarmInterfaces(techpackName, "Directory_Checker_test");
    expectLastCall().andThrow(new RuntimeException("Engine error!"));
    replay(mockEngine);
        
    final ManageDWHTab testInstance = new ManageDWHTab(mockedController);    
    final boolean ranSuccessfully = testInstance.runAlarmDirCheckers(techpackName, mcfMock);
    
    verify(mockEngine);
    verify(mcfMock);
    verify(vectorMock);
    verify(iteratorMock);
    verify(metaCollactionsMock);
    verify(mockedController);
    
    assertFalse("runAlarmIntfDirCheckers() should return false if there is an engine error", ranSuccessfully);
  }
  
  /**
   * Tests saveTypeActivationData where there is a ReferenceTable with an UpdatePolicy=4(HistoryDynamic). 
   * // eeoidiv,20110926:Automatically create _CALC and _HIST_RAW tables for update policy=4=HistoryDynamic (like _CURRENT_DC).
   * This test only interested in ReferenceTable part near end of saveTypeActivationData method.
   * @throws RemoteException
   */
  @Test
  public void testSaveTypeActivationData() throws RemoteException {;  
	  final DataModelController mockedController = createNiceMock(DataModelController.class);
	  final ManageDWHTab testInstance = new ManageDWHTab(mockedController);
	  boolean newActivation = true;
	  // Mock Tpactivation
	  final String techpackName = "TEST_SONV_CM";
	  final String versionId = techpackName + ":((4))";
	  final String status = "ACTIVE";
	  final Tpactivation mockedTpactivation = createNiceMock(Tpactivation.class);
	  	mockedTpactivation.getVersionid();
	  	expectLastCall().andReturn(versionId);
	  	expectLastCall().anyTimes();
	  	mockedTpactivation.getRockFactory();
	  	expectLastCall().andReturn(testDb);
	  	expectLastCall().anyTimes();
	  	mockedTpactivation.getTechpack_name();
	  	expectLastCall().andReturn(techpackName);
	  	expectLastCall().anyTimes();
	  	mockedTpactivation.getStatus();
	  	expectLastCall().andReturn(status);
	  	expectLastCall().anyTimes();
	  	replay(mockedTpactivation);
	  //Test
	  testInstance.saveTypeActivationData(newActivation, mockedTpactivation);
	  //Check if TypeActivation table has been updated as expected.
	  verifyTypeActivation("DC_E_LTE_SONV_CM_CELL_GIS_CALC");
	  verifyTypeActivation("DC_E_LTE_SONV_CM_CELL_GIS_HIST_RAW");
  }//testSaveTypeActivationData

private void verifyTypeActivation(final String typeName) {
	// Result should be 1 row with TypeName value being the ReferenceTable name 'DC_E_LTE_SONV_CM_CELL_GIS' ending with "_CALC";
	  final String selectString = "SELECT * FROM TypeActivation WHERE TYPENAME='"+typeName+"'";
	  String actual = null;
	  ResultSet rs = null;
	  try {
		  final Statement stmt = testDb.getConnection().createStatement();
		  rs = stmt.executeQuery(selectString);
		  while (rs.next()) {
			  actual = rs.getString("TYPENAME");
		  }
	  } catch (SQLException se) {
		  actual = null;
	  }
	  assertEquals("Changed data, do not want to REACTIVATEVIEWS", typeName, actual);
} //verifyTypeActivation

//  private void assertTableDropped(RockFactory rockFactory, String tableName) throws Exception {
//    String sql = "SELECT * FROM "+tableName;
//    String actual = null;
//    Statement st  = null;
//    ResultSet rs  = null;
//    try {
//      st = rockFactory.getConnection().createStatement();
//      rs = st.executeQuery(sql);
//
//      while (rs.next()) {
//        actual = actual + rs.getString(1);
//      }
//    } catch(SQLException e){
//      actual = e.getMessage();
//    }finally {
//      try {
//        if(rs != null){
//          rs.close();            
//        }
//        st.close();
//      } catch(SQLException e) {
//        actual = e.getMessage();
//      }
//    }
//    String expected = "Table not found in statement [SELECT * FROM "+tableName+"]";
//    assertEquals("Table "+tableName+ " Exists!", expected, actual);
//  }
//
//  private void assertTableNotDropped(RockFactory rockFactory, String tableName) throws Exception {
//    String sql = "SELECT * FROM "+tableName;
//    String actual = null;
//    Statement st  = null;
//    ResultSet rs  = null;
//    try {
//      st = rockFactory.getConnection().createStatement();
//      rs = st.executeQuery(sql);
//
//      while (rs.next()) {
//        actual = actual + rs.getString(1);
//      }
//    } catch(SQLException e){
//      actual = e.getMessage();
//    }finally {
//      try {
//        if(rs != null){
//          rs.close();            
//        }
//        st.close();
//      } catch(SQLException e) {
//        actual = e.getMessage();
//      }
//    }
//    String unexpected = "Table not found in statement [SELECT * FROM "+tableName+"]";
//
//    assertNotSame("Table "+tableName+" was dropped!", unexpected, actual);
//  }

  private void mockDropPlaceHolders(final Statement s, final String tName, final int pCount) throws Exception {
    for(int i=0;i<pCount;i++){
      s.execute(EasyMock.matches("DROP VIEW "+tName+"_CP"+i));
      expectLastCall().andReturn(true);
    }
    for(int i=0;i<pCount;i++){
      s.execute(EasyMock.matches("DROP VIEW "+tName+"_PP"+i));
      expectLastCall().andReturn(true);
    }
  }

  private void mockDropViewCall(final Statement s, final String view) throws Exception {
    s.execute(EasyMock.matches("Drop view "+view));
    expectLastCall().andReturn(true);
  }

  private void mockDropTableCall(final Statement s, final String tName, final int pCount) throws Exception {
    if(pCount == 1){
      s.execute(EasyMock.matches("Drop table "+tName));
      expectLastCall().andReturn(true);
    } else {
      for(int i=1;i<=pCount;i++){
        s.execute(EasyMock.matches("Drop table "+tName+"_0"+i));
        expectLastCall().andReturn(true);
      }
    }
  }

  @Before
  public void setUp() throws Exception {
    selectedNodeField = ManageDWHTab.class.getDeclaredField("selectedNode");
    selectedNodeField.setAccessible(true);
    
    activateEnabledField = ManageDWHTab.class.getDeclaredField("activateEnabled");
    activateEnabledField.setAccessible(true);
    
    deActivateEnabledField = ManageDWHTab.class.getDeclaredField("deActivateEnabled");
    deActivateEnabledField.setAccessible(true);
    
    upgradeEnabledField = ManageDWHTab.class.getDeclaredField("upgradeEnabled");
    upgradeEnabledField.setAccessible(true);
    
    activateBHEnabledField = ManageDWHTab.class.getDeclaredField("activateBHEnabled");
    activateBHEnabledField.setAccessible(true);
    
    setActiveStateOfButtonsMethod = ManageDWHTab.class.getDeclaredMethod("setActiveStateOfButtons", null);
    setActiveStateOfButtonsMethod.setAccessible(true);

    testDb = new RockFactory(DWHREP_URL, "SA", "", TESTDB_DRIVER, "test", true);
    loadSetup(testDb, "removeDWH");
    
    // Disable logging:
    Logger.getLogger(ManageDWHTab.class.getName()).setLevel(Level.OFF);
  }

  @After
  public void tearDown() throws Exception {
    final Statement stmt = testDb.getConnection().createStatement();
    stmt.executeUpdate("SHUTDOWN");
    stmt.close();
    testDb.getConnection().close();
    testDb = null;
  }

  private void loadSetup(final RockFactory testDB, final String base) throws Exception {
  	String currentLocation = System.getProperty("user.dir");
  	final String sep = System.getProperty("file.separator");
  	final String dir = "setupSQL"+sep+base;
  	// HACK: To get test to run on Hudson CI
  	if(currentLocation.endsWith("ant_common")){
  		//remove stuff at end...
  		currentLocation = currentLocation.substring(0, currentLocation.length()-16);
  		currentLocation = currentLocation + "design" + sep + "plat" + sep + "tp_ide" + sep + "dev" + sep + "tp_ide";
  	}
    //Platform projects imported using single eclipseProject (e.g. want path to be E:\efergcl_view\eniq_tools\eclipseProject\eniq_design_plat_tp_ide\dev\tp_ide\test\setupSQL)
	if(currentLocation.endsWith("eclipseProject")){
		currentLocation = currentLocation + sep + "eniq_design_plat_tp_ide" + sep + "dev" + sep + "tp_ide";
	}
    final String baseDir = currentLocation + sep + "test" + sep + dir;
    final File loadFrom = new File(baseDir);
    final File[] toLoad = loadFrom.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".sql") && !name.equals(_createStatementMetaFile)
        &&!name.equals(_viewStatementMetaFile);
      }
    });
    final Statement stmt = testDB.getConnection().createStatement();
    try{
      stmt.executeUpdate("create schema dc AUTHORIZATION DBA; create schema dcpublic AUTHORIZATION DBA;");
      } finally {
      stmt.close();
    }
    final File createFile = new File(baseDir + "/" + _createStatementMetaFile);
    loadSqlFile(createFile, testDB);
    for (File loadFile : toLoad) {
      loadSqlFile(loadFile, testDB);
    }
//    startDatabaseManager();
  }

  private void startDatabaseManager() {
    String[] args = new String[5];
    args[0] = "--driver";
    args[1] = "org.hsqldb.jdbcDriver";
    args[2] = "--url";
    args[3] = DWHREP_URL;//"jdbc:hsqldb:mem:" + this.databaseName;
    args[4] = "--noexit";

    // Start the database manager application
    DatabaseManagerSwing.main(args);
    while(true){
      try{
        Thread.sleep(500);

      }catch (Exception e){
        continue;
      }
    }
  }

  private void loadSqlFile(final File sqlFile, final RockFactory testDB) throws IOException, SQLException, ClassNotFoundException {
    if (!sqlFile.exists()) {
      return;
    }
    BufferedReader br = new BufferedReader(new FileReader(sqlFile));
    String line;
    int lineCount = 0;
    try {
      while ((line = br.readLine()) != null) {
        lineCount++;
        line = line.trim();
        if (line.length() == 0 || line.startsWith("#")) {
          continue;
        }
        while (!line.endsWith(";")) {
          final String tmp = br.readLine();
          if (tmp != null) {
            line += "\r\n";
            line += tmp;
          } else {
            break;
          }
        }
        update(line, testDB);
      }
      testDB.commit();
    } catch (SQLException e) {
      throw new SQLException("Error executing on line [" + lineCount + "] of " + sqlFile, e);
    } finally {
      br.close();
    }
  }
  private void update(final String insertSQL, final RockFactory testDB) throws SQLException, ClassNotFoundException, IOException {
    final Statement s = testDB.getConnection().createStatement();
    try {
      s.executeUpdate(insertSQL);
    } catch (SQLException e) {
      if (e.getSQLState().equals("S0004")) {
        System.out.println("Views not supported yet: " + e.getMessage());
      } else if (e.getSQLState().equals("S0001") || e.getSQLState().equals("42504")) {
        //ignore, table already exists.......
      } else {
        throw e;
      }
    }
  }
}
