//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package csk.taprats.toolkit;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Rect;
import csk.taprats.geometry.Transform;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

@SuppressWarnings({"MagicNumber", "UnclearExpression", "ProtectedField", "unused",
    "UnnecessaryConstantArrayCreationExpression", "ImplicitCallToSuper", "rawtypes", "RedundantCast",
    "ReassignedVariable", "RedundantThrows", "StringConcatenation", "deprecation", "OverlyBroadCatchBlock", "override",
    "CatchMayIgnoreException", "ConcatenationWithEmptyString", "SingleCharacterStringConcatenation",
    "FinalStaticMethod", "DataFlowIssue", "UnnecessaryLocalVariable", "FinalPrivateMethod", "FieldMayBeFinal",
    "UnusedReturnValue", "UnnecessaryExplicitNumericCast"})
public class GeoView extends Canvas {
  private double left;
  private double top;
  private double width;
  private double theta;
  private Transform transform;
  private Transform inverse;
  private Image backing_store;
  private boolean sink;
  protected int last_x;
  protected int last_y;
  private boolean do_tracking;
  protected int track_x;
  protected int track_y;
  private Dimension last_size;
  private final boolean j2d = true;
  private final boolean j2d_possible = true;
  private static Color[] bevel_h_0 = new Color[]{
      new Color(1, 1, 1),
      new Color(1, 1, 1),
      new Color(1, 1, 1),
      new Color(76, 76, 76),
      new Color(103, 103, 103)
  };
  private static Color[] bevel_h_1 = new Color[]{
      new Color(27, 27, 27),
      new Color(53, 53, 53),
      new Color(78, 78, 78),
      new Color(153, 153, 153),
      new Color(180, 180, 180)
  };
  private static Color[] bevel_h_2 = new Color[]{
      new Color(127, 127, 127),
      new Color(178, 178, 178),
      new Color(228, 228, 228),
      new Color(255, 255, 255),
      new Color(255, 255, 255)
  };
  private static Color[] bevel_h_3 = new Color[]{
      new Color(154, 154, 154),
      new Color(205, 205, 205),
      new Color(255, 255, 255),
      new Color(255, 255, 255),
      new Color(255, 255, 255)
  };
  private static Color[] bevel_v = new Color[]{
      new Color(52, 52, 52),
      new Color(103, 103, 103),
      new Color(228, 228, 228),
      new Color(255, 255, 255)
  };

