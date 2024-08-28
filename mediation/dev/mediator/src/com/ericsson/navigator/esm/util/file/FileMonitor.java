package com.ericsson.navigator.esm.util.file;

import java.util.*;
import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Class for monitoring changes in disk files.
 */
public class FileMonitor {
	private final Timer timer_;
	private final Map<File, Long> files_;
	private final Collection<WeakReference<FileListener>> listeners_;

	/**
	 * Create a file monitor instance with specified polling interval.
	 * 
	 * @param pollingInterval
	 *            Polling interval in milli seconds.
	 */
	public FileMonitor(final long pollingInterval) {
		files_ = Collections.synchronizedMap(new HashMap<File, Long>());
		listeners_ = Collections.synchronizedCollection(new ArrayList<WeakReference<FileListener>>());

		timer_ = new Timer(true);
		timer_.schedule(new FileMonitorNotifier(), 0, pollingInterval);
	}

	/**
	 * Stop the file monitor polling.
	 */
	public void stop() {
		timer_.cancel();
	}

	/**
	 * Add file to listen for. File may be any java.io.File (including a
	 * directory) and may well be a non-existing file in the case where the
	 * creating of the file is to be trepped.
	 * <p>
	 * More than one file can be listened for. When the specified file is
	 * created, modified or deleted, listeners are notified.
	 * 
	 * @param file
	 *            File to listen for.
	 */
	public void addFile(final File file) {
		if (!files_.containsKey(file)) {
			final long modifiedTime = file.exists() ? file.lastModified() : -1;
			files_.put(file, new Long(modifiedTime));
		}
	}

	/**
	 * Remove specified file for listening.
	 * 
	 * @param file
	 *            File to remove.
	 */
	public void removeFile(final File file) {
		files_.remove(file);
	}

	/**
	 * Add listener to this file monitor.
	 * 
	 * @param fileListener
	 *            Listener to add.
	 */
	public void addListener(final FileListener fileListener) {
		// Don't add if its already there
		for (final Iterator<WeakReference<FileListener>> i = listeners_.iterator(); i
				.hasNext();) {
			final WeakReference<FileListener> reference = i.next();
			final FileListener listener = reference.get();
			if (listener == fileListener) {
				return;
			}
		}
		// Use WeakReference to avoid memory leak if this becomes the
		// sole reference to the object.
		listeners_.add(new WeakReference<FileListener>(fileListener));
	}

	/**
	 * Remove listener from this file monitor.
	 * 
	 * @param fileListener
	 *            Listener to remove.
	 */
	public void removeListener(final FileListener fileListener) {
		for (final Iterator<WeakReference<FileListener>> i = listeners_.iterator(); i
				.hasNext();) {
			final WeakReference<FileListener> reference = i.next();
			final FileListener listener = reference.get();
			if (listener == fileListener) {
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
	private class FileMonitorNotifier extends TimerTask {
		@Override
		public void run() {
			// Loop over the registered files and see which have changed.
			// Use a copy of the list in case listener wants to alter the
			// list within its fileChanged method.
			synchronized(files_){
				final Collection<File> files = files_.keySet();
				for (File file : files) {
					final long lastModifiedTime = (files_.get(file)).longValue();
					final long newModifiedTime = file.exists() ? file.lastModified() : -1;
					checkModifiedTime(file, lastModifiedTime, newModifiedTime);
				}
			}
		}

		private void checkModifiedTime(final File file, final long lastModifiedTime,
				final long newModifiedTime) {
			// Check if file has changed
			if (newModifiedTime != lastModifiedTime) {

				// Register new modified time
				files_.put(file, new Long(newModifiedTime));//NOPMD

				// Notify listeners
				synchronized(listeners_){
					for (final Iterator<WeakReference<FileListener>> j = listeners_.iterator(); j
							.hasNext();) {
						final WeakReference<FileListener> reference = j.next();
						final FileListener listener = reference.get();

						// Remove from list if the back-end object has been GC'd
						if (listener == null) {
							j.remove();
						} else {
							listener.fileChanged(file);
						}
					}
				}
			}
		}
	}
}