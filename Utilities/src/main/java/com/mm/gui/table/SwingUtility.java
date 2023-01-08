package com.mm.gui.table;

import java.awt.FontMetrics;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;


/**
 * This class exists to work around the new location of SwingUtilities2 in Java 1.6. 
 * This class will have a different form on java 1.6, and shouldn't get checked in 
 * to subversion.
 */
public final class SwingUtility {
	private SwingUtility() { }
	
	public static String clipString(JComponent comp, FontMetrics fm, String str, int i) {
		return SwingUtilities2.clipString(comp, fm, str, i);
	}
	
	public static int stringWidth(JComponent comp, FontMetrics fm, String str) {
		return SwingUtilities2.stringWidth(comp, fm, str);
	}
}
