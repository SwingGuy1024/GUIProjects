package com.mm.gui.table;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Created using IntelliJ IDEA. Date: Jul 3, 2004 Time: 1:22:27 PM
 * 
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */


public class LongRenderer extends DefaultTableCellRenderer
{
	private static final NumberFormat sFmt = NumberFormat.getIntegerInstance();
//	private static final NumberFormat sDblFmt = NumberFormat.getNumberInstance();
	public LongRenderer() { super(); }

	public Component getTableCellRendererComponent(
	    JTable table,
	    Object value,
	    boolean isSelected,
	    boolean hasFocus,
	    int row,
	    int column
	    )
	{
		return super.getTableCellRendererComponent(
		    table, getLongValue(value), isSelected, hasFocus, row, column
		);    //To change body of overridden methods use File | Settings | File Templates.
	}
	
	public static Object getLongValue(Object value)
	{
		if (value instanceof Number)
		{
			long longVal=((Number)value).longValue();
			
////			String suffix;
//			if (longVal>9999000L)
//			{
//				return sDblFmt.format(1.0*longVal/(1024*1024)) + "MB";
////				suffix = "MB";
////				longVal = new Double(longVal)./(1024.0*1024.0));
////				longVal=longVal.;
//			}
//			else if (longVal>9999L)
//			{
//				return sDblFmt.format((1.0*longVal)/1024) + "KB";
////				suffix = "kB";
////				longVal /= 1024L;
//			}
////			else
////				suffix = "";
			return sFmt.format(longVal);
		}
		else
			return value;
	}
}
