package com.distocraft.dc5000.dwhm;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite( "Test for com.distocraft.dc5000.dwhm" );
		
		suite.addTestSuite( VersionUpdateActionTest.class );
		
		return suite;
	}
}
