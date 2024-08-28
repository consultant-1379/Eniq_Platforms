package com.ericsson.eniq.techpacksdk;

import com.distocraft.dc5000.dwhm.StorageTimeAction;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtechpacks;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyHourData;
import com.ericsson.eniq.techpacksdk.view.busyhourtree.BusyhourTreeModel;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Task to reactivate the BH placeholder views (and delete them if the placeholder is deleted/cleared).
 *
 */
public class ActivateBHTask extends Task<Void, Void> {
    /**
     * DataModelController instance from TpIde
     */
    private final DataModelController dataModelController;
    /**
     * A logger instance
     */
    private final Logger logger;
    /**
     * The parent panel. Used to get the current selected node.
     */
    private final ManageDWHTab parentPanel;
    /**
     * Application context
     */
    private final SingleFrameApplication application;

    private int viewsRecreated = 0;

    /**
     * Default constructor
     * @param app An Application context
     * @param dmc DataModelController from TpIde
     * @param parent The parent DWHTab
     * @param log Parent logger instance.
     */
    public ActivateBHTask(final SingleFrameApplication app, final DataModelController dmc,
                          final ManageDWHTab parent, final Logger log) {
        super(app);
        parentPanel = parent;
        dataModelController = dmc;
        logger = log;
        application = app;
    }

    /**
     * Warning to the use that the views will be reactivated. Placed in a method on its own so it can
     * be mocked in tests.
     * @return JOptionPane.YES_OPTION is OK selected, JOptionPane.NO_OPTION otherwise.
     */
    protected int askToConfirm() {
        return JOptionPane.showConfirmDialog(application.getMainFrame(),
                "Are you sure that you want to activate the BH", "Activate Busy Hour in DWHM?",
                JOptionPane.YES_NO_OPTION);
    }
    /**
     * Execute the view recreation or deletions. 
     */
    @Override
    protected Void doInBackground() throws Exception {
        logger.log(Level.FINEST, "Reactivation of Busy Hour Views started.");
        int selectedValue = askToConfirm();
        if (selectedValue == JOptionPane.YES_OPTION) {
            logger.log(Level.INFO, "Busy Hour View reactivation started.");
            dataModelController.setAndWaitActiveExecutionProfile("NoLoads");
//          dropBhViews() drops the views only, StorageTimeAction will regenerate the views' creation sql
//          including the drop clause i.e. 'drop view aaaa if exists; create view aaaa as ..........'
            String versionid = ((Versioning) parentPanel.getSelectedNode().getRockDBObject()).getVersionid();
            String techpackName = ((Versioning) parentPanel.getSelectedNode().getRockDBObject()).getTechpack_name();
            final Busyhour where = new Busyhour(dataModelController.getRockFactory());
            where.setVersionid(versionid);
            where.setReactivateviews(1); // only redo modified placeholders*/
            final BusyhourFactory fac = new BusyhourFactory(dataModelController.getRockFactory(), where);
            final List<Busyhour> toDoBh = fac.get();
            logger.log(Level.INFO, "Reactivating " + toDoBh.size() + " views");
            final StorageTimeAction sta = getStorageTimeAction();
            viewsRecreated = 0;
            for (Busyhour bhCheck : toDoBh) {
                recreateView(bhCheck, sta);
            }
            // Update the busy hour counter data.
            final Dwhtechpacks dwhtechpack = new Dwhtechpacks(dataModelController.getRockFactory());
            dwhtechpack.setVersionid(versionid);
            dwhtechpack.setTechpack_name(techpackName);
            sta.updateBHCounters(dwhtechpack);
            sta.updateBHCountersForCustomTechpack(dwhtechpack);
            
            parentPanel.setActivateBHEnabled(false);
            final BusyhourTreeModel check = dataModelController.getBusyhourTreeModel();
            if(check != null){
                // model may not have been created yet i.e. tp hasnt be edited yet...
                check.discard();
            }
            Tpactivation tpa = getActiveVersion(techpackName);
            if(tpa != null){
              tpa.setModified(0);
              tpa.saveDB();
            }
            dataModelController.getBusyhourHandlingDataModel().refresh();
        }
        dataModelController.setAndWaitActiveExecutionProfile("Normal");
        return null;
    }

    private Tpactivation getActiveVersion(final String techpackName) {

      try {

        Tpactivation tpa = new Tpactivation(dataModelController.getRockFactory());
        tpa.setTechpack_name(techpackName);
             
        TpactivationFactory tpaF = new TpactivationFactory(dataModelController.getRockFactory(), tpa);
        
        if (tpaF==null || tpaF.size()==0){
          return null;
        }  
        
        Tpactivation activeTpa = tpaF.getElementAt(0);
           return activeTpa;  
        
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);      
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void succeeded(final Void aVoid) {
        final String msg = viewsRecreated + " views recreated.";
        
        JOptionPane.showMessageDialog(application.getMainFrame(),
            msg, "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void failed(final Throwable throwable) {
        final JLabel lMsg = new JLabel();
        if(throwable instanceof SQLException){
            final SQLException e = (SQLException)throwable;
            final String txt = "<html>SQL Error activating Busy Hour Criteria" +
                    "<ul>" +
                    "<li>&nbsp;&nbsp;&nbsp;&nbsp;<i>SQL Error Code "+e.getErrorCode()+"</i></li>" +
                    "<li>&nbsp;&nbsp;&nbsp;&nbsp;<i>SQL Error State "+e.getSQLState()+"</i></li>" +
                    "</ul>" +
                    "Please check placeholder definitions are ok." +
                    "</html>";
            lMsg.setText(txt);
        } else {
            lMsg.setText(throwable.getMessage());
        }
        logger.log(Level.SEVERE, "Failed to regenerate BusyHour placeholder views", throwable);
        JOptionPane.showMessageDialog(application.getMainFrame(), lMsg,
                application.getContext().getResourceMap().getString("save.error.caption"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Execute the view recreation
     * @param bh The Busyhour defining the view
     * @param sta A StorageTimeAction instance to generate the view
     * @throws Exception If there were any errors
     */
    private void recreateView(final Busyhour bh, final StorageTimeAction sta) throws Exception {
        if (bh.getBhcriteria() == null || bh.getBhcriteria().length() == 0) {
            sta.dropBhRankViews(bh);
        } else {
        	//20110509,eeoidiv,HO15521:If TargetVersionId is not the same as VersionId, do like Custom.
            if (!bh.getVersionid().equals(bh.getTargetversionid())) {
        		//We are looking at a Custom TP.
        		sta.createCustomBhViewCreates(bh);
        	}else{
        		sta.createBhRankViews(bh);
        	}
        }
        viewsRecreated++;
        final List<BusyHourData> mod = dataModelController.getBusyhourHandlingDataModel().
                getBusyHourData(bh.getTargetversionid()); // need to save the one in the model....
        for (BusyHourData bhd : mod) {
            if (bhd.getBusyhour().equals(bh)) {
                final Busyhour bb = bhd.getBusyhour();
                bb.setReactivateviews(0);
                bhd.getBusyhour().updateDB();
                break;
            }
        }
        dataModelController.getBusyhourHandlingDataModel().setBusyHourData(mod);
    }

    /**
     * Get a reference to StorageTimeAction.
     * In a method on its own to make the tests easier.
     * @return StorageTimeAction instance
     * @throws Exception If there were errors creating StorageTimeAction.
     */
    protected StorageTimeAction getStorageTimeAction() throws Exception {
        return new StorageTimeAction(dataModelController.getDwhRockFactory(),
                dataModelController.getRockFactory(), logger);
    }
}
