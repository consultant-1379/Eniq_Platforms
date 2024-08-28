/**
 * 
 */
package com.ericsson.eniq.techpacksdk.view.reference;

import java.util.Vector;
//import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.Versioning;

/**
 * @author eheijun
 * 
 */
public class ReferenceTypeData {

//  private static final Logger logger = Logger.getLogger(ReferenceTypeData.class.getName());
  
  private Versioning versioning;
  
  private Referencetable referencetable;
  
  private Vector<Object> referencecolumns;

  private Vector<Referencecolumn> publiccolumns;

  private final RockFactory rockFactory;

  public final static String DEFAULT_NEW_NAME = "NEW_REFERENCE_TYPE";
  
  public ReferenceTypeData(Versioning versioning, RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    final String typename = DEFAULT_NEW_NAME;
    final String typeid = this.versioning.getVersionid() + ":" + typename;
    final String versionid = this.versioning.getVersionid();
    this.referencetable = new Referencetable(this.rockFactory);
    this.referencetable.setTypeid(typeid);
    this.referencetable.setTypename(typename);
    this.referencetable.setVersionid(versionid);
    this.referencetable.setObjectid(typeid);
    this.referencetable.setObjectname(typename);
    this.referencetable.setDescription("");
    this.referencetable.setUpdate_policy(0L);
    this.referencetable.setDataformatsupport(1);
    this.referencetable.setBasedef(0);

    this.referencecolumns = new Vector<Object>();
    if (versioning.getBasedefinition() != null) {
      this.publiccolumns = new Vector<Referencecolumn>();
    } else {
      this.publiccolumns = null;
    }
  }

  public ReferenceTypeData(Versioning versioning, Referencetable referencetable, Vector<Referencecolumn> referencecolumns, Vector<Referencecolumn> publiccolumns, RockFactory rockFactory) {
    this.rockFactory = rockFactory;
    this.versioning = versioning;
    this.referencetable = referencetable;
    if (referencecolumns == null) {
      this.referencecolumns = new Vector<Object>();
    } else {
      this.referencecolumns = new Vector<Object>(referencecolumns);
    }
    if (versioning.getBasedefinition() != null) {
      if (publiccolumns == null) {
        this.publiccolumns = new Vector<Referencecolumn>();
      } else {
        this.publiccolumns = new Vector<Referencecolumn>(publiccolumns);
      }
    } else {
      this.publiccolumns = null;
    }
  }
  
  public Versioning getVersioning() {
    return versioning;
  }
  
  public void setVersioning(final Versioning versioning) {
    this.versioning = versioning;
  }

  public Referencetable getReferencetable() {
    return referencetable;
  }

  
  public void setReferencetable(final Referencetable referencetable) {
    this.referencetable = referencetable;
  }

  
  public Vector<Object> getReferencecolumns() {
    return referencecolumns;
  }
  
  public void setReferencecolumns(final Vector<Object> referencecolumns) {
    this.referencecolumns = referencecolumns;
  }

  
  public Vector<Referencecolumn> getPubliccolumns() {
    return publiccolumns;
  }

  
  public void setPubliccolumns(final Vector<Referencecolumn> publiccolumns) {
    this.publiccolumns = publiccolumns;
  }

}
