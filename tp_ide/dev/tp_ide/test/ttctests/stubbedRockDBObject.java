package ttctests;

import java.sql.SQLException;
import java.util.Set;
import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;


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

  public int deleteDB() {
    isDeleted = true;
	return 0;
  }

  public void saveDB() {
    isSaved = true;
  }

@Override
public int insertDB() throws SQLException, RockException {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public int updateDB() throws SQLException, RockException {
	// TODO Auto-generated method stub
	return 0;
}

public Object clone()  {
    try {
		return super.clone();
	} catch (CloneNotSupportedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
}
