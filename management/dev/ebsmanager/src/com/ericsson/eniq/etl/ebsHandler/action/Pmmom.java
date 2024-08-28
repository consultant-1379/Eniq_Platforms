package com.ericsson.eniq.etl.ebsHandler.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.repository.dwhrep.Measurementtype;
import com.distocraft.dc5000.repository.dwhrep.Universeformulas;
import com.ericsson.eniq.common.ENIQEntityResolver;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;


/**
 * Parses the mom file.
 * The only checking done if the measObjClass is valid for the techpack.
 *
 * TODO Extend the mom validity checks to include:
 *  - storage size is valid
 *  - agg formula is valid
 *  - agg type is valid
 *  - formula function exists in db (Universeformula, use getFormulas() to check)
 *  - cross reference the counter measName with the formula arg measName
 *
 */
public class Pmmom extends DefaultHandler {


  private String charValue;

  private Vector<Meastype> meastypes;

  private String object = "";

  private String versionid;

  private String techpackName = "";

  private static final String FORMULA = "formula";

  private static final String COUNTER = "counter";

  private Map<String, Measurementtype> meastypesMap = null;

  private Vector<Map<String, Universeformulas>> formulas = null;

  private static final String MOMDATATAGPREFIX = "MOM_";

  private String pmMimVersion;

  private String applicationVersion;

  private static final Map<Character, String> eMap = new HashMap<Character, String>();
    private final List<String> supportedMeasTypes = new ArrayList<String>();
    private String inName = null;

      private Locator locator;
  static {
      eMap.put('<', "&lt;");
      eMap.put('>', "&gt;");
      eMap.put('&', "&amp;");
      eMap.put('"', "&quot;");
      eMap.put('\'', "&apos;");
  }

  public Pmmom() {
  }

    @Override
    public void setDocumentLocator(final Locator loc) {
        locator = loc;
    }

    public void read(File file) throws Exception {
        inName = file.getAbsolutePath();
        read(new InputSource(unzip(file)));
  }

  public void read(String str) throws Exception {
      inName = "StringStream";
    read(new InputSource(new StringReader(str)));
  }

  private void read(InputSource is) throws Exception {

      // do this here as the teckpack name & types should be set.
      for(Measurementtype mt : meastypesMap.values()){
          final String spType = mt.getTypename();
          if(!supportedMeasTypes.contains(spType)){
            supportedMeasTypes.add(spType);
          }
      }

    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
    xmlReader.setContentHandler(this);
    xmlReader.setErrorHandler(this);

    meastypes = new Vector<Meastype>();

    xmlReader.setEntityResolver(new ENIQEntityResolver("Test"));

    xmlReader.parse(is);

  }

  public void startDocument() {

  }

  public void endDocument() throws SAXException {
		// go through all the types that were defined in the mom file and generate
		// a list of undefined measurement types --> these get deleted...
		for(String mType : supportedMeasTypes){
			boolean defined = false;
			final String mocName = mType.substring(getTechpackName().length()+1);
			for(Meastype mt : meastypes){
				if(mt.getName().equalsIgnoreCase(mocName)){
					defined = true;
					break;
				}
			}
			if(!defined){
				final Meastype m = new Meastype();
				m.setName(mocName.toLowerCase());
				//For TR HN64621 fix
			}
		}
  }

    private String getCurrentLocation(){
        if (locator != null) {
            final StringBuilder msg = new StringBuilder();
            msg.append(" Line:").append(locator.getLineNumber());
            msg.append(" Column:").append(locator.getColumnNumber());
            msg.append(" Source:").append(inName);
            return msg.toString();
        } else {
            return "";
        }
    }

