package com.ericsson.eniq.busyhourcfg.dwhmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Properties;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;
import utils.MemoryDatabaseUtility;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.ericsson.eniq.busyhourcfg.exceptions.DWHManagerException;


public class DefaultDWHStorageTimeActionTest {
	public static final String TEST_APPLICATION = DefaultDWHStorageTimeActionTest.class.getName();
	private final Mockery context = new JUnit4Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	final RockFactory mockDwhRockFactory = context.mock(RockFactory.class, "DWHmock");
	private static RockFactory testDwhRep;
	
	private DefaultDWHStorageTimeAction objectUnderTest;
	
	@Before
	public void setUp() throws Exception {
		//Test dB
		testDwhRep = new RockFactory(MemoryDatabaseUtility.TEST_DWHREP_URL, MemoryDatabaseUtility.TESTDB_USERNAME,
		        MemoryDatabaseUtility.TESTDB_PASSWORD, MemoryDatabaseUtility.TESTDB_DRIVER, TEST_APPLICATION, true);
		final URL dwhrepsqlurl = ClassLoader.getSystemResource(MemoryDatabaseUtility.TEST_DWHREP_BASIC);
		MemoryDatabaseUtility.loadSetup(testDwhRep, dwhrepsqlurl);
		
	    //Instance
		objectUnderTest = new DefaultDWHStorageTimeAction(mockDwhRockFactory, testDwhRep);
		assertNotNull(objectUnderTest);
	}
	
	@After
	public void tearDown() throws Exception {
		MemoryDatabaseUtility.shutdown(testDwhRep);
		objectUnderTest = null;
	}
	
	/**
	 * Test method for {@link com.ericsson.eniq.busyhourcfg.dwhmanager.DefaultDWHStorageTimeAction#performCreateBhRankViews}.
	 * Want to see correct method is called in StorageTimeAction.
	 * VersionId=TargetVersionId => createBhRankViews [This test case]
	 * VersionId!=TargetVersionId => createCustomBhViewCreates
	 */
	@Test
	public void testPerformCreateBhRankViews_ProductTP() {
		boolean expectedCodePathFollowed = false;
		// Busyhour
        Busyhour bh = new Busyhour(testDwhRep);
        bh.setVersionid("DC_E_MGW:((999))");
        bh.setTargetversionid("DC_E_MGW:((999))");
        bh.setBhtype("PP0");
        bh.setBhcriteria("cast((avg(ifnull(pmBusyInstances,0,pmBusyInstances))) as numeric(18,8))");
        bh.setBhlevel("DC_E_MGW_TEST_NOT_FOUND");// bh.setBhlevel("DC_E_MGW_SERVICEBH");
        bh.setBhobject("SERVICE");
        bh.setBhelement(0);
    	final Properties p = new Properties();        
    	p.put("dwhm.debug", "true");        
    	final String currentLocation = System.getProperty("user.home");        
    	if(!currentLocation.endsWith("ant_common")){        	
    		p.put("dwhm.templatePath", ".\\jar\\"); // Gets tests running on laptop        
    	}        
    	try {
			StaticProperties.giveProperties(p);
		} catch (Exception e) {
			fail(e.getMessage());
		}          
		StringBuffer expected = new StringBuffer("No Measurementtypes found for DC_E_MGW:((999)) DC_E_MGW_TEST_NOT_FOUND"); //thrown by StorageTimeAction.createBhRankViews
		try {
			objectUnderTest.performCreateBhRankViews(bh);
		} catch (DWHManagerException e) {
			// For this test expect an Exception.
			// Determine if Code followed correct path if expected Exception thrown.
			if(e.getCause().getMessage().contentEquals(expected)) {
				expectedCodePathFollowed = true;
			} else {
				fail("Unexpected exception: "+e.getMessage()+", cause:"+e.getCause().getMessage());
			}
		}
		assertTrue("Did not call StorageTimeAction.createCustomBhViewCreates", expectedCodePathFollowed);
	}//testPerformCreateBhRankViews
	
