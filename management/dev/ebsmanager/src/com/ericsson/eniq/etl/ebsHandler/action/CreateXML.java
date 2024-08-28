package com.ericsson.eniq.etl.ebsHandler.action;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.helpers.DefaultHandler;

import ssc.rockfactory.RockDBObject;
import ssc.rockfactory.RockFactory;

import com.distocraft.dc5000.repository.dwhrep.Aggregation;
import com.distocraft.dc5000.repository.dwhrep.AggregationFactory;
import com.distocraft.dc5000.repository.dwhrep.Aggregationrule;
import com.distocraft.dc5000.repository.dwhrep.AggregationruleFactory;
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

public class CreateXML extends DefaultHandler {

  protected Logger log = Logger.getLogger("CreateXML");

  private RockFactory rock;

  private List<String> createdTypes;

  public CreateXML(final Logger log, final RockFactory rock) {
    this.log = log;
    this.rock = rock;
  }

  private StringBuffer doTabs(final int tabs) {

    final StringBuffer result = new StringBuffer();

    for (int i = 0; i < tabs; i++) {
      result.append("\t");
    }

    return result;
  }

  private void write(final String tablename, final StringBuffer sb, final int tabs, final String str) {
    if (createdTypes != null && createdTypes.contains(tablename)) {
      sb.append(doTabs(tabs));
      sb.append(str);
    }
  }

  private void delta(final RockDBObject ro) {

  }

  public String createEmptyXML(final String nullis) {

    final StringBuffer result = new StringBuffer();

    try {

      result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
      result.append("<techpack>\n");

      final Versioning ver = new Versioning(rock);
      write(ver.getTableName(), result, 0, ver.toXML_tag());

      final Measurementtypeclass measurementTypeClass = new Measurementtypeclass(rock);
      write(measurementTypeClass.getTableName(), result, 0, measurementTypeClass.toXML_tag());

      final Measurementtype measurementtype = new Measurementtype(rock);
      write(measurementtype.getTableName(), result, 0, measurementtype.toXML_tag());

      final Measurementkey measurementkey = new Measurementkey(rock);
      write(measurementkey.getTableName(), result, 0, measurementkey.toXML_tag());

      final Measurementobjbhsupport measurementobjbhsupport = new Measurementobjbhsupport(rock);
      write(measurementobjbhsupport.getTableName(), result, 0, measurementobjbhsupport.toXML_tag());

      final Measurementcounter measurementcounter = new Measurementcounter(rock);
      write(measurementcounter.getTableName(), result, 0, measurementcounter.toXML_tag());

      final Measurementtable measurementtable = new Measurementtable(rock);
      write(measurementtable.getTableName(), result, 0, measurementtable.toXML_tag());

      final Measurementvector measurementvector = new Measurementvector(rock);
      write(measurementvector.getTableName(), result, 0, measurementvector.toXML_tag());

      final Measurementdeltacalcsupport measurementdeltacalcsupport = new Measurementdeltacalcsupport(rock);
      write(measurementdeltacalcsupport.getTableName(), result, 0, measurementdeltacalcsupport.toXML_tag());

      final Measurementcolumn measurementcolumn = new Measurementcolumn(rock);
      write(measurementcolumn.getTableName(), result, 0, measurementcolumn.toXML_tag());

      final Referencetable referencetable = new Referencetable(rock);
      write(referencetable.getTableName(), result, 0, referencetable.toXML_tag());

      final Referencecolumn referencecolumn = new Referencecolumn(rock);
      write(referencecolumn.getTableName(), result, 0, referencecolumn.toXML_tag());

      final Dataformat dataformat = new Dataformat(rock);
      write(dataformat.getTableName(), result, 0, dataformat.toXML_tag());

      final Defaulttags defaulttags = new Defaulttags(rock);
      write(defaulttags.getTableName(), result, 0, defaulttags.toXML_tag());

      final Dataitem dataitem = new Dataitem(rock);
      write(dataitem.getTableName(), result, 0, dataitem.toXML_tag());

      final Aggregation aggregation = new Aggregation(rock);
      write(aggregation.getTableName(), result, 0, aggregation.toXML_tag());

      final Aggregationrule aggregationrule = new Aggregationrule(rock);
      write(aggregationrule.getTableName(), result, 0, aggregationrule.toXML_tag());

      final Externalstatement externalstatement = new Externalstatement(rock);
      write(externalstatement.getTableName(), result, 0, externalstatement.toXML_tag());

      final Transformer transformer = new Transformer(rock);
      write(transformer.getTableName(), result, 0, transformer.toXML_tag());

      final Transformation transformation = new Transformation(rock);
      write(transformation.getTableName(), result, 0, transformation.toXML_tag());

      final Supportedvendorrelease svr = new Supportedvendorrelease(rock);
      write(svr.getTableName(), result, 0, svr.toXML_tag());

      final Techpackdependency tpd = new Techpackdependency(rock);
      write(tpd.getTableName(), result, 0, tpd.toXML_tag());

      final Universeclass uc = new Universeclass(rock);
      write(uc.getTableName(), result, 0, uc.toXML_tag());

      final Verificationobject vo = new Verificationobject(rock);
      write(vo.getTableName(), result, 0, vo.toXML_tag());

      final Verificationcondition vc1 = new Verificationcondition(rock);
      write(vc1.getTableName(), result, 0, vc1.toXML_tag());

      final Universetable ut = new Universetable(rock);
      write(ut.getTableName(), result, 0, ut.toXML_tag());

      final Universename universename = new Universename(rock);
      write(universename.getTableName(), result, 0, universename.toXML_tag());

      final Universejoin uj = new Universejoin(rock);
      write(uj.getTableName(), result, 0, uj.toXML_tag());

      final Universeobject uo = new Universeobject(rock);
      write(uo.getTableName(), result, 0, uo.toXML_tag());

      final Universecomputedobject uco = new Universecomputedobject(rock);
      write(uco.getTableName(), result, 0, uco.toXML_tag());

      final Universecondition uc1 = new Universecondition(rock);
      write(uc1.getTableName(), result, 0, uc1.toXML_tag());

      result.append("</techpack>\n");

    } catch (Exception e) {

    }

    return result.toString().replace("null", nullis);
  }

