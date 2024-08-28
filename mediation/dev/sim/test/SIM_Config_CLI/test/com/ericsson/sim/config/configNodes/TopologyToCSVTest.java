package com.ericsson.sim.config.configNodes;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.model.parser.CsvParser;

public class TopologyToCSVTest {

	private XMLOutputFactory xof = XMLOutputFactory.newInstance();
	private XMLStreamWriter xtw = null;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString();
	private CsvParser parser;

	@Before
	public void setUp() throws Exception {

		// SIMEnvironment.CONFIGPATH = FILE_PATH + "/test";
		// createXML();
		// TopologyToCSV.main(new String[] { FILE_PATH + "/test/CSV.txt" });

		SIMEnvironment.CONFIGPATH = "test";
		createXML();
		TopologyToCSV.main(new String[] { "test/CSV.txt" });
	}

	@After
	public void tearDown() throws Exception {
		// Files.deleteIfExists(Paths.get((FILE_PATH + "/test/topology.xml"))
		// .toAbsolutePath());
		// Files.deleteIfExists(Paths.get((FILE_PATH + "/test/CSV.txt"))
		// .toAbsolutePath());
		// SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
		// "/eniq/sw/conf/sim");

		Files.deleteIfExists(Paths.get(("test/topology.xml")).toAbsolutePath());
		Files.deleteIfExists(Paths.get(("test/CSV.txt")).toAbsolutePath());
		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");
	}

	@Test
	public void testExport() {
		parser = new CsvParser();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		parser.loadFile("test/CSV.txt");

		String[] header = parser.getDocumentHeader();

		assertEquals(6, header.length);
		assertEquals("name", header[0]);
		assertEquals("pluginNames", header[5]);

		String[] nextLine = parser.getNextLine();
		assertEquals(6, nextLine.length);
		assertEquals("IPAddress", nextLine[1]);
		assertEquals("sftpUserName", nextLine[4]);

		parser.closeFileParser();

	}

	@Test(expected = NullPointerException.class)
	public void testExportFail() {
		TopologyToCSV.main(new String[] { "test" });

		parser.loadFile("CSV.txt");
	}

	public void createXML() {
		try {
			xtw = xof.createXMLStreamWriter(new FileOutputStream(
					"test/topology.xml"), "UTF-8");
			xtw.writeStartElement("simconfig");

			xtw.writeStartElement("Node");
			xtw.writeAttribute("name", "nodeName");
			xtw.writeAttribute("IPAddress", "IPAddress");
			xtw.writeAttribute("uniqueID", "uniqueID");
			xtw.writeStartElement("sftpPort");
			xtw.writeCharacters("22");
			xtw.writeEndElement();
			xtw.writeStartElement("sftpUserName");
			xtw.writeCharacters("sftpUserName");
			xtw.writeEndElement();
			xtw.writeStartElement("Protocols");
			xtw.writeCharacters("Protocols");
			xtw.writeEndElement();

			xtw.writeEndElement();

			xtw.writeEndElement();

			xtw.writeEndDocument();
			xtw.close();
			xof = null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.gc();
			// without this, throws FileSystemException
			// Cannot be accessed as it's being used by another process
		}

	}

}
