package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;

import ssc.rockfactory.RockException;

import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.TPActivationModifiedEnum;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.nonmigrated.NonMigratedManageBusyHourView;

import com.ericsson.eniq.techpacksdk.view.etlSetHandling.ETLSetHandlingView;


@SuppressWarnings("serial")
public class GeneralTechPackTab extends JPanel {

   private static final Logger logger = Logger.getLogger(GeneralTechPackTab.class.getName());

  JFrame frame;

  private final SingleFrameApplication application;

  boolean editable = true;

  DataModelController dataModelController;

  JTable tpT;

  JTable tppedencyT;

  JTabbedPane tabbedPanel;

  private String techpackType;

  private ManageGeneralPropertiesView generalTab;

  private String techpackName;
  
  //private Versioning versioning;
  
  protected CreateTabFactory tabFactory;
  
  private String clickedTab ;
  
  private int selectedIndex ;
  
  private Thread thr ; 

  private boolean isRunning = false ;
  
  public static boolean viewClosed;
  public static boolean editOpened;
  
	/**
	 * Used for testing only.
	 * @param application
	 */
  public GeneralTechPackTab(final SingleFrameApplication application, final JTabbedPane dummytabPanel){
		this.application = application;
		this.tabbedPanel = dummytabPanel;
  }


