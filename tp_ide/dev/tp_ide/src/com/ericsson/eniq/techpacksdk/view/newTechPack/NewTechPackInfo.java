package com.ericsson.eniq.techpacksdk.view.newTechPack;

import java.util.List;

public class NewTechPackInfo {

  private String name, version, product, rstate, licenseName, unvname, description, basedefinition, installdescription,
      type, fromtechpack, username;

  private List venrellist;

  /**
   * Universe Extension List is a table of UniverseExtensions and UniverseExtensionNames.
   * E.g.: ["a"="Standard","b"="Extended","c"="<some-value>"]
   */
  private List unvextlist;

  private List tpdep;

  // default constructor
  public NewTechPackInfo() {
    name = "";
    version = "";
    product = "";
    rstate = "";
    type = "";
    unvname = "";
    unvextlist = null;
    description = "";
    venrellist = null;
    basedefinition = "";
    tpdep = null;
    installdescription = "";
    fromtechpack = "";
    username = "";
  }

  public NewTechPackInfo(final String name, final String version, final String product, final String rstate, final String licenseName, final String type,
		  final String unvname, final List unvextlist, final String description, final List venrellist, final String basedefinition, final List tpdep,
		  final String installdescription, final String fromtechpack, final String username) {
    this.name = name;
    this.version = version;
    this.product = product;
    this.rstate = rstate;
    this.licenseName = licenseName;
    this.type = type;
    this.unvname = unvname;
    this.unvextlist = unvextlist;
    this.description = description;
    this.venrellist = venrellist;
    this.basedefinition = basedefinition;
    this.tpdep = tpdep;
    this.installdescription = installdescription;
    this.fromtechpack = fromtechpack;
    this.username = username;
  }

  public void setName(final String n) {
    name = n;
  }

  public void setVersion(final String v) {
    version = v;
  }

  public void setProduct(final String p) {
    product = p;
  }

  public void setRstate(final String r) {
    rstate = r;
  }

  public void setLicenseName(final String l) {
    this.licenseName = l;
  }

  public void setType(final String t) {
    type = t;
  }

  public void setUnvName(final String un) {
    unvname = un;
  }

  public void setUnvExt(final List uel) {
    unvextlist = uel;
  }

  public void setDescription(final String d) {
    description = d;
  }

  public void setVendorRelease(final List vrl) {
    venrellist = vrl;
  }

  public void setBaseDefinition(final String b) {
    basedefinition = b;
  }

  public void setTechPackDep(final List tpd) {
    tpdep = tpd;
  }

  public void setInstallDescription(final String id) {
    installdescription = id;
  }

  public void setFromTechPack(final String ftp) {
    fromtechpack = ftp;
  }

  public void setUserName(final String un) {
    username = un;
  }

  // getters
  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getProduct() {
    return product;
  }

  public String getRstate() {
    return rstate;
  }

  public String getLicenseName() {
    return licenseName;
  }

  public String getType() {
    return type;
  }

  public String getUnvName() {
    return unvname;
  }

  public List getUnvExt() {
    return unvextlist;
  }

  public String getDescription() {
    return description;
  }

  public List getVendorRelease() {
    return venrellist;
  }

  public String getBaseDefinition() {
    return basedefinition;
  }

  public List getTechPackDep() {
    return tpdep;
  }

  public String getInstallDescription() {
    return installdescription;
  }

  public String getFromTechPack() {
    return fromtechpack;
  }

  public String getUserName() {
    return username;
  }
}
