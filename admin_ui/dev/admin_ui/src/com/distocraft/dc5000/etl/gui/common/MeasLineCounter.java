package com.distocraft.dc5000.etl.gui.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeasLineCounter {
	private Connection repCon;
	private Connection iqCon;
	private final Log log = LogFactory.getLog(super.getClass());

	public MeasLineCounter(Connection iqCon, Connection repCon) {
		try {
			this.repCon = repCon;
			this.iqCon = iqCon;
			this.log.info("DataRowSummary :: DB connections established.");
		} catch (Exception e) {
			this.log.error("DataRowSummary :: Could not get connections: ", e);
		}
	}

	public ResultSet getMeasTypes(String likeStr, String level) throws SQLException {
		PreparedStatement statement = null;
		ResultSet result = null;
		String sql = null;
		try {
			if ((likeStr == null) || (likeStr.length() < 1)) {
				sql = "SELECT typename FROM TypeActivation WHERE tablelevel='" + level
						+ "' AND status like '%ACTIVE%' ORDER BY typename";
				this.log.debug(sql);
				statement = this.repCon.prepareStatement(sql);
			} else {
				sql = "SELECT typename FROM TypeActivation WHERE tablelevel='" + level
						+ "' AND status like '%ACTIVE%' AND techpack_name = '" + likeStr + "' ORDER BY typename";
				this.log.debug(sql);
				statement = this.repCon.prepareStatement(sql);
			}
			result = statement.executeQuery();
		} catch (SQLException sqle) {

			this.log.error("DataRowSummary :: Could not get the storageinfo row count: " + sqle);
		}
		return result;
	}

	// Modified for EQEV-12110,15029
	public MeasResultSet getMeasLines(int year, int month, int day, int dayCount, String measType, String level,
			boolean searchBackward, List<String> measurementType) throws SQLException {
		PreparedStatement statement = null;
		MeasResultSet measResult = new MeasResultSet(dayCount, measType);
		HashMap<String, Integer> rowcountvalue = new HashMap<String, Integer>();
		HashMap<String, Integer> numberOfRop = new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Integer>> mapRow = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, HashMap<String, Integer>> mapRop = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, ArrayList<Integer>> mapvalues = new HashMap<String, ArrayList<Integer>>();
		ResultSet result = null;
		String sql = null;
		String mlevel = level;
		List<String> datelist = new ArrayList<String>();
		datelist = calculateDate(year, month, day, dayCount, searchBackward);

		try {
			if (level.equalsIgnoreCase("RAW")) {
				sql = "SELECT TYPENAME, DATEFORMAT(datatime,'yyyy-mm-dd') AS  DATETIME,SUM(ROWCOUNT) AS ROWCOUNT ,COUNT(DISTINCT DATATIME) AS ROPCOUNT FROM LOG_SESSION_LOADER WHERE TYPENAME IN ("
						+ measType + ") AND DATE_ID BETWEEN '" + datelist.get(0) + "' and '"
						+ datelist.get(dayCount - 1) + "' GROUP BY TYPENAME,DATATIME";
			} else if (level.equalsIgnoreCase("DAYBH")) {
				sql = "SELECT TYPENAME, DATEFORMAT(datatime,'yyyy-mm-dd') AS  DATETIME,SUM(ROWCOUNT) AS ROWCOUNT ,COUNT(DISTINCT DATE_ID) AS ROPCOUNT FROM LOG_SESSION_AGGREGATOR WHERE TYPENAME IN ("
						+ measType + ") AND DATE_ID BETWEEN '" + datelist.get(0) + "' AND '"
						+ datelist.get(dayCount - 1) + "' AND  TIMELEVEL = '" + mlevel
						+ "' GROUP BY TYPENAME , DATETIME";
			} else {
				sql = "SELECT TYPENAME, DATEFORMAT(datatime,'yyyy-mm-dd') AS  DATETIME, SUM(ROWCOUNT) AS ROWCOUNT ,COUNT(DISTINCT DATE_ID) AS ROPCOUNT FROM LOG_SESSION_AGGREGATOR WHERE TYPENAME IN ("
						+ measType + ") AND DATE_ID BETWEEN '" + datelist.get(0) + "' and '"
						+ datelist.get(dayCount - 1) + "' AND SESSIONENDTIME=(SELECT MAX(SESSIONENDTIME) from LOG_SESSION_AGGREGATOR) AND  TIMELEVEL = '" + mlevel
						+ "' GROUP BY TYPENAME , DATETIME";
			}
			this.log.debug(sql);
			if (statement != null) {
				statement.close();
			}

			statement = this.iqCon.prepareStatement(sql);
			result = statement.executeQuery();

			while (result.next()) {
				for (int j = 0; j < datelist.size(); j++) {
					String date = datelist.get(j);
					if ((result.getString("DATETIME")).equalsIgnoreCase(date)) {
						if (result.getString("ROWCOUNT") != null) {
							if (!rowcountvalue.containsKey(date)) {
								rowcountvalue.put(date, result.getInt("ROWCOUNT"));
							} else {
								int valrow = rowcountvalue.get(date);

								
								valrow = valrow + result.getInt("ROWCOUNT");

								rowcountvalue.put(date, valrow);

							}

						} else {
							rowcountvalue.put(date, 0);
						}
						mapRow.put(result.getString("TYPENAME"), convertMap(rowcountvalue));
						if (result.getString("ROPCOUNT") != null) {
							if (!numberOfRop.containsKey(date)) {
								numberOfRop.put(date, result.getInt("ROPCOUNT"));
							} else {
								int valrop = numberOfRop.get(date);

								valrop = valrop + result.getInt("ROPCOUNT");

								numberOfRop.put(date, valrop);

							}

						} else {
							numberOfRop.put(date, 0);
						}
						mapRop.put(result.getString("TYPENAME"), convertMap(numberOfRop));
					} else {
						if (mapRow.containsKey(result.getString("TYPENAME"))) {
							if (!(mapRow.get(result.getString("TYPENAME")).containsKey(date))) {
								rowcountvalue.put(date, 0);
								mapRow.put(result.getString("TYPENAME"), convertMap(rowcountvalue));
							}
							if (!(mapRop.get(result.getString("TYPENAME")).containsKey(date))) {
								numberOfRop.put(date, 0);
								mapRop.put(result.getString("TYPENAME"), convertMap(numberOfRop));
							}

						} else {
							rowcountvalue.put(date, 0);
							mapRow.put(result.getString(1), convertMap(rowcountvalue));
							numberOfRop.put(date, 0);
							mapRop.put(result.getString(1), convertMap(numberOfRop));
						}
					}
				}
			}

			// If measurementType doesnot exist, 0 has to be displayed for each
			// type
			if (!result.next()) {
				this.log.debug("Result size doesnt exists");
				for (int i = 0; i < measurementType.size(); i++) {
					if (!mapRow.containsKey(measurementType.get(i))) {
						for (int j = 0; j < datelist.size(); j++) {

							rowcountvalue.put(datelist.get(j), 0);
							numberOfRop.put(datelist.get(j), 0);

							mapRow.put(measurementType.get(i), convertMap(rowcountvalue));
							mapRop.put(measurementType.get(i), convertMap(numberOfRop));
						}

					}
				}
			}

			/** iterating the map and adding the values to the mapValues **/
			for (String currentKey : mapRow.keySet()) {
				if ((mapRow.containsKey(currentKey)) && (mapRop.containsKey(currentKey))) {
					HashMap<String, Integer> rowcount = new HashMap<String, Integer>();
					HashMap<String, Integer> ropcount = new HashMap<String, Integer>();
					rowcount = mapRow.get(currentKey);
					ropcount = mapRop.get(currentKey);
					ArrayList<Integer> rowCountlist = new ArrayList<Integer>();
					ArrayList<Integer> ropCountlist = new ArrayList<Integer>();
					for (int i = datelist.size() - 1; i >= 0; i--) {
						if (rowcount.containsKey(datelist.get(i))) {
							rowCountlist.add(rowcount.get(datelist.get(i)));
							ropCountlist.add(ropcount.get(datelist.get(i)));
						}
					}
					mapvalues.put("RowCountValue", rowCountlist);
					mapvalues.put("RopCountValue", ropCountlist);
					measResult.addMapValue(currentKey, mapvalues);
					measResult.addMeasType(currentKey);
				}
			}
		} catch (SQLException sqle) {
			this.log.error("DataRowSummary :: Could not retrieve meas row counts: " + sqle);
			sqle.printStackTrace();
		} finally {
			result.close();
			statement.close();
		}
		return measResult;
	}

	private HashMap<String, Integer> convertMap(HashMap<String, Integer> dateValue) {
		HashMap<String, Integer> mapValue = new HashMap<String, Integer>();
		mapValue.putAll(dateValue);
		return mapValue;
	}

	public int getMeasLineCount() throws SQLException {
		PreparedStatement statement = null;
		ResultSet result = null;
		int rowCount = 0;
		try {
			statement = this.repCon.prepareStatement("SELECT COUNT(typename) AS rowcount FROM TypeActivation");
			result = statement.executeQuery();
			result.next();
			rowCount = result.getInt("rowcount");
		} catch (SQLException sqle) {
			this.log.error("DataRowSummary :: Could not get the storageinfo row count: " + sqle);
		} finally {
			statement.close();
		}
		return rowCount;
	}

	public void closeConnections() {
		try {
			this.repCon.close();
			this.iqCon.close();
			this.log.info("Connection closed in MeasLinecount");
		} catch (SQLException sqle) {
			this.log.error("DataRowSummary :: Could not close the database connections:" + sqle);
		}
	}

	/**
	 * determine the dates to be processed based on the search Direction, start
	 * date & Search days from the template
	 *
	 */

	private List<String> calculateDate(int year, int month, int day, int dayCount, boolean searchBackward) {
		List<String> datelist = new ArrayList<String>();
		DbCalendar date = new DbCalendar(year, month, day);

		for (int i = 0; i < dayCount; ++i) {
			datelist.add(date.getDbDate());
			if (searchBackward)
				date.add(5, -1);
			else
				date.add(5, 1);
		}
		Collections.sort(datelist);
		return datelist;
	}

}
