package com.ericsson.navigator.esm.model.pm.file.remote;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.ericsson.navigator.esm.MVMEnvironment;

/**
 * HelperClass to monitor the current thread.This class is called from CounterSetFileFetcher class. 
 * A Timer is started inside the constructor and timer shall be cancelled if the task is completed before timeout period.
 * The timeout period(THREAD_KEEPALIVETIME) is read from the MVMEnvironment class (default value : 5 minutes ). 
 * This value can be changed in ESM properties file.If the thread is still executing the task even after THREAD_KEEPALIVETIME,
 * the timer shall cancel the task and stop the thread. 
 * This functionality is implemented as part of TR HM54382- ERICesm: PM collection threads hang when node does not respond to remote commands 
 * 
 */
public class Threadhelper {
	private static final String classname = CounterSetFileFetcher.class.getName();
	private static final Logger logger = Logger.getLogger(classname);
	
    	final private Thread thread;
    	private Timer timer = null;
    	private PuttaClass putta = null;
    
    	public Threadhelper(final Thread currThread)
    	{
    		this.thread = currThread;
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
					logger.debug(" Thread timed out: " + thread.getName());
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
    		timer.schedule(putta,MVMEnvironment.THREAD_KEEPALIVETIME * 60000);
    		return timer;
    	}
    	
    	/**
		 * Cancel the timer 
		 * 
		 * @param 
		 * @return 
		 */
    	void cancelTimerTask()
    	{
    		if( timer != null)
    		{	
    			 putta.cancel();
      			 timer.cancel();
    			 timer = null;
    			
    		}
    	}
    	
}

