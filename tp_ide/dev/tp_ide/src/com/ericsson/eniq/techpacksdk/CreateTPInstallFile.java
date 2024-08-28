package com.ericsson.eniq.techpacksdk;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhour;
import com.distocraft.dc5000.repository.dwhrep.BusyhourFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourmapping;
import com.distocraft.dc5000.repository.dwhrep.BusyhourmappingFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourplaceholders;
import com.distocraft.dc5000.repository.dwhrep.BusyhourplaceholdersFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhourrankkeys;
import com.distocraft.dc5000.repository.dwhrep.BusyhourrankkeysFactory;
import com.distocraft.dc5000.repository.dwhrep.Busyhoursource;
import com.distocraft.dc5000.repository.dwhrep.BusyhoursourceFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataformat;
import com.distocraft.dc5000.repository.dwhrep.DataformatFactory;
import com.distocraft.dc5000.repository.dwhrep.Dataitem;
import com.distocraft.dc5000.repository.dwhrep.DataitemFactory;
import com.distocraft.dc5000.repository.dwhrep.Defaulttags;
import com.distocraft.dc5000.repository.dwhrep.DefaulttagsFactory;
import com.distocraft.dc5000.repository.dwhrep.Externalstatement;
import com.distocraft.dc5000.repository.dwhrep.ExternalstatementFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcolumn;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementcounter;
import com.distocraft.dc5000.repository.dwhrep.MeasurementcounterFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementdeltacalcsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementdeltacalcsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementkey;
import com.distocraft.dc5000.repository.dwhrep.MeasurementkeyFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementobjbhsupport;
import com.distocraft.dc5000.repository.dwhrep.MeasurementobjbhsupportFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtable;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtableFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementtypeclass;
import com.distocraft.dc5000.repository.dwhrep.MeasurementtypeclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Measurementvector;
import com.distocraft.dc5000.repository.dwhrep.MeasurementvectorFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencecolumn;
import com.distocraft.dc5000.repository.dwhrep.ReferencecolumnFactory;
import com.distocraft.dc5000.repository.dwhrep.Referencetable;
import com.distocraft.dc5000.repository.dwhrep.ReferencetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Supportedvendorrelease;
import com.distocraft.dc5000.repository.dwhrep.SupportedvendorreleaseFactory;
import com.distocraft.dc5000.repository.dwhrep.Techpackdependency;
import com.distocraft.dc5000.repository.dwhrep.TechpackdependencyFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformation;
import com.distocraft.dc5000.repository.dwhrep.TransformationFactory;
import com.distocraft.dc5000.repository.dwhrep.Transformer;
import com.distocraft.dc5000.repository.dwhrep.TransformerFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeclass;
import com.distocraft.dc5000.repository.dwhrep.UniverseclassFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecomputedobject;
import com.distocraft.dc5000.repository.dwhrep.UniversecomputedobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universecondition;
import com.distocraft.dc5000.repository.dwhrep.UniverseconditionFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.distocraft.dc5000.repository.dwhrep.UniverseformulasFactory;
import com.distocraft.dc5000.repository.dwhrep.Universejoin;
import com.distocraft.dc5000.repository.dwhrep.UniversejoinFactory;
import com.distocraft.dc5000.repository.dwhrep.Universename;
import com.distocraft.dc5000.repository.dwhrep.UniversenameFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeobject;
import com.distocraft.dc5000.repository.dwhrep.UniverseobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Universeparameters;
import com.distocraft.dc5000.repository.dwhrep.UniverseparametersFactory;
import com.distocraft.dc5000.repository.dwhrep.Universetable;
import com.distocraft.dc5000.repository.dwhrep.UniversetableFactory;
import com.distocraft.dc5000.repository.dwhrep.Verificationcondition;
import com.distocraft.dc5000.repository.dwhrep.VerificationconditionFactory;
import com.distocraft.dc5000.repository.dwhrep.Verificationobject;
import com.distocraft.dc5000.repository.dwhrep.VerificationobjectFactory;
import com.distocraft.dc5000.repository.dwhrep.Versioning;
import com.distocraft.dc5000.repository.dwhrep.VersioningFactory;
import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.distocraft.dc5000.repository.dwhrep.GrouptypesFactory;
import com.ericsson.eniq.techpacksdk.datamodel.DataModelController;

public class CreateTPInstallFile {

  private static final Logger LOGGER = Logger.getLogger(CreateTPInstallFile.class.getName());

  private DataModelController dataModelController;

  private Versioning versioning;

