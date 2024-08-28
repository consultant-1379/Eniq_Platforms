/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.prompts;

import java.util.Vector;
//import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Prompt;
import com.distocraft.dc5000.repository.dwhrep.Promptimplementor;
import com.distocraft.dc5000.repository.dwhrep.Promptoption;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * @author eheijun
 * 
 */
public class PromptImplementorData {

//  private static final Logger logger = Logger.getLogger(PromptImplementorData.class.getName());
  
  Promptimplementor promptimplementor;
  
  Vector<Object> prompts;
  
  Vector<Object> promptoptions;

  private final RockFactory rockFactory;

  public final static String DEFAULT_NEW_NAME = "NEW_PROMPT_IMPLEMENTOR";
  
  public PromptImplementorData(Versioning versioning, RockFactory rockFactory, Integer count) {
    this.rockFactory = rockFactory;
    this.promptimplementor = new Promptimplementor(this.rockFactory);
    this.promptimplementor.setPromptimplementorid(count);
    this.promptimplementor.setVersionid(versioning.getVersionid());
    this.promptimplementor.setPromptclassname(DEFAULT_NEW_NAME);
    this.promptimplementor.setPriority(30);
    this.prompts = new Vector<Object>();
    this.promptoptions = new Vector<Object>();
  }

  public PromptImplementorData(Promptimplementor promptimplementor, Vector<Prompt> prompts, Vector<Promptoption> promptoptions, RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.promptimplementor = promptimplementor;
    if (prompts == null) {
      this.prompts = new Vector<Object>();
    } else {
      this.prompts = new Vector<Object>(prompts);
    }
    if (promptoptions == null) {
      this.promptoptions = new Vector<Object>();
    } else {
      this.promptoptions = new Vector<Object>(promptoptions);
    }
  }

  
  public Promptimplementor getPromptimplementor() {
    return promptimplementor;
  }

  
  public void setPromptimplementor(final Promptimplementor promptimplementor) {
    this.promptimplementor = promptimplementor;
  }

  
  public Vector<Object> getPrompts() {
    return prompts;
  }

  
  public void setPrompts(final Vector<Object> prompts) {
    this.prompts = prompts;
  }

  
  public Vector<Object> getPromptoptions() {
    return promptoptions;
  }

  
  public void setPromptoptions(final Vector<Object> promptoptions) {
    this.promptoptions = promptoptions;
  }
  
}
