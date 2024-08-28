package com.ericsson.eniq.techpacksdk.view.newTechPack;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackTreeDataModel;

public class NewTechPackDataModel extends DefaultTreeModel implements DataModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  class TPTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Versioning> rowData = new Vector<Versioning>();

    public TPTableModel(final List columnNames, final Versioning obj) {

      this.columnNames = columnNames;

      try {
        if (obj != null) {
          final Versioning v = new Versioning(etlRock, true);
          final VersioningFactory vF = new VersioningFactory(etlRock, v, true);
          final Iterator iter = vF.get().iterator();

          while (iter.hasNext()) {
            final Versioning ver = (Versioning) iter.next();
            rowData.add((Versioning) ver.clone());
          }
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(final int row, final int col) {

      final Versioning ve = rowData.get(row);

      // name
      if (col == 0) {
        return ve.getTechpack_name();
      }

      // version
      if (col == 1) {
        return ve.getTechpack_version();
      }

      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

      try {

        final Versioning ver = rowData.get(row);

        if (col == 0) {
          ver.setTechpack_name((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          ver.setTechpack_version((String) value);
          this.fireTableChanged(null);
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

      final Versioning versi = new Versioning(etlRock, true);
      versi.setTechpack_name("");
      versi.setTechpack_version("");
      rowData.add(versi);

      this.fireTableChanged(null);
    }

    public void addNewRow(final Versioning row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      rowData.remove(row);
      this.fireTableChanged(null);
    }

    public Versioning getRow(final int row) {
      return rowData.get(row);
    }
  }

  class LicenseTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> columnNames;

    private Vector<String> rowData = new Vector<String>();

    public LicenseTableModel(final List<String> columnNames, final Versioning obj) {

      this.columnNames = columnNames;
      rowData = new Vector<String>();
      try {
        if (obj != null) {
          final Versioning v = new Versioning(etlRock, true);
          v.setVersionid(obj.getVersionid());
          final VersioningFactory vF = new VersioningFactory(etlRock, v, true);
          final Iterator<Versioning> iter = vF.get().iterator();

          while (iter.hasNext()) {
            final Versioning ver = (Versioning) iter.next();
            final String licensenamesStr = ver.getLicensename();
            if(!Utils.isEmpty(licensenamesStr)) {
              final String[] names = licensenamesStr.split(",");
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

    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(final int row, final int col) {
      return rowData.get(row);
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

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

    public void addNewRow(final String row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      rowData.remove(row);
      this.fireTableChanged(null);
    }

    public String getRow(final int row) {
      return rowData.get(row);
    }

  }

  class TPDepedencyTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Techpackdependency> rowData = new Vector<Techpackdependency>();

    public TPDepedencyTableModel(final TPDepedencyTableModel old) {
      columnNames = old.columnNames;
      rowData = old.rowData;
    }

    public TPDepedencyTableModel(final List columnNames, final Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
        	final Techpackdependency tpd = new Techpackdependency(etlRock);

        	final String vrVersionid = ((Versioning) obj).getVersionid();
          tpd.setVersionid(vrVersionid);
          final TechpackdependencyFactory tpdF = new TechpackdependencyFactory(etlRock, tpd, true);
          final Iterator iter = tpdF.get().iterator();

          while (iter.hasNext()) {
        	  final Techpackdependency tmpTpd = (Techpackdependency) iter.next();
            rowData.add((Techpackdependency) tmpTpd.clone());
          }
        }
      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
      return columnNames.get(col).toString();
    }

    public int getRowCount() {
      return rowData.size();
    }

    public int getColumnCount() {
      return columnNames.size();
    }

    public Object getValueAt(final int row, final int col) {

    	final Techpackdependency tpd = rowData.get(row);

      // name
      if (col == 0) {
        return tpd.getTechpackname();
      }

      // version
      if (col == 1) {
        return tpd.getVersion();
      }

      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {
      try {

    	  final Techpackdependency tpd = rowData.get(row);

        if (col == 0) {
          tpd.setTechpackname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          tpd.setVersion((String) value);
          this.fireTableChanged(null);
        }

      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

    	final Techpackdependency tpd = new Techpackdependency(etlRock, true);
      tpd.setTechpackname("");
      tpd.setVersion("");
      rowData.add(tpd);
      this.fireTableChanged(null);
    }

    public void addNewRow(final Techpackdependency row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      rowData.remove(row);
      this.fireTableChanged(null);
    }

    public Techpackdependency getRow(final int row) {
      return rowData.get(row);
    }
  }

  class UETableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Universename> rowData = new Vector<Universename>();

    private Vector<Universename> removedData = new Vector<Universename>();

    public UETableModel(final List columnNames, final Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
        	final Universename un = new Universename(etlRock);

        	final String vrVersionid = ((Versioning) obj).getVersionid();
          un.setVersionid(vrVersionid);
          final UniversenameFactory unF = new UniversenameFactory(etlRock, un, true);
          final Iterator iter = unF.get().iterator();

          while (iter.hasNext()) {
        	  final Universename tmpUn = (Universename) iter.next();
            if(!Utils.isEmpty(tmpUn.getUniversename())) {
              rowData.add((Universename) tmpUn.clone());
            }
          }
        }
      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
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

    public Object getValueAt(final int row, final int col) {

    	final Universename unvext = rowData.get(row);

      // Universwe extension
      if (col == 0) {
        return unvext.getUniverseextension();
      }
      if (col == 1) {
        return unvext.getUniverseextensionname();
      }
      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

      try {

    	  final Universename uname = rowData.get(row);

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

    	final Universename unvext = new Universename(etlRock, true);
      unvext.setUniverseextension("");
      unvext.setUniverseextensionname("");
      rowData.add(unvext);

      this.fireTableChanged(null);
    }

    public void addNewRow(final Universename row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      removedData.add((Universename) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Universename getRow(final int row) {
      return rowData.get(row);
    }
  }

  class VRTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List columnNames;

    private Vector<Supportedvendorrelease> rowData = new Vector<Supportedvendorrelease>();

    private Vector<Supportedvendorrelease> removedData = new Vector<Supportedvendorrelease>();

    public VRTableModel(final List columnNames , final Versioning obj) {
      try {

        this.columnNames = columnNames;

        if (obj != null) {
        	final Supportedvendorrelease vr = new Supportedvendorrelease(etlRock);

        	final String vrVersionid = ((Versioning) obj).getVersionid();
          vr.setVersionid(vrVersionid);
          final SupportedvendorreleaseFactory vrF = new SupportedvendorreleaseFactory(etlRock, vr, true);
          final Iterator iter = vrF.get().iterator();

          while (iter.hasNext()) {
        	  final Supportedvendorrelease tmpVr = (Supportedvendorrelease) iter.next();
            rowData.add((Supportedvendorrelease) tmpVr.clone());
          }
        }
      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public String getColumnName(final int col) {
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

    public Object getValueAt(final int row, final int col) {

    	final Supportedvendorrelease venrel = rowData.get(row);

      // Vendor release
      if (col == 0) {
        return venrel.getVendorrelease();
      }
      return null;
    }

    public boolean isCellEditable(final int row, final int col) {
      return true;
    }

    public void setValueAt(final Object value, final int row, final int col) {

      try {

    	  final Supportedvendorrelease vrel = rowData.get(row);

        if (col == 0) {
          vrel.setVendorrelease((String) value);
          this.fireTableChanged(null);
        }
      } catch (final Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    public void addEmptyNewRow() {

    	final Supportedvendorrelease venrel = new Supportedvendorrelease(etlRock, true);
      venrel.setVendorrelease("");
      rowData.add(venrel);

      this.fireTableChanged(null);
    }

    public void addNewRow(final Supportedvendorrelease row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(final int row) {
      removedData.add((Supportedvendorrelease) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Supportedvendorrelease getRow(final int row) {
      return rowData.get(row);
    }
  }

  RockFactory etlRock;

  TPTableModel atm;

  TPDepedencyTableModel atmd;

  VRTableModel atmv;

  UETableModel atmue;

  LicenseTableModel licmue;

  TechPackTreeDataModel tpData;

  DataModelController dataModelController = null;

  Versioning versioning;

  Supportedvendorrelease vendorrelease;

  Universename universename;

  public NewTechPackDataModel(final RockFactory etlRock, final String serverName) {
    super(null);
    this.atmd = createTPDepedencyTableModel(null);
    this.atm = createTPTableModel(null);
    this.atmv = createVRTableModel(null);
    this.atmue = createUETableModel(null);
    this.licmue = createLicenseTableModel(null);
    this.etlRock = etlRock;
    tpData = new TechPackTreeDataModel(etlRock, serverName);
  }

  public TPTableModel getTPTableModel() {
    return atm;
  }

  public TPDepedencyTableModel getTPDepedencyTableModel() {
    return atmd;
  }

  public VRTableModel getVRTableModel() {
    return atmv;
  }

  public UETableModel getUnvExtTableModel() {
    return atmue;
  }

  public LicenseTableModel getLicenseTableModel() {
    return licmue;
  }

  public void setDataModelController(final DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public Object[] getTechPacks() {
    try {
    	final Versioning ver = new Versioning(etlRock);
    	final VersioningFactory verF = new VersioningFactory(etlRock, ver);

      Object[] result = new Object[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
    	  final Versioning v = (Versioning) verF.get().get(i);
    	  final String version = v.getVersionid().substring(v.getVersionid().indexOf(":") + 1);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version() + ":" + version);
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  public DefaultTreeModel getDefaultTreeModel() {
    return tpData;
  }

  public List<String> getVersionIdList() {

	  final List<String> rowData = new ArrayList<String>();

    try {

    	final Versioning techPack = new Versioning(etlRock);
    	final VersioningFactory techPackF = new VersioningFactory(etlRock, techPack);
    	final Iterator iter = techPackF.get().iterator();

      while (iter.hasNext()) {
    	  final Versioning tmpVersioning = (Versioning) iter.next();
        rowData.add(tmpVersioning.getVersionid());
      }
    } catch (final Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return rowData;
  }

  private TPTableModel createTPTableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("R-State");

    final TPTableModel atm = new TPTableModel(columnNames, obj);
    return atm;
  }

  private TPDepedencyTableModel createTPDepedencyTableModel(final Versioning obj) {
	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("R-State");

    final TPDepedencyTableModel atmd = new TPDepedencyTableModel(columnNames, obj);
    return atmd;
  }

  private LicenseTableModel createLicenseTableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("License name");
    final LicenseTableModel uetm = new LicenseTableModel(columnNames, obj);
    return uetm;
  }

  private UETableModel createUETableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Universe Extension");
    columnNames.add("Universe Extension Name");
    final UETableModel uetm = new UETableModel(columnNames, obj);
    return uetm;
  }

  private VRTableModel createVRTableModel(final Versioning obj) {

	  final List<String> columnNames = new ArrayList<String>();
    columnNames.add("Vendor Release");
    final VRTableModel vrtm = new VRTableModel(columnNames, obj);
    return vrtm;
  }

  public void refresh() {
    this.atmd = createTPDepedencyTableModel(null);
    this.atm = createTPTableModel(null);
    this.atmv = createVRTableModel(null);
    this.atmue = createUETableModel(null);
    this.licmue = createLicenseTableModel(null);
  }

  public void refresh(final Versioning versioning) {
    // try {
    this.atmd = createTPDepedencyTableModel(versioning);
    this.atm = createTPTableModel(versioning);
    this.atmv = createVRTableModel(versioning);
    this.atmue = createUETableModel(versioning);
    this.licmue = createLicenseTableModel(versioning);

    /*
     * Techpackdependency tpd = new Techpackdependency(etlRock);
     * tpd.setVersionid(vrVersionid); TechpackdependencyFactory tpdF = new
     * TechpackdependencyFactory(etlRock, tpd); techpackdependency =
     * tpdF.getElementAt(0);
     * 
     * Supportedvendorrelease svr = new Supportedvendorrelease(etlRock);
     * svr.setVersionid(vrVersionid); SupportedvendorreleaseFactory svrF = new
     * SupportedvendorreleaseFactory(etlRock, svr); vendorrelease =
     * svrF.getElementAt(0);
     * 
     * Universename un = new Universename(etlRock);
     * un.setVersionid(vrVersionid); UniversenameFactory unF = new
     * UniversenameFactory(etlRock, un); universename = unF.getElementAt(0);
     */

    /*
     * } catch (Exception e) { logger.warning(e.getMessage()); }
     */
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

  public String getLicense_Name() {
    if (versioning == null) {
      return "";
    }
    return versioning.getLicensename();

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

  public String getUniversename(final String versionid) {

    if (versionid.equals("")) {
      return "";
    }

    Universename unvname = null;
    Universename u = null;

    try {
      unvname = new Universename(etlRock);
      unvname.setVersionid(versionid);
      final UniversenameFactory unf = new UniversenameFactory(etlRock, unvname);
      u = (Universename) unf.getElementAt(0);
      if (u != null) {
        return u.getUniversename();
      }
    } catch (final Exception e) {
      System.out.println(e);
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

  public String[] getBaseTechPacks() {
    try {
    	final Versioning ver = new Versioning(etlRock);
      ver.setTechpack_type("BASE");
      final VersioningFactory verF = new VersioningFactory(etlRock, ver);

      final String[] result = new String[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
    	  final Versioning v = (Versioning) verF.get().get(i);
        result[i] = (v.getVersionid());
      }
      return result;

    } catch (final Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  public void save() {

  }

  public boolean validateNew(final RockDBObject rObj) {
    return true;
  }

  public boolean validateDel(final RockDBObject rObj) {
    return true;
  }

  public boolean validateMod(final RockDBObject rObj) {
    return true;
  }

  public boolean newObj(final RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (final Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean delObj(final RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (final Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  public boolean modObj(final RockDBObject rObj) {
	  final RockDBObject[] r = new RockDBObject[1];
    r[0] = rObj;
    return modObj(r);
  }

  public boolean modObj(final RockDBObject rObj[]) {

    try {

      etlRock.getConnection().setAutoCommit(false);

      for (int i = 0; i < rObj.length; i++) {
        rObj[i].updateDB();
      }

      dataModelController.rockObjectsModified(this);

      etlRock.getConnection().commit();

    } catch (final Exception e) {
      try {
        etlRock.getConnection().rollback();
      } catch (final Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } finally {
      try {
        etlRock.getConnection().setAutoCommit(true);
      } catch (final Exception sqlE) {
        ExceptionHandler.instance().handle(sqlE);
        sqlE.printStackTrace();
      }
    }

    return true;
  }

  public boolean updated(final DataModel dataModel) {
    return false;
  }
}