  public GeneralTechPackTab(final SingleFrameApplication application, final DataModelController dataModelController,
      final Versioning versioning, final JFrame frame, final boolean editable) {
    super(new GridBagLayout());
    

    this.frame = frame;
    this.editable = editable;
    this.application = application;
    this.dataModelController = dataModelController;
    //this.versioning=versioning;
    tabbedPanel = new JTabbedPane();
    tabbedPanel.setTabPlacement(JTabbedPane.TOP);
    
    // Populating Tabs with null objects
    this.generalTab = new ManageGeneralPropertiesView(this.application, this.dataModelController, versioning,
          this.editable, this, this.frame);
    tabbedPanel.addTab("General", null, generalTab);
    tabbedPanel.addTab("Measurement Types", null);    
    tabbedPanel.addTab("Busy Hours", null);
    tabbedPanel.addTab("Sets/Actions/Schedulings", null);
    tabbedPanel.addTab("Data Formats", null);
    tabbedPanel.addTab("Reference Types", null);
    tabbedPanel.addTab("Transformers", null);
    tabbedPanel.addTab("External Statements", null);
    tabbedPanel.addTab("Universe", null);
    tabbedPanel.addTab("Verification Objects", null);
    tabbedPanel.addTab("Verification Conditions", null);
    tabbedPanel.addTab("Group Types", null);
    tabbedPanel.addMouseListener(new TabsListener());
    
     //EQEV-4399(start)
    this.frame.addWindowListener(new EditWindowListener());
    this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //EQEV-4399(end)
    this.tabFactory = new CreateTabFactory(application, dataModelController, versioning, frame, this, editable);

    if(editable){
    	this.setEditIsOpened(true);
    }
//
//    this.generalTab = new ManageGeneralPropertiesView(this.application, this.dataModelController, versioning,
//        this.editable, this, this.frame);
//    tabbedPanel.addTab("General", null, generalTab);
//
//    final ManageMeasurementView measurementTab = new ManageMeasurementView(this.application, this.dataModelController,
//        versioning, this, frame);
//    tabbedPanel.addTab("Measurement Types", null, measurementTab);
//
//		final Component busyhourTab = getBusyhourTab(dataModelController, versioning, editable, application, frame, this);		
//		tabbedPanel.addTab("Busy Hours", null, busyhourTab);
//
//    // Maybe in future...
//    // final ManageAggregationView aggregationTab = new
//    // ManageAggregationView(this.application, this.dataModelController,
//    // versioning, this, frame);
//    // tabbedPanel.addTab("Aggregations", null, aggregationTab);
//		
//		// Get the set type for the meta_collection_sets element ( stored in the sets .xml file in the .tpi package).
//    final int setType = getSetType(versioning.getTechpack_name(), versioning.getTechpack_type());
//		
//    tabbedPanel.addTab("Sets/Actions/Schedulings", null, new ETLSetHandlingView(application, dataModelController,
//        versioning.getTechpack_name(), versioning.getTechpack_version(), versioning.getVersionid(), versioning
//            .getTechpack_type(), setType, this.editable, this, frame));
//
//    tabbedPanel.addTab("Data Formats", null, new DataformatHandlingView(application, dataModelController, versioning
//        .getTechpack_name(), versioning.getTechpack_version(), versioning.getVersionid(),
//        versioning.getTechpack_type(), setType, this.editable, this, frame));
//
//    final ManageReferenceView referenceTab = new ManageReferenceView(this.application, this.dataModelController,
//        versioning, this, frame);
//    tabbedPanel.addTab("Reference Types", null, referenceTab);
//
//    final ManageTransformerView transformerTab = new ManageTransformerView(this.application, this.dataModelController,
//        versioning, this, frame);
//    tabbedPanel.addTab("Transformers", null, transformerTab);
//
//    // Prompts can not be defined in ENIQ 2.0
//    // final ManagePromptView promptTab = new ManagePromptView(this.application,
//    // this.dataModelController, versioning,
//    // this, frame);
//    // tabbedPanel.addTab("Prompt Implementors", null, promptTab);
//
//    final ManageExternalStatementView externalstatementTab = new ManageExternalStatementView(this.application,
//        this.dataModelController, versioning, this.editable, this, this.frame);
//    tabbedPanel.addTab("External Statements", null, externalstatementTab);
//
//    final UniverseTabs universeTabs = new UniverseTabs(this.application, this.dataModelController, versioning, this,
//        this.editable, this.frame);
//    tabbedPanel.addTab("Universe", null, universeTabs);
//
//    final ManageVerificationObjectView verificationobjectTab = new ManageVerificationObjectView(this.application,
//        this.dataModelController, versioning, this, frame);
//    tabbedPanel.addTab("Verification Objects", null, verificationobjectTab);
//
//    final ManageVerificationConditionView verificationconditionTab = new ManageVerificationConditionView(
//        this.application, this.dataModelController, versioning, this, frame);
//    tabbedPanel.addTab("Verification Conditions", null, verificationconditionTab);
//    
//    final ManageGroupView groupTab = new ManageGroupView(this.application, this.dataModelController, versioning, this,
//            frame);
//    tabbedPanel.addTab("Group Types", null, groupTab);


    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridheight = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;

    this.add(tabbedPanel, gbc);

    // Store the teckpack type for being able to enable different tabs for base
    // and normal techpacks.
    this.techpackType = versioning.getTechpack_type();
    this.techpackName = versioning.getTechpack_name();

    // Enable (only) the tabs that should be enabled for this techpack.
    enableTabs();
  }

