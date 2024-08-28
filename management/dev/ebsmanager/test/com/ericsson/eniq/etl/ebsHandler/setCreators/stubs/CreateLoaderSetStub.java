package com.ericsson.eniq.etl.ebsHandler.setCreators.stubs;

import com.ericsson.eniq.common.setWizards.SetCreator;
import junit.framework.Assert;

import org.apache.velocity.VelocityContext;

import ssc.rockfactory.RockFactory;

import com.ericsson.eniq.teststubs.AbstractTestStub;

/**
 * This is stub class that imitates CreateLoaderSet without its dependencies
 * to the rest of the system, by replacing the methods that create the 
 * dependencies, with empty implementations.
 * 
 * With lesser dependencies to its environment, CreateLoaderSetStub is used
 * by EBSUpdateActionTestSubclass to perform unit testing on EBSUpdateAction
 * class.
 * 
 * @author epiituo
 */
public class CreateLoaderSetStub extends AbstractTestStub implements SetCreator {
	
	public CreateLoaderSetStub(final String templateDir,final String name, final String version,
							  final  String versionid, final RockFactory dwhrepRock,
							   final RockFactory rock, final String techPackName) throws Exception {

		// Assert that none of the reference arguments are null
		Assert.assertNotNull(templateDir);
		Assert.assertNotNull(name);
		Assert.assertNotNull(version);
		Assert.assertNotNull(versionid);
		Assert.assertNotNull(dwhrepRock);
		Assert.assertNotNull(rock);
		Assert.assertNotNull(techPackName);
	}
	
	@Override
	public void create(final boolean skip)  {
		this.addMethodCall("create("+ skip +")");
	}

	@Override
	public String merge(final String templateName, final VelocityContext context) {
		this.addMethodCall("merge(" + templateName + "," + context + ")");
		return null;
	}

	@Override
	public void removeSets() {
		this.addMethodCall("removeSets()");
	}

}
