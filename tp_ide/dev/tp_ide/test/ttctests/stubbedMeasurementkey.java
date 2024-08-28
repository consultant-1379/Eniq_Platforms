package ttctests;

import java.sql.SQLException;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class stubbedMeasurementkey extends measurementType.Measurementkey {

    public boolean isSaved = false;
    public boolean isDeleted = false;

    public stubbedMeasurementkey(RockFactory rockFactory) {
	super(rockFactory);
    }

    @Override
    public void saveDB() throws SQLException, RockException {
	isSaved = true;
    }

    @Override
    public int deleteDB() throws SQLException, RockException {
	isDeleted = true;
	return super.deleteDB();
    }

}
