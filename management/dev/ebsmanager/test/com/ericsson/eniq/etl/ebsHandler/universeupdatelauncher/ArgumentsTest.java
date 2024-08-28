package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArgumentsTest {

	private Arguments sut;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.sut = new Arguments();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddArguments() {
		// Add arguments to the arguments object
		String firstExecutableName = "C:\\temp\\exec.exe"; // Does not need to exist
		String firstArgument = "execute properly";
		this.sut.addArgument(firstExecutableName, firstArgument);
		
		String secondArgument = "execute poorly";
		this.sut.addArgument(firstExecutableName, secondArgument);

		String secondExecutableName = "C:\\directory\\file.exe"; // Does not need to exist
		String thirdArgument = "do not execute";
		this.sut.addArgument(secondExecutableName, thirdArgument);
		
		// Assert that the arguments object now contains the added arguments
		String[] argumentsForFirstExecutable = this.sut.getArgumentsFor(firstExecutableName);
		Assert.assertEquals(2, argumentsForFirstExecutable.length);
		Assert.assertEquals(firstArgument, argumentsForFirstExecutable[0]);
		Assert.assertEquals(secondArgument, argumentsForFirstExecutable[1]);
		
		String[] argumentsForSecondExecutable = this.sut.getArgumentsFor(secondExecutableName);
		Assert.assertEquals(1, argumentsForSecondExecutable.length);
		Assert.assertEquals(thirdArgument, argumentsForSecondExecutable[0]);
		
		// Assert that arguments are found for executable name, also when the 
		// name is in upper of lower case.
		argumentsForFirstExecutable = null;
		argumentsForFirstExecutable = this.sut.getArgumentsFor(firstExecutableName.toLowerCase());
		Assert.assertEquals(2, argumentsForFirstExecutable.length);
		Assert.assertEquals(firstArgument, argumentsForFirstExecutable[0]);
		Assert.assertEquals(secondArgument, argumentsForFirstExecutable[1]);
		
		argumentsForSecondExecutable = null;
		argumentsForSecondExecutable = this.sut.getArgumentsFor(secondExecutableName.toUpperCase());
		Assert.assertEquals(1, argumentsForSecondExecutable.length);
		Assert.assertEquals(thirdArgument, argumentsForSecondExecutable[0]);
	}
	

	@Test
	public void testClearAddedArguments() {
		// Add arguments to the arguments object
		String executableName = "C:\\temp\\exec.exe"; // Does not need to exist
		String firstArgument = "execute properly";
		this.sut.addArgument(executableName, firstArgument);
		
		String secondArgument = "execute poorly";
		this.sut.addArgument(executableName, secondArgument);
		
		// Assert that the object now contains two arguments for the executable
		Assert.assertEquals(2, this.sut.getArgumentsFor(executableName).length);
		
		// Clear the arguments for some other executable name
		this.sut.clearAddedArguments("C:\\temporary folder\\executable.com");
		
		// Assert that the object still contains the arguments for the first
		// executable name.
		Assert.assertEquals(2, this.sut.getArgumentsFor(executableName).length);
		
		// Clear the arguments for the first executable name
		this.sut.clearAddedArguments(executableName);
		
		// Assert that the object no longer contains arguments for the 
		// executable name
		Assert.assertNull(this.sut.getArgumentsFor(executableName));
	}

}
