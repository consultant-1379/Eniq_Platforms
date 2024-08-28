/**
 * ETL Repository access library.<br>
 * <br>
 * Copyright &copy; Distocraft Ltd. 2004-5. All rights reserved.<br>
 * 
 * @author lemminkainen
 */
package ssc.rockfactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class RockResultSet {

  private Collection sqlResults;

  private ResultSet results;

  private Statement statement;

  public RockResultSet() {
    sqlResults = new ArrayList();
  }

  public void setResultSet(ResultSet results, Statement statement) {
    this.results = results;
    this.statement = statement;
  }

  public ResultSet getResultSet() {
    return this.results;
  }

  public void setCollection(Collection sqlResults) {
    this.sqlResults = sqlResults;
  }

  public Collection getCollection() {
    return this.sqlResults;
  }

  public void close() throws SQLException {
    if (this.results != null)
      this.results.close();
    if (this.statement != null)
      this.statement.close();
  }

}
