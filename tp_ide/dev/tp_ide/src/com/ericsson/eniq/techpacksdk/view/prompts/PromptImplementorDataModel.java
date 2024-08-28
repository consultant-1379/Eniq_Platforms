/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.prompts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockException;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Prompt;
import com.distocraft.dc5000.repository.dwhrep.PromptFactory;
import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.distocraft.dc5000.repository.dwhrep.PromptimplementorFactory;
import com.distocraft.dc5000.repository.dwhrep.Promptoption;
import com.distocraft.dc5000.repository.dwhrep.PromptoptionFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.datamodel.DataModel;
import com.ericsson.eniq.techpacksdk.datamodel.TechPackDataModel;

/**
 * @author eheijun
 * 
 */
public class PromptImplementorDataModel implements DataModel {

  private static final Logger logger = Logger.getLogger(PromptImplementorDataModel.class.getName());

  private final RockFactory rockFactory;

  private Versioning currentVersioning;

  private TechPackDataModel techPackDataModel;

  private DataModel universeDataModel;

  private List<PromptImplementorData> promptimplementors;

  public PromptImplementorDataModel(RockFactory rockFactory) {
    this.rockFactory = rockFactory;
  }

  public RockFactory getRockFactory() {
    return rockFactory;
  }

  public Versioning getCurrentVersioning() {
    return techPackDataModel.getVersioning();
  }

  public void setCurrentVersioning(final Versioning versioning) {
    currentVersioning = versioning;
  }

  public DataModel getUniverseDataModel() {
    return universeDataModel;
  }

  public void setUniverseDataModel(final DataModel universeDataModel) {
    this.universeDataModel = universeDataModel;
  }

  public List<PromptImplementorData> getPromptimplementors() {
    return promptimplementors;
  }

  public void refresh() {

    promptimplementors = new ArrayList<PromptImplementorData>();

    if (currentVersioning != null) {
      final Vector<Promptimplementor> pi = getPromptImplementors(currentVersioning.getVersionid());

      for (final Iterator<Promptimplementor> it = pi.iterator(); it.hasNext();) {
        final Promptimplementor promptimplementor = it.next();
        final Vector<Prompt> prompts = getPromptsForImplementor(promptimplementor);
        final Vector<Promptoption> promptoptions = getPromptoptionsForImplementor(promptimplementor);
        final PromptImplementorData data = new PromptImplementorData(promptimplementor, prompts, promptoptions,
            rockFactory);
        promptimplementors.add(data);
      }
    }

    logger.info("PromptImplementorDataModel refreshed from DB");
  }

  public void save() {

  }

  /**
   * Returns a list of prompts for promptimplementor
   * 
   * @param promptimplementor
   *          the parent of the columns
   * @return results a list of prompts
   */
  private Vector<Prompt> getPromptsForImplementor(final Promptimplementor promptimplementor) {
    Vector<Prompt> results = new Vector<Prompt>();

    final Prompt wherePrompt = new Prompt(rockFactory);
    wherePrompt.setVersionid(promptimplementor.getVersionid());
    wherePrompt.setPromptimplementorid(promptimplementor.getPromptimplementorid());
    try {
      final PromptFactory promptFactory = new PromptFactory(rockFactory, wherePrompt, true);
      results = promptFactory.get();
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return results;
  }

  /**
   * Returns a list of promptoptions for promptimplementor
   * 
   * @param promptimplementor
   *          the parent of the columns
   * @return results a list of promptoptions
   */
  private Vector<Promptoption> getPromptoptionsForImplementor(final Promptimplementor promptimplementor) {
    Vector<Promptoption> results = new Vector<Promptoption>();

    final Promptoption wherePromptoption = new Promptoption(rockFactory);
    wherePromptoption.setVersionid(promptimplementor.getVersionid());
    wherePromptoption.setPromptimplementorid(promptimplementor.getPromptimplementorid());
    try {
      final PromptoptionFactory promptFactory = new PromptoptionFactory(rockFactory, wherePromptoption, true);
      results = promptFactory.get();
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return results;
  }

  /**
   * Returns a list of prompt implementors by versionId
   * 
   * @return results a list of prompt implementors
   */
  private Vector<Promptimplementor> getPromptImplementors(final String versionId) {

    final Vector<Promptimplementor> results = new Vector<Promptimplementor>();

    final Promptimplementor wherePromptimplementor = new Promptimplementor(rockFactory);
    wherePromptimplementor.setVersionid(versionId);
    try {
      final PromptimplementorFactory PromptImplementorFactory = new PromptimplementorFactory(rockFactory,
          wherePromptimplementor, true);
      final Vector<Promptimplementor> targetPromptimplementors = PromptImplementorFactory.get();

      for (final Iterator<Promptimplementor> iter = targetPromptimplementors.iterator(); iter.hasNext();) {
        final Promptimplementor pi = iter.next();
        results.add(pi);
      }
    } catch (SQLException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    } catch (RockException e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();
    }

    return results;
  }

  public boolean delObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject obj) {
    return false;
  }

  public boolean modObj(final RockDBObject[] obj) {
    return false;
  }

  public boolean newObj(final RockDBObject obj) {
    return false;
  }

  public boolean validateDel(final RockDBObject obj) {
    return false;
  }

  public boolean validateMod(final RockDBObject obj) {
    return false;
  }

  public boolean validateNew(final RockDBObject obj) {
    return false;
  }

  public boolean updated(final DataModel dataModel) {
    if (dataModel instanceof TechPackDataModel) {
      techPackDataModel = (TechPackDataModel) dataModel;
      this.setCurrentVersioning(techPackDataModel.getVersioning());
      refresh();
      return true;
    }
    return false;
  }

}
