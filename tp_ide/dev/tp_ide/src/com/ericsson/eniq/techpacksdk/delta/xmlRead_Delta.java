//package com.ericsson.eniq.techpacksdk.delta;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.StringReader;
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
//public class xmlRead_Delta extends DefaultHandler {
//
//  private String charValue;
//
//  private Vector<String> stack;
//
//  private Document n1;
//
//  private Document n2;
//
//  public void aparse(String filename) throws Exception {
//
//    stack = new Vector<String>();
//
//    final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
//    xmlReader.setContentHandler(this);
//    xmlReader.setErrorHandler(this);
//
//    xmlReader.setEntityResolver(new ENIQEntityResolver("Test"));
//    FileInputStream fis = new FileInputStream(new File(filename));
//    xmlReader.parse(new InputSource(fis));
//
//  }
//
//  public Document parse(File file) throws Exception {
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
//  public Document parse(String str) throws Exception {
//
//    try {
//
//      DOMParser p = new DOMParser();
//      InputSource is = new InputSource(new StringReader(str));
//      p.parse(is);
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
//  public void doDelta() throws Exception {
//
//    doDelta(n1, n2);
//
//    printXML(n1);
//    printXML(n2);
//
//  }
//
//  public void addXML1(File file) throws Exception {
//    n1 = parse(file);
//  }
//
//  public void addXML2(File file) throws Exception {
//    n2 = parse(file);
//  }
//
//  public void addXML1(String str) throws Exception {
//    n1 = parse(str);
//  }
//
//  public void addXML2(String str) throws Exception {
//    n2 = parse(str);
//  }
//
//  private boolean compareNodes(Node n1, Node n2) {
//
//    // same name & type
//    if (!n1.getNodeName().equals(n2.getNodeName()) || n1.getNodeType() != n2.getNodeType()) {
//
//      return false;
//    }
//
//    // same number of attributes
//    if (n1.getAttributes().getLength() != n2.getAttributes().getLength()) {
//      return false;
//    }
//
//    // same attribute names and values
//    int size = n1.getAttributes().getLength();
//    int i = 0;
//    for (int i1 = 0; i1 < size; i1++) {
//      for (int i2 = 0; i2 < size; i2++) {
//
//        if (n1.getAttributes().item(i1).getNodeName().equals(n2.getAttributes().item(i2).getNodeName())
//            && n1.getAttributes().item(i1).getNodeValue().equals(n2.getAttributes().item(i2).getNodeValue())) {
//          i++;
//          break;
//        }
//      }
//    }
//
//    if (i != size) {
//      return false;
//    }
//
//    // the same..
//    return true;
//  }
//
//  private void setAttribute(Node n, String attrName, String newValue) {
//    if (n != null && n != null && n.getAttributes() != null) {
//      if (n.getAttributes().getNamedItem(attrName) != null) {
//        n.getAttributes().getNamedItem(attrName).setNodeValue(newValue);
//      }
//    }
//  }
//
//  private String getAttribute(Node n, String attrName) {
//    if (n != null && n != null && n.getAttributes() != null) {
//      if (n.getAttributes().getNamedItem(attrName) != null) {
//        return n.getAttributes().getNamedItem(attrName).getNodeValue();
//      }
//    }
//    return "";
//  }
//
//  private void doDelta(Node n1, Node n2) throws Exception {
//
//    NodeList nl1 = n1.getChildNodes();
//    NodeList nl2 = n2.getChildNodes();
//
//    for (int i1 = 0; i1 < nl1.getLength(); i1++) {
//      if (nl1.item(i1).getNodeType() != 3) {
//
//        for (int i2 = 0; i2 < nl2.getLength(); i2++) {
//          if (nl2.item(i2).getNodeType() != 3 && !getAttribute(nl2.item(i2), "DiffStatus").equals("DELETE")) {
//
//            if (nl1.item(i1).getNodeName().equals(nl2.item(i2).getNodeName())
//                && nl1.item(i1).getNodeType() == nl2.item(i2).getNodeType()) {
//
//              doDelta(nl1.item(i1), nl2.item(i2));
//
//              if (compareNodes(nl1.item(i1), nl2.item(i2))) {
//
//                setAttribute(nl2.item(i2), "DiffStatus", "DELETE");
//                setAttribute(nl1.item(i1), "DiffStatus", "DELETE");
//                break;
//              }
//            }
//          }
//        }
//      }
//    }
//  }
//
//  private String getAttributes(Node n) {
//    // columns
//    String s = "";
//    if (n != null && n.getAttributes() != null) {
//
//      for (int a = 0; a < n.getAttributes().getLength(); a++) {
//        n.getAttributes().item(a);
//        s += " " + n.getAttributes().item(a).getNodeName() + " = \"" + n.getAttributes().item(a).getNodeValue() + "\"";
//      }
//    }
//    return s;
//  }
//
//  public Document getDeltaTree() {
//    return n1;
//  }
//
//  private void printXML(Node node) {
//
//    NodeList nl = node.getChildNodes();
//    for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
//
//      /*
//       * // columns String s = ""; if (nl.item(i) != null &&
//       * nl.item(i).getAttributes() != null) {
//       * 
//       * for (int a = 0; a < nl.item(i).getAttributes().getLength(); a++) {
//       * nl.item(i).getAttributes().item(a); s += " " +
//       * nl.item(i).getAttributes().item(a).getNodeName() + " = \"" +
//       * nl.item(i).getAttributes().item(a).getNodeValue() + "\""; } }
//       */
//
//      String s = getAttributes(nl.item(i));
//
//      if (nl.item(i).getNodeType() != 3) {
//        if (nl.item(i) != null && nl.item(i).getParentNode() != null && nl.item(i).getAttributes() != null) {
//          if (nl.item(i).getAttributes().getNamedItem("DiffStatus") != null
//              && !nl.item(i).getAttributes().getNamedItem("DiffStatus").getNodeValue().equalsIgnoreCase("DELETE")) {
//            System.out.println(nl.item(i).getNodeName() + " " + s);
//          }
//        }
//      }
//
//      printXML(nl.item(i));
//    }
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
//    stack.add(qName);
//
//  }
//
//  public void endElement(final String uri, final String name, final String qName) throws SAXException {
//
//    if (stack.lastElement().equals(qName)) {
//      stack.remove(stack.size() - 1);
//    } else {
//
//    }
//
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
//  public static void main(String[] args) {
//
//    try {
//
//      xmlRead_Delta xr = new xmlRead_Delta();
//      xr.parse("/delta.xml");
//      System.out.println(xr);
//
//    } catch (Exception e) {
//      ExceptionHandler.instance().handle(e);
//      e.printStackTrace();
//    }
//
//  }
//
//}
