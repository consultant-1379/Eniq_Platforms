/**
 * Simple file for holding data related to ITable comparison in DatabaseTester-class
 */
package com.ericsson.eniq.techpacksdk.unittest.utils;


/**
 * @author epetrmi
 *
 */
public class TestTableItem {

    String className;
    String methodName;
    
    String dbName;
    String tableName;
    String[] ignoredCols;
    
    //constructor
    public TestTableItem(String className, String methodName, String dbName, String tableName, String[] ignoredCols) {
      super();
      this.className = className;
      this.dbName = dbName;
      this.ignoredCols = ignoredCols;
      this.methodName = methodName;
      this.tableName = tableName;
    }

    
    public String getClassName() {
      return className;
    }

    
    public void setClassName(String className) {
      this.className = className;
    }

    
    public String getMethodName() {
      return methodName;
    }

    
    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

    
    public String getDbName() {
      return dbName;
    }

    
    public void setDbName(String dbName) {
      this.dbName = dbName;
    }

    
    public String getTableName() {
      return tableName;
    }

    
    public void setTableName(String tableName) {
      this.tableName = tableName;
    }

    
    public String[] getIgnoredCols() {
      return ignoredCols;
    }

    
    public void setIgnoredCols(String[] ignoredCols) {
      this.ignoredCols = ignoredCols;
    }
    
    
    
        
}
