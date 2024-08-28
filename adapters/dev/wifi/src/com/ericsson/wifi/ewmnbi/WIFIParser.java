package com.ericsson.wifi.ewmnbi;

/*developed by xmudkum  */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MemoryRestrictedParser;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.ericsson.wifi.ewmnbi.EwmnbiCommon.MoType;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.CableModemDsChStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.CableModemUsChStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.EthernetStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.MeshStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.StatsBatch;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.TunnelStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.WifiStats;
import com.ericsson.wifi.ewmnbi.EwmnbiStats.WlanStats;

@SuppressWarnings({ "unused" })
public class WIFIParser implements Parser, MemoryRestrictedParser {

	SourceFile sf = null;
	private final HashMap<String, String> attrTable = new HashMap<String, String>();
	private final HashMap<String, String> commonvalue = new HashMap<String, String>();
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private SourceFile sourceFile;
	private int memoryConsumptionMB = 0;
	private MeasurementFile nodeStats = null;
	private MeasurementFile cableModemStats = null;
	private MeasurementFile cableModemDsChStats = null;
	private MeasurementFile cableModemUsChStats = null;
	private MeasurementFile tunnelStats = null;
	private MeasurementFile wifiStats = null;
	private MeasurementFile wlanStats = null;
	private MeasurementFile meshStats = null;
	private MeasurementFile ethernetStats = null;
	private MeasurementFile usbModemStats = null;
	private Logger log;
	private String techPack;
	private String setType;
	private String setName;
	private int status = 0;
	private Main mainParserObject = null;
	private String workerName = "";
	final private List errorList = new ArrayList();
	private String filename;

	/**
	 * 
	 */
	@Override
	public void init(final Main main, final String techPack, final String setType, final String setName,
			final String workerName) {

		this.mainParserObject = main;
		this.techPack = techPack;
		this.setType = setType;
		this.setName = setName;
		this.status = 1;
		this.workerName = workerName;

		String logWorkerName = "";
		if (workerName.length() > 0) {
			logWorkerName = "." + workerName;
		}
		this.memoryConsumptionMB = mainParserObject.getNextSFMemConsumptionMB();
		log = Logger
				.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.wifiparser" + logWorkerName);
	}

	@Override
	public int status() {
		return status;
	}

	public List errors() {
		return errorList;
	}

	@Override
	public void run() {

		try {

			this.status = 2;
			SourceFile sf = null;

			while ((sf = mainParserObject.nextSourceFile()) != null) {

				try {
					this.memoryConsumptionMB = sf.getMemoryConsumptionMB();
					mainParserObject.preParse(sf);
					parse(sf, techPack, setType, setName);
					mainParserObject.postParse(sf);
				} catch (final Exception e) {
					mainParserObject.errorParse(e, sf);
				} finally {

					mainParserObject.finallyParse(sf);
				}
			}
		} catch (final Exception e) {
			log.log(Level.WARNING, "Worker parser failed to exception", e);
			errorList.add(e);
		} finally {
			this.memoryConsumptionMB = 0;
			this.status = 3;
		}
	}

