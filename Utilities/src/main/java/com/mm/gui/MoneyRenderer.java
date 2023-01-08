package com.mm.gui;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import com.mm.gui.table.EditRenderer;

/**
 * @author Miguel Mu\u00f1oz
 * Copyright (c) 2004 by Miguel Munoz
 */
public class MoneyRenderer extends DefaultTableCellRenderer
{
	private static final NumberFormat mFmt = NumberFormat.getCurrencyInstance();
	private int          mCachedPadding = -1; // -1 is the uninitialized value.
	public  MoneyRenderer() { super(); }
	
	public static String makeCurrency(BigDecimal amt) { return mFmt.format(amt.doubleValue()); } 

	public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
			)
	{
		BigDecimal amount=(BigDecimal)value;
//		String money=mFmt.format(amount.doubleValue());
		// This line fixes the Nimbus rendering problem. I removed it because
		// I now modify Nimbus to make the rendering problem go away, but it's here
		// for reference.
//		setBackground(table.getBackground());
		JLabel cmp=(JLabel) super.getTableCellRendererComponent(
				    table, 
				    makeCurrency(amount), 
				    isSelected, 
				    hasFocus, 
				    row, 
				    column
				);
		// Test for negative
		if (amount.compareTo(BigDecimal.ZERO) < 0)
			if(isSelected)
				cmp.setForeground(blend(Color.red, getForeground()));
			else
				cmp.setForeground(Color.red);
		else
			if (isSelected)
				cmp.setForeground(table.getSelectionForeground());
			else
				cmp.setForeground(table.getForeground());
		cmp.setHorizontalAlignment(JLabel.RIGHT);

		// This needs to be done before the padding, below.
		EditRenderer.shadeReadOnly(cmp, table, row, column, isSelected);

		// Here's a trick to keep decimals aligned: The financial industry doesn't 
		// like to use the minus sign to show negative numbers. They prefer to 
		// see negative numbers in parentheses. But that prevents the decimals
		// from lining up properly. So, if it's a positive number, I pad it with
		// some extra space. I use the MatteBorder to supply the padding.
		// I wish I could cache the MatteBorders for efficiency, but the
		// background color changes as different cells get selected.
		int pad;
		if (amount.doubleValue() < 0.0)
			pad = 0;
		else
			pad = getCachedPadding();
		if (pad > 0)
			insertBorder(cmp, new MatteBorder(0, 0, 0, pad, cmp.getBackground()));
		return cmp;
	}
	
	private static Color blend(@SuppressWarnings("SameParameterValue") Color c1, Color c2)
	{
		int r = (c1.getRed() + c2.getRed())>>1;     // >> 1 means /2
		int g = (c1.getGreen() + c2.getGreen())>>1;
		int b = (c1.getBlue() + c2.getBlue())>>1;
		int a = (c1.getAlpha() + c2.getAlpha())>>1;
		return new Color(r, g, b, a);
	}

	/**
	 * This inserts a new border inside the existing border. It's safe to use 
	 * even if there is no existing border. <p> 
	 * I borrowed this from a class of border utilities I wrote. It also includes
	 * the more useful "addBorder" method, which puts the new border outside the
	 * existing one. The code for addBorder is identical to this, except it 
	 * swaps the parameters to the CompoundBorder constructor.
	 * @param cmp The component
	 * @param brdr The border
	 */ 
	public static void insertBorder(JComponent cmp, Border brdr)
	{
		Border currentBorder = cmp.getBorder();
		if (currentBorder == null)
			cmp.setBorder(brdr);
		else
			cmp.setBorder(new CompoundBorder(currentBorder, brdr));
	}

	/**
	 * Note: This may fail in other locales. A more sophisticated algorithm would
	 * extract the trailing character from the NumberFormat, and use that for
	 * padding. 
	 * @return the width of the ')' character.
	 */ 
	private int getCachedPadding()
	{
		if (mCachedPadding < 0)
    {
      mCachedPadding = 0;
      List<Character> trail = getTrailingChars();
      for (char cc : trail)
        mCachedPadding += getFontMetrics(getFont()).charWidth(cc);
    }
		return mCachedPadding;
	}
  
  private static List<Character> getTrailingChars()
  {
    List<Character> ret = new ArrayList<>();
    String pos = mFmt.format(4.5f);
    String neg = mFmt.format(-4.5f);
    char pTail = pos.charAt(pos.length()-1);
    for (int ii= neg.length()-1; ii>=0; --ii)
    {
      char nChr=neg.charAt(ii);
      if (nChr == pTail)
        return ret;
      else
        ret.add(neg.charAt(ii));
    }
    // shouldn't reach this point.
    throw new AssertionError("mismatch negative & positive strings: " + neg + "   " + pos);
  }
}
