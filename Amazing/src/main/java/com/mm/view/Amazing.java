package com.mm.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.jetbrains.annotations.Nullable;

/**
 * To do:
 * X 1) Exclude negative spoke counts.
 * X 2) Make this an applet.
 * 3) Add about box with licence
 * 4) Make inner ring and wall width configurable
 * X 5) Try removing rotation.
 * 6) Be sure to specify screen menus on Mac Bundle
 * 7) Get fg and bg from current values, when launched from an existing Labyrinth 
 * 8) Add a Text Label to the top of the color chooser, with the specified foregroung
 * and background colors.
 * 9) Replace dialog with JOptionPane!
 * 10) Put both colors in the same color chooser, with radio buttons for FG and BG.
 * <p>
 * Note: A few places in the drawing code are marked "I don't know why." Here's why:
 * Sometimes I used to use Graphics2D.draw() and other times I call Graphics2D.fill().
 * Apparently, fill is offset by 0.5, 0.5 from draw, for aliasing reasons. I wish I
 * could figure out how to turn that off, but I can't, so I periodically cancel it out
 * by shifting the origin before drawing.
 * However, I have since replaced all draw() calls with fills, and shifted them by (0.5, 0.5).
 * This should fix cross-platform drawing problems. But some fill() calls don't get translated, and
 * I'm not sure why. It may have something to do with the fact that I sometimes create a
 * strokedShape when I fill.
 * <p>
 * Anyway, it didn't fix the platform-specific drawing problems, so I put in some platform-
 * specific code to fix it.
 */
@SuppressWarnings({"MagicNumber", "HardCodedStringLiteral", "MagicCharacter", "AccessingNonPublicFieldOfAnotherObject", "NumericCastThatLosesPrecision", "CloneableClassWithoutClone"})
public class Amazing /* extends JPanel */ {
	public static final boolean platformCorrection = System.getProperty("os.name").contains("Mac");
	private static Color foregroundColor = Color.BLUE.darker();
	private static Color backgroundColor = Color.LIGHT_GRAY.brighter().brighter();
	public static void main(String[] args) {
		askForSpokes();
	}
	
	private static void askForSpokes(Color fgColor, Color bgColor) {
		askForSpokes(new LabyrinthData(fgColor, bgColor));
	}

	private static void askForSpokes() {
//		System.out.printf("Java Version: %s%n", System.getProperty("java.version")); // NON-NLS
		LabyrinthData data = new LabyrinthData();
		askForSpokes(data);
	}
	
	private static void askForSpokes(LabyrinthData data) {
		DialogContent content = new DialogContent();
		int value = JOptionPane.showConfirmDialog(
						null, 
						content.makeDialogContent(data), 
						"New Labyrinth", 
						JOptionPane.OK_CANCEL_OPTION
		);
		if (value == JOptionPane.OK_OPTION) {
			data.mFgColor = content.colorChoice.mFgColor;
			data.mBgColor = content.colorChoice.mBgColor;
			data.mSpokes = content.getSpokes();
			if (data.mSpokes < 0) {
				for (int ii = 0; ii < 11; ++ii) {
					makeLabyrinth(ii, data.mFgColor, data.mBgColor);
				}
			} else {
				makeLabyrinth(data.mSpokes, data.mFgColor, data.mBgColor);
			}
		}
	}

	private static void makeLabyrinth(int pSpokes, Color penColor, Color bgColor) {
		// save colors for next time:
		foregroundColor = penColor;
		backgroundColor = bgColor;
		JFrame mf = new JFrame(pSpokes + " spokes");
		mf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Labyrinth labyrinth = new Labyrinth(pSpokes, penColor, bgColor);
		mf.add(labyrinth);
		addMenus(mf, labyrinth);
		int ht = (Toolkit.getDefaultToolkit().getScreenSize().height * 100) / 95;
		mf.setSize(ht, ht);
		mf.setLocationByPlatform(true);
		mf.setVisible(true);
	}

//	public Amazing() {
//		super(new BorderLayout());
//		Thread.dumpStack();
//		JButton btn = new JButton("New Labyrinth");
//		ActionListener al = e -> askForSpokes();
//		JPanel btnPanel = new JPanel(new FlowLayout());
//		btnPanel.add(btn);
//		btn.addActionListener(al);
//		add(btn, BorderLayout.PAGE_END);
//	}

	private static class DialogContent {
		private final JPanel content = new JPanel(new BorderLayout());
		private final JPanel labels = new JPanel(new GridLayout(0, 1));
		private final JPanel values = new JPanel(new GridLayout(0, 1));
		private ColorChoice colorChoice;
		private JTextField mSpokesField;
		private LabyrinthData mData;

