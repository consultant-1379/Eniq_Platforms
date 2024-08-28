/**
 * 
 */
package com.ericsson.eniq.techpacksdk;

import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

/**
 * @author eheijun
 * 
 */
public class VersionPropertiesFile {

  private Versioning versioning;
  private DataModelController dataModelController;

  public VersionPropertiesFile(Versioning versioning, DataModelController dataModelController) {
    this.versioning = versioning;
    this.dataModelController = dataModelController;
  }

  // NAME:tech_pack.name=DC_E_MGW
  // R-STATESTA:tech_pack.version=R1D
  // TP DEPENDENCYT: required_tech_packs.DWH_BASE=R1C
  // required_tech_packs.DWH_MONITOR=R1C
  // required_tech_packs.DIM_E_CN=R1A
  // VAKIO tech_pack.metadata_version=2
  public void create(ZipOutputStream out) throws Exception {
    RockFactory rockFactory = dataModelController.getRockFactory();
    String name = "tech_pack.name=" + versioning.getTechpack_name() + "\n";
    String version = "tech_pack.version=" + versioning.getTechpack_version() + "\n";

    Techpackdependency tpd = new Techpackdependency(rockFactory);
    tpd.setVersionid(versioning.getVersionid());
    TechpackdependencyFactory tpdF = new TechpackdependencyFactory(rockFactory, tpd, true);
    Vector<Techpackdependency> dependencies = tpdF.get();
    
//    tarkista tuleeko base mukaan!
    String[] required = new String[dependencies.size()];
    int ind = 0;
    for (Iterator<Techpackdependency> iter = dependencies.iterator(); iter.hasNext(); ) {
      Techpackdependency dependency = iter.next();
      required[ind] = "required_tech_packs." + dependency.getTechpackname() + "=" + dependency.getVersion() + "\n";
      ind++;
    }
    String metadataversion = "tech_pack.metadata_version=2\n";
    
    out.write(name.getBytes());
    out.write(version.getBytes());
    for (int jnd = 0; jnd < required.length; jnd++) {
      out.write(required[jnd].getBytes());
    }
    out.write(metadataversion.getBytes());
  }

}