  private String escapeXML(final String s) {
    final StringBuffer str = new StringBuffer();
    final int len = (s != null) ? s.length() : 0;
    for (int i = 0; i < len; i++) {
      final char ch = s.charAt(i);
      switch (ch) {
      case '<':
        str.append("&lt;");
        break;
      case '>':
        str.append("&gt;");
        break;
      case '&':
        str.append("&amp;");
        break;
      case '"':
        str.append("&quot;");
        break;
      case '\'':
        str.append("&apos;");
        break;
      default:
        str.append(ch);
      }
    }
    return str.toString();
  }

  public String createXML(final String versionid, final boolean s, final List<String> createdTypes) {

    this.createdTypes = createdTypes;

    final StringBuffer result = new StringBuffer();
    final long t = System.currentTimeMillis();

    try {

      final Versioning v = new Versioning(rock);
      v.setVersionid(versionid);
      final VersioningFactory vF = new VersioningFactory(rock, v);

      final Versioning versioning = vF.getElementAt(0);

      int tabs = 0;

      result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
      result.append("<techpack>\n");

      // Versioning Iterator
      final Iterator versioningFI = createVersioning(versioning);

      while (versioningFI.hasNext()) {
        final Versioning ver = (Versioning) versioningFI.next();

        // versioning row(s)
        // write(.getTableName(),result, tabs, createVersioningRow(ver));

        if (s) {
          write(ver.getTableName(), result, tabs, ver.toXML_startTag());
        } else {
          write(ver.getTableName(), result, tabs, ver.toXML_tag());
        }

        delta(ver);
        if (s){
        	tabs++;
        }
          
        // Measurementtypeclass
        final Iterator measurementTypeClassFI = createMeasurementTypeClass(ver);

        while (measurementTypeClassFI.hasNext()) {
          final Measurementtypeclass mtc = (Measurementtypeclass) measurementTypeClassFI.next();

          // Measurementtypeclass row(s)
          // write(.getTableName(),result, tabs,
          // createMeasurementTypeClassRow(mtc));
          // write(.getTableName(),result, tabs, mtc.toXML_startTag());
          if (s) {
            write(mtc.getTableName(), result, tabs, mtc.toXML_startTag());
          } else {
            write(mtc.getTableName(), result, tabs, mtc.toXML_tag());
          }
          delta(mtc);
          if (s){
        	  tabs++;
          }
            
          // Measurementtype
          final Iterator measurementtypeFI = createMeasurementType(mtc);

          while (measurementtypeFI.hasNext()) {
            final Measurementtype mt = (Measurementtype) measurementtypeFI.next();

            // Measurementtype row(s)
            // write(.getTableName(),result, tabs,
            // createMeasurementTypeRow(mt));
            // write(.getTableName(),result, tabs, mt.toXML_startTag());
            if (s) {
              write(mt.getTableName(), result, tabs, mt.toXML_startTag());
            } else {
              write(mt.getTableName(), result, tabs, mt.toXML_tag());
            }
            delta(mt);
            if (s){
            	tabs++;
            }
              
            // Measurementkey
            final Iterator measurementkeyFI = createMeasurementKey(mt);

            while (measurementkeyFI.hasNext()) {
              final Measurementkey mk = (Measurementkey) measurementkeyFI.next();

              // Measurementtype row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementKeyRow(mk));
              write(mk.getTableName(), result, tabs, mk.toXML_tag());
              delta(mk);
            }

            // Measurementobjbhsupport
            final Iterator measurementobjbhsupportFI = createMeasurementobjbhsupport(mt);

            while (measurementobjbhsupportFI.hasNext()) {
              final Measurementobjbhsupport mbhs = (Measurementobjbhsupport) measurementobjbhsupportFI.next();

              // Measurementobjbhsupport row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementKeyRow(mbhs));
              write(mbhs.getTableName(), result, tabs, mbhs.toXML_tag());
              delta(mbhs);
            }

            // Measurementcounter
            final Iterator measurementcounterFI = createMeasurementCounter(mt);

            while (measurementcounterFI.hasNext()) {
              final Measurementcounter mc = (Measurementcounter) measurementcounterFI.next();

              // Measurementconter row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementCounterRow(mc));
              write(mc.getTableName(), result, tabs, mc.toXML_tag());
              delta(mc);
            }

            // MeasurementVector
            final Iterator measurementvectorFI = createMeasurementVector(mt);

            while (measurementvectorFI.hasNext()) {
              final Measurementvector mv = (Measurementvector) measurementvectorFI.next();

              // MeasurementVector row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementVectorRow(mv));
              write(mv.getTableName(), result, tabs, mv.toXML_tag());
              delta(mv);
            }

            // MeasurementDeltaCalcSupport
            final Iterator measurementdeltacalcsupportFI = createMeasurementDeltaCalcSupport(mt);

            while (measurementdeltacalcsupportFI.hasNext()) {
              final Measurementdeltacalcsupport mdcs = (Measurementdeltacalcsupport) measurementdeltacalcsupportFI.next();

              // MeasurementDeltaCalcSupport row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementDeltaCalcSupportRow(mdcs));
              write(mdcs.getTableName(), result, tabs, mdcs.toXML_tag());
              delta(mdcs);
            }

            // Measurementtable
            final Iterator measurementtableFI = createMeasurementTable(mt);

            while (measurementtableFI.hasNext()) {
              final Measurementtable mtt = (Measurementtable) measurementtableFI.next();

              // Measurementtable row(s)
              // write(.getTableName(),result, tabs,
              // createMeasurementTableRow(mtt));
              // write(.getTableName(),result, tabs, mtt.toXML_startTag());
              if (s) {
                write(mtt.getTableName(), result, tabs, mtt.toXML_startTag());
              } else {
                write(mtt.getTableName(), result, tabs, mtt.toXML_tag());
              }
              delta(mtt);
              if (s){
            	  tabs++;
              }
                
              // Measurementcolumn
              final Iterator measurementcolumnFI = createMeasurementColumn(mtt);

              while (measurementcolumnFI.hasNext()) {
                final Measurementcolumn mc = (Measurementcolumn) measurementcolumnFI.next();

                // Measurementtable row(s)
                // write(.getTableName(),result, tabs,
                // createMeasurementColumnRow(mc));
                // write(.getTableName(),result, tabs, mc.toXML_startTag());
                if (s) {
                  write(mc.getTableName(), result, tabs, mc.toXML_startTag());
                } else {
                  write(mc.getTableName(), result, tabs, mc.toXML_tag());
                }
                delta(mc);

                  write(mc.getTableName(), result, tabs, mc.toXML_endTag());
                delta(mc);
              }
              if (s){
            	  tabs--;
              } 
              if (s){
            	  write(mtt.getTableName(), result, tabs, mtt.toXML_endTag());
              }   
              delta(mtt);
            }
            if (s){
            	tabs--;
            } 
            if (s){
            	write(mt.getTableName(), result, tabs, mt.toXML_endTag());
            }   
            delta(mt);
          }
          if (s){
        	  tabs--;
          } 
          if (s){
        	  write(mtc.getTableName(), result, tabs, mtc.toXML_endTag());
          }
          delta(mtc);
        }

        // SupportedVendorRelease
        final Iterator svrFI = createSupportedVendorRelease(ver);

        while (svrFI.hasNext()) {
          final Supportedvendorrelease svr = (Supportedvendorrelease) svrFI.next();

          // SupportedVendorRelease row(s)
          // write(.getTableName(),result, tabs,
          // createSupportedVendorReleaseRow(svr));
          write(svr.getTableName(), result, tabs, svr.toXML_tag());
          delta(svr);
        }

        // TechPackDepedency
        final Iterator tpdFI = createTechpackDependency(ver);

        while (tpdFI.hasNext()) {
          final Techpackdependency tpd = (Techpackdependency) tpdFI.next();

          // TechPackDepedenciy row(s)
          // write(.getTableName(),result, tabs,
          // createTechpackDependencyRow(tpd));
          write(tpd.getTableName(), result, tabs, tpd.toXML_tag());
          delta(tpd);
        }

        // UniverseClass
        final Iterator ucFI = createUniverseClass(ver);

        while (ucFI.hasNext()) {
          final Universeclass uc = (Universeclass) ucFI.next();

          // Universeclass row(s)
          // write(.getTableName(),result, tabs, createUniverseClassRow(uc));
          write(uc.getTableName(), result, tabs, uc.toXML_tag());
          delta(uc);
        }

        // Verificationobject
        final Iterator voFI = createVerificationObject(ver);

        while (voFI.hasNext()) {
          final Verificationobject vo = (Verificationobject) voFI.next();

          // Verificationobject row(s)
          // write(.getTableName(),result, tabs,
          // createVerificationObjectRow(vo));
          write(vo.getTableName(), result, tabs, vo.toXML_tag());
          delta(vo);
        }

        // VerificationConditions
        final Iterator vcFI = createVerificationcondition(ver);

        while (vcFI.hasNext()) {
          final Verificationcondition vc = (Verificationcondition) vcFI.next();

          // VerificationConditions row(s)
          // write(.getTableName(),result, tabs,
          // createVerificationconditionRow(vc));
          write(vc.getTableName(), result, tabs, vc.toXML_tag());
          delta(vc);
        }

        // UniverseTable
        final Iterator utFI = createUniverseTable(ver);

        while (utFI.hasNext()) {
          final Universetable ut = (Universetable) utFI.next();

          // UniverseTable row(s)
          // write(.getTableName(),result, tabs, createUniverseTableRow(ut));
          write(ut.getTableName(), result, tabs, ut.toXML_tag());
          delta(ut);
        }

        // UniverseJoin
        final Iterator ujFI = createUniverseJoin(ver);

        while (ujFI.hasNext()) {
          final Universejoin uj = (Universejoin) ujFI.next();

          // UniverseJoin row(s)
          // write(.getTableName(),result, tabs, createUniverseJoinRow(uj));
          write(uj.getTableName(), result, tabs, uj.toXML_tag());
          delta(uj);
        }

        // UniverseObject
        final Iterator uoFI = createUniverseObject(ver);

        while (uoFI.hasNext()) {
          final Universeobject uo = (Universeobject) uoFI.next();

          // UniverseObject row(s)
          // write(.getTableName(),result, tabs, createUniverseObjectRow(uo));
          write(uo.getTableName(), result, tabs, uo.toXML_tag());
          delta(uo);
        }

        // UniverseComputedObject
        final Iterator<Universecomputedobject> ucmpoFI = createUniverseComputedObject(ver);

        while (ucmpoFI.hasNext()) {
          final Universecomputedobject uco = (Universecomputedobject) ucmpoFI.next();

          // UniverseComputedObject row(s)
          // write(.getTableName(),result, tabs,
          // createUniverseComputedObjectRow(uco));
          if (s) {
            write(uco.getTableName(), result, tabs, uco.toXML_startTag());
          } else {
            write(uco.getTableName(), result, tabs, uco.toXML_tag());
          }

          delta(uco);

          // UniverseParameters
          final Iterator<Universeparameters> upFI = createUniverseParameters(uco);

          while (upFI.hasNext()) {
            final Universeparameters up = (Universeparameters) upFI.next();

            // UniverseParameters row(s)
            write(up.getTableName(), result, tabs, up.toXML_tag());
            delta(up);
          }

          if (s){
        	  tabs--;
          } 
          if (s){
        	  write(uco.getTableName(), result, tabs, uco.toXML_endTag());
          } 
        }

        // UniverseFormulas
        final Iterator<Universeformulas> uvoFI = createUniverseFormulas(ver);

        while (uvoFI.hasNext()) {
          final Universeformulas uco = (Universeformulas) uvoFI.next();

          // Universeformulas row(s)
          write(uco.getTableName(), result, tabs, uco.toXML_tag());
          delta(uco);
        }

        // UniverseCondition
        final Iterator ucoFI = createUniverseCondition(ver);

        while (ucoFI.hasNext()) {
          final Universecondition uco = (Universecondition) ucoFI.next();

          // UniverseCondition row(s)
          // write(.getTableName(),result, tabs,
          // createUniverseConditionRow(uco));
          write(uco.getTableName(), result, tabs, uco.toXML_tag());
          delta(uco);
        }

        // Transformer
        final Iterator transfomerFI = createTransformer(ver);

        while (transfomerFI.hasNext()) {
          final Transformer tr = (Transformer) transfomerFI.next();

          // Transformer row(s)
          // write(.getTableName(),result, tabs, createTransformerRow(tr));
          // write(.getTableName(),result, tabs, tr.toXML_startTag());
          if (s) {
            write(tr.getTableName(), result, tabs, tr.toXML_startTag());
          } else {
            write(tr.getTableName(), result, tabs, tr.toXML_tag());
          }
          delta(tr);
          if (s){
        	  tabs++;
          }
            
          // Transformation
          final Iterator transformationFI = createTransformation(tr);

          while (transformationFI.hasNext()) {
            final Transformation trf = (Transformation) transformationFI.next();

            // Transformation row(s)
            // write(.getTableName(),result, tabs,
            // createTransformationRow(trf));
            write(trf.getTableName(), result, tabs, trf.toXML_tag());
            delta(trf);

          }
          if (s){
        	  tabs--;
          } 
          if (s){
        	  write(tr.getTableName(), result, tabs, tr.toXML_endTag());
          }
          delta(tr);
        }

        // Referencetable
        final Iterator referencetableFi = createReferenceTable(ver);

        while (referencetableFi.hasNext()) {
          final Referencetable rt = (Referencetable) referencetableFi.next();

          // Referencetable row(s)
          // write(.getTableName(),result, tabs, createReferenceTableRow(rt));
          // write(.getTableName(),result, tabs, rt.toXML_startTag());
          if (s) {
            write(rt.getTableName(), result, tabs, rt.toXML_startTag());
          } else {
            write(rt.getTableName(), result, tabs, rt.toXML_tag());
          }
          delta(rt);
          if (s){
        	  tabs++;
          }
            
          // Referencecolumn
          final Iterator referencecolumnFI = createReferenceColumn(rt);

          while (referencecolumnFI.hasNext()) {
            final Referencecolumn rc = (Referencecolumn) referencecolumnFI.next();

            // Referencecolumn row(s)
            // write(.getTableName(),result, tabs,
            // createReferenceColumnRow(rc));
            write(rc.getTableName(), result, tabs, rc.toXML_tag());
            delta(rc);

          }
          if (s){
        	  tabs--;
          }
          if (s){
        	  write(rt.getTableName(), result, tabs, rt.toXML_endTag());
          } 
          delta(rt);
        }

        // Dataformat
        final Iterator dataformatFi = createDataFormat(ver);

        while (dataformatFi.hasNext()) {
          final Dataformat df = (Dataformat) dataformatFi.next();

          // Dataformat row(s)
          // write(.getTableName(),result, tabs, createDataFormatRow(df));
          // write(.getTableName(),result, tabs, df.toXML_startTag());
          if (s) {
            write(df.getTableName(), result, tabs, df.toXML_startTag());
          } else {
            write(df.getTableName(), result, tabs, df.toXML_tag());
          }
          delta(df);
          if (s){
        	  tabs++;
          }
            
          // Defaulttags
          final Iterator defaulttagsFI = createDefaultTags(df);

          while (defaulttagsFI.hasNext()) {
            final Defaulttags dft = (Defaulttags) defaulttagsFI.next();

            // Defaulttags row(s)
            // write(.getTableName(),result, tabs, createDefaultTagsRow(dft));
            write(dft.getTableName(), result, tabs, dft.toXML_tag());
            delta(dft);

          }

          // Dataitem
          final Iterator dataItemFI = createDataItem(df);

          while (dataItemFI.hasNext()) {
            final Dataitem di = (Dataitem) dataItemFI.next();

            // Dataitem row(s)
            // write(.getTableName(),result, tabs, createDataItemRow(di));
            write(di.getTableName(), result, tabs, di.toXML_tag());
            delta(di);

          }
          if (s){
        	  tabs--;
          }
          if (s){
        	  write(df.getTableName(), result, tabs, df.toXML_endTag());
          }
          delta(df);
        }

        // Externalstatement
        final Iterator externalstatementFI = createExternalStatement(ver);

        while (externalstatementFI.hasNext()) {
          final Externalstatement ex = (Externalstatement) externalstatementFI.next();

          // Externalstatement row(s)
          // write(.getTableName(),result, tabs,
          // createExternalStatementRow(ex));
          write(ex.getTableName(), result, tabs, ex.toXML_tag());
          delta(ex);

        }

        // UniverseName
        final Iterator UniversenameFI = createUniverseName(ver);

        while (UniversenameFI.hasNext()) {
          final Universename un = (Universename) UniversenameFI.next();

          // UniverseName row(s)
          // write(.getTableName(),result, tabs, createUniverseNameRow(un));
          write(un.getTableName(), result, tabs, un.toXML_tag());
          delta(un);

        }

        // Aggregation
        final Iterator aggregationFI = createAggregation(ver);

        while (aggregationFI.hasNext()) {
          final Aggregation ag = (Aggregation) aggregationFI.next();

          // Aggregation row(s)
          // write(.getTableName(),result, tabs, createAggregationRow(ag));
          // write(.getTableName(),result, tabs, ag.toXML_startTag());
          if (s) {
            write(ag.getTableName(), result, tabs, ag.toXML_startTag());
          } else {
            write(ag.getTableName(), result, tabs, ag.toXML_tag());
          }
          delta(ag);
          if (s){
        	  tabs++;
          }
          // Aggregationrule
          final Iterator AggregationruleFI = createAggregationRule(ag);

          while (AggregationruleFI.hasNext()) {
            final Aggregationrule agr = (Aggregationrule) AggregationruleFI.next();

            // Aggregationrule row(s)
            // write(.getTableName(),result, tabs,
            // createAggregationRuleRow(agr));
            write(agr.getTableName(), result, tabs, agr.toXML_tag());
            delta(agr);

          }
          if (s){
        	  tabs--;
          }
          if (s){
        	  write(ag.getTableName(), result, tabs, ag.toXML_endTag());
          }
          delta(ag);
        }
        if (s){
        	tabs--;
        }
          
        if (s){
        	write(ver.getTableName(), result, tabs, ver.toXML_endTag());
        }
        delta(ver);
      }

    } catch (Exception e) {
      log.severe(e.getMessage());
    }

    result.append("</techpack>\n");
    log.finest("XML creation took " + (System.currentTimeMillis() - t) + " ms.");
    return result.toString();
  }

