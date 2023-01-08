package com.mm.gui;

/**
 * Created using IntelliJ IDEA.
 * Date: May 28, 2005
 * Time: 12:07:16 AM
 * @author Miguel Mu\u00f1oz
 *
 * Copyright (c) 2004 Miguel Munoz
 */

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * TextLayout object for drawing text with the baseline along the circumference
 * of a circle. 
 */ 
public class CurveText
{
	private final String mTxt;
	private final Font		mFont;
	private final Point2D	mCenter;
	private final float 	mRadius;
	/** 1 for outside text, -1 for inside text */
	private final	int		insideOrOutside;
	private final	float mCenterAngle;

	/**
	 * Construct a Curved text object, with the specified attributes.
	 * @param pText 	The Text to write
	 * @param pFnt		The font to rener with
	 * @param outside	true to write outside the baseline, false for inside
	 * @param center  The coordinates of the circle's center
	 * @param radius	The radius of the circle
	 * @param startingAngle The position on the circle's circumference on which
	 * the text will be centered. Starting angle is always measured with 
	 * clockwise for increasing. For drawing outside the circle, the angle is 
	 * measured from the top of the circle, which is the lowest y value. For 
	 * inside drawing, the angle is measured from the bottom of the circle, 
	 * which is the highest y value.
	 */ 
	public CurveText(String pText, Font pFnt, boolean outside, Point2D center, 
									 float radius, float startingAngle) {
		mTxt=pText;
		mFont = pFnt;
		int inside = outside ? 1 : -1;
		insideOrOutside = inside;
		mCenter = center;
		mRadius = radius;
		mCenterAngle = startingAngle;
	}

	/**
	 * 
	 * @param g0 The Graphics2D object
	 * the text. It is measured from the top of the circle. top: lowest Y value
	 */ 
	public void paintCurvedText(Graphics g0)
	{
//		Graphics2D ga = g0.clone();
//		Font fnt=new Font("Serif", 0, 18);
//		Point2D loc = new Point2D.Float(10, 50);

		Graphics2D g2=(Graphics2D)g0;
		Object hint = RenderingHints.VALUE_ANTIALIAS_ON;
		RenderingHints.Key key = RenderingHints.KEY_ANTIALIASING;
		g2.setRenderingHint(key, hint);

//		draw(mTxt, loc, g2, mFont);
		// (x-x0)^2 + (y-y0)^2 = r^2
		// x(t) = (x-xc)*sin(t)
		// y(t) = (y-yc)*cos(t)
		
		float radius = mRadius;
		int start = 0;
		int lineBreak = mTxt.indexOf('\n');
		FontMetrics mets = g2.getFontMetrics(mFont);
		float height = mets.getAscent() + mets.getDescent() + mets.getLeading();
		while (lineBreak >=0)
		{
			paintOneTextLine(g2, mTxt.substring(start, lineBreak), radius);
			start = lineBreak+1;
			lineBreak = mTxt.indexOf('\n', start);
			radius -= insideOrOutside*height;
		}
		paintOneTextLine(g2, mTxt.substring(start), radius);
	}
	
	private void paintOneTextLine(Graphics2D g2, String theText, float radius)
	{
		float advance;
		TextLayout measure = new TextLayout(theText, mFont, g2.getFontRenderContext());
		float halfWidth = measure.getAdvance()/2;
		float startingDistance = mCenterAngle*radius-insideOrOutside*halfWidth;
		float cumulativeAngle = startingDistance/radius;// + (float)Math.PI;
//		Font drawFont = mFont;
		for (int ii=0; ii<theText.length(); ++ii)
		{
			char cc = theText.charAt(ii);
			float angle=cumulativeAngle;
			Point2D thisLoc = getCPoint(angle, radius);
			
			advance = draw(cc, thisLoc, g2, angle); //
			cumulativeAngle += insideOrOutside*advance/radius;
		}
	}

	
	private Point2D getCPoint(float angle, float radius)
	{
		float x = (float)(mCenter.getX() + Math.sin(angle)*radius*insideOrOutside);
		float y = (float)(mCenter.getY() - Math.cos(angle)*radius*insideOrOutside);
		return new Point2D.Float(x, y);
	}
	
	private float draw(char letter, Point2D loc, Graphics2D g, float angle)
	{
		if("\n\r\f".indexOf(letter) >= 0)
			return 0.0f;
		FontRenderContext frc = g.getFontRenderContext();
		char[] chrs = {letter};
		String txt = new String(chrs);
		float advance=new TextLayout(txt, mFont, frc).getAdvance();
		
		// Add in rotation of 1/2 character width:
		angle += insideOrOutside*advance/mRadius;

		AffineTransform transform = AffineTransform.getRotateInstance(angle);
		Font rotatedFont = mFont.deriveFont(transform);
		TextLayout layout = new TextLayout(txt, rotatedFont, frc);
		
		layout.draw(g, (float)loc.getX(), (float)loc.getY());
		return advance;
	}

}
