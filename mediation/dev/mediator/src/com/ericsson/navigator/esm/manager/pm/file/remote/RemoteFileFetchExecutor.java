package com.ericsson.navigator.esm.manager.pm.file.remote;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ericsson.navigator.esm.util.component.Component;
import com.ericsson.navigator.esm.util.component.ComponentInitializationException;
import com.ericsson.navigator.esm.util.component.ComponentShutdownException;

public class RemoteFileFetchExecutor implements Component {

	private final ThreadPoolExecutor fileFetchPool;
	
	public RemoteFileFetchExecutor(final int corePoolSize, final int maximumPoolSize, 
			final int keepAliveMinutes, final int queueSize){
		fileFetchPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveMinutes, 
				TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(queueSize));
	}
	
	@Override
	public String getComponentName() {
		return "PM Remote File Fetch Executor";
	}

	@Override
	public void initialize() throws ComponentInitializationException {}
	
	public void execute(final Runnable command){
		fileFetchPool.execute(command);
	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		fileFetchPool.shutdownNow();
	}
}
