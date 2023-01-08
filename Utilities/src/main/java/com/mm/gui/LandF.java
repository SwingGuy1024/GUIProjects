package com.mm.gui;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 * More convenient way to set looks and feels. There are three ways to use this
 * class. An enum constant is defined for all standard looks and feels. If 
 * you're not worried about exceptions, you can call the setLookAndFeel method
 * from the constant:
 * <pre> 
 *   LandF.Ocean.quickSetLF();
 * </pre> or you can set call the static method: 
 * <pre>
 *   LandF.setLF(LandF.Ocean);
 * </pre> These two methods are equivalent. Both of these discard any Exceptions
 * that get thrown. Exceptions are only likely if the specified LandF is 
 * not supported on your platform. If you're not getting what you 
 * expect, and you want to see the exception, you can do this:
 * <pre>
 *       try { LandF.Ocean.setLookAndFeel(); }
 *      catch (Exception err) { err.printStackTrace(); }
 * </pre>
 * Created using IntelliJ IDEA. Date: Oct 3, 2004 Time: 5:46:28 PM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

@SuppressWarnings({"HardCodedStringLiteral"})
public enum LandF
{
              /** Platform's default Look and Feel */
  Platform  ("platform", UIManager.getSystemLookAndFeelClassName()), // NON-NLS

              /** Metal Look and feel, Steel theme */
  Metal     ("metal") { // NON-NLS
                  protected LookAndFeel makeLF() 
                  {
//                    System.out.println("Making Metal"); // NON-NLS
                    MetalLookAndFeel lf = new MetalLookAndFeel(); 
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                    return lf;
                  } 
                },

              /** Metal Look and Feel, Ocean theme */
  Ocean     ("ocean") {
	              protected LookAndFeel makeLF() {
//									System.out.println("making Ocean");
		              MetalLookAndFeel metalLookAndFeel = new MetalLookAndFeel();
		              MetalLookAndFeel.setCurrentTheme(new OceanTheme());
		              return metalLookAndFeel; 
								} 
              }, // NON-NLS

              /** GTK+ Look and Feel. (Apparently not supported under Windows.) */
  GTK       ("gtk", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"), // NON-NLS
  
  WindowsClassic("windowsclassic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"), // NON-NLS

              /** Motif Look and Feel */
  Motif     ("motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"), // NON-NLS

							/** Nimbus Look and Feel */
	Nimbus    ("nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel") {

								/**
								 * This works around the problems with tables in Nimbus. It eliminates
								 * striped rows, which allows existing renderers to work. This includes
								 * the Boolean renderer that Nimbus uses.
								 * You may find this workaround in the bug parade, Bug 6723524.
								 */
								@SuppressWarnings({"MagicNumber"}) 
								@Override 
								protected void cleanup() {
									LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
									UIDefaults defaults = lookAndFeel.getDefaults();
									defaults.remove("Table.alternateRowColor");
									defaults.put("Table.showGrid", Boolean.TRUE);
									defaults.put("Table.gridColor", new ColorUIResource(203, 209, 216));
									defaults.put("Table.intercellSpacing", new DimensionUIResource(1,1));
									defaults.put("Table.background", new ColorUIResource(242, 242, 242));
									defaults.put("Table:\"Table.cellRenderer\".background", new ColorUIResource(242, 242, 242));
								}}; // NON-NLS

  private final String      mName;
	public  final String      mLabel;

  private LandF(String pLabel, String pName) {
		mName = pName;
		mLabel = pLabel;
	}
  private LandF(String pLabel)             { this(pLabel, null); }
  public void setLookAndFeel()
    throws 
      UnsupportedLookAndFeelException,
      ClassNotFoundException,
      InstantiationException,
      IllegalAccessException
  {
    if (mName == null)
      UIManager.setLookAndFeel(makeLF());
    else
      UIManager.setLookAndFeel(mName);
	  cleanup();
  }
  /**
   * Attempts to set the specified look and feel, and throws away any
   * exceptions generated. Consequently, you may end up with the default 
   * look and feel. This is because not all Looks-and-Feels are supported on
   * all platforms. Use {@code setLookAndFeel()} to see the Exceptions. 
   */ 
  public void quickSetLF()
  {
	  //noinspection CatchGenericClass
	  try { setLookAndFeel(); }
    catch(Exception ex) { }
  }
	
	public void set() { quickSetLF(); }
  
  /**
   * This only needs to be defined in instances that use the empty constructor.
   * @return The look and feel. 
   */ 
  protected LookAndFeel makeLF() { //noinspection StringConcatenation
	  throw new AssertionError("makeName called from " + mName);
  }
	
  public static void setLF(LandF pLf) { pLf.quickSetLF(); }
	
//	// Static block shows all supported L&Fs on startup.
//  static 
//  {
//    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
//    for (UIManager.LookAndFeelInfo in : info)
//	    //noinspection ConcatenationWithEmptyString,StringConcatenation,StringContatenationInLoop
//	    System.out.println("" + in.getClassName() + "   " + in);
//  }
//
	/**
	 * Sets the first look and feel that works from the list of L&F labels.
	 * If all of them fail, and at least one of them fails by throwing an exception,
	 * this call will throw a RuntimeException with the first exception as its root cause.
	 * @param pLabel a series of labels of L&Fs
	 * TODO: Should this catch exceptions and keep going?
	 */
	public static void setLF(String... pLabel) {
		Exception firstException = null;
		for(String label: pLabel) {
			for (LandF lf:values()) {
				//noinspection CatchGenericClass
				try {
					//noinspection CallToStringEquals
					if (lf.mLabel.equals(label.toLowerCase())) {
						lf.setLookAndFeel();
						return;
					}
				}
				catch (Exception ex) {
					if (firstException == null) firstException = ex;
				}
			}
		}
		if (firstException != null) {
			//noinspection ProhibitedExceptionThrown
			throw new RuntimeException(firstException);
		}
	}

	public static void setLF(LandF... landf) {
		Exception lastException=null;
		for (LandF lf: landf) {
			//noinspection CatchGenericClass
			try {
				lf.setLookAndFeel();
				return;
			}
			catch (Exception ex) {
				lastException = ex;
			}
		}
		//noinspection ProhibitedExceptionThrown
		throw new RuntimeException(lastException);
	}
	
	protected void cleanup() { }
}

