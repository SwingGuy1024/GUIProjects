package com.mm.gui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;

/**
 * Created using IntelliJ IDEA. Date: Apr 19, 2005 Time: 5:27:53 AM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

public class CircularText
{
	/**
	 * Tool to draw Circular text. (Should this be a component?)
	 * @param pGr
	 * @param pText     The text to draw
	 * @param pCenter   The Center of the circle.
	 * @param pAngle    The angle from vertical (Constant X, positive Y)
	 * @param pRadius   The distance from the center to the start of the first
	 * character.
	 * @param pHighOut  LetterOrientatoin: Should the tops or bottoms of the
	 * letters be closer to the center? True means the bottoms should be 
	 * closer; False means the tops should be closer.                  
	 */ 
	public CircularText(Graphics2D pGr, 
	                    String  pText, 
	                    Point   pCenter, 
	                    double  pAngle,
	                    int     pRadius,
	                    boolean pHighOut)
	{
		pGr.translate(pCenter.x,  pCenter.y);
		Font fnt = pGr.getFont();
		FontRenderContext frc = pGr.getFontRenderContext();
		LineMetrics mets = fnt.getLineMetrics(pText, frc);
//		FontMetrics fm;
//		GlyphVector glyphs = fnt.createGlyphVector(frc, pText);
//		char[] text = pText.toCharArray();
		StringBuilder bld = new StringBuilder();
		float xx = (float)(pRadius*Math.sin(pAngle));
		float yy = (float)Math.cos(pAngle)*pRadius;
		for (int ii=0; ii<pText.length(); ++ii)
		{
			
			char thisChar = pText.charAt(ii);
			bld.append(thisChar);
			String letter = bld.toString();
			GlyphVector glyphs = fnt.createGlyphVector(frc, letter);
			Rectangle glyphRect = glyphs.getGlyphPixelBounds(0, frc, xx, yy);
			int charCenter = glyphRect.width/2;
			pGr.drawString(letter, xx, yy);
//			pGr.
//			int thisWidth = fnt.g;
		}
		pGr.translate(-pCenter.x,  -pCenter.y);
	}
}
