//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.geometry;

@SuppressWarnings("UseOfClone")
public class Polygon implements Cloneable {
  Point[] pts;
  int size;
  int total_size;
  int grow;

  public Polygon(int var1, int var2) {
    this.total_size = var1;
    this.grow = var2;
    this.size = 0;
    this.pts = new Point[this.total_size];
  }

  public Polygon(int var1) {
    this(var1, 0);
  }

  public Polygon() {
    this(8);
  }

  public Polygon(Point[] var1) {
    this.pts = new Point[var1.length];
    this.grow = 0;
    this.size = var1.length;
    System.arraycopy(var1, 0, this.pts, 0, var1.length);
  }

  public Polygon(Point[] var1, int var2, int var3) {
    this.size = var3 - var2;
    this.pts = new Point[this.size];
    this.grow = 0;
    System.arraycopy(var1, var2, this.pts, 0, this.size);
  }

  public Object clone() {
    return new Polygon(this.pts, 0, this.size);
  }

  public final void addVertex(Point var1) {
    this.ensureSize(this.size + 1);
    this.pts[this.size] = var1;
    ++this.size;
  }

  public final void removeVertex(int var1) {
    for (int var2 = var1; var2 < this.size - 1; ++var2) {
      this.pts[var2] = this.pts[var2 + 1];
    }

    --this.size;
  }

  public final void insertVertex(int var1, Point var2) {
    this.ensureSize(this.size + 1);

    for (int var3 = this.size; var3 > var1; --var3) {
      this.pts[var3] = this.pts[var3 - 1];
    }

    this.pts[var1] = var2;
    ++this.size;
  }

  public final void setVertex(int var1, Point var2) {
    this.pts[var1] = var2;
  }

  public final Point getVertex(int var1) {
    return this.pts[var1];
  }

  public final int numVertices() {
    return this.size;
  }

  public final void applyTransform(Transform var1) {
    for (int var2 = 0; var2 < this.size; ++var2) {
      this.pts[var2] = var1.apply(this.pts[var2]);
    }

  }

  private final void ensureSize(int var1) {
    int var2 = Math.max(this.total_size, 1);

    while (var2 < var1) {
      if (this.grow == 0) {
        var2 <<= 1;
      } else {
        var2 += this.grow;
      }
    }

    Point[] var3 = new Point[var2];
    System.arraycopy(this.pts, 0, var3, 0, this.size);
    this.pts = var3;
    this.total_size = var2;
  }

  public final String toString() {
    StringBuffer var1 = new StringBuffer();
    var1.append("[");
    boolean var2 = true;

    for (int var3 = 0; var3 < this.size; ++var3) {
      if (var2) {
        var2 = false;
      } else {
        var1.append(", ");
      }

      var1.append(this.pts[var3]);
    }

    var1.append("]");
    return var1.toString();
  }

  public final double arcLength(boolean var1) {
    double var2 = 0.0D;

    for (int var4 = 0; var4 < this.size - 1; ++var4) {
      var2 += this.pts[var4].dist(this.pts[var4 + 1]);
    }

    return var1 ? var2 + this.pts[0].dist(this.pts[this.size - 1]) : var2;
  }

  public final double signedArea() {
    double var1 = 0.0D;

    for (int var3 = 0; var3 < this.size; ++var3) {
      var1 += this.pts[var3].getX() * this.pts[(var3 + 1) % this.size].getY() - this.pts[var3].getY() * this.pts[(var3 + 1) % this.size].getX();
    }

    return var1 * 0.5D;
  }

  public final boolean isCCW() {
    return this.signedArea() > 0.0D;
  }

  public final void forceCCW() {
    if (!this.isCCW()) {
      Point var1 = null;

      for (int var2 = 0; var2 < this.size / 2; ++var2) {
        var1 = this.pts[var2];
        this.pts[var2] = this.pts[this.size - 1 - var2];
        this.pts[this.size - 1 - var2] = var1;
      }
    }

  }

  public final double area() {
    return Math.abs(this.signedArea());
  }

  public static final boolean pointInPoly(Point[] var0, int var1, int var2, Point var3) {
    int var4 = var2 - var1;
    boolean var5 = false;
    double var6 = var3.getX();
    double var8 = var3.getY();
    Point var10 = var0[var1];
    double var11 = var10.getX();
    double var13 = var10.getY();
    double var15 = 0.0D;
    double var17 = 0.0D;
    double var19 = 0.0D;
    double var21 = 0.0D;

    for (int var23 = 0; var23 < var4; ++var23) {
      Point var24 = var0[(var23 + var1 + 1) % var4];
      double var25 = var24.getX();
      double var27 = var24.getY();
      if (var11 < var25) {
        var15 = var11;
        var17 = var13;
        var19 = var25;
        var21 = var27;
      } else {
        var15 = var25;
        var17 = var27;
        var19 = var11;
        var21 = var13;
      }

      if (var15 <= var6 && var6 < var19 && (var6 - var15) * (var21 - var17) > (var8 - var17) * (var19 - var15)) {
        var5 = !var5;
      }

      var11 = var25;
      var13 = var27;
    }

    return var5;
  }

  public final boolean containsPoint(Point var1) {
    return pointInPoly(this.pts, 0, this.size, var1);
  }

  public final Rect getBoundingBox() {
    double var1 = this.pts[0].getX();
    double var3 = this.pts[0].getX();
    double var5 = this.pts[0].getY();
    double var7 = this.pts[0].getY();

    for (int var9 = 1; var9 < this.size; ++var9) {
      double var10 = this.pts[var9].getX();
      double var12 = this.pts[var9].getY();
      var1 = Math.min(var1, var10);
      var3 = Math.max(var3, var10);
      var5 = Math.min(var5, var12);
      var7 = Math.max(var7, var12);
    }

    return new Rect(var1, var7, var3 - var1, var7 - var5);
  }
}
