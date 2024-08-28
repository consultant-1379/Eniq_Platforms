package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.List;
import java.util.Vector;

import org.jdesktop.application.Application;

import ssc.rockfactory.RockFactory;
import tableTreeUtils.TableInformation;

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.ericsson.eniq.techpacksdk.common.Utils;

public class EventsReferenceTypeColumnTableModel extends ReferenceTypeColumnTableModel {

  private static final int INDEXES_COLUMN = 12;
  
  public EventsReferenceTypeColumnTableModel(final Application application, final RockFactory rockFactory,
      final Vector<TableInformation> tableInfos, final boolean isTreeEditable, final Referencetable referencetable,
      final boolean isBaseTechPack, final List<Referencecolumn> publiccolumns, final String techpackType,
      final String[] myColumnNames, final int[] myColumnWidths, final String myTableName, final String[] myDataTypes) {
    super(application, rockFactory, tableInfos, isTreeEditable, referencetable, isBaseTechPack, publiccolumns,
        techpackType, myColumnNames, myColumnWidths, myTableName, myDataTypes);
  }
  

  @Override
  protected Object getColumnValue(final Referencecolumn referencecolumn, final int col) {
    final Object result = super.getColumnValue(referencecolumn, col);

    if (referencecolumn != null && result == null) {
      if(col == INDEXES_COLUMN) {
        return Utils.replaceNull(referencecolumn.getIndexes());
      }
    }
    return result;

  }

  @Override
  protected void setColumnValue(final Referencecolumn referencecolumn, final int col, final Object value) {
    super.setColumnValue(referencecolumn, col, value);
    if (col == INDEXES_COLUMN) {
      referencecolumn.setIndexes((String) value);
    }
  }

}
