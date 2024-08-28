package ssc.rockfactory;

import java.util.Set;


public interface RockDBObject {

  public Set gimmeModifiedColumns();
  public void cleanModifiedColumns();
   
}
