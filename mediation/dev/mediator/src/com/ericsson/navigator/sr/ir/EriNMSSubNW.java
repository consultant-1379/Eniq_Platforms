package com.ericsson.navigator.sr.ir;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ericsson.navigator.sr.SystemRegistration;
import com.ericsson.navigator.sr.util.Status;

public class EriNMSSubNW implements IrNode, SNOSNEParent, IrNodeModify { //NOPMD
	private static final Logger logger = Logger
			.getLogger("System Registration");
	private final List<SNOSNE> snosne = new LinkedList<SNOSNE>();
	private String cimname;

	/**
	 * @return the snosne
	 */
	public List<SNOSNE> getSnosne() {
		return snosne;
	}

	public void setCimname(final String cimname) {
		this.cimname = cimname;
	}

	public boolean verify(final Ir ir) {
		boolean result = true;
		if (!cimname.equals("Service Network")) {
			logger.warn("In class EriNMSSubNW, the attribute cimname=" + "\""
					+ cimname + "\"" + ". It should be cimname=" + "\""
					+ "Service Network" + "\"");
			result = true;
		}
		final Iterator<SNOSNE> i = snosne.iterator();
		while (result && i.hasNext()) {
			result = i.next().verify(ir);
		}
		return result;
	}

	public Status srHrWrite(final String systemName, final int position) {
		Status result = Status.Success;
		System.out.println();
		System.out.println(cimname);
		System.out.println();

		if (systemName.equals(SystemRegistration.all)) {
			final Iterator<SNOSNE> i = snosne.iterator();
			while (i.hasNext() && (Status.Success == result)) {
				result = i.next().srHrWrite(systemName, position + 3);
			}
		} else {
			boolean found = false;
			final Iterator<SNOSNE> j = snosne.iterator();
			SNOSNE k = null;
			while (j.hasNext() && (Status.Success == result)) {
				k = j.next();
				if (k.getCimname().equals(systemName)) {
					found = true;
					result = k.srHrWrite(systemName, position + 3);
				}
			}
			if (!found) {
				result = Status.Fail;
			}
		}

		return result;
	}

	/**
	 * This method writes the XML element handled by this class to a file
	 * writer. It starts to write in the column given by input parameter
	 * position.
	 * 
	 * @param fileWriter
	 *            A file writer for a System Registration/System Topology File.
	 * @param position
	 *            The position where to start to write.
	 * @param systemName
	 *            Used to find the system with the cimname given by systemName.
	 * @param srDTDPath
	 *            Path to the System Registration DTD.
	 * @return Success if the file was saved successfully.
	 */
	public Status srWrite(final Writer fileWriter, final int position, final String systemName, final String srDTDPath) { //NOPMD
		Status result = Status.Success;
		String indent = "";
		for (int i = 0; i < position; i++) {
			indent += " ";
		}
		try {
			fileWriter.write(indent + "<EriNMSSubNW cimname=\"" + cimname
					+ "\"" + ">\n");

			if (systemName.equals(SystemRegistration.all)) {
				final Iterator<SNOSNE> i = snosne.iterator();
				while (i.hasNext() && (Status.Success == result)) {
					result = i.next().srWrite(fileWriter, position + 3,
							systemName, srDTDPath);
				}
			} else {
				boolean found = false;
				final Iterator<SNOSNE> j = snosne.iterator();
				SNOSNE k = null;
				while (j.hasNext() && (Status.Success == result)) {
					k = j.next();
					if (k.getCimname().equals(systemName)) {
						found = true;
						result = k.srWrite(fileWriter, position + 3,
								systemName, srDTDPath);
					}
				}
				if (!found) {
					result = Status.Fail;
				}
			}
			fileWriter.write(indent + "</EriNMSSubNW>\n");
		} catch (final IOException e) {
			logger.fatal("Failed to write system registration file. Reason: "
					+ e.getMessage());
			logger.debug("Caused by: ", e);
			result = Status.Fail;
		}
		return result;
	}

	/**
	 * @return returns the linked list containing the systems (SNOSNE).
	 */
	public List<SNOSNE> getSystems() {
		return snosne;
	}

	/**
	 * This method compares the systems in the received list, systems, with the
	 * systems in the present System Topology. If the received list contains a
	 * system that is already existing in the present System Topoplogy, then
	 * replace it. Otherwise add the system.
	 * 
	 * @param A
	 *            linked list of systems (SNOSNE) to be added to the present
	 *            System Topology
	 * @return Returns always success.
	 */
	public Status addSystems(final List<SNOSNE> systems) {
		boolean found = false;

		final Iterator<SNOSNE> i = systems.iterator();
		Iterator<SNOSNE> j;
		String tempNewSystem;
		String tempSystem;
		SNOSNE tempSystemRef = null;
		SNOSNE tempNewSystemRef = null;

		while (i.hasNext()) {
			found = false;
			tempNewSystemRef = i.next();
			tempNewSystem = tempNewSystemRef.getCimname();
			j = snosne.iterator();
			while (j.hasNext() && (!found)) {
				tempSystemRef = j.next();
				tempSystem = tempSystemRef.getCimname();
				if (tempNewSystem.equals(tempSystem)) {
					found = true;
					// Replace the present system with the new one.
					snosne.remove(tempSystemRef);
					snosne.add(tempNewSystemRef);
				}
			}
			if (!found) {
				// System not found. Add the system.
				snosne.add(tempNewSystemRef);
			}
		}
		return Status.Success; // Returns always success.
	}

	/**
	 * This method sorts the systems included in the list snosne in alphabetical
	 * order.
	 */
	public void sortSystems() {
		Collections.sort(snosne);
	}

	public boolean systemExists(final String systemName) {
		boolean found = false;
		Iterator<SNOSNE> i;
		i = snosne.iterator();
		while (i.hasNext() && !found) {
			if (i.next().getCimname().equals(systemName)) {
				found = true;
			}
		}
		return found;
	}

	public Status deleteSystem(final String systemName) {
		final Iterator<SNOSNE> i = snosne.iterator();
		logger.debug("Deleting system: " + systemName);
		while (i.hasNext()) {
			final SNOSNE system = i.next();
			logger.debug("system name is: " + system.getCimname());
			if (system.getCimname().equals(systemName)) {
				logger.debug("Deleting: " + system.getCimname());
				i.remove();
				return Status.Success;
			}
		}
		logger.debug("Not found.");
		return Status.Fail;
	}

	public Status getOperations(final String resource, final String ipAddress,
			final List<Properties> result) {
		Status status = Status.Fail;
		final Iterator<SNOSNE> i = snosne.iterator();
		while ((status == Status.Fail) && i.hasNext()) {
			status = i.next().getOperations(resource, ipAddress, result);
		}
		return status;
	}
}
