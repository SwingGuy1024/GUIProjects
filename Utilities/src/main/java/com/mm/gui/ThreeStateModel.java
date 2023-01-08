package com.mm.gui;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel
 * Date: Jul 13, 2005
 * Time: 11:43:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreeStateModel extends EarlyEnum
{
  public static final int ifalse          = 0;
  public static final int itrue           = 1;
  public static final int iunknown        = 2;

  public static final ThreeStateModel FALSESTATE = new ThreeStateModel(ifalse,   "False");
  public static final ThreeStateModel TRUESTATE  = new ThreeStateModel(itrue,    "True");
  public static final ThreeStateModel UNDECIDED  = new ThreeStateModel(iunknown, "Undecided");

  public static ThreeStateModel getState(int val)
  {
    switch (val)
    {
      case ifalse:
        return FALSESTATE;
      case itrue:
        return TRUESTATE;
      case iunknown:
        return UNDECIDED;
      default:
        throw new IllegalArgumentException("ThreeStateModel enumeration value out of range: " + val + " (Range from 0 to 2)");
    }
  }

  private ThreeStateModel(int val, String name) { super(val, name); }
}
