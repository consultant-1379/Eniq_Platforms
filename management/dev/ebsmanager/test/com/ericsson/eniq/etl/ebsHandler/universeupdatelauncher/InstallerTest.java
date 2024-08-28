package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A JUnit test class for the class ExecutableInstaller
 * 
 * @author epiituo
 *
 */
public class InstallerTest {

	/*
	 * An argument, defined in the testInstallScript.txt file.
	 */
	private static final String SCRIPT_ARGUMENT = "scriptArgument";
	
	private Installer sut;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		this.sut = new Installer();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testInstall() throws IOException {
		// Define the file names. These are also defined in the install script,
		// and therefore cannot be modified without also modifying the script 
		// file.
		String executableFileName = "H:\\temp\\updateLauncherTest_executable.bat"; 
		String installScriptFileName = "testInstallScript.txt";

		// Create added arguments for the batch file. Install script already 
		// contains one argument, and these will be added after it.
		Arguments args = new Arguments();
		String firstAddedArgument = "firstAddedArgument";
		String secondAddedArgument = "secondAddedArgument";
		args.addArgument(executableFileName, firstAddedArgument);
		args.addArgument(executableFileName, secondAddedArgument);
		
		// Run the install script, and store the output into a string.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.sut.setOutputStream(baos);
		this.sut.executeInstallScript(installScriptFileName, args);
		String output = baos.toString();
		
		// Print the installer's output
		System.out.println(output);
		
		// Assert that the file was successfully installed
		// Clean up
		File batchFile = new File(executableFileName);
		Assert.assertTrue(batchFile.exists());
		
		// Find the line printed by the installed batch file, that contains the
		// arguments given to the file. The line should begin with the substring
		// "BATCH FILE ARGUMENTS:"
		Vector<String> lines = getLines(output);
		int lineNumber = -1;
		for(int i=0; i<lines.size(); ++i) {
			String line = lines.elementAt(i).trim();
			if (line.startsWith("BATCH FILE ARGUMENTS:")) {
				lineNumber = i;
			}
		}
		Assert.assertTrue(lineNumber != -1); // Assert that a matching line was
											 // found.
		
		// Assert that the arguments printed by the batch file are equal to the
		// ones given as parameter in this test class.
		String line = lines.elementAt(lineNumber);
		StringTokenizer outputLineTokenizer = new StringTokenizer(line);
		
		while (!outputLineTokenizer.nextToken().equals("ARGUMENTS:")); 		// Skip the non-argument tokens
		
		Assert.assertEquals(SCRIPT_ARGUMENT, outputLineTokenizer.nextToken());
		Assert.assertEquals(firstAddedArgument, outputLineTokenizer.nextToken());
		Assert.assertEquals(secondAddedArgument, outputLineTokenizer.nextToken());
		
		// Clean up
		batchFile.delete();
	}
	
	private Vector<String> getLines(String text) {
		Vector<String> result = new Vector<String>();
		StringBuffer buffer = new StringBuffer(text);
		
		int prevSubstringEndIndex = 0;
		char character;
		for(int i=0; i<buffer.length(); ++i) {
			character = buffer.charAt(i);
			if (character == '\n') {
				String substring = buffer.substring(prevSubstringEndIndex, i);
				result.add(substring);
				prevSubstringEndIndex = i;
			}
		}
		String lastSubstring = buffer.substring(prevSubstringEndIndex);
		result.add(lastSubstring);
		
		return result;
	}

}
