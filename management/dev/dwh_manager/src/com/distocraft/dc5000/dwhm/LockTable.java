package com.distocraft.dc5000.dwhm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.repository.dwhrep.Dwhpartition;
import com.distocraft.dc5000.repository.dwhrep.DwhpartitionFactory;
import com.distocraft.dc5000.repository.dwhrep.Dwhtype;
import com.distocraft.dc5000.repository.dwhrep.DwhtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;

import ssc.rockfactory.RockFactory;

//Locks all the partitions of the Techpack.

public class LockTable {

	final private Logger log;

	private final RockFactory dwhrock;

	private final RockFactory reprock;

	private static final String ACTIVE = "ACTIVE";
	

	public LockTable(final RockFactory reprock, final RockFactory dwhrock, final Logger clog) throws SQLException {
		this.reprock = reprock;
		this.dwhrock = dwhrock;
		log = Logger.getLogger(clog.getName() + ".dwhm.LockTable");
	}

	public void lockPartition(String techPack) throws Exception {
		long start = System.currentTimeMillis();
		log.info("Locking Partitions started at " + start);
		final List<Dwhpartition> partitionList = getDWHPartitions(techPack);
		log.finest("There are " + partitionList.size() + "available for "+ techPack);
		final Iterator<Dwhpartition> iter = partitionList.iterator();
		final Connection con = this.dwhrock.getConnection();
		while (iter.hasNext()) {
		Statement stmnt = con.createStatement();
		String lockSql = "LOCK TABLE " + iter.next().getTablename() + " WITH HOLD IN EXCLUSIVE MODE WAIT '00:05:00';";
		log.finest("The SQL to Lock the table is " + lockSql);
		ResultSet res = null;
		try {
			res = stmnt.executeQuery(lockSql);
		} catch (SQLException esc) {
			esc.getMessage();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				else {
					log.fine("Res is null" );
				}
				if (stmnt != null) {
					stmnt.close();
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Error closing statement", e.getMessage());
			}
	}
	}
		long end = System.currentTimeMillis();
		log.info("Locked all partitions at  " + end);
	}
//Provides the list of partitions for the given Techpack
	private List<Dwhpartition> getDWHPartitions(String techPack) throws Exception {

		final List<Dwhpartition> result = new ArrayList<Dwhpartition>();

		try {

			// Tpactivation
			final Tpactivation tpActivation = new Tpactivation(reprock);
			tpActivation.setStatus(ACTIVE);
			tpActivation.setTechpack_name(techPack);

			final TpactivationFactory tpActivationFactory = new TpactivationFactory(reprock, tpActivation);

			if (tpActivationFactory != null) {

				for (Tpactivation tpaftmp : tpActivationFactory.get()) {

					try {

						// Dwhtype
						final Dwhtype dtp = new Dwhtype(reprock);
						dtp.setTechpack_name(tpaftmp.getTechpack_name());
						final DwhtypeFactory dwhTypeFactory = new DwhtypeFactory(reprock, dtp);

						if (dwhTypeFactory != null) {
							Iterator<Dwhtype> inneriter;

							inneriter = dwhTypeFactory.get().iterator();

							while (inneriter.hasNext()) {

								try {

									final Dwhtype dwhType = inneriter.next();

									// DWHPartitions
									final Dwhpartition dwhPartition = new Dwhpartition(reprock);
									dwhPartition.setStorageid(dwhType.getStorageid());
									final DwhpartitionFactory dwhPartitionFactory = new DwhpartitionFactory(reprock, dwhPartition);

									if (dwhPartitionFactory != null) {

										// loop DWHPartitions
										for (Dwhpartition dwhPArtitionTmp : dwhPartitionFactory.get()) {

											try {

												// collect datanames
												result.add(dwhPArtitionTmp);

											} catch (Exception e) {
												log.log(Level.WARNING, "Error while iterating Dwhpartition", e);
												throw (e);
											}
										}
									}

								} catch (Exception e) {
									log.log(Level.WARNING, "Error while iterating Dwhtype", e);
									throw (e);
								}
							}
						}

					} catch (Exception e) {
						log.log(Level.WARNING, "Error while iterating Tpactivation table", e);
						throw (e);
					}
				}
			}

		} catch (Exception e) {
			log.log(Level.WARNING, "Error while retrieving Versioning table", e);
			throw (e);
		}

		return result;

	}
}