  private ZipOutputStream out;

  private String newBuild;

  private String oldBuild;

  public CreateTPInstallFile(Versioning versioning, String oldBuild, String newBuild,
      DataModelController dataModelController) {

    this.dataModelController = dataModelController;
    this.versioning = versioning;
    this.newBuild = newBuild;
    this.oldBuild = oldBuild;
  }

  public void writer(String str) throws Exception {
    out.write(str.replace(oldBuild, newBuild).getBytes());
  }

  public void create(String base, ZipOutputStream out) {

    this.out = out;

    try {

      ZipEntry entry = new ZipEntry(base + "Tech_Pack_" + versioning.getTechpack_name() + ".sql");
      out.putNextEntry(entry);

      // Versioning Iterator
      Iterator<Versioning> versioningFI = createVersioning(versioning);

      while (versioningFI.hasNext()) {
        Versioning ver = (Versioning) versioningFI.next();
        ver.setLockedby(null);
        ver.setLockdate(null);
        // versioning row(s)
        // writer(createVersioningRow(ver));
        writer(ver.toSQLInsert());

        // SupportedVendorRelease
        Iterator<Supportedvendorrelease> svrFI = createSupportedVendorRelease(ver);

        while (svrFI.hasNext()) {
          Supportedvendorrelease svr = (Supportedvendorrelease) svrFI.next();

          // SupportedVendorRelease row(s)
          // writer(createSupportedVendorReleaseRow(svr));
          writer(svr.toSQLInsert());
        }

        // Measurementtypeclass
        Iterator<Measurementtypeclass> measurementTypeClassFI = createMeasurementTypeClass(ver);

        while (measurementTypeClassFI.hasNext()) {
          Measurementtypeclass mtc = (Measurementtypeclass) measurementTypeClassFI.next();

          // Measurementtypeclass row(s)
          writer(mtc.toSQLInsert());

          // Measurementtype
          Iterator<Measurementtype> measurementtypeFI = createMeasurementType(mtc);

          while (measurementtypeFI.hasNext()) {
            Measurementtype mt = (Measurementtype) measurementtypeFI.next();

            // Measurementtype row(s)
            writer(mt.toSQLInsert());

            // Measurementkey
            Iterator<Measurementkey> measurementkeyFI = createMeasurementKey(mt);

            while (measurementkeyFI.hasNext()) {
              Measurementkey mk = (Measurementkey) measurementkeyFI.next();

              // Measurementtype row(s)
              writer(mk.toSQLInsert());

            }

            // Measurementconter
            Iterator<Measurementcounter> measurementcounterFI = createMeasurementCounter(mt);

            while (measurementcounterFI.hasNext()) {
              Measurementcounter mc = (Measurementcounter) measurementcounterFI.next();

              // Measurementconter row(s)
              writer(mc.toSQLInsert());
            }

            // MeasurementVector
            Iterator<Measurementvector> measurementvectorFI = createMeasurementVector(mt);

            while (measurementvectorFI.hasNext()) {
              Measurementvector mv = (Measurementvector) measurementvectorFI.next();

              // MeasurementVector row(s)
              writer(mv.toSQLInsert());
            }

            // MeasurementDeltaCalcSupport
            Iterator<Measurementdeltacalcsupport> measurementdeltacalcsupportFI = createMeasurementDeltaCalcSupport(mt);

            while (measurementdeltacalcsupportFI.hasNext()) {
              Measurementdeltacalcsupport mdcs = (Measurementdeltacalcsupport) measurementdeltacalcsupportFI.next();

              // MeasurementDeltaCalcSupport row(s)
              writer(mdcs.toSQLInsert());
            }

            // MeasurementObjBHSupport
            Iterator<Measurementobjbhsupport> MeasurementObjBHSupportFI = createMeasurementObjBHSupport(mt);

            while (MeasurementObjBHSupportFI.hasNext()) {
              Measurementobjbhsupport mdcs = (Measurementobjbhsupport) MeasurementObjBHSupportFI.next();

              // MeasurementObjBHSupport row(s)
              writer(mdcs.toSQLInsert());
            }

            // Measurementtable
            Iterator<Measurementtable> measurementtableFI = createMeasurementTable(mt);

            while (measurementtableFI.hasNext()) {
              Measurementtable mtt = (Measurementtable) measurementtableFI.next();

              // Measurementtable row(s)
              writer(mtt.toSQLInsert());

              // Measurementcolumn
              Iterator<Measurementcolumn> measurementcolumnFI = createMeasurementColumn(mtt);

              while (measurementcolumnFI.hasNext()) {
                Measurementcolumn mc = (Measurementcolumn) measurementcolumnFI.next();

                // Measurementtable row(s)
                writer(mc.toSQLInsert());

              }
            }
          }
        }

        // Busyhour
        Iterator<Busyhour> bhFI = createBusyhour(ver);

        while (bhFI.hasNext()) {
          Busyhour tpd = (Busyhour) bhFI.next();

          // Busyhour row(s)
          writer(tpd.toSQLInsert());
        }

        // BusyhourRankkeys
        Iterator<Busyhourrankkeys> bhrFI = createBusyhourRankkeys(ver);

        while (bhrFI.hasNext()) {
          Busyhourrankkeys tpd = (Busyhourrankkeys) bhrFI.next();

          // Busyhourrankkeys row(s)
          writer(tpd.toSQLInsert());
        }

        // BusyhourSource
        Iterator<Busyhoursource> bhsFI = createBusyhourSource(ver);

        while (bhsFI.hasNext()) {
          Busyhoursource tpd = (Busyhoursource) bhsFI.next();

          // Busyhoursource row(s)
          writer(tpd.toSQLInsert());
        }

        // BusyhourMapping
        Iterator<Busyhourmapping> bhmFI = createBusyhourMapping(ver);

        while (bhmFI.hasNext()) {
          Busyhourmapping bhs = (Busyhourmapping) bhmFI.next();

          // Busyhourmapping row(s)
          writer(bhs.toSQLInsert());
        }

        // BusyhourPlaceHolders
        Iterator<Busyhourplaceholders> bhphFI = createBusyhourPlaceHolders(ver);

        while (bhphFI.hasNext()) {
          Busyhourplaceholders bhs = (Busyhourplaceholders) bhphFI.next();

          // BusyhourPlaceHolders row(s)
          writer(bhs.toSQLInsert());
        }

        // TechPackDepedency
        Iterator<Techpackdependency> tpdFI = createTechpackDependency(ver);

        while (tpdFI.hasNext()) {
          Techpackdependency tpd = (Techpackdependency) tpdFI.next();

          // TechPackDepedenciy row(s)
          writer(tpd.toSQLInsert());
        }

        // UniverseClass
        Iterator<Universeclass> ucFI = createUniverseClass(ver);

        while (ucFI.hasNext()) {
          Universeclass uc = (Universeclass) ucFI.next();

          // Universeclass row(s)
          writer(uc.toSQLInsert());
        }

        // Verificationobject
        Iterator<Verificationobject> voFI = createVerificationObject(ver);

        while (voFI.hasNext()) {
          Verificationobject vo = (Verificationobject) voFI.next();

          // Verificationobject row(s)
          writer(vo.toSQLInsert());
        }

        // VerificationConditions
        Iterator<Verificationcondition> vcFI = createVerificationcondition(ver);

        while (vcFI.hasNext()) {
          Verificationcondition vc = (Verificationcondition) vcFI.next();

          // VerificationConditions row(s)
          writer(vc.toSQLInsert());
        }

        // UniverseTable
        Iterator<Universetable> utFI = createUniverseTable(ver);

        while (utFI.hasNext()) {
          Universetable ut = (Universetable) utFI.next();

          // UniverseTable row(s)
          writer(ut.toSQLInsert());
        }

        // UniverseJoin
        Iterator<Universejoin> ujFI = createUniverseJoin(ver);

        while (ujFI.hasNext()) {
          Universejoin uj = (Universejoin) ujFI.next();

          // UniverseJoin row(s)
          writer(uj.toSQLInsert());
        }

        // UniverseObject
        Iterator<Universeobject> uoFI = createUniverseObject(ver);

        while (uoFI.hasNext()) {
          Universeobject uo = (Universeobject) uoFI.next();

          // UniverseObject row(s)
          writer(uo.toSQLInsert());
        }

        // UniverseComputedObject
        Iterator<Universecomputedobject> ucmpoFI = createUniverseComputedObject(ver);

        while (ucmpoFI.hasNext()) {
          Universecomputedobject uco = (Universecomputedobject) ucmpoFI.next();

          // UniverseComputedObject row(s)
          writer(uco.toSQLInsert());

          // UniverseParameters
          Iterator<Universeparameters> upFI = createUniverseParameters(uco);

          while (upFI.hasNext()) {
            Universeparameters up = (Universeparameters) upFI.next();

            // UniverseParameters row(s)
            writer(up.toSQLInsert());

          }

        }

        // UniverseFormulas
        Iterator<Universeformulas> uvoFI = createUniverseFormulas(ver);

        while (uvoFI.hasNext()) {
          Universeformulas uco = (Universeformulas) uvoFI.next();

          // Universeformulas row(s)
          writer(uco.toSQLInsert());

        }

        // UniverseCondition
        Iterator<Universecondition> ucoFI = createUniverseCondition(ver);

        while (ucoFI.hasNext()) {
          Universecondition uco = (Universecondition) ucoFI.next();

          // UniverseCondition row(s)
          writer(uco.toSQLInsert());
        }

        // Transformer
        Iterator<Transformer> transfomerFI = createTransformer(ver);

        while (transfomerFI.hasNext()) {
          Transformer tr = (Transformer) transfomerFI.next();

          // Transformer row(s)
          writer(tr.toSQLInsert());

          // Transformation
          Iterator<Transformation> transformationFI = createTransformation(tr);

          while (transformationFI.hasNext()) {
            Transformation trf = (Transformation) transformationFI.next();

            // Transformation row(s)
            writer(trf.toSQLInsert());

          }
        }

        // Referencetable
        Iterator<Referencetable> referencetableFi = createReferenceTable(ver);

        while (referencetableFi.hasNext()) {
          Referencetable rt = (Referencetable) referencetableFi.next();

          // Referencetable row(s)
          writer(rt.toSQLInsert());

          // Referencecolumn
          Iterator<Referencecolumn> referencecolumnFI = createReferenceColumn(rt);

          while (referencecolumnFI.hasNext()) {
            Referencecolumn rc = (Referencecolumn) referencecolumnFI.next();

            // Referencecolumn row(s)
            writer(rc.toSQLInsert());

          }
        }

        // Dataformat
        Iterator<Dataformat> dataformatFi = createDataFormat(ver);

        while (dataformatFi.hasNext()) {
          Dataformat df = (Dataformat) dataformatFi.next();

          // Dataformat row(s)
          writer(df.toSQLInsert());

          // Defaulttags
          Iterator<Defaulttags> defaulttagsFI = createDefaultTags(df);

          while (defaulttagsFI.hasNext()) {
            Defaulttags dft = (Defaulttags) defaulttagsFI.next();

            // Defaulttags row(s)
            writer(dft.toSQLInsert());

          }

          // Dataitem
          Iterator<Dataitem> dataItemFI = createDataItem(df);

          while (dataItemFI.hasNext()) {
            Dataitem di = (Dataitem) dataItemFI.next();

            // Dataitem row(s)
            writer(di.toSQLInsert());

          }
        }

        // Externalstatement
        Iterator<Externalstatement> externalstatementFI = createExternalStatement(ver);

        while (externalstatementFI.hasNext()) {
          Externalstatement ex = (Externalstatement) externalstatementFI.next();

          // Externalstatement row(s)
          writer(ex.toSQLInsert());

        }

        // UniverseName
        Iterator<Universename> UniversenameFI = createUniverseName(ver);

        while (UniversenameFI.hasNext()) {
          Universename un = (Universename) UniversenameFI.next();

          // UniverseName row(s)
          writer(un.toSQLInsert());

        }

        // Aggregation
        Iterator<Aggregation> aggregationFI = createAggregation(ver);

        while (aggregationFI.hasNext()) {
          Aggregation ag = (Aggregation) aggregationFI.next();

          // Aggregation row(s)
          writer(ag.toSQLInsert());

          // Aggregationrule
          Iterator<Aggregationrule> AggregationruleFI = createAggregationRule(ag);

          while (AggregationruleFI.hasNext()) {
            Aggregationrule agr = (Aggregationrule) AggregationruleFI.next();

            // Aggregationrule row(s)
            writer(agr.toSQLInsert());

          }
        }
				final Iterator<Grouptypes> grouptypeFI = createGrouptypes(ver);
				while(grouptypeFI.hasNext()){
					final Grouptypes type = grouptypeFI.next();
					writer(type.toSQLInsert());
				}
      }
    } catch (Exception e) {
      LOGGER.warning(e.getMessage());
    }
  }
	private Iterator<Grouptypes> createGrouptypes(final Versioning versioning) throws Exception {
		final Grouptypes where = new Grouptypes(dataModelController.getRockFactory());
		where.setVersionid(versioning.getVersionid());
		final GrouptypesFactory fac = new GrouptypesFactory(dataModelController.getRockFactory(), where);
		return fac.get().iterator();
	}

