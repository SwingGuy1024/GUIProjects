package com.mm.gui.table;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

/**
 * Created using IntelliJ IDEA. Date: Nov 28, 2004 Time: 12:09:02 PM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

public class ToolTipTable extends JTable
{
	public ToolTipTable() { super(); }
  public ToolTipTable(TableModel mdl) { super(mdl); }

//  /**
//   * Installs the tool tip into the default renderer.
//   *
//   * @param renderer the <code>TableCellRenderer</code> to prepare
//   * @param row      the row of the cell to render, where 0 is the first row
//   * @param column   the column of the cell to render, where 0 is the first
//   *                 column
//   *
//   * @return the <code>Component</code> under the event location
//   */
//  public Component prepareRenderer(
//      TableCellRenderer renderer, int row, int column
//      )
//  {
//    JComponent component=(JComponent)super.prepareRenderer(renderer, row, column);
//    component.setToolTipText(getValueAt(row, column).toString());
//    return component;
//  }

  public boolean isToolTipEnabled(int row, int col) { return true; }

  /**
   * Sets the font of the tool tip to match the font of the table.
   * @return the <code>JToolTip</code> used to display this toolTip
   */
  public JToolTip createToolTip()
  {
    JToolTip toolTip=super.createToolTip();

    String tipText=toolTip.getTipText();
    if (tipText != null && tipText.length() == 0)
      return null;
    toolTip.setFont(getFont());
    return toolTip;
  }

  private MouseEvent lastEvent;
  private String lastToolTip;
  public String getToolTipText(MouseEvent e)
  {
    if (e == lastEvent)
      return lastToolTip;
    lastEvent = e;
    int row=rowAtPoint(e.getPoint());
    int col=columnAtPoint(e.getPoint());

    if (row>= 0 && col>= 0)
    {
      if (isToolTipEnabled(row, col))
      {
        Object obj=getValueAt(row, col);

        if (obj==null)
        {
          lastToolTip = null;
          return null;
        }
        String txt=obj.toString();
        if (txt.equals(""))
        {
          lastToolTip = null;
          return null;
        }
        else
        {  // Get the actual rendered text, to compare it to the full text
          Rectangle ir=new Rectangle();
          Rectangle tr=new Rectangle();
          Rectangle vr=getCellRect(row, col, false);

          if (isJdk13())
            vr.width-=2;
          else
            vr.width-=4;  // I don't know why I have to do this. It may be because of a border.

          String displayText=SwingUtilities.layoutCompoundLabel(getFontMetrics(getFont()),
                                                                txt, null, 0, 0, 0, 0, vr, ir, tr, 0);
          if (displayText.equals(txt))
          {
            lastToolTip = null;
            return null;  // no tool tip needed, all text is visible
          }
          lastToolTip = txt;
          return txt;
        }
      }
      else
      {
        lastToolTip = super.getToolTipText(e);
        return lastToolTip;
      }
    }
    else
    {
      lastToolTip = null;
      return null;
    }
  }

  public Point getToolTipLocation(MouseEvent e)
  {
    int row=rowAtPoint(e.getPoint());
    int col=columnAtPoint(e.getPoint());

    //Mouse may have moved off the Table area
    if (row>= 0 && col>= 0)
    {
      Object obj=getValueAt(row, col);

      if (obj==null)
        return null;
      /*
       else if (obj.toString().equals(""))
         return null;
      */
   
      Point p=getCellRect(row, col, true).getLocation();
      if (isJdk13())
        p.translate(-3, -2);
      else
        p.translate(-1, -2);
      return p;
    }
    else
      return null;
  }
  
  private static boolean sJdkFound=false;
  private static boolean is13;
  private static boolean isJdk13()
  {
    if (!sJdkFound)
    {
      is13 = System.getProperty("java.version").compareTo("1.3") >= 0;
			System.out.println("jdk1.3 = " + is13);
      sJdkFound = true;
    }
    return is13;
  }
}
