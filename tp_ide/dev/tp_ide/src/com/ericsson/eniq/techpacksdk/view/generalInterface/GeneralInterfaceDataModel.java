package com.ericsson.eniq.techpacksdk.view.generalInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.application.ResourceMap;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.InterfacedependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.common.Utils;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;
import com.ericsson.eniq.techpacksdk.datamodel.InterfaceTreeDataModel;
import com.ericsson.eniq.techpacksdk.view.newInterface.ManageNewInterfaceView;

public class GeneralInterfaceDataModel extends DefaultTreeModel implements DataModel {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = Logger.getLogger(ManageNewInterfaceView.class.getName());

  class TPTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final List columnNames;

    private final Vector<Interfacetechpacks> rowData = new Vector<Interfacetechpacks>();

    private final Vector<Interfacetechpacks> removedData = new Vector<Interfacetechpacks>();

    public TPTableModel(List columnNames, Datainterface obj) {

      this.columnNames = columnNames;

      try {
        if (obj != null) {
          Interfacetechpacks intfDep = new Interfacetechpacks(etlRock, true);
          if (obj instanceof Datainterface) {
        	intfDep.setInterfacename((obj).getInterfacename());
            intfDep.setInterfaceversion((obj).getInterfaceversion());
          }
          InterfacetechpacksFactory intfDepF = new InterfacetechpacksFactory(etlRock, intfDep, true);
          Iterator iter = intfDepF.get().iterator();

          while (iter.hasNext()) {
            Interfacetechpacks iDep = (Interfacetechpacks) iter.next();
            rowData.add((Interfacetechpacks) iDep.clone());
          }
        }

      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }
    }

    @Override
    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    @Override
    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Interfacetechpacks> getDeletedRows() {
      return removedData;
    }

    @Override
    public int getColumnCount() {
      return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

      Interfacetechpacks itp = rowData.get(row);

      // name
      if (col == 0) {
        return itp.getTechpackname();
      }

      // version
      if (col == 1) {
        return itp.getTechpackversion();
      }

      return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

      try {

        Interfacetechpacks itp = rowData.get(row);

        if (col == 0) {
          itp.setTechpackname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          itp.setTechpackversion((String) value);
          this.fireTableChanged(null);
        }

      } catch (Exception e) {
        // TODO SHOW A NAG NAG
      }
    }

    public void addEmptyNewRow() {

      Interfacetechpacks itp = new Interfacetechpacks(etlRock, true);
      itp.setTechpackname("");
      itp.setTechpackversion("");
      itp.setInterfacename("");
      itp.setInterfaceversion("");

      rowData.add(itp);

      this.fireTableChanged(null);
    }

    public void addNewRow(Interfacetechpacks row) {

      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      removedData.add((Interfacetechpacks) rowData.get(row).clone());
      rowData.remove(row);

      this.fireTableChanged(null);
    }

