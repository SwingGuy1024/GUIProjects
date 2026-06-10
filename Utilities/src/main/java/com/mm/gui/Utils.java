package com.mm.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.net.URL;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: miguelmunoz
 * Date: Sep 6, 2010
 * Time: 8:35:34 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings({"UnnecessaryUnicodeEscape", "MagicNumber"})
public enum Utils {
	;

	public static final double toRadians = Math.PI / 180.0;
	public static final Object[] EMPTY = new Object[0];

	public static void main(String[] args) {
		String testString = "r\u00f4le\u030a, ro\u0302le 35\u00c5, A\u030arch \ufb01ne, first, 2\u2075 C\u0152R D\u00c6R";
		
		JFrame frame = new JFrame("Normal Test");
		JPanel mainPanel = new JPanel(new GridLayout(1, 0));
		frame.add(mainPanel);
		JTextArea field = new JTextArea(5, 40);
		JTextArea outField = new JTextArea(5, 40);
		StringBuilder bldr = new StringBuilder();
		append(testString, bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFC), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFD), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFKC), bldr);
		append(Normalizer.normalize(testString, Normalizer.Form.NFKD), bldr);
		field.setText(bldr.toString());
		mainPanel.add(field);
		outField.setText(strippedString(field.getText()));
		mainPanel.add(outField);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static void append(String text, StringBuilder bldr) {
		bldr.append(text);
		bldr.append(" (");
		bldr.append(text.length());
		bldr.append(")\n");
	}
	
	public static String strippedString(String rawInputString) {
//		for (int ii=0; ii<rawInputString.length(); ++ii) {
//			final char c = rawInputString.charAt(ii);
//			//noinspection HardcodedLineSeparator,HardcodedFileSeparator  
//			System.out.printf("%c  (\\u%4x): %s\n", c, ((int)c), Character.getType(c)); //NON-NLS
//		}
		String inputString = Normalizer.normalize(rawInputString, Normalizer.Form.NFKC);
		StringBuilder builder = new StringBuilder(inputString.length());
		for (int ii=0; ii<inputString.length(); ++ii) {
			final char c = inputString.charAt(ii);
			// transform the character to its decomposed form (n, followed by ~)
			final String s = Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFD);
			// The stripped character will be the first character of the normalized string
			final char strippedChar = s.charAt(0);
			if (Character.getType(strippedChar)!=Character.NON_SPACING_MARK) {
				builder.append(strippedChar);
			}
		}
		return builder.toString();
	}

  /**
   * <p>Wrap a JTextArea in a JScrollPane, set up for handling normal text, optionally using a monospace font.
   * This turns on line-wrapping on word breaks, and gives the JTextArea a vertical scrollbar</p>
   * @param textArea The JTextArea to wrap 
   * @param monospace True if a monospace font is needed.
   * @return A JScrollPane that wraps the JTextArea
   */
	public static JScrollPane prepareForText(JTextArea textArea, boolean monospace) {
		Font originalFont = textArea.getFont();
		if (monospace) {
			Font monoFont = new Font(Font.MONOSPACED, originalFont.getStyle(), originalFont.getSize());
			textArea.setFont(monoFont);
		}
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);
    return new JScrollPane(textArea,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

  }

  /**
   * <p>Wrap a JTextArea in a JScrollPane, set up for handling normal text. This turns on line-wrapping on word
   * breaks, and gives the JTextArea a vertical scrollbar</p>
   * @param textArea The TextArea to wrap
   * @return A JScrollPane that wraps the JTextArea
   */
	public static JScrollPane prepareForText(JTextArea textArea) {
    return prepareForText(textArea, false);
	}

  public static JScrollPane prepareTable(JTable table) {
      return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  /**
   * <p>Attaches a text Label to a component. More formally, wraps a component inside a JPanel with a JLabel
   * showing the specified text and an optional Icon.</p>
   * @param component The component to text
   * @param text The text of the text
   * @param icon The optional icon, usually an ImageIcon, or null for no icon
   * @return A JPanel containing the label, optional icon, and the component
   */
  public static JPanel wrapWithLabel(JComponent component, String text, @Nullable Icon icon) {
    JPanel labelPanel = new JPanel(new BorderLayout());
    JLabel label = new JLabel(text);
    label.setIcon(icon); // This method accepts null values.
    labelPanel.add(label, BorderLayout.LINE_START);
    labelPanel.add(component, BorderLayout.CENTER);
    return labelPanel;
  }

  /**
   * <p>Gives the specified component a specific minimum width by wrapping inside a JPanel with a rigid horizontal
   * strut along the bottom.</p>
   * This is preferable to using setPreferredSize() because it leaves the height to its natural value.
   * @param component The component to stretch
   * @param width The revised width
   * @return A JPanel guaranteed not to be smaller than the specified width
   */
  public static JPanel stretchHorizontal(JComponent component, int width) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(component, BorderLayout.CENTER);
    panel.add(Box.createHorizontalStrut(width), BorderLayout.PAGE_END);
    return panel;
  }

  /**
   * <p>Returns a new JPanel that uses the FlowLayout, loaded with the specified components.</p>
   * @param components The components to add to the panel
   * @return A JPanel that uses the FlowLayout, with the specified components
   */
  public static JPanel loadFlowPanel(JComponent... components) {
    JPanel flowPanel = new JPanel(new FlowLayout());
    for (JComponent component : components) {
      flowPanel.add(component);
    }
    return flowPanel;
  }

  /**
   * <p>Quick way to create a JPanel with a BorderLayout and pack it with components. This has chainable
   * methods to set all five components.</p>
   * <p><strong>Example:</strong></p>
   * <pre>
   *   JPanel panel = Utils.loadBorderPanel()
   *       .center(mainContents)  // Adds mainContents at BorderLayout.CENTER
   *       .north(headerPanel)    // Adds headerPanel at BorderLayout.PAGE_START
   *       .south(buttonPanel);   // Adds buttonPanel at BorderLayout.PAGE_END
   * </pre>
   * @return A BorderPanel with chainable methods
   */
  public static BorderPanel loadBorderPanel() {
    return new BorderPanel();
  }

	/**
	 * <p>This returns an Iterable that iterates over the characters of a String.</p>
	 * <p>For most Strings, this is unnecessary, as you can easily iterate like this:</p>
	 * <pre>
	 *   String text = ...
	 *   for (char ch: text.toCharArray()) {
	 *     ...
	 *   }
	 * </pre>
	 * <p>However, for long Strings, this is inefficient, because the {@code toCharArray()} method first copies the 
	 * characters to a new array. For large Strings, or for heavy duty processing of many Strings, this method can
	 * improve performance:
	 * </p>
	 * <pre>
	 *   String text = ...
	 *   for (char ch: iter(text)) {
	 *     ...
	 *   }
	 * </pre>
	 * @param s The String
	 * @return an {@literal Iterable<Character>} for the String
	 */
	public static Iterable<Character> iterator(String s) {

		return new Iterable<>() {
			private int i = 0;

			@NotNull
			@Override
			public Iterator<Character> iterator() {
				return new Iterator<>() {

					@Override
					public boolean hasNext() {
						return i < s.length();
					}

					@Override
					public Character next() {
						if (!hasNext()) {
							throw new NoSuchElementException(String.format("Index = %d", i));
						}
						return s.charAt(i++);
					}
				};
			}
		};
	}

	// Drawing and Geometry utilities

	public static double toRad(double degrees) {
		return toRadians * degrees;
	}

	/**
	 * Returns an Affine Transform that will flip a shape across a ray or line through the origin, extending at the
	 * specified angle.
	 * @param degrees The angle from the X axis
	 * @return The Affine Transform that can flip the shape.
	 */
	public static AffineTransform getFlipOverRayDegreesInstance(double degrees) {
		final double radians = toRad(degrees);
		AffineTransform transform = AffineTransform.getRotateInstance(-radians);
		AffineTransform flipOverHorAxis = AffineTransform.getScaleInstance(1.0, -1.0);
		AffineTransform rotateBack = AffineTransform.getRotateInstance(radians);
		flipOverHorAxis.concatenate(transform);
		rotateBack.concatenate(flipOverHorAxis);
		return rotateBack;
	}

	public static Path2D toShape(Iterable<Point2D> collection) {
		Path2D shape = new Path2D.Double();
		Iterator<Point2D> iterator = collection.iterator();
		Point2D start = iterator.next();
		shape.moveTo(start.getX(), start.getX());
		while (iterator.hasNext()) {
			Point2D next = iterator.next();
			shape.lineTo(next.getX(), next.getY());
		}
		shape.closePath();
		return shape;
	}

	public static Path2D toShape(Point2D... pointArray) {
		return toShape(Arrays.asList(pointArray));
	}

	public static <T> T[] emptyArray() {
		//noinspection unchecked,SuspiciousArrayCast
		return (T[]) EMPTY;
	}

	public static <T> T[] emptyIfNull(T[] array) {
		return (array == null) ? emptyArray() : array;
	}
	
	public static void addTopRight(JComponent container, JComponent componentToAdd) {
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(componentToAdd, BorderLayout.PAGE_START);
		container.add(innerPanel, BorderLayout.LINE_END);
	}

	/**
	 * Get a font, which has a bold variant, from a list of font names. If none of the named fonts are found, or if 
	 * none have a bold variant, returns Dialog
	 *
	 * @param fontNameList a comma-delimited list of names for the font, in order
	 *                     of preference.
	 * @param style        The font style
	 * @param size         The font size
	 * @return The first font found from the list of font names. If none of the
	 * named fonts are found, returns Dialog
	 */
	@SuppressWarnings("SameParameterValue")
	public static Font getFont(
      String fontNameList,
      @MagicConstant(flags = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int style,
      int size
  ) {
		String[] fonts = fontNameList.split(",");
		for (String name : fonts) {
			Font f = new Font(name.trim(), style, size);
			// If the font wasn't found, the font name will be "Dialog".
			if (!"Dialog".equals(f.getFontName())) {
//				System.out.printf("Bold: %s%n psName: %s%n Name: %s%n fName: %s%n", f, f.getPSName(), f.getName(), f.getFontName()); // NON-NLS
//				System.out.printf("Looking for bold%n"); // NON-NLS
				Font boldF = f.deriveFont(Font.BOLD);
//				System.out.printf("Bold: %s%n psName: %s%n Name: %s%n fName: %s%n", boldF, boldF.getPSName(), boldF.getName(), boldF.getFontName()); // NON-NLS

				// If their names are different, this font has a true bold.
        if (!f.getFontName().equals(boldF.getFontName())) {
          return f;
        }
      }
		}
		return new Font("Dialog", style, size);
	}

  /**
   * <p>Load the icon from the graphic image (the resource) specified by the resource class and the resourcePath.</p> 
   * @param resourceClass The class giving the starting point of the specified resourcePath
   * @param resourcePath The path to the graphic image, relative to the class.
   * @return An ImageIcon containing the loaded image.
   */
  public static ImageIcon loadIcon(Class<?> resourceClass, String resourcePath) {
    URL url = resourceClass.getResource(resourcePath);
    Objects.requireNonNull(url, "Resource " + resourcePath + " not found");
    return new ImageIcon(url);
  }

  /**
   * <p>Load the icon, using a scaling factor, from the graphic image (the resource) specified by the resource
   * class and the resourcePath. The size of the resulting ImageIcon is the original size multiplied by
   * the scaling factor.</p>
   *
   * @param resourceClass The class giving the starting point of the specified resourcePath
   * @param resourcePath  The path to the graphic image, relative to the class.
   * @param scale The amount of scaling to apply.
   * @return An ImageIcon containing the loaded image.
   */
  public static ImageIcon loadIcon(Class<?> resourceClass, String resourcePath, float scale) {
    URL url = resourceClass.getResource(resourcePath);
    Objects.requireNonNull(url, "Resource " + resourcePath + " not found");
    return new ImageIcon(url) {
      @Override
      public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform savedTransform = g2.getTransform();
        System.out.printf("Scale: %4.2f%n", scale);  // NON-NLS
        g2.scale(scale, scale);
        super.paintIcon(c, g2, x, y);
        g2.setTransform(savedTransform);
      }

      @Override
      public int getIconWidth() {
        return Math.round(scale * super.getIconWidth());
      }

      @Override
      public int getIconHeight() {
        return Math.round(scale * super.getIconHeight());
      }
    };
  }

  /**
   * <p>Class to return a JPanel with a BorderLayout and chainable methods to add components with the specific
   * constraints. This class cannot be instantiated directly, but may be instantiated by calling the
   * {@code loadBorderPanel()} method.</p>
   * @see #loadBorderPanel() 
   */
  public static final class BorderPanel extends JPanel {
    private BorderPanel() {
      super(new BorderLayout());
    }
    
    public BorderPanel center(Component centerComponent) {
      add(centerComponent, BorderLayout.CENTER);
      return this;
    }
    
    public BorderPanel left(Component leftComponent) {
      add(leftComponent, BorderLayout.LINE_START);
      return this;
    }
    
    public BorderPanel right(Component rightComponent) {
      add(rightComponent, BorderLayout.LINE_END);
      return this;
    }
    
    public BorderPanel top(Component topComponent) {
      add(topComponent, BorderLayout.PAGE_START);
      return this;
    }
    
    public BorderPanel bottom(Component bottomComponent) {
      add(bottomComponent, BorderLayout.PAGE_END);
      return this;
    }
    
    public BorderPanel north(Component northComponent) { return top(northComponent); }
    public BorderPanel south(Component southComponent) { return bottom(southComponent); }
    public BorderPanel west(Component westComponent) { return left(westComponent); }
    public BorderPanel east(Component eastComponent) { return right(eastComponent); }
    public BorderPanel pageStart(Component pageStartComponent) { return top(pageStartComponent); }
    public BorderPanel pageEnd(Component pageEndComponent) { return bottom(pageEndComponent); }
    public BorderPanel lineStart(Component lineStartComponent) { return left(lineStartComponent); }
    public BorderPanel lineEnd(Component lineEndComponent) { return right(lineEndComponent); }
  }
}
