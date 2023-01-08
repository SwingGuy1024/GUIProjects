package com.mm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;

/**
 * Created by IntelliJ IDEA.
 * User: Miguel
 * Date: Jul 13, 2005
 * Time: 11:50:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreeStateButton extends JCheckBox
{
  private Icon     myUndecidedIcon;
  private Icon     myTrueIcon;
  private Icon     myFalseIcon;
  private ThreeStateModel mStateModel;
  private boolean  myAllowUndecided=true;

  private static final Icon defFalseIcon     = UIManager.getIcon("CheckBox." + "icon");
  private static final Icon defTrueIcon = defFalseIcon;
  private static final Icon defUndecidedIcon = makeQuestionIcon();

  private static Icon myQuestionIcon = new UndecidedIcon();
  private static Icon makeQuestionIcon()
  {
    return new Icon()
    {
      public void paintIcon(Component comp, Graphics gr, int xx, int yy)
      {
        ThreeStateButton btn = (ThreeStateButton) comp;
        btn.myFalseIcon.paintIcon(comp, gr, xx, yy);
        if (btn.getTriState() == ThreeStateModel.UNDECIDED)
          myQuestionIcon.paintIcon(comp, gr, xx, yy);
      }

      public int getIconWidth()  { return defFalseIcon.getIconWidth(); }
      public int getIconHeight() { return defFalseIcon.getIconHeight(); }
    };
  }

  private List myListeners;

  public ThreeStateButton()
  {
    this(defTrueIcon, defFalseIcon, defUndecidedIcon);
  }

  public ThreeStateButton(String name)
  {
    this();
    setText(name);
  }

  public ThreeStateButton(String name, Icon trueIcon, Icon falseIcon, Icon undecidedIcon)
  {
    this(trueIcon, falseIcon, undecidedIcon);
    setText(name);
  }

  public ThreeStateButton(Icon trueIcon, Icon falseIcon, Icon undecidedIcon)
  {
    super(falseIcon);
    myTrueIcon =      trueIcon;
    myFalseIcon =     falseIcon;
    myUndecidedIcon = undecidedIcon;
    setState(ThreeStateModel.FALSESTATE);

    setHorizontalAlignment(LEFT);

    ActionListener clickEar = new ClickListener();
    super.addActionListener(clickEar);
  }

  public void setState(int val) { setState(ThreeStateModel.getState(val)); }
  public void setState(ThreeStateModel pStateModel)
  {
    mStateModel = pStateModel;
    if (pStateModel == ThreeStateModel.TRUESTATE)
      setIcon(myTrueIcon);
    else if (pStateModel == ThreeStateModel.FALSESTATE)
      setIcon(myFalseIcon);
    else
      setIcon(myUndecidedIcon);
    setSelected(pStateModel == ThreeStateModel.TRUESTATE);
  }

  public int getState() { return mStateModel.asInt(); }
  public ThreeStateModel getTriState() { return mStateModel; }

  public void addActionListener(ActionListener ear)
  {
    if (myListeners == null)
      myListeners = new LinkedList();
    myListeners.add(ear);
  }

  public void removeActionListener(ActionListener ear)
  {
    myListeners.remove(ear);
  }

  private class ClickListener implements ActionListener
  {
    public void actionPerformed(ActionEvent evt)
    {
      if (mStateModel == ThreeStateModel.TRUESTATE)
        setState(ThreeStateModel.FALSESTATE);
      else if (mStateModel == ThreeStateModel.FALSESTATE)
      {
        if (allowUndecided())
          setState(ThreeStateModel.UNDECIDED);
        else
          setState(ThreeStateModel.TRUESTATE);
      }
      else
        setState(ThreeStateModel.TRUESTATE);

      if (myListeners != null)
      {
        ListIterator li = myListeners.listIterator();
        while (li.hasNext())
        {
          ActionListener al = (ActionListener)li.next();
          if (al != null)
            al.actionPerformed(evt);
        }
      }
    }
  }

  private boolean allowUndecided()
  {
    return myAllowUndecided;
  }

  public void setAllowUndecided(boolean allow)
  {
    myAllowUndecided = allow;
  }

  public static class UndecidedIcon
    implements Icon
  {
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      JCheckBox cb = (JCheckBox) c;

      Color qColor;
      if(cb.isEnabled()) {
        qColor = UIManager.getColor("CheckBox.darkShadow");
      } else {
        qColor = UIManager.getColor("CheckBox.shadow");
      }
      if (qColor == null)
        qColor = Color.black;
      g.setColor(qColor);

      g.drawLine(x+5, y+3, x+8,  y+3);
      g.drawLine(x+4, y+4, x+5,  y+4);
      g.drawLine(x+8, y+4, x+9,  y+4);
      g.drawLine(x+4, y+5, x+5,  y+5);
      g.drawLine(x+8, y+5, x+9,  y+5);
      g.drawLine(x+7, y+6, x+8,  y+6);
      g.drawLine(x+6, y+7, x+7,  y+7);
      g.drawLine(x+6, y+8, x+7,  y+8);
      g.drawLine(x+6, y+10, x+7, y+10);
    }

    public int getIconWidth() { return 13; }
    public int getIconHeight() { return 13; }
  }
}
