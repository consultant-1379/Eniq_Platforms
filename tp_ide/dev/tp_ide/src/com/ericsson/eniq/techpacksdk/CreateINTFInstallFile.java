package com.ericsson.eniq.techpacksdk;

import java.util.Iterator;
import java.util.Vector;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacedependency;
import com.distocraft.dc5000.repository.dwhrep.InterfacedependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class CreateINTFInstallFile {

  private DataModelController dataModelController;

  private Datainterface datainterface;
  private String oldBuildnum;
  private String newBuildnum;

  public CreateINTFInstallFile(Datainterface datainterface, String oldBuildnum, String newBuildnum, DataModelController dataModelController) {

    this.dataModelController = dataModelController;
    this.datainterface = datainterface;
    this.newBuildnum = newBuildnum;
    this.oldBuildnum = oldBuildnum;
  }

  public void create(String base, ZipOutputStream out) throws Exception {

    StringBuffer sql = new StringBuffer();

    sql.append("\n-- Datainterface\n");

    // fetch datainterfaces
    DatainterfaceFactory aimf = new DatainterfaceFactory(dataModelController.getRockFactory(), datainterface);
    Vector<Datainterface> ims = aimf.get();

    if (ims != null) {
      Iterator<Datainterface> i = ims.iterator();

      while (i.hasNext()) {
        Datainterface im = (Datainterface) i.next();

        sql
            .append("insert into Datainterface(interfacename,interfacetype,interfaceversion,status,description,dataformattype,eniq_level,rstate,installdescription) values ('"
                + im.getInterfacename() + "','" + im.getInterfacetype() + "','" + im.getInterfaceversion() + "','" + im.getStatus() + "','" + im.getDescription() + "','"
                + im.getDataformattype() + "','" + im.getEniq_level() + "','" + im.getRstate() + "','" + im.getInstalldescription() + "')\n");

      }
    }

    sql.append("\n-- InterfaceTechpacks\n");

    // fetch interface techpack
    Interfacetechpacks aimt = new Interfacetechpacks(dataModelController.getRockFactory());
    aimt.setInterfacename(datainterface.getInterfacename());
    aimt.setInterfaceversion(datainterface.getInterfaceversion());
    InterfacetechpacksFactory aimtf = new InterfacetechpacksFactory(dataModelController.getRockFactory(), aimt);
    Vector<Interfacetechpacks>  imts = aimtf.get();

    if (imts != null) {
      Iterator<Interfacetechpacks> i = imts.iterator();

      while (i.hasNext()) {
        Interfacetechpacks imt = (Interfacetechpacks) i.next();

        sql
            .append("insert into InterfaceTechpacks(interfacename,techpackname,techpackversion,interfaceversion) values ('"
                + imt.getInterfacename()
                + "', '"
                + imt.getTechpackname()
                + "', '"
                + imt.getTechpackversion()
                + "', '"
                + imt.getInterfaceversion() + "')\n");

      }
    }

    sql.append("\n-- InterfaceDependency\n");

    // interface depedencies
    Interfacedependency infd = new Interfacedependency(dataModelController.getRockFactory());
    infd.setInterfacename(datainterface.getInterfacename());
    infd.setInterfaceversion(datainterface.getInterfaceversion());
    InterfacedependencyFactory infdf = new InterfacedependencyFactory(dataModelController.getRockFactory(), infd);
    Vector<Interfacedependency> infdv = infdf.get();

    if (infdv != null) {
      Iterator<Interfacedependency> i = infdv.iterator();

      while (i.hasNext()) {
        Interfacedependency id = (Interfacedependency) i.next();

        sql
            .append("insert into Interfacedependency(interfacename,interfaceversion,techpackname,techpackversion) values ('"
                + id.getInterfacename()
                + "', '"
                + id.getInterfaceversion()
                + "', '"
                + id.getTechpackname()
                + "', '"
                + id.getTechpackversion() + "')\n");

      }
    }

    ZipEntry entry = new ZipEntry(base+"Tech_Pack_" + datainterface.getInterfacename() + ".sql");
    out.putNextEntry(entry);

    String tmp = sql.toString().replace(oldBuildnum, newBuildnum);
    out.write(tmp.getBytes());

  }
}
