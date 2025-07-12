//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Rect;
import csk.taprats.geometry.Transform;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.toolkit.GeoView;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

@SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision", "UnclearExpression", "ProtectedField", "PackageVisibleField", "UnusedAssignment"})
public class Editor extends GeoView {
  protected PenroseTile tile;
  protected App app;
  boolean first;
  int drag_edge;
  int drag_vert;

  public Editor(PenroseTile var1, boolean var2) {
    this(var1, var2, getBoundingRect(var1, var2));
  }

  protected Editor(PenroseTile var1, boolean var2, Rect var3) {
    super(0.0D, 0.0D, 1.0D);
    this.tile = var1;
    this.first = var2;
    this.drag_edge = -1;
    this.setSink(true);
    this.setBackground(new Color(0.9372549F, 0.9372549F, 0.9372549F));
    MouseGuy var4 = new MouseGuy();
    this.addMouseListener(var4);
    this.addMouseMotionListener(var4);
    this.setBounds(var3.getLeft(), var3.getTop(), var3.getWidth());
  }

  private static Rect getBoundingRect(PenroseTile var0, boolean var1) {
    Point[] var2;
    if (var1) {
      var2 = var0.getFirstShape();
    } else {
      var2 = var0.getSecondShape();
    }

    Rect var3 = (new Polygon(var2)).getBoundingBox().centralScale(1.25D);
    double var4 = Math.max(var3.getWidth(), var3.getHeight());
    Point var6 = new Point(var3.getLeft() + 0.5D * var3.getWidth(), var3.getBottom() + 0.5D * var3.getHeight());
    return new Rect(var6.getX() - 0.5D * var4, var6.getY() + 0.5D * var4, var4, var4);
  }

  public void setApp(App var1) {
    this.app = var1;
  }

  public void setTile(PenroseTile var1) {
    this.tile = var1;
    this.drag_edge = -1;
    this.drag_vert = -1;
    Rect var2 = getBoundingRect(var1, this.first);
    this.setBounds(var2.getLeft(), var2.getTop(), var2.getWidth());
  }

  @Override
  public void redraw(GeoGraphics var1) {
    Color var5 = Color.black;
    Color var6 = null;
    Color var7 = new Color(0.0F, 0.0F, 0.25F);
    Color var8 = new Color(0.0F, 0.0F, 0.75F);
    Color var9 = new Color(1.0F, 0.0F, 0.0F);
    Point[] var2;
    Point[] var3;
    Point[][] var4;
    int var10;
    if (this.first) {
      var2 = this.tile.getFirstShape();
      var6 = PenroseApp.STANDARD_COLOR;
      var3 = new Point[this.tile.numFirstEdges()];
      var4 = new Point[this.tile.numFirstEdges()][];

      for (var10 = 0; var10 < this.tile.numFirstEdges(); ++var10) {
        var3[var10] = this.tile.getFirstVertex(var10);
        var4[var10] = this.tile.getFirstEdge(var10);
      }
    } else {
      var2 = this.tile.getSecondShape();
      var6 = PenroseApp.BRIGHTER_COLOR;
      var3 = new Point[this.tile.numSecondEdges()];
      var4 = new Point[this.tile.numSecondEdges()][];

      for (var10 = 0; var10 < this.tile.numSecondEdges(); ++var10) {
        var3[var10] = this.tile.getSecondVertex(var10);
        var4[var10] = this.tile.getSecondEdge(var10);
      }
    }

    var1.setColor(var6);
    var1.drawPolygon(var2, true);
    var1.setColor(var5);
    var1.drawPolygon(var2, false);
    Graphics var19 = var1.getDirectGraphics();

    for (int var11 = 0; var11 < var3.length; ++var11) {
      Point var12 = var3[var11];
      Point var13 = var3[(var11 + 1) % var3.length];
      Point var14 = this.worldToScreen(var12);
      int var15 = (int) var14.getX();
      int var16 = (int) var14.getY();
      var19.setColor(var7);
      var19.drawRect(var15 - 2, var16 - 2, 4, 4);
      if (var12.dist(var13) > 1.0E-7D) {
        var19.setColor(var8);
        Point[] var17 = var4[var11];
        if (this.first) {
          var17 = this.tile.getFirstTransform(var11).apply(var17);
        } else {
          var17 = this.tile.getSecondTransform(var11).apply(var17);
        }

        for (int var18 = 1; var18 < var17.length - 1; ++var18) {
          var14 = this.worldToScreen(var17[var18]);
          var15 = (int) var14.getX();
          var16 = (int) var14.getY();
          if (var11 == this.drag_edge && var18 == this.drag_vert) {
            var19.setColor(var9);
            var19.fillOval(var15 - 2, var16 - 2, 4, 4);
            var19.setColor(var8);
          } else {
            var19.fillOval(var15 - 2, var16 - 2, 4, 4);
          }
        }
      }
    }

  }

  @SuppressWarnings({"PublicConstructorInNonPublicClass", "ImplicitCallToSuper", "unused", "UnusedAssignment", "IfStatementWithIdenticalBranches"})
  class MouseGuy extends MouseAdapter implements MouseMotionListener {
    public MouseGuy() {
    }

    @Override
    public void mouseReleased(MouseEvent var1) {
      Editor.this.drag_edge = -1;
      Editor.this.drag_vert = -1;
      Editor.this.app.doUpdate();
    }

