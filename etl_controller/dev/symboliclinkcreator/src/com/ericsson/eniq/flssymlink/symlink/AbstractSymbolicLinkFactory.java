package com.ericsson.eniq.flssymlink.symlink;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ericsson.eniq.flssymlink.StaticProperties;

public abstract class AbstractSymbolicLinkFactory {

	/**
	 * Map<NeType, @SymbolicLinkSubDirConfiguration> Holds the @SymbolicLinkSubDirConfiguration
	 * for each NeType fetched from the @SymLinkDataSAXParser
	 */
	static Map<String, SymbolicLinkSubDirConfiguration> symbolicLinkSubDirConfigurations;
	
	/**
	 * The @SymbolicLinkWriter used for actual symbolic link creation
	 */
	private SymbolicLinkWriter symbolicLinkWriter = null;

	/**
	 * Specific to this factory and assigned a value only once
	 */
	public boolean isFactoryEnabled;
	
	Logger log;

	private String eniqPMDataDirectory;

	private String eniqImportDataDirectory;

	/**
	 * Subclasses can implement this to do any specific initialization they want
	 * to do
	 */
	abstract void finalizeConfiguration();
	
	/**
	 * Method to create required sub directories under the target mount point
	 * This is done only once during the factory initialization.
	 * 
	 * @param dirName
	 *            - NeType directory name
	 * @param subDirNames
	 *            - List of sub directory names to be created under dirName
	 */
	void createDirectories(final String dirName, final List<String> subDirNames) {
		for (final String subDirName : subDirNames) {
			final String targetSubDirName = eniqPMDataDirectory + dirName
					+ subDirName;
			makeDirectory(targetSubDirName);
		}
	}

	/**
	 * This method is called once during the factory initialization. It goes
	 * through all the @SymbolicLinkSubDirConfiguration and creates the sub
	 * directories as required.
	 */
	abstract void createRequiredSubDirectories();

	public void createSymbolicLink(final String nodeType, final String fileName) {
		try{
		log.fine("createSymbolicLink method is initiated");

		if (isFactoryEnabled()) {
			
			final SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration = getSymbolicLinkSubDirConfiguration(nodeType, log);
			if(symbolicLinkSubDirConfiguration!=null){
			final String linkNodeTypeDir = symbolicLinkSubDirConfiguration.getNodeTypeDir();
			
			final String linkSubDir = getLinkSubDir(symbolicLinkSubDirConfiguration, nodeType);
			
			final EniqSymbolicLink eniqSymbolicLink = new EniqSymbolicLink(
					eniqPMDataDirectory, linkNodeTypeDir, linkSubDir,
					eniqImportDataDirectory, fileName, log);
			
			writeSymbolicLink(eniqSymbolicLink);
			}
			else{
				log.warning("indir not found for nodeType: "+nodeType);
			}
		}
		}
		catch(Exception e){
			log.warning("Exception at createSymbolicLink method "+e.getMessage());
		}
	}


	/**
	 * This method returns the appropriate sub directory for creating the
	 * symbolic link. If the current directory has reached its maximum capacity,
	 * it checks for the next directories until it finds one. It throws
	 * 
	 * @SymbolicLinkCreationFailedException if all sub directories are full.
	 * 
	 * @param symbolicLinkSubDirConfiguration
	 *            for the NeType
	 * @param nodeName
	 *            for which Symbolic Link has to be created
	 * 
	 * @return a sub directory that can be used for symbolic link creation
	 * 
	 * @throws SymbolicLinkCreationFailedException
	 *             if all sub directories are full
	 */
	abstract String getLinkSubDir(
			SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration,
			String nodeName);

	/**
	 * @return - The map where NeType is the key and corresponding
	 * @SymbolicLinkSubDirConfiguration is the value
	 */
	protected Map<String, SymbolicLinkSubDirConfiguration> getSymbolicLinkConfiguration() {
		return DirectoryConfigurationFileParser.getInstance(log)
				.getSymbolicLinkSubDirConfigurations();
	}

	/**
	 * @return the folder underneath which the symbolic links will be written
	 */
	final String getSymbolicLinkParentFolder() {
		return eniqPMDataDirectory;
	}

	/**
	 * @param nodeType
	 * 
	 * @return @SymbolicLinkSubDirConfiguration for the passed nodeType from
	 *         <b>symbolicLinkSubDirConfigurations</b>
	 */
	abstract SymbolicLinkSubDirConfiguration getSymbolicLinkSubDirConfiguration(final String nodeType, Logger log);

