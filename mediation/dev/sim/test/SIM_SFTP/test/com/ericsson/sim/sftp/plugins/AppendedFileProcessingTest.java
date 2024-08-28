package com.ericsson.sim.sftp.plugins;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.Protocol;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.plugins.parser.ParserParent;

public class AppendedFileProcessingTest {

	private AppendedFileProcessing underTest;
	private SFTPproperties property;
	private Node node;
	//private final String FILE_PATH = Paths.get(".").toAbsolutePath()
	//		.normalize().toString()
	//		+ "/test";

	private final String FILE_PATH = "/view/ossadm100_design_SIM/vobs/eniq/design/plat/mediation/dev/sim/test/SIM_SFTP/test";

	@Before
	public void setUp() throws Exception {
		System.setProperty("RuntimePath", FILE_PATH);
		SIMEnvironment.PARSINGPATH = FILE_PATH;

		underTest = new AppendedFileProcessing();
		node = new Node();
		property = new SFTPproperties();
		property.addProperty("ParserName",
				"com.ericsson.sim.sftp.plugins.parser.DummyParser");
		property.setName("name");
		property.generateID();
		node.setName("name");
		node.generateID();

	}

	@Test
	public void testFilesNotThere() throws IOException {
		underTest.execute(node, property);


		assertTrue(Files.exists(Paths.get(SIMEnvironment.PARSINGPATH)));

	}

	@Test
	public void testShouldBeAppending() throws IOException, SIMException {

		assertTrue(new File(FILE_PATH).exists());

		Files.createFile(Paths.get(
				SIMEnvironment.PARSINGPATH + "/" + node.getID() + "_"
						+ property.getID(),
				node.getID() + "_" + property.getID() ));

		assertTrue(new File(SIMEnvironment.PARSINGPATH + "/" + node.getID()
				+ "_" + property.getID() + "/" + node.getID() + "_"
				+ property.getID() ).isFile());

		underTest.execute(node, property);

		assertFalse(new File("test/parsing/" + node.getID() + "_"
				+ property.getID() ).isFile());

	}

	@After
	public void tearDown() {

		String fileID = node.getID() + "_" + property.getID();

		File file = new File("test/parsing/" + fileID);

		if (file.exists()) {
			file.delete();
			new File("test/parsing/").delete();
		} else {
		}

	}

}
