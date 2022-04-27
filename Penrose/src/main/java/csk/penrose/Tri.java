//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Tri {
  private double x1;
  private double y1;
  private double x2;
  private double y2;
  private double x3;
  private double y3;

  public Tri(double var1, double var3, double var5, double var7, double var9, double var11) {
    this.x1 = var1;
    this.y1 = var3;
    this.x2 = var5;
    this.y2 = var7;
    this.x3 = var9;
    this.y3 = var11;
  }

  public Tri(Point2D var1, Point2D var2, Point2D var3) {
    this.x1 = var1.getX();
    this.y1 = var1.getY();
    this.x2 = var2.getX();
    this.y2 = var2.getY();
    this.x3 = var3.getX();
    this.y3 = var3.getY();
  }

  public void setPoints(double var1, double var3, double var5, double var7, double var9, double var11) {
    this.x1 = var1;
    this.y1 = var3;
    this.x2 = var5;
    this.y2 = var7;
    this.x3 = var9;
    this.y3 = var11;
  }

  public void setPoints(Point2D var1, Point2D var2, Point2D var3) {
    this.x1 = var1.getX();
    this.y1 = var1.getY();
    this.x2 = var2.getX();
    this.y2 = var2.getY();
    this.x3 = var3.getX();
    this.y3 = var3.getY();
  }

  public double getX1() {
    return this.x1;
  }

  public double getY1() {
    return this.y1;
  }

  public double getX2() {
    return this.x2;
  }

  public double getY2() {
    return this.y2;
  }

  public double getX3() {
    return this.x3;
  }

  public double getY3() {
    return this.y3;
  }

  public Point2D getP1() {
    return new Double(this.x1, this.y1);
  }

  public Point2D getP2() {
    return new Double(this.x2, this.y2);
  }

  public Point2D getP3() {
    return new Double(this.x3, this.y3);
  }

  public void getPoints(Point2D var1, Point2D var2, Point2D var3) {
    var1.setLocation(this.x1, this.y1);
    var2.setLocation(this.x2, this.y2);
    var3.setLocation(this.x3, this.y3);
  }
}
