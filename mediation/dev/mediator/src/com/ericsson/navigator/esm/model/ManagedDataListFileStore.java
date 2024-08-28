package com.ericsson.navigator.esm.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;

public class ManagedDataListFileStore implements Component {
	
	private static final String classname = ManagedDataListFileStore.class.getName(); 
	private static Logger logger = Logger.getLogger(classname);
	private final File file;
	private final File backupFile;
	private final Timer SAVETIMER;
	private final long ALARMLISTSAVEDELAY;
	private final ManagedModel<?> managedProtocolModel;
	
	public ManagedDataListFileStore(final ManagedModel<?> managedProtocolModel, final File file, final File backup, final long alarmListSaveDelay){
		this.file = file;
		this.backupFile = backup;
		SAVETIMER = new Timer();
		ALARMLISTSAVEDELAY = alarmListSaveDelay;
		this.managedProtocolModel = managedProtocolModel;
	}
	
	public void initialize() throws ComponentInitializationException {
		SAVETIMER.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					save();
				} catch (IOException e) {
					logger.error("Error occurred during scheduled alarm list save.", e);
				}
			}
		}, ALARMLISTSAVEDELAY, ALARMLISTSAVEDELAY);
		try {
			load();
		} catch (FileNotFoundException e ) {
			logger.error(classname+".initialize(); No active alarm list to load.");
		} catch (IOException e) {
			throw new ComponentInitializationException(
					"IO Error occurred when loading persistant data.", e, false);
		} catch (ClassNotFoundException e) {
			throw new ComponentInitializationException("Internal error occurred.", e, false);
		}
	}
	
	public void shutdown() throws ComponentShutdownException {
		SAVETIMER.cancel();
		try {
			save();
		} catch (IOException e) {
			throw new ComponentShutdownException(
					"Failed to save persistent data list at shutdown.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveData() throws IOException {
		final FileOutputStream fos = new FileOutputStream(file);
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		final Map<ManagedDataType, Map<String, List<ManagedData>>> typeMap = 
			new Hashtable<ManagedDataType, Map<String, List<ManagedData>>>();
		final List<ManagedData> listToSave = new Vector<ManagedData>();
		final ManagedDataListener<?> listener = new ManagedDataListener() {
			public void pushDataEvent(final ManagedDataEvent event) {
				save(event, listToSave);
			}
		};
		for(ManagedDataType type : ManagedDataType.values()){
			final Map<String, List<ManagedData>> dataLists = new Hashtable<String, List<ManagedData>>(); //NOPMD
			for (final ManagedDataList list : managedProtocolModel.getManagedDataLists(type)) {
				listToSave.clear();
				list.addManagedDataListener(listener, true);
				list.removeManagedDataListener(listener);
				dataLists.put(list.getFDN(), new Vector<ManagedData>(listToSave));//NOPMD
			}
			typeMap.put(type, dataLists);
		}
		oos.writeObject(typeMap);
		oos.flush();
		oos.close();
	}
	
	public synchronized void save() throws IOException {
		if (file.exists()) {
			backup();
			if (!file.delete()) {
				logger.error(classname+".saveActiveList(); Failed to delete alarm list file: "
						+ file.getAbsolutePath());
				return;
			}
		}
		if (!file.createNewFile()) {
			logger.error(classname+"saveActiveList(); Failed to create alarm list file: "
					+ file.getAbsolutePath());
			return;
		}
		saveData();
	}

	private void backup() throws IOException {
		if (backupFile.exists() && !backupFile.delete()) {
			logger.error(classname+".backup(); Failed to delete alarm list backup file: "
					+ backupFile.getAbsolutePath());
			return;
		}
		final FileInputStream in = new FileInputStream(file);
		final FileOutputStream out = new FileOutputStream(backupFile);
		final byte[] buffer = new byte[4024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) >= 0) {
			out.write(buffer, 0, bytesRead);
		}
		out.flush();
		out.close();
		in.close();
	}

	/**
	 * Save alarm if received from a managed system that does not support
	 * synchronize. Also save all Events or if the alarm is acknowledged to keep
	 * the acknowledge information.
	 * 
	 * @param event
	 *            The alarm event
	 * @param list
	 *            List to add alarm to if alarm is to be saved.
	 */
	@SuppressWarnings("unchecked")
	private void save(final ManagedDataEvent<?> event, final List<ManagedData> list) {
		if (event.getAction().equals(ManagedDataEvent.Action.INITIAL)) {
			if (event.getData().shallPersist()) {
				list.add(event.getData());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load() throws IOException, ClassNotFoundException{
		final FileInputStream fis = new FileInputStream(file);
		final ObjectInputStream ois = new ObjectInputStream(fis);
		final Map<ManagedDataType, Map<String, List<ManagedData>>> typeMap = 
			(Map<ManagedDataType, Map<String, List<ManagedData>>>) ois.readObject();
		for(ManagedDataType type : ManagedDataType.values()){
			final Map<String, List<ManagedData>> typeLists = typeMap.get(type);
			for (final ManagedDataList list : managedProtocolModel.getManagedDataLists(type)) {
				final List<ManagedData> dataList = typeLists.get(list.getFDN());
				for(ManagedData data : dataList){
					data.reset();
					list.correlate(data, false);
				}
			}
		}
		fis.close();
	}

	@Override
	public String getComponentName() {
		return ManagedDataListFileStore.class.getSimpleName();
	}
}