  /**
   * Gets the set type for the tech pack. 
   * This is the "type" of the meta_collections_sets in the sets .xml file in the .tpi package.
   * 
   * Default value is TECHPACK. Special case if the tech pack is the ALARMINTERFACES tech pack,  
   * and of type System, the set type should be MAINTENANCE.
   * @param   techPackName  The tech pack name.
   * @param   techPackType  The tech pack type.
   * @return  setType       The set type as an integer.
   */
//  protected int getSetType(final String techPackName, final String techPackType) {
//    int setType = 0;
//    if (techPackName.equalsIgnoreCase(Constants.ALARM_INTERFACES_TECHPACK_NAME)
//        && techPackType.equalsIgnoreCase(Constants.SYSTEM_TECHPACK)) {
//      setType = ETLSetHandlingView.MAINTENANCE;
//    } else {
//      setType = ETLSetHandlingView.TECHPACK;
//    }
//    return setType;
//  }

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
	public static Component getBusyhourTab(final DataModelController dmc, final Versioning ver, final boolean edit,
																				 final SingleFrameApplication app, final JFrame frame, final GeneralTechPackTab tpTab){
		final Busyhour where = new Busyhour(dmc.getRockFactory());
		where.setVersionid(ver.getVersionid());
		boolean oldFormat = false;
		try{
			final BusyhourFactory fac = new BusyhourFactory(dmc.getRockFactory(), where);
			final List<Busyhour> bhl = fac.get();
			oldFormat = !bhl.isEmpty() && (bhl.get(0).getPlaceholdertype() == null || bhl.get(0).getPlaceholdertype().length() == 0);
		} catch (Exception e){
			logger.warning(e.getMessage());
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

  public boolean isEditable() {
    return editable;
  }

  /**
   * close dialog action
   * 
   * @return
   */
  @Action
  public void closeDialog() {
	  
	  // EQEV - 2945
	  String frameName = this.frame.getName();
	  if (frameName.equalsIgnoreCase("EditTechPackWindow")){
		  this.setEditIsOpened(false);
	  }
	  
	  //20110201,EANGUAN,HN44746:To clear all the data structures used
	  // before closing the View/Edit Window
	  this.dataModelController = null ;
	  if(tpT != null){
		  tpT.removeAll();
		  tpT = null ;
	  }	  
	  if(tppedencyT != null){
		  tppedencyT.removeAll() ;
		  tppedencyT = null ;
	  }
	  if(tabbedPanel != null){
		  tabbedPanel.removeAll();
		  tabbedPanel = null ;
	  }
	  if(generalTab != null ){
		  generalTab.removeAll();
		  generalTab = null;
	  }
	  techpackName = null ;
	  techpackType = null ;
	  if(this.frame != null){
	  
		  this.frame.dispose();
	//EQEV-4399 (START)
		  //this.frame.removeAll();
		  //this.frame = null;
   //EQEV-4399(END)
	  }
	  com.ericsson.eniq.techpacksdk.ManageTechPackView.twist=false; //EQEV-4298 SIMILAR TO TECHPACK
	  //20110201,EANGUAN,HN44746: The below statement is introduced to just
	  //inform the JVM that Garbage collection is needed. It is not sure
	  //that JVM will do it right now or defer it.
	  System.gc();
  }
  //EQEV-4399(START)
  public void closeDialogForListener() {
	  
	 
	  
	  String frameName = this.frame.getName();
	
	  if (frameName.equalsIgnoreCase("EditTechPackWindow")){
		  this.setEditIsOpened(false);
	  }
	 
	  
	  //20110201,EANGUAN,HN44746:To clear all the data structures used
	  // before closing the View/Edit Window
	  this.dataModelController = null ;
	  if(tpT != null){
		  tpT.removeAll();
		  tpT = null ;
	  }	  
	  if(tppedencyT != null){
		  tppedencyT.removeAll() ;
		  tppedencyT = null ;
	  }
	  if(tabbedPanel != null){
		  tabbedPanel.removeAll();
		  tabbedPanel = null ;
	  }
	  if(generalTab != null ){
		  generalTab.removeAll();
		  generalTab = null;
	  }
	  techpackName = null ;
	  techpackType = null ;
	  if(this.frame != null){
		
		this.frame.removeAll();
		  
	  }
	  
	  //20110201,EANGUAN,HN44746: The below statement is introduced to just
	  //inform the JVM that Garbage collection is needed. It is not sure
	  //that JVM will do it right now or defer it.
	  System.gc();
  }
  //EQEV-4399(END)

  /**
   * disableTabs
   * 
   * @return
   */
  @Action
  public void disableTabs() {
	  if(!this.editable){
		  return;
	  }
	  for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
      tabbedPanel.setEnabledAt(i, false);
    }
  }

  /**
   * enableTabs
   * 
   * @return
   */
  @Action
  public void enableTabs() {

    // Get the universe name for this techpack.
    // String universeName = this.generalTab.unvnameF.getText();

    // Loop through all the tabs and enable all (with some exceptions)
	  for (int i = 0; i < tabbedPanel.getTabCount(); i++) {
		  final String title = tabbedPanel.getTitleAt(i);
		  // For base techpack, leave some tabs disabled. For other techpacks,
		  // enable all tabs.
		  if (this.techpackType.equalsIgnoreCase("base")) {
		    if (title.equals("Busy Hours") || title.equals("Sets/Actions/Schedulings") || title.equals("Data Formats")
		        || title.equals("Transformers") || title.equals("Verification Objects")
		        || title.equals("Verification Conditions")) {
		      tabbedPanel.setEnabledAt(i, false);
		    } else{
		    	tabbedPanel.setEnabledAt(i, true);
		    }
		  }
		  else if(title.equals("Verification Objects")
			        || title.equals("Verification Conditions")){
			  tabbedPanel.setEnabledAt(i, false);
		  }
		  else {
			  tabbedPanel.setEnabledAt(i, true);
		  }
        
      /*
       * Commented the code of disabling the Universe tab ,for IDE #667, Base
       * TechPack should be possible to configure different Universe Tables
       * (example Formulas),24/07/09, ejohabd
       */
      // Universe tab is disabled if universe name has not been defined.
      /*
       * if (universeName == null || (universeName != null &&
       * universeName.length() == 0)) { if (title.equals("Universe")) {
       * tabbedPanel.setEnabledAt(i, false); } }
       */
    }
  }

  /**
   * This method is called whenever a Save button is pressed in the tabbed panes.
   * @param modifiedBusyhour
   * @throws SQLException
   * @throws RockException
   */
  public void setTPActivationModified(final TPActivationModifiedEnum modifiedBusyhour) throws SQLException, RockException {
    final Tpactivation tpa = getActiveVersion();

    if (tpa!=null){
      switch (modifiedBusyhour){
      case MODIFIED_BUSYHOUR:
        //only allowed set it, if it has an equal or lower priority
        if(tpa.getModified() <= 1){ 
          tpa.setModified(1);
          tpa.saveToDB();
        }
        break;
      case MODIFIED_OTHER:
        tpa.setModified(2);
        tpa.saveToDB();
        break;
      }
    }
  }
  
  private Tpactivation getActiveVersion() {

    try {

      final Tpactivation tpa = new Tpactivation(dataModelController.getRockFactory());
      tpa.setTechpack_name(techpackName);
           
      final TpactivationFactory tpaF = new TpactivationFactory(dataModelController.getRockFactory(), tpa);
      
      if (tpaF==null || tpaF.size()==0){
        return null;
      }  
      
      final Tpactivation activeTpa = tpaF.getElementAt(0);
         return activeTpa;  
      
    } catch (final Exception e) {
      ExceptionHandler.instance().handle(e);      
      e.printStackTrace();
    }

    return null;
  }
  
  // 20110201,EANGUAN,HN44746:To get the JTabbedPane in TestCase GeneralTechPackTabTest
  protected JTabbedPane getJTabbedPane(){
	  return tabbedPanel ;
  }
  
  //20110201,EANGUAN,HN44746:To get the ManageGeneralPropertiesView in TestCase GeneralTechPackTabTest
  protected ManageGeneralPropertiesView getManageGeneralPropertiesView(){
	  return generalTab ;
  }
  
  /**
   * Gets the set type for the tech pack. 
   * This is the "type" of the meta_collections_sets in the sets .xml file in the .tpi package.
   * 
   * Default value is TECHPACK. Special case if the tech pack is the ALARMINTERFACES tech pack,  
   * and of type System, the set type should be MAINTENANCE.
   * @param   techPackName  The tech pack name.
   * @param   techPackType  The tech pack type.
   * @return  setType       The set type as an integer.
   */
  protected static int getSetType(final String techPackName, final String techPackType) {
    int setType = 0;
    if (techPackName.equalsIgnoreCase(Constants.ALARM_INTERFACES_TECHPACK_NAME)
        && techPackType.equalsIgnoreCase(Constants.SYSTEM_TECHPACK)) {
      setType = ETLSetHandlingView.MAINTENANCE;
    } else {
      setType = ETLSetHandlingView.TECHPACK;
    }
    return setType;
  }

  public void createAllDataModelsIfNotCreated(){
  	for(int i=0;i<tabbedPanel.getTabCount();i++){
  		String currentTabTitle=tabbedPanel.getTitleAt(i);		  	
  	  	 if (tabbedPanel.getComponentAt(i) == null){	  		
  	  		tabbedPanel.setComponentAt(i, tabFactory.createTab(currentTabTitle));	  		
  	  	}
  	}
  }
   @Action
   public void disableAllTabs() {
  	for(int i=0;i<tabbedPanel.getTabCount();i++){	  	
  	  	 //if (tabbedPanel.getComponentAt(i) == null){
  	  		tabbedPanel.setEnabledAt(i, false);
  	  	//}
  	}
  }
   
// this listener is used to create tabs upon user click
   private class TabsListener implements MouseListener{
  	 @Override
  	 public void mouseClicked(MouseEvent e) {
  		 
  	 }

  		@Override
  		public void mousePressed(MouseEvent e) {
  			
  			if (e.getButton() == 1){
  				clickedTab = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());
  	  			selectedIndex = tabbedPanel.getSelectedIndex() ;
  	  			if(!isRunning){
  	  				thr = new Thread(new BlockInput());
  	  	  			isRunning = true ;
  	  	  		    thr.start();
  	  			}
  			}
  		}

  		@Override
  		public void mouseReleased(MouseEvent e) {
  			//TODO
  			
  		}

  		@Override
  		public void mouseEntered(MouseEvent e) {
  			// TODO Auto-generated method stub
  		}

  		@Override
  		public void mouseExited(MouseEvent e) {
  			// TODO Auto-generated method stub
  		}
  	}
   
