package com.ericsson.eniq.techpacksdk.java.test.hsql.common;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

public class TestHSQLDatabaseConn extends CreateHSQLDatabase{
	
	@Before
	public void setUp(){
		CreateHSQLDatabase.createStatsVer(false);
	}
	
	@Test
	public void test() throws RockException, SQLException {
		final RockFactory dwhrep = getRockFactory(Schema.dwhrep);
	    final Versioning where = new Versioning(dwhrep);
	    final List<Versioning> versions = new VersioningFactory(dwhrep, where).get();
	    for (Versioning version : versions) {
	      System.out.println(version.getVersionid());
	    }
	}
}
