package com.ericsson.eniq.techpacksdk.view.universeParameters;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.ericsson.eniq.techpacksdk.common.Constants;
import com.ericsson.eniq.techpacksdk.common.Utils;

/**
 * Concrete class that models the Universe object table.
 * 
 * @author etogust
 * @author eheitur
 * 
 */
@SuppressWarnings("serial")
public class UniverseComputedObjectTableModel extends AbstractTableModel {

  public static final Logger logger = Logger.getLogger(UniverseComputedObjectTableModel.class.getName());

  static final int classnameColumnIdx = 0;

  static final int objectnameColumnIdx = 1;

  static final int descriptionColumnIdx = 2;

  static final int objecttypeColumnIdx = 3;

  static final int qualificationColumnIdx = 4;

  // IDE #614: Aggregation removed from computed objects table view.
  // static final int aggregationColumnIdx = 5;

  static final int selectColumnIdx = 5;

  static final int parametersColumnIdx = 6;

  static final int whereColumnIdx = 7;

  static final int promptColumnIdx = 8;

  static final int universeextensionColumnIdx = 9;

  static final String[] columnNames = { "Class name", "Object name", "Description", "Object type", "Qualification",
      "Select", "Parameters", "Where", "prompt", "Universe extension" };

  static final Integer[] myColumnWidths = { 100, 100, 100, 100, 100, 100, 100, 100, 100, 100 };

  final Vector<Universecomputedobject> data;

  private GenericUniverseTableModel<Universeparameters> parametersTableModel;

  private final RockFactory rock;

  public UniverseComputedObjectTableModel(final Vector<Universecomputedobject> data,
      GenericUniverseTableModel<Universeparameters> parametersTableModel, RockFactory rock) {
    this.data = data;
    this.parametersTableModel = parametersTableModel;
    this.rock = rock;
  }

  @SuppressWarnings("unchecked")
  public Class getColumnClass(final int col) {
    return String.class;
  }

  public String getColumnName(final int col) {
    return columnNames[col].toString();
  }

