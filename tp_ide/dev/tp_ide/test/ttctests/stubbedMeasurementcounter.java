package ttctests;

import java.sql.SQLException;

import measurementType.*;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class stubbedMeasurementcounter extends Measurementcounter {

    public boolean isSaved = false;
    public boolean isDeleted = false;

    public stubbedMeasurementcounter(RockFactory rockFactory) {
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
