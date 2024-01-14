//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "ProtectedField", "unused", "AssignmentOrReturnOfFieldWithMutableType", "rawtypes", "ReassignedVariable", "UseOfObsoleteCollectionType", "unchecked", "RedundantThrows", "StringConcatenation", "DataFlowIssue", "ReturnOfNull", "ForLoopReplaceableByForEach", "FinalStaticMethod", "NonReproducibleMathCall", "IfStatementWithNegatedCondition"})
public class PenroseTile {
  protected Point[] ea;
  protected Point[] eb;
  protected Point[] ec;
  protected Point[] ed;
  protected Point[] vs1;
  protected Point[] vs2;
  protected double[] ps;

  public PenroseTile() {
  }

  public Point[] getIntrinsicEdge(int var1) {
    switch (var1) {
      case 0:
        return this.ea;
      case 1:
        return this.eb;
      case 2:
        return this.ec;
      case 3:
      default:
        return this.ed;
    }
  }

  public int numFirstEdges() {
    return this.vs1.length;
  }

  public Point getFirstVertex(int var1) {
    return this.vs1[var1];
  }

  public Point[] getFirstEdge(int var1) {
    return null;
  }

  public void setFirstEdge(int var1, Point[] var2) {
  }

  public Transform getFirstTransform(int var1) {
    Point var2 = this.vs1[var1];
    Point var3 = this.vs1[(var1 + 1) % this.vs1.length];
    if (var2.dist(var3) > 1.0E-7D) {
      Transform var4 = Transform.matchLineSegment(var2, var3);
      return this.isFirstRotated(var1) ? var4.compose(new Transform(-1.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D)) : var4;
    } else {
      return null;
    }
  }

  public boolean isFirstRotated(int var1) {
    return false;
  }

  public int numSecondEdges() {
    return this.vs2.length;
  }

  public Point getSecondVertex(int var1) {
    return this.vs2[var1];
  }

  public Point[] getSecondEdge(int var1) {
    return null;
  }

  public void setSecondEdge(int var1, Point[] var2) {
  }

  public Transform getSecondTransform(int var1) {
    Point var2 = this.vs2[var1];
    Point var3 = this.vs2[(var1 + 1) % this.vs2.length];
    if (var2.dist(var3) > 1.0E-7D) {
      Transform var4 = Transform.matchLineSegment(var2, var3);
      return this.isSecondRotated(var1) ? var4.compose(new Transform(-1.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D)) : var4;
    } else {
      return null;
    }
  }

  public boolean isSecondRotated(int var1) {
    return false;
  }

  public Point[] getFirstShape() {
    Vector var1 = new Vector();

    for (int var2 = 0; var2 < this.vs1.length; ++var2) {
      if (this.vs1[var2].dist(this.vs1[(var2 + 1) % this.vs1.length]) > 1.0E-7D) {
        Transform var3 = this.getFirstTransform(var2);
        Point[] var4 = this.getFirstEdge(var2);
        int var5;
        if (this.isFirstRotated(var2)) {
          for (var5 = var4.length - 1; var5 > 0; --var5) {
            var1.addElement(var3.apply(var4[var5]));
          }
        } else {
          for (var5 = 0; var5 < var4.length - 1; ++var5) {
            var1.addElement(var3.apply(var4[var5]));
          }
        }
      }
    }

    Point[] var6 = new Point[var1.size()];
    var1.copyInto(var6);
    return var6;
  }

  public Point[] getSecondShape() {
    Vector var1 = new Vector();

    for (int var2 = 0; var2 < this.vs2.length; ++var2) {
      if (this.vs2[var2].dist(this.vs2[(var2 + 1) % this.vs2.length]) > 1.0E-7D) {
        Transform var3 = this.getSecondTransform(var2);
        Point[] var4 = this.getSecondEdge(var2);
        int var5;
        if (this.isSecondRotated(var2)) {
          for (var5 = var4.length - 1; var5 > 0; --var5) {
            var1.addElement(var3.apply(var4[var5]));
          }
        } else {
          for (var5 = 0; var5 < var4.length - 1; ++var5) {
            var1.addElement(var3.apply(var4[var5]));
          }
        }
      }
    }

    Point[] var6 = new Point[var1.size()];
    var1.copyInto(var6);
    return var6;
  }

  public void setParameter(int var1, double var2) {
    if (var1 % 2 != 0) {
      this.ps[var1] = var2 * 2.0D * 3.141592653589793D;
    } else {
      this.ps[var1] = var2;
    }

  }

  public void setParameters(double[] var1) {
    this.ps[0] = var1[0];
    this.ps[1] = var1[1] * 2.0D * 3.141592653589793D;
    this.ps[2] = var1[2];
    this.ps[3] = var1[3] * 2.0D * 3.141592653589793D;
  }

  public double getParameter(int var1) {
    return var1 % 2 != 0 ? this.ps[var1] / 6.283185307179586D : this.ps[var1];
  }

  public void write(PrintWriter var1, String var2) throws IOException {
  }

  protected static void writeEdgeShape(PrintWriter var0, String var1, Point[] var2) {
    var0.println(var1 + "<edge_shape>");

    for (int var3 = 0; var3 < var2.length; ++var3) {
      Point var4 = var2[var3];
      var0.println(var1 + "  <point x=\"" + var4.getX() + "\" y=\"" + var4.getY() + "\"/>");
    }

    var0.println(var1 + "</edge_shape>");
  }

  protected static final Point erectOn(Point var0, Point var1, double var2, double var4) {
    double var6 = var0.dist(var1);
    double var8 = Math.sin(var4);
    double var10 = Math.cos(var4);
    double var12 = (var1.getY() - var0.getY()) / var6;
    double var14 = (var1.getX() - var0.getX()) / var6;
    return new Point(var0.getX() + var2 * (var10 * var14 - var8 * var12), var0.getY() + var2 * (var10 * var12 + var8 * var14));
  }
}