  private Iterator createVersioning(final Versioning versioning) throws Exception {

    final VersioningFactory versioningF = new VersioningFactory(rock, versioning);

    final Iterator versioningFI = versioningF.get().iterator();

    return versioningFI;

  }

  private Iterator createMeasurementTypeClass(final Versioning versioning) throws Exception {

    final Measurementtypeclass measurementTypeClass = new Measurementtypeclass(rock);
    measurementTypeClass.setVersionid(versioning.getVersionid());
    final MeasurementtypeclassFactory measurementTypeClassF = new MeasurementtypeclassFactory(rock, measurementTypeClass);

    final Iterator measurementTypeClassFI = measurementTypeClassF.get().iterator();

    return measurementTypeClassFI;
  }

  private Iterator createMeasurementType(final Measurementtypeclass mtc) throws Exception {

    final Measurementtype measurementtype = new Measurementtype(rock);
    measurementtype.setTypeclassid(mtc.getTypeclassid());
    measurementtype.setVersionid(mtc.getVersionid());
    final MeasurementtypeFactory measurementtypeF = new MeasurementtypeFactory(rock, measurementtype);

    final Iterator measurementtypeFI = measurementtypeF.get().iterator();

    return measurementtypeFI;
  }

  private Iterator<Measurementtype> createMeasurementKey(final Measurementtype mt) throws Exception {

    final Measurementkey measurementkey = new Measurementkey(rock);
    measurementkey.setTypeid(mt.getTypeid());
    final MeasurementkeyFactory measurementkeyF = new MeasurementkeyFactory(rock, measurementkey);

    final Iterator measurementkeyFI = measurementkeyF.get().iterator();

    return measurementkeyFI;

  }

