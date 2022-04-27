//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.general;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

public class ParseXML {
  protected Document doc;
  private static DocumentBuilderFactory doc_factory;
  private static DocumentBuilder doc_builder;
  private static TransformerFactory transformer_factory;
  private static Transformer transformer;

  public ParseXML(Document var1) {
    this.doc = var1;
  }

  public ParseXML(InputStream var1) throws XMLParseError {
    if (doc_builder != null) {
      try {
        this.doc = doc_builder.parse(var1);
      } catch (SAXException var4) {
        throw new XMLParseError("Couldn't parse document");
      } catch (IOException var5) {
        throw new XMLParseError("Couldn't parse document");
      }
    }

  }

  public ParseXML(String var1) throws XMLParseError {
    if (doc_builder != null) {
      try {
        this.doc = doc_builder.parse(var1);
      } catch (SAXException var4) {
        throw new XMLParseError("Couldn't parse document");
      } catch (IOException var5) {
        throw new XMLParseError("Couldn't parse document");
      }
    }

  }

  public Element beginDocument() {
    return this.doc.getDocumentElement();
  }

  public static final String getProp(Element var0, String var1) {
    String var2 = var0.getAttribute(var1);
    return var2.length() == 0 ? null : var2;
  }

  public static final boolean hasProp(Element var0, String var1) {
    return var0.hasAttribute(var1);
  }

  public static double[] parseDoubles(Element var0, String var1, String var2) throws XMLParseError {
    Vector var3 = new Vector();
    verifyElementName(var0, var2);
    StringTokenizer var4 = new StringTokenizer(var1, ",");

    while (var4.hasMoreTokens()) {
      var3.addElement(new Double(getPropDouble(var0, var4.nextToken())));
    }

    double[] var5 = new double[var3.size()];

    for (int var6 = 0; var6 < var3.size(); ++var6) {
      var5[var6] = (Double) var3.elementAt(var6);
    }

    return var5;
  }

  public static int getElementInt(Element var0, String var1, String var2) throws XMLParseError {
    verifyElementName(var0, var2);
    return getPropInt(var0, var1);
  }

  public static int getPropInt(Element var0, String var1) {
    return Integer.parseInt(getProp(var0, var1));
  }

  public static double getElementDouble(Element var0, String var1, String var2) throws XMLParseError {
    verifyElementName(var0, var2);
    return getPropDouble(var0, var1);
  }

  public static Point getPoint(Element var0) throws XMLParseError {
    return new Point(getPropDouble(var0, "x"), getPropDouble(var0, "y"));
  }

  public static Transform getTransform(Element var0) throws XMLParseError {
    return new Transform(getPropDouble(var0, "a"), getPropDouble(var0, "b"), getPropDouble(var0, "c"), getPropDouble(var0, "d"), getPropDouble(var0, "e"), getPropDouble(var0, "f"));
  }

  public static double getPropDouble(Element var0, String var1) throws XMLParseError {
    try {
      return new Double(getProp(var0, var1));
    } catch (NumberFormatException var3) {
      throw new XMLParseError("Illegal floating point data in input");
    }
  }

  public static boolean getPropBool(Element var0, String var1) throws XMLParseError {
    String var2 = getElementProp(var0, var1);
    if (var2.equals("true")) {
      return true;
    } else if (var2.equals("false")) {
      return false;
    } else {
      throw new XMLParseError("Invalid boolean attribute");
    }
  }

  public static String getElementProp(Element var0, String var1) throws XMLParseError {
    String var2 = getProp(var0, var1);
    if (var2 == null) {
      throw new XMLParseError("Node didn't have expected property " + var1);
    } else {
      return var2;
    }
  }

  public static void verifyElementName(Element var0, String var1) throws XMLParseError {
    if (!var0.getTagName().equals(var1)) {
      throw new XMLParseError("Element name '" + var0.getTagName() + "' didn't verify against '" + var1 + "'");
    }
  }

  public static Enumeration getChildren(Element var0) {
    return new ChildEnum(var0);
  }

  public static int numChildren(Element var0) {
    int var1 = 0;

    for (Enumeration var2 = getChildren(var0); var2.hasMoreElements(); ++var1) {
      var2.nextElement();
    }

    return var1;
  }

  public static Element getOnlyChild(Element var0) throws XMLParseError {
    Enumeration var1 = getChildren(var0);
    if (var1.hasMoreElements()) {
      Element var2 = (Element) var1.nextElement();
      if (var1.hasMoreElements()) {
        throw new XMLParseError("Expected node to have exactly one child.");
      } else {
        return var2;
      }
    } else {
      throw new XMLParseError("Expected node to have exactly one child.");
    }
  }

  public void dumpDocument(OutputStream var1) {
    DOMSource var2 = new DOMSource(this.doc);
    StreamResult var3 = new StreamResult(var1);
    transformer.setOutputProperty("omit-xml-declaration", "no");

    try {
      transformer.transform(var2, var3);
    } catch (TransformerException var5) {
    }

  }

  public static void dumpNode(Node var0, OutputStream var1) {
    DOMSource var2 = new DOMSource(var0);
    StreamResult var3 = new StreamResult(var1);
    transformer.setOutputProperty("omit-xml-declaration", "yes");

    try {
      transformer.transform(var2, var3);
    } catch (TransformerException var5) {
    }

  }

  public ProcessingInstruction getPINode() {
    NodeList var1 = this.doc.getChildNodes();

    for (int var2 = 0; var2 < var1.getLength(); ++var2) {
      Node var3 = var1.item(var2);
      if (var3.getNodeType() == 7) {
        return (ProcessingInstruction) var3;
      }
    }

    return null;
  }

  public void verifyPINode() throws XMLParseError {
    ProcessingInstruction var1 = this.getPINode();
    if (var1 == null) {
      throw new XMLParseError("Document has no PI node.");
    } else if (!var1.getNodeName().equals("taprats")) {
      throw new XMLParseError("Document is not a Taprats file.");
    } else {
      System.err.println("Warning -- not verifying Taprats version.");
    }
  }

  public static final void main(String[] var0) {
    try {
      ParseXML var1 = new ParseXML(new FileInputStream(var0[0]));
      ProcessingInstruction var2 = var1.getPINode();
      System.out.println(var2.getTarget());
      System.out.println(var2.getData());
      dumpNode(var2, System.out);
    } catch (Exception var3) {
      var3.printStackTrace();
    }

  }

  static {
    try {
      doc_factory = DocumentBuilderFactory.newInstance();
      doc_builder = doc_factory.newDocumentBuilder();
      transformer_factory = TransformerFactory.newInstance();
      transformer = transformer_factory.newTransformer();
    } catch (Exception var1) {
      System.err.println("Warning!  Couldn't start XML engine.");
      System.err.println("Reading and writing of files will not work.");
    }

  }
}
