package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.apache.tools.ant.taskdefs.Sleep;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;
import tableTree.TTTableModel;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.techpacksdk.ActivateBHTask;
import com.ericsson.eniq.techpacksdk.ManageDWHTab;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.Engine;
import com.ericsson.eniq.techpacksdk.datamodel.Scheduler;


public class BusyhourAbstractPlaceholderTableModelTest extends TestCase {

    private final static String _createStatementMetaFile = "TableCreateStatements.sql";
    private static final String dbDriver = "org.hsqldb.jdbcDriver";
    private final String junitDbUrl = "jdbc:hsqldb:mem:test_placeholders";
    private RockFactory testDB = null;
    private final String versionId = "DC_E_BSS:((10))";
	private final String defaultTestBhLevel = "DC_E_BSS_BSCBH";
	private final String elemBhLevel = "DC_E_BSS_ELEMBH";
	private final String techpackTypeCustom = "CUSTOM";
	private final String techpackTypeOthers="PM";

	private int mockCount = 0;
	
	private TTTableModel testInstance = null;
    private DataModelController dmController = null;   
    private Application application =null;
    private BusyHourData bhdCustom= null;
    private BusyHourData bhdOthers= null;
    
    private SingleFrameApplication testApp = new SingleFrameApplication() {
		
		@Override
		protected void startup() {
			// TODO Auto-generated method stub
			
		}
	};
    
    protected Mockery context = new JUnit4Mockery();

    {
        // we need to mock classes, not just interfaces.
        context.setImposteriser(ClassImposteriser.INSTANCE);
    }

    private final ManageDWHTab manageDWHTabMock = context.mock(ManageDWHTab.class);
    private final DataTreeNode dataTreeNodeMock = context.mock(DataTreeNode.class);
    private final Versioning   versioningMock   = context.mock(Versioning.class);
	private boolean flagSet = false ;
    
//    private final ManageDWHTab tab = createNiceMock(ManageDWHTab.class);
//    private final DataTreeNode dtNode = createNiceMock(DataTreeNode.class);
//    private final Versioning version = createNiceMock(Versioning.class);

    public BusyhourAbstractPlaceholderTableModelTest() {
        super("BusyhourAbstractPlaceholderTableModelTest");
    }

    @Override
    public void tearDown() throws Exception {
//        reset(tab, dtNode, version);
        dmController = null;
        _shutdown_(testDB);
    }

    @Override
    protected void setUp() throws Exception {
        context.checking(new Expectations() {
            {

                allowing(manageDWHTabMock).getSelectedNode();
                will(returnValue(dataTreeNodeMock));
                
                allowing(manageDWHTabMock).setActivateBHEnabled(with(any(Boolean.class)));
                
                allowing(dataTreeNodeMock).getRockDBObject();
                will(returnValue(versioningMock));
                
                allowing(versioningMock).getVersionid();
                will(returnValue(versionId));
                
                allowing(versioningMock).getTechpack_name();
                will(returnValue("TechPackName"));
                
                //JUNIT FOR HP70451
                allowing(versioningMock).getTechpack_type();
                will(returnValue(techpackTypeCustom));
                
                allowing(versioningMock).getTechpack_type();
                will(returnValue(techpackTypeOthers));
            }
        });
      setupModels();
    }
    
    private void setupModels() throws Exception {
        testDB = new RockFactory(junitDbUrl, "SA", "", dbDriver, "con", true, -1);
        loadUnitDb(testDB, "setupSQL/bhPlaceholderModelData");
        String[] args = {};
    	testApp = new TestApplication();
        TestApplication.launch(TestApplication.class, args);
        Thread.sleep(2000);
        dmController = new DataModelController(
        		TestApplication.getInstance(), testDB, testDB, testDB, testDB, null, null, "", "", "", 0, "", ""){
            @Override
            protected Engine initEngine(String serverName, int rmiport, String engRef) {
                return null;
            }

            @Override
            protected Scheduler initScheduler(String serverName, int rmiport, String schRef) {
                return null;
            }
        };
        final Versioning where = new Versioning(testDB);
        where.setVersionid(versionId);
               
        final VersioningFactory fac = new VersioningFactory(testDB, where);
        final List<Versioning> versions = fac.get();
        assertFalse("No Versioning's found, setup failed ", versions.isEmpty());
        //this will create all dependant models.....
        initVelocity();
        
        new BusyhourTree(null, null, null, versions.get(0), dmController, true, null);
        testInstance = getTestInstance(defaultTestBhLevel);
        
       
    }

	
	

