/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.transformer;

import java.util.Vector;
// import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * @author eheijun
 * 
 */
public class TransformerData {

  // private static final Logger logger =
  // Logger.getLogger(TransformerData.class.getName());

  private Versioning versioning;

  private Transformer transformer;

  private Vector<Object> transformations;

  private final RockFactory rockFactory;

  public final static String DEFAULT_NEW_NAME = "NEW_TRANSFORMER";

  public TransformerData(Versioning versioning, RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    this.transformer = new Transformer(this.rockFactory);
    this.transformer.setVersionid(versioning.getVersionid());
    // Set the ID to something that looks empty
    this.transformer.setTransformerid(DEFAULT_NEW_NAME);
    this.transformations = new Vector<Object>();
  }

  public TransformerData(Versioning versioning, Transformer transformer, Vector<Transformation> transformations,
      RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    this.transformer = transformer;
    if (transformations == null) {
      this.transformations = new Vector<Object>();
    } else {
      this.transformations = new Vector<Object>(transformations);
    }
  }

  public Versioning getVersioning() {
    return versioning;
  }

  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  public Transformer getTransformer() {
    return transformer;
  }

  public void setTransformer(final Transformer transformer) {
    this.transformer = transformer;
  }

  public Vector<Object> getTransformations() {
    return transformations;
  }

  public void setTransformations(final Vector<Object> transformations) {
    this.transformations = transformations;
  }

  /**
   * parses name of the transformer, which is displayed at the tree
   * 
   * @return String name which is visible at the tree
   */
  public String parseTreeName() {
    Transformer transformer = getTransformer();

    String value = transformer.getTransformerid();
    if (value.startsWith(this.versioning.getVersionid()))
      value = value.substring(this.versioning.getVersionid().length());

    value = value.replaceFirst(":", "");
    if (value.contains(":"))
      value = value.substring(0, value.indexOf(":"));
    
    return value;
  }
}