  private Iterator createMeasurementobjbhsupport(final Measurementtype mt) throws Exception {

    final Measurementobjbhsupport measurementobjbhsupport = new Measurementobjbhsupport(rock);
    measurementobjbhsupport.setTypeid(mt.getTypeid());
    final MeasurementobjbhsupportFactory measurementobjbhsupportF = new MeasurementobjbhsupportFactory(rock,
        measurementobjbhsupport);

    final Iterator measurementobjbhsupportFI = measurementobjbhsupportF.get().iterator();

    return measurementobjbhsupportFI;

  }

  private Iterator createMeasurementCounter(final Measurementtype mt) throws Exception {

    final Measurementcounter measurementcounter = new Measurementcounter(rock);
    measurementcounter.setTypeid(mt.getTypeid());
    final MeasurementcounterFactory measurementcounterF = new MeasurementcounterFactory(rock, measurementcounter);

    final Iterator measurementcounterFI = measurementcounterF.get().iterator();

    return measurementcounterFI;

  }

  private Iterator createMeasurementTable(final Measurementtype mt) throws Exception {

    final Measurementtable measurementtable = new Measurementtable(rock);
    measurementtable.setTypeid(mt.getTypeid());
    final MeasurementtableFactory measurementcounterF = new MeasurementtableFactory(rock, measurementtable);

    final Iterator measurementcounterFI = measurementcounterF.get().iterator();

    return measurementcounterFI;

  }