   class BlockInput implements Runnable{

	@Override
	public void run() {
				
				//System.out.println(" -----------------> Running Thread " + Thread.currentThread().getId() + "<-----------");
				if (tabbedPanel.getComponentAt(selectedIndex) == null){
			  		try{
			  			//System.out.println(" Clciked Tab: " + clickedTab);
						//System.out.println(" Index Tab: " + selectedIndex);
						disableAllTabs();
		  				tabbedPanel.setEnabledAt(selectedIndex, true);
		  				
		  				frame.getGlassPane().setVisible(true);
			  			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			  			logger.info("Creating " + clickedTab + " Tab");
				  		tabbedPanel.setComponentAt(tabbedPanel.getSelectedIndex(), tabFactory.createTab(clickedTab));
				  		frame.getGlassPane().setVisible(false);
			  			frame.setCursor(Cursor.getDefaultCursor());		  		
			  			enableTabs();
			  			selectedIndex = 0 ;
			  			clickedTab = null;
			  		}catch(Exception e){
			  			// Nothing to do
			  			//System.out.println("exception in thread intruption"+e.getMessage());
			  		}
				}
				//System.out.println(" -----------------> Ending Thread " + Thread.currentThread().getId() + "<-----------");
				isRunning = false ;
	}
   }// class BlockInput
   
   // EQEV-2945
   public static void  setViewIsClosed(boolean view)
   {
	   viewClosed = view;
   }

   public static boolean  getViewIsClosed()
   {
       return viewClosed;
   }
   
   public static void  setEditIsOpened(boolean edit)
   {
	   editOpened = edit;
   }

   public static boolean  getEditIsOpened()
   {
       return editOpened;
   }
   //EQEV-4399(start)
  private class EditWindowListener implements WindowListener
  {

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		closeDialogForListener();
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	  
  }
	   
//EQEV-4399(end)   
}