  private Iterator<Versioning> createVersioning(Versioning versioning) throws Exception {

    VersioningFactory versioningF = new VersioningFactory(dataModelController.getRockFactory(), versioning);

    Iterator<Versioning> versioningFI = versioningF.get().iterator();

    return versioningFI;

  }

  private Iterator<Measurementtypeclass> createMeasurementTypeClass(Versioning versioning) throws Exception {

    Measurementtypeclass measurementTypeClass = new Measurementtypeclass(dataModelController.getRockFactory());
    measurementTypeClass.setVersionid(versioning.getVersionid());
    MeasurementtypeclassFactory measurementTypeClassF = new MeasurementtypeclassFactory(dataModelController
        .getRockFactory(), measurementTypeClass);

    Iterator<Measurementtypeclass> measurementTypeClassFI = measurementTypeClassF.get().iterator();

    return measurementTypeClassFI;
  }

  private Iterator<Measurementtype> createMeasurementType(Measurementtypeclass mtc) throws Exception {

    Measurementtype measurementtype = new Measurementtype(dataModelController.getRockFactory());
    measurementtype.setTypeclassid(mtc.getTypeclassid());
    measurementtype.setVersionid(mtc.getVersionid());
    MeasurementtypeFactory measurementtypeF = new MeasurementtypeFactory(dataModelController.getRockFactory(),
        measurementtype);

    Iterator<Measurementtype> measurementtypeFI = measurementtypeF.get().iterator();

    return measurementtypeFI;
  }

