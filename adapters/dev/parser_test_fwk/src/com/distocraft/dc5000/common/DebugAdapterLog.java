package com.distocraft.dc5000.common;

import com.ericsson.eniq.exception.ConfigurationException;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: eeipca
 * Date: 14/05/12
 * Time: 16:45
 */
public class DebugAdapterLog extends SessionLogger {

  public static final Map<String, Map<String, String>> ROP_COUNTERVOLUME_INFO = new HashMap<String, Map<String, String>>();

  private final AdapterLog adapterLogger = new AdapterLog();

  /**
   * Initialisation of protected content. This must be done before accepting any
   * log entries.
   */
  DebugAdapterLog() throws ConfigurationException, FileNotFoundException {
    super("ADAPTER");
  }

  @Override
  public void log(final Map<String, Object> data) {
    adapterLogger.log(data);
  }

  public static void clear() {
    ROP_COUNTERVOLUME_INFO.clear();
  }

  public static Map<String, Map<String, String>> getRopCountervolumeInfo() {
    return ROP_COUNTERVOLUME_INFO;
  }

  @Override
  public void bulkLog(final Collection<Map<String, Object>> data) {
    for (Map<String, Object> rowData : data) {
      if (rowData.containsKey("counterVolumes")) {
        final Map<String, Map<String, String>> rowInfo = (Map<String, Map<String, String>>) rowData.get("counterVolumes");
        for (Map<String, String> actualCountInfo : rowInfo.values()) {
          ROP_COUNTERVOLUME_INFO.put(actualCountInfo.get("typeName"), actualCountInfo);
        }
      } else {
        throw new NullPointerException("No counterVolumes data found!");
      }
    }
    adapterLogger.bulkLog(data);
  }
}
