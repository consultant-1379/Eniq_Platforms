package com.ericsson.eniq.techpacksdk;

import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.distocraft.dc5000.etl.importexport.ETLCExport;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class CreateSetInstallFile {

  private DataModelController dataModelController;

  private String name;
  private String version;
  private String oldBuildnum;
  private String newBuildnum;
  
  public CreateSetInstallFile(String name, String version, String oldBuildnum, String newBuildnum, DataModelController dataModelController) {
    this.dataModelController = dataModelController;
    this.name = name;
    this.version = version;
    this.newBuildnum = newBuildnum;
    this.oldBuildnum = oldBuildnum;
  }

  public void create(String base,ZipOutputStream out, boolean deactivatesets) throws Exception {
    
    String outputname = base+"Tech_Pack_" + name + ".xml";
   
    ZipEntry entry = new ZipEntry(outputname);
    out.putNextEntry(entry);
    
    String dir = System.getProperty("configtool.templatedir", "");

    if (name != null && version != null) {
      String replaseStr = "#version#=" + version + ",#techpack#=" + name;
      ETLCExport des = new ETLCExport(dir, dataModelController.getEtlRockFactory().getConnection());
      StringWriter strw = des.exportXml(replaseStr);
           
      String tmp = strw.getBuffer().toString().replace(oldBuildnum, newBuildnum);   
      if (deactivatesets){
        tmp = tmp.replaceAll("ENABLED_FLAG=\"Y\"", "ENABLED_FLAG=\"N\"");
      }
      if (!deactivatesets){
          tmp = tmp.replaceAll("ENABLED_FLAG=\"N\"", "ENABLED_FLAG=\"Y\"");
      }
      
      out.write(tmp.getBytes());

    }
  }
}
