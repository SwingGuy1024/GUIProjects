package com.mm.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jetbrains.annotations.NotNull;

/**
 * <p>This was created to answer a question on StackOverflow:</p>
 * <p><a href="https://stackoverflow.com/questions/60463402/icon-resources-in-a-java-shebang-application">https://stackoverflow.com/questions/60463402/icon-resources-in-a-java-shebang-application</a></p>
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 6/10/22
 * <p>Time: 7:33 PM
 *
 * @author Miguel Muñoz
 */
@SuppressWarnings("MagicNumber")
public class StarIcon extends JPanel {

  private static final int I_SIZE = 12;
  private static final double D_SIZE = I_SIZE;

  public static void main(String[] args) {
    JFrame frame = new JFrame("Star Icon");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new StarIcon(), BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
  }
  
  StarIcon() {
    super(new BorderLayout());
    Icon icon = makeStarIcon();
    JLabel label = new JLabel("Star", icon, SwingConstants.LEFT);

    // double the font size, just for fun.
    Font labelFont = label.getFont();
    label.setFont(labelFont.deriveFont(labelFont.getSize()*2.0f));

    final int ms = I_SIZE*10;
    final Color bg = label.getBackground();
    label.setBorder(BorderFactory.createMatteBorder(ms, ms, ms, ms, bg));
    add(BorderLayout.CENTER, label);
  }

  @NotNull
  public static Icon makeStarIcon() {
    Image image = makeStarImage();
    return new ImageIcon(image);
  }

  private static Image makeStarImage() {
    final double starRad = D_SIZE * 0.4;
    Point2D[] points = {
        toRect(starRad, 0.0),
        toRect(starRad, 144.0),
        toRect(starRad, 288.0),
        toRect(starRad, 72.0),
        toRect(starRad, 216.0),
        toRect(starRad, 0.0)
    };
    double center = D_SIZE/2.0;
    double side = D_SIZE;
    double arc = D_SIZE*0.3;
    RoundRectangle2D border = new RoundRectangle2D.Double(0, 0, side, side, arc, arc);
    GeneralPath star = new GeneralPath();
    star.moveTo(points[0].getX(), points[0].getY());
    for (int i=1; i<points.length; ++i) {
      star.lineTo(points[i].getX(), points[i].getY());
    }
    Image image = new BufferedImage(I_SIZE, I_SIZE, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) image.getGraphics();
    g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
    );
    g2.setRenderingHint(
        RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY
    );
    g2.setRenderingHint(
        RenderingHints.KEY_STROKE_CONTROL,
        RenderingHints.VALUE_STROKE_NORMALIZE
    );
    g2.setColor(Color.WHITE);
    g2.fill(border);
    AffineTransform savedTransform = g2.getTransform();
    g2.translate(center, center);
    g2.setColor(Color.BLUE);
    g2.fill(star);
    g2.setTransform(savedTransform);
    g2.dispose();
    return image;
  }

  // Actual polar to rectangular conversion in a cartesian plane:
  // x = r cos(theta)
  // y = r sin(theta)
  @SuppressWarnings("SameParameterValue")
  private static Point2D toRect(double r, double thetaDeg) {
    double thetaRad = (thetaDeg * Math.PI) / 180.0;
    final double x = r * StrictMath.sin(thetaRad);
    final double y = -r * StrictMath.cos(thetaRad);
    return new Point2D.Double(x, y);
  }
}
