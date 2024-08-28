package com.ericsson.sim.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class ProtocolPrinterTest {

	private SFTPproperties properties;
	private ProtocolPrinter protocolPrinter;
	private HashMap<String, String> map;

	@Before
	public void setUp() throws Exception {
		properties = new SFTPproperties();
		for (int i = 0; i < 3; i++) {
			properties.addProperty("key" + i, "value" + 1);
		}
		properties.setName("name");
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testprintProtocol() {

		protocolPrinter = new ProtocolPrinter(properties);
		protocolPrinter.printProtocol();
		

	}

}