  private Iterator createMeasurementVector(final Measurementtype mt) throws Exception {

    final Measurementvector measurementvector = new Measurementvector(rock);
    measurementvector.setTypeid(mt.getTypeid());
    final MeasurementvectorFactory measurementvectorF = new MeasurementvectorFactory(rock, measurementvector);

    final Iterator measurementvectorFI = measurementvectorF.get().iterator();

    return measurementvectorFI;

  }

  private Iterator createMeasurementDeltaCalcSupport(final Measurementtype mt) throws Exception {

    final Measurementdeltacalcsupport measurementdeltacalcsupport = new Measurementdeltacalcsupport(rock);
    measurementdeltacalcsupport.setTypeid(mt.getTypeid());
    final MeasurementdeltacalcsupportFactory measurementdeltacalcsupportF = new MeasurementdeltacalcsupportFactory(rock,
        measurementdeltacalcsupport);

    final Iterator measurementdeltacalcsupportFI = measurementdeltacalcsupportF.get().iterator();

    return measurementdeltacalcsupportFI;

  }

  private Iterator createMeasurementColumn(final Measurementtable mt) throws Exception {

    final Measurementcolumn measurementcolumn = new Measurementcolumn(rock);
    measurementcolumn.setMtableid(mt.getMtableid());
    final MeasurementcolumnFactory measurementcolumnF = new MeasurementcolumnFactory(rock, measurementcolumn);

    final Iterator measurementcolumnFI = measurementcolumnF.get().iterator();

    return measurementcolumnFI;

  }

