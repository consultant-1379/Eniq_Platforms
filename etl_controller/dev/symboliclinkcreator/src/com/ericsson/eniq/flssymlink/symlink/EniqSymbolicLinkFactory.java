package com.ericsson.eniq.flssymlink.symlink;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class EniqSymbolicLinkFactory extends AbstractSymbolicLinkFactory {

    /**
     * Map which stores
     * @SymbolicLinkSubDirConfiguration data for all NeTypes parsed from eniq.xml
     * using
     * @SymLinkDataSAXParser.
     */
    protected static Map<String, SymbolicLinkSubDirConfiguration> statsSymbolicLinkSubDirConfigurations = new HashMap<String, SymbolicLinkSubDirConfiguration>();
	/**
	* All supported NeTypes by this factory Mapping between the nodeType
	* representation by
	* 
	* @MONodeType and String representation in eniq.xml
	*/
	protected static final Map<String, Short> configurationNameToNodeType = new HashMap<String, Short>();

	/**
	* max number of links per directory rule If this rule is set, the factory
	* will go to next sub-directory only after the current directory reached
	* its maximum capacity.
	*/
	public static final int MAX_PER_DIRECTORY = 0;

	/**
	* max number of links per iteration rule, which is default
	*/
	public static final int MAX_PER_ITERATION = 1;

	/**
	* Holder for rule selected by the user
	*/
	protected int symLinkDirectoryRule = MAX_PER_ITERATION;

	/**
	* Map<NeType, SubDirStatus> Which stores
	* 
	* @SubDirStatus for each NeType.
	* @SubDirStatus represents the number of links created and currently using
	*               sub directory for each NeType
	*/
	private final Map<String, SubDirStatus> subDirStatusPerNodeType = new HashMap<String, SubDirStatus>();


   
    @Override
    void finalizeConfiguration() {

    	statsSymbolicLinkSubDirConfigurations = symbolicLinkSubDirConfigurations;
        symLinkDirectoryRule = Integer.parseInt("1");
    }

    /**
     * Called only once during
     *  initialization, so no performance issues
     */

    @Override
    void createRequiredSubDirectories() {
        for (final SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration : statsSymbolicLinkSubDirConfigurations
                .values()) {
            createDirectories(symbolicLinkSubDirConfiguration.getNodeTypeDir(),
                    symbolicLinkSubDirConfiguration.getSubDirs());
        }
    }

	@Override
	SymbolicLinkSubDirConfiguration getSymbolicLinkSubDirConfiguration(final String nodeType, Logger log) {
		this.log = log;
		return statsSymbolicLinkSubDirConfigurations.get(nodeType);
	}

	@Override
	String getLinkSubDir(
		final SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration,
		final String nodeName){
		try{
	final SubDirStatus subDirStatus = getSubDirStatus(symbolicLinkSubDirConfiguration
			.getName());
	final String result;

	/*
	 * Lock the status until we have counted and moved folder if required.
	 * Other Threads simply have to wait for this but it should be quick
	 * Many threads can be creating symbolic links hence the need for
	 * Synchronization
	 */
	synchronized (subDirStatus) {
		subDirStatus.numberOfLinks++; // incrementing for this symbolic
		// link creation iteration (in
		// advance)
		if (subDirStatus.numberOfLinks > symbolicLinkSubDirConfiguration
				.getMaxNumLinks()) {
			log.info("Reached max no links for directory number "+ subDirStatus.subDirNumber);
			subDirStatus.findNextAvailableSubDirectory(symbolicLinkSubDirConfiguration);
		}
		// subDirStatus is now updated with the sub dir number to be used
		// for this iteration

		result = symbolicLinkSubDirConfiguration.getSubDirs().get(subDirStatus.subDirNumber);
		}
	return result;
		}
		catch(Exception e){
			log.warning("Exception at getLinkSubDir method "+e.getMessage());
		}
		return null;
	}


	/**
	* @param name
	* @return current
	* @SubDirStatus for a given NeType
	*/
	private SubDirStatus getSubDirStatus(final String name) {
	SubDirStatus currentSubDirStatus = subDirStatusPerNodeType.get(name);
	if (currentSubDirStatus == null) {
		currentSubDirStatus = getSubDirStatus(log);
		subDirStatusPerNodeType.put(name, currentSubDirStatus);
	}
	return currentSubDirStatus;
	}

	/**
	* @return a new
	* @SubDirStatus object
	*/
	protected SubDirStatus getSubDirStatus(Logger log) {
	return new SubDirStatus(this, 0, 0,log);
	}

}
