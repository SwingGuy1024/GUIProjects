//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.geometry;

public class Transform implements Cloneable {
  public static final Transform ZERO = scale(0.0D);
  public static final Transform IDENTITY = scale(1.0D);
  private double a;
  private double b;
  private double c;
  private double d;
  private double e;
  private double f;

  public Transform(double var1, double var3, double var5, double var7, double var9, double var11) {
    this.a = var1;
    this.b = var3;
    this.c = var5;
    this.d = var7;
    this.e = var9;
    this.f = var11;
  }

  public Transform(double[] var1) {
    this.a = var1[0];
    this.b = var1[1];
    this.c = var1[2];
    this.d = var1[2];
    this.e = var1[4];
    this.f = var1[5];
  }

  public void get(double[] var1) {
    var1[0] = this.a;
    var1[1] = this.b;
    var1[2] = this.c;
    var1[3] = this.d;
    var1[4] = this.e;
    var1[5] = this.f;
  }

  public final double getUnitLength() {
    return Math.sqrt(this.a * this.a + this.d * this.d);
  }

  public final Object clone() {
    return new Transform(this.a, this.b, this.c, this.d, this.e, this.f);
  }

  public static final Transform scale(double var0) {
    return new Transform(var0, 0.0D, 0.0D, 0.0D, var0, 0.0D);
  }

  public static final Transform scale(double var0, double var2) {
    return new Transform(var0, 0.0D, 0.0D, 0.0D, var2, 0.0D);
  }

  public static final Transform translate(double var0, double var2) {
    return new Transform(1.0D, 0.0D, var0, 0.0D, 1.0D, var2);
  }

  public static final Transform translate(Point var0) {
    return new Transform(1.0D, 0.0D, var0.getX(), 0.0D, 1.0D, var0.getY());
  }

  public static final Transform rotate(double var0) {
    return new Transform(Math.cos(var0), -Math.sin(var0), 0.0D, Math.sin(var0), Math.cos(var0), 0.0D);
  }

  public static final Transform rotateToPoint(Point var0) {
    double var1 = var0.mag();
    double var3 = var0.getX() / var1;
    double var5 = var0.getY() / var1;
    return new Transform(var3, -var5, 0.0D, var5, var3, 0.0D);
  }

  public static final Transform rotateAroundPoint(Point var0, double var1) {
    return translate(var0).compose(rotate(var1).compose(translate(-var0.getX(), -var0.getY())));
  }

  public static final Transform reflectThroughLine(Point var0, Point var1) {
    Transform var2 = matchLineSegment(var0, var1);
    return var2.compose(scale(1.0D, -1.0D).compose(var2.invert()));
  }

  public static final Transform reflectAcrossMidpoint(Point var0, Point var1) {
    Point var2 = var0.convexSum(var1, 0.5D);
    return reflectThroughLine(var2, var2.add(var1.subtract(var0).perp()));
  }

  public final Transform compose(Transform var1) {
    return new Transform(this.a * var1.a + this.b * var1.d, this.a * var1.b + this.b * var1.e, this.a * var1.c + this.b * var1.f + this.c, this.d * var1.a + this.e * var1.d, this.d * var1.b + this.e * var1.e, this.d * var1.c + this.e * var1.f + this.f);
  }

  public final void composeD(Transform var1) {
    double var2 = this.a * var1.a + this.b * var1.d;
    double var4 = this.a * var1.b + this.b * var1.e;
    double var6 = this.a * var1.c + this.b * var1.f + this.c;
    double var8 = this.d * var1.a + this.e * var1.d;
    double var10 = this.d * var1.b + this.e * var1.e;
    double var12 = this.d * var1.c + this.e * var1.f + this.f;
    this.a = var2;
    this.b = var4;
    this.c = var6;
    this.d = var8;
    this.e = var10;
    this.f = var12;
  }

  public final Point apply(Point var1) {
    double var2 = var1.getX();
    double var4 = var1.getY();
    return new Point(this.a * var2 + this.b * var4 + this.c, this.d * var2 + this.e * var4 + this.f);
  }

  public final Point apply(double var1, double var3) {
    return new Point(this.a * var1 + this.b * var3 + this.c, this.d * var1 + this.e * var3 + this.f);
  }

  public final void applyD(Point var1) {
    double var2 = var1.getX();
    double var4 = var1.getY();
    var1.setX(this.a * var2 + this.b * var4 + this.c);
    var1.setY(this.d * var2 + this.e * var4 + this.f);
  }

