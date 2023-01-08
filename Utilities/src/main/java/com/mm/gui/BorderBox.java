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
 * Date: Nov 30, 2004
 * Time: 11:08:53 PM
  */
package com.mm.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Class to substitute for the Box class & the BoxLayout. This does 
 * pretty much the same thing without using the BoxLayout, and thus avoiding
 * any Box weirdness. You no longer need to worry about the AlignmentX or Y,
 * and you don't need to set preferred sizes. Everything expands to fill the
 * box width or height, and aligns according to the older 
 * setHorizontal/VerticalAlignment.
 * <p>All components added to this must use the add() method that takes
 * only a single parameter, because that's the only one that has been 
 * overridden to work with this class. You really don't need the other ones
 * anyway.
 * @author Miguel Mu\u00f1oz
 */
public class BorderBox extends JPanel
{
  /**
   * Matchs the value of X_AXIS in BoxLayout.
   */ 
  public static int X_AXIS = BoxLayout.X_AXIS;
  /**
   * Matchs the value of Y_AXIS in BoxLayout.
   */ 
  public static int Y_AXIS = BoxLayout.Y_AXIS;

  private String mDirection;
  private JPanel mLastAddedPanel;
  /**
   * Create a BorderBox aligned in the specified direction.
   * @param axis
   */ 
  public BorderBox(int axis)
  {
    super(new BorderLayout());
//    assert axis==X_AXIS || axis==Y_AXIS : "Illegal Value of " + axis + " for axis";
    if (X_AXIS==axis)
      mDirection = BorderLayout.WEST;
    else
      mDirection = BorderLayout.NORTH;
    mLastAddedPanel = this;
  }

  /**
   * Adds the component to the Box, packing it into the North or West side,
   * depending on the orientation specified in the constructor.
   */
  public Component add(Component comp)
  {
    mLastAddedPanel.add(comp, mDirection);
    JPanel insertPanel = new JPanel(new BorderLayout());
    mLastAddedPanel.add(insertPanel, BorderLayout.CENTER);
    mLastAddedPanel = insertPanel;
    return comp;
  }
  
//  public static void main(String[] args)
//  {
//    javax.swing.JFrame mf = new javax.swing.JFrame("BorderBox test");
//    mf.setDefaultCloseOperation(mf.EXIT_ON_CLOSE);
//    BorderBox box=new BorderBox(BorderBox.Y_AXIS);
//    mf.getContentPane().add(box);
//    box.add(new javax.swing.JLabel("Label One"));
//    box.add(new javax.swing.JLabel("<html>Label Two<br>Lable two line 2</html>"));
//    box.add(javax.swing.Box.createVerticalStrut(25));
//    box.add(new javax.swing.JTextField("Text field 3"));
//    String AppIcon="/install/images/ico.gif";
//    java.net.URL url = com.eagg.ResourceFetcher.class.getResource(AppIcon);
//    assert url != null : "Null URL for " + AppIcon;
//    javax.swing.ImageIcon icn = new javax.swing.ImageIcon(mf.getToolkit().getImage(url));
//    box.add(new javax.swing.JLabel(icn));
//    mf.setBounds(10, 10, 300, 300);
//    mf.setVisible(true);
//  }
}
