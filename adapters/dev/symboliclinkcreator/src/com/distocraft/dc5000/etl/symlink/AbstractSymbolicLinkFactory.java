package com.distocraft.dc5000.etl.symlink;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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

	/**
	 * Absolute location in the server where Symbolic Links for CellTrace files
	 * has to be created
	 */
	private String symbolicLinkParentFolder;

	private String segmentOnEniq;

	/**
	 * Subclasses can implement this to do any specific initialization they want
	 * to do
	 */
	abstract void finalizeConfiguration();
	
	/**
	 * Method to create required sub directories under the target mount point
	 * This is done only once during the factory initialisation.
	 * 
	 * @param dirName
	 *            - NeType directory name
	 * @param subDirNames
	 *            - List of sub directory names to be created under dirName
	 */
	void createDirectories(final String dirName, final List<String> subDirNames) {
		for (final String subDirName : subDirNames) {
			final String targetSubDirName = symbolicLinkParentFolder + dirName
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

	public void createSymbolicLink(final String nodeType, final String fileName) throws SymbolicLinkCreationFailedException {
			
		System.out.println("createSymbolicLink method is initiated");

		if (isFactoryEnabled()) {
			
			final SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration = getSymbolicLinkSubDirConfiguration(nodeType);
			final String linkNodeTypeDir = symbolicLinkSubDirConfiguration.getNodeTypeDir();
			
			System.out.println( "subdirectories for the node   :  "+ nodeType + "\n"+symbolicLinkSubDirConfiguration.getSubDirs());

			final String linkSubDir = getLinkSubDir(symbolicLinkSubDirConfiguration, nodeType);
			
			final EniqSymbolicLink eniqSymbolicLink = new EniqSymbolicLink(
					symbolicLinkParentFolder, linkNodeTypeDir, linkSubDir,
					segmentOnEniq, fileName);
			
			writeSymbolicLink(eniqSymbolicLink);
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
			String nodeName) throws SymbolicLinkCreationFailedException;

	/**
	 * This method is used to return the appropriate subDir structure for nodes
	 * whose symlinks are deployment specific. The subDir structure returned
	 * would be on the basis of the deployment under which the node is
	 * configured. e.g : RadioNode/MSRBS_V2 with eNodeB function: symlinks under
	 * RadioNode/LRAT/dirX RadioNode/MSRBS_V2 with NodeB function: symlinks
	 * under RadioNode/WRAT/dirX
	 * 
	 * @param symbolicLinkSubDirConfiguration
	 * @param nodeName
	 * @param depSpecificSubDirList
	 * @return
	 * @throws SymbolicLinkCreationFailedException
	 */
	abstract String getLinkSubDir(
			final SymbolicLinkSubDirConfiguration symbolicLinkSubDirConfiguration,
			final String nodeName, final List<String> depSpecificSubDirList,
			String depName) throws SymbolicLinkCreationFailedException;

	/**
	 * @return - The map where NeType is the key and corresponding
	 * @SymbolicLinkSubDirConfiguration is the value
	 */
	protected Map<String, SymbolicLinkSubDirConfiguration> getSymbolicLinkConfiguration() {
		return DirectoryConfigurationFileParser.getInstance()
				.getSymbolicLinkSubDirConfigurations();
	}

	/**
	 * @return the folder underneath which the symbolic links will be written
	 */
	final String getSymbolicLinkParentFolder() {
		return symbolicLinkParentFolder;
	}

	/**
	 * @param nodeType
	 * 
	 * @return @SymbolicLinkSubDirConfiguration for the passed nodeType from
	 *         <b>symbolicLinkSubDirConfigurations</b>
	 */
	abstract SymbolicLinkSubDirConfiguration getSymbolicLinkSubDirConfiguration(final String nodeType);

	/**
	 * Method to be called first before calling any other methods in this
	 * factory. Almost like a constructor, but required to silence PMD.
	 * 
	 * Populates the @SymbolicLinkSubDirConfiguration for all NeType from
	 * 
	 * @SymLinkDataSAXParser. Creates the sub directories as required.
	 */
	public void initialiseFactory() {
		isFactoryEnabled = isSubDirConfigurationAvailable() && getOSSAlias();
		if (isFactoryEnabled) {
			finalizeConfiguration();
			createRequiredSubDirectories();
		}
	}

	/**
	 * Loads the map symbolicLinkSubDirConfigurations
	 */
	private void initialiseSubDirectoryConfiguration() {
		symbolicLinkSubDirConfigurations = getSymbolicLinkConfiguration();
	}

	boolean getOSSAlias() {
		
		System.out.println("calling ossAlias method");
		final String mountInfoPath = "/eniq/sw/conf/.oss_ref_name_file";
        String line=null;
        String[] info = null ;
        String eniqOSSAlias = null ;
        
		try {
			Process p = Runtime.getRuntime().exec("cat " + mountInfoPath);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
            while((line=input.readLine()) != null) {
                info = line.split("\\s+");
                                
            }
            for ( int i = 0; i < info.length ; i++ ){
            	if( info[i].contains("eniq") ){
                	eniqOSSAlias = info[i];
                }
            }
			
            System.out.println("eniqOSSAlias   ::: "  + eniqOSSAlias);
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		symbolicLinkParentFolder = "/eniq/data/pmdata/" + eniqOSSAlias + File.separator;
		System.out.println("symbolicLinkParentFolder  :::  "  + symbolicLinkParentFolder);
		segmentOnEniq = "/eniq/data/importdata/"+ eniqOSSAlias + File.separator;
		System.out.println("segmentOnEniq  :::  "  + segmentOnEniq);
		
		return true;
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
			// The xml is not present of blank
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
		} catch (Exception em) {
			System.out.println("makeDirectories exception ::: " +  em);
			em.printStackTrace();
		}
	}

	/**
	 * Calling @SymbolicLinkWriter to write the symbolic link to disk.
	 * 
	 * @param eniqSymbolicLink
	 */
	protected void writeSymbolicLink(final EniqSymbolicLink eniqSymbolicLink) {
		symbolicLinkWriter = new SymbolicLinkWriter(eniqSymbolicLink);
		symbolicLinkWriter.createSymbolicLink();
	}


}