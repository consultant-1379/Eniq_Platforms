package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Vector;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universejoin;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe class table.
 * 
 * @author etogust
 * 
 */
@SuppressWarnings({ "serial", "unchecked" })
public class UniverseJoinTableModel extends GenericUniverseTableModel<Universejoin> {

  static final int sourcetableColumnIdx = 0;

  static final int sourcelevelColumnIdx = 1;

  static final int sourcecolumnColumnIdx = 2;

  static final int targettableColumnIdx = 3;

  static final int targetlevelColumnIdx = 4;

  static final int targetcolumnColumnIdx = 5;

  static final int expressionColumnIdx = 6;

  static final int cardinalityColumnIdx = 7;

  static final int contextColumnIdx = 8;

  static final int excludedcontextsColumnIdx = 9;

  public static final int universeextensionColumnIdx = 10;

  /**
   * Table model constructor
   * 
   * @param data
   * @param rock
   */
  public UniverseJoinTableModel(final Vector<Universejoin> data, RockFactory rock) {
    super(data, rock);
  }

  @Override
  void initColumnDefinitions() {
    columnNameArr = new String[] { "Source table", "Source level", "Source column", "Target table", "Target level",
        "Target column", "Expression", "Cardinality", "Context", "Excluded contexts", "Universe extension"};
    columnWidtArr = new int[] { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 180 };
  }

  public Object getColumnValue(final Universejoin u, final int row, final int col) {
    if (u != null) {
      u.setOrdernro(row + 0l);
      switch (col) {

      case sourcetableColumnIdx:
        return Utils.replaceNull(u.getSourcetable());

      case sourcelevelColumnIdx:
        return Utils.replaceNull(u.getSourcelevel());

      case sourcecolumnColumnIdx:
        return Utils.replaceNull(u.getSourcecolumn());

      case targettableColumnIdx:
        return Utils.replaceNull(u.getTargettable());

      case targetlevelColumnIdx:
        return Utils.replaceNull(u.getTargetlevel());

      case targetcolumnColumnIdx:
        return Utils.replaceNull(u.getTargetcolumn());

      case expressionColumnIdx:
        return Utils.replaceNull(u.getExpression());

      case cardinalityColumnIdx:
        return Utils.replaceNull(u.getCardinality());

      case contextColumnIdx:
        return Utils.replaceNull(u.getContext());

      case excludedcontextsColumnIdx:
        return Utils.replaceNull(u.getExcludedcontexts());
        
      case universeextensionColumnIdx:
        return Utils.replaceNull(u.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  public void setColumnValue(final Universejoin u, final int col, final Object value) {
    if (u != null) {
      String strVal = Utils.replaceNull((String) value).trim();

      switch (col) {

      case sourcetableColumnIdx:
        u.setSourcetable(strVal);
        break;

      case sourcelevelColumnIdx:
        u.setSourcelevel(strVal);
        break;

      case sourcecolumnColumnIdx:
        u.setSourcecolumn(strVal);
        break;

      case targettableColumnIdx:
        u.setTargettable(strVal);
        break;

      case targetlevelColumnIdx:
        u.setTargetlevel(strVal);
        break;

      case targetcolumnColumnIdx:
        u.setTargetcolumn(strVal);
        break;

      case expressionColumnIdx:
        u.setExpression(strVal);
        break;

      case cardinalityColumnIdx:
        u.setCardinality(strVal);
        break;

      case contextColumnIdx:
        u.setContext(strVal);
        break;

      case excludedcontextsColumnIdx:
        u.setExcludedcontexts(strVal);
        break;
        
      case universeextensionColumnIdx:
        u.setUniverseextension(strVal);
        break;

      default:
        break;
      }
    }
  }

  public void addEmptyNewRow(String version, int row) {

    Universejoin u = new Universejoin(rock, true);
    u.setOrdernro(row + 1l);
    u.setVersionid(version);
    data.add(row + 1, u);

    this.fireTableChanged(null);
  }
};
