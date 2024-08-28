package com.ericsson.eniq.techpacksdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


public class LoginTaskTest {

	@Test
	public void testGetEtlRepAuthenticationDetails(){
		TechPackIDE tpide = new TechPackIDE();
		LoginTask loginTask = new LoginTask(tpide, null, null, null, null);
		try {
			String result = loginTask.getEtlRepAuthenticationDetails();
			String expected = "?SQLINITSTRING=SET TEMPORARY OPTION CONNECTION_AUTHENTICATION='Company=Ericsson;Application=ENIQ;Signature=000fa55157edb8e14d818eb4fe3db41447146f1571g539f0a8f80fd6239ea117b9d74be36c19c58dc14'";
			assertEquals(expected, result);
		} catch (Exception e) {
			fail("ETLCServer.properties files not found.");
		}

	} //testGetEtlRepAuthenticationDetails

	@Test
	public void testGetEtlRepAuthenticationDetailsEmpty() throws Exception{
		TechPackIDE tpide = new TechPackIDE();
		tpide.setEtlAuth("");
		LoginTask loginTask = new LoginTask(tpide, null, null, null, null);
		String result = loginTask.getEtlRepAuthenticationDetails();
		String expected = "";
		assertEquals(expected, result);
	} //testGetEtlRepAuthenticationDetailsEmpty

}