		private int getSpokes() {
			if (mSpokesField.getText().trim().isEmpty()) {
				mData.mSpokes = -1;
			} else {
				mData.mSpokes = Integer.parseInt(mSpokesField.getText());
			}
			return mData.mSpokes;
		}

		private JComponent makeDialogContent(final LabyrinthData pData) {
			mData = pData;
			content.add(BorderLayout.LINE_START, labels);
			content.add(BorderLayout.CENTER, values);

			mSpokesField = new JTextField(String.valueOf(pData.mSpokes), 10);
			installFilter(mSpokesField, new NumberFilter());
			labels.add(new JLabel("Number of Spokes: "));
			values.add(mSpokesField);

			colorChoice = new ColorChoice(pData);
			content.add(colorChoice, BorderLayout.PAGE_END);

			return content;
		}
	}
		
	private static final class LabyrinthData {
		private Color mBgColor = backgroundColor;
		private Color mFgColor = foregroundColor;
		private int mSpokes = 7;

		private LabyrinthData() { }
		private LabyrinthData(Color fg, Color bg) {
			mFgColor = fg;
			mBgColor = bg;
		}
	}
		
	@SuppressWarnings("PublicField")
	private static final class Labyrinth extends JPanel implements Printable {
		private double mKk;
		private double mMaxRadius;
		private double mGap;
		private static final double PI = Math.PI;
		public static final double toDeg = 180.0 / PI;
		public static final double toRad = PI / 180.0;
		private double mWallWidth;
		private double mHalfWidth;
		public Color fillColor;
		@Nullable
		private PageFormat mPageFormat;

		private double innerSize = 0.2;
		private final int mRings;
		private final int spokes;
		private static final double spaceToStrokeRatio = 10.0;

		private Labyrinth(int pSpokes, Color penColor, Color bgColor) {
			super(new BorderLayout());
			spokes = pSpokes;
			setForeground(penColor);
			setBackground(bgColor);
			mRings = (2 * spokes) + 4;
			if (spokes < 4) {
				innerSize *= 1.5;
			}
			if (spokes < 1) {
				innerSize = 0.35;
			}
			fillColor = getForeground();
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g); // fills background
			Graphics2D gg = (Graphics2D) g;
			setHints(gg);
			gg.setColor(getForeground());
			Dimension size = calculateSizeParameters(gg);
			gg.translate(size.width / 2.0, size.height / 2.0);
			for (int ii = 0; ii < mRings; ++ii) {
				double radius = radiusForRing(ii);
				drawCircle(gg, radius);
			}
			double deltaTheta = (2 * PI) / spokes;
			int start = 2 - ((spokes - 1) % 3);
			for (int spoke = 1; spoke < spokes; ++spoke) {
				start++;
				if (start > 3) {
					start = 1;
				}
				double theta = (deltaTheta * spoke) + PI;
				int ii = start;
				while (ii < (mRings - 1)) {
					int ring = mRings - 1 - ii;
					drawAdv(gg, ring, theta, RingSide.both, Direction.both);
					ii += 3;
				}
			}

			Rectangle2D clip = new Rectangle2D.Double(-mGap + mHalfWidth, 0, (2 * (mGap)) - mWallWidth, mMaxRadius + mGap);
			drawChannel(gg, clip);
		}