  public int getRowCount() {
    if (data != null) {
      return data.size();
    }
    return 0;
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public Object getValueAt(final int row, final int col) {
    if (row >= data.size()) {
      return null;
    }
    Universecomputedobject a = data.get(row);
    a.setOrdernro(row + 0l);
    return getColumnValue(a, col);
  }

  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  public void setValueAt(final Object value, final int row, final int col) {
    Universecomputedobject u = data.get(row);
    setColumnValue(u, col, value);
    fireTableCellUpdated(row, col);
  }

  public void addRow(final Universecomputedobject u) {
    data.add(u);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  public Universecomputedobject removeRow(final int row) {
    Universecomputedobject u = data.remove(row);

    String versionId = u.getVersionid();
    String className = u.getClassname();
    String objectName = u.getObjectname();
    String univExt = u.getUniverseextension();

    removeUniverseParameters(versionId, className, objectName, univExt);

    fireTableRowsDeleted(row, row);
    return u;
  }

  public Universecomputedobject duplicateRow(final int row) {
    Universecomputedobject u = data.get(row);
    Universecomputedobject newU = (Universecomputedobject) u.clone();

    // Set the new object name as the same as the old and a running number
    // suffix.
    if (!u.getObjectname().equals("")) {
      int count = 0;
      for (Iterator<Universecomputedobject> iter = data.iterator(); iter.hasNext();) {
        Object obj = iter.next();
        if (obj instanceof Universecomputedobject) {
          Universecomputedobject uco = (Universecomputedobject) obj;
          if (uco.getObjectname().equals(newU.getObjectname())) {
            count = 1;
          }
          if (uco.getObjectname().startsWith(newU.getObjectname() + "-")) {
            count++;
          }
        }
      }
      count++;
      newU.setObjectname(u.getObjectname() + "-" + count);
    }

    // Set the order number as the last one (data size before addition).
    newU.setOrdernro(new Long(data.size()));

    // Set the universe parameters for the new object
    setUniverseParameters(newU, getUniverseParameterNames(u));

    data.add(data.size(), newU);
    fireTableRowsInserted(data.size() - 1, data.size() - 1);
    return u;
  }

  public void moveupRow(int row) {
    if (row <= 0) {
      return;
    }
    data.add(row - 1, (Universecomputedobject) (data.get(row)));
    data.remove(row + 1);
  }

  public void movedownRow(int row) {
    if (row >= data.size() - 1) {
      return;
    }
    data.add(row + 2, (Universecomputedobject) (data.get(row)));
    data.remove(row);
  }

  private Object getColumnValue(final Universecomputedobject o, final int col) {

    if (o != null) {
      switch (col) {
      case classnameColumnIdx:
        return Utils.replaceNull(o.getClassname());

      case objectnameColumnIdx:
        return Utils.replaceNull(o.getObjectname());

      case descriptionColumnIdx:
        return Utils.replaceNull(o.getDescription());

      case objecttypeColumnIdx:
        // Return capitalized string (first letter caps, others small), as old
        // SDK techpacks might have different upper / lower case used.
        String objectType = Utils.replaceNull(o.getObjecttype());
        return objectType.substring(0, 1).toUpperCase() + objectType.substring(1, objectType.length()).toLowerCase();
        
      case qualificationColumnIdx:
        return Utils.replaceNull(o.getQualification());

        // case aggregationColumnIdx:
        // return Utils.replaceNull(o.getAggregation()).toUpperCase();

      case selectColumnIdx:
        return Utils.replaceNull(o.getObjselect());

      case parametersColumnIdx:
        return getUniverseParameterNames(o);

      case whereColumnIdx:
        return Utils.replaceNull(o.getObjwhere());

      case promptColumnIdx:
        return Utils.replaceNull(o.getPrompthierarchy());

      case universeextensionColumnIdx:
        return Utils.replaceNull(o.getUniverseextension());

      default:
        break;
      }
    }
    return null;
  }

  private void setColumnValue(final Universecomputedobject o, final int col, final Object value) {
    if (o != null) {
      switch (col) {

      case classnameColumnIdx:
        o.setClassname(Utils.replaceNull((String) value).trim());
        break;

      case objectnameColumnIdx:
        o.setObjectname(Utils.replaceNull((String) value).trim());
        break;

      case descriptionColumnIdx:
        o.setDescription(Utils.replaceNull((String) value).trim());
        break;

      case objecttypeColumnIdx:
        o.setObjecttype(Utils.replaceNull((String) value).trim());
        break;

      case qualificationColumnIdx:
        o.setQualification(Utils.replaceNull((String) value).trim());
        break;

      // case aggregationColumnIdx:
      // o.setAggregation(Utils.replaceNull((String) value).trim());
      // break;

      case selectColumnIdx:
        o.setObjselect(Utils.replaceNull((String) value).trim());
        break;

      case parametersColumnIdx:
        String strValue = Utils.replaceNull((String) value).trim();
        setUniverseParameters(o, strValue);
        break;

      case whereColumnIdx:
        o.setObjwhere(Utils.replaceNull((String) value).trim());
        break;

      case promptColumnIdx:
        o.setPrompthierarchy(Utils.replaceNull((String) value).trim());
        break;

      case universeextensionColumnIdx:
        o.setUniverseextension(Utils.replaceNull((String) value).trim());
        // updateUniverseParameterUniverseExtensions(o);
        break;
      default:
        break;
      }
    }
  }

  public void addEmptyNewRow(String version) {

    Universecomputedobject u = new Universecomputedobject(rock, true);
    // u.setOrdernro(row + 1l);
    u.setOrdernro(data.size() + 0l);
    u.setVersionid(version);
    u.setUniverseextension(Constants.UNIVERSEEXTENSIONTYPES[0]);
    u.setObjecttype(Constants.UNIVERSEOBJECTTYPES[0]);
    data.add(data.size(), u);

    fireTableRowsInserted(data.size() - 1, data.size() - 1);
  }

  /**
   * Get the universe parameter objects matching the give key values.
   * 
   * @param versionId
   * @param className
   * @param objectName
   * @param univExt
   * @return a vector of the universe parameters
   */
  public Vector<Universeparameters> getUniverseParameters(String versionId, String className, String objectName,
      String univExt) {
    Vector<Universeparameters> result = new Vector<Universeparameters>();

    // First, get the universe parameters for the computed object
    Iterator<Universeparameters> allParametersIterator = this.parametersTableModel.data.iterator();
    while (allParametersIterator.hasNext()) {
      Universeparameters parameters = allParametersIterator.next();
      if (Utils.replaceNull(parameters.getVersionid()).equals(versionId)
          && Utils.replaceNull(parameters.getClassname()).equals(className)
          && Utils.replaceNull(parameters.getObjectname()).equals(objectName)
          && Utils.replaceNull(parameters.getUniverseextension()).equals(univExt)) {

        result.add(parameters);
      }
    }

    // Sort the parameters vector, according to the orderNro field.
    Collections.sort(result, new UniverseparametersComparator());

    return result;
  }

  public void removeUniverseParameters(String versionId, String className, String objectName, String univExt) {

    Vector<Universeparameters> deletionVector = new Vector<Universeparameters>();

    Iterator<Universeparameters> allParametersIterator = this.parametersTableModel.data.iterator();
    while (allParametersIterator.hasNext()) {
      Universeparameters parameters = allParametersIterator.next();
      if (Utils.replaceNull(parameters.getVersionid()).equals(versionId)
          && Utils.replaceNull(parameters.getClassname()).equals(className)
          && Utils.replaceNull(parameters.getObjectname()).equals(objectName)
          && Utils.replaceNull(parameters.getUniverseextension()).equals(univExt)) {
        deletionVector.add(parameters);
      }
    }

    this.parametersTableModel.data.removeAll(deletionVector);
  }

  public String getUniverseParameterNames(Universecomputedobject computedObject) {

    String versionId = computedObject.getVersionid();
    String className = computedObject.getClassname();
    String objectName = computedObject.getObjectname();
    String univExt = computedObject.getUniverseextension();

    // Get the parameters objects for the computedObject
    Vector<Universeparameters> myParameters = getUniverseParameters(versionId, className, objectName, univExt);

    // Create the result string
    Iterator<Universeparameters> myParametersIterator = myParameters.iterator();
    StringBuffer resultBuffer = new StringBuffer();
    while (myParametersIterator.hasNext()) {
      Universeparameters parameters = myParametersIterator.next();
      resultBuffer.append("[");
      resultBuffer.append(parameters.getTypename());
      resultBuffer.append(", ");
      resultBuffer.append(parameters.getName());
      resultBuffer.append("] ");
    }
    String result = resultBuffer.toString();
    return result;
  }

  // public void updateUniverseParameterUniverseExtensions(
  // Universecomputedobject computedObject) {
  //
  // Iterator<Universeparameters> parametersIterator =
  // this.parametersTableModel.data
  // .iterator();
  // while (parametersIterator.hasNext()) {
  // Universeparameters p = parametersIterator.next();
  // p.setUniverseextension(computedObject.getUniverseextension());
  // }
  // }

  public void setUniverseParameters(Universecomputedobject computedObject, final String parameterString) {

    String versionId = computedObject.getVersionid();
    String className = computedObject.getClassname();
    String objectName = computedObject.getObjectname();
    String universeExtension = computedObject.getUniverseextension();

    // First, delete the previous parameters with this versionId and
    // objectName
    Vector<Universeparameters> deleteVector = new Vector<Universeparameters>();
    Iterator<Universeparameters> parametersIterator = this.parametersTableModel.data.iterator();
    while (parametersIterator.hasNext()) {
      Universeparameters p = parametersIterator.next();
      if (Utils.replaceNull(p.getVersionid()).equals(versionId)
          && Utils.replaceNull(p.getClassname()).equals(className)
          && Utils.replaceNull(p.getObjectname()).equals(objectName)) {
        deleteVector.add(p);
      }
    }
    this.parametersTableModel.data.removeAll(deleteVector);

    // Parse the parameter names
    Vector<String> parameterNames = new Vector<String>();
    Vector<String> parameterTypeNames = new Vector<String>();

    StringTokenizer stringTokenizer = new StringTokenizer(parameterString, "[");
    while (stringTokenizer.hasMoreTokens()) {
      String parameterToken = stringTokenizer.nextToken(); // typename,
      // name]

      int closingBracketIndex = parameterToken.indexOf(']');
      parameterToken = parameterToken.substring(0, closingBracketIndex);

      StringTokenizer parameterTokenTokenizer = new StringTokenizer(parameterToken, ",");
      String typenameToken = parameterTokenTokenizer.nextToken().trim(); // typename
      String nameToken = parameterTokenTokenizer.nextToken().trim(); // name

      parameterTypeNames.add(typenameToken.trim());
      parameterNames.add(nameToken.trim());
    }

    // Create new Universeparameters objects, based on the parameter names.
    for (int i = 0; i < parameterNames.size(); ++i) {
      String typeName = parameterTypeNames.elementAt(i);
      String name = parameterNames.elementAt(i);
      Integer orderNumber = new Integer(i);

      Universeparameters parametersObject = new Universeparameters(this.rock);
      parametersObject.setVersionid(versionId);
      parametersObject.setClassname(className);
      parametersObject.setObjectname(objectName);
      parametersObject.setTypename(typeName);
      parametersObject.setName(name);
      parametersObject.setUniverseextension(universeExtension);
      parametersObject.setOrdernro(orderNumber);

      this.parametersTableModel.data.add(parametersObject);
    }
  }

  private class UniverseparametersComparator implements Comparator<Universeparameters> {

    public int compare(Universeparameters o1, Universeparameters o2) {
      return o1.getOrdernro().compareTo(o2.getOrdernro());
    }
  }

};