  public GeoView(double var1, double var3, double var5) {
    this.left = var1;
    this.top = var3;
    this.width = var5;
    this.transform = null;
    this.backing_store = null;
    this.sink = false;
    this.last_size = new Dimension(200, 200);
    this.do_tracking = false;
    this.setBackground(Color.white);
    MouseENator mouseENator = new MouseENator();
    this.addMouseListener(mouseENator);
    this.addMouseMotionListener(mouseENator);
//    this.j2d = false;
//    this.j2d_possible = false;

    try {
      Class var8 = Class.forName("java.awt.Graphics2D");
//      this.j2d_possible = true;
//      this.j2d = true;
      this.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent var1) {
          if (var1.getKeyCode() == 113) {  // 0x71 (113 = 127 - 14 = 0x7F - 0x0E = 0x71) = VK_F2
//            GeoView.this.j2d = !GeoView.this.j2d;
            GeoView.this.forceRedraw();
          } else if (var1.getKeyCode() == 66) { // = 0x42 (66 = 64 + 2 = 0x40 + 0x02) = VK_B
            System.err.println("" + GeoView.this.left + " " + GeoView.this.top + " " + GeoView.this.width);
          }

        }
      });
    } catch (Exception var9) {
    }

    this.forceRedraw();
  }

  public void update(Graphics var1) {
    this.paint(var1);
  }

  public void setSink(boolean var1) {
    this.sink = var1;
  }

  public void setTrack(boolean var1) {
    this.do_tracking = var1;
  }

  public void paint(Graphics var1) {
    this.buildBackingStore();
    var1.drawImage(this.backing_store, 0, 0, this);
  }

  private final void buildBackingStore() {
    if (this.backing_store == null) {
      Dimension var1 = this.getSize();
      this.backing_store = this.createImage(var1.width, var1.height);
      this.redraw();
    }

  }

  public void forceRedraw() {
    this.redraw();
    Graphics var1 = this.getGraphics();
    if (var1 != null) {
      var1.drawImage(this.backing_store, 0, 0, this);
    }

    this.repaint();
  }

  protected Graphics2D getBackGraphics() {
    return (Graphics2D) this.backing_store.getGraphics();
  }

  public void setSize(int var1, int var2) {
    super.setSize(var1, var2);
    this.last_size = new Dimension(var1, var2);
  }

  public Dimension getPreferredSize() {
    return this.last_size;
  }

  public Dimension getMinimumSize() {
    return this.last_size;
  }

  public void setBounds(double var1, double var3, double var5) {
    this.left = var1;
    this.top = var3;
    this.width = var5;
    this.transform = null;
    this.forceRedraw();
  }

  public void setTheta(double var1) {
    this.theta = var1;
    this.transform = null;
    this.forceRedraw();
  }

  public boolean redraw() {
    if (this.backing_store == null) {
      return false;
    } else {
      this.computeTransform();
      Graphics2D var1 = this.getBackGraphics();
//      if (this.j2d) {
        Graphics2D var2 = (Graphics2D) var1;
        GeoGraphics.installRenderingHints(var2);
//      }

      Dimension var6 = this.getSize();
      var1.clearRect(0, 0, var6.width, var6.height);
      GeoGraphics var3 = new GeoGraphics(var1, this.transform, this);
      this.redraw(var3);
      if (this.sink) {
        int wd = var6.width;
        int ht = var6.height;
        drawH(wd, 0, bevel_h_0, var1);
        drawH(wd, 1, bevel_h_1, var1);
        drawH(wd, ht - 2, bevel_h_2, var1);
        drawH(wd, ht - 1, bevel_h_3, var1);
        drawV(ht, 0, bevel_v[0], var1);
        drawV(ht, 1, bevel_v[1], var1);
        drawV(ht, wd - 2, bevel_v[1], var1);
        drawV(ht, wd - 1, bevel_v[2], var1);
      }

      return true;
    }
  }

  protected void redraw(GeoGraphics var1) {
  }

  protected final void computeTransform() {
    if (this.transform == null) {
      Dimension var1 = this.getSize();
      double var2 = (double) var1.width;
      double var4 = (double) var1.height;
      double var6 = var2 / var4;
      double var8 = this.width / var6;
      Point var10 = new Point(var2 / 2.0D, var4 / 2.0D);
      Transform var11 = Transform.translate(-this.left, -(this.top - var8));
      Transform var12 = Transform.scale(var2 / this.width, -(var4 / var8));
      Transform var13 = Transform.translate(0.0D, var4);
      Transform var14 = Transform.rotateAroundPoint(var10, this.theta);
      this.transform = var14.compose(var13.compose(var12.compose(var11)));
      this.inverse = this.transform.invert();
    }

  }

  public void invalidate() {
    super.invalidate();
    this.backing_store = null;
    this.transform = null;
  }

  public final double getLeft() {
    return this.left;
  }

  public final double getTop() {
    return this.top;
  }

  public final double getViewWidth() {
    return this.width;
  }

  public final double getTheta() {
    return this.theta;
  }

  public final Polygon getBoundary() {
    Dimension var1 = this.getSize();
    double var2 = (double) var1.width;
    double var4 = (double) var1.height;
    Polygon var6 = new Polygon(4);
    var6.addVertex(this.inverse.apply(0.0D, 0.0D));
    var6.addVertex(this.inverse.apply(var2, 0.0D));
    var6.addVertex(this.inverse.apply(var2, var4));
    var6.addVertex(this.inverse.apply(0.0D, var4));
    return var6;
  }

  public final Rect getBoundingBox() {
    return this.getBoundary().getBoundingBox();
  }

  public void lookAt(Rect var1) {
    var1 = var1.centralScale(1.25D);
    Dimension var2 = this.getSize();
    Transform var3 = var1.centerInside(new Rect(0.0D, 0.0D, (double) var2.width, (double) var2.height));
    Transform var4 = var3.invert();
    Point var5 = var4.apply(new Point(0.0D, 0.0D));
    Point var6 = var4.apply(new Point((double) var2.width, 0.0D));
    this.left = var5.getX();
    this.top = var5.getY();
    this.width = var6.getX() - var5.getX();
    this.theta = 0.0D;
    this.transform = null;
    this.forceRedraw();
  }

  public final Point worldToScreen(Point var1) {
    if (this.transform == null) {
      this.computeTransform();
    }

    return this.transform.apply(var1);
  }

  public final Point screenToWorld(Point var1) {
    if (this.transform == null) {
      this.computeTransform();
    }

    return this.inverse.apply(var1);
  }

  public final Point screenToWorld(int var1, int var2) {
    double var3 = (double) var1;
    double var5 = (double) var2;
    return this.inverse.apply(new Point(var3, var5));
  }

  public final Transform getTransform() {
    this.computeTransform();
    return this.transform;
  }

  public final Transform getInverseTransform() {
    this.computeTransform();
    return this.inverse;
  }

  private final void startMove(MouseEvent var1) {
    this.last_x = var1.getX();
    this.last_y = var1.getY();
  }

  private final void startRotate(MouseEvent var1) {
    this.last_x = var1.getX();
    this.last_y = var1.getY();
  }

  private final void startScale(MouseEvent var1) {
    this.last_x = var1.getX();
    this.last_y = var1.getY();
  }

  private final void dragMove(MouseEvent var1) {
    int var2 = var1.getX();
    int var3 = var1.getY();
    Point var4 = this.screenToWorld(this.last_x, this.last_y);
    Point var5 = this.screenToWorld(var2, var3);
    this.left -= var5.getX() - var4.getX();
    this.top -= var5.getY() - var4.getY();
    this.transform = null;
    this.forceRedraw();
    this.last_x = var2;
    this.last_y = var3;
  }

  protected void dragRotate(MouseEvent var1) {
    int var2 = var1.getX();
    int var3 = var1.getY();
    Point var4 = this.screenToWorld(this.last_x, this.last_y);
    Point var5 = this.screenToWorld(var2, var3);
    Dimension var6 = this.getSize();
    Point var7 = this.screenToWorld(var6.width / 2, var6.height / 2);
    double var8 = var7.sweep(var5, var4);
    this.theta += var8;
    this.transform = null;
    this.forceRedraw();
    this.last_x = var2;
    this.last_y = var3;
  }

  private final void dragScale(MouseEvent var1) {
    int var2 = var1.getX();
    int var3 = var1.getY();
    Point var4 = this.screenToWorld(this.last_x, this.last_y);
    Point var5 = this.screenToWorld(var2, var3);
    Dimension var6 = this.getSize();
    double var7 = (double) var6.width;
    double var9 = (double) var6.height;
    double var11 = var7 / var9;
    double var13 = this.width / var11;
    Point var15 = new Point(this.left + this.width * 0.5D, this.top - var13 * 0.5D);
    double var16 = var15.dist(var4);
    double var18 = var15.dist(var5);
    if (this.width * var16 < 10000.0D * var18) {
      double var20 = var16 / var18;
      double var22 = this.width * var20;
      this.left -= (var22 - this.width) * 0.5D;
      this.top += (var13 * var20 - var13) * 0.5D;
      this.width *= var20;
      this.transform = null;
      this.forceRedraw();
    }

    this.last_x = var2;
    this.last_y = var3;
  }

  private final void commitMove(MouseEvent var1) {
  }

  private final void commitRotate(MouseEvent var1) {
  }

  private final void commitScale(MouseEvent var1) {
  }

  public static boolean isShift(MouseEvent var0) {
    return var0.isShiftDown();
  }

  public static boolean isControl(MouseEvent var0) {
    return var0.isControlDown();
  }

  public static boolean isAlt(MouseEvent var0) {
    return var0.isAltDown();
  }

  public static boolean isButton(MouseEvent var0, int var1) {
    switch (var1) {
      case 1:
      default:
        return (var0.getModifiers() & (8 | 4)) == 0;
      case 2:
        return (var0.getModifiers() & 8) != 0;
      case 3:
        return (var0.getModifiers() & 4) != 0;
    }
  }

  private static final void drawH(int var0, int var1, Color[] var2, Graphics var3) {
    var3.setColor(var2[0]);
    var3.drawLine(0, var1, 0, var1);
    var3.setColor(var2[1]);
    var3.drawLine(1, var1, 1, var1);
    var3.setColor(var2[2]);
    var3.drawLine(2, var1, var0 - 2, var1);
    var3.setColor(var2[3]);
    var3.drawLine(var0 - 2, var1, var0 - 2, var1);
    var3.setColor(var2[4]);
    var3.drawLine(var0 - 1, var1, var0 - 1, var1);
  }

  private static final void drawV(int var0, int var1, Color var2, Graphics var3) {
    var3.setColor(var2);
    var3.drawLine(var1, 2, var1, var0 - 2);
  }

  protected final boolean haveJava2D() {
    return this.j2d;
  }

  public final void setJava2D(boolean var1) {
//    if (this.j2d_possible && this.j2d != var1) {
//      this.j2d = var1;
//      this.forceRedraw();
  }

  class MouseENator implements MouseListener, MouseMotionListener {
    MouseENator() {
    }

    public void mouseClicked(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
    }

    public void mouseEntered(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
      if (GeoView.this.do_tracking) {
        GeoView.this.requestFocus();
      }

    }

    public void mouseExited(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
    }

    public void mousePressed(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
      if (GeoView.isShift(var1)) {
        if (GeoView.isButton(var1, 1)) {
          GeoView.this.startMove(var1);
        } else if (GeoView.isButton(var1, 2)) {
          GeoView.this.startRotate(var1);
        } else if (GeoView.isButton(var1, 3)) {
          GeoView.this.startScale(var1);
        }
      }

    }

    public void mouseReleased(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
      if (GeoView.isShift(var1)) {
        if (GeoView.isButton(var1, 1)) {
          GeoView.this.commitMove(var1);
        } else if (GeoView.isButton(var1, 2)) {
          GeoView.this.commitRotate(var1);
        } else if (GeoView.isButton(var1, 3)) {
          GeoView.this.commitScale(var1);
        }
      }

    }

    public void mouseMoved(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
    }

    public void mouseDragged(MouseEvent var1) {
      GeoView.this.track_x = var1.getX();
      GeoView.this.track_y = var1.getY();
      if (GeoView.isShift(var1)) {
        if (GeoView.isButton(var1, 1)) {
          GeoView.this.dragMove(var1);
        } else if (GeoView.isButton(var1, 2)) {
          GeoView.this.dragRotate(var1);
        } else if (GeoView.isButton(var1, 3)) {
          GeoView.this.dragScale(var1);
        }
      }

    }
  }
}
