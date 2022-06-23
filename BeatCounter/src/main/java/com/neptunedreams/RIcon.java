package com.neptunedreams;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * This class loads fixed application ImageIcons from resource files. It also 
 * encapsulates the troublesome task of specifying image resources, to ensure
 * that they can be read from either a jar file or a directory. This is because
 * the standard way of specifying the file is a potential source for the worst
 * kind of bug -- A bug that only shows up in testing (after the code has been
 * jarred), and can't be reproduced by development.<p>
 * Here's why:
 * <br>
 * This code: {@code new ImageIcon("picture.gif"))} will execute properly
 * from a directory, but it won't work from a jar file. <p>
 * But if we say 
 * {@code new ImageIcon(getClass().getResource("picture.gif)) }
 * the code will run fine from both a directory and a .jar file.
 * <p>
 * This class encapsulates the call to {@code getResource()}, so we can't make 
 * this mistake. <p>
 * When specifying an icon from a resource file, always use RIcon instead of 
 * ImageIcon, and your file is guaranteed to be found.
 */
 
public class RIcon extends ImageIcon
{
	private final URL mUrl;
  /**
   * Make an RIcon from the file specified by the raw file name, 
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
  public RIcon(Res pRes, String rawFilename, String description) {
	  super();
	  setImage(pRes.getImage(rawFilename));
	  setDescription(description);
	  mUrl = getUrl(pRes.getClass(), rawFilename);
  }

  /**
   * Make an RIcon from the file specified by the raw file name.
   * The first parameter, which is class Res or a subclass, specifies 
   * the path to the file.
   *
   * @param pRes The resource class, which determines the path to the file name.
   * @param rawFilename The base name of the file without the path. For 
   * simplicity, the path will be that of the resource class specified by pRes.
   * @see Res
   */
  public RIcon(Res pRes, String rawFilename) {
	  super();
	  setImage(pRes.getImage(rawFilename));
	  setDescription(pRes.getURL(rawFilename).toExternalForm());
	  mUrl = getUrl(pRes.getClass(), rawFilename);
  }

	/**
	 * Construct an RIcon from a raw file name. The file's path is assumed to be
	 * relative to this class. (This class may be subclassed, in which the path
	 * will be relative to the subclass.)
	 * @param rawFileName   The path to the icon file, relative to {@code this}
	 * class.
	 */
  public RIcon(String rawFileName) {
	  super();
	  setImage(getImage(rawFileName));
	  mUrl = getUrl(getClass(), rawFileName);
	  setDescription(Res.getURL(getClass(), rawFileName).toExternalForm());
  }

	/**
	 * Construct an RIcon from a path and a class. The path of the icon file
	 * is relative to the the specified class
	 * @param rawFileName  The path to the icon file, relative to the specifed
	 * class.
	 * @param baseClass    The class from which the specified path is relative.
	 */
  public RIcon(String rawFileName, Class<?> baseClass) {
	  super();
	  setImage(getImage(rawFileName, baseClass));
	  mUrl = getUrl(baseClass, rawFileName);
  }

	/**
	 * Get an image from a filename, relative to this class. {@code this}
	 * may refer to a subclass.
	 * @param pFileName File name
	 * @return the Image in the specified file.
	 */
  public Image getImage(String pFileName) {
    return getImage(pFileName, getClass());
  }
	
	public URL getUrl() { return mUrl; }

	/**
	 * Returns the image in the file, relative to the specified class.
	 * @param pRawFileName The path to the icon data, relative to the specified
	 * class.
	 * @param pSource The class from which the specified path is relative.
	 * @return The image specifed by the file.
	 */
  public static Image getImage(String pRawFileName, Class<?> pSource)
  {
	  URL theRes = getUrl(pSource, pRawFileName);
	  return (Toolkit.getDefaultToolkit().getImage(theRes));
  }

	private static URL getUrl(Class<?> pSource, String pRawFileName) {
		return Res.getURL(pSource, pRawFileName);
	}
}
