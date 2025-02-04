package com.mm.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Range;

/**
 * <p>This is a much-improved version of GridBagConstraints, which is its base class.</p>
 * <p>This class adds no new fields, but adds several new methods which may be chained. It also
 * changes one default value. The fill member now defaults to BOTH instead of NONE, which is 
 * much more useful.</p>
 * <p>Here is an example to show the method chaining in action. With GridBagConstraints, I would write code like this:</p>
 * <pre>
 *   import static GridBagConstraints.*;
 *   ...
 *   JPanel panel = new JPanel(new GridBagLayout());
 *   GridBagConstraints c = new GridBagConstraints();
 *   c.anchor = LINE_START;
 *   panel.add(versionLabel, c);
 *   
 *   c.gridy = 1;
 *   c.anchor = CENTER;
 *   c.weightx = 1.0;
 *   panel.add(new JLabel("Find:"), c);
 *   
 *   c.gridx = 1;
 *   c.gridwidth = 3;
 *   panel.add(new JTextField(15), c);
 *   // ...
 * </pre>
 * 
 * <p>With The Constrainer Class, it would look like this:</p>
 * <pre>
 *   import static GridBagConstraints.*;
 *   ...
 *   JPanel panel = new JPanel(new GridBagLayout());
 *   Constrainer c = new Constrainer();
 *   panel.add(versionLabel, c.anchor(LINE_START));
 *   panel.add(new JLabel("Find:"), c.at(0, 1).anchor(CENTER).weight(1.0, 0.0));
 *   panel.add(new JTextField(15), c.at(1, 1).gridSize(1, 3));
 *   // ...
 * </pre>
 * 
 * <p>Created by IntelliJ IDEA.
 * <br>Date: 2/3/25
 * <br>Time: 4:59 PM
 * <br>@author Miguel Muñoz</p>
 */
public class Constrainer extends GridBagConstraints {
//  private final JPanel panel;
//  private GridBagConstraints constraints = defaultConstraint();
  
  public Constrainer() {
    super();
    fill = GridBagConstraints.BOTH;
  } 

//  private static GridBagConstraints defaultConstraint() {
//    GridBagConstraints constraints = new GridBagConstraints();
//    constraints.fill = GridBagConstraints.BOTH;
//    return constraints;
//  }
  
//  public GridHelp() {
//    this.panel = new JPanel(new GridBagLayout());
//  }

//  public GridHelp(JPanel panel) {
//    this.panel = panel;
//  }
  
  public static JPanel createPanel() { return new JPanel(new GridBagLayout()); }

  @Override
  public Object clone() {
    return super.clone();
  }

  public Constrainer at(int x, int y) {
    gridx = x;
    gridy = y;
    return this;
  }
  
  public Constrainer gridSize(int x, int y) {
    gridwidth = x;
    gridheight = y;
    return this;
  }
  
  public Constrainer weight(double weightx, double weighty) {
    this.weightx = weightx;
    this.weighty = weighty;
    return this;
  }
  
  @Range(from = GridBagConstraints.CENTER, to = GridBagConstraints.BELOW_BASELINE_TRAILING)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer anchor(int anchor) {
    this.anchor = anchor;
    return this;
  }
  
  @Range(from = GridBagConstraints.NONE, to = GridBagConstraints.VERTICAL)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer fill(int fill) {
    this.fill = fill;
    return this;
  }
  
  public Constrainer insets(int top, int left, int bottom, int right) {
    this.insets = new Insets(top, left, bottom, right);
    return this;
  }
  
  public Constrainer insets(Insets insets) {
    this.insets = insets;
    return this;
  }
  
  public Constrainer pad(int padx, int pady) {
    ipadx += padx;
    ipady += pady;
    return this;
  }
  
//  public void add(JComponent child) {
//    panel.add(child, constraints);
//  }
  
//  public GridHelp withConstraints(GridBagConstraints constraints) {
//    this.constraints = (GridBagConstraints) constraints.clone();
//    return this;
//  }
//  
//  public GridBagConstraints copyOfConstraints() {
//    return (GridBagConstraints) constraints.clone();
//  }
//  
//  public GridHelp clear() {
//    constraints = defaultConstraint();
//    return this;
//  }
}
