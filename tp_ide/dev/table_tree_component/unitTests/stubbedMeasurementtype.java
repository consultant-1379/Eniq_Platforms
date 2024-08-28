package unitTests;

import java.sql.SQLException;

import ssc.rockfactory.Measurementtype;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

public class stubbedMeasurementtype extends Measurementtype {

    public boolean isSaved = false;
    public boolean isDeleted = false;
    public boolean descriptionChanged = false;

    public stubbedMeasurementtype(RockFactory rockFactory) {
	super(rockFactory);
    }

    @Override
    public void saveDB() throws SQLException, RockException {
	isSaved = true;
    }

    @Override
    public void setDescription(String newDescription) {
	descriptionChanged = true;
    }

    @Override
    public int deleteDB() throws SQLException, RockException {
	isDeleted = true;
	return super.deleteDB();
    }

}
