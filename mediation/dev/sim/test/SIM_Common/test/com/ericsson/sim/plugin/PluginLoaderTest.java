package com.ericsson.sim.plugin;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.sftp.plugins.LsAppendedGet;

public class PluginLoaderTest {

	private PluginLoader pLoader;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize()
			.toString()
			+ "/test/";

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.PERSPATH = FILE_PATH + "pers";
		SIMEnvironment.CONFIGPATH = FILE_PATH;
		pLoader = PluginLoader.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		SIMEnvironment.PERSPATH = System.getProperty("PersPath",
				SIMEnvironment.CONFIGPATH + "/pers");
	}

	@Test
	public void testgetInstance() {
		assertNotNull(PluginLoader.getInstance());
	}

	@Test
	public void testgetPluginNotBSH() throws SIMException {

		assertTrue(pLoader
				.getPlugin("com.ericsson.sim.sftp.plugins.LsAppendedGet") instanceof LsAppendedGet);

	}

	@Test(expected = SIMException.class)
	public void testgetPluginBSH() throws SIMException {

		pLoader.getPlugin(".bsh");

	}

}