    public Interfacetechpacks getRow(int row) {
      return rowData.get(row);
    }
  }

  class TPDepedencyTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final List columnNames;

    private final Vector<Interfacedependency> rowData = new Vector<Interfacedependency>();

    private final Vector<Interfacedependency> removedData = new Vector<Interfacedependency>();

    public TPDepedencyTableModel(List columnNames, Datainterface obj) {

      this.columnNames = columnNames;

      try {
        if (obj != null) {
          Interfacedependency intfDep = new Interfacedependency(etlRock);
          if (obj instanceof Datainterface) {
        	  intfDep.setInterfacename((obj).getInterfacename());
              intfDep.setInterfaceversion((obj).getInterfaceversion());
          }
          InterfacedependencyFactory intfDepF = new InterfacedependencyFactory(etlRock, intfDep, true);
          Iterator iter = intfDepF.get().iterator();

          while (iter.hasNext()) {
            Interfacedependency iDep = (Interfacedependency) iter.next();
            rowData.add((Interfacedependency) iDep.clone());
          }

        }
      } catch (Exception e) {
        ExceptionHandler.instance().handle(e);
        e.printStackTrace();
      }

    }

    @Override
    public String getColumnName(int col) {
      return columnNames.get(col).toString();
    }

    @Override
    public int getRowCount() {
      return rowData.size();
    }

    public Vector<Interfacedependency> getDeletedRows() {
      return removedData;
    }

    @Override
    public int getColumnCount() {
      return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

      Interfacedependency itp = rowData.get(row);

      // name
      if (col == 0) {
        return itp.getTechpackname();
      }

      // version
      if (col == 1) {
        return itp.getTechpackversion();
      }

      return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

      try {

        Interfacedependency itp = rowData.get(row);

        if (col == 0) {
          itp.setTechpackname((String) value);
          this.fireTableChanged(null);
        }

        if (col == 1) {
          itp.setTechpackversion((String) value);
          this.fireTableChanged(null);
        }

      } catch (Exception e) {
        // TODO SHOW A NAG NAG
      }
    }

    public void addEmptyNewRow() {

      Interfacedependency itp = new Interfacedependency(etlRock, true);
      itp.setTechpackname("");
      itp.setTechpackversion("");
      rowData.add(itp);

      this.fireTableChanged(null);
    }

    public void addNewRow(Interfacedependency row) {
      rowData.add(row);
      this.fireTableChanged(null);
    }

    public void removeRow(int row) {
      removedData.add((Interfacedependency) rowData.get(row).clone());
      rowData.remove(row);
      this.fireTableChanged(null);
    }

    public Interfacedependency getRow(int row) {
      return rowData.get(row);
    }

  }

  RockFactory etlRock;

  private TPTableModel atm;

  private TPDepedencyTableModel atmd;

  private final InterfaceTreeDataModel tpData;

  private DataModelController dataModelController = null;

  private Datainterface datainterface;

  private String iName;

  private String iVersion;

  private final ResourceMap resourceMap;

  public GeneralInterfaceDataModel(RockFactory etlRock, final String serverName, final ResourceMap resourceMap) {
    super(null);
    this.atmd = createTPDepedencyTableModel(null);
    this.atm = createTPTableModel(null);
    this.etlRock = etlRock;
    tpData = new InterfaceTreeDataModel(etlRock, serverName);
    this.resourceMap = resourceMap;
  }

  public TPTableModel getTPTableModel() {
    return atm;
  }

  public TPDepedencyTableModel getTPDepedencyTableModel() {
    return atmd;
  }

  public void setInterface(DataTreeNode dataTreeNode) {
    Datainterface i = (Datainterface) dataTreeNode.getRockDBObject();
    iName = i.getInterfacename();
    iVersion = i.getInterfaceversion();
  }

  public String[] getAllTechPacks() {

    try {

      Versioning ver = new Versioning(etlRock);
      VersioningFactory verF = new VersioningFactory(etlRock, ver);

      String[] result = new String[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
        Versioning v = verF.get().get(i);
        String version = v.getVersionid().substring(v.getVersionid().indexOf(":")+1);        
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version() + " "+version);
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  private void saveTPTableModel() throws Exception {

    Vector<Interfacetechpacks> deleted = dataModelController.getEditInterfaceDataModel().atm.getDeletedRows();

    // remove
    while (deleted.size() > 0) {
      try {
        deleted.remove(0).deleteDB();
      } catch (Exception e) {
        // ignored
      }
    }

    int row = dataModelController.getEditInterfaceDataModel().atm.getRowCount();

    for (int r = 0; r < row; r++) {
      dataModelController.getEditInterfaceDataModel().atm.getRow(r).saveToDB();
    }
  }

  private void saveTPDepedencyTableModel() throws Exception {

    Vector<Interfacedependency> deleted = dataModelController.getEditInterfaceDataModel().atmd.getDeletedRows();

    // remove
    while (deleted.size() > 0) {
      try {
        deleted.remove(0).deleteDB();
      } catch (Exception e) {
        // ignored
      }
    }

    int rowd = dataModelController.getEditInterfaceDataModel().atmd.getRowCount();

    for (int r = 0; r < rowd; r++) {
      dataModelController.getEditInterfaceDataModel().atmd.getRow(r).saveToDB();
    }

  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public String[] getTechPacks() {
    try {
      Versioning ver = new Versioning(etlRock);
      VersioningFactory verF = new VersioningFactory(etlRock, ver, true);

      String[] result = new String[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
        Versioning v = verF.get().get(i);
        String version = v.getVersionid().substring(v.getVersionid().indexOf(":")+1);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version()+ ":" + version);
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

  private TPTableModel createTPTableModel(Datainterface obj) {

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("R-State");

    TPTableModel atm = new TPTableModel(columnNames, obj);
    return atm;
  }

  private TPDepedencyTableModel createTPDepedencyTableModel(Datainterface obj) {
    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("R-State");

    TPDepedencyTableModel atmd = new TPDepedencyTableModel(columnNames, obj);
    return atmd;
  }

  @Override
  public void refresh() {
    try {

      Datainterface di = new Datainterface(etlRock);
      di.setInterfacename(iName);
      di.setInterfaceversion(iVersion);
      DatainterfaceFactory diF = new DatainterfaceFactory(etlRock, di);
      datainterface = diF.getElementAt(0);
      
      this.atmd = createTPDepedencyTableModel(datainterface);
      this.atm = createTPTableModel(datainterface);
      
    } catch (Exception e) {
      logger.warning(e.getMessage());
    }
  }

  @Override
  public RockFactory getRockFactory() {
    return etlRock;
  }

  public void setInterfacename(String str) {
    datainterface.setInterfacename(str);
  }

  public void setProductnumber(String str) {
    datainterface.setProductnumber(str);
  }

  public void setInterfaceversion(String str) {
    datainterface.setInterfaceversion(str);

  }

  public void setDescription(String str) {
    datainterface.setDescription(str);

  }

  public void setInstalldescription(String str) {
    datainterface.setInstalldescription(str);

  }

  public void setFormat(String str) {
    datainterface.setDataformattype(str);

  }

  public void setInterfacetype(String str) {
    datainterface.setInterfacetype(str);

  }

  public void setDataformattype(String str) {
    datainterface.setDataformattype(str);

  }

  public void setStatus(Long l) {
    datainterface.setStatus(l);

  }

  public void setRState(String s) {
    datainterface.setRstate(s);

  }

  public String getRState() {

    if (datainterface == null || datainterface.getRstate() == null) {
      return "";
    }

    return datainterface.getRstate();
  }
  
  public Long getStatus() {

    if (datainterface == null) {
      return 0L;
    }

    return datainterface.getStatus();
  }

  public String getInterfacename() {

    if (datainterface == null) {
      return "";
    }

    return datainterface.getInterfacename();
  }

  public String getProductnumber() {
    if (datainterface == null) {
      return "";
    }
    return datainterface.getProductnumber();

  }

  public String getDescription() {
    if (datainterface == null) {
      return "";
    }
    return datainterface.getDescription();

  }

  public String getInstalldescription() {
    if (datainterface == null) {
      return "";
    }
    return Utils.replaceNull(datainterface.getInstalldescription());

  }


  public String getFormat() {
    String dataformat = "";

    if (datainterface != null) {
      String parserformat = resourceMap.getString(datainterface.getInterfacename() + ".parserformat");
      if (parserformat == null) {
        dataformat = datainterface.getDataformattype();
      } else {
        dataformat = parserformat;
      }
    }
    return dataformat;
  }

  public String getInterfacetype() {
    if (datainterface == null) {
      return "";
    }
    return datainterface.getInterfacetype();

  }

  public String getInterfaceversion() {
    if (datainterface == null) {
      return "";
    }
    return datainterface.getInterfaceversion();

  }

  public String getDataformattype() {
    if (datainterface == null) {
      return "";
    }
    return datainterface.getInterfacetype();

  }

  @Override
  public void save() {

    try {

      dataModelController.getRockFactory().getConnection().setAutoCommit(false);

      datainterface.saveToDB();

      dataModelController.getRockFactory().getConnection().commit();

      saveTPTableModel();
      saveTPDepedencyTableModel();

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

  @Override
  public boolean validateNew(RockDBObject rObj) {
    return true;
  }

  @Override
  public boolean validateDel(RockDBObject rObj) {
    return true;
  }

  @Override
  public boolean validateMod(RockDBObject rObj) {
    return true;
  }

  @Override
  public boolean newObj(RockDBObject rObj) {
    try {
      rObj.insertDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean delObj(RockDBObject rObj) {
    try {
      rObj.deleteDB();
    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean modObj(RockDBObject rObj) {
    RockDBObject[] r = new RockDBObject[1];
    r[0] = rObj;
    return modObj(r);
  }

  @Override
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

  @Override
  public boolean updated(DataModel dataModel) {
    // TODO Auto-generated method stub
    return false;
  }

}
