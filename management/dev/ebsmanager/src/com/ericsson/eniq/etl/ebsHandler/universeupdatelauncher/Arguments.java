package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import java.util.HashMap;

/**
 * A class for providing arguments for executable files that are executed after
 * the installation.
 * 
 * @author epiituo
 * 
 */
public class Arguments {

  private HashMap<String, String[]> arguments;

  public Arguments() {
    this.arguments = new HashMap<String, String[]>();
  }

  /**
   * Adds an argument to the list of arguments for 'executableName'. The
   * arguments are stored in the same order that they were added.
   * 
   * @param executableName
   *          The name of the executable that the argument is added to. Can not
   *          be null.
   * @param argument
   *          The argument. Can not be null.
   */
  public void addArgument(String executableName, String argument) {

    // First, make sure that the executable name is exactly in the same
    // form, as when the file is executed. Most importantly, this means
    // that any reference to %USERPROFILE% must be replaced with the
    // absolute directory name.
    executableName = Installer.transformToFileNameToken(executableName);
    executableName = executableName.toLowerCase();

    String[] previousAddedArguments = this.arguments.get(executableName);
    String[] currentAddedArguments;
    if (previousAddedArguments == null) {
      // Since there were no earlier added arguments, create a new
      // array of arguments, and add the current argument to it.
      currentAddedArguments = new String[1];
      currentAddedArguments[0] = argument;
    } else {
      // There were already arguments stored for this executable, therefore
      // create a new larger array, and move the arguments there.
      currentAddedArguments = new String[previousAddedArguments.length + 1];
      for (int i = 0; i < previousAddedArguments.length; ++i) {
        currentAddedArguments[i] = previousAddedArguments[i];
      }
      currentAddedArguments[currentAddedArguments.length - 1] = argument;
    }
    this.arguments.put(executableName, currentAddedArguments);
  }

  /**
   * Clears all the arguments added for 'executableName'.
   * 
   * @param executableName
   */
  public void clearAddedArguments(String executableName) {
    executableName = Installer.transformToFileNameToken(executableName);
    executableName = executableName.toLowerCase();
    this.arguments.remove(executableName);
  }

  /**
   * Returns the arguments attached to the 'executableName'.
   * 
   * @param executableName
   * @return An array of arguments added for the executable name defined by the
   *         parameter. If there were no parameters added for the name, then
   *         returns null.
   */
  public String[] getArgumentsFor(String executableName) {
    executableName = Installer.transformToFileNameToken(executableName);
    executableName = executableName.toLowerCase();
    return arguments.get(executableName);
  }
}
