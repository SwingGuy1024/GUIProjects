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

public class Rhombs extends PenroseTile {
  public static final double phi = 1.61803398875D;
  public static final double thinx = 1.0D / Math.tan(1.2566370614359172D);
  public static final double thickx = 1.0D / Math.tan(0.9424777960769379D);
  public static final Point THIN0;
  public static final Point THIN1;
  public static final Point THIN2;
  public static final Point THIN3;
  public static final Point THICK0;
  public static final Point THICK1;
  public static final Point THICK2;
  public static final Point THICK3;
  public static final boolean[] rot_thin = new boolean[]{false, false, true, false, false, true, false, true, true, true};
  public static final boolean[] rot_thick = new boolean[]{false, true, true, false, false, false, false, true, false, true, true, true};

  public Rhombs() {
    this.ps = new double[]{1.0E-5D, 0.0D, 1.0E-5D, 0.0D};
    this.ea = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.eb = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.ec = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.ed = new Point[]{new Point(0.0D, 0.0D), new Point(1.0D, 0.0D)};
    this.setupTilingVertices();
  }

  private void setupTilingVertices() {
    Transform var1 = Transform.rotateAroundPoint(THIN3, -1.2566370614359172D);
    Transform var2 = Transform.rotateAroundPoint(THIN2, -2.5132741228718345D);
    Transform var3 = Transform.rotateAroundPoint(THIN0, 2.5132741228718345D);
    this.vs1 = new Point[10];
    this.vs1[0] = THIN0;
    this.vs1[1] = erectOn(THIN0, THIN1, this.ps[0], 1.8849555921538759D + this.ps[1]);
    this.vs1[2] = erectOn(THIN1, THIN0, this.ps[2], -this.ps[3]);
    this.vs1[3] = THIN2;
    this.vs1[4] = var2.apply(this.vs1[2]);
    this.vs1[5] = erectOn(THIN3, THIN2, this.ps[0], this.ps[1]);
    this.vs1[6] = THIN3;
    this.vs1[7] = var1.apply(this.vs1[5]);
    this.vs1[8] = var1.apply(this.vs1[4]);
    this.vs1[9] = var3.apply(this.vs1[1]);
    var1 = Transform.rotateAroundPoint(THICK1, -1.2566370614359172D);
    var2 = Transform.rotateAroundPoint(THICK0, -2.5132741228718345D);
    var3 = Transform.rotateAroundPoint(THICK3, -1.2566370614359172D);
    Transform var4 = Transform.matchTwoSegments(THIN3, THIN2, THICK0, THICK1);
    Transform var5 = Transform.matchTwoSegments(THIN3, THIN0, THICK3, THICK2);
    this.vs2 = new Point[12];
    this.vs2[0] = THICK0;
    this.vs2[1] = var4.apply(this.vs1[5]);
    this.vs2[2] = var4.apply(this.vs1[4]);
    this.vs2[3] = THICK1;
    this.vs2[4] = var1.apply(this.vs2[2]);
    this.vs2[5] = var1.apply(this.vs2[1]);
    this.vs2[6] = var5.apply(this.vs1[8]);
    this.vs2[7] = var5.apply(this.vs1[7]);
    this.vs2[8] = THICK3;
    this.vs2[9] = var3.apply(this.vs2[7]);
    this.vs2[10] = var3.apply(this.vs2[6]);
    this.vs2[11] = var2.apply(this.vs2[1]);
  }

  public void setParameter(int var1, double var2) {
    super.setParameter(var1, var2);
    this.setupTilingVertices();
  }

  public void setParameters(double[] var1) {
    super.setParameters(var1);
    this.setupTilingVertices();
  }

  public Rhombs(Element var1, ParseXML var2) throws XMLParseError {
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
    var1.println(var2 + "<penrose type=\"P3\">");
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

  public boolean isFirstRotated(int var1) {
    return rot_thin[var1];
  }

  public boolean isSecondRotated(int var1) {
    return rot_thick[var1];
  }

  public Point[] getFirstEdge(int var1) {
    switch (var1) {
      case 1:
      case 8:
        return this.ea;
      case 2:
      case 3:
        return this.ec;
      case 4:
      case 7:
        return this.eb;
      case 5:
      case 6:
      default:
        return this.ed;
    }
  }

  public void setFirstEdge(int var1, Point[] var2) {
    switch (var1) {
      case 1:
      case 8:
        this.ea = var2;
        return;
      case 2:
      case 3:
        this.ec = var2;
        return;
      case 4:
      case 7:
        this.eb = var2;
        return;
      case 5:
      case 6:
      default:
        this.ed = var2;
    }
  }

  public Point[] getSecondEdge(int var1) {
    switch (var1) {
      case 1:
      case 4:
      case 6:
      case 9:
        return this.eb;
      case 2:
      case 3:
        return this.ec;
      case 5:
      case 10:
        return this.ea;
      case 7:
      case 8:
      default:
        return this.ed;
    }
  }

  public void setSecondEdge(int var1, Point[] var2) {
    switch (var1) {
      case 1:
      case 4:
      case 6:
      case 9:
        this.eb = var2;
        return;
      case 2:
      case 3:
        this.ec = var2;
        return;
      case 5:
      case 10:
        this.ea = var2;
        return;
      case 7:
      case 8:
      default:
        this.ed = var2;
    }
  }

  static {
    THIN0 = new Point(thinx, 0.0D);
    THIN1 = new Point(0.0D, 1.0D);
    THIN2 = new Point(-thinx, 0.0D);
    THIN3 = new Point(0.0D, -1.0D);
    THICK0 = new Point(thickx, 0.0D);
    THICK1 = new Point(0.0D, 1.0D);
    THICK2 = new Point(-thickx, 0.0D);
    THICK3 = new Point(0.0D, -1.0D);
  }
}
