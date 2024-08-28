package com.ericsson.eniq.licensing.cache;

import com.distocraft.dc5000.etl.rock.Meta_databases;
import com.ericsson.eniq.common.RemoteExecutor;
import com.ericsson.eniq.common.lwp.LwProcess;
import com.ericsson.eniq.common.lwp.LwpException;
import com.ericsson.eniq.common.lwp.LwpOutput;
import com.ericsson.eniq.repository.DBUsersGet;
import com.jcraft.jsch.JSchException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Detects the number of CPUs (physical CPUs or cores) present in a system.
 * @author eciacah
 */
public class CPUDetector {

  /** Command used to get number of cores on system */
  public static final String CORE_CPUS_COMMAND = "/usr/sbin/psrinfo";

  /** Command used to get number of cores on system */
  public static final String PHYSICAL_CPUS_COMMAND = "/usr/sbin/psrinfo -p";

  /** Logger */
  private static final Logger log = Logger.getLogger("licensing.cache.CPUDetector");

  /**
   * Gets the number of physical CPUs installed in a system.
   * 
   * @return
   */
  public int getNumberOfPhysicalCPUs(final boolean forceLocal) {
    return this.getNumberOfCPUs(true, forceLocal);
  }

  /**
   * Gets the number of cores installed in a system.
   * 
   * @return
   */
  public int getNumberOfCores(final boolean forceLocal) {
    return this.getNumberOfCPUs(false, forceLocal);
  }

  /**
   * Executes a command in the operating system.
   * 
   * @param command
   *          The text of the command to execute.
   * @return ps The process for the command.
   * @throws IOException
   */
  protected Process executeCommand(final String command) throws IOException {
    final Process ps = Runtime.getRuntime().exec(command);
    return ps;
  }

  /**
   * Creates a BufferedReader from the input stream of a process.
   * 
   * @param process
   *          A process.
   * @return in The new BufferedReader.
   */
  protected BufferedReader createBufferedReaderForProcess(final Process process) {
    final BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    return in;
  }

  /**
   * Gets the number of CPUs for a machine.
   * 
   * @param getPhysicalCPUs
   *          True if we need the number of physical CPUs, else returns the
   *          number of cores
   * @return numberOfProcessors The number of CPUs. Will return -1 if there is
   *         an error.
   */
  private int getNumberOfCPUs(final boolean getPhysicalCPUs, final boolean forceLocal) {
    int numberOfProcessors = 0;
    String command = "";
    if (getPhysicalCPUs) {
      command = PHYSICAL_CPUS_COMMAND;
    } else {
      command = CORE_CPUS_COMMAND;
    }
    if(forceLocal){
      numberOfProcessors = executeLocal(command, getPhysicalCPUs);
    } else {
      try {
        final LwpOutput results = LwProcess.execute(command, true, log);
        if (results.getExitCode() != 0) {
          log.warning("Error getting number of processors: " + results);
          numberOfProcessors = -1;
        }
 else if(command.equals(PHYSICAL_CPUS_COMMAND))
 {
numberOfProcessors =calculatePhysicalCPUs(new BufferedReader(new StringReader(results.getStdout())));
}

else {
          numberOfProcessors = calculateNoCores(new BufferedReader(new StringReader(results.getStdout())));
        }
      } catch (LwpException e) {
        log.log(Level.WARNING, "Error getting number of processors", e);
        numberOfProcessors = -1;
      } catch (Throwable e) {
        log.log(Level.WARNING, "Error getting number of processors", e);
        numberOfProcessors = -1;
      }
    }
    log.info("Detected " + numberOfProcessors + " processors");
    return numberOfProcessors;
  }

