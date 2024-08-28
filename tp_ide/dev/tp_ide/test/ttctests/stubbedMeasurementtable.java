package ttctests;

import java.sql.SQLException;


import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class stubbedMeasurementtable extends measurementType.Measurementtable {

    public boolean isSaved = false;
    public boolean isDeleted = false;
    public boolean planChanged = false;

    public stubbedMeasurementtable(RockFactory rockFactory) {
	super(rockFactory);
    }

    @Override
    public void saveDB() throws SQLException, RockException {
	isSaved = true;
    }

    @Override
    public void setPartitionplan(String newPlan) {
	planChanged = true;
    }

    @Override
    public int deleteDB() throws SQLException, RockException {
	isDeleted = true;
	return super.deleteDB();
    }

}