  private Iterator<Measurementkey> createMeasurementKey(Measurementtype mt) throws Exception {

    Measurementkey measurementkey = new Measurementkey(dataModelController.getRockFactory());
    measurementkey.setTypeid(mt.getTypeid());
    MeasurementkeyFactory measurementkeyF = new MeasurementkeyFactory(dataModelController.getRockFactory(),
        measurementkey);

    Iterator<Measurementkey> measurementkeyFI = measurementkeyF.get().iterator();

    return measurementkeyFI;

  }

  private Iterator<Measurementcounter> createMeasurementCounter(Measurementtype mt) throws Exception {

    Measurementcounter measurementcounter = new Measurementcounter(dataModelController.getRockFactory());
    measurementcounter.setTypeid(mt.getTypeid());
    MeasurementcounterFactory measurementcounterF = new MeasurementcounterFactory(dataModelController.getRockFactory(),
        measurementcounter);

    Iterator<Measurementcounter> measurementcounterFI = measurementcounterF.get().iterator();

    return measurementcounterFI;

  }

  private Iterator<Measurementtable> createMeasurementTable(Measurementtype mt) throws Exception {

    Measurementtable measurementtable = new Measurementtable(dataModelController.getRockFactory());
    measurementtable.setTypeid(mt.getTypeid());
    MeasurementtableFactory measurementtableF = new MeasurementtableFactory(dataModelController.getRockFactory(),
        measurementtable);

    Iterator<Measurementtable> measurementtableFI = measurementtableF.get().iterator();

    return measurementtableFI;

  }

