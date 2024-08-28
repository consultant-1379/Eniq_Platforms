package com.ericsson.eniq.afj;

import java.util.List;

import com.ericsson.eniq.afj.common.AFJDelta;
import com.ericsson.eniq.afj.common.AFJTechPack;
import com.ericsson.eniq.exception.AFJConfiguationException;
import com.ericsson.eniq.exception.AFJException;

/**
 * Inteface for mapping the operations triggered from the AdminUI for AFJ.
 * 
 * @author esunbal
 * 
 */
public interface AFJManager {

  /**
   * Returns a list of all the techpacks for which AFJ is enabled
   * @return list of AFJTechPack objects
   */
  List<AFJTechPack> getAFJTechPacks() throws AFJException, AFJConfiguationException;

  /**
   * Returns a AFJ enabled techpack by name
   * @return AFJTechPack object
   */
  AFJTechPack getAFJTechPack(String techPackName) throws AFJException, AFJConfiguationException;

  /**
   * Takes in the AFJTechPack object as param to return an AFJDelta object which contains the techpack details and the
   * delta (measurement types and counters).
   * @param techPack
   * @return
   */
  AFJDelta getAFJDelta(AFJTechPack techPack) throws AFJException;

  /**
   * Takes in the delta as input to do the AFJ upgrade and returns the status of teh upgrade.
   * @param delta
   * @return
   */
  String upgradeAFJTechPack(AFJDelta delta) throws AFJException;

  /**
   * Removes all installed AFJ features from the previously upgraded techpack.
   * @return true when restore is successfully done
   */
  Boolean restoreAFJTechPack(AFJTechPack techPack) throws AFJException;

}
