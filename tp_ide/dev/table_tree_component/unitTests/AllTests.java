package unitTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
	TestSuite suite = new TestSuite("Test for unitTests");
	// $JUnit-BEGIN$
	suite.addTestSuite(MeasurementTypeFactoryTest.class);
	suite.addTestSuite(MeasurementTypeCounterTableModelTest.class);
	suite.addTestSuite(MeasurementTypeKeyTableModelTest.class);
	suite.addTestSuite(TablePopupMenuListenerTest.class);
	suite.addTestSuite(MeasurementTypeParameterModelTest.class);
	suite.addTestSuite(TableTreeComponentTest.class);
	suite.addTestSuite(FilterDialogTest.class);
	suite.addTestSuite(TableInformationTest.class);	
	// $JUnit-END$
	return suite;
    }

}
