package com.ericsson.eniq.techpacksdk.view.generaltechpack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.ericsson.eniq.component.DataTreeNode;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

@SuppressWarnings("serial")
public class TechPackDependencyDataModel extends DefaultTreeModel implements DataModel {

  class MyAbstractTableModel extends AbstractTableModel {

    private List columnNames;

    private List<List<String>> rowData;

    public MyAbstractTableModel(MyAbstractTableModel old) {
      columnNames = old.columnNames;
      rowData = old.rowData;
    }

    public MyAbstractTableModel(List columnNames, List rowData) {
      this.columnNames = columnNames;
      this.rowData = rowData;
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

    public Object getValueAt(int row, int col) {
      return ((List) rowData.get(row)).get(col);
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    public void setValueAt(Object value, int row, int col) {
      ((List) rowData.get(row)).set(col, value);
    }

    public void addEmptyNewRow() {
      List<String> l = new ArrayList<String>();
      l.add("");
      l.add("");
      rowData.add(l);
    }

    public void addNewRow(List row) {
      rowData.add(row);
    }

    public void removeRow(int row) {
      rowData.remove(row);
    }

  }

  RockFactory etlRock;

  MyAbstractTableModel atm;

  MyAbstractTableModel atmd;

  DataModelController dataModelController = null;

  Techpackdependency techpackdependency;

  public TechPackDependencyDataModel(RockFactory etlRock) {
    super(null);
    // this.atmd = createTPDepedencyTableModel();
    this.atm = createTPDTableModel();
    this.etlRock = etlRock;
  }

  public void setTechPackDependency(DataTreeNode dataTreeNode) {
    techpackdependency = (Techpackdependency) dataTreeNode.getRockDBObject();
  }

  public MyAbstractTableModel getTPDTableModel() {
    return atm;
  }

  public MyAbstractTableModel getTPDepedencyTableModel() {
    return atmd;
  }

  public void setDataModelController(DataModelController dataModelController) {
    this.dataModelController = dataModelController;
  }

  public Object[] getTechPacks() {

    try {

      Versioning ver = new Versioning(etlRock);
      VersioningFactory verF = new VersioningFactory(etlRock, ver);

      Object[] result = new Object[verF.get().size()];
      for (int i = 0; i < verF.get().size(); i++) {
        Versioning v = (Versioning) verF.get().get(i);
        result[i] = (v.getTechpack_name() + ":" + v.getTechpack_version());
      }

      return result;

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }
    return null;
  }

  private MyAbstractTableModel createTPDTableModel() {

    List<List<String>> rowData = new ArrayList<List<String>>();

    try {

      if (techpackdependency != null) {

        Techpackdependency tpd = new Techpackdependency(etlRock);
        tpd.setVersionid(techpackdependency.getVersionid());

        TechpackdependencyFactory tpdF = new TechpackdependencyFactory(etlRock, tpd);
        Iterator iter = tpdF.get().iterator();

        while (iter.hasNext()) {
          Techpackdependency ttpd = (Techpackdependency) iter.next();
          List<String> row = new ArrayList<String>();
          row.add(ttpd.getTechpackname());
          row.add(ttpd.getVersion());
          rowData.add(row);
        }
      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    List<String> columnNames = new ArrayList<String>();
    columnNames.add("Name");
    columnNames.add("Version");

    MyAbstractTableModel atm = new MyAbstractTableModel(columnNames, rowData);
    return atm;
  }

  public RockFactory getRockFactory() {
    return etlRock;
  }

  public String getVersionid() {

    if (techpackdependency == null) {
      return "";
    }

    return techpackdependency.getVersionid();
  }

  public String getTechPackName() {
    if (techpackdependency == null) {
      return "";
    }
    return techpackdependency.getTechpackname();
  }

  public String getVersion() {
    if (techpackdependency == null) {
      return "";
    }
    return techpackdependency.getVersion();
  }

  public void save() {

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

  public void refresh() {
    // this.atmd = createTPDepedencyTableModel();
    this.atm = createTPDTableModel();
  }
}
