/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package ssc.rockfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CopySchema {

  // The database connect
  private RockFactory rockFact;

  // Catalog name (not used at the moment
  private String catalog;

  // The database schema
  private String schema;

  // A hash table of setmehods, oldvalues and new values
  private Hashtable setValues;

  // loaded classes in a hash
  private Hashtable loadedClasses;

  // The name for the project
  private String packagePath;

  // A vector of already copied tables
  private Vector copiedTables;

  /**
   * A class to copy a tables schema tree information.
   * 
   * @param rockFact
   *          The database connection
   * @param catalog
   * @param schema
   * @param setValues
   * @return
   */
  public CopySchema(RockFactory rockFact, String catalog, String schema, Hashtable setValues, String packagePath)
      throws RockException {

    if (rockFact == null) {
      throw new RockException(FactoryRes.VALUE_EQUALS_NULL + " (rockFact)");
    }

    if (schema == null) {
      throw new RockException(FactoryRes.VALUE_EQUALS_NULL + " (schema)");
    }

    this.rockFact = rockFact;
    this.catalog = catalog;
    this.schema = schema;
    this.setValues = setValues;
    this.loadedClasses = new Hashtable();

    this.packagePath = packagePath;

    this.copiedTables = new Vector();

  }

  /**
   * Finds all child tables for a table from database metadata. Makes a copy for each row containing
   * the old value and replaces the copies column value with the new value Calls itself to make a
   * copy of all elements in the schema tree.
   * 
   * @param parentTableName
   * @exception SQLException
   * @exception RockException
   */
  public void copy(String parentTableName) throws SQLException, RockException {

    if (parentTableName == null) {
      throw new RockException(FactoryRes.VALUE_EQUALS_NULL + " (parentTableName)");
    }

    try {
      ResultSet results = this.rockFact.getConnection().getMetaData().getExportedKeys(this.catalog, this.schema,
          parentTableName);

      while (results.next()) {

        String tab = results.getString("FKTABLE_NAME").toLowerCase();

        if (results.getString("FKTABLE_SCHEM").toUpperCase().equals(this.schema.toUpperCase())) {

          if (this.copiedTables.contains(tab) == false) {

            // rockFact.commit();

            // recursive call !!!
            copy(tab);
          }
        }
      }
    } catch (SQLException sqlE) {
      throw sqlE;
    }
  }

  /**
   * Copies one tables data .
   * 
   * @param tab
   */
  public void copyNonRecursive(String tab) throws RockException {

    try {
      this.copiedTables.addElement(tab);

      String tableName = tab.substring(0, 1).toUpperCase();
      if (tab.length() > 1) {
        tableName = new StringBuffer(tableName).append(tab.substring(1, tab.length()).toLowerCase()).toString();
      }

      String factoryElementStr = tableName + "Factory";
      Class factoryClass = Class.forName(this.packagePath + "." + factoryElementStr);
      Method factorySizeMethod = factoryClass.getMethod("size", null);
      Method factoryGetElementMethod = factoryClass.getMethod("getElementAt", new Class[] { int.class });
      Class whereClass = Class.forName(this.packagePath + "." + tableName);

      Constructor whereConstr = whereClass.getConstructor(new Class[] { rockFact.getClass() });
      Object whereObject = whereConstr.newInstance(new Object[] { rockFact });

      if (areMethodsIncluded(whereClass)) {

        invokeSetMethods(whereObject, false);

        Constructor factoryConstr = factoryClass.getConstructor(new Class[] { rockFact.getClass(),
            whereObject.getClass() });
        Object factoryObject = factoryConstr.newInstance(new Object[] { rockFact, whereObject });

        for (int i = 0; i < ((Integer) (factorySizeMethod.invoke(factoryObject, null))).intValue(); i++) {

          Object itemObject = factoryGetElementMethod.invoke(factoryObject, new Object[] { new Integer(i) });

          Method cloneMethod = itemObject.getClass().getMethod("clone", null);

          Object newItemObject = cloneMethod.invoke(itemObject, null);

          invokeSetMethods(newItemObject, true);

          repairSeqValues(newItemObject);

          Method insertMethod = newItemObject.getClass().getMethod("insertDB", null);

          insertMethod.invoke(newItemObject, null);

        }
      }
    } catch (Throwable e) {
      throw new RockException(e.getMessage());
    }

  }

  /**
   * Executes the objects setmethods with values.
   * 
   * @param obj
   *          The object to hold the methods.
   * @param newValues
   *          If true set method values are read from hashtable values[1] else from values[0]
   */
  private void invokeSetMethods(Object obj, boolean newValues) throws InvocationTargetException, NoSuchMethodException,
      IllegalAccessException {
    Enumeration keys = this.setValues.keys();

    while (keys.hasMoreElements()) {

      String setMethodName = (String) keys.nextElement();
      int position = 0;
      if (newValues) {
        position = 1;
      }
      Method setMethod = obj.getClass().getMethod(setMethodName,
          (new Class[] { (((Object[]) this.setValues.get(setMethodName))[position]).getClass() }));
      setMethod.invoke(obj, (new Object[] { (((Object[]) this.setValues.get(setMethodName))[position]) }));

    }
  }

  /**
   * Tests whether the class includes the setmethod names or not.
   * 
   * @param cl
   *          The class to test
   * @return
   */
  private boolean areMethodsIncluded(Class cl) {

    Method[] methods = cl.getMethods();

    Enumeration keys = this.setValues.keys();

    while (keys.hasMoreElements()) {

      String setMethodName = (String) keys.nextElement();
      boolean found = false;

      for (int i = 0; i < methods.length; i++) {

        String methodName = methods[i].getName();

        if (methodName.equals(setMethodName)) {

          found = true;

        }
      }
      if (found == false) {
        return false;
      }

    }
    return true;
  }

  /**
   * Those values that are set by the hashtable must not be included in the sequence list of the db
   * class.
   * 
   * @param newItemObject
   * @exception InvocationTargetException
   * @exception NoSuchMethodException
   * @exception IllegalAccessException
   */
  private void repairSeqValues(Object newItemObject) throws InvocationTargetException, NoSuchMethodException,
      IllegalAccessException {

    boolean found = false;
    Method getSeqsMethod = newItemObject.getClass().getMethod("getcolumnsAndSequences", null);
    String[] seqs = (String[]) getSeqsMethod.invoke(newItemObject, null);

    for (int i = 0; i * 2 < seqs.length; i++) {
      if (seqs[i * 2].length() > 0) {
        String setMethod = "set" + seqs[i * 2].substring(0, 1).toUpperCase();
        if (seqs[i * 2].length() > 1) {
          setMethod = new StringBuffer(setMethod).append(seqs[i * 2].substring(1, seqs[i * 2].length()).toLowerCase())
              .toString();
        }

        if ((this.setValues.containsKey(setMethod) == false) && (isSetMethodInPrimary(newItemObject))) {

          seqs[i * 2] = "";
          found = true;
        }
      }
    }

    if (found) {
      Method setSeqsMethod = newItemObject.getClass().getMethod("setcolumnsAndSequences",
          new Class[] { String[].class });
      Object[] values = { seqs };
      setSeqsMethod.invoke(newItemObject, values);
    }

  }

  /**
   * Test if the hashtable containing the setmethods has an element for at least primary key column.
   * 
   * @param newItemObject
   * @return boolean
   * @exception InvocationTargetException
   * @exception NoSuchMethodException
   * @exception IllegalAccessException
   */
  public boolean isSetMethodInPrimary(Object newItemObject) throws InvocationTargetException, NoSuchMethodException,
      IllegalAccessException {

    Method getPrimaryKeysMethod = newItemObject.getClass().getMethod("getprimaryKeyNames", null);
    String[] primaryKeys = (String[]) getPrimaryKeysMethod.invoke(newItemObject, null);

    for (int i = 0; i < primaryKeys.length; i++) {
      if (primaryKeys[i].length() > 0) {
        String setMethod = "set" + primaryKeys[i].substring(0, 1).toUpperCase();
        if (primaryKeys[i].length() > 1) {
          setMethod = new StringBuffer(setMethod).append(
              primaryKeys[i].substring(1, primaryKeys[i].length()).toLowerCase()).toString();
        }

        if (this.setValues.containsKey(setMethod)) {

          return true;
        }
      }
    }
    return false;

  }

  /**
   * Removes elements from the vector.
   * 
   */
  public void close() {
    this.copiedTables.removeAllElements();
  }

}