	/**
	 * Method to be called first before calling any other methods in this
	 * factory. Almost like a constructor, but required to silence PMD.
	 * 
	 * Populates the @SymbolicLinkSubDirConfiguration for all NeType from
	 * 
	 * @SymLinkDataSAXParser. Creates the sub directories as required.
	 */
	public void initialiseFactory(Logger log) {
		this.log = log;
		isFactoryEnabled = isSubDirConfigurationAvailable() && getOSSAlias();
		if (isFactoryEnabled) {
			finalizeConfiguration();
			createRequiredSubDirectories();
		}
		else{
			log.info("Failed to Initialize!!");
		}
	}

	/**
	 * Loads the map symbolicLinkSubDirConfigurations
	 */
	private void initialiseSubDirectoryConfiguration() {
		symbolicLinkSubDirConfigurations = getSymbolicLinkConfiguration();
	}

	boolean getOSSAlias() {
		String eniqOSSAlias = null ;
		boolean isFLSEnabled = false;
		try{
			eniqPMDataDirectory = StaticProperties.getProperty("PMDATA_PATH", "/eniq/data/pmdata" );
			if (!eniqPMDataDirectory.endsWith(File.separator)) {
				eniqPMDataDirectory += File.separator;
			}
			
			eniqImportDataDirectory = StaticProperties.getProperty("IMPORTDATA_PATH", "/eniq/data/importdata" );
			if (!eniqImportDataDirectory.endsWith(File.separator)) {
				eniqImportDataDirectory += File.separator;
			}
			
			Process pFLS = Runtime.getRuntime().exec("cat " + StaticProperties.getProperty("FLS_CONF_PATH", "/eniq/installation/config/fls_conf") );
			BufferedReader inputFLS = new BufferedReader(new InputStreamReader(pFLS.getInputStream()));
			
			String line;
			String[]  fls_elabled_enm=null;
			while ((line = inputFLS.readLine()) != null) {
				 fls_elabled_enm= line.split("\\s+");
			}
			 
			if(fls_elabled_enm.length>0){
				eniqOSSAlias = fls_elabled_enm[0];
				isFLSEnabled = true;
				
				eniqPMDataDirectory = eniqPMDataDirectory + eniqOSSAlias + File.separator;
				eniqImportDataDirectory = eniqImportDataDirectory + eniqOSSAlias + File.separator;
				
				log.info("symbolicLinkParentFolder  :::  "  + eniqPMDataDirectory);
				log.info("segmentOnEniq  :::  "  + eniqImportDataDirectory);
			
			}else{
				log.info("FLS mode is not configured in the server");
			}
		}catch (Exception e) {
			log.info("Exception occured while retrieving the ENIQ alias name from "
					+ "the file :: /eniq/installation/config/fls_conf  \n " + e.getMessage());
			e.printStackTrace();
		}		
		return isFLSEnabled;
	}

	/**
	 * @return true if the factory is enabled
	 */
	boolean isFactoryEnabled() {
		return isFactoryEnabled;
	}

	/**
	 * @return true if the eniq.xml file is existing
	 */
	boolean isSubDirConfigurationAvailable() {
		initialiseSubDirectoryConfiguration();
		if (symbolicLinkSubDirConfigurations.isEmpty()) {
			// The xml is not present or blank
			log.info("eniq.xml file is either not present or blank");
			return false;
		}
		// the map is not empty so the values are present
		return true;
	}

	/**
	 * Creates the directory named by dirName, including any necessary but
	 * nonexistent parent directories.
	 * 
	 * @param dirName
	 *            - Absolute path of the directory to be created
	 */
	private void makeDirectory(final String dirName) {
		try {
			final File result = new File(dirName);
			if (!result.exists()) {
				result.mkdirs();
			}
		} catch (Exception e) {
			log.warning("makeDirectories exception ::: " +  e.getMessage());
			
		}
	}

	/**
	 * Calling @SymbolicLinkWriter to write the symbolic link to disk.
	 * 
	 * @param eniqSymbolicLink
	 */
	protected void writeSymbolicLink(final EniqSymbolicLink eniqSymbolicLink) {
		symbolicLinkWriter = new SymbolicLinkWriter(eniqSymbolicLink, log);
		symbolicLinkWriter.createSymbolicLink();
	}


}