/**------------------------------------------------------------------------
 *
 *
 *      COPYRIGHT (C)                   ERICSSON RADIO SYSTEMS AB, Sweden
 *
 *      The  copyright  to  the document(s) herein  is  the property of
 *      Ericsson Radio Systems AB, Sweden.
 *
 *      The document(s) may be used  and/or copied only with the written
 *      permission from Ericsson Radio Systems AB  or in accordance with
 *      the terms  and conditions  stipulated in the  agreement/contract
 *      under which the document(s) have been supplied.
 *
 *------------------------------------------------------------------------
 */
package com.ericsson.eniq.techpacksdk.view.etlSetHandling;

import javax.swing.JFrame;

import org.jdesktop.application.SingleFrameApplication;

import tableTree.TableTreeComponent;

import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;


/**
 * This factory class is used for creating a set wizard which is displayed to the user in techpack IDE.
 * The set wizard displayed is dependent on the type of techpack
 * 
 * @author epaujor
 * 
 */
public class TechpackSetWizardViewFactory {

  /**
   * Creates the specific set wizard set and returns it as a instance of the superclass, TechpackSetWizardView.
   * 
   * @param application
   * @param dataModelController
   * @param myTTC
   * @param frame
   * @param fFrame
   * @param setName
   * @param setVersion
   * @param versionid
   * @param setType
   * @param editable
   * @return
   */
  public static TechpackSetWizardView createTechpackSetWizardView(final SingleFrameApplication application,
      final DataModelController dataModelController, final TableTreeComponent myTTC, final JFrame frame,
      final JFrame fFrame, final String setName, final String setVersion, final String versionid, final String setType,
      final boolean editable) {
    
    if(setType.equals(Constants.ENIQ_EVENT) && setName.contains(Constants.SONV)){
      return new SonvEventsTechpackSetWizardView(application, dataModelController, myTTC, frame, fFrame, setName,
          setVersion, versionid, setType, editable);
    } else if (setType.equals(Constants.ENIQ_EVENT)) {
      return new EventsTechpackSetWizardView(application, dataModelController, myTTC, frame, fFrame, setName,
          setVersion, versionid, setType, editable);
    }

    return new TechpackSetWizardView(application, dataModelController, myTTC, frame, fFrame, setName, setVersion,
        versionid, setType, editable);
  }
}
