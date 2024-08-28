package com.ericsson.navigator.esm.model.pm.file.remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVMEnvironment;
import com.ericsson.navigator.esm.manager.pm.file.remote.ParserPluginController;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetCallback;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.CounterSetFileParser;
import com.ericsson.navigator.esm.manager.pm.file.remote.plugin.ParserException;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.file.remote.sftp.SFTPFileCounterSetList;
import com.ericsson.navigator.esm.util.file.DirectoryHouseKeeping;
import com.maverick.sftp.SftpFile;


@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class CounterSetFileFetcher implements Runnable {

	private static final String DEFAULT_COUNTERSET_PARSER = "com.ericsson.navigator.esm.manager.pm.file.remote.plugin.irpxml.IrpXMLCounterSetParser";
	
	private String enableHashCode = "";
	public boolean navFirstRun = true;
	private int port = -1;
	private String nameComponents;
	private int directDownload = -1;
	private final String hostname;
	private final String backupIP;
	private String nbName;
	private String nodeName;
	private String check;
	private String user = null;
	private Pattern fileNamePattern = null;
	private String remoteDirectory = null;
	private String remoteSubDirectory = null;
	private final Set<String> fetchedFiles;
	private String inputDirectory;
	
	private boolean northBoundSubDirectory;
	private String northBoundDirectoryDefault = MVMEnvironment.NB_DIRECTORY;
	private final boolean nbDirectory = MVMEnvironment.NAME_NBDIRECTORY;
	private String northBoundDirectory = null;
	private final boolean enableDownload = MVMEnvironment.ENABLEDOWNLOAD;
	
	private String corruptDirectory;
	private CounterSetCallback counterSetCallback = null;
	private final ParserPluginController parserPluginController;
	private String mainClass = null;
	private String comparator = null;
	private final CounterSetDefinitionsController counterSetDefinitionsController;
	private final SFTPFileCounterSetList sftpFileCounterSetList;
	private final RemoteFileCounterSetScheduling counterSetSchedule;
	private final FileTransferProtocolProvider provider;
	private String filePerDay = null;
	private List<String> filePerDayExt;
	private int maxFilePerROP;
	private boolean noNewFilesThisROP = false;
	private boolean soemLookUp = false;
	private String filePrefix = null;
	private final List<String> preInstallList = new ArrayList<String>();
	private boolean remoteFirstROP = false;
	private Date creationDate = null;
	
	private List<String> backupList = new ArrayList<String>();
	private String ipAddress = "";
	boolean failedConnection = false;
	
	private FileInputStream sourceStream = null;   
        private FileOutputStream targetStream = null;  
        private FileChannel sourceFileChannel = null;  
        private FileChannel targetFileChannel = null;
		
	private static final String classname = CounterSetFileFetcher.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	public CounterSetFileFetcher(final SFTPFileCounterSetList sftpFileCounterSetList,
	        final RemoteFileCounterSetScheduling counterSetSchedule,
	        final CounterSetDefinitionsController counterSetDefinitionsController, final String localDirectory,
	        final ParserPluginController pluginController, final FileTransferProtocolProvider provider){
		this.hostname = sftpFileCounterSetList.getAddressInformation().getAddress();
		this.backupIP = sftpFileCounterSetList.getAddressInformation().getBackupIp();
		this.check = sftpFileCounterSetList.getAddressInformation().getType();
		this.nbName = counterSetSchedule.getFdn();
		this.sftpFileCounterSetList = sftpFileCounterSetList;
		this.inputDirectory = new File(localDirectory, "input").getAbsolutePath();
		this.corruptDirectory = new File(localDirectory, "corrupted").getAbsolutePath();
		this.fetchedFiles = sftpFileCounterSetList.getFetchedFiles(counterSetSchedule.getFdn()
		        + counterSetSchedule.getPluginDirectory());
		this.counterSetSchedule = counterSetSchedule;
		this.parserPluginController = pluginController;
		this.counterSetDefinitionsController = counterSetDefinitionsController;
		this.provider = provider;
	}

	@Override
	public void run() {
		
		logger.debug(classname + ".run(); Fetching files from node: " + hostname + ", pluginDir= "+ counterSetSchedule.getPluginDirectory());
		try {				
				if(System.getProperty("MemUsageListener", "false").equalsIgnoreCase("false")){
					logger.warn(classname + ".run(); Mediator memory usage exceeded. " +
							"No files will be downloaded this ROP");
				}else{
					ipAddress = hostname;
					extractTransformLoad();
				}
				
				
		} finally {
			try {
				//sftpFileCounterSetList.parsefilePM(counterSetSchedule);
				provider.disconnect();
			} catch (final FileTransferException e) {
				logger.warn(classname + ".run(); Failed to close SSH connection towards node: " + hostname + ", "
				        + e.getMessage(), e);
			}
		}
	}
	
	private void startHouseKeeping(){
		if(soemLookUp){
			new DirectoryHouseKeeping(new File(MVMEnvironment.SOEMFAILEDDIR)).start();
		}
	}
	

	private void extractTransformLoad() {
		logger.debug(classname + " .extractTransformLoad(); -->");
		if (!retrievePluginProperties()) {
			return;
		}
		startHouseKeeping();
		resolveNodeName();
		final Thread currThread = Thread.currentThread();
		Threadhelper threadhelper = new Threadhelper(currThread); // HM54382 - ERICesm: PM collection threads hang when node does not respond to remote commands 
		try{
			provider.initialize();
			provider.connect(ipAddress, port, user);
			
			final List<File> downloadedFiles = downloadFiles();
			threadhelper.cancelTimerTask();
			threadhelper = null;
			
			if(this.directDownload==1){
				final CounterSetFileParser counterSetFileParser = loadParser();
				if (counterSetFileParser == null) {
					logger.error(classname + ".extractTransformLoad(); Failed to load plugin parser: " + mainClass);
					deleteFiles(downloadedFiles);
					return;
				}
				this.counterSetCallback = new DefaultCounterSetCallback(sftpFileCounterSetList, counterSetSchedule,
				        counterSetDefinitionsController, mainClass);
			
				logger.debug("Going to parse files!");
				parseFiles(downloadedFiles, counterSetFileParser);
			}
	
			
			logger.debug(classname + " .extractTransformLoad(); <--");
	   }  catch (final FileTransferException e) {
		   if(ipAddress.equals(hostname) && !failedConnection && !backupIP.equals("")){
			   threadhelper.cancelTimerTask();
			threadhelper = null;
			   logger.error(classname + ".downloadFiles(); Failed to fetch files from directory: "+remoteDirectory+" on the node:" + hostname + " because "+ e.getMessage());
			   logger.warn(classname + ".downloadFiles(); Attempting to fetch files from backup address: "+backupIP);
			   failedConnection = true;
			   ipAddress = backupIP;
			   if(backupList.size()>0){
				logger.debug(classname + ".downloadFiles(); Successfully downloaded files before error: " + backupList);
			   }
			   fetchedFiles.addAll(backupList);
			   extractTransformLoad();
		   }else{
			  //e.printStackTrace();
			   logger.error(classname + ".downloadFiles(); Failed to fetch files from directory: "+remoteDirectory+" on the node:" + hostname + " because "+ e.getMessage());
				logger.error(classname + ".downloadFiles(); Failed to fetch files from node: " + hostname + ""+remoteDirectory+"/" + e.getMessage());
				threadhelper.cancelTimerTask();
				threadhelper = null;
			}
		}
	}

	private boolean retrievePluginProperties() {
		
		remoteFirstROP = parserPluginController.checkList(counterSetSchedule.getPluginDirectory());
		
		final Properties pluginProps = parserPluginController.getPluginProperties(counterSetSchedule.getPluginDirectory());
		if (pluginProps == null) {
			logger.error(classname + ".retrievePluginProperties(); Failed to load plugin properties for plugin: "
			        + counterSetSchedule.getPluginDirectory());
			return false;
		}
		
		String soem = pluginProps.getProperty("enableSOEMLookup", "false");
		this.soemLookUp = Boolean.parseBoolean(soem);
		
		this.directDownload = Integer.parseInt(pluginProps.getProperty("directDownload", "0"));
		this.port = Integer.parseInt(pluginProps.getProperty("port", "22"));
		this.user = pluginProps.getProperty("user", "");
		if (user.equals("")) {
			logger.error(classname + ".retrievePluginProperties(); Failed to load plugin property: user, for plugin: "
			        + counterSetSchedule.getPluginDirectory());
			return false;
		}
		this.remoteDirectory = pluginProps.getProperty("remoteDirectory", "");
		if (remoteDirectory.equals("")) {
			logger.error(classname
			        + ".retrievePluginProperties(); Failed to load plugin property: remoteDirectory, for plugin: "
			        + counterSetSchedule.getPluginDirectory());
			return false;
		}
		  
		this.northBoundDirectory = pluginProps.getProperty("northBoundDirectory", northBoundDirectoryDefault);
		if (this.directDownload == 0) {
			inputDirectory = northBoundDirectory;
		}else if(soemLookUp){
			inputDirectory = MVMEnvironment.SOEMSORTDIR;
			corruptDirectory = MVMEnvironment.SOEMFAILEDDIR;
		}
		this.fileNamePattern = Pattern.compile(pluginProps.getProperty("fileNamePattern", ""));
		if (fileNamePattern.equals("")) {
			logger.error(classname
			        + ".retrievePluginProperties(); Failed to load plugin property: fileNamePattern, for plugin: "
			        + counterSetSchedule.getPluginDirectory());
			return false;
		}
		this.remoteSubDirectory = pluginProps.getProperty("remoteSubDirectory", "");
		if (remoteSubDirectory.equals("")) {
			logger.debug(classname + ".retrievePluginProperties(); remoteSubDirectory not specified, for plugin: "
			        + counterSetSchedule.getPluginDirectory());
			this.remoteSubDirectory = "none";
		}
		String subDirectory = pluginProps.getProperty("northBoundSubDirectory", "false");
		this.northBoundSubDirectory = Boolean.parseBoolean(subDirectory);

		// TO-DO :Define the thresholds for the MAX FILE TRANSFERS allowed.
		this.maxFilePerROP = Integer.parseInt(pluginProps.getProperty("maxFileTransfersPerRop", "24"));
		if (maxFilePerROP < 24) {
			logger.debug(classname
			        + ".retrievePluginProperties(); Number of files to be transferred for a given ROP is low : "
			        + " \nDefault Value of 24 will be picked for the plugin: "
			        + counterSetSchedule.getPluginDirectory());
			this.maxFilePerROP = 24;
		}
		
		String filePerDayExtensions = pluginProps.getProperty("filePerDayExt", null);
		if(filePerDayExtensions!=null){
			filePerDayExt = Arrays.asList(filePerDayExtensions.split(","));
		}else{
			filePerDayExt = new ArrayList<String>();
		}
		
		this.enableHashCode = pluginProps.getProperty("enableHashCode", "false");
		this.filePerDay = pluginProps.getProperty("filePerDay", "false");
		this.nameComponents = pluginProps.getProperty("nameComponents", "");
		this.filePrefix = pluginProps.getProperty("filePrefix", "none");
		this.mainClass = pluginProps.getProperty("mainClass", DEFAULT_COUNTERSET_PARSER);
		if (mainClass.equals("")) {
			this.mainClass = DEFAULT_COUNTERSET_PARSER;
		}
		
		this.comparator = pluginProps.getProperty("comparator", "");
		if (comparator.equals("")) {
			this.comparator = null;
		}
		
		return true;
	}

	private void deleteFiles(final List<File> downloadedFiles) {
		for (final File file : downloadedFiles) {
			deleteFile(file);
		}
	}

	private void deleteFile(final File file) {
		if (!file.delete()) {
			logger.error(classname + ".deleteFiles(); Failed to delete file: " + file.getAbsolutePath());
		}
	}
 
	
	private List<File> downloadFiles() throws FileTransferException {
		logger.debug(classname + " .downloadFiles()-->");
		if (verifySubDir()) {
			final List<File> downloadedFilesfromSubDir = new ArrayList<File>();
			final List<String> matchingFilesFromSubDir = new ArrayList<String>();
			final List<String> subDirectoryList = getSubDirList();
			logger.debug(classname + ".downloadFiles(); subDirectoryList.size = " +subDirectoryList.size());
			for (final String subDirName : subDirectoryList) {
				try {
					
					if (this.directDownload == 0) {
						inputDirectory = northBoundDirectory;
					}
					provider.cd(remoteDirectory);
					provider.cd(subDirName);
					final SftpFile[] allFiles = provider.ls();
					logger.debug(classname + ".downloadFiles(); Available files: " + Arrays.asList(allFiles));
					final List<String> matchingFiles = getMatchingFiles(allFiles);
					logger.debug(classname + ".downloadFiles(); Matching files: " + matchingFiles);
					final List<String> orderedFiles = orderFilesNames(matchingFiles);
					logger.debug(classname + ".downloadFiles(); Ordering files: " + orderedFiles);
					final List<String> newFiles = getNewFiles(orderedFiles);
					logger.debug(classname + ".downloadFiles(); Identified new files: " + newFiles);
					final List<File> downloadedFiles = downloadFiles(newFiles);
					logger.debug(classname + ".downloadFiles(); Successfully downloaded files: " + downloadedFiles);
					logger.info(classname + ".downloadFiles(); Successfully downloaded "+ downloadedFiles.size() +" files.");
					matchingFilesFromSubDir.addAll(backupList);
					downloadedFilesfromSubDir.addAll(downloadedFiles);
				}
				
				catch (final FileTransferException e) {
					e.printStackTrace();
					logger.error(classname + ".downloadFiles(); Failed to fetch files from node: " + hostname + "\n"+remoteDirectory+"/"+subDirName+"\n " + e.getMessage());
					continue;
				}
				catch (final Exception e) {
					e.printStackTrace();
					logger.error(classname + ".downloadFiles(); Failed to fetch files from node: " + hostname + "\n"+remoteDirectory+"/"+subDirName+"\n " + e.getMessage());
					continue;
				}
				
			}
			
			counterSetSchedule.setNavFirstRun(false);
			if (preInstallList.isEmpty()) {
			fetchedFiles.addAll(matchingFilesFromSubDir);
			}
			else {
				fetchedFiles.addAll(preInstallList);
			}
			logger.debug(classname + " .downloadFiles()<--");
			return downloadedFilesfromSubDir;
		}
			
		provider.cd(remoteDirectory);
		final SftpFile[] allFiles = provider.ls();
		logger.debug(classname + ".downloadFiles(); Available files: " + Arrays.asList(allFiles));
		final List<String> matchingFiles = getMatchingFiles(allFiles);
		logger.debug(classname + ".downloadFiles(); Matching files: " + matchingFiles);
		final List<String> orderedFiles = orderFilesNames(matchingFiles);
		logger.debug(classname + ".downloadFiles(); Ordering files: " + orderedFiles);
		final List<String> newFiles = getNewFiles(orderedFiles);
		logger.debug(classname + ".downloadFiles(); Identified new files: " + newFiles);
		final List<File> downloadedFiles = downloadFiles(newFiles);
		logger.info(classname + ".downloadFiles(); Successfully downloaded files: " + downloadedFiles);
		//logger.info(classname + ".downloadFiles(); Successfully downloaded "+ downloadedFiles.size() +" files.");
		addToFetchedFiles(matchingFiles, newFiles);
		logger.debug(classname + ".downloadFiles()<--");
	    return downloadedFiles;
	}

	/**
	 * @param matchingFiles
	 * @param newFiles
	 */
	private void addToFetchedFiles(final List<String> matchingFiles, final List<String> newFiles) {
		if (newFiles.size() >= maxFilePerROP) {
			fetchedFiles.addAll(backupList);
		} else if (noNewFilesThisROP) {
			fetchedFiles.clear();
			fetchedFiles.addAll(matchingFiles);
		} else if(preInstallList.isEmpty()){
			fetchedFiles.clear();
			fetchedFiles.addAll(backupList);
		}
		else {
			fetchedFiles.clear();
			fetchedFiles.addAll(preInstallList);
		}
	}

	/**
	 * @param matchingFiles
	 * @param newFiles
	 * @return
	 */

	private boolean verifySubDir() {
		if (remoteSubDirectory.equalsIgnoreCase("none")) {
			return false;
		}
		return true;
	}

	private List<String> getSubDirList() {
		final List<String> subDirs = new ArrayList<String>();
		final StringTokenizer st = new StringTokenizer(remoteSubDirectory, ",");
		while (st.hasMoreTokens()) {
			subDirs.add(st.nextToken());
		}
		return subDirs;
	}
	
	private Comparator loadComparator(String comparator){
		logger.debug(classname + ".loadComparator() -->");

		final File parserPluginDir = new File(MVMEnvironment.PARSERPLUGINDIR 
				+ File.separator + counterSetSchedule.getPluginDirectory());
		Comparator counterSetFileParser = null;
		try {
			final List<URL> urlClassPathList = new ArrayList<URL>();
			
			final File libDir = new File(parserPluginDir.getPath() + File.separator + "lib" + File.separator);
			if(libDir.isDirectory()) {
				for(final File jar : libDir.listFiles(new JarFilenameFilter())) { //NOPMD
					urlClassPathList.add(jar.toURI().toURL());
				}
			}
			
			final URL[] urlClassPath = urlClassPathList.toArray(new URL[0]);
			final URLClassLoader pluginClassLoader = 
				new URLClassLoader(urlClassPath, getClass().getClassLoader());
			final Class<? extends Comparator> parserClass = 
				Class.forName(comparator, true, pluginClassLoader).asSubclass(Comparator.class);
			counterSetFileParser = parserClass.newInstance();
			logger.debug(classname + ".loadComparator(); Loaded Plugin Comparator " 
					+ counterSetFileParser.getClass().getName());
		} catch (final ClassNotFoundException e) {
			logger.error(classname + ".loadComparator(); Class " 
					+ comparator +" not found in specified class path: " 
					+ parserPluginDir.getPath(), e);
		} catch (final InstantiationException e) {
			logger.error(classname + ".loadComparator(); Cannot instantiate class " 
					+ comparator , e);
		} catch (final IllegalAccessException e) {
			logger.error(classname + ".loadComparator(); " 
					+ "Accessing method does not have access to specified method of class " 
					+ comparator , e);
		} catch (final MalformedURLException e) {
			logger.error(classname + ".loadComparator(); " 
					+ "Could not create URL from specified directory URI " 
					+ parserPluginDir.getPath() + File.separator , e);
		}
		logger.debug(classname + ".loadComparator() <--");
		return counterSetFileParser;
	}
	
	class JarFilenameFilter implements FilenameFilter {
		 public boolean accept(final File dir, final String name) {
			 if(name.endsWith(".jar")) {
				 return true;
			 }
			 return false;
		 }
	}
	
	
	private List<String> orderFilesNames(final List<String> matchingFiles){
		
		boolean defaultSort = true;
		
		try{
			if(comparator != null){
				Comparator comp = loadComparator(comparator);
				if(comp != null){
					defaultSort = false;
					Collections.sort(matchingFiles, comp);
				}
			}
			
		}catch(Exception e){
			logger.error(classname + ".orderFilesNames(); Unable to order files with comparator: "+comparator+"\n"+ e.getMessage());
			defaultSort = true;
		}finally{
			if(defaultSort){
				Collections.sort(matchingFiles, Collections.reverseOrder());
			}
		}
		
		return matchingFiles;
	}
	

	private List<String> getMatchingFiles(final SftpFile[] allFiles) {
		final List<String> matchingFiles = new ArrayList<String>();
		for (final SftpFile file : allFiles) {
			if (fileNamePattern.matcher(file.getFilename()).matches()) {
				matchingFiles.add(file.getFilename());
			}
		}
		return matchingFiles;
	}

	private void parseFiles(List<File> downloadedFiles, final CounterSetFileParser counterSetFileParser) {
		if(soemLookUp){
			downloadedFiles =  Arrays.asList(new File(inputDirectory).listFiles());
		}
		for (final File file : downloadedFiles) {
			parseFile(file, counterSetFileParser);
		}
		logger.debug(classname+".parseFiles();Parse done");
		
	}

	private void parseFile(final File file,
			final CounterSetFileParser counterSetFileParser) {
		try {
			final long startTime = System.currentTimeMillis();
			logger.debug(classname + ".parseFile(); Starting to parse file "
					+ file.getPath() + " using parser: "
					+ counterSetFileParser.getClass().getName()
					+ ", for host: " + hostname);
			File newData = counterSetFileParser.parseFile(sftpFileCounterSetList.getFDN(),
					file.getAbsolutePath(), counterSetCallback, soemLookUp);
			logger.debug(classname + ".parseFile(); Parsing of file "
					+ file.getPath() + ", size: " + file.length()
					+ "(bytes), completed in: "
					+ (System.currentTimeMillis() - startTime)
					+ "(ms), for host: " + hostname);
			
			if(soemLookUp){
				this.inputDirectory = counterSetFileParser.getDirectory();
			}else{
				this.inputDirectory = northBoundDirectory;
			}

			if(newData != null){
				if(!moveFileToNorthBoundDirectory(newData)) {
					moveToCorruptDirectory(newData);
				}
			}
			
			if(checkFilePerDayExt(file.getPath())){ //TR-ERICesm: 24hr recovery for AIR/VS nodes, All Raw Data not processed by esm 
				createFile(file);	
				file.delete();
			}
			
			
		} catch (final ParserException e) {
			logger.error(classname + ".parseFile(); Parsing file "
					+ file.getPath() + " failed. "+ e.getMessage());
			moveToCorruptDirectory(file);
		} catch (IOException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a file say 10.20.12.246-251122434_FSC-AccountFinderClientIf_3.0_A_1-20110114010014.stat.pr under /nav/var/esm/pm/remote/input/ and write the System current Time into it.
	 * This gives the last parsed time of the file from a particular node.
	 *  
	 * @param file
	 * @return
	 */
	private void createFile(final File file) throws IOException 
	{
		final File topFile = new File(file.getPath()+".pr");
		BufferedWriter top = null;		
		try {
		topFile.createNewFile();		
		top = new BufferedWriter(new FileWriter(topFile));
			top.write(""+new Date(System.currentTimeMillis()));
		}catch(IOException e){
			logger.error(classname + ".createFile(); Failed to create file: " + file.getAbsolutePath());			
		}finally {
			if (top != null) {
				top.close();
			}
		}
		
	}
	private boolean moveFileToNorthBoundDirectory(final File file){
		logger.debug(classname + ".moveFileToNorthBoundDirectory(); Moving file " + file.getAbsolutePath()
		        + "to northbound directory: " + inputDirectory);
				
		
		createNorthBoundSubDirectory(inputDirectory);

		final File newFile = new File(inputDirectory, file.getName());
		if(newFile.exists()){
			if(!newFile.delete()) {
				logger.error(classname + ".moveFileToNorthBoundDirectory(); Failed to delete existing file: " + file.getAbsolutePath()
				        + " from north bound directory: " + inputDirectory);
				return false;
			}
		}
		
//		if (!file.renameTo( newFile)) {
//			logger.error(classname + ".moveFileToNorthBoundDirectory(); Failed to move file: " + file.getAbsolutePath()
//			        + " to north bound directory: " + northBoundDirectory);
//			return false;
//		}
		
		
		try{
			sourceStream = new FileInputStream(file.getPath());  
			targetStream = new FileOutputStream(newFile.getPath());  
	      
			sourceFileChannel = sourceStream.getChannel();  
			targetFileChannel = targetStream.getChannel();  
	      
			final long size = sourceFileChannel.size();  
			sourceFileChannel.transferTo(0, size, targetFileChannel); 
			
			file.delete();
		}catch(Exception e){
			logger.error(classname + ".moveFileToNorthBoundDirectory(); Failed to move file: " + file.getAbsolutePath()
			        + " to north bound directory: " + inputDirectory);
			return false;
		}

		
		return true;
}
	
	public void createNorthBoundSubDirectory(String path){
		File northbound;
		if(northBoundSubDirectory){
			if(!nbDirectory){
				northbound = new File(path , hostname);
			}else{
				northbound = new File(path , nodeName);
			}
		}else{
			northbound = new File(path);
		}

		if(!northbound.exists()){
			if(!northbound.mkdirs()){
				logger.error(classname+ ".moveFileToNorthBoundDirectory(); Failed to create north bound directory. Files will be moved" +
						" to default directory: " + northBoundDirectoryDefault);
				northbound = new File(northBoundDirectoryDefault);
			}
		}
		
		inputDirectory = northbound.getAbsolutePath();
	}

  

  private CounterSetFileParser loadParser() {
		final CounterSetFileParser counterSetFileParser = parserPluginController.getParserPluginInstance(
		        counterSetSchedule.getPluginDirectory(), mainClass);
		if (counterSetFileParser != null) {
			logger.debug(classname + ".loadParser(); Loaded Plugin Parser: " + counterSetFileParser.getDescription());
		}
		return counterSetFileParser;
	}

	private void moveToCorruptDirectory(final File file) {
		logger.debug(classname + ".moveToCorruptDirectory(); Moving corrupt file " + file.getAbsolutePath()
		        + "that failed to parse to directory: " + corruptDirectory);
		file.setLastModified(System.currentTimeMillis());
		if (!file.renameTo(new File(corruptDirectory, file.getName()))) {
			logger.error(classname + ".moveToCorruptDirectory(); Failed to move file: " + file.getAbsolutePath()
			        + " to corrupt directory: " + corruptDirectory);
		}
	}

	private List<File> downloadFiles(final List<String> files) throws FileTransferException {
		final List<File> localFiles = new ArrayList<File>();
		
		if(this.directDownload==0)
			createNorthBoundSubDirectory(inputDirectory);
		
		createDirectory(inputDirectory);
		provider.lcd(inputDirectory);
		
		String uniqueFileName=null;
   		try {
   		for(final String fileName : files){       
				creationDate = provider.creationTime(fileName);
				if(filePerDay.equalsIgnoreCase("true")){
					uniqueFileName = getPrefix(fileName, checkFilePerDayExt(fileName)).toString();
				}else{
					uniqueFileName = getPrefix(fileName, false).toString();
				}
				provider.get(fileName, uniqueFileName);
				localFiles.add(new File(inputDirectory, uniqueFileName)); // NOPMD
				backupList.add(fileName);	//JIRA PM-3 Store collected files incase connection is lost to node
		}
   		} catch (final FileTransferException e) {
   			File localFile = new File(inputDirectory,uniqueFileName);
   			if(localFile.exists()){
   				deleteFile(localFile);
   				logger.warn(classname + ".downloadFiles(); File "+localFile.toString()+" download was unsuccessful.Deleted from local directory");
   			}
			logger.warn(classname + ".downloadFiles(); SFTP file collection failure towards node: " + hostname, e);
		}finally{ //clearing the builder to avoid memory issues - HM36270

			if(uniqueFileName!=null){
				uniqueFileName=null;
			}
		}
		return localFiles;
	}
	
	
	/**
	 * Function resolves node name from fdn  string based on nbDirectory parameter
	 * 
	 * @param
	 * @return
	 */
	private void resolveNodeName(){
	  int start = nbName.indexOf(check+"=")+check.length()+1;
    //int stop = nbName.indexOf("," , start);
    int stop;
    if((stop=nbName.indexOf("," , start))==-1)
      stop=nbName.length();
    
    nodeName = nbName.substring(start,stop);
  
	}
	/**
	 * Gives the prefix for the input files which comprises of hostname and hashcode of FDN
	 *   
	 * @param 
	 * @param 
	 * @return
	 */

	private StringBuffer getPrefix(String fileName,boolean isFilePerDay)
	{
	
		StringBuffer prefix = new StringBuffer("");
		
		if(nameComponents.equals("") || nameComponents.equals("0")){
			prefix.append(fileName);
		}
		else{
			if(nameComponents.trim().equals("1")){
				nameComponents = "%N_%F_";
			}
			if(nameComponents.trim().equals("2")){
				nameComponents = "%N_%T_%F_";
			}
			if(nameComponents.trim().equals("3")){
				nameComponents = "%N_%T_%P_%F_";
			}
			
			prefix =getPrefixFromFormattedString(fileName, isFilePerDay, prefix);
		}
		
		return prefix;
	}
	
	private StringBuffer getPrefixFromFormattedString(String fileName,boolean isFilePerDay, StringBuffer prefix) {
		
		int index = -1;
		String[] prefixParts = nameComponents.split("%");
		int len = prefixParts.length;
		String ext = "";
		index = fileName.lastIndexOf('.');
		if(index != -1){
			ext = fileName.substring(index);
			fileName = fileName.substring(0,index);
		}
		
		index = -1;
		for(int i=1; i<len; i++){
			char element = prefixParts[i].charAt(0);
			
			if(element == 'f' || element == 'F'){
				prefix.append(fileName);
			} 
			else if (element == 'h' || element == 'H'){					//Test if Hashcode is added to fetched file
				String hashcode = counterSetSchedule.getFdn().hashCode()+"";
				if(hashcode.startsWith("-")){
					hashcode = hashcode.substring(1);
				}
				prefix.append(hashcode);
			}
			else if (element == 'p' || element == 'P'){			//Test if filePrefix is added to fetched file
				prefix.append(this.filePrefix);
			}
			else if (element == 'n' || element == 'N'){			//Test if node name/ipaddress is added to fetched file
				if(!nbDirectory){
					prefix.append(hostname);
			    }else{
			    	prefix.append(nodeName);
			    } 
			}
			else if (element == 't' || element == 'T'){			//Test if TimeStamp is added to fetched file
				if(!isFilePerDay){									//Only add TimeStamp is filePerDay is false
					FieldPosition fp = new FieldPosition(prefix.length()-1);
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmm");
					sdf.format(creationDate,prefix,fp);
				}else{
					//element T can not be added id filePerDay is true
					logger.error(classname+ ".getPrefix(); Argument in nameComponents setting: '"+ element+"' can not be added as filePerDay setting is "+ isFilePerDay);
					continue;
				}
			}
			else{
				//Character in nameComponents that is unknown. Not used to rename fetched files
				logger.error(classname+ ".getPrefix(); Unknown argument in nameComponents setting: '"+ element+"'. Attribute will not be added!!");
				continue;
			}
			
			try{
				//Add seperator to fetched file name
				char seperator = prefixParts[i].charAt(1);
				if(seperator == '_' || seperator == '-' || seperator == '.'){
					prefix.append(seperator);
				}else{
					throw new IndexOutOfBoundsException();
				}
				
			}catch(IndexOutOfBoundsException e){
				//No seperate was placed between elements
				//logger.error(classname+ ".getPrefix(); Invalid seperator character for element: '"+ element +"'. Default seperator '_' was entered!");
				prefix.append("_");
				
			}
			
		}
		
		int prefixLength = prefix.length();
		char lastChar = prefix.charAt(prefix.length()-1);
		if(lastChar == '_' || lastChar == '-' || lastChar == '.'){
			prefix.delete(prefixLength-1 ,prefixLength) ;
		}
		return prefix.append(ext);
	}
		
	
	private boolean createDirectory(final String path) {
		final File dir = new File(path);

		if (!dir.exists()) {
			return dir.mkdir();
		}
		return true;
	}

	/**
	 * New file means the files not present on the node in previous ROP. Any files with Data items appended after
	 * previous ROP.
	 * 
	 * @param files
	 * @return
	 */
	private List<String> getNewFiles(final List<String> files) {
		final List<String> newFiles = new ArrayList<String>();		
		
		final Calendar curDate = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		final String commonTimeStamp = sdf1.format(curDate.getTime());
		final String uncommonTimeStamp = sdf.format(curDate.getTime());
		
		int fileCount = 0;
		//if (counterSetSchedule.isNavFirstRun() ) {
		//	logger.debug(classname + ".getNewFiles(); checking new files to be downloaded: "
		//	        + "This is the first time the PM files are getting collected from this node: " + hostname);
		//}
		fileCount = determineNewFiles(files, newFiles, commonTimeStamp, uncommonTimeStamp, navFirstRun,
		        fileCount);
		if (newFiles.isEmpty() && filePerDay.equalsIgnoreCase("false")) {
			noNewFilesThisROP = true;
		}
		logger.debug(classname + ".getNewFiles(); returning in **Normal run **: " + newFiles.size() + "final-ite: "
		        + fileCount);
		return newFiles;
	}

	/**
	 * @param files
	 * @param newFiles
	 * @param appendedFiles
	 * @param commonTimeStamp
	 * @param uncommonTimeStamp
	 * @param navFirstRun
	 * @param fileCount
	 * @return
	 */
	private int determineNewFiles(final List<String> files, final List<String> newFiles, final String commonTimeStamp, final String uncommonTimeStamp,
	        final boolean navFirstRun, int fileCount){
		final boolean recoveryFlag = false;
			
		if ((counterSetSchedule.isNavFirstRun() && fetchedFiles.isEmpty() && !sftpFileCounterSetList.persistantStorageFileExists()) || remoteFirstROP) {
			logger.debug(classname + ".getNewFiles(); checking new files to be downloaded: "
			        + "This is the first time the PM files are getting collected from this node: " + hostname);
			
			preInstallList.addAll(files);
			logger.debug(classname + ".getNewFiles(); Mediator first run: No files will be fetched this ROP" );
			if (!verifySubDir()){
				counterSetSchedule.setNavFirstRun(false);
			}
			
		} else {
			Date lastCollectedTime = null;
			for (final String fileName : files) {
				
				if(filePerDay.equalsIgnoreCase("true") && checkFilePerDayExt(fileName)){
						try {
							lastCollectedTime = readLastParsedFile(getPrefix(fileName,true).toString(),lastCollectedTime);
							logger.debug(classname + ".getNewFiles(); lastCollectedTime:"+ lastCollectedTime);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					if (checkAppendedFiles(commonTimeStamp, uncommonTimeStamp, fileName,recoveryFlag,lastCollectedTime))  {
						newFiles.add(fileName);
						//fetchedFiles.add(fileName);
						logger.debug(classname + ".getNewFiles(); appendedFiles in **Normal run **: "+ newFiles.size() + "Filename: "+fileName);
					}
				}

				else if (!fetchedFiles.contains(fileName) && (fileCount < maxFilePerROP)) {
					newFiles.add(fileName);
					fileCount++;
					logger.debug(classname + ".getNewFiles(); newFiles in **Normal run **: " + newFiles.size());
				}
			}
		}
		
		if (counterSetSchedule.isNavFirstRun() && !verifySubDir()){
			counterSetSchedule.setNavFirstRun(false);
		}
		
		return fileCount;
	}

	/**
	 * Read File 10.20.12.246-251122434_FSC-AccountFinderClientIf_3.0_A_1-20110114010014.stat.pr under /nav/var/esm/pm/remote/input/.
	 * Get the last parsed time of the file .
	 *  
	 * @param file
	 * @return lastCollectedTime
	 */
	private Date readLastParsedFile(final String filePath,Date lastCollectedTime) throws IOException {

		final File topFile = new File(inputDirectory,filePath + ".pr");
		if (topFile.exists()) {
			final BufferedReader top = new BufferedReader(new FileReader(topFile));
			
			//Thu Feb 17 11:02:12 GMT 2011
			final SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"); //Fix for IllegalArgumentException
			final String strDate = top.readLine();
			try{
				lastCollectedTime = sdf2.parse(strDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		
			return lastCollectedTime;
		} else{
			logger.debug(classname + ".readLastParsedFile;File does not exist: " + topFile );
		}
		return null;
	}
	/**
	 * @param commonTimeStamp
	 * @param uncommonTimeStamp
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean checkAppendedFiles(final String commonTimeStamp, final String uncommonTimeStamp,
	        final String fileName,boolean recoveryFlag,final Date lastCollectedTime) {
				
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		final Calendar recoveryDate = Calendar.getInstance();
		recoveryDate.set(Calendar.HOUR_OF_DAY, recoveryDate.get(Calendar.HOUR_OF_DAY)-24); // get the date and time for yesterday.
		
		final Calendar currDate1 = Calendar.getInstance();
		final Date currDate = currDate1.getTime();
		
		final String commonTimeStamp_Recovery = sdf1.format(recoveryDate.getTime());
		final String uncommonTimeStamp_Recovery = sdf.format(recoveryDate.getTime());

		
		if(lastCollectedTime!=null && currDate.getDate()>lastCollectedTime.getDate()){ 
			recoveryFlag = true;
			logger.debug(classname + ".checkAppendedFiles();either 24HR recovery/last hour PM collection, recoveryFlag:"+recoveryFlag + "lastCollectedTime: " + lastCollectedTime);
		}		
		
		if (!(filePerDay.equalsIgnoreCase("false"))
		        && (fileName.contains(commonTimeStamp) || fileName.contains(uncommonTimeStamp))) {
			return true;
		}
		if(recoveryFlag && (fileName.contains(commonTimeStamp_Recovery) || fileName.contains(uncommonTimeStamp_Recovery)) ){ //Mainly for File per day node recovery scenario
			return true;
		}
		return false;
	}
	
	
	private boolean checkFilePerDayExt(String fileName){		
		if(!filePerDayExt.isEmpty() && !(filePerDay.equalsIgnoreCase("false"))){
			for(String extension : filePerDayExt){
				if(fileName.contains(extension))
					return true;
			}
		}
		return false;
	}
	
	
}
