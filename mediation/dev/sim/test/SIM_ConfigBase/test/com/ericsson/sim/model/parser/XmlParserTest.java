package com.ericsson.sim.model.parser;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlParserTest {

	private Document document;
	private XmlParser parser;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize()
			.toString();
	String fileName = "file.xml";
	XMLOutputFactory xof = XMLOutputFactory.newInstance();
	XMLStreamWriter xtw = null;

	@Before
	public void setUp() {
		parser = new XmlParser();
		try {
			xtw = xof.createXMLStreamWriter(new FileOutputStream(fileName),
					"UTF-8");
			xtw.writeStartElement("root");
			xtw.writeStartElement("element");
			xtw.writeAttribute("id", "1");
			xtw.writeStartElement("node");
			xtw.writeCharacters("value");
			xtw.writeEndElement();
			xtw.writeStartElement("node");
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
		parser.loadFile(FILE_PATH + "/file.xml");
	}

	@After
	public void tearDown() throws Exception {
		parser.setDocument(null);
		Files.deleteIfExists(Paths.get((FILE_PATH + "/file.xml"))
				.toAbsolutePath());
	}

	@Test
	public void testloadFile() {
		parser.loadFile(FILE_PATH + "/noFileHere.xml");

		parser.loadFile(FILE_PATH + "/file.xml");

	}

	@Test
	public void testgetNodeValue() {
		assertEquals(parser.getNodeValue("node", 0), "value");
		assertNotSame(parser.getNodeValue("node", 0), "anotherValue");
	}

	@Test
	public void testgetAttribute() {
		assertEquals(parser.getAttribute("element", 0, "id"), "1");
		assertNotSame(parser.getAttribute("element", 0, "id"), "otherValue");
	}

	@Test
	public void testhasElement() {
		assertTrue(parser.hasElement("element"));
		assertFalse(parser.hasElement("otherElement"));
	}

	@Test
	public void testgetTagListLength() {
		assertEquals(parser.getTagListLength("node"), 2);
	}

	@Test
	public void testnodeList() {
		NodeList nList = parser.nodeList("node");
		assertEquals(nList.item(0).getTextContent(),
				parser.getNodeValue("node", 0));
	}

	@Test
	public void testsetDocument() {
		parser.setDocument(document);
		assertEquals(parser.getDocument(), document);
	}

	@Test
	public void testgetDocument() {
		assertNotNull(parser.getDocument());
		parser.setDocument(null);
		assertNull(parser.getDocument());
	}

}
