package com.mm.gui.scale;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Formatter;

/**
 * Created by IntelliJ IDEA. <b>
 * User: mmunoz <b>
 * Date: Jul 13, 2005 <b>
 * Time: 8:05:15 PM <b>
 * <p>
 * And absolute point, which
 */
public class ScaledPoint {
	Point2D mAbsolutePoint;
	Scale  mScale;

	/**
	 * Creates a ScaledPoint form a pair of absolute coordinates and a Scale.
	 * To make a ScaledPoint from scaled coordinates, use a factory method.
	 * @param x         The X value in absolute coordinates
	 * @param y         The y value in absolute coordinates
	 * @param pScale    The Scale.
	 */
	public ScaledPoint(double x, double y, Scale pScale)
	{
		mScale = pScale;
		mAbsolutePoint = new Point2D.Double(x, y);
	}

	/**
	 * Creates a ScaledPoint form an absolute point and a Scale.
	 * To make a ScaledPoint from scaled corrdintates, use a factory method.
	 * @param pt        The point, in absolute coordinates.
	 * @param pScale    The Scale.
	 */
	public ScaledPoint(Point2D pt, Scale pScale)
	{
		mScale = pScale;
		mAbsolutePoint = (Point2D) pt.clone();
	}

  /**
   * Construct a ScaledPoint from a Scale and a MouseEvent. MouseEvents always use scaled coordinates, so this
   * constructor unscales them.
   * @param pScale The scale
   * @param evt    the MouseEvent that supplies the scaled point.
   */
  public ScaledPoint(Scale pScale, MouseEvent evt)
  {
    this(evt.getX()/pScale.getScale(), evt.getY()/pScale.getScale(), pScale);
  }

	public Point2D getAbsolutePoint2D() {
		return (Point2D)mAbsolutePoint.clone();
	}

	public Point2D getScaledPoint2D() {
		return getScaledPoint2D(mScale);
	}

  public Point2D getScaledPoint() {
    return getScaledPoint(mScale);
  }

	/**
	 * Gets a point, scaled to some arbitrary scale, rather than the internal one.
	 * @param   pScale The scale with which to calculate the point.
	 * @return  A Point2D, scaled to the specified scale.
	 */
	private Point2D getScaledPoint2D(Scale pScale)
	{
		double scale = pScale.getScale();
		return new Point2D.Double(mAbsolutePoint.getX()*scale,
		        mAbsolutePoint.getY()*scale);
	}

  private Point getScaledPoint(Scale pScale)
  {
    double scale = pScale.getScale();
    return new Point((int)Math.round(mAbsolutePoint.getX()*scale), (int)Math.round(mAbsolutePoint.getY()*scale));
  }

	public void setAbsolutePoint(Point2D pt)
	{
		mAbsolutePoint = (Point2D)pt.clone();
	}

	public void setScaledPoint(Point2D scPt)
	{
		setScaledPoint(scPt, mScale);
	}

	/**
	 * Should we get rid of this one because it's not clear from the API if it
//	 * sets the scale to the specified scale?
	 * @param scPt
	 * @param pScale
	 */
	public void setScaledPoint(Point2D scPt, Scale pScale)
	{
		double scale = pScale.getScale();
		mAbsolutePoint = new Point2D.Double(
		        scPt.getX()/scale,
		        scPt.getY()/scale
		);
		mScale = pScale;
	}

	public Scale getScale() { return mScale; }

	public static ScaledPoint makePointFromScale(double scaledX, double scaledY, Scale pScale)
	{
		double sc = pScale.getScale();
		return new ScaledPoint(scaledX/sc, scaledY/sc, pScale);
	}

  public String toString()
  {
    StringBuilder bldr = new StringBuilder();
    Formatter out = new Formatter(bldr);
    out.format("[%1$4.2f, %2$4.2f] * %3$4.2f = [%4$4.2f, %5$4.2f]",
      mAbsolutePoint.getX(),
      mAbsolutePoint.getY(),
      mScale.getScale(),
      mAbsolutePoint.getX()*mScale.getScale(),
      mAbsolutePoint.getY()*mScale.getScale()
      );
    return bldr.toString();
  }
}
