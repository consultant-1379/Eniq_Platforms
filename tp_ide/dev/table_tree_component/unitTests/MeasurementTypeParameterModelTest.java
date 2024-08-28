package unitTests;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import junit.framework.TestCase;

import measurementType.MeasurementTypeParameterModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

public class MeasurementTypeParameterModelTest extends TestCase {

    stubbedMeasurementtable tableObject = null;
    stubbedMeasurementtype typeObject = null;
    RockFactory rockFactory = null;
    MeasurementTypeParameterModel modelUnderTest = null;

    @Before
    public void setUp() throws Exception {

	rockFactory = new RockFactory("anUrl", "user", "password", "driver",
		"con", false);
	tableObject = new stubbedMeasurementtable(rockFactory);
	typeObject = new stubbedMeasurementtype(rockFactory);
	modelUnderTest = new MeasurementTypeParameterModel(typeObject,
		tableObject, rockFactory, true);
	modelUnderTest.createParametersPanel();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testActionPerformedSizing() {
	ActionEvent ae = new ActionEvent(new JPanel(), 0, "Sizing");
	modelUnderTest.actionPerformed(ae);
	assertTrue(
		"The table object should have been changed as a result of THE action",
		tableObject.planChanged);
    }

    @Test
    public void testActionPerformedDescription() {
	ActionEvent ae = new ActionEvent(new JPanel(), 0, "Description");
	modelUnderTest.actionPerformed(ae);
	assertTrue(
		"The table object should have been changed as a result of THE action",
		typeObject.descriptionChanged);
    }

    @Test
    public void testActionPerformedRandomCheckbox() {
	ActionEvent ae = new ActionEvent(new JPanel(), 0, "Ranking");
	modelUnderTest.actionPerformed(ae);

	/*
	 * Uncomment and update this assert when a target data for the checkbox
	 * has been identified and stubbed checkable
	 */

	// assertTrue("The table object should have been changed as a result of
	// THE action", typeObject.descriptionChanged);
    }

    @Test
    public void testActionPerformedRandomJTextField() {
	ActionEvent ae = new ActionEvent(new JPanel(), 0, "Total Agg Desc");
	modelUnderTest.actionPerformed(ae);

	/*
	 * Uncomment and update this assert when a target data for the checkbox
	 * has been identified and stubbed checkable
	 */

	// assertTrue("The table object should have been changed as a result of
	// THE action", typeObject.descriptionChanged);
    }
}
