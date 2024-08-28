package com.ericsson.sim.sftp.session;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.HashMap;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.sshtools.sftp.SftpClient;

public class SessionManagerTest {
	private SessionManager sessionManager;
	private Node node;
	private SftpSession session;
	// private final String FILE_PATH = Paths.get(".").toAbsolutePath()
	// .normalize().toString()
	// + "/test/";

	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_SFTP/test/";

	Mockery context = new Mockery() { 
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@SuppressWarnings({ "static-access" })
	@Before
	public void setUp() throws Exception {
		SIMEnvironment.SSHKEYSPATH = FILE_PATH + ".ssh/id_rsa";
		SIMEnvironment.MAVERICKLICENSE = FILE_PATH + "maverickLicense.txt";

		sessionManager = sessionManager.getInstance();

		node = new Node();
		node.setName("name");
		node.generateID();
		node.addProperty("sftpPort", "22");
		node.addProperty("IPAddress", "atrcx2024.athtem.eei.ericsson.se");
		node.addProperty("sftpUserName", "dcuser");

		// Must create an actual connection
		// session = sessionManager.createSession(node);

	}

	@After
	public void tearDown() throws Exception {

		SIMEnvironment.SSHKEYSPATH = System.getProperty("SSHKeysPath",
				"/eniq/home/dcuser/.ssh/id_rsa");
		SIMEnvironment.MAVERICKLICENSE = System.getProperty("MaverickLicense",
				SIMEnvironment.CONFIGPATH + "/maverickLicense.txt");
	}
	
	@Test 
	public void testExplained(){
		// All these tests cannot be done due to need of SFTP connection
		// done with feature test
	}

//	@Test
//	public void testCreateSession() {
//		assertTrue(session instanceof SftpSession);
//	}
//
//	@Test
//	public void testRemoveSessionByNodeID() {
//		HashMap<Integer, SftpSession> sessions;
//		sessions = new HashMap<Integer, SftpSession>(sessionManager.sessions);
//
//		assertSame(sessionManager.sessions.keySet().size(), sessions.keySet()
//				.size());
//
//		sessionManager.removeSession(node.getID());
//
//		assertNotSame(sessionManager.sessions.keySet(), sessions.keySet());
//		assertNotSame(sessionManager.sessions.keySet().size(), sessions
//				.keySet().size());
//
//	}
//
//	@Test
//	public void testRemoveSessionByNode() {
//		HashMap<Integer, SftpSession> sessions;
//		sessions = new HashMap<Integer, SftpSession>(sessionManager.sessions);
//
//		assertSame(sessionManager.sessions.keySet().size(), sessions.keySet()
//				.size());
//
//		sessionManager.removeSession(node);
//
//		assertNotSame(sessionManager.sessions.keySet(), sessions.keySet());
//		assertNotSame(sessionManager.sessions.keySet().size(), sessions
//				.keySet().size());
//
//	}
//
//	@Test
//	public void testGetSession() {
//		SftpSession s = null;
//
//		try {
//			s = sessionManager.getSession(node);
//		} catch (SIMException e) {
//			System.out.println("SessionManagerTest" + e.getMessage());
//		}
//
//		assertTrue(session.hashCode() == s.hashCode());
//
//	}

}