    private void checkSupported(final String mtName) throws SAXParseException {
    	final String toCheck = MOMDATATAGPREFIX + mtName;
        if(!meastypesMap.containsKey(toCheck)){            
        	final StringBuilder msg = new StringBuilder();
            msg.append("measObjClass type '").append(mtName).append("' is not supported in ").append(getTechpackName());
            msg.append(getCurrentLocation());
            throw new SAXParseException(msg.toString(), null);
        }
    }
  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
      throws SAXException {

    charValue = "";

    if (qName.equals("measObjClass")) {
      final Meastype m = new Meastype();
        final String mtName = atts.getValue("name");
        m.setName(mtName);
        checkSupported(m.getName());
        meastypes.add(m);
    }

    if (qName.equals("group")) {
      Group g = new Group();
      g.setName(atts.getValue("name"));
      meastypes.lastElement().addGroup(g);
    }

    if (qName.equals("formula")) {
      object = FORMULA;
      Formula f = new Formula();
      f.setName(atts.getValue("name"));
      meastypes.lastElement().getLastGroup().addFormula(f);
    }

    if (qName.equals("counter")) {
      object = COUNTER;
      Counter c = new Counter();
      meastypes.lastElement().getLastGroup().addCounter(c);
    }

  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {

    if (qName.equals("pmMimVersion")) {
      pmMimVersion = charValue;
    }

    if (qName.equals("applicationVersion")) {
      applicationVersion = charValue;
    }

    if (object.equals(COUNTER) && qName.equals("measName")) {
      meastypes.lastElement().getLastGroup().getLastCounter().setName(charValue);
    }

    if (object.equals(COUNTER) && qName.equals("description")) {
      meastypes.lastElement().getLastGroup().getLastCounter().setDesc(charValue);
    }

    if (object.equals(COUNTER) && qName.equals("size")) {
      meastypes.lastElement().getLastGroup().getLastCounter().setSize(charValue);
    }

    if (object.equals(COUNTER) && qName.equals("aggregation")) {
      if ("average".equalsIgnoreCase(charValue)) {
        charValue = "AVG";
      }
      meastypes.lastElement().getLastGroup().getLastCounter().setAggregation(charValue);
    }

    if (object.equals(COUNTER) && qName.equals("type")) {
      meastypes.lastElement().getLastGroup().getLastCounter().setType(charValue);
    }

    if (object.equals(FORMULA) && qName.equals("measName")) {
      meastypes.lastElement().getLastGroup().getLastFormula().setMeasName(charValue);
    }

    if (object.equals(FORMULA) && qName.equals("function")) {
        final Formula f = meastypes.lastElement().getLastGroup().getLastFormula();
        f.setFunction(charValue);
        /*
        Uncomment if the function should actually exist.
        if(getFormulas(f.getFunction()) == null){
            final StringBuilder msg = new StringBuilder();
            msg.append("Function type '").append(f.getFunction()).append("' is not supported in ").append(getTechpackName());
            msg.append(getCurrentLocation());
            throw new SAXParseException(msg.toString(), null);
        }*/
    }

    if (object.equals(FORMULA) && qName.equals("argument")) {
      meastypes.lastElement().getLastGroup().getLastFormula().addArgument(charValue);
    }

  }

  public void characters(final char ch[], final int start, final int length) {
    final StringBuffer charBuffer = new StringBuffer(length);
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charBuffer.append(ch[i]);
      }
    }
    charValue += charBuffer;
  }

  private Node getNode(String name, Document doc) {
    NodeList nl = doc.getChildNodes().item(0).getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.item(i).getNodeName().equals(name)) {
        return nl.item(i);
      }
    }
    return null;
  }

  private String getAttributes(Node n) {
    // columns
    String s = "";
    if (n != null && n.getAttributes() != null) {

      for (int a = 0; a < n.getAttributes().getLength(); a++) {
        n.getAttributes().item(a);
        String as = n.getAttributes().item(a).getNodeValue();
        if (as.length() == 0)
          as = " ";
        s += " " + n.getAttributes().item(a).getNodeName() + "=\"" + escapeXML(as) + "\"";
      }
    }
    return s;
  }
    private String escapeXML(final String s) {
        if(s == null || s.length() == 0){
            return "";
        }
        final StringBuffer str = new StringBuffer();
        for (int i = 0; i < s.length() ; i++) {
            char ch = s.charAt(i);
            if(eMap.containsKey(ch)){
                str.append(eMap.get(ch));
            } else {
                str.append(ch);
            }
        }
        return str.toString();
    }


  public String toXML(String str) {
    return toXML(readXML(str));
  }

  private Measurementtype getMeasType(String tag) {

    if (meastypesMap != null) {
      if (meastypesMap.containsKey(MOMDATATAGPREFIX + tag)) {
        return meastypesMap.get(MOMDATATAGPREFIX + tag);
      } else {
        return new Measurementtype(null);
      }
    } else {
      return new Measurementtype(null);
    }
  }

  private String toXML(Document doc) {
    return toXML(doc, null);
  }

  private Universeformulas getFormulas(String formulaName) {
    if (formulas != null) {
        for (Map<String, Universeformulas> m : formulas) {
            if (m.containsKey(formulaName)) {
                return m.get(formulaName);
            }
        }
    }

    return null;
  }

  public String toString() {

    StringBuffer r = new StringBuffer();

      for (Meastype meas : meastypes) {
          r.append(meas.getName()).append("\n");
          for (Group group : meas.getGroups()) {
              if (group.getName() != null) {
                  r.append("\t").append(group.getName()).append("\n");
              }
              for (Counter counter : group.getCounters()) {
                  r.append("\t\t").append(counter.getName());
                  if (counter.getDesc() != null && counter.getDesc().length() > 0) {
                      r.append("\t").append(counter.getDesc());
                  }
                  r.append("\n");
              }
          }
      }
    return r.toString();
  }

  /**
   * Creates an Universeclass xml row.
   * 
   * 
   * @param order .
   * @param classname .
   * @param parent .
   * @param doc .
   * @return .
   */
  private String writeUniverseClass(int order, String classname, String parent, Document doc) {

    // Universe class
    Node UniverseclassNode = getNode("Universeclass", doc);

    // VERSIONID
    UniverseclassNode.getAttributes().getNamedItem("VERSIONID").setNodeValue("'" + versionid + "'");

    // CLASSNAME
    UniverseclassNode.getAttributes().getNamedItem("CLASSNAME").setNodeValue("'" + classname + "'");

    // UNIVERSEEXTENSION
    UniverseclassNode.getAttributes().getNamedItem("UNIVERSEEXTENSION").setNodeValue("'ALL'");

    // DESCRIPTION
    UniverseclassNode.getAttributes().getNamedItem("DESCRIPTION").setNodeValue("''");

    // PARENT
    UniverseclassNode.getAttributes().getNamedItem("PARENT").setNodeValue("'" + parent + "'");

    // OBJ_BH_REL
    UniverseclassNode.getAttributes().getNamedItem("OBJ_BH_REL").setNodeValue("0");

    // ELEM_BH_REL
    UniverseclassNode.getAttributes().getNamedItem("ELEM_BH_REL").setNodeValue("0");

    // INHERITANCE
    UniverseclassNode.getAttributes().getNamedItem("INHERITANCE").setNodeValue("0");

    // ORDERNRO
    UniverseclassNode.getAttributes().getNamedItem("ORDERNRO").setNodeValue(order + "");

    return "\t\t<Universeclass" + getAttributes(UniverseclassNode) + " />\n";
  }

  /**
   * 
   * Creates an Measurementcounter xml row.
   * 
   * @param collNro .
   * @param typeid .
   * @param counter .
   * @param defaultData .
   * @param doc .
   * @return .
   */
  private String writeMeasCounter(int collNro, String typeid, Counter counter, Map<String, String> defaultData,
      Document doc) {

    // Counter
    Node measCounterNode = getNode("Measurementcounter", doc);

    if (defaultData != null) {
        for (String key : defaultData.keySet()) {
            String data = defaultData.get(key);
            measCounterNode.getAttributes().getNamedItem(key).setNodeValue(data);
        }
    }

    measCounterNode.getAttributes().getNamedItem("COLNUMBER").setNodeValue("" + collNro);

    measCounterNode.getAttributes().getNamedItem("TYPEID").setNodeValue("'" + typeid + "'");

    measCounterNode.getAttributes().getNamedItem("DATANAME").setNodeValue("'" + counter.getName() + "'");

    measCounterNode.getAttributes().getNamedItem("UNIVOBJECT").setNodeValue("'" + counter.getName() + "'");

    measCounterNode.getAttributes().getNamedItem("DESCRIPTION").setNodeValue("'" + counter.getDesc() + "'");

    // numeric(18.0) -> 'numeric' and '18.0'
    String datatype = counter.getSize().substring(0, counter.getSize().indexOf("("));
    String datasizetmp = counter.getSize()
        .substring(counter.getSize().indexOf("(") + 1, counter.getSize().indexOf(")"));

    String datasize = datasizetmp.substring(0, datasizetmp.indexOf(","));
    String datascale = datasizetmp.substring(datasizetmp.indexOf(",") + 1);

    measCounterNode.getAttributes().getNamedItem("DATASIZE").setNodeValue(datasize);

    measCounterNode.getAttributes().getNamedItem("DATASCALE").setNodeValue(datascale);

    measCounterNode.getAttributes().getNamedItem("DATATYPE").setNodeValue("'" + datatype + "'");

    measCounterNode.getAttributes().getNamedItem("COUNTAGGREGATION").setNodeValue("'" + counter.getAggregation() + "'");

    measCounterNode.getAttributes().getNamedItem("COUNTERTYPE").setNodeValue("'" + counter.getType() + "'");

    measCounterNode.getAttributes().getNamedItem("COUNTERPROCESS").setNodeValue("'" + counter.getType() + "'");

    measCounterNode.getAttributes().getNamedItem("TIMEAGGREGATION").setNodeValue("'" + counter.getAggregation() + "'");

    measCounterNode.getAttributes().getNamedItem("GROUPAGGREGATION").setNodeValue("'" + counter.getAggregation() + "'");

    measCounterNode.getAttributes().getNamedItem("INCLUDESQL").setNodeValue("" + 1);

    measCounterNode.getAttributes().getNamedItem("DATAID").setNodeValue("'" + counter.getName() + "'");

    return "\t\t<Measurementcounter" + getAttributes(measCounterNode) + " />\n";
  }

  /**
   * 
   * Creates an Universeparameters xml row.
   * 
   * @param typename .
   * @param classname .
   * @param argument .
   * @param order .
   * @param formula .
   * @param doc .
   * @return .
   */
  private String writeUniverseParameters(String typename, String classname, String argument, int order,
      Formula formula, Document doc) {

    // Universe computed object
    Node UniverseParametersNode = getNode("Universeparameters", doc);

    // VERSIONID
    UniverseParametersNode.getAttributes().getNamedItem("VERSIONID").setNodeValue("'" + versionid + "'");

    // CLASSNAME
    UniverseParametersNode.getAttributes().getNamedItem("CLASSNAME").setNodeValue("'" + classname + "'");

    // OBJECTNAME
    UniverseParametersNode.getAttributes().getNamedItem("OBJECTNAME").setNodeValue("'" + formula.getName() + "'");

    // UNIVERSEEXTENSION
    UniverseParametersNode.getAttributes().getNamedItem("UNIVERSEEXTENSION").setNodeValue("'ALL'");

    // ORDERNRO
    UniverseParametersNode.getAttributes().getNamedItem("ORDERNRO").setNodeValue("" + order);

    // NAME
    UniverseParametersNode.getAttributes().getNamedItem("NAME").setNodeValue("'" + argument + "'");

    // TYPENAME
    UniverseParametersNode.getAttributes().getNamedItem("TYPENAME").setNodeValue("'" + typename + "'");

    return "\t\t\t<Universeparameters" + getAttributes(UniverseParametersNode) + " />\n";

  }

  /**
   * 
   * Creates an Universecomputedobject xml row.
   * 
   * @param order .
   * @param classname .
   * @param formula .
   * @param doc .
   * @return .
   */
  private String writeUniversecomputedobject(int order, String classname, Formula formula, Document doc) {

    Universeformulas unf = getFormulas(formula.getFunction());

    // Universe computed object
    Node UniversecomputedobjectNode = getNode("Universecomputedobject", doc);

    // VERSIONID
    UniversecomputedobjectNode.getAttributes().getNamedItem("VERSIONID").setNodeValue("'" + versionid + "'");

    // CLASSNAME
    UniversecomputedobjectNode.getAttributes().getNamedItem("CLASSNAME").setNodeValue("'" + classname + "'");

    // UNIVERSEEXTENSION
    UniversecomputedobjectNode.getAttributes().getNamedItem("UNIVERSEEXTENSION").setNodeValue("'ALL'");

    // OBJECTNAME
    UniversecomputedobjectNode.getAttributes().getNamedItem("OBJECTNAME").setNodeValue("'" + formula.getName() + "'");

    // DESCRIPTION
    UniversecomputedobjectNode.getAttributes().getNamedItem("DESCRIPTION").setNodeValue("''");

    if (unf == null) {

      // OBJECTTYPE
      UniversecomputedobjectNode.getAttributes().getNamedItem("OBJECTTYPE").setNodeValue("''");

      // QUALIFICATION
      UniversecomputedobjectNode.getAttributes().getNamedItem("QUALIFICATION").setNodeValue("''");

      // AGGREGATION
      UniversecomputedobjectNode.getAttributes().getNamedItem("AGGREGATION").setNodeValue("''");

    } else {

      // OBJECTTYPE
      UniversecomputedobjectNode.getAttributes().getNamedItem("OBJECTTYPE").setNodeValue(
          "'" + unf.getObjecttype() + "'");

      // QUALIFICATION
      UniversecomputedobjectNode.getAttributes().getNamedItem("QUALIFICATION").setNodeValue(
          "'" + unf.getQualification() + "'");

      // AGGREGATION
      UniversecomputedobjectNode.getAttributes().getNamedItem("AGGREGATION").setNodeValue(
          "'" + unf.getAggregation() + "'");

    }

    // OBJSELECT
    UniversecomputedobjectNode.getAttributes().getNamedItem("OBJSELECT")
        .setNodeValue("'" + formula.getFunction() + "'");

    // OBJWHERE
    UniversecomputedobjectNode.getAttributes().getNamedItem("OBJWHERE").setNodeValue("''");

    // PROMPTHIERARCHY
    UniversecomputedobjectNode.getAttributes().getNamedItem("PROMPTHIERARCHY").setNodeValue("''");

    // OBJ_BH_REL
    UniversecomputedobjectNode.getAttributes().getNamedItem("OBJ_BH_REL").setNodeValue("0");

    // ELEM_BH_REL
    UniversecomputedobjectNode.getAttributes().getNamedItem("ELEM_BH_REL").setNodeValue("0");

    // INHERITANCE
    UniversecomputedobjectNode.getAttributes().getNamedItem("INHERITANCE").setNodeValue("0");

    // ORDERNRO
    UniversecomputedobjectNode.getAttributes().getNamedItem("ORDERNRO").setNodeValue(order + "");

    return "\t\t<Universecomputedobject" + getAttributes(UniversecomputedobjectNode) + " >\n";
  }

  /**
   * 
   * Creates an XML file...
   * 
   * @param doc .
   * @param defaultData .
   * @return .
   */
  private String toXML(Document doc, Map<String, String> defaultData) {

    StringBuffer sb = new StringBuffer();

    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    sb.append("<techpack>\n");

      for (Meastype meas : meastypes) {
          int collNro = 100;

          // meastype
          Measurementtype mt = getMeasType(meas.getName());
          if(mt == null){        	  
        	  continue;
          }          
          meas.setMtype(mt);
          String parent = "Computed Counters";
          int universeClassOrder = 0;
          int universecomputedobjectOrder = 0;
          for (Group group : meas.getGroups()) {
              sb.append(writeUniverseClass(universeClassOrder, meas.getName() + "." + group.getName(), parent, doc));
              universeClassOrder++;

              // create groups formulas
              for (Formula formula : group.getFormulas()) {
                  sb.append(writeUniversecomputedobject(universecomputedobjectOrder, meas.getName() + "." + group.getName(),
                          formula, doc));
                  universecomputedobjectOrder++;

                  // parameters to UniverseParameters table
                  int universeParametersOrder = 0;
                  for (String argument : formula.getArguments()) {
                      sb.append(writeUniverseParameters(mt.getTypename(), meas.getName() + "." + group.getName(), argument,
                              universeParametersOrder, formula, doc));
                      universeParametersOrder++;
                  }
                  sb.append("\t\t</Universecomputedobject>\n");
              }

              // create groups counters
              for (Counter counter : group.getCounters()) {
                  sb.append(writeMeasCounter(collNro, mt.getTypeid(), counter, defaultData, doc));
                  collNro++;
              }
          }
      }

    sb.append("</techpack>\n");

    return sb.toString().replace("\" \"", "\"\"");
  }

  private Document readXML(String str) {

    try {

      DOMParser p = new DOMParser();
      InputSource is = new InputSource(new StringReader(str));
      p.parse(is);

      return p.getDocument();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public Vector<Meastype> getMeastypes() {
    return meastypes;
  }

  public String getTechpackName() {
    return techpackName;
  }

  public void setTechpackName(String techpackName) {
    this.techpackName = techpackName;
  }

  public String getVersionid() {
    return versionid;
  }

  public void setVersionid(String versionid) {
    this.versionid = versionid;
  }

  public Map<String, Measurementtype> getMeastypesMap() {
    return meastypesMap;
  }

  public void setMeastypesMap(final Map<String, Measurementtype> mtMap) {
      meastypesMap = mtMap;
  }

  public Vector<Map<String, Universeformulas>> getFormulasMap() {
    return formulas;
  }

  public void setFormulasMap(Vector<Map<String, Universeformulas>> formulasMap) {
    this.formulas = formulasMap;
  }

  public String getPmMimVersion() {
    return pmMimVersion;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }

  private InputStream unzip(final File f) throws Exception {
    if (f.getName().endsWith(".gz") || f.getName().endsWith(".GZ")) {
      return new GZIPInputStream(new FileInputStream(f));
    } else {
      return new FileInputStream(f);
    }

  }

}