		/**
		 * draws the entrance and exit.
		 *
		 * @param gg      The full Graphics
		 * @param startPath The clipping rectangle
		 */
		@SuppressWarnings("ReuseOfLocalVariable")
		private void drawChannel(Graphics2D gg, Rectangle2D startPath) {
			Point2D outer = polarToGrid(radiusForRing(0), PI);
			Point2D inner = polarToGrid(radiusForRing(mRings - 1), PI);

			drawEntranceSwitchbacks(gg);
			int ring = mRings - 2;
			drawExitSwitchbacks(ring, gg);
			// clear the entry and final channels
			gg.setColor(getBackground());
			gg.fill(startPath);
			gg.setColor(getForeground());

			drawEntranceChannel(gg);
			ring = mRings - 2;
			drawAdv(gg, ring, PI, RingSide.inner, Direction.retreating);

			drawExitChannel(ring, gg);
			ring = 1;
			// draw last pie on outermost ring
			drawAdv(gg, ring, PI, RingSide.outer, Direction.advancing);

			// draw the line separating the entry channel from the final channel
			Line2D line = new Line2D.Double(outer, inner);
			gg.setColor(getForeground());
			drawShape(gg, line);
			
			// Remove this. For testing only.
//			Arc2D.Double arc = new Arc2D.Double(-mMaxRadius, -mMaxRadius, 2*mMaxRadius, 2*mMaxRadius, 0, 45, Arc2D.OPEN);
//			paintShape(gg, arc);
//			paintShape(gg, arc);
//			paintShape(gg, arc);
//			paintShape(gg, arc);
//			double rx = radiusForRing(-1);
//			Arc2D.Double arc2 = new Arc2D.Double(-rx, -rx, 2*rx, 2*rx, 0, 90, Arc2D.OPEN);
//			paintShape(gg, arc2);
		}

//		BufferedImage offScrImage;
//		Graphics2D offScrGg;
//		
//		private void makeOSGraphics() {
//			if (offScrImage == null) {
//				Dimension size = getSize();
//				offScrImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
//				offScrGg = offScrImage.createGraphics();
//				setHints(offScrGg);
//				offScrGg.translate(size.width / 2.0, size.height / 2.0);
//			}
//		}
//		/**
//		 * This was an attempt to draw over existing lines without making them stronger. My thinking
//		 * was that I could draw against a transparent background, then copy the drawing to the original
//		 * without affecting the anti-aliased pixels in the original. It didn't work, however, I noticed
//		 * something interesting. When I drew to the off screen image, it produced a curve that was both
//		 * fuzzier and less aliased. I don't know why.
//		 * I'm removing this code for now, but I'm keeping it commented out so I can experiment with it 
//		 * more later.
//		 * @param gg The Graphics
//		 * @param pShape The shape to draw.
//		 */
//		@SuppressWarnings({"UnusedDeclaration"})
//		private void paintShape(Graphics2D gg, Shape pShape) {
//			makeOSGraphics();
//			Dimension size = getSize();
//			Color alpha = new Color(255, 255, 255, 0);
//			offScrGg.setColor(alpha);
//			offScrGg.fillRect(-size.width/2, -size.height/2, size.width, size.height);
//			offScrGg.setColor(gg.getColor());
//			drawShape(offScrGg, pShape);
//			gg.drawImage(offScrImage, -size.width/2, -size.height/2, this);
//		}
//
		@SuppressWarnings({"ReuseOfLocalVariable", "AssignmentToMethodParameter"})
		private void drawExitSwitchbacks(int pRing, Graphics2D gg) {
			while (pRing > 0) {
				double sinDeltaThetaInner = mGap / radiusForRing(pRing + 1);
				double theta = PI - StrictMath.asin(sinDeltaThetaInner);
				double deltaThetaInner = StrictMath.asin(sinDeltaThetaInner) / 2;
//			fillColor = Color.ORANGE;
				fillBack(gg, pRing + 1, pRing - 1, theta + (2 * deltaThetaInner), theta - (deltaThetaInner / 3));
				double radius = radiusForRing(pRing);
				theta = StrictMath.asin(-mGap / radius) + PI;
				double shift = 2 * (1.0 - StrictMath.cos(PI - theta));
				drawAdv(gg, pRing, theta - shift, RingSide.both, Direction.advancing);
				pRing -= 2;
				if (pRing > 0) {
					radius = radiusForRing(pRing);
					sinDeltaThetaInner = mGap / radiusForRing(pRing + 1);
					theta = PI - StrictMath.asin(sinDeltaThetaInner);
					fillBack(gg, pRing + 1, pRing - 1, theta + deltaThetaInner, theta - (deltaThetaInner / 3));
					theta = StrictMath.asin(-mGap / radius) + PI;
					shift = (1.0 - StrictMath.cos(PI - theta));
					drawAdv(gg, pRing, theta - shift, RingSide.both, Direction.advancing);
				}
				pRing -= 4;
			}
		}

		@SuppressWarnings("AssignmentToMethodParameter")
		private void drawExitChannel(int pRing, Graphics2D gg) {
			while (pRing > 0) {
				pRing -= 3;
				if (pRing > 0) {
					drawAdv(gg, pRing, PI, RingSide.outer, Direction.advancing);
					pRing -= 2;
				}
				if (pRing > 0) {
					drawAdv(gg, pRing, PI, RingSide.inner, Direction.advancing);
					drawCleanupRightArc(pRing, gg);
					pRing -= 1;
				}
			}
			
			// Draw the outermost cleanup arc.
			drawCleanupRightArc(-1, gg);
		}

