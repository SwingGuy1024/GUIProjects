//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.Stack;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Transform;

import static java.awt.RenderingHints.*;

@SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision", "unused", "UnnecessaryConstantArrayCreationExpression", "rawtypes", "RedundantCast", "ReassignedVariable", "UseOfObsoleteCollectionType", "unchecked", "RedundantThrows", "DataFlowIssue", "FieldMayBeFinal"})
public class GeoGraphics {
  private Graphics2D graphics;
  private Transform transform;
  private Stack pushed;
  private Canvas component;

  public GeoGraphics(Graphics var1, Transform var2, Canvas var3) {
    this.graphics = (Graphics2D) var1;
    this.transform = var2;
    this.pushed = null;
    this.component = var3;
    installRenderingHints(this.graphics);
  }
  
  public static void installRenderingHints(Graphics2D var1) {
    // These are an attempt to use the higher resolution for drawing. They didn't work.
    var1.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    var1.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
    var1.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
    var1.setRenderingHint(KEY_RESOLUTION_VARIANT, VALUE_RESOLUTION_VARIANT_DPI_FIT);
    var1.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
    var1.setStroke(new BasicStroke(1.0f));
  }

  public GeoGraphics(Graphics var1, Transform var2) {
    this(var1, var2, (Canvas) null);
  }

  void dispose() {
    this.graphics.dispose();
  }

  public final Graphics2D getDirectGraphics() {
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
    // I replaced this with the line below it to try to improve the drawing resolution, but it didn't work.
//    this.graphics.drawLine((int) var9, (int) var11, (int) var13, (int) var15);
    Line2D line = new Line2D.Double(var9, var11, var13, var15);
    graphics.draw(line);
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
    int size = var3 - var2;
    double[] var6 = new double[size];
    double[] var7 = new double[size];
    int var8 = 0;

    for (int var9 = var2; var9 < var3; ++var9) {
      double var10 = var1[var9].getX();
      double var12 = var1[var9].getY();
      var6[var8] = this.transform.applyX(var10, var12);
      var7[var8] = this.transform.applyY(var10, var12);
      ++var8;
    }
    
    // This used to call this.graphics.fillPolygon() or .drawPolygon. I replaced it with a Path2D in a failed
    // attempt to fix the drawing resolution. I get the same results..
    java.awt.geom.Path2D path2D = new java.awt.geom.Path2D.Double();
    path2D.moveTo(var6[0], var7[0]);
    for (int i=1; i<var6.length; ++i) {
      path2D.lineTo(var6[i], var7[i]);
    }
    path2D.lineTo(var6[0], var7[0]);

    if (var4) {
      this.graphics.fill(path2D);
//      this.graphics.fillPolygon(var6, var7, var5);
    } else {
      this.graphics.draw(path2D);
//      this.graphics.drawPolygon(var6, var7, var5);
    }
  }

  public final void drawPolygon(Polygon var1, boolean var2) {
    int size = var1.numVertices();
    int[] var4 = new int[size];
    int[] var5 = new int[size];

    for (int var6 = 0; var6 < size; ++var6) {
      Point var7 = var1.getVertex(var6);
      double var8 = var7.getX();
      double var10 = var7.getY();
      var4[var6] = (int) this.transform.applyX(var8, var10);
      var5[var6] = (int) this.transform.applyY(var8, var10);
    }

    if (var2) {
      this.graphics.fillPolygon(var4, var5, size);
    } else {
      this.graphics.drawPolygon(var4, var5, size);
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
