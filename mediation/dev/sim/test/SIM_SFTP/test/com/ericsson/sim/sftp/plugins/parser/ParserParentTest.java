package com.ericsson.sim.sftp.plugins.parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;
import com.ericsson.sim.sftp.serialization.appended.FileProperties;

@RunWith(JMock.class)
public class ParserParentTest {
	private ParserParent classUnderTest;
	private SFTPproperties props;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";

	private Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final FileProperties properties = context
			.mock(FileProperties.class);
	private final BufferedReader br = context.mock(BufferedReader.class);
	final Sequence seqOne = context.sequence("seqOne");
	final Sequence seqTwo = context.sequence("seqTwo");
	final Sequence seqThree = context.sequence("seqThree");

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.CONFIGPATH = FILE_PATH;

		classUnderTest = new ParserParent();
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(
					SIMEnvironment.CONFIGPATH + "parserParentTest.csv")));
			br.write("DATE/TIME,Request,Response,Air:GetFaFList_3.0:In,Air:UpdateFaFList_3.0:In,Air:GetAllowedServiceClasses_3.0:In");
			br.newLine();
			br.write("2015-07-16-0000,9,9,9,9,9");
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {

		new File(SIMEnvironment.CONFIGPATH + "parserParentTest.csv").delete();

		new File(SIMEnvironment.CONFIGPATH
				+ "destinationMove/parserParentTest.csv").delete();
		new File(SIMEnvironment.CONFIGPATH + "destinationMove/").delete();

		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");
	}

	@Test
	public void testIsValidFile() {
		assertTrue(classUnderTest.isValidFile(new File(
				SIMEnvironment.CONFIGPATH + "parserParentTest.csv")));
		assertFalse(classUnderTest.isValidFile(new File(
				SIMEnvironment.CONFIGPATH + "notThere")));
	}

	@Test
	public void testIsHeader() {
		assertFalse(classUnderTest.isHeader("2015-07-16-0000,9,9,9,9,9",
				"yyyy-MM-dd-HHmm", ","));
		assertTrue(classUnderTest.isHeader(
				"DATE/TIME,Request,Response,Air:GetFaFList_3.0:In",
				"yyyy-MM-dd-HHmm", ","));
	}

	// @Test
	// public void testGetReader() {
	// BufferedReader br;
	// try {
	// br = classUnderTest.getReader(new File(SIMEnvironment.CONFIGPATH
	// + "parserParentTest.csv"));
	// assertEquals(
	// "DATE/TIME,Request,Response,Air:GetFaFList_3.0:In,Air:UpdateFaFList_3.0:In,Air:GetAllowedServiceClasses_3.0:In",
	// br.readLine());
	// br.close();
	// } catch (IOException | SIMException e) {
	// System.out.println("ParserParentTest" + e.getMessage());
	// }
	// }

	@Test
	public void testFindNewData() {

		try {
			context.checking(new Expectations() {
				{

					oneOf(properties).getLastParsedLine();
					will(returnValue(""));
					inSequence(seqOne);
					oneOf(br).readLine();
					will(returnValue("2015-07-16-0000,9,9,9,9,9"));
					inSequence(seqOne);
					oneOf(br).readLine();
					will(returnValue(null));
					inSequence(seqOne);
					oneOf(br).close();
					inSequence(seqOne);

					oneOf(properties).getLastParsedLine();
					will(returnValue(""));
					inSequence(seqTwo);
					oneOf(br).readLine();
					will(returnValue("2015-07-16-0000,9,9,9,9,9"));
					inSequence(seqTwo);
					oneOf(br).readLine();
					will(returnValue(null));
					inSequence(seqTwo);
					oneOf(br).close();
					inSequence(seqTwo);

					oneOf(properties).getLastParsedLine();
					will(returnValue("NotLastLine"));
					inSequence(seqThree);
					oneOf(br).readLine();
					will(returnValue("NotLastLine"));
					inSequence(seqThree);
					oneOf(br).readLine();
					will(returnValue("NewData"));
					inSequence(seqThree);
					oneOf(br).readLine();
					will(returnValue(null));
					inSequence(seqThree);
					oneOf(br).close();

				}
			});
		} catch (IOException e) {
			System.out.println("ParserParetnFindNewData" + e.getMessage());
		}

		try {
			assertEquals(1, classUnderTest.findNewData(br, properties).size());
			br.close();
			assertEquals("2015-07-16-0000,9,9,9,9,9", classUnderTest
					.findNewData(br, properties).get(0));
			br.close();
			assertEquals("NewData", classUnderTest.findNewData(br, properties)
					.get(0));
			br.close();

		} catch (SIMException | IOException e) {
			System.out.println("ParserParetnFindNewData" + e.getMessage());
		}

	}

	@Test
	public void testGetMaximumColumnCount() {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("one,two,three");
		classUnderTest.data = lines;
		classUnderTest.header = "one,two,three";
		assertEquals(3, classUnderTest.getMaximumColumnCount(","));
		assertNotSame(4, classUnderTest.getMaximumColumnCount(","));

		classUnderTest.header = null;
		assertEquals(3, classUnderTest.getMaximumColumnCount(","));
		assertNotSame(4, classUnderTest.getMaximumColumnCount(","));
	}

	@Test
	public void testWriteNewFile() {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("one,two,three");
		classUnderTest.data=lines;
		classUnderTest.header = "headerOne, headerTwo, headerThree";
		int columnCount = 3;
		BufferedReader bufferedReader = null;

		try {
			classUnderTest.writeNewFile(new File(SIMEnvironment.CONFIGPATH
					+ "parserParentTest.csv"), ",", columnCount);
		} catch (SIMException e) {
			System.out.println("ParserParent" + e.getMessage());
		}

		try {
			bufferedReader = new BufferedReader(new FileReader(new File(
					SIMEnvironment.CONFIGPATH + "parserParentTest.csv")));
		} catch (FileNotFoundException e) {
			System.out.println("ParserParentTest" + e.getMessage());
		}

		try {
			assertEquals("headerOne, headerTwo, headerThree", bufferedReader.readLine());
			bufferedReader.close();
		} catch (IOException e) {
			System.out.println("ParserParentTest" + e.getMessage());
		}

	}

	@Test
	public void testMoveFile() throws SIMException, IOException {
		props = new SFTPproperties();
		props.addProperty("DestinationDir", SIMEnvironment.CONFIGPATH
				+ "destinationMove/");

		Files.createDirectories(Paths.get(SIMEnvironment.CONFIGPATH
				+ "destinationMove/"));
		File file = new File(SIMEnvironment.CONFIGPATH + "parserParentTest.csv");

		classUnderTest.moveFile(props, file);

		assertTrue(new File(SIMEnvironment.CONFIGPATH
				+ "destinationMove/parserParentTest.csv").exists());
	}

}
