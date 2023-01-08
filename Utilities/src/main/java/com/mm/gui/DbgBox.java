/*
 * Copyright (c) 2004 e-Aggregator, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * e-Aggregator, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with e-Aggregator, Inc.
 *
 * E-AGGREGATOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. E-AGGREGATOR SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * Date: Dec 1, 2004
 * Time: 9:49:41 AM
  */
package com.mm.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 * Class to help you debug your Box. This class is usually very simple to use.
 * If you only used the add(Component) method to add components to your Box,
 * you can simply replace the constructor call for your troublesome box 
 * with this box, as follows.
 * <p>
 * Just replace this:
 * <pre>    Box myPanel = new Box(BoxLayout.Y_AXIS); </pre>
 * <p>With this:<p>
 * 
 *    <pre>Box myPanel = new DbgBox(BoxLayout.Y_AXIS); </pre>
 * <p>
 * Every component you place in the box will get a color-coded border. Red
 * borders mean the alignment is set to 1.0, and blue borders mean 0.0. 
 * Anything inbetween will get a color somewhere between red and blue. So 
 * a purple border indicates an alignment of 0.5.
 * <p>
 * If you place boxes within boxes, be sure to substitute all Boxes with 
 * DbgBoxes.
 * <p>
 * If you used the BoxLayout on some other component, you can still add
 * colored borders to all your components using the <code>static
 * colorAllComponents()</code> method.  
 * @author Miguel Mu\u00f1oz
 */
public class DbgBox extends Box
{
  // Blue means 0
  // Red means 1
  int mAlign;
  public DbgBox(int align) { super(align); mAlign = align; }

  public Component add(Component comp)
  {
    colorComponent(comp, mAlign);
    return super.add(comp);
  }
    
  /**
   * Static method to add a color-coded border to each JComponent in the
   * Box. This may also be used for other components that use the BoxLayout.
   * @param comp
   * @param axis
   */ 
  public static void colorComponent(Component comp, int axis)
  {
    float align = axis==BoxLayout.X_AXIS? comp.getAlignmentY() : comp.getAlignmentX();
    int r = (int)(align*255);
    int b = (int)((1.0f-align)*255);
    Borders.addBorder((JComponent)comp, new LineBorder(new Color(r, 0, b)));
  }
    
  /**
   * Static method to color all components of a Container that uses the 
   * BoxLayout. Call this method after all children have been added.
   * @param parent  The compoent that uses the BoxLayout.
   * @param axis    Either X_AXIS or Y_AXIS. Specify the direction of the
   * BoxLayout.
   */ 
  public static void colorAllComponents(JComponent parent, int axis)
  {
    for (int ii=0; ii<parent.getComponentCount(); ++ii)
      colorComponent(parent.getComponent(ii), axis);
  }
//    
//  /**
//   * This adds the specified border to the outside of the component,
//   * without removing any existing border. (This is generally useful outside
//   * of this class.)
//   * @param cmp
//   * @param brdr
//   */ 
//  public static void addBorder(JComponent cmp, Border brdr)
//  {
//    if (cmp.getBorder() == null)
//      cmp.setBorder(brdr);
//    else
//      cmp.setBorder(new CompoundBorder(brdr, cmp.getBorder()));
//  }
}
