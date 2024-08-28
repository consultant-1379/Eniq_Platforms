package com.ericsson.navigator.esm.util.file;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
/**
 * This will listen to directories and if any of the directories change
 * (create/update/delete) it will notify the listener. The notification 
 * contains the directory that is changed.
 */
public class DirectoryMonitor {

	private final Timer timer_;
	private final Map<File, Long> directories_;
	private final List<WeakReference<DirectoryListener>> listeners_;
	private static final String classname = DirectoryMonitor.class.getName();
	private static final Logger logger = Logger.getLogger(classname);

	/**
	 * Creates a directory monitor instance with specified polling interval.
	 * 
	 * @param pollingInterval
	 *            Polling interval in milli seconds.
	 */
	public DirectoryMonitor(final long pollingInterval) {

		directories_ = Collections.synchronizedMap(new HashMap<File, Long>());
		listeners_ = Collections.synchronizedList(new LinkedList<WeakReference<DirectoryListener>>());

		timer_ = new Timer(true);
		timer_.schedule(new DirectoryMonitorNotifier(), 0, pollingInterval);
	}

	/** Stops the directory monitoring */
	public void stop() {
		timer_.cancel();
	}

	/** Adds directory to listen for */
	public void addDirectory(final File directory) {
		if (directory.isDirectory()) {
			directories_.put(directory, new Long(directory.lastModified()));
		}
	}
	
	public boolean containsDirectory(final File directory){
		return directories_.containsKey(directory);
	}

	/** Removes directory to listen for */
	public void removeDirectory(final File directory) {
		directories_.remove(directory);	
	}

	/** Adds listener to this directory monitor */
	public void addListener(final DirectoryListener directoryListener) {
		for (final WeakReference<DirectoryListener> ref : listeners_) {
			final DirectoryListener listener = ref.get();
			
			if (listener == directoryListener) {
				return;
			}
		}
		
		// Use WeakReference to avoid memory leak if this becomes the
		// sole reference to the object.
		listeners_.add(new WeakReference<DirectoryListener>(directoryListener));
	}

	/** Removes listener from this file monitor */
	public void removeListener(final DirectoryListener directoryListener) {
		for (final Iterator<WeakReference<DirectoryListener>> i = listeners_.iterator(); i
				.hasNext();) {
			final WeakReference<DirectoryListener> reference = i.next();
			final DirectoryListener listener = reference.get();
			if (listener == directoryListener) {
				i.remove();
				break;
			}
		}
	}

	/**
	 * This is the timer thread which is executed every n milliseconds according
	 * to the setting of the file monitor. It investigates the file in question
	 * and notify listeners if changed.
	 */
	private class DirectoryMonitorNotifier extends TimerTask {
		Iterator<File> i = null;
		File dir = null;
		@Override
		public void run() {

			synchronized(directories_){

				Set<File> dir1 = directories_.keySet();
				i = dir1.iterator();
				while (i.hasNext()) {
					dir = i.next();
					if (dir.exists()) {
						
					final Long lastModified = directories_.get(dir);
					long latestLastModified = 0;

						if (dir.lastModified() > lastModified) { // file created
																	// or
																	// removed?
							latestLastModified = dir.lastModified();
						}
					
					for (File file : dir.listFiles()) {

						final long fileLastModified = file.lastModified();
						if (fileLastModified > lastModified &&          // any file updates?
								fileLastModified > latestLastModified) { 
								latestLastModified = fileLastModified;
						}
					}

					if (latestLastModified > 0) {
							logger.debug(" latestLastModified > 0 dir=" + dir);
						directories_.put(dir, latestLastModified);		
						sendNotification(dir);
						dir1 = directories_.keySet();
						i = dir1.iterator();

					}
						
					} else {
						logger.debug("dir " + dir + " does not exist");
						try {
							i.remove();
						} catch (Exception e) {
							logger.debug(" Unable to remove " + dir
									+ " from the stored directory list "
									+ e.getMessage());
						}
					}
				}
			}

		}

		private synchronized void sendNotification(final File dir) {
			
			for (final WeakReference<DirectoryListener> ref : listeners_) {

				final DirectoryListener listener = ref.get();
				if(listener != null) {
					listener.directoryChanged(dir);
				}
			}
			
		}
	}
}
