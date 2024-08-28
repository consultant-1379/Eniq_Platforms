/**
 * 
 */
package com.ericsson.eniq.engine;

import com.ericsson.eniq.common.lwp.LwProcess;
import com.ericsson.eniq.common.lwp.LwpException;
import com.ericsson.eniq.common.lwp.LwpOutput;
import com.ericsson.eniq.exception.AFJException;


/**
 * @author eheijun
 *
 */
public class EngineRestarter {
  
  private EngineRestarter() {
  }
  
  /**
   * Restarts engine
   * @throws AFJException
   */
  public static void execute() throws AFJException {
    final String engineRestartCommand = "/eniq/sw/bin/engine restart";
    runCommand(engineRestartCommand);
  }

  /**
   * This command is support for executing any system commands from GUI. Use
   * getExitValue() to get the exitValue of the system command.
   * 
   * @param command
   *          the command that is needed to run
   * @return returns the output of the completed command
   */
  private static String runCommand(final String command) throws AFJException {
    try {
      final LwpOutput results = LwProcess.execute(command, true, null);
      final StringBuilder returnString = new StringBuilder(results.getStdout());
      returnString.append("Command executed with exitvalue ").append(results.getExitCode());
      return returnString.toString();
    } catch (LwpException e) {
      throw new AFJException("engine restart command failed", e);
    }
  }
}
