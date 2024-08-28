package com.ericsson.eniq.etl.test.parser;

import com.distocraft.dc5000.repository.cache.DFormat;
import com.distocraft.dc5000.repository.cache.DItem;
import com.distocraft.dc5000.repository.cache.DataFormatCache;
import com.ericsson.eniq.common.INIGet;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * User: eeipca
 * Date: 14/05/12
 * Time: 11:11
 */
public class DataFormatCacheBuilder {

  public static void main(final String[] args) throws IOException {
    buildCache(new File("C:\\cc_storage\\eniq_eeipca_stats120_sidebranch\\adapters\\dev\\parser_test_fwk\\conf\\dformats"),
      "INTF_DC_E_RNC");
  }

  public static Map<String, DFormat> buildCache(final File configDir, final String interfaceName) throws IOException {
    final INIGet iniReader = new INIGet();

    final File dataDir = new File(configDir, interfaceName);

    final File[] dataFormInfo = dataDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return !(name.startsWith("__") && name.endsWith("__.txt"));
      }
    });

    final Map<String, DFormat> it_map = new HashMap<String, DFormat>();
    for (File tagFile : dataFormInfo) {
      final DFormat dFormat = parseDFormat(tagFile, iniReader, interfaceName);
      if(dFormat == null){
        continue;
      }
      it_map.put(interfaceName + "_" + dFormat.getTagID(), dFormat);
    }

    final Set<String> if_names = new HashSet<String>(1);
    if_names.add(interfaceName);

    DataFormatCache.testInitialize(it_map, null, if_names, null);
    return it_map;
  }

  public static DFormat parseDFormat(final File tagFile, final INIGet iniReader, final String interfaceName) throws IOException {
    final Map<String, Map<String, String>> objectClass = iniReader.readIniFile(tagFile);
    if(objectClass.isEmpty()){
      return null;
    }
    final Map<String, String> dFormatInfo = objectClass.get("GENERAL");

    final List<DItem> dItemList = new ArrayList<DItem>();
    int colIndex = 0;
    if (dFormatInfo.containsKey("dItems_imports")) {
      final StringTokenizer st = new StringTokenizer(dFormatInfo.get("dItems_imports"), ",");
      while (st.hasMoreTokens()) {
        dItemList.addAll(
          buildDItemList(new File(tagFile.getParentFile(), st.nextToken().trim()), iniReader, colIndex)
        );
        colIndex = dItemList.size();
      }
    }

    for (Map.Entry<String, Map<String, String>> dItemInfo : objectClass.entrySet()) {
      if (dItemInfo.getKey().equalsIgnoreCase("GENERAL")) {
        continue;
      }
      final DItem ditem = ditem(dItemInfo.getKey(), dItemInfo.getValue(), colIndex++);
      dItemList.add(ditem);
    }

    final DFormat dFormat = new DFormat(interfaceName, dFormatInfo.get("tagId"),
      dFormatInfo.get("dataFormatID"), dFormatInfo.get("folderName"), "GenericTransformer");
    dFormat.setItems(dItemList);
    return dFormat;
  }

  private static List<DItem> buildDItemList(final File dataFile, final INIGet iniReader, final int colStartIndex) throws IOException {
    final Map<String, Map<String, String>> iniConfig = iniReader.readIniFile(dataFile);
    int colNumber = colStartIndex;
    final List<DItem> dItemList = new ArrayList<DItem>(iniConfig.size());
    for (Map.Entry<String, Map<String, String>> dItem : iniConfig.entrySet()) {
      final Map<String, String> dItemInfo = iniConfig.get(dItem.getKey());
      final DItem ditem = ditem(dItem.getKey(), dItemInfo, colNumber++);
      dItemList.add(ditem);
    }
    return dItemList;
  }

  private static DItem ditem(final String dItemName, final Map<String, String> dItemInfo, final int colNumber) {
    return new DItem(
      dItemName,
      colNumber,
      dItemName,
      dItemInfo.containsKey("pi") ? dItemInfo.get("pi") : "",
      dItemInfo.get("dataType"),
      dItemInfo.containsKey("dataSize") ? Integer.valueOf(dItemInfo.get("dataSize")) : 0,
      dItemInfo.containsKey("dataScale") ? Integer.valueOf(dItemInfo.get("dataScale")) : 0,
      dItemInfo.containsKey("isCounter") ? Integer.valueOf(dItemInfo.get("isCounter")) : 0);
  }
}
