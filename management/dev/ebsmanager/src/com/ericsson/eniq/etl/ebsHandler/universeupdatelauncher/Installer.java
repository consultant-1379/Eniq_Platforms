package com.ericsson.eniq.etl.ebsHandler.universeupdatelauncher;

import com.ericsson.eniq.common.lwp.LwProcess;
import com.ericsson.eniq.common.lwp.LwpException;
import com.ericsson.eniq.common.lwp.LwpOutput;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * A class for installing files to file system, and optionally executing some of
 * them.
 * 
 * @author epiituo
 * 
 */
public class Installer {

  private static final CharSequence USER_HOME_ALIAS = "%USERHOME%";

  private PrintStream out;

  /**
   * A class for encapsulating the contents of a single install script line.
   * 
   * @author epiituo
   * 
   */
  private class InstallItem {

    public String fromFileName = null;

    public String toFileName = null;

    public boolean executeAfterInstallation = false;

    public String[] arguments = null;
  }

  /**
   * The default constructor.
   * 
   */
  public Installer() {
    this.out = System.out;
  }

  /**
   * Sets the output stream used by this installer. This enables the user of the
   * class to receive information on the ongoing installation.
   * 
   * The default output stream for the installer is System.out.
   * 
   * @param outputStream
   */
  public void setOutputStream(OutputStream outputStream) {
    this.out = new PrintStream(outputStream);
  }

  /**
   * Executes the install script file without any additional arguments.
   * 
   * @param scriptResourceFileName
   * @throws IOException
   */
  public void executeInstallScript(String scriptResourceFileName) throws IOException {
    this.executeInstallScript(scriptResourceFileName, null);
  }

  /**
   * Executes the install script file with additional arguments. For each
   * executed file, the additional arguments are added after the arguments
   * defined in the install script.
   * 
   * @param scriptResourceFileName
   * @param arguments
   * @throws IOException
   */
  public void executeInstallScript(String scriptResourceFileName, Arguments arguments) throws IOException {
    Vector<String> scriptLines = readInstallScript(scriptResourceFileName);

    Vector<InstallItem> installItems = createInstallItems(scriptLines.iterator());

    install(installItems.iterator());

    execute(installItems.iterator(), arguments);
  }

  /**
   * Reads the install script file, and returns the nonempty and non-comment
   * lines in a vector.
   * 
   * @param scriptResourceFileName
   *          A resource file name. Can not be null.
   * @return A vector containing the nonempty and non-comment lines.
   * @throws IOException
   */
  private Vector<String> readInstallScript(String scriptResourceFileName) throws IOException {
    Vector<String> result = new Vector<String>();

    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(scriptResourceFileName);
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    String line = bufferedReader.readLine();
    while (line != null) {
      if (!lineShouldBeIgnored(line)) {
        result.add(line);
      }
      line = bufferedReader.readLine();
    }

    return result;
  }

  /**
   * A method for examining whether or not a line should be ignored as a comment
   * line or an empty line.
   * 
   * @param scriptLine
   *          A string containing one line from the script file.
   * @return True, if line should be ignored. False, if the line should not be
   *         ignored.
   */
  private boolean lineShouldBeIgnored(String scriptLine) {
    if (scriptLine.startsWith("#")) {
      // A comment line
      return true;
    }
    if (scriptLine.trim().equals("")) {
      // The line contains only white space
      return true;
    }
    return false;
  }

