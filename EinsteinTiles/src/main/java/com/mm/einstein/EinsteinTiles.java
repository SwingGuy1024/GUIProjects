package com.mm.einstein;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/27/23
 * <p>Time: 6:21 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class EinsteinTiles extends JPanel {

  public static final double HALF_CIRCLE = 180.0;
  public static final double ROOT_3 = StrictMath.sqrt(3.0);
  public static final double QUARTER_TURN = 90.0;
  public static final double THIRD_TURN = 120.0;
  public static final double THIRTY = 30.0;
  public static final double SIXTY = 60.0;
  public static final double ONE_FIFTY = 150.0;
  public static final AffineTransform IDENTITY = new AffineTransform();
  
  // sqrt(192) is the area of a fedora with a short length of 1, and a long length of sqrt(3). 
  public static final double AREA = StrictMath.sqrt(192.0);
  private final Dimension STARTING_DIMENSION;
  private final double STARTING_ASPECT_RATIO;
  
  public double drawingScale = 1.0;

  public static void main(String[] args) {
    testArea();
    JFrame frame = new JFrame("Einstein Tiles");
    final EinsteinTiles einsteinTiles = new EinsteinTiles();
    frame.add(einsteinTiles);
    einsteinTiles.installDragListener(frame);
    frame.setLocationByPlatform(true);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
  
  private final Shape start = getShape("start", 0.0, 1.0, 20.0);
  private final Shape falcon = getShape("falcon", 0.2, 1.0, 20.0);
  private final Shape hat = getShape("red&blue", 1.0, 2.0, 20.0);
  private final Shape fedora = getFedoraShape2(20.0);
  private final Shape middle = getShape("middle", 1.0, 1.0, 20.0);
  private final Shape turtle = getTurtleShape2(20.0);
  private final Shape boat = getShape("boat", 1.0, 0.3, 20.0);
  private final Shape end = getShape("end", 1.0, 0.0, 20.0);
  private final Shape turtleUp = upright(turtle, 20.0);
  
  @SuppressWarnings("MagicNumber")
  EinsteinTiles() {
    super(new BorderLayout());
    System.out.println("Initial Check");
    double fedoraArea = getArea(1.0, ROOT_3);
    printArea("Fedora", fedoraArea);

    double turtleArea = getArea(ROOT_3, 1.0);
    printArea("Turtle", turtleArea);
    System.out.println();

    final int multiplier = 20;
    JPanel canvas = new JPanel() {

      final double rightShift = 260.0;
      final double upShift = 0;

      @Override
      public void paint(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setTransform(AffineTransform.getScaleInstance(drawingScale, drawingScale));
        // shifts for printing mirror images
        g2.translate(40.0, 110.0);
        paintShape(g2, start);
        paintShape(g2, falcon);
        paintShape(g2, hat);
        paintShape(g2, fedora);
        paintShape(g2, middle);
        double biggerShift = (rightShift+10)*2.0;
        g2.translate(biggerShift, 0.0);
        g2.draw(turtleUp);
        g2.translate(-biggerShift, 0.0);
        paintShape(g2, turtle);
        paintShape(g2, boat);
        paintShape(g2, end);
      }
      
      private void paintShape(Graphics2D g2, Shape shape) {
        g2.draw(shape);
        g2.translate(rightShift, upShift);
        g2.draw(reverse(shape));
        g2.translate(-rightShift, 120.0-upShift);
      }
    };
    // for each added row, add 6 to the y multiplier
    STARTING_DIMENSION = new Dimension(multiplier * 25, multiplier * 50);

    canvas.setSize(STARTING_DIMENSION);
    canvas.setPreferredSize(STARTING_DIMENSION);
//    canvas.setMinimumSize(STARTING_DIMENSION);
    STARTING_ASPECT_RATIO = STARTING_DIMENSION.getWidth() / STARTING_DIMENSION.getHeight();
    
    add(canvas, BorderLayout.CENTER);
  }
  
  private void installDragListener(Window window) {
    ComponentListener componentListener = new ComponentAdapter() {
      @Override
      public void componentResized(final ComponentEvent e) {
        Dimension newSize = e.getComponent().getSize();
        drawingScale = getNewScale(newSize);
        window.repaint();
      }
    };
    window.addComponentListener(componentListener);
  }
  
  private double getNewScale(Dimension newSize) {
    double aspectRatio = newSize.getWidth()/newSize.getHeight();
    if (aspectRatio > STARTING_ASPECT_RATIO) {
      return newSize.getWidth() / STARTING_DIMENSION.getWidth();
    }
    return newSize.getHeight() / STARTING_DIMENSION.getHeight();
  }
  
  private static void printArea(String label, double area) {
    System.out.printf("%s Area = %9.5f (a^2 = %9.5f)%n", label, area, area*area); // NON-NLS
  }
  
  Shape getShape(String name, double alpha, double beta, double scale) {
    System.out.printf("%n%s-:%n", name);
    GeneralPath path = getPath(alpha, beta);
//    System.out.println("0.0,0.0\n");
//    System.out.printf("Area = %9.5f%n", 8.0 * LEG_LENGTH); // NON-NLS
    return path.createTransformedShape(scaledTransform(scale));
  }

  Shape getFedoraShape2(double scale) {
    final double area = 8.0 * ROOT_3;
    printArea("\nFedora (no scale)", area);
    return getShape("Fedora", 1.0, ROOT_3, scale);
//    System.out.println("\nFedora:\n0.0,0.0");
//    GeneralPath path = getPath(ROOT_3, 1.0);
//    return path.createTransformedShape(scale(scale));
  }

  Shape getTurtleShape2(double scale) {
    final double area = 10.0 * ROOT_3;
    printArea("\nTurtle (no scale", area);
    return getShape("Turtle", ROOT_3, 1.0, scale);
//    System.out.println("\nTurtle:\n0.0,0.0");
//    GeneralPath path = getPath(1.0, ROOT_3);
//    return path.createTransformedShape(scale(scale));
  }

  Shape reverse(Shape shape) {
    GeneralPath path = new GeneralPath(shape);
    return path.createTransformedShape(AffineTransform.getScaleInstance(-1.0, 1.0));
  }

  GeneralPath getPath(double alpha, double beta) {
    double origArea = getArea(alpha, beta);
    double a;
    double b;
    // AREA here is the desired area. We use this to recalculate the values of alpha and beta,
    // which we call a and b. Each formula produces the same results, but each fails when one
    // of the two values is zero, hence the test.
    if (alpha == 0.0) {
      double ratio = alpha / beta;
      double bSquared = AREA / (ROOT_3 * ((2 * ratio * ratio) + (ROOT_3 * ratio) + 1.0));
      b = StrictMath.sqrt(bSquared);
      a = ratio * b;
    } else {
      double ratio = beta / alpha;
      double aSquared = AREA / (ROOT_3 * (2 + (ROOT_3 * ratio) + (ratio * ratio)));
      a = StrictMath.sqrt(aSquared);
      b = ratio * a;
    }
    System.out.printf("Transformed (%5.2f, %5.2f) to (%5.2f, %5.2f) -- [%7.4f, %7.4f]%n", alpha, beta, a, b, a+b, a*b); // NON-NLS
    
    // reality check
    double area = getArea(a, b);
    printArea("   Calculated", area);
    printArea("Modified from", origArea);
    System.out.println("0.0,0.0");
    GeneralPath path = new GeneralPath();
    path.moveTo(0.0, 0.0);
    lineTo(path, a, THIRD_TURN);
    lineTo(path, b, THIRTY);
    lineTo(path, b, QUARTER_TURN);
    lineTo(path, a, 0.0);
    lineTo(path, a, SIXTY);
    lineTo(path, b, -THIRTY);
    lineTo(path, b, -QUARTER_TURN);
    lineTo(path, a, 0.0);
    lineTo(path, a, -SIXTY);
    lineTo(path, b, -ONE_FIFTY);
    lineTo(path, b, ONE_FIFTY);
    lineTo(path, a, -THIRD_TURN);
    path.closePath();
    System.out.println("0.0,0.0");
    final double area1 = area(path.getPathIterator(IDENTITY));
    printArea(String.format("Unscaled Area for (%f, %f):", a, b), area1);
    printArea(String.format("  Scaled Area for (%f, %f):", alpha, beta), origArea);
    return path;
  }

  private double getArea(final double a, final double b) {
    return ROOT_3 * ((2 * a*a) + (ROOT_3 * a*b) + (b*b));
  }

  Shape getFedoraShape(double scale) {
    System.out.println("Fedora:\n0.0,0.0");
    GeneralPath path = new GeneralPath();
    path.moveTo(0.0, 0.0);
    lineTo(path, 1.0, THIRD_TURN);
    lineTo(path, ROOT_3, THIRTY);
    lineTo(path, ROOT_3, QUARTER_TURN);
    lineTo(path, 1.0, 0.0);
    lineTo(path, 1.0, SIXTY);
    lineTo(path, ROOT_3, -THIRTY);
    lineTo(path, ROOT_3, -QUARTER_TURN);
    lineTo(path, 1.0, 0.0);
    lineTo(path, 1.0, -SIXTY);
    lineTo(path, ROOT_3, -ONE_FIFTY);
    lineTo(path, ROOT_3, ONE_FIFTY);
    lineTo(path, 1.0, -THIRD_TURN);
    path.closePath();
    System.out.println("0.0,0.0\n");
    System.out.printf("Area = %9.5f%n", 8.0* ROOT_3); // NON-NLS
    return path.createTransformedShape(scaledTransform(scale));
  }
  
  Shape getTurtleShape(double scale) {
    System.out.println("Turtle:\n0.0,0.0");
    GeneralPath path = new GeneralPath();
    path.moveTo(0.0, 0.0);
    lineTo(path, ROOT_3, THIRD_TURN);
    lineTo(path, 1.0, THIRTY);
    lineTo(path, 1.0, QUARTER_TURN);
    lineTo(path, ROOT_3, 0.0);
    lineTo(path, ROOT_3, SIXTY);
    lineTo(path, 1.0, -THIRTY);
    lineTo(path, 1.0, -QUARTER_TURN);
    lineTo(path, ROOT_3, 0.0);
    lineTo(path, ROOT_3, -SIXTY);
    lineTo(path, 1.0, -ONE_FIFTY);
    lineTo(path, 1.0, ONE_FIFTY);
    lineTo(path, ROOT_3, -THIRD_TURN);
    path.closePath();
    System.out.println("0.0,0.0\n");
    System.out.printf("Area = %9.5f%n", 10.0* ROOT_3); // NON-NLS
    return path.createTransformedShape(scaledTransform(scale));
  }
  
  private AffineTransform scaledTransform(double scale) {
    return AffineTransform.getScaleInstance(scale, scale);
  }

  private void lineTo(GeneralPath path, double length, double angleDegrees) {
    if (length != 0.0) {
      Point2D here = path.getCurrentPoint(); // float
      Point2D dest = polarToGrid(length, angleDegrees);
      Point2D sum = new Point2D.Double(here.getX() + dest.getX(), here.getY() + dest.getY());
//    System.out.println("                                                   " + sum.getX() + "," + sum.getY());
      System.out.printf("%9.5f,%9.5f   { Squares: %8.5f,%8.5f }%n", sum.getX(), sum.getY(), sum.getX()*sum.getX(), sum.getY()*sum.getY()); // NON-NLS
      lineTo(path, sum);
    }
  }
  
  private void lineTo(GeneralPath path, Point2D pt) {
    path.lineTo(pt.getX(), pt.getY());
  }
  
  private static Shape upright(Shape shape, double scale) {
    AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI/6.0);
    AffineTransform toCenter = AffineTransform.getTranslateInstance(-3.0*scale, 0.0);
    AffineTransform tipOver = AffineTransform.getRotateInstance(Math.PI);
    AffineTransform back = AffineTransform.getTranslateInstance(3.0*scale, -ROOT_3*scale/2.0);
    rotate.concatenate(toCenter);
    rotate.concatenate(tipOver);
    rotate.concatenate(back);
    return rotate.createTransformedShape(shape);
  } 

  public static Point2D.Double polarToGrid(double radius, double thetaDegrees) {
    double theta = (thetaDegrees * Math.PI) / HALF_CIRCLE;
    double xx = radius * StrictMath.cos(theta);
    double yy = -radius * StrictMath.sin(theta);
    return new Point2D.Double(xx, yy);
  }
  
  public static double area(Shape shape) {
    return area(shape.getPathIterator(IDENTITY));
  }

  /**
   * <p>Get the area of a simple shape. This assumes no outline segments intersect, and all segments are straight
   * lines. So if you created a shape using {@link Path2D#quadTo(double, double, double, double)}, this won't give you
   * the correct area.</p>
   * {@see <a href="https://www.wikihow.com/Calculate-the-Area-of-a-Polygon#Finding-the-Area-of-Irregular-Polygons"
   * >Area of an Irregular Polygon</a>}
   * @param path The PathIterator
   * @return The area of the polygon
   */
  public static double area(PathIterator path) {

    System.out.printf("Area iterator of %s%n", path.getClass()); // NON-NLS
    double[] points = new double[6];
    double forwardSum = 0.0;
    double backSum = 0.0;
    double[] priorPoints = new double[6];
    path.currentSegment(priorPoints);
    double[] startingPoint = Arrays.copyOf(priorPoints, 6);
    path.next();
    while (!path.isDone()) {
      path.currentSegment(points);
      forwardSum += priorPoints[0]*points[1];
      backSum += priorPoints[1] * points[0];
      priorPoints = Arrays.copyOf(points, 6);
      path.next();
    }
    forwardSum += points[0] * startingPoint[1];
    backSum += points[1] * startingPoint[0];
    return Math.abs(forwardSum - backSum)/2.0;
  }
  
  @SuppressWarnings("MagicNumber")
  public static void testArea() {
    final double radius = 5.0;
    Rectangle2D rect1 = new Rectangle2D.Double(3.0, 5.0, 7.0, 3.0);
    System.out.printf("Expecting 21: %8.5f%n", area(rect1)); // NON-NLS
    
    Rectangle2D rect2 = new Rectangle2D.Double(-1.0, -1.0, 3.0, 7.0);
    System.out.printf("Expecting 21: %8.5f%n", area(rect2)); // NON-NLS

    Rectangle2D rect3 = new Rectangle2D.Double(-3.0, 4.0, 3.0, 7.0);
    System.out.printf("Expecting 21: %8.5f%n", area(rect3)); // NON-NLS

    Rectangle2D rect4 = new Rectangle2D.Double(1.0, 1.0, 3.0, 7.0);
    System.out.printf("Expecting 21: %8.5f%n", area(rect4)); // NON-NLS

    Rectangle2D rect5 = new Rectangle2D.Double(-1.0, 4.0, 3.0, 7.0);
    System.out.printf("Expecting 21: %8.5f%n", area(rect5)); // NON-NLS

    GeneralPath hexPath = new GeneralPath();
    Point2D stPt = polarToGrid(radius, 0.0);
    hexPath.moveTo(stPt.getX(), stPt.getY());
    for (int i=1; i<6; ++i) {
      Point2D pt = polarToGrid(radius, i * 60.0);
      hexPath.lineTo(pt.getX(), pt.getY());
    }
    hexPath.closePath();

    Shape hexagon = hexPath.createTransformedShape(IDENTITY);
    double expectedHexArea = (6.0 * ROOT_3 * radius * radius) / 4.0;
    System.out.printf("Expecting %8.5f: %8.5f%n", expectedHexArea, area(hexagon)); // NON-NLS

    final AffineTransform translateInstance = AffineTransform.getTranslateInstance(-20.0, -25.0);
    translateInstance.concatenate(AffineTransform.getScaleInstance(2.0, 2.0));
    Shape bigHexagon = translateInstance.createTransformedShape(hexagon);
    double expectedBigHexArea = expectedHexArea*4.0;
    System.out.printf("Expecting %8.5f: %8.5f%n", expectedBigHexArea, area(bigHexagon)); // NON-NLS
    
    GeneralPath bowTiePath = new GeneralPath(Path2D.WIND_EVEN_ODD);
    bowTiePath.moveTo(0.0, 0.0);
    bowTiePath.lineTo(0.0, 2.0);
    bowTiePath.lineTo(2.0, 0.0);
    bowTiePath.lineTo(4.0, 2.0);
    bowTiePath.lineTo(4.0, 0.0);
    bowTiePath.lineTo(2.0, 2.0);
    bowTiePath.closePath();
    
    Shape bowTie = bowTiePath.createTransformedShape(IDENTITY);
    double expectedBowTieArea = 0.0;
    System.out.printf("Expecting %8.5f: %8.5f%n%n", expectedBowTieArea, area(bowTie)); // NON-NLS
  }
}