		@SuppressWarnings("ReuseOfLocalVariable")
		private void drawEntranceSwitchbacks(Graphics2D gg) {
			int ring = 1;
			while (ring < (mRings - 1)) {
				double innerRadius = radiusForRing(ring - 1);
				double sinDeltaThetaInner = mGap / innerRadius;
				double deltaThetaInner = StrictMath.asin(sinDeltaThetaInner);
				double theta = deltaThetaInner + PI;
				// grouts the outer entrance-channel wall.
//				fillColor = Color.GREEN;
				fillBack(gg, ring + 1, ring - 1, theta - (deltaThetaInner / 2), theta + (deltaThetaInner / 2));
				fillColor = getForeground();
				// shift keeps the circles from protruding into the entrance and exit channels.
				double deltaTheta = mGap / radiusForRing(ring);
				theta = StrictMath.asin(deltaTheta) + PI;
				double shift = (1.0 - StrictMath.cos(PI - theta));
				drawAdv(gg, ring, theta + shift, RingSide.both, Direction.retreating);
				ring += 2;
				if (ring < (mRings - 1)) {
					sinDeltaThetaInner = mGap / radiusForRing(ring - 1);
					theta = StrictMath.asin(sinDeltaThetaInner) + PI;
//					fillColor = Color.GREEN;
					fillBack(gg, ring + 1, ring - 1, theta - (deltaThetaInner * 2), theta + deltaThetaInner);
					fillColor = getForeground();
					theta = StrictMath.asin(mGap / radiusForRing(ring)) + PI;
					double innerTheta = StrictMath.asin(mGap / radiusForRing(ring + 1)) + PI;
					shift = (1.0 - StrictMath.cos(PI - innerTheta));
					drawAdv(gg, ring, theta + shift, RingSide.both, Direction.retreating);
				}
				ring += 4;
			}
		}

		private void drawEntranceChannel(Graphics2D gg) {
			int ring = 1;
			while (ring < (mRings - 1)) {
				ring += 3;
				if (ring < (mRings - 1)) {
					drawAdv(gg, ring, PI, RingSide.inner, Direction.retreating);
					drawCleanupArc(ring, gg);
					ring += 2;
				}
				if (ring < (mRings - 1)) {
					drawAdv(gg, ring, PI, RingSide.outer, Direction.retreating);
					ring += 1;
				}
			}
			
			// At this point, if ring is divisible by 3, the innermost cleanup arc has already
			// been drawn, so we don't need to do it.
			if ((ring % 3) != 0){
				drawCleanupArc(mRings-2, gg);// Draws the innermost cleanup arc.
			}
		}

		private void drawShape(Graphics2D gg, Shape pShape) {
			Shape cShape = gg.getStroke().createStrokedShape(pShape);
			gg.fill(cShape); // Fills a stroked shape
		}

		private void drawCleanupArc(int pRing, Graphics2D gg) {
			drawCleanupArc(pRing, gg, false);
		}

		private void drawCleanupArc(int pRing, Graphics2D gg, boolean rightSide) {
			double rad = radiusForRing(pRing + 1);
			double arcLength = StrictMath.asin((spaceToStrokeRatio * mWallWidth) / rad)*toDeg;
			double epsilonLength = StrictMath.asin(mHalfWidth/rad)*toDeg;
			double start = rightSide ? (270 + epsilonLength) : (270 - arcLength - epsilonLength);
			Arc2D arc = new Arc2D.Double(-rad, -rad, 2 * rad, 2 * rad, start, arcLength, Arc2D.OPEN);
			gg.setColor(getForeground());
			drawShape(gg, arc);
		}

		private void drawCleanupRightArc(int pRing, Graphics2D gg) {
			drawCleanupArc(pRing, gg, true);
		}

		private void fillBack(Graphics2D gg, int innerRing, int outerRing, double th1, double th2) {
			fillBack(gg, innerRing, th1, th2, outerRing, th2, th1);
		}

		private void fillBack(Graphics2D gg, int innerRing, double th1, double th2, int outerRing, double th3, double th4) {
			if ((innerRing >= 0) && (outerRing < mRings)) {
				int in = Math.max(innerRing, outerRing);
				int out = Math.min(innerRing, outerRing);
				PolarPoint p1 = new PolarPoint(in, th1).shiftIn();
				PolarPoint p2 = new PolarPoint(in, th2).shiftIn();
				PolarPoint p3 = new PolarPoint(out, th3).shiftOut();
				PolarPoint p4 = new PolarPoint(out, th4).shiftOut();
				fillBack(gg, p1, p2, p3, p4);
			}
		}

