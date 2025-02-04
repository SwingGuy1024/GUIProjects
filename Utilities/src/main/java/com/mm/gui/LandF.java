package com.mm.gui;

import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.JTextComponent;

import org.jetbrains.annotations.Nullable;

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
 * Copyright (c) 2004 Miguel Munoz
 */

@SuppressWarnings({"HardCodedStringLiteral", "unused", "UnnecessaryUnicodeEscape", "OverlyBroadCatchBlock"})
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
	Nimbus    ("nimbus", NimbusLookAndFeel.class.getName()) {

								/**
								 * This works around the problems with tables in Nimbus. It eliminates
								 * striped rows, which allows existing renderers to work. This includes
								 * the Boolean renderer that Nimbus uses.
								 * You may find this workaround in the bug parade, Bug 6723524.
								 */
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

	@Nullable
  private final String      mName;
	public  final String      mLabel;

  LandF(String pLabel, @Nullable String pName) {
		mName = pName;
		mLabel = pLabel;
	}
  LandF(String pLabel)             { this(pLabel, null); }
  public void setLookAndFeel()
    throws 
      UnsupportedLookAndFeelException,
      ClassNotFoundException,
      InstantiationException,
      IllegalAccessException
  {
    if (mName == null) {
			UIManager.setLookAndFeel(makeLF());
		} else {
			UIManager.setLookAndFeel(mName);
		}
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
		//noinspection CatchGenericClass,OverlyBroadCatchBlock
	  try { setLookAndFeel(); }
    catch(Exception ex) { ex.printStackTrace(); }
  }
	
	public void set() { quickSetLF(); }
  
  /**
   * This only needs to be defined in instances that use the empty constructor.
   * @return The look and feel. 
   */ 
  protected LookAndFeel makeLF() {
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
					if (firstException == null) { firstException = ex; }
				}
			}
		}
		if (firstException != null) {
			//noinspection ProhibitedExceptionThrown
			throw new RuntimeException(firstException);
		}
	}

	@SuppressWarnings("OverlyBroadCatchBlock")
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

	/**
	 * <p>Add Mac KeyStroke bindings to a JTextComponent. This tries to remove the
	 * Windows bindings, but it doesn't work, because the Windows bindings 
	 * aren't in the available InputMap. I don't know where they are or if I
	 * can remove them.</p> 
	 * <p>(This method does nothing when not run on a Mac, so it's safe for any platform.)</p>
	 * @see LandF#addOSXKeyStrokesMac(InputMap) 
	 * @param textComponent The JTextComponent subclass to fix.
	 */
	public static void addOSXKeyStrokesMac(JTextComponent textComponent) {
		addOSXKeyStrokesMac(textComponent.getInputMap(JComponent.WHEN_FOCUSED));
		// Note. There seem to be three InputMaps, for WHEN_FOCUSED, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, and
		// WHEN_IN_FOCUSED_WINDOW. Maybe they're not getting removed because I'm doing this wrong. 
	}

	/**
	 * <p>Wrap a JTextComponent subclass to adapt it for Mac in a non-Mac look and feel like Nimbus.</p>
	 * <p>(This method does nothing when not run on a Mac, so it's safe for any platform.)</p>
	 * <p>For example:</p>
	 * <pre>
	 *   JTextArea textArea = wrapForMac(new JTextArea(6, 40));
	 *   JTextField textField = wrapForMac(new JTextField(40));
	 * </pre>
	 * @param textComponent The text component.
	 * @return The text component
	 * @param <TC> The JTextComponent subclass for textComponent
	 */
	public static <TC extends JTextComponent> TC wrapForPlatform(TC textComponent) {
		addOSXKeyStrokesMac(textComponent.getInputMap(JComponent.WHEN_FOCUSED));
		return textComponent;
	}

	/**
	 * <p>(This method does nothing when not run on a Mac, so it's safe for any platform.)</p>
	 * <p>This fix was suggested by 
	 * <a href=https://stackoverflow.com/questions/9780028/mac-keyboard-shortcuts-with-nimbus-laf>This stackoverflow question</a>.
	 * Most LookAndFeel classes install windows and unix key bindings. This makes them less usable on Macs. This
	 * installs Mac keyboard bindings. (It doesn't get all of them.) It also tries to uninstall the Windows/Unix
	 * bindings, but that doesn't work. So it leaves JTextComponents with both bindings working. These bindings
	 * are for all JTextComponents. But they don't share the same InputMaps, so these need to be installed on
	 * every JTextComponent instance to be useful.</p>
	 * <p>Debugging Note. I tried to figure out why it didn't remove the old key bindings. I stepped through the
	 * code and confirmed that the call to remove the key-binding was working, and I didn't even need to recurse
	 * through the parents, which were null anyway. So I have no idea why the old key bindings are still in place.</p>
	 * <p>Here's a list of all the Aqua Key Bindings:<br>
	 * https://hg.openjdk.org/jdk8/jdk8/jdk/file/687fd7c7986d/src/macosx/classes/com/apple/laf/AquaKeyBindings.java</p>
	 * @param inputMap The InputMap
	 */
	public static void addOSXKeyStrokesMac(InputMap inputMap) {
		if (!System.getProperty("os.name").startsWith("Mac")) {
			return;
		}
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), "copy-to-clipboard");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), "cut-to-clipboard");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), "paste-from-clipboard");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), "select-all");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.ALT_DOWN_MASK), "delete-next-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, KeyEvent.ALT_DOWN_MASK), "caret-next-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK), "caret-next-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "selection-next-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "selection-next-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.ALT_DOWN_MASK), "delete-previous-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "caret-previous-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, KeyEvent.ALT_DOWN_MASK), "caret-previous-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, KeyEvent.CTRL_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "selection-previous-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "selection-previous-word");
		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));

//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.META_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "");
//		remove(inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
//		inputMap.put()
	}
	
	private static void remove(InputMap inputMap, KeyStroke keyStroke) {
		InputMap parent = inputMap;
		do {
			final Object value = parent.get(keyStroke);
			if (value != null) {
				parent.remove(keyStroke);
				return;
			}
			parent = parent.getParent();
		} while (parent != null);
	}
	
	protected void cleanup() { }
}