  private Iterator<Measurementvector> createMeasurementVector(Measurementtype mt) throws Exception {

    Measurementvector measurementvector = new Measurementvector(dataModelController.getRockFactory());
    measurementvector.setTypeid(mt.getTypeid());
    MeasurementvectorFactory measurementvectorF = new MeasurementvectorFactory(dataModelController.getRockFactory(),
        measurementvector);

    Iterator<Measurementvector> measurementvectorFI = measurementvectorF.get().iterator();

    return measurementvectorFI;

  }

  private Iterator<Measurementdeltacalcsupport> createMeasurementDeltaCalcSupport(Measurementtype mt) throws Exception {

    Measurementdeltacalcsupport measurementdeltacalcsupport = new Measurementdeltacalcsupport(dataModelController
        .getRockFactory());
    measurementdeltacalcsupport.setTypeid(mt.getTypeid());
    MeasurementdeltacalcsupportFactory measurementdeltacalcsupportF = new MeasurementdeltacalcsupportFactory(
        dataModelController.getRockFactory(), measurementdeltacalcsupport);

    Iterator<Measurementdeltacalcsupport> measurementdeltacalcsupportFI = measurementdeltacalcsupportF.get().iterator();

    return measurementdeltacalcsupportFI;

  }

  private Iterator<Measurementcolumn> createMeasurementColumn(Measurementtable mt) throws Exception {

    Measurementcolumn measurementcolumn = new Measurementcolumn(dataModelController.getRockFactory());
    measurementcolumn.setMtableid(mt.getMtableid());
    MeasurementcolumnFactory measurementcolumnF = new MeasurementcolumnFactory(dataModelController.getRockFactory(),
        measurementcolumn);

    Iterator<Measurementcolumn> measurementcolumnFI = measurementcolumnF.get().iterator();

    return measurementcolumnFI;

  }

