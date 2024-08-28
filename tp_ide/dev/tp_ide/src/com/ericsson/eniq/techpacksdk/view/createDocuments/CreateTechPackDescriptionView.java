package com.ericsson.eniq.techpacksdk.view.createDocuments;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.BusyIndicator;
import com.ericsson.eniq.techpacksdk.BusyIndicatorInputBlocker;
import com.ericsson.eniq.techpacksdk.TechPackIDE;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class CreateTechPackDescriptionView extends JPanel {

  final private Logger logger = Logger.getLogger(CreateTechPackDescriptionView.class.getName());

  JTextField wdir;

  JFrame frame;

  ResourceMap resourceMap;

  Properties props;

  Application application;

  Versioning current;

  DataModelController dmc;

  public CreateTechPackDescriptionView(SingleFrameApplication application, DataModelController dataModelController,
      JFrame frame) {
    init(application, frame, dataModelController);
    setLayout(new GridBagLayout());

    final GridBagConstraints c = new GridBagConstraints();

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(10, 10, 10, 10);
    c.gridwidth = 1;
    c.gridy = 1;

    // add(new JLabel(resourceMap.getString("LoginPanel.server")), c);
    add(new JLabel("TP Directory"), c);

    c.gridx = 1;
    c.weightx = 1;

    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;

    c.gridx = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    Properties props = getConProps();
    String server = props.getProperty("lastServer", "");
    wdir = new JTextField(props.getProperty(server + ".lastworkingdir", System.getProperty("user.home")), 35);
    wdir.setEditable(false);
    add(wdir, c);

    c.gridx = 2;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.weightx = 1;
    add(new JButton(application.getContext().getActionMap(this).get("selectworkingdir")), c);

    // ************** buttons **********************

    c.gridy++;
    c.gridx = 1;
    c.fill = GridBagConstraints.NONE;

    JButton create = new JButton("Create");
    create.setName("CreateNewTechpack");
    create.setAction(application.getContext().getActionMap(this).get("createTPDescription"));
    create.setActionCommand("create");
    create.setToolTipText("Create");
    add(create, c);

    c.gridx = 2;
    JButton close = new JButton("Close");
    close.setName("Close");
    close.setActionCommand("close");
    close.setAction(application.getContext().getActionMap(this).get("closeCreateTPDescription"));
    close.setToolTipText("Close");
    close.setName("Close");
    add(close, c);

  }

  /**
   * Inits the members
   * 
   * @param app
   * @param frame
   */
  private void init(Application app, JFrame frame, DataModelController dmc) {
    this.application = app;
    this.frame = frame;
    this.dmc = dmc;
    // dmc.getDwhRockFactory()
    resourceMap = app.getContext().getResourceMap(getClass());
    props = getConProps();
  }

  /**
   * Gets connection properties
   * 
   * @return
   */
  private Properties getConProps() {
    try {
      Properties props = Utils.getProperties(TechPackIDE.CONPROPS_FILE);
      return props;
    } catch (FileNotFoundException e) {
      logger.info(TechPackIDE.CONPROPS_FILE + " does not exists");
    } catch (Exception e) {
      logger.log(Level.WARNING, "Unable to read connection.properties", e);
    }
    return new Properties();
  }

  /**
   * Sets the current Object for this view. Current object is handle to techpack
   * we are generating documents from.
   */
  public void setCurrent(Versioning current) {
    this.current = current;
    //String i = ((Versioning) current).getTechpack_name();
  }

  // ************** Actions **********************

  /**
   * Action to open the fileChooser
   * 
   */
  @Action
  public void selectworkingdir() {

    while (true) {
      final JFileChooser jfc = new JFileChooser(new File(wdir.getText()));
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setFileFilter(new FileFilter() {

        public boolean accept(File f) {
          return f.isDirectory() && f.exists() && f.canWrite();
        }

        public String getDescription() {
          return resourceMap.getString("LoginPanel.filedescription");
        }
      });

      final int returnVal = jfc.showDialog(this, resourceMap.getString("LoginPanel.filechoose"));

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File f = jfc.getSelectedFile();

        if (!f.isDirectory() || !f.exists() || !f.canWrite()) {
          JOptionPane.showMessageDialog(frame, resourceMap.getString("Login.workingdirerror.message"), resourceMap
              .getString("Login.workingdirerror.title"), JOptionPane.ERROR_MESSAGE);
          continue;
        }

        wdir.setText(f.getAbsolutePath());

      }
      break;
    }
  }

  /**
   * Action to do the work
   * 
   */
  @Action
  public Task createTPDescription() {

    final Task ctpdTask = new CreateTechPackDescriptionTask(wdir.getText(), current, dmc.getRockFactory(), application,
        logger);

    BusyIndicator busyIndicator = new BusyIndicator();

    frame.setGlassPane(busyIndicator);

    ctpdTask.setInputBlocker(new BusyIndicatorInputBlocker(ctpdTask, busyIndicator));

    return ctpdTask;
  }

  /**
   * Action to close the frame
   * 
   */
  @Action
  public void closeCreateTPDescription() {
    logger.log(Level.INFO, "Closed CreateTechPackDescriptionView");
    frame.dispose();
  }
}
