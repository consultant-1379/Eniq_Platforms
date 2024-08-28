package unitTests;

import java.util.Set;

import ssc.rockfactory.RockDBObject;

public class stubbedRockDBObject implements RockDBObject {

  public boolean isDeleted = false;

  public boolean isSaved = false;

  public int value0 = 123;

  public int value1 = 213;

  public void cleanModifiedColumns() {

  }

  public Set gimmeModifiedColumns() {
    return null;
  }

  public void deleteDB() {
    isDeleted = true;
  }

  public void saveDB() {
    isSaved = true;
  }
}