  public final double applyX(double var1, double var3) {
    return this.a * var1 + this.b * var3 + this.c;
  }

  public final double applyY(double var1, double var3) {
    return this.d * var1 + this.e * var3 + this.f;
  }

  public final Point[] apply(Point[] var1) {
    int var2 = var1.length;
    Point[] var3 = new Point[var2];

    for (int var4 = 0; var4 < var2; ++var4) {
      var3[var4] = this.apply(var1[var4]);
    }

    return var3;
  }

  public final void applyD(Point[] var1) {
    int var2 = var1.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      this.applyD(var1[var3]);
    }

  }

  public final Transform invert() {
    double var1 = this.a * this.e - this.b * this.d;
    if (var1 == 0.0D) {
      throw new IllegalArgumentException("Non invertible matrix.");
    } else {
      return new Transform(this.e / var1, -this.b / var1, (this.b * this.f - this.c * this.e) / var1, -this.d / var1, this.a / var1, (this.c * this.d - this.a * this.f) / var1);
    }
  }

  public final void invertD() {
    double var1 = this.a * this.e - this.b * this.d;
    if (var1 == 0.0D) {
      throw new IllegalArgumentException("Non invertible matrix.");
    } else {
      double var3 = this.e / var1;
      double var5 = -this.b / var1;
      double var7 = (this.b * this.f - this.c * this.e) / var1;
      double var9 = -this.d / var1;
      double var11 = this.a / var1;
      double var13 = (this.c * this.d - this.a * this.f) / var1;
      this.a = var3;
      this.b = var5;
      this.c = var7;
      this.d = var9;
      this.e = var11;
      this.f = var13;
    }
  }

  public static final Transform matchLineSegment(Point var0, Point var1) {
    double var2 = var0.getX();
    double var4 = var0.getY();
    double var6 = var1.getX();
    double var8 = var1.getY();
    return new Transform(var6 - var2, var4 - var8, var2, var8 - var4, var6 - var2, var4);
  }

  public static final Transform matchTwoSegments(Point var0, Point var1, Point var2, Point var3) {
    Transform var4 = matchLineSegment(var0, var1);
    Transform var5 = matchLineSegment(var2, var3);
    return var5.compose(var4.invert());
  }

  public static final Transform basisChange(Point var0, Point var1) {
    double var2 = var0.getX();
    double var4 = var0.getY();
    double var6 = var1.getX();
    double var8 = var1.getY();
    double var10 = 1.0D / (var2 * var8 - var6 * var4);
    return new Transform(var8 * var10, -var6 * var10, 0.0D, -var4 * var10, var2 * var10, 0.0D);
  }

  public static final Transform basisChange(Point var0, Point var1, Point var2) {
    double var3 = var0.getX();
    double var5 = var0.getY();
    double var7 = var1.getX() - var3;
    double var9 = var1.getY() - var5;
    double var11 = var2.getX() - var3;
    double var13 = var2.getY() - var5;
    double var15 = 1.0D / (var7 * var13 - var11 * var9);
    double var17 = var13 * var15;
    double var19 = -var11 * var15;
    double var21 = -var9 * var15;
    double var23 = var7 * var15;
    double var25 = -(var17 * var3 + var19 * var5);
    double var27 = -(var21 * var3 + var23 * var5);
    return new Transform(var17, var19, var25, var21, var23, var27);
  }

  public final String toString() {
    return "[ " + this.a + " " + this.b + " " + this.c + " ]\n" + "[ " + this.d + " " + this.e + " " + this.f + " ]\n" + "[ 0 0 1 ]";
  }

  public final boolean equals(Object var1) {
    if (!(var1 instanceof Transform)) {
      return false;
    } else {
      Transform var2 = (Transform) var1;
      return this.a == var2.a && this.b == var2.b && this.c == var2.c && this.d == var2.d && this.e == var2.e && this.f == var2.f;
    }
  }

  public final int hashCode() {
    return (new Double(this.a)).hashCode() ^ (new Double(this.b)).hashCode() ^ (new Double(this.c)).hashCode() ^ (new Double(this.d)).hashCode() ^ (new Double(this.e)).hashCode() ^ (new Double(this.f)).hashCode();
  }

  public final boolean flips() {
    return this.a * this.e - this.b * this.d < 0.0D;
  }
}