	/**
	 * Test method for {@link com.ericsson.eniq.techpacksdk.ActivateBHTask#recreateView}.
	 * Want to see correct method is called in StorageTimeAction.
	 * VersionId=TargetVersionId  => createBhRankViews [This test case]
	 * VersionId!=TargetVersionId => createCustomBhViewCreates
	 */
	@Test
	public void testPerformCreateBhRankViews_CustomTPWithSameTarget() {
		boolean expectedCodePathFollowed = false;
		// Busyhour
		Busyhour bh = new Busyhour(testDwhRep);
		bh.setVersionid("CUSTOM_DC_E_MGW:((888))");
		bh.setTargetversionid("CUSTOM_DC_E_MGW:((888))");
		bh.setBhtype("CTP_PP0");
		bh.setBhcriteria("cast((avg(ifnull(pmBusyInstances,0,pmBusyInstances))) as numeric(18,8))");
		bh.setBhlevel("DC_E_MGW_TEST_NOT_FOUND");// bh.setBhlevel("DC_E_MGW_SERVICEBH");
		bh.setBhobject("SERVICE");
		bh.setBhelement(0);
		final Properties p = new Properties();
		p.put("dwhm.debug", "true");
		final String currentLocation = System.getProperty("user.home");
		if (!currentLocation.endsWith("ant_common")) {
			p.put("dwhm.templatePath", ".\\jar\\"); // Gets tests running on laptop
		}
		try {
			StaticProperties.giveProperties(p);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		StringBuffer expected = new StringBuffer("No Measurementtypes found for CUSTOM_DC_E_MGW:((888)) DC_E_MGW_TEST_NOT_FOUND"); // thrown by StorageTimeAction.createBhRankViews
		try {
			objectUnderTest.performCreateBhRankViews(bh);
		} catch (DWHManagerException e) {
			// For this test expect an Exception.
			// Determine if Code followed correct path if expected Exception thrown.
			if (e.getCause().getMessage().contentEquals(expected)) {
				expectedCodePathFollowed = true;
			} else {
				fail("Unexpected exception: "+e.getCause().getMessage()+", cause:"+e.getCause().getMessage());
			}
		}
		assertTrue("Did not call StorageTimeAction.createBhRankViews", expectedCodePathFollowed);
	}//testPerformCreateBhRankViews
	
	/**
	 * Test method for {@link com.ericsson.eniq.techpacksdk.ActivateBHTask#recreateView}.
	 * Want to see correct method is called in StorageTimeAction.
	 * VersionId=TargetVersionId  => createBhRankViews 
	 * VersionId!=TargetVersionId => createCustomBhViewCreates [This test case]
	 */
	@Test
	public void testPerformCreateBhRankViews_CustomTPWithDiffTarget() {
		boolean expectedCodePathFollowed = false;
		// Busyhour
		Busyhour bh = new Busyhour(testDwhRep);
		bh.setVersionid("CUSTOM_DC_E_MGW:((888))");
		bh.setTargetversionid("DC_E_MGW:((999))");
		bh.setBhtype("CTP_PP0");
		bh.setBhcriteria("cast((avg(ifnull(pmBusyInstances,0,pmBusyInstances))) as numeric(18,8))");
		bh.setBhlevel("DC_E_MGW_TEST_NOT_FOUND");// bh.setBhlevel("DC_E_MGW_SERVICEBH");
		bh.setBhobject("SERVICE");
		bh.setBhelement(0);
		final Properties p = new Properties();
		p.put("dwhm.debug", "true");
		final String currentLocation = System.getProperty("user.home");
		if (!currentLocation.endsWith("ant_common")) {
			p.put("dwhm.templatePath", ".\\jar\\"); // Gets tests running on laptop
		}
		try {
			StaticProperties.giveProperties(p);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		StringBuffer expected = new StringBuffer("String index out of range: -5"); // thrown by StorageTimeAction.createCustomBhViewCreates
		try {
			objectUnderTest.performCreateBhRankViews(bh);
		} catch (DWHManagerException e) {
			// For this test expect an Exception.
			// Determine if Code followed correct path if expected Exception thrown.
			if (e.getCause().getMessage().contentEquals(expected)) {
				expectedCodePathFollowed = true;
			} else {
				fail("Unexpected exception: "+e.getCause().getMessage()+", cause:"+e.getCause().getMessage());
			}
		}
		assertTrue("Did not call StorageTimeAction.createCustomBhViewCreates", expectedCodePathFollowed);
	}//testPerformCreateBhRankViews
}
