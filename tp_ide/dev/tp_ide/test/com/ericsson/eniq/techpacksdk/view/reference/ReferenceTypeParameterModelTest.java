package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.Vector;

import junit.framework.TestCase;

import org.jdesktop.application.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

public class ReferenceTypeParameterModelTest extends TestCase {
	
	private ReferenceTypeParameterModel testInstance;
	private Referencetable referencetable;

	@Test
	public void testValidateData() {
		Vector<String> expected = new Vector<String>();
		// Empty referencetable.Typename should give a validation error.
		expected.add(ReferenceTypeParameterModel.TYPENAME + " is required");
		Vector<String> actual = testInstance.validateData();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testValidateDataAGGLEVEL() {
		Vector<String> expected = new Vector<String>(); // No error
		referencetable.setTypename("AGGLEVEL");
		Vector<String> actual = testInstance.validateData();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testValidateDataTPNAMEAGGLEVEL() {
		// The format SELECT_(TPNAME)_AGGLEVEL e.g. SELECT_DC_E_MGW_AGGLEVEL should give a validation error.
		Vector<String> expected = new Vector<String>();
		referencetable.setTypename("SELECT_DC_E_MGW_AGGLEVEL");
		expected.add(referencetable.getTypename()+" reference type cannot be used as it is defined in the base techpack.");
		Vector<String> actual = testInstance.validateData();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstructor() {
		assertNotNull(testInstance);
	}

	@Before
    protected void setUp() throws Exception {
		Application application = null;
		
		RockFactory rockFactory = null; 
		boolean isTreeEditable = false;
		Versioning versioning = new Versioning(rockFactory);
		versioning.setTechpack_name("DC_E_MGW");
		referencetable = new Referencetable(rockFactory);
	    referencetable.setTypename("");
		testInstance = new ReferenceTypeParameterModel(application, versioning, referencetable, rockFactory, isTreeEditable);
	}

	@After
    public void tearDown() throws Exception {
		testInstance = null;
	}

}
