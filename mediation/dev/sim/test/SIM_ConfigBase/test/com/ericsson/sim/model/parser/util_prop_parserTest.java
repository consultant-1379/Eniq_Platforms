package com.ericsson.sim.model.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class util_prop_parserTest {

	private util_prop_parser parser;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize()
			.toString();
	String fileName = "ufile.xml";
	XMLOutputFactory xof = XMLOutputFactory.newInstance();
	XMLStreamWriter xtw = null;

	@Before
	public void setUp() throws Exception {
		parser = new util_prop_parser();
		try {
			xtw = xof.createXMLStreamWriter(new FileOutputStream(fileName),
					"UTF-8");
			xtw.writeStartElement("root");
			xtw.writeStartElement("element");
			xtw.writeAttribute("id", "1");
			xtw.writeStartElement("node");
			xtw.writeAttribute("name", "name");
			xtw.writeAttribute("attribute", "attribute");
			xtw.writeCharacters("value");
			xtw.writeEndElement();
			xtw.writeStartElement("node");
			xtw.writeAttribute("name", "anotherName");
			xtw.writeAttribute("attribute", "anotherAttribute");
			xtw.writeCharacters("anotherValue");
			xtw.writeEndElement();
			xtw.writeEndElement();
			xtw.writeEndDocument();
			xtw.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.gc();
			// without this, throws FileSystemException
			// Cannot be accessed as it's being used by another process
		}
		parser.loadFile(FILE_PATH + "/ufile.xml");
	}

	@After
	public void tearDown() throws Exception {
		Files.deleteIfExists(Paths.get((FILE_PATH + "/ufile.xml"))
				.toAbsolutePath());
	}

	@Test
	public void testloadFile() {

		try {
			parser.loadFile(FILE_PATH + "/noFileHere.xml");
		} catch (Exception e) {
		}
		assertFalse(new File(FILE_PATH + "/noFileHere.xml").exists());
		try {
			parser.loadFile(FILE_PATH + "/ufile.xml");
		} catch (Exception e) {
		}
		assertTrue(new File(FILE_PATH + "/ufile.xml").exists());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = null;
		try {
			document = builder.parse(new FileInputStream(new File(FILE_PATH
					+ "/ufile.xml")));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		assertEquals(document.getChildNodes().getLength(), parser.document
				.getChildNodes().getLength());
		assertEquals(document.getChildNodes().item(2), parser.document
				.getChildNodes().item(2));
		assertEquals(document.getChildNodes().toString(), parser.document
				.getChildNodes().toString());

	}

	@Test
	public void testgetContent() {
		assertEquals(2, parser.getContent("node"));
		assertEquals(0, parser.getContent("anotherNode"));
	}

	@Test
	public void testgetAttribute() {
		assertEquals("value",
				parser.getAttribute("node", 0, "attribute", "attribute"));
		assertEquals("", parser.getAttribute("node", 0, "name", "not here"));
	}

}
