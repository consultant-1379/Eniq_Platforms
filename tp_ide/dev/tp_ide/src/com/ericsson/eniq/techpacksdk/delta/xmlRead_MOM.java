//package com.ericsson.eniq.techpacksdk.delta;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.util.Iterator;
//import java.util.Vector;
//
//import org.apache.xerces.parsers.DOMParser;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.Attributes;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.DefaultHandler;
//
//import com.ericsson.eniq.common.ENIQEntityResolver;
//import com.ericsson.eniq.component.ExceptionHandler;
//
//public class xmlRead_MOM extends DefaultHandler {
//
//  private class Meastype {
//
//    private String name;
//
//    private Vector<Group> groups;
//
//    public Meastype() {
//      groups = new Vector<Group>();
//    }
//
//    public Vector<Group> getGroups() {
//      return groups;
//    }
//
//    public Group getLastGroup() {
//      return groups.lastElement();
//    }
//
//    public void setGroups(Vector<Group> groups) {
//      this.groups = groups;
//    }
//
//    public void addGroup(Group group) {
//      this.groups.add(group);
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public void setName(String name) {
//      this.name = name;
//    }
//  }
//
//  private class Group {
//
//    private String name;
//
//    private Vector<Counter> counters;
//
//    public Group() {
//      counters = new Vector<Counter>();
//    }
//
//    public Vector<Counter> getCounters() {
//      return counters;
//    }
//
//    public void addCounter(Counter counter) {
//      this.counters.add(counter);
//    }
//
//    public void setCounters(Vector<Counter> counters) {
//      this.counters = counters;
//    }
//
//    public Counter getLastCounter() {
//      if (counters.isEmpty()) {
//        return null;
//      }
//      return counters.lastElement();
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public void setName(String name) {
//      this.name = name;
//    }
//
//  }
//
//  private class Counter {
//
//    private String name;
//
//    private String desc;
//
//    public Counter() {
//
//    }
//
//    public Counter(String name) {
//      this.name = name;
//    }
//
//    public String getDesc() {
//      return desc;
//    }
//
//    public void setDesc(String desc) {
//      this.desc = desc;
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public void setName(String name) {
//      this.name = name;
//    }
//  }
//
//  private class Formula {
//
//    private String name;
//
//    private String desc;
//
//    public String getDesc() {
//      return desc;
//    }
//
//    public void setDesc(String desc) {
//      this.desc = desc;
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public void setName(String name) {
//      this.name = name;
//    }
//  }
//
//  private String charValue;
//
//  private Vector<Meastype> meastypes;
//
//  private Meastype meastype;
//
//  public void parse(String filename) throws Exception {
//
//    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
//    xmlReader.setContentHandler(this);
//    xmlReader.setErrorHandler(this);
//    // xmlReader.setProperty("http://apache.org/xml/properties/input-buffer-size",
//    // 100);
//
//    meastypes = new Vector<Meastype>();
//
//    xmlReader.setEntityResolver(new ENIQEntityResolver("Test"));
//    FileInputStream fis = new FileInputStream(new File(filename));
//    xmlReader.parse(new InputSource(fis));
//
//  }
//
//  public void startDocument() {
//
//  }
//
//  public void endDocument() throws SAXException {
//
//  }
//
//  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
//      throws SAXException {
//
//    charValue = "";
//
//    if (qName.equals("attribute")) {
//      meastype.getLastGroup().addCounter(new Counter(atts.getValue("name")));
//    }
//
//    if (qName.equals("description")) {
//
//    }
//
//    if (qName.equals("class")) {
//      meastype = new Meastype();
//      meastype.addGroup(new Group());
//      meastype.setName(atts.getValue("name"));
//    }
//  }
//
//  public void endElement(final String uri, final String name, final String qName) throws SAXException {
//
//    if (qName.equals("attribute")) {
//    }
//
//    if (qName.equals("description")) {
//      if (meastype != null && meastype.getLastGroup().getLastCounter() != null) {
//        meastype.getLastGroup().getLastCounter().setDesc(charValue);
//      }
//    }
//
//    if (qName.equals("class")) {
//      if (meastypes != null) {
//        meastypes.add(meastype);
//      }
//    }
//  }
//
//  public void characters(final char ch[], final int start, final int length) {
//    final StringBuffer charBuffer = new StringBuffer(length);
//    for (int i = start; i < start + length; i++) {
//      // If no control char
//      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
//        charBuffer.append(ch[i]);
//      }
//    }
//    charValue += charBuffer;
//  }
//
//  public String toString() {
//
//    StringBuffer r = new StringBuffer();
//
//    Iterator measI = meastypes.iterator();
//    while (measI.hasNext()) {
//      Meastype meas = (Meastype) measI.next();
//      r.append(meas.getName() + "\n");
//      Iterator groupsI = meas.getGroups().iterator();
//      while (groupsI.hasNext()) {
//        Group group = (Group) groupsI.next();
//        if (group.getName() != null) {
//          r.append("\t" + group.getName() + "\n");
//        }
//        Iterator countersI = group.getCounters().iterator();
//        while (countersI.hasNext()) {
//          Counter counter = (Counter) countersI.next();
//          r.append("\t\t" + counter.getName() + "\t" + counter.getDesc() + "\n");
//        }
//      }
//    }
//
//    return r.toString();
//  }
//
//  private Node getNode(String name, Document doc) {
//    NodeList nl = doc.getChildNodes().item(0).getChildNodes();
//    for (int i = 0; i < nl.getLength(); i++) {
//      if (nl.item(i).getNodeName().equals(name)) {
//        return nl.item(i);
//      }
//    }
//    return null;
//  }
//
//  private String getAttributes(Node n) {
//    // columns
//    String s = "";
//    if (n != null && n.getAttributes() != null) {
//
//      for (int a = 0; a < n.getAttributes().getLength(); a++) {
//        n.getAttributes().item(a);
//        String as = n.getAttributes().item(a).getNodeValue();
//        if (as.length() == 0)
//          as = " ";
//        s += " " + n.getAttributes().item(a).getNodeName() + "=\"" + escapeXML(as) + "\"";
//      }
//    }
//    return s;
//  }
//
//  public String escapeXML(String s) {
//    StringBuffer str = new StringBuffer();
//    int len = (s != null) ? s.length() : 0;
//    for (int i = 0; i < len; i++) {
//      char ch = s.charAt(i);
//      switch (ch) {
//      case '<':
//        str.append("&lt;");
//        break;
//      case '>':
//        str.append("&gt;");
//        break;
//      case '&':
//        str.append("&amp;");
//        break;
//      case '"':
//        str.append("&quot;");
//        break;
//      case '\'':
//        str.append("&apos;");
//        break;
//      default:
//        str.append(ch);
//      }
//    }
//    return str.toString();
//  }
//
//  public String toXML(File file) throws Exception {
//
//    Document doc = readXML(file);
//
//    StringBuffer sb = new StringBuffer();
//
//    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
//    sb.append("<techpack>\n");
//
//    Iterator measI = meastypes.iterator();
//    while (measI.hasNext()) {
//      Meastype meas = (Meastype) measI.next();
//
//      // meastype
//      Node measTypeNode = getNode("Measurementtype", doc);
//      measTypeNode.getAttributes().getNamedItem("TYPENAME").setNodeValue(meas.getName());
//      sb.append("\t<Measurementtype" + getAttributes(measTypeNode) + ">\n");
//
//      Iterator groupsI = meas.getGroups().iterator();
//      while (groupsI.hasNext()) {
//        Group group = (Group) groupsI.next();
//        if (group.getName() != null) {
//
//        }
//        Iterator countersI = group.getCounters().iterator();
//        while (countersI.hasNext()) {
//          Counter counter = (Counter) countersI.next();
//
//          // Counter
//          Node measCounterNode = getNode("Measurementcounter", doc);
//          measCounterNode.getAttributes().getNamedItem("DATANAME").setNodeValue(counter.getName());
//          measCounterNode.getAttributes().getNamedItem("DESCRIPTION").setNodeValue(counter.getDesc());
//          sb.append("\t\t<Measurementcounter" + getAttributes(measCounterNode) + " />\n");
//
//        }
//      }
//
//      sb.append("\t</" + measTypeNode.getNodeName() + ">\n");
//
//    }
//    sb.append("</techpack>\n");
//    return sb.toString();
//  }
//
//  public Document readXML(File file) throws Exception {
//
//    try {
//
//      DOMParser p = new DOMParser();
//      FileInputStream fis = new FileInputStream(file);
//      p.parse(new InputSource(fis));
//
//      return p.getDocument();
//
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//
//    return null;
//  }
//
//  public static void main(String[] args) {
//
//    try {
//
//      xmlRead_MOM xr = new xmlRead_MOM();
//      xr.parse("/test.xml");
//
//      FileWriter fw = new FileWriter(new File("/delta1.xml"));
//      fw.write(xr.toXML(new File("/defaults.xml")));
//      fw.close();
//
//      System.out.println(xr.toXML(new File("/defaults.xml")));
//
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//
//  }
//
//}
