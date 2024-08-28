//******************************************************************************
//
//  COPYRIGHT Ericsson AB, Sweden 2008. All rights reserved.
//
//  The Copyright to the computer program(s) herein is the property of
//  Ericsson AB, Sweden. The program(s) may be used and/or copied only
//  with the written permission from Ericsson AB or in accordance with
//  the terms and conditions stipulated in the agreement/contract under
//  which the program(s) have been supplied.
//
//******************************************************************************
package com.ericsson.navigator.sr;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ericsson.navigator.sr.ir.Ir;
import com.ericsson.navigator.sr.ir.SNOSNE;
import com.ericsson.navigator.sr.util.Status;

/**
 * 
 * @author epkfjo
 * 
 */
@SuppressWarnings( "PMD.CyclomaticComplexity" )
public class SystemRegistration {

	public static Logger logger = Logger.getLogger("System Registration");

	private static SystemRegistration sysreg;
	final public static String all = "#all";
	private String repositoryPath;
	private String srDTDPath;
	private String logConfig;

	private boolean backupExists = false;

	public void initLogging() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
	}

	public Status initLogging(final String cfgFile) {
		Status result = Status.Success;
		final File p = new File(cfgFile);
		if (p.exists() && p.isFile() && p.canRead()) {
			try {
				final FileInputStream fis = new FileInputStream(cfgFile);
				final Properties prop = new Properties();
				prop.load(fis);

				if (result == Status.Success) {
					logConfig = prop.getProperty("logg.config",
							"/nav/opt/esm/etc/logg.cfg");
					if (!(new File(logConfig)).exists()) {
						logger.warn(logConfig + " does not exist.");
					} else {
						logger.debug("Configuring log4j using file: "
								+ logConfig);
						BasicConfigurator.resetConfiguration();
						DOMConfigurator.configure(logConfig);
					}
				}
			} catch (final java.io.IOException e) {
				logger.error("Failed to read configuration file. ");
				logger.debug("Caused by: ", e);
				result = Status.Fail;
			}
		} else {
			logger.fatal("Configuration file is not redable: " + cfgFile);
			usage();
			result = Status.Fail;
		}
		if (result == Status.Fail) {
			logger
					.warn("The System Registration configuration file contains errors");
		}
		return result;
	}

	/**
	 * This method reads the System Registration configuration file.
	 * 
	 * @param cfgFile
	 *            The configuration file.
	 * @return Success if the file was read properly.
	 */
	private Status processCfg(final String cfgFile) {

		if (cfgFile == null) {
			return Status.Fail;
		}
		Status result = Status.Success;
		final File p = new File(cfgFile);
		if (p.exists() && p.isFile() && p.canRead()) {
			try {
				final FileInputStream fis = new FileInputStream(cfgFile);
				final Properties prop = new Properties();
				prop.load(fis);

				repositoryPath = prop.getProperty("repository.path",
						"/nav/var/esm/repository/SystemTopology.xml");
				if (!(new File(repositoryPath)).exists()) {
					logger.fatal(repositoryPath + " does not exist.");
					result = Status.Fail;
				}

				if (result == Status.Success) {
					srDTDPath = prop.getProperty("dtd.path",
							"file:///nav/opt/esm/etc/model.dtd");

					if (srDTDPath.contains("file://")) {
						final String testSrDTDPath = srDTDPath.replace("file://", "");
						if (!(new File(testSrDTDPath)).exists()) {
							logger.fatal(testSrDTDPath + " does not exist.");
							result = Status.Fail;
						}
					}
				}
			} catch (final java.io.IOException e) {
				logger.error("Failed to read configuration file. ");
				logger.debug("Caused by: ", e);
				result = Status.Fail;
			}
		} else {
			logger.fatal("Configuration file is not redable: " + cfgFile);
			usage();
			result = Status.Fail;
		}
		if (result == Status.Fail) {
			logger
					.warn("The System Registration configuration file contains errors");
		}
		return result;
	};

	/**
	 * Verifies the validity of the given system registration file.
	 * 
	 * @param args
	 *            The following arguments are expected:<br>
	 *            args[0] -verify<br>
	 *            args[1] Filename for the System Registration File.<br>
	 *            args[2] The system registration configuration file.
	 * @return Always returns success.
	 */
	public Status verify(final String[] args) {

		Ir ir;
		Status result = Status.Fail;

		if (args.length == 3) {
			final File f = new File(args[1]);
			final File p = new File(args[2]);
			if (f.exists() && f.isFile() && f.canRead() && p.exists()
					&& p.isFile() && p.canRead()) {
				if ((result = initLogging(args[2])) == Status.Fail) {
					logger.fatal("cannot read configuration file " + args[2]
							+ ", will use default logging");
				} else {
					try {
						/*
						 * Fetch the specified system from the system registry
						 * as an IR.
						 */
						ir = srfRead(args[1]);
						if (ir == null) {
							logger
									.fatal("Failed to read the System Registration File "
											+ args[1]);
							result = Status.Fail;
						} else {
							/* The contents of the IR is verified. */
							result = irVerify(ir);
						}
					} catch (final Exception e) {
						logger
								.fatal("Could not parse the System Registration file: "
										+ args[1]);
						logger.debug("Caused by: ", e);
						result = Status.Fail;
					}
				}
			} else {
				logger.fatal("Could not find one or more arguments.");
				usageVerify();
				result = Status.Fail;
			}
		} else {
			logger.fatal("Wrong number of arguments.");
			usageVerify();
		}
		return result;
	};

	/**
	 * This method parses a System Registration File and creates an Intermediate
	 * Representation (IR). The System Topology File is updated with the
	 * contents from the IR.
	 * 
	 * @param args
	 *            The following arguments shall be received:<br>
	 *            args[0] -save <br>
	 *            args[1] Filename for the System Registration File.<br>
	 *            args[2] The system registration configuration file.
	 * @return Success if the system registration file was successfully saved.
	 */
	public Status save(final String[] args) {

		Ir ir, irSystem;
		Status result = Status.Success;
		Status resultAddSystems = Status.Fail;

		if (args.length == 3) {

			final File f = new File(args[1]);
			final File p = new File(args[2]);

			if (f.exists() && f.isFile() && f.canRead() && p.exists()
					&& p.isFile() && p.canRead()) {
				if ((result = initLogging(args[2])) == Status.Fail) {
					logger.fatal("cannot read configuration file " + args[2]
							+ ", will use default logging");
				} else if ((result = processCfg(args[2])) == Status.Fail) {
					logger.fatal("cannot read configuration file " + args[2]);
				} else {
					try {
						irSystem = srfRead(args[1]); // Create an IR for the
						// System Registration
						// File.
						ir = srfRead(repositoryPath); // Create an IR for the
						// System Topology File.
						if (irSystem == null) {
							// There is no system to save.
							logger
									.fatal("Failed to read the System Registration File "
											+ args[1]);
							return Status.Fail;
						} else {
							if (ir == null) {
								// There is no current repository, replace with
								// given system registration file.
								logger
										.info("The repository is empty, given system registration file will become new repository.");
							} else {
								// Normal case.
								// Both registry and system registration file
								// exists, replace content in repository
								// with content in system registration file.
								// Fetch the system(s) to be added to the IR.
								final List<SNOSNE> systems = irSystem
										.getSystems();

								// Update the IR with the new system(s).
								if (systems != null) {
									resultAddSystems = ir.addSystems(systems);
								} else {
									logger.fatal("Failed to get system from "
											+ args[1]);
									return Status.Fail;
								}
								if (resultAddSystems == Status.Fail) {
									logger
											.fatal("Not possible to save the System Registration File "
													+ args[1]
													+ ". Unable to add the system to the IR.");
									return Status.Fail;
								}
							}
						}
						// now save if result can be verified
						if (irVerify(ir) == Status.Success) {
							result = srfWrite(repositoryPath, ir, all);
							if (result == Status.Success) {
								logger
										.debug("The System Registration File was saved successfully.");
							} else {
								logger
										.warn("The System Registration File was not saved.");
							}
						} else {
							logger
									.fatal("Producing a combined intermediate result topology failed.");
							result = Status.Fail;
						}
					} catch (final Exception e) {
						logger
								.fatal("Could not parse the System Registration file: "
										+ args[1]);
						logger.debug("Caused by: ", e);
						result = Status.Fail;
					} // End try
				}// End if (result == Status.Success)
			} else {
				logger.fatal("Could not find one or more arguments.");
				usageSave();
				result = Status.Fail;
			} // End if (f.exist() && f.isFile()&& f.canRead()&& p.exists()&&
			// p.isFile()&& p.canRead())
		} else {
			logger.fatal("Wrong number of arguments.");
			usageSave();
			result = Status.Fail;
		} // End if (args.length == 3)

		return result;
	}
	
	/**
	 * Get the repository path of the SystemTopology.xml file
	 * @param cfgFile
	 * @return
	 */
    String getRepositoryPath(final String cfgFile){
		if (cfgFile != null) {
			final File p = new File(cfgFile);
			if (p.exists() && p.isFile() && p.canRead()) {
				try{
					final FileInputStream fis = new FileInputStream(cfgFile);
					final Properties prop = new Properties();
					prop.load(fis);
				
					repositoryPath = prop.getProperty("repository.path",
							"/nav/var/esm/repository/SystemTopology.xml");
				}catch(Exception e){
					
				}
			}
		}
		return repositoryPath;
	}
    
	/**
	 * Exports a system registration file from the repository.
	 * 
	 * @param args
	 *            This method expects three of five arguments, in the case of
	 *            three arguments: <br>
	 *            args[0] -export<br>
	 *            args[1] Output filename for the exported system registry.<br>
	 *            args[2] The system registration configuration file.<br>
	 *            <br>
	 *            In the case of five arguments:<br>
	 *            args[0] -export<br>
	 *            args[1] -system<br>
	 *            args[2] The name of the system to be exported.<br>
	 *            args[3] Output filename for the exported specified system of
	 *            the system registry.<br>
	 *            args[4] The system registration configuration file.<br>
	 *            <br>
	 * @return Success if the system registration file was exported correctly.
	 */
	public Status export(final String[] args) {
		Ir ir;
		Status result = Status.Fail;
		String targetFileName = null;
		String configFileName = null;
		String systemName = all;

		switch (args.length) {
		case 3:
			targetFileName = args[1];
			configFileName = args[2];
			repositoryPath = getRepositoryPath(configFileName);
			// To avoid the user from mentioned the SystemTopology file path for the export
			if(targetFileName.equals(repositoryPath)){
				logger.fatal("Cannot export to the path "+repositoryPath+" mentioned, use different Path...");
                return Status.Fail;
			}
			break;
		case 5:
			systemName = args[2];
			targetFileName = args[3];
			configFileName = args[4];
			repositoryPath = getRepositoryPath(configFileName);
			// To avoid the user from mentioned the SystemTopology file path for the export
			if(targetFileName.equals(repositoryPath)){
				logger.fatal("Cannot export to the path "+repositoryPath+" mentioned, use different Path...");
                return Status.Fail;
			}
			break;
		default:
			logger.fatal("Wrong number of arguments.");
			usageExport();
			return Status.Fail;
		}

		// process config file
		if ((result = initLogging(configFileName)) == Status.Fail) {
			logger.fatal("cannot read configuration file " + configFileName
					+ ", will use default logging");
		} else if (processCfg(configFileName) == Status.Fail) {
			logger
					.fatal("Could not read configuration file: "
							+ configFileName);
			return Status.Fail;
		}

		// Test target file...
		final File targetFile = new File(targetFileName);
		if (targetFile.isDirectory()) {
			logger
					.fatal("Unable to write system registration file (target is a directory): "
							+ targetFileName);
			return Status.Fail;
		} else if (targetFile.exists()) {
			if (!targetFile.delete()) {
				logger
						.fatal("Unable to write system registration file (permission denied): "
								+ targetFileName);
				return Status.Fail;
			}
		}
		// we have configuration and arguments, now write the read the registry
		// and export
		ir = srfRead(repositoryPath);

		if (args.length == 5) {
			// Check if the system given in command exists before trying to
			// write to file
			if (!ir.systemExists(systemName)) {
				logger.error("System " + systemName + " does not exist.");
				return Status.Fail;
			}
		}
		/* The contents of the IR is written to file. */
		result = srfWrite(targetFileName, ir, systemName);
		return result;
	}

	/**
	 * Displays the contents of the system registry.
	 * 
	 * @param args
	 *            This command expects two or four arguments, in the case of two
	 *            arguments:<br>
	 *            args[0} -view <br>
	 *            args[1] The system registration configuration file. <br>
	 *            <br>
	 *            In the case of four arguments: args[0} -view args[1] -system
	 *            args[2] The system name of the system to be deleted. args[3] -
	 *            args[n] Could be a system name containing spaces.
	 *            args[args.length -1] The system registration configuration
	 *            file.
	 * @return Success if the command was executed properly.
	 */
	public Status view(final String[] args) {

		Ir ir;
		Status result = Status.Fail;

		String systemName = null;
		String config = null;
		final int maxArgLength = args.length - 1;

		if (args.length < 2 || args.length == 3) {
			logger.fatal("Wrong number of arguments.");
			usageView();
			return Status.Fail;
		}

		if (args.length == 2) {
			systemName = all;
			config = args[1];
		}

		if (args.length == 4) {
			systemName = args[2];
			config = args[3];
		}

		if (args.length > 4) { // Spaces in System Name
			systemName = "";
			for (int a = 2; a <= maxArgLength - 2; a++) {
				systemName = systemName + args[a] + " ";
			}
			systemName = systemName + args[maxArgLength - 1];
			config = args[maxArgLength];
		}

		if ((result = initLogging(config)) == Status.Fail) {
			logger.fatal("cannot read configuration file " + config
					+ ", will use default logging");
		} else if ((result = processCfg(config)) == Status.Fail) {
			logger.fatal("Cannot read configuration file " + config);
		} else {
			/* Fetch the whole system registry as an IR. */
			ir = srfRead(repositoryPath);
			if (ir == null) {
				logger.fatal("The repository is empty.");
				return Status.Fail;
			} else if (args.length >= 4) {
				// Check if the system given in command exists before trying to
				// write to console
				if (!ir.systemExists(systemName)) {
					logger.error("System " + systemName + " does not exist.");
					return Status.Fail;
				}
			}
			/* The contents of the IR is written to the console. */
			result = hrWrite(ir, systemName);
		}
		return result;
	}

	/**
	 * Deletes a named system from the repository.
	 * 
	 * @param args
	 *            The following arguments are expected:<br>
	 *            args[0] -delete<br>
	 *            args[1] The system name of the system to be deleted.<br>
	 *            args[2] - args[n] Could be a system name containing spaces.
	 *            args[args.length -1] The system registration configuration
	 *            file.<br>
	 * @return Success if the system was deleted.
	 */
	public Status delete(final String[] args) {

		Status result = Status.Fail;
		String systemName = args[1];
		final int maxArgLength = args.length - 1;

		if (args.length < 3) {
			logger.fatal("Too few arguments.");
			usageDelete();
			return Status.Fail;
		}
		if (args.length > 3) { // Spaces in System Name
			systemName = "";
			for (int a = 1; a <= maxArgLength - 2; a++) {
				systemName = systemName + args[a] + " ";
			}
			systemName = systemName + args[maxArgLength - 1];
		}
		if ((result = initLogging(args[maxArgLength])) == Status.Fail) {
			logger.fatal("cannot read configuration file " + args[maxArgLength]
					+ ", will use default logging");
		} else if ((result = processCfg(args[maxArgLength])) == Status.Fail) {
			logger.fatal("cannot read configuration file" + args[maxArgLength]);
		} else {
			final Ir irSystemRegistry = srfRead(repositoryPath);

			if ((result = srfDelete(systemName, irSystemRegistry)) == Status.Success) {

				result = srfWrite(repositoryPath, irSystemRegistry, all);
			} else {
				logger.error("System " + systemName + " does not exist");
			}
		}
		return result;
	}

	private static final String useVerify = "	[-verify <system registration file>]\n";
	private static final String useSave = "	[-save <system registration file> <system registration config file>]\n";
	private static final String usePublish = "	[-publish <system registration file> <system registration config file>]\n";
	private static final String useExport = "	[-export [-system <system name>] <system registration file> <system registration config file>]\n";
	private static final String useView = "	[-view <system registration file> <system registration config file>]\n";
	private static final String useDiff = "	[-diff <system registration file> <system registration config file>]\n";
	private static final String useDelete = "	[-delete <system name> <system registration config file>]\n";
	private static final String useNotify = "	[-notify <system registration config file>]";

	private void usage() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useVerify
				+ useSave + usePublish + useExport + useView + useDiff
				+ useDelete + useNotify + "\n");
	}

	private void usageVerify() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useVerify
				+ "\n");
	}

	private void usageSave() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useSave
				+ "\n");
	}

	private void usageExport() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useExport
				+ "\n");
	}

	private void usageView() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useView
				+ "\n");
	}

	private void usageDelete() {
		System.out.print("Usage:\n" + "java -jar <systregjar>\n" + useDelete
				+ "\n");
	}

	/**
	 * The main entry point to the System Registration API. The argument list
	 * follows the following usage:<br>
	 * <br>
	 * [-verify system-registration-file]<br>
	 * [-save system-registration-file system-registration-config-file]<br>
	 * [-publish system-registration-file system-registration-config-file]<br>
	 * [-export [-system system-name] system-registration-file
	 * system-registration-config-file]<br>
	 * [-view system-registration-file system-registration-config-file]<br>
	 * [-diff system-registration-file system-registration-config-file]<br>
	 * [-delete system-name system-registration-config-file]<br>
	 * [-notify system-registration-config-file]<br>
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length > 0) {
			sysreg = new SystemRegistration();
			sysreg.initLogging();
			if (args[0].equals("-verify")) {
				if (sysreg.verify(args) == Status.Success) {
					logger
							.info("The system registration file was successfully verified.");
				} else {
					logger
							.fatal("The system registration file was not verified.");
				}
			} else if (args[0].equals("-save")) {
				if (sysreg.save(args) == Status.Success) {
					logger
							.info("The system registration file was successfully saved.");
				} else {
					logger.fatal("Failed to save system registration file.");
				}
			} else if (args[0].equals("-export")) {
				if (sysreg.export(args) == Status.Success) {
					logger.info("The registry was successfully exported.");
				} else {
					logger.fatal("Failed to export registry.");
				}
			} else if (args[0].equals("-view")) {
				if (sysreg.view(args) == Status.Success) {
					logger.info("The registry was successfully displayed.");
				} else {
					logger.fatal("Failed to display registry.");
				}
			}  else if (args[0].equals("-delete")) {
				if (sysreg.delete(args) == Status.Success) {
					logger
							.info("The system was successfully deleted from the registry.");
				} else {
					logger.fatal("Failed to delete system from the registry.");
				}
			} else {
				System.out.println("Unknown command: " + args[0]);
				sysreg.usage();
			}
		}
	}

	/* Help methods */

	/**
	 * Checks that the contents of a system registration file is correct.
	 * 
	 * @param ir
	 *            The intermediate representation
	 * @return Success if the input file is correct.
	 */
	private Status irVerify(final Ir ir) {
		boolean result = true;
		result = ir.verify(ir);
		if (result) {
			return Status.Success;
		} else {
			return Status.Fail;
		}
	}

	/**
	 * This method takes the contents of an Intermediate Representation (IR) and
	 * writes it to a file.
	 * 
	 * @param filePath
	 *            The target file
	 * @param ir
	 *            The intermediate representation
	 * @param systemName
	 *            System name or SystemRegistration.all
	 * @return Success if the target file was produced correctly.
	 */
	public Status srfWrite(final String filePath, final Ir ir, final String systemName) {

		ir.sortSystems();
		Status result = Status.Fail;

		File backup = null;
		try {
			backup = backup(filePath);

			final Writer fileWriter = new FileWriter(filePath); /*
															 * Constructs a
															 * FileWriter object
															 * given a file
															 * name.
															 */
			result = ir.srWrite(fileWriter, systemName, srDTDPath);
			fileWriter.close();
			
		} catch (final IOException e) {
			logger.fatal("Failed to write: " + filePath);
		} finally {
			if (restore(filePath, backup, result) == Status.Fail) {
				logger.warn("Failed to restore backup");
			}
		}
		return result;
	}

	public Ir srfRead(final String srFile) {
		try {
			final SRFContentHandler handler = new SRFContentHandler();
			final SRFErrorHandler errorHandler = new SRFErrorHandler();
			srfRead(srFile, handler, errorHandler);
			if (errorHandler.getStatus() == Status.Fail) {
				logger.fatal("Failed to parse System Registration File: "
						+ srFile);
				return null;
			} else {
				logger.debug("Parsed system registration file.");
			}
			return handler.getResult();
		} catch (final java.io.IOException e) {
			logger.debug("IOException " + e.getCause());
			logger.fatal("Failed to parse System Registration File ("
					+ e.getMessage() + "): " + srFile);
		} catch (final SAXException e) {
			logger.debug("SAXException" + e.getCause());
			logger.fatal("Failed to parse System Registration File ("
					+ e.getMessage() + "): " + srFile);
		}
		return null;
	}

	/**
	 * This method takes the System Registration File, srFile, and creates an
	 * Intermediate Representattion (IR) of the srFile. The srFile is parsed
	 * using a SAX parser and the result is stored in the IR.
	 * 
	 * @param srFile
	 *            The system registration file
	 * @return An intermediate representation of the system registration file.
	 */
	public void srfRead(final String srFile, final AbstractSRFContentHandler contentHandler,
			final ErrorHandler errorHandler) throws IOException, SAXException {
		final File f = new File(srFile);
		if (!f.exists()) {
			logger.fatal("System registration file does not exist.");
			throw new IOException("System registration file does not exist.");
		}
		final FileInputStream in = new FileInputStream(srFile);
		final InputSource source = new InputSource(in);

		final XMLReader srfReader = XMLReaderFactory.createXMLReader();
		srfReader.setContentHandler(contentHandler);

		srfReader.setErrorHandler(errorHandler);
		srfReader.setFeature("http://xml.org/sax/features/validation", true);

		srfReader.parse(source);
	}

	/**
	 * Make a backup of the given file path and return the File object. The
	 * given file is removed.
	 * 
	 * @param filePath
	 *            The file to backup
	 * @return The backup file object.
	 */
	public File backup(final String filePath) {
		File backup = null;
		try {
			final File original = new File(filePath);
			if ((original != null) && original.exists()) {
				backup = new File(original.getAbsolutePath() + ".bak");
				backup.delete();
				if (!original.renameTo(backup)) {
					logger.warn("Failed to create backup file for: "
							+ filePath);
					logger.debug("Not allowed to rename file: " + filePath
							+ " to:" + backup.getCanonicalPath());
					backup = null; // invalidate backup.
				} else {
					backupExists = true;
					logger.debug("Created backup file for: " + filePath);
				}
			} else {
				backupExists = false;
				logger.debug("No backup created, original file does not exist.");
			}
		} catch (final IOException e) {
			logger.warn("Failed to create backup for: " + filePath);
			logger.debug("caused by: ", e);
		}
		return backup;
	}

	/**
	 * Restore a backup depending on the given status. If status is Fail the
	 * backup is restored, otherwise the backup is deleted.
	 * 
	 * @param originalPath
	 *            The destination of the restore.
	 * @param backup
	 *            The backup file object.
	 * @param status
	 *            The status directing the restore.
	 * @return Success if restore executed correctly.
	 */
	public Status restore(final String originalPath, final File backup, final Status status) {
		Status result = Status.Fail;
		if (!backupExists) {
			return Status.Success;
		}
		try {
			if ((backup != null) && backup.exists()) {
				if (status == Status.Success) {
					backup.delete();
					result = Status.Success;
				} else {
					logger.info("Recovering backup file.");
					final File original = new File(originalPath);
					if ((original != null) && original.exists()) {
						// remove corrupt file...
						original.delete();
					}
					if (!backup.renameTo(original)) {
						logger.fatal("Failed to recover backup file: "
								+ backup.getCanonicalPath());
					} else {
						result = Status.Success;
					}
				}
			} else {
				if (status == Status.Success) {
					result = Status.Success;
				} else {
					logger.warn("No backup exists.");
				}
			}
		} catch (final Exception e) {
			logger.warn("Failed to restore backup for: " + originalPath);
			logger.debug("caused by: ", e);
		}
		return result;
	}

	/**
	 * Prints the whole contents of the intermediate representation (IR )or for
	 * a specific system in a human readable format.
	 * 
	 * @param ir
	 *            The intermediate representation of the System Topology
	 * @param systemName
	 *            System name or SystemRegistration.all
	 * @return Success if the printout was correct
	 */
	private Status hrWrite(final Ir ir, final String systemName) {

		// Find the system in the ir.
		final Status result = ir.srHrWrite(systemName);

		return result;
	}

	public Status srfDelete(final String systemName, final Ir ir) {
		if (ir == null) {
			logger
					.fatal("Failed to delete system, the registry contains no data.");
			return Status.Fail;
		}
		return ir.deleteSystem(systemName);
	}

	/**
	 * Read the timestamp of the system registry file using class File,
	 * lastModified, type date.
	 * 
	 * @param srProperties
	 *            System Registration property file path.
	 * @return A long value representing the time the file was last modified,
	 *         measured in milliseconds since the epoch (00:00:00 GMT, January
	 *         1, 1970),or 0L if the file does not exist or if an I/O error
	 *         occurs
	 */
	public long getTimestamp(final String srProperties) {

		long result = 0;
		if (processCfg(srProperties) == Status.Fail) {
			logger
					.fatal("Failed to get timestamp of repository, cannot read configuration file"
							+ srProperties);
		} else {
			final File f = new File(repositoryPath);

			if (f.exists() && f.isFile() && f.canRead()) {
				result = f.lastModified();
				logger.debug("Repository: " + repositoryPath
						+ " last modified: " + (new Date(result)).toString());
			} else {
				logger
						.fatal("Failed to get timestamp of repository, could not find repository: "
								+ repositoryPath);
				result = 0;
			}
		}
		return result;
	}

	/**
	 * Get the operations for the given resource and IP.
	 * 
	 * @param resource
	 *            The resource ID.
	 * @param ipAddress
	 *            The IP address
	 * @param srProperties
	 *            The system registration property file path.
	 * @return A vector containing a Property object for each operation found.
	 *         The attributes from the SNOSOP tag in the system registration
	 *         file is saved as properties with the attribute name as key and
	 *         attribute value as value.
	 */
	public List<Properties> getOperations(final String resource, final String ipAddress,
			final String srProperties) {
		final Vector<Properties> result = new Vector<Properties>();

		if (processCfg(srProperties) == Status.Fail) {
			logger
					.fatal("Failed to get operations, cannot read configuration file"
							+ srProperties);
		} else {
			logger.debug("Repository path is: " + repositoryPath);
		}

		logger.debug("Getting operations for resource: " + resource + " ip: "
				+ ipAddress + " from repository: " + repositoryPath);

		final Ir ir = srfRead(repositoryPath);
		if (ir == null) {
			logger.warn("Repository is empty");
		} else {
			if (ir.getOperations(resource, ipAddress, result) == Status.Fail) {
				logger.fatal("Failed to get opertations for resource: "
						+ resource + " ip: " + ipAddress + " from repository: "
						+ repositoryPath);
				result.clear();
			}
		}
		logger.debug("Found " + result.size() + " operations.");
		return result;
	}

	/**
	 * @param srDTDPath
	 *            the srDTDPath to set
	 */
	public void setSrDTDPath(final String srDTDPath) {
		this.srDTDPath = srDTDPath;
	}

};
