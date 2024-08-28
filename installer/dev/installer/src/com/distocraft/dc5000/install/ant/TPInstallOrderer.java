package com.distocraft.dc5000.install.ant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class TPInstallOrderer extends Task {

	private static String techpackDirectory = null;

	private String installListFile = null;

	private String debug = null;

	private File tpDir = null;

	private boolean checkForRequiredTechPacks = true;

	List<String> combinedList = new ArrayList<String>();

	List<String> tps_list = new ArrayList<String>();

	public void execute() throws BuildException {
		try {

			if (techpackDirectory == null) {
				throw new BuildException(
						"parameter techpackDirectory has to be defined");
			}

			if (installListFile == null) {
				throw new BuildException(
						"parameter installListFile has to be defined");
			}

			tpDir = new File(techpackDirectory);

			if (!tpDir.exists() || !tpDir.canRead()) {
				throw new BuildException("Unable to read techpackDirectory");
			}

			final File listFile = new File(installListFile);

			if (!listFile.exists() || !listFile.canRead()) {
				throw new BuildException("Unable to read installListFile");
			}

			final List<TPEntry> thelist = new ArrayList<TPEntry>();

			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(listFile));

				String line = null;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					final TPEntry tpe = loadTPE(line);

					if (tpe != null) {
						if (!this.checkForRequiredTechPacks) {
							System.out
									.println("Not checking required tech packs of tech pack "
											+ tpe.name);
							tpe.deps = new String[0];
						}
						thelist.add(tpe);
					}

					// else
					// throw new BuildException("Techpack " + line +
					// " not found");
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Error reading installListFile", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
			}

			if (debug != null) {
				System.out.println("First read done. " + thelist.size());
				printList(thelist);
			}

			fillRequirements(thelist);

			if (debug != null) {
				System.out.println("Fill requirements done. " + thelist.size());
				printList(thelist);
			}

			findOrder(thelist);

			if (debug != null) {
				System.out.println("Ordering done. Final list. "
						+ thelist.size());
				printList(thelist);
			}

			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(listFile));

				Iterator<String> i = tps_list.iterator();

				while (i.hasNext()) {
					out.println(i.next());
				}

				out.flush();

			} catch (Exception e) {
				throw new BuildException("Error writing installListFile", e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
					}
				}
			}

		} catch (BuildException be) {
			throw be;
		} catch (Exception e) {
			throw new BuildException("Unexpected failure", e);
		}

	}

	public void removeDuplicates(List<String> myList) {
		HashSet<String> set = new HashSet<String>(myList);
		myList.clear();
		myList.addAll(set);
	}

	private void fillRequirements(final List<TPEntry> list) throws Exception {
		for (int i = 0; i < list.size(); i++) {
			final TPEntry tpe = list.get(i);

			for (int j = 0; j < tpe.deps.length; j++) {
				if (!isInList(tpe.deps[j], list)) {
					final TPEntry dtpe = loadTPE(tpe.deps[j]);
					if (dtpe != null) {
						list.add(dtpe);
					}
					// else {
					// throw new BuildException("Dependency of " + tpe.name +
					// " failed. Techpack " + tpe.deps[j] + " not found");
					// }
				}
			}
		}
	}

	private void findOrder(final List<TPEntry> list) throws Exception {

		List<String> initial_list = new ArrayList<String>();

		Iterator<TPEntry> it1 = list.iterator();
		while (it1.hasNext()) {
			TPEntry tp1 = it1.next();
			combinedList.add(tp1.name);
			initial_list.add(tp1.name);
			for (int jj = 0; jj < tp1.deps.length; jj++) {
				combinedList.add(tp1.deps[jj]);
			}
		}

		removeDuplicates(combinedList);

		int position_dep;
		int position_tp;
		int loop_count = 0;

		while (loop_count < 100) {

			for (int i = 0; i < list.size(); i++) {
				TPEntry tp2 = list.get(i);

				for (int x = 0; x < tp2.deps.length; x++) {
					position_dep = combinedList.indexOf(tp2.deps[x]);
					position_tp = combinedList.indexOf(tp2.name);
					if (position_dep > position_tp) {
						combinedList.remove(position_tp);
						combinedList.add(position_dep, tp2.name);
					}
				}
			}
			++loop_count;
		}

		combinedList.retainAll(initial_list);

		for (String tpi_name : combinedList) {
			for (int y = 0; y < list.size(); y++) {
				TPEntry tp0 = list.get(y);
				if (tp0.name.equals(tpi_name)) {
					tps_list.add(tp0.filename);
				}
			}
		}
	}

	private void printList(final List<TPEntry> list) {
		int ix = 0;
		final Iterator<TPEntry> i = list.iterator();

		while (i.hasNext()) {
			final TPEntry tpe = i.next();
			String ou = "  " + (ix++) + " " + tpe.name + " -< [ ";
			for (int j = 0; j < tpe.deps.length; j++) {
				ou += tpe.deps[j] + " ";
			}
			ou += "]";
			System.out.println(ou);
		}
	}

	private boolean isInList(final String name, final List<TPEntry> list) {
		Iterator<TPEntry> i = list.iterator();
		while (i.hasNext()) {
			final TPEntry tpe = i.next();

			if (tpe.name.equals(name)) {
				return true;
			}
		}

		return false;
	}

	private TPEntry loadTPE(final String name) throws Exception {

		final File[] files = tpDir.listFiles(new FileFilter() {

			public boolean accept(final File cand) {
				if (cand.getName().endsWith(".tpi") && cand.isFile()
						&& cand.canRead()) {

					final String[] nameParts = cand.getName().split("_");

					// Name of the tpi file is for example
					// DC_Z_ALARM_R1A_b55.tpi

					if (nameParts.length < 3) {
						// Not a tpi with a valid name.
						return false;
					}

					String techPackName = "";

					// Iterate through the parts to drop the last two
					// "_"-characters.
					for (int i = 0; i < (nameParts.length - 2); i++) {
						techPackName += nameParts[i] + "_";
					}

					// Now the name should contain DC_Z_ALARM_
					// Drop the last "_" from the name to get the name of the
					// tech pack.
					// For example DC_Z_ALARM

					techPackName = techPackName.substring(0,
							(techPackName.length() - 1));

					if (techPackName.equalsIgnoreCase(name)) {
						return true;
					} else {
						return false;
					}

				} else {
					return false;
				}

			}
		});

		if (files.length < 1) {
			return null; // No such TP
		} else if (files.length > 1) {
			throw new BuildException("Multiple techpacks with name " + name
					+ " in techpack directory");
		}

		final ZipFile zf = new ZipFile(files[0]);

		final Enumeration entries = zf.entries();

		while (entries.hasMoreElements()) {
			final ZipEntry ze = (ZipEntry) entries.nextElement();

			if (ze.getName().endsWith("version.properties")) {
				final TPEntry tpe = new TPEntry();
				tpe.filename = files[0].getName();

				final Properties p = new Properties();
				p.load(zf.getInputStream(ze));

				// Check if the properties file is encrypted or not!
				if (p.containsKey("tech_pack.name") == false) {
					// Could not found tech_pack.name property. This techpack
					// must be
					// encrypted.
					// TODO: Check for the license...
					System.out
							.println(files[0].getName()
									+ " is encrypted. Trying to decrypt version.properties file...");

					// create a temporary byte array stream
					// ByteArrayOutputStream bos = new ByteArrayOutputStream();

					// TODO: Maybe do some CRC32 checking later?
					// CRC32 crc = new CRC32();

					// read and buffer the input.
					// while ((in = zis.read()) != -1) {
					// bos.write(in);
					// }

					final InputStream cryptIn = zf.getInputStream(ze);

					final ZipCrypter crypter = new ZipCrypter();
					final Key rsaKey = ZipCrypter.getPublicKey(
							ZipCrypter.DEFAULT_KEY_MOD,
							ZipCrypter.DEFAULT_KEY_EXP);

					final byte[] extra = ze.getExtra();

					// System.out.println("ze.getExtra() = " + new
					// String(extra));
					// System.out.println("rsaKey = " + rsaKey.toString());

					// get the aesKey from the RSA encrypted metadata.

					final Key aesKey = crypter.decryptAESKey(ze.getExtra(),
							rsaKey);

					// System.out.println("aesKey = " + aesKey.toString());

					final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					final AESCrypter aesCrypt = new AESCrypter();
					aesCrypt.decrypt(cryptIn, bos, aesKey);
					final byte[] aesOutput = bos.toByteArray();

					// System.out.println("DEBUG: ze.getSize(): " +
					// ze.getSize());

					final InputStream versionPropInStream = new ByteArrayInputStream(
							aesOutput);

					// System.out.println("versionPropInStream.toString() = " +
					// versionPropInStream.toString());
					p.load(versionPropInStream);

					// System.out.println("p = " + p.toString());

					if (p.containsKey("tech_pack.name") == false) {
						// Even the decrypted techpack is a mess. Bail out...
						throw new BuildException(
								"Techpack "
										+ files[0].getName()
										+ " is errorneous or does not have required name of the techpack. Installation is aborted.");
					}

				} else {
					System.out.println(files[0].getName()
							+ " is not encrypted.");
				}

				// System.out.println("name = " + name);
				// System.out.println("p.getProperty(tech_pack.name) = " +
				// p.getProperty("tech_pack.name"));

				if (!name.equals(p.getProperty("tech_pack.name"))) {
					throw new BuildException("Techpack " + files[0].getName()
							+ " is errorneous (name)");
				}

				tpe.name = p.getProperty("tech_pack.name");

				final List<String> reqs = new ArrayList<String>();

				final Enumeration keys = p.keys();
				while (keys.hasMoreElements()) {
					final String key = (String) keys.nextElement();

					if (key.startsWith("required_tech_packs.")) {
						reqs.add(key.substring(key.indexOf(".") + 1));
					}
				}

				tpe.deps = (String[]) reqs.toArray(new String[reqs.size()]);

				return tpe;
			}

		}

		return null;
	}

	public void setTechpackDirectory(final String dir) {
		techpackDirectory = dir;
	}

	public static String getTechpackDirectory() {
		return techpackDirectory;
	}

	public void setInstallListFile(final String file) {
		installListFile = file;
	}

	public String getInstallListFile() {
		return installListFile;
	}

	public void setDebug(final String deb) {
		debug = deb;
	}

	public String getDebug() {
		return debug;
	}

	public class TPEntry {

		public String name;

		public String[] deps;

		public String filename;

	};

	public class NonException extends Exception {
	};

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final TPInstallOrderer tpio = new TPInstallOrderer();
		tpio.setTechpackDirectory(args[0]);
		tpio.setInstallListFile(args[1]);
		tpio.execute();

	}

	public String getCheckForRequiredTechPacks() {
		return String.valueOf(checkForRequiredTechPacks);
	}

	public void setCheckForRequiredTechPacks(
			final String checkForRequiredTechPacks) {
		this.checkForRequiredTechPacks = Boolean
				.parseBoolean(checkForRequiredTechPacks);
	}

}
