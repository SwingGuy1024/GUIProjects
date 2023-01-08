package com.mm.gui;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Hashtable;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel
 * Date: Jul 13, 2005
 * Time: 11:46:24 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Base class implementing all Enum features.
 * <p><i>
 * This class comes from Dr. Dobbs October 1999 issue, P 113, in the
 * Q&A column, but Evan Easton </i></p><p><i>
 * It's an answer to java's lack of an enum keyword. According to the
 * article, this class handles serialization well. The == operator is
 * supported even after serialization. This uses features introduced in
 * JDK 1.2</i></p>
 * <p>
 * NOTE: This class requires JDK 1.2 / Java 2 to work properly.  It is
 * recommended that you compile it with the JDK 1.2 option -target 1.2
 * to prevent its accidental use in Java 1.1 environments.
 * </p><p>
 * A subclass need only have a private ctor passing this class
 * the distinct id for each declared enum instance and the public static
 * declarations for the enums.
 * </p><p>
 * This class is not implemented to allow aliasing of multiple Enum
 * instances to the same id value.
 * </p><p>
 * Here is an example of how to create an enumerated type:
 * <pre>
 *   public class TableType extends Enum
 *   {
 *     public static final TableType createSlr  = new TableType(0);
 *     public static final TableType editSlr    = new TableType(1);
 *     public static final TableType offLineTbl = new TableType(2);
 * 
 *     private TableType(int val) { super(val); }
 *   }
 * </pre>
 * <p>
 * I have since added support for optional String representations of
 * the enumerated values. These are obtained by the toString() method.
 * -- Miguel Mu&ntilde;oz
 * </p><p>
 * Here is an example of an enumerated type with strings:
 * <pre>
 *   public class TableType extends Enum
 *   {
 *     public static final TableType createSlr  = new TableType(0, "Create SLR");
 *     public static final TableType editSlr    = new TableType(1, "Edit SLR");
 *     public static final TableType offLineTbl = new TableType(2, "Off-Line Table");
 * 
 *     private TableType(int val, String name) { super(val, name); }
 *   }
 * </pre>
 *
 * @author R Evan Easton
 */
public class EarlyEnum
  implements
  Serializable,
  Comparable<EarlyEnum>
{
  private static final Hashtable subclassInstanceDictionary__ = new Hashtable(5, 0.75f);
  private int id_;
  private transient String myName;

  /**
   * Construct an instance and index it in an instance dictionary that
   * is separate for each subclass.
   *
   * @param id a distinct id to distinguish Enums from each other per
   *           subclass.
   * @throws DuplicateEnumIdException if another Enum has already
   *                                  been declared with the specified id for the concrete subclass
   */
  protected EarlyEnum(int id)
    throws DuplicateEnumIdException
  {
    id_ = id;

    Hashtable instance_dictionary = (Hashtable) getInstanceDictionaryFor(getClass());
    synchronized (instance_dictionary)
    {
      Integer obj_id = new Integer(id);
      if (instance_dictionary.containsKey(obj_id))
        throw new DuplicateEnumIdException(getClass(), obj_id);

      instance_dictionary.put(obj_id, this);
    }
  }

  /**
   * Construct a named instance and index it in an instance dictionary
   * that is separate for each subclass.
   *
   * @param id   A distinct id to distinguish Enums from each other per
   *             subclass.
   * @param name A String name for the value, returned by toString().
   * @throws DuplicateEnumIdException if another Enum has already
   *                                  been declared with the specified id for the concrete subclass
   */
  protected EarlyEnum(int id, String name)
    throws DuplicateEnumIdException
  {
    this(id);
    myName = name;
  }

  protected void setName(String theName)
  {
    myName = theName;
  }

  /**
   * Get the instance dictionary for the specified class.
   *
   * @return always returns a non-null reference
   */
  private static synchronized Hashtable getInstanceDictionaryFor(Class cls)
  {
    Hashtable ret = (Hashtable) subclassInstanceDictionary__.get(cls);
    if (ret == null)
    {
      ret = new Hashtable(5, 0.75f);
      subclassInstanceDictionary__.put(cls, ret);
    }
    return ret;
  }

  /**
   * The unique object identity for this Enum instance within its subclass.
   * <P><FONT COLOR="#cc0000">
   * WARNING: Since it is public others can get the int value.  Therefore,
   * this should be used only after careful consideration.
   * </FONT></P>
   */
  public final int asInt()
  {
    return id_;
  }

  /**
   * Resolve the this instance to one of the unique instances in the instance dictionary.
   *
   * @throws NonExistentEnumException if the declared instance based on the id value cannot be found in the subclass's instance dictionary
   */
  protected Object readResolve()
    throws NonExistentEnumException
  {
    EarlyEnum ret = getInstance(asInt(), getClass());

// if it doesn't exist throw an exception
    if (ret == null)
      throw new NonExistentEnumException(getClass(), new Integer(asInt()));

    return ret;
  }

  /**
   * Gets the Enum for a given integer.
   */
  public static final EarlyEnum getInstance(int val, Class theClass)
  {
    Hashtable instance_dictionary = (Hashtable) getInstanceDictionaryFor(theClass);

    // look up the already registered enum
    return (EarlyEnum) instance_dictionary.get(new Integer(val));
  }

  /**
   * The hashcode will be the hash code of simply concatenating the id and class name together.
   * <P>
   * It is marked final to prevent a subclass from breaking the method because of faulty assumptions.
   */
  public final int hashCode()
  {
    String tmp = "" + asInt() + ":" + getClass().getName();
    return tmp.hashCode();
  }

  /**
   * Value-wise comparison.
   * This degenerates to reference equality because all instances
   * are resolved to the declared instances even in deserialization.
   * Try un-commenting the value compare code to see.
   * <P>
   * It is marked final to prevent a subclass from breaking the method because of faulty assumptions.
   */
  public final boolean equals(Object obj)
  {
    boolean ret = (obj == this);

//		if(!ret && obj != null && obj.getClass() == getClass())
//			ret = ((Enum)obj).asInt() == asInt();

    return ret;
  }

  /**
   * Returns the name, if this it has one. [All this does is call toString()]
   */
  public String getName()
  {
    return toString();
  }

  /**
   * Return a String representation of the enumerated value. If the
   * sub class was created with the named initializer, this returns
   * that name. Otherwise, this returns the class name, followed by the
   * integer value in parentheses.
   */
  public String toString()
  {
    if (myName != null)
      return myName;
    return getClass().getName() + " (" + id_ + ")";
  }

  /**
   * Implements the Comparable interface
   */
  @Override
  public int compareTo(@NotNull EarlyEnum other)
  {
    return asInt() - ((EarlyEnum) other).asInt();
  }

  public static class DuplicateEnumIdException
    extends RuntimeException
  {
    /**
     * Non-public ctor that only Enum really needs
     */
    DuplicateEnumIdException(Class cls, Object id)
    {
      super("An enum for class " + cls.getName() + " is already registered for id " + id + ".");
    }
  }

  public static class NonExistentEnumException
    extends InvalidObjectException
  {
    /**
     * Non-public ctor that only Enum really needs
     */
    NonExistentEnumException(Class cls, Object id)
    {
      super("The enum for class " + cls.getName() + " and id " + id + " no longer exists but was found during deserialization.");
    }
  }
}
