//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import csk.taprats.general.ParseXML;
import csk.taprats.general.XMLParseError;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Element;

public class KiteDart extends PenroseTile {
  public static final double phi = 1.61803398875D;
  public static final Point A = new Point(0.0D, 0.0D);
  public static final Point B = new Point(Math.cos(0.3141592653589793D), Math.sin(0.3141592653589793D));
  public static final Point C = new Point(0.0D, 1.61803398875D);
  public static final Point D = new Point(-Math.cos(0.3141592653589793D), Math.sin(0.3141592653589793D));
  public static final Point E = new Point(0.0D, -1.0D);
  public static final boolean[] keo = new boolean[]{false, false, false, true, false, true, false, true, true, true};
  public static final boolean[] deo = new boolean[]{true, true, true, false, true, false, true, false, false, false};

  public boolean isFirstRotated(int var1) {
    return keo[var1];
  }

  public boolean isSecondRotated(int var1) {
    return deo[var1];
  }

  public Point[] getFirstEdge(int var1) {
    switch (var1) {
      case 0:
      case 3:
      case 4:
      case 9:
        return this.ea;
      case 1:
      case 8:
        return this.ec;
      case 2:
      case 5:
        return this.eb;
      case 6:
      case 7:
      default:
        return this.ed;
    }
  }

  public void setFirstEdge(int var1, Point[] var2) {
    switch (var1) {
      case 0:
      case 3:
      case 4:
      case 9:
        this.ea = var2;
        return;
      case 1:
      case 8:
        this.ec = var2;
        return;
      case 2:
      case 5:
        this.eb = var2;
        return;
      case 6:
      case 7:
      default:
        this.ed = var2;
    }
  }

  public Point[] getSecondEdge(int var1) {
    switch (var1) {
      case 0:
      case 3:
      case 4:
      case 9:
        return this.ed;
      case 1:
      case 8:
        return this.ec;
      case 2:
      case 5:
        return this.eb;
      case 6:
      case 7:
      default:
        return this.ea;
    }
  }

  public void setSecondEdge(int var1, Point[] var2) {
    switch (var1) {
      case 0:
      case 3:
      case 4:
      case 9:
        this.ed = var2;
        return;
      case 1:
      case 8:
        this.ec = var2;
        return;
      case 2:
      case 5:
        this.eb = var2;
        return;
      case 6:
      case 7:
      default:
        this.ea = var2;
    }
  }

  public KiteDart() {
    this.ps = new double[]{1.0E-5D, 0.0D, 1.0E-5D, 0.0D};
    this.ea = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.eb = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.ec = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.ed = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.setupTilingVertices();
  }

  private void setupTilingVertices() {
    Point var1 = erectOn(D, A, this.ps[0], this.ps[1]);
    Point var2 = erectOn(A, C, this.ps[2], this.ps[3]);
    Point var3 = erectOn(E, D, this.ps[2], this.ps[3]);
    Transform var4 = Transform.rotateAroundPoint(E, -1.2566370614359172D);
    Transform var5 = Transform.rotateAroundPoint(A, -2.5132741228718345D);
    this.vs1 = new Point[]{A, var2, var1, var3, E, var4.apply(var3), var4.apply(var1), B, var5.apply(var1), var5.apply(var2)};
    var1 = erectOn(A, B, this.ps[0], this.ps[1]);
    var2 = erectOn(B, C, this.ps[2], this.ps[3]);
    var3 = erectOn(C, B, this.ps[0], this.ps[1] + 0.6283185307179586D);
    var4 = Transform.rotateAroundPoint(C, -1.2566370614359172D);
    var5 = Transform.rotateAroundPoint(A, 2.5132741228718345D);
    this.vs2 = new Point[]{A, var1, var2, var3, C, var4.apply(var3), var4.apply(var2), D, var5.apply(var2), var5.apply(var1)};
  }

  public void setParameter(int var1, double var2) {
    super.setParameter(var1, var2);
    this.setupTilingVertices();
  }

  public void setParameters(double[] var1) {
    super.setParameters(var1);
    this.setupTilingVertices();
  }

  public KiteDart(Element var1, ParseXML var2) throws XMLParseError {
    this.ps = new double[4];
    Point[][] var3 = new Point[4][];
    int var4 = 0;
    Enumeration var5 = ParseXML.getChildren(var1);

    while (true) {
      while (var5.hasMoreElements()) {
        Element var6 = (Element) ((Element) var5.nextElement());
        Enumeration var8;
        Element var9;
        if (var6.getNodeName().equals("vertices")) {
          int var10 = 0;

          for (var8 = ParseXML.getChildren(var6); var8.hasMoreElements(); ++var10) {
            var9 = (Element) ((Element) var8.nextElement());
            this.ps[var10] = ParseXML.getElementDouble(var9, "val", "param");
          }

          double[] var10000 = this.ps;
          var10000[1] *= 6.283185307179586D;
          var10000 = this.ps;
          var10000[3] *= 6.283185307179586D;
        } else if (var6.getNodeName().equals("edge_shape")) {
          Vector var7 = new Vector();
          var8 = ParseXML.getChildren(var6);

          while (var8.hasMoreElements()) {
            var9 = (Element) ((Element) var8.nextElement());
            var7.addElement(ParseXML.getPoint(var9));
          }

          var3[var4] = new Point[var7.size()];
          var7.copyInto(var3[var4]);
          ++var4;
        }
      }

      this.ea = var3[0];
      this.eb = var3[1];
      this.ec = var3[2];
      this.ed = var3[3];
      this.setupTilingVertices();
      return;
    }
  }

  public void write(PrintWriter var1, String var2) throws IOException {
    var1.println(var2 + "<penrose type=\"P2\">");
    var1.println(var2 + "  <vertices>");
    var1.println(var2 + "    <param val=\"" + this.ps[0] + "\"/>");
    var1.println(var2 + "    <param val=\"" + this.ps[1] / 6.283185307179586D + "\"/>");
    var1.println(var2 + "    <param val=\"" + this.ps[2] + "\"/>");
    var1.println(var2 + "    <param val=\"" + this.ps[3] / 6.283185307179586D + "\"/>");
    var1.println(var2 + "  </vertices>");
    var1.println();
    writeEdgeShape(var1, var2 + "  ", this.ea);
    writeEdgeShape(var1, var2 + "  ", this.eb);
    writeEdgeShape(var1, var2 + "  ", this.ec);
    writeEdgeShape(var1, var2 + "  ", this.ed);
    var1.println(var2 + "</penrose>");
  }
}