  private Iterator<Referencetable> createReferenceTable(Versioning versioning) throws Exception {

    Referencetable referencetable = new Referencetable(dataModelController.getRockFactory());
    referencetable.setVersionid(versioning.getVersionid());
    ReferencetableFactory referencetableF = new ReferencetableFactory(dataModelController.getRockFactory(),
        referencetable);

    Iterator<Referencetable> referencetableFI = referencetableF.get().iterator();

    return referencetableFI;

  }

  private Iterator<Referencecolumn> createReferenceColumn(Referencetable referenceTable) throws Exception {

    Referencecolumn referencecolumn = new Referencecolumn(dataModelController.getRockFactory());
    referencecolumn.setTypeid(referenceTable.getTypeid());
    ReferencecolumnFactory referencecolumnF = new ReferencecolumnFactory(dataModelController.getRockFactory(),
        referencecolumn);

    Iterator<Referencecolumn> referencecolumnFI = referencecolumnF.get().iterator();

    return referencecolumnFI;

  }

  private Iterator<Dataformat> createDataFormat(Versioning versioning) throws Exception {

    Dataformat dataformat = new Dataformat(dataModelController.getRockFactory());
    dataformat.setVersionid(versioning.getVersionid());
    DataformatFactory dataformatF = new DataformatFactory(dataModelController.getRockFactory(), dataformat);

    Iterator<Dataformat> dataformatFI = dataformatF.get().iterator();

    return dataformatFI;

  }

  private Iterator<Defaulttags> createDefaultTags(Dataformat dataFormat) throws Exception {

    Defaulttags defaulttags = new Defaulttags(dataModelController.getRockFactory());
    defaulttags.setDataformatid(dataFormat.getDataformatid());
    DefaulttagsFactory defaulttagsF = new DefaulttagsFactory(dataModelController.getRockFactory(), defaulttags);

    Iterator<Defaulttags> defaulttagsFI = defaulttagsF.get().iterator();

    return defaulttagsFI;

  }

  private Iterator<Dataitem> createDataItem(Dataformat dataFormat) throws Exception {

    Dataitem dataitem = new Dataitem(dataModelController.getRockFactory());
    dataitem.setDataformatid(dataFormat.getDataformatid());
    DataitemFactory dataitemF = new DataitemFactory(dataModelController.getRockFactory(), dataitem);

    Iterator<Dataitem> dataitemFI = dataitemF.get().iterator();

    return dataitemFI;
  }

  private Iterator<Aggregation> createAggregation(Versioning versioning) throws Exception {

    Aggregation aggregation = new Aggregation(dataModelController.getRockFactory());
    aggregation.setVersionid(versioning.getVersionid());
    AggregationFactory aggregationF = new AggregationFactory(dataModelController.getRockFactory(), aggregation);

    Iterator<Aggregation> aggregationFI = aggregationF.get().iterator();

    return aggregationFI;

  }

  private Iterator<Aggregationrule> createAggregationRule(Aggregation aggregation) throws Exception {

    Aggregationrule aggregationrule = new Aggregationrule(dataModelController.getRockFactory());
    aggregationrule.setAggregation(aggregation.getAggregation());
    aggregationrule.setVersionid(aggregation.getVersionid());
    AggregationruleFactory aggregationruleF = new AggregationruleFactory(dataModelController.getRockFactory(),
        aggregationrule);

    Iterator<Aggregationrule> aggregationruleFI = aggregationruleF.get().iterator();

    return aggregationruleFI;

  }

  private Iterator<Externalstatement> createExternalStatement(Versioning versioning) throws Exception {

    Externalstatement externalstatement = new Externalstatement(dataModelController.getRockFactory());
    externalstatement.setVersionid(versioning.getVersionid());
    ExternalstatementFactory externalstatementF = new ExternalstatementFactory(dataModelController.getRockFactory(),
        externalstatement);

    Iterator<Externalstatement> externalstatementFI = externalstatementF.get().iterator();

    return externalstatementFI;

  }