		// assumptions made by this method are enforced by the previous method.
		@SuppressWarnings("ReuseOfLocalVariable")
		private void fillBack(Graphics2D gg, PolarPoint p1, PolarPoint p2, PolarPoint p3, PolarPoint p4) {
			GeneralPath path = new GeneralPath();
			path.moveTo(p1.getXf(), p1.getYf());
			double sweep = p2.mTheta - p1.mTheta;
			double controlRadius = p1.mRadius / StrictMath.cos(sweep / 2);
			PolarPoint controlPoint = new PolarPoint(controlRadius, (p1.mTheta + p2.mTheta) / 2);
			path.quadTo(controlPoint.getXf(), controlPoint.getYf(), p2.getXf(), p2.getYf());
			path.lineTo(p3.getXf(), p3.getYf());
			sweep = p4.mTheta - p3.mTheta;
			controlRadius = p3.mRadius / StrictMath.cos(sweep / 2);
			controlPoint = new PolarPoint(controlRadius, (p3.mTheta + p4.mTheta) / 2);
			path.quadTo(controlPoint.getXf(), controlPoint.getYf(), p4.getXf(), p4.getYf());
			path.closePath();
			Area area = new Area(path);
			gg.setColor(fillColor);
			gg.fill(area);
		}

		/**
		 * Draw advancing or retreating turnaround inside the specified circle. Advancing and retreating
		 * are defined as clockwise and counterclockwise motion.
		 * <p>
		 * I used to do this by rotating the Graphics2D object to put the Arc2Ds on the vertical center
		 * line, but I got bad results with clipping. So now I rotate each point by creating a PolarPoint,
		 * and do the whole thing without any rotation transforms. It works better this way, even though
		 * some of the rotated objects got clipped after they were drawn. So I don't entirely understand
		 * why it helps to remove the rotations.
		 * </p><p>
		 * @param gg    The graphics object
		 * @param ring  The ring number. The outer ring is zero.
		 * @param theta The angle, measured from a vertical line extending from the center to the top.
		 * @param where draw the Inner or Outer turnaround, or both
		 * @param dir   Direction Draw Advancing (clockwise), retreating (counter clockwise), or both.
		 */
		private void drawAdv(Graphics2D gg, int ring, double theta, RingSide where, Direction dir) {
			double padding = (where == RingSide.both) ? 0 : 45;

			// Fill behind the turnaround with black
			double gap = 0.0;
			if (where.isInner()) {
				gap = fillInnerArc(ring, gg, true, dir, theta);
			}
			if (where.isOuter()) {
				gap = fillInnerArc(ring - 1, gg, false, dir, theta);
			}
			double radius = radiusForRing(ring);
			double tangentAngle = StrictMath.asin(gap / radius / 2.0) * 2.0;
			double modGap = gap - mHalfWidth; // modified gap
			gg.setColor(getBackground());
			if (dir.isAdvancing()) {
				double rotation = theta - tangentAngle;
				double rotDeg = rotation * toDeg;
				double extra = padding * Math.signum(where.sweep());
				double extra2 = 2 * extra;
				Point2D center = new PolarPoint(radius, rotation).getCartPoint();
				Arc2D circle = new Arc2D.Double(center.getX() - modGap, center.getY() - modGap, 2 * modGap, 2 * modGap, where.startAngle() - extra - rotDeg, where.sweep() + extra2, Arc2D.PIE);
				fillShape(gg, circle);
			}
			if (dir.isRetreating()) {
				double rotation = theta + tangentAngle;
				double rotDeg = rotation * toDeg;
				double shift = -90.0;
				if (where == RingSide.outer) {
					shift += 180;
				}
				double extra = padding * Math.signum(where.sweep());
				double extra2 = 2 * extra;
				Point2D center = new PolarPoint(radius, rotation).getCartPoint();
				Arc2D circle = new Arc2D.Double(center.getX() - modGap, center.getY() - modGap, 2 * modGap, 2 * modGap, (shift - where.startAngle() - extra) - rotDeg, where.sweep() + extra2, Arc2D.PIE);
				fillShape(gg, circle);
			}
		}

		/**
		 * For some reason, shapes that are filled draw in the wrong place on the Mac. I don't know why, but
		 * I translate the graphics by half a pixel in each direction to account for this. I only do this on the Mac.
		 * 
		 * @param gg The Graphics2D
		 * @param pShape The shape to draw.
		 */
		private static void fillShape(Graphics2D gg, Arc2D pShape) {
			if (platformCorrection) {
				AffineTransform saved = gg.getTransform();
//				gg.translate(-0.5, -0.5);
				gg.fill(pShape); // shifted for workaround
				gg.setTransform(saved);
			} else {
				gg.fill(pShape);
			}
		}

