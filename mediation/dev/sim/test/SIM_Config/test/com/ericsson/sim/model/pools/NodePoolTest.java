package com.ericsson.sim.model.pools;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.model.node.Node;

public class NodePoolTest {
	
	private NodePool nodePool;
	private Node node = new Node();
	private Node anotherNode = new Node(); 
	//private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString();
	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_Config";
	
	@Before
	public void setUp() throws Exception {
		nodePool = NodePool.getInstance();
		node.setName("name");
		node.addProperty("IPAddress", "1.2.3.");
		node.addProperty("uniqueID", "1");
		node.generateID();
		anotherNode.setName("anotherName");
		anotherNode.addProperty("IPAddress", "3.2.1");
		anotherNode.addProperty("uniqueID", "2");
		anotherNode.generateID();
	}

	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(Paths.get((FILE_PATH + "/fake/topology.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/fake")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/testconfig/topology.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/testconfig")).toAbsolutePath());
		
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/intervals.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/protocols.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/topology.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config/properties.simc")).toAbsolutePath());
		Files.deleteIfExists(Paths.get((FILE_PATH + "/config")).toAbsolutePath());
		nodePool.getNodes().clear();
	}

	@Test
	public void testgetInstance() {
		assertNotNull(NodePool.getInstance());
	}

	@Test
	public void testgetNode() {
		assertNull(nodePool.getNode(node.getID()));
	}
	

	@Test
	public void testgetNodes() {
		assertEquals(nodePool.getNodes().size(), 0);
		nodePool.addNode(node);
		assertEquals(nodePool.getNodes().size(), 1);
		nodePool.addNode(anotherNode);
		assertEquals(nodePool.getNodes().size(), 2);
		
	}
	
	@Test
	public void testaddNode() {
		Node nnode = new Node();
		nodePool.addNode(nnode);
		assertEquals(1, nodePool.getNodes().size());
		assertEquals(nnode, nodePool.getNodes().get(nnode.getID()));
	}
	
	@Test
	public void testhasNode() {
		assertFalse(nodePool.hasNode(node.getID()));
		nodePool.addNode(node);
		assertTrue(nodePool.hasNode(node.getID()));
	}
	
	@Test
	public void testwritePersistedFile() throws Exception {
		File fakeFile = new File(FILE_PATH + "/fake");
		assertTrue(!fakeFile.exists());
		nodePool.writePersistedFile(FILE_PATH + "/fake");
		assertTrue(fakeFile.exists());
		
		File file = new File(FILE_PATH + "/config");
		//assertTrue(file.exists());
    	NodePool.getInstance().writePersistedFile(FILE_PATH + "/config");
    	assertTrue((new File(FILE_PATH + "/config/topology.simc")).exists());
	}
	
	@Test
	public void testloadPersistedFile() throws Exception {
		nodePool.addNode(node);
		nodePool.addNode(anotherNode);
		nodePool.writePersistedFile(FILE_PATH + "/config");
		assertTrue((new File(FILE_PATH + "/config/topology.simc")).exists());
		nodePool.loadPersistedFile(FILE_PATH + "/config/topology.simc");
		HashMap<Integer, Node> actualNodes = nodePool.getNodes();
    	assertEquals(actualNodes.size(), 2);
	}
	

}



