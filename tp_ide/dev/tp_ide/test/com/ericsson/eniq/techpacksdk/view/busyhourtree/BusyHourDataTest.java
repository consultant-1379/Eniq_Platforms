package com.ericsson.eniq.techpacksdk.view.busyhourtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@RunWith(JMock.class)
public class BusyHourDataTest {

	protected Mockery context = new JUnit4Mockery();
	{
		// we need to mock classes, not just interfaces.
		context.setImposteriser(ClassImposteriser.INSTANCE);
	}

	private DataModelController dataModelControllerMock;
	private Versioning versioningMock;
	private RockFactory rockFactory = null;
	
	@Before
	public void setup(){
		dataModelControllerMock = context.mock(DataModelController.class);
		versioningMock = context.mock(Versioning.class);
		
		//Create a concrete RockFactory
		
	}
	
	@Test
	public void testCreationOfBusyHourDataObject(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_AAL2APBH";
		final String placeholderType = "PP";

		setExpectations();

		//Execution...
		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);

		//Assertion...
		assertNotNull(bhData);
	}
	
	@Test
	public void testCreationOfBusyHourDataCheckingBHObject(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_AAL2APBH";
		final String placeholderType = "PP";
		final String expectedBhObject = "AAL2AP";

		setExpectations();

		//Execution...
		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);

		//Assertion...
		assertNotNull(bhData);
		assertEquals("Expected BhObject="+expectedBhObject+" for "+bhLevel+":", expectedBhObject, bhData.getBusyhour().getBhobject());
	} // testCreationOfBusyHourDataCheckingBHObject
	
	@Test
	public void testGenerateBusyHourMappings(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_VCLTPBH";
		final String placeholderType = "PP";
		final String expectedBhObject = "VCLTP";
		final List<Measurementobjbhsupport> supports = setupMeasurementobjbhsupports();

		setExpectations();
		
		context.checking(new Expectations() {
			{
				oneOf(versioningMock).getTechpack_type();
				will(returnValue(Constants.CUSTOM_TECHPACK));
			}
		});
		
		//Execution...
		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);
		bhData.setBHType(0);
		bhData.generateBusyHourMappings(supports);
		//Assertions...
		assertNotNull(bhData);
		assertEquals("Expected BhObject="+expectedBhObject+" for "+bhLevel+":", expectedBhObject, bhData.getBusyhour().getBhobject());
		// Check BusyhourMappings
		List<Busyhourmapping> busyHourMappings =  bhData.getBusyhourmapping();
		assertNotNull(busyHourMappings);
		// Check number of BusyHourMappings is as expected, should be no more or less.
		assertEquals("For "+targetVersionId+" problem with Busyhourmappings in list for "+bhLevel, 2, busyHourMappings.size());
		// Expect list, so check contents are correct.
		// Creating Hashtable of results, then check contents of list.
		Hashtable<String, String> mappings = new Hashtable<String, String>();
		for (Busyhourmapping bhm : busyHourMappings) {
			mappings.put(bhm.getBhtargettype(), bhm.getTypeid());
		} // for
		// Expect {DC_E_CPP_VCLTP_V=DC_E_CPP:((123)):DC_E_CPP_VCLTP_V, DC_E_CPP_VCLTP=DC_E_CPP:((123)):DC_E_CPP_VCLTP}
		assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP", mappings.get("DC_E_CPP_VCLTP"));
		assertEquals(targetVersionId+ ":"+"DC_E_CPP_VCLTP_V", mappings.get("DC_E_CPP_VCLTP_V"));
	} // testGenerateBusyHourMappings
	
	@Test
	public void testGenerateBusyHourMappingsEmptyBH(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_VCLTPBH";
		final String placeholderType = "PP";
		final List<Measurementobjbhsupport> supports = setupMeasurementobjbhsupports();

		setExpectations();

		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);
		bhData.getBusyhour().setBhtype("");
		// Incomplete Busyhour should return skip and return an empty list
		bhData.generateBusyHourMappings(supports);
		//Assertions...
		assertNotNull(bhData);
		// Check BusyhourMappings
		List<Busyhourmapping> busyHourMappings =  bhData.getBusyhourmapping();
		assertNotNull(busyHourMappings);
		// Check number of BusyHourMappings is as expected, should be no more or less.
		assertEquals("For "+targetVersionId+" problem with Busyhourmappings in list for "+bhLevel, 0, busyHourMappings.size());
	} // testGenerateBusyHourMappings

	@Test
	public void testSetBHTypeForCustomTPProductBusyhour(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_AAL2APBH";
		final String placeholderType = "PP";

		final int placeholderNumber = 0;
		
		setExpectations();

		context.checking(new Expectations() {
			{
				oneOf(versioningMock).getTechpack_type();
				will(returnValue(Constants.CUSTOM_TECHPACK));
			}
		});

		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);
		
		//Execution...
		bhData.setBHType(placeholderNumber);
		
		//Assertion...
		String actual = bhData.getBusyhour().getBhtype();
		String expected = "CTP_PP0";
		
		assertEquals(expected, actual);
	}

	@Test
	public void testSetBHTypeForProductTPProductBusyhour(){
		//Setup...
		final String targetVersionId = "DC_E_CPP:((123))";
		final String bhLevel		 = "DC_E_CPP_AAL2APBH";
		final String placeholderType = "PP";

		final int placeholderNumber = 1;
		
		setExpectations();

		context.checking(new Expectations() {
			{
				oneOf(versioningMock).getTechpack_type();
				will(returnValue(Constants.PM_TECHPACK));
			}
		});

		BusyHourData bhData = new BusyHourData(dataModelControllerMock, versioningMock, targetVersionId, bhLevel, placeholderType);
		
		//Execution...
		bhData.setBHType(placeholderNumber);
		
		//Assertion...
		String actual = bhData.getBusyhour().getBhtype();
		String expected = "PP1";
		
		assertEquals(expected, actual);
	}

	private void setExpectations() {
		//Set up the Mock Objects for this call.
		context.checking(new Expectations() {
			{
				allowing(dataModelControllerMock).getRockFactory();
				will(returnValue(rockFactory));
				
				oneOf(versioningMock).getVersionid();
				will(returnValue("CUSTOM_DC_E_CPP:((888))"));
			}
		});
	}
	
	private List<Measurementobjbhsupport> setupMeasurementobjbhsupports() {
		final List<Measurementobjbhsupport> supports = new Vector<Measurementobjbhsupport>();
		Measurementobjbhsupport sup1 = new Measurementobjbhsupport(rockFactory);
		Measurementobjbhsupport sup2 = new Measurementobjbhsupport(rockFactory);
		Measurementobjbhsupport sup3 = new Measurementobjbhsupport(rockFactory);
		Measurementobjbhsupport sup4 = new Measurementobjbhsupport(rockFactory);
		Measurementobjbhsupport sup5 = new Measurementobjbhsupport(rockFactory);
		Measurementobjbhsupport sup6 = new Measurementobjbhsupport(rockFactory);
		sup1.setTypeid("DC_E_CPP:((118)):DC_E_CPP_VCLTP"); sup1.setObjbhsupport("VCLTP");
		sup2.setTypeid("DC_E_CPP:((118)):DC_E_CPP_VCLTP_V"); sup2.setObjbhsupport("VCLTP");
		sup3.setTypeid("DC_E_CPP:((118)):DC_E_CPP_VCLTPBH"); sup3.setObjbhsupport("VCLTP");
		sup4.setTypeid("DC_E_CPP:((123)):DC_E_CPP_VCLTP"); sup4.setObjbhsupport("VCLTP");
		sup5.setTypeid("DC_E_CPP:((123)):DC_E_CPP_VCLTP_V"); sup5.setObjbhsupport("VCLTP");
		sup6.setTypeid("DC_E_CPP:((123)):DC_E_CPP_VCLTPBH"); sup6.setObjbhsupport("VCLTP");
		supports.add(sup1);
		supports.add(sup2);
		supports.add(sup3);
		supports.add(sup4);
		supports.add(sup5);
		supports.add(sup6);
		return supports;
	} // setupMeasurementobjbhsupports
	
	
}