  private Iterator createReferenceTable(final Versioning versioning) throws Exception {

    final Referencetable referencetable = new Referencetable(rock);
    referencetable.setVersionid(versioning.getVersionid());
    final ReferencetableFactory referencetableF = new ReferencetableFactory(rock, referencetable);

    final Iterator referencetableFI = referencetableF.get().iterator();

    return referencetableFI;

  }

  private Iterator createReferenceColumn(final Referencetable referenceTable) throws Exception {

    final Referencecolumn referencecolumn = new Referencecolumn(rock);
    referencecolumn.setTypeid(referenceTable.getTypeid());
    final ReferencecolumnFactory referencecolumnF = new ReferencecolumnFactory(rock, referencecolumn);

    final Iterator referencecolumnFI = referencecolumnF.get().iterator();

    return referencecolumnFI;

  }

  private Iterator createDataFormat(final Versioning versioning) throws Exception {

    final Dataformat dataformat = new Dataformat(rock);
    dataformat.setVersionid(versioning.getVersionid());
    final DataformatFactory dataformatF = new DataformatFactory(rock, dataformat);

    final Iterator dataformatFI = dataformatF.get().iterator();

    return dataformatFI;

  }

  private Iterator createDefaultTags(final Dataformat dataFormat) throws Exception {

    final Defaulttags defaulttags = new Defaulttags(rock);
    defaulttags.setDataformatid(dataFormat.getDataformatid());
    final DefaulttagsFactory defaulttagsF = new DefaulttagsFactory(rock, defaulttags);

    final Iterator defaulttagsFI = defaulttagsF.get().iterator();

    return defaulttagsFI;

  }