    @Override
    public void mousePressed(MouseEvent var1) {
      if (!GeoView.isShift(var1)) {
        Editor.this.drag_edge = -1;
        Editor.this.drag_vert = -1;
        Point var5 = new Point(var1.getX(), var1.getY());
        Point[] var2;
        Point[] var3;
        Point[][] var4;
        int var6;
        if (Editor.this.first) {
          var2 = Editor.this.tile.getFirstShape();
          var3 = new Point[Editor.this.tile.numFirstEdges()];
          var4 = new Point[Editor.this.tile.numFirstEdges()][];

          for (var6 = 0; var6 < Editor.this.tile.numFirstEdges(); ++var6) {
            var3[var6] = Editor.this.tile.getFirstVertex(var6);
            var4[var6] = Editor.this.tile.getFirstEdge(var6);
          }
        } else {
          var2 = Editor.this.tile.getSecondShape();
          var3 = new Point[Editor.this.tile.numSecondEdges()];
          var4 = new Point[Editor.this.tile.numSecondEdges()][];

          for (var6 = 0; var6 < Editor.this.tile.numSecondEdges(); ++var6) {
            var3[var6] = Editor.this.tile.getSecondVertex(var6);
            var4[var6] = Editor.this.tile.getSecondEdge(var6);
          }
        }

        Point var7;
        Point var8;
        Point[] var9;
        Transform var10;
        int var11;
        Point var12;
        for (var6 = 0; var6 < var3.length; ++var6) {
          var7 = var3[var6];
          var8 = var3[(var6 + 1) % var3.length];
          if (var7.dist(var8) > 1.0E-7D) {
            var9 = var4[var6];
            var10 = null;
            if (Editor.this.first) {
              var10 = Editor.this.tile.getFirstTransform(var6);
            } else {
              var10 = Editor.this.tile.getSecondTransform(var6);
            }

            for (var11 = 1; var11 < var9.length - 1; ++var11) {
              var12 = Editor.this.worldToScreen(var10.apply(var9[var11]));
              if (var5.dist2(var12) < 49.0D) {
                if (GeoView.isControl(var1)) {
                  Point[] var13 = new Point[var9.length - 1];
                  System.arraycopy(var9, 0, var13, 0, var11);
                  System.arraycopy(var9, var11 + 1, var13, var11, var9.length - (var11 + 1));
                  if (Editor.this.first) {
                    Editor.this.tile.setFirstEdge(var6, var13);
                  } else {
                    Editor.this.tile.setSecondEdge(var6, var13);
                  }

                  Editor.this.app.doUpdate();
                  return;
                }

                Editor.this.drag_edge = var6;
                Editor.this.drag_vert = var11;
                Editor.this.app.doUpdate();
                return;
              }
            }
          }
        }

        if (GeoView.isControl(var1)) {
          Editor.this.app.doUpdate();
        } else {
          for (var6 = 0; var6 < var3.length; ++var6) {
            var7 = var3[var6];
            var8 = var3[(var6 + 1) % var3.length];
            if (var7.dist(var8) > 1.0E-7D) {
              var9 = var4[var6];
              var10 = null;
              if (Editor.this.first) {
                var10 = Editor.this.tile.getFirstTransform(var6);
              } else {
                var10 = Editor.this.tile.getSecondTransform(var6);
              }

              for (var11 = 1; var11 < var9.length; ++var11) {
                var12 = Editor.this.worldToScreen(var10.apply(var9[var11 - 1]));
                Point var16 = Editor.this.worldToScreen(var10.apply(var9[var11]));
                if (var5.distToSegment(var12, var16) < 7.0D) {
                  Point var14 = var10.invert().apply(Editor.this.screenToWorld(var5));
                  Point[] var15 = new Point[var9.length + 1];
                  System.arraycopy(var9, 0, var15, 0, var11);
                  var15[var11] = var14;
                  System.arraycopy(var9, var11, var15, var11 + 1, var9.length - var11);
                  if (Editor.this.first) {
                    Editor.this.tile.setFirstEdge(var6, var15);
                  } else {
                    Editor.this.tile.setSecondEdge(var6, var15);
                  }

                  Editor.this.drag_edge = var6;
                  Editor.this.drag_vert = var11;
                  Editor.this.app.doUpdate();
                  return;
                }
              }
            }
          }

          Editor.this.app.doUpdate();
        }
      }
    }

    @Override
    public void mouseDragged(MouseEvent var1) {
      if (!GeoView.isShift(var1)) {
        if (Editor.this.drag_edge != -1) {
          Point[] var2;
          Transform var3;
          if (Editor.this.first) {
            var2 = Editor.this.tile.getFirstEdge(Editor.this.drag_edge);
            var3 = Editor.this.tile.getFirstTransform(Editor.this.drag_edge);
            var2[Editor.this.drag_vert] = var3.invert().apply(Editor.this.screenToWorld(var1.getX(), var1.getY()));
            Editor.this.app.doUpdate();
          } else {
            var2 = Editor.this.tile.getSecondEdge(Editor.this.drag_edge);
            var3 = Editor.this.tile.getSecondTransform(Editor.this.drag_edge);
            var2[Editor.this.drag_vert] = var3.invert().apply(Editor.this.screenToWorld(var1.getX(), var1.getY()));
            Editor.this.app.doUpdate();
          }
        }
      }
    }
  }
}
