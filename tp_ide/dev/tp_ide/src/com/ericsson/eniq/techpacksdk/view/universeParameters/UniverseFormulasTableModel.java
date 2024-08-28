package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.ericsson.eniq.techpacksdk.common.Utils;

@SuppressWarnings( { "serial", "unchecked" })
public class UniverseFormulasTableModel extends GenericUniverseTableModel<Universeformulas> {

  public static final int NAME_COLUMN_INDEX = 0;

  public static final int FORMULA_COLUMN_INDEX = 1;

  public static final int OBJECT_TYPE_COLUMN_INDEX = 2;

  public static final int QUALIFICATION_COLUMN_INDEX = 3;

  public static final int AGGREGATION_COLUMN_INDEX = 4;

  public UniverseFormulasTableModel(Vector<Universeformulas> data, RockFactory rock) {
    super(data, rock);
  }

  @Override
  public void addEmptyNewRow(String version, int row) {
    Universeformulas uF = new Universeformulas(rock, true);
    uF.setOrdernro(row + 1l);
    uF.setVersionid(version);
    this.data.add(row + 1, uF);

    this.fireTableChanged(null);
  }

  @Override
  public Object getColumnValue(final Universeformulas universeFormulas, final int row, final int columnIndex) {
    if (universeFormulas != null) {
      universeFormulas.setOrdernro(row + 0l);
      switch (columnIndex) {

      case NAME_COLUMN_INDEX:
        return Utils.replaceNull(universeFormulas.getName());

      case FORMULA_COLUMN_INDEX:
        return Utils.replaceNull(universeFormulas.getFormula());

      case OBJECT_TYPE_COLUMN_INDEX:
        return Utils.replaceNull(universeFormulas.getObjecttype());

      case QUALIFICATION_COLUMN_INDEX:
        return Utils.replaceNull(universeFormulas.getQualification());

      case AGGREGATION_COLUMN_INDEX:
        return Utils.replaceNull(universeFormulas.getAggregation());

      default:

        break;
      }
    }
    return null;
  }

  @Override
  void setColumnValue(Universeformulas universeFormulas, int columnIndex, Object value) {

    String strVal = Utils.replaceNull((String) value).trim();

    if (universeFormulas != null) {
      switch (columnIndex) {

      case NAME_COLUMN_INDEX:
        universeFormulas.setName(strVal);
        break;

      case FORMULA_COLUMN_INDEX:
        universeFormulas.setFormula(strVal);
        break;

      case OBJECT_TYPE_COLUMN_INDEX:
        universeFormulas.setObjecttype(strVal);
        break;

      case QUALIFICATION_COLUMN_INDEX:
        universeFormulas.setQualification(strVal);

        break;

      case AGGREGATION_COLUMN_INDEX:
        universeFormulas.setAggregation(strVal);

        break;

      default:

        break;
      }
    }
  }

  @Override
  void initColumnDefinitions() {
    this.columnNameArr = new String[] { "Name", "Formula", "Object Type", "Qualification", "Aggregation" };
    this.columnWidtArr = new int[] { 160, 320, 80, 80, 80 };
  }

  /**
   * Overridden version of the removeRow method to be able to validate and
   * confirm the remove before execution.
   * 
   * @see com.ericsson.eniq.techpacksdk.view.universeParameters.GenericUniverseTableModel#removeRow(int)
   */
  @Override
  public Universeformulas removeRow(final int row) {
    List<Universeformulas> list = new ArrayList<Universeformulas>();

    list.add(data.get(row));

    String warning = UniverseFormulasDataModel.validateDelete(rock, list);

    if (warning.length() == 0
        || JOptionPane.showConfirmDialog(null, warning, "Confirm Remove", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

      Universeformulas uc = data.remove(row);
      fireTableRowsDeleted(row, row);
      return uc;
    }
    return null;
  }

}