	private List<Busyhour> getBusyhourList(final String bhLevel, final PlaceholderType type, final int index) throws RockException, SQLException {
		final Busyhour where = new Busyhour(testDB);
		where.setPlaceholdertype(PlaceholderType.Product.toString());
		where.setBhlevel(bhLevel);
		where.setBhtype(type.toString()+index);
		final BusyhourFactory fac = new BusyhourFactory(testDB, where);
		return fac.get();
	}

   

    
	private TTTableModel getTestInstance(final String bhLevel){
		return getTestInstance(bhLevel, PlaceholderType.Product);
	}
	private TTTableModel getTestInstance(final String bhLevel, final PlaceholderType type){
		final TTTableModel ti = createModel(type);
		final BusyhourHandlingDataModel dModel = dmController.getBusyhourHandlingDataModel();
		final List<BusyHourData> data = dModel.getBusyHourData();
		final Vector<Object> vO = new Vector<Object>(data.size());
		for(BusyHourData bhd : data){
			if(bhd.getBusyhour().getBhlevel().equals(bhLevel)
				 && bhd.getBusyhour().getPlaceholdertype().equals(type.toString())){
					vO.add(bhd);
			}
		}
		ti.setData(vO);
		return ti;
	}
    public void testReactivateFlagResetOnActivate() throws Exception {
        final int COL = 3;
        testInstance.setValueAt("weeeeeeeeeee", 0, COL); //changes made to BHCriteria in PP0.
        testInstance.saveChanges();
        

			final String bhType = "PP0";

        final Busyhour whereCheck = getBusyhour(bhType, defaultTestBhLevel);
        assertEquals("Changed formula, views should be recreated", 1, whereCheck.getReactivateviews().intValue());

        final List<BusyHourData> mod = dmController.getBusyhourHandlingDataModel().getBusyHourData(versionId);

			BusyHourData toCheck = null;
			for(BusyHourData bhd : mod){
				if(bhd.getBusyhour().getBhtype().equals(bhType) && bhd.getBusyhour().getBhlevel().equals(defaultTestBhLevel)){
					toCheck = bhd;
					break;
				}
			}
			if(toCheck == null){
				fail("Couldn't find data to check");
			}
        final int preActivateValue = toCheck.getBusyhour().getReactivateviews();
        assertEquals(1, preActivateValue);
        final List<String> executedSQL = new ArrayList<String>();
        final StorageTimeAction sta = new StorageTimeAction(testDB, testDB, Logger.getAnonymousLogger()){
            @Override
            protected void executeSql(RockFactory rf, String... statements) throws Exception {
                executedSQL.addAll(Arrays.asList(statements));
            }
            @Override
            public void createBhRankViews(final Busyhour bh) throws Exception {
            	final String viewName = whereCheck.getBhlevel() + "_RANKBH_" + whereCheck.getBhobject()+"_"+whereCheck.getBhtype();
            	String statementString = "DROP VIEW " + viewName + " " + "create view "+viewName;
            	executeSql(null, statementString);
            }
        };
        final AtomicInteger atomIn = new AtomicInteger(0);
        final SingleFrameApplication app1 = new TestApplication() ;
        String [] arrArgs = {} ;
        TestApplication.launch(TestApplication.class, arrArgs);
        final SingleFrameApplication inst = new TestApplication();
        int loopCount = 0;
        while(!(inst instanceof SingleFrameApplication)) {
        	if(loopCount>9) {break; }
    		Thread.sleep(100); // Pause for application to be initialized from Application$NoApplication to SingleFrameApplication.
    		loopCount++;
        }
        final ActivateBHTask activateTask = new ActivateBHTask((SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
            @Override
            protected int askToConfirm() {
                return JOptionPane.YES_OPTION;
            }
            @Override
            protected StorageTimeAction getStorageTimeAction() throws Exception {
                return sta;
            }
            @Override
            protected void failed(Throwable throwable) {
                atomIn.set(1);
            }
            @Override
            protected void succeeded(Void aVoid) {
                atomIn.set(2);
            }
        };
        activateTask.execute();
        while(!activateTask.isDone()){
            Thread.sleep(500);
        }
        assertEquals("succeeded() should have been called", 2, atomIn.get());
        final int postActivateValue = mod.get(0).getBusyhour().getReactivateviews();
        assertEquals("Reactivate flag should be false", 0, postActivateValue);
        assertEquals("Wrong number of SQL statements executed", 1, executedSQL.size());
        final String eSql = executedSQL.get(0);
        final String viewName = whereCheck.getBhlevel() + "_RANKBH_" + whereCheck.getBhobject()+"_"+whereCheck.getBhtype();
        assertTrue( eSql.contains("DROP VIEW " + viewName) && eSql.contains("create view "+viewName));
    }
    
    public void testCopyPlaceholderNoEmptyProducts(){
		final String bhLevel = "DC_E_BSS_CELLBH";
		final BusyhourCustomPlaceholderTableModel customModel = (BusyhourCustomPlaceholderTableModel)
			 getTestInstance(bhLevel, PlaceholderType.Custom);
		final BusyhourProductPlaceholderTableModel productModel = (BusyhourProductPlaceholderTableModel)
					 getTestInstance(bhLevel, PlaceholderType.Product);
		final int customIndex = 0;
		final BusyHourData customPlaceholder = (BusyHourData)customModel.getData().get(customIndex);
		assertFalse(customModel.isEmpty(customPlaceholder));
		try{
			productModel.copyTo(customPlaceholder);
			fail("Exception should have been thrown");
		} catch (Exception e){
			assertTrue("Wrong error cought? cant verify test", e.getMessage().equals("Placeholders full."));
		}
	}
	public void testCopyPlaceholder(){
		final BusyhourCustomPlaceholderTableModel customModel = (BusyhourCustomPlaceholderTableModel)
			 getTestInstance(defaultTestBhLevel, PlaceholderType.Custom);
		final BusyhourProductPlaceholderTableModel productModel = (BusyhourProductPlaceholderTableModel)
					 getTestInstance(defaultTestBhLevel, PlaceholderType.Product);
		//making sure custom placeholder is populates and product placeholder is empty...
		final int productIndex = 1;
		final int customIndex = 0;
		final BusyHourData checkEmpty = (BusyHourData)productModel.getData().get(productIndex);
		assertTrue(productModel.isEmpty(checkEmpty));
		final BusyHourData customPlaceholder = (BusyHourData)customModel.getData().get(customIndex);
		assertFalse(customModel.isEmpty(customPlaceholder));

		try{
			productModel.copyTo(customPlaceholder);

			final List<Busyhour> preList = getBusyhourList(defaultTestBhLevel, PlaceholderType.Product, productIndex);
			assertFalse("This list shouldn't be empty", preList.isEmpty());
			assertTrue("Placeholder is not empty, test not setup correctly", preList.get(0).getBhcriteria().length() == 0);
			productModel.saveChanges();
			final List<Busyhour> postList = getBusyhourList(defaultTestBhLevel, PlaceholderType.Product, productIndex);
			assertFalse("This list shouldn't be empty", postList.isEmpty());
			assertFalse("Placeholder not popolated, copy failed", preList.get(0).getBhcriteria().length() > 0);
		} catch (Exception e){
			fail(e.toString());
		}
	}
	
//	public void testGroupingDisabledOnElemBh() throws Exception {
//		final boolean edit1 = testInstance.isCellEditable(0, PlaceholderType.GROUPING_COL);
//		assertTrue(edit1);
//		testInstance = getTestInstance(elemBhLevel);
//		final boolean edit2 = testInstance.isCellEditable(0, PlaceholderType.GROUPING_COL);
//		assertFalse(edit2);
//	}
	
	
	public void testSuccess() throws Exception {
	      
	      Properties prop = new Properties();
	      prop.setProperty("dwhm.debug", "true");
	      StaticProperties.giveProperties(prop);

	        final StorageTimeAction sta = new StorageTimeAction(testDB, testDB, Logger.getAnonymousLogger()){
	          
	          @Override
	            protected void executeSql(RockFactory rf, String... statements) throws Exception {
	                //
	            }
	        };
	        final AtomicInteger atomIn = new AtomicInteger(0);
	        final SingleFrameApplication app1 = new TestApplication() ;
	        String [] arrArgs = {} ;
	        TestApplication.launch(TestApplication.class, arrArgs);
	        final SingleFrameApplication inst = new TestApplication();
	        final ActivateBHTask activateTask = new ActivateBHTask((SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
	            @Override
	            protected int askToConfirm() {
	                return JOptionPane.YES_OPTION;
	            }
	            @Override
	            protected StorageTimeAction getStorageTimeAction() throws Exception {
	                return sta;
	            }
	            @Override
	            protected void failed(Throwable throwable) {
	            	throwable.printStackTrace();
	                atomIn.set(1);
	            }
	            @Override
	            protected void succeeded(Void aVoid) {
	                atomIn.set(2);
	            }
	        };
	        activateTask.execute();
	        while(!activateTask.isDone()){
	            Thread.sleep(500);
	        }
	        assertEquals("succeeded() should have been called", 2, atomIn.get());
	    }//testSuccess
	
		/**
		 * Test method for {@link com.ericsson.eniq.techpacksdk.ActivateBHTask#recreateView}.
		 * Want to see correct method is called in StorageTimeAction.
		 * VersionId=TargetVersionId  => createBhRankViews [This test case]
		 * VersionId!=TargetVersionId => createCustomBhViewCreates
		 */
		public void testRecreateView_ProductTP() throws Exception {
			Properties prop = new Properties();
	      	prop.setProperty("dwhm.debug", "true");
	      	StaticProperties.giveProperties(prop);
	      	final StorageTimeAction sta = new StorageTimeAction(testDB, testDB, Logger.getAnonymousLogger()){
	          @Override
	          public void createBhRankViews(Busyhour bh) throws Exception {
	        	  //Expected method, Do nothing.
	          }
	          @Override
	          public void createCustomBhViewCreates(Busyhour bh) throws Exception {
	        	  fail("StorageTimeAction.createCustomBhViewCreates should not have been called for Busyhour with same VersionId & TargetVersionId");
	          }
	        };
	        final AtomicInteger atomIn = new AtomicInteger(0);
	        final SingleFrameApplication app1 = new TestApplication() ;
	        String [] arrArgs = {} ;
	        TestApplication.launch(TestApplication.class, arrArgs);
	        final SingleFrameApplication inst = new TestApplication();
	        final ActivateBHTask activateTask = new ActivateBHTask((SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
	            @Override
	            protected int askToConfirm() {
	                return JOptionPane.YES_OPTION;
	            }
	            @Override
	            protected StorageTimeAction getStorageTimeAction() throws Exception {
	                return sta;
	            }
	            @Override
	            protected void failed(Throwable throwable) {
	            	throwable.printStackTrace();
	                atomIn.set(1);
	            }
	            @Override
	            protected void succeeded(Void aVoid) {
	                atomIn.set(2);
	            }
	        };
	        // Busyhour
	        Busyhour bh = new Busyhour(testDB);
	        bh.setVersionid("DC_E_BSS:((10))");
	        bh.setTargetversionid("DC_E_BSS:((10))");
	        bh.setBhtype("PP0");
	        bh.setBhcriteria("SUM(CELTCHH_THTRALACC)");
	        
	        //activateTask.recreateView(final Busyhour bh, final StorageTimeAction sta);
	        Method recreateViewMethod = ActivateBHTask.class.getDeclaredMethod("recreateView", Busyhour.class, StorageTimeAction.class);
	        recreateViewMethod.setAccessible(true);
	        recreateViewMethod.invoke(activateTask, bh, sta);
	    }//testRecreateView
		
		/**
		 * Test method for {@link com.ericsson.eniq.techpacksdk.ActivateBHTask#recreateView}.
		 * Want to see correct method is called in StorageTimeAction.
		 * VersionId=TargetVersionId  => createBhRankViews [This test case]
		 * VersionId!=TargetVersionId => createCustomBhViewCreates
		 */
		public void testRecreateView_CustomTPWithSameTarget() throws Exception {
			Properties prop = new Properties();
	      	prop.setProperty("dwhm.debug", "true");
	      	StaticProperties.giveProperties(prop);
	      	final StorageTimeAction sta = new StorageTimeAction(testDB, testDB, Logger.getAnonymousLogger()){
	          @Override
	          public void createBhRankViews(Busyhour bh) throws Exception {
	        	  //Expected method, Do nothing.
	          }
	          @Override
	          public void createCustomBhViewCreates(Busyhour bh) throws Exception {
	        	  fail("StorageTimeAction.createCustomBhViewCreates should not have been called for Busyhour with same VersionId & TargetVersionId");
	          }
	        };
	        final AtomicInteger atomIn = new AtomicInteger(0);
	        final SingleFrameApplication app1 = new TestApplication() ;
	        String [] arrArgs = {} ;
	        TestApplication.launch(TestApplication.class, arrArgs);
	        final SingleFrameApplication inst = new TestApplication();
	        final ActivateBHTask activateTask = new ActivateBHTask((SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
	            @Override
	            protected int askToConfirm() {
	                return JOptionPane.YES_OPTION;
	            }
	            @Override
	            protected StorageTimeAction getStorageTimeAction() throws Exception {
	                return sta;
	            }
	            @Override
	            protected void failed(Throwable throwable) {
	            	throwable.printStackTrace();
	                atomIn.set(1);
	            }
	            @Override
	            protected void succeeded(Void aVoid) {
	                atomIn.set(2);
	            }
	        };
	        // Busyhour
	        Busyhour bh = new Busyhour(testDB);
	        bh.setVersionid("CUSTOM_DC_E_BSS:((11))");
	        bh.setTargetversionid("CUSTOM_DC_E_BSS:((11))");
	        bh.setBhtype("CTP_PP0");
	        bh.setBhcriteria("SUM(CELTCHH_THTRALACC)");
	        
	        //activateTask.recreateView(final Busyhour bh, final StorageTimeAction sta);
	        Method recreateViewMethod = ActivateBHTask.class.getDeclaredMethod("recreateView", Busyhour.class, StorageTimeAction.class);
	        recreateViewMethod.setAccessible(true);
	        recreateViewMethod.invoke(activateTask, bh, sta);
	    }//testRecreateView
		
		/**
		 * Test method for {@link com.ericsson.eniq.techpacksdk.ActivateBHTask#recreateView}.
		 * Want to see correct method is called in StorageTimeAction.
		 * VersionId=TargetVersionId  => createBhRankViews 
		 * VersionId!=TargetVersionId => createCustomBhViewCreates [This test case]
		 */
		public void testRecreateView_CustomTPWithDiffTarget() throws Exception {
			Properties prop = new Properties();
	      	prop.setProperty("dwhm.debug", "true");
	      	StaticProperties.giveProperties(prop);
	      	final StorageTimeAction sta = new StorageTimeAction(testDB, testDB, Logger.getAnonymousLogger()){
	          @Override
	          public void createBhRankViews(Busyhour bh) throws Exception {
	        	  fail("StorageTimeAction.createBhRankViews should not have been called for Busyhour with different VersionId to TargetVersionId");
	          }
	          @Override
	          public void createCustomBhViewCreates(Busyhour bh) throws Exception {
	        	//Expected method, Do nothing.
	          }
	        };
	        final AtomicInteger atomIn = new AtomicInteger(0);
	        final SingleFrameApplication app1 = new TestApplication() ;
	        String [] arrArgs = {} ;
	        TestApplication.launch(TestApplication.class, arrArgs);
	        final SingleFrameApplication inst = new TestApplication();
	        final ActivateBHTask activateTask = new ActivateBHTask((SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
	            @Override
	            protected int askToConfirm() {
	                return JOptionPane.YES_OPTION;
	            }
	            @Override
	            protected StorageTimeAction getStorageTimeAction() throws Exception {
	                return sta;
	            }
	            @Override
	            protected void failed(Throwable throwable) {
	            	throwable.printStackTrace();
	                atomIn.set(1);
	            }
	            @Override
	            protected void succeeded(Void aVoid) {
	                atomIn.set(2);
	            }
	        };
	        // Busyhour
	        Busyhour bh = new Busyhour(testDB);
	        bh.setVersionid("CUSTOM_DC_E_BSS:((11))");
	        bh.setTargetversionid("DC_E_BSS:((10))");
	        bh.setBhtype("CTP_PP0");
	        bh.setBhcriteria("SUM(CELTCHH_THTRALACC)");
	        
	        //activateTask.recreateView(final Busyhour bh, final StorageTimeAction sta);
	        Method recreateViewMethod = ActivateBHTask.class.getDeclaredMethod("recreateView", Busyhour.class, StorageTimeAction.class);
	        recreateViewMethod.setAccessible(true);
	        recreateViewMethod.invoke(activateTask, bh, sta);
	    }//testRecreateView
	    
	    
	    public void testFailureMessages() throws Exception {
	        final String eMgs = "test_errir";
	        final AtomicInteger atomIn = new AtomicInteger(0);
	        final SingleFrameApplication app1 = new TestApplication() ;
	        String [] arrArgs = {} ;
	        TestApplication.launch(TestApplication.class, arrArgs);
	        final SingleFrameApplication inst = new TestApplication();
	        final ActivateBHTask activateTask = new ActivateBHTask(
	        		(SingleFrameApplication)TestApplication.getInstance(), dmController, manageDWHTabMock, Logger.getAnonymousLogger()){
	            @Override
	            protected int askToConfirm() {
	                return JOptionPane.YES_OPTION;
	            }
	            @Override
	            protected StorageTimeAction getStorageTimeAction() throws Exception {
	                throw new Exception(eMgs);
	            }
	            @Override
	            protected void failed(final Throwable throwable) {
	                if(throwable.getMessage().equals(eMgs)){
	                    atomIn.set(1);
	                } else {
	                    atomIn.set(2);
	                }
	            }

	            @Override
	            protected void succeeded(final Void aVoid) {
	                atomIn.set(3);
	            }
	        };
	        activateTask.execute();
	        while(!activateTask.isDone()){
	            Thread.sleep(500);
	        }
	        assertEquals("Wrong Exception sent to failed() method", 1, atomIn.get());
	    }
	    public void testReactivateFlagSetOnCriteriaChange() throws Exception {
	        final int COL = 3;
	        final String form = (String)testInstance.getValueAt(0, COL);
	        testInstance.setValueAt("weeeeeeeeeee", 0, COL);
	        testInstance.saveChanges();
	        final String memSaved = (String)testInstance.getValueAt(0, COL);
	        assertFalse(form.equals(memSaved));
	        final Busyhour whereCheck = getBusyhour("PP0", defaultTestBhLevel);
	        assertEquals("Changed formula, views should be recreated", 1, whereCheck.getReactivateviews().intValue());
	    }
	    public void testSetDataAndSave() throws Exception {
	        final String newValueToSet = "test";
	        testInstance.setValueAt(newValueToSet, 0, 0);
	        final Object next = testInstance.getValueAt(0, 0);
	        assertEquals(newValueToSet, next);
	        testInstance.saveChanges();
	        final Busyhour whereCheck = getBusyhour("PP0", defaultTestBhLevel);
	        final Object savedDbValue = whereCheck.getDescription();
	        assertEquals(newValueToSet, savedDbValue);
				final int shouldReactivate = whereCheck.getReactivateviews();
	        assertEquals("Reactivate views should be true(1) when description changed", 1, shouldReactivate);
	    }
	    
	    public void testProductPlaceholerTableName() {
	        checkCreatePlaceHolder(PlaceholderType.Product);
	    }

	    public void testCustomPlaceholerTableName() {
	        checkCreatePlaceHolder(PlaceholderType.Custom);
	    }

	    public void testProductTableInformation() {
	        checkTableInfo(PlaceholderType.Product, Arrays.asList(
	                "Description", "Source", "Where", "Formula", "Keys",
	                "Busy Hour Type", "Mapped Types", "Clause", "Enabled"
	        ));
	    }

	    public void testCustomTableInformation() {
	        checkTableInfo(PlaceholderType.Custom, Arrays.asList(
	                "Description", "Source", "Where", "Formula", "Keys",
	                "Busy Hour Type", "Mapped Types", "Clause", "Enabled"
	        ));
	    }
	    
	    
	    
	    public void testClearData()throws Exception{    	
	    	final BusyhourCustomPlaceholderTableModel customModel = (BusyhourCustomPlaceholderTableModel)
			 getTestInstance(defaultTestBhLevel, PlaceholderType.Custom);
	    	final BusyhourProductPlaceholderTableModel productModel = (BusyhourProductPlaceholderTableModel)
					 getTestInstance(defaultTestBhLevel, PlaceholderType.Product);
	    	//making sure custom placeholder is populates and product placeholder is empty...
	    	final int productIndex = 1;
	    	final int customIndex = 0;
	    	
	    	// For Product Placeholders
	    	final BusyHourData bhDataProductPlaceholder = (BusyHourData)productModel.getData().get(productIndex);
	    	List<Busyhoursource> busyhourSource = new ArrayList<Busyhoursource>();
	    	busyhourSource.add(new Busyhoursource(testDB));
	    	busyhourSource.add(new Busyhoursource(testDB));
	    	busyhourSource.add(new Busyhoursource(testDB));
	    	final String bhType = "PP0";
	        final Busyhour whereCheck = getBusyhour(bhType, defaultTestBhLevel);
	        whereCheck.setBhcriteria("BHCRITERIA for Test");
	        whereCheck.setDescription("DESCRIPTION for Test");
	        whereCheck.setWhereclause("WHERECLAUSE for Test");
	        whereCheck.setClause("CLAUSE for Test");
	    	bhDataProductPlaceholder.setBusyhour(whereCheck);
	    	bhDataProductPlaceholder.setBusyhourSource(busyhourSource);
	    	assertTrue("BusyHourData for Product Placeholder is empty. It should not be empty",productModel.isBHNotEmpty(bhDataProductPlaceholder));
	    	try{
	    		productModel.clearData(bhDataProductPlaceholder);
	    	}catch(Exception e){
	    		assertTrue("Exception comes in clearData()",false);
	    	}
	    	assertFalse("BusyHourData for Product Placeholder is not empty. It should be empty",productModel.isBHNotEmpty(bhDataProductPlaceholder));
	    	
	    	// For Custom placeholders
	    	final BusyHourData bhDataCustomPlaceholder = (BusyHourData)customModel.getData().get(customIndex);
	    	final String bhType1 = "CP0";
	        final Busyhour whereCheck1 = getBusyhour(bhType1, defaultTestBhLevel);
	        whereCheck1.setBhcriteria("BHCRITERIA for Test");
	        whereCheck1.setDescription("DESCRIPTION for Test");
	        whereCheck1.setWhereclause("WHERECLAUSE for Test");
	        whereCheck1.setClause("CLAUSE for Test");
	        bhDataCustomPlaceholder.setBusyhour(whereCheck1);
	        bhDataCustomPlaceholder.setBusyhourSource(busyhourSource);
	    	assertTrue("BusyHourData for Custom Placeholder should not be empty",productModel.isBHNotEmpty(bhDataCustomPlaceholder));
	    	try{
	    		productModel.clearData(bhDataCustomPlaceholder);
	    	}catch(Exception e){
	    		assertTrue("Exception comes in clearData()",false);
	    	}
	    	assertFalse("BusyHourData for Custom Placeholder should be empty",productModel.isBHNotEmpty(bhDataCustomPlaceholder));
	    }
	    
	  
	    private void checkCreatePlaceHolder(final PlaceholderType type) {
	        final TTTableModel testInstance = createModel(type);
	        assertEquals("Table name not set correctly", type.getFullName(), testInstance.getTableName());
	    }    
   

    private Busyhour getBusyhour(final String placeholderName, final String bhLevel) throws Exception {
        final Busyhour whereCheck = new Busyhour(testDB);
        whereCheck.setBhtype(placeholderName);
			whereCheck.setBhlevel(bhLevel);
        final BusyhourFactory checkFac = new BusyhourFactory(testDB, whereCheck);
        final List<Busyhour> bhList = checkFac.get();
        return bhList.get(0);
    }

    public void checkTableInfo(final PlaceholderType type, final List<String> expectedColNames) {
        final TableInformation tInfo = PlaceholderType.getTableInfo(type);
        assertEquals("TableInformation type not correct", type.getFullName(), tInfo.getType());
        for (String colName : tInfo.getColumnNamesInOriginalOrder()) {
            assertTrue("Unknown Column name in " + type.getFullName(), expectedColNames.contains(colName));
        }
        final List<String> tmp = Arrays.asList(tInfo.getColumnNamesInOriginalOrder());
        for (String required : expectedColNames) {
            assertTrue("Required column not defined : " + type.getFullName() + "[" + required + "]", tmp.contains(required));
        }
    }

    private TTTableModel createModel(final PlaceholderType type) {
//    	Date date = new Date();
    	final Application applicationMock = context.mock(Application.class, "application"+mockCount++); //naming the mock objects makes them unique
    	final RockFactory rockFactoryMock = context.mock(RockFactory.class, "rockFactory"+mockCount++);
    	
//        final Application mockApp = createNiceMock(Application.class);
//        final RockFactory mockRock = createNiceMock(RockFactory.class);
        return PlaceholderType.createModel(type, applicationMock, rockFactoryMock, true);
    }

    private void loadUnitDb(final RockFactory unitdb, final String dir) throws Exception {
    	
    	String currentLocation = System.getProperty("user.dir");
    	String sep = System.getProperty("file.separator");
    	String sourceSQLLocation = "";
    	
    	if(currentLocation.endsWith("ant_common")){
    		//remove stuff at end...
    		currentLocation = currentLocation.substring(0, currentLocation.length()-16);
    		currentLocation = currentLocation + "design" + sep + "plat" + sep + "tp_ide" + sep + "dev" + sep + "tp_ide";
    	}
    	sourceSQLLocation = currentLocation + System.getProperty("file.separator") + "test" + System.getProperty("file.separator") + dir;
//    	System.out.println("***************** The Location is = "+sourceSQLLocation+ "********************************");
        try{
//        	System.out.println("The current working dir = "+System.getProperty("user.dir"));
//        	System.out.println("CLASSPATH = "+System.getProperty("java.class.path"));
//            final URL url = ClassLoader.getSystemClassLoader().getResource(dir);
//					if(url == null){
//						throw new Exception("Couldn't find '"+dir+"' on the classpath");
//					}
//            final File f = new File(url.toURI());
        	final File f = new File(sourceSQLLocation);
            loadTestData(unitdb, f);
        } catch (SQLException e){
            throw new Exception(e.getMessage() + "\n\t" + e.getCause());
        }
    }
    private void loadTestData(final RockFactory testDB, final File baseDir) throws ClassNotFoundException, IOException, SQLException {
        final File[] toLoad = baseDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql") && !name.equals(_createStatementMetaFile);
            }
        });
        final File createFile = new File(baseDir + "/" + _createStatementMetaFile);
        _loadSqlFile_(createFile, testDB);
        for (File loadFile : toLoad) {
            _loadSqlFile_(loadFile, testDB);
        }
    }

    private void _shutdown_(final RockFactory db) {
        try {
            if (db != null && !db.getConnection().isClosed()) {
                final Statement stmt = db.getConnection().createStatement();
                stmt.executeUpdate("SHUTDOWN");
                stmt.close();
                db.getConnection().close();
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private void _loadSqlFile_(final File sqlFile, final RockFactory testDB) throws IOException, SQLException, ClassNotFoundException {
        if (!sqlFile.exists()) {
            System.out.println(sqlFile + " doesnt exist, skipping..");
            return;
        }
//        System.out.println("Loading " + sqlFile);
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
                _update_(line, testDB);
            }
            testDB.commit();
        } catch (SQLException e) {
            throw new SQLException("Error executing on line [" + lineCount + "] of " + sqlFile, e);
        } finally {
            br.close();
        }
    }

    private void _update_(final String insertSQL, final RockFactory testDB) throws SQLException, ClassNotFoundException, IOException {
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

    enum PlaceholderType {
        Custom("CP"),
        Product("PP");
        private final String type;
			private String fullName;
			//public static final int GROUPING_COL = 7;
        PlaceholderType(final String type) {
            this.type = type;
					if(this.type.equals("CP")){
						fullName = "Custom Placeholders";
					} else {
						fullName = "Product Placeholders";
					}
        }
        @Override
        public String toString() {
            return type;
        }
			public String getFullName(){
				return fullName;
			}
        public static TTTableModel createModel(final PlaceholderType type,
                                                                        final Application application,
                                                                        final RockFactory rockFactory,
                                                                        final boolean editable) {
            final TTTableModel model;
            final Vector<TableInformation> tInfo = new Vector<TableInformation>();
            switch (type) {
                case Product:
                    tInfo.add(BusyhourProductPlaceholderTableModel.createTableTypeInfo());
                    model = new BusyhourProductPlaceholderTableModel(
                            application, rockFactory, tInfo, null, editable, null);
                    break;
                default:
                    tInfo.add(BusyhourCustomPlaceholderTableModel.createTableTypeInfo());
                    model = new BusyhourCustomPlaceholderTableModel(
                            application, rockFactory, tInfo, null, editable, null, null);
            }
            return model;
        }
        public static TableInformation getTableInfo(final PlaceholderType type) {
            switch (type) {
                case Product:
                    return BusyhourProductPlaceholderTableModel.createTableTypeInfo();
                default:
                    return BusyhourCustomPlaceholderTableModel.createTableTypeInfo();
            }
        }
    }// enum PlaceholderType
    
    
    
      public void testIsTableAddAllowed() {
    	
    	final Versioning where = new Versioning(testDB);
    	where.setTechpack_type(techpackTypeCustom);  
        bhdCustom = new BusyHourData(dmController, where, defaultTestBhLevel, defaultTestBhLevel, defaultTestBhLevel);	    	   
    	testInstance.checkTPType(bhdCustom);
    	assertEquals(true,testInstance.checkTPType(bhdCustom));
    	    	  
    	final Versioning where1 = new Versioning(testDB);
    	where1.setTechpack_type(techpackTypeOthers);  
        bhdOthers = new BusyHourData(dmController, where1, defaultTestBhLevel, defaultTestBhLevel, defaultTestBhLevel);	    	   
    	testInstance.checkTPType(bhdOthers);
    	assertEquals(false,testInstance.checkTPType(bhdOthers));
    	}
    	
    	    
    
    private void initVelocity() throws Exception{
    	
    		final Properties props = new Properties();
            props.put("dwhm.debug", "true");
            //Setting dir for Velocity templates             
        	final String currentLocation = System.getProperty("user.dir");
//        	if(!currentLocation.endsWith("ant_common")){ 
//        		props.put("dwhm.templatePath",System.getProperty("user.dir")+"\\jar\\"); // Gets tests running on laptop        
//        	}
        	StaticProperties.giveProperties(props);
        	// eanguan 20110614 :: Commenting it to enable the Templates pick from real dwh_manager JAR
        	// Make sure you have dwh_manger JAR in the build path
//    		org.apache.velocity.app.Velocity.setProperty("resource.loader", "file");
////    		org.apache.velocity.app.Velocity.setProperty("class.resource.loader.class",
////                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//    		//org.apache.velocity.app.Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//    		org.apache.velocity.app.Velocity.setProperty("file.resource.loader.path", StaticProperties.getProperty("dwhm.templatePath",
//                "/dc/dc5000/conf/dwhm_templates"));
//    		//org.apache.velocity.app.Velocity.setProperty("file.resource.loader.cache", "true");
//    		//org.apache.velocity.app.Velocity.setProperty("file.resource.loader.modificationCheckInterval", "60");
//    		org.apache.velocity.app.Velocity.init();
////            System.out.println(" Velocity Initialized"); 
    }
}// class

class TestApplication extends SingleFrameApplication {
    

    @Override
    protected void startup() {
//        final Properties props = new Properties();
//        props.put("dwhm.debug", "true");
//        //Setting dir for Velocity templates             
//    	final String currentLocation = System.getProperty("user.home");        
//    	if(!currentLocation.endsWith("ant_common")){        	
//    		props.put("dwhm.templatePath", ".\\jar\\5.2\\"); // Gets tests running on laptop        
//    	}
//    	try {
//			StaticProperties.giveProperties(props);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Velocity.setProperty("resource.loader", "class");
//        Velocity.setProperty("class.resource.loader.class",
//            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        Velocity
//            .setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//        Velocity.setProperty("file.resource.loader.path", StaticProperties.getProperty("dwhm.templatePath",
//            "/dc/dc5000/conf/dwhm_templates"));
//        Velocity.setProperty("file.resource.loader.cache", "true");
//        Velocity.setProperty("file.resource.loader.modificationCheckInterval", "60");
//        try {
//			Velocity.init();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println(" Velocity Initialized");
    }
};