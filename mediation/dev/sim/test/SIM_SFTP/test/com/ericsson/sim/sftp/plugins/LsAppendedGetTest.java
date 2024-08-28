package com.ericsson.sim.sftp.plugins;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.serialization.SFTPserFile;
import com.ericsson.sim.sftp.serialization.appended.FilePropertyManager;
import com.ericsson.sim.sftp.session.SftpSession;
import com.maverick.ssh.SshException;

public class LsAppendedGetTest {

	private LsAppendedGet classUnderTest;

	private Node node;
	private SFTPproperties sftpProperties;
	private Protocol protocol;
	private SFTPserFile serFile;
	private SftpSession sftpSession;
	private FilePropertyManager fpm;
	private File file;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";

	@Before
	public void setUp() throws Exception {

		SIMEnvironment.PERSPATH = FILE_PATH + "pers";
		SIMEnvironment.SSHKEYSPATH = FILE_PATH+".ssh/id_rsa";
		SIMEnvironment.MAVERICKLICENSE = FILE_PATH+"maverickLicense.txt";
		SIMEnvironment.CONFIGPATH = FILE_PATH;

		fpm = FilePropertyManager.getInstance();

		node = new Node();
		node.setName("name");
		node.generateID();
		node.addProperty("sftpPort", "22");
		node.addProperty("IPAddress", "atrcx2024.athtem.eei.ericsson.se");
		node.addProperty("sftpUserName", "dcuser");

		// Should be protocol but class cast
		sftpProperties = new SFTPproperties();
		sftpProperties.setName("name");
		sftpProperties.generateID();
		sftpProperties.addProperty("remoteDirectory",
				"/eniq/home/dcuser/test/remoteDir");

		sftpProperties.addProperty("lsRegex", "");
		sftpProperties.addProperty("dateFormatInFileName", "HH/mm/ss");


		node.addProperty("firstRop", "firstRop");

		classUnderTest = new LsAppendedGet();

	}

	@After
	public void tearDown() throws Exception {

		new File(Paths.get(SIMEnvironment.PERSPATH).toString()).delete();

		SIMEnvironment.PERSPATH = System.getProperty("PersPath",
				SIMEnvironment.CONFIGPATH + "pers");
		SIMEnvironment.SSHKEYSPATH = System.getProperty("SSHKeysPath",
				"/eniq/home/dcuser/.ssh/id_rsa");
		SIMEnvironment.MAVERICKLICENSE = System.getProperty("MaverickLicense",
				SIMEnvironment.CONFIGPATH + "maverickLicense.txt");
		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");

	}

	@Test
	public void testFirstRopNull() throws IOException {

		if (new File(Paths.get(SIMEnvironment.PERSPATH).toString()).exists()) {

		} else {
			Files.createDirectory(Paths.get(SIMEnvironment.PERSPATH));
		}

		classUnderTest.execute(node, sftpProperties);
	}

	@Test
	public void testFirstRopNotNull() throws IOException {
		node.addProperty("firstRop", String.valueOf(sftpProperties.getID()));
		sftpProperties.addProperty("maxFilesPerRop", "2");

		if (new File(Paths.get(SIMEnvironment.PERSPATH).toString()).exists()) {

		} else {
			Files.createDirectory(Paths.get(SIMEnvironment.PERSPATH));
		}

		classUnderTest.execute(node, sftpProperties);

	}

	@Test
	public void testFindAndCollectFiles() throws SshException {
		node.addProperty("firstRop", String.valueOf(sftpProperties.getID()));
		sftpProperties.addProperty("maxFilesPerRop", "2");

		if (new File(Paths.get(SIMEnvironment.PERSPATH).toString()).exists()) {

		} else {
			try {
				Files.createDirectory(Paths.get(SIMEnvironment.PERSPATH));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		classUnderTest.execute(node, sftpProperties);
		ArrayList<String> list = new ArrayList<>();
		list.add("intervals.simc");
		try {
			classUnderTest.findAndCollectFiles(list);
		} catch (SshException e) {
			e.printStackTrace();
		}

	}

}
