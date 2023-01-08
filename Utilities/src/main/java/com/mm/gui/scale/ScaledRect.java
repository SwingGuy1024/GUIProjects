package com.mm.gui.scale;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA. <br>
 * User: mmunoz <br>
 * Date: Jul 13, 2005 <br>
 * Time: 8:05:15 PM <br>
 * <p>
 * And absolute rectangle, which
 */
public class ScaledRect {
	Rectangle2D mAbsoluteRect;
	Scale  mScale;

	/**
	 * Creates a ScaledRect form a pair of absolute coordinates and a Scale.
	 * To make a ScaledRect from scaled corrdintates, use a factory method.
	 * @param absoluteMinX The X value in absolute coordinates
	 * @param absoluteMinY The absoluteY value in absolute coordinates
	 * @param pScale    The Scale.
	 */
	public ScaledRect(double absoluteMinX, double absoluteMinY,
	                  double absoluteWidth, double absoluteHeight, Scale pScale)
	{
		mScale = pScale;
		mAbsoluteRect = new Rectangle2D.Double(absoluteMinX, absoluteMinY,
		        absoluteWidth, absoluteHeight);
	}

    /**
     * Construct a ScaledRect from two opposing corners.
     * @param pScale   The Scaler
     * @param cornerA  A MouseEvent containing one corner. (Coordinates are assumed to be scaled.)
     * @param cornerB  A MouseEvent containing the opposite corner. (Coordinates are assumed to be scaled.)
     */
    public ScaledRect(Scale pScale, MouseEvent cornerA, MouseEvent cornerB)
    {
        mScale = pScale;
        int left;
        int right;
        int top;
        int bottom;
        if (cornerA.getPoint().x < cornerB.getPoint().x)
        {
            left = cornerA.getPoint().x;
            right = cornerB.getPoint().x;
        }
        else
        {
            left = cornerB.getPoint().x;
            right = cornerA.getPoint().x;
        }
        if (cornerA.getPoint().y < cornerB.getPoint().y)
        {
            top = cornerA.getPoint().y;
            bottom = cornerB.getPoint().y;
        }
        else
        {
            top = cornerB.getPoint().y;
            bottom = cornerA.getPoint().y;
        }
        double scale = pScale.getScale();
        mAbsoluteRect = new Rectangle2D.Double(left/scale, top/scale, (right-left)/scale, (bottom-top)/scale);
    }

	/**
	 * Creates a ScaledRect form an absolute rectangle and a Scale.
	 * To make a ScaledRect from scaled corrdintates, use a factory method.
	 * @param rect      The rectangle, in absolute coordinates.
	 * @param pScale    The Scale.
	 */
	public ScaledRect(Rectangle2D rect, Scale pScale)
	{
		mScale = pScale;
		mAbsoluteRect = (Rectangle2D) rect.clone();
	}

	public Rectangle2D getAbsoluteRect2D() {
		return (Rectangle2D)mAbsoluteRect.clone();
	}

	public Rectangle2D getScaledRect2D() {
		return getScaledRect2D(mScale);
	}

  public Rectangle getScaledRect() {
    double scale = mScale.getScale();
    return new Rectangle(
      (int)Math.round(mAbsoluteRect.getMinX()*scale),
      (int)Math.round(mAbsoluteRect.getMinY()*scale),
      (int)Math.round(mAbsoluteRect.getWidth()*scale),
      (int)Math.round(mAbsoluteRect.getHeight()*scale)
    );
  }

	/**
	 * Gets a rectangle, scaled to some arbitrary scale, rather than the internal
	 * one.
	 * @param   pScale The scale with which to calculate the rectangle.
	 * @return  A Rectangle2D, scaled to the specified scale.
	 */
	public Rectangle2D getScaledRect2D(Scale pScale)
	{
		double scale = pScale.getScale();
		return new Rectangle2D.Double(
      mAbsoluteRect.getMinX()*scale,
      mAbsoluteRect.getMinY()*scale,
      mAbsoluteRect.getWidth()*scale,
      mAbsoluteRect.getHeight()*scale
    );
	}

	public void setAbsoluteRect(Rectangle2D rect)
	{
		mAbsoluteRect = (Rectangle2D)rect.clone();
	}

	public void setScaledRect(Rectangle2D scRect)
	{
		setScaledRect(scRect, mScale);
	}

	/**
	 * Should we get rid of this one because it's not clear from the API if it
//	 * sets the scale to the specified scale?
	 * @param scRect
	 * @param pScale
	 */
	public void setScaledRect(Rectangle2D scRect, Scale pScale)
	{
		mScale = pScale;
		double scale = pScale.getScale();
		mAbsoluteRect = new Rectangle2D.Double(
		        scRect.getX()/scale,
		        scRect.getY()/scale,
		        scRect.getWidth()/scale,
		        scRect.getHeight()/scale
		);
	}

	public Scale getScale() { return mScale; }

	public static ScaledRect makeRectFromScale(double scaledX,
	                                           double scaledY,
	                                           double scaledWidth,
	                                           double scaledHeight,
	                                           Scale pScale)
	{
		double sc = pScale.getScale();
		return new ScaledRect(scaledX/sc, scaledY/sc, scaledWidth/sc,
		        scaledHeight/sc, pScale);
	}

	public boolean contains(ScaledPoint pt)
	{
		return mAbsoluteRect.contains(pt.getAbsolutePoint2D());
	}
}