  /**
   * Creates install items, based on script lines. The method parses the line,
   * and then creates an install item that contains the file names, possible
   * execute command and execution arguments read from the line.
   * 
   * @param scriptLinesIterator
   *          An iterator that enumerates the nonempty and non-comment script
   *          lines.
   * @return A vector containing install items, each of which is based on an
   *         install script line.
   */
  private Vector<InstallItem> createInstallItems(Iterator<String> scriptLinesIterator) {
    Vector<InstallItem> result = new Vector<InstallItem>();

    StringTokenizer scriptLineTokenizer;
    String delimiterCharacters = ",";
    String executeCommand = "EXECUTE";

    while (scriptLinesIterator.hasNext()) {
      InstallItem installItem = new InstallItem();
      scriptLineTokenizer = new StringTokenizer(scriptLinesIterator.next(), delimiterCharacters);

      int numberOfTokens = scriptLineTokenizer.countTokens();

      // The script line should have at least two tokens,
      // the source and destination file names.
      if (numberOfTokens >= 2) {
        installItem.fromFileName = transformFromFileNameToken(scriptLineTokenizer.nextToken());
        installItem.toFileName = transformToFileNameToken(scriptLineTokenizer.nextToken());
      }

      // If the script line has over two tokens, examine the token number
      // three. If it equals the defined execute command, then set the
      // install item as executable, and the possible arguments are stored.
      if (numberOfTokens >= 3) {
        String token = transformExecuteToken(scriptLineTokenizer.nextToken());
        installItem.executeAfterInstallation = token.equalsIgnoreCase(executeCommand);

        // Get the arguments
        Vector<String> argumentsVector = new Vector<String>();
        while (scriptLineTokenizer.hasMoreTokens()) {
          String argument = transformArgumentToken(scriptLineTokenizer.nextToken());
          argumentsVector.add(argument);
        }
        String[] arguments = new String[argumentsVector.size()];
        argumentsVector.toArray(arguments);
        installItem.arguments = arguments;
      }

      result.add(installItem);
    }

    return result;
  }

  /**
   * Performs install on all the installItems enumerated by the iterator.
   * 
   * @param installItemsIterator
   * @throws IOException
   */
  private void install(Iterator<InstallItem> installItemsIterator) throws IOException {
    while (installItemsIterator.hasNext()) {
      install(installItemsIterator.next());
    }
  }

  /**
   * Writes the contents of a resource file into a destination file. Both source
   * and destination file are defined in installItem.
   * 
   * @param installItem
   *          An installItem defining the source and destination file names.
   * @throws IOException
   */
  private void install(InstallItem installItem) throws IOException {

    this.out.println("Writing " + installItem.fromFileName + " to " + installItem.toFileName + "...");

    String resourceFileName = installItem.fromFileName;
    String destinationFileName = installItem.toFileName;

    // Create the destination file, and open an output stream to it.
    File destinationFile = new File(installItem.toFileName);
    File parentFolder = destinationFile.getParentFile();
    parentFolder.mkdirs();
    destinationFile.createNewFile();

    // Open an output stream into the file
    FileOutputStream outputStream = new FileOutputStream(destinationFileName);

    // Open an input stream into the executable file stored in the jar file
    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(resourceFileName);

    // Write the stored file's contents into the temporary file
    int byteValue = inputStream.read();
    while (byteValue != -1) {
      outputStream.write(byteValue);
      byteValue = inputStream.read();
    }
    outputStream.flush();

    // Deallocate resources
    inputStream.close();
    outputStream.close();

    this.out.println("...Done!");
  }

  /**
   * The method executes all the install items enumerated by the
   * 'installItemsIterator', given as a parameter. For each installItem,
   * arguments are retrieved both from the installItem itself and the
   * 'runTimeArguents' parameter, in this order.
   * 
   * @param installItemsIterator
   *          An iterator to a collection of install items.
   * @param runtimeArguments
   *          An instance of Arguments class. Contains arguments for an
   *          arbitrary number of file names.
   * @throws IOException
   */
  private void execute(Iterator<InstallItem> installItemsIterator, Arguments runtimeArguments) throws IOException {
    while (installItemsIterator.hasNext()) {
      InstallItem installItem = installItemsIterator.next();
      if (installItem.executeAfterInstallation) {

        // Merge the script arguments with the run time arguments
        String[] scriptArguments = installItem.arguments;
        String[] runtimeArgumentsForThisExecutable;
        if (runtimeArguments != null) {
          runtimeArgumentsForThisExecutable = runtimeArguments.getArgumentsFor(installItem.toFileName);
        } else {
          runtimeArgumentsForThisExecutable = null;
        }

        String[] arguments = merge(scriptArguments, runtimeArgumentsForThisExecutable);

        // Execute the file with its arguments.
        executeFile(installItem.toFileName, arguments);
      }
    }
  }

