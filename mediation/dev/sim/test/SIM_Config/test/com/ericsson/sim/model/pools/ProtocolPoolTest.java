package com.ericsson.sim.model.pools;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.protocol.Protocol;

public class ProtocolPoolTest {

	private ProtocolPool obj;
	private Protocol protocol = new Protocol();
	// private final String FILE_PATH =
	// Paths.get(".").toAbsolutePath().normalize().toString();
	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_Config/";

	@Before
	public void setUp() throws Exception {

//		if (new File(FILE_PATH).exists()) {
//
//		} else {
//			Files.createDirectory(Paths.get(FILE_PATH));
//		}

		obj = ProtocolPool.getInstance();
		protocol.setName("name");
	}

	@After
	public void tearDown() throws Exception {
		//Files.deleteIfExists(Paths.get(
			//	(FILE_PATH + "configFiles/protocols.simc")).toAbsolutePath());

//		Files.deleteIfExists(Paths.get((FILE_PATH + "configFiles"))
//				.toAbsolutePath());
//		Files.deleteIfExists(Paths.get((FILE_PATH)).toAbsolutePath());
//
//		Files.deleteIfExists(Paths.get((FILE_PATH + "config/intervals.simc"))
//				.toAbsolutePath());
//		Files.deleteIfExists(Paths.get((FILE_PATH + "config/protocols.simc"))
//				.toAbsolutePath());
//		Files.deleteIfExists(Paths.get((FILE_PATH + "config/topology.simc"))
//				.toAbsolutePath());
//		Files.deleteIfExists(Paths.get((FILE_PATH + "config/properties.simc"))
//				.toAbsolutePath());
//		Files.deleteIfExists(Paths.get((FILE_PATH + "config")).toAbsolutePath());

		obj.getProtocols().clear();
	}

	@Test
	public void testgetInstance() {
		assertNotNull(ProtocolPool.getInstance());
	}

	@Test
	public void testgetProtocol() {
		assertNull(obj.getProtocol(protocol.getID()));
	}

	@Test
	public void testcontainsProtocolID() {
		assertFalse(obj.containsProtocol(protocol.getID()));
		obj.addProtocol(protocol);
		assertTrue(obj.containsProtocol(protocol.getID()));
	}

	@Test
	public void testcontainsProtocolName() {
		assertFalse(obj.containsProtocol(protocol.getName()));
		obj.addProtocol(protocol);
		assertTrue(obj.containsProtocol(protocol.getName()));
	}

	@Test
	public void testaddProtocol() {
		obj.addProtocol(protocol);
		assertEquals(protocol, obj.getProtocol(protocol.getID()));
		obj.addProtocol(protocol);
		assertEquals(protocol, obj.getProtocol(protocol.getID()));
		assertEquals(obj.getProtocols().size(), 1);
	}

	@Test
	public void testgetProtocols() {
		assertEquals(obj.getProtocols().size(), 0);
		obj.addProtocol(protocol);
		assertEquals(obj.getProtocols().size(), 1);
	}

//	@Test
//	public void testwritePersistedFile() throws Exception {
//
//		assertFalse(new File(FILE_PATH + "configFiles").exists());
//		obj.writePersistedFile(FILE_PATH + "configFiles");
//		assertTrue(new File(FILE_PATH + "configFiles").exists());
//
//	}

//	@Test
//	public void testloadPersistedFile() throws Exception {
//		obj.addProtocol(protocol);
//		obj.writePersistedFile(FILE_PATH + "configFiles");
//		Thread.sleep(1000);
//		obj.loadPersistedFile(FILE_PATH + "configFiles/properties.simc");
//		HashMap<Integer, Protocol> actualPRotocols = obj.getProtocols();
//		assertEquals(actualPRotocols.size(), 1);
//	}

}
