package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import static org.easymock.classextension.EasyMock.createNiceMock;

import java.awt.Component;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.jdesktop.application.SingleFrameApplication;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourHandlingDataModel;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourTreeModel;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.nonmigrated.NonMigratedManageBusyHourView;



/**
 * Test for CreateTabFactory Class. 
 * CreateTabFactory class creates edit frame tabs 
 *   and passes tab back to GeneralTechPackTab
 * 
 * @author 
 */
public class CreateTabFactoryTest extends TestCase {
	
	//Mocks:
	
	private DataModelController dmcMock;	
	private RockFactory rockFactoryMock;
	private Versioning versioningMock;	
	private GeneralTechPackTab generalTPTabMock;	
	private CreateTabFactory testInstance = null;	
	private JFrame mockFrame;
	private BusyhourHandlingDataModel mockBusyHourHandlingDatamodel;
	private BusyhourTreeModel mockBusyHourtreeModel;
	private ManageBusyHourView mockBusyHourView;
	private NonMigratedManageBusyHourView mockNonMigratedManageBusyHourView;
	private SingleFrameApplication tpide;
	
	private boolean editable = true;
	
	private final Mockery context = new JUnit4Mockery() {
	    {
	      setImposteriser(ClassImposteriser.INSTANCE);
	    }
	};	

	
	public CreateTabFactoryTest() {
	    super("CreateTabFactoryTest");
	  }

	@Before
	  protected void setUp() throws Exception {
		tpide =  new TechPackIDE();
		tpide.getContext().setApplicationClass(TechPackIDE.class);
		
		versioningMock = context.mock(Versioning.class, "versioningMock");		
		dmcMock = context.mock(DataModelController.class, "dmcMock");		
		generalTPTabMock = context.mock(GeneralTechPackTab.class, "generalTPTabMock");
		rockFactoryMock = createNiceMock(RockFactory.class);
		mockFrame = context.mock(JFrame.class, "mockFrame");	
		mockBusyHourHandlingDatamodel = context.mock(BusyhourHandlingDataModel.class, "mockBusyHourHandlingDatamodel");		
		mockBusyHourtreeModel = context.mock(BusyhourTreeModel.class, "mockBusyHourtreeModel");
		mockBusyHourView = context.mock(ManageBusyHourView.class, "mockBusyHourView");
		mockNonMigratedManageBusyHourView =  context.mock(NonMigratedManageBusyHourView.class, "mockNonMigratedManageBusyHourView");
		createContext();		
		testInstance = new CreateTabFactoryDummy(tpide,dmcMock, versioningMock, mockFrame, generalTPTabMock, editable);
	}
	
	// Set expectations for mock objects
	private void createContext() throws Exception {		
		context.checking(new Expectations() {
			{			
				allowing(dmcMock).getBusyhourHandlingDataModel();
				will(returnValue(mockBusyHourHandlingDatamodel));			
								
				allowing(mockBusyHourHandlingDatamodel).setCurrentVersioning(versioningMock);
				allowing(mockBusyHourHandlingDatamodel).refreshAllBusyHourSourcesFromDb();
				allowing(mockBusyHourHandlingDatamodel).refresh();
				allowing(mockBusyHourHandlingDatamodel).getBusyHourTargetVersionIDs();
				allowing(dmcMock).setBusyHourTreeModel(mockBusyHourtreeModel);
				allowing(dmcMock).setBusyHourTreeModel(mockBusyHourtreeModel);
				
				allowing(versioningMock).getBasedefinition();
				will(returnValue("DUMMY"));
				
				allowing(versioningMock).getVersionid();
				will(returnValue("DUMMY"));
				
				allowing(versioningMock).getTechpack_name();
				will(returnValue("DUMMY"));
				
				allowing(generalTPTabMock).isEditable();
				will(returnValue(true));
			
				allowing(dmcMock).getRockFactory();
				will(returnValue(rockFactoryMock));
			
			}
		});
	}

	@After
	  protected void tearDown() throws Exception {
		testInstance = null;
	  }
	
	//test only Busy Hour tab...
	public void testCreateTab() throws Exception {
		Component busyhourTab = testInstance.createTab("Busy Hours");	
		Assert.assertNotNull(busyhourTab);		
			
		}
	
	
	private class CreateTabFactoryDummy extends CreateTabFactory {
		
		public CreateTabFactoryDummy(final SingleFrameApplication theApplication, final DataModelController adataModelController,
			      final Versioning aversioning, final JFrame aframe, GeneralTechPackTab atpTab, boolean editable) {
			
				super(theApplication, adataModelController, aversioning, aframe, atpTab, editable);			
		}
		
		public Component getBusyhourTab(final DataModelController dmcMock, final Versioning versioningMock, final boolean edit,
				 final SingleFrameApplication tpide, final JFrame mockFrame, final GeneralTechPackTab generalTPTabMock){
			final Busyhour where = new Busyhour(dmcMock.getRockFactory());
			where.setVersionid(versioningMock.getVersionid());
			boolean oldFormat = false;
			try{
			final BusyhourFactory fac = new BusyhourFactory(dmcMock.getRockFactory(), where);
			final List<Busyhour> bhl = fac.get();
			oldFormat = !bhl.isEmpty() && (bhl.get(0).getPlaceholdertype() == null || bhl.get(0).getPlaceholdertype().length() == 0);
			} catch (Exception e){
			
			// presume the busyhour is the new format....
			}
			final Component busyhourTab;
			if(oldFormat){
				// retun mock Busy Hour Object for non migrated.
				busyhourTab = mockNonMigratedManageBusyHourView;
			} else {
				// retun mock Busy Hour Object
				busyhourTab = mockBusyHourView;
			}
			return busyhourTab;
			}
	}
}