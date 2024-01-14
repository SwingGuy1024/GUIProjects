//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.geometry;

import csk.taprats.general.Loose;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "unused", "UnnecessaryConstantArrayCreationExpression", "ReassignedVariable", "RedundantThrows", "StringConcatenation", "override", "SingleCharacterStringConcatenation", "FinalStaticMethod", "MethodDoesntCallSuperMethod", "UnnecessaryLocalVariable", "ConditionalExpressionWithNegatedCondition", "StatementWithEmptyBody", "SimplifiableConditionalExpression", "NonFinalFieldReferencedInHashCode", "StringBufferMayBeStringBuilder", "NonReproducibleMathCall"})
public class Point implements Cloneable {
  private static final double TRUNC = 1.0E7D;
  public static final Point ORIGIN = new Point(0.0D, 0.0D);
  public static final Point UNIT_X = new Point(1.0D, 0.0D);
  public static final Point UNIT_Y = new Point(0.0D, 1.0D);
  private double x;
  private double y;

  public Point(double var1, double var3) {
    this.x = var1;
    this.y = var3;
  }

  public Point() {
    this(0.0D, 0.0D);
  }

  public final Object clone() {
    return new Point(this.x, this.y);
  }

  public final double getX() {
    return this.x;
  }

  public final double getY() {
    return this.y;
  }

  public final void setX(double var1) {
    this.x = var1;
  }

  public final void setY(double var1) {
    this.y = var1;
  }

  public final double[] get() {
    double[] var1 = new double[]{this.x, this.y};
    return var1;
  }

  public final double mag2() {
    return this.x * this.x + this.y * this.y;
  }

  public final double mag() {
    return Math.sqrt(this.mag2());
  }

  public final double dist2(Point var1) {
    double var2 = this.x - var1.x;
    double var4 = this.y - var1.y;
    return var2 * var2 + var4 * var4;
  }

  public final double dist(Point var1) {
    return Math.sqrt(this.dist2(var1));
  }

  public final Point normalize() {
    double var1 = this.mag();
    return var1 != 0.0D ? this.scale(1.0D / var1) : this;
  }

  public final void normalizeD() {
    double var1 = this.mag();
    if (var1 != 0.0D) {
      this.scaleD(1.0D / var1);
    }

  }

  public final Point add(Point var1) {
    return new Point(this.x + var1.x, this.y + var1.y);
  }

  public final void addD(Point var1) {
    this.x += var1.x;
    this.y += var1.y;
  }

  public final Point subtract(Point var1) {
    return new Point(this.x - var1.x, this.y - var1.y);
  }

  public final void subtractD(Point var1) {
    this.x -= var1.x;
    this.y -= var1.y;
  }

  public final double dot(Point var1) {
    return this.x * var1.x + this.y * var1.y;
  }

  public final Point scale(double var1) {
    return new Point(this.x * var1, this.y * var1);
  }

  public final Point scale(double var1, double var3) {
    return new Point(this.x * var1, this.y * var3);
  }

  public final void scaleD(double var1) {
    this.x *= var1;
    this.y *= var1;
  }

  public final Point perp() {
    return new Point(-this.y, this.x);
  }

  public final void perpD() {
    double var1 = this.x;
    this.x = -this.y;
    this.y = var1;
  }

  public final Point convexSum(Point var1, double var2) {
    double var4 = 1.0D - var2;
    return new Point(var4 * this.x + var2 * var1.x, var4 * this.y + var2 * var1.y);
  }

  public final void convexSumD(Point var1, double var2) {
    double var4 = 1.0D - var2;
    this.x = var4 * this.x + var2 * var1.x;
    this.y = var4 * this.y + var2 * var1.y;
  }

  public final double getAngle(Point var1) {
    return Math.atan2(var1.getY() - this.y, var1.getX() - this.x);
  }

  public final double getAngle() {
    return Math.atan2(this.y, this.x);
  }

  public final double cross(Point var1) {
    return this.x * var1.y - this.y * var1.x;
  }

  public final double sweep(Point var1, Point var2) {
    double var3 = this.getAngle(var1);
    double var5 = this.getAngle(var2);

    double var7;
    for (var7 = var5 - var3; var7 < 0.0D; var7 += 6.283185307179586D) {
    }

    return var7;
  }

  public final double distToSegment(Point var1, Point var2) {
    double var3 = this.parameterizationOnLine(var1, var2);
    if (var3 >= 0.0D && var3 <= 1.0D) {
      double var5 = var1.x + var3 * (var2.x - var1.x);
      double var7 = var1.y + var3 * (var2.y - var1.y);
      return Math.sqrt((this.x - var5) * (this.x - var5) + (this.y - var7) * (this.y - var7));
    } else {
      return var3 < 0.0D ? var1.dist(this) : var2.dist(this);
    }
  }

  public final double distToLine(Point var1, Point var2) {
    return this.dist(this.projectToLine(var1, var2));
  }

  public final double parameterizationOnLine(Point var1, Point var2) {
    Point var3 = var2.subtract(var1);
    return this.subtract(var1).dot(var3) / var3.dot(var3);
  }

  public final Point projectToLine(Point var1, Point var2) {
    return var1.convexSum(var2, this.parameterizationOnLine(var1, var2));
  }

  public final String toString() {
    return "[ " + this.x + " " + this.y + " ]";
  }

  public final boolean equals(Object var1) {
    return !(var1 instanceof Point) ? false : Loose.equals(this, (Point) var1);
  }

  public final int hashCode() {
    long var1 = Double.doubleToLongBits(Math.floor(this.x * 1.0E7D));
    long var3 = Double.doubleToLongBits(Math.floor(this.y * 1.0E7D));
    return (int) (var1 ^ var1 >> 32 ^ var3 ^ var3 >> 32);
  }

  public static final String toString(Point[] var0) {
    int var1 = var0.length;
    if (var1 == 0) {
      return "";
    } else if (var1 == 1) {
      return var0[0].toString();
    } else {
      StringBuffer var2 = new StringBuffer();
      var2.append(var0[0].toString());

      for (int var3 = 1; var3 < var1; ++var3) {
        var2.append(" ");
        var2.append(var0[var3]);
      }

      return var2.toString();
    }
  }

  public static final void main(String[] var0) {
    Point var1 = new Point(Double.parseDouble(var0[0]), Double.parseDouble(var0[1]));
    Point var2 = new Point(Double.parseDouble(var0[2]), Double.parseDouble(var0[3]));
    Point var3 = new Point(Double.parseDouble(var0[4]), Double.parseDouble(var0[5]));
    System.out.println(var1.distToSegment(var2, var3));
  }
}