  private Iterator createDataItem(final Dataformat dataFormat) throws Exception {

    final Dataitem dataitem = new Dataitem(rock);
    dataitem.setDataformatid(dataFormat.getDataformatid());
    final DataitemFactory dataitemF = new DataitemFactory(rock, dataitem);

    final Iterator dataitemFI = dataitemF.get().iterator();

    return dataitemFI;
  }

  private Iterator createAggregation(final Versioning versioning) throws Exception {

    final Aggregation aggregation = new Aggregation(rock);
    aggregation.setVersionid(versioning.getVersionid());
    final AggregationFactory aggregationF = new AggregationFactory(rock, aggregation);

    final Iterator aggregationFI = aggregationF.get().iterator();

    return aggregationFI;

  }

  private Iterator createAggregationRule(final Aggregation aggregation) throws Exception {

    final Aggregationrule aggregationrule = new Aggregationrule(rock);
    aggregationrule.setAggregation(aggregation.getAggregation());
    aggregationrule.setVersionid(aggregation.getVersionid());
    final AggregationruleFactory aggregationruleF = new AggregationruleFactory(rock, aggregationrule);

    final Iterator aggregationruleFI = aggregationruleF.get().iterator();

    return aggregationruleFI;

  }

  private Iterator createExternalStatement(final Versioning versioning) throws Exception {

    final Externalstatement externalstatement = new Externalstatement(rock);
    externalstatement.setVersionid(versioning.getVersionid());
    final ExternalstatementFactory externalstatementF = new ExternalstatementFactory(rock, externalstatement);

    final Iterator externalstatementFI = externalstatementF.get().iterator();

    return externalstatementFI;

  }

  private Iterator createTransformer(final Versioning versioning) throws Exception {

    final Transformer transformer = new Transformer(rock);
    transformer.setVersionid(versioning.getVersionid());
    final TransformerFactory transformerF = new TransformerFactory(rock, transformer);

    final Iterator transformerFI = transformerF.get().iterator();

    return transformerFI;

  }

  private Iterator createTransformation(final Transformer transformer) throws Exception {

    final Transformation transformation = new Transformation(rock);
    transformation.setTransformerid(transformer.getTransformerid());
    final TransformationFactory transformationF = new TransformationFactory(rock, transformation);

    final Iterator transformationFI = transformationF.get().iterator();

    return transformationFI;

  }

