package com.ericsson.sim.sftp.session;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.internal.ExpectationCollector;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.maverick.sftp.SftpFile;
import com.maverick.sftp.SftpStatusException;
import com.maverick.ssh.HostKeyVerification;
import com.maverick.ssh.ShellTimeoutException;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.SshConnector;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.components.SshPublicKey;
import com.sshtools.sftp.SftpClient;

public class SftpSessionTest {

	private SftpSession session;
	private SessionManager sessionManager;
	private Node node;
	private final String DIR = "/eniq/home/dcuser/test/remoteDir";
	//private final String FILE_PATH = Paths.get(".").toAbsolutePath()
		//	.normalize().toString()
			//+ "/test/";
	
	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_SFTP/test/";

	@SuppressWarnings("static-access")
	@Before
	public void setUpAndTestConnectInitialize() throws Exception {

		SIMEnvironment.SSHKEYSPATH = FILE_PATH + ".ssh/id_rsa";
		SIMEnvironment.MAVERICKLICENSE = FILE_PATH + "maverickLicense.txt";
		sessionManager = sessionManager.getInstance();

		node = new Node();
		node.setName("name");
		node.generateID();
		node.addProperty("sftpPort", "22");
		node.addProperty("IPAddress", "atrcx2024.athtem.eei.ericsson.se");
		node.addProperty("sftpUserName", "dcuser");

		sessionManager.loadMaverickLicense();
		session = new SftpSession(SIMEnvironment.SSHKEYSPATH);
		session.initialize();

		// session.connect("atrcx2024.athtem.eei.ericsson.se", 22, "dcuser");
	}

	@After
	public void tearDown() throws Exception {

		SIMEnvironment.SSHKEYSPATH = System.getProperty("SSHKeysPath",
				"/eniq/home/dcuser/.ssh/id_rsa");
		SIMEnvironment.MAVERICKLICENSE = System.getProperty("MaverickLicense",
				SIMEnvironment.CONFIGPATH + "/maverickLicense.txt");
	}

	@Test
	public void testInitializeThrowsSIMException() throws SIMException {
		session.disconnect();
		Node n = new Node();
		n.setName("n");
		n.generateID();
		n.addProperty("sftpPort", "22");
		n.addProperty("IPAddress", "atrcx202 4.athtem.eei.ericsson.se");
		n.addProperty("sftpUserName", "root");

		SftpSession s = new SftpSession("not there");
		s.initialize();

	}

	@Test(expected = SIMException.class)
	public void testSetKeysThrowsSshExceptionWrappedInSIMException()
			throws SIMException {

		session.connect("atrcx2024.athtem.eei.ericsson.se", 22, "badUser");
	}

//	@Test(expected = SIMException.class)
//	public void testGetThrowsSIMException() throws SIMException, SshException {
//		final String remote = "/eniq/home/dcuser/test/remoteDir/";
//		final String local = "C:/Users/ecalmcg/Desktop/stuff/serverCopy";
//
//		session.get(remote, local);
//
//	}

	@Test
	public void testDisconnect() {
		try {
			session.disconnect();
		} catch (SIMException e) {
			System.out.println("SftpSessiontest " + e.getMessage());
		}
	}
//
//	@Test
//	public void testLS() throws SIMException {
//		SftpFile[] file = session.ls("/eniq/home/dcuser/test/remoteDir/");
//		assertEquals(6, file.length);
//
//		SftpFile[] fileTwo = session.ls();
//		assertEquals(20, fileTwo.length);
//
//	}

//	@Test(expected = SIMException.class)
//	public void testLSWithFileNameThrowsSIMException() throws SIMException {
//		SftpFile[] file = session.ls("/incorrect/path");
//		assertTrue(file.length == 0);
//
//	}
//
//	@Test(expected = SIMException.class)
//	public void testLSNoFileNameThrowsSIMException() throws SIMException {
//
//		session.disconnect();
//		SftpFile[] fileTwo = session.ls();
//		
//
//	}

//	@Test(expected = SIMException.class)
//	public void testLSNoFileNameThrowsSSHExceptionWrappedInSIMException()
//			throws SIMException {
//
//		session.client.disconnect();
//		session.ls();
//
//	}
//
//
//	@Test(expected = SIMException.class)
//	public void testLSWithFileNameThrowsSSHExceptionWrappedInSIMException()
//			throws SIMException {
//
//		session.client.disconnect();
//		session.ls("/eniq/home/dcuser/test/remoteDir");
//
//	}

//	@Test
//	public void testLCD() throws SIMException {
//		session.lcd("C://Program Files");
//	}
//
//	@Test(expected = SIMException.class)
//	public void testLCDThrowsSIMException() throws SIMException {
//		session.lcd("C://Prog");
//	}
//
//	@Test
//	public void testCD() throws SIMException {
//		session.cd("/eniq/home/dcuser/test/remoteDir");
//	}
//
//	@Test(expected = SIMException.class)
//	public void testCDThrowsSIMException() throws SIMException {
//		session.cd("/eniq/dcuser/test/remoteDir");
//	}

//	@Test(expected = SIMException.class)
//	public void testCDThrowsSshExceptionWrappedInSIMException()
//			throws SIMException {
//		session.client.disconnect();
//		session.cd("/eniq/home/dcuser/test/remoteDir");
//	}
//
//	@Test
//	public void testCopyRemoteDirectory() throws SIMException {
//		try {
//			session.copyRemoteDirectory("/eniq/home/dcuser/test/remoteDir",
//					"C:/Users/ecalmcg/Desktop/stuff/serverCopy", true, true,
//					true);
//		} catch (SIMException e) {
//			System.out.println("SftpSessionTest" + e.getMessage());
//		}
//
//	}

}
