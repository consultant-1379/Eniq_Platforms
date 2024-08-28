package com.ericsson.navigator.esm.model.pm.file.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.manager.pm.file.remote.ParserPluginController;
import com.ericsson.navigator.esm.manager.pm.file.remote.RemoteFileFetchExecutor;
import com.ericsson.navigator.esm.model.AddressInformation;
import com.ericsson.navigator.esm.model.alarm.ProtocolAlarmList;
import com.ericsson.navigator.esm.model.conversion.ConversionController;
import com.ericsson.navigator.esm.model.pm.AbstractProtocolCounterSetList;
import com.ericsson.navigator.esm.model.pm.CounterSetDefinitionsController;
import com.ericsson.navigator.esm.model.pm.file.remote.sftp.SFTPFileCounterSetList;
import com.ericsson.navigator.esm.util.SupervisionException;

public abstract class RemoteFileCounterSetList extends AbstractProtocolCounterSetList<AddressInformation, RemoteFileCounterSetScheduling> {
	
	private final RemoteFileFetchExecutor executor;
	protected final ParserPluginController pluginController;
	private final File persistantStorageFile;
	protected final String localDirectory;
	private Map<String, Set<String>> fetchedFiles = null;
	private static final String classname = SFTPFileCounterSetList.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	private boolean serExists = false;
	
	public RemoteFileCounterSetList(final String fdn, final List<RemoteFileCounterSetScheduling> counterSetSchedulings,
			final AddressInformation addressInformation,
			final CounterSetDefinitionsController counterSetDefinitionsController,
			final ConversionController conversionController,
			final ProtocolAlarmList<?> alarmList,
			final RemoteFileFetchExecutor executor,
			final ParserPluginController pluginController,
			final String localDirectory,
			final String persistantStoragePath) {
		super(fdn, counterSetSchedulings, addressInformation, null, alarmList,
				counterSetDefinitionsController, conversionController);
		this.executor = executor;
		this.pluginController = pluginController;
		this.localDirectory = localDirectory;
		this.persistantStorageFile = new File(persistantStoragePath, getAddressInformation().getAddress() + "." + 
				fdn.hashCode()+".ser");
	}

	@Override
	protected void fetchCounterSet (final RemoteFileCounterSetScheduling counterSetSchedule) throws SupervisionException {
		if(!counterSetSchedule.isNavFirstRun()){
			removeFetchedFileLists();
			writeFetchedFileLists();
		}
		executor.execute(createCounterSetRequest(counterSetSchedule));
		
	}
	
	protected abstract Runnable createCounterSetRequest(
			final RemoteFileCounterSetScheduling counterSetSchedule) throws SupervisionException;
	
	public Set<String> getFetchedFiles(final String fdnPlusPluginDir){
		Set<String> files = fetchedFiles.get(fdnPlusPluginDir);
		serExists = true;
		if(files == null){
			files = new HashSet<String>();
			fetchedFiles.put(fdnPlusPluginDir, files);
			serExists = false;
		}
		return files;
	}
	
	@Override
	public void startSupervision() throws SupervisionException {
		readFetchedFileLists();
		super.startSupervision();
	}

	@SuppressWarnings("unchecked")
	private void readFetchedFileLists() throws SupervisionException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(persistantStorageFile)));
			fetchedFiles = (Map<String, Set<String>>) in.readObject();
		} catch (FileNotFoundException e) {
			fetchedFiles = new HashMap<String, Set<String>>();
		} catch (IOException e) {
			throw new SupervisionException(
					"IO error occured when loading previously fetched file list: " 
					+ persistantStorageFile.getAbsolutePath(), e);
		} catch (ClassNotFoundException e) {
			throw new SupervisionException(
					"Internal build error occured when loading previously fetched file list: "
					+ persistantStorageFile.getAbsolutePath(), e);
		} finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					logger.warn(classname + ".readFetchedFileLists(); IO error occured when closing " +
							"sftp fetched list file: " + persistantStorageFile.getAbsolutePath(), e);
				}
			}
		}
	}

	private void writeFetchedFileLists() throws SupervisionException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(persistantStorageFile)));
			out.writeObject(fetchedFiles);
		} catch (IOException e) {
			throw new SupervisionException(
					"IO error occured when loading previously fetched file list: " 
					+ persistantStorageFile.getAbsolutePath(), e);
		} finally {
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					logger.warn(classname + ".writeFetchedFileLists(); IO error occured when closing " +
							"sftp fetched list file: " + persistantStorageFile.getAbsolutePath(), e);
				}
			}
		}
	}

	@Override
	public void stopSupervision(final boolean isRemoved) throws SupervisionException {
		if(isRemoved){
			removeFetchedFileLists();
		} else {
			writeFetchedFileLists();
		}
		super.stopSupervision(isRemoved);
	}

	private void removeFetchedFileLists() {
		if(persistantStorageFile.exists() && !persistantStorageFile.delete()){
			logger.warn(classname + ".stopSupervision(); Failed to delete no longer used " +
					"sftp fetched list file: " + persistantStorageFile.getAbsolutePath());
		}
	}
	
	
	public boolean persistantStorageFileExists(){
		return serExists;	
	}
	
	
	
}