	@Override
	public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
			throws Exception {

		final long start = System.currentTimeMillis();
		this.sourceFile = sf;
		this.filename = sf.getName();
		final MeasurementFile mFile = null;

		final long middle = System.currentTimeMillis();

		System.out.println("Reading in encoded file named : " + filename);

		try {
			InputStream input = null;
			try {

				input = sf.getFileInputStream();
				final StatsBatch.Builder builder = StatsBatch.newBuilder();
				while (builder.mergeDelimitedFrom(input) == true) {
					final StatsBatch statsBatch = builder.build();
					final Long time = statsBatch.getTimestamp();
					final Date date = new Date(time);
					final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					final String UTC_DATETIME_ID = format.format(date);
					final String DATE_ID = df.format(date);
					final String nodeMac = statsBatch.getMoId().getNodeMac();
					final MoType Motype = statsBatch.getMoId().getMoType();
					final String moType = Motype.toString();
					commonvalue.put("nodeMac", nodeMac);
					commonvalue.put("moType", moType);
					commonvalue.put("UTC_DATETIME_ID", UTC_DATETIME_ID);
					commonvalue.put("DATE_ID", DATE_ID.toString());
					commonvalue.put("DIRNAME", sf.getDir());
					commonvalue.put("JVM_TIMEZONE", JVM_TIMEZONE);

					if (statsBatch.hasNodeStats()) {
						final Integer criticalAlarmCount = statsBatch.getNodeStats().getCriticalAlarmCount();
						final Integer majorAlarmCount = statsBatch.getNodeStats().getMajorAlarmCount();
						final Integer minorAlarmCount = statsBatch.getNodeStats().getMinorAlarmCount();
						final Integer warningAlarmCount = statsBatch.getNodeStats().getWarningAlarmCount();
						final Integer roundTripDelayMin = statsBatch.getNodeStats().getRoundTripDelayMin();
						final Integer roundTripDelayMax = statsBatch.getNodeStats().getRoundTripDelayMax();
						final Integer roundTripDelayAvg = statsBatch.getNodeStats().getRoundTripDelayAvg();
						final Integer cpuUsageMax = statsBatch.getNodeStats().getCpuUsageMax();
						final Integer cpuUsageAvg = statsBatch.getNodeStats().getCpuUsageAvg();
						final Integer memUsageMax = statsBatch.getNodeStats().getMemUsageMax();
						final Integer memUsageAvg = statsBatch.getNodeStats().getMemUsageAvg();
						attrTable.putAll(commonvalue);
						attrTable.put("criticalAlarmCount", criticalAlarmCount.toString());
						attrTable.put("majorAlarmCount", majorAlarmCount.toString());
						attrTable.put("minorAlarmCount", minorAlarmCount.toString());
						attrTable.put("warningAlarmCount", warningAlarmCount.toString());
						attrTable.put("roundTripDelayMin", roundTripDelayMin.toString());
						attrTable.put("roundTripDelayMax", roundTripDelayMax.toString());
						attrTable.put("roundTripDelayAvg", roundTripDelayAvg.toString());
						attrTable.put("cpuUsageMax", cpuUsageMax.toString());
						attrTable.put("cpuUsageAvg", cpuUsageAvg.toString());
						attrTable.put("memUsageMax", memUsageMax.toString());
						attrTable.put("memUsageAvg", memUsageAvg.toString());
						try {

							nodeStats = Main.createMeasurementFile(sourceFile, "nodeStats", techPack, setType, setName,
									workerName, log);

						} catch (final Exception e) {

							log.log(Level.FINEST, "Error getting measurement file details ", e);
							e.printStackTrace();

						}
						try {
							nodeStats.addData(attrTable);
							nodeStats.setData(attrTable);
							nodeStats.saveData();
							nodeStats.close();
							attrTable.clear();
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
					if (statsBatch.hasCableModemStats()) {
						final Integer cmIndex = statsBatch.getCableModemStats().getCmIndex();
						final Integer uas = statsBatch.getCableModemStats().getUas();
						final Integer resets = statsBatch.getCableModemStats().getResets();
						final Integer downEvents = statsBatch.getCableModemStats().getDownEvents();
						attrTable.putAll(commonvalue);
						attrTable.put("cmIndex", cmIndex.toString());
						attrTable.put("uas", uas.toString());
						attrTable.put("resets", resets.toString());
						attrTable.put("downEvents", downEvents.toString());
						try {

							cableModemStats = Main.createMeasurementFile(sourceFile, "cableModemStats", techPack,
									setType, setName, workerName, log);

						} catch (final Exception e) {

							log.log(Level.FINEST, "Error getting measurement file details ", e);
							e.printStackTrace();

						}
						try {
							cableModemStats.addData(attrTable);
							cableModemStats.setData(attrTable);
							cableModemStats.saveData();
							cableModemStats.close();
							attrTable.clear();
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}if (statsBatch.hasUsbModemStats()) {
									
                        Integer uslot = statsBatch.getUsbModemStats().getSlot();
						Integer uport = statsBatch.getUsbModemStats().getPort();
						Integer urxPkts = statsBatch.getUsbModemStats().getRxPkts();
						Integer utxPkts = statsBatch.getUsbModemStats().getTxPkts();
						Integer urxMBytes = statsBatch.getUsbModemStats().getRxMBytes();
						Integer utxMBytes = statsBatch.getUsbModemStats().getTxMBytes();
						Integer urx_dropped = statsBatch.getUsbModemStats().getRxDropped();
						Integer utx_dropped = statsBatch.getUsbModemStats().getTxDropped();
						Integer urx_errors = statsBatch.getUsbModemStats().getRxErrors();
						Integer utx_errors = statsBatch.getUsbModemStats().getTxErrors();
						Integer ulink_failures = statsBatch.getUsbModemStats().getLinkFailures();
						attrTable.putAll(commonvalue);
						attrTable.put("slot",uslot.toString());
						attrTable.put("port",uport.toString());
						attrTable.put("rxPkts",urxPkts.toString());
						attrTable.put("txPkts",utxPkts.toString());
						attrTable.put("rxMBytes",urxMBytes.toString());
						attrTable.put("txMBytes",utxMBytes.toString());
						attrTable.put("rx_dropped",urx_dropped.toString());
						attrTable.put("tx_dropped", utx_dropped.toString());
						attrTable.put("rx_errors", urx_errors.toString());
						attrTable.put("tx_errors", utx_errors.toString());
						attrTable.put("link_failures", ulink_failures.toString());
						try {

							usbModemStats = Main.createMeasurementFile(sourceFile,"usbModemStats", techPack, setType, setName,workerName, log);

						} catch (Exception e) {

							log.log(Level.FINEST,"Error getting measurement file details ",e);
							e.printStackTrace();

						}
						try {
							usbModemStats.addData(attrTable);
							usbModemStats.setData(attrTable);
							usbModemStats.saveData();
							attrTable.clear();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (statsBatch.getCableModemDsChStatsCount() > 0) {
						for (final CableModemDsChStats c : statsBatch.getCableModemDsChStatsList()) {
							final CableModemDsChStats.Builder cableModemDsChStatsbuilder = CableModemDsChStats
									.newBuilder();

							attrTable.putAll(commonvalue);
							if (!cableModemDsChStatsbuilder.hasCmIndex()) {
								final Integer cmIndex = c.getCmIndex();
								attrTable.put("cmIndex", cmIndex.toString());
							}
							if (!cableModemDsChStatsbuilder.hasChannel()) {
								final Integer channel = c.getChannel();
								attrTable.put("channel", channel.toString());
							}
							if (!cableModemDsChStatsbuilder.hasRxPowerMin()) {
								final Float rxPowerMin = c.getRxPowerMin();
								attrTable.put("rxPowerMin", rxPowerMin.toString());
							}
							if (!cableModemDsChStatsbuilder.hasRxPowerMax()) {
								final Float rxPowerMax = c.getRxPowerMax();
								attrTable.put("rxPowerMax", rxPowerMax.toString());
							}
							if (!cableModemDsChStatsbuilder.hasRxPowerAvg()) {
								final Float rxPowerAvg = c.getRxPowerAvg();
								attrTable.put("rxPowerAvg", rxPowerAvg.toString());
							}
							if (!cableModemDsChStatsbuilder.hasSnrMin()) {
								final Float snrMin = c.getSnrMin();
								attrTable.put("snrMin", snrMin.toString());
							}
							if (!cableModemDsChStatsbuilder.hasSnrMax()) {
								final Float snrMax = c.getSnrMax();
								attrTable.put("snrMax", snrMax.toString());
							}
							if (!cableModemDsChStatsbuilder.hasSnrAvg()) {
								final Float snrAvg = c.getSnrAvg();
								attrTable.put("snrAvg", snrAvg.toString());
							}
							if (!cableModemDsChStatsbuilder.hasUcer()) {
								final Float ucer = c.getUcer();
								attrTable.put("ucer", ucer.toString());
							}
							if (!cableModemDsChStatsbuilder.hasCcer()) {
								final Float ccer = c.getCcer();
								attrTable.put("ccer", ccer.toString());
							}
							if (!cableModemDsChStatsbuilder.hasCer()) {
								final Float cer = c.getCer();
								attrTable.put("cer", cer.toString());
							}
							try {

								cableModemDsChStats = Main.createMeasurementFile(sourceFile, "cableModemDsChStats",
										techPack, setType, setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								cableModemDsChStats.addData(attrTable);
								cableModemDsChStats.setData(attrTable);
								cableModemDsChStats.saveData();
								cableModemDsChStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
					if (statsBatch.getCableModemUsChStatsCount() > 0) {

						for (final CableModemUsChStats c : statsBatch.getCableModemUsChStatsList()) {
							final CableModemUsChStats.Builder cableModemUsChStatsbuilder = CableModemUsChStats
									.newBuilder();

							attrTable.putAll(commonvalue);
							if (!cableModemUsChStatsbuilder.hasCmIndex()) {
								final Integer cmIndex = c.getCmIndex();
								attrTable.put("cmIndex", cmIndex.toString());
							}
							if (!cableModemUsChStatsbuilder.hasChannel()) {
								final Integer channel = c.getChannel();
								attrTable.put("channel", channel.toString());
							}
							if (!cableModemUsChStatsbuilder.hasTxPowerMin()) {
								final Float txPowerMin = c.getTxPowerMin();
								attrTable.put("txPowerMin", txPowerMin.toString());
							}
							if (!cableModemUsChStatsbuilder.hasTxPowerMax()) {
								final Float txPowerMax = c.getTxPowerMax();
								attrTable.put("txPowerMax", txPowerMax.toString());
							}
							if (!cableModemUsChStatsbuilder.hasTxPowerAvg()) {
								final Float txPowerAvg = c.getTxPowerAvg();
								attrTable.put("txPowerAvg", txPowerAvg.toString());
							}
							if (!cableModemUsChStatsbuilder.hasT3Timeouts()) {
								final Integer t3Timeouts = c.getT3Timeouts();
								attrTable.put("t3Timeouts", t3Timeouts.toString());
							}
							if (!cableModemUsChStatsbuilder.hasT4Timeouts()) {
								final Integer t4Timeouts = c.getT4Timeouts();
								attrTable.put("t4Timeouts", t4Timeouts.toString());
							}
							try {

								cableModemUsChStats = Main.createMeasurementFile(sourceFile, "cableModemUsChStats",
										techPack, setType, setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								cableModemUsChStats.addData(attrTable);
								cableModemUsChStats.setData(attrTable);
								cableModemUsChStats.saveData();
								cableModemUsChStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					}
					if (statsBatch.getTunnelStatsCount() > 0) {
						for (final TunnelStats c : statsBatch.getTunnelStatsList()) {

							final TunnelStats.Builder tunnelStatsbuilder = TunnelStats.newBuilder();

							attrTable.putAll(commonvalue);
							if (!tunnelStatsbuilder.hasTunnelEngine()) {
								final Integer tunnelEngine = c.getTunnelEngine();
								attrTable.put("tunnelEngine", tunnelEngine.toString());
							}
							if (!tunnelStatsbuilder.hasTunnel()) {
								final Integer tunnel = c.getTunnel();
								attrTable.put("tunnel", tunnel.toString());
							}
							if (!tunnelStatsbuilder.hasUas()) {
								final Integer uas = c.getUas();
								attrTable.put("uas", uas.toString());
							}
							if (!tunnelStatsbuilder.hasRxPkts()) {
								final Integer rxPkts = c.getRxPkts();
								attrTable.put("rxPkts", rxPkts.toString());
							}
							if (!tunnelStatsbuilder.hasTxPkts()) {
								final Integer txPkts = c.getTxPkts();
								attrTable.put("txPkts", txPkts.toString());
							}
							if (!tunnelStatsbuilder.hasRxMBytes()) {
								final Integer rxMBytes = c.getRxMBytes();
								attrTable.put("rxMBytes", rxMBytes.toString());
							}
							if (!tunnelStatsbuilder.hasTxMBytes()) {
								final Integer txMBytes = c.getTxMBytes();
								attrTable.put("txMBytes", txMBytes.toString());
							}
							if (!tunnelStatsbuilder.hasRxBcastPkts()) {
								final Integer rxBcastPkts = c.getRxBcastPkts();
								attrTable.put("rxBcastPkts", rxBcastPkts.toString());
							}
							if (!tunnelStatsbuilder.hasTxBcastPkts()) {
								final Integer txBcastPkts = c.getTxBcastPkts();
								attrTable.put("txBcastPkts", txBcastPkts.toString());
							}
							if (!tunnelStatsbuilder.hasTxOverLimit()) {
								final Integer txOverLimit = c.getTxOverLimit();
								attrTable.put("txOverLimit", txOverLimit.toString());
							}
							if (!tunnelStatsbuilder.hasEchoLossRate()) {
								final Integer echoLossRate = c.getEchoLossRate();
								attrTable.put("echoLossRate", echoLossRate.toString());
							}
							if (!tunnelStatsbuilder.hasDownEvents()) {
								final Integer downEvents = c.getDownEvents();
								attrTable.put("downEvents", downEvents.toString());
							}
							try {

								tunnelStats = Main.createMeasurementFile(sourceFile, "tunnelStats", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								tunnelStats.addData(attrTable);
								tunnelStats.setData(attrTable);
								tunnelStats.saveData();
								tunnelStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}

					}
					if (statsBatch.getWifiStatsCount() > 0) {

						for (final WifiStats c : statsBatch.getWifiStatsList()) {

							final WifiStats.Builder wifiStatsbuilder = WifiStats.newBuilder();

							attrTable.putAll(commonvalue);
							if (!wifiStatsbuilder.hasSlot()) {
								final Integer slot = c.getSlot();
								attrTable.put("slot", slot.toString());
							}
							if (!wifiStatsbuilder.hasPort()) {
								final Integer port = c.getPort();
								attrTable.put("port", port.toString());
							}
							if (!wifiStatsbuilder.hasRxMBytes()) {
								final Integer rxMBytes = c.getRxMBytes();
								attrTable.put("rxMBytes", rxMBytes.toString());
							}
							if (!wifiStatsbuilder.hasTxMBytes()) {
								final Integer txMBytes = c.getTxMBytes();
								attrTable.put("txMBytes", txMBytes.toString());
							}
							if (!wifiStatsbuilder.hasAccessTxErrorRate()) {
								final Integer accessTxErrorRate = c.getAccessTxErrorRate();
								attrTable.put("accessTxErrorRate", accessTxErrorRate.toString());
							}
							if (!wifiStatsbuilder.hasAccessRxErrorRate()) {
								final Integer accessRxErrorRate = c.getAccessRxErrorRate();
								attrTable.put("accessRxErrorRate", accessRxErrorRate.toString());
							}
							if (!wifiStatsbuilder.hasAccessRxDuplicateFrameRate()) {
								final Integer accessRxDuplicateFrameRate = c.getAccessRxDuplicateFrameRate();
								attrTable.put("accessRxDuplicateFrameRate", accessRxDuplicateFrameRate.toString());
							}
							if (!wifiStatsbuilder.hasAccessRxLowModulationRate()) {
								final Integer accessRxLowModulationRate = c.getAccessRxLowModulationRate();
								attrTable.put("accessRxLowModulationRate", accessRxLowModulationRate.toString());
							}
							if (!wifiStatsbuilder.hasNoiseFloorMin()) {
								final Float noiseFloorMin = c.getNoiseFloorMin();
								attrTable.put("noiseFloorMin", noiseFloorMin.toString());
							}
							if (!wifiStatsbuilder.hasNoiseFloorMax()) {
								final Float noiseFloorMax = c.getNoiseFloorMax();
								attrTable.put("noiseFloorMax", noiseFloorMax.toString());
							}
							if (!wifiStatsbuilder.hasNoiseFloorAvg()) {
								final Float noiseFloorAvg = c.getNoiseFloorAvg();
								attrTable.put("noiseFloorAvg", noiseFloorAvg.toString());
							}
							if (!wifiStatsbuilder.hasUniqueMacs()) {
								final Integer uniqueMacs = c.getUniqueMacs();
								attrTable.put("uniqueMacs", uniqueMacs.toString());
							}
							if (!wifiStatsbuilder.hasAssociationCount()) {
								final Integer associationCount = c.getAssociationCount();
								attrTable.put("associationCount", associationCount.toString());
							}
							if (!wifiStatsbuilder.hasSessionCount()) {
								final Integer sessionCount = c.getSessionCount();
								attrTable.put("sessionCount", sessionCount.toString());
							}
							if (!wifiStatsbuilder.hasMinuteCount()) {
								final Integer minuteCount = c.getMinuteCount();
								attrTable.put("minuteCount", minuteCount.toString());
							}
							if (!wifiStatsbuilder.hasChBusynessTot()) {
								final Integer chBusynessTot = c.getChBusynessTot();
								attrTable.put("chBusynessTot", chBusynessTot.toString());
							}
							if (!wifiStatsbuilder.hasChBusynessNoise()) {
								final Integer chBusynessNoise = c.getChBusynessNoise();
								attrTable.put("chBusynessNoise", chBusynessNoise.toString());
							}
							if (!wifiStatsbuilder.hasChBusynessNonLocal()) {
								final Integer chBusynessNonLocal = c.getChBusynessNonLocal();
								attrTable.put("chBusynessNonLocal", chBusynessNonLocal.toString());
							}
							if (!wifiStatsbuilder.hasWmos()) {
								final Float wmos = c.getWmos();
								attrTable.put("wmos", wmos.toString());
							}
							try {

								wifiStats = Main.createMeasurementFile(sourceFile, "wifiStats", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								wifiStats.addData(attrTable);
								wifiStats.setData(attrTable);
								wifiStats.saveData();
								wifiStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}

					}
					if (statsBatch.getWlanStatsCount() > 0) {

						for (final WlanStats c : statsBatch.getWlanStatsList()) {

							final WlanStats.Builder wlanStatsbuilder = WlanStats.newBuilder();

							attrTable.putAll(commonvalue);
							if (!wlanStatsbuilder.hasSlot()) {
								final Integer slot = c.getSlot();
								attrTable.put("slot", slot.toString());
							}
							if (!wlanStatsbuilder.hasPort()) {
								final Integer port = c.getPort();
								attrTable.put("port", port.toString());
							}
							if (!wlanStatsbuilder.hasWlanId()) {
								final Integer wlanId = c.getWlanId();
								attrTable.put("wlanId", wlanId.toString());
							}
							if (!wlanStatsbuilder.hasRxMBytes()) {
								final Integer rxMBytes = c.getRxMBytes();
								attrTable.put("rxMBytes", rxMBytes.toString());
							}
							if (!wlanStatsbuilder.hasTxMBytes()) {
								final Integer txMBytes = c.getTxMBytes();
								attrTable.put("txMBytes", txMBytes.toString());
							}
							if (!wlanStatsbuilder.hasUniqueMacs()) {
								final Integer uniqueMacs = c.getUniqueMacs();
								attrTable.put("uniqueMacs", uniqueMacs.toString());
							}
							if (!wlanStatsbuilder.hasAssociationCount()) {
								final Integer associationCount = c.getAssociationCount();
								attrTable.put("associationCount", associationCount.toString());
							}
							if (!wlanStatsbuilder.hasSessionCount()) {
								final Integer sessionCount = c.getSessionCount();
								attrTable.put("sessionCount", sessionCount.toString());
							}
							if (!wlanStatsbuilder.hasMinuteCount()) {
								final Integer minuteCount = c.getMinuteCount();
								attrTable.put("minuteCount", minuteCount.toString());
							}
							try {

								wlanStats = Main.createMeasurementFile(sourceFile, "wlanStats", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								wlanStats.addData(attrTable);
								wlanStats.setData(attrTable);
								wlanStats.saveData();
								wlanStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}

					}
					if (statsBatch.getMeshStatsCount() > 0) {

						for (final MeshStats c : statsBatch.getMeshStatsList()) {

							final MeshStats.Builder meshStatsbuilder = MeshStats.newBuilder();

							attrTable.putAll(commonvalue);
							if (!meshStatsbuilder.hasSlot()) {
								final Integer slot = c.getSlot();
								attrTable.put("slot", slot.toString());
							}
							if (!meshStatsbuilder.hasPort()) {
								final Integer port = c.getPort();
								attrTable.put("port", port.toString());
							}
							if (!meshStatsbuilder.hasLinkId()) {
								final Integer linkId = c.getLinkId();
								attrTable.put("linkId", linkId.toString());
							}
							if (!meshStatsbuilder.hasRssiMin()) {
								final Float rssiMin = c.getRssiMin();
								attrTable.put("rssiMin", rssiMin.toString());
							}
							if (!meshStatsbuilder.hasRssiMax()) {
								final Float rssiMax = c.getRssiMax();
								attrTable.put("rssiMax", rssiMax.toString());
							}
							if (!meshStatsbuilder.hasRssiAvg()) {
								final Float rssiAvg = c.getRssiAvg();
								attrTable.put("rssiAvg", rssiAvg.toString());
							}
							if (!meshStatsbuilder.hasBhTxErrorRate()) {
								final Integer bhTxErrorRate = c.getBhTxErrorRate();
								attrTable.put("bhTxErrorRate", bhTxErrorRate.toString());
							}
							try {

								meshStats = Main.createMeasurementFile(sourceFile, "meshStats", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								meshStats.addData(attrTable);
								meshStats.setData(attrTable);
								meshStats.saveData();
								meshStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}

					}
					if (statsBatch.getEthernetStatsCount() > 0) {

						for (final EthernetStats c : statsBatch.getEthernetStatsList()) {

							final EthernetStats.Builder ethernetStatsbuilder = EthernetStats.newBuilder();

							attrTable.putAll(commonvalue);
							if (!ethernetStatsbuilder.hasSlot()) {
								final Integer slot = c.getSlot();
								attrTable.put("slot", slot.toString());
							}
							if (!ethernetStatsbuilder.hasPort()) {
								final Integer port = c.getPort();
								attrTable.put("port", port.toString());
							}
							if (!ethernetStatsbuilder.hasRxMBytes()) {
								final Integer rxMBytes = c.getRxMBytes();
								attrTable.put("rxMBytes", rxMBytes.toString());
							}
							if (!ethernetStatsbuilder.hasTxMBytes()) {
								final Integer txMBytes = c.getTxMBytes();
								attrTable.put("txMBytes", txMBytes.toString());
							}
							try {

								ethernetStats = Main.createMeasurementFile(sourceFile, "ethernetStats", techPack,
										setType, setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								ethernetStats.addData(attrTable);
								ethernetStats.setData(attrTable);
								ethernetStats.saveData();
								ethernetStats.close();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}

					}
					builder.clear();
				}
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				input.close();
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		final long end = System.currentTimeMillis();
		log.log(Level.FINER, "Data parsed. Parser initialization took " + (middle - start) + " ms, parsing "
				+ (end - middle) + " ms. Total: " + (end - start) + " ms.");
	}

	@Override
	public int memoryConsumptionMB() {
		return memoryConsumptionMB;
	}

	@Override
	public void setMemoryConsumptionMB(final int memoryConsumptionMB) {
		this.memoryConsumptionMB = memoryConsumptionMB;
	}
}
