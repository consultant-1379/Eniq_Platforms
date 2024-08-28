package com.ericsson.eniq.techpacksdk;

import org.jdesktop.application.Task;
import org.jdesktop.application.Task.InputBlocker;


public class BusyIndicatorInputBlocker extends InputBlocker {
  
  private BusyIndicator busyIndicator;
  
  public BusyIndicatorInputBlocker(Task<?, ?> task, BusyIndicator busyIndicator) {
    super(task, Task.BlockingScope.WINDOW, busyIndicator);
    this.busyIndicator = busyIndicator;
  }

  @Override
  protected void block() {
    busyIndicator.start();
  }

  @Override
  protected void unblock() {
    busyIndicator.stop();
  }


}