  private int executeLocal(final String command, final boolean getPhysicalCPUs) {
    BufferedReader in = null;

    log.info("Getting number of CPUs");

    int numberOfProcessors = 0;
    try {
      // execute the sunos command psrinfo -p to get information about the
      // physical CPUs:
      final Process cpuinfo = executeCommand(command);
      in = createBufferedReaderForProcess(cpuinfo);
      cpuinfo.waitFor();

      if (getPhysicalCPUs) {
        numberOfProcessors = calculatePhysicalCPUs(in);
        log.info(command + " reported " + numberOfProcessors + " processors.");
      } else {
        numberOfProcessors = calculateNoCores(in);
        log.info(command + " reported " + numberOfProcessors + " cores.");
      }

    } catch (Exception e) {
      log.warning("Error getting number of processors: " + e.toString());
      numberOfProcessors = -1;
      e.printStackTrace();
    } finally {
      // close the stream
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          log.warning("IOException when closing the psrinfo stream reader.");
        }
      }
    }
    return numberOfProcessors;
  }
  
  /**
   * Calculates the number of physical CPUS, given output from psrinfo -p command.
   * @param   in            The BufferedReader holding the output from psrinfo.
   * @return  numberOfCpus  Number of cpus.           
   * @throws IOException
   */
  private int calculatePhysicalCPUs(final BufferedReader in) throws IOException {
    int numberOfCpus = 0;

      // psrinfo -p outputs a single line with the number of processors:
      final String processorsString = in.readLine();
      if (processorsString != null && processorsString.length() > 0) {
        numberOfCpus = Integer.parseInt(processorsString);
        log.info("psrinfo -p reported " + numberOfCpus + " processors.");
      } else {
        log.warning("Read in a blank line from psrinfo command.");
      }
      if(checkMultiBlade().equalsIgnoreCase("multiblade")){
    	  numberOfCpus = numberOfCpus*4;
    	  log.info("Server is Multiblade, so total " + numberOfCpus + " processors.");
    	  return numberOfCpus;
      }
    return numberOfCpus;
  }
  
  /**
   * Calculates the number of cores, given output from psrinfo command.
   * @param   in            The BufferedReader holding the output from psrinfo.
   * @return  numberOfCpus  Number of cores.  
   * @throws IOException
   */
  private int calculateNoCores(final BufferedReader in) throws IOException {
    int numberOfCpus = 0;
      // psrinfo outputs one line for each core. Count the lines to get the
      // number of cores.
      String newLine = in.readLine();
      while (newLine != null) {
        try {
          if (newLine.length() > 0) {
            // Check that the first character is a number:
            final char firstChar = newLine.charAt(0);
            final String charString = Character.toString(firstChar);
            // Check if we can parse the string:
            Integer.parseInt(charString);
            numberOfCpus++;
          } else {
            log.warning("Read in a blank line from psrinfo command.");
          }
        } catch (NumberFormatException numberFormatExc) {
          log.severe("Error reading output from psrinfo command. Could not parse the number of the core: "
              + numberFormatExc.toString());
          log.severe("Line = " + newLine);
        } catch (Exception exc) {
          log.severe("Error reading line from psrinfo command. Line did not start with the number of the core. Line = " + newLine);
        }
        // Read in the next line:
        newLine = in.readLine();
    }
      log.info("psrinfo reported " + numberOfCpus + " cores.");
      return numberOfCpus;
  }
  
	private  String checkMultiBlade()
	  {
	    try
	    {
	      String command = "";
	      List localList = DBUsersGet.getMetaDatabases("dcuser", "engine");
	        localList = DBUsersGet.getMetaDatabases("dcuser", "engine");
	        if (localList.isEmpty()) {
	          throw new Exception("Could not find an entry for dcuser:engine in repdb! (was is added?)");
	        }
	      String password = ((Meta_databases)localList.get(0)).getPassword();
	      command = "cat /eniq/installation/config/installed_server_type";
	      String result = RemoteExecutor.executeComand("dcuser", password, "engine", command);
	      if(result.contains("stats") && !result.trim().equalsIgnoreCase("eniq_stats")){
	         return "multiblade";
	      }else{
	    	  return "blade";
	      }

	    }
	    catch (JSchException localJSchException)
	    {
	      localJSchException.printStackTrace();
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
		return "blade";
	  }

}