  private Iterator<Transformer> createTransformer(Versioning versioning) throws Exception {

    Transformer transformer = new Transformer(dataModelController.getRockFactory());
    transformer.setVersionid(versioning.getVersionid());
    TransformerFactory transformerF = new TransformerFactory(dataModelController.getRockFactory(), transformer);

    Iterator<Transformer> transformerFI = transformerF.get().iterator();

    return transformerFI;

  }

  private Iterator<Transformation> createTransformation(Transformer transformer) throws Exception {

    Transformation transformation = new Transformation(dataModelController.getRockFactory());
    transformation.setTransformerid(transformer.getTransformerid());
    TransformationFactory transformationF = new TransformationFactory(dataModelController.getRockFactory(),
        transformation);

    Iterator<Transformation> transformationFI = transformationF.get().iterator();

    return transformationFI;

  }

  private Iterator<Supportedvendorrelease> createSupportedVendorRelease(Versioning versioning) throws Exception {

    Supportedvendorrelease svr = new Supportedvendorrelease(dataModelController.getRockFactory());
    svr.setVersionid(versioning.getVersionid());
    SupportedvendorreleaseFactory svrF = new SupportedvendorreleaseFactory(dataModelController.getRockFactory(), svr);

    Iterator<Supportedvendorrelease> svrFI = svrF.get().iterator();

    return svrFI;

  }

