package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.Component;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JFrame;


import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.nonmigrated.NonMigratedManageBusyHourView;
import com.ericsson.eniq.techpacksdk.view.dataFormat.DataformatHandlingView;
import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLSetHandlingView;
import com.ericsson.eniq.techpacksdk.view.universeParameters.UniverseTabs;



public class CreateTabFactory {
	
	private static final Logger LOGGER = Logger.getLogger(CreateTabFactory.class.getName());

	private JFrame frame;

	private SingleFrameApplication application;

	private DataModelController dataModelController;	
	
	private Versioning versioning;
	
	private  GeneralTechPackTab tpTab;
	
	//private static boolean isCalled = false ;
	
	boolean editable = true;
	
	
	public CreateTabFactory(){
	
	}
	
	
		public CreateTabFactory(final SingleFrameApplication theApplication, final DataModelController adataModelController,
		      final Versioning aversioning, final JFrame aframe, GeneralTechPackTab atpTab, boolean editable) {
		
			this.frame = aframe;	    
		    this.application = theApplication;
		    this.dataModelController = adataModelController;
		    this.versioning=aversioning;
		    this.tpTab = atpTab ;
		   
		    
			
	}	
	
	

	public Component createTab(final String clickedTab) {		
		if(clickedTab.equals("Measurement Types")){				
			final ManageMeasurementView measurementTab = new ManageMeasurementView(application, dataModelController, versioning, tpTab, tpTab.frame);
			LOGGER.info("Created ManageMeasurementView");
			return measurementTab;
			
		}else if(clickedTab.equals("Busy Hours")){
			final Component busyhourTab = getBusyhourTab(dataModelController, versioning, tpTab.editable, application, tpTab.frame, tpTab);
			LOGGER.info("Created ManageBusyHourView");
			return busyhourTab;
			
		}else if(clickedTab.equals("Sets/Actions/Schedulings")){
			int setType = GeneralTechPackTab.getSetType(versioning.getTechpack_name(), versioning.getTechpack_type());
			final ETLSetHandlingView etlSetTab = new ETLSetHandlingView(application, dataModelController,
					versioning.getTechpack_name(), versioning.getTechpack_version(), versioning.getVersionid(), 
					versioning.getTechpack_type(), setType, tpTab.editable, tpTab, tpTab.frame);
			LOGGER.info("Created ETLSetHandlingView");
			return etlSetTab;
			
		}else if(clickedTab.equals("Data Formats")){
			int setType = GeneralTechPackTab.getSetType(versioning.getTechpack_name(), versioning.getTechpack_type());
			final DataformatHandlingView dataFormatTab = new DataformatHandlingView(application, dataModelController, versioning.getTechpack_name(), 
					versioning.getTechpack_version(), versioning.getVersionid(),versioning.getTechpack_type(), setType, tpTab.editable, tpTab, tpTab.frame);
			LOGGER.info("Created DataformatHandlingView");
			return dataFormatTab;
			
		}else if(clickedTab.equals("Reference Types")){
			final ManageReferenceView referenceTab = new ManageReferenceView(application, dataModelController, versioning, tpTab, frame);
			LOGGER.info("Created ManageReferenceView");
			return referenceTab;
			
		}else if(clickedTab.equals("Transformers")){
			final ManageTransformerView transformerTab = new ManageTransformerView(application, dataModelController,versioning, tpTab, frame);
			LOGGER.info("Created ManageTransformerView");
			return transformerTab;
			
		}else if(clickedTab.equals("External Statements")){
			final ManageExternalStatementView externalstatementTab = new ManageExternalStatementView(application,dataModelController, 
					versioning, tpTab.editable, tpTab, tpTab.frame);
			LOGGER.info("Created ManageExternalStatementView");
			return externalstatementTab;
			
		}else if(clickedTab.equals("Universe")){
			final UniverseTabs universeTabs = new UniverseTabs(application, dataModelController, versioning, tpTab,
				     tpTab.editable, frame);
			LOGGER.info("Created UniverseTabs");
			return universeTabs;
			
		}else if(clickedTab.equals("Verification Objects")){
			final ManageVerificationObjectView verificationobjectTab = new ManageVerificationObjectView(application, dataModelController, 
					versioning, tpTab, tpTab.frame);
			LOGGER.info("Created ManageVerificationObjectView");
			return verificationobjectTab;
			
		}else if(clickedTab.equals("Verification Conditions")){
			final ManageVerificationConditionView verificationconditionTab = new ManageVerificationConditionView(
				     application, dataModelController, versioning, tpTab, tpTab.frame);
			LOGGER.info("Created ManageVerificationConditionView");
			return verificationconditionTab;
			
		}else if(clickedTab.equals("Group Types")){
			final ManageGroupView groupTab = new ManageGroupView(application, dataModelController, versioning, tpTab,
					tpTab.frame);
			LOGGER.info("Created ManageGroupView");
			return groupTab ;
			
		}			
		return null;
	}
	
	/**
	 * Get the BusyHour tab to display the aggregations.
	 * This will figure out of the Busyhour's have been migrated to the new Placeholder format
	 * @param dmc The DataModelController to use in the view
	 * @param ver The Tech Pack version
	 * @param edit Should the busy hours be editable. Only used if the Busyhours have been migrated to the new Placeholder
	 * format, the non migrated Busyhour aggregations are not editable, a new version of the Tech Pack needs to be created
	 * (which will migrate the Busyhours to the new Placeholder format)
	 * @param app The main Application
	 * @param frame The parent JFrame
	 * @param tpTab The GeneralTechPackTab view the Busyhour view will be added to
	 * @return The Busyhour View.
	 */
	public  Component getBusyhourTab(final DataModelController dmc, final Versioning ver, final boolean edit,
																				 final SingleFrameApplication app, final JFrame frame, final GeneralTechPackTab tpTab){
		final Busyhour where = new Busyhour(dmc.getRockFactory());
		where.setVersionid(ver.getVersionid());
		boolean oldFormat = false;
		try{
			final BusyhourFactory fac = new BusyhourFactory(dmc.getRockFactory(), where);
			final List<Busyhour> bhl = fac.get();
			oldFormat = !bhl.isEmpty() && (bhl.get(0).getPlaceholdertype() == null || bhl.get(0).getPlaceholdertype().length() == 0);
		} catch (Exception e){
			LOGGER.warning(e.getMessage());
			// presume the busyhour is the new format....
		}
		final Component busyhourTab;
		if(oldFormat){
			busyhourTab = new NonMigratedManageBusyHourView(app, dmc, ver, tpTab);
		} else {
			busyhourTab = new ManageBusyHourView(app, dmc, ver, edit, tpTab, frame);
		}
		return busyhourTab;
	}
	
}
