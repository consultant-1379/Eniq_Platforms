package com.ericsson.sim.sftp.serialization;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;

public class SFTPserFileTest {
	private SFTPserFile classUnderTest;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.CONFIGPATH = FILE_PATH;

		classUnderTest = new SFTPserFile();
	}

	@After
	public void tearDown() throws Exception {

		if (new File(SIMEnvironment.CONFIGPATH + "SftpSerFileTest").exists()) {
			new File(SIMEnvironment.CONFIGPATH + "SftpSerFileTest").delete();
		}

		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");
	}

	@Test
	public void testWriteSerFile() throws SIMException, IOException,
			ClassNotFoundException {
		String path = SIMEnvironment.CONFIGPATH + "SftpSerFileTest";
		ArrayList<String> collection = new ArrayList<>();
		classUnderTest.writeserFile(path, collection);

		ObjectInputStream fin = new ObjectInputStream(new FileInputStream(path));
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) fin.readObject();
		fin.close();
		assertEquals(collection, list);
	}

	@Test
	public void testLoadPersistedFile() throws SIMException {

		String path = SIMEnvironment.CONFIGPATH + "SftpSerFileTest";
		ArrayList<String> collection = new ArrayList<>();
		classUnderTest.writeserFile(path, collection);

		assertNotSame(
				collection,
				classUnderTest.loadPersistedFile(SIMEnvironment.CONFIGPATH
						+ "SftpSerFileTestNotThere"));
		assertEquals(collection, classUnderTest.loadPersistedFile(path));
	}

	@Test
	public void testContainsSERFile() throws SIMException {

		String path = SIMEnvironment.CONFIGPATH + "SftpSerFileTest";
		assertFalse(classUnderTest.containsSERFile(path));
		ArrayList<String> collection = new ArrayList<>();
		classUnderTest.writeserFile(path, collection);
		assertTrue(classUnderTest.containsSERFile(path));

	}

}
