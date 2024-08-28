package com.ericsson.wifi.ewmnbi;

/*developed by xmudkum  */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import com.ericsson.wifi.ewmnbi.EwmnbiCommon.NodeType;
import com.ericsson.wifi.ewmnbi.EwmnbiInventory.WifiInventory;

@SuppressWarnings({ "unused" })
public class WIFIInventoryParser implements Parser, MemoryRestrictedParser {

	SourceFile sf = null;
	private final HashMap<String, String> attrTable = new HashMap<String, String>();
	private final HashMap<String, String> commonvalue = new HashMap<String, String>();
	private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private SourceFile sourceFile;
	private MeasurementFile nodeInfo = null;
	private MeasurementFile wlanInfo = null;
	private int memoryConsumptionMB = 0;
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
				final WifiInventory.Builder builder = WifiInventory.newBuilder();
				while (builder.mergeDelimitedFrom(input) == true) {
					final WifiInventory wifiinventory = builder.build();
					final String nodeMac = wifiinventory.getMoId().getNodeMac();
					final MoType Motype = wifiinventory.getMoId().getMoType();
					final String moType = Motype.toString();
					commonvalue.put("nodeMac", nodeMac);
					commonvalue.put("moType", moType);
					commonvalue.put("DIRNAME", sf.getDir());
					commonvalue.put("JVM_TIMEZONE", JVM_TIMEZONE);
					if (wifiinventory.hasType()) {
						switch (wifiinventory.getType()) {
						case NODE:
							final NodeType nodeType = wifiinventory.getNodeInfo().getNodeType();
							final String sysName = wifiinventory.getNodeInfo().getSysName();
							final String sysLocation = wifiinventory.getNodeInfo().getSysLocation();
							final String sysContact = wifiinventory.getNodeInfo().getSysContact();
							final Double latitude = wifiinventory.getNodeInfo().getLatitude();
							final Double longitude = wifiinventory.getNodeInfo().getLongitude();
							final String controllerMac = wifiinventory.getNodeInfo().getControllerMac();
							int customfieldcount = wifiinventory.getNodeInfo().getCustomFieldsCount();
							while (customfieldcount-- > 0) {
								final Integer index = wifiinventory.getNodeInfo().getCustomFields(customfieldcount)
										.getIndex();
								final String cfindexname = "CustomField" + index + "index";
								final String cfvaluename = "CustomField" + index + "value";
								final String value = wifiinventory.getNodeInfo().getCustomFields(customfieldcount)
										.getValue();
								attrTable.put(cfindexname, index.toString());
								attrTable.put(cfvaluename, value);
							}
							attrTable.putAll(commonvalue);
							attrTable.put("nodeType", nodeType.toString());
							attrTable.put("sysName", sysName.toString());
							attrTable.put("sysLocation", sysLocation.toString());
							attrTable.put("sysContact", sysContact.toString());
							attrTable.put("latitude", latitude.toString());
							attrTable.put("longitude", longitude.toString());
							attrTable.put("controllerMac", controllerMac.toString());

							try {

								nodeInfo = Main.createMeasurementFile(sourceFile, "nodeInfo", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {
								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								nodeInfo.addData(attrTable);
								nodeInfo.setData(attrTable);
								nodeInfo.saveData();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
							break;
						case WLAN:
							final String inServiceSsid = wifiinventory.getWlanInfo().getInServiceSsid();
							log.log(Level.INFO, "WIFI_Inventory -- INServiceSSID value : " + inServiceSsid);
							final Integer wlanId = wifiinventory.getMoId().getWlanId();
							final Integer slot = wifiinventory.getMoId().getSlot();
							final Integer port = wifiinventory.getMoId().getPort();
							attrTable.putAll(commonvalue);
							attrTable.put("inServiceSsid", inServiceSsid);
							attrTable.put("wlanId", wlanId.toString());
							attrTable.put("slot", slot.toString());
							attrTable.put("port", port.toString());
							try {

								wlanInfo = Main.createMeasurementFile(sourceFile, "wlanInfo", techPack, setType,
										setName, workerName, log);

							} catch (final Exception e) {

								log.log(Level.FINEST, "Error getting measurement file details ", e);
								e.printStackTrace();

							}
							try {
								wlanInfo.addData(attrTable);
								wlanInfo.setData(attrTable);
								wlanInfo.saveData();
								attrTable.clear();
							} catch (final Exception e) {
								e.printStackTrace();
							}
							break;

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