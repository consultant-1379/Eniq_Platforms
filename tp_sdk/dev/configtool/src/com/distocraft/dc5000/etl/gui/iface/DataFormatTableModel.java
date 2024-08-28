package com.distocraft.dc5000.etl.gui.iface;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;

public class DataFormatTableModel extends AbstractTableModel {

  private static final String[] columns = { "TagID", "DataFormat", "Transformer", "Status", "Description", "Last modified" };

  private static final SimpleDateFormat MODIF_FORMATER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private Vector measurements = new Vector(10);

  private Datainterface di;
  
  private RockFactory dwhrepRock;

  DataFormatTableModel(Datainterface di, RockFactory dwhrepRock) throws Exception {
    this.di = di;
    this.dwhrepRock = dwhrepRock;
    // session.update(di);

    System.err.println("InterfaceName " + this.di.getInterfacename());

//    Set ims = di.getInterfaceMeasurements();

//    if (ims == null)
//      return;

    try {
      dwhrepRock.getConnection().commit();
    } catch (Exception e) {
    }

    // Iterator i = ims.iterator();

    //Query q = session.createQuery("from InterfaceMeasurement where INTERFACENAME=?");
    //q.setString(0, this.di.getInterfacename());
    //Iterator i = q.list().iterator();
    
    Interfacemeasurement aim = new Interfacemeasurement(dwhrepRock);
    aim.setInterfacename(this.di.getInterfacename());
    InterfacemeasurementFactory aimf = new InterfacemeasurementFactory(dwhrepRock,aim);
    Iterator i = aimf.get().iterator();

    // Query q = session.createQuery("from InterfaceMeasurement where INTERFACENAME=?");
    // q.setString(0, this.di.getInterfacename());
    // Iterator i = q.iterate();

    while (i.hasNext()) {
      Interfacemeasurement im = (Interfacemeasurement) i.next();

      addRow(im);
    }

  }
   
  public Interfacemeasurement getMeasurementAt(int row) {
    return (Interfacemeasurement) measurements.get(row);
  }

  public String getColumnName(int col) {
    return columns[col];
  }

  public Class getColumnClass(int col) {
    if (col == 3)
      return Integer.class;
    else
      return String.class;
  }

  public Object getValueAt(int row, int col) {
    Interfacemeasurement im = (Interfacemeasurement) measurements.get(row);

    if (col == 0)
      return im.getTagid();
    else if (col == 1)
      return im.getDataformatid();
    else if (col == 2)
      return im.getTransformerid();
    else if (col == 3)
      return new Integer(im.getStatus().intValue());
    else if (col == 4)
      return im.getDescription();
    else if (col == 5)
      return MODIF_FORMATER.format(im.getModiftime());
    else
      return "undefined";
  }

  public int getColumnCount() {
    return columns.length;
  }

  public int getRowCount() {
    return measurements.size();
  }

  int addRow(Interfacemeasurement im) throws Exception {
    if (im == null)
      return 0;
     
    measurements.add(im);
    fireTableRowsInserted(measurements.size(), measurements.size());

    return measurements.size() - 1;
  }

}