  /**
   * Executes the file defined by the parameter 'fileName'. The file is executed
   * with the arguments defined in the parameter 'arguments'.
   * 
   * @param fileName
   *          The full path and name of the file, that is to be executed. Can
   *          not be null.
   * @param arguments
   *          The arguments for the execution of the file. Can not be null.
   * @throws IOException
   * @throws InterruptedException
   */
  private void executeFile(String fileName, String[] arguments) throws IOException {
    // Create the arguments list, that will be used to create the process.
    List<String> argumentList = new ArrayList<String>();
    argumentList.add("cmd.exe");
    argumentList.add("/c");
    argumentList.add(fileName);
    for(int i=0; i<arguments.length; ++i) {
      argumentList.add(arguments[i]);
    }
    // Create a string containing the arguments, and print them.
    StringBuffer argumentsBuffer = new StringBuffer();

    StringBuffer hidePassword = new StringBuffer();
    for (int i = 0; i < arguments.length; ++i) {
    	if(i!=2){
    		hidePassword.append(arguments[i]);
    		hidePassword.append(" ");
    	}
      argumentsBuffer.append(arguments[i]);
      argumentsBuffer.append(" ");
    	
    }
 
    this.out.println("Executing " + fileName + " with the following arguments:"+hidePassword.toString());

    try {
      final LwpOutput results = LwProcess.execute(argumentList, true, null);
      this.out.println(addIndentation(results.getStdout(), 2));
    } catch (LwpException e) {
      this.out.println("...An error occured when waiting for the process to end.");
    } catch (Throwable e) {
      this.out.println("...An error occured when waiting for the process to end.");
    }
    
  }

  /**
   * Adds indentation to a string contained by the string buffer given as a
   * parameter.
   * 
   * @param buffer
   * @return
   */
  private static String addIndentation(String text, int depth) {
    // First, construct the indentation string
    StringBuffer indentationBuffer = new StringBuffer();
    for (int i = 0; i < depth; ++i) {
      indentationBuffer.append(' ');
    }
    String indentation = indentationBuffer.toString();

    // Add the indentation to the beginnings of lines
    StringBuffer resultBuffer = new StringBuffer(indentation);
    for (int i = 0; i < text.length(); ++i) {
      char character = text.charAt(i);
      resultBuffer.append(character);
      if (character == '\n') {
        resultBuffer.append(indentation);
      }
    }
    return resultBuffer.toString();
  }

  /**
   * A utility method for merging two string arrays.
   * 
   * @param firstArray
   *          The first string array, can not be null.
   * @param secondArray
   *          The second string array, may be null.
   * @return A new string array that contains the strings from the first array,
   *         appended with the strings from the second array.
   */
  private static String[] merge(String[] firstArray, String[] secondArray) {

    if (secondArray == null) {
      return firstArray;
    }

    String[] result = new String[firstArray.length + secondArray.length];
    for (int i = 0; i < firstArray.length; ++i) {
      result[i] = firstArray[i];
    }
    for (int i = 0; i < secondArray.length; ++i) {
      result[firstArray.length + i] = secondArray[i];
    }
    return result;
  }

  /**
   * A utility method for transforming a string containing a source file names
   * into uniform form. This ensures that using the same file name in slightly
   * different forms will have the same effects.
   * 
   * @param fromFileName
   *          A string containing the file name
   * @return The result of the transformations made to the parameter
   *         'toFileName'.
   */
  private String transformFromFileNameToken(String fromFileName) {
    String result = fromFileName.trim();
    return result;
  }

  /**
   * A utility method for transforming a string containing a destination file
   * names into uniform form. This ensures that using the same file name in
   * slightly different forms will have the same effects.
   * 
   * @param toFileName
   *          A string containing the file name
   * @return The result of the transformations made to the parameter
   *         'toFileName'.
   * 
   */
  protected static String transformToFileNameToken(String toFileName) {
    CharSequence userHomeReplacement = System.getProperty("user.home");

    // TODO Should more environment variables be evaluated?
    String result = toFileName.replace(USER_HOME_ALIAS, userHomeReplacement);
    result = result.trim();

    return result;
  }

  /**
   * A utility method for transforming execute tokens into uniform form, by for
   * example removing the white space characters around the token.
   * 
   * @param executeToken
   * @return
   */
  protected static String transformExecuteToken(String executeToken) {
    String result = executeToken.trim();
    return result;
  }

  /**
   * A utility method for transforming argument tokens into uniform form, by for
   * example removing the white space characters around the token.
   * 
   * @param argumentToken
   * @return
   */
  protected static String transformArgumentToken(String argumentToken) {
    String result = argumentToken.trim();
    return result;
  }

}