  private Iterator createSupportedVendorRelease(final Versioning versioning) throws Exception {

    final Supportedvendorrelease svr = new Supportedvendorrelease(rock);
    svr.setVersionid(versioning.getVersionid());
    final SupportedvendorreleaseFactory svrF = new SupportedvendorreleaseFactory(rock, svr);

    final Iterator svrFI = svrF.get().iterator();

    return svrFI;

  }

  private Iterator createTechpackDependency(final Versioning versioning) throws Exception {

    final Techpackdependency tpd = new Techpackdependency(rock);
    tpd.setVersionid(versioning.getVersionid());
    final TechpackdependencyFactory tpdF = new TechpackdependencyFactory(rock, tpd);

    final Iterator tpdFI = tpdF.get().iterator();

    return tpdFI;

  }

  private Iterator createUniverseClass(final Versioning versioning) throws Exception {

    final Universeclass uc = new Universeclass(rock);
    uc.setVersionid(versioning.getVersionid());
    final UniverseclassFactory ucF = new UniverseclassFactory(rock, uc);

    final Iterator ucFI = ucF.get().iterator();

    return ucFI;

  }

  private Iterator createVerificationObject(final Versioning versioning) throws Exception {

    final Verificationobject vo = new Verificationobject(rock);
    vo.setVersionid(versioning.getVersionid());
    final VerificationobjectFactory voF = new VerificationobjectFactory(rock, vo);

    final Iterator voFI = voF.get().iterator();

    return voFI;

  }

  private Iterator createVerificationcondition(final Versioning versioning) throws Exception {

    final Verificationcondition vc = new Verificationcondition(rock);
    vc.setVersionid(versioning.getVersionid());
    final VerificationconditionFactory vcF = new VerificationconditionFactory(rock, vc);

    final Iterator vcFI = vcF.get().iterator();

    return vcFI;

  }

  private Iterator createUniverseTable(final Versioning versioning) throws Exception {

    final Universetable ut = new Universetable(rock);
    ut.setVersionid(versioning.getVersionid());
    final UniversetableFactory utF = new UniversetableFactory(rock, ut);

    final Iterator utFI = utF.get().iterator();

    return utFI;

  }

  private Iterator createUniverseName(final Versioning versioning) throws Exception {

    final Universename universename = new Universename(rock);
    universename.setVersionid(versioning.getVersionid());
    final UniversenameFactory universenameF = new UniversenameFactory(rock, universename);

    final Iterator universenameFI = universenameF.get().iterator();

    return universenameFI;

  }

  private Iterator createUniverseJoin(final Versioning versioning) throws Exception {

    final Universejoin uj = new Universejoin(rock);
    uj.setVersionid(versioning.getVersionid());
    final UniversejoinFactory ujF = new UniversejoinFactory(rock, uj);

    final Iterator ujFI = ujF.get().iterator();

    return ujFI;

  }

  private Iterator createUniverseObject(final Versioning versioning) throws Exception {

    final Universeobject uo = new Universeobject(rock);
    uo.setVersionid(versioning.getVersionid());
    final UniverseobjectFactory uoF = new UniverseobjectFactory(rock, uo);

    final Iterator uoFI = uoF.get().iterator();

    return uoFI;

  }

  private Iterator<Universecomputedobject> createUniverseComputedObject(final Versioning versioning) throws Exception {

    final Universecomputedobject uco = new Universecomputedobject(rock);
    uco.setVersionid(versioning.getVersionid());
    final UniversecomputedobjectFactory ucoF = new UniversecomputedobjectFactory(rock, uco);

    final Iterator<Universecomputedobject> ucoFI = ucoF.get().iterator();

    return ucoFI;

  }

  private Iterator<Universeparameters> createUniverseParameters(final Universecomputedobject ucobj) throws Exception {

    final Universeparameters uco = new Universeparameters(rock);
    uco.setVersionid(ucobj.getVersionid());
    uco.setClassname(ucobj.getClassname());
    uco.setObjectname(ucobj.getObjectname());
    uco.setUniverseextension(ucobj.getUniverseextension());

    final UniverseparametersFactory ucoF = new UniverseparametersFactory(rock, uco);

    final Iterator<Universeparameters> ucoFI = ucoF.get().iterator();

    return ucoFI;

  }

  private Iterator createUniverseCondition(final Versioning versioning) throws Exception {

    final Universecondition uc = new Universecondition(rock);
    uc.setVersionid(versioning.getVersionid());
    final UniverseconditionFactory ucF = new UniverseconditionFactory(rock, uc);

    final Iterator ucFI = ucF.get().iterator();

    return ucFI;

  }

  private Iterator<Universeformulas> createUniverseFormulas(final Versioning versioning) throws Exception {

    final Universeformulas uc = new Universeformulas(rock);
    uc.setVersionid(versioning.getVersionid());
    final UniverseformulasFactory ucF = new UniverseformulasFactory(rock, uc);

    final Iterator<Universeformulas> ucFI = ucF.get().iterator();

    return ucFI;

  }

}
