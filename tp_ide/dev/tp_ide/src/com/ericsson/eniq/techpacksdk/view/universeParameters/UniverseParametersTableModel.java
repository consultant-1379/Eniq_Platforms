package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.ericsson.eniq.techpacksdk.common.Utils;

@SuppressWarnings({ "serial", "unchecked" })
public class UniverseParametersTableModel extends GenericUniverseTableModel<Universeparameters> {

  public static final int NAME_COLUMN_INDEX = 0;

  public static final int TYPENAME_INDEX = 1;

  public UniverseParametersTableModel(Vector<Universeparameters> data, RockFactory rock) {
    super(data, rock);
  }

  @Override
  public void addEmptyNewRow(String versionId, int row) {
    Universeparameters uP = new Universeparameters(this.rock);
    uP.setOrdernro(row + 1);
    uP.setVersionid(versionId);
    this.data.add(row + 1, uP);

    fireTableChanged(null); // TODO Should a TableModelEvent be constructed?
  }

  @Override
  Object getColumnValue(Universeparameters u, int row, int col) {
    if (u != null) {
      u.setOrdernro(row);
      switch (col) {

      case UniverseParametersTableModel.NAME_COLUMN_INDEX:
        return u.getName();

      case UniverseParametersTableModel.TYPENAME_INDEX:
        return u.getTypename();

      default:
        return null;
      }
    }
    return null;
  }

  @Override
  void setColumnValue(Universeparameters u, int col, Object value) {

    String strVal = Utils.replaceNull((String) value).trim();

    switch (col) {

    case UniverseParametersTableModel.TYPENAME_INDEX:
      u.setTypename(strVal);
      break;

    case UniverseParametersTableModel.NAME_COLUMN_INDEX:
      u.setName(strVal);
      break;

    default:

      break;

    }
  }

  @Override
  void initColumnDefinitions() {
    this.columnNameArr = new String[] { "Type name", "Name" };
    this.columnWidtArr = new int[] { 200, 200 };
  }

}
