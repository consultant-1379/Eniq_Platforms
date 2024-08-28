/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package ssc.rockfactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class RockFactory implements RockFactory_I {

  protected Connection connection;

  protected String dbURL;

  protected String driverName;

  protected String strUserName;

  protected String strPassword;

  protected boolean autoCommit;

  protected String sqlQuote;

  /**
   * Create RockFactory with connection name
   */
  public RockFactory(String dbURL, String strUserName, String strPassword, String driverName, String conName,
      boolean autoCommit) throws RockException {

    this.dbURL = dbURL;
    this.driverName = driverName;
    this.strUserName = strUserName;
    this.strPassword = strPassword;
    this.autoCommit = autoCommit;

    try {
      // Driver driver = (Driver) Class.forName(driverName).newInstance();
      Properties p = new Properties();
      p.put("user", strUserName);
      p.put("password", strPassword);
      p.put("REMOTEPWD", ",,CON=" + conName);
      // connection = driver.connect(dbURL, p);
      // connection.setAutoCommit(autoCommit);
      //
      // DatabaseMetaData metaData = connection.getMetaData();
      // this.sqlQuote = metaData.getIdentifierQuoteString();

      // } catch (SQLException sqlE) {
      // throw sqlE;
    } catch (Exception e) {
      throw new RockException("Driver not found", e);
    }
  }

  /**
   * Return the correct is null database function according to the databse type.
   * 
   * @return String
   */
  private String getIsNullFName() {

    if (this.driverName.indexOf(FactoryRes.SYBASE_DRIVER_NAME) > 0) {
      return FactoryRes.SYBASE_IS_NULL_FUNCTION_NAME;
    } else {
      return FactoryRes.ORACLE_IS_NULL_FUNCTION_NAME;
    }

  }

  /**
   * Returns the sql error code when child records exist when trying to delete a
   * row.
   * 
   * @return
   */
  public int getChildRecorsExistCode() {
    if (this.driverName.indexOf(FactoryRes.SYBASE_DRIVER_NAME) > 0) {
      return FactoryRes.SYBASE_CHILD_RECORD_EXIST_CODE;
    } else {
      return FactoryRes.ORACLE_CHILD_RECORD_EXIST_CODE;
    }
  }

  public String getDbURL() {
    return this.dbURL;
  }

  public String getDriverName() {
    return this.driverName;
  }

  public String getUserName() {
    return this.strUserName;
  }

  public String getPassword() {
    return this.strPassword;
  }

  public boolean getAutoCommit() {
    return this.autoCommit;
  }

  /**
   * isColumnName tests if name is a columns name
   * 
   * @return boolean true if a column name, else false
   * @exception
   */
  protected boolean isColumnName(String fieldName, String fieldTypeName) {
    if (fieldTypeName.equals("Vector"))
      return false;
    for (int i = 0; i < FactoryRes.NOT_COLUMN_NAMES.length; i++) {
      if (fieldName.equals(FactoryRes.NOT_COLUMN_NAMES[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * getFieldValueWithQuotes returns a string containing quotes if type is
   * String or Timestamp.
   * 
   * @return a SELECT - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String getFieldValueWithQuotes(Object obj, Field field, Method method) throws RockException {
    try {
      String[] argv = null;
      Object tempObj = method.invoke(obj, argv);
      String helpString = "NULL";
      if (tempObj != null) {
        helpString = tempObj.toString();
      }

      Class fieldClass = field.getType();

      String className = fieldClass.toString();
      int pointLoc = className.lastIndexOf(".") + 1;
      String name = className.substring(pointLoc, className.length());

      if (name.equals("Timestamp")) {
        if (helpString.equals("NULL")) {
          return helpString;
        } else {
          pointLoc = helpString.lastIndexOf(".");
          if (this.driverName.indexOf(FactoryRes.ORACLE_DRIVER_NAME) > 0) {
            return "TO_DATE('" + helpString.substring(0, pointLoc) + "','yyyy-mm-dd hh24:mi:ss')";
          } else {
            return "'" + helpString.substring(0, pointLoc) + "'";
          }
        }
      } else {
        if (name.equals("String")) {
          if (helpString.equals("NULL")) {
            return helpString;
          } else {
            String newString = "";
            newString = helpString.replaceAll("'", "''");
            if (newString.length() > 0) {
              return "'" + newString + "'";
            } else {
              return "'" + helpString + "'";
            }
          }
        } else {
          return helpString;
        }
      }
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createSelectSQL is general SQL -clause parser for all of the SELECT
   * -clauses.
   * 
   * @return a SELECT - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String createSelectSQL(Object obj, Class c, String whereString) throws RockException {

    String sQLString = "SELECT ";
    Field[] classFields = c.getDeclaredFields();

    for (int i = 0; i < classFields.length; i++) {

      Field field = classFields[i];
      String fieldType = field.getType().getName();
      String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

      if (isColumnName(field.getName(), fieldTypeName)) {

        if (!sQLString.equals("SELECT ")) {
          sQLString = new StringBuffer(sQLString).append(",").toString();
        }
        sQLString = new StringBuffer(sQLString).append(field.getName()).toString();
      }

    }

    sQLString = new StringBuffer(sQLString).append(" FROM ").append(getTableName(obj, c)).toString();
    sQLString = new StringBuffer(sQLString).append(whereString).toString();

    return sQLString;

  }

  /**
   * getFieldGetMethod returns the getmethod for a field
   * 
   * @return method get method the a given field.
   * @exception
   */
  protected Method getFieldMethod(Class objClass, Field field, String setOrGet) throws RockException {
    try {
      String columnName = field.getName();
      String firstCharOfColumn = columnName.substring(0, 1);
      String restOfTheColumn = columnName.substring(1, columnName.length());

      String getMethodName = setOrGet + firstCharOfColumn.toUpperCase() + restOfTheColumn.toLowerCase();
      Class[] parameterTypes = { field.getType() };
      if (setOrGet.equals("get")) {
        parameterTypes = null;
      }
      return objClass.getMethod(getMethodName, parameterTypes);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createUpdateSQL is general SQL -clause parser for all of the UPDATE
   * -clauses.
   * 
   * @return a UPDATE - String containing the WHERE -part of the SQL -clause
   * @exception
   */
  protected String createUpdateSQL(Object obj, Class dataClass, String whereString) throws RockException {
    try {
      String sQLString = "UPDATE ";
      sQLString = new StringBuffer(sQLString).append(getTableName(obj, dataClass)).append(" SET ").toString();
      Field[] classFields = dataClass.getDeclaredFields();
      String setStr = " ";

      Set modCols = null;

      if (obj instanceof RockDBObject)
        modCols = ((RockDBObject) obj).gimmeModifiedColumns();

      for (int i = 0; i < classFields.length; i++) {

        Field field = classFields[i];
        String fieldType = field.getType().getName();
        String fieldTypeName = fieldType.substring(fieldType.lastIndexOf(".") + 1, fieldType.length());

        if (isColumnName(field.getName(), fieldTypeName)) {

          if (modCols != null && !modCols.contains(field.getName()))
            continue;

          if (!setStr.equals(" ")) {
            setStr = new StringBuffer(setStr).append(",").toString();
          }
          setStr = new StringBuffer(setStr).append(field.getName()).append("=").toString();
          Method method = getFieldMethod(dataClass, field, "get");
          setStr = new StringBuffer(setStr).append(getFieldValueWithQuotes(obj, field, method)).toString();
        }
      }
      sQLString = new StringBuffer(sQLString).append(setStr).toString();
      sQLString = new StringBuffer(sQLString).append(whereString).toString();

      return sQLString;
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * compares a string and a string vector
   * 
   * @param String
   * @param String
   *          []
   * @return int index of a string in the string vector
   * @exception
   */

  private int getSeqColIndex(String fieldName, String[] colSeqNames) {
    for (int i = 0; i < colSeqNames.length - 1; i++)
      if (fieldName.toLowerCase().equals(colSeqNames[i].toLowerCase()))
        return i;
    return -1;
  }

  /**
   * Check if the parameter column name exists in the class fields
   * 
   * @param dataClass
   *          class where the field should be
   * @param columnName
   *          column name to compare
   * @return boolean true if the column existed else false
   */
  private boolean columnExists(Class dataClass, String columnName) {
    Field[] classFields = dataClass.getDeclaredFields();

    for (int i = 0; i < classFields.length; i++) {
      Field field = classFields[i];

      if (field.getName().toLowerCase().equals(columnName.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * createInsertSQL is general SQL -clause parser for all of the INSERT
   * -clauses.
   * 
   * @param boolean useTimestamp If true, the current time is automatically
   *        updated.
   * @return a INSERT -String SQL -clause
   * @exception SQLException
   */
  protected String createInsertSQL(Object obj, Class dataClass, boolean useTimestamp, boolean useSequence)
      throws RockException {
    return "HELLO";
  }

  /**
   * getTableName gets the table name from a class name.
   * 
   * @param Class
   *          dataclass
   * @return String tablename
   * @exception
   */

  protected String getTableName(Object obj, Class dataClass) throws RockException {

    try {
      Method getTableNameMethod = dataClass.getMethod(FactoryRes.GET_TABLE_NAME_METHOD_NAME, new Class[] {});
      String tableName = (String) getTableNameMethod.invoke(obj, new Object[] {});

      return tableName;

    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }

  }

  /**
   * fieldValueOtherThanInitial tests wheather the fields value is the initial
   * one or not
   * 
   * @param Object
   *          obj
   * @param Class
   *          whereClass
   * @param Field
   *          field
   * @return Boolean false if the field contains the initial value else true
   * @exception
   */
  protected boolean fieldValueOtherThanInitial(Object obj, Class whereClass, Field field) throws RockException {
    try {
      Method method = getFieldMethod(whereClass, field, "get");
      String[] argv = null;
      Object tempObj = method.invoke(obj, argv);

      Class tempClass = method.getReturnType();

      if (tempObj == null) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      throw new RockException(e.getMessage(), e);
    }
  }

  /**
   * createWhereSQL is general SQL -clause parser for all of the WHERE -parts.
   * 
   * @return a WHERE -SQL clause
   * @exception
   */
  protected String createWhereSQL(Object obj, Class whereClass) throws RockException {
    return "Hello";

  }

  /**
   * createWhereSQLfromPrimaries Creates a SQL clause from primaryKey columns.
   * 
   * @return a WHERE -SQL clause
   * @exception
   */
  private String createWhereSQLfromPrimaries(Object dataObj, String columnNotIncluded) throws RockException {
    String whereSQL = "Goodbye";
    return whereSQL;
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and
   * class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @param boolean useTimestamp If true, the current time is automatically
   *        updated.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  private int insertDataPriv(Object dataObj, boolean useTimestamp, boolean useSequence) throws RockException {
    int records = -1;
    return records;
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and
   * class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @param boolean useTimestamp If true, the current time is automatically
   *        updated.
   * @param boolean useTimestamp If true, the number fields associated with
   *        sequences get values automatically.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  public int insertData(Object dataObj, boolean useTimestamp, boolean useSequence) throws RockException {
    return insertDataPriv(dataObj, useTimestamp, useSequence);
  }

  /**
   * insertData Executes an insert SQL -clause based on given object data and
   * class information.
   * 
   * @param Object
   *          dataObj The data object to insert.
   * @return int Number of inserted rows.
   * @exception SQLException
   */
  public int insertData(Object dataObj) throws RockException {
    return insertDataPriv(dataObj, true, true);
  }

  /**
   * Executes a delete SQL -clause based on given object data and class
   * information.
   * 
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  private int deleteDataPriv(boolean isPrimaryKeyDelete, Object whereObj, String whereString) throws RockException {
    int records = -1;
    return records;
  }

  /**
   * Executes a delete SQL -clause based on given object data and class
   * information.
   * 
   * @param Object
   *          whereObj The data object to delete.
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  public int deleteData(boolean isPrimaryKeyDelete, Object whereObj) throws RockException {
    return deleteDataPriv(isPrimaryKeyDelete, whereObj, null);
  }

  /**
   * Executes a delete SQL -clause based on given object data and class
   * information.
   * 
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @return int Number of deleted rows.
   * @exception SQLException
   */
  public int deleteData(boolean isPrimaryKeyDelete, String whereString) throws RockException {
    return deleteDataPriv(isPrimaryKeyDelete, null, whereString);
  }

  /**
   * updateData Executes an update SQL -clause based on given object data and
   * class information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean isPrimaryKeyUpdate If true, where -clause is created from
   *        primary key information.
   * @param Object
   *          whereObj The data for the WHERE -clause.
   * @param boolean useTimestamp If false, it is not checked wheather the data
   *        has changed.
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @return int Number of updated rows.
   * @exception SQLException
   */
  private int updateDataPriv(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj, boolean useTimestamp,
      String whereString) throws RockException {
    int records = -1;
    return records;

  }

  /**
   * Executes an update SQL -clause based on given object data and class
   * information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean isPrimaryKeyUpdate If true, where -clause is created from
   *        primary key information.
   * @param Object
   *          whereObj The data for the WHERE -clause.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj) throws RockException {
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, whereObj, true, null);
  }

  /**
   * Executes an update SQL -clause based on given object data and class
   * information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean isPrimaryKeyUpdate If true, where -clause is created from
   *        primary key information.
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param boolean useTimestamp If false, it is not checked wheather the data
   *        has changed.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, Object whereObj, boolean useTimestamp)
      throws RockException {
    System.out.println("Saving object " + dataObj);
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, whereObj, useTimestamp, null);
  }

  /**
   * Executes an update SQL -clause based on given object data and class
   * information.
   * 
   * @param Object
   *          dataObj The data object to update.
   * @param boolean isPrimaryKeyUpdate If true, where -clause is created from
   *        primary key information.
   * @param String
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @param boolean useTimestamp If false, it is not checked wheather the data
   *        has changed.
   * @return int Number of updated rows.
   * @exception SQLException
   */
  public int updateData(Object dataObj, boolean isPrimaryKeyUpdate, String whereStr, boolean useTimestamp)
      throws RockException {
    return updateDataPriv(dataObj, isPrimaryKeyUpdate, null, useTimestamp, whereStr);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else
   *          WHERE is constructed from whereDBParameter
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @return
   * @exception SQLException
   */
  private RockResultSet setSelectSQLPriv(boolean isPrimaryKeySelect, Object whereObj, String whereString,
      String orderByStr) throws RockException {
    RockResultSet rockResults = new RockResultSet();
    return rockResults;
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else
   *          WHERE is constructed from whereDBParameter
   * @param whereObj
   *          all field that are != null are used for the WHERE -part
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE).
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(String selectString) throws RockException {
    RockResultSet rockResults = new RockResultSet();
    // try {
    // Statement sqlSelectStatement = connection.createStatement();
    //
    // rockResults.setResultSet(sqlSelectStatement.executeQuery(selectString),
    // sqlSelectStatement);
    // } catch (SQLException sqlE) {
    // throw sqlE;
    // } catch (Exception e) {
    // throw new RockException(e.getMessage(), e);
    // }
    return rockResults;
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else
   *          WHERE is constructed from whereDBParameter
   * @param whereDBParameter
   *          all field that are != null are used for the WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, Object whereObj) throws RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, whereObj, null, null);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else
   *          WHERE is constructed from whereDBParameter
   * @param whereDBParameter
   *          all field that are != null are used for the WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, Object whereObj, String orderByStr)
      throws RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, whereObj, null, orderByStr);
  }

  /**
   * Sets the cursor for a SQL -clause
   * 
   * @param isPrimaryKeySelect
   *          if true the primary key data is used for the WHERE -part, else
   *          WHERE is constructed from whereDBParameter
   * @param whereString
   *          whereStr The data for the WHERE -clause ( a sql clause for the
   *          where part, does not include WHERE). WHERE -part
   * @return
   * @exception SQLException
   */
  public RockResultSet setSelectSQL(boolean isPrimaryKeySelect, String whereString, String orderByStr)
      throws RockException {
    return setSelectSQLPriv(isPrimaryKeySelect, null, whereString, orderByStr);
  }

  /**
   * Gets data from the cursor for a SQL -clause
   * 
   * @param dataObj
   *          holds the data retrieved from the db
   * 
   * @return true while more data is available
   * @exception SQLException
   */
  public Iterator getData(Object dataObj, RockResultSet rockResults) throws RockException {
    // RockResultSet results = rockResults.getResultSet();
    Collection cSql = rockResults.getCollection();
    return cSql.iterator();
  }

  /**
   * Executes an insert SQL -clause based on String.
   * 
   * @param sqlStr
   *          String to execute
   * @exception SQLException
   */
  public void executeSql(String sqlStr) throws RockException {
  }

  /**
   * Executes a prepared SQL -clause based on String.
   * 
   * @param preparedSqlStr
   *          String to execute
   * @param objVec
   *          vector containig the objects related to the string
   * @exception SQLException
   */
  public void executePreparedSql(String preparedSqlStr, Vector rowVec) throws RockException {
  }

  /**
   * Executes a prepared SQL -clause based on String. If update cannot be done
   * the an insert is exec.
   * 
   * @param preparedSqlStr
   *          String to execute
   * @param objVec
   *          vector containig the objects related to the string
   * @exception SQLException
   */
  public void executePreparedInsAndUpdSql(String preparedUpdStr, Vector rowVec, String preparedInsStr)
      throws RockException {
  }

  /**
   * commit Commits the transaction if autocommit is OFF.
   * 
   * @exception SQLException
   */
  public void commit() {
  }

  /**
   * rollback Rollbacks the transaction if autocommit is OFF.
   * 
   * @exception SQLException
   */
  public void rollback() {
  }

  /**
   * begin Begins a transaction (ended by commit/rollback ) DOES NOTHING IN THIS
   * CONTEXT (just to implement the interface needs
   * 
   */
  public void begin() {
  }

  /**
   * Returns the database connect element
   * 
   * 
   * @return The connect element
   */
  public Connection getConnection() {
    return this.connection;
  }

  /**
   * Copies tables child table information
   * 
   * @param tableName
   * @param setValues
   *          key <setMethodName> value {oldValue,newValue}
   */
  public void copySchema(String tableName, Hashtable setValues, String packagePath) throws RockException {
  }

  /**
   * Copies tables child table information
   * 
   * @param tableName
   * @param setValues
   *          key <setMethodName> value {oldValue,newValue}
   */
  public void copySchemaNonRecursive(String[] tableNames, Hashtable setValues, String packagePath) throws RockException {
  }

}
