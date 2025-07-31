package com.mm.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

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
@SuppressWarnings("unused")
public class Constrainer extends GridBagConstraints {

  /**
   * Construct a constrainer with a fill value of BOTH.
   */
  public Constrainer() {
    super();
    //noinspection AssignmentToSuperclassField
    super.fill = GridBagConstraints.BOTH;
  }

  /**
   * <p>Copy Constructor. Use this to make a copy of another instance of Constrainer or GridBagConstraints.</p>
   *
   * @param model The original Constrainer to copy.
   */
  public Constrainer(GridBagConstraints model) {
    this();
    this.at(model.gridx, model.gridy)
        .anchor(model.anchor)
        .fill(model.fill)
        .gridSize(model.gridwidth, model.gridheight)
        .weight(model.weightx, model.weighty)
        .insets(model.insets)
        .pad(model.ipadx, model.ipady)
    ;
  }


  @SuppressWarnings("UseOfClone")
  @Override
  public Constrainer clone() {
    return (Constrainer) super.clone();
  }

  /**
   * Sets the gridx and gridy properties to x and y, respectively.
   *
   * @param x the new gridx value
   * @param y the new gridy value
   * @return this, for method chaining
   * @see #gridx(int)
   * @see #gridy(int)
   */
  public Constrainer at(int x, int y) {
    gridx = x;
    gridy = y;
    return this;
  }

  /**
   * Sets gridx to the parameter
   *
   * @param x the new gridx
   * @return this, for method chaining
   * @see #at(int, int)
   */
  public Constrainer gridx(int x) {
    gridx = x;
    return this;
  }

  /**
   * Sets gridy to the parameter
   *
   * @param y the new gridy
   * @return this, for method chaining
   * @see #at(int, int)
   */
  public Constrainer gridy(int y) {
    gridy = y;
    return this;
  }

  /**
   * Sets gridwidth and gridheight to x and y
   *
   * @param x The new gridwidth
   * @param y The new gridheight
   * @return this, for method chaining 
   * @see #gridWidth(int)
   * @see #gridHeight(int) 
   */
  public Constrainer gridSize(int x, int y) {
    gridwidth = x;
    gridheight = y;
    return this;
  }

  /**
   * Sets gridwidth to w
   *
   * @param w the new gridwidth
   * @return this, for method chaining
   * @see #gridSize(int, int) 
   */
  public Constrainer gridWidth(int w) { gridwidth = w; return this; }

  /**
   * Sets gridheight to h
   *
   * @param h the new gridheight
   * @return this, for method chaining
   * @see #gridSize(int, int) 
   */
  public Constrainer gridHeight(int h) { gridheight = h; return this; }

  /**
   * Sets the weightx and weighty values
   *
   * @param weightX the new weightx
   * @param weightY the new weighty
   * @return this, for method chaining
   * @see #weightX(double) 
   * @see #weightY(double) 
   */
  public Constrainer weight(double weightX, double weightY) {
    this.weightx = weightX;
    this.weighty = weightY;
    return this;
  }

  /**
   * Sets the weightx value
   * 
   * @param weightX the new weightx value
   * @return this, for method chaining
   * @see #weight(double, double) 
   */
  public Constrainer weightX(double weightX) { weightx = weightX; return this; }

  /**
   * Sets the new weighty value
   *
   * @param weightY the new weighty value
   * @return this, for method chaining
   * @see #weight(double, double) 
   */
  public Constrainer weightY(double weightY) { weighty = weightY; return this; }

  /**
   * Sets the new anchor value
   *
   * @param anchor The new anchor value
   * @return this, for method chaining
   */
  @Range(from = GridBagConstraints.CENTER, to = GridBagConstraints.BELOW_BASELINE_TRAILING)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer anchor(int anchor) {
    this.anchor = anchor;
    return this;
  }

  /**
   * Sets the new fill value
   *
   * @param fill The new fill value
   * @return this, for method chaining
   */
  @Range(from = GridBagConstraints.NONE, to = GridBagConstraints.VERTICAL)
  @MagicConstant(valuesFromClass = GridBagConstraints.class)
  public Constrainer fill(int fill) {
    this.fill = fill;
    return this;
  }

  /**
   * Sets the new insets
   *
   * @param top    The new top value
   * @param left   the new left value
   * @param bottom the new bottom value
   * @param right  the new right value
   * @return this, for method chaining
   */
  public Constrainer insets(int top, int left, int bottom, int right) {
    this.insets = new Insets(top, left, bottom, right);
    return this;
  }

  /**
   * Sets the new insets to a copy of the specified insets
   *
   * @param insets The insets to set
   * @return this, for method chaining
   */
  @SuppressWarnings("UseOfClone")
  public Constrainer insets(Insets insets) {
    this.insets = (Insets) insets.clone();
    return this;
  }

  /**
   * Sets the insets to a new Insets instance with all four sides set to the same value.
   *
   * @param allSides The insets for all four sides
   * @return this, for method chaining
   */
  public Constrainer insets(int allSides) {
    this.insets = new Insets(allSides, allSides, allSides, allSides);
    return this;
  }

  /**
   * Sets the ipadx and ipady values
   *
   * @param padX the new ipadx value
   * @param padY the new ipady value
   * @return this, for method chaining
   * @see #iPadX(int) 
   * @see #iPadY(int) 
   */
  public Constrainer pad(int padX, int padY) {
    ipadx = padX;
    ipady = padY;
    return this;
  }

  /**
   * Sets the ipadx value
   *
   * @param padX the new ipadx value
   * @return this, for method chaining
   * @see #pad(int, int) 
   */
  public Constrainer iPadX(int padX) { ipadx = padX; return this; }

  /**
   * Sets the ipady value
   *
   * @param padY the new ipady value
   * @return this, for method chaining
   * @see #pad(int, int) 
   */
  public Constrainer iPadY(int padY) { ipady = padY; return this; }
}
