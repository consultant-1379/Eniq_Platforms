package com.ericsson.sim.rmi;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;

public class SIMStatusTest {
	private SIMStatus simStatus;

	@Before
	public void setUp() throws Exception {
		simStatus = new SIMStatus();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStatus() throws RemoteException {
		System.setProperty("SIMLicense", "valid");
		ArrayList<String> list = (ArrayList<String>) simStatus.status();
		
		assertEquals(11, list.size());
		assertEquals("--- SIM ---" ,list.get(0));
		
	}

}
