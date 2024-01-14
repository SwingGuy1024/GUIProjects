//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.penrose;

import csk.taprats.general.Input;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Transform;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.toolkit.GeoView;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Vector;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "ProtectedField", "unused", "AssignmentOrReturnOfFieldWithMutableType", "rawtypes", "LiteralAsArgToStringEquals", "ReassignedVariable", "UseOfObsoleteCollectionType", "unchecked", "RedundantThrows", "StringConcatenation", "override", "NestedTryStatement", "CatchMayIgnoreException", "ConcatenationWithEmptyString", "SingleCharacterStringConcatenation"})
public class Viewer extends GeoView {
  protected PenroseTile tile;
  protected Transform[] first_xforms;
  protected Transform[] second_xforms;

  Viewer(PenroseTile var1) {
    super(0.0D, 1.0D, 1.0D);
    this.tile = var1;
    this.setBackground(new Color(0.9372549F, 0.9372549F, 0.9372549F));
    this.setSink(true);
  }

  public void setTile(PenroseTile var1) {
    this.tile = var1;
    if (var1 instanceof KiteDart) {
      this.loadConfig(Places.p2_first, Places.p2_second);
    } else {
      this.loadConfig(Places.p3_first, Places.p3_second);
    }

    this.forceRedraw();
  }

  public void loadConfig(Transform[] var1, Transform[] var2) {
    this.first_xforms = var2;
    this.second_xforms = var1;
    this.forceRedraw();
  }

  public void loadConfig(Reader var1) {
    try {
      Input var2 = new Input(var1);
      int var3 = var2.readInt();
      int var4 = var2.readInt();
      Transform var5 = Transform.scale(1.0D / Math.max(var3, var4));
      Vector var6 = new Vector();
      Vector var7 = new Vector();

      while (true) {
        try {
          String var12 = var2.readString();
          Transform var13 = new Transform(var2.readDouble(), var2.readDouble(), var2.readDouble(), var2.readDouble(), var2.readDouble(), var2.readDouble());
          if (var12.equals("first")) {
            var6.addElement(var5.compose(var13));
          } else if (var12.equals("second")) {
            var7.addElement(var5.compose(var13));
          }
        } catch (IOException var10) {
          Transform[] var8 = new Transform[var6.size()];
          Transform[] var9 = new Transform[var7.size()];
          var6.copyInto(var8);
          var7.copyInto(var9);
          this.loadConfig(var8, var9);
          break;
        }
      }
    } catch (IOException var11) {
    }

  }

  public void redraw(GeoGraphics var1) {
    if (this.first_xforms != null && this.second_xforms != null) {
      Point[] var2 = this.tile.getFirstShape();
      Point[] var3 = this.tile.getSecondShape();
      Color var4 = Color.black;
      Color var5 = new Color(0.87058824F, 0.8392157F, 0.7764706F);
      Color var6 = new Color(0.9372549F, 0.90588236F, 0.8392157F);

      int var7;
      Point[] var8;
      for (var7 = 0; var7 < this.first_xforms.length; ++var7) {
        var8 = this.first_xforms[var7].apply(var2);
        var1.setColor(var5);
        var1.drawPolygon(var8, true);
        var1.setColor(var4);
        var1.drawPolygon(var8, false);
      }

      for (var7 = 0; var7 < this.second_xforms.length; ++var7) {
        var8 = this.second_xforms[var7].apply(var3);
        var1.setColor(var6);
        var1.drawPolygon(var8, true);
        var1.setColor(var4);
        var1.drawPolygon(var8, false);
      }

    }
  }

  public void emitShapes(PrintWriter var1) {
    Point[] var2 = this.tile.getFirstShape();
    Point[] var3 = this.tile.getSecondShape();
    var1.println(var2.length);

    int var4;
    for (var4 = 0; var4 < var2.length; ++var4) {
      var1.println("" + var2[var4].getX() + " " + var2[var4].getY());
    }

    var1.println();
    var1.println(var3.length);

    for (var4 = 0; var4 < var3.length; ++var4) {
      var1.println("" + var3[var4].getX() + " " + var3[var4].getY());
    }

  }
}
