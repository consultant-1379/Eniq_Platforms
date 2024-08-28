package com.ericsson.sim.sftp.serialization.appended;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.stream.FileImageInputStream;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.constants.SIMEnvironment;

public class FilePropertyManagerTest {
	private FilePropertyManager classUnderTest;
//	 private final String FILE_PATH = Paths.get(".").toAbsolutePath()
//	 .normalize().toString()
//	 + "/test/";

	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_SFTP/test/";
	
	
	protected ConcurrentHashMap<String, FileProperties> map;

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.CONFIGPATH = FILE_PATH + "FilePropertyManager/";

		if (new File(FILE_PATH + "FilePropertyManager/").exists()) {

		} else {
			Files.createDirectory(Paths.get(FILE_PATH + "FilePropertyManager/"));
		}

		classUnderTest = FilePropertyManager.getInstance();
		classUnderTest.files.clear();
	}

	@After
	public void tearDown() throws Exception {

		classUnderTest.removeProperties("There");
		classUnderTest.removeProperties("NotThereShouldAdd");
		classUnderTest.removeProperties("prop");
		classUnderTest.removeProperties("prop1");
		classUnderTest.removeProperties("prop2");
		classUnderTest.removeProperties("appendedFiles.simc");

		classUnderTest.destroyInstance();

		Files.deleteIfExists(Paths.get(SIMEnvironment.CONFIGPATH,
				"appendedFiles.simc"));
File folder = new File(SIMEnvironment.CONFIGPATH);
		File[] files = folder.listFiles();
		for(int i=0; i<files.length;i++){
			files[i].delete();
		}
		Files.deleteIfExists(Paths.get(SIMEnvironment.CONFIGPATH));

		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddAndGetRemoveProperties() {
		FileProperties props = new FileProperties();

		classUnderTest.addProperties("There", props);
		Object fp = classUnderTest.getProperties("NotThereShouldAdd");

		assertTrue(fp instanceof FileProperties);
		assertEquals(props, classUnderTest.getProperties("There"));

		@SuppressWarnings("rawtypes")
		Map files = new HashMap<String, FileProperties>();
		files.put("There", props);

		// Removing property, then adding exact same key to
		// prove that it is different i.e. has been removed
		// to the new one just added from the method call in the assert
		classUnderTest.removeProperties("NotThereShouldAdd");
		assertNotSame("fp", classUnderTest.getProperties("NotThereShouldAdd"));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testWritePerFile() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		Map<String, FileProperties> files = new HashMap<String, FileProperties>();
		map = new ConcurrentHashMap<String, FileProperties>();

		FileProperties props = new FileProperties();
		FileProperties props1 = new FileProperties();
		FileProperties props11 = new FileProperties();

		classUnderTest.addProperties("prop", props);
		classUnderTest.addProperties("prop1", props1);
		classUnderTest.addProperties("prop2", props11);

		files.put("prop", props);
		files.put("prop1", props11);
		files.put("prop2", props11);

		classUnderTest.writePerFile();

		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				new File(SIMEnvironment.CONFIGPATH + "appendedFiles.simc")));

	
		ConcurrentHashMap<String, FileProperties> ma = (ConcurrentHashMap<String, FileProperties>) in.readObject();

		map.putAll(ma);

		in.close();

		assertTrue(files.keySet().equals(map.keySet()));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLoadPersistedFile() {
		
		map = new ConcurrentHashMap<String, FileProperties>();

		FileProperties props = new FileProperties();
		FileProperties props1 = new FileProperties();
		FileProperties props11 = new FileProperties();
		classUnderTest.addProperties("prop", props); 
		classUnderTest.addProperties("prop1", props1);
		classUnderTest.addProperties("prop2", props11);

		classUnderTest.writePerFile();
		classUnderTest.loadPersistedFile();

		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(new File(
					SIMEnvironment.CONFIGPATH + "appendedFiles.simc")));
			ConcurrentHashMap<String, FileProperties> ma = (ConcurrentHashMap<String, FileProperties>) in.readObject();
			map.putAll(ma);
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("FilePropertyTest" + e.getMessage());
		}

		assertTrue(classUnderTest.files.keySet().equals(map.keySet()));

	}

	@Test
	public void testDestroyInstance() {
		FilePropertyManager secondInstance = FilePropertyManager.getInstance();
		assertTrue(classUnderTest.equals(secondInstance));
	}

}
