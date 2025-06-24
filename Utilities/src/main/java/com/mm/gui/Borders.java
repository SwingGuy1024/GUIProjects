package com.mm.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * The JComponent's setBorder method is clumsy to work with, and BorderFactory 
 * doesn't help much. This class makes it easier to add borders to Swing
 * components. More convenience methods may be added later as needed.
 * <p>
 * Thought: Should the addXxxBorder methods return the newly added border?
 * This would make it easier to apply the same border to multiple objects.
 * @author Miguel Mu\u00f1oz
 */
public enum Borders
{
  ;

  /**
	 * Adds the specified border to the outside of the existing border, if
	 * there is one, or directly to the JComponent if not.
	 * @param cmp   The JComponent to get the border
	 * @param border  The border
	 */ 
	public static void addBorder(JComponent cmp, Border border)
	{
		Border curBdr = cmp.getBorder();
		if (curBdr != null)
			border = new CompoundBorder(border, curBdr);
		cmp.setBorder(border);
	}
	
	/**
	 * Adds the specified border to all the JComponents in the array. Adds it
	 * to the outside of the existing border, if there is one, or directly 
	 * to the JComponent if not. This makes it easy for several components
	 * to share the same Border. 
	 * @param pBorder
	 * @param pCmp
	 */ 
	public static void addBorder(Border pBorder, JComponent[] pCmp)
	{
		for (JComponent cmp : pCmp) addBorder(cmp, pBorder);
	}

	/**
	 * Inserts a border in between the current border and the JComponent. 
	 * If there are already multiple borders, this becoms the innermost border. 
	 * @param cmp   The JComponent to get the border
	 * @param brdr  The border to insert
	 */ 
	public static void insertBorder(JComponent cmp, Border brdr)
	{
		Border curBdr = cmp.getBorder();
		if (curBdr != null)
			brdr = new CompoundBorder(curBdr, brdr);
		cmp.setBorder(brdr);
	}
	
	public static void copyBorder(JComponent pFrom, JComponent pTo)
	{
		pTo.setBorder(pFrom.getBorder());
	}
	
	/**
	 * Adds an empty border to the JComponent. Puts it outside any existing 
	 * borders.
	 * @param cmp     The JComponent to get the border.
	 * @param top     The top, in pixels
	 * @param left    The left side, in pixels
	 * @param bottom  The bottom, in pixels
	 * @param right   The right side, in pixels
	 */ 
	public static void addEmptyBorder(JComponent cmp, int top, int left, int bottom, int right)
	{
		addBorder(cmp, BorderFactory.createEmptyBorder(top, left, bottom, right));
	}
	
	private static Map<Integer, Border> emptyBorders;
	
	/**
	 * Adds an empty border to the JComponent. Puts it outside any existing 
	 * borders.
	 * @param cmp     The JComponent to get the border.
	 * @param size    The size, in pixels
	 */ 
	public static void addEmptyBorder(JComponent cmp, int size)
	{
		if (size==0)
			addBorder(cmp, BorderFactory.createEmptyBorder()); // reuses a singleton.
		else {
			if (emptyBorders==null)
				emptyBorders = new HashMap<Integer, Border>();
			Border emptyBorder;
			if (emptyBorders.containsKey(size))
				emptyBorder = emptyBorders.get(size);
			else {
				emptyBorder = BorderFactory.createEmptyBorder(size, size, size, size);
				emptyBorders.put(size, emptyBorder);
			}
			addBorder(cmp, emptyBorder);
		}
	}
	
	/**
	 * Adds a TitledBorder to the JComponent, putting it outside any existing
	 * border.
	 * @param cmp
	 * @param title
	 */ 
	public static void addTitledBorder(JComponent cmp, String title)
	{
		addBorder(cmp, BorderFactory.createTitledBorder(title));
	}
	
	public static void addEtchedBorder(JComponent cmp)
	{
		// we use the shared border from the BorderFactory.
		addBorder(cmp, BorderFactory.createEtchedBorder());
	}
	
	public static void addMatte(JComponent cmp, int top, int left, int bottom, int right)
	{
		addBorder(cmp, new MatteBorder(top, left, bottom, right, cmp.getBackground()));
	}
	
	public static void addMatte(JComponent cmp, int top, int left, int bottom, int right, Color clr)
	{
		addBorder(cmp, new MatteBorder(top, left, bottom, right, clr));
	}
	
	public static void addMatte(JComponent cmp, int size, Color clr)
	{
		addBorder(cmp, new MatteBorder(size, size, size, size, clr));
	}
	
	public static void addMatte(JComponent cmp, int size)
	{
		addMatte(cmp, size, cmp.getBackground());
	}
	
	public static void addLine(Color pClr, int pThick, boolean pRnd, JComponent[] pCmp)
	{
			addBorder(new LineBorder(pClr, pThick, pRnd), pCmp);
	}
	public static void addLine(Color pClr, int pThick, JComponent pCmp[]) { addLine(pClr, pThick, false, pCmp); }
	public static void addLine(Color pClr, JComponent[] pCmp) { addLine(pClr, 1, false, pCmp); }
	public static void addLine(int pThick, JComponent[] pCmp) { addLine(Color.BLACK, pThick, false, pCmp); }
	public static void addLine(JComponent... pCmps) { addLine(Color.BLACK, 1, false, pCmps); }
    
    	public static void addRaisedBevelBorder(JComponent cmp)
	{
	    addBorder(cmp, new BevelBorder(BevelBorder.RAISED));
	}
    public static void addLoweredBevelBorder(JComponent cmp)
    {
	addBorder(cmp, new BevelBorder(BevelBorder.LOWERED));
    }
}
