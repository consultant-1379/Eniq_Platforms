package com.ericsson.eniq.techpacksdk.view.generalInterface;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Datainterface;
import com.distocraft.dc5000.repository.dwhrep.DatainterfaceFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacemeasurement;
import com.distocraft.dc5000.repository.dwhrep.InterfacemeasurementFactory;
import com.distocraft.dc5000.repository.dwhrep.Interfacetechpacks;
import com.distocraft.dc5000.repository.dwhrep.InterfacetechpacksFactory;
import com.distocraft.dc5000.repository.dwhrep.Tpactivation;
import com.distocraft.dc5000.repository.dwhrep.TpactivationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Typeactivation;
import com.distocraft.dc5000.repository.dwhrep.TypeactivationFactory;
import com.ericsson.eniq.component.ExceptionHandler;
import com.ericsson.eniq.techpacksdk.ManageInterfaceTab;

public class ActivateInterface {

  private static final Logger logger = Logger.getLogger(ActivateInterface.class.getName());
  
  private RockFactory dwhrepRockFactory;

  private String activatedInterfaceName;
  private String activatedInterfaceVersion;

  public ActivateInterface(String activatedInterfaceName, String activatedInterfaceVersion, RockFactory dwhrepRockFactory) {
    this.dwhrepRockFactory = dwhrepRockFactory;
    this.activatedInterfaceName = activatedInterfaceName;
    this.activatedInterfaceVersion = activatedInterfaceVersion;
  }

