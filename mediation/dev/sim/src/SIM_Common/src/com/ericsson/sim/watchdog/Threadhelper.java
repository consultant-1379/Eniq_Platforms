package com.ericsson.sim.watchdog;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Threadhelper {
	
	final private Thread thread;
	final private long timeLimit;
	private Timer timer = null;
	private PuttaClass putta = null;

	public Threadhelper(final Thread currThread, final long timeLimit)
	{
		this.timeLimit = timeLimit;
		this.thread = currThread;
	}
	
	public void startTimer(){
		timer = startTimerTask(); //NOPMD
	}
	
	/**
	 * InnerClass to support the TimerTask functionality
	 * 
	 * @param 
	 * @return boolean
	 */
	private class PuttaClass extends TimerTask{
		
		final private AtomicBoolean isCancelled = new AtomicBoolean(false);
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if(!isCancelled.get()){
				System.out.println(" Thread timed out: " + thread.getName());
				thread.stop();
			}
			
			putta.cancel();
			timer.cancel();
			putta = null;
			timer = null;
			}
		
		/**
		 * Override the cancel method to cancel the TimerTask
		 * 
		 * @param 
		 * @return boolean
		 */
		@Override
		public boolean cancel() {
			isCancelled.getAndSet(true);
			return super.cancel();
			
		}

	}
	
	/**
	 * Schedule the timer 
	 * 
	 * @param 
	 * @return Timer instance
	 */
	private Timer startTimerTask()
	{
		putta = new PuttaClass();
		timer = new Timer(false);
		timer.schedule(putta,timeLimit);
		return timer;
	}
	
	/**
	 * Cancel the timer 
	 * 
	 * @param 
	 * @return 
	 */
	public void cancelTimerTask()
	{
		if( timer != null)
		{	
			 putta.cancel();
  			 timer.cancel();
			 timer = null;
			
		}
	}
    	
}