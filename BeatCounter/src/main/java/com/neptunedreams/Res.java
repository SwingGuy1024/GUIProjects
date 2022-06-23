package com.neptunedreams;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/**
 * This class encapsulates the ResourceBundle and provides many convenience 
 * methods to load the resources.
 * It assumes the resources are in the same package as this class. Subclasses
 * assume resources are in the subclass package. When subclassing this, it is
 * advisable to only allow a single instance of the class, to be shared by any
 * class that needs the resources. Here is an example of a subclass:
 * <pre>
 * public class GuiRes extends Res
 * {
 *   private GuiRes() { }
 *   static public final Res sResource = loadRes(new GuiRes(), "Stock");
 * }</pre>
 * This would load a ResourceBundle from a file called "Stock.properties", 
 * located in the same directory as the {@code GuiRes.class}.
 * <p>
 * Classes would use this by declaring a static instance, like this:
 * 
 * <pre>   Res res = GuiRes.sResource; </pre>
 * <p>
 * Any method that needs to read a resource would do this:
 * {@code String dragonName = res.get("dragonKey");}
 * <p>
 * Alternatively, if the ResourceBundle was loaded externally, a resource could
 * be read like this:
 * <p>
 * {@code String dragonName = Res.get(bndl, "dragonKey"); // bndl is the ResourceBundle }
 * <p>
 * If the resource doesn't exist in the file, this retuns an error string, 
 * instead of throwing an exception. It also prints a message to System.out. 
 * This way, a missing resource won't prevent the applicaton from running, but
 * users will see the error message where the resource was supposed to be.
 * <p>
 * This class makes use of the "raw" name of a file. That is the base
 * name, without the preceeding path or the extension. 
 * The path for the raw name starts with 
 * the location of the subclass of this class. So for a Res subclass called
 * {@code com.mystuff.beasts.NameRes}, and a resource file with a path of
 * {@code com/mystuff/beasts/images/Names.properties}, the raw name would be
 * {@code "images/Names".}
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"unused", "UseOfSystemOutOrSystemErr", "HardCodedStringLiteral", "MagicCharacter", "StringConcatenation"})
public class Res
{
//  private static Class<?>    sDefaultClass=Res.class;
  private ResourceBundle  mResBndl;

  protected Res() { }
  public Res(ResourceBundle pRB) { mResBndl = pRB; }
  public static final Res mInstance;
  
  static
  {
	  // If this doesn't work, be sure the "Strings" file was copied to the output directory.
    mInstance = loadRes(new Res(), "Strings"); // NON-NLS
  }
  
  public static Res loadRes(Res pRes)
  {
    return pRes;
  }
  
  /**
   * Loads the ResourceBundle into the instance of this class or a 
   * subclass.
   * @param pRes          The Res subclass from which the file path will begin. 
   * @param pRawFileName  The raw file name, measured from the folder 
   * containing the subclass of Res in {@code pRes}.
   * @return The same resource file passed in as the first parameter.
   */ 
  public static Res loadRes(Res pRes, String pRawFileName)
  {
    try
    {
      pRes.mResBndl = (getBundle(pRes.getClass(), pRawFileName)); // Allows us to move this class around, if needed
      return pRes;
    }
    catch (MissingResourceException e)
    {
      System.err.println(e.getMessage());
      System.err.printf("Resource load attempted from %s%n", pRes.getClass());
      System.err.println(getCall());
      e.printStackTrace();
      throw e;
    }
  }
  
  /**
   * Returns the full name of a resource bundle, with the proper package
   * prepended to the short name. For example, if the short name is "Text",
   * and the Resource class is com.mypackage.ui.Resources, then calling 
   * <br>{@code  getBundle(Resources.class, "Text")}<br>
   * will return the bundle for {@code com/mypackage/ui/Text.properties}
   * @param pClass  The class with the resource file in the same package
   * @param pName   The short name of the resource file
   * @return        The resource bundle for the specified file.
   */ 
  private static ResourceBundle getBundle(Class<?> pClass, String pName)
  {
    String base;
    base=extractPackage(pClass);
    base = String.format("%s.%s", base, pName); // NON-NLS
    return ResourceBundle.getBundle(base);
  }

  /**
   * Extracts the package string from the given class. For example, if the
   * class is com.mystuff.widgets.Widget, this will return "com.mystuf.widgets"
   * @param pClass The class
   * @return A string describing the package containing the class
   */ 
  public static String extractPackage(Class<?> pClass)
  {
    Package pkg=pClass.getPackage();
    // for some reason, getPackage sometimes returns null. I have no idea why.
    // So I test for that case.
    if (pkg == null)
    {
      String classString = pClass.getName();
      int lastDot = classString.lastIndexOf('.');
      return classString.substring(0, lastDot);
    }
    else {
      return pkg.getName();
    }
  }
  
  /**
   * Determines if the specified file actually exists.
   * @param rawFileName The raw name of the file.
   * @return true if the file exists, false otherwise.
   */ 
  public boolean resExists(String rawFileName)
  {
    return getURLInt(this.getClass(), rawFileName) != null;
  }

  /**
   * Loads the image from a graphics file. You may call this directly, 
   * or you may instantiate ResIcon. Instantiating ResIcon is the recommended way 
   * to load an Image from a file and represent it as an Icon.
   * @param fileName  The name of the file.
   * @return          The Image in the file.
   */ 
  Image getImage(String fileName)
  {
    // we call the getClass() method so that subclasses will pass their own 
    // class in as the parameter, instead of this one.
    return getTkImage(fileName, getClass());
  }
  
  public static Image getTkImage(String pRawFileName, Class<?> pSource)
  {
    URL theRes = Res.getURL(pSource, pRawFileName);
    return Toolkit.getDefaultToolkit().getImage(theRes);
  }
  
  public ImageIcon getIcon(String pFileName) { return new ResIcon(pFileName); }
  public ImageIcon getIcon(String pFileName, String pDesc) { return new ResIcon(this, pFileName, pDesc); }
  public ImageIcon getIcon(String pFileName, String pDesc, Res pRs) { return new ResIcon(pRs, pFileName, pDesc); }
	public static ImageIcon getIcon(String pFileName, Class<?> baseClass) { return new ImageIcon(getTkImage(pFileName, baseClass), pFileName); }
  static URL getURL(Class<?> pClass, String fileName)
  {
    URL theRes = getURLInt(pClass, fileName);
    if (theRes == null) {
      throw new MissingResourceException("Res Error -- File not found: " + fileName + " (" + pClass + ')',  pClass.getName(), fileName);
    }
    return theRes;
  }
  
  private static URL getURLInt(Class<?> pClass, String pName)
  {
    return pClass.getResource(pName);
  }
  
  URL getURL(String pRawName) { return getURL(this.getClass(), pRawName); }

  private static String             getCall() { return "\tat " + getCaller(); }
  private static StackTraceElement  getCaller()
  {
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    boolean found = false;
    boolean foundRes = false;
    for (int ii=0; !found & ii<trace.length; ++ii)
    {
      if (foundRes)
      {
        if (!trace[ii].getClassName().equals(Res.class.getName())) {
          return trace[ii];
        }
      }
      else if (trace[ii].getClassName().equals(Res.class.getName())) {
        foundRes = true;
      }
    }
    throw new IllegalStateException("Stack Trace Element missing");
  }
  
  /**
   * Gets the String from the internal ResoureBundle. 
   * If the resource doesn't exist, prints a message to System.err, and
   * returns an error message in the string.
   * This way, a missing resource won't prevent the application from running. 
   * @param pKey  The key
   * @return      The string resource, or an error message if the resource
   * wasn't found
   */ 
  public String get(String pKey)
  {
    return get(mResBndl, pKey);
  }
  
  /**
   * Gets the String from the specified ResourceBundle.
   * If the resource doesn't exist, prints a message to System.err, and
   * returns an error message in the string.
   * This way, a missing resource won't prevent the application from running. 
   * @param pRes  The ResourceBundle
   * @param pKey  The key
   * @return      The string resource, or an error message if the resource
   * wasn't found
   */ 
  public static String get(ResourceBundle pRes, String pKey)
  {
    try { return pRes.getString(pKey); }
    catch (MissingResourceException e)
    {
      String m1 = "\u25ba Missing Resource: " + pKey;
      String m2 = getCall();
      final String msg=m1 + '\n' + m2;
      System.err.println(msg);
      return m1 + m2;
    }
  }
  
  /**
   * Reads an resource from a resource file, and returns its value as an int.
   * So if the resource file contained this line: <br>
   * {@code number = 3247}<br>
   * The call {@code int nmb = getInt("number");} would set nmb to 3247.
   * @param   pKey The resource key
   * @return  The int
   * @throws  NumberFormatException Not likely
   */ 
  public int getInt(String pKey) { return Integer.parseInt(get(pKey)); }
  
  /**
   * Returns an enumeration of all the keys in the resource bundle.
   * This class just delegates to the ResourceBundle.
   * @return the enumeration of all keys.
   */ 
  public Enumeration<String> getKeys() { return mResBndl.getKeys(); }

  public static String htmlWrap(String tag, String txt)
  {
    return htmlWrap(tag, new StringBuilder(txt)).toString();
  }
  
  public static StringBuilder htmlWrap(String tag, StringBuilder buf)
  {
//    StringBuilder buf = new StringBuilder(txt.length() + 2*tag.length() + 5);
    buf.insert(0, '>').insert(0, tag).insert(0, '<');
    buf.append("</").append(tag).append('>');
    return buf;
  }
  
  public static String makeHtml(StringBuilder buf)
  {
    return htmlWrap("html", htmlWrap("body", buf)).toString();
  }

  public static String makeHtml(String txt)
  {
    return htmlWrap("html", htmlWrap("body", txt));
  }
  
  @SuppressWarnings("NestedAssignment")
  public static StringBuilder convertLineBreaks(StringBuilder txt)
  {
    int nonBreakSpace;
    while ((nonBreakSpace=txt.indexOf("\n "))>=0) {
      txt.replace(nonBreakSpace, nonBreakSpace+2, "\n&nbsp;");
    }
    while ((nonBreakSpace=txt.indexOf("&nbsp; "))>=0) {
      txt.replace(nonBreakSpace, nonBreakSpace+7, "&nbsp;&nbsp;");
    }
    while ((nonBreakSpace=txt.indexOf("\n"))>=0) {
      txt.replace(nonBreakSpace, nonBreakSpace+1, "<br>");
    }
    return txt;
  }
  public static String toXmlData(String pTxt)
  {
    return toXmlData(new StringBuffer(pTxt)).toString();
  }
  
  public static StringBuffer toXmlData(StringBuffer pTxt)
  {
    pTxt.insert(0, "<![CDATA[");
    pTxt.append("]]>");
    return pTxt;
  }

	/**
	 * I'm not sure yet if this just duplicates the functionality of RIcon. If so,
	 * I should remove it.
	 */
  private final class ResIcon extends ImageIcon
  {
    /**
     * Make an ResIcon from the file specified by the raw file name, 
     * and a description.
     * <p>
     * The first parameter, which is class Res or a subclass, specifies 
     * the path to the file.
     *
     * @param pRes The resource class, which determines the path to the file name.
     * @param rawFilename The base name of the file without the path. For 
     * simplicity, the path will be that of the resource. 
     * @param description The description.
     */
    private ResIcon(Res pRes, String rawFilename, String description) {
      super();
      setImage(pRes.getImage(rawFilename));
      setDescription(description);
    }

    /**
     * Make an ResIcon from the file specified by the raw file name.
     * The first parameter, which is class Res or a subclass, specifies 
     * the path to the file.
     *
     * @param pRes The resource class, which determines the path to the file name.
     * @param rawFilename The base name of the file without the path. For 
     * simplicity, the path will be that of the resource. 
     * @see Res
     */
    private ResIcon (Res pRes, String rawFilename) {
      super();
      setImage(pRes.getImage(rawFilename));
      setDescription(pRes.getURL(rawFilename).toExternalForm());
    }
  
    private ResIcon(String rawFileName)
    {
      this(Res.this, rawFileName);
//      setImage(getImage(rawFileName));
//      setDescription(Res.getURL(getClass(), rawFileName).toExternalForm());
    }
  }
  
}
