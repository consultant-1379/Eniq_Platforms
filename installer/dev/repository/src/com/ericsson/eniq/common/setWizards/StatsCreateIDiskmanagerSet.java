/**------------------------------------------------------------------------
 *
 *
 *      COPYRIGHT (C)                   ERICSSON RADIO SYSTEMS AB, Sweden
 *
 *      The  copyright  to  the document(s) herein  is  the property of
 *      Ericsson Radio Systems AB, Sweden.
 *
 *      The document(s) may be used  and/or copied only with the written
 *      permission from Ericsson Radio Systems AB  or in accordance with
 *      the terms  and conditions  stipulated in the  agreement/contract
 *      under which the document(s) have been supplied.
 *
 *------------------------------------------------------------------------
 */
package com.ericsson.eniq.common.setWizards;

import ssc.rockfactory.RockFactory;

/**
 * This class is used for creating the Disk Manager set for a ENIQ STATS techpack.
 * 
 * @author epaujor
 * 
 */
public class StatsCreateIDiskmanagerSet extends CreateIDiskmanagerSet {

  public StatsCreateIDiskmanagerSet(final String objType, final String version, final RockFactory rock,
      final long techPackID, final String name, final String name1) throws Exception {
    super(objType, version, rock, techPackID, name, name1);
  }

}
