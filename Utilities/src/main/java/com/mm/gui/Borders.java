package com.mm.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

/**
 * The JComponent's setBorder method is clumsy to work with, and BorderFactory 
 * doesn't help much. This class makes it easier to add borders to Swing
 * components. More convenience methods may be added later as needed.
 * <p>
 * Thought: Should the addXxxBorder methods return the newly added border?
 * This would make it easier to apply the same border to multiple objects.
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public enum Borders
{
  ;

  /**
	 * Adds the specified border to the outside of the existing border, if
	 * there is one, or directly to the JComponent if not.
	 * Returns the provided border
	 * @param cmp   The JComponent to get the border
	 * @param border  The border
	 * @return {@code border}
	 */
	public static <T extends Border> T addBorder(JComponent cmp, T border)
	{
		Border curBdr = cmp.getBorder();
		if (curBdr != null) {
			CompoundBorder cmpBorder = new CompoundBorder(border, curBdr);
			cmp.setBorder(cmpBorder);
		} else {
			cmp.setBorder(border);
		}
		return border;
	}
	
	/**
	 * Adds the specified border to all the JComponents in the array. Adds it
	 * to the outside of the existing border, if there is one, or directly 
	 * to the JComponent if not. This makes it easy for several components
	 * to share the same Border. 
	 * @param border
	 * @param pCmp
	 * @return {@code border}
	 */ 
	public static <T extends Border> T addBorder(T border, JComponent[] pCmp)
	{
		for (JComponent cmp : pCmp) {
      addBorder(cmp, border);
    }
		return border;
	}

	/**
	 * Inserts a border in between the current border and the JComponent. 
	 * If there are already multiple borders, this becomes the innermost border. 
	 * @param cmp   The JComponent to get the border
	 * @param border  The border to insert
	 * @return {@code border}
	 */ 
	public static <T extends Border> T insertBorder(JComponent cmp, T border)
	{
		Border curBdr = cmp.getBorder();
		if (curBdr != null) {
      CompoundBorder cmpBorder = new CompoundBorder(curBdr, border);
			cmp.setBorder(cmpBorder);
    } else {
			cmp.setBorder(border);
		}
		return border;
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
	 * @return the new empty border, which may be shared with other components.
	 */ 
	public static Border addEmptyBorder(JComponent cmp, int top, int left, int bottom, int right)
	{
		final Border emptyBorder = BorderFactory.createEmptyBorder(top, left, bottom, right);
		return addBorder(cmp, emptyBorder);
	}
	
	private static Map<Integer, Border> emptyBorders;
	
	/**
	 * Adds an empty border to the JComponent. Puts it outside any existing 
	 * borders.
	 * @param cmp     The JComponent to get the border.
	 * @param size    The size, in pixels
	 * @return the new empty border, which may be shared with other components.
	 */ 
	public static Border addEmptyBorder(JComponent cmp, int size)
	{
		Border emptyBorder;
		if (size==0) {
			emptyBorder = BorderFactory.createEmptyBorder();
			addBorder(cmp, emptyBorder); // reuses a singleton.
		} else {
			if (emptyBorders==null) {
        emptyBorders = new HashMap<Integer, Border>();
      }
			if (emptyBorders.containsKey(size)) {
        emptyBorder = emptyBorders.get(size);
      } else {
				emptyBorder = BorderFactory.createEmptyBorder(size, size, size, size);
				emptyBorders.put(size, emptyBorder);
			}
			addBorder(cmp, emptyBorder);
		}
		return emptyBorder;
	}
	
	/**
	 * Adds a TitledBorder to the JComponent, putting it outside any existing
	 * border.
	 * @param cmp		The JComponent to get the border
	 * @param title The title
	 * @return the new TitledBorder, which may be shared with other components.
	 */ 
	public static TitledBorder addTitledBorder(JComponent cmp, String title)
	{
		final TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
		return addBorder(cmp, titledBorder);
	}

	/**
	 * Adds a new EtchedBorder to the JComponent, putting it outside any existing border
	 * @param cmp The component to get the border
	 * @return the new EtchedBorder, created by BorderFactory, which may be shared with other components.
	 */
	public static EtchedBorder addEtchedBorder(JComponent cmp)
	{
		// we use the shared border from the BorderFactory.
		final EtchedBorder etchedBorder = (EtchedBorder) BorderFactory.createEtchedBorder();
		return addBorder(cmp, etchedBorder);
	}
	
	public static MatteBorder addMatte(JComponent cmp, int top, int left, int bottom, int right)
	{
		return addBorder(cmp, new MatteBorder(top, left, bottom, right, cmp.getBackground()));
	}
	
	public static Border addMatte(JComponent cmp, int top, int left, int bottom, int right, Color clr)
	{
		return addBorder(cmp, new MatteBorder(top, left, bottom, right, clr));
	}
	
	public static void addMatte(JComponent cmp, int size, Color clr)
	{
		addBorder(cmp, new MatteBorder(size, size, size, size, clr));
	}
	
	public static void addMatte(JComponent cmp, int size)
	{
		addMatte(cmp, size, cmp.getBackground());
	}
	
	public static MatteBorder insertMatte(JComponent cmp, int size) {
		return insertMatte(cmp, size, size, size, size, cmp.getBackground());
	}
	
	public static MatteBorder insertMatte(JComponent cmp, int top, int left, int bottom, int right, Color clr) {
		final MatteBorder matteBorder = new MatteBorder(top, left, bottom, right, clr);
		insertBorder(cmp, matteBorder);
		return matteBorder;
	}
	
	public static Border insertEmptyBorder(JComponent cmp, int top, int left, int bottom, int right) {
		return insertBorder(cmp, new EmptyBorder(top, left, bottom, right));
	}
	
	public static Border insertEmptyBorder(JComponent cmp, int size) {
		return insertBorder(cmp, new EmptyBorder(size, size, size, size));
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