  public void activateInterface() {

    try {

      // HashMap containing dataformat entries.
      // Key: uniqueId contains of tagid from the DefaultTags table + "#" +
      // dataformatid from DataFormat table.
      // Value: RockObject of DataFormat entry.
      final HashMap dataFormats = new HashMap();

      // Get the dataformattype of this interface.
      final Datainterface whereDataInterface = new Datainterface(this.dwhrepRockFactory);
      whereDataInterface.setInterfacename(this.activatedInterfaceName);
      whereDataInterface.setInterfaceversion(this.activatedInterfaceVersion);
      final DatainterfaceFactory dataInterfaceFactory = new DatainterfaceFactory(this.dwhrepRockFactory,
          whereDataInterface);
      final Vector dataInterfaceVector = dataInterfaceFactory.get();
      String dataFormatType = "";

      if (dataInterfaceVector.size() > 0) {
        final Datainterface targetDataInterface = (Datainterface) dataInterfaceVector.get(0);
        dataFormatType = targetDataInterface.getDataformattype();

        if (dataFormatType == null) {
          // throw new BuildException("DataFormatType was null. Interface activation cannot continue.");
        }

      } else {
        // throw new BuildException("Dataformattype not found for interface " + this.activatedInterfaceName + ".
        // Interface activation cannot continue.");
      }

      // Get tech packs related to this interaface.
      final Interfacetechpacks whereInterfaceTechPacks = new Interfacetechpacks(this.dwhrepRockFactory);
      whereInterfaceTechPacks.setInterfacename(this.activatedInterfaceName);
      whereInterfaceTechPacks.setInterfaceversion(this.activatedInterfaceVersion);
      final InterfacetechpacksFactory interfaceTechPacksFactory = new InterfacetechpacksFactory(this.dwhrepRockFactory,
          whereInterfaceTechPacks);
      final Vector interfaceTechPacks = interfaceTechPacksFactory.get();
      final Iterator interfaceTechPacksIterator = interfaceTechPacks.iterator();

      while (interfaceTechPacksIterator.hasNext()) {
        final Interfacetechpacks currentTechPack = (Interfacetechpacks) interfaceTechPacksIterator.next();
        final String techPackName = currentTechPack.getTechpackname();
        // Get the activated tech pack activation.
        final Tpactivation whereTPActivation = new Tpactivation(this.dwhrepRockFactory);
        whereTPActivation.setTechpack_name(techPackName);
        whereTPActivation.setStatus("ACTIVE");
        final TpactivationFactory tpActivationFactory = new TpactivationFactory(this.dwhrepRockFactory,
            whereTPActivation);
        final Vector tpActivations = tpActivationFactory.get();
        final Iterator tpActivationsIterator = tpActivations.iterator();

        while (tpActivationsIterator.hasNext()) {
          final Tpactivation currentTpActivation = (Tpactivation) tpActivationsIterator.next();
          // VersionId is used to map TypeActivation entries to table
          // DataFormat.
          final String techPackVersionId = currentTpActivation.getVersionid();

          // Get the TypeActivations of this TPActivation.
          final Typeactivation whereTypeActivation = new Typeactivation(this.dwhrepRockFactory);
          whereTypeActivation.setTechpack_name(techPackName);
          final TypeactivationFactory typeActivationFactory = new TypeactivationFactory(this.dwhrepRockFactory,
              whereTypeActivation);
          final Vector typeActivations = typeActivationFactory.get();
          final Iterator typeActivationsIterator = typeActivations.iterator();

          while (typeActivationsIterator.hasNext()) {
            final Typeactivation currentTypeActivation = (Typeactivation) typeActivationsIterator.next();
            final String typeName = currentTypeActivation.getTypename();
            // TypeId in table DataFormat is in format
            // VERSIONID:TYPENAME:DATAFORMATTYPE.
            final String DataFormatTypeId = techPackVersionId + ":" + typeName + ":" + dataFormatType;

            logger.fine("Looking for entries with dataformatid: " + DataFormatTypeId);

            final Dataformat whereDataFormat = new Dataformat(this.dwhrepRockFactory);
            whereDataFormat.setDataformatid(DataFormatTypeId);
            final DataformatFactory dataFormatFactory = new DataformatFactory(this.dwhrepRockFactory, whereDataFormat);
            final Vector dataFormatVector = dataFormatFactory.get();
            final Iterator dataFormatIterator = dataFormatVector.iterator();

            while (dataFormatIterator.hasNext()) {
              final Dataformat currentDataFormat = (Dataformat) dataFormatIterator.next();
              // Get the tagid's used by this dataformat.
              final Defaulttags whereDefaultTags = new Defaulttags(this.dwhrepRockFactory);
              whereDefaultTags.setDataformatid(currentDataFormat.getDataformatid());
              final DefaulttagsFactory defaultTagsFactory = new DefaulttagsFactory(this.dwhrepRockFactory,
                  whereDefaultTags);
              final Vector defaultTagsVector = defaultTagsFactory.get();
              final Iterator defaultTagsIterator = defaultTagsVector.iterator();

              while (defaultTagsIterator.hasNext()) {
                final Defaulttags currentDefaultTag = (Defaulttags) defaultTagsIterator.next();
                // Found related dataformat with unique tagid. Add it to
                // comparable dataformat entries.

                logger.fine("Adding dataFormat: " + currentDefaultTag.getTagid() + "#"
                    + currentDataFormat.getDataformatid());

                dataFormats.put(currentDefaultTag.getTagid() + "#" + currentDataFormat.getDataformatid(),
                    currentDataFormat);
              }
            }
          }
        }
      }

      // At this point dataformat entries are collected to dataFormats hashmap.
      // Remove the old entries from InterfaceMeasurement if they exist.
      final Interfacemeasurement whereInterfaceMeasurement = new Interfacemeasurement(this.dwhrepRockFactory);
      whereInterfaceMeasurement.setInterfacename(this.activatedInterfaceName);
      whereInterfaceMeasurement.setInterfaceversion(this.activatedInterfaceVersion);
      final InterfacemeasurementFactory interfaceMeasurementFactory = new InterfacemeasurementFactory(
          this.dwhrepRockFactory, whereInterfaceMeasurement);
      final Vector interfaceMeasurementsVector = interfaceMeasurementFactory.get();
      final Iterator interfaceMeasurementsIterator = interfaceMeasurementsVector.iterator();

      while (interfaceMeasurementsIterator.hasNext()) {
        final Interfacemeasurement currentInterfaceMeasurement = (Interfacemeasurement) interfaceMeasurementsIterator
            .next();

        currentInterfaceMeasurement.deleteDB();

        logger.fine("Removed old InterfaceMeasurement " + currentInterfaceMeasurement.getDataformatid());
      }

      final Date currentTime = new Date();
      final Timestamp currentTimeTimestamp = new Timestamp(currentTime.getTime());

      // Start inserting the values collected from DataFormat table.
      final Set dataFormatsSet = dataFormats.keySet();
      final Iterator dataFormatsIterator = dataFormatsSet.iterator();

      while (dataFormatsIterator.hasNext()) {
        final String uniqueId = (String) dataFormatsIterator.next();
        final Dataformat currentDataFormat = (Dataformat) dataFormats.get(uniqueId);
        final String currentTagId = uniqueId.substring(0, uniqueId.indexOf("#"));

        // Get the defaultTag for this dataformat from DefaultTags table.
        // Long tagId = new Long(0);
        String description = "";
        final Defaulttags whereDefaultTag = new Defaulttags(this.dwhrepRockFactory);
        whereDefaultTag.setDataformatid(currentDataFormat.getDataformatid());
        final DefaulttagsFactory defaultTagsFactory = new DefaulttagsFactory(this.dwhrepRockFactory, whereDefaultTag);
        final Vector defaultTagsVector = defaultTagsFactory.get();

        if (defaultTagsVector.size() == 0) {
          logger.fine("No tagid found for dataformat " + currentDataFormat.getDataformatid());

        } else {
          final Defaulttags targetDefaultTag = (Defaulttags) defaultTagsVector.get(0);
          // tagId = targetDefaultTag.getTagid();
          description = targetDefaultTag.getDescription();
        }

        // Create a new row to table InterfaceMeasurement.
        final Interfacemeasurement newInterfaceMeasurement = new Interfacemeasurement(this.dwhrepRockFactory);
        // String currentTagId = uniqueId.substring(0, uniqueId.indexOf("#"));

        newInterfaceMeasurement.setTagid(currentTagId);
        newInterfaceMeasurement.setDescription(description);

        newInterfaceMeasurement.setDataformatid(currentDataFormat.getDataformatid());
        newInterfaceMeasurement.setInterfacename(this.activatedInterfaceName);
        newInterfaceMeasurement.setInterfaceversion(this.activatedInterfaceVersion);
        newInterfaceMeasurement.setTechpackversion("N/A");
        // Check if the TransformerId exists in table Transformer.
        // If it doesn't exist in the Transformer, insert null to the column
        // TRANFORMERID in the InterfaceMeasurement.
        final Transformer whereTransformer = new Transformer(this.dwhrepRockFactory);
        whereTransformer.setTransformerid(currentDataFormat.getDataformatid());
        final TransformerFactory transformerFactory = new TransformerFactory(this.dwhrepRockFactory, whereTransformer);
        final Vector targetTransformerVector = transformerFactory.get();

        if (targetTransformerVector.size() > 0) {
          // TransformerId exists in table Transformer.
          newInterfaceMeasurement.setTransformerid(currentDataFormat.getDataformatid());
        } else {
          // No transformerId found. Set null to the InterfaceMeasurement's
          // TransformerId.
          newInterfaceMeasurement.setTransformerid(null);
        }

        newInterfaceMeasurement.setStatus(new Long(1));
        newInterfaceMeasurement.setModiftime(currentTimeTimestamp);
        // Save the new InterfaceMeasurement to database table
        newInterfaceMeasurement.insertDB();

      }

    } catch (Exception e) {
      ExceptionHandler.instance().handle(e);
      e.printStackTrace();e.printStackTrace();
      // throw new BuildException("Function activateInterface failed.", e);
    }

  }

}