		private double fillInnerArc(int ring, Graphics2D gg, boolean inner, Direction dir, double theta) {

			double radius = radiusForRing(ring);
			double innerRadius = radiusForRing(ring + 1);
			double gap = radius - innerRadius;
			// sweep is the angle over which we draw the arc.
			double sweep = -gap / (inner ? radius : innerRadius);
			double start;
			double end;

			switch (dir) {
				case advancing:
					start = 0.0;
					end = sweep;
					break;
				case retreating:
					start = -sweep;
					end = 0.0;
					break;
				case both:
					start = -sweep;
					end = sweep;
					sweep *= 2;
					break;
				default:
					throw new AssertionError("Not all cases handled");
			}

			PolarPoint loc = new PolarPoint(radius, start + theta);

			GeneralPath path = new GeneralPath();
			path.moveTo(loc.getXf(), loc.getYf());

			double controlTheta = (start + end) / 2;
			quadArc(radius, sweep, controlTheta + theta, path, Direction.advancing);

			PolarPoint innerCorner = new PolarPoint(innerRadius, end + theta);
			path.lineTo(innerCorner.getXf(), innerCorner.getYf());

			quadArc(innerRadius, sweep, controlTheta + theta, path, Direction.retreating);

			path.closePath();
			Area area = new Area(path);

			// These are properly drawn when they're not shifted, but in this case it doesn't matter if 
			// they get shifted. They don't mess up the drawing.
			gg.setColor(getForeground());
			gg.fill(area);
			return gap;
		}

		/**
		 * add a quad point to the path, to follow the perimeter of a circle.
		 *
		 * @param pRadius       The radius
		 * @param pSweep        Angle of the arc
		 * @param pControlTheta center of the arc
		 * @param pPath         The shape's path
		 * @param dir           The Direction
		 */
		private void quadArc(double pRadius, double pSweep, double pControlTheta, GeneralPath pPath, Direction dir) {
			int back = (dir == Direction.advancing) ? 1 : -1;
			PolarPoint end = new PolarPoint(pRadius, pControlTheta + ((back * pSweep) / 2));
			double controlR = pRadius / StrictMath.cos(pSweep / 2);
			PolarPoint outerControl = new PolarPoint(controlR, pControlTheta);
			pPath.quadTo(outerControl.getXf(), outerControl.getYf(), end.getXf(), end.getYf());
		}

		Point2D.Double polarToGrid(double radius, double theta) {
			double xx = radius * StrictMath.sin(theta);
			double yy = -radius * StrictMath.cos(theta);
			return new Point2D.Double(xx, yy);
		}

		private Dimension calculateSizeParameters(Graphics2D gg) {
			Dimension size;
			if (mPageFormat != null) {
				//noinspection NumericCastThatLosesPrecision
				size = new Dimension((int) mPageFormat.getImageableWidth(), (int) mPageFormat.getImageableHeight());
			} else {
				size = getSize();
			}
			int diameterLimit = Math.min(size.width, size.height);
			mMaxRadius = (diameterLimit * 0.9) / 2.0;
			mKk = (innerSize - 1.0) / (mRings - 1.0);
			mGap = -mKk * mMaxRadius;

			double slope = mKk * mMaxRadius;
			mWallWidth = -slope / spaceToStrokeRatio;
			mHalfWidth = mWallWidth / 2;
			//noinspection NumericCastThatLosesPrecision
			gg.setStroke(new BasicStroke((float) mWallWidth));
			return size;
		}

		private double radiusForRing(int ring) {return mMaxRadius * ((mKk * (ring)) + 1.0);}

