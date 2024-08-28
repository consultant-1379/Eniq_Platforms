package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.SupportedvendorreleaseFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class GeneralTechPackDataModel extends DefaultTreeModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(ManageGeneralPropertiesView.class.getName());

  public String vrVersionid;

  class LicenseTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<String> rowData = new Vector<String>();

    public LicenseTableModel(List<String> columnNames, Versioning obj) {

      this.columnNames = columnNames;

      try {
        if (obj != null) {
          Versioning v = new Versioning(etlRock, true);
          v.setVersionid(obj.getVersionid());
          VersioningFactory vF = new VersioningFactory(etlRock, v, true);
          Iterator<Versioning> iter = vF.get().iterator();

          while (iter.hasNext()) {
            Versioning ver = (Versioning) iter.next();
            String licensenamesStr = ver.getLicensename();
            if (licensenamesStr != null) {
              String[] names = licensenamesStr.split(",");
              for (int i = 0; i < names.length; i++) {
                rowData.add(names[i]);
              }
            }
          }
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public String getValueAt(int row, int col) {
      return rowData.get(row);
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    public void setValueAt(Object value, int row, int col) {

      try {

        rowData.set(row, (String) value);
        this.fireTableChanged(null);

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

      rowData.add("");
      this.fireTableChanged(null);
    }

    public void addNewRow(String row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      rowData.remove(row);
      this.fireTableChanged(null);
    }

    public String getRow(int row) {
      return rowData.get(row);
    }
  }

  class UETableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<Universename> rowData = new Vector<Universename>();

    private Vector<Universename> removedData = new Vector<Universename>();

    public UETableModel(List<String> columnNames, Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
          Universename un = new Universename(etlRock);

          String vrVersionid = ((Versioning) obj).getVersionid();
          un.setVersionid(vrVersionid);
          UniversenameFactory unF = new UniversenameFactory(etlRock, un, true);
          Iterator<Universename> iter = unF.get().iterator();

          while (iter.hasNext()) {
            Universename tmpUn = (Universename) iter.next();
            rowData.add((Universename) tmpUn.clone());
          }
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Universename> getDeletedRows() {
      return removedData;
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(int row, int col) {

      Universename unvext = rowData.get(row);

      // Universwe extension
      if (col == 0) {
        return unvext.getUniverseextension();
      }
      if (col == 1) {
        return unvext.getUniverseextensionname();
      }
      return null;
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    public void setValueAt(Object value, int row, int col) {

      try {

        Universename uname = rowData.get(row);

        if (col == 0) {
          uname.setUniverseextension((String) value);
          this.fireTableChanged(null);
        }
        if (col == 1) {
          uname.setUniverseextensionname((String) value);
          this.fireTableChanged(null);
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

      Universename unvext = new Universename(etlRock, true);
      unvext.setUniverseextension("");
      unvext.setUniverseextensionname("");
      rowData.add(unvext);

      this.fireTableChanged(null);
    }

    public void addNewRow(Universename row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      removedData.add((Universename) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Universename getRow(int row) {
      return rowData.get(row);
    }
  }

  class VRTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<Supportedvendorrelease> rowData = new Vector<Supportedvendorrelease>();

    private Vector<Supportedvendorrelease> removedData = new Vector<Supportedvendorrelease>();

    public VRTableModel(List<String> columnNames, Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
          Supportedvendorrelease vr = new Supportedvendorrelease(etlRock);

          String vrVersionid = ((Versioning) obj).getVersionid();
          vr.setVersionid(vrVersionid);
          SupportedvendorreleaseFactory vrF = new SupportedvendorreleaseFactory(etlRock, vr, true);
          Iterator<Supportedvendorrelease> iter = vrF.get().iterator();

          while (iter.hasNext()) {
            Supportedvendorrelease tmpVr = (Supportedvendorrelease) iter.next();
            rowData.add((Supportedvendorrelease) tmpVr.clone());
          }
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Supportedvendorrelease> getDeletedRows() {
      return removedData;
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(int row, int col) {

      Supportedvendorrelease venrel = rowData.get(row);

      // Vendor release
      if (col == 0) {
        return venrel.getVendorrelease();
      }
      return null;
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    public void setValueAt(Object value, int row, int col) {

      try {

        Supportedvendorrelease vrel = rowData.get(row);

        if (col == 0) {
          vrel.setVendorrelease((String) value);
          this.fireTableChanged(null);
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

      Supportedvendorrelease venrel = new Supportedvendorrelease(etlRock, true);
      venrel.setVendorrelease("");
      rowData.add(venrel);

      this.fireTableChanged(null);
    }

    public void addNewRow(String txt) {

      Supportedvendorrelease venrel = new Supportedvendorrelease(etlRock, true);
      venrel.setVendorrelease(txt);
      rowData.add(venrel);

      this.fireTableChanged(null);
    }

    public void addNewRow(Supportedvendorrelease row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      removedData.add((Supportedvendorrelease) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Supportedvendorrelease getRow(int row) {
      return rowData.get(row);
    }
  }

  class TPDTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<Techpackdependency> rowData = new Vector<Techpackdependency>();

    private Vector<Techpackdependency> removedData = new Vector<Techpackdependency>();

    public TPDTableModel(List<String> columnNames, Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
          Techpackdependency tpd = new Techpackdependency(etlRock);

          String vrVersionid = ((Versioning) obj).getVersionid();
          tpd.setVersionid(vrVersionid);
          TechpackdependencyFactory tpdF = new TechpackdependencyFactory(etlRock, tpd, true);
          Iterator<Techpackdependency> iter = tpdF.get().iterator();

          while (iter.hasNext()) {
            Techpackdependency tmpTpd = (Techpackdependency) iter.next();
            rowData.add((Techpackdependency) tmpTpd.clone());
          }
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Techpackdependency> getDeletedRows() {
      return removedData;
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(int row, int col) {

      Techpackdependency tpdep = rowData.get(row);

      // Tech Pack name
      if (col == 0) {
        return tpdep.getTechpackname();
      }

      if (col == 1) {
        return tpdep.getVersion();
      }
      return null;
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    public void setValueAt(Object value, int row, int col) {

      try {

        Techpackdependency tpdep = rowData.get(row);

        if (col == 0) {
          tpdep.setTechpackname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          tpdep.setVersion((String) value);
          this.fireTableChanged(null);
        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

      Techpackdependency techpackdep = new Techpackdependency(etlRock, true);
      techpackdep.setTechpackname("");
      techpackdep.setVersion("");
      rowData.add(techpackdep);
      this.fireTableChanged(null);
    }

    public void addNewRow(Techpackdependency row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      removedData.add((Techpackdependency) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Techpackdependency getRow(int row) {
      return rowData.get(row);
    }
  }

  RockFactory etlRock;

  private UETableModel uetm;

  private TPDTableModel tpdtm;

  private VRTableModel vrtm;

  private LicenseTableModel licmue;

  DataModelController dataModelController = null;

  Versioning versioning;

  Techpackdependency techpackdependency;

  Supportedvendorrelease vendorrelease;

  Universename universename;

  public GeneralTechPackDataModel(RockFactory etlRock) {
    super(null);

    this.tpdtm = updateTPDTableModel(null);
    this.licmue = updateLicenseTableModel(null);
    this.vrtm = updateVRTableModel(null);
    this.uetm = updateUETableModel(null);
    this.etlRock = etlRock;
  }

  public UETableModel getUnvExtTableModel() {
    return uetm;
  }

  public VRTableModel getVRTableModel() {
    return vrtm;
  }

  public TPDTableModel getTPDepedencyTableModel() {
    return tpdtm;
  }

  public LicenseTableModel getLicenseTableModel() {
    return licmue;
  }

  public String[] getTechPacks() {

    try {

      Versioning ver = new Versioning(etlRock);
      VersioningFactory verF = new VersioningFactory(etlRock, ver);

      String[] result = new String[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
        Versioning v = (Versioning) verF.get().get(i);
        String version = v.getVersionid().substring(v.getVersionid().indexOf(":") + 1);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version() + ":" + version);
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  private void saveVRTableModel() throws Exception {

    Vector<Supportedvendorrelease> deleted = dataModelController.getEditGeneralTechPackDataModel().vrtm
        .getDeletedRows();

    // remove
    while (deleted.size() > 0) {
      try {
        deleted.remove(0).deleteDB();
      } catch (Exception e) {
        // ignored
      }
    }

    int row = dataModelController.getEditGeneralTechPackDataModel().vrtm.getRowCount();

    for (int r = 0; r < row; r++) {
      dataModelController.getEditGeneralTechPackDataModel().vrtm.getRow(r).saveToDB();
    }
  }

  private void saveUETableModel() throws Exception {

    Vector<Universename> deleted = dataModelController.getEditGeneralTechPackDataModel().uetm.getDeletedRows();

    // remove
    while (deleted.size() > 0) {
      try {
        deleted.remove(0).deleteDB();
      } catch (Exception e) {
        // ignored
      }
    }

    int row = dataModelController.getEditGeneralTechPackDataModel().uetm.getRowCount();

    for (int r = 0; r < row; r++) {
      dataModelController.getEditGeneralTechPackDataModel().uetm.getRow(r).saveToDB();
    }
  }

  private void saveTPDTableModel() throws Exception {

    Vector<Techpackdependency> deleted = dataModelController.getEditGeneralTechPackDataModel().tpdtm.getDeletedRows();

    // remove
    while (deleted.size() > 0) {
      try {
        deleted.remove(0).deleteDB();
      } catch (Exception e) {
        // ignored
      }
    }

    int row = dataModelController.getEditGeneralTechPackDataModel().tpdtm.getRowCount();

    for (int r = 0; r < row; r++) {
      dataModelController.getEditGeneralTechPackDataModel().tpdtm.getRow(r).saveToDB();
    }
  }

  private UETableModel updateUETableModel(Versioning obj) {

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Universe Extension");
    columnNames.add("Universe Extension Name");
    UETableModel uetm = new UETableModel(columnNames, obj);
    return uetm;
  }

  private VRTableModel updateVRTableModel(Versioning obj) {

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Vendor Release");
    VRTableModel vrtm = new VRTableModel(columnNames, obj);
    return vrtm;
  }

  private TPDTableModel updateTPDTableModel(Versioning obj) {

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("R-State");
    TPDTableModel tpdtm = new TPDTableModel(columnNames, obj);
    return tpdtm;
  }

  private LicenseTableModel updateLicenseTableModel(Versioning obj) {

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("License Name");
    LicenseTableModel tpdtm = new LicenseTableModel(columnNames, obj);
    return tpdtm;
  }

  public void refresh(Versioning versioning) {
    try {

      this.versioning = versioning;
      this.uetm = updateUETableModel(versioning);
      this.vrtm = updateVRTableModel(versioning);
      this.tpdtm = updateTPDTableModel(versioning);
      this.licmue = updateLicenseTableModel(versioning);

      Techpackdependency tpd = new Techpackdependency(etlRock);
      tpd.setVersionid(vrVersionid);
      TechpackdependencyFactory tpdF = new TechpackdependencyFactory(etlRock, tpd);
      techpackdependency = tpdF.getElementAt(0);

      Supportedvendorrelease svr = new Supportedvendorrelease(etlRock);
      svr.setVersionid(vrVersionid);
      SupportedvendorreleaseFactory svrF = new SupportedvendorreleaseFactory(etlRock, svr);
      vendorrelease = svrF.getElementAt(0);

      Universename un = new Universename(etlRock);
      un.setVersionid(vrVersionid);
      UniversenameFactory unF = new UniversenameFactory(etlRock, un);
      universename = unF.getElementAt(0);

    } catch (Exception e) {
      logger.warning(e.getMessage());
    }
  }

  public void refresh() {
    this.uetm = updateUETableModel(null);
    this.tpdtm = updateTPDTableModel(null);
    this.vrtm = updateVRTableModel(null);
    this.licmue = updateLicenseTableModel(versioning);
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  public String getVersionid() {

    if (versioning == null) {
      return "";
    }

    return versioning.getVersionid();
  }

  public Versioning getVersioning() {

    return versioning;
  }

  public String getDescription() {
    if (versioning == null) {
      return "";
    }
    return versioning.getDescription();

  }

  public Long getStatus() {
    if (versioning == null) {
      return null;
    }
    return versioning.getStatus();

  }

  public String getTechpack_name() {
    if (versioning == null) {
      return "";
    }
    return versioning.getTechpack_name();

  }

  public String getTechpack_version() {
    if (versioning == null) {
      return "";
    }
    return versioning.getTechpack_version();

  }

  public String getTechpack_type() {
    if (versioning == null) {
      return "";
    }
    return versioning.getTechpack_type();

  }

  public String getProduct_number() {
    if (versioning == null) {
      return "";
    }
    return versioning.getProduct_number();

  }

  public String getBasedefinition() {
    if (versioning == null) {
      return "";
    }
    return versioning.getBasedefinition();

  }

  public String getBaseversion() {
    if (versioning == null) {
      return "";
    }
    return versioning.getBaseversion();

  }

  public String getInstalldescription() {
    if (versioning == null) {
      return "";
    }
    return versioning.getInstalldescription();

  }

  public String getUniversename(String Versionid) {

    try {
      Universename universename = new Universename(etlRock);
      universename.setVersionid(Versionid);
      UniversenameFactory universenameF = new UniversenameFactory(etlRock, universename);
      Iterator<Universename> iter = universenameF.get().iterator();
      while (iter.hasNext()) {
        Universename tmpuUniversename = (Universename) iter.next();
        return tmpuUniversename.getUniversename();
      }
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return "";
  }

  public String getVendorrelease() {
    if (vendorrelease == null) {
      return "";
    }
    return vendorrelease.getVendorrelease();

  }

  public String getUniverseextension() {
    if (universename == null) {
      return "";
    }
    return universename.getUniverseextension();

  }

  public String getLockedby() {
    if (versioning == null) {
      return "";
    }
    return versioning.getLockedby();

  }

  public Timestamp getLockdate() {
    if (versioning == null) {
      return null;
    }
    return versioning.getLockdate();
  }

  public void save() {
    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);

      techpackdependency.saveToDB();
      vendorrelease.saveToDB();
      universename.saveToDB();

      dataModelController.getRockFactory().getConnection().commit();

      saveUETableModel();
      saveTPDTableModel();
      saveVRTableModel();

      dataModelController.rockObjectsModified(this);
      dataModelController.getRockFactory().getConnection().commit();
    } catch (Exception e) {
      try {
        dataModelController.getRockFactory().getConnection().rollback();
      } catch (Exception ex) {
        ExceptionHandler.instance().handle(ex);
        ex.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {

      try {
        dataModelController.getRockFactory().getConnection().setAutoCommit(true);
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }
  }

  public boolean validateNew(RockDBObject rObj) {
    return true;
  }

  public boolean validateDel(RockDBObject rObj) {
    return true;
  }

  public boolean validateMod(RockDBObject rObj) {
    return true;
  }

  public boolean newObj(RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean delObj(RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(RockDBObject rObj) {
    RockDBObject[] r = new RockDBObject[1];
    r[0] = rObj;
    return modObj(r);
  }

  public boolean modObj(RockDBObject rObj[]) {

    try {

      etlRock.getConnection().setAutoCommit(false);

      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }

      dataModelController.rockObjectsModified(this);

      etlRock.getConnection().commit();

    } catch (Exception e) {
      try {
        etlRock.getConnection().rollback();
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        etlRock.getConnection().setAutoCommit(true);
      } catch (Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
    }

    return true;
  }

  public boolean updated(DataModel dataModel) {
    return false;
  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public DataModelController getDataModelController() {
    return dataModelController;
  }
}
