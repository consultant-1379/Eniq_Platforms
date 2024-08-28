package com.ericsson.sim.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutionPool {

	private ExecutorService executor = null;
	private int queuedCount=0;
	
	public ExecutionPool(int noOfexecutionSlots){
		executor = Executors.newFixedThreadPool(noOfexecutionSlots);
	}
	
	public void addToExecutor(ExecutorThread thread){
		executor.execute(thread);
		queuedCount++;
	}
	
	public void shutdown(){
		executor.shutdown();
	}
	
	public int getQueuedCount(){
		return queuedCount;
	}
}