  private Iterator<Busyhour> createBusyhour(Versioning versioning) throws Exception {

    Busyhour tpd = new Busyhour(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    BusyhourFactory tpdF = new BusyhourFactory(dataModelController.getRockFactory(), tpd);

    Iterator<Busyhour> tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator<Busyhourrankkeys> createBusyhourRankkeys(Versioning versioning) throws Exception {

    Busyhourrankkeys tpd = new Busyhourrankkeys(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    BusyhourrankkeysFactory tpdF = new BusyhourrankkeysFactory(dataModelController.getRockFactory(), tpd);

    Iterator<Busyhourrankkeys> tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator<Busyhoursource> createBusyhourSource(Versioning versioning) throws Exception {

    Busyhoursource tpd = new Busyhoursource(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    BusyhoursourceFactory tpdF = new BusyhoursourceFactory(dataModelController.getRockFactory(), tpd);

    Iterator<Busyhoursource> tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator<Busyhourmapping> createBusyhourMapping(Versioning versioning) throws Exception {

    Busyhourmapping tpd = new Busyhourmapping(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    BusyhourmappingFactory tpdF = new BusyhourmappingFactory(dataModelController.getRockFactory(), tpd);

    Iterator<Busyhourmapping> tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator<Busyhourplaceholders> createBusyhourPlaceHolders(Versioning versioning) throws Exception {

    Busyhourplaceholders bhph = new Busyhourplaceholders(dataModelController.getRockFactory());
    bhph.setVersionid(versioning.getVersionid());
    BusyhourplaceholdersFactory bhphF = new BusyhourplaceholdersFactory(dataModelController.getRockFactory(), bhph);

    Iterator<Busyhourplaceholders> bhphFI = bhphF.get().iterator();

    return bhphFI;

  }

  private Iterator<Measurementobjbhsupport> createMeasurementObjBHSupport(Measurementtype mt) throws Exception {

    Measurementobjbhsupport measurementobjbhsupport = new Measurementobjbhsupport(dataModelController.getRockFactory());
    measurementobjbhsupport.setTypeid(mt.getTypeid());
    MeasurementobjbhsupportFactory measurementobjbhsupportF = new MeasurementobjbhsupportFactory(dataModelController
        .getRockFactory(), measurementobjbhsupport);

    Iterator<Measurementobjbhsupport> measurementobjbhsupportFI = measurementobjbhsupportF.get().iterator();

    return measurementobjbhsupportFI;

  }

  private Iterator<Techpackdependency> createTechpackDependency(Versioning versioning) throws Exception {

    Techpackdependency tpd = new Techpackdependency(dataModelController.getRockFactory());
    tpd.setVersionid(versioning.getVersionid());
    TechpackdependencyFactory tpdF = new TechpackdependencyFactory(dataModelController.getRockFactory(), tpd);

    Iterator<Techpackdependency> tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator<Universeclass> createUniverseClass(Versioning versioning) throws Exception {

    Universeclass uc = new Universeclass(dataModelController.getRockFactory());
    uc.setVersionid(versioning.getVersionid());
    UniverseclassFactory ucF = new UniverseclassFactory(dataModelController.getRockFactory(), uc);

    Iterator<Universeclass> ucFI = ucF.get().iterator();

    return ucFI;

  }

  private Iterator<Verificationobject> createVerificationObject(Versioning versioning) throws Exception {

    Verificationobject vo = new Verificationobject(dataModelController.getRockFactory());
    vo.setVersionid(versioning.getVersionid());
    VerificationobjectFactory voF = new VerificationobjectFactory(dataModelController.getRockFactory(), vo);

    Iterator<Verificationobject> voFI = voF.get().iterator();

    return voFI;

  }

  private Iterator<Verificationcondition> createVerificationcondition(Versioning versioning) throws Exception {

    Verificationcondition vc = new Verificationcondition(dataModelController.getRockFactory());
    vc.setVersionid(versioning.getVersionid());
    VerificationconditionFactory vcF = new VerificationconditionFactory(dataModelController.getRockFactory(), vc);

    Iterator<Verificationcondition> vcFI = vcF.get().iterator();

    return vcFI;

  }

  private Iterator<Universetable> createUniverseTable(Versioning versioning) throws Exception {

    Universetable ut = new Universetable(dataModelController.getRockFactory());
    ut.setVersionid(versioning.getVersionid());
    UniversetableFactory utF = new UniversetableFactory(dataModelController.getRockFactory(), ut);

    Iterator<Universetable> utFI = utF.get().iterator();

    return utFI;

  }

  private Iterator<Universename> createUniverseName(Versioning versioning) throws Exception {

    Universename universename = new Universename(dataModelController.getRockFactory());
    universename.setVersionid(versioning.getVersionid());
    UniversenameFactory universenameF = new UniversenameFactory(dataModelController.getRockFactory(), universename);

    Iterator<Universename> universenameFI = universenameF.get().iterator();

    return universenameFI;

  }

  private Iterator<Universejoin> createUniverseJoin(Versioning versioning) throws Exception {

    Universejoin uj = new Universejoin(dataModelController.getRockFactory());
    uj.setVersionid(versioning.getVersionid());
    UniversejoinFactory ujF = new UniversejoinFactory(dataModelController.getRockFactory(), uj);

    Iterator<Universejoin> ujFI = ujF.get().iterator();

    return ujFI;

  }

  private Iterator<Universeobject> createUniverseObject(Versioning versioning) throws Exception {

    Universeobject uo = new Universeobject(dataModelController.getRockFactory());
    uo.setVersionid(versioning.getVersionid());
    UniverseobjectFactory uoF = new UniverseobjectFactory(dataModelController.getRockFactory(), uo);

    Iterator<Universeobject> uoFI = uoF.get().iterator();

    return uoFI;

  }

  private Iterator<Universecomputedobject> createUniverseComputedObject(Versioning versioning) throws Exception {

    Universecomputedobject uco = new Universecomputedobject(dataModelController.getRockFactory());
    uco.setVersionid(versioning.getVersionid());
    UniversecomputedobjectFactory ucoF = new UniversecomputedobjectFactory(dataModelController.getRockFactory(), uco);

    Iterator<Universecomputedobject> ucoFI = ucoF.get().iterator();

    return ucoFI;

  }

  private Iterator<Universecondition> createUniverseCondition(Versioning versioning) throws Exception {

    Universecondition uc = new Universecondition(dataModelController.getRockFactory());
    uc.setVersionid(versioning.getVersionid());
    UniverseconditionFactory ucF = new UniverseconditionFactory(dataModelController.getRockFactory(), uc);

    Iterator<Universecondition> ucFI = ucF.get().iterator();

    return ucFI;

  }

  private Iterator<Universeformulas> createUniverseFormulas(Versioning versioning) throws Exception {

    Universeformulas uc = new Universeformulas(dataModelController.getRockFactory());
    uc.setVersionid(versioning.getVersionid());
    UniverseformulasFactory ucF = new UniverseformulasFactory(dataModelController.getRockFactory(), uc);

    Iterator<Universeformulas> ucFI = ucF.get().iterator();

    return ucFI;

  }

  private Iterator<Universeparameters> createUniverseParameters(Universecomputedobject ucobj) throws Exception {

    Universeparameters uco = new Universeparameters(dataModelController.getRockFactory());
    uco.setVersionid(ucobj.getVersionid());
    uco.setClassname(ucobj.getClassname());
    uco.setObjectname(ucobj.getObjectname());
    uco.setUniverseextension(ucobj.getUniverseextension());

    UniverseparametersFactory ucoF = new UniverseparametersFactory(dataModelController.getRockFactory(), uco);

    Iterator<Universeparameters> ucoFI = ucoF.get().iterator();

    return ucoFI;

  }

}
