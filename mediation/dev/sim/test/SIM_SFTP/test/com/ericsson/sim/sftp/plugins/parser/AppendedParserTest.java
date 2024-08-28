package com.ericsson.sim.sftp.plugins.parser;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.sim.constants.SIMEnvironment;
import com.ericsson.sim.exception.SIMException;
import com.ericsson.sim.model.node.Node;
import com.ericsson.sim.model.protocol.sftp.SFTPproperties;

public class AppendedParserTest {
	private AppendedParser classUnderTest;
	private SFTPproperties properties;
	private Node node;
	private File file;
	private final String FILE_PATH = Paths.get(".").toAbsolutePath()
			.normalize().toString()
			+ "/test/";

	@Before
	public void setUp() throws Exception {
		SIMEnvironment.CONFIGPATH = FILE_PATH;

		classUnderTest = new AppendedParser();
		node = new Node();
		properties = new SFTPproperties();
		file = new File(SIMEnvironment.CONFIGPATH + "air.csv");

		properties.addProperty("dateFormatInFile", "yyyy-MM-dd-HHmm");
		properties.addProperty("delimitor", ",");
		properties.addProperty("dateFormatInFileName", "yyyy-MM-dd-HHmm");
	}

	@After
	public void tearDown() throws Exception {

		if (new File(SIMEnvironment.CONFIGPATH + "air.csv").exists()) {
			System.out.println("exists");
			new File(SIMEnvironment.CONFIGPATH + "air.csv").delete();
		}

		SIMEnvironment.CONFIGPATH = System.getProperty("ConfigPath",
				"/eniq/sw/conf/sim");

	}

	@Test
	public void testNotExist() {
		try {
			classUnderTest.parseFile(node, properties, file);
		} catch (SIMException e) {
			System.out.println("exception in AppendedParserTests");
		}

	}

	@Test
	public void testDoesExist() {

		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(
					SIMEnvironment.CONFIGPATH + "air.csv")));
			br.write("DATE/TIME,Request,Response,Air:GetFaFList_3.0:In,Air:UpdateFaFList_3.0:In,Air:GetAllowedServiceClasses_3.0:In");
			br.newLine();
			br.write("2015-07-16-0000,9,9,9,9,9");
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			classUnderTest.parseFile(node, properties, file);
		} catch (SIMException e) {
			System.out.println("exception in AppendedParserTesta");
		}

	}

}