		private void setHints(Graphics2D g) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
//			showHint(g, RenderingHints.KEY_ALPHA_INTERPOLATION);
//			showHint(g, RenderingHints.KEY_ANTIALIASING);
//			showHint(g, RenderingHints.KEY_COLOR_RENDERING);
//			showHint(g, RenderingHints.KEY_DITHERING);
//			showHint(g, RenderingHints.KEY_FRACTIONALMETRICS);
//			showHint(g, RenderingHints.KEY_INTERPOLATION);
//			showHint(g, RenderingHints.KEY_RENDERING);
//			showHint(g, RenderingHints.KEY_STROKE_CONTROL);
//			showHint(g, RenderingHints.KEY_TEXT_ANTIALIASING);
////			showHint(g, RenderingHints.KEY_TEXT_LCD_CONTRAST);
		}
		
		@SuppressWarnings("UnusedDeclaration")
		private void showHint(Graphics2D g, RenderingHints.Key hint) {
//			//noinspection StringConcatenation
//			System.out.println(hint + " = " + g.getRenderingHint(hint));
		}

		private void drawCircle(Graphics2D gg, double radius) {
			double diameter = 2.0 * radius;
			Shape circle = new Ellipse2D.Double(-radius, -radius, diameter, diameter);
			drawShape(gg, circle);
		}

		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
			if (pageIndex > 0) {
				return Printable.NO_SUCH_PAGE;
			}
			graphics.translate((int) pageFormat.getImageableX(),
							(int) pageFormat.getImageableY());
			mPageFormat = pageFormat;
			print(graphics);
			mPageFormat = null;
			graphics.dispose();
			return Printable.PAGE_EXISTS;
		}

		private final class PolarPoint {
			private final double mTheta;
			private final double mRadius;
			private Point2D mCartPoint;

			private PolarPoint(double radius, double theta) {
				mTheta = theta;
				mRadius = radius;
			}

			private PolarPoint(int ring, double theta) {
				mRadius = radiusForRing(ring);
				mTheta = theta;
			}

			private void makeCartPoint() {
				if (mCartPoint == null) {
					mCartPoint = polarToGrid(mRadius, mTheta);
				}
			}

			public Point2D getCartPoint() {
				makeCartPoint();
				return mCartPoint;
			}

			public double getX() {
				makeCartPoint();
				return mCartPoint.getX();
			}

			public double getY() {
				makeCartPoint();
				return mCartPoint.getY();
			}

			public PolarPoint shiftIn() { return new PolarPoint(mRadius - mHalfWidth, mTheta); }

			public PolarPoint shiftOut() { return new PolarPoint(mRadius + mHalfWidth, mTheta); }

			public float getXf() { return (float) getX(); }

			public float getYf() { return (float) getY(); }

			@Override
			public String toString() {
				//noinspection StringConcatenation,MagicCharacter
				return "r=" + mRadius + " \u03b8=" + mTheta + " (" + getXf() + ", " + getYf() + ')'; // NON-NLS
			}
		}
	}

	private static void addMenus(final JFrame frame, final Labyrinth labyrinth) {
		Action newLabyrinth = new AbstractAction("New Labyrinth") {
			public void actionPerformed(ActionEvent e) {
				askForSpokes(labyrinth.getForeground(), labyrinth.getBackground());
			}
		};
		newLabyrinth.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putMnemonicValue(newLabyrinth, 'N');
		
		JMenu fileMenu = new JMenu("File");
		if (!platformCorrection) {
			fileMenu.setMnemonic('F');
		}
		fileMenu.add(newLabyrinth);
		Action closeAction = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};
		closeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putMnemonicValue(closeAction, 'C');
		fileMenu.add(closeAction);
		Action printLabyrinth = makePrintAction(labyrinth);
		fileMenu.add(printLabyrinth);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
	}
	
	private static void putMnemonicValue(Action action, int key) {
		// Don't add the mnemonic on the Macintosh
		if (!platformCorrection) {
			action.putValue(Action.MNEMONIC_KEY, key);
		}
	}

	private static Action makePrintAction(final Labyrinth labyrinth) {
		Action printAction = new AbstractAction("Print") {
			public void actionPerformed(ActionEvent e) {
				PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setPrintable(labyrinth);
				PageFormat pageFormat = pj.defaultPage();
				PageFormat pf = pj.pageDialog(pageFormat);

				// If the user didn't hit cancel...

				//noinspection ObjectEquality
				if (pf != pageFormat) {
					pj.validatePage(pf);
					if (pj.printDialog()) {
						try {
							pj.print();
						} catch (PrinterException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
		printAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putMnemonicValue(printAction, 'P');
		return printAction;
	}
	
	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private static void installFilter(JTextComponent comp, DocumentFilter filter) {
		((PlainDocument)comp.getDocument()).setDocumentFilter(filter);
		
	}
	
	private static class NumberFilter extends DocumentFilter {
		public boolean allowed(char ch) {
			return (Character.isDigit(ch));
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			super.insertString(fb, offset, process(string), attr);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, process(text), attrs);
		}

		private String process(String string) {
			StringBuilder bldr = new StringBuilder(string.length());
			for (int ii=0; ii<string.length(); ++ii) {
				char cc = string.charAt(ii);
				if (allowed(cc)) {
					bldr.append(cc);
				}
			}
			return bldr.toString();
		}
	}
	
	private static final class ColorChoice extends JPanel {
		private static final int GAP = 8;
		private Color mFgColor;
		private Color mBgColor;
		private final JButton mButton;
		private final JLabel label = new JLabel(" (Uses these colors) ");
		private final LabyrinthData mData;
		private boolean mForeground;
		private static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED);

		private ColorChoice(LabyrinthData data) {
			super(new GridLayout(1, 0));
			setBorder(new MatteBorder(GAP, GAP, GAP, GAP, getBackground()));
			mForeground = true;
			mButton = new JButton("Choose Colors...");
			mFgColor = data.mFgColor;
			mBgColor = data.mBgColor;
			mData = data;
			add(mButton);
			add(label);
			label.setOpaque(true);
			label.setForeground(mFgColor);
			label.setBackground(mBgColor);
			label.setBorder(BEVEL_BORDER);
			ActionListener al = e -> showColorDialog(String.format("Choose the %s Color", mButton.getText()));
			mButton.addActionListener(al);
		}
		
		private void showColorDialog(String title) {
			final JLabel banner = new JLabel("Current Colors");
			banner.setFont(new Font("SansSerif", Font.BOLD, 24));
			banner.setForeground(mData.mFgColor);
			banner.setBackground(mData.mBgColor);
			banner.setOpaque(true);
			banner.setBorder(BEVEL_BORDER);
			banner.setHorizontalAlignment(JLabel.CENTER);
			
			final JColorChooser chooser = new JColorChooser(mFgColor);
			ChangeListener colorLsnr = e -> {
				Color clr = chooser.getColor();
				if (mForeground) {
					banner.setForeground(clr);
				} else {
					banner.setBackground(clr);
				}
			};
			chooser.getSelectionModel().addChangeListener(colorLsnr);

			JPanel topPanel = makeTopPanel(banner, chooser);

			JPanel chooserPanel = new JPanel(new BorderLayout());
			chooserPanel.add(chooser, BorderLayout.CENTER);
			chooserPanel.add(topPanel, BorderLayout.PAGE_START);
			int value = JOptionPane.showConfirmDialog(this, chooserPanel, title, JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.OK_OPTION) {
				mFgColor = banner.getForeground();
				mBgColor = banner.getBackground();
				label.setForeground(mFgColor);
				label.setBackground(mBgColor);
				saveChosenColors();
			}
		}
		
		private JPanel makeTopPanel(final JLabel banner, final JColorChooser chooser) {
			ButtonGroup choices = new ButtonGroup();
			final JRadioButton fgChoice = new JRadioButton("Foreground");
			final JRadioButton bgChoice = new JRadioButton("Background");
			choices.add(fgChoice);
			choices.add(bgChoice);
			fgChoice.setSelected(true);
			
			ActionListener clickListener = e -> {
				//noinspection ObjectEquality
				mForeground = (e.getSource() == fgChoice);
				Color clr = mForeground? banner.getForeground() : banner.getBackground();
				chooser.setColor(clr);
			};
			fgChoice.addActionListener(clickListener);
			bgChoice.addActionListener(clickListener);

			JPanel choicePanel = new JPanel(new GridLayout(0, 1));
			choicePanel.add(fgChoice);
			choicePanel.add(bgChoice);
			
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(banner, BorderLayout.PAGE_START);
			topPanel.add(choicePanel, BorderLayout.CENTER);
			
			return topPanel;
		}

		private void saveChosenColors() {
			mData.mFgColor = mFgColor;
			mData.mBgColor = mBgColor;
		}
	}
	
	private enum RingSide {
		inner(1),
		outer(2),
		both(3);

		private final int mWhich;

		RingSide(int which) {
			mWhich = which;
		}

		public boolean isInner() { return (mWhich & 1) != 0; }

		public boolean isOuter() { return (mWhich & 2) != 0; }

		public double startAngle() {
			if (mWhich == 3) {
				return -135.0;
			}
			return 0.0;
		}

		public double sweep() {
			if (mWhich == 3) {
				return 270.0;
			}
			if (isInner()) {
				return -90.0;
			}
			return 90.0;
		}
	}

	private enum Direction {
		advancing,
		retreating,
		both;

		public boolean isAdvancing() { return this != retreating; }

		public boolean isRetreating() { return this != advancing; }

		public Direction multiply(int signum) {
			if (signum < 0) {
				return (this == retreating) ? advancing : retreating;
			}
			return this;
		}
	}

}
