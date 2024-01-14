//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.geometry;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "unused", "RedundantThrows", "StringConcatenation", "override", "DataFlowIssue", "ReturnOfNull", "FieldMayBeFinal"})
public class Rect {
  private double x;
  private double y;
  private double width;
  private double height;

  public Rect(double var1, double var3, double var5, double var7) {
    this.x = var1;
    this.y = var3;
    this.width = var5;
    this.height = var7;
  }

  public Rect(Point var1, double var2, double var4) {
    this.x = var1.getX();
    this.y = var1.getY();
    this.width = var2;
    this.height = var4;
  }

  public Rect(Point var1, Point var2) {
    this.x = var1.getX();
    this.y = var1.getY();
    this.width = var2.getX() - this.x;
    this.height = this.y - var2.getY();
  }

  public final double getX() {
    return this.x;
  }

  public final double getLeft() {
    return this.x;
  }

  public final double getTop() {
    return this.y;
  }

  public final double getY() {
    return this.y;
  }

  public final double getWidth() {
    return this.width;
  }

  public final double getHeight() {
    return this.height;
  }

  public final double getRight() {
    return this.x + this.width;
  }

  public final double getBottom() {
    return this.y - this.height;
  }

  public final Point getTopLeft() {
    return new Point(this.x, this.y);
  }

  public final Point getBottomLeft() {
    return new Point(this.x, this.y - this.height);
  }

  public final Point getTopRight() {
    return new Point(this.x + this.width, this.y);
  }

  public final Point getBottomRight() {
    return new Point(this.x + this.width, this.y - this.height);
  }

  public final Point getCenter() {
    return new Point(this.x + 0.5D * this.width, this.y - 0.5D * this.height);
  }

  public final Rect union(Rect var1) {
    double var2 = Math.min(this.x, var1.x);
    double var4 = Math.max(this.y, var1.y);
    double var6 = Math.max(this.x + this.width, var1.x + var1.width);
    double var8 = Math.min(this.y - this.height, var1.y - var1.height);
    return new Rect(var2, var4, var6 - var2, var4 - var8);
  }

  public final Rect intersection(Rect var1) {
    double var2 = Math.max(this.x, var1.x);
    double var4 = Math.min(this.y, var1.y);
    double var6 = Math.min(this.x + this.width, var1.x + var1.width);
    double var8 = Math.max(this.y - this.height, var1.y - var1.height);
    return !(var6 < var2) && !(var8 > var4) ? new Rect(var2, var4, var6 - var2, var4 - var8) : null;
  }

  public final Rect centralScale(double var1) {
    double var3 = this.x + this.width * 0.5D;
    double var5 = this.y - this.height * 0.5D;
    double var7 = this.width * var1;
    double var9 = this.height * var1;
    return new Rect(var3 - var7 * 0.5D, var5 + var9 * 0.5D, var7, var9);
  }

  public final Transform centerInside(Rect var1) {
    double var2 = var1.width / this.width;
    double var4 = var1.height / this.height;
    double var6 = Math.min(var2, var4);
    Transform var8 = Transform.scale(var6, var6);
    Point var9 = new Point(this.x + this.width / 2.0D, this.y - this.height / 2.0D);
    Point var10 = new Point(var1.x + var1.width / 2.0D, var1.y - var1.height / 2.0D);
    return Transform.translate(var10).compose(var8).compose(Transform.translate(var9.scale(-1.0D)));
  }

  public final String toString() {
    return "[ (" + this.x + ", " + this.y + "); (" + this.width + ", " + this.height + ") ]";
  }
}
