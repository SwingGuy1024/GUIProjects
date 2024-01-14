//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Stack;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Transform;

@SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision", "unused", "UnnecessaryConstantArrayCreationExpression", "rawtypes", "RedundantCast", "ReassignedVariable", "UseOfObsoleteCollectionType", "unchecked", "RedundantThrows", "DataFlowIssue", "FieldMayBeFinal"})
public class GeoGraphics {
  private Graphics graphics;
  private Transform transform;
  private Stack pushed;
  private Canvas component;

  public GeoGraphics(Graphics var1, Transform var2, Canvas var3) {
    this.graphics = var1;
    this.transform = var2;
    this.pushed = null;
    this.component = var3;
  }

  public GeoGraphics(Graphics var1, Transform var2) {
    this(var1, var2, (Canvas) null);
  }

  void dispose() {
    this.graphics.dispose();
  }

  public final Graphics getDirectGraphics() {
    return this.graphics;
  }

  public final Transform getTransform() {
    return this.transform;
  }

  public final void drawLine(double var1, double var3, double var5, double var7) {
    double var9 = this.transform.applyX(var1, var3);
    double var11 = this.transform.applyY(var1, var3);
    double var13 = this.transform.applyX(var5, var7);
    double var15 = this.transform.applyY(var5, var7);
    this.graphics.drawLine((int) var9, (int) var11, (int) var13, (int) var15);
  }

  public final void drawLine(Point var1, Point var2) {
    this.drawLine(var1.getX(), var1.getY(), var2.getX(), var2.getY());
  }

  public final void drawThickLine(double var1, double var3, double var5, double var7, double var9) {
    this.drawThickLine(new Point(var1, var3), new Point(var5, var7), var9);
  }

  public final void drawThickLine(Point var1, Point var2, double var3) {
    Point var5 = var2.subtract(var1).perp();
    Point[] var6 = new Point[]{var1.add(var5.scale(var3)), var1.subtract(var5.scale(var3)), var2.subtract(var5.scale(var3)), var2.add(var5.scale(var3))};
    this.drawPolygon(var6, true);
    this.drawCircle(var1, var3 / 2.0D, true);
    this.drawCircle(var2, var3 / 2.0D, true);
  }

  public final void drawRect(Point var1, double var2, double var4, boolean var6) {
    double var7 = var1.getX();
    double var9 = var1.getY();
    Point[] var11 = new Point[]{var1, new Point(var7 + var2, var9), new Point(var7 + var2, var9 + var4), new Point(var7, var9 + var4)};
    this.drawPolygon(var11, var6);
  }

  public final void drawPolygon(Point[] var1, boolean var2) {
    this.drawPolygon(var1, 0, var1.length, var2);
  }

  public final void drawPolygon(Point[] var1, int var2, int var3, boolean var4) {
    int var5 = var3 - var2;
    int[] var6 = new int[var5];
    int[] var7 = new int[var5];
    int var8 = 0;

    for (int var9 = var2; var9 < var3; ++var9) {
      double var10 = var1[var9].getX();
      double var12 = var1[var9].getY();
      var6[var8] = (int) this.transform.applyX(var10, var12);
      var7[var8] = (int) this.transform.applyY(var10, var12);
      ++var8;
    }

    if (var4) {
      this.graphics.fillPolygon(var6, var7, var5);
    } else {
      this.graphics.drawPolygon(var6, var7, var5);
    }

  }

  public final void drawPolygon(Polygon var1, boolean var2) {
    int var3 = var1.numVertices();
    int[] var4 = new int[var3];
    int[] var5 = new int[var3];

    for (int var6 = 0; var6 < var3; ++var6) {
      Point var7 = var1.getVertex(var6);
      double var8 = var7.getX();
      double var10 = var7.getY();
      var4[var6] = (int) this.transform.applyX(var8, var10);
      var5[var6] = (int) this.transform.applyY(var8, var10);
    }

    if (var2) {
      this.graphics.fillPolygon(var4, var5, var3);
    } else {
      this.graphics.drawPolygon(var4, var5, var3);
    }

  }

  public final void drawCircle(Point var1, double var2, boolean var4) {
    Point var5 = this.transform.apply(new Point(var2, 0.0D));
    Point var6 = this.transform.apply(new Point(0.0D, 0.0D));
    double var7 = var5.dist(var6);
    Point var9 = this.transform.apply(var1);
    Point var10 = var9.subtract(new Point(var7, var7));
    int var11 = (int) (var7 * 2.0D);
    if (var4) {
      this.graphics.fillOval((int) var10.getX(), (int) var10.getY(), var11, var11);
    } else {
      this.graphics.drawOval((int) var10.getX(), (int) var10.getY(), var11, var11);
    }

  }

  public final void drawScreenCircle(Point var1, double var2, boolean var4) {
    Point var5 = this.transform.apply(var1);
    if (var4) {
      this.graphics.fillOval((int) (var5.getX() - var2), (int) (var5.getY() - var2), (int) (2.0D * var2), (int) (2.0D * var2));
    } else {
      this.graphics.drawOval((int) (var5.getX() - var2), (int) (var5.getY() - var2), (int) (2.0D * var2), (int) (2.0D * var2));
    }

  }

  public final void drawCircle(Point var1, double var2) {
    this.drawCircle(var1, var2, false);
  }

  public final void drawImage(Image var1, Point var2) {
    if (this.component != null) {
      Point var3 = this.transform.apply(var2);
      this.graphics.drawImage(var1, (int) var3.getX(), (int) var3.getY(), this.component);
    }

  }

  public final void drawImage(Image var1) {
    if (this.component != null) {
      this.graphics.drawImage(var1, 0, 0, this.component);
    }

  }

  public final Color getColor() {
    return this.graphics.getColor();
  }

  public final void setColor(Color var1) {
    this.graphics.setColor(var1);
  }

  public final void pushAndCompose(Transform var1) {
    if (this.pushed == null) {
      this.pushed = new Stack();
    }

    this.pushed.push(this.transform);
    this.transform = this.transform.compose(var1);
  }

  public final Transform pop() {
    Transform var1 = this.transform;
    this.transform = (Transform) this.pushed.pop();
    return var1;
  }
}
